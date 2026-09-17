const test = require('node:test');
const assert = require('node:assert/strict');
const { formView, parseDraft, reviewView } = require('../notificationForm');
const { registerNotificationFlow, openingError } = require('../notificationFlow');

// Dati sintetici creati esclusivamente per test locali, mai inviati a servizi remoti.
function submission(overrides = {}, files) {
    const data = {
        environment: 'test', subject: 'Prova locale', abstract: 'Descrizione di prova',
        senderName: 'Ente di prova', senderTaxId: '00000000000', taxonomy: '010202N',
        communication: 'AR_REGISTERED_LETTER', recipientType: 'PF', taxId: 'AAAAAA00A00A000A',
        denomination: 'Destinatario di prova', address: 'Via di prova 1', zip: '00100',
        municipality: 'Comune di prova', province: 'rm', ...overrides,
    };
    const selects = new Set(['environment', 'communication', 'recipientType']);
    const values = Object.fromEntries(Object.entries(data).map(([key, value]) => [key, {
        value: selects.has(key) ? { selected_option: { value } } : { value },
    }]));
    values.document = { value: { files: files ?? [{ id: 'FTEST', name: 'prova.pdf', mimetype: 'application/pdf', size: 1234 }] } };
    return { state: { values } };
}

test('Mappa il form nel corpo SEND senza ereditare template, pagamenti o riferimenti fittizi', () => {
    const { draft, errors } = parseDraft(submission({ pec: 'test@example.invalid' }));
    assert.equal(errors, undefined);
    assert.equal(draft.environment, 'test');
    assert.equal(draft.request.abstract, 'Descrizione di prova');
    assert.equal(draft.request._abstract, undefined);
    assert.equal(draft.request.notificationFeePolicy, 'FLAT_RATE');
    assert.equal(draft.request.recipients[0].physicalAddress.province, 'RM');
    assert.deepEqual(draft.request.recipients[0].digitalDomicile, { type: 'PEC', address: 'test@example.invalid' });
    assert.equal(draft.request.recipients[0].payments, undefined);
    assert.equal(draft.request.documents, undefined);
    assert.equal(draft.document.slackFileId, 'FTEST');
    assert.match(draft.request.paProtocolNumber, /^HACK-/);
});

test('PG, protocollo esplicito e opzioni assenti', () => {
    const { draft } = parseDraft(submission({ recipientType: 'PG', taxId: '00000000000', protocol: 'PROTO-TEST', abstract: '' }));
    assert.equal(draft.request.paProtocolNumber, 'PROTO-TEST');
    assert.equal(draft.request.recipients[0].recipientType, 'PG');
    assert.equal(draft.request.recipients[0].digitalDomicile, undefined);
    assert.equal(draft.request.abstract, undefined);
    assert.equal(draft.request.group, undefined);
});

test('Rifiuta produzione, valori fuori elenco e campi obbligatori assenti', () => {
    const { errors } = parseDraft(submission({ environment: 'production', communication: 'INVALID', recipientType: 'INVALID', subject: '', address: '' }));
    for (const key of ['environment', 'communication', 'recipientType', 'subject', 'address']) assert.ok(errors[key]);
});

test('Validazione base di CF, CAP, provincia, PEC, tassonomia e lunghezze', () => {
    const { errors } = parseDraft(submission({ taxId: 'bad', senderTaxId: 'bad', zip: '123', province: '123',
        pec: 'bad', taxonomy: 'bad', subject: 'a'.repeat(135), denomination: 'Nome|non valido' }));
    for (const key of ['taxId', 'senderTaxId', 'zip', 'province', 'pec', 'taxonomy', 'subject', 'denomination']) assert.ok(errors[key]);
});

test('Richiede un solo PDF e controlla i metadati disponibili', () => {
    for (const files of [[], [{ id: 'F1' }, { id: 'F2' }], [{ id: 'F1', name: 'bad.exe' }],
        [{ id: 'F1', mimetype: 'image/png' }], [{ id: 'F1', size: 10 * 1024 * 1024 + 1 }]]) {
        assert.ok(parseDraft(submission({}, files)).errors.document);
    }
});

test('Bozze indipendenti e nessuna interpretazione Markdown dei dati', () => {
    const first = parseDraft(submission({ subject: '<!channel>' })).draft;
    const second = parseDraft(submission()).draft;
    assert.notEqual(first.request.paProtocolNumber, second.request.paProtocolNumber);
    first.request.recipients[0].denomination = 'Modificata';
    assert.notEqual(first.request.recipients[0].denomination, second.request.recipients[0].denomination);
    for (const block of reviewView('id', first).blocks) assert.equal(block.text.type, 'plain_text');
});

