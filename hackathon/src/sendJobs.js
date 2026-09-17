const fs = require('node:fs');
const path = require('node:path');
const { createHash, randomUUID } = require('node:crypto');

function createJobStore(directory) {
    fs.mkdirSync(directory, { recursive: true, mode: 0o700 });
    const location = id => {
        if (!/^[a-f0-9-]{36}$/.test(id)) throw new Error('ID lavorazione non valido.');
        return path.join(directory, `${id}.json`);
    };
    function get(id) {
        try { return JSON.parse(fs.readFileSync(location(id), 'utf8')); }
        catch (error) { if (error.code === 'ENOENT') return null; throw error; }
    }
    function save(job) {
        const file = location(job.id);
        const temp = `${file}.${randomUUID()}.tmp`;
        fs.writeFileSync(temp, JSON.stringify(job), { mode: 0o600 });
        fs.renameSync(temp, file);
        return job;
    }
    function reserve(draft, owner) {
        const fingerprint = createHash('sha256').update(JSON.stringify([
            draft.environment, draft.request.senderTaxId, draft.request.paProtocolNumber,
        ])).digest('hex');
        const id = randomUUID();
        // Il lock non scade automaticamente: impedisce reinvii dopo timeout, riavvii o conferme ripetute.
        try { fs.writeFileSync(path.join(directory, `${fingerprint}.lock`), id, { flag: 'wx', mode: 0o600 }); }
        catch (error) { if (error.code === 'EEXIST') return null; throw error; }
        return save({ id, owner, environment: 'test', protocol: draft.request.paProtocolNumber,
            state: 'PREPARING', createdAt: new Date().toISOString() });
    }
    const patch = (id, changes) => {
        const job = get(id);
        if (!job) throw new Error('Lavorazione non trovata.');
        if (['ACCEPTED', 'REFUSED'].includes(job.state) && changes.state !== job.state) return job;
        return save({ ...job, ...changes, updatedAt: new Date().toISOString() });
    };
    return { reserve, get, patch };
}

function jobMessage(job) {
    const labels = { PREPARING: 'Preparazione PDF e upload', SUBMITTING: 'Invio in corso o esito da verificare',
        WAITING: 'Richiesta acquisita, esito in attesa', ACCEPTED: 'Notifica accettata', REFUSED: 'Richiesta rifiutata',
        FAILED: 'Operazione non completata', UNCERTAIN: 'Esito invio incerto: non reinviare' };
    return [`SEND test — ${labels[job.state] || job.state}`, `Lavorazione: ${job.id}`,
        `Protocollo: ${job.protocol}`,
        job.notificationRequestId && `notificationRequestId: ${job.notificationRequestId}`,
        job.iun && `IUN: ${job.iun}`, job.errorCodes?.length && `Codici rifiuto: ${job.errorCodes.join(', ')}`,
        job.message, `Per consultare l’esito: /send-notification stato ${job.id}`,
        'Tieni il bot attivo fino all’esito. Dopo un riavvio nessun invio viene ripetuto automaticamente.'].filter(Boolean).join('\n');
}

module.exports = { createJobStore, jobMessage };
