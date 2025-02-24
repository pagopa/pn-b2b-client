package it.pagopa.pn.cucumber.steps.messaggiCortesiaBanche;

import io.cucumber.java.ParameterType;

import static it.pagopa.pn.cucumber.steps.messaggiCortesiaBanche.EmdCheckTppEndpoint.TOKEN_CHECK_TPP;
import static it.pagopa.pn.cucumber.steps.messaggiCortesiaBanche.EmdCheckTppEndpoint.EMD_CHECK_TPP;

public class EmdEndpointParameterTypes {

    @ParameterType("tokenCheckTPP | emdCheckTPP")
    public EmdCheckTppEndpoint emdEndpoint(String emdEndpoint) {
        return switch (emdEndpoint) {
            case "tokenCheckTPP" -> TOKEN_CHECK_TPP;
            case "emdCheckTPP" -> EMD_CHECK_TPP;
            default -> throw new IllegalArgumentException("Invalid emd endpoint: " + emdEndpoint);
        };
    }
}
