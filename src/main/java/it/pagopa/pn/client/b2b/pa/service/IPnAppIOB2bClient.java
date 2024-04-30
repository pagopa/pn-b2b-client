package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.ThirdPartyMessage;
import org.springframework.web.client.RestClientException;


public interface IPnAppIOB2bClient {
    NotificationAttachmentDownloadMetadataResponse getSentNotificationDocument(String iun, Integer docIdx, String xPagopaCxTaxid) throws RestClientException;
    ThirdPartyMessage getReceivedNotification(String iun, String xPagopaCxTaxid) throws RestClientException;
    NotificationAttachmentDownloadMetadataResponse getReceivedNotificationAttachment(String iun, String attachmentName, String xPagopaCxTaxid, Integer attachmentIdx) throws RestClientException ;
    NotificationAttachmentDownloadMetadataResponse getReceivedNotificationAttachmentByUrl(String url, String xPagopaCxTaxid) throws RestClientException ;
}