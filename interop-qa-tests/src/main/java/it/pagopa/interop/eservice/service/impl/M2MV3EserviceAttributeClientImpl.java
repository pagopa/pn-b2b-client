package it.pagopa.interop.eservice.service.impl;

import static it.pagopa.interop.utils.ApiClientUtils.V3_UNSUPPORTED_BEARER_MSG;

import it.pagopa.interop.M2MVersionsMapper;
import it.pagopa.interop.common.client.AbstractDPoPClient;
import it.pagopa.interop.common.rest_template.DpopRestTemplate;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.eservice.service.EServiceAttribute;
import it.pagopa.interop.eservice.service.IM2MEserviceDescriptorClient.EserviceDescriptorsListRequest;
import it.pagopa.interop.eservice.service.IM2MV3EServiceAttributeClient;
import it.pagopa.interop.eservice.service.mapper.EServiceAttributeMapper;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DeclaredAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorCertifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorCertifiedAttributes;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorDeclaredAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorVerifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.VerifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.*;
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
public class M2MV3EserviceAttributeClientImpl extends AbstractDPoPClient implements IM2MV3EServiceAttributeClient {
    private final EservicesApi eservicesApi;
    private final String basePath;
    private final EServiceAttributeMapper attributeMapper;

    private final EserviceDescriptorsListRequest defaultDescriptorListRequest;
    private final M2MVersionsMapper vMapper;

