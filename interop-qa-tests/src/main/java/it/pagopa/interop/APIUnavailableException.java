package it.pagopa.interop;

import lombok.experimental.StandardException;

/**
 * Utilizzata nei metodi dei clients per notificare la mancanza dell'implementazione di una delle
 * APIs modellate nelle interfacce ereditate
 */
@StandardException
public class APIUnavailableException extends RuntimeException {

}
