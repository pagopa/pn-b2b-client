package it.pagopa.interop.attribute.service.impl;

import it.pagopa.interop.attribute.service.IM2MAttributeClient;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.api.AttributesApi;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttributeSeed;
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
public class M2MAttributeClientImpl implements IM2MAttributeClient {
    private final AttributesApi attributesApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public M2MAttributeClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getM2mBaseUrl();
        this.attributesApi = new AttributesApi(createApiClient("dummyBearer"));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.attributesApi.setApiClient(createApiClient(bearerToken));
    }

    @Override
    public CertifiedAttribute createCertifiedAttribute(CertifiedAttributeSeed agreementPayload) {
        return attributesApi.createCertifiedAttribute(agreementPayload);
    }

    @Override
    public CertifiedAttribute getCertifiedAttribute(UUID id) {
       return attributesApi.getCertifiedAttribute(id);
    }
}
