package it.pagopa.interop.e_service_template;

import it.pagopa.interop.ListRequest;
import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementApprovalPolicy;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Documents;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplate;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersionState;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTemplateVersions;
import java.util.UUID;
import lombok.Builder;
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

    /* TODO 10/09/2025: modellano DTO le cui specifiche non sono state ancora rilasciate, per cui
    *   dovranno essere sottoposte a tutti gli adattamenti del caso. Si consiglia di reperire
    *   tutti i punti in cui queste strutture sono state utilizzate per verificare ed eventualmente
    *   modificare l'utilizzo fatto. */
    @Data
    @Builder
    class EServiceTemplatePatchRequest {
        private String name;
        private String intendedTarget;
        private String description;
        private EServiceTechnology technology;
        private EServiceMode mode;
        private Boolean isSignalHubEnabled;
    }

    @Data
    @Builder
    class EServiceTemplateVersionPatchRequest {
        private String description;
        private Integer voucherLifespan;
        private Integer dailyCallsPerConsumer;
        private Integer dailyCallsTotal;
        private AgreementApprovalPolicy agreementApprovalPolicy;
    }

    @Data
    @Builder
    class EServiceTemplateVersionQuotasPatchRequest {
        private Integer dailyCallsPerConsumer;
        private Integer dailyCallsTotal;
        private Integer voucherLifespan;
    }
    /* *******************************************************************************************/

    EServiceTemplate getEserviceTemplate(UUID templateId);

    EServiceTemplateVersions getEserviceTemplateVersions(EserviceTemplateListRequest request);

    EServiceTemplateVersion getEserviceTemplateVersion(UUID templateId, UUID versionId);

    // API BFF
    // TODO: aggiornare ad API m2m appena disponibili
    CreatedEServiceTemplateVersion createEserviceTemplate(EServiceTemplateSeed payload);

    Documents getDocuments(UUID templateId, UUID versionId);

    void unsuspend(UUID templateId, UUID versionId);

    EServiceTemplate patchEServiceTemplate(UUID templateId, EServiceTemplatePatchRequest patchRequest);

    EServiceTemplateVersion patchEServiceTemplateVersion(UUID templateId, UUID versionId, EServiceTemplateVersionPatchRequest patchRequest);

    EServiceTemplateVersion patchEServiceTemplateVersionQuotas(UUID templateId, UUID versionId, EServiceTemplateVersionQuotasPatchRequest patchRequest);
}
