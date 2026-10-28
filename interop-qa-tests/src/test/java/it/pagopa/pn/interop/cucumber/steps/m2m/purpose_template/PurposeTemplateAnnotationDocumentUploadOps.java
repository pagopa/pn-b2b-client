package it.pagopa.pn.interop.cucumber.steps.m2m.purpose_template;

import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Document;
import it.pagopa.interop.purpose.service.IM2MPurposeTemplateClient;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.upload.DocumentUploadOps;
import it.pagopa.pn.interop.cucumber.steps.common.upload.UploadOperationResult;
import it.pagopa.pn.interop.cucumber.steps.common.upload.UploadRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;

import java.util.UUID;

final class PurposeTemplateAnnotationDocumentUploadOps implements DocumentUploadOps {

    private final SharedStepsContext sharedStepsContext;
    private final IM2MPurposeTemplateClient purposeTemplateClient;
    private final PollingService pollingService;

    PurposeTemplateAnnotationDocumentUploadOps(
        SharedStepsContext sharedStepsContext,
        IM2MPurposeTemplateClient purposeTemplateClient,
        PollingService pollingService
    ) {
        this.sharedStepsContext = sharedStepsContext;
        this.purposeTemplateClient = purposeTemplateClient;
        this.pollingService = pollingService;
    }

    @Override
    public UploadOperationResult upload(UploadRequest request, Resource uploadResource, String prettyName) {
        UUID purposeTemplateId = requirePurposeTemplateId();
        UUID answerId = requireRiskAnalysisAnswerId();

        HttpStatus uploadStatus = sharedStepsContext.getHttpCallExecutor().performCall(
            () -> purposeTemplateClient.uploadRiskAnalysisTemplateAnswerAnnotationDocument(
                purposeTemplateId,
                answerId,
                prettyName,
                uploadResource
            )
        );

        return new UploadOperationResult(
            uploadStatus,
            sharedStepsContext.getHttpCallExecutor().getResponse(),
            sharedStepsContext.getHttpCallExecutor().getErrorMessage()
        );
    }

    @Override
    public UUID extractDocumentId(Object uploadResponse) {
        if (uploadResponse instanceof Document document) {
            return document.getId();
        }
        return null;
    }

    @Override
    public void pollDocumentAvailability(UUID documentId) {
        UUID purposeTemplateId = requirePurposeTemplateId();

        pollingService.makePolling(
            () -> sharedStepsContext.getHttpCallExecutor().performCall(
                () -> purposeTemplateClient.getRiskAnalysisTemplateAnswerAnnotationDocument(
                    purposeTemplateId,
                    documentId
                )
            ),
            HttpStatus.OK::equals,
            status -> "getRiskAnalysisTemplateAnswerAnnotationDocument non disponibile, status=" + status
        );
    }

    private UUID requirePurposeTemplateId() {
        UUID purposeTemplateId = sharedStepsContext.getPurposeTemplateContext().getPurposeTemplateId();
        if (purposeTemplateId == null) {
            throw new IllegalStateException("purposeTemplateId assente nel contesto condiviso");
        }
        return purposeTemplateId;
    }

    private UUID requireRiskAnalysisAnswerId() {
        UUID answerId = sharedStepsContext.getPurposeTemplateContext().getRiskAnalysisAnswerId();
        if (answerId == null) {
            throw new IllegalStateException("riskAnalysisAnswerId assente nel contesto condiviso");
        }
        return answerId;
    }
}
