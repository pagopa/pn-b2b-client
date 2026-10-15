package it.pagopa.pn.client.b2b.pa.parsing.parser.impl;

import it.pagopa.pn.client.b2b.pa.parsing.config.PnLegalFactTokens;
import it.pagopa.pn.client.b2b.pa.parsing.dto.IPnLegalFact;
import it.pagopa.pn.client.b2b.pa.parsing.dto.PnParserRecord;
import it.pagopa.pn.client.b2b.pa.parsing.dto.impLegalFact.*;
import it.pagopa.pn.client.b2b.pa.parsing.dto.implDestinatario.PnDestinatario;
import it.pagopa.pn.client.b2b.pa.parsing.dto.implDestinatario.PnDestinatarioDigitale;
import it.pagopa.pn.client.b2b.pa.parsing.parser.IPnParserLegalFact;
import it.pagopa.pn.client.b2b.pa.parsing.parser.utils.PnLegalFactContent;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class PnParserLegalFact extends PnLegalFactContent implements IPnParserLegalFact {
    public PnParserLegalFact(PnLegalFactTokens pnLegalFactTokens) {
        super(pnLegalFactTokens);
    }

    @Override
    public IPnLegalFact getLegalFactNotificaDowntime(PnParserRecord.PnParserContent content) {
        log.info("PnParserContent: {}", content);
        return PnLegalFactNotificaDowntime.builder()
                .title(getTitle(content))
                .dataOraDecorrenza(getDataOraDecorrenza(content))
                .dataOraFine(getDataOraFineDecorrenza(content))
                .build();
    }

    @Override
    public IPnLegalFact getLegalFactNotificaDigitale(PnParserRecord.PnParserContent content) {
        log.info("PnParserContent: {}", content);
        return PnLegalFactNotificaDigitale.builder()
                .title(getTitle(content))
                .iun(getIun(content, false))
                .pnDestinatario((PnDestinatarioDigitale) getDestinatario(content, true, false, true, false))
                .dataAttestazioneOpponibile(getDataAttestazioneOpponibile(content, false, false, false, true, false))
                .build();
    }

    @Override
    public IPnLegalFact getLegalFactNotificaMancatoRecapito(PnParserRecord.PnParserContent content) {
        log.info("PnParserContent: {}", content);
        return PnLegalFactNotificaMancatoRecapito.builder()
                .title(getTitle(content))
                .iun(getIun(content, false))
                .pnDestinatario((PnDestinatario) getDestinatario(content, true, false, false, false))
                .primaData(getPrimaData(content))
                .secondaData(getSecondaData(content))
                .dataAttestazioneOpponibile(getDataAttestazioneOpponibile(content, false, false, false, false, true))
                .build();
    }

    @Override
    public IPnLegalFact getLegalFactNotificaAvvenutoAccesso(PnParserRecord.PnParserContent content) {
        log.info("PnParserContent: {}", content);
        return PnLegalFactNotificaAvvenutoAccesso.builder()
                .title(getTitle(content))
                .iun(getIun(content, false))
                .dataAttestazioneOpponibile(getDataAttestazioneOpponibile(content, true, false, false, false, false))
                .pnDestinatario((PnDestinatario) getDestinatario(content, false, false, false, false))
                .build();
    }

    @Override
    public IPnLegalFact getLegalFactNotificaAvvenutoAccessoDelegato(PnParserRecord.PnParserContent content) {
        log.info("PnParserContent: {}", content);
        return PnLegalFactNotificaAvvenutoAccessoDelegato.builder()
                .title(getTitle(content))
                .iun(getIun(content, false))
                .dataAttestazioneOpponibile(getDataAttestazioneOpponibile(content, false, true, false, false, false))
                .pnDestinatario((PnDestinatario) getDestinatarioOrDelegato(content, true))
                .delegato((PnDestinatario) getDestinatarioOrDelegato(content, false))
                .build();
    }

    @Override
    public IPnLegalFact getLegalFactNotificaPresaInCarico(PnParserRecord.PnParserContent content) {
        log.info("PnParserContent: {}", content);
        return PnLegalFactNotificaPresaInCarico.builder()
                .title(getTitle(content))
                .dataAttestazioneOpponibile(getDataAttestazioneOpponibile(content, false, false, true, false, false))
                .mittente(getMittente(content))
                .cfMittente(getCfMittente(content))
                .iun(getIun(content, true))
                .pnDestinatario((PnDestinatario) getDestinatario(content, false, true, false, false))
                .build();
    }

    @Override
    public IPnLegalFact getLegalFactNotificaPresaInCaricoMultiDestinatario(PnParserRecord.PnParserContent content) {
        log.info("PnParserContent: {}", content);
        return PnLegalFactNotificaPresaInCaricoMultiDestinatario.builder()
                .title(getTitle(content))
                .dataAttestazioneOpponibile(getDataAttestazioneOpponibile(content, false, false, true, false, false))
                .mittente(getMittente(content))
                .cfMittente(getCfMittente(content))
                .iun(getIun(content, true))
                .pnDestinatario((PnDestinatario) getDestinatario(content, false, true, false, true))
                .destinatariAnalogici(getDestinatariAnalogici(content))
                .build();
    }

    @Override
    public IPnLegalFact getLegalFactNotificationCancelled(PnParserRecord.PnParserContent content) {
        log.info("PnParserContent: {}", content);
        String title = getTitle(content);
        String text = content.text();

        String iun = getIun(content, false);
        if (iun == null) {
            iun = getIun(content, true);
        }
        if (iun == null && text != null) {
            iun = extractBetween(text, "avente IUN", ", eseguita");
        }

        String data = getDataAttestazioneOpponibile(content, false, false, true, false, false);
        if (data == null && text != null) {
            data = extractBetween(text, "in data", ".");
        }

        String mittente = getMittente(content);
        if (mittente == null && text != null) {
            mittente = extractBetween(text, "del Mittente", "in data");
        }

        String nome = getNomeCognomeRagioneSociale(content, true);
        if (nome == null && text != null) {
            nome = extractBetween(text, "Nome e Cognome / Ragione Sociale", "Codice fiscale");
            if (nome == null) {
                nome = extractBetween(text, "Nome e Cognome / Ragione Sociale", "Codice Fiscale");
            }
        }

        String cf = getCodiceFiscale(content, false, false);
        if (cf == null) {
            cf = getCodiceFiscale(content, false, true);
        }
        if (cf == null && text != null) {
            cf = extractBetween(text, "Codice fiscale", "è stata annullata");
            if (cf == null) {
                cf = extractBetween(text, "Codice fiscale", "stata annullata");
            }
            if (cf == null) {
                cf = extractBetween(text, "Codice Fiscale", "stata annullata");
            }
        }

        return PnLegalFactNotificationCancelled.builder()
                .title(title)
                .dataAttestazioneOpponibile(data)
                .iun(iun)
                .mittente(mittente)
                .pnDestinatario(new PnDestinatario(nome, cf))
                .build();
    }

    @Override
    public IPnLegalFact getLegalFactAnalogFailure(PnParserRecord.PnParserContent content) {
        log.info("PnParserContent: {}", content);
        String title = getTitle(content);
        String text = content.text();

        String nome = extractBetween(text, "essendo risultato il destinatario", "CF:");
        String cf = extractBetween(text, "CF:", ", irreperibile");
        if (cf == null) {
            cf = extractBetween(text, "CF:", "irreperibile");
        }
        String iun = extractBetween(text, "avente IUN", ", ai sensi");
        String data = extractBetween(text, "in data", "l'avviso di avvenuta ricezione");
        if (data == null) {
            data = extractBetween(text, "in data", "l’avviso di avvenuta ricezione");
        }

        if (nome == null) {
            nome = getNomeCognomeRagioneSociale(content, true);
        }
        if (cf == null) {
            cf = getCodiceFiscale(content, false, false);
        }
        if (iun == null) {
            iun = getIun(content, false);
        }
        if (data == null) {
            data = getDataAttestazioneOpponibile(content, false, false, true, false, false);
        }

        return PnLegalFactAnalogFailure.builder()
                .title(title)
                .dataAttestazioneOpponibile(data)
                .iun(iun)
                .pnDestinatario(new PnDestinatario(nome, cf))
                .build();
    }

    @Override
    public IPnLegalFact getLegalFactNotificationAar(PnParserRecord.PnParserContent content) {
        log.info("PnParserContent NotificationAar: {}", content);
        String text = content.text();
        String title = "Avviso di avvenuta ricezione";
        // Due alla disposizione a due colonne del PDF, il testo estratto da PDFTextStripperByArea
        // può contenere testo della colonna sinistra (es. "RICEZIONE") prima dell'IUN nella stessa riga.
        // L'IUN è sempre un token compatto senza spazi: si estrae l'ultimo token dopo la suddivisione.
        String iunRaw = extractBetween(text, "Identificativo Univoco Notifica:", "Codice fiscale:");
        String iun = extractLastToken(iunRaw);
        String cfRaw = extractBetween(text, "Codice fiscale:", "notifichedigitali.it");
        String cf = extractFirstToken(cfRaw);
        String mittente = extractBetween(text, "Hai ricevuto una comunicazione a valore legale da", "con oggetto:");

        return PnLegalFactNotificationAar.builder()
                .title(title)
                .iun(iun)
                .destinatarioCodiceFiscale(cf)
                .mittente(mittente)
                .build();
    }

    @Override
    public IPnLegalFact getLegalFactNotificationAarRaddAlt(PnParserRecord.PnParserContent content) {
        log.info("PnParserContent NotificationAarRaddAlt: {}", content);
        String text = content.text();
        String title = "Avviso di avvenuta ricezione";
        // Nel layout a due colonne di RADD_ALT, tra "Identificativo Univoco Notifica:" e "Codice fiscale:"
        // sono presenti il testo del punto di ritiro e il numero telefonico "numero 06.9318.95.55".
        // L'IUN reale precede "numero 06...": isoliamo il token prima di "numero".
        String iunRaw = extractBetween(text, "Identificativo Univoco Notifica:", "Codice fiscale:");
        String iun = null;
        if (iunRaw != null) {
            String lower = iunRaw.toLowerCase();
            int numIdx = lower.indexOf("numero");
            if (numIdx != -1) {
                iun = extractLastToken(iunRaw.substring(0, numIdx).trim());
            } else {
                iun = extractLastToken(iunRaw);
            }
        }

        // Il codice fiscale si trova dopo "Codice fiscale:" ma nel testo interlacciato
        // compare prima "Recati al punto di ritiro portando con te: <cf>".
        // Isoliamo il token effettivo.
        String cf = null;
        String cfRaw = extractBetween(text, "Codice fiscale:", "questo avviso");
        if (cfRaw == null) {
            cfRaw = extractBetween(text, "Codice fiscale:", "Recati al punto");
        }
        if (cfRaw == null) {
            cfRaw = extractBetween(text, "Codice fiscale:", "Paga");
        }
        if (cfRaw != null) {
            String lowerCf = cfRaw.toLowerCase();
            int portandoIdx = lowerCf.indexOf("portando con te:");
            if (portandoIdx != -1) {
                String sub = cfRaw.substring(portandoIdx + "portando con te:".length()).trim();
                cf = extractFirstToken(sub);
            } else {
                cf = extractFirstToken(cfRaw);
            }
        }

        String mittente = extractBetween(text, "Hai ricevuto una comunicazione a valore legale da", "con oggetto:");

        return PnLegalFactNotificationAarRaddAlt.builder()
                .title(title)
                .iun(iun)
                .destinatarioCodiceFiscale(cf)
                .mittente(mittente)
                .build();
    }

    @Override
    public IPnLegalFact getLegalFactAnalogFeedbackAvailability(PnParserRecord.PnParserContent content) {
        log.info("PnParserContent AnalogFeedbackAvailability: {}", content);
        String text = content.text();
        String title = "Dichiarazione data di messa a disposizione dell'esito analogico";
        String data = extractBetween(text, "in data", ", il Gestore");
        String mittente = extractBetween(text, "all’amministrazione mittente", "(C.F.");
        if (mittente == null) {
            mittente = extractBetween(text, "all'amministrazione mittente", "(C.F.");
        }
        String cfMittente = extractBetween(text, "(C.F.", ") l’esito");
        if (cfMittente == null) {
            cfMittente = extractBetween(text, "(C.F.", ") l'esito");
        }
        String iun = extractBetween(text, "notifica avente IUN", ".");
        if (iun != null && iun.endsWith(".")) {
            iun = iun.substring(0, iun.length() - 1).trim();
        }

        return PnLegalFactAnalogFeedbackAvailabilityStatement.builder()
                .title(title)
                .iun(iun)
                .dataAttestazioneOpponibile(data)
                .mittente(mittente)
                .cfMittente(cfMittente)
                .build();
    }

    @Override
    public IPnLegalFact getLegalFactInformalAnalogCommunication(PnParserRecord.PnParserContent content) {
        log.info("PnParserContent InformalAnalogCommunication: {}", content);
        String text = content.text();
        String title = "COMUNICAZIONE DA PARTE DI";
        String mittente = extractBetween(text, "COMUNICAZIONE DA PARTE DI", "Comunicazione");
        String iun = extractBetween(text, "IUN:", "Codice fiscale:");
        String cf = extractBetween(text, "Codice fiscale:", "notifichedigitali.it");
        String destinatario = extractBetween(text, "Ciao", ",");

        return PnLegalFactInformalAnalogCommunication.builder()
                .title(title)
                .iun(iun)
                .destinatarioCodiceFiscale(cf)
                .mittente(mittente)
                .destinatarioNomeCognomeRagioneSociale(destinatario)
                .build();
    }

    @Override
    public IPnLegalFact getLegalFactAnalogDeliveryWorkflowTimeout(PnParserRecord.PnParserContent content) {
        log.info("PnParserContent AnalogDeliveryWorkflowTimeout: {}", content);
        String text = content.text();
        String title = "Attestazione opponibile a terzi: mancata ricezione esiti dall'addetto al recapito estero";
        String iun = extractBetween(text, "identificata con IUN", ", avente come destinatario");
        String destinatario = extractBetween(text, "avente come destinatario", ",");
        String data = extractBetween(text, "alla data del", "risulta decorso");

        return PnLegalFactAnalogDeliveryWorkflowTimeout.builder()
                .title(title)
                .iun(iun)
                .destinatarioNomeCognomeRagioneSociale(destinatario)
                .destinatarioCodiceFiscale("string")
                .dataOraDecorrenza(data)
                .build();
    }

    /**
     * Estrae il testo tra due token delimitatori, normalizzando le legature tipografiche Unicode
     * (ﬁ→fi, ﬂ→fl, ecc.) e usando una ricerca case-insensitive.
     */
    private String extractBetween(String text, String startToken, String endToken) {
        if (text == null || startToken == null || endToken == null) {
            return null;
        }
        // Normalizzazione legature: ﬁ→fi, ﬂ→fl, ﬀ→ff, ﬃ→ffi, ﬄ→ffl
        String normText = text
                .replace("\ufb01", "fi").replace("ﬁ", "fi")
                .replace("\ufb02", "fl").replace("ﬂ", "fl")
                .replace("\ufb00", "ff")
                .replace("\ufb03", "ffi")
                .replace("\ufb04", "ffl");
        String lowerText = normText.toLowerCase();
        String lowerStart = startToken
                .replace("\ufb01", "fi").replace("ﬁ", "fi")
                .replace("\ufb02", "fl").replace("ﬂ", "fl")
                .replace("\ufb00", "ff").replace("\ufb03", "ffi").replace("\ufb04", "ffl")
                .toLowerCase();
        String lowerEnd = endToken
                .replace("\ufb01", "fi").replace("ﬁ", "fi")
                .replace("\ufb02", "fl").replace("ﬂ", "fl")
                .replace("\ufb00", "ff").replace("\ufb03", "ffi").replace("\ufb04", "ffl")
                .toLowerCase();

        int startIdx = lowerText.indexOf(lowerStart);
        if (startIdx == -1) {
            return null;
        }
        startIdx += lowerStart.length();

        int endIdx = lowerText.indexOf(lowerEnd, startIdx);
        if (endIdx == -1) {
            return null;
        }

        String result = normText.substring(startIdx, endIdx).trim();
        return result.replaceAll("[\r\n]+", " ").replaceAll("\\s+", " ").trim();
    }

    /**
     * Restituisce l'ultimo token (separato da spazi) di una stringa.
     * Usato per rimuovere il testo spurio della colonna sinistra del PDF AAR che
     * il layout a due colonne posiziona prima del valore effettivo dell'IUN.
     */
    private String extractLastToken(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        String[] parts = value.trim().split("\\s+");
        return parts[parts.length - 1];
    }

    /**
     * Restituisce il primo token (separato da spazi) di una stringa.
     * Usato per pulire il valore del codice fiscale che può avere suffissi spuri
     * derivanti dal layout a due colonne.
     */
    private String extractFirstToken(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        String[] parts = value.trim().split("\\s+");
        return parts[0];
    }
}