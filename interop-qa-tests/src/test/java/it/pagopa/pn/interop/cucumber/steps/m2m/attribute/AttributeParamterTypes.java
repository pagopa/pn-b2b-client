package it.pagopa.pn.interop.cucumber.steps.m2m.attribute;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.attribute.service.enums.AttributeRequestType;

public class AttributeParamterTypes {
    @ParameterType("valid|valido|invalid|invalido|null|nullo")
    public AttributeRequestType attributeRequestType(String type) {
        return AttributeRequestType.fromString(type);
    }
}
