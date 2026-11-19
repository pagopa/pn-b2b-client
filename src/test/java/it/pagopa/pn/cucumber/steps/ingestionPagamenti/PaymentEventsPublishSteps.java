package it.pagopa.pn.cucumber.steps.ingestionPagamenti;

import io.cucumber.java.en.When;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.context.PaymentEventsScenarioContext;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.dto.PaymentChannel;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.dto.PaymentEventAnomaly;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.dto.PaymentNotice;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.service.PagoPaPaymentEventService;
import it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheB2bSteps;
import it.pagopa.pn.cucumber.steps.pa.PresaInCaricoNoticaBonariaSteps;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comunicazione dei pagamenti a SEND dai diversi canali: pagoPA, App IO e API B2B dell'ente.
 */
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class PaymentEventsPublishSteps {

    private static final String INFORMAL_PAYMENT_CATEGORY = "PAYMENT";
    private static final String PAGOPA_CHANNEL = "pagoPA";
    private static final String SENDER_CHANNEL = "ente";

    private final PaymentEventsScenarioContext context;
    private final PagoPaPaymentEventService paymentEventService;
    private final PresaInCaricoNoticaBonariaSteps informalNotificationSteps;
    private final AvanzamentoNotificheB2bSteps legalNotificationSteps;
    private final PaymentEventsAssertionSteps assertionSteps;

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

    @When("l'ente comunica il pagamento dell'avviso tramite API B2B")
    public void senderNotifiesPaymentViaB2b() {
        legalNotificationSteps.laNotificaVienePagata();
    }

    /**
     * Il pagamento del primo canale viene atteso in timeline prima del secondo, così l'ordine è quello dichiarato.
     */
    @When("il pagamento dell'avviso legale viene comunicato prima da {string} e poi da {string}")
    public void legalPaymentNotifiedByPagoPaAndSender(String firstChannel, String secondChannel) {
        assertThat(List.of(firstChannel, secondChannel)).as("I due canali devono essere pagoPA ed ente, in un ordine qualsiasi")
                .containsExactlyInAnyOrder(PAGOPA_CHANNEL, SENDER_CHANNEL);
        if (PAGOPA_CHANNEL.equals(firstChannel)) {
            pagoPaNotifiesPayment();
            assertionSteps.paymentNoticePaidRegistered();
            senderNotifiesPaymentViaB2b();
        } else {
            senderNotifiesPaymentViaB2b();
            assertionSteps.paymentNotifiedBySender();
            pagoPaNotifiesPayment();
        }
    }

    /**
     * Il pagamento da App IO lo segnala il mock di App IO, configurato dall'oggetto del messaggio: lo step ne attende
     * il PAYMENT.
     */
    @When("App IO comunica il pagamento dell'avviso")
    public void appIoNotifiesPayment() {
        assertThat(context.isPaidAlsoOnAppIo()).as("Lo scenario deve prevedere il pagamento tramite App IO").isTrue();
        informalNotificationSteps.verifyTimelineElementArrive(INFORMAL_PAYMENT_CATEGORY);
    }

    /**
     * Si attende il PAYMENT del primo canale prima che possa intervenire il secondo: se l'ordine non viene rispettato,
     * la verifica su chi ha scritto il PAYMENT lo rileva.
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
