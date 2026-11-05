package it.pagopa.pn.cucumber.steps.ingestionPagamenti.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.nodopagopa.model.NoticePaymentRequest;
import it.pagopa.pn.client.b2b.pa.service.IPnNodoPagoPaMockClient;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.context.IngestionPagamentiScenarioContext;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.dto.PaymentEventAnomaly;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.dto.PaymentNotice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Pubblica gli eventi di pagamento tramite il mock del Nodo pagoPA di pn-external-channels, registrando nel contesto
 * dello scenario il contenuto con cui ritrovarli in DLQ o nei log. Il mock genera id, timestamp e i dati statici
 * dell'evento (fra cui paymentChannel "IO"): il test valorizza solo i campi che il mock accetta.
 */
@Component
@RequiredArgsConstructor
public class PagoPaPaymentEventService {

    private final IPnNodoPagoPaMockClient nodoPagoPaMockClient;
    private final IngestionPagamentiScenarioContext context;

    /**
     * L'evento si ritrova tramite il numero avviso. L'importo è in centesimi sia nell'avviso sia nel mock.
     * La data di pagamento è troncata al secondo, così il confronto con l'eventTimestamp del PAYMENT non dipende
     * dalla precisione con cui SEND la memorizza o la espone.
     */
    public void publishPaymentOf(PaymentNotice notice) {
        Instant paymentDateTime = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        nodoPagoPaMockClient.publishPaymentEvent(paymentRequestOf(notice, paymentDateTime));
        context.setPagoPaPaymentDateTime(paymentDateTime);
        trackPublishedEvent(notice.noticeCode());
    }

    /**
     * Il numero avviso è il riferimento più preciso; se l'anomalia lo ha rimosso si usa la data di pagamento,
     * che il mock pubblica così come viene inviata.
     */
    public void publishMalformed(PaymentEventAnomaly anomaly) {
        NoticePaymentRequest request = anomaly.applyTo(paymentRequestOf(PaymentNotice.unknownToSend(), Instant.now()));
        nodoPagoPaMockClient.publishPaymentEvent(request);
        String marker = request.getNoticeNumber() != null ? request.getNoticeNumber() : request.getPaymentDateTime();
        context.setMalformedEventMarker(marker);
        trackPublishedEvent(marker);
    }

    private static NoticePaymentRequest paymentRequestOf(PaymentNotice notice, Instant paymentDateTime) {
        return new NoticePaymentRequest()
                .noticeNumber(notice.noticeCode())
                .creditorTaxId(notice.creditorTaxId())
                .paymentDateTime(paymentDateTime.toString())
                .amount(String.valueOf(notice.amount()));
    }

    private void trackPublishedEvent(String marker) {
        context.setLastEventMarker(marker);
        context.setLastEventPublishedAt(Instant.now());
    }
}
