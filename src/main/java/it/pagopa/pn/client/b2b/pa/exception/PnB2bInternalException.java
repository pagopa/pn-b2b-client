package it.pagopa.pn.client.b2b.pa.exception;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import lombok.Getter;


@Getter
public class PnB2bInternalException extends PnInternalException {
    private String secondaryMessage;


    public PnB2bInternalException(String message, String errorCode) {
        super(message, errorCode);
    }

    public PnB2bInternalException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }

    public PnB2bInternalException(String message, String errorCode, String secondaryMessage) {
        super(message, errorCode);
        this.secondaryMessage = secondaryMessage;
    }

    public PnB2bInternalException(String message, String errorCode, String secondaryMessage, Throwable cause) {
        super(message, errorCode, cause);
        this.secondaryMessage = secondaryMessage;
    }
}