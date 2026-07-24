package it.pagopa.interop.e_service_template.impl;

import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.e_service_template.IM2MEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.api.EserviceTemplatesApi;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.*;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@ToString
@EqualsAndHashCode
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Primary
public class M2MEServiceTemplateClientImpl implements IM2MEServiceTemplateClient {
    private final EserviceTemplatesApi eserviceTemplatesApi;
    private final it.pagopa.interop.generated.openapi.clients.bff.api.EserviceTemplatesApi bffEserviceTemplatesApi;
    private final RestTemplate restTemplate;
    private final String basePath;
    private final EServiceTemplateMainMapper mapper;

    public M2MEServiceTemplateClientImpl(
        RestTemplate restTemplate,
        InteropClientConfigs interopClientConfigs,
        EServiceTemplateMainMapper mapper
    ) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getM2mBaseUrl();
        this.eserviceTemplatesApi = new EserviceTemplatesApi(createApiClient("dummyBearer"));
        this.bffEserviceTemplatesApi = new it.pagopa.interop.generated.openapi.clients.bff.api.EserviceTemplatesApi(createBffApiClient("dummyBearer"));
        this.mapper = mapper;
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
    public EServiceTemplateVersions getEserviceTemplateVersions(UUID templateId) {
        return this.getEserviceTemplateVersions(EserviceTemplateListRequest.builder()
            .templateId(templateId)
            .offset(0)
            .limit(30)
            .build());
    }

    @Override
    public EServiceTemplateVersion getEserviceTemplateVersion(UUID templateId, UUID versionId) {
        return eserviceTemplatesApi.getEServiceTemplateVersion(templateId, versionId);
    }

    @Override
    public EServiceTemplate createEServiceTemplate(EServiceTemplateSeed payload) {
        return eserviceTemplatesApi.createEServiceTemplate(payload);
    }

    @Override
    public ResponseEntity<EServiceTemplateVersion> createEserviceTemplateVersion(
        UUID templateId,
        EServiceTemplateVersionCreationRequest request) {
        return eserviceTemplatesApi.createEServiceTemplateVersionWithHttpInfo(
            templateId,
            this.mapper.mapCreationRequestToSeed(request));
    }

    @Override
    public Document uploadDocument(UUID templateId, UUID versionId, Resource file, String prettyName) {
        return eserviceTemplatesApi.uploadEServiceTemplateVersionDocument(templateId, versionId, file, prettyName);
    }

    @Override
    public FileDownloadMultipart downloadDocument(UUID templateId, UUID versionId, UUID documentId) {
        return eserviceTemplatesApi.downloadEServiceTemplateVersionDocument(templateId, versionId, documentId);
    }

    @Override
    public Documents getDocuments(UUID templateId, UUID versionId) {
        return eserviceTemplatesApi.getEServiceTemplateVersionDocuments(templateId, versionId, 0, 30);
    }

    @Override
    public void unsuspend(UUID templateId, UUID versionId) {
        eserviceTemplatesApi.unsuspendEServiceTemplateVersion(templateId, versionId);
    }

    @Override
    public EServiceTemplate patchEServiceTemplate(UUID templateId,
        EServiceTemplatePatchRequest patchRequest) {
        return eserviceTemplatesApi.updateDraftEServiceTemplate(templateId, new EServiceTemplateDraftUpdateSeed()
            .description(patchRequest.getDescription())
            .name(patchRequest.getName())
            .technology(patchRequest.getTechnology())
            .mode(patchRequest.getMode())
            .intendedTarget(patchRequest.getIntendedTarget())
            .isSignalHubEnabled(patchRequest.getIsSignalHubEnabled())
        );
    }

    @Override
    public EServiceTemplate patchEServiceTemplateDescription(UUID templateId,
        EServiceTemplateDescriptionPatchRequest patchRequest) {
        return eserviceTemplatesApi.updatePublishedEServiceTemplateDescription(templateId, new EServiceTemplateDescriptionUpdateSeed()
            .description(patchRequest.getDescription())
        );
    }

    @Override
    public EServiceTemplateVersion patchEServiceTemplateVersion(UUID templateId, UUID versionId,
        EServiceTemplateVersionPatchRequest patchRequest) {
        return eserviceTemplatesApi.updateDraftEServiceTemplateVersion(
            templateId,
            versionId,
            this.mapper.mapPatchRequestToSeed(patchRequest)
        );
    }

    @Override
    public EServiceTemplateVersion patchEServiceTemplateVersionQuotas(UUID templateId,
        UUID versionId, EServiceTemplateVersionQuotasPatchRequest patchRequest) {
        return eserviceTemplatesApi.updatePublishedEServiceTemplateVersionQuotas(
            templateId,
            versionId,
            new EServiceTemplateVersionQuotasUpdateSeed()
                .dailyCallsPerConsumer(patchRequest.getDailyCallsPerConsumer())
                .voucherLifespan(patchRequest.getVoucherLifespan())
                .dailyCallsTotal(patchRequest.getDailyCallsTotal())
        );
    }

    @Override
    public void deleteEServiceTemplate(UUID templateId) {
        eserviceTemplatesApi.deleteEServiceTemplate(templateId);
    }
}
