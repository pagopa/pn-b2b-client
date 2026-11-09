package it.pagopa.pn.cucumber.steps.ingestionPagamenti;

import io.cucumber.java.en.Then;
import it.pagopa.common.util.CloudWatchQueryBuilder;
import it.pagopa.pn.client.b2b.pa.domain.DynamoTableName;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.InformalTimelineElementDetailsV1;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.InformalTimelineElementV1;
import it.pagopa.pn.client.b2b.pa.service.DynamoDbService;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.ProgressResponseElementV30;
import it.pagopa.pn.cucumber.steps.SendSharedContext;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.informalNotification.utils.NotificationInformalUtilsWorkFlowV1;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.context.IngestionPagamentiScenarioContext;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.dto.PaymentChannel;
import it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheB2bSteps;
import it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheWebhookB2bSteps;
import it.pagopa.pn.cucumber.steps.pa.PresaInCaricoNoticaBonariaSteps;
import it.pagopa.pn.cucumber.steps.recipient.RicezioneNotificheWebSteps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Verifica degli effetti dei pagamenti su SEND: posizione debitoria, timeline, stato e workflow delle notifiche.
 */
@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class IngestionPagamentiVerificheSteps {

    private static final String PAYMENT_NOTICE_PAID = "PAYMENT_NOTICE_PAID";
    // Il pagamento comunicato dall'ente è esposto come NOTIFICATION_PAID (id NOTIFICATION_PAID.IUN_...), ma su
    // pn-Timelines la sua categoria è PAYMENT: le query su DynamoDB filtrano per categoria
    private static final String SENDER_PAYMENT_CATEGORY = "PAYMENT";
    private static final String INFORMAL_PAYMENT = "PAYMENT";
    private static final String WORKFLOW_DONE_REACHED = "WORKFLOW_DONE_REACHED";
    private static final String SEND_DIGITAL_MESSAGE = "SEND_DIGITAL_MESSAGE";
    private static final String INFORMAL_STATUS_COMPLETED = "COMPLETED_REACHED";
    private static final String COMMUNICATION_TYPE = "communicationType";
    private static final String NOTIFICATION_COST_LOG_GROUP = "/aws/ecs/pn-notification-cost-service";
    private static final String IO_PAYMENT_PROGRESS = "SEND_DIGITAL_MESSAGE_PROGRESS";
    private static final String IO_PAYMENT_PROGRESS_CODE = "PAID";
    private static final String APP_IO_PAYMENT_SOURCE_CHANNEL = "IO";
    private static final String COST_VALIDATION_REQUEST = "NOTIFICATION_COST_VALIDATION_REQUEST";
    private static final String COST_VALIDATION_RESPONSE = "NOTIFICATION_COST_VALIDATION_RESPONSE";
    private static final String COST_VALIDATION_ELEMENT_PREFIX = "NOTIFICATION_COST_VALIDATION";
    private static final String PAID_METADATA = "paid";
    private static final String INFORMAL_RECIPIENT_TYPE = "PF";
    // L'SRS chiede un log INFO per lo scarto (REQ-FIL-02) senza definirne il testo: è quello di pn-notification-cost-service (PN-21959)
    private static final String PAYMENT_DISCARDED_LOG_MESSAGE = "PaymentInfo not found";

    private static final Duration ASYNC_PROCESSING_TIMEOUT = Duration.ofMinutes(3);
    private static final Duration POLL_INTERVAL = Duration.ofSeconds(5);
    // Effetti scritti dallo stesso consumer di un elemento già osservato: bastano pochi secondi oltre la scrittura
    private static final Duration SIDE_EFFECT_OBSERVATION_WINDOW = Duration.ofSeconds(30);
    // Timeout del primo canale delle campagne usate (EMAIL, PT4M in BonarieAllChannels) più il margine di schedulazione
    private static final Duration ESCALATION_WINDOW = Duration.ofMinutes(6);
    private static final Duration IGNORED_PAYMENT_OBSERVATION_WINDOW = Duration.ofMinutes(1);
    // pn-stream riceve gli elementi di timeline in modo asincrono rispetto alla loro scrittura
    private static final Duration STREAM_PROPAGATION_DELAY = Duration.ofSeconds(30);
    private static final String STREAM_VERSION = "V30";
    private static final int MAX_STREAM_CONSUME_CALLS = 500;

    private final IngestionPagamentiScenarioContext context;
    private final SharedSteps sharedSteps;
    private final SendSharedContext sendSharedContext;
    private final DynamoDbService dynamoDbService;
    private final CloudWatchLogsClient cloudWatchLogsClient;
    private final PresaInCaricoNoticaBonariaSteps informalNotificationSteps;
    private final AvanzamentoNotificheB2bSteps legalNotificationSteps;
    private final RicezioneNotificheWebSteps searchMetadataSteps;
    private final AvanzamentoNotificheWebhookB2bSteps webhookSteps;

    // =========================================================================
    // POSIZIONE DEBITORIA
    // =========================================================================

    /**
     * Con US-3 anche la validazione delle bonarie con avviso pagoPA passa dal microservizio dei costi e ne lascia
     * traccia con i due elementi diagnostici (REQ-VAL-07).
     */
    @Then("SEND registra la validazione della posizione debitoria nella timeline della comunicazione bonaria")
    public void costValidationRegistered() {
        awaitTimelineElementOnDynamo(COST_VALIDATION_REQUEST);
        awaitTimelineElementOnDynamo(COST_VALIDATION_RESPONSE);
    }

    @Then("la validazione della posizione debitoria non è esposta nella timeline della notifica")
    public void costValidationNotExposed() {
        assertElementNotExposed(COST_VALIDATION_ELEMENT_PREFIX);
    }

    /**
     * Gli elementi della validazione sono diagnostici anche per pn-stream (REQ-VAL-07, SRS §6.7): con US-3 compaiono
     * per la prima volta nelle timeline delle bonarie.
     */
    @Then("la validazione della posizione debitoria non è esposta negli eventi dello stream dell'ente")
    public void costValidationNotStreamed() {
        assertElementNotStreamed(COST_VALIDATION_ELEMENT_PREFIX);
    }

    /**
     * Per le bonarie non si calcolano i costi di notificazione (REQ-VAL-06): pn-NotificationDeliveryCost non ha record.
     * Il record verrebbe scritto in modo asincrono, quindi l'assenza va verificata per tutta la finestra di osservazione.
     */
    @Then("SEND non registra costi di notificazione per la comunicazione bonaria")
    public void noDeliveryCostForInformal() {
        await().during(SIDE_EFFECT_OBSERVATION_WINDOW)
                .atMost(SIDE_EFFECT_OBSERVATION_WINDOW.plus(Duration.ofSeconds(30)))
                .pollInterval(Duration.ofSeconds(5))
                .until(() -> deliveryCostItems().isEmpty());
    }

    private List<Map<String, AttributeValue>> deliveryCostItems() {
        return dynamoDbService.call(DynamoTableName.NOTIFICATION_DELIVERY_COST, Map.of(
                ":v_pk", AttributeValue.builder().s(sharedSteps.getNotificationIun()).build(),
                ":v_sk", AttributeValue.builder().n("0").build())).items();
    }

    @Then("la posizione debitoria dell'avviso è censita come {string}")
    public void paymentInfoHasCommunicationType(String expectedCommunicationType) {
        String pk = context.getNotice().paymentInfoPk();
        try {
            await().atMost(ASYNC_PROCESSING_TIMEOUT).pollInterval(POLL_INTERVAL).untilAsserted(() -> {
                List<Map<String, AttributeValue>> items = paymentInfoItems(pk);
                assertThat(items).as("Record pn-PaymentInfo con pk %s non trovato", pk).hasSize(1);
                AttributeValue communicationType = items.get(0).get(COMMUNICATION_TYPE);
                assertThat(communicationType)
                        .as("Il record pn-PaymentInfo %s deve avere l'attributo %s", pk, COMMUNICATION_TYPE)
                        .isNotNull();
                assertThat(communicationType.s()).isEqualTo(expectedCommunicationType);
            });
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    // =========================================================================
    // TIMELINE
    // =========================================================================

    @Then("SEND registra il pagamento pagoPA nella timeline della notifica")
    public void paymentNoticePaidRegistered() {
        awaitTimelineElementOnDynamo(PAYMENT_NOTICE_PAID);
    }

    /**
     * PAYMENT_NOTICE_PAID è diagnostico e l'API della timeline non lo espone: il conteggio si fa su DynamoDB.
     * Per tutta la finestra di osservazione non deve comparire una seconda occorrenza.
     */
    @Then("nella timeline della notifica è presente un solo pagamento pagoPA")
    public void singlePaymentNoticePaid() {
        await().during(SIDE_EFFECT_OBSERVATION_WINDOW)
                .atMost(SIDE_EFFECT_OBSERVATION_WINDOW.plus(Duration.ofSeconds(30)))
                .pollInterval(Duration.ofSeconds(5))
                .until(() -> timelineItems(PAYMENT_NOTICE_PAID).size() <= 1);
        assertThat(timelineItems(PAYMENT_NOTICE_PAID))
                .as("Attesa una sola occorrenza di %s nella timeline della notifica %s", PAYMENT_NOTICE_PAID, sharedSteps.getNotificationIun())
                .hasSize(1);
    }

    /**
     * Per tutta la finestra di osservazione l'elemento PAYMENT_NOTICE_PAID non deve comparire.
     */
    @Then("SEND non registra il pagamento pagoPA nella timeline della notifica")
    public void paymentNoticePaidNotRegistered() {
        await().during(IGNORED_PAYMENT_OBSERVATION_WINDOW)
                .atMost(IGNORED_PAYMENT_OBSERVATION_WINDOW.plus(Duration.ofMinutes(1)))
                .pollInterval(Duration.ofSeconds(10))
                .until(() -> timelineItems(PAYMENT_NOTICE_PAID).isEmpty());
    }

    /**
     * Il flusso pagoPA delle legali scrive solo PAYMENT_NOTICE_PAID (REQ-LEG-03): l'eventuale NOTIFICATION_PAID verrebbe
     * scritto dallo stesso consumer, quindi la finestra di osservazione parte dopo la registrazione del pagamento pagoPA.
     */
    @Then("SEND non registra il pagamento pagoPA come pagamento comunicato dall'ente")
    public void notificationPaidNotRegistered() {
        await().during(SIDE_EFFECT_OBSERVATION_WINDOW)
                .atMost(SIDE_EFFECT_OBSERVATION_WINDOW.plus(Duration.ofSeconds(30)))
                .pollInterval(Duration.ofSeconds(5))
                .until(() -> timelineItems(SENDER_PAYMENT_CATEGORY).isEmpty());
    }

    /**
     * PAYMENT_NOTICE_PAID è diagnostico e non va esposto (REQ-LEG-04, REQ-INF-06). Il suo id è costruito come quello di
     * NOTIFICATION_PAID con la chiave PAYMENT_NOTICE_PAID (SRS §6.5). Una categoria sconosciuta esposta dall'API
     * farebbe fallire anche la deserializzazione della risposta.
     */
    @Then("il pagamento pagoPA non è esposto nella timeline della notifica")
    public void paymentNoticePaidNotExposed() {
        assertElementNotExposed(PAYMENT_NOTICE_PAID);
    }

    /**
     * PAYMENT_NOTICE_PAID è diagnostico anche per pn-stream, sia per le legali sia per le bonarie (REQ-STR-01).
     */
    @Then("il pagamento pagoPA non è esposto negli eventi dello stream dell'ente")
    public void paymentNoticePaidNotStreamed() {
        assertElementNotStreamed(PAYMENT_NOTICE_PAID);
    }

    /**
     * Lo stream dell'ente, creato prima della notifica, viene letto tutto dall'inizio e devono esserci eventi della
     * notifica, altrimenti la verifica non sarebbe significativa. Come per l'API, una categoria sconosciuta esposta
     * farebbe fallire anche la deserializzazione degli eventi.
     */
    private void assertElementNotStreamed(String elementIdPrefix) {
        await().pollDelay(STREAM_PROPAGATION_DELAY)
                .atMost(STREAM_PROPAGATION_DELAY.plus(POLL_INTERVAL))
                .until(() -> true);
        List<ProgressResponseElementV30> events = streamEventsOfNotification(webhookSteps.getStreamId(STREAM_VERSION));
        assertThat(events)
                .as("Lo stream dell'ente non contiene eventi della notifica %s", sharedSteps.getNotificationIun())
                .isNotEmpty();
        assertThat(events.stream().map(this::streamedElementId).toList())
                .as("Lo stream dell'ente non deve esporre elementi %s della notifica %s", elementIdPrefix, sharedSteps.getNotificationIun())
                .noneMatch(elementId -> elementId != null && elementId.startsWith(elementIdPrefix));
    }

    private String streamedElementId(ProgressResponseElementV30 event) {
        if (sharedSteps.isLegalNotification()) {
            return event.getElement() != null ? event.getElement().getElementId() : null;
        }
        return event.getInformalElement() != null ? event.getInformalElement().getElementId() : null;
    }

    private List<ProgressResponseElementV30> streamEventsOfNotification(UUID streamId) {
        String iun = sharedSteps.getNotificationIun();
        List<ProgressResponseElementV30> events = new ArrayList<>();
        String lastEventId = null;
        for (int call = 0; call < MAX_STREAM_CONSUME_CALLS; call++) {
            List<ProgressResponseElementV30> page = webhookSteps.getWebhookB2bClient().consumeEventStreamV30(streamId, lastEventId);
            if (page == null || page.isEmpty()) {
                return events;
            }
            page.stream().filter(event -> iun.equals(event.getIun())).forEach(events::add);
            lastEventId = page.get(page.size() - 1).getEventId();
        }
        throw new IllegalStateException("Stream " + streamId + " non letto per intero dopo " + MAX_STREAM_CONSUME_CALLS + " chiamate");
    }

    /**
     * Gli elementi diagnostici hanno id che iniziano con la categoria (SRS §6.5, §6.7).
     */
    private void assertElementNotExposed(String elementIdPrefix) {
        List<String> exposedElementIds = sharedSteps.isLegalNotification()
                ? sharedSteps.getSentNotificationLastVersion().getTimeline().stream().map(element -> element.getElementId()).toList()
                : sharedSteps.getSentInformalSentNotificationLastVersion().getTimeline().stream().map(InformalTimelineElementV1::getElementId).toList();
        assertThat(exposedElementIds)
                .as("La timeline esposta della notifica %s non deve contenere elementi %s", sharedSteps.getNotificationIun(), elementIdPrefix)
                .noneMatch(elementId -> elementId != null && elementId.startsWith(elementIdPrefix));
    }

    @Then("la notifica risulta pagata")
    public void legalNotificationPaid() {
        legalNotificationSteps.siAttendeIlCorrettoPagamentoDellaNotifica();
    }

    @Then("il pagamento risulta comunicato dall'ente")
    public void paymentNotifiedBySender() {
        awaitTimelineElementOnDynamo(SENDER_PAYMENT_CATEGORY);
    }

    @Then("la comunicazione bonaria risulta pagata")
    public void informalNotificationPaid() {
        informalNotificationSteps.verifyTimelineElementArrive(INFORMAL_PAYMENT);
    }

    /**
     * PAYMENT e PAYMENT_NOTICE_PAID impostano paid = true sui metadati di ricerca della bonaria (REQ-MET-01).
     * L'esposizione nell'API di ricerca (REQ-MET-02) richiede il client generato dal contratto con CommunicationOutcomes.paid.
     */
    @Then("i metadati di ricerca della comunicazione bonaria la indicano come pagata")
    public void informalSearchMetadataPaid() {
        String recipientTaxId = sendSharedContext.getInformalNotificationContext().getRecipient().getDestinatario().getTaxId();
        searchMetadataSteps.verifyNotificationsMetadataAttributes(sharedSteps.getNotificationIun(), recipientTaxId,
                INFORMAL_RECIPIENT_TYPE, Map.of(PAID_METADATA, Boolean.TRUE.toString()));
    }

    /**
     * Il pagamento da App IO produce anche un SEND_DIGITAL_MESSAGE_PROGRESS con code PAID (SRS §3.3): è la prova che
     * il secondo canale ha davvero comunicato il pagamento, anche quando il suo PAYMENT viene scartato come duplicato.
     */
    @Then("App IO ha comunicato il pagamento dell'avviso")
    public void appIoNotifiedPayment() {
        informalNotificationSteps.verifyTimelineElementAndDetailsArrive(IO_PAYMENT_PROGRESS, Map.of("details_deliveryDetailCode", IO_PAYMENT_PROGRESS_CODE));
    }

    /**
     * Va usato dopo le verifiche che entrambi i canali hanno comunicato il pagamento: la finestra copre solo l'ordine
     * non definito fra PAYMENT_NOTICE_PAID e PAYMENT nel consumer pagoPA (SRS 13.15). Per tutta la finestra non deve
     * comparire un secondo elemento e alla fine deve essercene esattamente uno.
     */
    @Then("nella timeline della comunicazione bonaria è presente un solo elemento {string}")
    public void singleInformalTimelineElement(String category) {
        await().during(SIDE_EFFECT_OBSERVATION_WINDOW)
                .atMost(SIDE_EFFECT_OBSERVATION_WINDOW.plus(Duration.ofSeconds(30)))
                .pollInterval(Duration.ofSeconds(5))
                .until(() -> informalTimelineCount(category) <= 1);
        assertThat(informalTimelineCount(category))
                .as("Attesa una sola occorrenza di %s nella timeline della comunicazione bonaria %s", category, sharedSteps.getNotificationIun())
                .isEqualTo(1);
    }

    /**
     * Il paymentSourceChannel non distingue i canali (vedi {@link PaymentChannel}): il PAYMENT scritto da pagoPA ha
     * come eventTimestamp la data di pagamento pubblicata dal test (WI-5.7), quello scritto da App IO no.
     * Con entrambi i canali la verifica rileva anche la sovrascrittura del PAYMENT da parte del secondo canale,
     * che il solo conteggio degli elementi non distingue.
     */
    @Then("l'elemento PAYMENT della comunicazione bonaria è stato scritto dal pagamento comunicato da {string}")
    public void informalPaymentWrittenBy(String channel) {
        List<InformalTimelineElementV1> payments = NotificationInformalUtilsWorkFlowV1
                .getTimelineElementsByCategory(sharedSteps.getSentInformalSentNotificationLastVersion(), INFORMAL_PAYMENT);
        assertThat(payments).as("Elemento %s non presente nella comunicazione bonaria %s", INFORMAL_PAYMENT, sharedSteps.getNotificationIun()).hasSize(1);
        InformalTimelineElementDetailsV1 details = payments.get(0).getDetails();
        assertThat(details).as("Details dell'elemento %s non valorizzati", INFORMAL_PAYMENT).isNotNull();
        assertThat(details.getEventTimestamp()).as("eventTimestamp dell'elemento %s non valorizzato", INFORMAL_PAYMENT).isNotNull();

        Instant paymentDate = details.getEventTimestamp().toInstant();
        Instant pagoPaPaymentDate = context.getPagoPaPaymentDateTime();
        if (PaymentChannel.fromLabel(channel) == PaymentChannel.PAGOPA) {
            assertThat(pagoPaPaymentDate).as("Nessun pagamento pagoPA pubblicato nello scenario").isNotNull();
            assertThat(paymentDate)
                    .as("Il %s deve riportare la data del pagamento pubblicato da pagoPA", INFORMAL_PAYMENT)
                    .isEqualTo(pagoPaPaymentDate);
        } else {
            assertThat(details.getPaymentSourceChannel())
                    .as("Il %s deve essere scritto dal flusso App IO", INFORMAL_PAYMENT)
                    .isEqualTo(APP_IO_PAYMENT_SOURCE_CHANNEL);
            if (pagoPaPaymentDate != null) {
                assertThat(paymentDate)
                        .as("Il %s non deve riportare la data del pagamento pubblicato da pagoPA", INFORMAL_PAYMENT)
                        .isNotEqualTo(pagoPaPaymentDate);
            }
        }
    }

    // =========================================================================
    // WORKFLOW E STATO
    // =========================================================================

    /**
     * Con stopOnPaid = true il pagamento chiude subito il workflow (REQ-INF-05): per tutta la durata del primo canale
     * della campagna non deve partire alcun invio su un canale successivo. La campagna non deve prevedere feedback
     * desiderati sul primo canale, altrimenti la chiusura potrebbe dipendere da quelli.
     */
    @Then("il workflow della comunicazione bonaria si interrompe al pagamento senza proseguire sugli altri canali")
    public void informalWorkflowStopsOnPaid() {
        informalNotificationSteps.verifyTimelineElementArrive(WORKFLOW_DONE_REACHED);
        informalNotificationSteps.verifyFinalNotificationStatus(INFORMAL_STATUS_COMPLETED);
        await().during(ESCALATION_WINDOW)
                .atMost(ESCALATION_WINDOW.plus(Duration.ofMinutes(1)))
                .pollInterval(Duration.ofSeconds(10))
                .until(() -> escalationSends().isEmpty());
    }

    /**
     * Con stopOnPaid = false o assente il pagamento non interrompe il workflow: allo scadere del primo canale la
     * comunicazione viene inviata sul canale successivo, senza chiusure precedenti. Lo scenario è significativo solo
     * se il pagamento è stato registrato prima di quell'invio.
     */
    @Then("il workflow della comunicazione bonaria prosegue sugli altri canali dopo il pagamento")
    public void informalWorkflowContinuesAfterPayment() {
        InformalTimelineElementV1 escalation = await().atMost(ESCALATION_WINDOW).pollInterval(Duration.ofSeconds(10))
                .until(() -> escalationSends().stream().findFirst(), Optional::isPresent)
                .orElseThrow();
        List<InformalTimelineElementV1> timeline = sharedSteps.getSentInformalSentNotificationLastVersion().getTimeline();

        assertThat(elementsOf(timeline, INFORMAL_PAYMENT))
                .as("Il %s deve precedere l'invio sul canale %s, altrimenti lo scenario non verifica la prosecuzione del workflow",
                        INFORMAL_PAYMENT, escalation.getDetails().getChannel())
                .isNotEmpty()
                .allMatch(payment -> payment.getTimestamp().isBefore(escalation.getTimestamp()));
        assertThat(elementsOf(timeline, WORKFLOW_DONE_REACHED))
                .as("Il workflow non deve chiudersi prima dell'invio sul canale %s", escalation.getDetails().getChannel())
                .noneMatch(workflowDone -> workflowDone.getTimestamp().isBefore(escalation.getTimestamp()));
    }

    /**
     * Invii digitali successivi al primo canale usato, in ordine cronologico.
     */
    private List<InformalTimelineElementV1> escalationSends() {
        List<InformalTimelineElementV1> sends = elementsOf(sharedSteps.getSentInformalSentNotificationLastVersion().getTimeline(), SEND_DIGITAL_MESSAGE)
                .stream()
                .sorted(Comparator.comparing(InformalTimelineElementV1::getTimestamp))
                .toList();
        if (sends.isEmpty()) {
            return sends;
        }
        String firstChannel = sends.get(0).getDetails().getChannel();
        return sends.stream().filter(send -> !firstChannel.equals(send.getDetails().getChannel())).toList();
    }

    private static List<InformalTimelineElementV1> elementsOf(List<InformalTimelineElementV1> timeline, String category) {
        return timeline.stream()
                .filter(element -> element.getCategory() != null && category.equals(element.getCategory().getValue()))
                .toList();
    }

    @Then("la notifica legale resta nello stato {string}")
    public void legalNotificationStatusUnchanged(String expectedStatus) {
        assertThat(sharedSteps.getSentNotificationLastVersion().getNotificationStatus().getValue())
                .as("Lo stato della notifica legale %s non deve cambiare dopo il pagamento", sharedSteps.getNotificationIun())
                .isEqualTo(expectedStatus);
    }

    @Then("la comunicazione bonaria resta nello stato {string}")
    public void informalNotificationStatusUnchanged(String expectedStatus) {
        assertThat(sharedSteps.getSentInformalSentNotificationLastVersion().getNotificationStatus().getValue())
                .as("Lo stato della comunicazione bonaria %s non deve cambiare dopo il pagamento", sharedSteps.getNotificationIun())
                .isEqualTo(expectedStatus);
    }

    // =========================================================================
    // PAGAMENTI NON DI COMPETENZA DI SEND
    // =========================================================================

    /**
     * Prova che l'evento è arrivato a pn-notification-cost-service ed è stato scartato perché l'avviso non è su
     * pn-PaymentInfo (REQ-FIL-02, REQ-FIL-07): le verifiche negative successive partono da un'elaborazione avvenuta.
     */
    @Then("SEND scarta il pagamento perché l'avviso non è censito")
    public void paymentDiscardedForUnknownNotice() {
        String query = String.format("\"%s\" \"%s\"", PAYMENT_DISCARDED_LOG_MESSAGE, context.getNotice().paymentInfoPk());
        try {
            await().atMost(ASYNC_PROCESSING_TIMEOUT).pollInterval(Duration.ofSeconds(10)).untilAsserted(() ->
                    assertThat(logCount(NOTIFICATION_COST_LOG_GROUP, query))
                            .as("Nessun log di scarto su %s per la ricerca %s", NOTIFICATION_COST_LOG_GROUP, query)
                            .isPositive());
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    /**
     * Va usato dopo la verifica dello scarto: per tutta la finestra di osservazione non devono comparire errori,
     * così è coperto anche un log scritto o indicizzato da CloudWatch dopo quello di scarto.
     */
    @Then("SEND ignora il pagamento senza registrare errori")
    public void paymentIgnoredWithoutErrors() {
        String errorLogQuery = String.format("\"ERROR\" \"%s\"", context.getNotice().noticeCode());
        await().during(IGNORED_PAYMENT_OBSERVATION_WINDOW)
                .atMost(IGNORED_PAYMENT_OBSERVATION_WINDOW.plus(Duration.ofMinutes(1)))
                .pollInterval(Duration.ofSeconds(10))
                .until(() -> logCount(NOTIFICATION_COST_LOG_GROUP, errorLogQuery) == 0);
    }

    private void awaitTimelineElementOnDynamo(String category) {
        try {
            await().atMost(ASYNC_PROCESSING_TIMEOUT).pollInterval(POLL_INTERVAL).untilAsserted(() ->
                    assertThat(timelineItems(category)).as("Elemento %s non presente nella timeline", category).isNotEmpty());
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    private List<Map<String, AttributeValue>> timelineItems(String category) {
        QueryResponse response = dynamoDbService.call(DynamoTableName.TIMELINE, Map.of(
                ":v_iun", AttributeValue.builder().s(sharedSteps.getNotificationIun()).build(),
                ":v_category", AttributeValue.builder().s(category).build()));
        return response.items();
    }

    private List<Map<String, AttributeValue>> paymentInfoItems(String pk) {
        return dynamoDbService.call(DynamoTableName.PAYMENT_INFO, Map.of(
                ":v_pk", AttributeValue.builder().s(pk).build())).items();
    }

    private long informalTimelineCount(String category) {
        return NotificationInformalUtilsWorkFlowV1
                .getTimelineElementsByCategory(sharedSteps.getSentInformalSentNotificationLastVersion(), category)
                .size();
    }

    private int logCount(String logGroup, String query) {
        long minutesSincePublish = Duration.between(context.getLastEventPublishedAt(), Instant.now()).toMinutes() + 1;
        return cloudWatchLogsClient.filterLogEvents(CloudWatchQueryBuilder.search(logGroup, query, minutesSincePublish))
                .events()
                .size();
    }
}
