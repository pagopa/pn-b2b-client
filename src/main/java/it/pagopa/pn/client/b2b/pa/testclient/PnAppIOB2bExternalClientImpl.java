package it.pagopa.pn.client.b2b.pa.testclient;

import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.ApiClient;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.api.AppIoPnDocumentsApi;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.api.AppIoPnNotificationApi;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.api.AppIoPnPaymentsApi;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.IOReceivedNotification;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.ThirdPartyMessage;
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
    private final AppIoPnNotificationApi appIoPnNotificationApi;
    private final AppIoPnPaymentsApi appIoPnPaymentsApi;

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
        this.appIoPnNotificationApi = new AppIoPnNotificationApi( newApiClient( restTemplate, devBasePath, devApiKey) );
        this.appIoPnPaymentsApi = new AppIoPnPaymentsApi( newApiClient( restTemplate, devBasePath, devApiKey) );

    }


    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String apikey ) {
        ApiClient newApiClient = new ApiClient( restTemplate );
        newApiClient.addDefaultHeader("Accept", "application/io+json" );
        newApiClient.setBasePath( basePath );
        newApiClient.addDefaultHeader("x-api-key", apikey );
        return newApiClient;
    }

    public NotificationAttachmentDownloadMetadataResponse getSentNotificationDocument(String iun, Integer docIdx, String xPagopaCxTaxid) throws RestClientException {
        return this.appIoPnDocumentsApi.getReceivedNotificationDocument(iun, docIdx, xPagopaCxTaxid,null,null,null,null,null,null,null,null,null);
    }




    public ThirdPartyMessage getReceivedNotification(String iun, String xPagopaCxTaxid) throws RestClientException {
        return this.appIoPnNotificationApi.getReceivedNotification(iun, xPagopaCxTaxid,null,null,null,null,null,null,null,null,null);
    }



    public NotificationAttachmentDownloadMetadataResponse getReceivedNotificationAttachment(String iun, String attachmentName, String xPagopaCxTaxid, Integer attachmentIdx) throws RestClientException {
        return this.appIoPnPaymentsApi.getReceivedNotificationAttachment(iun, attachmentName, xPagopaCxTaxid, attachmentIdx, null, null, null, null, null, null, null, null, null);
    }


}
