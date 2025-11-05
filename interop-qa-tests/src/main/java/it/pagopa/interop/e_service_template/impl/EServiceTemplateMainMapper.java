package it.pagopa.interop.e_service_template.impl;

import it.pagopa.interop.e_service_template.IM2MEServiceTemplateClient.EServiceTemplateVersionCreationRequest;
import it.pagopa.interop.e_service_template.IM2MEServiceTemplateClient.EServiceTemplateVersionPatchRequest;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersionDraftUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersionSeed;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EServiceTemplateMainMapper {
    EServiceTemplateVersionDraftUpdateSeed mapPatchRequestToSeed(
        EServiceTemplateVersionPatchRequest request);
    EServiceTemplateVersionSeed mapCreationRequestToSeed(
        EServiceTemplateVersionCreationRequest request);
}
