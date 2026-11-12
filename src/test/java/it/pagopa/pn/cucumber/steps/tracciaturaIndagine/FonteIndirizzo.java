package it.pagopa.pn.cucumber.steps.tracciaturaIndagine;

import java.util.Arrays;

/**
 * Valori di ReceiverAddressSourceType (pn-timeline-service).
 */
public enum FonteIndirizzo {
    MITTENTE,
    ANPR,
    REGISTRO_IMPRESE,
    INDAGINE;

    public static FonteIndirizzo fromValue(String value) {
        return Arrays.stream(values())
                .filter(fonte -> fonte.name().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Fonte dell'indirizzo non gestita: " + value
                        + " (valori ammessi: " + Arrays.toString(values()) + ")"));
    }
}
