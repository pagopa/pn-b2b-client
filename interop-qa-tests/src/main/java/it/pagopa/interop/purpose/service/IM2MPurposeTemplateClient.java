package it.pagopa.interop.purpose.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.core.io.Resource;

import java.util.UUID;

public interface IM2MPurposeTemplateClient extends SettableBearerToken {
    @Data
    @Builder
    class PurposeTemplatePatchRequest {
        private String name;
        private String intendedTarget;
        private String description;
        private EServiceTechnology technology;
        private EServiceMode mode;
        private Boolean isSignalHubEnabled;
    }

    PurposeTemplate getPurposeTemplate(UUID id);
    PurposeTemplate patchPurposeTemplate(UUID id, PurposeTemplateDraftUpdateSeed purposePatchSeed);
    Document uploadRiskAnalysisTemplateAnswerAnnotationDocument(UUID purposeTemplateId, UUID answerId, String prettyName, Resource file);
    FileDownloadMultipart getRiskAnalysisTemplateAnswerAnnotationDocument(UUID purposeTemplateId, UUID documentId);
}
