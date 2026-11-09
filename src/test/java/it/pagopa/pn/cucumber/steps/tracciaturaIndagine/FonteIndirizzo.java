package it.pagopa.pn.cucumber.steps.tracciaturaIndagine;

import java.util.Arrays;

/**
 * Fonte dell'indirizzo utilizzato per un invio cartaceo, come riportata in details.receiverAddressSourceType.
 * Valori allineati all'enum ReceiverAddressSourceType di pn-timeline-service (schemas-pn-timeline-service.yaml),
 * condiviso con pn-paper-channel, pn-delivery-push-workflow e pn-workflow-manager:
 * <ul>
 *     <li>MITTENTE: indirizzo fornito dalla PA (nessuna validazione VAS);</li>
 *     <li>ANPR / REGISTRO_IMPRESE: indirizzo dei registri nazionali, dal VAS (PUBLIC_REGISTRY_VALIDATION_RESPONSE)
 *     o dalla ricerca per il secondo invio, in base al tipo di destinatario (PF / PG);</li>
 *     <li>INDAGINE: indirizzo individuato dal postino, solo per il secondo invio.</li>
 * </ul>
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
