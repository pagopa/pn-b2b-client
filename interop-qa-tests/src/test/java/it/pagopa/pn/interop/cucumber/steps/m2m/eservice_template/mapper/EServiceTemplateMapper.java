package it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template.mapper;

import it.pagopa.interop.e_service_template.IM2MEServiceTemplateClient.EServiceTemplatePatchRequest;
import it.pagopa.interop.e_service_template.IM2MEServiceTemplateClient.EServiceTemplateVersionCreationRequest;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplate;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersion;
import it.pagopa.pn.interop.cucumber.steps.m2m.ResourceMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EServiceTemplateMapper extends ResourceMapper<EServiceTemplatePatchRequest, EServiceTemplate> {
    EServiceTemplateVersionCreationRequest mapToRequest(EServiceTemplateVersion version);
}
