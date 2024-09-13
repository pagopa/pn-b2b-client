package it.pagopa.pn.cucumber.steps.pg;

public enum PublicKeyStates {
    BLOCKED("blocked"),
    ROTATED("rotated"),
    ACTIVE("active"),
    CANCELLED("cancelled");

    private String state;

    PublicKeyStates(String state) {
        this.state = state;
    }
}
