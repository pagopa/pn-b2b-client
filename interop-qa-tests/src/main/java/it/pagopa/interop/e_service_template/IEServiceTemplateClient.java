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

    CreatedEServiceTemplateVersion createEServiceTemplate(String xCorrelationId, EServiceTemplateSeed eserviceSeed);

    void updateEServiceTemplate(String xCorrelationId, UUID eServiceTemplateId,
        UpdateEServiceTemplateSeed updateEServiceTemplateSeed);

    ResponseEntity<Void> updateEServiceTemplateWithHttpInfo(String xCorrelationId,
        UUID eServiceTemplateId,
        UpdateEServiceTemplateSeed updateEServiceTemplateSeed);

    CreatedResource createEServiceTemplateVersion(String xCorrelationId, UUID eServiceTemplateId);

    ResponseEntity<CreatedResource> createEServiceTemplateVersionWithHttpInfo(String xCorrelationId,
        UUID eServiceTemplateId);

    void updateEServiceTemplateVersion(
        String xCorrelationId,
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        UpdateEServiceTemplateVersionSeed seed);

    ResponseEntity<Void> updateEServiceTemplateVersionWithHttpInfo(
        String xCorrelationId,
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        UpdateEServiceTemplateVersionSeed seed);

    void deleteEServiceTemplateVersion(String xCorrelationId, UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId);

    ResponseEntity<Void> deleteEServiceTemplateVersionWithHttpInfo(String xCorrelationId,
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId);

    void publishEServiceTemplate(String xCorrelationId, UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId);

    ResponseEntity<Void> publishEServiceTemplateWithHttpInfo(String xCorrelationId,
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId);

    void suspendEServiceTemplate(String xCorrelationId, UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId);

    ResponseEntity<Void> suspendEServiceTemplateWithHttpInfo(String xCorrelationId,
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId);

    void activateEServiceTemplate(String xCorrelationId, UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId);

    ResponseEntity<Void> activateEServiceTemplateWithHttpInfo(String xCorrelationId,
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId);

    ResponseEntity<EServiceTemplateDetails> getEServiceTemplateWithHttpInfo(String xCorrelationId,
        UUID eServiceTemplateId);

    EServiceTemplateDetails getEServiceTemplate(String xCorrelationId, UUID eServiceTemplateId);

    EServiceTemplateVersionDetails getEServiceTemplateVersion(String xCorrelationId, UUID eServiceTemplateId, UUID eServiceTemplateVersionId);

    ResponseEntity<EServiceTemplateVersionDetails> getEServiceTemplateVersionWithHttpInfo(
        String xCorrelationId,
        UUID eServiceTemplateId, UUID eServiceTemplateVersionId);

    void addRiskAnalysis(String xCorrelationId, UUID eServiceTemplateId,
        EServiceRiskAnalysisSeed seed);

    void deleteRiskAnalysis(
        String xCorrelationId,
        UUID eServiceTemplateId,
        UUID riskAnalysisId);

    void editRiskAnalysis(
        String xCorrelationId,
        UUID eServiceTemplateId,
        UUID riskAnalysisId,
        EServiceRiskAnalysisSeed seed
    );

    ResponseEntity<Void> editRiskAnalysisWithHttpInfo(
        String xCorrelationId,
        UUID eServiceTemplateId,
        UUID riskAnalysisId,
        EServiceRiskAnalysisSeed seed
    );

    File getDocument(
        String xCorrelationId,
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        UUID documentId
    );

    ResponseEntity<File> getDocumentWithHttpInfo(
        String xCorrelationId,
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        UUID documentId
    );

    ResponseEntity<CreatedResource> addDocumentWithHttpInfo(
        String xCorrelationId,
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        EServiceTemplateDocumentKind kind,
        String prettyName,
        Resource doc
    );

    CreatedResource addDocument(
        String xCorrelationId,
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        EServiceTemplateDocumentKind kind,
        String prettyName,
        Resource doc
    );

    void updateDocument(
        String xCorrelationId,
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        UUID documentId,
        UpdateEServiceTemplateVersionDocumentSeed updateEServiceTemplateVersionDocumentSeed
    );

    ResponseEntity<Void> updateDocumentWithHttpInfo(
        String xCorrelationId,
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        UUID documentId,
        UpdateEServiceTemplateVersionDocumentSeed updateEServiceTemplateVersionDocumentSeed
    );

    void deleteDocument(String xCorrelationId, UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId, UUID documentId);

    ResponseEntity<Void> deleteDocumentWithHttpInfo(String xCorrelationId, UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId, UUID documentId);

    void updateEServiceTemplateName(String xCorrelationId, UUID eServiceTemplateId,
        EServiceTemplateNameUpdateSeed eserviceTemplateNameUpdateSeed);

    ResponseEntity<Void> updateEServiceTemplateNameWithHttpInfo(String xCorrelationId,
        UUID eServiceTemplateId,
        EServiceTemplateNameUpdateSeed eserviceTemplateNameUpdateSeed);

    void updateEServiceIntendedTarget(String xCorrelationId, UUID eServiceTemplateId,
        EServiceTemplateDescriptionUpdateSeed seed);

    ResponseEntity<Void> updateEServiceIntendedTargetWithHttpInfo(
        String xCorrelationId,
        UUID eServiceTemplateId,
        EServiceTemplateDescriptionUpdateSeed seed);

    void updateEServiceTemplateDescription(String xCorrelationId,
        UUID eServiceTemplateId,
        EServiceTemplateDescriptionUpdateSeed seed);

    ResponseEntity<Void> updateEServiceTemplateDescriptionWithHttpInfo(
        String xCorrelationId,
        UUID eServiceTemplateId,
        EServiceTemplateDescriptionUpdateSeed seed);

    void updateEServiceTemplateVersionQuotas(String xCorrelationId, UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId, EServiceTemplateVersionQuotasUpdateSeed seed);

    ResponseEntity<Void> updateEServiceTemplateVersionQuotasWithHttpInfo(String xCorrelationId,
        UUID eServiceTemplateId, UUID eServiceTemplateVersionId,
        EServiceTemplateVersionQuotasUpdateSeed seed);

    CreatedResource updateEServiceTemplateVersionAttributes(String xCorrelationId,
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId, DescriptorAttributesSeed seed);

    ResponseEntity<CreatedResource> updateEServiceTemplateVersionAttributesWithHttpInfo(
        String xCorrelationId,
        UUID eServiceTemplateId, UUID eServiceTemplateVersionId,
        DescriptorAttributesSeed seed);

    // DEV. NOTE: si cambia naming convention omettendo il suffisso "withHttpInfo", rendendolo implicito da qui in avanti
    ResponseEntity<CatalogEServiceTemplates> getEServiceTemplatesCatalog(String xCorrelationId);

    ResponseEntity<ProducerEServiceTemplates> getCreatorEServiceTemplates(String xCorrelationId);

    ResponseEntity<ProducerEServiceTemplates> getCreatorEServiceTemplates(
        String xCorrelationId,
        Integer offset,
        Integer limit,
        String q);

    ResponseEntity<CompactOrganizations> getEServiceTemplateCreators(String xCorrelationId);

    ResponseEntity<CompactOrganizations> getEServiceTemplateCreators(
        String xCorrelationId,
        Integer offset,
        Integer limit,
        String q);

    ResponseEntity<CatalogEServiceTemplates> getEServiceTemplatesCatalog(String xCorrelationId,
        Integer offset, Integer limit, String q, List<UUID> creatorsIds);
}
