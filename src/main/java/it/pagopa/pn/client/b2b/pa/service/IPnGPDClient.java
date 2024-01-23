package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.web.generated.openapi.clients.gpd.model.PaymentPositionModel;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.gpd.model.PaymentPositionModelBaseResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.gpd.model.PaymentPositionsInfo;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;

public interface IPnGPDClient {

    public PaymentPositionModel createPosition(String organizationfiscalcode, PaymentPositionModel paymentPositionModel, String xRequestId, Boolean toPublish) throws RestClientException;

    public String deletePosition(String organizationfiscalcode, String iupd, String xRequestId) throws RestClientException;

    public PaymentPositionModelBaseResponse getOrganizationDebtPositionByIUPD(String organizationfiscalcode, String iupd, String xRequestId) throws RestClientException;

    public PaymentPositionsInfo getOrganizationDebtPositions(String organizationfiscalcode, Integer page, String xRequestId, Integer limit, LocalDate dueDateFrom, LocalDate dueDateTo, LocalDate paymentDateFrom, LocalDate paymentDateTo, String status, String orderby, String ordering) throws RestClientException;

    public PaymentPositionModel updatePosition(String organizationfiscalcode, String iupd, PaymentPositionModel paymentPositionModel, String xRequestId) throws RestClientException;

    }
