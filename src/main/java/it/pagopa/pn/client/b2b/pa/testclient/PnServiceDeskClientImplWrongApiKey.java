package it.pagopa.pn.client.b2b.pa.testclient;

import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.ApiClient;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.api.HealthCheckApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.api.NotificationApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.api.OperationApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


@Component
public class PnServiceDeskClientImplWrongApiKey implements IPServiceDeskClientImplWrongApiKey{

    private final ApplicationContext ctx;
    private final RestTemplate restTemplate;



    private final String basePath;

    private final String apiKey;

    private final HealthCheckApi healthCheckApi;

    private final NotificationApi notification;

    private final OperationApi operation;

    private final String paId;
    private final String operatorId;

    public PnServiceDeskClientImplWrongApiKey(
            ApplicationContext ctx,
            RestTemplate restTemplate,
            @Value("${pn.externalChannels.base-url.pagopa}") String deliveryBasePath ,
            @Value("${pn.external.api-keys.service-desk}") String apiKeyBase ,
            @Value("${pn.internal.pa-id}") String paId
    ) {

        this.paId = paId;
        this.operatorId = "AutomationMv";
        this.ctx = ctx;
        this.restTemplate = restTemplate;
        this.basePath = deliveryBasePath;
        this.apiKey=apiKeyBase+"ERR";
        this.healthCheckApi = new HealthCheckApi(newApiClient( restTemplate, basePath,apiKey));
        this.notification = new NotificationApi(newApiClient( restTemplate, basePath,apiKey));
        this.operation = new OperationApi(newApiClient( restTemplate, basePath,apiKey));


    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String apiKey) {
 //private static ApiClient newApiClient(RestTemplate restTemplate, String basePath) {
        ApiClient newApiClient = new ApiClient( restTemplate );
        newApiClient.setBasePath( basePath );
        newApiClient.addDefaultHeader("x-api-key", apiKey );
        return newApiClient;
    }

    /*
     * Permette di recuperare per ogni CF il numero totale di pratiche non consegnate per irreperibilità totale con ultimo
     * con ultimo tentativo di consegna <120 gg
     * <p><b>200</b> - OK
     * <p><b>400</b> - Invalid input
     * <p><b>404</b> - Not found
     * <p><b>500</b> - Internal Server Error
     * @param cf tax id del cliente persona fisica
     * @return NotificationsUnreachableResponse
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */

    /*
    public NotificationsUnreachableResponse notification(String cf) throws RestClientException {
        req.setTaxId(cf);
        return notification.numberOfUnreachableNotifications(operatorId,req);

    }
    */

    public NotificationsUnreachableResponse notification(NotificationRequest notificationRequest) throws RestClientException {
        return notification.numberOfUnreachableNotifications(operatorId, notificationRequest);

    }

    /*
    public OperationsResponse createOperation(String cf, String ticketId, String operationTickerId, AnalogAddress address)throws RestClientException {
        creaOp.setTaxId(cf);
        creaOp.setTicketId(ticketId);
        creaOp.setTicketOperationId(operationTickerId);
        address.setAddress("via roma");
        address.setAddressRow2("via nizza");
        creaOp.setAddress(address);
        return operation.createOperation(operatorId,creaOp);
    }
    */
    public OperationsResponse createOperation(CreateOperationRequest createOperationRequest)throws RestClientException {
        return operation.createOperation(operatorId,createOperationRequest);
    }

/*
    public VideoUploadResponse presignedUrlVideoUpload(String operationid, String preloadIdx, String sha256,String contentType){
        videoreq.setPreloadIdx(preloadIdx);
        videoreq.setSha256(sha256);
        videoreq.setContentType(contentType);
        return operation.presignedUrlVideoUpload(operatorId, operationid, videoreq);
        }
 */

    public VideoUploadResponse presignedUrlVideoUpload(String operationid, VideoUploadRequest videoUploadRequest){
        return operation.presignedUrlVideoUpload(operatorId, operationid, videoUploadRequest);
        }


/*
    public SearchResponse searchOperationsFromTaxId(String cf){
        searchreq.setTaxId(cf);
        return operation.searchOperationsFromTaxId(operatorId, searchreq);

    }
*/
    public SearchResponse searchOperationsFromTaxId(SearchNotificationRequest searchNotificationRequest){
    return operation.searchOperationsFromTaxId(operatorId, searchNotificationRequest);

}



}
