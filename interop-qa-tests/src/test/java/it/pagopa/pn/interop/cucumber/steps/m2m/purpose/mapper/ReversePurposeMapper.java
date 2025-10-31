package it.pagopa.pn.interop.cucumber.steps.m2m.purpose.mapper;

import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purpose;
import it.pagopa.interop.purpose.service.IM2MPurposeClient.ReversePurposePatchRequest;
import it.pagopa.pn.interop.cucumber.steps.m2m.ResourceMapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ReversePurposeMapper extends ResourceMapper<ReversePurposePatchRequest, Purpose> {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "dailyCalls", target = "currentVersion.dailyCalls")
    void copyPatchRequestToResource(ReversePurposePatchRequest request, @MappingTarget Purpose resource);
}