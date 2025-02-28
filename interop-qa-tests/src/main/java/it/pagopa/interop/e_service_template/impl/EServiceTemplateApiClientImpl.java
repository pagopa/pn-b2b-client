package it.pagopa.interop.e_service_template.impl;

import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EserviceTemplatesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateSeed;
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
    public void setBearerToken(String bearerToken) {
        this.eserviceTemplatesApi.setApiClient(createApiClient(bearerToken));
    }
}
