package it.pagopa.pn.cucumber.steps.ingestionPagamenti.dto;

import java.util.Arrays;

/**
 * Canali da cui SEND riceve il pagamento di una comunicazione bonaria. Il paymentSourceChannel del PAYMENT non li
 * distingue: il mock del Nodo pagoPA pubblica anch'esso il canale "IO".
 */
public enum PaymentChannel {
    APP_IO("App IO"),
    PAGOPA("pagoPA");

    private final String label;

    PaymentChannel(String label) {
        this.label = label;
    }

    /**
     * Canale corrispondente all'etichetta usata negli scenari.
     */
    public static PaymentChannel fromLabel(String label) {
        return Arrays.stream(values())
                .filter(channel -> channel.label.equalsIgnoreCase(label))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Canale di pagamento non gestito: " + label));
    }
}
