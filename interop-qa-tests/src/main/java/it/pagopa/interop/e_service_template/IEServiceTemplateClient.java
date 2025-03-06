package it.pagopa.interop.e_service_template;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDoc;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysisSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDescriptionUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateNameUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateVersionDocumentSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateVersionSeed;
import java.io.File;
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

    CreatedResource createEServiceTemplateVersion(String xCorrelationId, UUID eServiceTemplateId);

    ResponseEntity<CreatedResource> createEServiceTemplateVersionWithHttpInfo(String xCorrelationId,
        UUID eServiceTemplateId);

    void updateEServiceTemplateVersion(
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

    EServiceDoc updateDocument(
        String xCorrelationId,
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        UUID documentId,
        UpdateEServiceTemplateVersionDocumentSeed updateEServiceTemplateVersionDocumentSeed
    );

    ResponseEntity<EServiceDoc> updateDocumentWithHttpInfo(
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

    void updateEServiceTemplateAudienceDescription(String xCorrelationId, UUID eServiceTemplateId,
        EServiceTemplateDescriptionUpdateSeed seed);

    ResponseEntity<Void> updateEServiceTemplateAudienceDescriptionWithHttpInfo(
        String xCorrelationId,
        UUID eServiceTemplateId,
        EServiceTemplateDescriptionUpdateSeed seed);
}
