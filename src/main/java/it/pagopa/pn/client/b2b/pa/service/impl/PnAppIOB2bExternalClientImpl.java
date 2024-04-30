package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.ApiClient;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.api.AppIoPnDocumentsApi;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.api.AppIoPnNotificationApi;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.api.AppIoPnPaymentsApi;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.ThirdPartyMessage;
import it.pagopa.pn.client.b2b.pa.service.IPnAppIOB2bClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class PnAppIOB2bExternalClientImpl implements IPnAppIOB2bClient {
    private final AppIoPnDocumentsApi appIoPnDocumentsApi;
    private final AppIoPnNotificationApi appIoPnNotificationApi;
    private final AppIoPnPaymentsApi appIoPnPaymentsApi;


    public PnAppIOB2bExternalClientImpl(RestTemplate restTemplate,
                                        @Value("${pn.appio.externa.base-url}") String devBasePath,
                                        @Value("${pn.external.appio.api-key}") String devApiKey) {
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

    public NotificationAttachmentDownloadMetadataResponse getReceivedNotificationAttachmentByUrl(String url, String xPagopaCxTaxid) throws RestClientException {
        Object postBody = null;
        // verify the required parameter 'xPagopaCxTaxid' is set
        if (xPagopaCxTaxid == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'xPagopaCxTaxid' when calling getReceivedNotificationAttachment");
        }

        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<>();
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();
        final String[] localVarAccepts = {
                "application/json", "application/problem+json"
        };

        headerParams.add("x-pagopa-cx-taxid", appIoPnPaymentsApi.getApiClient().parameterToString(xPagopaCxTaxid));

        final List<MediaType> localVarAccept = appIoPnPaymentsApi.getApiClient().selectHeaderAccept(localVarAccepts);
        final String[] contentTypes = {  };
        final MediaType localVarContentType = appIoPnPaymentsApi.getApiClient().selectHeaderContentType(contentTypes);

        String[] authNames = new String[] { "ApiKeyAuth" };

        ParameterizedTypeReference<NotificationAttachmentDownloadMetadataResponse> returnType = new ParameterizedTypeReference<>() {};
        return appIoPnPaymentsApi.getApiClient().invokeAPI(url, HttpMethod.GET, uriVariables, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, authNames, returnType).getBody();
     }
}