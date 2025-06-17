package it.pagopa.pn.interop.cucumber.steps.m2m.common;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.pn.interop.cucumber.steps.m2m.attribute.AttributeSteps;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.EserviceDescriptorSteps;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.EserviceSteps;
import org.apache.commons.lang3.tuple.Pair;

public class ParameterTypes {

    @ParameterType("certifiedAttribute|descriptor|eService")
    public Pair<String, Class<? extends ICommonSteps>> entityType(String type) {
        return switch (type) {
            case "certifiedAttribute" -> Pair.of("attributeSteps", AttributeSteps.class);
            case "descriptor" -> Pair.of("eserviceDescriptorSteps", EserviceDescriptorSteps.class);
            case "eService" -> Pair.of("eserviceSteps", EserviceSteps.class);
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        };
    }

    @ParameterType("invalid|invalido|null|nullo|inesistente")
    public EntityIdType entityIdType(String idType) {
        return EntityIdType.fromString(idType);
    }

}
