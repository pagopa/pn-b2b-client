package it.pagopa.pn.client.b2b.pa.testclient;

import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.ThirdPartyMessage;
import org.springframework.web.client.RestClientException;

public interface IPnAppIOB2bClient {

    NotificationAttachmentDownloadMetadataResponse getSentNotificationDocument(String iun, Integer docIdx, String xPagopaCxTaxid) throws RestClientException;

    LegalFactDownloadMetadataResponse getLegalFact(String iun, String legalFactType, String legalFactId, String xPagopaCxTaxid) throws RestClientException;

    ThirdPartyMessage getReceivedNotification(String iun, String xPagopaCxTaxid) throws RestClientException;

}
