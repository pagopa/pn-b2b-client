package it.pagopa.pn.client.b2b.pa.exception;

import it.pagopa.pn.commons.exceptions.PnRuntimeException;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class PnB2bAssertionClientException extends PnRuntimeException {
    private String secondaryMessage;


    public PnB2bAssertionClientException(String message, String description, String errorCode) {
        super(message, description, HttpStatus.NOT_FOUND.value(), errorCode, null, null);
    }

    public PnB2bAssertionClientException(String message, String description, String errorCode, String secondaryMessage) {
        super(message, description, HttpStatus.NOT_FOUND.value(), errorCode, null, null);
        this.secondaryMessage = secondaryMessage;
    }

    public PnB2bAssertionClientException(String message, String description, String errorCode, Throwable ex) {
        super(message, description, HttpStatus.NOT_FOUND.value(), errorCode, null, null, ex);
    }

    public PnB2bAssertionClientException(String message, String description, String errorCode, String secondaryMessage, Throwable ex) {
        super(message, description, HttpStatus.NOT_FOUND.value(), errorCode, null, null, ex);
        this.secondaryMessage = secondaryMessage;
    }
}