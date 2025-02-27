package it.pagopa.interop.e_service_template;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface IEServiceTemplateClient extends SettableBearerToken {
    CreatedEServiceTemplateVersion createEServiceTemplate(String xCorrelationId, EServiceTemplateSeed eserviceSeed);

    void publishEServiceTemplate(String xCorrelationId, UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId);

    void suspendEServiceTemplate(String xCorrelationId, UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId);

    void activateEServiceTemplate(String xCorrelationId, UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId);

    EServiceTemplateVersionDetails getEServiceTemplateVersion(String xCorrelationId, UUID eServiceTemplateId, UUID eServiceTemplateVersionId);

    ResponseEntity<EServiceTemplateVersionDetails> getEServiceTemplateVersionWithHttpInfo(
        String xCorrelationId,
        UUID eServiceTemplateId, UUID eServiceTemplateVersionId);
}
