package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.config.PnBaseUrlConfig;
import it.pagopa.pn.client.b2b.pa.config.PnBearerTokenExternalConfig;
import it.pagopa.pn.client.b2b.pa.config.PnExternalApiKeyConfig;
import it.pagopa.pn.client.b2b.pa.service.IPnWebPaClient;
import it.pagopa.pn.client.web.generated.openapi.clients.webPa.ApiClient;
import it.pagopa.pn.client.web.generated.openapi.clients.webPa.api.SenderReadWebApi;
import it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.NotificationSearchResponse;
import it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.NotificationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;


@Component
public class PnWebPaClientImpl implements IPnWebPaClient {
    private final SenderReadWebApi senderReadWebApi;
    private final RestTemplate restTemplate;
    private final String basePath;
    private final String userAgent;
    private final String bearerTokenCom1;
    private final String bearerTokenCom2;
    private final String bearerTokenSON;
    private final String bearerTokenROOT;
    private final String bearerTokenGA;
    private BearerTokenType bearerTokenSetted;

    @Autowired
    public PnWebPaClientImpl(RestTemplate restTemplate,
                             PnBearerTokenExternalConfig pnBearerTokenExternalConfig,
                             PnBaseUrlConfig pnBaseUrlConfig,
                             PnExternalApiKeyConfig pnExternalApiKeyConfig) {
        this.bearerTokenCom1 = pnBearerTokenExternalConfig.getPa1();
        this.bearerTokenCom2 = pnBearerTokenExternalConfig.getPa2();
        this.bearerTokenSON = pnBearerTokenExternalConfig.getPaSON();
        this.bearerTokenROOT = pnBearerTokenExternalConfig.getPaROOT();
        this.bearerTokenGA = pnBearerTokenExternalConfig.getPaGA();
        this.restTemplate = restTemplate;
        this.basePath = pnBaseUrlConfig.getWebApiExternalBaseUrl();
        this.userAgent = pnExternalApiKeyConfig.getUserAgent();
        this.senderReadWebApi = new SenderReadWebApi(newApiClient(restTemplate, basePath, bearerTokenCom1, userAgent));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String bearerToken, String userAgent) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("user-agent", userAgent);
        newApiClient.addDefaultHeader("Authorization", "Bearer " + bearerToken);
        return newApiClient;
    }

    @Override
    public NotificationSearchResponse searchSentNotification(OffsetDateTime startDate, OffsetDateTime endDate, String recipientId, NotificationStatus status, String subjectRegExp, String iunMatch, Integer size, String nextPagesKey) throws RestClientException {
        return senderReadWebApi.searchSentNotification(startDate, endDate, recipientId, status, subjectRegExp, iunMatch, size, nextPagesKey);
    }


    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        boolean beenSet = false;
        switch (bearerToken) {
            case MVP_1 -> {
                this.senderReadWebApi.setApiClient(newApiClient(restTemplate, basePath, bearerTokenCom1, userAgent));
                beenSet = true;
            }
            case MVP_2 -> {
                this.senderReadWebApi.setApiClient(newApiClient(restTemplate, basePath, bearerTokenCom2, userAgent));
                beenSet = true;
            }
            case GA -> {
                this.senderReadWebApi.setApiClient(newApiClient(restTemplate, basePath, bearerTokenGA, userAgent));
                beenSet = true;
            }
            case SON -> {
                this.senderReadWebApi.setApiClient(newApiClient(restTemplate, basePath, bearerTokenSON, userAgent));
                beenSet = true;
            }
            case ROOT -> {
                this.senderReadWebApi.setApiClient(newApiClient(restTemplate, basePath, bearerTokenROOT, userAgent));
                beenSet = true;
            }
            default -> throw new IllegalStateException("Unexpected value: " + bearerToken);
        }
        return beenSet;
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return this.bearerTokenSetted;
    }

}