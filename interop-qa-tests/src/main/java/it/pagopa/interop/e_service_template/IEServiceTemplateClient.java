package it.pagopa.interop.e_service_template;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.CatalogEServiceTemplates;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganizations;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributesSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysisSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDescriptionUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateIntendedTargetUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateNameUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionQuotasUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceTemplates;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateVersionDocumentSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateVersionSeed;
import java.io.File;
import java.util.List;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface IEServiceTemplateClient extends SettableBearerToken {
    enum EServiceTemplateDocumentKind {
        INTERFACE, DOCUMENT
    }

    CreatedEServiceTemplateVersion createEServiceTemplate(EServiceTemplateSeed eserviceSeed);

    void updateEServiceTemplate(UUID eServiceTemplateId,
        UpdateEServiceTemplateSeed updateEServiceTemplateSeed);

    ResponseEntity<Void> updateEServiceTemplateWithHttpInfo(UUID eServiceTemplateId,
        UpdateEServiceTemplateSeed updateEServiceTemplateSeed);

    CreatedResource createEServiceTemplateVersion(UUID eServiceTemplateId);

    ResponseEntity<CreatedResource> createEServiceTemplateVersionWithHttpInfo(UUID eServiceTemplateId);

    void updateEServiceTemplateVersion(
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        UpdateEServiceTemplateVersionSeed seed);

    ResponseEntity<Void> updateEServiceTemplateVersionWithHttpInfo(
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        UpdateEServiceTemplateVersionSeed seed);

    void deleteEServiceTemplateVersion(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId);

    ResponseEntity<Void> deleteEServiceTemplateVersionWithHttpInfo(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId);

    void publishEServiceTemplate(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId);

    ResponseEntity<Void> publishEServiceTemplateWithHttpInfo(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId);

    void suspendEServiceTemplate(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId);

    ResponseEntity<Void> suspendEServiceTemplateWithHttpInfo(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId);

    void activateEServiceTemplate(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId);

    ResponseEntity<Void> activateEServiceTemplateWithHttpInfo(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId);

    ResponseEntity<EServiceTemplateDetails> getEServiceTemplateWithHttpInfo(UUID eServiceTemplateId);

    EServiceTemplateDetails getEServiceTemplate(UUID eServiceTemplateId);

    EServiceTemplateVersionDetails getEServiceTemplateVersion(UUID eServiceTemplateId, UUID eServiceTemplateVersionId);

    ResponseEntity<EServiceTemplateVersionDetails> getEServiceTemplateVersionWithHttpInfo(
        UUID eServiceTemplateId, UUID eServiceTemplateVersionId);

    void addRiskAnalysis(UUID eServiceTemplateId,
        EServiceRiskAnalysisSeed seed);

    void deleteRiskAnalysis(
        UUID eServiceTemplateId,
        UUID riskAnalysisId);

    void editRiskAnalysis(
        UUID eServiceTemplateId,
        UUID riskAnalysisId,
        EServiceRiskAnalysisSeed seed
    );

    ResponseEntity<Void> editRiskAnalysisWithHttpInfo(
        UUID eServiceTemplateId,
        UUID riskAnalysisId,
        EServiceRiskAnalysisSeed seed
    );

    File getDocument(
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        UUID documentId
    );

    ResponseEntity<File> getDocumentWithHttpInfo(
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        UUID documentId
    );

    ResponseEntity<CreatedResource> addDocumentWithHttpInfo(
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        EServiceTemplateDocumentKind kind,
        String prettyName,
        Resource doc
    );

    CreatedResource addDocument(
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        EServiceTemplateDocumentKind kind,
        String prettyName,
        Resource doc
    );

    void updateDocument(
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        UUID documentId,
        UpdateEServiceTemplateVersionDocumentSeed updateEServiceTemplateVersionDocumentSeed
    );

    ResponseEntity<Void> updateDocumentWithHttpInfo(
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        UUID documentId,
        UpdateEServiceTemplateVersionDocumentSeed updateEServiceTemplateVersionDocumentSeed
    );

    void deleteDocument(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId, UUID documentId);

    ResponseEntity<Void> deleteDocumentWithHttpInfo(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId, UUID documentId);

    void updateEServiceTemplateName(UUID eServiceTemplateId,
        EServiceTemplateNameUpdateSeed eserviceTemplateNameUpdateSeed);

    ResponseEntity<Void> updateEServiceTemplateNameWithHttpInfo(UUID eServiceTemplateId,
        EServiceTemplateNameUpdateSeed eserviceTemplateNameUpdateSeed);

    void updateEServiceIntendedTarget(UUID eServiceTemplateId,
        EServiceTemplateIntendedTargetUpdateSeed seed);

    ResponseEntity<Void> updateEServiceIntendedTargetWithHttpInfo(
        UUID eServiceTemplateId,
        EServiceTemplateIntendedTargetUpdateSeed seed);

    void updateEServiceTemplateDescription(UUID eServiceTemplateId,
        EServiceTemplateDescriptionUpdateSeed seed);

    ResponseEntity<Void> updateEServiceTemplateDescriptionWithHttpInfo(
        UUID eServiceTemplateId,
        EServiceTemplateDescriptionUpdateSeed seed);

    void updateEServiceTemplateVersionQuotas(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId, EServiceTemplateVersionQuotasUpdateSeed seed);

    ResponseEntity<Void> updateEServiceTemplateVersionQuotasWithHttpInfo(UUID eServiceTemplateId, UUID eServiceTemplateVersionId,
        EServiceTemplateVersionQuotasUpdateSeed seed);

    CreatedResource updateEServiceTemplateVersionAttributes(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId, DescriptorAttributesSeed seed);

    ResponseEntity<CreatedResource> updateEServiceTemplateVersionAttributesWithHttpInfo(
        UUID eServiceTemplateId, UUID eServiceTemplateVersionId, DescriptorAttributesSeed seed);

    // DEV. NOTE: si cambia naming convention omettendo il suffisso "withHttpInfo", rendendolo implicito da qui in avanti
    ResponseEntity<CatalogEServiceTemplates> getEServiceTemplatesCatalog();

    ResponseEntity<ProducerEServiceTemplates> getCreatorEServiceTemplates();

    ResponseEntity<ProducerEServiceTemplates> getCreatorEServiceTemplates(
        Integer offset,
        Integer limit,
        String q);

    ResponseEntity<CompactOrganizations> getEServiceTemplateCreators();

    ResponseEntity<CompactOrganizations> getEServiceTemplateCreators(
        Integer offset,
        Integer limit,
        String q);

    ResponseEntity<CatalogEServiceTemplates> getEServiceTemplatesCatalog(Integer offset, Integer limit, String q, List<UUID> creatorsIds);
}
