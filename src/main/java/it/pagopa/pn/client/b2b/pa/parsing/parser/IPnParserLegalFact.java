package it.pagopa.pn.client.b2b.pa.parsing.parser;

import it.pagopa.pn.client.b2b.pa.parsing.dto.IPnLegalFact;
import it.pagopa.pn.client.b2b.pa.parsing.dto.PnParserRecord;
import lombok.Getter;
import java.util.Arrays;
import java.util.List;


public interface IPnParserLegalFact {
    String DESTINATARIO = "DESTINATARIO";

    @Getter
    enum LegalFactTypeTitle {
        TYPE_TITLE_NOTIFICA_DOWNTIME(LegalFactType.LEGALFACT_NOTIFICA_DOWNTIME, "Attestazione opponibile a terzi"),
        TYPE_TITLE_NOTIFICA_DIGITALE(LegalFactType.LEGALFACT_NOTIFICA_DIGITALE, "Attestazione opponibile a terzi: notifica digitale"),
        TYPE_TITLE_NOTIFICA_MANCATO_RECAPITO(LegalFactType.LEGALFACT_NOTIFICA_MANCATO_RECAPITO,"Attestazione opponibile a terzi: mancato recapito digitale"),
        TYPE_TITLE_NOTIFICA_PRESA_IN_CARICO_MULTIDESTINATARIO(LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO_MULTIDESTINATARIO,"Attestazione opponibile a terzi: notifica presa in carico"),
        TYPE_TITLE_NOTIFICA_PRESA_IN_CARICO(LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO,"Attestazione opponibile a terzi: notifica presa in carico"),
        TYPE_TITLE_NOTIFICA_AVVENUTO_ACCESSO(LegalFactType.LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO,"Attestazione opponibile a terzi: avvenuto accesso"),
        TYPE_TITLE_NOTIFICA_AVVENUTO_ACCESSO_DELEGATO(LegalFactType.LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO_DELEGATO,"Attestazione opponibile a terzi: avvenuto accesso");

        private final LegalFactType type;
        private final String title;
        LegalFactTypeTitle(LegalFactType type, String title){
            this.type = type;
            this.title = title;
        }

        public static String getTitleByType(LegalFactType type) {
            return Arrays.stream(LegalFactTypeTitle.values())
                    .filter(value -> value.getType().equals(type))
                    .findAny()
                    .get()
                    .getTitle();
        }
    }

    @Getter
    enum LegalFactType {
        LEGALFACT_NOTIFICA_DOWNTIME(
                LegalFactField.TITLE,
                LegalFactField.DATA_ORA_DECORRENZA,
                LegalFactField.DATA_ORA_FINE),
        LEGALFACT_NOTIFICA_DIGITALE(
                LegalFactField.TITLE,
                LegalFactField.IUN,
                LegalFactField.DATA_ATTESTAZIONE_OPPONIBILE,
                LegalFactField.DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE,
                LegalFactField.DESTINATARIO_CODICE_FISCALE,
                LegalFactField.DESTINATARIO_DOMICILIO_DIGITALE,
                LegalFactField.DESTINATARIO_TIPO_DOMICILIO_DIGITALE,
                LegalFactField.DESTINATARIO_INDIRIZZO_FISICO),
        LEGALFACT_NOTIFICA_MANCATO_RECAPITO(
                LegalFactField.TITLE,
                LegalFactField.IUN,
                LegalFactField.DATA_ATTESTAZIONE_OPPONIBILE,
                LegalFactField.DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE,
                LegalFactField.DESTINATARIO_CODICE_FISCALE,
                LegalFactField.DESTINATARIO_DOMICILIO_DIGITALE,
                LegalFactField.DESTINATARIO_TIPO_DOMICILIO_DIGITALE,
                LegalFactField.DESTINATARIO_INDIRIZZO_FISICO,
                LegalFactField.PRIMA_DATA,
                LegalFactField.SECONDA_DATA),
        LEGALFACT_NOTIFICA_PRESA_IN_CARICO(
                LegalFactField.TITLE,
                LegalFactField.IUN,
                LegalFactField.DATA_ATTESTAZIONE_OPPONIBILE,
                LegalFactField.DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE,
                LegalFactField.DESTINATARIO_CODICE_FISCALE,
                LegalFactField.DESTINATARIO_DOMICILIO_DIGITALE,
                LegalFactField.DESTINATARIO_TIPO_DOMICILIO_DIGITALE,
                LegalFactField.DESTINATARIO_INDIRIZZO_FISICO,
                LegalFactField.MITTENTE,
                LegalFactField.CF_MITTENTE),
        LEGALFACT_NOTIFICA_PRESA_IN_CARICO_MULTIDESTINATARIO(
                LegalFactField.TITLE,
                LegalFactField.IUN,
                LegalFactField.DATA_ATTESTAZIONE_OPPONIBILE,
                LegalFactField.MITTENTE,
                LegalFactField.CF_MITTENTE,
                LegalFactField.DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE,
                LegalFactField.DESTINATARIO_CODICE_FISCALE,
                LegalFactField.DESTINATARIO_DOMICILIO_DIGITALE,
                LegalFactField.DESTINATARIO_TIPO_DOMICILIO_DIGITALE,
                LegalFactField.DESTINATARIO_INDIRIZZO_FISICO),
        LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO(
                LegalFactField.TITLE,
                LegalFactField.IUN,
                LegalFactField.DATA_ATTESTAZIONE_OPPONIBILE,
                LegalFactField.DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE,
                LegalFactField.DESTINATARIO_CODICE_FISCALE,
                LegalFactField.DESTINATARIO_DOMICILIO_DIGITALE,
                LegalFactField.DESTINATARIO_TIPO_DOMICILIO_DIGITALE,
                LegalFactField.DESTINATARIO_INDIRIZZO_FISICO),
        LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO_DELEGATO(
                LegalFactField.TITLE,
                LegalFactField.IUN,
                LegalFactField.DATA_ATTESTAZIONE_OPPONIBILE,
                LegalFactField.DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE,
                LegalFactField.DESTINATARIO_CODICE_FISCALE,
                LegalFactField.DESTINATARIO_DOMICILIO_DIGITALE,
                LegalFactField.DESTINATARIO_TIPO_DOMICILIO_DIGITALE,
                LegalFactField.DESTINATARIO_INDIRIZZO_FISICO,
                LegalFactField.DELEGATO_NOME_COGNOME_RAGIONE_SOCIALE,
                LegalFactField.DELEGATO_CODICE_FISCALE,
                LegalFactField.DELEGATO_DOMICILIO_DIGITALE,
                LegalFactField.DELEGATO_TIPO_DOMICILIO_DIGITALE,
                LegalFactField.DELEGATO_INDIRIZZO_FISICO);

