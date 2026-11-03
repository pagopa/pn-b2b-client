package it.pagopa.pn.cucumber.steps.ingestionPagamenti.dto;

import it.pagopa.pn.client.b2b.pa.model.kafka.PagoPaPaymentMessage;

import java.util.Arrays;
import java.util.function.UnaryOperator;

/**
 * Anomalie che rendono un evento di pagamento non conforme alle regole di validazione di pn-kafka-bridge.
 * Le anomalie strutturali partono da un evento valido e ne alterano un campo; NOT_JSON produce un contenuto
 * non deserializzabile.
 */
public enum PaymentEventAnomaly {
    NOTICE_NUMBER_MISSING("numero avviso mancante",
            m -> m.toBuilder().debtorPosition(m.getDebtorPosition().toBuilder().noticeNumber(null).build()).build()),
    CREDITOR_TAX_ID_MISSING("codice fiscale dell'ente creditore mancante",
            m -> m.toBuilder().creditor(m.getCreditor().toBuilder().idPA(null).build()).build()),
    PAYMENT_DATE_MISSING("data di pagamento mancante",
            m -> m.toBuilder().paymentInfo(m.getPaymentInfo().toBuilder().paymentDateTime(null).build()).build()),
    AMOUNT_MISSING("importo mancante",
            m -> m.toBuilder().paymentInfo(m.getPaymentInfo().toBuilder().amount(null).build()).build()),
    ID_MISSING("identificativo mancante",
            m -> m.toBuilder().id(null).build()),
    // Limite di 20 caratteri ricavato empiricamente: non è previsto dall'SRS (REQ-ING-03 chiede solo che id sia valorizzato)
    ID_TOO_LONG("identificativo più lungo di 20 caratteri",
            m -> m.toBuilder().id(m.getId() + "-TOO-LONG").build()),
    DEBTOR_POSITION_MISSING("sezione posizione debitoria assente",
            m -> m.toBuilder().debtorPosition(null).build()),
    CREDITOR_MISSING("sezione ente creditore assente",
            m -> m.toBuilder().creditor(null).build()),
    PAYMENT_INFO_MISSING("sezione dati di pagamento assente",
            m -> m.toBuilder().paymentInfo(null).build()),
    NOT_JSON("contenuto non in formato JSON", UnaryOperator.identity());

    private final String label;
    private final UnaryOperator<PagoPaPaymentMessage> mutation;

    PaymentEventAnomaly(String label, UnaryOperator<PagoPaPaymentMessage> mutation) {
        this.label = label;
        this.mutation = mutation;
    }

    public PagoPaPaymentMessage applyTo(PagoPaPaymentMessage validMessage) {
        return mutation.apply(validMessage);
    }

    public boolean producesNonJsonContent() {
        return this == NOT_JSON;
    }

    public static PaymentEventAnomaly fromLabel(String label) {
        return Arrays.stream(values())
                .filter(anomaly -> anomaly.label.equalsIgnoreCase(label))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Anomalia dell'evento di pagamento non gestita: " + label));
    }
}
