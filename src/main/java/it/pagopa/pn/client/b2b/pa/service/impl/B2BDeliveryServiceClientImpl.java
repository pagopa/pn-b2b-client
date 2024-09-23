package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.api.RecipientReadB2BApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.FullReceivedNotificationV23;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.NotificationSearchResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.NotificationStatus;;
import it.pagopa.pn.client.b2b.pa.service.B2BDeliveryServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
public class B2BDeliveryServiceClientImpl implements B2BDeliveryServiceClient {

    private final String marioCucumberBearerToken;
    private final String marioGherkinBearerToken;
    private final String leonardoBearerToken;
    private final String gherkinSrlBearerToken;
    private final String cucumberSpaBearerToken;

    private final RestTemplate restTemplate;
    private final String basePath;

    private final RecipientReadB2BApi recipientReadB2BApi;

    public B2BDeliveryServiceClientImpl(RestTemplate restTemplate,
                                        @Value("${pn.delivery.base-url}") String basePath,
                                        @Value("${pn.bearer-token.user1}") String marioCucumberBearerToken,
                                        @Value("${pn.bearer-token.user2}") String marioGherkinBearerToken,
                                        @Value("${pn.bearer-token.user3}") String leonardoBearerToken,
                                        @Value("${pn.bearer-token.pg1}") String gherkinSrlBearerToken,
                                        @Value("${pn.bearer-token.pg2}") String cucumberSpaBearerToken) {
        this.marioCucumberBearerToken = marioCucumberBearerToken;
        this.marioGherkinBearerToken = marioGherkinBearerToken;
        this.leonardoBearerToken = leonardoBearerToken;
        this.gherkinSrlBearerToken = gherkinSrlBearerToken;
        this.cucumberSpaBearerToken = cucumberSpaBearerToken;
        this.restTemplate = restTemplate;
        this.basePath = basePath;
        this.recipientReadB2BApi = new RecipientReadB2BApi(newApiClient(restTemplate, basePath, cucumberSpaBearerToken));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String bearerToken) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.addDefaultHeader("Authorization","Bearer " + bearerToken);
        newApiClient.setBasePath(basePath);
        return newApiClient;
    }

    @Override
    public NotificationAttachmentDownloadMetadataResponse getReceivedNotificationAttachment(String iun, String attachmentName, UUID mandateId, Integer attachmentIdx) throws RestClientException {
        return recipientReadB2BApi.getReceivedNotificationAttachment(iun, attachmentName, mandateId, attachmentIdx);
    }

    @Override
    public NotificationAttachmentDownloadMetadataResponse getReceivedNotificationDocument(String iun, Integer docIdx, UUID mandateId) throws RestClientException {
        return recipientReadB2BApi.getReceivedNotificationDocument(iun, docIdx, mandateId);
    }

    @Override
    public FullReceivedNotificationV23 getReceivedNotificationV23(String iun, String mandateId) throws RestClientException {
        return recipientReadB2BApi.getReceivedNotificationV23(iun, mandateId);
    }

    @Override
    public NotificationSearchResponse searchReceivedDelegatedNotification(String startDate, String endDate, String senderId, String recipientId, String group, String iunMatch, NotificationStatus status, Integer size, String nextPagesKey) throws RestClientException {
        return recipientReadB2BApi.searchReceivedDelegatedNotification(startDate, endDate, senderId, recipientId, group, iunMatch, status, size, nextPagesKey);
    }

    @Override
    public NotificationSearchResponse searchReceivedNotification(String startDate, String endDate, String mandateId, String senderId, NotificationStatus status, String subjectRegExp, String iunMatch, Integer size, String nextPagesKey) {
        return recipientReadB2BApi.searchReceivedNotification(startDate, endDate, mandateId, senderId, status, subjectRegExp, iunMatch, size, nextPagesKey);
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        boolean operation = false;
        switch (bearerToken) {
            case USER_1 -> {
                this.recipientReadB2BApi.setApiClient(newApiClient(restTemplate, basePath, marioCucumberBearerToken));
                operation = true;
            }
            case USER_2 -> {
                this.recipientReadB2BApi.setApiClient(newApiClient(restTemplate, basePath, marioGherkinBearerToken));
                operation = true;
            }
            case USER_3 -> {
                this.recipientReadB2BApi.setApiClient(newApiClient(restTemplate, basePath, leonardoBearerToken));
                operation = true;
            }
            case PG_1 -> {
                this.recipientReadB2BApi.setApiClient(newApiClient(restTemplate, basePath, gherkinSrlBearerToken));
                operation = true;
            }
            case PG_2 -> {
                this.recipientReadB2BApi.setApiClient(newApiClient(restTemplate, basePath, cucumberSpaBearerToken));
                operation = true;
            }
            default -> throw new IllegalStateException("Unexpected value: " + bearerToken);
        }
        return operation;
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return null;
    }
}
