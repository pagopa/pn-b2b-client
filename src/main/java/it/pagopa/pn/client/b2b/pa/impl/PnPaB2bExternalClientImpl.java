package it.pagopa.pn.client.b2b.pa.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.ApiClient;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api.LegalFactsApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api.NewNotificationApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api.NotificationPriceApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api.SenderReadB2BApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

@Component()
@ConditionalOnProperty( name = IPnPaB2bClient.IMPLEMENTATION_TYPE_PROPERTY, havingValue = "external", matchIfMissing = true)
public class PnPaB2bExternalClientImpl implements IPnPaB2bClient {

    private final ApplicationContext ctx;
    private final RestTemplate restTemplate;
    private final NewNotificationApi newNotificationApi;
    private final SenderReadB2BApi senderReadB2BApi;
    private final LegalFactsApi legalFactsApi;
    private final NotificationPriceApi notificationPriceApi;

    private final String apiKey;

    public PnPaB2bExternalClientImpl(
            ApplicationContext ctx,
            RestTemplate restTemplate,
            @Value("${pn.external.base-url}") String basePath,
            @Value("${pn.external.api-key}") String apiKey
    ) {
        this.ctx = ctx;
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;

        this.newNotificationApi = new NewNotificationApi( newApiClient( restTemplate, basePath, apiKey) );
        this.senderReadB2BApi = new SenderReadB2BApi( newApiClient( restTemplate, basePath, apiKey) );
        this.legalFactsApi = new LegalFactsApi(newApiClient( restTemplate, basePath, apiKey));
        this.notificationPriceApi = new NotificationPriceApi(newApiClient( restTemplate, basePath, apiKey));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String apikey ) {
        ApiClient newApiClient = new ApiClient( restTemplate );
        newApiClient.setBasePath( basePath );
        newApiClient.addDefaultHeader("x-api-key", apikey );
        return newApiClient;
    }

    public NotificationAttachmentDownloadMetadataResponse getSentNotificationDocument(String iun, Integer docidx) {
        return senderReadB2BApi.getSentNotificationDocument(iun, docidx);
    }

    public NotificationAttachmentDownloadMetadataResponse getSentNotificationAttachment(String iun, Integer recipientIdx, String attachname) {
        return senderReadB2BApi.getSentNotificationAttachment(iun, recipientIdx, attachname);
    }

    public LegalFactDownloadMetadataResponse getLegalFact(String iun, LegalFactCategory legalFactType, String legalFactId) {
        return legalFactsApi.getLegalFact(iun, legalFactType, legalFactId);
    }

    @Override
    public NotificationPriceResponse getNotificationPrice(String paTaxId, String noticeCode) throws RestClientException {
        return this.notificationPriceApi.getNotificationPrice(paTaxId,noticeCode);
    }

    public List<PreLoadResponse> presignedUploadRequest(List<PreLoadRequest> preLoadRequest) {
        return newNotificationApi.presignedUploadRequest( preLoadRequest );
    }

    public NewNotificationResponse sendNewNotification(NewNotificationRequest newNotificationRequest) {
        return newNotificationApi.sendNewNotification( newNotificationRequest );
    }

    @Override
    public FullSentNotification getSentNotification(String iun) {
        return senderReadB2BApi.getSentNotification( iun );
    }

    @Override
    public NewNotificationRequestStatusResponse getNotificationRequestStatus(String notificationRequestId) {
        return senderReadB2BApi.getNotificationRequestStatus( notificationRequestId, null, null );
    }
}
