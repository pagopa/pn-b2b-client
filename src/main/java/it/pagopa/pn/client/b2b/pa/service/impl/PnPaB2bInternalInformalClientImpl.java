package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.api.MessagesApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.api.NewInformalNotificationApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.api.SenderReadInformalNotificationB2BApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component()
//@ConditionalOnProperty(name = IPnPaB2bClient.IMPLEMENTATION_TYPE_PROPERTY, havingValue = "internal")
public class PnPaB2bInternalInformalClientImpl {
    private final String paId;
    private final String operatorId;

    private final MessagesApi messagesApi;
    private final SenderReadInformalNotificationB2BApi senderReadInformalNotificationB2BApi;
    private final NewInformalNotificationApi newInformalNotificationApi;

    private final List<String> groups;

    public PnPaB2bInternalInformalClientImpl(
            RestTemplate restTemplate,
            @Value("${pn.internal.delivery-push-base-url}") String deliveryBasePath,
            @Value("${pn.internal.pa-id}") String paId) {

        this.paId = paId;
        this.operatorId = "TestMv";
        this.groups = Collections.emptyList();

        this.messagesApi = new MessagesApi(newInformalApiClient(restTemplate, deliveryBasePath));
        this.senderReadInformalNotificationB2BApi = new SenderReadInformalNotificationB2BApi(newInformalApiClient(restTemplate, deliveryBasePath));
        this.newInformalNotificationApi = new NewInformalNotificationApi(newInformalApiClient(restTemplate, deliveryBasePath));

    }

    private static it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.ApiClient newInformalApiClient(RestTemplate restTemplate, String basePath) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.ApiClient newApiClient = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        return newApiClient;
    }

    public MessageResponse createMessage(NewMessageRequest request) {
        return messagesApi.newMessage(operatorId, CxTypeAuthFleet.PA, paId, request, groups);
    }

    public MessageResponse getMessage(UUID messageId) {
        return messagesApi.getMessageById(messageId, operatorId, CxTypeAuthFleet.PA, paId, groups);
    }

    public NewInformalNotificationResponse sendNewInformalNotificationV1(InformalNotificationRequestV1 informalNotificationRequestV1) throws RestClientException {
        return newInformalNotificationApi.sendNewInformalNotificationV1(operatorId, CxTypeAuthFleet.PA, "5b994d4a-0fa8-47ac-9c7b-354f1d44a1ce", "B2B", informalNotificationRequestV1, groups, null, null);
    }

    public NewInformalNotificationRequestStatusResponseV1 getInformalNotificationRequestStatusV1(String notificationRequestId) throws RestClientException {
        return senderReadInformalNotificationB2BApi.getInformalNotificationRequestStatusV1(operatorId, CxTypeAuthFleet.PA, paId, groups, notificationRequestId, null, null);
    }

    public NotificationAttachmentDownloadMetadataResponse getSentInformalNotificationAttachment(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String iun, Integer recipientIdx, String attachmentName, List<String> xPagopaPnCxGroups, Integer attachmentIdx) throws RestClientException {
        return senderReadInformalNotificationB2BApi.getSentInformalNotificationAttachment(operatorId, CxTypeAuthFleet.PA, paId, iun, recipientIdx, attachmentName, xPagopaPnCxGroups, attachmentIdx);
    }

    public NotificationAttachmentDownloadMetadataResponse getSentInformalNotificationDocument(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String iun, Integer docIdx, List<String> xPagopaPnCxGroups) throws RestClientException {
        return senderReadInformalNotificationB2BApi.getSentInformalNotificationDocument(operatorId, CxTypeAuthFleet.PA, paId, iun, docIdx, xPagopaPnCxGroups);
    }
}
