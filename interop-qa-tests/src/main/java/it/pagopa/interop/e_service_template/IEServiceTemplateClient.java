package it.pagopa.interop.e_service_template;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import java.util.UUID;

public interface IEServiceTemplateClient extends SettableBearerToken {
    CreatedEServiceTemplateVersion createEServiceTemplate(String xCorrelationId, EServiceTemplateSeed eserviceSeed);
    EServiceTemplateVersionDetails getEServiceTemplateVersion(String xCorrelationId, UUID eServiceTemplateId, UUID eServiceTemplateVersionId);
}
