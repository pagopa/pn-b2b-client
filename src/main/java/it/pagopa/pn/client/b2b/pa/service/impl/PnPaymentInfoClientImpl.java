package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.service.IPnPaymentInfoClient;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.payment_info.ApiClient;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.payment_info.api.PaymentInfoApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.payment_info.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.List;


@Component
public class PnPaymentInfoClientImpl implements IPnPaymentInfoClient {
    private final PaymentInfoApi paymentInfoApi;


    public PnPaymentInfoClientImpl(RestTemplate restTemplate,
                                   @Value("${pn.webapi.external.base-url}") String deliveryBasePath ,
                                   @Value("${pn.bearer-token-payinfo}") String key) {
        this.paymentInfoApi = new PaymentInfoApi(newApiClient( restTemplate, deliveryBasePath,key));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String key) {
        ApiClient newApiClient = new ApiClient( restTemplate );
        newApiClient.setBasePath( basePath );
        newApiClient.setBearerToken(key);
        return newApiClient;
    }

    public PaymentInfo getPaymentInfo(String paTaxId, String noticeNumber) throws RestClientException {
        return paymentInfoApi.getPaymentInfoWithHttpInfo(paTaxId, noticeNumber).getBody();
    }

    @Override
    public List<PaymentInfoV21> getPaymentInfoV21(List<PaymentInfoRequest> paymentInfoRequest) throws RestClientException {
        return paymentInfoApi.getPaymentInfoV21WithHttpInfo(paymentInfoRequest).getBody();
    }

    public PaymentResponse checkoutCart(PaymentRequest paymentRequest) throws RestClientException {
        return paymentInfoApi.checkoutCartWithHttpInfo(paymentRequest).getBody();
    }
}