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
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateRiskAnalysisSeed;
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
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

/* TODO considerato che le varianti con HTTP info conservano lo stato di errore potrebbe essere
    preferibile utilizzare solo quelle, rimuovere le altre e adattare gli utilizzi di conseguenza */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Retryable(
        retryFor = { HttpServerErrorException.class },
        backoff = @Backoff(delay = 2000)
)
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
    public CreatedEServiceTemplateVersion createEServiceTemplate(EServiceTemplateSeed eserviceSeed) {
        return eserviceTemplatesApi.createEServiceTemplate(eserviceSeed);
    }

    @Override
    public void updateEServiceTemplate(UUID eServiceTemplateId,
        UpdateEServiceTemplateSeed updateEServiceTemplateSeed) {
        eserviceTemplatesApi.updateEServiceTemplate(eServiceTemplateId, updateEServiceTemplateSeed);
    }

    @Override
    public ResponseEntity<Void> updateEServiceTemplateWithHttpInfo(UUID eServiceTemplateId,
        UpdateEServiceTemplateSeed updateEServiceTemplateSeed) {
        return eserviceTemplatesApi.updateEServiceTemplateWithHttpInfo(eServiceTemplateId, updateEServiceTemplateSeed);
    }

    @Override
    public CreatedResource createEServiceTemplateVersion(UUID eServiceTemplateId) {
        return eserviceTemplatesApi.createEServiceTemplateVersion(eServiceTemplateId);
    }

    @Override
    public ResponseEntity<CreatedResource> createEServiceTemplateVersionWithHttpInfo(
        UUID eServiceTemplateId) {
        return eserviceTemplatesApi.createEServiceTemplateVersionWithHttpInfo(eServiceTemplateId);
    }

    @Override
    public void updateEServiceTemplateVersion(
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        UpdateEServiceTemplateVersionSeed seed)
    {
        eserviceTemplatesApi.updateDraftTemplateVersion(
            eServiceTemplateId,
            eServiceTemplateVersionId,
            seed);
    }

    @Override
    public ResponseEntity<Void> updateEServiceTemplateVersionWithHttpInfo(
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        UpdateEServiceTemplateVersionSeed seed)
    {
        return eserviceTemplatesApi.updateDraftTemplateVersionWithHttpInfo(
            eServiceTemplateId,
            eServiceTemplateVersionId,
            seed);
    }

    @Override
    public void deleteEServiceTemplateVersion(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId) {
        eserviceTemplatesApi.deleteDraftTemplateVersion(
            eServiceTemplateId,
            eServiceTemplateVersionId);
    }

    @Override
    public ResponseEntity<Void> deleteEServiceTemplateVersionWithHttpInfo(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId) {
        return eserviceTemplatesApi.deleteDraftTemplateVersionWithHttpInfo(
            eServiceTemplateId,
            eServiceTemplateVersionId);
    }

    @Override
    public void publishEServiceTemplate(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId) {
        eserviceTemplatesApi.publishEServiceTemplateVersion(
            eServiceTemplateId,
            eServiceTemplateVersionId);
    }

    @Override
    public ResponseEntity<Void> publishEServiceTemplateWithHttpInfo(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId) {
        return eserviceTemplatesApi.publishEServiceTemplateVersionWithHttpInfo(
            eServiceTemplateId,
            eServiceTemplateVersionId);
    }

    @Override
    public void suspendEServiceTemplate(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId) {
        eserviceTemplatesApi.suspendEServiceTemplateVersion(
            eServiceTemplateId,
            eServiceTemplateVersionId);
    }

    @Override
    public ResponseEntity<Void> suspendEServiceTemplateWithHttpInfo(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId) {
        return eserviceTemplatesApi.suspendEServiceTemplateVersionWithHttpInfo(
            eServiceTemplateId,
            eServiceTemplateVersionId);
    }

    @Override
    public void activateEServiceTemplate(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId) {
        eserviceTemplatesApi.activateEServiceTemplateVersion(
            eServiceTemplateId,
            eServiceTemplateVersionId);
    }

    @Override
    public ResponseEntity<Void> activateEServiceTemplateWithHttpInfo(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId) {
        return eserviceTemplatesApi.activateEServiceTemplateVersionWithHttpInfo(
            eServiceTemplateId,
            eServiceTemplateVersionId);
    }

    @Override
    public ResponseEntity<EServiceTemplateDetails> getEServiceTemplateWithHttpInfo(
        UUID eServiceTemplateId) {
        return eserviceTemplatesApi.getEServiceTemplateWithHttpInfo(eServiceTemplateId);
    }

    @Override
    public EServiceTemplateDetails getEServiceTemplate(UUID eServiceTemplateId) {
        return eserviceTemplatesApi.getEServiceTemplate(eServiceTemplateId);
    }

    @Override
    public EServiceTemplateVersionDetails getEServiceTemplateVersion(UUID eServiceTemplateId, UUID eServiceTemplateVersionId) {
        return eserviceTemplatesApi.getEServiceTemplateVersion(eServiceTemplateId,
            eServiceTemplateVersionId);
    }

    @Override
    public ResponseEntity<EServiceTemplateVersionDetails> getEServiceTemplateVersionWithHttpInfo(
        UUID eServiceTemplateId, UUID eServiceTemplateVersionId) {
        return eserviceTemplatesApi.getEServiceTemplateVersionWithHttpInfo(eServiceTemplateId,
            eServiceTemplateVersionId);
    }

    @Override
    public void addRiskAnalysis(
        UUID eServiceTemplateId,
        EServiceTemplateRiskAnalysisSeed seed
    ) {
        this.eserviceTemplatesApi.createEServiceTemplateRiskAnalysis(eServiceTemplateId, seed);
    }

    @Override
    public void deleteRiskAnalysis(
        UUID eServiceTemplateId,
        UUID riskAnalysisId
    ) {
        this.eserviceTemplatesApi.deleteEServiceTemplateRiskAnalysis(eServiceTemplateId, riskAnalysisId);
    }

    @Override
    public void editRiskAnalysis(
        UUID eServiceTemplateId,
        UUID riskAnalysisId,
        EServiceTemplateRiskAnalysisSeed seed
    ) {
        this.eserviceTemplatesApi.updateEServiceTemplateRiskAnalysis(eServiceTemplateId, riskAnalysisId, seed);
    }

    @Override
    public ResponseEntity<Void> editRiskAnalysisWithHttpInfo(
        UUID eServiceTemplateId,
        UUID riskAnalysisId,
        EServiceTemplateRiskAnalysisSeed seed
    ) {
        return this.eserviceTemplatesApi.updateEServiceTemplateRiskAnalysisWithHttpInfo(eServiceTemplateId, riskAnalysisId, seed);
    }

    @Override
    public CreatedResource addDocument(
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        EServiceTemplateDocumentKind kind,
        String prettyName,
        Resource doc
    ) {
        return this.eserviceTemplatesApi.createEServiceTemplateDocument(eServiceTemplateId, eServiceTemplateVersionId,
            kind.name(), prettyName, doc);
    }

    @Override
    public ResponseEntity<CreatedResource> addDocumentWithHttpInfo(
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        EServiceTemplateDocumentKind kind,
        String prettyName,
        Resource doc
    ) {
        return this.eserviceTemplatesApi.createEServiceTemplateDocumentWithHttpInfo(eServiceTemplateId, eServiceTemplateVersionId,
            kind.name(), prettyName, doc);
    }

    @Override
    public File getDocument(
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        UUID documentId
    ) {
        return this.eserviceTemplatesApi.getEServiceTemplateDocumentById(eServiceTemplateId, eServiceTemplateVersionId, documentId);
    }

    @Override
    public ResponseEntity<File> getDocumentWithHttpInfo(
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        UUID documentId
    ) {
        return this.eserviceTemplatesApi.getEServiceTemplateDocumentByIdWithHttpInfo(eServiceTemplateId, eServiceTemplateVersionId, documentId);
    }

    @Override
    public void updateDocument(
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        UUID documentId,
        UpdateEServiceTemplateVersionDocumentSeed updateEServiceTemplateVersionDocumentSeed
    ) {
        this.eserviceTemplatesApi.updateEServiceTemplateDocumentById(eServiceTemplateId, eServiceTemplateVersionId, documentId, updateEServiceTemplateVersionDocumentSeed);
    }

    @Override
    public ResponseEntity<Void> updateDocumentWithHttpInfo(
        UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId,
        UUID documentId,
        UpdateEServiceTemplateVersionDocumentSeed updateEServiceTemplateVersionDocumentSeed
    ) {
        return this.eserviceTemplatesApi.updateEServiceTemplateDocumentByIdWithHttpInfo(eServiceTemplateId, eServiceTemplateVersionId, documentId, updateEServiceTemplateVersionDocumentSeed);
    }

    @Override
    public void deleteDocument(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId, UUID documentId) {
        this.eserviceTemplatesApi.deleteEServiceTemplateDocumentById(eServiceTemplateId, eServiceTemplateVersionId, documentId);
    }

    @Override
    public ResponseEntity<Void> deleteDocumentWithHttpInfo(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId, UUID documentId) {
        return this.eserviceTemplatesApi.deleteEServiceTemplateDocumentByIdWithHttpInfo(eServiceTemplateId, eServiceTemplateVersionId, documentId);
    }

    @Override
    public void updateEServiceTemplateName(UUID eServiceTemplateId,
        EServiceTemplateNameUpdateSeed eserviceTemplateNameUpdateSeed) {
        this.eserviceTemplatesApi.updateEServiceTemplateName(eServiceTemplateId, eserviceTemplateNameUpdateSeed);
    }

    @Override
    public ResponseEntity<Void> updateEServiceTemplateNameWithHttpInfo(UUID eServiceTemplateId,
        EServiceTemplateNameUpdateSeed eserviceTemplateNameUpdateSeed) {
        return this.eserviceTemplatesApi.updateEServiceTemplateNameWithHttpInfo(eServiceTemplateId, eserviceTemplateNameUpdateSeed);
    }

    @Override
    public void updateEServiceIntendedTarget(UUID eServiceTemplateId,
        EServiceTemplateIntendedTargetUpdateSeed seed) {
        this.eserviceTemplatesApi.updateEServiceTemplateIntendedTarget(eServiceTemplateId, seed);
    }

    @Override
    public ResponseEntity<Void> updateEServiceIntendedTargetWithHttpInfo(
        UUID eServiceTemplateId,
        EServiceTemplateIntendedTargetUpdateSeed seed) {
        return this.eserviceTemplatesApi.updateEServiceTemplateIntendedTargetWithHttpInfo(eServiceTemplateId, seed);
    }

    @Override
    public void updateEServiceTemplateDescription(UUID eServiceTemplateId,
        EServiceTemplateDescriptionUpdateSeed seed) {
        this.eserviceTemplatesApi.updateEServiceTemplateDescription(eServiceTemplateId, seed);
    }

    @Override
    public ResponseEntity<Void> updateEServiceTemplateDescriptionWithHttpInfo(
        UUID eServiceTemplateId,
        EServiceTemplateDescriptionUpdateSeed seed) {
        return this.eserviceTemplatesApi.updateEServiceTemplateDescriptionWithHttpInfo(eServiceTemplateId, seed);
    }

    @Override
    public void updateEServiceTemplateVersionQuotas(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId, EServiceTemplateVersionQuotasUpdateSeed seed) {
        this.eserviceTemplatesApi.updateTemplateVersionQuotas(eServiceTemplateId, eServiceTemplateVersionId, seed);
    }

    @Override
    public ResponseEntity<Void> updateEServiceTemplateVersionQuotasWithHttpInfo(
        UUID eServiceTemplateId, UUID eServiceTemplateVersionId,
        EServiceTemplateVersionQuotasUpdateSeed seed) {
        return this.eserviceTemplatesApi.updateTemplateVersionQuotasWithHttpInfo(eServiceTemplateId, eServiceTemplateVersionId, seed);
    }

    @Override
    public void updateEServiceTemplateVersionAttributes(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId, DescriptorAttributesSeed seed) {
        this.eserviceTemplatesApi.updateEServiceTemplateVersionAttributes(eServiceTemplateId, eServiceTemplateVersionId, seed);
    }

    @Override
    public ResponseEntity<Void> updateEServiceTemplateVersionAttributesWithHttpInfo(
        UUID eServiceTemplateId, UUID eServiceTemplateVersionId,
        DescriptorAttributesSeed seed) {
        return this.eserviceTemplatesApi.updateEServiceTemplateVersionAttributesWithHttpInfo(eServiceTemplateId, eServiceTemplateVersionId, seed);
    }

    // DEV. NOTE: si cambia naming convention omettendo il suffisso "withHttpInfo" rendendolo implicito da qui in avanti
    @Override
    public ResponseEntity<CatalogEServiceTemplates> getEServiceTemplatesCatalog() {
        return this.getEServiceTemplatesCatalog(0, 50, null, null);
    }

    @Override
    public ResponseEntity<ProducerEServiceTemplates> getCreatorEServiceTemplates() {
        return this.eserviceTemplatesApi.getCreatorEServiceTemplatesWithHttpInfo(0, 50, null);
    }

    @Override
    public ResponseEntity<ProducerEServiceTemplates> getCreatorEServiceTemplates(
        Integer offset,
        Integer limit,
        String q) {
        return this.eserviceTemplatesApi.getCreatorEServiceTemplatesWithHttpInfo(offset, limit, q);
    }

    @Override
    public ResponseEntity<CompactOrganizations> getEServiceTemplateCreators() {
        return this.getEServiceTemplateCreators(0, 50, null);
    }

    @Override
    public ResponseEntity<CompactOrganizations> getEServiceTemplateCreators(
        Integer offset,
        Integer limit,
        String q) {
        return this.eserviceTemplatesApi.getEServiceTemplateCreatorsWithHttpInfo(offset, limit, q);
    }

    @Override
    public ResponseEntity<CatalogEServiceTemplates> getEServiceTemplatesCatalog(
        Integer offset, Integer limit, String q, List<UUID> creatorsIds) {
        /* DEV. NOTE 22/10/2025: il campo "personalData" è stato aggiunto a posteriori della
         * stesura di questo metodo. Essendo opzionale, lo si pone a null per mantenere compatibilità con i test esistenti. */
        return this.eserviceTemplatesApi.getEServiceTemplatesCatalogWithHttpInfo(offset, limit, null, q, creatorsIds);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.eserviceTemplatesApi.setApiClient(createApiClient(bearerToken));
    }
}
