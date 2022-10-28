package it.pagopa.pn.client.b2b.pa.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import org.springframework.web.client.RestClientException;
import java.util.List;

public interface IPnPaB2bClient {

    String IMPLEMENTATION_TYPE_PROPERTY = "pn.api-type";

    enum ApiKeyType { MVP_1, MVP_2, GA }

    boolean setApiKeys(ApiKeyType apiKey);

    ApiKeyType getApiKeySetted();

    List<PreLoadResponse> presignedUploadRequest(List<PreLoadRequest> preLoadRequest);

    NewNotificationResponse sendNewNotification(NewNotificationRequest newNotificationRequest);

    FullSentNotification getSentNotification(String iun);

    NewNotificationRequestStatusResponse getNotificationRequestStatus(String notificationRequestId);


    NotificationAttachmentDownloadMetadataResponse getSentNotificationDocument(String iun, Integer docidx) ;

    NotificationAttachmentDownloadMetadataResponse getSentNotificationAttachment(String iun, Integer recipientIdx, String attachname) ;

    LegalFactDownloadMetadataResponse getLegalFact(String iun, LegalFactCategory legalFactType, String legalFactId) ;

    NotificationPriceResponse getNotificationPrice(String paTaxId, String noticeCode) throws RestClientException;

}
