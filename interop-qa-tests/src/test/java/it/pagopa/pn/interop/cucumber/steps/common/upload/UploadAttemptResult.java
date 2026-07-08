package it.pagopa.pn.interop.cucumber.steps.common.upload;

public record UploadAttemptResult(String fileType, String fileExtension, boolean success, String errorMessage) {
}

