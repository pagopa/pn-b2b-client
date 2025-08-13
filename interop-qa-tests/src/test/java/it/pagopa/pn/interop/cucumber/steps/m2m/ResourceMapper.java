package it.pagopa.pn.interop.cucumber.steps.m2m;

import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

public interface ResourceMapper<PATCH_REQUEST, RESOURCE> {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void copyPatchRequestToResource(PATCH_REQUEST request, @MappingTarget RESOURCE resource);

    RESOURCE copyResource(RESOURCE resource);
}