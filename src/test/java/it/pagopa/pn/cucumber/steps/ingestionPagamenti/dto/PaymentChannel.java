package it.pagopa.pn.cucumber.steps.ingestionPagamenti.dto;

import java.util.Arrays;

/**
 * Canali da cui SEND riceve la notizia di un pagamento di una comunicazione bonaria.
 * Il paymentSourceChannel del PAYMENT non li distingue: App IO scrive "IO" (SRS §3.3) e pagoPA copia
 * paymentInfo.paymentChannel dell'evento (WI-5.7), che il mock del Nodo pagoPA fissa anch'esso a "IO".
 */
public enum PaymentChannel {
    APP_IO("App IO"),
    PAGOPA("pagoPA");

    private final String label;

    PaymentChannel(String label) {
        this.label = label;
    }

    public static PaymentChannel fromLabel(String label) {
        return Arrays.stream(values())
                .filter(channel -> channel.label.equalsIgnoreCase(label))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Canale di pagamento non gestito: " + label));
    }
}
