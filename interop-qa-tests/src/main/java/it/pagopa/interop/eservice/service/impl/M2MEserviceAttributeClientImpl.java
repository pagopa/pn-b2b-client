package it.pagopa.interop.eservice.service.impl;

import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.eservice.service.IM2MEServiceAttributeClient;
import it.pagopa.interop.eservice.service.IM2MEserviceDescriptorClient.EserviceDescriptorsListRequest;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttribute;
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
        UUID eServiceId, UUID descriptorId, int groupId) {
        // TODO 09/10/2025 placeholder di una API non ancora rilasciata. Aggiornare una volta ottenuta la specifica.
        return List.of(EServiceAttribute.<CertifiedAttribute>builder().build());
    }
}
