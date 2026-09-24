package it.pagopa.interop.e_service_template.impl;

import static it.pagopa.interop.utils.ApiClientUtils.V3_UNSUPPORTED_BEARER_MSG;

import it.pagopa.interop.M2MVersionsMapper;
import it.pagopa.interop.common.client.AbstractDPoPClient;
import it.pagopa.interop.common.rest_template.DpopRestTemplate;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.e_service_template.IM2MV3EServiceTemplateAttributeClient;
import it.pagopa.interop.eservice.service.EServiceAttribute;
import it.pagopa.interop.eservice.service.IM2MEserviceDescriptorClient.EserviceDescriptorsListRequest;
import it.pagopa.interop.eservice.service.mapper.EServiceAttributeMapper;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DeclaredAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersionCertifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersionCertifiedAttributes;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersionCertifiedAttributesGroup;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersionDeclaredAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersionDeclaredAttributes;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersionDeclaredAttributesGroup;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersionVerifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersionVerifiedAttributes;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersionVerifiedAttributesGroup;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.VerifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.api.EserviceTemplatesApi;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceTemplateVersionAttributesGroupSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceTemplateVersionCertifiedDiscreteAttributes;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceTemplateVersionCertifiedDiscreteAttributesGroup;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceTemplateVersionCertifiedDiscreteAttributesGroupSeed;
import it.pagopa.interop.utils.ApiClientUtils;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@ToString
@EqualsAndHashCode
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class M2MV3EserviceTemplateAttributeClientImpl extends AbstractDPoPClient implements IM2MV3EServiceTemplateAttributeClient {
    private final EserviceTemplatesApi templatesApi;
    private final String basePath;
    private final EServiceAttributeMapper attributeMapper;

    private final EserviceDescriptorsListRequest defaultDescriptorListRequest;
    private final M2MVersionsMapper vMapper;

    public M2MV3EserviceTemplateAttributeClientImpl(
        DpopRestTemplate restTemplate,
        InteropClientConfigs interopClientConfigs,
        EServiceAttributeMapper mapper,
        M2MVersionsMapper vMapper) {
        super(restTemplate);
        this.basePath = interopClientConfigs.getM2mV3BaseUrl();
        this.templatesApi = new EserviceTemplatesApi(ApiClientUtils.createApiClient(restTemplate, basePath,
            Collections.emptyMap()));

        this.defaultDescriptorListRequest = EserviceDescriptorsListRequest.builder()
                .limit(30)
                .offset(0)
                .eserviceId(UUID.randomUUID())
                .build();
        this.attributeMapper = mapper;
        this.vMapper = vMapper;
    }

    @Override
    public void addCertifiedAttributes(
        UUID templateId,
        UUID versionId,
        int groupId,
        List<UUID> attributes
    ) {
        this.templatesApi.assignEServiceTemplateVersionCertifiedAttributesToGroup(
            templateId, 
            versionId, 
            groupId,
            new EServiceTemplateVersionAttributesGroupSeed().attributeIds(attributes));
    }

    @Override
    public List<EServiceAttribute<CertifiedAttribute>> createCertifiedAttributesGroup(
        UUID templateId,
        UUID versionId,
        List<UUID> attributes
    ) {
        EServiceTemplateVersionCertifiedAttributesGroup group = vMapper.mapToV2(this.templatesApi.createEServiceTemplateVersionCertifiedAttributesGroup(
            templateId,
            versionId,
            new EServiceTemplateVersionAttributesGroupSeed().attributeIds(attributes)
        ));

        return group.getAttributes().stream()
            .map(this.attributeMapper::map)
            .collect(Collectors.toList());
    }

    @Override
    public List<EServiceAttribute<CertifiedAttribute>> getCertifiedAttributes(
        UUID templateId, UUID versionId) {
        EServiceTemplateVersionCertifiedAttributes attributes = vMapper.mapToV2(this.templatesApi.getEServiceTemplateVersionCertifiedAttributes(
            templateId, versionId, 0, 30));

        return attributes.getResults().stream()
            .map(this::mapToEServiceAttribute)
            .toList();
    }

    @Override
    public void deleteCertifiedAttribute(UUID templateId, UUID versionId, int groupIndex,
        UUID attributeId) {
        this.templatesApi.deleteEServiceTemplateVersionCertifiedAttributeFromGroup(templateId, versionId, groupIndex, attributeId);
    }

    private EServiceAttribute<CertifiedAttribute> mapToEServiceAttribute(
        EServiceTemplateVersionCertifiedAttribute attribute) {
        return EServiceAttribute.<CertifiedAttribute>builder()
            .attribute(attribute.getAttribute())
            .groupIndex(attribute.getGroupIndex())
            .build();
    }

    @Override
    public void addDeclaredAttributes(
        UUID templateId,
        UUID versionId,
        int groupId,
        List<UUID> attributes
    ) {
        this.templatesApi.assignEServiceTemplateVersionDeclaredAttributesToGroup(
            templateId,
            versionId,
            groupId,
            new EServiceTemplateVersionAttributesGroupSeed().attributeIds(attributes));
    }

    @Override
    public List<EServiceAttribute<DeclaredAttribute>> createDeclaredAttributesGroup(
        UUID templateId,
        UUID versionId,
        List<UUID> attributes
    ) {
        EServiceTemplateVersionDeclaredAttributesGroup group = vMapper.mapToV2(this.templatesApi.createEServiceTemplateVersionDeclaredAttributesGroup(
            templateId,
            versionId,
            new EServiceTemplateVersionAttributesGroupSeed().attributeIds(attributes)
        ));

        return group.getAttributes().stream()
            .map(this.attributeMapper::map)
            .collect(Collectors.toList());
    }

    @Override
    public List<EServiceAttribute<DeclaredAttribute>> getDeclaredAttributes(
        UUID templateId, UUID versionId) {
        EServiceTemplateVersionDeclaredAttributes attributes = vMapper.mapToV2(this.templatesApi.getEServiceTemplateVersionDeclaredAttributes(
            templateId, versionId, 0, 30));

        return attributes.getResults().stream()
            .map(this::mapToEServiceAttribute)
            .toList();
    }

    @Override
    public void deleteDeclaredAttribute(UUID templateId, UUID versionId, int groupIndex,
        UUID attributeId) {
        this.templatesApi.deleteEServiceTemplateVersionDeclaredAttributeFromGroup(templateId, versionId, groupIndex, attributeId);
    }

    private EServiceAttribute<DeclaredAttribute> mapToEServiceAttribute(
        EServiceTemplateVersionDeclaredAttribute attribute) {
        return EServiceAttribute.<DeclaredAttribute>builder()
            .attribute(attribute.getAttribute())
            .groupIndex(attribute.getGroupIndex())
            .build();
    }

    @Override
    public void addVerifiedAttributes(
            UUID templateId,
            UUID versionId,
            int groupId,
            List<UUID> attributes
    ) {
        this.templatesApi.assignEServiceTemplateVersionVerifiedAttributesToGroup(
            templateId,
            versionId,
            groupId,
            new EServiceTemplateVersionAttributesGroupSeed().attributeIds(attributes));
    }

    @Override
    public List<EServiceAttribute<VerifiedAttribute>> createVerifiedAttributesGroup(
            UUID templateId,
            UUID versionId,
            List<UUID> attributes
    ) {
        EServiceTemplateVersionVerifiedAttributesGroup group = vMapper.mapToV2(this.templatesApi.createEServiceTemplateVersionVerifiedAttributesGroup(
            templateId,
            versionId,
            new EServiceTemplateVersionAttributesGroupSeed().attributeIds(attributes)
        ));

        return group.getAttributes().stream()
            .map(this.attributeMapper::map)
            .collect(Collectors.toList());
    }

    @Override
    public List<EServiceAttribute<VerifiedAttribute>> getVerifiedAttributes(
            UUID templateId, UUID versionId) {
        EServiceTemplateVersionVerifiedAttributes attributes = vMapper.mapToV2(this.templatesApi.getEServiceTemplateVersionVerifiedAttributes(
                templateId, versionId, 0, 30));

        return attributes.getResults().stream()
                .map(this::mapToEServiceAttribute)
                .toList();
    }

    @Override
    public void deleteVerifiedAttribute(UUID templateId, UUID versionId, int groupIndex,
                                         UUID attributeId) {
        this.templatesApi.deleteEServiceTemplateVersionVerifiedAttributeFromGroup(templateId, versionId, groupIndex, attributeId);
    }

    private EServiceAttribute<VerifiedAttribute> mapToEServiceAttribute(
            EServiceTemplateVersionVerifiedAttribute attribute) {
        return EServiceAttribute.<VerifiedAttribute>builder()
                .attribute(attribute.getAttribute())
                .groupIndex(attribute.getGroupIndex())
                .build();
    }

    // Queste API sono disponibili solo per la versione 3

    @Override
    public EServiceTemplateVersionCertifiedDiscreteAttributes getEServiceTemplateVersionCertifiedDiscreteAttributes(UUID templateId, UUID versionId, Integer offset, Integer limit) {
        return this.templatesApi.getEServiceTemplateVersionCertifiedDiscreteAttributes(templateId, versionId, offset, limit);
    }

    @Override
    public EServiceTemplateVersionCertifiedDiscreteAttributesGroup createEServiceTemplateVersionCertifiedDiscreteAttributesGroup(UUID templateId, UUID versionId, EServiceTemplateVersionCertifiedDiscreteAttributesGroupSeed eserviceTemplateVersionCertifiedDiscreteAttributesGroupSeed) {
        return this.templatesApi.createEServiceTemplateVersionCertifiedDiscreteAttributesGroup(templateId, versionId, eserviceTemplateVersionCertifiedDiscreteAttributesGroupSeed);
    }

    @Override
    public Object assignEServiceTemplateVersionCertifiedDiscreteAttributesToGroup(UUID templateId, UUID versionId, Integer groupIndex, EServiceTemplateVersionCertifiedDiscreteAttributesGroupSeed eserviceTemplateVersionCertifiedDiscreteAttributesGroupSeed) {
        return this.templatesApi.assignEServiceTemplateVersionCertifiedDiscreteAttributesToGroup(templateId, versionId, groupIndex, eserviceTemplateVersionCertifiedDiscreteAttributesGroupSeed);
    }

    @Override
    public Object deleteEServiceTemplateVersionCertifiedDiscreteAttributeFromGroup(UUID templateId, UUID versionId, Integer groupIndex, UUID attributeId) {
        return this.templatesApi.deleteEServiceTemplateVersionCertifiedDiscreteAttributeFromGroup(templateId, versionId, groupIndex, attributeId);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        throw new UnsupportedOperationException(V3_UNSUPPORTED_BEARER_MSG);
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        this.templatesApi.setApiClient(
            ApiClientUtils.createApiClient(super.getRestTemplate(), basePath, headers));
    }
}
