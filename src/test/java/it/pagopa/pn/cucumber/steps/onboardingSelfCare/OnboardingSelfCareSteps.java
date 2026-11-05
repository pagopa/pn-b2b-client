package it.pagopa.pn.cucumber.steps.onboardingSelfCare;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.domain.DynamoTableName;
import it.pagopa.pn.client.b2b.pa.provider.SenderInfoProvider;
import it.pagopa.pn.client.b2b.pa.service.DynamoDbService;
import it.pagopa.pn.client.b2b.pa.service.IPServiceDeskClient;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDeskIntegration.model.PaSummary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.FilterLogEventsRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.FilteredLogEvent;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

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
import static org.awaitility.Awaitility.await;

/**
 * Non regressione della ricezione delle adesioni degli enti da SelfCare dopo la WI-1.2 di PN-20853 (SRS §6.12:
 * proprietà Kafka per cluster, nuova container factory, coesistenza con il consumer dei pagamenti).
 * <p>
 * SEND non può pubblicare sul topic di SelfCare, quindi gli step osservano il consumer di onboarding di
 * pn-kafka-bridge senza alimentarlo:
 * <ul>
 *     <li>log di ribilanciamento di spring-kafka: "&lt;groupId&gt;: partitions assigned: [sc-contracts-0, ...]"
 *     e "partitions revoked";</li>
 *     <li>log del client Kafka, che riportano "groupId=&lt;groupId&gt;";</li>
 *     <li>enti già aderenti su pn-OnboardInstitutions e sull'API del service desk.</li>
 * </ul>
 * Nessun valore dipende dall'ambiente: il consumer group è ricavato dai log, gli enti dagli alias di
 * {@link SenderInfoProvider}. Log group e topic hanno un default valido in tutti gli ambienti, sovrascrivibile da
 * proprietà di sistema.
 */
