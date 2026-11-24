package it.pagopa.pn.interop.cucumber.steps.m2m.attribute.mapper;

import io.cucumber.java.DataTableType;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.CertifiedDiscreteAttributeSeed;

import java.util.Map;

public class CertifiedDiscreteAttributeSeedMapper {
    private final AttributeSeedMapper<CertifiedDiscreteAttributeSeed> genericAttributeMapper
        = new AttributeSeedMapper<>(this::specificAttributeMapper);

    @DataTableType
    public CertifiedDiscreteAttributeSeed mapAttributeSeed(Map<String, String> entry) {
        return genericAttributeMapper.mapAttributeSeed(entry);
    }

    private CertifiedDiscreteAttributeSeed specificAttributeMapper(AttributePrototype prototype) {
        return new CertifiedDiscreteAttributeSeed()
            .name(prototype.getName())
            .description(prototype.getDescription())
            .code(prototype.getCode());
    }
}
