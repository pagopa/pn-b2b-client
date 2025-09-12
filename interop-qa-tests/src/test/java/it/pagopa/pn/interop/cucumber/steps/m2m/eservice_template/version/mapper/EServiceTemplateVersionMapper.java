package it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template.version.mapper;

import it.pagopa.interop.e_service_template.IM2MEServiceTemplateClient.EServiceTemplateVersionPatchRequest;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersion;
import it.pagopa.pn.interop.cucumber.steps.m2m.ResourceMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EServiceTemplateVersionMapper extends ResourceMapper<EServiceTemplateVersionPatchRequest, EServiceTemplateVersion> {

}
