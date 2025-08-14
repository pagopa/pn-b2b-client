package it.pagopa.pn.interop.cucumber.steps.m2m.eservice.mapper.descriptor;

import it.pagopa.interop.eservice.service.IM2MEserviceDescriptorClient.EServiceDescriptorPatchRequest;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor;
import it.pagopa.pn.interop.cucumber.steps.m2m.ResourceMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EServiceDescriptorMapper extends ResourceMapper<EServiceDescriptorPatchRequest, EServiceDescriptor> {

}
