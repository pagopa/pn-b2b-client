package it.pagopa.interop.agreement.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.util.List;
import java.util.UUID;

public interface IEServiceClient extends SettableBearerToken {
    CreatedEServiceDescriptor createEService(EServiceSeed eserviceSeed);

    void deleteEService(UUID eServiceId);

    void deleteEServiceRiskAnalysis(UUID eServiceId, UUID riskAnalysisId);

    ResponseEntity<Void> activateDescriptor(UUID eServiceId, UUID descriptorId);

    CreatedResource updateDraftDescriptor(UUID eServiceId, UUID descriptorId, UpdateEServiceDescriptorSeed updateEServiceDescriptorSeed);

    void updateDescriptorAttributes(UUID eServiceId, UUID descriptorId, DescriptorAttributesSeed descriptorAttributesSeed);

    CreatedResource updateDescriptor(UUID eServiceId, UUID descriptorId, UpdateEServiceDescriptorQuotas updateEServiceDescriptorQuotas);

    CreatedResource createEServiceDocument(UUID eServiceId, UUID descriptorId, String kind, String prettyName, org.springframework.core.io.Resource doc);

    void publishDescriptor(UUID eServiceId, UUID descriptorId);

    ResponseEntity<Void> suspendDescriptor(UUID eServiceId, UUID descriptorId);

        ResponseEntity<Void> scheduleArchiveDescriptor(UUID eServiceId, UUID descriptorId, GracePeriodDays gracePeriodDays);

        ResponseEntity<Void> scheduleArchiveEService(UUID eServiceId, EServiceArchivingSeed eserviceArchivingSeed);

    ResponseEntity<Void> cancelDescriptorArchiving(UUID eServiceId, UUID descriptorId);

    ResponseEntity<Void> cancelEServiceArchiving(UUID eServiceId);

    CreatedResource createDescriptor(UUID eServiceId);

    void deleteDraft(UUID eServiceId, UUID descriptorId);

    void deleteEServiceDocumentById(UUID eServiceId, UUID descriptorId, UUID documentId);

    void addRiskAnalysisToEService(UUID eServiceId, EServiceRiskAnalysisSeed eserviceRiskAnalysisSeed);

    FileResource exportEServiceDescriptor(UUID eserviceId, UUID descriptorId);

    CatalogEServices getEServicesCatalog(Integer offset, Integer limit, String q, List<UUID> producersIds,
                                         List<UUID> attributesIds, List<EServiceDescriptorState> states,
                                         List<AgreementState> agreementStates, EServiceMode mode, Boolean isConsumerDelegable);

    CatalogEServiceDescriptor getCatalogEServiceDescriptor(UUID eserviceId, UUID descriptorId);

    File getEServiceDocumentById(UUID eServiceId, UUID descriptorId, UUID documentId);

    EServiceDoc updateEServiceDocumentById(UUID eServiceId, UUID descriptorId, UUID documentId, UpdateEServiceDescriptorDocumentSeed updateEServiceDescriptorDocumentSeed);

    CreatedResource updateEServiceById(UUID eServiceId, UpdateEServiceSeed updateEServiceSeed);

    CreatedResource updateEServiceDelegationFlags(UUID eServiceId, EServiceDelegationFlagsUpdateSeed eserviceDelegationFlagsUpdateSeed);

    CreatedResource updateEServiceDescription(UUID eServiceId, EServiceDescriptionUpdateSeed eserviceDescriptionUpdateSeed);

    CreatedEServiceDescriptor cloneEServiceByDescriptor(UUID eServiceId, UUID descriptorId);

    void updateEServiceName(UUID eServiceId, EServiceNameUpdateSeed eserviceNameUpdateSeed);

    File getEServiceConsumers(UUID eServiceId);

    EServiceRiskAnalysis getEServiceRiskAnalysis(UUID eServiceId, UUID riskAnalysisId);

    void updateEServiceRiskAnalysis(UUID eServiceId, UUID riskAnalysisId, EServiceRiskAnalysisSeed eserviceRiskAnalysisSeed);

    PresignedUrl getImportEservicePresignedUrl(String fileName);

    CreatedEServiceDescriptor importEService(FileResource fileResource);

    void updateEServicePersonalDataFlagAfterPublication(UUID eServiceId, EServicePersonalDataFlagUpdateSeed seed);

    ResponseEntity<CreatedResource> createEServiceInstanceFromTemplateWithHttpInfo(
            UUID templateId, InstanceEServiceSeed instanceEServiceSeed);

    ResponseEntity<EServiceTemplateInstances> getEServiceTemplateInstancesWithHttpInfo(
            UUID templateId);

    ResponseEntity<EServiceTemplateInstances> getEServiceTemplateInstancesWithHttpInfo(
            UUID templateId, Integer offset, Integer limit, String producerName,
            List<EServiceDescriptorState> states);

    ResponseEntity<EServiceTemplateInstances> getMyEServiceTemplateInstancesWithHttpInfo(UUID templateId, Integer offset, Integer limit);

    ResponseEntity<CreatedResource> upgradeEServiceInstanceWithHttpInfo(UUID eServiceId);

    ResponseEntity<ProducerEServiceDescriptor> getProducerEServiceDescriptorWithHttpInfo(
            UUID eserviceId, UUID descriptorId);

    ResponseEntity<ProducerEServices> getProducerEServicesWithHttpInfo(
            String eServiceName);

    ResponseEntity<CreatedResource> updateEServiceTemplateInstanceByIdWithHttpInfo(
            UUID eServiceId,
            UpdateEServiceTemplateInstanceSeed updateEServiceTemplateInstanceSeed
    );

    ResponseEntity<CreatedResource> updateEServiceInstanceLabelAfterPublicationWithHttpInfo(
            UUID eServiceId,
            EServiceInstanceLabelUpdateSeed eServiceInstanceLabelUpdateSeed
    );

    ResponseEntity<CreatedResource> updateDraftDescriptorTemplateInstanceWithHttpInfo(
            UUID eServiceId,
            UUID descriptorId,
            UpdateEServiceDescriptorTemplateInstanceSeed updateEServiceDescriptorTemplateInstanceSeed
    );

    ResponseEntity<CreatedResource> updateTemplateInstanceDescriptorWithHttpInfo(
            UUID eServiceId,
            UUID descriptorId,
            UpdateEServiceTemplateInstanceDescriptorQuotas descriptorQuotas
    );

    ResponseEntity<ProducerEServiceDetails> getProducerEServiceDetailsWithHttpInfo(
            UUID eserviceId);

    ResponseEntity<CreatedResource> addEServiceTemplateInstanceInterfaceRestWithHttpInfo(
            UUID eServiceId, UUID descriptorId, TemplateInstanceInterfaceRESTSeed templateInstanceInterfaceRESTSeed);

    void editAgreementApprovalPolicy(UUID eServiceId, UUID descriptorId, AgreementApprovalPolicy policy);

    ProducerEServiceDescriptor getEServiceDescriptor(UUID eServiceId, UUID descriptorId);

    void approveDelegatedEServiceDescriptor(UUID eServiceId, UUID descriptorId);
}