        private final List<LegalFactField> legalFactFieldList;
        LegalFactType(LegalFactField... field){
            this.legalFactFieldList = List.of(field);
        }
    }

    @Getter
    enum LegalFactField {
        TITLE("title"),
        IUN("iun"),
        DATA_ATTESTAZIONE_OPPONIBILE("dataAttestazioneOpponibile"),
        MITTENTE("mittente"),
        CF_MITTENTE("cfMittente"),
        PRIMA_DATA("primaData"),
        SECONDA_DATA("secondaData"),
        DATA_ORA_DECORRENZA("dataOraDecorrenza"),
        DATA_ORA_FINE("dataOraFine"),
        DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE("destinatarioNomeCognomeRagioneSociale"),
        DESTINATARIO_CODICE_FISCALE("destinatarioCodiceFiscale"),
        DESTINATARIO_DOMICILIO_DIGITALE("destinatarioDomicilioDigitale"),
        DESTINATARIO_TIPO_DOMICILIO_DIGITALE("destinatarioTipoDomicilioDigitale"),
        DESTINATARIO_INDIRIZZO_FISICO("destinatarioIndirizzoFisico"),
        DELEGATO_NOME_COGNOME_RAGIONE_SOCIALE("delegatoNomeCognomeRagioneSociale"),
        DELEGATO_CODICE_FISCALE("delegatoCodiceFiscale"),
        DELEGATO_DOMICILIO_DIGITALE("delegatoDomicilioDigitale"),
        DELEGATO_TIPO_DOMICILIO_DIGITALE("delegatoTipoDomicilioDigitale"),
        DELEGATO_INDIRIZZO_FISICO("delegatoIndirizzoFisico");

        private final String field;
        LegalFactField(String field){
            this.field = field;
        }
    }

    IPnLegalFact getLegalFactNotificaDowntime(PnParserRecord.PnParserContent content);
    IPnLegalFact getLegalFactNotificaDigitale(PnParserRecord.PnParserContent content);
    IPnLegalFact getLegalFactNotificaMancatoRecapito(PnParserRecord.PnParserContent content);
    IPnLegalFact getLegalFactNotificaAvvenutoAccesso(PnParserRecord.PnParserContent content);
    IPnLegalFact getLegalFactNotificaAvvenutoAccessoDelegato(PnParserRecord.PnParserContent content);
    IPnLegalFact getLegalFactNotificaPresaInCarico(PnParserRecord.PnParserContent content);
    IPnLegalFact getLegalFactNotificaPresaInCaricoMultiDestinatario(PnParserRecord.PnParserContent content);
}