package it.pagopa.pn.cucumber.steps.pg;

import lombok.Data;

public enum VirtualKeyState {
    BLOCKED("BLOCK"),
    ROTATED("ROTATE"),
    ENABLE("ENABLE"),
    CANCELLED("CANCELLED");

    private String state;

    VirtualKeyState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}
