package it.pagopa.interop.e_service_template;

import it.pagopa.interop.ListRequest;
import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import javax.annotation.Nullable;
import java.util.UUID;

public interface IM2MEServiceTemplateClient extends SettableBearerToken {
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
    class EServiceTemplateDescriptionPatchRequest {
        private String description;
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

    EServiceTemplate createEServiceTemplate(EServiceTemplateSeed payload);

    ResponseEntity<EServiceTemplateVersion> createEserviceTemplateVersion(
        UUID templateId,
        EServiceTemplateVersionCreationRequest request);

    Document uploadDocument(UUID templateId, UUID versionId, Resource file, String prettyName);

    FileDownloadMultipart downloadDocument(UUID templateId, UUID versionId, UUID documentId);

    Documents getDocuments(UUID templateId, UUID versionId);

    void unsuspend(UUID templateId, UUID versionId);

    EServiceTemplate patchEServiceTemplate(UUID templateId, EServiceTemplatePatchRequest patchRequest);

    EServiceTemplate patchEServiceTemplateDescription(UUID templateId, EServiceTemplateDescriptionPatchRequest patchRequest);

    EServiceTemplateVersion patchEServiceTemplateVersion(UUID templateId, UUID versionId, EServiceTemplateVersionPatchRequest patchRequest);

    EServiceTemplateVersion patchEServiceTemplateVersionQuotas(UUID templateId, UUID versionId, EServiceTemplateVersionQuotasPatchRequest patchRequest);

    void deleteEServiceTemplate(UUID templateId);
}
