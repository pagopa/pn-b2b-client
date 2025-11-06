package it.pagopa.pn.interop.cucumber.steps.m2m.eservice.descriptor.mapper;

import it.pagopa.interop.eservice.service.IM2MEserviceDescriptorClient.EServiceDescriptorQuotasPatchRequest;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor;
import it.pagopa.pn.interop.cucumber.steps.m2m.ResourceMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EServiceDescriptorQuotasMapper extends ResourceMapper<EServiceDescriptorQuotasPatchRequest, EServiceDescriptor> {

}
