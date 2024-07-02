package it.pagopa.pn.client.b2b.pa.parsing.config;

import it.pagopa.pn.client.b2b.pa.parsing.service.IPnParserService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;


@Getter
@Component
public class PnLegalFactTokens {
    private final PnLegalFactTokenProperty tokenProps;
    private final List<PnLegalFactTypeTokenGroup> fieldTokenList;


    public PnLegalFactTokens(PnLegalFactTokenProperty tokenProps) {
        this.tokenProps = tokenProps;
        this.fieldTokenList = Arrays.asList(
                new PnLegalFactTypeTokenGroup(
                        Arrays.asList(IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_DIGITALE,
                        IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_MANCATO_RECAPITO,
                        IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO,
                        IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO_DELEGATO),
                        IPnParserService.LegalFactField.IUN.getField(),
                        tokenProps.getIunStart(),
                        tokenProps.getIunEnd1()),
                new PnLegalFactTypeTokenGroup(
                        Arrays.asList(IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO,
                        IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO_MULTIDESTINATARIO),
                        IPnParserService.LegalFactField.IUN.getField(),
                        tokenProps.getIunStart(),
                        tokenProps.getIunEnd2()),
                new PnLegalFactTypeTokenGroup(
                        Arrays.asList(IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_DIGITALE,
                        IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO,
                        IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO_DELEGATO,
                        IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO,
                        IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO_MULTIDESTINATARIO,
                        IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_MANCATO_RECAPITO),
                        IPnParserService.LegalFactField.NOME_COGNOME_RAGIONE_SOCIALE.getField(),
                        tokenProps.getNomeCognomeRagioneSocialeStart1(),
                        tokenProps.getNomeCognomeRagioneSocialeEnd()),
                new PnLegalFactTypeTokenGroup(List.of(IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO_DELEGATO),
                        IPnParserService.LegalFactField.NOME_COGNOME_RAGIONE_SOCIALE.getField(),
                        tokenProps.getNomeCognomeRagioneSocialeStart2(),
                        tokenProps.getNomeCognomeRagioneSocialeEnd()),
                new PnLegalFactTypeTokenGroup(List.of(IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO),
                        IPnParserService.LegalFactField.CODICE_FISCALE.getField(),
                        tokenProps.getCodiceFiscaleStart(),
                        tokenProps.getCodiceFiscaleEnd1()),
                new PnLegalFactTypeTokenGroup(List.of(IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO_DELEGATO),
                        IPnParserService.LegalFactField.CODICE_FISCALE.getField(),
                        tokenProps.getCodiceFiscaleStart(),
                        tokenProps.getCodiceFiscaleEnd2()),
                new PnLegalFactTypeTokenGroup(Arrays.asList(IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO,
                        IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO_MULTIDESTINATARIO,
                        IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_DIGITALE,
                        IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_MANCATO_RECAPITO),
                        IPnParserService.LegalFactField.CODICE_FISCALE.getField(),
                        tokenProps.getCodiceFiscaleStart(),
                        tokenProps.getCodiceFiscaleEnd3()),
                new PnLegalFactTypeTokenGroup(List.of(IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO),
                        IPnParserService.LegalFactField.DATA_ATTESTAZIONE_OPPONIBILE.getField(),
                        tokenProps.getDataAttestazioneOpponibileStart(),
                        tokenProps.getDataAttestazioneOpponibileEnd1()),
                new PnLegalFactTypeTokenGroup(List.of(IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO_DELEGATO),
                        IPnParserService.LegalFactField.DATA_ATTESTAZIONE_OPPONIBILE.getField(),
                        tokenProps.getDataAttestazioneOpponibileStart(),
                        tokenProps.getDataAttestazioneOpponibileEnd2()),
                new PnLegalFactTypeTokenGroup(Arrays.asList(IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO,
                        IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO_MULTIDESTINATARIO),
                        IPnParserService.LegalFactField.DATA_ATTESTAZIONE_OPPONIBILE.getField(),
                        tokenProps.getDataAttestazioneOpponibileStart(),
                        tokenProps.getDataAttestazioneOpponibileEnd3()),
                new PnLegalFactTypeTokenGroup(List.of(IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_DIGITALE),
                        IPnParserService.LegalFactField.DATA_ATTESTAZIONE_OPPONIBILE.getField(),
                        tokenProps.getDataAttestazioneOpponibileStart(),
                        tokenProps.getDataAttestazioneOpponibileEnd4()),
                new PnLegalFactTypeTokenGroup(List.of(IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_MANCATO_RECAPITO),
                        IPnParserService.LegalFactField.DATA_ATTESTAZIONE_OPPONIBILE.getField(),
                        tokenProps.getDataAttestazioneOpponibileStart(),
                        tokenProps.getDataAttestazioneOpponibileEnd5()),
                new PnLegalFactTypeTokenGroup(Arrays.asList(IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO,
                        IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO_MULTIDESTINATARIO,
                        IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_DIGITALE,
                        IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_MANCATO_RECAPITO),
                        IPnParserService.LegalFactField.DOMICILIO_DIGITALE.getField(),
                        tokenProps.getDomicilioDigitaleStart(),
                        tokenProps.getDomicilioDigitaleEnd()),
                new PnLegalFactTypeTokenGroup(Arrays.asList(IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO,
                        IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO_MULTIDESTINATARIO),
                        IPnParserService.LegalFactField.TIPO_DOMICILIO_DIGITALE.getField(),
                        tokenProps.getTipologiaDomicilioDigitaleStart(),
                        tokenProps.getTipologiaDomicilioDigitaleEnd1()),
                new PnLegalFactTypeTokenGroup(List.of(IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_DIGITALE),
                        IPnParserService.LegalFactField.TIPO_DOMICILIO_DIGITALE.getField(),
                        tokenProps.getTipologiaDomicilioDigitaleStart(),
                        tokenProps.getTipologiaDomicilioDigitaleEnd2()),
                new PnLegalFactTypeTokenGroup(List.of(IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_MANCATO_RECAPITO),
                        IPnParserService.LegalFactField.TIPO_DOMICILIO_DIGITALE.getField(),
                        tokenProps.getTipologiaDomicilioDigitaleStart(),
                        tokenProps.getTipologiaDomicilioDigitaleEnd3()),
                new PnLegalFactTypeTokenGroup(List.of(IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_DOWNTIME),
                        IPnParserService.LegalFactField.DATA_ORA_DECORRENZA.getField(),
                        tokenProps.getDataDecorrenzaStart(),
                        tokenProps.getDataDecorrenzaEnd()),
                new PnLegalFactTypeTokenGroup(List.of(IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_DOWNTIME),
                        IPnParserService.LegalFactField.DATA_ORA_DECORRENZA.getField(),
                        tokenProps.getOraDecorrenzaStart(),
                        tokenProps.getOraDecorrenzaEnd()),
                new PnLegalFactTypeTokenGroup(List.of(IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_DOWNTIME),
                        IPnParserService.LegalFactField.DATA_ORA_FINE.getField(),
                        tokenProps.getDataFineDecorrenzaStart(),
                        tokenProps.getDataFineDecorrenzaEnd()),
                new PnLegalFactTypeTokenGroup(List.of(IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_DOWNTIME),
                        IPnParserService.LegalFactField.DATA_ORA_FINE.getField(),
                        tokenProps.getOraFineDecorrenzaStart(),
                        tokenProps.getOraFineDecorrenzaEnd()),
                new PnLegalFactTypeTokenGroup(List.of(IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO_MULTIDESTINATARIO),
                        IPnParserService.LegalFactField.INDIRIZZO_FISICO.getField(),
                        tokenProps.getIndirizzoFisicoStart(),
                        tokenProps.getIndirizzoFisicoEnd1()),
                new PnLegalFactTypeTokenGroup(Arrays.asList(IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO_MULTIDESTINATARIO,
                        IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO),
                        IPnParserService.LegalFactField.INDIRIZZO_FISICO.getField(),
                        tokenProps.getIndirizzoFisicoStart(),
                        tokenProps.getIndirizzoFisicoEnd2()),
                new PnLegalFactTypeTokenGroup(Arrays.asList(IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO,
                        IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO_MULTIDESTINATARIO),
                        IPnParserService.LegalFactField.MITTENTE.getField(),
                        tokenProps.getMittenteStart(),
                        tokenProps.getMittenteEnd()),
                new PnLegalFactTypeTokenGroup(Arrays.asList(IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO,
                        IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO_MULTIDESTINATARIO),
                        IPnParserService.LegalFactField.CF_MITTENTE.getField(),
                        tokenProps.getCfMittenteStart(),
                        tokenProps.getCfMittenteEnd()),
                new PnLegalFactTypeTokenGroup(List.of(IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_MANCATO_RECAPITO),
                        IPnParserService.LegalFactField.PRIMA_DATA.getField(),
                        tokenProps.getPrimaDataStart(),
                        tokenProps.getPrimaDataEnd()),
                new PnLegalFactTypeTokenGroup(List.of(IPnParserService.LegalFactType.LEGALFACT_NOTIFICA_MANCATO_RECAPITO),
                        IPnParserService.LegalFactField.SECONDA_DATA.getField(),
                        tokenProps.getSecondaDataStart(),
                        tokenProps.getSecondaDataEnd()));
    }

    public String getFieldByToken(String token) {
        return fieldTokenList.stream()
                .filter(pnTokenFieldGroup -> pnTokenFieldGroup.contains(token))
                .map(PnLegalFactTypeTokenGroup::getField)
                .findFirst()
                .orElse(null);
    }

    @Getter
    @AllArgsConstructor
    public class PnLegalFactTypeTokenGroup {
        private List<IPnParserService.LegalFactType> legalFactTypeList;
        private String field;
        private String tokenStart;
        private String tokenEnd;

        public boolean contains(String values) {
            return tokenStart.equals(values) || tokenEnd.equals(values);
        }
    }
}