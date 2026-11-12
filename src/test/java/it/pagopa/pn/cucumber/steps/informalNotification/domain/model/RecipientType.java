package it.pagopa.pn.cucumber.steps.informalNotification.domain.model;

public enum RecipientType {
    PF,
    PG;

    public static RecipientType fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (RecipientType b : RecipientType.values()) {
            if (b.name().equalsIgnoreCase(value.trim())) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
