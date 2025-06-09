package it.pagopa.interop.purpose.service.impl;

import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.api.PurposesApi;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purposes;
import it.pagopa.interop.purpose.service.IM2MPurposeClient;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class M2MPurposeClientImpl implements IM2MPurposeClient {
    private final PurposesApi purposesApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public M2MPurposeClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getM2mBaseUrl();
        this.purposesApi = new PurposesApi(createApiClient("dummyBearer"));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public Purposes getPurposes(PurposesListRequest request) {
        return purposesApi.getPurposes(
            request.getOffset(),
            request.getLimit(),
            request.getEservicesIds());
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.purposesApi.setApiClient(createApiClient(bearerToken));
    }
}
