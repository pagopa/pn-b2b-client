package it.pagopa.pn.interop.cucumber.steps.common.upload;

import org.springframework.core.io.Resource;

import java.util.UUID;

public interface DocumentUploadOps {
    UploadOperationResult upload(UploadRequest request, Resource uploadResource, String prettyName) throws Exception;

    UUID extractDocumentId(Object uploadResponse);

    void pollDocumentAvailability(UUID documentId);
}

