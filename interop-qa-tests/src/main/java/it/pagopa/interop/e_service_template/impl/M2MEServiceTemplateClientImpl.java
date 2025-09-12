package it.pagopa.interop.e_service_template.impl;

import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.e_service_template.IM2MEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.api.EserviceTemplatesApi;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.*;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@ToString
@EqualsAndHashCode
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class M2MEServiceTemplateClientImpl implements IM2MEServiceTemplateClient {
    private final EserviceTemplatesApi eserviceTemplatesApi;
    private final it.pagopa.interop.generated.openapi.clients.bff.api.EserviceTemplatesApi bffEserviceTemplatesApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public M2MEServiceTemplateClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getM2mBaseUrl();
        this.eserviceTemplatesApi = new EserviceTemplatesApi(createApiClient("dummyBearer"));
        this.bffEserviceTemplatesApi = new it.pagopa.interop.generated.openapi.clients.bff.api.EserviceTemplatesApi(createBffApiClient("dummyBearer"));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);

        apiClient.setBearerToken(bearerToken);

        return apiClient;
    }

    private it.pagopa.interop.generated.openapi.clients.bff.ApiClient createBffApiClient(String bearerToken) {
        it.pagopa.interop.generated.openapi.clients.bff.ApiClient apiClient = new it.pagopa.interop.generated.openapi.clients.bff.ApiClient(restTemplate);
        apiClient.setBasePath(basePath);

        apiClient.setBearerToken(bearerToken);

        return apiClient;
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.eserviceTemplatesApi.setApiClient(createApiClient(bearerToken));
        this.bffEserviceTemplatesApi.setApiClient(createBffApiClient(bearerToken));
    }

    @Override
    public EServiceTemplate getEserviceTemplate(UUID templateId) {
        return eserviceTemplatesApi.getEServiceTemplate(templateId);
    }

    @Override
    public EServiceTemplateVersions getEserviceTemplateVersions(EserviceTemplateListRequest request) {
        return eserviceTemplatesApi.getEServiceTemplateVersions(
                request.getTemplateId(),
                request.getOffset(),
                request.getLimit(),
                request.getState()
        );
    }

    @Override
    public EServiceTemplateVersion getEserviceTemplateVersion(UUID templateId, UUID versionId) {
        return eserviceTemplatesApi.getEServiceTemplateVersion(templateId, versionId);
    }

    @Override
    public CreatedEServiceTemplateVersion createEserviceTemplate(EServiceTemplateSeed payload) {
        return bffEserviceTemplatesApi.createEServiceTemplate(payload);
    }

    @Override
    public Documents getDocuments(UUID templateId, UUID versionId) {
        return null; // TODO 08/09/2025 sostituire con implementazione reale non appena disponibile
    }

    @Override
    public void unsuspend(UUID templateId, UUID versionId) {
        // TODO 09/09/2025 sostituire con implementazione reale non appena disponibile
    }

    @Override
    public EServiceTemplate patchEServiceTemplate(UUID templateId,
        EServiceTemplatePatchRequest patchRequest) {
        return null; // TODO 10/09/2025 sostituire con implementazione reale non appena disponibile
    }

    @Override
    public EServiceTemplateVersion patchEServiceTemplateVersion(UUID templateId, UUID versionId,
        EServiceTemplateVersionPatchRequest patchRequest) {
        return null; // TODO 10/09/2025 sostituire con implementazione reale non appena disponibile
    }
}
