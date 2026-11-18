package it.pagopa.pn.cucumber.steps.onboardingSelfCare;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.common.polling.Poller;
import it.pagopa.common.polling.PollingConfig;
import it.pagopa.common.polling.PollingTimeoutException;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.nodopagopa.model.OnboardingInstitution;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.nodopagopa.model.OnboardingRequest;
import it.pagopa.pn.client.b2b.pa.domain.DynamoTableName;
import it.pagopa.pn.client.b2b.pa.service.DynamoDbService;
import it.pagopa.pn.client.b2b.pa.service.IPnKafkaMockClient;
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
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Elaborazione delle adesioni degli enti pubblicate con il mock Kafka di pn-external-channels: pn-kafka-bridge le
 * filtra per prodotto SEND e le inoltra a pn-external-registries, che le registra su pn-OnboardInstitutions con chiave
 * l'internalInstitutionId dell'adesione.
 * <p>
 * Ogni scenario usa un internalInstitutionId nuovo, così non tocca enti esistenti. Il prodotto SEND dell'ambiente è
 * ricavato dal log di avvio del filtro di pn-kafka-bridge; si sovrascrive con pn.onboarding.selfcare.product-id.
 */
@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InstitutionOnboardingSteps {

    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_SUSPENDED = "SUSPENDED";
    // "Filter enabled for onboarding: true with productId: prod-pn-dev", dentro il log JSON di CloudWatch
    private static final String PRODUCT_FILTER_LOG = "Filter enabled for onboarding";
    private static final Pattern PRODUCT_FILTER = Pattern.compile("Filter enabled for onboarding: (true|false) with productId: ([^\\s\",]*)");
    private static final Duration LOG_LOOKBACK = Duration.ofDays(14);
    private static final Duration POLL_INTERVAL = Duration.ofSeconds(5);

    private final IPnKafkaMockClient kafkaMockClient;
    private final DynamoDbService dynamoDbService;
    private final CloudWatchLogsClient cloudWatchLogsClient;
    private final String kafkaBridgeLogGroup;
    private final String configuredSendProductId;
    private final String otherProductId;
    private final String institutionTaxCode;
    private final Duration registrationTimeout;
    private final Duration ignoredOnboardingWindow;

    private OnboardingRequest onboarding;

    public InstitutionOnboardingSteps(IPnKafkaMockClient kafkaMockClient,
                                      DynamoDbService dynamoDbService,
                                      CloudWatchLogsClient cloudWatchLogsClient,
                                      @Value("${pn.kafka-bridge.log-group:/aws/ecs/pn-kafka-bridge}") String kafkaBridgeLogGroup,
                                      @Value("${pn.onboarding.selfcare.product-id:}") String configuredSendProductId,
                                      @Value("${pn.onboarding.selfcare.other-product-id:prod-io}") String otherProductId,
                                      @Value("${pn.onboarding.test-institution.tax-code:12345678901}") String institutionTaxCode,
                                      @Value("${pn.onboarding.timeout.registration:PT3M}") String registrationTimeout,
                                      @Value("${pn.onboarding.window.ignored-onboarding:PT1M}") String ignoredOnboardingWindow) {
        this.kafkaMockClient = kafkaMockClient;
        this.dynamoDbService = dynamoDbService;
        this.cloudWatchLogsClient = cloudWatchLogsClient;
        this.kafkaBridgeLogGroup = kafkaBridgeLogGroup;
        this.configuredSendProductId = configuredSendProductId;
        this.otherProductId = otherProductId;
        this.institutionTaxCode = institutionTaxCode;
        this.registrationTimeout = Duration.parse(registrationTimeout);
        this.ignoredOnboardingWindow = Duration.parse(ignoredOnboardingWindow);
    }

    @Given("SelfCare comunica l'adesione a SEND di un nuovo ente")
    public void selfCareSendsSendOnboarding() {
        publishNewOnboarding(sendProductId());
    }

    /**
     * Lo scenario ha senso solo se pn-kafka-bridge filtra per prodotto: senza filtro accetterebbe qualunque adesione.
     */
    @Given("SelfCare comunica l'adesione di un nuovo ente a un prodotto diverso da SEND")
    public void selfCareSendsOtherProductOnboarding() {
        assertThat(otherProductId)
                .as("Il prodotto diverso da SEND coincide con quello di SEND")
                .isNotEqualTo(sendProductId());
        publishNewOnboarding(otherProductId);
    }

    @When("SelfCare comunica la sospensione dell'adesione dell'ente")
    public void selfCareSuspendsOnboarding() {
        assertThat(onboarding).as("Nessuna adesione pubblicata nello scenario").isNotNull();
        onboarding.state(STATUS_SUSPENDED).updatedAt(now());
        kafkaMockClient.publishOnboardingEvent(onboarding);
    }

    @Then("SEND censisce l'ente tra gli enti aderenti con i dati comunicati da SelfCare")
    public void institutionRegistered() {
        Map<String, AttributeValue> institution = awaitInstitutionWithStatus(STATUS_ACTIVE);
        OnboardingInstitution expected = onboarding.getInstitution();
        assertThat(stringAttribute(institution, "taxCode")).as("taxCode").isEqualTo(expected.getTaxCode());
        assertThat(stringAttribute(institution, "description")).as("description").isEqualTo(expected.getDescription());
        assertThat(stringAttribute(institution, "digitalAddress")).as("digitalAddress").isEqualTo(expected.getDigitalAddress());
        assertThat(stringAttribute(institution, "ipaCode")).as("ipaCode").isEqualTo(expected.getOriginId());
        assertThat(stringAttribute(institution, "rootId"))
                .as("Un ente senza ente padre è radice di se stesso")
                .isEqualTo(onboarding.getInternalInstitutionId());
    }

    @Then("l'adesione dell'ente risulta sospesa")
    public void institutionSuspended() {
        awaitInstitutionWithStatus(STATUS_SUSPENDED);
    }

    /**
     * Per tutta la finestra di osservazione l'ente non deve comparire su pn-OnboardInstitutions.
     */
    @Then("SEND non censisce l'ente tra gli enti aderenti")
    public void institutionNotRegistered() {
        String id = onboarding.getInternalInstitutionId();
        try {
            Optional<Map<String, AttributeValue>> found = Poller.pollUntil(() -> institution(id), Optional::isPresent,
                    PollingConfig.of(ignoredOnboardingWindow, POLL_INTERVAL));
            assertThat(found).as("L'ente %s di un prodotto diverso da SEND è stato censito: %s", id, found).isEmpty();
        } catch (PollingTimeoutException expected) {
            log.info("Ente {} non censito per tutta la finestra di {}", id, ignoredOnboardingWindow);
        }
    }

    private void publishNewOnboarding(String productId) {
        String institutionId = "pn-b2b-test-" + UUID.randomUUID();
        OffsetDateTime now = now();
        onboarding = new OnboardingRequest()
                .id(UUID.randomUUID().toString())
                .internalInstitutionId(institutionId)
                .onboardingTokenId(UUID.randomUUID().toString())
                .product(productId)
                .state(STATUS_ACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .institution(new OnboardingInstitution()
                        .taxCode(institutionTaxCode)
                        .description("Ente di test pn-b2b-client " + institutionId)
                        .digitalAddress("ente-test-pn-b2b@pec.it")
                        .institutionType("PA")
                        .origin("IPA")
                        .originId("pnb2b" + institutionId.substring(institutionId.length() - 6)));
        kafkaMockClient.publishOnboardingEvent(onboarding);
        log.info("Adesione {} pubblicata per il prodotto {}", institutionId, productId);
    }

    private Map<String, AttributeValue> awaitInstitutionWithStatus(String expectedStatus) {
        assertThat(onboarding).as("Nessuna adesione pubblicata nello scenario").isNotNull();
        String id = onboarding.getInternalInstitutionId();
        return Poller.pollUntil(() -> institution(id),
                        found -> found.map(item -> expectedStatus.equals(stringAttribute(item, "status"))).orElse(false),
                        PollingConfig.of(registrationTimeout, POLL_INTERVAL),
                        last -> String.format("L'ente %s non risulta su %s con stato %s entro %s (ultimo valore: %s)",
                                id, DynamoTableName.ONBOARD_INSTITUTIONS.getValue(), expectedStatus, registrationTimeout, last))
                .orElseThrow();
    }

    private Optional<Map<String, AttributeValue>> institution(String id) {
        return dynamoDbService.call(DynamoTableName.ONBOARD_INSTITUTIONS, Map.of(
                ":v_id", AttributeValue.builder().s(id).build())).items().stream().findFirst();
    }

    private static String stringAttribute(Map<String, AttributeValue> item, String name) {
        AttributeValue value = item.get(name);
        return value != null ? value.s() : null;
    }

    /**
     * Il prodotto atteso da pn-kafka-bridge cambia per ambiente: si legge dall'ultimo avvio del filtro delle adesioni.
     */
    private String sendProductId() {
        if (!configuredSendProductId.isBlank()) {
            return configuredSendProductId;
        }
        FilterLogEventsRequest request = FilterLogEventsRequest.builder()
                .logGroupName(kafkaBridgeLogGroup)
                .filterPattern(String.format("\"%s\"", PRODUCT_FILTER_LOG))
                .startTime(Instant.now().minus(LOG_LOOKBACK).toEpochMilli())
                .build();
        List<FilteredLogEvent> events = cloudWatchLogsClient.filterLogEventsPaginator(request).events().stream().toList();
        FilteredLogEvent last = events.stream().max(Comparator.comparingLong(FilteredLogEvent::timestamp))
                .orElseThrow(() -> new AssertionError(String.format(
                        "Nessun log \"%s\" su %s negli ultimi %d giorni: impostare pn.onboarding.selfcare.product-id",
                        PRODUCT_FILTER_LOG, kafkaBridgeLogGroup, LOG_LOOKBACK.toDays())));
        Matcher matcher = PRODUCT_FILTER.matcher(last.message());
        assertThat(matcher.find()).as("Prodotto non ricavabile dal log: %s", last.message()).isTrue();
        assertThat(matcher.group(1))
                .as("pn-kafka-bridge non filtra le adesioni per prodotto: %s", last.message())
                .isEqualTo("true");
        return matcher.group(2);
    }

    private static OffsetDateTime now() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }
}
