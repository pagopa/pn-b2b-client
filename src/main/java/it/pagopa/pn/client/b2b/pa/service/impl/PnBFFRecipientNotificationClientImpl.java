package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.pa.recipient.NotificationSentApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.recipient.NotificationReceivedApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffFullNotificationV1;
import it.pagopa.pn.client.b2b.pa.config.PnBaseUrlConfig;
import it.pagopa.pn.client.b2b.pa.config.PnBearerTokenConfigs;
import it.pagopa.pn.client.b2b.pa.config.PnBearerTokenExternalConfig;
import it.pagopa.pn.client.b2b.pa.service.IPnBFFRecipientNotificationClient;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class PnBFFRecipientNotificationClientImpl implements IPnBFFRecipientNotificationClient {
    private final NotificationReceivedApi notificationReceivedApi;
    private final NotificationSentApi notificationSentApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    private final String bearerTokenCom1;
    private final String bearerTokenCom2;
    private final String bearerTokenSON;
    private final String bearerTokenROOT;
    private final String bearerTokenGA;
    private final String marioCucumberBearerToken;
    private final String marioGherkinBearerToken;
    private final String leonardoBearerToken;
    private final String gherkinSrlBearerToken;
    private final String cucumberSpaBearerToken;

    @Autowired
    public PnBFFRecipientNotificationClientImpl(RestTemplate restTemplate,
                                                PnBearerTokenConfigs pnBearerTokenConfigs,
                                                PnBearerTokenExternalConfig pnBearerTokenExternalConfig,
                                                PnBaseUrlConfig pnBaseUrlConfig) {
        this.bearerTokenCom1 = pnBearerTokenExternalConfig.getPa1();
        this.bearerTokenCom2 = pnBearerTokenConfigs.getPg2();
        this.bearerTokenSON = pnBearerTokenExternalConfig.getPaSON();
        this.bearerTokenROOT = pnBearerTokenExternalConfig.getPaROOT();
        this.bearerTokenGA = pnBearerTokenExternalConfig.getPaGA();
        this.marioCucumberBearerToken = pnBearerTokenConfigs.getUser1();
        this.marioGherkinBearerToken = pnBearerTokenConfigs.getUser2();
        this.leonardoBearerToken = pnBearerTokenConfigs.getUser3();
        this.gherkinSrlBearerToken = pnBearerTokenConfigs.getPg1();
        this.cucumberSpaBearerToken = pnBearerTokenConfigs.getPg2();
        this.restTemplate = restTemplate;
        this.basePath = pnBaseUrlConfig.getWebApiExternalBaseUrl();
        this.notificationReceivedApi = new NotificationReceivedApi(newApiClientForRecipient(restTemplate, basePath, marioGherkinBearerToken));
        this.notificationSentApi = new NotificationSentApi(newApiClientForSender(restTemplate, basePath, pnBearerTokenExternalConfig.getPaGA()));
    }

    private static ApiClient newApiClientForRecipient(RestTemplate restTemplate, String basePath, String bearerToken) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("Authorization", "Bearer " + bearerToken);
        return newApiClient;
    }

    private static it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.pa.ApiClient newApiClientForSender(RestTemplate restTemplate, String basePath, String bearerToken) {
        it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.pa.ApiClient apiClient = new it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.pa.ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.addDefaultHeader("Authorization", "Bearer " + bearerToken);
        return apiClient;
    }

    @Override
    public ResponseEntity<BffFullNotificationV1> getReceivedNotificationV1WithHttpInfoForRecipient(String iun) {
        return notificationReceivedApi.getReceivedNotificationV1WithHttpInfo(iun, null);
    }

    @Override
    public ResponseEntity<it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffFullNotificationV1> getSentNotificationV1WithHttpInfoForSender(String iun) {
        return notificationSentApi.getSentNotificationV1WithHttpInfo(iun);
    }

    @Override
    public void setRecipientBearerToken(SettableBearerToken.BearerTokenType bearerToken) {
        switch (bearerToken) {
            case USER_1 -> {
                this.notificationReceivedApi.setApiClient(newApiClientForRecipient(restTemplate, basePath, marioCucumberBearerToken));
            }
            case USER_2 -> {
                this.notificationReceivedApi.setApiClient(newApiClientForRecipient(restTemplate, basePath, marioGherkinBearerToken));
            }
            case USER_3 -> {
                this.notificationReceivedApi.setApiClient(newApiClientForRecipient(restTemplate, basePath, leonardoBearerToken));
            }
            case PG_1 -> {
                this.notificationReceivedApi.setApiClient(newApiClientForRecipient(restTemplate, basePath, gherkinSrlBearerToken));
            }
            case PG_2 -> {
                this.notificationReceivedApi.setApiClient(newApiClientForRecipient(restTemplate, basePath, cucumberSpaBearerToken));
            }
            default -> throw new IllegalStateException("Unexpected value: " + bearerToken);
        }
    }

    @Override
    public void setSenderBearerToken(SettableBearerToken.BearerTokenType bearerToken) {
        switch (bearerToken) {
            case MVP_1 -> {
                this.notificationSentApi.setApiClient(newApiClientForSender(this.restTemplate, basePath, bearerTokenCom1));
            }
            case MVP_2 -> {
                this.notificationSentApi.setApiClient(newApiClientForSender(restTemplate, basePath, bearerTokenCom2));
            }
            case GA -> {
                this.notificationSentApi.setApiClient(newApiClientForSender(restTemplate, basePath, bearerTokenGA));
            }
            case SON -> {
                this.notificationSentApi.setApiClient(newApiClientForSender(restTemplate, basePath, bearerTokenSON));
            }
            case ROOT -> {
                this.notificationSentApi.setApiClient(newApiClientForSender(restTemplate, basePath, bearerTokenROOT));
            }
            default -> throw new IllegalStateException("Unexpected value: " + bearerToken);
        }
    }

}
