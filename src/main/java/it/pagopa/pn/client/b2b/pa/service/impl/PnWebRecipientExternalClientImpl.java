package it.pagopa.pn.client.b2b.pa.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.FullReceivedNotificationV26;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.NotificationSearchResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.deliverypushb2b.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.recipient.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.recipient.v1.NotificationReceivedApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.v1.BffDocumentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.v1.BffDocumentType;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.v1.BffFullNotificationV1;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.v2.BffLegalFactId;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.v2.BffNotificationsResponse;
import it.pagopa.pn.client.b2b.pa.exception.PnB2bException;
import it.pagopa.pn.client.b2b.pa.service.IPnWebRecipientClient;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.NotificationStatusV26;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.v25.api.DocumentsWebApi;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.v25.api.LegalFactsApi;
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
import java.util.stream.Collectors;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnWebRecipientExternalClientImpl implements IPnWebRecipientClient {
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private final RestTemplate restTemplate;
    private final NotificationReceivedApi notificationReceivedApiV1;
    private final it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.recipient.v2.NotificationReceivedApi notificationReceivedApiV2;
    private final LegalFactsApi legalFactsApi;
    private final DocumentsWebApi documentsWebApi;
    private BearerTokenType bearerTokenSetted;
    private final String marioCucumberBearerToken;
    private final String marioGherkinBearerToken;
    private final String leonardoBearerToken;
    private final String dinoBearerToken;
    private final String userBearerTokenScaduto;
    private final String gherkinSrlBearerToken;
    private final String cucumberSpaBearerToken;
    private final String basePath;
    private final String userAgent;


    public PnWebRecipientExternalClientImpl(RestTemplate restTemplate,
                                            @Value("${pn.webapi.external.base-url}") String basePath,
                                            @Value("${pn.bearer-token.user1}") String marioCucumberBearerToken,
                                            @Value("${pn.bearer-token.user2}") String marioGherkinBearerToken,
                                            @Value("${pn.bearer-token.user3}") String leonardoBearerToken,
                                            @Value("${pn.bearer-token.user5}") String dinoBearerToken,
                                            @Value("${pn.bearer-token.scaduto}") String userBearerTokenScaduto,
                                            @Value("${pn.bearer-token.pg1}") String gherkinSrlBearerToken,
                                            @Value("${pn.bearer-token.pg2}") String cucumberSpaBearerToken,
                                            @Value("${pn.webapi.external.user-agent}") String userAgent) {
        this.restTemplate = restTemplate;
        this.marioCucumberBearerToken = marioCucumberBearerToken;
        this.marioGherkinBearerToken = marioGherkinBearerToken;
        this.leonardoBearerToken = leonardoBearerToken;
        this.dinoBearerToken = dinoBearerToken;
        this.userBearerTokenScaduto = userBearerTokenScaduto;
        this.gherkinSrlBearerToken = gherkinSrlBearerToken;
        this.cucumberSpaBearerToken = cucumberSpaBearerToken;
        this.basePath = basePath;
        this.userAgent = userAgent;
        this.notificationReceivedApiV1 = new NotificationReceivedApi(newApiClient(restTemplate, basePath, marioGherkinBearerToken, userAgent));
        this.notificationReceivedApiV2 = new it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.recipient.v2.NotificationReceivedApi(newApiClient(restTemplate, basePath, marioGherkinBearerToken, userAgent));
        this.legalFactsApi = new LegalFactsApi(newApiClientV25(restTemplate, basePath, marioGherkinBearerToken));
        this.documentsWebApi = new DocumentsWebApi(newApiClientV25(restTemplate, basePath, marioGherkinBearerToken));
        this.bearerTokenSetted = BearerTokenType.USER_2;
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String bearerToken, String userAgent) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("user-agent", userAgent);
        newApiClient.addDefaultHeader("Authorization", "Bearer " + bearerToken);
        return newApiClient;
    }

    private static it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.recipient.ApiClient newNotificationReceivedApiClient(RestTemplate restTemplate, String basePath, String bearerToken, String userAgent) {
        it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.recipient.ApiClient newApiClient = new it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.recipient.ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("user-agent", userAgent);
        newApiClient.addDefaultHeader("Authorization", "Bearer " + bearerToken);
        return newApiClient;
    }

    private static it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.v25.ApiClient newApiClientV25(RestTemplate restTemplate, String basePath, String bearerToken) {
        it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.v25.ApiClient newApiClient = new it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.v25.ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader(AUTHORIZATION, BEARER + bearerToken);
        return newApiClient;
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        boolean beenSet = false;
        switch (bearerToken) {
            case USER_1 -> {
                this.notificationReceivedApiV1.setApiClient(newNotificationReceivedApiClient(restTemplate, basePath, marioCucumberBearerToken, userAgent));
                this.notificationReceivedApiV2.setApiClient(newNotificationReceivedApiClient(restTemplate, basePath, marioCucumberBearerToken, userAgent));
                this.legalFactsApi.setApiClient(newApiClientV25(restTemplate, basePath, marioCucumberBearerToken));
                this.documentsWebApi.setApiClient(newApiClientV25(restTemplate, basePath, marioCucumberBearerToken));
                this.bearerTokenSetted = BearerTokenType.USER_1;
                beenSet = true;
            }
            case USER_2 -> {
                this.notificationReceivedApiV1.setApiClient(newNotificationReceivedApiClient(restTemplate, basePath, marioGherkinBearerToken, userAgent));
                this.notificationReceivedApiV2.setApiClient(newNotificationReceivedApiClient(restTemplate, basePath, marioGherkinBearerToken, userAgent));
                this.legalFactsApi.setApiClient(newApiClientV25(restTemplate, basePath, marioGherkinBearerToken));
                this.documentsWebApi.setApiClient(newApiClientV25(restTemplate, basePath, marioGherkinBearerToken));
                this.bearerTokenSetted = BearerTokenType.USER_2;
                beenSet = true;
            }
            case USER_3 -> {
                this.notificationReceivedApiV1.setApiClient(newNotificationReceivedApiClient(restTemplate, basePath, leonardoBearerToken, userAgent));
                this.notificationReceivedApiV2.setApiClient(newNotificationReceivedApiClient(restTemplate, basePath, leonardoBearerToken, userAgent));
                this.legalFactsApi.setApiClient(newApiClientV25(restTemplate, basePath, leonardoBearerToken));
                this.documentsWebApi.setApiClient(newApiClientV25(restTemplate, basePath, leonardoBearerToken));
                this.bearerTokenSetted = BearerTokenType.USER_3;
                beenSet = true;
            }
            case USER_5 -> {
                this.notificationReceivedApiV1.setApiClient(newNotificationReceivedApiClient(restTemplate, basePath, dinoBearerToken, userAgent));
                this.notificationReceivedApiV2.setApiClient(newNotificationReceivedApiClient(restTemplate, basePath, dinoBearerToken, userAgent));
                this.legalFactsApi.setApiClient(newApiClientV25(restTemplate, basePath, dinoBearerToken));
                this.documentsWebApi.setApiClient(newApiClientV25(restTemplate, basePath, dinoBearerToken));
                this.bearerTokenSetted = BearerTokenType.USER_5;
                beenSet = true;
            }
            case PG_1 -> {
                this.notificationReceivedApiV1.setApiClient(newNotificationReceivedApiClient(restTemplate, basePath, gherkinSrlBearerToken, userAgent));
                this.notificationReceivedApiV2.setApiClient(newNotificationReceivedApiClient(restTemplate, basePath, gherkinSrlBearerToken, userAgent));
                this.legalFactsApi.setApiClient(newApiClientV25(restTemplate, basePath, gherkinSrlBearerToken));
                this.documentsWebApi.setApiClient(newApiClientV25(restTemplate, basePath, gherkinSrlBearerToken));
                this.bearerTokenSetted = BearerTokenType.PG_1;
                beenSet = true;
            }
            case PG_2 -> {
                this.notificationReceivedApiV1.setApiClient(newNotificationReceivedApiClient(restTemplate, basePath, cucumberSpaBearerToken, userAgent));
                this.notificationReceivedApiV2.setApiClient(newNotificationReceivedApiClient(restTemplate, basePath, cucumberSpaBearerToken, userAgent));
                this.legalFactsApi.setApiClient(newApiClientV25(restTemplate, basePath, cucumberSpaBearerToken));
                this.documentsWebApi.setApiClient(newApiClientV25(restTemplate, basePath, cucumberSpaBearerToken));
                this.bearerTokenSetted = BearerTokenType.PG_2;
                beenSet = true;
            }
            case USER_SCADUTO -> {
                this.notificationReceivedApiV1.setApiClient(newNotificationReceivedApiClient(restTemplate, basePath, userBearerTokenScaduto, userAgent));
                this.notificationReceivedApiV2.setApiClient(newNotificationReceivedApiClient(restTemplate, basePath, userBearerTokenScaduto, userAgent));
                this.legalFactsApi.setApiClient(newApiClientV25(restTemplate, basePath, userBearerTokenScaduto));
                this.documentsWebApi.setApiClient(newApiClientV25(restTemplate, basePath, userBearerTokenScaduto));
                this.bearerTokenSetted = BearerTokenType.USER_SCADUTO;
                beenSet = true;
            }
        }
        return beenSet;
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return this.bearerTokenSetted;
    }

    public FullReceivedNotificationV26 getReceivedNotification(String iun, String mandateId) throws RestClientException {
        it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.v2.BffFullNotificationV1 notification = getReceivedNotificationV2(iun, mandateId);
        return deepCopy(notification, FullReceivedNotificationV26.class);
    }

    @Override
    public BffDocumentDownloadMetadataResponse getReceivedNotificationAttachment(String iun, String attachmentName, UUID mandateId) throws RestClientException {
        return notificationReceivedApiV1.getReceivedNotificationDocumentV1(iun, BffDocumentType.ATTACHMENT, mandateId, null, attachmentName, null);
    }

    public BffFullNotificationV1 getReceivedNotificationV1(String iun, String mandateId) throws RestClientException {
        return notificationReceivedApiV1.getReceivedNotificationV1(iun, mandateId);
    }

    public it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.v2.BffFullNotificationV1 getReceivedNotificationV2(String iun, String mandateId) throws RestClientException {
        return notificationReceivedApiV2.getReceivedNotificationV1(iun, mandateId);
    }

    public NotificationAttachmentDownloadMetadataResponse getReceivedNotificationAttachment(String iun, String attachmentName, UUID mandateId, Integer attachmentIdx) throws RestClientException {
        it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.v2.BffDocumentDownloadMetadataResponse bffDocumentDownloadMetadataResponse = notificationReceivedApiV2.getReceivedNotificationDocumentV1(iun, it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.v2.BffDocumentType.ATTACHMENT, mandateId, 0, attachmentName);
        return deepCopy(bffDocumentDownloadMetadataResponse, NotificationAttachmentDownloadMetadataResponse.class);
    }

    public NotificationAttachmentDownloadMetadataResponse getReceivedNotificationDocument(String iun, Integer docIdx, UUID mandateId) throws RestClientException {
        it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.v2.BffDocumentDownloadMetadataResponse bffDocumentDownloadMetadataResponse = notificationReceivedApiV2.getReceivedNotificationDocumentV1(iun, it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.v2.BffDocumentType.ATTACHMENT, mandateId, docIdx, null);
        return deepCopy(bffDocumentDownloadMetadataResponse, NotificationAttachmentDownloadMetadataResponse.class);
    }

    public NotificationSearchResponse searchReceivedNotification(OffsetDateTime startDate, OffsetDateTime endDate, String mandateId, String senderId, NotificationStatusV26 status, String subjectRegExp, String iunMatch, Integer size, String nextPagesKey) throws RestClientException {
        it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.v2.NotificationStatusV26 statusV26 = Optional.ofNullable(status)
                .map(NotificationStatusV26::getValue)
                .map(it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.v2.NotificationStatusV26::fromValue)
                .orElse(null);

        BffNotificationsResponse notificationsResponse = notificationReceivedApiV2.searchReceivedNotificationsV1(startDate, endDate, mandateId, senderId, statusV26, subjectRegExp, iunMatch, size, nextPagesKey);
        return deepCopy(notificationsResponse, NotificationSearchResponse.class);
    }

    public NotificationSearchResponse searchReceivedDelegatedNotification(OffsetDateTime startDate, OffsetDateTime endDate, String senderId, String recipientId, String group, NotificationStatusV26 status, String iunMatch, Integer size, String nextPagesKey) throws RestClientException {
        it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.v2.NotificationStatusV26 statusV26 = Optional.ofNullable(status)
                .map(NotificationStatusV26::getValue)
                .map(it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.v2.NotificationStatusV26::fromValue)
                .orElse(null);

        BffNotificationsResponse notificationsResponse = notificationReceivedApiV2.searchReceivedDelegatedNotificationsV1(startDate, endDate, senderId, recipientId, group, statusV26, iunMatch, size, nextPagesKey);
        return deepCopy(notificationsResponse, NotificationSearchResponse.class);
    }

    @Override
    public LegalFactDownloadMetadataResponse getLegalFact(String iun, LegalFactCategory legalFactType, String legalFactId) throws RestClientException {
        it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.v2.BffDocumentDownloadMetadataResponse bffDocumentDownloadMetadataResponse = notificationReceivedApiV2.getReceivedNotificationDocumentV1(iun, it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.v2.BffDocumentType.LEGAL_FACT, null, null, legalFactId);
        return deepCopy(bffDocumentDownloadMetadataResponse, LegalFactDownloadMetadataResponse.class);
    }

    @Override
    public List<BffLegalFactId> getLegalFactsV20(String iun, UUID mandateId) throws RestClientException {
        it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.v2.BffFullNotificationV1 bffFullNotificationV1 =
                notificationReceivedApiV2.getReceivedNotificationV1(iun, mandateId != null ? mandateId.toString() : null);
        return bffFullNotificationV1.getNotificationStatusHistory().stream()
                .flatMap(bffNotificationStatusHistory -> bffNotificationStatusHistory.getSteps().stream())
                .flatMap(bffNotificationDetailTimeline -> bffNotificationDetailTimeline.getLegalFactsIds().stream())
                .collect(Collectors.toList());
    }

    @Override
    public it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.v2.BffDocumentDownloadMetadataResponse downloadLegalFactById(String iun, String legalFactId, UUID mandateId) throws RestClientException {
        return notificationReceivedApiV2.getReceivedNotificationDocumentV1(iun, it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.v2.BffDocumentType.LEGAL_FACT, mandateId, null, legalFactId);
    }

    public it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.v2.BffDocumentDownloadMetadataResponse getDocumentsWeb(String iun, String documentId, UUID mandateId) throws RestClientException {
        return notificationReceivedApiV2.getReceivedNotificationDocumentV1(iun, it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.v2.BffDocumentType.AAR, mandateId, null, documentId);
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