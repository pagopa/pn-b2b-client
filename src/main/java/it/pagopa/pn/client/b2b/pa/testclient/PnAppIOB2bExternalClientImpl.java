package it.pagopa.pn.client.b2b.pa.testclient;

import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.ApiClient;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.api.AppIoPnDocumentsApi;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.api.AppIoPnLegalFactsApi;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.api.AppIoPnNotificationApi;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.IOReceivedNotification;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.NotificationAttachmentDownloadMetadataResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class PnAppIOB2bExternalClientImpl implements IPnAppIOB2bClient{

    private final ApplicationContext ctx;
    private final RestTemplate restTemplate;
    private final AppIoPnDocumentsApi appIoPnDocumentsApi;
    private final AppIoPnLegalFactsApi appIoPnLegalFactsApi;
    private final AppIoPnNotificationApi appIoPnNotificationApi;

    private final String devApiKey;
    private final String devBasePath;


    public PnAppIOB2bExternalClientImpl(
            ApplicationContext ctx,
            RestTemplate restTemplate,
            @Value("${pn.appio.externa.base-url}") String devBasePath,
            @Value("${pn.external.appio.api-key}") String devApiKey
    ) {
        this.ctx = ctx;
        this.restTemplate = restTemplate;
        this.devApiKey = devApiKey;
        this.devBasePath = devBasePath;

        this.appIoPnDocumentsApi = new AppIoPnDocumentsApi( newApiClient( restTemplate, devBasePath, devApiKey) );
        this.appIoPnLegalFactsApi = new AppIoPnLegalFactsApi( newApiClient( restTemplate, devBasePath, devApiKey) );
        this.appIoPnNotificationApi = new AppIoPnNotificationApi( newApiClient( restTemplate, devBasePath, devApiKey) );
    }


    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String apikey ) {
        ApiClient newApiClient = new ApiClient( restTemplate );
        newApiClient.setBasePath( basePath );
        newApiClient.addDefaultHeader("x-api-key", apikey );
        return newApiClient;
    }

    public NotificationAttachmentDownloadMetadataResponse getSentNotificationDocument(String iun, Integer docIdx, String xPagopaCxTaxid) throws RestClientException {
        return this.appIoPnDocumentsApi.getSentNotificationDocument(iun, docIdx, xPagopaCxTaxid);
    }


    public LegalFactDownloadMetadataResponse getLegalFact(String iun, String legalFactType, String legalFactId, String xPagopaCxTaxid) throws RestClientException {
        return this.appIoPnLegalFactsApi.getLegalFact(iun, legalFactType, legalFactId, xPagopaCxTaxid);
    }


    public IOReceivedNotification getReceivedNotification(String iun, String xPagopaCxTaxid) throws RestClientException {
        return this.appIoPnNotificationApi.getReceivedNotification(iun, xPagopaCxTaxid).getDetails();
    }

}
