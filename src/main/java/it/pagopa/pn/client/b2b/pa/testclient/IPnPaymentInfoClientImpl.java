package it.pagopa.pn.client.b2b.pa.testclient;

import it.pagopa.pn.client.b2b.web.generated.openapi.clients.gpd.model.PaymentPositionModel;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.gpd.model.PaymentPositionModelBaseResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.gpd.model.PaymentPositionsInfo;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.payment_info.model.*;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.List;

public interface IPnPaymentInfoClientImpl {

    public PaymentInfo getPaymentInfo(String paTaxId, String noticeNumber) throws RestClientException;

    public List<PaymentInfoV21> getPaymentInfoV21(List<PaymentInfoRequest> requestBody) throws RestClientException ;

    public PaymentResponse checkoutCart(PaymentRequest paymentRequest) throws RestClientException;


    }
