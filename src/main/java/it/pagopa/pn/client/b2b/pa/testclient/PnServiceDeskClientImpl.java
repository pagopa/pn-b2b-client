package it.pagopa.pn.client.b2b.pa.testclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.ApiClient;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.api.HealthCheckApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.api.NotificationApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.api.OperationApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.model.*;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDeskIntegration.api.ApiKeysApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDeskIntegration.api.NotificationAndMessageApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDeskIntegration.api.PaApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDeskIntegration.api.ProfileApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDeskIntegration.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.List;


@Component
public class PnServiceDeskClientImpl implements IPServiceDeskClientImpl{

    private final ApplicationContext ctx;
    private final RestTemplate restTemplate;



    private final String basePath;

    private final String apiKey;

    //Call Center Evoluto....
    private final HealthCheckApi healthCheckApi;
    private final NotificationApi notification;
    private final OperationApi operation;

    //Integration Cruscotto Assistenza....
    private final ApiKeysApi apiKeysApi;
    private final NotificationAndMessageApi notificationAndMessageApi;
    private final PaApi paApi;
    private final ProfileApi profileApi;
/*
    private final NotificationRequest req;
    private final CreateOperationRequest creaOp;

    private final VideoUploadRequest videoreq;

    private final SearchNotificationRequest searchreq;

    private final AnalogAddress address;

 */

    private final String paId;
    private final String operatorId;

    public PnServiceDeskClientImpl(
            ApplicationContext ctx,
            RestTemplate restTemplate,
            @Value("${pn.externalChannels.base-url}") String deliveryBasePath ,
            @Value("${pn.external.api-keys.service-desk}") String apiKeyBase ,
            @Value("${pn.internal.pa-id}") String paId
    ) {

        this.paId = paId;
        this.operatorId = "AutomationMv";
        this.ctx = ctx;
        this.restTemplate = restTemplate;
        this.basePath = deliveryBasePath;
        this.apiKey=apiKeyBase;

        //Call Center Evoluto....
        this.healthCheckApi = new HealthCheckApi(newApiClient( restTemplate, basePath,apiKey));
        this.notification = new NotificationApi(newApiClient( restTemplate, basePath,apiKey));
        this.operation = new OperationApi(newApiClient( restTemplate, basePath,apiKey));

        //Integration Cruscotto Assistenza....
        this.apiKeysApi = new ApiKeysApi(newApiClientIntegration( restTemplate, basePath,apiKey));
        this.notificationAndMessageApi = new NotificationAndMessageApi(newApiClientIntegration( restTemplate, basePath,apiKey));
        this.paApi = new PaApi(newApiClientIntegration( restTemplate, basePath,apiKey));
        this.profileApi = new ProfileApi(newApiClientIntegration( restTemplate, basePath,apiKey));

    }

    //Call Center Evoluto....
    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String apiKey) {
        ApiClient newApiClient = new ApiClient( restTemplate );
        newApiClient.setBasePath( basePath );
        newApiClient.addDefaultHeader("x-api-key", apiKey );
        return newApiClient;
    }

    //Integration Cruscotto Assistenza....
    private static it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDeskIntegration.ApiClient newApiClientIntegration(RestTemplate restTemplate, String basePath, String apiKey) {
        it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDeskIntegration.ApiClient newApiClient = new it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDeskIntegration.ApiClient( restTemplate );
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

    //Integration Cruscotto Assistenza....
    public ResponseApiKeys getApiKeys(String paId) throws RestClientException{
        return apiKeysApi.getApiKeys(paId);
    }

    public DocumentsResponse getDocumentsOfIUN(String iun, DocumentsRequest documentsRequest) throws RestClientException {
        return notificationAndMessageApi.getDocumentsOfIUN(operatorId, iun, documentsRequest);
    }

    public NotificationDetailResponse getNotificationFromIUN(String iun) throws RestClientException {
        return notificationAndMessageApi.getNotificationFromIUN(operatorId, iun);
    }

    public TimelineResponse getTimelineOfIUN(String iun) throws RestClientException {
        return notificationAndMessageApi.getTimelineOfIUN(operatorId, iun);
    }

    public SearchNotificationsResponse searchNotificationsAsDelegateFromInternalId(String mandateId, String delegateInternalId, String recipientType, Integer size, String nextPagesKey, OffsetDateTime startDate, OffsetDateTime endDate) throws RestClientException {
     return notificationAndMessageApi.searchNotificationsAsDelegateFromInternalId(operatorId, mandateId, delegateInternalId,recipientType, startDate, endDate, size, nextPagesKey );
    }

    public SearchNotificationsResponse searchNotificationsFromTaxId(Integer size, String nextPagesKey, OffsetDateTime startDate, OffsetDateTime endDate, SearchNotificationsRequest searchNotificationsRequest) throws RestClientException {
        return notificationAndMessageApi.searchNotificationsFromTaxId(operatorId, startDate, endDate, size,nextPagesKey,  searchNotificationsRequest);
    }

    public TimelineResponse getTimelineOfIUNAndTaxId(String iun, SearchNotificationsRequest searchNotificationsRequest) throws RestClientException{
        return notificationAndMessageApi.getTimelineOfIUNAndTaxId(operatorId, iun, searchNotificationsRequest);
    }

    public List<PaSummary> getListOfOnboardedPA() throws RestClientException {
        return paApi.getListOfOnboardedPA(operatorId);
    }

    public SearchNotificationsResponse searchNotificationsFromSenderId(Integer size, String nextPagesKey, PaNotificationsRequest paNotificationsRequest) throws RestClientException {
        return paApi.searchNotificationsFromSenderId(operatorId, size, nextPagesKey, paNotificationsRequest);
    }

    public ProfileResponse getProfileFromTaxId(ProfileRequest profileRequest) throws RestClientException {
        return profileApi.getProfileFromTaxId(operatorId, profileRequest);
    }
}
