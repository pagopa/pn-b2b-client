package it.pagopa.interop.e_service_template.impl;

import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EserviceTemplatesApi;
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
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/* TODO considerato che le varianti con HTTP info conservano lo stato di errore senza
 *  - presumibilmente - lanciare alcuna eccezione, potrebbe essere preferibile utilizzare solo
 *  quelle, rimuovere le altre e adattare gli utilizzi di conseguenza */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EServiceTemplateApiClientImpl implements IEServiceTemplateClient {
    private final EserviceTemplatesApi eserviceTemplatesApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public EServiceTemplateApiClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getBaseUrl();
        this.eserviceTemplatesApi = new EserviceTemplatesApi(createApiClient("dummyBearer"));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public CreatedEServiceTemplateVersion createEServiceTemplate(String xCorrelationId,
        EServiceTemplateSeed eserviceSeed) {
        return eserviceTemplatesApi.createEServiceTemplate(xCorrelationId, eserviceSeed);
    }

    @Override
    public void updateEServiceTemplate(String xCorrelationId, UUID eServiceTemplateId,
        UpdateEServiceTemplateSeed updateEServiceTemplateSeed) {
        eserviceTemplatesApi.updateEServiceTemplate(xCorrelationId, eServiceTemplateId, updateEServiceTemplateSeed);
    }

    @Override
    public ResponseEntity<Void> updateEServiceTemplateWithHttpInfo(String xCorrelationId,
        UUID eServiceTemplateId,
        UpdateEServiceTemplateSeed updateEServiceTemplateSeed) {
        return eserviceTemplatesApi.updateEServiceTemplateWithHttpInfo(xCorrelationId, eServiceTemplateId, updateEServiceTemplateSeed);
    }

    @Override
    public CreatedResource createEServiceTemplateVersion(String xCorrelationId,
        UUID eServiceTemplateId) {
        return eserviceTemplatesApi.createEServiceTemplateVersion(xCorrelationId, eServiceTemplateId);
    }

    @Override
    public ResponseEntity<CreatedResource> createEServiceTemplateVersionWithHttpInfo(
        String xCorrelationId,
        UUID eServiceTemplateId) {
        return eserviceTemplatesApi.createEServiceTemplateVersionWithHttpInfo(xCorrelationId, eServiceTemplateId);
    }

    @Override
    public void updateEServiceTemplateVersion(
        String xCorrelationId,
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        UpdateEServiceTemplateVersionSeed seed)
    {
        eserviceTemplatesApi.updateDraftTemplateVersion(
            xCorrelationId,
            eServiceTemplateId,
            eServiceTemplateVersionId,
            seed);
    }

    @Override
    public ResponseEntity<Void> updateEServiceTemplateVersionWithHttpInfo(
        String xCorrelationId,
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        UpdateEServiceTemplateVersionSeed seed)
    {
        return eserviceTemplatesApi.updateDraftTemplateVersionWithHttpInfo(
            xCorrelationId,
            eServiceTemplateId,
            eServiceTemplateVersionId,
            seed);
    }

    @Override
    public void deleteEServiceTemplateVersion(String xCorrelationId, UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId) {
        eserviceTemplatesApi.deleteDraftTemplateVersion(
            xCorrelationId,
            eServiceTemplateId,
            eServiceTemplateVersionId);
    }

    @Override
    public ResponseEntity<Void> deleteEServiceTemplateVersionWithHttpInfo(String xCorrelationId,
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId) {
        return eserviceTemplatesApi.deleteDraftTemplateVersionWithHttpInfo(
            xCorrelationId,
            eServiceTemplateId,
            eServiceTemplateVersionId);
    }

    @Override
    public void publishEServiceTemplate(String xCorrelationId, UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId) {
        eserviceTemplatesApi.publishEServiceTemplateVersion(
            xCorrelationId,
            eServiceTemplateId,
            eServiceTemplateVersionId);
    }

    @Override
    public ResponseEntity<Void> publishEServiceTemplateWithHttpInfo(String xCorrelationId,
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId) {
        return eserviceTemplatesApi.publishEServiceTemplateVersionWithHttpInfo(
            xCorrelationId,
            eServiceTemplateId,
            eServiceTemplateVersionId);
    }

    @Override
    public void suspendEServiceTemplate(String xCorrelationId, UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId) {
        eserviceTemplatesApi.suspendEServiceTemplateVersion(
            xCorrelationId,
            eServiceTemplateId,
            eServiceTemplateVersionId);
    }

    @Override
    public ResponseEntity<Void> suspendEServiceTemplateWithHttpInfo(String xCorrelationId,
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId) {
        return eserviceTemplatesApi.suspendEServiceTemplateVersionWithHttpInfo(
            xCorrelationId,
            eServiceTemplateId,
            eServiceTemplateVersionId);
    }

    @Override
    public void activateEServiceTemplate(String xCorrelationId, UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId) {
        eserviceTemplatesApi.activateEServiceTemplateVersion(
            xCorrelationId,
            eServiceTemplateId,
            eServiceTemplateVersionId);
    }

    @Override
    public ResponseEntity<Void> activateEServiceTemplateWithHttpInfo(String xCorrelationId,
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId) {
        return eserviceTemplatesApi.activateEServiceTemplateVersionWithHttpInfo(
            xCorrelationId,
            eServiceTemplateId,
            eServiceTemplateVersionId);
    }

    @Override
    public ResponseEntity<EServiceTemplateDetails> getEServiceTemplateWithHttpInfo(
        String xCorrelationId,
        UUID eServiceTemplateId) {
        return eserviceTemplatesApi.getEServiceTemplateWithHttpInfo(xCorrelationId, eServiceTemplateId);
    }

    @Override
    public EServiceTemplateDetails getEServiceTemplate(String xCorrelationId,
        UUID eServiceTemplateId) {
        return eserviceTemplatesApi.getEServiceTemplate(xCorrelationId, eServiceTemplateId);
    }

    @Override
    public EServiceTemplateVersionDetails getEServiceTemplateVersion(String xCorrelationId,
        UUID eServiceTemplateId, UUID eServiceTemplateVersionId) {
        return eserviceTemplatesApi.getEServiceTemplateVersion(xCorrelationId, eServiceTemplateId,
            eServiceTemplateVersionId);
    }

    @Override
    public ResponseEntity<EServiceTemplateVersionDetails> getEServiceTemplateVersionWithHttpInfo(
        String xCorrelationId,
        UUID eServiceTemplateId, UUID eServiceTemplateVersionId) {
        return eserviceTemplatesApi.getEServiceTemplateVersionWithHttpInfo(xCorrelationId, eServiceTemplateId,
            eServiceTemplateVersionId);
    }

    @Override
    public void addRiskAnalysis(
        String xCorrelationId,
        UUID eServiceTemplateId,
        EServiceRiskAnalysisSeed seed
    ) {
        this.eserviceTemplatesApi.createEServiceTemplateRiskAnalysis(xCorrelationId, eServiceTemplateId, seed);
    }

    @Override
    public void deleteRiskAnalysis(
        String xCorrelationId,
        UUID eServiceTemplateId,
        UUID riskAnalysisId
    ) {
        this.eserviceTemplatesApi.deleteEServiceTemplateRiskAnalysis(xCorrelationId, eServiceTemplateId, riskAnalysisId);
    }

    @Override
    public void editRiskAnalysis(
        String xCorrelationId,
        UUID eServiceTemplateId,
        UUID riskAnalysisId,
        EServiceRiskAnalysisSeed seed
    ) {
        this.eserviceTemplatesApi.updateEServiceTemplateRiskAnalysis(xCorrelationId, eServiceTemplateId, riskAnalysisId, seed);
    }

    @Override
    public ResponseEntity<Void> editRiskAnalysisWithHttpInfo(
        String xCorrelationId,
        UUID eServiceTemplateId,
        UUID riskAnalysisId,
        EServiceRiskAnalysisSeed seed
    ) {
        return this.eserviceTemplatesApi.updateEServiceTemplateRiskAnalysisWithHttpInfo(xCorrelationId, eServiceTemplateId, riskAnalysisId, seed);
    }

    @Override
    public CreatedResource addDocument(
        String xCorrelationId,
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        EServiceTemplateDocumentKind kind,
        String prettyName,
        Resource doc
    ) {
        return this.eserviceTemplatesApi.createEServiceTemplateDocument(xCorrelationId, eServiceTemplateId, eServiceTemplateVersionId,
            kind.name(), prettyName, doc);
    }

    @Override
    public ResponseEntity<CreatedResource> addDocumentWithHttpInfo(
        String xCorrelationId,
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        EServiceTemplateDocumentKind kind,
        String prettyName,
        Resource doc
    ) {
        return this.eserviceTemplatesApi.createEServiceTemplateDocumentWithHttpInfo(xCorrelationId, eServiceTemplateId, eServiceTemplateVersionId,
            kind.name(), prettyName, doc);
    }

    @Override
    public File getDocument(
        String xCorrelationId,
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        UUID documentId
    ) {
        return this.eserviceTemplatesApi.getEServiceTemplateDocumentById(xCorrelationId, eServiceTemplateId, eServiceTemplateVersionId, documentId);
    }

    @Override
    public ResponseEntity<File> getDocumentWithHttpInfo(
        String xCorrelationId,
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        UUID documentId
    ) {
        return this.eserviceTemplatesApi.getEServiceTemplateDocumentByIdWithHttpInfo(xCorrelationId, eServiceTemplateId, eServiceTemplateVersionId, documentId);
    }

    @Override
    public void updateDocument(
        String xCorrelationId,
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        UUID documentId,
        UpdateEServiceTemplateVersionDocumentSeed updateEServiceTemplateVersionDocumentSeed
    ) {
        this.eserviceTemplatesApi.updateEServiceTemplateDocumentById(xCorrelationId, eServiceTemplateId, eServiceTemplateVersionId, documentId, updateEServiceTemplateVersionDocumentSeed);
    }

    @Override
    public ResponseEntity<Void> updateDocumentWithHttpInfo(
        String xCorrelationId,
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        UUID documentId,
        UpdateEServiceTemplateVersionDocumentSeed updateEServiceTemplateVersionDocumentSeed
    ) {
        return this.eserviceTemplatesApi.updateEServiceTemplateDocumentByIdWithHttpInfo(xCorrelationId, eServiceTemplateId, eServiceTemplateVersionId, documentId, updateEServiceTemplateVersionDocumentSeed);
    }

    @Override
    public void deleteDocument(String xCorrelationId, UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId, UUID documentId) {
        this.eserviceTemplatesApi.deleteEServiceTemplateDocumentById(xCorrelationId, eServiceTemplateId, eServiceTemplateVersionId, documentId);
    }

    @Override
    public ResponseEntity<Void> deleteDocumentWithHttpInfo(String xCorrelationId,
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId, UUID documentId) {
        return this.eserviceTemplatesApi.deleteEServiceTemplateDocumentByIdWithHttpInfo(xCorrelationId, eServiceTemplateId, eServiceTemplateVersionId, documentId);
    }

    @Override
    public void updateEServiceTemplateName(String xCorrelationId, UUID eServiceTemplateId,
        EServiceTemplateNameUpdateSeed eserviceTemplateNameUpdateSeed) {
        this.eserviceTemplatesApi.updateEServiceTemplateName(xCorrelationId, eServiceTemplateId, eserviceTemplateNameUpdateSeed);
    }

    @Override
    public ResponseEntity<Void> updateEServiceTemplateNameWithHttpInfo(String xCorrelationId,
        UUID eServiceTemplateId,
        EServiceTemplateNameUpdateSeed eserviceTemplateNameUpdateSeed) {
        return this.eserviceTemplatesApi.updateEServiceTemplateNameWithHttpInfo(xCorrelationId, eServiceTemplateId, eserviceTemplateNameUpdateSeed);
    }

    @Override
    public void updateEServiceIntendedTarget(String xCorrelationId,
        UUID eServiceTemplateId,
        EServiceTemplateDescriptionUpdateSeed seed) {
        this.eserviceTemplatesApi.updateEServiceTemplateIntendedTarget(xCorrelationId, eServiceTemplateId, seed);
    }

    @Override
    public ResponseEntity<Void> updateEServiceIntendedTargetWithHttpInfo(
        String xCorrelationId,
        UUID eServiceTemplateId,
        EServiceTemplateDescriptionUpdateSeed seed) {
        return this.eserviceTemplatesApi.updateEServiceTemplateIntendedTargetWithHttpInfo(xCorrelationId, eServiceTemplateId, seed);
    }

    @Override
    public void updateEServiceTemplateDescription(String xCorrelationId,
        UUID eServiceTemplateId,
        EServiceTemplateDescriptionUpdateSeed seed) {
        this.eserviceTemplatesApi.updateEServiceTemplateDescription(xCorrelationId, eServiceTemplateId, seed);
    }

    @Override
    public ResponseEntity<Void> updateEServiceTemplateDescriptionWithHttpInfo(
        String xCorrelationId,
        UUID eServiceTemplateId,
        EServiceTemplateDescriptionUpdateSeed seed) {
        return this.eserviceTemplatesApi.updateEServiceTemplateDescriptionWithHttpInfo(xCorrelationId, eServiceTemplateId, seed);
    }

    @Override
    public void updateEServiceTemplateVersionQuotas(String xCorrelationId, UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId, EServiceTemplateVersionQuotasUpdateSeed seed) {
        this.eserviceTemplatesApi.updateTemplateVersionQuotas(xCorrelationId, eServiceTemplateId, eServiceTemplateVersionId, seed);
    }

    @Override
    public ResponseEntity<Void> updateEServiceTemplateVersionQuotasWithHttpInfo(
        String xCorrelationId,
        UUID eServiceTemplateId, UUID eServiceTemplateVersionId,
        EServiceTemplateVersionQuotasUpdateSeed seed) {
        return this.eserviceTemplatesApi.updateTemplateVersionQuotasWithHttpInfo(xCorrelationId, eServiceTemplateId, eServiceTemplateVersionId, seed);
    }

    @Override
    public CreatedResource updateEServiceTemplateVersionAttributes(String xCorrelationId,
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId, DescriptorAttributesSeed seed) {
        return this.eserviceTemplatesApi.updateEServiceTemplateVersionAttributes(xCorrelationId, eServiceTemplateId, eServiceTemplateVersionId, seed);
    }

    @Override
    public ResponseEntity<CreatedResource> updateEServiceTemplateVersionAttributesWithHttpInfo(
        String xCorrelationId,
        UUID eServiceTemplateId, UUID eServiceTemplateVersionId,
        DescriptorAttributesSeed seed) {
        return this.eserviceTemplatesApi.updateEServiceTemplateVersionAttributesWithHttpInfo(xCorrelationId, eServiceTemplateId, eServiceTemplateVersionId, seed);
    }

    // DEV. NOTE: si cambia naming convention omettendo il suffisso "withHttpInfo" rendendolo implicito da qui in avanti
    @Override
    public ResponseEntity<CatalogEServiceTemplates> getEServiceTemplatesCatalog(
        String xCorrelationId) {
        return this.getEServiceTemplatesCatalog(xCorrelationId, 0, 100, null, null);
    }

    @Override
    public ResponseEntity<ProducerEServiceTemplates> getProducerEServiceTemplates(
        String xCorrelationId) {
        return this.eserviceTemplatesApi.getCreatorEServiceTemplatesWithHttpInfo(xCorrelationId, 0, 100, null);
    }

    @Override
    public ResponseEntity<ProducerEServiceTemplates> getProducerEServiceTemplates(
        String xCorrelationId,
        Integer offset,
        Integer limit,
        String q) {
        return this.eserviceTemplatesApi.getCreatorEServiceTemplatesWithHttpInfo(xCorrelationId, offset, limit, q);
    }

    @Override
    public ResponseEntity<CompactOrganizations> getEServiceTemplateCreators(String xCorrelationId) {
        return this.getEServiceTemplateCreators(xCorrelationId, 0, 100, null);
    }

    @Override
    public ResponseEntity<CompactOrganizations> getEServiceTemplateCreators(
        String xCorrelationId,
        Integer offset,
        Integer limit,
        String q) {
        return this.eserviceTemplatesApi.getEServiceTemplateCreatorsWithHttpInfo(xCorrelationId, offset, limit, q);
    }

    @Override
    public ResponseEntity<CatalogEServiceTemplates> getEServiceTemplatesCatalog(
        String xCorrelationId, Integer offset, Integer limit, String q, List<UUID> creatorsIds) {
        return this.eserviceTemplatesApi.getEServiceTemplatesCatalogWithHttpInfo(xCorrelationId, offset, limit, q, creatorsIds);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.eserviceTemplatesApi.setApiClient(createApiClient(bearerToken));
    }
}
