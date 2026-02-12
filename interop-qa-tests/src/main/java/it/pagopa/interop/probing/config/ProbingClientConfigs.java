package it.pagopa.interop.probing.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class ProbingClientConfigs {

    /**
     * Token da usare per le probing-api, non possiamo staccarlo in autonomia,
     * bisogna fornire una client_assertion al team dev il quale ci fornirà un token a lunga scadenza (3 settimane)
     */
    private String bearerTokenKms;

    private String baseUrl;

    /**
     * Token da usare per le probing-statistics-api, non possiamo staccarlo in autonomia,
     * il flow cognito è SRP(Secure Remote Password) e non abbiamo tutte le info, AWS QA non ha nessun sistema cognito dichiarato
     * ed evidentemente punta altrove.
     * <p>
     * Il token lo si prende da FE facendo accesso con le opportune credenziali, dura 1 giorno.
     */
    private String bearerTokenTelemetry;

    public ProbingClientConfigs(
            @Value("${pn.interop.probing.base-url}") String baseUrl,
            @Value("${pn.interop.probing.bearer-token-kms}") String bearerTokenKms,
            @Value("${pn.interop.probing.bearer-token-telemetry}") String bearerTokenTelemetry
    ) {
        this.baseUrl = baseUrl;
        this.bearerTokenKms = bearerTokenKms;
        this.bearerTokenTelemetry = bearerTokenTelemetry;
    }
}