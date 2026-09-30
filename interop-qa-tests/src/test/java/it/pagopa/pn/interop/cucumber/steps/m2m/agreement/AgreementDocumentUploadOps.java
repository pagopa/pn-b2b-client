package it.pagopa.pn.interop.cucumber.steps.m2m.agreement;

import it.pagopa.interop.agreement.service.IM2MAgreementClient;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Document;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.upload.DocumentUploadOps;
import it.pagopa.pn.interop.cucumber.steps.common.upload.UploadOperationResult;
import it.pagopa.pn.interop.cucumber.steps.common.upload.UploadRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;

import java.util.UUID;

final class AgreementDocumentUploadOps implements DocumentUploadOps {

    private final SharedStepsContext sharedStepsContext;
    private final IM2MAgreementClient agreementClient;
    private final PollingService pollingService;

    AgreementDocumentUploadOps(
        SharedStepsContext sharedStepsContext,
        IM2MAgreementClient agreementClient,
        PollingService pollingService
    ) {
        this.sharedStepsContext = sharedStepsContext;
        this.agreementClient = agreementClient;
        this.pollingService = pollingService;
    }

    @Override
    public UploadOperationResult upload(UploadRequest request, Resource uploadResource, String prettyName) {
        UUID agreementId = sharedStepsContext.getAgreementId();

        HttpStatus uploadStatus = sharedStepsContext.getHttpCallExecutor().performCall(
            () -> agreementClient.uploadConsumerDocument(agreementId, uploadResource, prettyName)
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
        UUID agreementId = sharedStepsContext.getAgreementId();

        pollingService.makePolling(
            () -> sharedStepsContext.getHttpCallExecutor().performCall(
                () -> agreementClient.getConsumerDocument(agreementId, documentId)
            ),
            HttpStatus.OK::equals,
            status -> "getConsumerDocument non disponibile, status=" + status
        );
    }
}

