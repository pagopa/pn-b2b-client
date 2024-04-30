package it.pagopa.pn.client.b2b.pa.exception;

import lombok.Getter;


@Getter
public class PnB2bException extends RuntimeException {
    private final String message;

    public PnB2bException(String message) {
        this.message = message;
    }
}