package it.pagopa.pn.cucumber.steps.pa.domain;

import io.cucumber.java.ParameterType;

import static it.pagopa.pn.cucumber.steps.pa.domain.DocumentTransformation.NORMALIZZAZIONE;
import static it.pagopa.pn.cucumber.steps.pa.domain.DocumentTransformation.RASTERIZZAZIONE;

public class DocumentTransformationParameterTypes {

    @ParameterType("rasterizzazione|normalizzazione")
    public DocumentTransformation documentTransformation(String transformation) {
        return switch (transformation) {
            case "rasterizzazione" -> RASTERIZZAZIONE;
            case "normalizzazione" -> NORMALIZZAZIONE;
            default -> throw new IllegalArgumentException("Invalid document transformation: " + transformation);
        };
    }
}
