package it.pagopa.pn.client.b2b.pa.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.api.RecipientReadB2BApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.ApiClient;
import it.pagopa.pn.client.b2b.pa.exception.PnB2bException;
import it.pagopa.pn.client.b2b.pa.service.IPnWebRecipientClient;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.*;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model_v1.FullReceivedNotification;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model_v1.NotificationAttachmentDownloadMetadataResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class B2BRecipientExternalClientImpl implements IPnWebRecipientClient {
    private final String gherkinSrlBearerToken;
    private final String cucumberSpaBearerToken;
    private final RestTemplate restTemplate;
    private final String basePath;
    private final RecipientReadB2BApi recipientReadB2BApi;
    private BearerTokenType bearerTokenSetted;

    public B2BRecipientExternalClientImpl(RestTemplate restTemplate,
                                          @Value("${pn.external.dest.base-url}") String basePath,
                                          @Value("${pn.bearer-token.pg1}") String gherkinSrlBearerToken,
                                          @Value("${pn.bearer-token-b2b.pg2}") String cucumberSpaBearerToken) {
        this.gherkinSrlBearerToken = gherkinSrlBearerToken;
        this.cucumberSpaBearerToken = cucumberSpaBearerToken;
        this.restTemplate = restTemplate;
        this.basePath = basePath;
        this.recipientReadB2BApi = new RecipientReadB2BApi(newApiClient(restTemplate, basePath, cucumberSpaBearerToken));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String bearerToken) {
        ApiClient newApiClient = new ApiClient( restTemplate );
        newApiClient.setBasePath( basePath );
        newApiClient.setBearerToken(bearerToken);
        return newApiClient;
    }

    @Override
    public FullReceivedNotificationV23 getReceivedNotification(String iun, String mandateId) throws RestClientException {
        return deepCopy(recipientReadB2BApi.getReceivedNotificationV23(iun, mandateId), FullReceivedNotificationV23.class);
    }

    @Override
    public NotificationAttachmentDownloadMetadataResponse getReceivedNotificationAttachment(String iun, String attachmentName, UUID mandateId) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FullReceivedNotification getReceivedNotificationV1(String iun, String mandateId) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model_v2.FullReceivedNotification getReceivedNotificationV2(String iun, String mandateId) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.NotificationAttachmentDownloadMetadataResponse getReceivedNotificationAttachment(String iun, String attachmentName, UUID mandateId, Integer attachmentIdx) throws RestClientException {
        return deepCopy(recipientReadB2BApi.getReceivedNotificationAttachment(iun, attachmentName, mandateId, null), it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.NotificationAttachmentDownloadMetadataResponse.class);
    }

    @Override
    public it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.NotificationAttachmentDownloadMetadataResponse getReceivedNotificationDocument(String iun, Integer docIdx, UUID mandateId) throws RestClientException {
        return deepCopy(recipientReadB2BApi.getReceivedNotificationDocument(iun, docIdx, mandateId), it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.NotificationAttachmentDownloadMetadataResponse.class);
    }

    @Override
    public NotificationSearchResponse searchReceivedDelegatedNotification(OffsetDateTime startDate, OffsetDateTime endDate, String recipientId, String group, String senderId, NotificationStatus status, String iunMatch, Integer size, String nextPagesKey) throws RestClientException {
        it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.NotificationSearchResponse response = recipientReadB2BApi.searchReceivedDelegatedNotification(
                startDate.toString(), endDate.toString(), senderId,recipientId, group, iunMatch,  convertStatus(status), size, nextPagesKey);
        return deepCopy(response, NotificationSearchResponse.class);
    }

    @Override
    public NotificationSearchResponse searchReceivedNotification(OffsetDateTime startDate, OffsetDateTime endDate, String mandateId, String senderId, NotificationStatus status, String subjectRegExp, String iunMatch, Integer size, String nextPagesKey) throws RestClientException {
        it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.NotificationSearchResponse response = recipientReadB2BApi.searchReceivedNotification(startDate.toString(), endDate.toString(), mandateId,
                senderId, convertStatus(status), subjectRegExp, iunMatch, size, nextPagesKey);
        return deepCopy(response, NotificationSearchResponse.class);
    }

    @Override
    public LegalFactDownloadMetadataResponse getLegalFact(String iun, LegalFactCategory legalFactType, String legalFactId) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public DocumentDownloadMetadataResponse getDocumentsWeb(String iun, DocumentCategory documentType, String documentId, String mandateId) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        switch (bearerToken) {
            case PG_1 -> {
                this.recipientReadB2BApi.setApiClient(newApiClient(restTemplate, basePath, gherkinSrlBearerToken));
                this.bearerTokenSetted = BearerTokenType.PG_1;
            }
            case PG_2 -> {
                this.recipientReadB2BApi.setApiClient(newApiClient(restTemplate, basePath, cucumberSpaBearerToken));
                this.bearerTokenSetted = BearerTokenType.PG_2;
            }
            default -> throw new IllegalStateException("Unexpected value: " + bearerToken);
        }
        return true;
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return this.bearerTokenSetted;
    }

    private it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.NotificationStatus convertStatus(NotificationStatus status) {
        return Optional.ofNullable(status)
                .map(NotificationStatus::getValue)
                .map(it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.NotificationStatus::fromValue)
                .orElse(null);
    }

    private <T> T deepCopy( Object obj, Class<T> toClass) {
        ObjectMapper objMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
        try {
            String json = objMapper.writeValueAsString( obj );
            return objMapper.readValue( json, toClass );
        } catch (JsonProcessingException exc) {
            throw new PnB2bException(exc.getMessage());
        }
    }
}