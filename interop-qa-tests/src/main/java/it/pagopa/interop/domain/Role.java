package it.pagopa.interop.domain;

public enum Role {
    ADMIN("admin"),
    API("api"),
    SECURITY("security"),
    SUPPORT("support"),
    API_SECURITY("api,security");

    Role(String admin) {}
}
