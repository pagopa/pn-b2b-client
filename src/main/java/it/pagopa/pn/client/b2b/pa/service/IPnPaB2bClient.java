package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.FullSentNotification;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.FullSentNotificationV20;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.FullSentNotificationV21;
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
    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationResponse sendNewNotificationV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationRequest newNotificationRequest);

    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationRequestStatusResponse getNotificationRequestStatusV1(String notificationRequestId);

    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationRequestStatusResponse getNotificationRequestStatusAllParamV1(String notificationRequestId, String paProtocolNumber, String idempotenceToken);

    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationAttachmentDownloadMetadataResponse getSentNotificationDocumentV1(String iun, Integer docidx);

    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationAttachmentDownloadMetadataResponse getSentNotificationAttachmentV1(String iun, Integer recipientIdx, String attachmentName);

    void paymentEventsRequestPagoPaV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.PaymentEventsRequestPagoPa paymentEventsRequestPagoPa) throws RestClientException;


    /**
     * V2
     */
    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationResponse sendNewNotificationV2(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationRequest newNotificationRequest);

    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationRequestStatusResponse getNotificationRequestStatusV2(String notificationRequestId);

    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationRequestStatusResponse getNotificationRequestStatusAllParamV2(String notificationRequestId, String paProtocolNumber, String idempotenceToken);

    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationAttachmentDownloadMetadataResponse getSentNotificationDocumentV2(String iun, Integer docidx);

    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationAttachmentDownloadMetadataResponse getSentNotificationAttachmentV2(String iun, Integer recipientIdx, String attachmentName);

    void paymentEventsRequestPagoPaV2(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.PaymentEventsRequestPagoPa paymentEventsRequestPagoPa) throws RestClientException;

    /**
     * V21
     */
    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationResponse sendNewNotificationV21(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationRequestV21 newNotificationRequest);

    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationRequestStatusResponseV21 getNotificationRequestStatusV21(String notificationRequestId);

    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationRequestStatusResponseV21 getNotificationRequestStatusAllParamV21(String notificationRequestId, String paProtocolNumber, String idempotenceToken);

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

    NewNotificationRequestStatusResponseV25 getNotificationRequestStatusAllParamV25(String notificationRequestId, String protocoloNumber, String idempotenceToken);

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


    NotificationAttachmentDownloadMetadataResponse getSentNotificationDocument(String iun, Integer docidx);

    LegalFactDownloadMetadataResponse getLegalFact(String iun, LegalFactCategory legalFactType, String legalFactId);

    LegalFactDownloadMetadataResponse getDownloadLegalFact(String iun, String legalFactId);

    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPriceResponse getNotificationPrice(String paTaxId, String noticeCode) throws RestClientException;

    NotificationPriceResponseV23 getNotificationPriceV23(String paTaxId, String noticeCode) throws RestClientException;

    NotificationProcessCostResponse getNotificationProcessCost(String iun, Integer recipientIndex, it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v24.NotificationFeePolicy notificationFeePolicy, Boolean applyCost, Integer paFee, Integer vat) throws RestClientException;

    void paymentEventsRequestPagoPa(PaymentEventsRequestPagoPa paymentEventsRequestPagoPa) throws RestClientException;

    void paymentEventsRequestF24(PaymentEventsRequestF24 paymentEventsRequestF24) throws RestClientException;

    RequestStatus notificationCancellation(String iun) throws RestClientException;

    NotificationAttachmentDownloadMetadataResponse getSentNotificationAttachment(String iun, Integer recipientIdx, String attachname, Integer attachmentIdx);
}