    public M2MV3EserviceAttributeClientImpl(
        DpopRestTemplate restTemplate,
        InteropClientConfigs interopClientConfigs,
        EServiceAttributeMapper mapper,
        M2MVersionsMapper vMapper
    ) {
        super(restTemplate);
        this.basePath = interopClientConfigs.getM2mV3BaseUrl();
        this.eservicesApi = new EservicesApi(
            ApiClientUtils.createApiClient(restTemplate, basePath,
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
        UUID eServiceId,
        UUID descriptorId,
        int groupId,
        List<UUID> attributes
    ) {
        this.eservicesApi.assignEServiceDescriptorCertifiedAttributesToGroup(
            eServiceId,
            descriptorId,
            groupId,
            new EServiceDescriptorAttributesGroupSeed().attributeIds(attributes)
        );
    }

    @Override
    public List<EServiceAttribute<CertifiedAttribute>> createCertifiedAttributesGroup(
        UUID eServiceId,
        UUID descriptorId,
        List<UUID> attributes
    ) {
        EServiceDescriptorCertifiedAttributesGroup group = this.eservicesApi.createEServiceDescriptorCertifiedAttributesGroup(
            eServiceId,
            descriptorId,
            new EServiceDescriptorAttributesGroupSeed().attributeIds(attributes)
        );

        return group.getAttributes().stream()
            .map(vMapper::mapToV2)
            .map(this.attributeMapper::map)
            .collect(Collectors.toList());
    }

    @Override
    public List<EServiceAttribute<CertifiedAttribute>> getCertifiedAttributes(
        UUID eServiceId, UUID descriptorId) {
        EServiceDescriptorCertifiedAttributes attributes = vMapper.mapToV2(this.eservicesApi.getEServiceDescriptorCertifiedAttributes(
            eServiceId, descriptorId, 0, 30));

        return attributes.getResults().stream()
            .map(this::mapToEServiceAttribute)
            .toList();
    }

    @Override
    public void deleteCertifiedAttribute(UUID eServiceId, UUID descriptorId, int groupIndex,
        UUID attributeId) {
        this.eservicesApi.deleteEServiceDescriptorCertifiedAttributeFromGroup(eServiceId, descriptorId, groupIndex, attributeId);
    }

    private EServiceAttribute<CertifiedAttribute> mapToEServiceAttribute(EServiceDescriptorCertifiedAttribute attribute) {
        return EServiceAttribute.<CertifiedAttribute>builder()
            .attribute(attribute.getAttribute())
            .groupIndex(attribute.getGroupIndex())
            .build();
    }

    @Override
    public EServiceDescriptorCertifiedDiscreteAttributesGroup createEServiceDescriptorCertifiedDiscreteAttributesGroup(UUID eserviceId, UUID descriptorId, EServiceDescriptorCertifiedDiscreteAttributesGroupSeed eserviceDescriptorCertifiedDiscreteAttributesGroupSeed) {
        return this.eservicesApi.createEServiceDescriptorCertifiedDiscreteAttributesGroup(eserviceId, descriptorId, eserviceDescriptorCertifiedDiscreteAttributesGroupSeed);
    }

    @Override
    public EServiceDescriptorCertifiedDiscreteAttributes getEServiceDescriptorCertifiedDiscreteAttributes(UUID eserviceId, UUID descriptorId, Integer offset, Integer limit) {
        return this.eservicesApi.getEServiceDescriptorCertifiedDiscreteAttributes(eserviceId, descriptorId, offset, limit);
    }

    @Override
    public Object assignEServiceDescriptorCertifiedDiscreteAttributesToGroup(UUID eserviceId, UUID descriptorId, Integer groupIndex, EServiceDescriptorCertifiedDiscreteAttributesGroupSeed eserviceDescriptorCertifiedDiscreteAttributesGroupSeed) {
        return this.eservicesApi.assignEServiceDescriptorCertifiedDiscreteAttributesToGroup(eserviceId, descriptorId, groupIndex, eserviceDescriptorCertifiedDiscreteAttributesGroupSeed);
    }

    @Override
    public Object deleteEServiceDescriptorCertifiedDiscreteAttributeFromGroup(UUID eserviceId, UUID descriptorId, Integer groupIndex, UUID attributeId) {
        return this.eservicesApi.deleteEServiceDescriptorCertifiedDiscreteAttributeFromGroup(eserviceId, descriptorId, groupIndex, attributeId);
    }

    @Override
    public void addDeclaredAttributes(
        UUID eServiceId,
        UUID descriptorId,
        int groupId,
        List<UUID> attributes
    ) {
        this.eservicesApi.assignEServiceDescriptorDeclaredAttributesToGroup(
            eServiceId,
            descriptorId,
            groupId,
            new EServiceDescriptorAttributesGroupSeed().attributeIds(attributes)
        );
    }

    @Override
    public List<EServiceAttribute<DeclaredAttribute>> createDeclaredAttributesGroup(
        UUID eServiceId,
        UUID descriptorId,
        List<UUID> attributes
    ) {
        EServiceDescriptorDeclaredAttributesGroup group = this.eservicesApi.createEServiceDescriptorDeclaredAttributesGroup(
            eServiceId,
            descriptorId,
            new EServiceDescriptorAttributesGroupSeed().attributeIds(attributes)
        );

        return group.getAttributes().stream()
            .map(vMapper::mapToV2)
            .map(this.attributeMapper::map)
            .collect(Collectors.toList());
    }

    @Override
    public List<EServiceAttribute<DeclaredAttribute>> getDeclaredAttributes(
        UUID eServiceId, UUID descriptorId) {
        EServiceDescriptorDeclaredAttributes attributes = this.eservicesApi.getEServiceDescriptorDeclaredAttributes(
            eServiceId, descriptorId, 0, 30);

        return attributes.getResults().stream()
            .map(vMapper::mapToV2)
            .map(this::mapToEServiceAttribute)
            .toList();
    }

    @Override
    public void deleteDeclaredAttribute(UUID eServiceId, UUID descriptorId, int groupIndex,
        UUID attributeId) {
        this.eservicesApi.deleteEServiceDescriptorDeclaredAttributeFromGroup(eServiceId, descriptorId, groupIndex, attributeId);
    }

    private EServiceAttribute<DeclaredAttribute> mapToEServiceAttribute(
        EServiceDescriptorDeclaredAttribute attribute) {
        return EServiceAttribute.<DeclaredAttribute>builder()
            .attribute(attribute.getAttribute())
            .groupIndex(attribute.getGroupIndex())
            .build();
    }

    @Override
    public void addVerifiedAttributes(
            UUID eServiceId,
            UUID descriptorId,
            int groupId,
            List<UUID> attributes
    ) {
        this.eservicesApi.assignEServiceDescriptorVerifiedAttributesToGroup(
            eServiceId,
            descriptorId,
            groupId,
            new EServiceDescriptorAttributesGroupSeed().attributeIds(attributes)
        );
    }

    @Override
    public List<EServiceAttribute<VerifiedAttribute>> createVerifiedAttributesGroup(
            UUID eServiceId,
            UUID descriptorId,
            List<UUID> attributes
    ) {
        EServiceDescriptorVerifiedAttributesGroup group = this.eservicesApi.createEServiceDescriptorVerifiedAttributesGroup(
            eServiceId,
            descriptorId,
            new EServiceDescriptorAttributesGroupSeed().attributeIds(attributes)
        );

        return group.getAttributes().stream()
            .map(vMapper::mapToV2)
            .map(this.attributeMapper::map)
            .collect(Collectors.toList());
    }

    @Override
    public List<EServiceAttribute<VerifiedAttribute>> getVerifiedAttributes(
            UUID eServiceId, UUID descriptorId) {
        EServiceDescriptorVerifiedAttributes attributes = this.eservicesApi.getEServiceDescriptorVerifiedAttributes(
                eServiceId, descriptorId, 0, 30);

        return attributes.getResults().stream()
                .map(vMapper::mapToV2)
                .map(this::mapToEServiceAttribute)
                .toList();
    }

    @Override
    public void deleteVerifiedAttribute(UUID eServiceId, UUID descriptorId, int groupIndex,
                                        UUID attributeId) {
        this.eservicesApi.deleteEServiceDescriptorVerifiedAttributeFromGroup(eServiceId, descriptorId, groupIndex, attributeId);
    }

    private EServiceAttribute<VerifiedAttribute> mapToEServiceAttribute(
            EServiceDescriptorVerifiedAttribute attribute) {
        return EServiceAttribute.<VerifiedAttribute>builder()
                .attribute(attribute.getAttribute())
                .groupIndex(attribute.getGroupIndex())
                .build();
    }
//    ---------------------------------------

    @Override
    public void setBearerToken(String bearerToken) {
        throw new UnsupportedOperationException(V3_UNSUPPORTED_BEARER_MSG);
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        this.eservicesApi.setApiClient(
            ApiClientUtils.createApiClient(super.getRestTemplate(), basePath, headers));
    }

}