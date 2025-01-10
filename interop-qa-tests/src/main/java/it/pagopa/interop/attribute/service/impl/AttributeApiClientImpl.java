package it.pagopa.interop.attribute.service.impl;

import it.pagopa.interop.attribute.service.IAttributeApiClient;
import it.pagopa.interop.conf.springconfig.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.AttributesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.Attribute;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.Attributes;
import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedAttributeSeed;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class AttributeApiClientImpl implements IAttributeApiClient {
    private final AttributesApi attributesApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public AttributeApiClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getBaseUrl();
        this.attributesApi = new AttributesApi(createApiClient("bearerToken"));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public Attribute createCertifiedAttribute(String xCorrelationId, CertifiedAttributeSeed certifiedAttributeSeed) {
        return attributesApi.createCertifiedAttribute(xCorrelationId, certifiedAttributeSeed);
    }

    @Override
    public Attribute createVerifiedAttribute(String xCorrelationId, AttributeSeed attributeSeed) {
        return attributesApi.createVerifiedAttribute(xCorrelationId, attributeSeed);
    }

    @Override
    public Attribute createDeclaredAttribute(String xCorrelationId, AttributeSeed attributeSeed) {
        return attributesApi.createDeclaredAttribute(xCorrelationId, attributeSeed);
    }

    @Override
    public Attributes getAttributes(String xCorrelationId, Integer limit, Integer offset, List<AttributeKind> kinds, String q, String origin) {
        return attributesApi.getAttributes(xCorrelationId, limit, offset, kinds, q, origin);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.attributesApi.setApiClient(createApiClient(bearerToken));
    }
}
