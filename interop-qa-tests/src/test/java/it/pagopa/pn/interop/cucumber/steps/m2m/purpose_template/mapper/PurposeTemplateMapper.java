package it.pagopa.pn.interop.cucumber.steps.m2m.purpose_template.mapper;

import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplate;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplateDraftUpdateSeed;
import it.pagopa.pn.interop.cucumber.steps.m2m.ResourceMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PurposeTemplateMapper extends ResourceMapper<PurposeTemplateDraftUpdateSeed, PurposeTemplate> {
}
