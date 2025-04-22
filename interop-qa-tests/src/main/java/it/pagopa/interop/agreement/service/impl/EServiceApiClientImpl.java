package it.pagopa.interop.agreement.service.impl;

import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysisSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.FileResource;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import java.util.UUID;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EServiceApiClientImpl implements IEServiceClient {
    private final EservicesApi eservicesApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public EServiceApiClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getBaseUrl();
        this.eservicesApi = new EservicesApi(createApiClient("dummyBearer"));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public CreatedEServiceDescriptor createEService(String xCorrelationId, EServiceSeed eserviceSeed) {
        return eservicesApi.createEService(eserviceSeed);
    }

    @Override
    public ResponseEntity<Void> activateDescriptor(String xCorrelationId, UUID eServiceId, UUID descriptorId) {
        return eservicesApi.activateDescriptorWithHttpInfo(eServiceId, descriptorId);
    }

    @Override
    public CreatedResource updateDraftDescriptor(String xCorrelationId, UUID eServiceId, UUID descriptorId, UpdateEServiceDescriptorSeed updateEServiceDescriptorSeed) {
        return eservicesApi.updateDraftDescriptor(eServiceId, descriptorId, updateEServiceDescriptorSeed);
    }

    @Override
    public CreatedResource createEServiceDocument(String xCorrelationId, UUID eServiceId, UUID descriptorId, String kind, String prettyName, org.springframework.core.io.Resource doc) {
        return eservicesApi.createEServiceDocument(eServiceId, descriptorId, kind, prettyName, doc);
    }

    @Override
    public void publishDescriptor(String xCorrelationId, UUID eServiceId, UUID descriptorId) {
        eservicesApi.publishDescriptor(eServiceId, descriptorId);
    }

    @Override
    public void suspendDescriptor(String xCorrelationId, UUID eServiceId, UUID descriptorId) {
        eservicesApi.suspendDescriptor(eServiceId, descriptorId);
    }

    @Override
    public CreatedResource createDescriptor(String xCorrelationId, UUID eServiceId) {
        return eservicesApi.createDescriptor(eServiceId);
    }

    @Override
    public void deleteDraft(UUID eServiceId, UUID descriptorId) {
        eservicesApi.deleteDraft(eServiceId, descriptorId);
    }

    @Override
    public void addRiskAnalysisToEService(UUID eServiceId, EServiceRiskAnalysisSeed eserviceRiskAnalysisSeed) {
        eservicesApi.addRiskAnalysisToEService(eServiceId, eserviceRiskAnalysisSeed);
    }


    public FileResource exportEServiceDescriptor(UUID eserviceId, UUID descriptorId) {
        return eservicesApi.exportEServiceDescriptor(eserviceId, descriptorId);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.eservicesApi.setApiClient(createApiClient(bearerToken));
    }

}
