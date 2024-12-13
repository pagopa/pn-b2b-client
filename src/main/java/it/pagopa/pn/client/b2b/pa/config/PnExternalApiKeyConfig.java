package it.pagopa.pn.client.b2b.pa.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class PnExternalApiKeyConfig {

    @Value("${pn.external.api-key}")
    private String apiKeyMvp1;
    @Value("${pn.external.api-key-2}")
    private String apiKeyMvp2;
    @Value("${pn.external.api-key-GA}")
    private String apiKeyGa;
    @Value("${pn.external.api-key-SON}")
    private String apiKeySON;
    @Value("${pn.external.api-key-ROOT}")
    private String apiKeyROOT;
    @Value("${pn.external.appio.api-key}")
    private String appIoApiKey;
    @Value("${pn.external.api-key-taxID}")
    private String senderTaxId1;
    @Value("${pn.external.api-key-2-taxID}")
    private String senderTaxId2;
    @Value("${pn.external.api-key-GA-taxID}")
    private String apiKeyTaxIdGA;
    @Value("${pn.external.api-key-SON-taxID}")
    private String apiKeyTaxIdSON;
    @Value("${pn.external.api-key-ROOT-taxID}")
    private String apiKeyTaxIdROOT;
    @Value("${pn.consolidatore.api.key}")
    private String consolidatoreApiKey;
    @Value("${pn.external.api-subscription-key}")
    private String subscriptionApiKey;
    @Value("${pn.external.api-keys.service-desk}")
    private String serviceDeskApiKey;
    @Value("${pn.webapi.external.user-agent}")
    private String userAgent;
}
