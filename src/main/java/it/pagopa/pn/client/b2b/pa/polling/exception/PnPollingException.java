package it.pagopa.pn.client.b2b.pa.polling.exception;

import lombok.Getter;

@Getter
public class PnPollingException extends IllegalStateException {
    private final String message;

    public PnPollingException(String message) {
        this.message = message;
    }
}