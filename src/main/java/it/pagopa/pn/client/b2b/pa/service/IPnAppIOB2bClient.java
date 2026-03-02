package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.RequestCheckQrMandateDto;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.ResponseCheckQrMandateDto;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.ThirdPartyMessage;
import org.springframework.web.client.RestClientException;

import java.util.UUID;


public interface IPnAppIOB2bClient {
    NotificationAttachmentDownloadMetadataResponse getSentNotificationDocument(String iun, Integer docIdx, String xPagopaCxTaxid, UUID mandateId) throws RestClientException;
    ThirdPartyMessage getReceivedNotification(String iun, String xPagopaCxTaxid, UUID mandateId) throws RestClientException;
    ThirdPartyMessage getReceivedNotification(String iun, String xPagopaCxTaxid, UUID mandateId, String xPagopaPnIoSrc) throws RestClientException;
    NotificationAttachmentDownloadMetadataResponse getReceivedNotificationAttachment(String iun, String attachmentName, String xPagopaCxTaxid, Integer attachmentIdx, UUID mandateId) throws RestClientException ;
    NotificationAttachmentDownloadMetadataResponse getReceivedNotificationAttachmentByUrl(String url, String xPagopaCxTaxid) throws RestClientException ;
    ResponseCheckQrMandateDto checkAarQrCodeIO(String xPagopaCxTaxid, RequestCheckQrMandateDto requestCheckQrMandateDto) throws RestClientException;
    ResponseCheckQrMandateDto checkAarQrCodeIO(String xPagopaCxTaxid, String xPagopaLollipopUserId, RequestCheckQrMandateDto requestCheckQrMandateDto) throws RestClientException;
}