@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OnboardingSelfCareSteps {

    private static final String PARTITIONS_ASSIGNED = "partitions assigned";
    private static final String PARTITIONS_REVOKED = "partitions revoked";
    private static final String TAX_CODE = "taxCode";
    private static final String DESCRIPTION = "description";
    // "<groupId>: partitions assigned: [...]": il group-id è la parola che precede il messaggio di spring-kafka
    private static final Pattern ASSIGNMENT_GROUP_ID = Pattern.compile("([A-Za-z0-9._-]+): " + PARTITIONS_ASSIGNED);
    // Pari alla retention del log group di pn-kafka-bridge: è la storia disponibile per confrontare i rilasci
    private static final Duration LOG_LOOKBACK = Duration.ofDays(14);
    // Il consumer di onboarding non ha coda di lavoro: un minuto basta a far emergere un errore o un ribilanciamento
    private static final Duration ISOLATION_OBSERVATION_WINDOW = Duration.ofMinutes(1);
    // Margine per i log scritti o indicizzati da CloudWatch con qualche secondo di ritardo
    private static final Duration LOG_INDEXING_DELAY = Duration.ofSeconds(30);

    private final CloudWatchLogsClient cloudWatchLogsClient;
    private final DynamoDbService dynamoDbService;
    private final IPServiceDeskClient serviceDeskClient;
    private final SenderInfoProvider senderInfoProvider;
    private final String logGroup;
    private final String topicName;

    private Instant observationStart;
    private String observedGroupId;

    public OnboardingSelfCareSteps(CloudWatchLogsClient cloudWatchLogsClient,
                                   DynamoDbService dynamoDbService,
                                   IPServiceDeskClient serviceDeskClient,
                                   SenderInfoProvider senderInfoProvider,
                                   @Value("${pn.kafka-bridge.log-group:/aws/ecs/pn-kafka-bridge}") String logGroup,
                                   @Value("${pn.kafka-bridge.onboarding.topic-name:sc-contracts}") String topicName) {
        this.cloudWatchLogsClient = cloudWatchLogsClient;
        this.dynamoDbService = dynamoDbService;
        this.serviceDeskClient = serviceDeskClient;
        this.senderInfoProvider = senderInfoProvider;
        this.logGroup = logGroup;
        this.topicName = topicName;
    }

    // =========================================================================
    // COLLEGAMENTO A SELFCARE
    // =========================================================================

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
        List<FilteredLogEvent> errors = onboardingErrorsSince(Instant.ofEpochMilli(lastAssignment.timestamp()), consumerGroupOf(lastAssignment));
        assertThat(errors)
                .as("Errori del consumer di onboarding su %s dall'ultimo aggancio: %s", logGroup, messages(errors))
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

    // =========================================================================
    // ISOLAMENTO DAI PAGAMENTI
    // =========================================================================

    @Given("SEND sta ricevendo le adesioni degli enti da SelfCare")
    public void startObservingOnboardingConsumer() {
        onboardingConsumerIsAttached();
        observedGroupId = consumerGroupOf(requiredLastAssignment());
        observationStart = Instant.now();
    }

    /**
     * Il consumer dei pagamenti ha container factory, error handler e cluster propri (SRS §6.12): un suo errore non
     * deve causare ribilanciamenti né errori sul consumer di onboarding.
     */
    @Then("la ricezione delle adesioni prosegue senza interruzioni né errori")
    public void onboardingConsumerUnaffectedByPaymentEvent() {
        assertThat(observationStart).as("Lo scenario deve prima mettere in osservazione il consumer di onboarding").isNotNull();
        Duration window = ISOLATION_OBSERVATION_WINDOW.plus(LOG_INDEXING_DELAY);
        await().during(window)
                .atMost(window.plus(Duration.ofMinutes(1)))
                .pollInterval(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    List<FilteredLogEvent> revocations = events(rebalanceFilter(PARTITIONS_REVOKED), observationStart);
                    List<FilteredLogEvent> errors = onboardingErrorsSince(observationStart, observedGroupId);
                    assertThat(revocations)
                            .as("Partizioni del topic %s revocate durante l'elaborazione dell'evento di pagamento: %s", topicName, messages(revocations))
                            .isEmpty();
                    assertThat(errors)
                            .as("Errori del consumer di onboarding durante l'elaborazione dell'evento di pagamento: %s", messages(errors))
                            .isEmpty();
                });
    }

    // =========================================================================
    // ENTI GIÀ ADERENTI
    // =========================================================================

    @Then("l'ente {string} risulta aderente a SEND con il proprio codice fiscale")
    public void paIsOnboardedWithTaxCode(String pa) {
        String senderId = senderInfoProvider.getSenderId(pa);
        assertThat(attribute(onboardedInstitution(pa), TAX_CODE))
                .as("La PA %s (id %s) su pn-OnboardInstitutions non ha il codice fiscale", pa, senderId)
                .isNotBlank();
    }

    /**
     * L'elenco completo restituisce solo una parte degli enti: la PA viene cercata per prefisso della denominazione
     * (paNameFilter), usando quella censita all'adesione.
     */
    @Then("l'operatore dell'assistenza trova l'ente {string} cercandolo per denominazione")
    public void paIsListedByServiceDesk(String pa) {
        String senderId = senderInfoProvider.getSenderId(pa);
        String denomination = attribute(onboardedInstitution(pa), DESCRIPTION);
        assertThat(denomination).as("La PA %s (id %s) su pn-OnboardInstitutions non ha la denominazione", pa, senderId).isNotBlank();

        List<PaSummary> onboardedPas = serviceDeskClient.getListOfOnboardedPA(denomination);
        assertThat(onboardedPas)
                .as("La PA %s (id %s, \"%s\") non compare nell'elenco delle PA onboardate del service desk", pa, senderId, denomination)
                .extracting(PaSummary::getId)
                .contains(senderId);
    }

    private Map<String, AttributeValue> onboardedInstitution(String pa) {
        String senderId = senderInfoProvider.getSenderId(pa);
        List<Map<String, AttributeValue>> items = dynamoDbService.call(DynamoTableName.ONBOARD_INSTITUTIONS, Map.of(
                ":v_id", AttributeValue.builder().s(senderId).build())).items();
        assertThat(items).as("La PA %s (id %s) non è presente su pn-OnboardInstitutions", pa, senderId).isNotEmpty();
        return items.get(0);
    }

    private static String attribute(Map<String, AttributeValue> item, String name) {
        return Optional.ofNullable(item.get(name)).map(AttributeValue::s).orElse(null);
    }

    // =========================================================================
    // RICERCA NEI LOG DI pn-kafka-bridge
    // =========================================================================

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

    private List<FilteredLogEvent> onboardingErrorsSince(Instant from, String consumerGroup) {
        List<FilteredLogEvent> errors = new ArrayList<>();
        errors.addAll(events(String.format("\"ERROR\" \"%s\"", topicName), from));
        errors.addAll(events(String.format("\"ERROR\" \"groupId=%s\"", consumerGroup), from));
        return errors;
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

    private static List<String> messages(List<FilteredLogEvent> events) {
        return events.stream().map(FilteredLogEvent::message).toList();
    }
}
