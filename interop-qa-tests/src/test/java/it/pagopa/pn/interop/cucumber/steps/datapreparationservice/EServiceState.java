package it.pagopa.pn.interop.cucumber.steps.datapreparationservice;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

// DEV. NOTE 19/03/2026 al momento non c'è una classe che modella lo stato dell'e-service in sé, ma solo del suo
// ultimo descriptor
public enum EServiceState {
    DRAFT("DRAFT"),

    PUBLISHED("PUBLISHED"),

    DEPRECATED("DEPRECATED"),

    SUSPENDED("SUSPENDED"),

    ARCHIVED("ARCHIVED"),

    WAITING_FOR_APPROVAL("WAITING_FOR_APPROVAL");

    private String value;

    EServiceState(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static EServiceState fromValue(String value) {
        for (EServiceState b : EServiceState.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

