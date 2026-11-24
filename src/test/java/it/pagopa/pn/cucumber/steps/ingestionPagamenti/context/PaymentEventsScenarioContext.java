package it.pagopa.pn.cucumber.steps.ingestionPagamenti.context;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.dto.PaymentNotice;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
@Getter
@Setter
@ScenarioScope
public class PaymentEventsScenarioContext {

    /**
     * Avviso pagoPA della notifica (legale o bonaria) oggetto dello scenario.
     */
    private PaymentNotice notice;

    /**
     * IUN della notifica precedente, quando lo scenario ne crea una seconda con lo stesso avviso.
     */
    private String previousNotificationIun;

    /**
     * Stato HTTP con cui SEND ha rifiutato la seconda notifica con lo stesso avviso; null se è stata accettata.
     */
    private Integer secondNotificationRejectionStatus;

    /**
     * Indica che la comunicazione bonaria viene inviata in modo che il mock di App IO ne segnali il pagamento.
     */
    private boolean paidAlsoOnAppIo;

    /**
     * Indica che il destinatario è raggiungibile su App IO ma il mock non segnala mai il pagamento.
     */
    private boolean reachableOnAppIoWithoutPayment;

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
     * Stato della notifica legale al termine del workflow, prima del pagamento.
     */
    private String legalStatusBeforePayment;

    /**
     * Durata del primo canale della campagna della comunicazione bonaria.
     */
    private Duration firstChannelTimeout;

    /**
     * Data dell'ultimo pagamento pagoPA pubblicato: indica se il PAYMENT di una bonaria è stato scritto da pagoPA.
     */
    private Instant pagoPaPaymentDateTime;
}
