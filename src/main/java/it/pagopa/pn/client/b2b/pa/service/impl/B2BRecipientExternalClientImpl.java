package it.pagopa.pn.client.b2b.pa.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.api.RecipientReadB2BApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.LegalNotificationSearchResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.deliverypushb2b.api.LegalFactsApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.deliverypushb2b.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffDocumentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffFullNotificationV1;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffLegalFactId;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.NotificationStatusV26;
import it.pagopa.pn.client.b2b.pa.exception.PnB2bException;
import it.pagopa.pn.client.b2b.pa.service.IPnWebRecipientClient;
import it.pagopa.pn.client.b2b.pa.wrapper.BundleFullReceivedNotification;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.v25.model.LegalFactCategory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class B2BRecipientExternalClientImpl implements IPnWebRecipientClient {

    private final String marioCucumberBearerToken;
    private final String marioGherkinBearerToken;
    private final String leonardoBearerToken;
    private final String gherkinSrlBearerToken;
    private final String cucumberSpaBearerToken;
    private final RestTemplate restTemplate;
    private final String webBasePath;
    private final String b2bBasePath;
    private final RecipientReadB2BApi recipientReadB2BApi;
    private final LegalFactsApi legalFactsApi;
    private BearerTokenType bearerTokenSetted;

    public B2BRecipientExternalClientImpl(RestTemplate restTemplate,
                                          @Value("${pn.webapi.external.base-url}") String webBasePath,
                                          @Value("${pn.external.dest.base-url}") String b2bBasePath,
                                          @Value("${pn.bearer-token.user1}") String marioCucumberBearerToken,
                                          @Value("${pn.bearer-token.user2}") String marioGherkinBearerToken,
                                          @Value("${pn.bearer-token.user3}") String leonardoBearerToken,
                                          @Value("${pn.bearer-token-b2b.pg1}") String gherkinSrlBearerToken,
                                          @Value("${pn.bearer-token-b2b.pg2}") String cucumberSpaBearerToken) {
        this.marioCucumberBearerToken = marioCucumberBearerToken;
        this.marioGherkinBearerToken = marioGherkinBearerToken;
        this.leonardoBearerToken = leonardoBearerToken;
        this.gherkinSrlBearerToken = gherkinSrlBearerToken;
        this.cucumberSpaBearerToken = cucumberSpaBearerToken;
        this.restTemplate = restTemplate;
        this.webBasePath = webBasePath;
        this.b2bBasePath = b2bBasePath;
        this.bearerTokenSetted = BearerTokenType.PG_1;
        this.recipientReadB2BApi = new RecipientReadB2BApi(newApiClient(restTemplate, webBasePath, gherkinSrlBearerToken));
        this.legalFactsApi = new LegalFactsApi(newLegalFactApiClient(restTemplate, b2bBasePath, gherkinSrlBearerToken));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String bearerToken) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.setBearerToken(bearerToken);
        return newApiClient;
    }

    private static it.pagopa.pn.client.b2b.generated.openapi.clients.deliverypushb2b.ApiClient newLegalFactApiClient(RestTemplate restTemplate, String basePath, String bearerToken) {
        it.pagopa.pn.client.b2b.generated.openapi.clients.deliverypushb2b.ApiClient newApiClient = new it.pagopa.pn.client.b2b.generated.openapi.clients.deliverypushb2b.ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.setBearerToken(bearerToken);
        return newApiClient;
    }

    @Override
    public BundleFullReceivedNotification getFullReceivedNotification(String iun, String mandateId) throws RestClientException {
        return deepCopy(recipientReadB2BApi.getReceivedNotificationV28(iun, mandateId), BundleFullReceivedNotification.class);
    }

    @Override
    public BffFullNotificationV1 getBffFullNotification(String iun, String mandateId) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public NotificationAttachmentDownloadMetadataResponse getReceivedNotificationAttachment(String iun, String attachmentName, UUID mandateId, Integer attachmentIdx) throws RestClientException {
        return recipientReadB2BApi.getReceivedNotificationAttachment(iun, attachmentName, mandateId, null);
    }

    @Override
    public BffDocumentDownloadMetadataResponse getReceivedNotificationAttachment(String iun, String attachmentName, UUID mandateId) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public NotificationAttachmentDownloadMetadataResponse getReceivedNotificationDocument(String iun, Integer docIdx, UUID mandateId) throws RestClientException {
        return recipientReadB2BApi.getReceivedNotificationDocument(iun, docIdx, mandateId);
    }

    @Override
    public LegalNotificationSearchResponse searchReceivedDelegatedNotification(OffsetDateTime startDate, OffsetDateTime endDate, String recipientId, String group, String senderId, NotificationStatusV26 status, String iunMatch, Integer size, String nextPagesKey) throws RestClientException {
        return recipientReadB2BApi.searchReceivedDelegatedNotification(
                startDate.toString(), endDate.toString(), senderId, recipientId, group, iunMatch, convertStatus(status), size, nextPagesKey);
    }

    @Override
    public LegalNotificationSearchResponse searchReceivedNotification(OffsetDateTime startDate, OffsetDateTime endDate, String mandateId, String senderId, NotificationStatusV26 status, String subjectRegExp, String iunMatch, Integer size, String nextPagesKey) throws RestClientException {
        it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.FullNotificationSearchResponse response = recipientReadB2BApi.searchReceivedNotification(startDate.toString(), endDate.toString(), mandateId,
                senderId, iunMatch, size, nextPagesKey, null);//TODO_MATTEO
        return deepCopy(response, LegalNotificationSearchResponse.class);
    }

    @Override
    public LegalFactDownloadMetadataResponse getLegalFact(String iun, LegalFactCategory legalFactType, String legalFactId) throws RestClientException {
        return legalFactsApi.deliveryPushIunDownloadLegalFactsLegalFactIdGet(iun, legalFactId);
    }

    @Override
    public BffDocumentDownloadMetadataResponse getDocumentsWeb(String iun, String documentId, UUID mandateId) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<BffLegalFactId> getLegalFactsV20(String iun, UUID mandateId) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public BffDocumentDownloadMetadataResponse downloadLegalFactById(String iun, String legalFactId, UUID mandateId) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        switch (bearerToken) {
            case USER_1 -> {
                this.recipientReadB2BApi.setApiClient(newApiClient(restTemplate, webBasePath, marioCucumberBearerToken));
                this.bearerTokenSetted = BearerTokenType.USER_1;
            }
            case USER_2 -> {
                this.recipientReadB2BApi.setApiClient(newApiClient(restTemplate, webBasePath, marioGherkinBearerToken));
                this.bearerTokenSetted = BearerTokenType.USER_2;
            }
            case USER_3 -> {
                this.recipientReadB2BApi.setApiClient(newApiClient(restTemplate, webBasePath, leonardoBearerToken));
                this.bearerTokenSetted = BearerTokenType.USER_3;
            }
            case PG_1 -> {
                this.recipientReadB2BApi.setApiClient(newApiClient(restTemplate, b2bBasePath, gherkinSrlBearerToken));
                this.bearerTokenSetted = BearerTokenType.PG_1;
            }
            case PG_2 -> {
                this.recipientReadB2BApi.setApiClient(newApiClient(restTemplate, b2bBasePath, cucumberSpaBearerToken));
                this.legalFactsApi.setApiClient(newLegalFactApiClient(restTemplate, b2bBasePath, cucumberSpaBearerToken));
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

    private it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.NotificationStatusV26 convertStatus(NotificationStatusV26 status) {
        return Optional.ofNullable(status)
                .map(NotificationStatusV26::getValue)
                .map(it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.NotificationStatusV26::fromValue)
                .orElse(null);
    }

    private <T> T deepCopy(Object obj, Class<T> toClass) {
        ObjectMapper objMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();
        try {
            String json = objMapper.writeValueAsString(obj);
            return objMapper.readValue(json, toClass);
        } catch (JsonProcessingException exc) {
            throw new PnB2bException(exc.getMessage());
        }
    }
}