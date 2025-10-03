package it.pagopa.interop.eservice_template;

import it.pagopa.interop.ListRequest;
import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplate;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersionState;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersions;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

public interface IM2MEServiceTemplateClient extends SettableBearerToken {
    @Data
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    class EserviceTemplateListRequest extends ListRequest {
        private UUID templateId;
        private EServiceTemplateVersionState state;
    }

    EServiceTemplate getEserviceTemplate(UUID templateId);

    EServiceTemplateVersions getEserviceTemplateVersions(EserviceTemplateListRequest request);

    EServiceTemplateVersion getEserviceTemplateVersion(UUID templateId, UUID versionId);

    // API BFF
    // TODO: aggiornare ad API m2m appena disponibili
    CreatedEServiceTemplateVersion createEserviceTemplate(EServiceTemplateSeed payload);
}
