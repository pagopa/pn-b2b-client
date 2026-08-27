package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.FullNotificationSearchResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.LegalNotificationSearchResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.deliverypushb2b.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffDocumentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffFullNotificationV1;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffLegalFactId;
import it.pagopa.pn.client.b2b.pa.domain.Destinatario;
import it.pagopa.pn.client.b2b.pa.domain.NotificationSearchParam;
import it.pagopa.pn.client.b2b.pa.service.IPnWebRecipientClient;
import it.pagopa.pn.client.b2b.pa.wrapper.BundleFullReceivedNotification;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.v25.model.LegalFactCategory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.UUID;

/**
 * Router tra le due implementazioni di IPnWebRecipientClient disponibili nel flusso @useB2B:
 * le utenze PG dedicate con token _B2B (PG_B2B_1/PG_B2B_2) usano {@link RecipientB2BExternalClientImpl}
 * (openapi di destinatari strutturati); ogni altra utenza (PF o PG classica) usa
 * {@link RecipientInternalClientImpl} (openapi internal). La scelta avviene una sola volta in
 * {@link #setBearerToken(BearerTokenType)}, cosi' i metodi di business si limitano a inoltrare
 * la chiamata all'implementazione attiva.
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class B2BRecipientExternalClientImpl implements IPnWebRecipientClient {
    private final RecipientInternalClientImpl internalClient;
    private final RecipientB2BExternalClientImpl b2bExternalClient;
    private IPnWebRecipientClient activeClient;

    public B2BRecipientExternalClientImpl(RecipientInternalClientImpl internalClient, RecipientB2BExternalClientImpl b2bExternalClient) {
        this.internalClient = internalClient;
        this.b2bExternalClient = b2bExternalClient;
        this.activeClient = internalClient;
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        this.activeClient = isB2BUtenza(bearerToken) ? b2bExternalClient : internalClient;
        return activeClient.setBearerToken(bearerToken);
    }

    private static boolean isB2BUtenza(BearerTokenType bearerToken) {
        return bearerToken == BearerTokenType.PG_B2B_1 || bearerToken == BearerTokenType.PG_B2B_2;
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return activeClient.getBearerTokenSetted();
    }

    @Override
    public BundleFullReceivedNotification getFullReceivedNotification(String iun, String mandateId) throws RestClientException {
        return activeClient.getFullReceivedNotification(iun, mandateId);
    }

    @Override
    public BffFullNotificationV1 getBffFullNotification(String iun, String mandateId) throws RestClientException {
        return activeClient.getBffFullNotification(iun, mandateId);
    }

    @Override
    public NotificationAttachmentDownloadMetadataResponse getReceivedNotificationAttachment(String iun, String attachmentName, UUID mandateId, Integer attachmentIdx) throws RestClientException {
        return activeClient.getReceivedNotificationAttachment(iun, attachmentName, mandateId, attachmentIdx);
    }

    @Override
    public BffDocumentDownloadMetadataResponse getReceivedNotificationAttachment(String iun, String attachmentName, UUID mandateId) throws RestClientException {
        return activeClient.getReceivedNotificationAttachment(iun, attachmentName, mandateId);
    }

    @Override
    public NotificationAttachmentDownloadMetadataResponse getReceivedNotificationDocument(String iun, Integer docIdx, UUID mandateId) throws RestClientException {
        return activeClient.getReceivedNotificationDocument(iun, docIdx, mandateId);
    }

    @Override
    public LegalNotificationSearchResponse searchReceivedDelegatedNotification(Destinatario destinatario, NotificationSearchParam param) throws RestClientException {
        return activeClient.searchReceivedDelegatedNotification(destinatario, param);
    }

    @Override
    public FullNotificationSearchResponse searchReceivedNotification(Destinatario destinatario, NotificationSearchParam param) throws RestClientException {
        return activeClient.searchReceivedNotification(destinatario, param);
    }

    @Override
    public LegalFactDownloadMetadataResponse getLegalFact(String iun, LegalFactCategory legalFactType, String legalFactId) throws RestClientException {
        return activeClient.getLegalFact(iun, legalFactType, legalFactId);
    }

    @Override
    public BffDocumentDownloadMetadataResponse getDocumentsWeb(String iun, String documentId, UUID mandateId) throws RestClientException {
        return activeClient.getDocumentsWeb(iun, documentId, mandateId);
    }

    @Override
    public List<BffLegalFactId> getLegalFactsV20(String iun, UUID mandateId) throws RestClientException {
        return activeClient.getLegalFactsV20(iun, mandateId);
    }

    @Override
    public BffDocumentDownloadMetadataResponse downloadLegalFactById(String iun, String legalFactId, UUID mandateId) throws RestClientException {
        return activeClient.downloadLegalFactById(iun, legalFactId, mandateId);
    }
}
