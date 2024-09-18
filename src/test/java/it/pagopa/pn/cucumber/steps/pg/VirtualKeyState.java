package it.pagopa.pn.cucumber.steps.pg;

import lombok.Data;

public enum VirtualKeyState {
    BLOCKED("blocked"),
    ROTATED("rotated"),
    ACTIVE("active"),
    CANCELLED("cancelled");

    private String state;

    VirtualKeyState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}
