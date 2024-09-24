package it.pagopa.pn.cucumber.steps.pg;

public enum VirtualKeyState {
    BLOCK("BLOCK"),
    BLOCKED("BLOCKED"),
    ROTATE("ROTATE"),
    ROTATED("ROTATE"),
    ENABLE("ENABLE"),
    REACTIVE("ENABLE"),
    CANCELLED("CANCELLED");

    private String state;

    VirtualKeyState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}
