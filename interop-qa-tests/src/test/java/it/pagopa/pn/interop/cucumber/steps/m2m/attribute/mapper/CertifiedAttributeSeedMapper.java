package it.pagopa.pn.interop.cucumber.steps.m2m.attribute.mapper;

import io.cucumber.java.DataTableType;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttributeSeed;
import java.util.Map;

public class CertifiedAttributeSeedMapper {
    private final AttributeSeedMapper<CertifiedAttributeSeed> genericAttributeMapper
        = new AttributeSeedMapper<>(this::specificAttributeMapper);

    @DataTableType
    public CertifiedAttributeSeed mapAttributeSeed(Map<String, String> entry) {
        return genericAttributeMapper.mapAttributeSeed(entry);
    }

    private CertifiedAttributeSeed specificAttributeMapper(AttributePrototype prototype) {
        return new CertifiedAttributeSeed()
            .name(prototype.getName())
            .description(prototype.getDescription())
            .code(prototype.getCode());
    }
}
