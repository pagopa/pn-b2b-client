package it.pagopa.interop.purpose.service.impl;

import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.api.PurposesApi;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeVersion;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeVersionSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeVersions;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purposes;
import it.pagopa.interop.purpose.service.IM2MPurposeClient;
import java.util.UUID;
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
    public PurposeVersion createPurposeVersion(
        UUID purposeId, PurposeVersionSeed purposeVersionSeed) {
        return purposesApi.createPurposeVersion(purposeId.toString(), purposeVersionSeed);
    }

    @Override
    public PurposeVersion getVersion(UUID purposeId, UUID purposeVersionId) {
        return purposesApi.getPurposeVersion(purposeId, purposeVersionId);
    }

    @Override
    public PurposeVersions getVersions(PurposeVersionsListRequest request) {
        return purposesApi.getPurposeVersions(
            request.getPurposeId().toString(),
            request.getOffset(),
            request.getLimit(),
            null);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.purposesApi.setApiClient(createApiClient(bearerToken));
    }
}
