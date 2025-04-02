package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.FullSentNotification;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.FullSentNotificationV20;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.FullSentNotificationV21;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.NotificationProcessCostResponse;
import org.springframework.web.client.RestClientException;

import java.util.List;


public interface IPnPaB2bClient extends SettableApiKey {
    String IMPLEMENTATION_TYPE_PROPERTY = "pn.api-type";

    List<PreLoadResponse> presignedUploadRequest(List<PreLoadRequest> preLoadRequest);

    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationResponse sendNewNotificationV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationRequest newNotificationRequest);

    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationResponse sendNewNotificationV2(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationRequest newNotificationRequest);

    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationResponse sendNewNotificationV21(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationRequestV21 newNotificationRequest);

    NewNotificationResponse sendNewNotificationV23(NewNotificationRequestV23 newNotificationRequest);

    NewNotificationResponse sendNewNotificationV24(NewNotificationRequestV24 newNotificationRequest);

    NewNotificationResponse sendNewNotificationV25(NewNotificationRequestV25 newNotificationRequest);

    FullSentNotification getSentNotificationV1(String iun);

    FullSentNotificationV20 getSentNotificationV2(String iun);

    FullSentNotificationV21 getSentNotificationV21(String iun);

    FullSentNotificationV23 getSentNotificationV23(String iun);

    FullSentNotificationV24 getSentNotificationV24(String iun);

    FullSentNotificationV25 getSentNotificationV25(String iun);

    FullSentNotificationV26 getSentNotificationV26(String iun);

    FullSentNotificationV27 getSentNotificationV27(String iun);

    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationRequestStatusResponse getNotificationRequestStatusV1(String notificationRequestId);

    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationRequestStatusResponse getNotificationRequestStatusV2(String notificationRequestId);

    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationRequestStatusResponseV21 getNotificationRequestStatusV21(String notificationRequestId);

    NewNotificationRequestStatusResponseV23 getNotificationRequestStatusV23(String notificationRequestId);

    NewNotificationRequestStatusResponseV24 getNotificationRequestStatusV24(String notificationRequestId);

    NewNotificationRequestStatusResponseV25 getNotificationRequestStatusV25(String notificationRequestId);

    NewNotificationRequestStatusResponseV23 getNotificationRequestStatusAllParam(String notificationRequestId, String paProtocolNumber, String idempotenceToken);

    NotificationAttachmentDownloadMetadataResponse getSentNotificationDocument(String iun, Integer docidx);

    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationAttachmentDownloadMetadataResponse getSentNotificationDocumentV1(String iun, Integer docidx);

    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationAttachmentDownloadMetadataResponse getSentNotificationDocumentV2(String iun, Integer docidx);

    LegalFactDownloadMetadataResponse getLegalFact(String iun, LegalFactCategory legalFactType, String legalFactId);

    LegalFactDownloadMetadataResponse getDownloadLegalFact(String iun, String legalFactId);

    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPriceResponse getNotificationPrice(String paTaxId, String noticeCode) throws RestClientException;

    NotificationPriceResponseV23 getNotificationPriceV23(String paTaxId, String noticeCode) throws RestClientException;

    NotificationProcessCostResponse getNotificationProcessCost(String iun, Integer recipientIndex, it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.NotificationFeePolicy notificationFeePolicy, Boolean applyCost, Integer paFee, Integer vat) throws RestClientException;

    void paymentEventsRequestPagoPa(PaymentEventsRequestPagoPa paymentEventsRequestPagoPa) throws RestClientException;

    void paymentEventsRequestPagoPaV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.PaymentEventsRequestPagoPa paymentEventsRequestPagoPa) throws RestClientException;

    void paymentEventsRequestPagoPaV2(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.PaymentEventsRequestPagoPa paymentEventsRequestPagoPa) throws RestClientException;

    void paymentEventsRequestF24(PaymentEventsRequestF24 paymentEventsRequestF24) throws RestClientException;

    RequestStatus notificationCancellation(String iun) throws RestClientException;

    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationAttachmentDownloadMetadataResponse getSentNotificationAttachmentV1(String iun, Integer recipientIdx, String attachmentName);

    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationAttachmentDownloadMetadataResponse getSentNotificationAttachmentV2(String iun, Integer recipientIdx, String attachmentName);

    NotificationAttachmentDownloadMetadataResponse getSentNotificationAttachment(String iun, Integer recipientIdx, String attachname, Integer attachmentIdx);
}