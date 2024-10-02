package it.pagopa.pn.cucumber.steps.pg;

public enum VirtualKeyState {
    BLOCK("BLOCK"),
    BLOCKED("BLOCKED"),
    ROTATE("ROTATE"),
    ROTATED("ROTATED"),
    ENABLE("ENABLE"),
    REACTIVE("ENABLED"),
    CANCELLED("CANCELLED"),
   // ENABLED("ENABLED"),
    UNKNOWN("UNKNOWN");

    private String state;

    VirtualKeyState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}
