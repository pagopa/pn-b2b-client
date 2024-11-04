package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.DocumentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaldeliveryPushb2bpa.api.DocumentsWebApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaldeliveryPushb2bpa.api.LegalFactsApi;
import it.pagopa.pn.client.b2b.pa.service.IPnWebRecipientClient;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.api.RecipientReadApi;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.FullReceivedNotificationV24;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.NotificationSearchResponse;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.NotificationStatus;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model_v1.FullReceivedNotification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.UUID;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnWebRecipientExternalClientImpl implements IPnWebRecipientClient {
    private final RestTemplate restTemplate;
    private final RecipientReadApi recipientReadApi;
    private final it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.api_v1.RecipientReadApi recipientReadApiV1;
    private final it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.api_v2.RecipientReadApi recipientReadApiV2;
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
        this.recipientReadApi = new RecipientReadApi(newApiClientV25(restTemplate, basePath, marioGherkinBearerToken, userAgent));
        this.recipientReadApiV1 = new it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.api_v1.RecipientReadApi(newApiClient(restTemplate, basePath, marioGherkinBearerToken, userAgent));
        this.recipientReadApiV2 = new it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.api_v2.RecipientReadApi(newApiClient(restTemplate, basePath, marioGherkinBearerToken, userAgent));
        this.legalFactsApi = new LegalFactsApi(newApiClient(restTemplate, basePath, marioGherkinBearerToken, userAgent));
        this.documentsWebApi = new DocumentsWebApi(newApiClient(restTemplate, basePath, marioGherkinBearerToken, userAgent));
        this.bearerTokenSetted = BearerTokenType.USER_2;
    }

    private static it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaldeliveryPushb2bpa.ApiClient newApiClient(RestTemplate restTemplate, String basePath, String bearerToken, String userAgent) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaldeliveryPushb2bpa.ApiClient newApiClient = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaldeliveryPushb2bpa.ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("user-agent", userAgent);
        newApiClient.setBearerToken(bearerToken);
        return newApiClient;
    }

    private static it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.ApiClient newApiClientV25(RestTemplate restTemplate, String basePath, String bearerToken, String userAgent) {
        it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.ApiClient newApiClient = new it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("user-agent", userAgent);
        newApiClient.setBearerToken(bearerToken);
        return newApiClient;
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        boolean beenSet = false;
        switch (bearerToken) {
            case USER_1:
                this.recipientReadApi.setApiClient(newApiClientV25(restTemplate, basePath, marioCucumberBearerToken, userAgent));
                this.recipientReadApiV1.setApiClient(newApiClientV25(restTemplate, basePath, marioCucumberBearerToken, userAgent));
                this.recipientReadApiV2.setApiClient(newApiClientV25(restTemplate, basePath, marioCucumberBearerToken, userAgent));
                this.legalFactsApi.setApiClient(newApiClient(restTemplate, basePath, marioCucumberBearerToken, userAgent));
                this.documentsWebApi.setApiClient(newApiClient(restTemplate, basePath, marioCucumberBearerToken, userAgent));
                this.bearerTokenSetted = BearerTokenType.USER_1;
                beenSet = true;
                break;
            case USER_2:
                this.recipientReadApi.setApiClient(newApiClientV25(restTemplate, basePath, marioGherkinBearerToken, userAgent));
                this.recipientReadApiV1.setApiClient(newApiClientV25(restTemplate, basePath, marioGherkinBearerToken, userAgent));
                this.recipientReadApiV2.setApiClient(newApiClientV25(restTemplate, basePath, marioGherkinBearerToken, userAgent));
                this.legalFactsApi.setApiClient(newApiClient(restTemplate, basePath, marioGherkinBearerToken, userAgent));
                this.documentsWebApi.setApiClient(newApiClient(restTemplate, basePath, marioGherkinBearerToken, userAgent));
                this.bearerTokenSetted = BearerTokenType.USER_2;
                beenSet = true;
                break;
            case USER_3:
                this.recipientReadApi.setApiClient(newApiClientV25(restTemplate, basePath, leonardoBearerToken, userAgent));
                this.recipientReadApiV1.setApiClient(newApiClientV25(restTemplate, basePath, leonardoBearerToken, userAgent));
                this.recipientReadApiV2.setApiClient(newApiClientV25(restTemplate, basePath, leonardoBearerToken, userAgent));
                this.legalFactsApi.setApiClient(newApiClient(restTemplate, basePath, leonardoBearerToken, userAgent));
                this.documentsWebApi.setApiClient(newApiClient(restTemplate, basePath, leonardoBearerToken, userAgent));
                this.bearerTokenSetted = BearerTokenType.USER_3;
                beenSet = true;
                break;
            case USER_5:
                this.recipientReadApi.setApiClient(newApiClientV25(restTemplate, basePath, dinoBearerToken, userAgent));
                this.recipientReadApiV1.setApiClient(newApiClientV25(restTemplate, basePath, dinoBearerToken, userAgent));
                this.recipientReadApiV2.setApiClient(newApiClientV25(restTemplate, basePath, dinoBearerToken, userAgent));
                this.legalFactsApi.setApiClient(newApiClient(restTemplate, basePath, dinoBearerToken, userAgent));
                this.documentsWebApi.setApiClient(newApiClient(restTemplate, basePath, dinoBearerToken, userAgent));
                this.bearerTokenSetted = BearerTokenType.USER_5;
                beenSet = true;
                break;
            case PG_1:
                this.recipientReadApi.setApiClient(newApiClientV25(restTemplate, basePath, gherkinSrlBearerToken, userAgent));
                this.recipientReadApiV1.setApiClient(newApiClientV25(restTemplate, basePath, gherkinSrlBearerToken, userAgent));
                this.recipientReadApiV2.setApiClient(newApiClientV25(restTemplate, basePath, gherkinSrlBearerToken, userAgent));
                this.legalFactsApi.setApiClient(newApiClient(restTemplate, basePath, gherkinSrlBearerToken, userAgent));
                this.documentsWebApi.setApiClient(newApiClient(restTemplate, basePath, gherkinSrlBearerToken, userAgent));
                this.bearerTokenSetted = BearerTokenType.PG_1;
                beenSet = true;
                break;
            case PG_2:
                this.recipientReadApi.setApiClient(newApiClientV25(restTemplate, basePath, cucumberSpaBearerToken, userAgent));
                this.recipientReadApiV1.setApiClient(newApiClientV25(restTemplate, basePath, cucumberSpaBearerToken, userAgent));
                this.recipientReadApiV2.setApiClient(newApiClientV25(restTemplate, basePath, cucumberSpaBearerToken, userAgent));
                this.legalFactsApi.setApiClient(newApiClient(restTemplate, basePath, cucumberSpaBearerToken, userAgent));
                this.documentsWebApi.setApiClient(newApiClient(restTemplate, basePath, cucumberSpaBearerToken, userAgent));
                this.bearerTokenSetted = BearerTokenType.PG_2;
                beenSet = true;
                break;
            case USER_SCADUTO:
                this.recipientReadApi.setApiClient(newApiClientV25(restTemplate, basePath, userBearerTokenScaduto, userAgent));
                this.recipientReadApiV1.setApiClient(newApiClientV25(restTemplate, basePath, userBearerTokenScaduto, userAgent));
                this.recipientReadApiV2.setApiClient(newApiClientV25(restTemplate, basePath, userBearerTokenScaduto, userAgent));
                this.legalFactsApi.setApiClient(newApiClient(restTemplate, basePath, userBearerTokenScaduto, userAgent));
                this.documentsWebApi.setApiClient(newApiClient(restTemplate, basePath, userBearerTokenScaduto, userAgent));
                this.bearerTokenSetted = BearerTokenType.USER_SCADUTO;
                beenSet = true;
                break;
        }
        return beenSet;
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return this.bearerTokenSetted;
    }

    public FullReceivedNotificationV24 getReceivedNotification(String iun, String mandateId) throws RestClientException {
        return recipientReadApi.getReceivedNotificationV23(iun, mandateId);
    }

    @Override
    public it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model_v1.NotificationAttachmentDownloadMetadataResponse getReceivedNotificationAttachment(String iun, String attachmentName, UUID mandateId) throws RestClientException {
        return recipientReadApiV1.getReceivedNotificationAttachment(iun, attachmentName, mandateId);
    }

    public FullReceivedNotification getReceivedNotificationV1(String iun, String mandateId) throws RestClientException {
        return recipientReadApiV1.getReceivedNotification(iun, mandateId);
    }

    public it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model_v2.FullReceivedNotification getReceivedNotificationV2(String iun, String mandateId) throws RestClientException {
        return recipientReadApiV2.getReceivedNotification(iun, mandateId);
    }

    public NotificationAttachmentDownloadMetadataResponse getReceivedNotificationAttachment(String iun, String attachmentName, UUID mandateId, Integer attachmentIdx) throws RestClientException {
        return recipientReadApi.getReceivedNotificationAttachment(iun, attachmentName, mandateId, attachmentIdx);
    }

    public NotificationAttachmentDownloadMetadataResponse getReceivedNotificationDocument(String iun, Integer docIdx, UUID mandateId) throws RestClientException {
        return recipientReadApi.getReceivedNotificationDocument(iun, docIdx, mandateId);
    }

    public NotificationSearchResponse searchReceivedNotification(OffsetDateTime startDate, OffsetDateTime endDate, String mandateId, String senderId, NotificationStatus status, String subjectRegExp, String iunMatch, Integer size, String nextPagesKey) throws RestClientException {
        return recipientReadApi.searchReceivedNotification(startDate, endDate, mandateId, senderId, status, subjectRegExp, iunMatch, size, nextPagesKey);
    }

    public NotificationSearchResponse searchReceivedDelegatedNotification(OffsetDateTime startDate, OffsetDateTime endDate, String senderId, String recipientId, String group, NotificationStatus status, String iunMatch, Integer size, String nextPagesKey) throws RestClientException {
        return recipientReadApi.searchReceivedDelegatedNotification(startDate, endDate, senderId, recipientId, group, iunMatch, status, size, nextPagesKey);
    }

    //OLD VERSION PRE v25
//    @Override
//    public LegalFactDownloadMetadataResponse getLegalFact(String iun, LegalFactCategory legalFactType, String legalFactId) throws RestClientException {
//        return this.legalFactsApi.getLegalFact(iun,legalFactType,legalFactId);
//    }

    @Override
    public LegalFactDownloadMetadataResponse getLegalFact(String iun, String legalFactType, String legalFactId) throws RestClientException {
        //TODO MATTEO
        return this.legalFactsApi.getLegalFact(iun, legalFactType, legalFactId);
    }

    //OLD VERSION PRE v25
//    public DocumentDownloadMetadataResponse getDocumentsWeb(String iun, DocumentCategory documentType, String documentId, String mandateId) throws RestClientException {
//        return this.documentsWebApi.getDocumentsWeb(iun, documentType, documentId, mandateId);
//    }

    @Override
    public DocumentDownloadMetadataResponse getDocumentsWeb(String iun, String documentType, String documentId, String mandateId) throws RestClientException {
        //TODO MATTEO
        return this.documentsWebApi.getDocumentsWeb(iun, documentType, documentId, mandateId);
    }
}