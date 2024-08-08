package it.pagopa.pn.client.b2b.pa.parsing.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@Component
@ConfigurationProperties(prefix = "pn.parser.legalfact.config.token", ignoreUnknownFields = false)
public class PnLegalFactTokenProperty {
    @Value("${pn.parser.legalfact.config.cleanup-delegato}")
    private String cleanupDelegato;
    @Value("${pn.parser.legalfact.config.cleanup-destinatario}")
    private String cleanupDestinatario;
    @Value("${pn.parser.legalfact.config.cleanup-regex-nsbp}")
    private String regexCleanupNsbp;
    @Value("${pn.parser.legalfact.config.cleanup-regex-carriage-newline}")
    private String regexCarriageNewline;

    private String iunStart;
    private String iunEnd1;
    private String iunEnd2;

    private String nomeCognomeRagioneSocialeStart1;
    private String nomeCognomeRagioneSocialeStart2;
    private String nomeCognomeRagioneSocialeEnd;

    private String codiceFiscaleStart;
    private String codiceFiscaleEnd1;
    private String codiceFiscaleEnd2;
    private String codiceFiscaleEnd3;

    private String dataAttestazioneOpponibileStart;
    private String dataAttestazioneOpponibileEnd1;
    private String dataAttestazioneOpponibileEnd2;
    private String dataAttestazioneOpponibileEnd3;
    private String dataAttestazioneOpponibileEnd4;
    private String dataAttestazioneOpponibileEnd5;

    private String domicilioDigitaleStart;
    private String domicilioDigitaleEnd;

    private String tipologiaDomicilioDigitaleStart;
    private String tipologiaDomicilioDigitaleEnd1;
    private String tipologiaDomicilioDigitaleEnd2;
    private String tipologiaDomicilioDigitaleEnd3;

    private String dataDecorrenzaStart;
    private String dataDecorrenzaEnd;
    private String oraDecorrenzaStart;
    private String oraDecorrenzaEnd;

    private String dataFineDecorrenzaStart;
    private String dataFineDecorrenzaEnd;
    private String oraFineDecorrenzaStart;
    private String oraFineDecorrenzaEnd;

    private String indirizzoFisicoStart;
    private String indirizzoFisicoEnd1;
    private String indirizzoFisicoEnd2;

    private String mittenteStart;
    private String mittenteEnd;

    private String cfMittenteStart;
    private String cfMittenteEnd;

    private String primaDataStart;
    private String primaDataEnd;

    private String secondaDataStart;
    private String secondaDataEnd;
}