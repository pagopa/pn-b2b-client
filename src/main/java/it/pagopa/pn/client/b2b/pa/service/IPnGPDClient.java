package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.web.generated.openapi.clients.gpd.model.PaymentPositionModel;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.gpd.model.PaymentPositionModelBaseResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.gpd.model.PaymentPositionsInfo;
import org.springframework.web.client.RestClientException;
import java.time.LocalDate;


public interface IPnGPDClient {
    PaymentPositionModel createPosition(String organizationfiscalcode, PaymentPositionModel paymentPositionModel, String xRequestId, Boolean toPublish) throws RestClientException;
    String deletePosition(String organizationfiscalcode, String iupd, String xRequestId) throws RestClientException;
    PaymentPositionModelBaseResponse getOrganizationDebtPositionByIUPD(String organizationfiscalcode, String iupd, String xRequestId) throws RestClientException;
    PaymentPositionsInfo getOrganizationDebtPositions(String organizationfiscalcode, Integer page, String xRequestId, Integer limit, LocalDate dueDateFrom, LocalDate dueDateTo, LocalDate paymentDateFrom, LocalDate paymentDateTo, String status, String orderby, String ordering) throws RestClientException;
    PaymentPositionModel updatePosition(String organizationfiscalcode, String iupd, PaymentPositionModel paymentPositionModel, String xRequestId) throws RestClientException;
}