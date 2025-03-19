package it.pagopa.interop.authorization.domain;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("admin"),
    API("api"),
    SECURITY("security"),
    SUPPORT("support"),
    API_SECURITY("api,security");

    private final String value;

    Role(String value) {
        this.value = value;
    }

}
