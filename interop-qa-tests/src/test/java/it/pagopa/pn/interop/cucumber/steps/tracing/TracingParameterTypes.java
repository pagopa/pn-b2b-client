package it.pagopa.pn.interop.cucumber.steps.tracing;

import io.cucumber.java.ParameterType;

public class TracingParameterTypes {

    @ParameterType("GET|POST")
    public String method(String value) {
        return value;
    }

    @ParameterType("endpoint|id")
    public String subpath(String value) {
        return value;
    }

    @ParameterType("not found|bad request")
    public String esito(String value) {
        return value;
    }
}
