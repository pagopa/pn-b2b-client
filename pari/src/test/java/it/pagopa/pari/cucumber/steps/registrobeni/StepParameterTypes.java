package it.pagopa.pari.cucumber.steps.registrobeni;

import io.cucumber.java.ParameterType;
import it.pagopa.pari.registrobeni.domain.RdbRole;

public class StepParameterTypes {

    @ParameterType("NON_SONO|SONO")
    public ConsentAction consentAction(String consentAction) {
        return switch (consentAction) {
            case "NON_SONO" -> ConsentAction.NON_SONO;
            case "SONO" -> ConsentAction.SONO;
            default ->
                    throw new IllegalArgumentException("Invalid consent action: " + consentAction);
        };
    }

    public enum ConsentAction {
        NON_SONO,
        SONO
    }

    @ParameterType("PRODUTTORE_1|PRODUTTORE_2|INVITALIA_L1|INVITALIA_L2")
    public RdbRole rdbRole(String role) {
        return switch (role) {
            case "PRODUTTORE_1" -> RdbRole.PRODUTTORE_1;
            case "PRODUTTORE_2" -> RdbRole.PRODUTTORE_2;
            case "INVITALIA_L1" -> RdbRole.INVITALIA_L1;
            case "INVITALIA_L2" -> RdbRole.INVITALIA_L2;
            default ->
                    throw new IllegalArgumentException("Invalid RDB role: " + role);
        };
    }

}
