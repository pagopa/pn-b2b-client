package it.pagopa.interop.attribute.service.impl;

import it.pagopa.interop.attribute.service.IAttributeApiClient;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.AttributesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.Attribute;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.Attributes;
import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedAttributeSeed;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Retryable(
        retryFor = { HttpServerErrorException.class },
        backoff = @Backoff(delay = 2000)
)
public class AttributeApiClientImpl implements IAttributeApiClient {
    private final AttributesApi attributesApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public AttributeApiClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getBaseUrl();
        this.attributesApi = new AttributesApi(createApiClient("dummyBearer"));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public Attribute createCertifiedAttribute(CertifiedAttributeSeed certifiedAttributeSeed) {
        return attributesApi.createCertifiedAttribute(certifiedAttributeSeed);
    }

    @Override
    public Attribute createCertifiedDiscreteAttribute(AttributeSeed attributeSeed) {
        return attributesApi.createCertifiedDiscreteAttribute(attributeSeed);
    }

    @Override
    public Attribute createVerifiedAttribute(AttributeSeed attributeSeed) {
        return attributesApi.createVerifiedAttribute(attributeSeed);
    }

    @Override
    public Attribute createDeclaredAttribute(AttributeSeed attributeSeed) {
        return attributesApi.createDeclaredAttribute(attributeSeed);
    }

    @Override
    public Attributes getAttributes(Integer limit, Integer offset, List<AttributeKind> kinds, String q, String origin) {
        return attributesApi.getAttributes(limit, offset, kinds, q, origin);
    }

    @Override
    public ResponseEntity<Attribute> createCertifiedAttributeRE(CertifiedAttributeSeed certifiedAttributeSeed) {
        return attributesApi.createCertifiedAttributeWithHttpInfo(certifiedAttributeSeed);
    }

    @Override
    public ResponseEntity<Attribute> createDeclaredAttributeRE(AttributeSeed declaredAttributeSeed) {
        return attributesApi.createDeclaredAttributeWithHttpInfo(declaredAttributeSeed);
    }

    @Override
    public ResponseEntity<Attribute> createVerifiedAttributeRE(AttributeSeed declaredAttributeSeed) {
        return attributesApi.createVerifiedAttributeWithHttpInfo(declaredAttributeSeed);
    }

    @Override
    public ResponseEntity<Attribute> getAttributeByIdRE(UUID attributeId) {
        return attributesApi.getAttributeByIdWithHttpInfo(attributeId);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.attributesApi.setApiClient(createApiClient(bearerToken));
    }
}
