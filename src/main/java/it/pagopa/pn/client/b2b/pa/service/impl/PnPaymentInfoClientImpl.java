package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.payment.PaymentsApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.payment.BffPaymentInfoItem;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.payment.BffPaymentRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.payment.BffPaymentResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.payment.PaymentInfoRequest;
import it.pagopa.pn.client.b2b.pa.service.IPnPaymentInfoClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.ApiClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.List;


@Component
public class PnPaymentInfoClientImpl implements IPnPaymentInfoClient {
    private final PaymentsApi paymentInfoApi;


    public PnPaymentInfoClientImpl(RestTemplate restTemplate,
                                   @Value("${pn.webapi.external.base-url}") String deliveryBasePath ,
                                   @Value("${pn.bearer-token-payinfo}") String key) {
        this.paymentInfoApi = new PaymentsApi(newApiClient( restTemplate, deliveryBasePath,key));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String key) {
        ApiClient newApiClient = new ApiClient( restTemplate );
        newApiClient.setBasePath( basePath );
        newApiClient.addDefaultHeader("Authorization","Bearer " + key);

        return newApiClient;
    }



    @Override
    public List<BffPaymentInfoItem> getPaymentInfoV21(List<PaymentInfoRequest> paymentInfoRequest) throws RestClientException {
        return paymentInfoApi.getPaymentsInfoV1WithHttpInfo(paymentInfoRequest).getBody();
    }

    public BffPaymentResponse checkoutCart(BffPaymentRequest paymentRequest) throws RestClientException {
        return paymentInfoApi.paymentsCartV1WithHttpInfo(paymentRequest).getBody();
    }
}