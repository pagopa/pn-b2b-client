package it.pagopa.pn.interop.cucumber.steps.common.upload;

import org.springframework.http.HttpStatus;

public record UploadAttemptResult(
	String fileType,
	String fileExtension,
	HttpStatus status,
	boolean success,
	String errorMessage
) {
}

