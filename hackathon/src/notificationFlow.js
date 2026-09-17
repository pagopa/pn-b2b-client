const { randomUUID } = require('node:crypto');
const { formView, parseDraft, reviewView, resultView } = require('./notificationForm');
const { jobMessage } = require('./sendJobs');

function openingError(error) {
    const knownCodes = new Set(['missing_scope', 'invalid_arguments', 'invalid_arg_name', 'invalid_auth',
        'not_authed', 'token_revoked', 'token_expired', 'account_inactive', 'no_permission',
        'access_denied', 'accesslimited', 'team_access_not_granted', 'expired_trigger_id',
        'invalid_trigger_id', 'exchanged_trigger_id', 'view_too_large', 'ratelimited',
        'internal_error', 'service_unavailable', 'slack_webapi_request_error', 'slack_webapi_http_error']);
    const candidate = error?.data?.error || error?.code;
    const code = knownCodes.has(candidate) ? candidate : 'errore_non_classificato';
    const view = formView();
    const messages = error?.data?.response_metadata?.messages;
    const details = [];
    // Non stampare l'errore SDK: può contenere header e token. Esporre solo codici e nomi locali.
    for (const message of Array.isArray(messages) ? messages : []) {
        if (typeof message !== 'string') continue;
        const match = message.match(/\[json-pointer:\/view\/blocks\/(\d+)(?:\/|\])/);
        const block = match && view.blocks[Number(match[1])];
        if (block?.label?.text) details.push(`Campo: ${block.label.text}`);
        if (message.includes('files:read')) details.push('Il dettaglio Slack menziona files:read');
    }
    const hints = {
        missing_scope: 'Controlla files:read nei Bot Token Scopes e reinstalla l’app nel workspace.',
        expired_trigger_id: 'Rilancia il comando: il tempo utile per aprire il form è scaduto.',
        exchanged_trigger_id: 'Chiudi eventuali altre istanze del bot e rilancia il comando.',
        invalid_arguments: 'Slack ha rifiutato la struttura del form. Riporta questo codice e i campi indicati.',
    };
    return [`Impossibile aprire il form. Codice Slack: ${code}.`, ...new Set(details),
        hints[code] || 'Riporta questo codice per verificare la configurazione o la connessione.'].join(' ');
}

