package it.pagopa.pn.client.b2b.pa.parsing.service;

import it.pagopa.pn.client.b2b.pa.parsing.dto.PnParserParameter;
import it.pagopa.pn.client.b2b.pa.parsing.dto.PnParserResponse;
import lombok.Getter;
import java.io.IOException;
import java.util.List;


public interface IPnParserService {
    @Getter
    enum LegalFactType {
        LEGALFACT_NOTIFICA_DOWNTIME(
                LegalFactField.DATA_ORA_DECORRENZA,
                LegalFactField.DATA_ORA_FINE),
        LEGALFACT_NOTIFICA_DIGITALE(
                LegalFactField.IUN,
                LegalFactField.DATA_ATTESTAZIONE_OPPONIBILE,
                LegalFactField.DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE,
                LegalFactField.DESTINATARIO_CODICE_FISCALE,
                LegalFactField.DESTINATARIO_DOMICILIO_DIGITALE,
                LegalFactField.DESTINATARIO_TIPO_DOMICILIO_DIGITALE,
                LegalFactField.DESTINATARIO_INDIRIZZO_FISICO),
        LEGALFACT_NOTIFICA_MANCATO_RECAPITO(
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
                LegalFactField.IUN,
                LegalFactField.DATA_ATTESTAZIONE_OPPONIBILE,
                LegalFactField.DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE,
                LegalFactField.DESTINATARIO_CODICE_FISCALE,
                LegalFactField.DESTINATARIO_DOMICILIO_DIGITALE,
                LegalFactField.DESTINATARIO_TIPO_DOMICILIO_DIGITALE,
                LegalFactField.DESTINATARIO_INDIRIZZO_FISICO),
        LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO_DELEGATO(
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
        LegalFactType(LegalFactField... keyValues){
            this.legalFactFieldList = List.of(keyValues);
        }
    }

    @Getter
    enum LegalFactField {
        IUN("iun"),
        DATA_ATTESTAZIONE_OPPONIBILE("dataAttestazioneOpponibile"),

        DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE("destinatarioNomeCognomeRagioneSociale"),
        DESTINATARIO_CODICE_FISCALE("destinatarioCodiceFiscale"),
        DESTINATARIO_DOMICILIO_DIGITALE("destinatarioDomicilioDigitale"),
        DESTINATARIO_TIPO_DOMICILIO_DIGITALE("destinatarioTipoDomicilioDigitale"),
        DESTINATARIO_INDIRIZZO_FISICO("destinatarioIndirizzoFisico"),

        DELEGATO_NOME_COGNOME_RAGIONE_SOCIALE("delegatoNomeCognomeRagioneSociale"),
        DELEGATO_CODICE_FISCALE("delegatoCodiceFiscale"),
        DELEGATO_DOMICILIO_DIGITALE("delegatoDomicilioDigitale"),
        DELEGATO_TIPO_DOMICILIO_DIGITALE("delegatoTipoDomicilioDigitale"),
        DELEGATO_INDIRIZZO_FISICO("delegatoIndirizzoFisico"),

        MITTENTE("mittente"),
        CF_MITTENTE("cfMittente"),
        PRIMA_DATA("primaData"),
        SECONDA_DATA("secondaData"),
        DATA_ORA_DECORRENZA("dataOraDecorrenza"),
        DATA_ORA_FINE("dataOraFine"),
        NOME_COGNOME_RAGIONE_SOCIALE("nomeCognomeRagioneSociale"),
        CODICE_FISCALE("codiceFiscale"),
        DOMICILIO_DIGITALE("domicilioDigitale"),
        TIPO_DOMICILIO_DIGITALE("tipoDomicilioDigitale"),
        INDIRIZZO_FISICO("indirizzoFisico");

        private final String field;

        LegalFactField(String field){
            this.field = field;
        }
    }
    PnParserResponse extractSingleField(byte[] source, PnParserParameter parserParameter) throws IOException, NoSuchFieldException, IllegalAccessException;
    PnParserResponse extractAllField(byte[] source, PnParserParameter parserParameter) throws IOException;
}