package it.pagopa.pn.interop.cucumber.steps.common.upload;

import org.springframework.http.HttpStatus;

public record UploadOperationResult(HttpStatus status, Object response, String errorMessage) {

    public boolean isSuccess() {
        return status != null && status.is2xxSuccessful();
    }
}

