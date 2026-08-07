package it.pagopa.interop.attribute.service.impl;

import it.pagopa.interop.attribute.service.IM2MV3CertifiedDiscreteAttributeClient;
import it.pagopa.interop.common.client.AbstractDPoPClient;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.common.operation.SimpleOperation;
import it.pagopa.interop.common.rest_template.DpopRestTemplate;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.api.AttributesApi;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.CertifiedDiscreteAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.CertifiedDiscreteAttributeSeed;
import it.pagopa.interop.utils.ApiClientUtils;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static it.pagopa.interop.utils.ApiClientUtils.V3_UNSUPPORTED_BEARER_MSG;

@ToString
@EqualsAndHashCode(callSuper = true)
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class M2MV3CertifiedDiscreteAttributeClientImpl extends AbstractDPoPClient
        implements IM2MV3CertifiedDiscreteAttributeClient
{
    private final AttributesApi attributesApi;
    private final String basePath;

    public M2MV3CertifiedDiscreteAttributeClientImpl(
        DpopRestTemplate restTemplate,
        InteropClientConfigs interopClientConfigs
    ) {
        super(restTemplate);
        this.basePath = interopClientConfigs.getM2mV3BaseUrl();
        this.attributesApi = new AttributesApi(ApiClientUtils.createApiClient(restTemplate, basePath, Collections.emptyMap()));
    }

    @Override
    public void setBearerToken(String bearerToken) {
        throw new UnsupportedOperationException(V3_UNSUPPORTED_BEARER_MSG);
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        this.attributesApi.setApiClient(ApiClientUtils.createApiClient(super.getRestTemplate(), basePath, headers));
    }

    @Override
    public CertifiedDiscreteAttribute get(UUID id) {
        return this.performOperation(SimpleOperation.of(
                () -> this.attributesApi.getCertifiedDiscreteAttribute(id),
                res -> res
        )).orElse(null);
    }

    @Override
    public List<CertifiedDiscreteAttribute> getAll() {
        return this.performOperation(SimpleOperation.of(
                () -> attributesApi.getCertifiedDiscreteAttributes(0, 30),
                res -> res.getResults()
        )).orElse(Collections.emptyList());
    }

    @Override
    public UUID getId(CertifiedDiscreteAttribute entity) {
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
    public CertifiedDiscreteAttribute create(CertifiedDiscreteAttributeSeed agreementPayload) {
        return this.performOperation(SimpleOperation.of(
            () -> this.attributesApi.createCertifiedDiscreteAttribute(agreementPayload),
            res -> res
        )).orElse(null);
    }
}
