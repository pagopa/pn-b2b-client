package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.selfcaredelegationstest.ApiClient;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.selfcaredelegationstest.api.DelegationsTestApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.selfcaredelegationstest.model.AllowedProductId;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.selfcaredelegationstest.model.DelegationRequest;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.selfcaredelegationstest.model.DelegationResponse;
import it.pagopa.pn.client.b2b.pa.service.IPnSelfcareDelegationsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Client dell'openapi delegations-test di Selfcare: creazione, revoca e lettura delle deleghe PA→PT
 * usate come precondizione dei test sugli Enti deleganti.
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnSelfcareDelegationsClientImpl implements IPnSelfcareDelegationsClient {

    private final DelegationsTestApi delegationsTestApi;
    private final AllowedProductId productId;

    // TODO: placeholder, valorizzare pn.selfcare.delegations.subscription-key nelle properties
    public PnSelfcareDelegationsClientImpl(RestTemplate restTemplate,
                                           @Value("${pn.selfcare.delegations.base-url:https://api.dev.selfcare.pagopa.it/external/test/v1}") String basePath,
                                           @Value("${pn.selfcare.delegations.subscription-key:PLACEHOLDER_SELFCARE_SUBSCRIPTION_KEY}") String subscriptionKey,
                                           @Value("${pn.selfcare.delegations.product-id:prod-pn-dev}") String productId) {
        this.productId = AllowedProductId.fromValue(productId);
        this.delegationsTestApi = new DelegationsTestApi(newApiClient(restTemplate, basePath, subscriptionKey));
    }

    // la subscription key viene inviata nell'header Ocp-Apim-Subscription-Key
    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String subscriptionKey) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setApiKey(subscriptionKey);
        return apiClient;
    }

    @Override
    public DelegationResponse createDelegation(DelegationRequest delegationRequest) throws RestClientException {
        return delegationsTestApi.createDelegationUsingPOST(delegationRequest);
    }

    @Override
    public void deleteDelegation(String delegationId) throws RestClientException {
        delegationsTestApi.deleteDelegationUsingDELETE(delegationId);
    }

    @Override
    public List<DelegationResponse> getDelegations(String institutionId, String brokerId) throws RestClientException {
        return delegationsTestApi.getDelegationsUsingGET(productId, institutionId, brokerId, null, null, null, null, null);
    }

    @Override
    public AllowedProductId getProductId() {
        return productId;
    }
}
