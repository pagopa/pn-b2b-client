package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v24.NotificationProcessCostResponse;
import org.springframework.web.client.RestClientException;

import java.util.List;


public interface IPnPaB2bClient extends SettableApiKey {
    String IMPLEMENTATION_TYPE_PROPERTY = "pn.api-type";

    List<PreLoadResponse> presignedUploadRequest(List<PreLoadRequest> preLoadRequest);

    /**
     * V1
     */
    NewNotificationResponse sendNewNotificationV1(NewNotificationRequest newNotificationRequest);

    NewNotificationRequestStatusResponse getNotificationRequestStatusV1(String notificationRequestId);

    NewNotificationRequestStatusResponse getNotificationRequestStatusAllParamV1(String notificationRequestId, String paProtocolNumber, String idempotenceToken);

    //TODO MATTEO
//    NotificationAttachmentDownloadMetadataResponse getSentNotificationDocumentV1(String iun, Integer docIndex);
//
//    NotificationAttachmentDownloadMetadataResponse getSentNotificationAttachmentV1(String iun, Integer recipientIdx, String attachmentName);
//
//    void paymentEventsRequestPagoPaV1(PaymentEventsRequestPagoPa paymentEventsRequestPagoPa) throws RestClientException;


    /**
     * V2
     */
    NewNotificationResponse sendNewNotificationV2(NewNotificationRequest newNotificationRequest);

    NewNotificationRequestStatusResponse getNotificationRequestStatusV2(String notificationRequestId);

    NewNotificationRequestStatusResponse getNotificationRequestStatusAllParamV2(String notificationRequestId, String paProtocolNumber, String idempotenceToken);

    //TODO MATTEO
//    NotificationAttachmentDownloadMetadataResponse getSentNotificationDocumentV2(String iun, Integer docIndex);
//
//    NotificationAttachmentDownloadMetadataResponse getSentNotificationAttachmentV2(String iun, Integer recipientIdx, String attachmentName);
//
//    void paymentEventsRequestPagoPaV2(PaymentEventsRequestPagoPa paymentEventsRequestPagoPa) throws RestClientException;

    /**
     * V21
     */
    NewNotificationResponse sendNewNotificationV21(NewNotificationRequestV21 newNotificationRequest);

    NewNotificationRequestStatusResponseV21 getNotificationRequestStatusV21(String notificationRequestId);

    NewNotificationRequestStatusResponseV21 getNotificationRequestStatusAllParamV21(String notificationRequestId, String paProtocolNumber, String idempotenceToken);

    /**
     * V23
     */
    NewNotificationResponse sendNewNotificationV23(NewNotificationRequestV23 newNotificationRequest);

    NewNotificationRequestStatusResponseV23 getNotificationRequestStatusV23(String notificationRequestId);

    NewNotificationRequestStatusResponseV23 getNotificationRequestStatusAllParamV23(String notificationRequestId, String paProtocolNumber, String idempotenceToken);

    /**
     * V24
     */
    NewNotificationResponse sendNewNotificationV24(NewNotificationRequestV24 newNotificationRequest);

    NewNotificationRequestStatusResponseV24 getNotificationRequestStatusV24(String notificationRequestId);

    NewNotificationRequestStatusResponseV24 getNotificationRequestStatusAllParamV24(String notificationRequestId, String paProtocolNumber, String idempotenceToken);

    /**
     * V25
     */
    NewNotificationResponse sendNewNotificationV25(NewNotificationRequestV25 newNotificationRequest);

    NewNotificationRequestStatusResponseV25 getNotificationRequestStatusV25(String notificationRequestId);

    NewNotificationRequestStatusResponseV25 getNotificationRequestStatusAllParamV25(String notificationRequestId, String protocolNumber, String idempotenceToken);

    /**
     * FullSentNotifications
     */
    FullSentNotification getSentNotificationV1(String iun);

    FullSentNotificationV20 getSentNotificationV2(String iun);

    FullSentNotificationV21 getSentNotificationV21(String iun);

    FullSentNotificationV23 getSentNotificationV23(String iun);

    FullSentNotificationV24 getSentNotificationV24(String iun);

    FullSentNotificationV25 getSentNotificationV25(String iun);

    FullSentNotificationV26 getSentNotificationV26(String iun);

    FullSentNotificationV27 getSentNotificationV27(String iun);


    NotificationAttachmentDownloadMetadataResponse getSentNotificationDocument(String iun, Integer docIndex);

    NotificationAttachmentDownloadMetadataResponse getSentNotificationAttachment(String iun, Integer recipientIdx, String attachmentName, Integer attachmentIdx);

    LegalFactDownloadMetadataResponse getLegalFact(String iun, LegalFactCategory legalFactType, String legalFactId);

    LegalFactDownloadMetadataResponse getDownloadLegalFact(String iun, String legalFactId);

    void paymentEventsRequestF24(PaymentEventsRequestF24 paymentEventsRequestF24) throws RestClientException;

    RequestStatus notificationCancellation(String iun) throws RestClientException;

    // PRICE E RESPONSE
    void paymentEventsRequestPagoPa(PaymentEventsRequestPagoPa paymentEventsRequestPagoPa) throws RestClientException;

    NotificationPriceResponse getNotificationPrice(String paTaxId, String noticeCode) throws RestClientException;

    NotificationPriceResponseV23 getNotificationPriceV23(String paTaxId, String noticeCode) throws RestClientException;

    NotificationProcessCostResponse getNotificationProcessCost(String iun, Integer recipientIndex, it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v24.NotificationFeePolicy notificationFeePolicy, Boolean applyCost, Integer paFee, Integer vat) throws RestClientException;
}