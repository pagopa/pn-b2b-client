package it.pagopa.pn.client.b2b.pa.polling.exception;


public class PnPollingException extends IllegalStateException {
    private final String message;

    public PnPollingException(String message) {
        this.message = message;
    }
}