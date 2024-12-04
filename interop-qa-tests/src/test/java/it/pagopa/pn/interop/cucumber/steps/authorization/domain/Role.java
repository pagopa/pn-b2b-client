package it.pagopa.pn.interop.cucumber.steps.authorization.domain;

public enum Role {
    ADMIN("admin"),
    API("api"),
    SECURITY("security"),
    SUPPORT("support"),
    API_SECURITY("api,security");

    Role(String admin) {}
}
