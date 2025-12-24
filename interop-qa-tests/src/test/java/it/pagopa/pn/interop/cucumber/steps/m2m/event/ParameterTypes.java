package it.pagopa.pn.interop.cucumber.steps.m2m.event;

import io.cucumber.java.ParameterType;

public class ParameterTypes {

    @ParameterType("senza delega in erogazione|con delega in erogazione")
    public M2MDelegationEventConfig m2mEventDelegationConfig(String type) {
        return switch (type) {
            case "senza delega in erogazione" -> new M2MDelegationEventConfig(false, false);
            case "con delega in erogazione" -> new M2MDelegationEventConfig(true, false);
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        };
    }

}
