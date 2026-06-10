package it.pagopa.interop.purpose.service;

import it.pagopa.interop.authorization.service.utils.Authenticable;
import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.authorization.service.utils.SettableHeaders;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceTemplates;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.PurposeTemplateLinkEServiceTemplate;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface IM2MV3PurposeTemplateClient extends IM2MPurposeTemplateClient, SettableBearerToken, SettableHeaders, Authenticable {

    ResponseEntity<EServiceTemplates> getPurposeTemplateLinkableEServiceTemplate(UUID purposeTemplateId, int offset, int limit, List<UUID> creatorIds, String eserviceTemplateName);
    ResponseEntity<Object> linkEServiceTemplateToPurposeTemplate(UUID purposeTemplateId, PurposeTemplateLinkEServiceTemplate purposeTemplateLinkEServiceTemplate);
    ResponseEntity<Object> unlinkEServiceTemplateFromPurposeTemplate(UUID purposeTemplateId, UUID eServiceTemplateId);
}
