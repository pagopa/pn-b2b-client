package it.pagopa.pn.interop.cucumber.steps.m2m.attribute.mapper;

import io.cucumber.java.DataTableType;
import it.pagopa.interop.attribute.service.IM2MVerifiedAttributeClient.VerifiedAttributeSeed;
import java.util.Map;

public class VerifiedAttributeSeedMapper {
    private final AttributeSeedMapper<VerifiedAttributeSeed> genericAttributeMapper
        = new AttributeSeedMapper<>(this::specificAttributeMapper);

    @DataTableType
    public VerifiedAttributeSeed mapAttributeSeed(Map<String, String> entry) {
        return genericAttributeMapper.mapAttributeSeed(entry);
    }

    protected VerifiedAttributeSeed specificAttributeMapper(AttributePrototype prototype) {
        return new VerifiedAttributeSeed()
            .name(prototype.getName())
            .description(prototype.getDescription())
            .code(prototype.getCode());
    }
}
