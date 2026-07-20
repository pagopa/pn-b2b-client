package it.pagopa.pn.cucumber.steps.informalNotification.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InformalApiKeyProvider {

    @Value("${pn.external.api-key}")
    private String apiKeyMvp1;

    @Value("${pn.external.api-key-2}")
    private String apiKeyMvp2;

    @Value("${pn.external.api-key-GA}")
    private String apiKeyGa;

    @Value("${pn.external.api-key-ROOT}")
    private String apiKeyRoot;

    public String getApiKey(String paName) {

        return switch (paName) {
            case "Comune_1" -> apiKeyMvp1;
            case "Comune_2" -> apiKeyMvp2;
            case "Comune_Multi" -> apiKeyGa;
            case "Comune_Root" -> apiKeyRoot;
            default -> throw new IllegalArgumentException(
                    "ApiKey non configurata per " + paName);
        };
    }
}
