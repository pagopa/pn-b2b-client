package it.pagopa.interop.agreement.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ClientType {
    CONSUMER,
    PRODUCER;

    @JsonCreator
    public static ClientType fromValue(String value) {
        for (ClientType b : ClientType.values()) {
            if (b.name().equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
