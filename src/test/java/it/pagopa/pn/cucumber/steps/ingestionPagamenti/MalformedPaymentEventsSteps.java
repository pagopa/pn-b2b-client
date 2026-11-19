package it.pagopa.pn.cucumber.steps.ingestionPagamenti;

import io.cucumber.java.en.Then;
import it.pagopa.common.polling.Poller;
import it.pagopa.common.polling.PollingConfig;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.config.PaymentEventsSettings;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.context.PaymentEventsScenarioContext;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.dto.PnErrorEventPayload;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.service.PaymentErrorQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Consumer;

import static it.pagopa.pn.cucumber.utils.ObservationWindow.assertHoldsFor;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifica dello scarto degli eventi di pagamento malformati.
 */
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class MalformedPaymentEventsSteps {

    private static final Duration DLQ_POLL_INTERVAL = Duration.ofSeconds(5);
    private static final Duration DLQ_OBSERVATION_POLL_INTERVAL = Duration.ofSeconds(10);

    private final PaymentEventsScenarioContext context;
    private final PaymentEventsSettings settings;
    private final PaymentErrorQueueService paymentErrorQueueService;

    @Then("l'evento malformato viene scartato nella coda degli eventi di pagamento malformati")
    public void eventDiscardedInDlq() {
        String marker = context.getMalformedEventMarker();
        Consumer<Optional<PnErrorEventPayload>> verify = errorEvent -> {
            assertThat(errorEvent).as("Nessun evento in DLQ contiene %s", marker).isPresent();
            assertThat(errorEvent.get().description()).isEqualTo(PnErrorEventPayload.MALFORMED_DESCRIPTION);
        };
        Poller.pollUntil(() -> paymentErrorQueueService.findEventContaining(marker), verify,
                PollingConfig.of(settings.getDlqTimeout(), DLQ_POLL_INTERVAL));
    }

    @Then("l'evento non è presente nella coda degli eventi di pagamento malformati")
    public void eventNotInDlq() {
        String marker = context.getLastEventMarker();
        assertHoldsFor(settings.getDlqObservationWindow(), DLQ_OBSERVATION_POLL_INTERVAL,
                () -> paymentErrorQueueService.findEventContaining(marker),
                errorEvent -> assertThat(errorEvent).as("L'evento %s è stato scartato in DLQ", marker).isEmpty());
    }
}
