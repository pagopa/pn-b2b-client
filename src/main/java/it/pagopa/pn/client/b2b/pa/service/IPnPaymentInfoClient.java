package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.payment.BffPaymentInfoItem;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.payment.BffPaymentRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.payment.BffPaymentResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.payment.PaymentInfoRequest;

import org.springframework.web.client.RestClientException;
import java.util.List;


public interface IPnPaymentInfoClient {
    List<BffPaymentInfoItem> getPaymentInfoV21(List<PaymentInfoRequest> requestBody) throws RestClientException ;
    BffPaymentResponse checkoutCart(BffPaymentRequest paymentRequest) throws RestClientException;
}