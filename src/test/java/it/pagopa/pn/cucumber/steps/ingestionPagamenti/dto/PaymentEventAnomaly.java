package it.pagopa.pn.cucumber.steps.ingestionPagamenti.dto;

import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.nodopagopa.model.NoticePaymentRequest;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Anomalie riproducibili con il mock del Nodo pagoPA: un campo obbligatorio omesso viene pubblicato assente.
 */
public enum PaymentEventAnomaly {
    NOTICE_NUMBER_MISSING("numero avviso mancante", request -> request.setNoticeNumber(null)),
    CREDITOR_TAX_ID_MISSING("codice fiscale dell'ente creditore mancante", request -> request.setCreditorTaxId(null)),
    PAYMENT_DATE_MISSING("data di pagamento mancante", request -> request.setPaymentDateTime(null));

    private final String label;
    private final Consumer<NoticePaymentRequest> mutation;

    PaymentEventAnomaly(String label, Consumer<NoticePaymentRequest> mutation) {
        this.label = label;
        this.mutation = mutation;
    }

    /**
     * Applica l'anomalia alla richiesta e la restituisce.
     */
    public NoticePaymentRequest applyTo(NoticePaymentRequest validRequest) {
        mutation.accept(validRequest);
        return validRequest;
    }

    /**
     * Anomalia corrispondente all'etichetta usata negli scenari.
     */
    public static PaymentEventAnomaly fromLabel(String label) {
        return Arrays.stream(values())
                .filter(anomaly -> anomaly.label.equalsIgnoreCase(label))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Anomalia dell'evento di pagamento non gestita: " + label));
    }
}
