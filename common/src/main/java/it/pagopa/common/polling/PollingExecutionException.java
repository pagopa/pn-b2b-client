package it.pagopa.common.polling;

/**
 * Errore tecnico durante il polling: eccezione della chiamata o della condizione, oppure thread interrotto.
 */
public class PollingExecutionException extends RuntimeException {
    public PollingExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
