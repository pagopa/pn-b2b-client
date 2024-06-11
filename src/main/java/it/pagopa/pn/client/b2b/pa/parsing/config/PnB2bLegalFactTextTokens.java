package it.pagopa.pn.client.b2b.pa.parsing.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@Component
@ConfigurationProperties(prefix = "pn.pdf.configuration", ignoreUnknownFields = false)
public class PnB2bLegalFactTextTokens {
    private String nomeCognomeRagioneSociale;
    private String iun;
    private String codiceFiscale;
    private String codiceFiscalePartitaIva;
    private String domicilioDigitale;
    private String tipoDomicilioDigitale;
    private String tipoDomicilioDigitaleMancatoRecapito;
    private String tipoDomicilioDigitaleNotificaPresaInCarico;
    private String dataOraDecorrenza;
    private String dataOraFine;
    private String indirizzoFisicoNotificaPresaInCarico;
    private String indirizzoFisicoNotificaPresaInCaricoMultiDestinatario;
    private String dataAttestazioneOpponibile;
    private String mittente;
    private String cfMittente;
}