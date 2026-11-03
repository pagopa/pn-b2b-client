package it.pagopa.pn.client.b2b.pa.model.kafka;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Evento di pagamento nel tracciato pubblicato da pagoPA e consumato da pn-kafka-bridge (SRS §6.1).
 * Campi obbligatori per il bridge (SRS §5.1.3): id, debtorPosition.noticeNumber, creditor.idPA,
 * paymentInfo.paymentDateTime, paymentInfo.amount.
 * Usato solo per costruire gli eventi grezzi (malformati o oltre 1MB): gli eventi validi passano da {@link PaymentEventRequest}.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagoPaPaymentMessage {

    private String id;
    private String timestamp;
    private DebtorPosition debtorPosition;
    private Creditor creditor;
    private PaymentInfo paymentInfo;

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DebtorPosition {
        private String noticeNumber;
        private String iuv;
    }

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Creditor {
        private String idPA;
        private String companyName;
    }

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PaymentInfo {
        private String paymentDateTime;
        private Integer amount;
    }
}
