package it.pagopa.interop.e_service_template.impl;

import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.e_service_template.IM2MEServiceTemplateAttributeClient;
import it.pagopa.interop.eservice.service.EServiceAttribute;
import it.pagopa.interop.eservice.service.IM2MEserviceDescriptorClient.EserviceDescriptorsListRequest;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.api.EserviceTemplatesApi;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.*;

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
public class M2MEserviceTemplateAttributeClientImpl implements IM2MEServiceTemplateAttributeClient {
    private final EserviceTemplatesApi templatesApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    private final EserviceDescriptorsListRequest defaultDescriptorListRequest;

    public M2MEserviceTemplateAttributeClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getM2mBaseUrl();
        this.templatesApi = new EserviceTemplatesApi(createApiClient("dummyBearer"));

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
        this.templatesApi.setApiClient(createApiClient(bearerToken));
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
        EServiceTemplateVersionCertifiedAttributes attributes = this.templatesApi.getEServiceTemplateVersionCertifiedAttributes(
            eServiceId, descriptorId, 0, 30);

        return attributes.getResults().stream()
            .map(this::mapToEServiceAttribute)
            .toList();
    }

    @Override
    public void deleteCertifiedAttribute(UUID eServiceId, UUID descriptorId, int groupIndex,
        UUID attributeId) {
        this.templatesApi.deleteEServiceTemplateVersionCertifiedAttributeFromGroup(eServiceId, descriptorId, groupIndex, attributeId);
    }

    private EServiceAttribute<CertifiedAttribute> mapToEServiceAttribute(
        EServiceTemplateVersionCertifiedAttribute attribute) {
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
        EServiceTemplateVersionDeclaredAttributes attributes = this.templatesApi.getEServiceTemplateVersionDeclaredAttributes(
            eServiceId, descriptorId, 0, 30);

        return attributes.getResults().stream()
            .map(this::mapToEServiceAttribute)
            .toList();
    }

    @Override
    public void deleteDeclaredAttribute(UUID eServiceId, UUID descriptorId, int groupIndex,
        UUID attributeId) {
        this.templatesApi.deleteEServiceTemplateVersionDeclaredAttributeFromGroup(eServiceId, descriptorId, groupIndex, attributeId);
    }

    private EServiceAttribute<DeclaredAttribute> mapToEServiceAttribute(
        EServiceTemplateVersionDeclaredAttribute attribute) {
        return EServiceAttribute.<DeclaredAttribute>builder()
            .attribute(attribute.getAttribute())
            .groupIndex(attribute.getGroupIndex())
            .build();
    }
//----------------------------------------------------------------------
    @Override
    public List<EServiceAttribute<VerifiedAttribute>> addVerifiedAttributes(
            UUID eServiceId,
            UUID descriptorId,
            int groupId,
            List<UUID> attributes
    ) {
        // TODO 09/10/2025 placeholder di una API non ancora rilasciata. Aggiornare una volta ottenuta la specifica.
        return List.of(EServiceAttribute.<VerifiedAttribute>builder().build());
    }

    @Override
    public List<EServiceAttribute<VerifiedAttribute>> createVerifiedAttributesGroup(
            UUID eServiceId,
            UUID descriptorId,
            List<UUID> attributes
    ) {
        // TODO 09/10/2025 placeholder di una API non ancora rilasciata. Aggiornare una volta ottenuta la specifica.
        return List.of(EServiceAttribute.<VerifiedAttribute>builder().build());
    }

    @Override
    public List<EServiceAttribute<VerifiedAttribute>> getVerifiedAttributes(
            UUID eServiceId, UUID descriptorId) {
        EServiceTemplateVersionVerifiedAttributes attributes = this.templatesApi.getEServiceTemplateVersionVerifiedAttributes(
                eServiceId, descriptorId, 0, 30);

        return attributes.getResults().stream()
                .map(this::mapToEServiceAttribute)
                .toList();
    }

    @Override
    public void deleteVerifiedAttribute(UUID eServiceId, UUID descriptorId, int groupIndex,
                                         UUID attributeId) {
        this.templatesApi.deleteEServiceTemplateVersionVerifiedAttributeFromGroup(eServiceId, descriptorId, groupIndex, attributeId);
    }

    private EServiceAttribute<VerifiedAttribute> mapToEServiceAttribute(
            EServiceTemplateVersionVerifiedAttribute attribute) {
        return EServiceAttribute.<VerifiedAttribute>builder()
                .attribute(attribute.getAttribute())
                .groupIndex(attribute.getGroupIndex())
                .build();
    }


}
