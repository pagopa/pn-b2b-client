package it.pagopa.pn.interop.cucumber.steps.m2m.common;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.common.enums.EntityIdType;

import java.util.HashMap;
import java.util.Map;

public class ParameterTypes {

    private static final Map<String, ICommonSteps> REGISTRY = new HashMap<>();

    public static void register(String name, ICommonSteps steps) {
        REGISTRY.put(name, steps);
    }

    @ParameterType("eService|descriptor|certifiedAttribute")
    public ICommonSteps entityType(String name) {
        ICommonSteps steps = REGISTRY.get(name);
        if (steps == null) {
            throw new IllegalArgumentException("No step implementation registered for entityType: " + name);
        }
        return steps;
    }


    @ParameterType("invalid|invalido|null|nullo|inesistente")
    public EntityIdType entityIdType(String idType) {
        return EntityIdType.fromString(idType);
    }

}
