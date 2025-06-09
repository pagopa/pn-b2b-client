package it.pagopa.interop.eservice_template;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplate;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersionState;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersions;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

public interface IM2MEserviceTemplateClient extends SettableBearerToken {
    @Data
    @Builder
    class EserviceTemplateListRequest {
        @NonNull
        private Integer offset;
        @NonNull
        private Integer limit;
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
