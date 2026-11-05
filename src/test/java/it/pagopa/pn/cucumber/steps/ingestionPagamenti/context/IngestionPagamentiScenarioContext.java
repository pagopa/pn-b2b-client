package it.pagopa.pn.cucumber.steps.ingestionPagamenti.context;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.dto.PaymentNotice;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Getter
@Setter
@ScenarioScope
public class IngestionPagamentiScenarioContext {

    /**
     * Avviso pagoPA della notifica (legale o bonaria) oggetto dello scenario.
     */
    private PaymentNotice notice;

    /**
     * Indica che la comunicazione bonaria viene inviata in modo che il mock di App IO ne segnali il pagamento.
     */
    private boolean paidAlsoOnAppIo;

    /**
     * Contenuto univoco dell'ultimo evento pubblicato, usato per ritrovarlo in DLQ o nei log.
     */
    private String lastEventMarker;

    /**
     * Contenuto univoco dell'ultimo evento malformato pubblicato: resta valido anche dopo la pubblicazione di altri eventi.
     */
    private String malformedEventMarker;

    private Instant lastEventPublishedAt;

    /**
     * Data di pagamento dell'ultimo pagamento pagoPA pubblicato: il flusso pagoPA la copia nell'eventTimestamp del
     * PAYMENT delle bonarie (WI-5.7), quindi indica se il PAYMENT è stato scritto da pagoPA.
     */
    private Instant pagoPaPaymentDateTime;
}
