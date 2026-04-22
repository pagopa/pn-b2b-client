package it.pagopa.interop.e_service_template.impl;

import it.pagopa.interop.M2MVersionsMapper;
import it.pagopa.interop.common.client.AbstractDPoPClient;
import it.pagopa.interop.common.rest_template.DpopRestTemplate;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.e_service_template.IM2MV3EServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Documents;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplate;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersionQuotasUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersionSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersions;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.api.EserviceTemplatesApi;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceTemplateDescriptionUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceTemplateDraftUpdateSeed;
import it.pagopa.interop.utils.ApiClientUtils;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@ToString
@EqualsAndHashCode
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class M2MV3EServiceTemplateClientImpl extends AbstractDPoPClient implements IM2MV3EServiceTemplateClient {
    private final EserviceTemplatesApi eserviceTemplatesApi;
    private final it.pagopa.interop.generated.openapi.clients.bff.api.EserviceTemplatesApi bffEserviceTemplatesApi;
    private final String basePath;
    private final EServiceTemplateMainMapper mapper;
    private final M2MVersionsMapper vMapper;

    public M2MV3EServiceTemplateClientImpl(
        DpopRestTemplate restTemplate,
        InteropClientConfigs interopClientConfigs,
        EServiceTemplateMainMapper mapper,
        M2MVersionsMapper vMapper
    ) {
        super(restTemplate);
        this.basePath = interopClientConfigs.getM2mV3BaseUrl();
        this.eserviceTemplatesApi = new EserviceTemplatesApi(
            ApiClientUtils.createApiClient(restTemplate, basePath,
                Collections.emptyMap()));
        this.bffEserviceTemplatesApi = new it.pagopa.interop.generated.openapi.clients.bff.api.EserviceTemplatesApi(
            createBffApiClient("dummyBearer"));
        this.mapper = mapper;
        this.vMapper = vMapper;
    }

    private it.pagopa.interop.generated.openapi.clients.bff.ApiClient createBffApiClient(
        String bearerToken) {
        it.pagopa.interop.generated.openapi.clients.bff.ApiClient apiClient = new it.pagopa.interop.generated.openapi.clients.bff.ApiClient(
            super.getRestTemplate());
        apiClient.setBasePath(basePath);

        apiClient.setBearerToken(bearerToken);

        return apiClient;
    }

    @Override
    public EServiceTemplate getEserviceTemplate(UUID templateId) {
        return vMapper.mapToV2(eserviceTemplatesApi.getEServiceTemplate(templateId));
    }

    @Override
    public EServiceTemplateVersions getEserviceTemplateVersions(
        EserviceTemplateListRequest request) {
        return vMapper.mapToV2(eserviceTemplatesApi.getEServiceTemplateVersions(
            request.getTemplateId(),
            request.getOffset(),
            request.getLimit(),
            vMapper.mapToV3(request.getState()))
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
        return vMapper.mapToV2(
            eserviceTemplatesApi.getEServiceTemplateVersion(templateId, versionId));
    }

    @Override
    public EServiceTemplate createEServiceTemplate(EServiceTemplateSeed payload) {
        return vMapper.mapToV2(eserviceTemplatesApi.createEServiceTemplate(
                vMapper.mapToV3(payload)));
    }

    @Override
    public ResponseEntity<EServiceTemplateVersion> createEserviceTemplateVersion(
        UUID templateId,
        EServiceTemplateVersionCreationRequest request) {
        EServiceTemplateVersionSeed v2Seed = this.mapper.mapCreationRequestToSeed(
            request);
        return vMapper.map(
            eserviceTemplatesApi.createEServiceTemplateVersionWithHttpInfo(templateId,
                vMapper.mapToV3(v2Seed)),
            vMapper::mapToV2);
    }

    @Override
    public Documents getDocuments(UUID templateId, UUID versionId) {
        return vMapper.mapToV2(
            eserviceTemplatesApi.getEServiceTemplateVersionDocuments(templateId, versionId, 0, 30));
    }

    @Override
    public void unsuspend(UUID templateId, UUID versionId) {
        eserviceTemplatesApi.unsuspendEServiceTemplateVersion(templateId, versionId);
    }

    @Override
    public EServiceTemplate patchEServiceTemplate(UUID templateId,
        EServiceTemplatePatchRequest patchRequest) {
        return vMapper.mapToV2(eserviceTemplatesApi.updateDraftEServiceTemplate(templateId,
            new EServiceTemplateDraftUpdateSeed()
                .description(patchRequest.getDescription())
                .name(patchRequest.getName())
                .technology(vMapper.mapToV3(patchRequest.getTechnology()))
                .mode(vMapper.mapToV3(patchRequest.getMode()))
                .intendedTarget(patchRequest.getIntendedTarget())
                .isSignalHubEnabled(patchRequest.getIsSignalHubEnabled())
        ));
    }

    @Override
    public EServiceTemplate patchEServiceTemplateDescription(UUID templateId,
        EServiceTemplateDescriptionPatchRequest patchRequest) {
        return vMapper.mapToV2(eserviceTemplatesApi.updatePublishedEServiceTemplateDescription(templateId,
                new EServiceTemplateDescriptionUpdateSeed()
                        .description(patchRequest.getDescription())
        ));
    }

    @Override
    public EServiceTemplateVersion patchEServiceTemplateVersion(UUID templateId, UUID versionId,
        EServiceTemplateVersionPatchRequest patchRequest) {
        return vMapper.mapToV2(eserviceTemplatesApi.updateDraftEServiceTemplateVersion(
            templateId,
            versionId,
            vMapper.mapToV3(this.mapper.mapPatchRequestToSeed(patchRequest))
        ));
    }

    @Override
    public EServiceTemplateVersion patchEServiceTemplateVersionQuotas(UUID templateId,
        UUID versionId, EServiceTemplateVersionQuotasPatchRequest patchRequest) {
        return vMapper.mapToV2(eserviceTemplatesApi.updatePublishedEServiceTemplateVersionQuotas(
            templateId,
            versionId,
            vMapper.mapToV3(new EServiceTemplateVersionQuotasUpdateSeed()
                .dailyCallsPerConsumer(patchRequest.getDailyCallsPerConsumer())
                .voucherLifespan(patchRequest.getVoucherLifespan())
                .dailyCallsTotal(patchRequest.getDailyCallsTotal()))
        ));
    }

    @Override
    public void deleteEServiceTemplate(UUID templateId) {
        eserviceTemplatesApi.deleteEServiceTemplate(templateId);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.bffEserviceTemplatesApi.setApiClient(createBffApiClient(bearerToken));
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        this.eserviceTemplatesApi.setApiClient(
            ApiClientUtils.createApiClient(super.getRestTemplate(), basePath, headers));
    }
}
