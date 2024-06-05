package it.pagopa.pn.client.b2b.pa.exception;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import lombok.Getter;


@Getter
public class PnB2bReadFileException extends PnInternalException {
    private String secondaryMessage;


    public PnB2bReadFileException(String message, String errorCode) {
        super(message, errorCode);
    }

    public PnB2bReadFileException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }

    public PnB2bReadFileException(String message, int httpStatus, String errorCode) {
        super(message, httpStatus, errorCode);
    }
}