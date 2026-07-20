package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.api.InformalNotificationTerminationApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.api.MessagesApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.api.NewInformalNotificationApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.api.SenderReadInformalNotificationB2BApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.*;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internawebrecipientinformal.api.RecipientReadInformalNotificationApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internawebrecipientinformal.model.FullReceivedInformalNotificationV1;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDelivery.api.InternalOnlyApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDelivery.model.InformalSentNotificationV1;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class PnPaB2bInternalInformalClientImpl {
    private final String paId;
    private final String operatorId;
    private final MessagesApi messagesApi;
    private final SenderReadInformalNotificationB2BApi senderReadInformalNotificationB2BApi;
    private final NewInformalNotificationApi newInformalNotificationApi;
    private final InformalNotificationTerminationApi informalNotificationTerminationApi;
    private final RecipientReadInformalNotificationApi recipientReadInformalNotificationApi;
    private final InternalOnlyApi internalOnlyApi;
    private final List<String> groups;
    private String deliveryBasePath;

    public PnPaB2bInternalInformalClientImpl(
            RestTemplate restTemplate,
            @Value("${pn.delivery.base-url}") String deliveryBasePathOrigin,
            @Value("${pn.internal.pa-id}") String paId) {

        this.deliveryBasePath = deliveryBasePathOrigin+"/informal/";
        this.paId = paId;
        this.operatorId = "TestMv";
        this.groups = Collections.emptyList();
        this.messagesApi = new MessagesApi(newInformalApiClient(restTemplate, deliveryBasePathOrigin));
        this.senderReadInformalNotificationB2BApi = new SenderReadInformalNotificationB2BApi(newInformalApiClient(restTemplate, deliveryBasePathOrigin));
        this.newInformalNotificationApi = new NewInformalNotificationApi(newInformalApiClient(restTemplate, deliveryBasePathOrigin));
        this.informalNotificationTerminationApi = new InformalNotificationTerminationApi();
        this.recipientReadInformalNotificationApi = new RecipientReadInformalNotificationApi(newRecipientInformalApiClient(restTemplate, deliveryBasePath));
        this.internalOnlyApi = new InternalOnlyApi(newPrivateDeliveryApiClient(restTemplate, deliveryBasePathOrigin));
    }

    private static it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.ApiClient newInformalApiClient(RestTemplate restTemplate, String basePath) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.ApiClient newApiClient = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
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


    public MessageResponse createMessage(String cxId, NewMessageRequest request) {
        return messagesApi.newMessage(operatorId, CxTypeAuthFleet.PA, cxId, request, groups);
    }

    public MessageResponse getMessage(UUID messageId, String cxId) {
        return messagesApi.messageById(messageId, operatorId, CxTypeAuthFleet.PA, cxId, groups);
    }

    public NewInformalNotificationResponse sendNewInformalNotificationV1(String cxId, InformalNotificationRequestV1 informalNotificationRequestV1) throws RestClientException {
        return newInformalNotificationApi.sendNewInformalNotificationV1(operatorId, CxTypeAuthFleet.PA, cxId, "B2B", informalNotificationRequestV1, groups, null, null);
    }

    public NewInformalNotificationRequestStatusResponseV1 getNotificationStatusByRequestId(String cxId, String notificationRequestId) {
        return senderReadInformalNotificationB2BApi.getInformalNotificationRequestStatusV1(operatorId, CxTypeAuthFleet.PA, cxId, groups, notificationRequestId, null, null);
    }

    public NotificationAttachmentDownloadMetadataResponse getSentInformalNotificationDocument(String cxId, String iun, int docIdx) {
        return senderReadInformalNotificationB2BApi.getSentInformalNotificationDocument(operatorId, CxTypeAuthFleet.PA, cxId, iun, docIdx, groups);
    }

    public NotificationAttachmentDownloadMetadataResponse getSentInformalNotificationAttachment(String iun, String cxId, int recipientIdx, int attachmentIdx) {
        return senderReadInformalNotificationB2BApi.getSentInformalNotificationAttachment(operatorId, CxTypeAuthFleet.PA, cxId, iun, recipientIdx, "PAGOPA", groups, attachmentIdx);
    }

    public TerminationRequestStatus terminateInformalWorkflow(String cxId, String iun) {
        return informalNotificationTerminationApi.terminateInformalWorkflow(operatorId, CxTypeAuthFleet.PA, cxId, iun, groups);
    }

    public InformalSentNotificationV1 getSentInformalNotification(String iun) {
        return internalOnlyApi.getSentInformalNotificationPrivateV1(iun);
    }

    public List<InformalPreLoadResponse> informalPresignedUploadRequest(String cxId, List<InformalPreLoadRequest> requests) {
        return newInformalNotificationApi.informalPresignedUploadRequest(operatorId, CxTypeAuthFleet.PA, cxId, requests);
    }

    public FullSentInformalNotificationV1 getSentInformalNotificationSender(String cxId, String iun, Boolean retrieveMessage) {
        return senderReadInformalNotificationB2BApi.getSentInformalNotificationV1(operatorId,CxTypeAuthFleet.PA, cxId,iun, groups, retrieveMessage);
    }

    public FullReceivedInformalNotificationV1 getReceivedInformalNotification(String cxId, String iun, it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internawebrecipientinformal.model.CxTypeAuthFleet recipientType) {
        return recipientReadInformalNotificationApi.getReceivedInformalNotificationV1(operatorId, recipientType, cxId, "WEB", iun, null, null
        );
    }

    }
