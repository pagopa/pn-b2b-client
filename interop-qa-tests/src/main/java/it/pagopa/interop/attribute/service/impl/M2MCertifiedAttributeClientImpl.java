package it.pagopa.interop.attribute.service.impl;

import it.pagopa.interop.attribute.service.IM2MCertifiedAttributeClient;
import it.pagopa.interop.common.client.AbstractClient;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.common.operation.SimpleOperation;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.api.AttributesApi;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttributes;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@ToString
@EqualsAndHashCode(callSuper = true)
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Primary
public class M2MCertifiedAttributeClientImpl extends AbstractClient implements
    IM2MCertifiedAttributeClient {
    private final AttributesApi attributesApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public M2MCertifiedAttributeClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
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
    public CertifiedAttribute get(UUID id) {
       return this.performOperation(SimpleOperation.of(
               () -> this.attributesApi.getCertifiedAttribute(id),
               res -> res
       )).orElse(null);
    }

    @Override
    public List<CertifiedAttribute> getAll() {
        return this.performOperation(
            SimpleOperation.of(
                () -> attributesApi.getCertifiedAttributes(0, 30),
                CertifiedAttributes::getResults)).orElse(Collections.emptyList());
    }

    @Override
    public List<CertifiedAttribute> getPage(int page, int size) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public UUID getId(CertifiedAttribute entity) {
        return entity == null ? null : entity.getId();
    }

    @Override
    public UUID generateId(EntityIdType type) {
        return switch (type){
            case INVALID_ID -> UUID.fromString("00000000-0000-4000-8000-abcdefabcdef"); // La classe UUID non permette di formare un UUID malformato
            case NON_EXISTENT_ID -> UUID.fromString("00000000-0000-4000-8000-abcdefabcdef");
            default -> throw new IllegalStateException("Tipo di id non supportato: " + type.name());
        };
    }

    @Override
    public CertifiedAttribute create(CertifiedAttributeSeed agreementPayload) {
        return this.performOperation(SimpleOperation.of(
                () -> this.attributesApi.createCertifiedAttribute(agreementPayload),
                res -> res
        )).orElse(null);
    }
}
