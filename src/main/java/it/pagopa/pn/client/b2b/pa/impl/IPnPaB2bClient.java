package it.pagopa.pn.client.b2b.pa.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.testclient.SettableApiKey;
import org.springframework.web.client.RestClientException;
import java.util.List;

public interface IPnPaB2bClient extends SettableApiKey {

    String IMPLEMENTATION_TYPE_PROPERTY = "pn.api-type";

    List<PreLoadResponse> presignedUploadRequest(List<PreLoadRequest> preLoadRequest);

    NewNotificationResponse sendNewNotification(NewNotificationRequest newNotificationRequest);

    FullSentNotificationV20 getSentNotification(String iun);

    NewNotificationRequestStatusResponse getNotificationRequestStatus(String notificationRequestId);

    NewNotificationRequestStatusResponse getNotificationRequestStatusAllParam(String notificationRequestId,String paProtocolNumber, String idempotenceToken);

    NotificationAttachmentDownloadMetadataResponse getSentNotificationDocument(String iun, Integer docidx) ;

    NotificationAttachmentDownloadMetadataResponse getSentNotificationAttachment(String iun, Integer recipientIdx, String attachname) ;

    LegalFactDownloadMetadataResponse getLegalFact(String iun, LegalFactCategory legalFactType, String legalFactId) ;

    LegalFactDownloadMetadataResponse getDownloadLegalFact(String iun, String legalFactId) ;

    NotificationPriceResponse getNotificationPrice(String paTaxId, String noticeCode) throws RestClientException;

    void paymentEventsRequestPagoPa(PaymentEventsRequestPagoPa paymentEventsRequestPagoPa) throws RestClientException;

    void paymentEventsRequestF24(PaymentEventsRequestF24 paymentEventsRequestF24) throws RestClientException;

}
