package it.pagopa.interop.attribute.service.impl;

import static it.pagopa.interop.utils.ApiClientUtils.V3_UNSUPPORTED_BEARER_MSG;
import static java.util.function.Function.identity;

import it.pagopa.interop.M2MVersionsMapper;
import it.pagopa.interop.attribute.service.IM2MV3DeclaredAttributeClient;
import it.pagopa.interop.common.client.AbstractDPoPClient;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.common.operation.SimpleOperation;
import it.pagopa.interop.common.rest_template.DpopRestTemplate;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DeclaredAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DeclaredAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.api.AttributesApi;
import it.pagopa.interop.utils.ApiClientUtils;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@ToString
@EqualsAndHashCode(callSuper = true)
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class M2MV3DeclaredAttributeClientImpl extends AbstractDPoPClient implements
    IM2MV3DeclaredAttributeClient {
    private final AttributesApi attributesApi;
    private final String basePath;
    private final M2MVersionsMapper mapper;

    public M2MV3DeclaredAttributeClientImpl(
        DpopRestTemplate restTemplate,
        InteropClientConfigs interopClientConfigs,
        M2MVersionsMapper mapper
    ) {
        super(restTemplate);
        this.basePath = interopClientConfigs.getM2mV3BaseUrl();
        this.attributesApi = new AttributesApi(ApiClientUtils.createApiClient(restTemplate, basePath,
            Collections.emptyMap()));
        this.mapper = mapper;
    }

    @Override
    public DeclaredAttribute get(UUID id) {
       return this.performOperation(SimpleOperation.of(
           () -> this.attributesApi.getDeclaredAttribute(id),
           identity()
       )).map(mapper::mapToV2).orElse(null);
    }

    @Override
    public List<DeclaredAttribute> getAll() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<DeclaredAttribute> getPage(int page, int size) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public UUID getId(DeclaredAttribute entity) {
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
    public DeclaredAttribute create(DeclaredAttributeSeed agreementPayload) {
        it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.DeclaredAttributeSeed v3Payload = this.mapper.mapToV3(
            agreementPayload);
        return this.performOperation(SimpleOperation.of(
            () -> this.attributesApi.createDeclaredAttribute(v3Payload),
            identity()
        )).map(mapper::mapToV2).orElse(null);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        throw new UnsupportedOperationException(V3_UNSUPPORTED_BEARER_MSG);
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        this.attributesApi.setApiClient(
            ApiClientUtils.createApiClient(super.getRestTemplate(), basePath, headers));
    }
}
