package it.pagopa.pn.interop.cucumber.utility.enums;

public enum ResolvableToken {
    ACTUAL("%actual"),
    NULL("%null"),
    EXPECTED("%expected"),
    RANDOM("%random"),
    BLANK("%blank"),

    EMPTY_LIST("%empty_list"),
    NULL_ELEMENT_LIST("%null_element_list");

    private final String value;

    ResolvableToken(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static ResolvableToken from(String raw) {
        if (raw == null) return null;

        String normalized = raw.trim().toLowerCase();
        for (ResolvableToken token : values()) {
            if (token.value.equals(normalized)) {
                return token;
            }
        }
        return null;
    }
}

