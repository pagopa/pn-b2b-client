package it.pagopa.pn.cucumber.steps.ingestionPagamenti.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.client.b2b.pa.model.kafka.PagoPaPaymentMessage;
import it.pagopa.pn.client.b2b.pa.model.kafka.PaymentEventRequest;
import it.pagopa.pn.client.b2b.pa.service.IPnKafkaMockClient;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.context.IngestionPagamentiScenarioContext;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.dto.PaymentChannel;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.dto.PaymentEventAnomaly;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.dto.PaymentNotice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Pubblica gli eventi di pagamento tramite pn-kafka-mock, registrando nel contesto dello scenario il contenuto
 * con cui ritrovarli in DLQ o nei log. Gli eventi validi usano l'API notice-payment del mock; quelli malformati
 * sono costruiti nel tracciato pagoPA e pubblicati come contenuto grezzo.
 */
@Component
@RequiredArgsConstructor
public class PagoPaPaymentEventService {

    private static final String EVENT_ID_PREFIX = "QA";
    private static final int MAX_EVENT_ID_LENGTH = 20;
    private static final int OVERSIZE_BYTES = 1024 * 1024 + 1024;

    private final IPnKafkaMockClient kafkaMockClient;
    private final IngestionPagamentiScenarioContext context;
    private final ObjectMapper objectMapper;

    /**
     * id e timestamp dell'evento li genera il mock: l'evento si ritrova tramite il numero avviso.
     * L'importo dell'avviso è in centesimi, quello del mock in euro.
     * Il canale di pagamento è esplicito perché diventa il paymentSourceChannel dell'elemento PAYMENT delle bonarie.
     */
    public void publishPaymentOf(PaymentNotice notice) {
        kafkaMockClient.publishPaymentEvent(PaymentEventRequest.builder()
                .noticeNumber(notice.noticeCode())
                .idPA(notice.creditorTaxId())
                .paymentDateTime(Instant.now().toString())
                .amount(BigDecimal.valueOf(notice.amount(), 2))
                .paymentChannel(PaymentChannel.PAGOPA.paymentSourceChannel())
                .build());
        trackPublishedEvent(notice.noticeCode());
    }

    public void publishMalformed(PaymentEventAnomaly anomaly) {
        PagoPaPaymentMessage validMessage = validPaymentOf(PaymentNotice.unknownToSend());
        String payload = anomaly.producesNonJsonContent()
                ? "evento non JSON " + validMessage.getId() + " " + validMessage.getDebtorPosition().getNoticeNumber()
                : toJson(anomaly.applyTo(validMessage));
        kafkaMockClient.publishRawPaymentEvent(payload);
        trackPublishedMalformedEvent(markerOf(payload, validMessage));
    }

    /**
     * Il limite di 1MB di pn-kafka-bridge si applica agli eventi malformati diretti in DLQ: l'evento è reso
     * malformato (importo assente) e l'identificativo resta nei primi 256 caratteri, riportati nel log FATAL.
     */
    public void publishOversizeMalformed() {
        PagoPaPaymentMessage validMessage = validPaymentOf(PaymentNotice.unknownToSend());
        PagoPaPaymentMessage oversize = PaymentEventAnomaly.AMOUNT_MISSING.applyTo(validMessage).toBuilder()
                .creditor(validMessage.getCreditor().toBuilder().companyName("A".repeat(OVERSIZE_BYTES)).build())
                .build();
        kafkaMockClient.publishRawPaymentEvent(toJson(oversize));
        trackPublishedMalformedEvent(validMessage.getId());
    }

    private PagoPaPaymentMessage validPaymentOf(PaymentNotice notice) {
        return PagoPaPaymentMessage.builder()
                .id(newEventId())
                .timestamp(Instant.now().toString())
                .debtorPosition(PagoPaPaymentMessage.DebtorPosition.builder().noticeNumber(notice.noticeCode()).build())
                .creditor(PagoPaPaymentMessage.Creditor.builder().idPA(notice.creditorTaxId()).build())
                .paymentInfo(PagoPaPaymentMessage.PaymentInfo.builder()
                        .paymentDateTime(Instant.now().toString())
                        .amount(notice.amount())
                        .build())
                .build();
    }

    private static String newEventId() {
        String random = UUID.randomUUID().toString().replace("-", "");
        return EVENT_ID_PREFIX + random.substring(0, MAX_EVENT_ID_LENGTH - EVENT_ID_PREFIX.length());
    }

    /**
     * L'identificativo è il riferimento più preciso; se l'anomalia lo ha rimosso si usa il numero avviso.
     */
    private static String markerOf(String payload, PagoPaPaymentMessage validMessage) {
        return payload.contains(validMessage.getId())
                ? validMessage.getId()
                : validMessage.getDebtorPosition().getNoticeNumber();
    }

    private void trackPublishedMalformedEvent(String marker) {
        context.setMalformedEventMarker(marker);
        trackPublishedEvent(marker);
    }

    private void trackPublishedEvent(String marker) {
        context.setLastEventMarker(marker);
        context.setLastEventPublishedAt(Instant.now());
    }

    private String toJson(PagoPaPaymentMessage message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Serializzazione dell'evento di pagamento non riuscita", e);
        }
    }
}