function registerNotificationFlow(app, allowedUsers, { now = Date.now, sendService, jobs } = {}) {
    const live = Boolean(sendService && jobs);
    const running = new Set();
    const launch = task => {
        const promise = Promise.resolve().then(task).catch(() => console.error('Errore lavorazione: consultare lo stato prima di qualsiasi nuovo invio.'));
        running.add(promise);
        promise.finally(() => running.delete(promise));
    };
    const showJob = async (client, viewId, job) => {
        try { await client.views.update({ view_id: viewId, view: resultView(jobMessage(job)) }); }
        catch { console.warn('Aggiornamento modal non disponibile. Esito consultabile con il comando stato.'); }
    };
    const drafts = new Map();
    const ttl = 15 * 60 * 1000;
    const owner = body => `${body.team?.id || body.team_id}:${body.user?.id || body.user_id}`;
    const authorized = body => allowedUsers.has(body.user?.id || body.user_id);
    const cleanup = () => {
        for (const [id, entry] of drafts) if (entry.expiresAt <= now()) drafts.delete(id);
    };
    const timer = setInterval(cleanup, 60_000);
    timer.unref();
    const update = (ack, message) => ack({ response_action: 'update', view: resultView(message) });

    app.command('/send-notification', async ({ command, ack, client, respond }) => {
        await ack();
        if (!authorized(command)) {
            await respond({ response_type: 'ephemeral', text: 'Utente non autorizzato a utilizzare il bot.' });
            return;
        }
        try {
            if (command.text?.trim()) {
                const match = command.text.trim().match(/^stato ([a-f0-9-]{36})$/);
                const job = live && match ? jobs.get(match[1]) : null;
                if (!job || job.owner !== owner(command)) {
                    await respond({ response_type: 'ephemeral', text: 'Lavorazione non trovata o non accessibile. Uso: /send-notification oppure /send-notification stato ID.' });
                    return;
                }
                const opened = await client.views.open({ trigger_id: command.trigger_id, view: resultView(jobMessage(job)) });
                if (['WAITING', 'UNCERTAIN', 'SUBMITTING'].includes(job.state)) launch(async () => {
                    try {
                        const status = await sendService.checkStatus(job.notificationRequestId,
                            { paProtocolNumber: job.protocol, idempotenceToken: job.id });
                        await showJob(client, opened.view.id, jobs.patch(job.id, { ...status, message: undefined }));
                    } catch { await showJob(client, opened.view.id, { ...job, message: 'Stato temporaneamente non disponibile. Nessun reinvio effettuato.' }); }
                });
                return;
            }
            await client.views.open({ trigger_id: command.trigger_id, view: formView({ live }) });
            console.log('Form notifica aperto.');
        } catch (error) {
            const diagnostic = openingError(error);
            console.error(diagnostic);
            await respond({ response_type: 'ephemeral', text: diagnostic });
        }
    });

    app.view('send_notification_form', async ({ ack, body, view }) => {
        if (!authorized(body)) return update(ack, 'Utente non autorizzato.');
        const { errors, draft } = parseDraft(view);
        if (errors) return ack({ response_action: 'errors', errors });
        if (live && draft.environment !== 'test') return ack({ response_action: 'errors', errors: { environment: 'Invio abilitato soltanto in test.' } });
        cleanup();
        if (drafts.size >= 100) return update(ack, 'Troppe bozze aperte. Riprova tra qualche minuto.');
        const id = randomUUID();
        drafts.set(id, { draft, owner: owner(body), expiresAt: now() + ttl });
        await ack({ response_action: 'update', view: reviewView(id, draft, { live }) });
        console.log('Riepilogo notifica trasmesso a Slack.');
    });

    app.view('send_notification_confirm', async ({ ack, body, view, client }) => {
        cleanup();
        const entry = drafts.get(view.private_metadata);
        if (!authorized(body) || !entry || entry.owner !== owner(body)) {
            return update(ack, 'Bozza scaduta, già confermata o non accessibile. Riapri /send-notification.');
        }
        if (live) {
            let job;
            try { job = jobs.reserve(entry.draft, entry.owner); }
            catch { return update(ack, 'Impossibile registrare la lavorazione. Nessun invio avviato.'); }
            drafts.delete(view.private_metadata);
            if (!job) return update(ack, 'Questo protocollo è già stato utilizzato per il mittente in test. Consulta la lavorazione precedente: nessun reinvio avviato.');
            // Nessuna chiamata remota prima della conferma; risposta Slack prima del lavoro lento.
            await update(ack, jobMessage(job));
            launch(async () => {
                const result = await sendService.execute(entry.draft, client, job.id, async changes => {
                    const updated = jobs.patch(job.id, changes);
                    await showJob(client, view.id, updated);
                });
                await showJob(client, view.id, jobs.patch(job.id, result));
            });
            return;
        }
        drafts.delete(view.private_metadata);
        await update(ack, 'Bozza verificata. Invio a SEND non ancora attivo: nessuna notifica creata e nessuno IUN generato. I dati temporanei della bozza sono stati rimossi dal bot; il PDF caricato rimane soggetto alla conservazione di Slack.');
        console.log('Verifica bozza conclusa. Nessun invio SEND.');
    });

    app.view({ callback_id: 'send_notification_confirm', type: 'view_closed' }, async ({ ack, body, view }) => {
        await ack();
        if (drafts.get(view.private_metadata)?.owner === owner(body)) drafts.delete(view.private_metadata);
    });
    const dispose = () => { clearInterval(timer); drafts.clear(); };
    dispose.drain = () => Promise.all([...running]);
    return dispose;
}

module.exports = { registerNotificationFlow, openingError };
