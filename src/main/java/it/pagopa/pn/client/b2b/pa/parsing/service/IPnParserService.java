package it.pagopa.pn.client.b2b.pa.parsing.service;

import it.pagopa.pn.client.b2b.pa.parsing.design.IPnLegalFact;
import lombok.Getter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public interface IPnParserService {
    @Getter
    enum LegalFactType {
        LEGALFACT_NOTIFICA_DOWNTIME(
                LegalFactKeyValues.DATA_ORA_DECORRENZA,
                LegalFactKeyValues.DATA_ORA_FINE),
        LEGALFACT_NOTIFICA_DIGITALE(
                LegalFactKeyValues.IUN,
                LegalFactKeyValues.DATA_ATTESTAZIONE_OPPONIBILE,
                LegalFactKeyValues.DESTINATARIO),
        LEGALFACT_NOTIFICA_MANCATO_RECAPITO(
                LegalFactKeyValues.IUN,
                LegalFactKeyValues.DATA_ATTESTAZIONE_OPPONIBILE,
                LegalFactKeyValues.DESTINATARIO_DIGITALE,
                LegalFactKeyValues.SECONDO_DESTINATARIO_DIGITALE,
                LegalFactKeyValues.PRIMA_DATA,
                LegalFactKeyValues.SECONDA_DATA),
        LEGALFACT_NOTIFICA_PRESA_IN_CARICO(
                LegalFactKeyValues.IUN,
                LegalFactKeyValues.DATA_ATTESTAZIONE_OPPONIBILE,
                LegalFactKeyValues.DESTINATARIO,
                LegalFactKeyValues.MITTENTE,
                LegalFactKeyValues.CF_MITTENTE),
        LEGALFACT_NOTIFICA_PRESA_IN_CARICO_MULTIDESTINATARIO(
                LegalFactKeyValues.IUN,
                LegalFactKeyValues.DATA_ATTESTAZIONE_OPPONIBILE,
                LegalFactKeyValues.MITTENTE,
                LegalFactKeyValues.CF_MITTENTE,
                LegalFactKeyValues.DESTINATARIO,
                LegalFactKeyValues.DESTINATARI_ANALOGICI),
        LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO(
                LegalFactKeyValues.IUN,
                LegalFactKeyValues.DATA_ATTESTAZIONE_OPPONIBILE,
                LegalFactKeyValues.DESTINATARIO),
        LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO_DELEGATO(
                LegalFactKeyValues.IUN,
                LegalFactKeyValues.DATA_ATTESTAZIONE_OPPONIBILE,
                LegalFactKeyValues.DESTINATARIO,
                LegalFactKeyValues.DELEGATO);

        private final List<LegalFactKeyValues> legalFactKeyValuesList;
        LegalFactType(LegalFactKeyValues... keyValues){
            this.legalFactKeyValuesList = List.of(keyValues);
        }
    }

    @Getter
    enum LegalFactKeyValues {
        IUN("iun"),
        DATA_ATTESTAZIONE_OPPONIBILE("dataAttestazioneOpponibile"),
        DESTINATARIO("destinatario"),
        DESTINATARIO_DIGITALE("destinatarioDigitale"),
        SECONDO_DESTINATARIO_DIGITALE("secondoDestinatarioDigitale"),
        DELEGATO("delegato"),
        DESTINATARI_ANALOGICI("destinatariAnalogici"),
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

        LegalFactKeyValues(String field){
            this.field = field;
        }
    }
    HashMap<String, Object> extractSingleField(String source, IPnParserService.LegalFactKeyValues field, LegalFactType legalFactType) throws IOException, NoSuchFieldException, IllegalAccessException;
    IPnLegalFact extractAllField(String source, LegalFactType legalFactType) throws IOException;
}