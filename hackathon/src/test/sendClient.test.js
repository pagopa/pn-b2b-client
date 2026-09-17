const test = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const os = require('node:os');
const path = require('node:path');
const { createHash } = require('node:crypto');
const { PDFDocument } = require('pdf-lib');
const { createSendClient, safeUrl } = require('../sendClient');
const { validateTestConfig } = require('../sendConfig');
const { createJobStore } = require('../sendJobs');
const { registerNotificationFlow } = require('../notificationFlow');

const config = { apiKey: 'test-key', preloadUrl: 'https://api.test.notifichedigitali.it/delivery/attachments/preload',
    requestUrl: 'https://api.test.notifichedigitali.it/delivery/v2.4/requests' };
const draft = () => ({ environment: 'test', document: { slackFileId: 'FTEST' }, request: {
    subject: 'Test locale', senderTaxId: '00000000000', paProtocolNumber: 'TEST-PROTOCOL', recipients: [],
} });

async function fixture(options = {}) {
    const pdf = await PDFDocument.create(); pdf.addPage();
    const bytes = Buffer.from(await pdf.save());
    const calls = [], records = [], sleeps = [];
    let time = 0, reads = 0;
    const http = async request => {
        calls.push(request);
        if (request.url.includes('files.slack.com')) return { status: 200, data: options.bytes ?? bytes };
        if (request.url === config.preloadUrl) return { data: [{ key: 'storage-key', secret: 'upload-secret', httpMethod: 'PUT',
            url: 'https://bucket.s3.eu-south-1.amazonaws.com/document' }] };
        if (request.method === 'PUT') return { headers: options.missingVersion ? {} : { 'x-amz-version-id': 'real-version' } };
        if (request.method === 'POST') {
            if (options.sendError) throw options.sendError;
            return { data: { notificationRequestId: 'REQUEST-ID' } };
        }
        return { data: options.statuses?.[reads++] ?? { notificationRequestStatus: 'ACCEPTED', iun: 'TEST-IUN' } };
    };
    const service = createSendClient(config, 'slack-token', { http, now: () => time,
        sleep: async ms => { sleeps.push(ms); time += ms; }, pollWindow: options.pollWindow ?? 300_000 });
    const slack = { files: { info: async () => ({ file: { mimetype: 'application/pdf', size: bytes.length,
        url_private: 'https://files.slack.com/file.pdf' } }) } };
    const run = value => service.execute(value ?? draft(), slack, 'JOB-ID', async update => { records.push(update); });
    return { service, run, calls, records, bytes, sleeps };
}

test('Completa download, preload, upload, invio e IUN con credenziali separate e versione reale', async () => {
    const f = await fixture(); const result = await f.run();
    assert.equal(result.state, 'ACCEPTED'); assert.equal(result.iun, 'TEST-IUN');
    const upload = f.calls.find(c => c.method === 'PUT');
    assert.equal(upload.headers.Authorization, undefined);
    assert.equal(upload.headers['x-api-key'], undefined);
    const send = f.calls.find(c => c.url === config.requestUrl && c.method === 'POST');
    assert.equal(send.data.documents[0].ref.versionToken, 'real-version');
    assert.equal(send.data.documents[0].digests.sha256, createHash('sha256').update(f.bytes).digest('base64'));
    assert.equal(send.data.idempotenceToken, 'JOB-ID');
    assert.equal(f.calls[0].headers['x-api-key'], undefined);
    assert.equal(f.records[0].state, 'SUBMITTING');
    assert.equal(f.records[1].notificationRequestId, 'REQUEST-ID');
});

test('PDF fasullo o versione upload assente impediscono la POST notifica', async () => {
    for (const option of [{ bytes: Buffer.from('<html>Not a PDF</html>') }, { missingVersion: true }]) {
        const f = await fixture(option); const result = await f.run();
        assert.equal(result.state, 'FAILED');
        assert.equal(f.calls.filter(c => c.url === config.requestUrl && c.method === 'POST').length, 0);
    }
});

test('Timeout invio ambiguo non viene ritentato e non espone l’errore originale', async () => {
    const f = await fixture({ sendError: new Error('secret-value') }); const result = await f.run();
    assert.equal(result.state, 'UNCERTAIN');
    assert.equal(f.calls.filter(c => c.url === config.requestUrl && c.method === 'POST').length, 1);
    assert.doesNotMatch(JSON.stringify(result), /secret-value/);
});

test('Rifiuto sincrono distingue errore certo da timeout', async () => {
    const f = await fixture({ sendError: { response: { status: 400 } } });
    assert.equal((await f.run()).state, 'FAILED');
});

test('Polling rispetta retryAfter e distingue rifiuto e attesa senza IUN', async () => {
    const f = await fixture({ statuses: [{ notificationRequestStatus: 'WAITING', retryAfter: 9 },
        { notificationRequestStatus: 'REFUSED', errors: [{ code: 'PN_TEST', detail: 'sensitive-value' }] }] });
    const result = await f.run();
    assert.equal(result.state, 'REFUSED'); assert.deepEqual(f.sleeps, [9000]);
    assert.deepEqual(result.errorCodes, ['PN_TEST']); assert.doesNotMatch(JSON.stringify(result), /sensitive-value/);
    const waiting = await fixture({ pollWindow: 1000, statuses: [{ notificationRequestStatus: 'WAITING', retryAfter: 5 }] });
    const pending = await waiting.run();
    assert.equal(pending.state, 'WAITING'); assert.equal(pending.notificationRequestId, 'REQUEST-ID');
    assert.equal(pending.iun, undefined);
});

