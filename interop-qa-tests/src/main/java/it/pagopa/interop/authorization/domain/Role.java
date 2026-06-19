package it.pagopa.interop.authorization.domain;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("admin"),
    API("api"),
    SECURITY("security"),
    SUPPORT("support"),
    API_SECURITY("api,security"),
    REVIEWER("reviewer"),
    VIEWER("viewer");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public static Role fromValue(String value) {
        for (Role role : Role.values()) {
            if (role.getValue().equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + value);
    }

}
