package it.pagopa.pn.interop.cucumber.steps.m2m.purpose;

import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purpose;
import it.pagopa.interop.purpose.service.IM2MPurposeClient.PurposePatchRequest;
import it.pagopa.pn.interop.cucumber.steps.m2m.ResourceMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PurposeMapper extends ResourceMapper<PurposePatchRequest, Purpose> {

}