test('Recupero esito incerto usa protocollo e idempotenceToken, senza nuovo invio', async () => {
    const f = await fixture();
    await f.service.checkStatus(undefined, { paProtocolNumber: 'PROTOCOL', idempotenceToken: 'JOB-ID' });
    assert.equal(f.calls.length, 1); assert.equal(f.calls[0].method, 'GET');
    assert.equal(f.calls[0].params.idempotenceToken, 'JOB-ID');
});

test('Blocca altri ambienti e host prima delle chiamate', async () => {
    const f = await fixture(); assert.equal((await f.run({ ...draft(), environment: 'production' })).state, 'FAILED');
    assert.equal(f.calls.length, 0);
    for (const url of ['http://files.slack.com/a', 'https://files.slack.com.evil.test/a', 'https://localhost/a', 'https://user:pass@files.slack.com/a']) {
        assert.throws(() => safeUrl(url, 'slack'));
    }
    const env = { PN_EXTERNAL_API_KEY_GA: 'test', PN_EXTERNAL_BASE_URL_UPLOAD_NOTIFICATION: config.preloadUrl,
        PN_EXTERNAL_BASE_URL_SENT_NOTIFICATION: config.requestUrl };
    assert.doesNotThrow(() => validateTestConfig(env));
    assert.throws(() => validateTestConfig({ ...env, PN_EXTERNAL_BASE_URL_SENT_NOTIFICATION: 'https://api.notifichedigitali.it/delivery/v2.4/requests' }));
});

test('Accetta TEST v2.4 e v2.5 senza riscrivere URL; blocca versioni e componenti inattesi', () => {
    const env = { PN_EXTERNAL_API_KEY_GA: 'test', PN_EXTERNAL_BASE_URL_UPLOAD_NOTIFICATION: config.preloadUrl };
    for (const version of ['2.4', '2.5']) {
        const url = `https://api.test.notifichedigitali.it/delivery/v${version}/requests`;
        assert.equal(validateTestConfig({ ...env, PN_EXTERNAL_BASE_URL_SENT_NOTIFICATION: url }).requestUrl, url);
    }
    for (const url of ['https://api.test.notifichedigitali.it/delivery/v9/requests',
        `${config.requestUrl}?key=value`, `${config.requestUrl}#fragment`,
        'https://user:pass@api.test.notifichedigitali.it/delivery/v2.5/requests']) {
        assert.throws(() => validateTestConfig({ ...env, PN_EXTERNAL_BASE_URL_SENT_NOTIFICATION: url }));
    }
});

function storeFixture(t) {
    const directory = fs.mkdtempSync(path.join(os.tmpdir(), 'send-bot-test-'));
    t.after(() => fs.rmSync(directory, { recursive: true, force: true }));
    return { directory, jobs: createJobStore(directory) };
}

test('Journal persiste ID/stato e impedisce riuso protocollo anche dopo riavvio', t => {
    const { jobs, directory } = storeFixture(t);
    const job = jobs.reserve(draft(), 'T1:U1');
    jobs.patch(job.id, { state: 'WAITING', notificationRequestId: 'REQUEST-ID' });
    const reopened = createJobStore(directory);
    assert.equal(reopened.get(job.id).notificationRequestId, 'REQUEST-ID');
    assert.equal(reopened.reserve(draft(), 'T1:U1'), null);
    jobs.patch(job.id, { state: 'ACCEPTED', iun: 'TEST-IUN' });
    assert.equal(jobs.patch(job.id, { state: 'WAITING' }).state, 'ACCEPTED');
    assert.throws(() => jobs.get('../../secret'));
});

test('Conferma live risponde prima del lavoro lento e doppio click non invia due volte', async t => {
    const { jobs } = storeFixture(t); const views = new Map();
    let acknowledged = false, sends = 0;
    const dispose = registerNotificationFlow({ command() {}, view(name, cb) { views.set(name, cb); } }, new Set(['U1']), {
        jobs, sendService: { execute: async () => { assert.equal(acknowledged, true); sends++; return { state: 'ACCEPTED', iun: 'TEST-IUN' }; } },
    });
    t.after(dispose);
    const fields = { environment: 'test', subject: 'Prova', senderName: 'Ente', senderTaxId: '00000000000', taxonomy: '010202N',
        communication: 'AR_REGISTERED_LETTER', recipientType: 'PF', taxId: 'AAAAAA00A00A000A', denomination: 'Prova',
        address: 'Via di prova', zip: '00100', municipality: 'Comune', province: 'RM' };
    const values = Object.fromEntries(Object.entries(fields).map(([key, value]) => [key, { value: { value } }]));
    values.document = { value: { files: [{ id: 'FTEST', name: 'test.pdf' }] } };
    const body = { team: { id: 'T1' }, user: { id: 'U1' } };
    let review;
    await views.get('send_notification_form')({ body, view: { state: { values } }, ack: async result => { review = result.view; } });
    assert.equal(review.submit.text, 'Invia in TEST');
    const args = { body, view: { ...review, id: 'V1' }, ack: async () => { acknowledged = true; }, client: { views: { update: async () => {} } } };
    await views.get('send_notification_confirm')(args);
    await views.get('send_notification_confirm')(args);
    await dispose.drain(); assert.equal(sends, 1);
});
