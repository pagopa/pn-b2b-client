package it.pagopa.pn.cucumber.steps.ingestionPagamenti;

import io.cucumber.java.en.When;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.context.IngestionPagamentiScenarioContext;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.dto.PaymentChannel;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.dto.PaymentEventAnomaly;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.dto.PaymentNotice;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.service.PagoPaPaymentEventService;
import it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheB2bSteps;
import it.pagopa.pn.cucumber.steps.pa.PresaInCaricoNoticaBonariaSteps;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comunicazione dei pagamenti a SEND dai diversi canali: pagoPA (via pn-kafka-mock), App IO e API B2B dell'ente.
 */
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class IngestionPagamentiEventiSteps {

    private static final String INFORMAL_PAYMENT_CATEGORY = "PAYMENT";

    private final IngestionPagamentiScenarioContext context;
    private final PagoPaPaymentEventService paymentEventService;
    private final PresaInCaricoNoticaBonariaSteps informalNotificationSteps;
    private final AvanzamentoNotificheB2bSteps legalNotificationSteps;

    @When("pagoPA comunica il pagamento dell'avviso")
    public void pagoPaNotifiesPayment() {
        assertThat(context.getNotice()).as("Nessun avviso pagoPA preparato nello scenario").isNotNull();
        paymentEventService.publishPaymentOf(context.getNotice());
    }

    @When("pagoPA comunica il pagamento di un avviso sconosciuto a SEND")
    public void pagoPaNotifiesUnknownNoticePayment() {
        context.setNotice(PaymentNotice.unknownToSend());
        paymentEventService.publishPaymentOf(context.getNotice());
    }

    @When("pagoPA pubblica un evento di pagamento con {string}")
    public void pagoPaPublishesMalformedEvent(String anomaly) {
        paymentEventService.publishMalformed(PaymentEventAnomaly.fromLabel(anomaly));
    }

    @When("pagoPA pubblica un evento di pagamento di dimensione superiore a 1MB")
    public void pagoPaPublishesOversizeEvent() {
        paymentEventService.publishOversizeMalformed();
    }

    @When("l'ente comunica il pagamento dell'avviso tramite API B2B")
    public void senderNotifiesPaymentViaB2b() {
        legalNotificationSteps.laNotificaVienePagata();
    }

    /**
     * Il pagamento da App IO non viene inviato dal test: lo segnala il mock di App IO al polling di pn-io-connector,
     * perché il messaggio App IO della comunicazione bonaria ha nell'oggetto la sequenza OK_READ_THEN_PAID (PAID dopo 90 secondi).
     * Lo step attende il PAYMENT scritto dal flusso App IO.
     */
    @When("App IO comunica il pagamento dell'avviso")
    public void appIoNotifiesPayment() {
        assertThat(context.isPaidAlsoOnAppIo()).as("Lo scenario deve prevedere il pagamento tramite App IO").isTrue();
        informalNotificationSteps.verifyTimelineElementArrive(INFORMAL_PAYMENT_CATEGORY);
    }

    /**
     * Il pagamento da App IO non viene inviato dal test: lo segnala il mock di App IO al polling di pn-io-connector,
     * perché il messaggio App IO della comunicazione bonaria ha nell'oggetto la sequenza OK_READ_THEN_PAID (PAID dopo 90 secondi).
     * In entrambi gli ordini si attende il PAYMENT del primo canale prima che possa intervenire il secondo:
     * se l'ordine non viene rispettato, la verifica sul paymentSourceChannel del PAYMENT lo rileva.
     */
    @When("il pagamento dell'avviso viene comunicato prima da {string} e poi da {string}")
    public void paymentNotifiedByBothChannels(String firstChannel, String secondChannel) {
        PaymentChannel first = PaymentChannel.fromLabel(firstChannel);
        assertThat(PaymentChannel.fromLabel(secondChannel)).as("I due canali di pagamento devono essere diversi").isNotEqualTo(first);
        assertThat(context.isPaidAlsoOnAppIo()).as("Lo scenario deve prevedere il pagamento anche tramite App IO").isTrue();

        if (first == PaymentChannel.APP_IO) {
            informalNotificationSteps.verifyTimelineElementArrive(INFORMAL_PAYMENT_CATEGORY);
            pagoPaNotifiesPayment();
        } else {
            pagoPaNotifiesPayment();
            informalNotificationSteps.verifyTimelineElementArrive(INFORMAL_PAYMENT_CATEGORY);
        }
    }
}
