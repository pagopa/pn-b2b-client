package it.pagopa.interop.agreement.service.impl;

import static it.pagopa.interop.utils.BlobFileCreationUtils.createTempFile;
import static java.util.Objects.isNull;

import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Retryable(
        retryFor = {HttpServerErrorException.class},
        backoff = @Backoff(delay = 2000)
)
public class EServiceApiClientImpl implements IEServiceClient {
    private final EservicesApi eservicesApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public EServiceApiClientImpl(
            RestTemplate restTemplate,
            InteropClientConfigs interopClientConfigs
    ) {
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
    public void updateDescriptorAttributes(UUID eServiceId, UUID descriptorId, DescriptorAttributesSeed descriptorAttributesSeed) {
        eservicesApi.updateDescriptorAttributes(eServiceId, descriptorId, descriptorAttributesSeed);
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
    public ResponseEntity<Void> suspendDescriptor(UUID eServiceId, UUID descriptorId) {
        return eservicesApi.suspendDescriptorWithHttpInfo(eServiceId, descriptorId);
    }

    @Override
    public ResponseEntity<Void> scheduleArchiveDescriptor(UUID eServiceId, UUID descriptorId) {
        return eservicesApi.scheduleArchiveEserviceDescriptorWithHttpInfo(eServiceId, descriptorId);
    }

    @Override
    public ResponseEntity<Void> scheduleArchiveEService(UUID eServiceId, EServiceArchivingReasonSeed eserviceArchivingReasonSeed) {
        return eservicesApi.scheduleArchiveEserviceWithHttpInfo(eServiceId, eserviceArchivingReasonSeed);
    }

    @Override
    public ResponseEntity<Void> cancelDescriptorArchiving(UUID eServiceId, UUID descriptorId) {
        return eservicesApi.cancelEServiceDescriptorArchivingWithHttpInfo(eServiceId, descriptorId);
    }

    @Override
    public ResponseEntity<Void> cancelEServiceArchiving(UUID eServiceId) {
        return eservicesApi.cancelScheduleArchiveEserviceWithHttpInfo(eServiceId);
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

        /* DEV. NOTE 22/10/2025: il campo "personalData" è stato aggiunto a posteriori della
         * stesura di questo metodo. Essendo opzionale, lo si pone a null per mantenere compatibilità con i test esistenti. */
        return eservicesApi.getEServicesCatalog(offset, limit, null, q, producersIds, attributesIds, states, agreementStates, mode, isConsumerDelegable);
    }

    public CatalogEServiceDescriptor getCatalogEServiceDescriptor(UUID eserviceId, UUID descriptorId) {
        return eservicesApi.getCatalogEServiceDescriptor(eserviceId, descriptorId);
    }

    public File getEServiceDocumentById(UUID eServiceId, UUID descriptorId, UUID documentId) {
        try {
            Resource resourceResponse = eservicesApi.getEServiceDocumentById(eServiceId,
                    descriptorId, documentId);
            return createTempFile("e-service-document-", resourceResponse.getInputStream());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public EServiceDoc updateEServiceDocumentById(UUID eServiceId, UUID descriptorId, UUID documentId, UpdateEServiceDescriptorDocumentSeed updateEServiceDescriptorDocumentSeed) {
        return eservicesApi.updateEServiceDocumentById(eServiceId, descriptorId, documentId, updateEServiceDescriptorDocumentSeed);
    }

    public CreatedResource updateEServiceById(UUID eServiceId, UpdateEServiceSeed updateEServiceSeed) {
        return eservicesApi.updateEServiceById(eServiceId, updateEServiceSeed);
    }

    public CreatedResource updateEServiceDelegationFlags(UUID eServiceId, EServiceDelegationFlagsUpdateSeed eserviceDelegationFlagsUpdateSeed) {
         return eservicesApi.updateEServiceDelegationFlags(eServiceId, eserviceDelegationFlagsUpdateSeed);
    }

    public CreatedResource updateEServiceDescription(UUID eServiceId, EServiceDescriptionUpdateSeed eserviceDescriptionUpdateSeed) {
        return eservicesApi.updateEServiceDescription(eServiceId, eserviceDescriptionUpdateSeed);
    }

    public CreatedEServiceDescriptor cloneEServiceByDescriptor(UUID eServiceId, UUID descriptorId) {
        return eservicesApi.cloneEServiceByDescriptor(eServiceId, descriptorId);
    }

    public void updateEServiceName(UUID eServiceId, EServiceNameUpdateSeed eserviceNameUpdateSeed) {
        eservicesApi.updateEServiceName(eServiceId, eserviceNameUpdateSeed);
    }

    public File getEServiceConsumers(UUID eServiceId) {
        try {
            Resource resourceResponse = eservicesApi.getEServiceConsumers(eServiceId);
            return createTempFile("e-service-document-", resourceResponse.getInputStream());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
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
    public void updateEServicePersonalDataFlagAfterPublication(UUID eServiceId, EServicePersonalDataFlagUpdateSeed seed) {
        eservicesApi.updateEServicePersonalDataFlagAfterPublication(eServiceId, seed);
    }

    @Override
    public ResponseEntity<CreatedEServiceDescriptor> createEServiceInstanceFromTemplateWithHttpInfo(
            UUID templateId, InstanceEServiceSeed instanceEServiceSeed) {
        /* DEV. NOTE 10/03/2025: al momento InstanceEServiceSeed è required dalla API, tuttavia
         * nessuno dei suoi campi lo è; per comodità si permette a questo metodo di passare NULL
         * mappandolo con un'istanza vuota. */
        return this.eservicesApi.createEServiceInstanceFromTemplateWithHttpInfo(
                templateId,
                isNull(instanceEServiceSeed) ? new InstanceEServiceSeed() : instanceEServiceSeed);
    }

    @Override
    public ResponseEntity<EServiceTemplateInstances> getEServiceTemplateInstancesWithHttpInfo(
            UUID templateId) {
        /* Di default l'api NON restituisce le istanze in stato DRAFT, invece si chiedono in
         * questo modo tutte quante */
        List<EServiceDescriptorState> states = Arrays.stream(EServiceDescriptorState.values()).toList();
        return this.eservicesApi.getEServiceTemplateInstancesWithHttpInfo(templateId, 0, 50, null, states);
    }

    //
    @Override
    public ResponseEntity<EServiceTemplateInstances> getMyEServiceTemplateInstancesWithHttpInfo(UUID templateId, Integer offset, Integer limit) {
        return this.eservicesApi.getMyEServiceTemplateInstancesWithHttpInfo(templateId, offset, limit);
    }

    @Override
    public ResponseEntity<EServiceTemplateInstances> getEServiceTemplateInstancesWithHttpInfo(
            UUID templateId, Integer offset, Integer limit, String producerName,
            List<EServiceDescriptorState> states) {
        return this.eservicesApi.getEServiceTemplateInstancesWithHttpInfo(templateId, offset, limit, producerName, states);
    }

    @Override
    public ResponseEntity<CreatedResource> upgradeEServiceInstanceWithHttpInfo(UUID eServiceId) {
        return this.eservicesApi.upgradeEServiceInstanceWithHttpInfo(eServiceId);
    }

    @Override
    public ResponseEntity<ProducerEServiceDescriptor> getProducerEServiceDescriptorWithHttpInfo(
            UUID eserviceId, UUID descriptorId) {
        return this.eservicesApi.getProducerEServiceDescriptorWithHttpInfo(eserviceId, descriptorId);
    }

    @Override
    public ResponseEntity<ProducerEServices> getProducerEServicesWithHttpInfo(
            String eServiceName) {
        /* DEV. NOTE 22/10/2025: il campo "personalData" è stato aggiunto a posteriori della
         * stesura di questo metodo. Essendo opzionale, lo si pone a null per mantenere compatibilità con i test esistenti. */
        return this.eservicesApi.getProducerEServicesWithHttpInfo(0, 50, null, eServiceName, null, null);
    }

    @Override
    public ResponseEntity<CreatedResource> updateEServiceTemplateInstanceByIdWithHttpInfo(
            UUID eServiceId,
            UpdateEServiceTemplateInstanceSeed updateEServiceTemplateInstanceSeed
    ) {
        return this.eservicesApi.updateEServiceTemplateInstanceByIdWithHttpInfo(eServiceId, updateEServiceTemplateInstanceSeed);
    }

    @Override
    public ResponseEntity<CreatedResource> updateEServiceInstanceLabelAfterPublicationWithHttpInfo(
            UUID eServiceId,
            EServiceInstanceLabelUpdateSeed eServiceInstanceLabelUpdateSeed
    ) {
        return this.eservicesApi.updateEServiceInstanceLabelAfterPublicationWithHttpInfo(eServiceId, eServiceInstanceLabelUpdateSeed);
    }

    @Override
    public ResponseEntity<CreatedResource> updateDraftDescriptorTemplateInstanceWithHttpInfo(
            UUID eServiceId,
            UUID descriptorId,
            UpdateEServiceDescriptorTemplateInstanceSeed updateEServiceDescriptorTemplateInstanceSeed
    ) {
        return this.eservicesApi.updateDraftDescriptorTemplateInstanceWithHttpInfo(eServiceId, descriptorId, updateEServiceDescriptorTemplateInstanceSeed);
    }


    @Override
    public ResponseEntity<CreatedResource> updateTemplateInstanceDescriptorWithHttpInfo(
            UUID eServiceId,
            UUID descriptorId,
            UpdateEServiceTemplateInstanceDescriptorQuotas descriptorQuotas
    ) {
        return this.eservicesApi.updateTemplateInstanceDescriptorWithHttpInfo(eServiceId, descriptorId, descriptorQuotas);
    }

    @Override
    public ResponseEntity<ProducerEServiceDetails> getProducerEServiceDetailsWithHttpInfo(
            UUID eserviceId) {
        return this.eservicesApi.getProducerEServiceDetailsWithHttpInfo(eserviceId);
    }

    @Override
    public ResponseEntity<CreatedResource> addEServiceTemplateInstanceInterfaceRestWithHttpInfo(
            UUID eServiceId, UUID descriptorId,
            TemplateInstanceInterfaceRESTSeed templateInstanceInterfaceRESTSeed) {
        return this.eservicesApi.addEServiceTemplateInstanceInterfaceRestWithHttpInfo(eServiceId, descriptorId, templateInstanceInterfaceRESTSeed);
    }

    @Override
    public void editAgreementApprovalPolicy(UUID eServiceId, UUID descriptorId,
                                            AgreementApprovalPolicy policy) {
        eservicesApi.updateAgreementApprovalPolicy(
                eServiceId,
                descriptorId,
                new UpdateEServiceDescriptorAgreementApprovalPolicySeed()
                        .agreementApprovalPolicy(policy)
        );
    }

    @Override
    public ProducerEServiceDescriptor getEServiceDescriptor(UUID eServiceId, UUID descriptorId) {
        return eservicesApi.getProducerEServiceDescriptor(eServiceId, descriptorId);
    }

    @Override
    public void approveDelegatedEServiceDescriptor(UUID eServiceId, UUID descriptorId) {
        this.eservicesApi.approveDelegatedEServiceDescriptor(eServiceId, descriptorId);
    }

    @Override
    public void rejectDelegatedEServiceDescriptor(UUID eServiceId, UUID descriptorId, RejectDelegatedEServiceDescriptorSeed rejectDelegatedEServiceDescriptorSeed) {
        this.eservicesApi.rejectDelegatedEServiceDescriptor(eServiceId, descriptorId, rejectDelegatedEServiceDescriptorSeed);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.eservicesApi.setApiClient(createApiClient(bearerToken));
    }

}
