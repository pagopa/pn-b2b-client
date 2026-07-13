package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.FullNotificationSearchResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.LegalNotificationSearchResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.deliverypushb2b.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.recipient.NotificationReceivedApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffDocumentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffDocumentType;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffFullNotificationV1;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffLegalFactId;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffNotificationsResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.NotificationStatusV26;
import it.pagopa.pn.client.b2b.pa.domain.Destinatario;
import it.pagopa.pn.client.b2b.pa.domain.NotificationSearchParam;
import it.pagopa.pn.client.b2b.pa.service.IPnWebRecipientClient;
import it.pagopa.pn.client.b2b.pa.wrapper.BundleFullReceivedNotification;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.v25.api.DocumentsWebApi;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.v25.api.LegalFactsApi;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.v25.model.LegalFactCategory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static it.pagopa.pn.client.b2b.pa.utils.JsonDeepCopyMapper.deepCopy;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnWebRecipientExternalClientImpl implements IPnWebRecipientClient {
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private final RestTemplate restTemplate;
    private final NotificationReceivedApi notificationReceivedApi;
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
        this.notificationReceivedApi = new NotificationReceivedApi(newApiClient(restTemplate, basePath, marioGherkinBearerToken, userAgent));
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
                this.notificationReceivedApi.setApiClient(newApiClient(restTemplate, basePath, marioCucumberBearerToken, userAgent));
                this.legalFactsApi.setApiClient(newApiClientV25(restTemplate, basePath, marioCucumberBearerToken));
                this.documentsWebApi.setApiClient(newApiClientV25(restTemplate, basePath, marioCucumberBearerToken));
                this.bearerTokenSetted = BearerTokenType.USER_1;
                beenSet = true;
            }
            case USER_2 -> {
                this.notificationReceivedApi.setApiClient(newApiClient(restTemplate, basePath, marioGherkinBearerToken, userAgent));
                this.legalFactsApi.setApiClient(newApiClientV25(restTemplate, basePath, marioGherkinBearerToken));
                this.documentsWebApi.setApiClient(newApiClientV25(restTemplate, basePath, marioGherkinBearerToken));
                this.bearerTokenSetted = BearerTokenType.USER_2;
                beenSet = true;
            }
            case USER_3 -> {
                this.notificationReceivedApi.setApiClient(newApiClient(restTemplate, basePath, leonardoBearerToken, userAgent));
                this.legalFactsApi.setApiClient(newApiClientV25(restTemplate, basePath, leonardoBearerToken));
                this.documentsWebApi.setApiClient(newApiClientV25(restTemplate, basePath, leonardoBearerToken));
                this.bearerTokenSetted = BearerTokenType.USER_3;
                beenSet = true;
            }
            case USER_5 -> {
                this.notificationReceivedApi.setApiClient(newApiClient(restTemplate, basePath, dinoBearerToken, userAgent));
                this.legalFactsApi.setApiClient(newApiClientV25(restTemplate, basePath, dinoBearerToken));
                this.documentsWebApi.setApiClient(newApiClientV25(restTemplate, basePath, dinoBearerToken));
                this.bearerTokenSetted = BearerTokenType.USER_5;
                beenSet = true;
            }
            case PG_1 -> {
                this.notificationReceivedApi.setApiClient(newApiClient(restTemplate, basePath, gherkinSrlBearerToken, userAgent));
                this.legalFactsApi.setApiClient(newApiClientV25(restTemplate, basePath, gherkinSrlBearerToken));
                this.documentsWebApi.setApiClient(newApiClientV25(restTemplate, basePath, gherkinSrlBearerToken));
                this.bearerTokenSetted = BearerTokenType.PG_1;
                beenSet = true;
            }
            case PG_2 -> {
                this.notificationReceivedApi.setApiClient(newApiClient(restTemplate, basePath, cucumberSpaBearerToken, userAgent));
                this.legalFactsApi.setApiClient(newApiClientV25(restTemplate, basePath, cucumberSpaBearerToken));
                this.documentsWebApi.setApiClient(newApiClientV25(restTemplate, basePath, cucumberSpaBearerToken));
                this.bearerTokenSetted = BearerTokenType.PG_2;
                beenSet = true;
            }
            case USER_SCADUTO -> {
                this.notificationReceivedApi.setApiClient(newApiClient(restTemplate, basePath, userBearerTokenScaduto, userAgent));
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

    @Override
    public BundleFullReceivedNotification getFullReceivedNotification(String iun, String mandateId) throws RestClientException {
        return deepCopy(getBffFullNotification(iun, mandateId), BundleFullReceivedNotification.class);
    }

    @Override
    public BffFullNotificationV1 getBffFullNotification(String iun, String mandateId) throws RestClientException {
        return notificationReceivedApi.getReceivedNotificationV1(iun, mandateId);
    }

    @Override
    public NotificationAttachmentDownloadMetadataResponse getReceivedNotificationAttachment(String iun, String attachmentName, UUID mandateId, Integer attachmentIdx) throws RestClientException {
        BffDocumentDownloadMetadataResponse bffDocumentDownloadMetadataResponse = notificationReceivedApi.getReceivedNotificationDocumentV1(iun, BffDocumentType.ATTACHMENT, mandateId, 0, attachmentName);
        return deepCopy(bffDocumentDownloadMetadataResponse, NotificationAttachmentDownloadMetadataResponse.class);
    }

    @Override
    public BffDocumentDownloadMetadataResponse getReceivedNotificationAttachment(String iun, String attachmentName, UUID mandateId) throws RestClientException {
        return notificationReceivedApi.getReceivedNotificationDocumentV1(iun, BffDocumentType.ATTACHMENT, mandateId, null, attachmentName);
    }

    @Override
    public NotificationAttachmentDownloadMetadataResponse getReceivedNotificationDocument(String iun, Integer docIdx, UUID mandateId) throws RestClientException {
        BffDocumentDownloadMetadataResponse bffDocumentDownloadMetadataResponse = notificationReceivedApi.getReceivedNotificationDocumentV1(iun, BffDocumentType.ATTACHMENT, mandateId, docIdx, null);
        return deepCopy(bffDocumentDownloadMetadataResponse, NotificationAttachmentDownloadMetadataResponse.class);
    }

    @Override
    public FullNotificationSearchResponse searchReceivedNotification(Destinatario destinatario, NotificationSearchParam searchParam) throws RestClientException {
        NotificationStatusV26 statusV26 = Optional.ofNullable(searchParam.status)
                .map(NotificationStatusV26::fromValue)
                .orElse(null);

        BffNotificationsResponse notificationsResponse = notificationReceivedApi.searchReceivedNotificationsV1(searchParam.startDate, searchParam.endDate, searchParam.mandateId, searchParam.senderId, statusV26, searchParam.subjectRegExp, searchParam.iunMatch, searchParam.size, searchParam.nextPagesKey);
        return deepCopy(notificationsResponse, FullNotificationSearchResponse.class);
    }

    @Override
    public LegalNotificationSearchResponse searchReceivedDelegatedNotification(Destinatario destinatario, NotificationSearchParam searchParam) throws RestClientException {
        NotificationStatusV26 statusV26 = Optional.ofNullable(searchParam.status)
                .map(NotificationStatusV26::fromValue)
                .orElse(null);

        BffNotificationsResponse notificationsResponse = notificationReceivedApi.searchReceivedDelegatedNotificationsV1(searchParam.startDate, searchParam.endDate, searchParam.senderId, searchParam.recipientId, searchParam.group, statusV26, searchParam.iunMatch, searchParam.size, searchParam.nextPagesKey);
        return deepCopy(notificationsResponse, LegalNotificationSearchResponse.class);
    }

    @Override
    public LegalFactDownloadMetadataResponse getLegalFact(String iun, LegalFactCategory legalFactType, String legalFactId) throws RestClientException {
        BffDocumentDownloadMetadataResponse bffDocumentDownloadMetadataResponse = notificationReceivedApi.getReceivedNotificationDocumentV1(iun, BffDocumentType.LEGAL_FACT, null, null, legalFactId);
        return deepCopy(bffDocumentDownloadMetadataResponse, LegalFactDownloadMetadataResponse.class);
    }

    @Override
    public List<BffLegalFactId> getLegalFactsV20(String iun, UUID mandateId) throws RestClientException {
        BffFullNotificationV1 bffFullNotificationV1 =
                notificationReceivedApi.getReceivedNotificationV1(iun, mandateId != null ? mandateId.toString() : null);
        return bffFullNotificationV1.getNotificationStatusHistory().stream()
                .flatMap(bffNotificationStatusHistory -> bffNotificationStatusHistory.getSteps().stream())
                .flatMap(bffNotificationDetailTimeline -> bffNotificationDetailTimeline.getLegalFactsIds().stream())
                .collect(Collectors.toList());
    }

    @Override
    public BffDocumentDownloadMetadataResponse downloadLegalFactById(String iun, String legalFactId, UUID mandateId) throws RestClientException {
        return notificationReceivedApi.getReceivedNotificationDocumentV1(iun, BffDocumentType.LEGAL_FACT, mandateId, null, legalFactId);
    }

    public BffDocumentDownloadMetadataResponse getDocumentsWeb(String iun, String documentId, UUID mandateId) throws RestClientException {
        return notificationReceivedApi.getReceivedNotificationDocumentV1(iun, BffDocumentType.AAR, mandateId, null, documentId);
    }

}