package it.pagopa.pn.interop.cucumber.steps.m2m.eservice;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.eservice.service.enums.EserviceRequestType;
import it.pagopa.interop.eservice.service.enums.EserviceEntityType;

public class EserviceParameterTypes {
    @ParameterType("eService|descriptor|eServices|descriptors")
    public EserviceEntityType entityType(String type) {
        return EserviceEntityType.fromString(type);
    }

    @ParameterType("invalido|inesistente")
    public EserviceRequestType descriptorRequestType(String type) {
        return EserviceRequestType.fromString(type);
    }
}
