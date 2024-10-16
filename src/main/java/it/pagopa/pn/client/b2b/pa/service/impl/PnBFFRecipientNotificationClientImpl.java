package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.pa.recipient.NotificationSentApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.recipient.NotificationReceivedApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffFullNotificationV1;
import it.pagopa.pn.client.b2b.pa.service.IPnBFFRecipientNotificationClient;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    public PnBFFRecipientNotificationClientImpl(RestTemplate restTemplate,
                                                @Value("${pn.external.bearer-token-pa-1}") String bearerTokenCom1,
                                                @Value("${pn.external.bearer-token-pa-2}") String bearerTokenCom2,
                                                @Value("${pn.external.bearer-token-pa-SON}") String bearerTokenSON,
                                                @Value("${pn.external.bearer-token-pa-ROOT}") String bearerTokenROOT,
                                                @Value("${pn.external.bearer-token-pa-GA}") String bearerTokenGA,
                                                @Value("${pn.bearer-token.user1}") String marioCucumberBearerToken,
                                                @Value("${pn.bearer-token.user2}") String marioGherkinBearerToken,
                                                @Value("${pn.bearer-token.user3}") String leonardoBearerToken,
                                                @Value("${pn.bearer-token.user5}") String dinoBearerToken,
                                                @Value("${pn.bearer-token.scaduto}") String userBearerTokenScaduto,
                                                @Value("${pn.bearer-token.pg1}") String gherkinSrlBearerToken,
                                                @Value("${pn.bearer-token.pg2}") String cucumberSpaBearerToken,
                                                @Value("${pn.webapi.external.base-url}") String basePath,
                                                @Value("${pn.bearer-token.user2}") String berearTokenRecipient,
                                                @Value("${pn.external.bearer-token-pa-GA}") String berearTokenSender) {
        this.bearerTokenCom1 = bearerTokenCom1;
        this.bearerTokenCom2 = bearerTokenCom2;
        this.bearerTokenSON = bearerTokenSON;
        this.bearerTokenROOT = bearerTokenROOT;
        this.bearerTokenGA = bearerTokenGA;
        this.marioCucumberBearerToken = marioCucumberBearerToken;
        this.marioGherkinBearerToken = marioGherkinBearerToken;
        this.leonardoBearerToken = leonardoBearerToken;
        this.gherkinSrlBearerToken = gherkinSrlBearerToken;
        this.cucumberSpaBearerToken = cucumberSpaBearerToken;
        this.restTemplate = restTemplate;
        this.basePath = basePath;
        this.notificationReceivedApi = new NotificationReceivedApi(newApiClientForRecipient(restTemplate, basePath, berearTokenRecipient));
        this.notificationSentApi = new NotificationSentApi(newApiClientForSender(restTemplate, basePath, berearTokenSender));
    }

    private static ApiClient newApiClientForRecipient(RestTemplate restTemplate, String basePath, String bearerToken) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("Authorization","Bearer " + bearerToken);
        return newApiClient;
    }

    private static it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.pa.ApiClient newApiClientForSender(RestTemplate restTemplate, String basePath, String bearerToken) {
        it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.pa.ApiClient apiClient = new it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.pa.ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.addDefaultHeader("Authorization","Bearer "+bearerToken);
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
                this.notificationSentApi.setApiClient( newApiClientForSender( this.restTemplate, basePath, bearerTokenCom1));
            }
            case MVP_2 -> {
                this.notificationSentApi.setApiClient( newApiClientForSender( restTemplate, basePath, bearerTokenCom2));
            }
            case GA -> {
                this.notificationSentApi.setApiClient( newApiClientForSender( restTemplate, basePath, bearerTokenGA));
            }
            case SON -> {
                this.notificationSentApi.setApiClient( newApiClientForSender( restTemplate, basePath, bearerTokenSON));
            }
            case ROOT -> {
                this.notificationSentApi.setApiClient( newApiClientForSender( restTemplate, basePath, bearerTokenROOT));
            }
            default -> throw new IllegalStateException("Unexpected value: " + bearerToken);
        }
    }

}
