package it.pagopa.pn.client.b2b.pa.exception;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import lombok.Getter;


@Getter
public class PnB2bAssertionException extends PnInternalException {
    public PnB2bAssertionException(String message, String errorCode) {
        super(message, errorCode);
    }

    public PnB2bAssertionException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }

    public PnB2bAssertionException(String message, int httpStatus, String errorCode) {
        super(message, httpStatus, errorCode);
    }
}