package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.api.InformalNotificationTerminationApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.api.MessagesApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.api.NewInformalNotificationApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.api.SenderReadInformalNotificationB2BApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.model.*;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformalmonitorcampaign.api.CampaignStatisticsApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformalmonitorcampaign.model.CampaignStatisticsResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internawebrecipientinformal.api.RecipientReadInformalNotificationApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internawebrecipientinformal.model.FullReceivedInformalNotificationV1;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDelivery.api.InternalOnlyApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDelivery.model.InformalSentNotificationV1;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDelivery.model.LegalNotificationSearchResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDelivery.model.NotificationStatusV26;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class PnPaB2bInternalInformalClientImpl implements SettableApiKey {
    private final String operatorId;
    private final MessagesApi messagesApi;
    private final SenderReadInformalNotificationB2BApi senderReadInformalNotificationB2BApi;
    private final NewInformalNotificationApi newInformalNotificationApi;
    private final InformalNotificationTerminationApi informalNotificationTerminationApi;
    private final RecipientReadInformalNotificationApi recipientReadInformalNotificationApi;
    private final InternalOnlyApi internalOnlyApi;
    private ApiKeyType apiKeySetted = null;
    private final CampaignStatisticsApi campaignStatisticsApi;
    private final List<String> groups;
    private final RestTemplate restTemplate;

    private final String apiKeyMvp1;
    private final String apiKeyMvp2;
    private final String apiKeyGa;
    private final String apiKeySon;
    private final String apiKeyRoot;
    private final String baseExternalPath;

    public PnPaB2bInternalInformalClientImpl(RestTemplate restTemplate,
                                             @Value("${pn.external.informal-base-url.pagopa}") String baseExternalPath,
                                             @Value("${pn.delivery.base-url}") String deliveryBasePathOrigin,
                                             @Value("${pn.external.api-key}") String apiKeyMvp1,
                                             @Value("${pn.external.api-key-2}") String apiKeyMvp2,
                                             @Value("${pn.external.api-key-GA}") String apiKeyGa,
                                             @Value("${pn.external.api-key-SON}") String apiKeySon,
                                             @Value("${pn.external.api-key-ROOT}") String apiKeyRoot) {

        this.operatorId = "TestMv";
        this.groups = Collections.emptyList();
        this.messagesApi = new MessagesApi(newInformalApiClient(restTemplate, baseExternalPath, null));
        this.senderReadInformalNotificationB2BApi = new SenderReadInformalNotificationB2BApi(newInformalApiClient(restTemplate, baseExternalPath, null));
        this.newInformalNotificationApi = new NewInformalNotificationApi(newInformalApiClient(restTemplate, baseExternalPath, null));
        this.informalNotificationTerminationApi = new InformalNotificationTerminationApi();
        this.recipientReadInformalNotificationApi = new RecipientReadInformalNotificationApi(newRecipientInformalApiClient(restTemplate, deliveryBasePathOrigin));
        this.internalOnlyApi = new InternalOnlyApi(newPrivateDeliveryApiClient(restTemplate, deliveryBasePathOrigin));
        this.campaignStatisticsApi = new CampaignStatisticsApi(newcampaignStatisticsApiClient(restTemplate, deliveryBasePathOrigin));

        this.apiKeyMvp1 = apiKeyMvp1;
        this.apiKeyMvp2 = apiKeyMvp2;
        this.apiKeyGa = apiKeyGa;
        this.apiKeySon = apiKeySon;
        this.apiKeyRoot = apiKeyRoot;
        this.restTemplate = restTemplate;
        //setApiKey(apiKeyGa);
        this.apiKeySetted = null;
        this.baseExternalPath = baseExternalPath;
    }

    private static it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.ApiClient newInformalApiClient(RestTemplate restTemplate, String baseExternalPath, String apiKey) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.ApiClient newApiClient = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.ApiClient(restTemplate);
        newApiClient.setApiKey(apiKey);
        newApiClient.setBasePath(baseExternalPath);
        return newApiClient;
    }

    private static it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDelivery.ApiClient newPrivateDeliveryApiClient(RestTemplate restTemplate, String basePath) {
        it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDelivery.ApiClient client = new it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDelivery.ApiClient(restTemplate);
        client.setBasePath(basePath);
        return client;
    }

    private static it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internawebrecipientinformal.ApiClient newRecipientInformalApiClient(RestTemplate restTemplate, String basePath) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internawebrecipientinformal.ApiClient client = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internawebrecipientinformal.ApiClient(restTemplate);
        client.setBasePath(basePath);
        return client;
    }

    private static it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformalmonitorcampaign.ApiClient newcampaignStatisticsApiClient(RestTemplate restTemplate, String basePath) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformalmonitorcampaign.ApiClient client = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformalmonitorcampaign.ApiClient(restTemplate);
        client.setBasePath(basePath);
        return client;
    }


    public MessageResponse createMessage(NewMessageRequest request) {
        return messagesApi.newMessage(request);
    }

    public MessageResponse getMessage(UUID messageId) {
        return messagesApi.messageById(messageId);
    }

    public NewNotificationResponse sendNewInformalNotificationV1(InformalNotificationRequestV1 informalNotificationRequestV1) throws RestClientException {
        return newInformalNotificationApi.sendNewInformalNotificationV1(informalNotificationRequestV1);
    }

    public NewInformalNotificationRequestStatusResponseV1 getNotificationStatusByRequestId(String notificationRequestId) {
        return senderReadInformalNotificationB2BApi.retrieveInformalNotificationRequestStatusV1(notificationRequestId, null, null);
    }

    public NotificationAttachmentDownloadMetadataResponse getSentInformalNotificationDocument(String iun, int docIdx) {
        return senderReadInformalNotificationB2BApi.retrieveSentInformalNotificationDocument(iun, docIdx);
    }

    public NotificationAttachmentDownloadMetadataResponse getSentInformalNotificationAttachment(String iun, int recipientIdx, int attachmentIdx) {
        return senderReadInformalNotificationB2BApi.retrieveSentInformalNotificationAttachment(iun, recipientIdx, "PAGOPA", attachmentIdx);
    }

    public TerminationRequestStatus terminateInformalWorkflow(String iun) {
        return informalNotificationTerminationApi.terminateInformalWorkflow(iun);
    }

    public InformalSentNotificationV1 getSentInformalNotification(String iun) {
        return internalOnlyApi.getSentInformalNotificationPrivateV1(iun, null);
    }

    public LegalNotificationSearchResponse searchNotificationsPrivate(OffsetDateTime startDate, OffsetDateTime endDate, String recipientId, Boolean recipientIdOpaque, String senderId, List<NotificationStatusV26> status, String mandateId, String cxType, Integer size, String nextPagesKey) {
        return internalOnlyApi.searchNotificationsPrivate(startDate, endDate, recipientId, recipientIdOpaque, senderId, status, mandateId, cxType, size, nextPagesKey);
    }

    public List<InformalPreLoadResponse> informalPresignedUploadRequest(String cxId, List<InformalPreLoadRequest> requests) {
        return newInformalNotificationApi.informalPresignedUploadRequest(requests);
    }

    public FullSentInformalNotificationV1 getSentInformalNotificationSender(String iun, Boolean retrieveMessage) {
        return senderReadInformalNotificationB2BApi.getSentInformalNotificationV1(iun, retrieveMessage);
    }

    public FullReceivedInformalNotificationV1 getReceivedInformalNotification(String cxId, String iun, it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internawebrecipientinformal.model.CxTypeAuthFleet recipientType) {
        return recipientReadInformalNotificationApi.getReceivedInformalNotificationV1(operatorId, recipientType, cxId, "WEB", iun, null, null, false);
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internawebrecipientinformal.model.NotificationAttachmentDownloadMetadataResponse getReceivedInformalNotificationDocument(String cxId, String iun, it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internawebrecipientinformal.model.CxTypeAuthFleet recipientType, Integer docIdx) {
        return recipientReadInformalNotificationApi.getReceivedInformalNotificationDocumentV1(operatorId, recipientType, cxId, "WEB", iun, docIdx, null, null);
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internawebrecipientinformal.model.NotificationAttachmentDownloadMetadataResponse getReceivedInformalNotificationAttachment(String cxId, String iun, it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internawebrecipientinformal.model.CxTypeAuthFleet recipientType, Integer attachmentIdx) {
        return recipientReadInformalNotificationApi.getReceivedInformalNotificationAttachmentV1(operatorId, recipientType, cxId, "WEB", iun, "PAGOPA", null, null, attachmentIdx);
    }

    @Override
    public boolean setApiKeys(ApiKeyType apiKey) {
        boolean beenSet = false;
        switch (apiKey) {
            case MVP_1 -> {
                if (this.apiKeySetted != ApiKeyType.MVP_1) {
                    setApiKey(apiKeyMvp1);
                    this.apiKeySetted = ApiKeyType.MVP_1;
                }
                beenSet = true;
            }
            case MVP_2 -> {
                if (this.apiKeySetted != ApiKeyType.MVP_2) {
                    setApiKey(apiKeyMvp2);
                    this.apiKeySetted = ApiKeyType.MVP_2;
                }
                beenSet = true;
            }
            case GA -> {
                if (this.apiKeySetted != ApiKeyType.GA) {
                    setApiKey(apiKeyGa);
                    this.apiKeySetted = ApiKeyType.GA;
                }
                beenSet = true;
            }
            case SON -> {
                if (this.apiKeySetted != ApiKeyType.SON) {
                    setApiKey(apiKeySon);
                    this.apiKeySetted = ApiKeyType.SON;
                }
                beenSet = true;
            }
            case ROOT -> {
                if (this.apiKeySetted != ApiKeyType.ROOT)
                {setApiKey(apiKeyRoot);
                    this.apiKeySetted = ApiKeyType.ROOT;}
                beenSet = true;
            }
        }
        return beenSet;
    }

    @Override
    public void setApiKey(String apiKey) {

        this.messagesApi.setApiClient(newInformalApiClient(restTemplate, baseExternalPath, apiKey));
        this.senderReadInformalNotificationB2BApi.setApiClient(newInformalApiClient(restTemplate, baseExternalPath, apiKey));
        this.newInformalNotificationApi.setApiClient(newInformalApiClient(restTemplate, baseExternalPath, apiKey));
    }
    public CampaignStatisticsResponse getCampaignStatistics(String cxId, String campaignId) {
        return campaignStatisticsApi.getCampaignStatistics(cxId, campaignId);
    }

    @Override
    public ApiKeyType getApiKeySetted() {
        return this.apiKeySetted;
    }
}
