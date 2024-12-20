package it.pagopa.interop.purpose.service.impl;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.conf.springconfig.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.PurposesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

public class PurposeApiClientImpl implements IPurposeApiClient {
    private final PurposesApi purposesApi;
    private final RestTemplate restTemplate;
    private final String basePath;
    private final String bearerToken;
    private final InteropClientConfigs interopClientConfigs;

    public PurposeApiClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.interopClientConfigs = interopClientConfigs;
        this.basePath = interopClientConfigs.getBaseUrl();
        this.bearerToken = "bearerToken";
        this.purposesApi = new PurposesApi(createApiClient(bearerToken));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public RiskAnalysisFormConfig retrieveLatestRiskAnalysisConfiguration(String xCorrelationId) {
        return purposesApi.retrieveLatestRiskAnalysisConfiguration(xCorrelationId, null);
    }

    @Override
    public CreatedResource createPurpose(String xCorrelationId, PurposeSeed purposeSeed) {
        return purposesApi.createPurpose(xCorrelationId, purposeSeed);
    }

    @Override
    public CreatedResource createPurposeForReceiveEservice(String xCorrelationId, PurposeEServiceSeed purposeEServiceSeed) {
        return purposesApi.createPurposeForReceiveEservice(xCorrelationId, purposeEServiceSeed);
    }

    @Override
    public Purpose getPurpose(String xCorrelationId, UUID purposeId) {
        return purposesApi.getPurpose(xCorrelationId, purposeId);
    }

    @Override
    public PurposeVersionResource activatePurposeVersion(String xCorrelationId, UUID purposeId, UUID versionId) {
        return purposesApi.activatePurposeVersion(xCorrelationId, purposeId, versionId);
    }

    @Override
    public PurposeVersionResource suspendPurposeVersion(String xCorrelationId, UUID purposeId, UUID versionId) {
        return purposesApi.suspendPurposeVersion(xCorrelationId, purposeId, versionId);
    }

    @Override
    public PurposeVersionResource archivePurposeVersion(String xCorrelationId, UUID purposeId, UUID versionId) {
        return purposesApi.archivePurposeVersion(xCorrelationId, purposeId, versionId);
    }

    @Override
    public void rejectPurposeVersion(String xCorrelationId, UUID purposeId, UUID versionId, RejectPurposeVersionPayload rejectPurposeVersionPayload) {
        purposesApi.rejectPurposeVersion(xCorrelationId, purposeId, versionId, rejectPurposeVersionPayload);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.purposesApi.setApiClient(createApiClient(bearerToken));
    }
}