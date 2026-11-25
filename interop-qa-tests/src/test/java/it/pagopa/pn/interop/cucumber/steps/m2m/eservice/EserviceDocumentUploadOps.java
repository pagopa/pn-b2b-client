package it.pagopa.pn.interop.cucumber.steps.m2m.eservice;

import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.eservice.service.IM2MEserviceDescriptorClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Document;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.upload.DocumentUploadOps;
import it.pagopa.pn.interop.cucumber.steps.common.upload.UploadOperationResult;
import it.pagopa.pn.interop.cucumber.steps.common.upload.UploadRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;

import java.util.UUID;

final class EserviceDocumentUploadOps implements DocumentUploadOps {

    private final SharedStepsContext sharedStepsContext;
    private final IM2MEserviceDescriptorClient descriptorClient;
    private final PollingService pollingService;

    EserviceDocumentUploadOps(
        SharedStepsContext sharedStepsContext,
        IM2MEserviceDescriptorClient descriptorClient,
        PollingService pollingService
    ) {
        this.sharedStepsContext = sharedStepsContext;
        this.descriptorClient = descriptorClient;
        this.pollingService = pollingService;
    }

    @Override
    public UploadOperationResult upload(UploadRequest request, Resource uploadResource, String prettyName) {
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();

        HttpStatus uploadStatus = sharedStepsContext.getHttpCallExecutor().performCall(
            () -> descriptorClient.uploadDocument(eServiceId, descriptorId, uploadResource, prettyName)
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
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();

        pollingService.makePolling(
            () -> sharedStepsContext.getHttpCallExecutor().performCall(
                () -> descriptorClient.downloadDocument(eServiceId, descriptorId, documentId)
            ),
            HttpStatus.OK::equals,
            status -> "downloadDocument non disponibile, status=" + status
        );
    }
}

