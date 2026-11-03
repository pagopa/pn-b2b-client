package it.pagopa.pn.cucumber.steps.ingestionPagamenti;

import io.cucumber.java.en.Then;
import it.pagopa.common.util.CloudWatchQueryBuilder;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.context.IngestionPagamentiScenarioContext;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.dto.PnErrorEventPayload;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.service.PaymentErrorQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Verifica della gestione degli eventi di pagamento non conformi da parte di pn-kafka-bridge.
 */
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class IngestionPagamentiAnomalieSteps {

    private static final String KAFKA_BRIDGE_LOG_GROUP = "/aws/ecs/pn-kafka-bridge";
    // L'SRS chiede solo un log FATAL (REQ-ING-07): i log FATAL di SEND contengono "* FATAL *", ricercato dalle sonde
    private static final String FATAL_LOG_LEVEL = "FATAL";

    private static final Duration DLQ_TIMEOUT = Duration.ofMinutes(2);
    private static final Duration DLQ_OBSERVATION_WINDOW = Duration.ofMinutes(1);
    private static final Duration LOG_TIMEOUT = Duration.ofMinutes(3);

    private final IngestionPagamentiScenarioContext context;
    private final PaymentErrorQueueService paymentErrorQueueService;
    private final CloudWatchLogsClient cloudWatchLogsClient;

    @Then("l'evento malformato viene scartato nella coda degli eventi di pagamento malformati")
    public void eventDiscardedInDlq() {
        String marker = context.getMalformedEventMarker();
        await().atMost(DLQ_TIMEOUT).pollInterval(Duration.ofSeconds(5)).untilAsserted(() -> {
            Optional<PnErrorEventPayload> errorEvent = paymentErrorQueueService.findEventContaining(marker);
            assertThat(errorEvent).as("Nessun evento in DLQ contiene %s", marker).isPresent();
            assertThat(errorEvent.get().getDescription()).isEqualTo(PnErrorEventPayload.MALFORMED_DESCRIPTION);
        });
    }

    @Then("l'evento non è presente nella coda degli eventi di pagamento malformati")
    public void eventNotInDlq() {
        String marker = context.getLastEventMarker();
        await().during(DLQ_OBSERVATION_WINDOW)
                .atMost(DLQ_OBSERVATION_WINDOW.plus(Duration.ofMinutes(1)))
                .pollInterval(Duration.ofSeconds(10))
                .until(() -> paymentErrorQueueService.findEventContaining(marker).isEmpty());
    }

    /**
     * Il log viene correlato all'evento tramite l'identificativo, che il test colloca in testa al payload perché
     * compaia nell'anteprima dell'evento riportata nel log.
     */
    @Then("pn-kafka-bridge scarta l'evento registrando un errore FATAL")
    public void oversizeEventDiscardedWithFatalLog() {
        String query = String.format("\"%s\" \"%s\"", FATAL_LOG_LEVEL, context.getLastEventMarker());
        await().atMost(LOG_TIMEOUT).pollInterval(Duration.ofSeconds(10)).untilAsserted(() -> {
            long minutesSincePublish = Duration.between(context.getLastEventPublishedAt(), Instant.now()).toMinutes() + 1;
            assertThat(cloudWatchLogsClient.filterLogEvents(CloudWatchQueryBuilder.search(KAFKA_BRIDGE_LOG_GROUP, query, minutesSincePublish)).events())
                    .as("Nessun log su %s per la ricerca %s", KAFKA_BRIDGE_LOG_GROUP, query)
                    .isNotEmpty();
        });
    }
}
