package it.pagopa.pn.interop.cucumber.steps.m2m.attribute.mapper;

import io.cucumber.java.DataTableType;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DeclaredAttributeSeed;
import java.util.Map;

public class DeclaredAttributeSeedMapper {
    private final AttributeSeedMapper<DeclaredAttributeSeed> genericAttributeMapper
        = new AttributeSeedMapper<>(this::specificAttributeMapper);

    @DataTableType
    public DeclaredAttributeSeed mapAttributeSeed(Map<String, String> entry) {
        return genericAttributeMapper.mapAttributeSeed(entry);
    }

    private DeclaredAttributeSeed specificAttributeMapper(AttributePrototype prototype) {
        return new DeclaredAttributeSeed()
            .name(prototype.getName())
            .description(prototype.getDescription());
    }
}
