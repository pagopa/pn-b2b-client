package it.pagopa.interop.purpose.service.impl;

import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.PurposesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeCloneSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeEServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.RejectPurposeVersionPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormConfig;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PurposeApiClientImpl implements IPurposeApiClient {
    private final PurposesApi purposesApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public PurposeApiClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getBaseUrl();
        this.purposesApi = new PurposesApi(createApiClient("dummyBearer"));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public RiskAnalysisFormConfig retrieveLatestRiskAnalysisConfiguration() {
        return purposesApi.retrieveLatestRiskAnalysisConfiguration(null);
    }

    @Override
    public CreatedResource createPurpose(PurposeSeed purposeSeed) {
        return purposesApi.createPurpose(purposeSeed);
    }

    @Override
    public CreatedResource createPurposeForReceiveEservice(PurposeEServiceSeed purposeEServiceSeed) {
        return purposesApi.createPurposeForReceiveEservice(purposeEServiceSeed);
    }

    @Override
    public Purpose getPurpose(UUID purposeId) {
        return purposesApi.getPurpose(purposeId);
    }

    @Override
    public PurposeVersionResource activatePurposeVersion(UUID purposeId, UUID versionId) {
        return purposesApi.activatePurposeVersion(purposeId, versionId);
    }

    @Override
    public PurposeVersionResource suspendPurposeVersion(UUID purposeId, UUID versionId) {
        return purposesApi.suspendPurposeVersion(purposeId, versionId);
    }

    @Override
    public PurposeVersionResource archivePurposeVersion(UUID purposeId, UUID versionId) {
        return purposesApi.archivePurposeVersion(purposeId, versionId);
    }

    @Override
    public void rejectPurposeVersion(UUID purposeId, UUID versionId, RejectPurposeVersionPayload rejectPurposeVersionPayload) {
        purposesApi.rejectPurposeVersion(purposeId, versionId, rejectPurposeVersionPayload);
    }

    @Override
    public PurposeVersionResource clonePurpose(UUID purposeId, PurposeCloneSeed purposeCloneSeed) {
        return purposesApi.clonePurpose(purposeId, purposeCloneSeed);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.purposesApi.setApiClient(createApiClient(bearerToken));
    }
}
