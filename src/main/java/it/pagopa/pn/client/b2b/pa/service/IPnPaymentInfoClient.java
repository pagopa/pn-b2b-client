package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.web.generated.openapi.clients.payment_info.model.*;
import org.springframework.web.client.RestClientException;

import java.util.List;

public interface IPnPaymentInfoClient {

    public PaymentInfo getPaymentInfo(String paTaxId, String noticeNumber) throws RestClientException;

    public List<PaymentInfoV21> getPaymentInfoV21(List<PaymentInfoRequest> requestBody) throws RestClientException ;

    public PaymentResponse checkoutCart(PaymentRequest paymentRequest) throws RestClientException;


    }
