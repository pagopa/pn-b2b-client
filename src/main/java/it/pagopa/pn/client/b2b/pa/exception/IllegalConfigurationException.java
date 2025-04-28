package it.pagopa.pn.client.b2b.pa.exception;

import lombok.Getter;


@Getter
public class IllegalConfigurationException extends RuntimeException {
    private final String message;

    public IllegalConfigurationException(String message) {
        this.message = message;
    }
}