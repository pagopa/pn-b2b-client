package it.pagopa.interop.eservice.service.impl;

import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.eservice.service.IM2MEServiceAttributeClient;
import it.pagopa.interop.eservice.service.IM2MEserviceDescriptorClient.EserviceDescriptorsListRequest;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DeclaredAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorCertifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorCertifiedAttributes;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorDeclaredAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorDeclaredAttributes;
import java.util.List;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@ToString
@EqualsAndHashCode
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class M2MEserviceAttributeClientImpl implements IM2MEServiceAttributeClient {
    private final EservicesApi eservicesApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    private final EserviceDescriptorsListRequest defaultDescriptorListRequest;

    public M2MEserviceAttributeClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getM2mBaseUrl();
        this.eservicesApi = new EservicesApi(createApiClient("dummyBearer"));

        this.defaultDescriptorListRequest = EserviceDescriptorsListRequest.builder()
                .limit(30)
                .offset(0)
                .eserviceId(UUID.randomUUID())
                .build();
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
    public List<EServiceAttribute<CertifiedAttribute>> addCertifiedAttributes(
        UUID eServiceId,
        UUID descriptorId,
        int groupId,
        List<UUID> attributes
    ) {
        // TODO 09/10/2025 placeholder di una API non ancora rilasciata. Aggiornare una volta ottenuta la specifica.
        return List.of(EServiceAttribute.<CertifiedAttribute>builder().build());
    }

    @Override
    public List<EServiceAttribute<CertifiedAttribute>> createCertifiedAttributesGroup(
        UUID eServiceId,
        UUID descriptorId,
        List<UUID> attributes
    ) {
        // TODO 09/10/2025 placeholder di una API non ancora rilasciata. Aggiornare una volta ottenuta la specifica.
        return List.of(EServiceAttribute.<CertifiedAttribute>builder().build());
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
    public List<EServiceAttribute<DeclaredAttribute>> addDeclaredAttributes(
        UUID eServiceId,
        UUID descriptorId,
        int groupId,
        List<UUID> attributes
    ) {
        // TODO 09/10/2025 placeholder di una API non ancora rilasciata. Aggiornare una volta ottenuta la specifica.
        return List.of(EServiceAttribute.<DeclaredAttribute>builder().build());
    }

    @Override
    public List<EServiceAttribute<DeclaredAttribute>> createDeclaredAttributesGroup(
        UUID eServiceId,
        UUID descriptorId,
        List<UUID> attributes
    ) {
        // TODO 09/10/2025 placeholder di una API non ancora rilasciata. Aggiornare una volta ottenuta la specifica.
        return List.of(EServiceAttribute.<DeclaredAttribute>builder().build());
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
}
