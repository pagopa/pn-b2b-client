package it.pagopa.interop.agreement.service.impl;

import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementState;
import it.pagopa.interop.generated.openapi.clients.bff.model.CatalogEServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.CatalogEServices;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptionUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDoc;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysis;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysisSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.FileResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.PresignedUrl;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorDocumentSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorQuotas;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceSeed;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Retryable(
        retryFor = { HttpServerErrorException.class },
        backoff = @Backoff(delay = 2000)
)
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
    public CreatedEServiceDescriptor createEService(EServiceSeed eserviceSeed) {
        return eservicesApi.createEService(eserviceSeed);
    }

    @Override
    public void deleteEService(UUID eServiceId) {
        eservicesApi.deleteEService(eServiceId);
    }

    @Override
    public void deleteEServiceRiskAnalysis(UUID eServiceId, UUID riskAnalysisId) {
        eservicesApi.deleteEServiceRiskAnalysis(eServiceId, riskAnalysisId);
    }

    @Override
    public ResponseEntity<Void> activateDescriptor(UUID eServiceId, UUID descriptorId) {
        return eservicesApi.activateDescriptorWithHttpInfo(eServiceId, descriptorId);
    }

    @Override
    public CreatedResource updateDraftDescriptor(UUID eServiceId, UUID descriptorId, UpdateEServiceDescriptorSeed updateEServiceDescriptorSeed) {
        return eservicesApi.updateDraftDescriptor(eServiceId, descriptorId, updateEServiceDescriptorSeed);
    }

    @Override
    public CreatedResource updateDescriptor(UUID eServiceId, UUID descriptorId, UpdateEServiceDescriptorQuotas updateEServiceDescriptorQuotas) {
        return eservicesApi.updateDescriptor(eServiceId, descriptorId, updateEServiceDescriptorQuotas);
    }

    @Override
    public CreatedResource createEServiceDocument(UUID eServiceId, UUID descriptorId, String kind, String prettyName, org.springframework.core.io.Resource doc) {
        return eservicesApi.createEServiceDocument(eServiceId, descriptorId, kind, prettyName, doc);
    }

    @Override
    public void publishDescriptor(UUID eServiceId, UUID descriptorId) {
        eservicesApi.publishDescriptor(eServiceId, descriptorId);
    }

    @Override
    public void suspendDescriptor(UUID eServiceId, UUID descriptorId) {
        eservicesApi.suspendDescriptor(eServiceId, descriptorId);
    }

    @Override
    public CreatedResource createDescriptor(UUID eServiceId) {
        return eservicesApi.createDescriptor(eServiceId);
    }

    @Override
    public void deleteDraft(UUID eServiceId, UUID descriptorId) {
        eservicesApi.deleteDraft(eServiceId, descriptorId);
    }

    @Override
    public void deleteEServiceDocumentById(UUID eServiceId, UUID descriptorId, UUID documentId) {
        eservicesApi.deleteEServiceDocumentById(eServiceId, descriptorId, documentId);
    }

    @Override
    public void addRiskAnalysisToEService(UUID eServiceId, EServiceRiskAnalysisSeed eserviceRiskAnalysisSeed) {
        eservicesApi.addRiskAnalysisToEService(eServiceId, eserviceRiskAnalysisSeed);
    }

    public FileResource exportEServiceDescriptor(UUID eserviceId, UUID descriptorId) {
        return eservicesApi.exportEServiceDescriptor(eserviceId, descriptorId);
    }

    public CatalogEServices getEServicesCatalog(Integer offset, Integer limit, String q, List<UUID> producersIds,
                                                List<UUID> attributesIds, List<EServiceDescriptorState> states,
                                                List<AgreementState> agreementStates, EServiceMode mode, Boolean isConsumerDelegable) {
        return eservicesApi.getEServicesCatalog(offset, limit, q, producersIds, attributesIds, states, agreementStates, mode, isConsumerDelegable);
    }

    public CatalogEServiceDescriptor getCatalogEServiceDescriptor(UUID eserviceId, UUID descriptorId) {
        return eservicesApi.getCatalogEServiceDescriptor(eserviceId, descriptorId);
    }

    public File getEServiceDocumentById(UUID eServiceId, UUID descriptorId, UUID documentId) {
        return eservicesApi.getEServiceDocumentById(eServiceId.toString(), descriptorId.toString(), documentId.toString());
    }

    public EServiceDoc updateEServiceDocumentById(UUID eServiceId, UUID descriptorId, UUID documentId, UpdateEServiceDescriptorDocumentSeed updateEServiceDescriptorDocumentSeed) {
        return eservicesApi.updateEServiceDocumentById(eServiceId, descriptorId, documentId, updateEServiceDescriptorDocumentSeed);
    }

    public CreatedResource updateEServiceById(UUID eServiceId, UpdateEServiceSeed updateEServiceSeed) {
        return eservicesApi.updateEServiceById(eServiceId, updateEServiceSeed);
    }

    public CreatedResource updateEServiceDescription(UUID eServiceId, EServiceDescriptionUpdateSeed eserviceDescriptionUpdateSeed) {
        return eservicesApi.updateEServiceDescription(eServiceId, eserviceDescriptionUpdateSeed);
    }

    public CreatedEServiceDescriptor cloneEServiceByDescriptor(UUID eServiceId, UUID descriptorId) {
        return eservicesApi.cloneEServiceByDescriptor(eServiceId, descriptorId);
    }

    public File getEServiceConsumers(UUID eServiceId) {
        return eservicesApi.getEServiceConsumers(eServiceId);
    }

    public EServiceRiskAnalysis getEServiceRiskAnalysis(UUID eServiceId, UUID riskAnalysisId) {
        return eservicesApi.getEServiceRiskAnalysis(eServiceId, riskAnalysisId);
    }

    @Override
    public void updateEServiceRiskAnalysis(UUID eServiceId, UUID riskAnalysisId, EServiceRiskAnalysisSeed eserviceRiskAnalysisSeed) {
        eservicesApi.updateEServiceRiskAnalysis(eServiceId, riskAnalysisId, eserviceRiskAnalysisSeed);
    }

    @Override
    public PresignedUrl getImportEservicePresignedUrl(String fileName) {
        return eservicesApi.getImportEservicePresignedUrl(fileName);
    }

    @Override
    public CreatedEServiceDescriptor importEService(FileResource fileResource) {
        return eservicesApi.importEService(fileResource);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.eservicesApi.setApiClient(createApiClient(bearerToken));
    }

}
