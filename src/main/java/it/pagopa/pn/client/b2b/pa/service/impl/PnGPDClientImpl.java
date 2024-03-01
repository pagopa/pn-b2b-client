package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.service.IPnGPDClient;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.gpd.ApiClient;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.gpd.api.DebtPositionsApiApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.gpd.model.PaymentPositionModel;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.gpd.model.PaymentPositionModelBaseResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.gpd.model.PaymentPositionsInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;


@Component
public class PnGPDClientImpl implements IPnGPDClient {

    private final ApplicationContext ctx;
    private final RestTemplate restTemplate;


    private final String basePath;

    private final String key;

    private final DebtPositionsApiApi debtPositionsApiApi;

/*
    private final NotificationRequest req;
    private final CreateOperationRequest creaOp;

    private final VideoUploadRequest videoreq;

    private final SearchNotificationRequest searchreq;

    private final AnalogAddress address;

 */

    // private final String paId;
    private final String operatorId;

    public PnGPDClientImpl(
            ApplicationContext ctx,
            RestTemplate restTemplate,
            @Value("${pn.internal.gpd-base-url}") String deliveryBasePath,
            @Value("${pn.external.api-subscription-key}") String key
    ) {

        // this.paId = paId;
        this.operatorId = "AutomationMv";
        this.ctx = ctx;
        this.restTemplate = restTemplate;
        this.basePath = deliveryBasePath;
        this.key = key;
        this.debtPositionsApiApi = new DebtPositionsApiApi(newApiClient(restTemplate, basePath, key));


    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String key) {
        //private static ApiClient newApiClient(RestTemplate restTemplate, String basePath) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("Ocp-Apim-Subscription-Key", key);
        newApiClient.addDefaultHeader("Content-Type", "application/json");
        return newApiClient;
    }


    @Override
    public PaymentPositionModel createPosition(String organizationfiscalcode, PaymentPositionModel paymentPositionModel, String xRequestId, Boolean toPublish) throws RestClientException {
        return debtPositionsApiApi.createPositionWithHttpInfo(organizationfiscalcode, paymentPositionModel, xRequestId, toPublish).getBody();
    }

    @Override
    public String deletePosition(String organizationfiscalcode, String iupd, String xRequestId) throws RestClientException {
        return debtPositionsApiApi.deletePositionWithHttpInfo(organizationfiscalcode, iupd, xRequestId).getBody();
    }

    @Override
    public PaymentPositionModelBaseResponse getOrganizationDebtPositionByIUPD(String organizationfiscalcode, String iupd, String xRequestId) throws RestClientException {
        return debtPositionsApiApi.getOrganizationDebtPositionByIUPDWithHttpInfo(organizationfiscalcode, iupd, xRequestId).getBody();
    }

    @Override
    public PaymentPositionsInfo getOrganizationDebtPositions(String organizationfiscalcode, Integer page, String xRequestId, Integer limit, LocalDate dueDateFrom, LocalDate dueDateTo, LocalDate paymentDateFrom, LocalDate paymentDateTo, String status, String orderby, String ordering) throws RestClientException {
        return debtPositionsApiApi.getOrganizationDebtPositionsWithHttpInfo(organizationfiscalcode, page, xRequestId, limit, dueDateFrom, dueDateTo, paymentDateFrom, paymentDateTo, status, orderby, ordering).getBody();
    }

    @Override
    public PaymentPositionModel updatePosition(String organizationfiscalcode, String iupd, PaymentPositionModel paymentPositionModel, String xRequestId) throws RestClientException {
        return debtPositionsApiApi.updatePositionWithHttpInfo(organizationfiscalcode, iupd, paymentPositionModel, xRequestId,false).getBody();
    }

}
