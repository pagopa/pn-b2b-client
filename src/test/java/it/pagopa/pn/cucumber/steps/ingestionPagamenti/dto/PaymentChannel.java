package it.pagopa.pn.cucumber.steps.ingestionPagamenti.dto;

import java.util.Arrays;

/**
 * Canali da cui SEND riceve la notizia di un pagamento di una comunicazione bonaria, con il valore che ciascuno
 * scrive in details.paymentSourceChannel dell'elemento PAYMENT.
 * App IO: "IO" (SRS §3.3). pagoPA: paymentInfo.paymentChannel dell'evento (SRS §6.2, §6.6), valorizzato dal test.
 */
public enum PaymentChannel {
    APP_IO("App IO", "IO"),
    PAGOPA("pagoPA", "onLine");

    private final String label;
    private final String paymentSourceChannel;

    PaymentChannel(String label, String paymentSourceChannel) {
        this.label = label;
        this.paymentSourceChannel = paymentSourceChannel;
    }

    public String paymentSourceChannel() {
        return paymentSourceChannel;
    }

    public static PaymentChannel fromLabel(String label) {
        return Arrays.stream(values())
                .filter(channel -> channel.label.equalsIgnoreCase(label))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Canale di pagamento non gestito: " + label));
    }
}
