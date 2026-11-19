package it.pagopa.pn.cucumber.steps.ingestionPagamenti.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.nodopagopa.model.NoticePaymentRequest;
import it.pagopa.pn.client.b2b.pa.service.IPnKafkaMockClient;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.context.PaymentEventsScenarioContext;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.dto.PaymentEventAnomaly;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.dto.PaymentNotice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Pubblica gli eventi di pagamento tramite il mock del Nodo pagoPA, registrando nel contesto dello scenario il
 * contenuto con cui ritrovarli in DLQ o nei log.
 */
@Component
@RequiredArgsConstructor
public class PagoPaPaymentEventService {

    private static final ZoneId ROME = ZoneId.of("Europe/Rome");
    // Ora locale di Roma, senza offset, con 6 decimali di secondo: il formato degli eventi pagoPA
    private static final DateTimeFormatter PAGOPA_DATE_TIME = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSSSSS");

    private final IPnKafkaMockClient nodoPagoPaMockClient;
    private final PaymentEventsScenarioContext context;

    /**
     * Pubblica il pagamento dell'avviso. La data è troncata al secondo, così il confronto con l'eventTimestamp del
     * PAYMENT non dipende dalla precisione con cui SEND la memorizza.
     */
    public void publishPaymentOf(PaymentNotice notice) {
        Instant paymentDateTime = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        nodoPagoPaMockClient.publishPaymentEvent(paymentRequestOf(notice, paymentDateTime));
        context.setPagoPaPaymentDateTime(paymentDateTime);
        trackPublishedEvent(notice.noticeCode());
    }

    /**
     * Pubblica un evento con l'anomalia indicata. Il riferimento per ritrovarlo è il numero avviso o, se l'anomalia
     * lo ha rimosso, la data di pagamento.
     */
    public void publishMalformed(PaymentEventAnomaly anomaly) {
        NoticePaymentRequest request = anomaly.applyTo(paymentRequestOf(PaymentNotice.unknownToSend(), Instant.now()));
        nodoPagoPaMockClient.publishPaymentEvent(request);
        String marker = request.getNoticeNumber() != null ? request.getNoticeNumber() : request.getPaymentDateTime();
        context.setMalformedEventMarker(marker);
        trackPublishedEvent(marker);
    }

    // L'importo dell'avviso è in centesimi, l'evento pagoPA lo riporta in euro
    private static NoticePaymentRequest paymentRequestOf(PaymentNotice notice, Instant paymentDateTime) {
        return new NoticePaymentRequest()
                .noticeNumber(notice.noticeCode())
                .creditorTaxId(notice.creditorTaxId())
                .paymentDateTime(PAGOPA_DATE_TIME.format(paymentDateTime.atZone(ROME)))
                .amount(BigDecimal.valueOf(notice.amount(), 2).toPlainString());
    }

    private void trackPublishedEvent(String marker) {
        context.setLastEventMarker(marker);
        context.setLastEventPublishedAt(Instant.now());
    }
}
