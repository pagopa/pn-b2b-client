const { randomUUID } = require('node:crypto');

const ENVIRONMENTS = { development: 'Development (test)', test: 'Test' };
const COMMUNICATIONS = {
    AR_REGISTERED_LETTER: 'Raccomandata A/R',
    REGISTERED_LETTER_890: 'Notifica ex legge 890',
};
const text = value => ({ type: 'plain_text', text: value });
const section = value => ({ type: 'section', text: text(value) });
const input = (id, label, element, optional = false) => ({
    type: 'input', block_id: id, label: text(label), optional,
    element: { action_id: 'value', ...element },
});
const field = (id, label, max, optional = false, multiline = false) =>
    input(id, label, { type: 'plain_text_input', max_length: max, multiline }, optional);
const select = (id, label, values) => input(id, label, {
    type: 'static_select', options: Object.entries(values).map(([value, label]) => ({ text: text(label), value })),
});

function formView({ live = false } = {}) {
    return {
        type: 'modal', callback_id: 'send_notification_form',
        title: text('Nuova notifica SEND'), submit: text('Riepilogo'), close: text('Annulla'),
        blocks: [
            section(live
                ? 'Prepara una notifica in TEST: un destinatario in Italia, un PDF, senza pagamenti. Dopo il riepilogo, “Invia in TEST” crea una vera notifica nell’ambiente di test.'
                : 'Prepara una notifica di test: un destinatario in Italia, un PDF, senza pagamenti. In questa fase puoi verificare e confermare la bozza; l’invio a SEND non è attivo.'),
            select('environment', 'Ambiente di test', live ? { test: 'Test' } : ENVIRONMENTS),
            section('Notifica e mittente'),
            field('subject', 'Oggetto', 134),
            field('abstract', 'Descrizione', 1024, true, true),
            field('protocol', 'Protocollo PA (vuoto = generato dal bot)', 256, true),
            field('senderName', 'Denominazione ente mittente', 80),
            field('senderTaxId', 'Codice fiscale ente mittente', 11),
            field('group', 'ID gruppo mittente', 1024, true),
            field('taxonomy', 'Codice tassonomico', 7),
            select('communication', 'Comunicazione cartacea', COMMUNICATIONS),
            section('Destinatario'),
            select('recipientType', 'Tipo destinatario', { PF: 'Persona fisica', PG: 'Persona giuridica' }),
            field('taxId', 'Codice fiscale destinatario', 16),
            field('denomination', 'Nome e cognome / ragione sociale', 80),
            field('pec', 'PEC destinatario', 320, true),
            section('Indirizzo fisico italiano'),
            field('address', 'Via / piazza e numero civico', 1024),
            field('addressDetails', 'Dettagli indirizzo (scala, interno)', 1024, true),
            field('at', 'Presso', 1024, true),
            field('zip', 'CAP', 5),
            field('municipality', 'Comune', 256),
            field('municipalityDetails', 'Frazione / località', 256, true),
            field('province', 'Sigla provincia', 2),
            input('document', 'Documento da notificare (PDF, massimo 10 MB)', { type: 'file_input', filetypes: ['pdf'], max_files: 1 }),
            section('Il PDF viene caricato su Slack. La bozza usa costi forfettari (FLAT_RATE), senza pagamenti e senza attualizzazione PagoPA.'),
        ],
    };
}

