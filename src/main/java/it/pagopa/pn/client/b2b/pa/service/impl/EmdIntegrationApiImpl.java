package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.emd.ApiClient;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.emd.api.CheckTppApi;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.emd.api.MessageApi;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.emd.api.PaymentApi;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.emd.model.PaymentUrlResponse;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.emd.model.RetrievalPayload;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.emd.model.SendMessageRequestBody;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.emd.model.SendMessageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class EmdIntegrationApiImpl {
    private final MessageApi messageApi;
    private final CheckTppApi checkTppApi;
    private final PaymentApi paymentApi;

    public EmdIntegrationApiImpl(RestTemplate restTemplate,
                                 @Value("${pn.delivery.base-url}") String basePath) {
        this.messageApi = new MessageApi(createApiClient(restTemplate, basePath));
        this.checkTppApi = new CheckTppApi(createApiClient(restTemplate, basePath));
        this.paymentApi = new PaymentApi(createApiClient(restTemplate, basePath));
    }

    private ApiClient createApiClient(RestTemplate restTemplate, String basePath) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        return newApiClient;
    }

    public ResponseEntity<SendMessageResponse> sendMessage(SendMessageRequestBody sendMessageRequestBody) {
        return messageApi.sendMessageWithHttpInfo(sendMessageRequestBody);
    }

    public ResponseEntity<RetrievalPayload> emdCheckTPP(String retrievalId) {
        return checkTppApi.emdCheckTPPWithHttpInfo(retrievalId);
    }

    public ResponseEntity<RetrievalPayload> tokenCheckTPP(String retrievalId) {
        return checkTppApi.tokenCheckTPPWithHttpInfo(retrievalId);
    }

    public ResponseEntity<PaymentUrlResponse> getPaymentUrl(String retrievalId, String noticeCode, String paTaxId) {
        return paymentApi.getPaymentUrlWithHttpInfo(retrievalId, noticeCode, paTaxId);
    }
}
