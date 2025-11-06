package it.pagopa.pn.interop.cucumber.steps.m2m.eservice.mapper;

import it.pagopa.interop.eservice.service.IM2MEserviceClient.EServiceDescriptionPatchRequest;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EService;
import it.pagopa.pn.interop.cucumber.steps.m2m.ResourceMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EServiceDescriptionMapper extends ResourceMapper<EServiceDescriptionPatchRequest, EService> {

}
