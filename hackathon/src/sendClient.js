const axios = require('axios');
const { createHash } = require('node:crypto');
const { PDFDocument } = require('pdf-lib');

const MAX_BYTES = 10 * 1024 * 1024;
const TIMEOUT = 30_000;
const pause = ms => new Promise(resolve => setTimeout(resolve, ms));
class SendError extends Error {}
const identifier = value => typeof value === 'string' && value.length > 0 && value.length <= 1024;
const safeCodes = errors => (Array.isArray(errors) ? errors : [])
    .map(error => error?.code).filter(code => typeof code === 'string' && /^[A-Za-z0-9_.-]{1,100}$/.test(code)).slice(0, 10);

function safeUrl(value, kind) {
    let url;
    try { url = new URL(value); } catch { throw new SendError('URL del documento non valido.'); }
    const allowed = kind === 'slack'
        ? ['files.slack.com', 'files-origin.slack.com'].includes(url.hostname)
        : /(^|\.)s3[.-][a-z0-9.-]*amazonaws\.com$/.test(url.hostname);
    if (url.protocol !== 'https:' || url.username || url.password || url.port || !allowed) {
        throw new SendError('Host del documento non consentito.');
    }
    return url.href;
}

function createSendClient(config, slackToken, { http = options => axios.request(options), sleep = pause, now = Date.now, pollWindow = 300_000 } = {}) {
    const apiHeaders = { 'x-api-key': config.apiKey, Accept: 'application/json', 'Content-Type': 'application/json' };
    const api = options => http({ timeout: TIMEOUT, maxRedirects: 0, ...options, headers: apiHeaders });

    async function download(fileId, slack) {
        const response = await slack.files.info({ file: fileId });
        const file = response.file;
        if (!file || file.is_external || file.mimetype !== 'application/pdf' || file.size > MAX_BYTES) {
            throw new SendError('Carica un PDF su Slack, non esterno e di massimo 10 MB.');
        }
        let url = safeUrl(file.url_private_download || file.url_private, 'slack');
        for (let attempt = 0; attempt < 4; attempt++) {
            const result = await http({ method: 'GET', url, timeout: TIMEOUT, maxRedirects: 0,
                responseType: 'arraybuffer', maxContentLength: MAX_BYTES,
                headers: { Authorization: `Bearer ${slackToken}` }, validateStatus: status => status >= 200 && status < 400 });
            if (result.status >= 300) {
                url = safeUrl(new URL(result.headers.location, url).href, 'slack');
                continue;
            }
            const bytes = Buffer.from(result.data);
            if (!bytes.length || bytes.length > MAX_BYTES || !bytes.subarray(0, 1024).includes(Buffer.from('%PDF-'))) {
                throw new SendError('Il file scaricato non è un PDF valido entro 10 MB.');
            }
            try {
                const pdf = await PDFDocument.load(bytes, { throwOnInvalidObject: true });
                if (!pdf.getPageCount()) throw new Error();
            } catch { throw new SendError('PDF non leggibile, cifrato o senza pagine.'); }
            return bytes;
        }
        throw new SendError('Troppi reindirizzamenti durante il download del PDF.');
    }

    async function checkStatus(notificationRequestId, reference) {
        const params = notificationRequestId ? { notificationRequestId } : reference;
        if (!params?.notificationRequestId && (!params?.paProtocolNumber || !params?.idempotenceToken)) {
            throw new SendError('Identificativo o protocollo/idempotenceToken necessari.');
        }
        const { data } = await api({ method: 'GET', url: config.requestUrl, params });
        const requestId = identifier(data?.notificationRequestId) ? data.notificationRequestId : notificationRequestId;
        const id = requestId ? { notificationRequestId: requestId } : {};
        if (data?.notificationRequestStatus === 'ACCEPTED' && identifier(data.iun)) {
            return { ...id, state: 'ACCEPTED', iun: data.iun };
        }
        if (data?.notificationRequestStatus === 'REFUSED') return { ...id, state: 'REFUSED', errorCodes: safeCodes(data.errors) };
        if (['WAITING', 'ACCEPTED'].includes(data?.notificationRequestStatus)) {
            return { ...id, state: 'WAITING', retryAfter: Number.isFinite(data.retryAfter) && data.retryAfter > 0 ? data.retryAfter : 5 };
        }
        throw new SendError('Stato SEND non riconosciuto.');
    }

    async function execute(draft, slack, jobId, record) {
        let stage = 'PDF';
        let submitted = false;
        let requestId;
        try {
            if (draft.environment !== 'test') throw new SendError('Invio consentito solo in test.');
            const bytes = await download(draft.document.slackFileId, slack);
            const sha256 = createHash('sha256').update(bytes).digest('base64');
            stage = 'PRELOAD';
            const { data } = await api({ method: 'POST', url: config.preloadUrl,
                data: [{ preloadIdx: '0', sha256, contentType: 'application/pdf' }] });
            const preload = Array.isArray(data) ? data[0] : null;
            if (!preload?.key || !preload.secret || !['PUT', 'POST'].includes(preload.httpMethod)) {
                throw new SendError('Risposta preload incompleta.');
            }
            stage = 'UPLOAD';
            const uploaded = await http({ method: preload.httpMethod, url: safeUrl(preload.url, 'upload'),
                timeout: TIMEOUT, maxRedirects: 0, maxBodyLength: MAX_BYTES, data: bytes,
                headers: { 'Content-Type': 'application/pdf', 'Content-Length': bytes.length,
                    'x-amz-checksum-sha256': sha256, 'x-amz-meta-secret': preload.secret } });
            const versionToken = uploaded.headers['x-amz-version-id'];
            if (!identifier(versionToken)) throw new SendError('Upload senza x-amz-version-id: notifica non inviata.');
            stage = 'INVIO';
            // Journal aggiornato PRIMA della POST. Nessun retry automatico della richiesta di notifica.
            await record({ state: 'SUBMITTING' });
            submitted = true;
            const sent = await api({ method: 'POST', url: config.requestUrl, data: {
                ...draft.request, idempotenceToken: jobId,
                documents: [{ contentType: 'application/pdf', digests: { sha256 }, ref: { key: preload.key, versionToken } }],
            } });
            if (!identifier(sent.data?.notificationRequestId)) throw new SendError('Risposta di invio priva di identificativo.');
            requestId = sent.data.notificationRequestId;
            await record({ state: 'WAITING', notificationRequestId: requestId });
            stage = 'STATO';
            const deadline = now() + pollWindow;
            for (let attempt = 0; attempt < 60 && now() < deadline; attempt++) {
                let status;
                try { status = await checkStatus(requestId); }
                catch (error) {
                    if (![404, 429, 500, 502, 503, 504].includes(error.response?.status)) throw error;
                    status = { state: 'WAITING', retryAfter: 5 };
                }
                if (status.state !== 'WAITING') return { ...status, notificationRequestId: requestId };
                const delay = Math.max(1000, status.retryAfter * 1000);
                if (now() + delay >= deadline) break;
                await sleep(delay);
            }
            return { state: 'WAITING', notificationRequestId: requestId, message: 'Esito non ancora disponibile. Usa il comando stato; non reinviare.' };
        } catch (error) {
            const status = error.response?.status;
            const rejected = stage === 'INVIO' && [400, 401, 403, 422].includes(status);
            const state = requestId ? 'WAITING' : submitted && !rejected ? 'UNCERTAIN' : 'FAILED';
            const message = error instanceof SendError ? error.message : `Errore nella fase ${stage}${Number.isInteger(status) ? ` (HTTP ${status})` : ''}.`;
            return { state, ...(requestId ? { notificationRequestId: requestId } : {}), message,
                ...(state === 'UNCERTAIN' ? { uncertain: true } : {}) };
        }
    }
    return { execute, checkStatus };
}

module.exports = { createSendClient, safeUrl };
