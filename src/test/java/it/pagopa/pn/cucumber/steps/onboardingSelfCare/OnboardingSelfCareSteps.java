package it.pagopa.pn.cucumber.steps.onboardingSelfCare;

import io.cucumber.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.FilterLogEventsRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.FilteredLogEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Non regressione della ricezione delle adesioni degli enti da SelfCare dopo la WI-1.2 di PN-20853 (SRS §6.12:
 * proprietà Kafka per cluster, nuova container factory, coesistenza con il consumer dei pagamenti).
 * <p>
 * SEND non può pubblicare sul topic di SelfCare, quindi gli step osservano il consumer di onboarding di
 * pn-kafka-bridge senza alimentarlo, tramite i log:
 * <ul>
 *     <li>log di ribilanciamento di spring-kafka: "&lt;groupId&gt;: partitions assigned: [sc-contracts-0, ...]"
 *     e "partitions revoked";</li>
 *     <li>log del client Kafka, che riportano "groupId=&lt;groupId&gt;".</li>
 * </ul>
 * Nessun valore dipende dall'ambiente: il consumer group è ricavato dai log. Log group e topic hanno un default
 * valido in tutti gli ambienti, sovrascrivibile da proprietà di sistema.
 */
@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OnboardingSelfCareSteps {

    private static final String PARTITIONS_ASSIGNED = "partitions assigned";
    private static final String PARTITIONS_REVOKED = "partitions revoked";
    // "<groupId>: partitions assigned: [...]": il group-id è la parola che precede il messaggio di spring-kafka
    private static final Pattern ASSIGNMENT_GROUP_ID = Pattern.compile("([A-Za-z0-9._-]+): " + PARTITIONS_ASSIGNED);
    // Pari alla retention del log group di pn-kafka-bridge: è la storia disponibile per confrontare i rilasci
    private static final Duration LOG_LOOKBACK = Duration.ofDays(14);

    private final CloudWatchLogsClient cloudWatchLogsClient;
    private final String logGroup;
    private final String topicName;

    public OnboardingSelfCareSteps(CloudWatchLogsClient cloudWatchLogsClient,
                                   @Value("${pn.kafka-bridge.log-group:/aws/ecs/pn-kafka-bridge}") String logGroup,
                                   @Value("${pn.kafka-bridge.onboarding.topic-name:sc-contracts}") String topicName) {
        this.cloudWatchLogsClient = cloudWatchLogsClient;
        this.logGroup = logGroup;
        this.topicName = topicName;
    }

    /**
     * Il consumer è agganciato se l'ultima assegnazione di partizioni del topic è successiva all'ultima revoca:
     * a ogni avvio o ribilanciamento spring-kafka logga la revoca e poi la nuova assegnazione, allo spegnimento
     * solo la revoca.
     */
    @Then("SEND è collegato al canale con cui SelfCare comunica le adesioni degli enti")
    public void onboardingConsumerIsAttached() {
        FilteredLogEvent lastAssignment = requiredLastAssignment();
        Optional<FilteredLogEvent> lastRevocation = lastEvent(PARTITIONS_REVOKED);
        log.info("Ultima assegnazione del topic {}: {}", topicName, lastAssignment.message());
        lastRevocation.ifPresent(revocation -> log.info("Ultima revoca del topic {}: {}", topicName, revocation.message()));

        lastRevocation.ifPresent(revocation -> assertThat(revocation.timestamp())
                .as("Le partizioni del topic %s sono state revocate dopo l'ultima assegnazione (%s): il consumer di onboarding è staccato",
                        topicName, revocation.message())
                .isLessThanOrEqualTo(lastAssignment.timestamp()));
    }

    /**
     * Copre gli errori di configurazione che la WI-1.2 può introdurre: credenziali o bootstrap del cluster (log del
     * client Kafka con groupId) e deserializer o listener (errori sulle partizioni del topic).
     * La finestra parte dall'ultima assegnazione, cioè dall'avvio del consumer attualmente in esercizio.
     */
    @Then("la ricezione delle adesioni non ha registrato errori dall'ultimo collegamento")
    public void onboardingConsumerHasNoErrorsSinceLastAssignment() {
        FilteredLogEvent lastAssignment = requiredLastAssignment();
        Instant from = Instant.ofEpochMilli(lastAssignment.timestamp());
        List<FilteredLogEvent> errors = new ArrayList<>();
        errors.addAll(events(String.format("\"ERROR\" \"%s\"", topicName), from));
        errors.addAll(events(String.format("\"ERROR\" \"groupId=%s\"", consumerGroupOf(lastAssignment)), from));
        assertThat(errors)
                .as("Errori del consumer di onboarding su %s dall'ultimo aggancio: %s", logGroup,
                        errors.stream().map(FilteredLogEvent::message).toList())
                .isEmpty();
    }

    /**
     * Il consumer group non deve cambiare tra un rilascio e l'altro (SRS §6.12, coesistenza dei consumer): con
     * auto.offset.reset=earliest un group nuovo rileggerebbe tutto il topic, e ambienti collegati allo stesso EventHub
     * con lo stesso group si dividerebbero le partizioni.
     * Il group atteso non è configurato: tutte le assegnazioni del topic nella storia dei log devono appartenere a un
     * solo group. Prima del rilascio la storia contiene il group AS-IS dell'ambiente; dopo un rilascio che lo cambia,
     * o che mette un altro consumer sul topic con un group diverso, i group diventano due.
     */
    @Then("la ricezione delle adesioni usa la stessa sottoscrizione dei rilasci precedenti")
    public void onboardingConsumerGroupUnchanged() {
        List<FilteredLogEvent> assignments = events(rebalanceFilter(PARTITIONS_ASSIGNED), lookbackStart());
        assertThat(assignments)
                .as("Nessuna assegnazione di partizioni del topic %s su %s negli ultimi %d giorni", topicName, logGroup, LOG_LOOKBACK.toDays())
                .isNotEmpty();

        Map<String, Instant> lastAssignmentByGroup = new TreeMap<>();
        assignments.forEach(assignment -> lastAssignmentByGroup.merge(consumerGroupOf(assignment),
                Instant.ofEpochMilli(assignment.timestamp()), (first, second) -> first.isAfter(second) ? first : second));
        log.info("Consumer group che hanno ricevuto partizioni del topic {} negli ultimi {} giorni (ultima assegnazione): {}",
                topicName, LOG_LOOKBACK.toDays(), lastAssignmentByGroup);

        assertThat(lastAssignmentByGroup)
                .as("Il topic %s è stato consumato da più consumer group negli ultimi %d giorni (ultima assegnazione per group): %s",
                        topicName, LOG_LOOKBACK.toDays(), lastAssignmentByGroup)
                .hasSize(1);
    }

    private FilteredLogEvent requiredLastAssignment() {
        return lastEvent(PARTITIONS_ASSIGNED)
                .orElseThrow(() -> new AssertionError(String.format(
                        "Nessuna assegnazione di partizioni del topic %s su %s negli ultimi %d giorni: il consumer di onboarding "
                                + "non si è agganciato oppure il servizio non viene riavviato da più tempo",
                        topicName, logGroup, LOG_LOOKBACK.toDays())));
    }

    private static String consumerGroupOf(FilteredLogEvent assignment) {
        Matcher matcher = ASSIGNMENT_GROUP_ID.matcher(assignment.message());
        if (!matcher.find()) {
            throw new AssertionError("Consumer group non ricavabile dal log di assegnazione: " + assignment.message());
        }
        return matcher.group(1);
    }

    private Optional<FilteredLogEvent> lastEvent(String rebalanceMessage) {
        return events(rebalanceFilter(rebalanceMessage), lookbackStart()).stream()
                .max(Comparator.comparingLong(FilteredLogEvent::timestamp));
    }

    private String rebalanceFilter(String rebalanceMessage) {
        return String.format("\"%s\" \"%s\"", rebalanceMessage, topicName);
    }

    /**
     * Scorre tutte le pagine: con una finestra lunga CloudWatch può restituire pagine vuote prima di quelle con i
     * risultati.
     */
    private List<FilteredLogEvent> events(String filterPattern, Instant from) {
        FilterLogEventsRequest request = FilterLogEventsRequest.builder()
                .logGroupName(logGroup)
                .filterPattern(filterPattern)
                .startTime(from.toEpochMilli())
                .build();
        List<FilteredLogEvent> events = new ArrayList<>();
        cloudWatchLogsClient.filterLogEventsPaginator(request).events().forEach(events::add);
        log.debug("Log {} con '{}' dal {}: {}", logGroup, filterPattern, from, events.size());
        return events;
    }

    private static Instant lookbackStart() {
        return Instant.now().minus(LOG_LOOKBACK);
    }
}
