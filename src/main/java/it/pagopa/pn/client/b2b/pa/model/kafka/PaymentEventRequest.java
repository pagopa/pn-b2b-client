package it.pagopa.pn.client.b2b.pa.model.kafka;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Body di POST /kafka-mock-private/notice-payment (PaymentEventRequest dell'SRS, §6.8).
 * Obbligatori: noticeNumber, idPA, paymentDateTime. I campi non valorizzati assumono i default del mock,
 * che genera anche id, timestamp e version dell'evento pubblicato su Kafka.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentEventRequest {

    private String noticeNumber;
    private Integer modelType;
    private String iuv;
    private String iur;
    private String idPA;
    private String companyName;
    private String paymentDateTime;
    private String dueDate;
    private Integer totalNotice;
    private String paymentChannel;
    /**
     * Importo in euro (es. 10.50).
     */
    private BigDecimal amount;
    private Integer fee;
    private String paymentMethod;
}
