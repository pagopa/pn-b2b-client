package it.pagopa.interop.eservice_template.impl;

import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.eservice.service.IM2MEserviceClient;
import it.pagopa.interop.eservice_template.IM2MEserviceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.api.EserviceTemplatesApi;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.api.EservicesApi;
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
public class M2MEserviceTemplateClientImpl implements IM2MEserviceTemplateClient {
    private final EserviceTemplatesApi eserviceTemplatesApi;
    private final it.pagopa.interop.generated.openapi.clients.bff.api.EserviceTemplatesApi bffEserviceTemplatesApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public M2MEserviceTemplateClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
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
}
