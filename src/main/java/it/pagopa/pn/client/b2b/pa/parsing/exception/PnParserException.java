package it.pagopa.pn.client.b2b.pa.parsing.exception;

import lombok.Getter;


@Getter
public class PnParserException extends IllegalStateException {
    private final String message;

    public PnParserException(String message) {
        this.message = message;
    }
}
