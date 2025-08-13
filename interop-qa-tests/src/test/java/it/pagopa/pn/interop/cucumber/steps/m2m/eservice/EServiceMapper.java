package it.pagopa.pn.interop.cucumber.steps.m2m.eservice;

import it.pagopa.interop.eservice.service.impl.M2MEserviceClientImpl.EServicePatchRequest;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EService;
import it.pagopa.pn.interop.cucumber.steps.m2m.ResourceMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EServiceMapper extends ResourceMapper<EServicePatchRequest, EService> {

}
