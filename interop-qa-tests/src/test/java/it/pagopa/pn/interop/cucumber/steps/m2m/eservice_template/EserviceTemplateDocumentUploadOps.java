package it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template;

import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IM2MEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Document;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.upload.DocumentUploadOps;
import it.pagopa.pn.interop.cucumber.steps.common.upload.UploadOperationResult;
import it.pagopa.pn.interop.cucumber.steps.common.upload.UploadRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;

import java.util.UUID;

final class EserviceTemplateDocumentUploadOps implements DocumentUploadOps {

    private final SharedStepsContext sharedStepsContext;
    private final IM2MEServiceTemplateClient eServiceTemplateClient;
    private final PollingService pollingService;

    EserviceTemplateDocumentUploadOps(
        SharedStepsContext sharedStepsContext,
        IM2MEServiceTemplateClient eServiceTemplateClient,
        PollingService pollingService
    ) {
        this.sharedStepsContext = sharedStepsContext;
        this.eServiceTemplateClient = eServiceTemplateClient;
        this.pollingService = pollingService;
    }

    @Override
    public UploadOperationResult upload(UploadRequest request, Resource uploadResource, String prettyName) {
        UUID templateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
        UUID versionId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId();

        HttpStatus uploadStatus = sharedStepsContext.getHttpCallExecutor().performCall(
            () -> eServiceTemplateClient.uploadDocument(templateId, versionId, uploadResource, prettyName)
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
        UUID templateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
        UUID versionId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId();

        pollingService.makePolling(
            () -> sharedStepsContext.getHttpCallExecutor().performCall(
                () -> eServiceTemplateClient.downloadDocument(templateId, versionId, documentId)
            ),
            HttpStatus.OK::equals,
            status -> "downloadDocument non disponibile, status=" + status
        );
    }
}

