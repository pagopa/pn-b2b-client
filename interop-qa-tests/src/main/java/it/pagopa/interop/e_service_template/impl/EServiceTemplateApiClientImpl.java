package it.pagopa.interop.e_service_template.impl;

import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EserviceTemplatesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysisSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateVersionSeed;
import java.util.UUID;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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
        // TODO 28/02/2025: il template id, a differenza di altre api, se lo aspetta in semplice formato stringa e non UUID, va segnalato
        eserviceTemplatesApi.updateEServiceTemplate(xCorrelationId, eServiceTemplateId.toString(), updateEServiceTemplateSeed);
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

            // TODO 28/02/2025: gli id, a differenza di altre api, se li aspetta in semplice formato stringa e non UUID, va segnalato
            eServiceTemplateId.toString(),
            eServiceTemplateVersionId.toString(),

            seed);
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
    public void suspendEServiceTemplate(String xCorrelationId, UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId) {
        eserviceTemplatesApi.suspendEServiceTemplateVersion(
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
    public ResponseEntity<EServiceTemplateDetails> getEServiceTemplateWithHttpInfo(
        String xCorrelationId,
        UUID eServiceTemplateId) {
        // TODO 04/03/2025: gli id, a differenza di altre api, se li aspetta in semplice formato stringa e non UUID, va segnalato
        return eserviceTemplatesApi.getEServiceTemplateWithHttpInfo(xCorrelationId, eServiceTemplateId.toString());
    }

    @Override
    public EServiceTemplateDetails getEServiceTemplate(String xCorrelationId,
        UUID eServiceTemplateId) {
        // TODO 04/03/2025: gli id, a differenza di altre api, se li aspetta in semplice formato stringa e non UUID, va segnalato
        return eserviceTemplatesApi.getEServiceTemplate(xCorrelationId, eServiceTemplateId.toString());
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
    public void setBearerToken(String bearerToken) {
        this.eserviceTemplatesApi.setApiClient(createApiClient(bearerToken));
    }
}