test('Form e riepilogo rispettano i limiti strutturali Slack anche ai massimi locali', () => {
    const view = formView();
    const overrides = {};
    for (const block of view.blocks) {
        if (['address', 'at', 'addressDetails', 'municipality', 'municipalityDetails', 'group', 'abstract', 'subject', 'protocol'].includes(block.block_id)) {
            overrides[block.block_id] = 'a'.repeat(block.element.max_length);
        }
    }
    const draft = parseDraft(submission(overrides)).draft;
    for (const modal of [view, reviewView('id', draft)]) {
        assert.ok(modal.blocks.length <= 100);
        assert.ok(modal.title.text.length <= 24);
        assert.ok(modal.submit.text.length <= 24);
        for (const block of modal.blocks) {
            if (block.text) assert.ok(block.text.text.length <= 3000);
            if (block.label) assert.ok(block.label.text.length <= 2000);
        }
    }
});

function fixture(t) {
    const views = new Map();
    let command;
    let time = 0;
    const dispose = registerNotificationFlow({
        command(name, handler) { command = handler; },
        view(name, handler) { views.set(typeof name === 'string' ? name : name.type, handler); },
    }, new Set(['U1', 'U2']), { now: () => time });
    t.after(dispose);
    return { views, command, advance() { time += 16 * 60 * 1000; } };
}
const body = (id = 'U1', team = 'T1') => ({ user: { id }, team: { id: team } });

test('Diagnostica apertura: distingue permessi e payload senza esporre errore o credenziali', () => {
    const documentIndex = formView().blocks.findIndex(block => block.block_id === 'document');
    const result = openingError({ data: { error: 'invalid_arguments', response_metadata: {
        messages: [`sensitive-value [json-pointer:/view/blocks/${documentIndex}/element]`, 'files:read required'],
    } }, message: 'sensitive-value', headers: { Authorization: 'sensitive-value' } });
    assert.match(result, /invalid_arguments/);
    assert.match(result, /Documento da notificare/);
    assert.match(result, /files:read/);
    assert.doesNotMatch(result, /sensitive-value/);
    assert.match(openingError({ data: { error: 'missing_scope' } }), /reinstalla/);
    assert.doesNotMatch(openingError({ data: { error: 'sensitive-value' } }), /sensitive-value/);
});

test('Fallimento views.open restituisce un errore privato e classificato', async t => {
    const { command } = fixture(t);
    let response;
    await command({ command: { user_id: 'U1' }, ack: async () => {},
        client: { views: { open: async () => { throw { data: { error: 'missing_scope' } }; } } },
        respond: async value => { response = value; } });
    assert.equal(response.response_type, 'ephemeral');
    assert.match(response.text, /missing_scope/);
});
async function call(handler, args) {
    let response;
    await handler({ ...args, ack: async value => { response = value; } });
    return response;
}

test('Il comando rifiuta utenti non autorizzati senza aprire il form', async t => {
    const { command } = fixture(t);
    let denied = false;
    await command({ command: { user_id: 'OTHER' }, ack: async () => {}, client: { views: { open: () => assert.fail() } },
        respond: async response => { denied = response.response_type === 'ephemeral'; } });
    assert.equal(denied, true);
});

test('Errori per campo prima del riepilogo e controllo autorizzazioni alla submission', async t => {
    const { views } = fixture(t);
    const handler = views.get('send_notification_form');
    const invalid = await call(handler, { body: body(), view: submission({ zip: '' }) });
    assert.equal(invalid.response_action, 'errors');
    assert.ok(invalid.errors.zip);
    const denied = await call(handler, { body: body('OTHER'), view: submission() });
    assert.match(denied.view.blocks[0].text.text, /non autorizzato/);
});

test('Riepilogo e conferma sono isolati per utente/workspace; conferma non ripetibile', async t => {
    const { views } = fixture(t);
    const review = await call(views.get('send_notification_form'), { body: body(), view: submission() });
    assert.equal(review.response_action, 'update');
    assert.match(review.view.private_metadata, /^[a-f0-9-]+$/);
    const confirm = views.get('send_notification_confirm');
    for (const actor of [body('U2'), body('U1', 'T2')]) {
        const denied = await call(confirm, { body: actor, view: review.view });
        assert.match(denied.view.blocks[0].text.text, /non accessibile/);
    }
    const done = await call(confirm, { body: body(), view: review.view });
    assert.match(done.view.blocks[0].text.text, /nessuna notifica creata/);
    const repeated = await call(confirm, { body: body(), view: review.view });
    assert.match(repeated.view.blocks[0].text.text, /già confermata/);
});

test('Scadenza e annullamento eliminano le bozze', async t => {
    const { views, advance } = fixture(t);
    for (const action of ['expire', 'close']) {
        const review = await call(views.get('send_notification_form'), { body: body(), view: submission() });
        if (action === 'expire') advance();
        else await call(views.get('view_closed'), { body: body(), view: review.view });
        const result = await call(views.get('send_notification_confirm'), { body: body(), view: review.view });
        assert.match(result.view.blocks[0].text.text, /Bozza scaduta/);
    }
});
