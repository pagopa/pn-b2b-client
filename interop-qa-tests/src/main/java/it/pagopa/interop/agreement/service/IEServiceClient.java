package it.pagopa.interop.agreement.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementState;
import it.pagopa.interop.generated.openapi.clients.bff.model.CatalogEServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.CatalogEServices;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptionUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDoc;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysis;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysisSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.FileResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.PresignedUrl;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorDocumentSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorQuotas;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceSeed;
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
    CreatedResource updateDescriptor(UUID eServiceId, UUID descriptorId, UpdateEServiceDescriptorQuotas updateEServiceDescriptorQuotas);
    CreatedResource createEServiceDocument(UUID eServiceId, UUID descriptorId, String kind, String prettyName, org.springframework.core.io.Resource doc);
    void publishDescriptor(UUID eServiceId, UUID descriptorId);
    void suspendDescriptor(UUID eServiceId, UUID descriptorId);
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
    CreatedResource updateEServiceDescription(UUID eServiceId, EServiceDescriptionUpdateSeed eserviceDescriptionUpdateSeed);
    CreatedEServiceDescriptor cloneEServiceByDescriptor(UUID eServiceId, UUID descriptorId);
    File getEServiceConsumers(UUID eServiceId);
    EServiceRiskAnalysis getEServiceRiskAnalysis(UUID eServiceId, UUID riskAnalysisId);
    void updateEServiceRiskAnalysis(UUID eServiceId, UUID riskAnalysisId, EServiceRiskAnalysisSeed eserviceRiskAnalysisSeed);
    PresignedUrl getImportEservicePresignedUrl(String fileName);
    CreatedEServiceDescriptor importEService(FileResource fileResource);
}
