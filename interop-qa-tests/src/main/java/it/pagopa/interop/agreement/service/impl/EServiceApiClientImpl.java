package it.pagopa.interop.agreement.service.impl;

import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.conf.springconfig.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

public class EServiceApiClientImpl implements IEServiceClient {
    private final EservicesApi eservicesApi;
    private final RestTemplate restTemplate;
    private final InteropClientConfigs interopClientConfigs;
    private final String basePath;
    private final String bearerToken;

    public EServiceApiClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.interopClientConfigs = interopClientConfigs;
        this.basePath = interopClientConfigs.getBaseUrl();
        this.bearerToken = "bearerToken";
        this.eservicesApi = new EservicesApi(createApiClient(bearerToken));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public CreatedEServiceDescriptor createEService(String xCorrelationId, EServiceSeed eserviceSeed) {
        return eservicesApi.createEService(xCorrelationId, eserviceSeed);
    }

    @Override
    public CreatedResource updateDraftDescriptor(String xCorrelationId, UUID eServiceId, UUID descriptorId, UpdateEServiceDescriptorSeed updateEServiceDescriptorSeed) {
        return eservicesApi.updateDraftDescriptor(xCorrelationId, eServiceId, descriptorId, updateEServiceDescriptorSeed);
    }

    @Override
    public CreatedResource createEServiceDocument(String xCorrelationId, UUID eServiceId, UUID descriptorId, String kind, String prettyName, org.springframework.core.io.Resource doc) {
        return eservicesApi.createEServiceDocument(xCorrelationId, eServiceId, descriptorId, kind, prettyName, doc);
    }

    @Override
    public void publishDescriptor(String xCorrelationId, UUID eServiceId, UUID descriptorId) {
        eservicesApi.publishDescriptor(xCorrelationId, eServiceId, descriptorId);
    }

    @Override
    public void suspendDescriptor(String xCorrelationId, UUID eServiceId, UUID descriptorId) {
        eservicesApi.suspendDescriptor(xCorrelationId, eServiceId, descriptorId);
    }

    @Override
    public CreatedResource createDescriptor(String xCorrelationId, UUID eServiceId) {
        return eservicesApi.createDescriptor(xCorrelationId, eServiceId);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.eservicesApi.setApiClient(createApiClient(bearerToken));
    }

}
