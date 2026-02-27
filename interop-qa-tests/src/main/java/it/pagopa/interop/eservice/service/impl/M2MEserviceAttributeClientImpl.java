package it.pagopa.interop.eservice.service.impl;

import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.eservice.service.EServiceAttribute;
import it.pagopa.interop.eservice.service.IM2MEServiceAttributeClient;
import it.pagopa.interop.eservice.service.IM2MEserviceDescriptorClient.EserviceDescriptorsListRequest;
import it.pagopa.interop.eservice.service.mapper.EServiceAttributeMapper;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DeclaredAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorAttributesGroupSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorCertifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorCertifiedAttributes;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorCertifiedAttributesGroup;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorDeclaredAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorDeclaredAttributes;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorDeclaredAttributesGroup;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorVerifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorVerifiedAttributes;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorVerifiedAttributesGroup;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.VerifiedAttribute;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@ToString
@EqualsAndHashCode
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Primary
public class M2MEserviceAttributeClientImpl implements IM2MEServiceAttributeClient {
    private final EservicesApi eservicesApi;
    private final RestTemplate restTemplate;
    private final String basePath;
    private final EServiceAttributeMapper attributeMapper;

    private final EserviceDescriptorsListRequest defaultDescriptorListRequest;

    public M2MEserviceAttributeClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs, EServiceAttributeMapper mapper) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getM2mBaseUrl();
        this.eservicesApi = new EservicesApi(createApiClient("dummyBearer"));

        this.defaultDescriptorListRequest = EserviceDescriptorsListRequest.builder()
                .limit(30)
                .offset(0)
                .eserviceId(UUID.randomUUID())
                .build();
        this.attributeMapper = mapper;
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.eservicesApi.setApiClient(createApiClient(bearerToken));
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
            .map(this.attributeMapper::map)
            .collect(Collectors.toList());
    }

    @Override
    public List<EServiceAttribute<CertifiedAttribute>> getCertifiedAttributes(
        UUID eServiceId, UUID descriptorId) {
        EServiceDescriptorCertifiedAttributes attributes = this.eservicesApi.getEServiceDescriptorCertifiedAttributes(
            eServiceId, descriptorId, 0, 30);

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
            .map(this.attributeMapper::map)
            .collect(Collectors.toList());
    }

    @Override
    public List<EServiceAttribute<DeclaredAttribute>> getDeclaredAttributes(
        UUID eServiceId, UUID descriptorId) {
        EServiceDescriptorDeclaredAttributes attributes = this.eservicesApi.getEServiceDescriptorDeclaredAttributes(
            eServiceId, descriptorId, 0, 30);

        return attributes.getResults().stream()
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
            .map(this.attributeMapper::map)
            .collect(Collectors.toList());
    }

    @Override
    public List<EServiceAttribute<VerifiedAttribute>> getVerifiedAttributes(
            UUID eServiceId, UUID descriptorId) {
        EServiceDescriptorVerifiedAttributes attributes = this.eservicesApi.getEServiceDescriptorVerifiedAttributes(
                eServiceId, descriptorId, 0, 30);

        return attributes.getResults().stream()
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


}
