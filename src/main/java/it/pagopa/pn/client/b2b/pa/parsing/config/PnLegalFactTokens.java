package it.pagopa.pn.client.b2b.pa.parsing.config;

import static it.pagopa.pn.client.b2b.pa.parsing.parser.IPnParserLegalFact.*;
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
                        Arrays.asList(LegalFactType.LEGALFACT_NOTIFICA_DIGITALE,
                        LegalFactType.LEGALFACT_NOTIFICA_MANCATO_RECAPITO,
                        LegalFactType.LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO,
                        LegalFactType.LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO_DELEGATO,
                        LegalFactType.LEGALFACT_NOTIFICATION_CANCELLED,
                        LegalFactType.LEGALFACT_ANALOG_FAILURE,
                        LegalFactType.LEGALFACT_NOTIFICATION_AAR,
                        LegalFactType.LEGALFACT_NOTIFICATION_AAR_RADD_ALT,
                        LegalFactType.LEGALFACT_ANALOG_FEEDBACK_AVAILABILITY_STATEMENT,
                        LegalFactType.LEGALFACT_INFORMAL_ANALOG_COMMUNICATION,
                        LegalFactType.LEGALFACT_ANALOG_DELIVERY_WORKFLOW_TIMEOUT_LEGAL_FACT),
                        tokenProps.getIunStart(),
                        tokenProps.getIunEnd1()),
                new PnLegalFactTypeTokenGroup(
                        Arrays.asList(LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO,
                        LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO_MULTIDESTINATARIO),
                        tokenProps.getIunStart(),
                        tokenProps.getIunEnd2()),
                new PnLegalFactTypeTokenGroup(
                        Arrays.asList(LegalFactType.LEGALFACT_NOTIFICA_DIGITALE,
                        LegalFactType.LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO,
                        LegalFactType.LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO_DELEGATO,
                        LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO,
                        LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO_MULTIDESTINATARIO,
                        LegalFactType.LEGALFACT_NOTIFICA_MANCATO_RECAPITO,
                        LegalFactType.LEGALFACT_NOTIFICATION_CANCELLED,
                        LegalFactType.LEGALFACT_ANALOG_DELIVERY_WORKFLOW_TIMEOUT_LEGAL_FACT),
                        tokenProps.getNomeCognomeRagioneSocialeStart1(),
                        tokenProps.getNomeCognomeRagioneSocialeEnd()),
                new PnLegalFactTypeTokenGroup(List.of(LegalFactType.LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO_DELEGATO),
                        tokenProps.getNomeCognomeRagioneSocialeStart2(),
                        tokenProps.getNomeCognomeRagioneSocialeEnd()),
                new PnLegalFactTypeTokenGroup(List.of(LegalFactType.LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO),
                        tokenProps.getCodiceFiscaleStart(),
                        tokenProps.getCodiceFiscaleEnd1()),
                new PnLegalFactTypeTokenGroup(List.of(LegalFactType.LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO_DELEGATO),
                        tokenProps.getCodiceFiscaleStart(),
                        tokenProps.getCodiceFiscaleEnd2()),
                new PnLegalFactTypeTokenGroup(Arrays.asList(LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO,
                        LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO_MULTIDESTINATARIO,
                        LegalFactType.LEGALFACT_NOTIFICA_DIGITALE,
                        LegalFactType.LEGALFACT_NOTIFICA_MANCATO_RECAPITO,
                        LegalFactType.LEGALFACT_NOTIFICATION_CANCELLED,
                        LegalFactType.LEGALFACT_NOTIFICATION_AAR,
                        LegalFactType.LEGALFACT_NOTIFICATION_AAR_RADD_ALT,
                        LegalFactType.LEGALFACT_INFORMAL_ANALOG_COMMUNICATION),
                        tokenProps.getCodiceFiscaleStart(),
                        tokenProps.getCodiceFiscaleEnd3()),
                new PnLegalFactTypeTokenGroup(List.of(LegalFactType.LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO),
                        tokenProps.getDataAttestazioneOpponibileStart(),
                        tokenProps.getDataAttestazioneOpponibileEnd1()),
                new PnLegalFactTypeTokenGroup(List.of(LegalFactType.LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO_DELEGATO),
                        tokenProps.getDataAttestazioneOpponibileStart(),
                        tokenProps.getDataAttestazioneOpponibileEnd2()),
                new PnLegalFactTypeTokenGroup(Arrays.asList(LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO,
                        LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO_MULTIDESTINATARIO,
                        LegalFactType.LEGALFACT_NOTIFICATION_CANCELLED),
                        tokenProps.getDataAttestazioneOpponibileStart(),
                        tokenProps.getDataAttestazioneOpponibileEnd3()),
                new PnLegalFactTypeTokenGroup(List.of(LegalFactType.LEGALFACT_NOTIFICA_DIGITALE),
                        tokenProps.getDataAttestazioneOpponibileStart(),
                        tokenProps.getDataAttestazioneOpponibileEnd4()),
                new PnLegalFactTypeTokenGroup(List.of(LegalFactType.LEGALFACT_NOTIFICA_MANCATO_RECAPITO),
                        tokenProps.getDataAttestazioneOpponibileStart(),
                        tokenProps.getDataAttestazioneOpponibileEnd5()),
                new PnLegalFactTypeTokenGroup(Arrays.asList(LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO,
                        LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO_MULTIDESTINATARIO,
                        LegalFactType.LEGALFACT_NOTIFICA_DIGITALE,
                        LegalFactType.LEGALFACT_NOTIFICA_MANCATO_RECAPITO),
                        tokenProps.getDomicilioDigitaleStart(),
                        tokenProps.getDomicilioDigitaleEnd()),
                new PnLegalFactTypeTokenGroup(Arrays.asList(LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO,
                        LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO_MULTIDESTINATARIO),
                        tokenProps.getTipologiaDomicilioDigitaleStart(),
                        tokenProps.getTipologiaDomicilioDigitaleEnd1()),
                new PnLegalFactTypeTokenGroup(List.of(LegalFactType.LEGALFACT_NOTIFICA_DIGITALE),
                        tokenProps.getTipologiaDomicilioDigitaleStart(),
                        tokenProps.getTipologiaDomicilioDigitaleEnd2()),
                new PnLegalFactTypeTokenGroup(List.of(LegalFactType.LEGALFACT_NOTIFICA_MANCATO_RECAPITO),
                        tokenProps.getTipologiaDomicilioDigitaleStart(),
                        tokenProps.getTipologiaDomicilioDigitaleEnd3()),
                new PnLegalFactTypeTokenGroup(List.of(LegalFactType.LEGALFACT_NOTIFICA_DOWNTIME),
                        tokenProps.getDataDecorrenzaStart(),
                        tokenProps.getDataDecorrenzaEnd()),
                new PnLegalFactTypeTokenGroup(List.of(LegalFactType.LEGALFACT_NOTIFICA_DOWNTIME),
                        tokenProps.getOraDecorrenzaStart(),
                        tokenProps.getOraDecorrenzaEnd()),
                new PnLegalFactTypeTokenGroup(List.of(LegalFactType.LEGALFACT_NOTIFICA_DOWNTIME),
                        tokenProps.getDataFineDecorrenzaStart(),
                        tokenProps.getDataFineDecorrenzaEnd()),
                new PnLegalFactTypeTokenGroup(List.of(LegalFactType.LEGALFACT_NOTIFICA_DOWNTIME),
                        tokenProps.getOraFineDecorrenzaStart(),
                        tokenProps.getOraFineDecorrenzaEnd()),
                new PnLegalFactTypeTokenGroup(List.of(LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO_MULTIDESTINATARIO),
                        tokenProps.getIndirizzoFisicoStart(),
                        tokenProps.getIndirizzoFisicoEnd1()),
                new PnLegalFactTypeTokenGroup(Arrays.asList(LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO_MULTIDESTINATARIO,
                        LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO),
                        tokenProps.getIndirizzoFisicoStart(),
                        tokenProps.getIndirizzoFisicoEnd2()),
                new PnLegalFactTypeTokenGroup(Arrays.asList(LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO,
                        LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO_MULTIDESTINATARIO,
                        LegalFactType.LEGALFACT_NOTIFICATION_CANCELLED),
                        tokenProps.getMittenteStart(),
                        tokenProps.getMittenteEnd()),
                new PnLegalFactTypeTokenGroup(Arrays.asList(LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO,
                        LegalFactType.LEGALFACT_NOTIFICA_PRESA_IN_CARICO_MULTIDESTINATARIO),
                        tokenProps.getCfMittenteStart(),
                        tokenProps.getCfMittenteEnd()),
                new PnLegalFactTypeTokenGroup(List.of(LegalFactType.LEGALFACT_NOTIFICA_MANCATO_RECAPITO),
                        tokenProps.getPrimaDataStart(),
                        tokenProps.getPrimaDataEnd()),
                new PnLegalFactTypeTokenGroup(List.of(LegalFactType.LEGALFACT_NOTIFICA_MANCATO_RECAPITO),
                        tokenProps.getSecondaDataStart(),
                        tokenProps.getSecondaDataEnd()));
    }

    @Getter
    @AllArgsConstructor
    public class PnLegalFactTypeTokenGroup {
        private List<LegalFactType> legalFactTypeList;
        private String tokenStart;
        private String tokenEnd;

        public boolean contains(String values) {
            return tokenStart.equals(values) || tokenEnd.equals(values);
        }
    }
}