// Limiti locali del form: non sostituiscono la validazione SEND né la verifica del contenuto PDF.
function parseDraft(view) {
    const state = view.state?.values || {};
    const value = id => {
        const item = state[id]?.value;
        return String(item?.selected_option?.value ?? item?.value ?? '').trim();
    };
    const errors = {};
    const limits = { environment: 20, subject: 134, abstract: 1024, protocol: 256, senderName: 80,
        senderTaxId: 11, group: 1024, taxonomy: 7, communication: 40, recipientType: 2, taxId: 16,
        denomination: 80, pec: 320, address: 1024, addressDetails: 1024, at: 1024, zip: 5,
        municipality: 256, municipalityDetails: 256, province: 2 };
    const optional = new Set(['abstract', 'protocol', 'group', 'pec', 'addressDetails', 'at', 'municipalityDetails']);
    const data = {};
    for (const [id, max] of Object.entries(limits)) {
        data[id] = value(id);
        if (!optional.has(id) && !data[id]) errors[id] = 'Campo obbligatorio.';
        else if (data[id].length > max) errors[id] = `Massimo ${max} caratteri.`;
    }
    for (const id of ['taxId', 'senderTaxId', 'province', 'taxonomy']) data[id] = data[id].toUpperCase();
    if (!Object.hasOwn(ENVIRONMENTS, data.environment)) errors.environment = 'Seleziona un ambiente di test previsto.';
    if (!Object.hasOwn(COMMUNICATIONS, data.communication)) errors.communication = 'Seleziona una modalità prevista.';
    if (!['PF', 'PG'].includes(data.recipientType)) errors.recipientType = 'Seleziona PF o PG.';
    const fiscalCode = /^[A-Z]{6}[0-9LMNPQRSTUV]{2}[A-EHLMPRST][0-9LMNPQRSTUV]{2}[A-Z][0-9LMNPQRSTUV]{3}[A-Z]$/;
    if (!(fiscalCode.test(data.taxId) || /^\d{11}$/.test(data.taxId))) {
        errors.taxId = 'Inserisci un codice fiscale di 16 caratteri o numerico di 11 cifre.';
    }
    if (!/^\d{11}$/.test(data.senderTaxId)) errors.senderTaxId = 'Servono 11 cifre.';
    if (!/^\d{5}$/.test(data.zip)) errors.zip = 'Il CAP italiano deve contenere 5 cifre.';
    if (!/^[A-Z]{2}$/.test(data.province)) errors.province = 'Inserisci la sigla di due lettere.';
    if (!/^\d{6}[A-Z]$/.test(data.taxonomy)) errors.taxonomy = 'Inserisci 6 cifre seguite da una lettera.';
    if (data.pec && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(data.pec)) errors.pec = 'Indirizzo PEC non valido.';
    for (const id of ['denomination', 'senderName']) {
        if (!/^[\x20-\x7B\x7D-\x7E\u00A0-\u00FF]+$/.test(data[id])) {
            errors[id] = 'Usa caratteri Latin-1, senza | o caratteri di controllo.';
        }
    }
    const files = state.document?.value?.files;
    const file = Array.isArray(files) && files.length === 1 ? files[0] : null;
    if (!file?.id || !/^F[A-Z0-9]+$/.test(file.id)) errors.document = 'Carica un PDF.';
    else if (file.name && !/\.pdf$/i.test(file.name)) errors.document = 'Il documento deve essere un PDF.';
    else if (file.mimetype && file.mimetype !== 'application/pdf') errors.document = 'Il documento deve essere un PDF.';
    else if (file.size > 10 * 1024 * 1024) errors.document = 'Per questo form il limite è 10 MB.';
    if (Object.keys(errors).length) return { errors };

    const physicalAddress = { address: data.address, zip: data.zip, municipality: data.municipality, province: data.province };
    for (const id of ['addressDetails', 'at', 'municipalityDetails']) if (data[id]) physicalAddress[id] = data[id];
    const recipient = { recipientType: data.recipientType, taxId: data.taxId, denomination: data.denomination, physicalAddress };
    if (data.pec) recipient.digitalDomicile = { type: 'PEC', address: data.pec };
    const request = {
        paProtocolNumber: data.protocol || `HACK-${randomUUID()}`,
        subject: data.subject, recipients: [recipient],
        senderDenomination: data.senderName, senderTaxId: data.senderTaxId,
        taxonomyCode: data.taxonomy, physicalCommunicationType: data.communication,
        notificationFeePolicy: 'FLAT_RATE', pagoPaIntMode: 'NONE',
    };
    if (data.abstract) request.abstract = data.abstract;
    if (data.group) request.group = data.group;
    // Il file Slack resta separato: documents richiede key, versionToken e digest restituiti dal preload SEND.
    return { draft: { environment: data.environment, request, document: { slackFileId: file.id, name: String(file.name || 'Documento PDF').slice(0, 255) } } };
}

function reviewView(id, draft, { live = false } = {}) {
    const r = draft.request;
    const recipient = r.recipients[0];
    const address = recipient.physicalAddress;
    return {
        type: 'modal', callback_id: 'send_notification_confirm', private_metadata: id, notify_on_close: true,
        title: text('Riepilogo notifica'), submit: text(live ? 'Invia in TEST' : 'Conferma bozza'), close: text('Annulla'),
        blocks: [
            section(live
                ? 'Controlla i dati e il mittente associato alle credenziali. “Invia in TEST” carica il documento e invia la notifica a SEND test. Per modificare, annulla e riapri il comando.'
                : 'Controlla i dati. La conferma conclude la verifica della bozza: non invia la notifica e non genera uno IUN. Per modificare i dati, annulla e riapri il comando.'),
            section(`Ambiente: ${ENVIRONMENTS[draft.environment]}\nMittente: ${r.senderDenomination}\nCF ente: ${r.senderTaxId}\nGruppo: ${r.group || 'Non indicato'}`),
            section(`Oggetto: ${r.subject}\nProtocollo: ${r.paProtocolNumber}\nCodice tassonomico: ${r.taxonomyCode}\nComunicazione: ${COMMUNICATIONS[r.physicalCommunicationType]}`),
            ...(r.abstract ? [section(`Descrizione: ${r.abstract}`)] : []),
            section(`Destinatario: ${recipient.denomination} (${recipient.recipientType})\nCodice fiscale: ${recipient.taxId}\nPEC: ${recipient.digitalDomicile?.address || 'Non indicata'}`),
            ...(address.at ? [section(`Presso: ${address.at}`)] : []),
            section(address.address),
            ...(address.addressDetails ? [section(address.addressDetails)] : []),
            section([`${address.zip} ${address.municipality} (${address.province})`, address.municipalityDetails, 'Italia'].filter(Boolean).join('\n')),
            section(`Documento: ${draft.document.name}\nPagamenti: assenti\nCosti: FLAT_RATE\nAttualizzazione PagoPA: NONE`),
        ],
    };
}

function resultView(message) {
    return { type: 'modal', title: text('Notifica SEND'), close: text('Chiudi'), blocks: [section(message)] };
}

module.exports = { formView, parseDraft, reviewView, resultView };
