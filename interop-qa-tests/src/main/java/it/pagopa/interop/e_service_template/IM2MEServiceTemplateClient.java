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
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

public interface IM2MEServiceTemplateClient extends SettableBearerToken {
    // TODO 07/10/2025 approssimazione di un oggetto la cui specifica non è ancora nota.
    //  Adattare una volta rilasciata la specifica.
    @Data
    @Builder
    class EServiceTemplateVersionCreationRequest {
        private String description;
        private Integer voucherLifespan;
        private Integer dailyCallsPerConsumer;
        private Integer dailyCallsTotal;
        private AgreementApprovalPolicy agreementApprovalPolicy;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    class EserviceTemplateListRequest extends ListRequest {
        private UUID templateId;

        @Nullable
        private EServiceTemplateVersionState state;
    }

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

    EServiceTemplate getEserviceTemplate(UUID templateId);

    EServiceTemplateVersions getEserviceTemplateVersions(EserviceTemplateListRequest request);

    EServiceTemplateVersions getEserviceTemplateVersions(UUID templateId);

    EServiceTemplateVersion getEserviceTemplateVersion(UUID templateId, UUID versionId);

    // API BFF
    // TODO: aggiornare ad API m2m appena disponibili
    CreatedEServiceTemplateVersion createEserviceTemplate(EServiceTemplateSeed payload);

    EServiceTemplateVersion createEserviceTemplateVersion(
        UUID templateId,
        EServiceTemplateVersionCreationRequest request);

    Documents getDocuments(UUID templateId, UUID versionId);

    void unsuspend(UUID templateId, UUID versionId);

    EServiceTemplate patchEServiceTemplate(UUID templateId, EServiceTemplatePatchRequest patchRequest);

    EServiceTemplateVersion patchEServiceTemplateVersion(UUID templateId, UUID versionId, EServiceTemplateVersionPatchRequest patchRequest);

    EServiceTemplateVersion patchEServiceTemplateVersionQuotas(UUID templateId, UUID versionId, EServiceTemplateVersionQuotasPatchRequest patchRequest);

    void deleteEServiceTemplate(UUID templateId);
}
