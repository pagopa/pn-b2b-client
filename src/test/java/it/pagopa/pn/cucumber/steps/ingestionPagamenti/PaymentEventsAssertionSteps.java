package it.pagopa.pn.cucumber.steps.ingestionPagamenti;

import io.cucumber.java.en.Then;
import it.pagopa.common.polling.Poller;
import it.pagopa.common.polling.PollingConfig;
import it.pagopa.common.util.CloudWatchQueryBuilder;
import it.pagopa.pn.client.b2b.pa.domain.DynamoTableName;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.InformalTimelineElementDetailsV1;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.InformalTimelineElementV1;
import it.pagopa.pn.client.b2b.pa.service.DynamoDbService;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.ProgressResponseElementV30;
import it.pagopa.pn.cucumber.steps.SendSharedContext;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.config.PaymentEventsSettings;
import it.pagopa.pn.cucumber.steps.ingestionPagamenti.context.PaymentEventsScenarioContext;
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
import java.util.function.Consumer;
import java.util.function.Predicate;

import static it.pagopa.pn.cucumber.utils.ObservationWindow.assertHoldsFor;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifica degli effetti dei pagamenti su SEND: posizione debitoria, timeline, stato e workflow delle notifiche.
 */
@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class PaymentEventsAssertionSteps {

    private static final String PAYMENT_NOTICE_PAID = "PAYMENT_NOTICE_PAID";
    // NOTIFICATION_PAID nell'API, PAYMENT su pn-Timelines
    private static final String SENDER_PAYMENT_CATEGORY = "PAYMENT";
    private static final String INFORMAL_PAYMENT = "PAYMENT";
    private static final String WORKFLOW_DONE_REACHED = "WORKFLOW_DONE_REACHED";
    private static final String SEND_DIGITAL_MESSAGE = "SEND_DIGITAL_MESSAGE";
    private static final String INFORMAL_STATUS_COMPLETED = "COMPLETED_REACHED";
    private static final String COMMUNICATION_TYPE = "communicationType";
    private static final String IO_PAYMENT_PROGRESS = "SEND_DIGITAL_MESSAGE_PROGRESS";
    private static final String IO_PAYMENT_PROGRESS_CODE = "PAID";
    private static final String APP_IO_PAYMENT_SOURCE_CHANNEL = "IO";
    private static final String COST_VALIDATION_REQUEST = "NOTIFICATION_COST_VALIDATION_REQUEST";
    private static final String COST_VALIDATION_RESPONSE = "NOTIFICATION_COST_VALIDATION_RESPONSE";
    private static final String COST_VALIDATION_ELEMENT_PREFIX = "NOTIFICATION_COST_VALIDATION";
    private static final String PAID_METADATA = "paid";
    private static final String INFORMAL_RECIPIENT_TYPE = "PF";

    private static final Duration POLL_INTERVAL = Duration.ofSeconds(5);
    private static final Duration SLOW_POLL_INTERVAL = Duration.ofSeconds(10);
    private static final Duration STREAM_POLL_INTERVAL = Duration.ofSeconds(15);
    private static final String STREAM_VERSION = "V30";
    private static final int MAX_STREAM_CONSUME_CALLS = 500;

    private final PaymentEventsScenarioContext context;
    private final PaymentEventsSettings settings;
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

    @Then("SEND registra la validazione della posizione debitoria nella timeline della comunicazione bonaria")
    public void costValidationRegistered() {
        awaitTimelineElementOnDynamo(COST_VALIDATION_REQUEST);
        awaitTimelineElementOnDynamo(COST_VALIDATION_RESPONSE);
    }

    @Then("la validazione della posizione debitoria non è esposta nella timeline della notifica")
    public void costValidationNotExposed() {
        assertElementNotExposed(COST_VALIDATION_ELEMENT_PREFIX);
    }

    @Then("la validazione della posizione debitoria non è esposta negli eventi dello stream dell'ente")
    public void costValidationNotStreamed() {
        assertElementNotStreamed(COST_VALIDATION_ELEMENT_PREFIX);
    }

    /**
     * Il record verrebbe scritto in modo asincrono: l'assenza si verifica per tutta la finestra di osservazione.
     */
    @Then("SEND non registra costi di notificazione per la comunicazione bonaria")
    public void noDeliveryCostForInformal() {
        assertHoldsFor(settings.getSideEffectWindow(), POLL_INTERVAL, this::deliveryCostItems, items ->
                assertThat(items).as("Costi di notificazione registrati per la comunicazione bonaria %s", sharedSteps.getNotificationIun()).isEmpty());
    }

    private List<Map<String, AttributeValue>> deliveryCostItems() {
        return dynamoDbService.call(DynamoTableName.NOTIFICATION_DELIVERY_COST, Map.of(
                ":v_pk", AttributeValue.builder().s(sharedSteps.getNotificationIun()).build(),
                ":v_sk", AttributeValue.builder().n("0").build())).items();
    }

    @Then("la posizione debitoria dell'avviso è censita come {string}")
    public void paymentInfoHasCommunicationType(String expectedCommunicationType) {
        String pk = context.getNotice().paymentInfoPk();
        Consumer<List<Map<String, AttributeValue>>> verify = items -> {
            assertThat(items).as("Record pn-PaymentInfo con pk %s non trovato", pk).hasSize(1);
            AttributeValue communicationType = items.get(0).get(COMMUNICATION_TYPE);
            assertThat(communicationType)
                    .as("Il record pn-PaymentInfo %s deve avere l'attributo %s", pk, COMMUNICATION_TYPE)
                    .isNotNull();
            assertThat(communicationType.s()).isEqualTo(expectedCommunicationType);
        };
        try {
            Poller.pollUntil(() -> paymentInfoItems(pk), verify, PollingConfig.of(settings.getAsyncProcessingTimeout(), POLL_INTERVAL));
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
     * L'API della timeline non espone PAYMENT_NOTICE_PAID: il conteggio si fa su DynamoDB.
     */
    @Then("nella timeline della notifica è presente un solo pagamento pagoPA")
    public void singlePaymentNoticePaid() {
        List<Map<String, AttributeValue>> payments = assertHoldsFor(settings.getSideEffectWindow(), POLL_INTERVAL,
                () -> timelineItems(PAYMENT_NOTICE_PAID),
                current -> assertThat(current)
                        .as("Più di un %s nella timeline della notifica %s", PAYMENT_NOTICE_PAID, sharedSteps.getNotificationIun())
                        .hasSizeLessThanOrEqualTo(1));
        assertThat(payments)
                .as("Attesa una sola occorrenza di %s nella timeline della notifica %s", PAYMENT_NOTICE_PAID, sharedSteps.getNotificationIun())
                .hasSize(1);
    }

    @Then("SEND non registra il pagamento pagoPA nella timeline della notifica")
    public void paymentNoticePaidNotRegistered() {
        assertNoTimelineElementFor(settings.getIgnoredPaymentWindow(), sharedSteps.getNotificationIun(), PAYMENT_NOTICE_PAID);
    }

    /**
     * L'eventuale NOTIFICATION_PAID verrebbe scritto dallo stesso consumer di PAYMENT_NOTICE_PAID: la finestra parte
     * dopo la registrazione del pagamento pagoPA.
     */
    @Then("SEND non registra il pagamento pagoPA come pagamento comunicato dall'ente")
    public void notificationPaidNotRegistered() {
        assertNoTimelineElementFor(settings.getSideEffectWindow(), sharedSteps.getNotificationIun(), SENDER_PAYMENT_CATEGORY);
    }

    @Then("il pagamento pagoPA non è esposto nella timeline della notifica")
    public void paymentNoticePaidNotExposed() {
        assertElementNotExposed(PAYMENT_NOTICE_PAID);
    }

    @Then("il pagamento pagoPA non è esposto negli eventi dello stream dell'ente")
    public void paymentNoticePaidNotStreamed() {
        assertElementNotStreamed(PAYMENT_NOTICE_PAID);
    }

    /**
     * Lo stream viene riletto dall'inizio per tutto il tempo di propagazione verso pn-stream. Deve contenere eventi
     * della notifica, altrimenti l'assenza dell'elemento non sarebbe significativa.
     */
    private void assertElementNotStreamed(String elementIdPrefix) {
        UUID streamId = webhookSteps.getStreamId(STREAM_VERSION);
        String iun = sharedSteps.getNotificationIun();
        List<ProgressResponseElementV30> events = assertHoldsFor(settings.getStreamPropagationDelay(), STREAM_POLL_INTERVAL,
                () -> streamEventsOf(streamId, iun),
                streamed -> assertThat(streamed.stream().map(this::streamedElementId).toList())
                        .as("Lo stream dell'ente non deve esporre elementi %s della notifica %s", elementIdPrefix, iun)
                        .noneMatch(elementId -> elementId != null && elementId.startsWith(elementIdPrefix)));
        assertThat(events).as("Lo stream dell'ente non contiene eventi della notifica %s", iun).isNotEmpty();
    }

    private String streamedElementId(ProgressResponseElementV30 event) {
        if (sharedSteps.isLegalNotification()) {
            return event.getElement() != null ? event.getElement().getElementId() : null;
        }
        return event.getInformalElement() != null ? event.getInformalElement().getElementId() : null;
    }

    private List<ProgressResponseElementV30> streamEventsOf(UUID streamId, String iun) {
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
     * Gli id degli elementi diagnostici iniziano con la categoria.
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

    @Then("i metadati di ricerca della comunicazione bonaria la indicano come pagata")
    public void informalSearchMetadataPaid() {
        String recipientTaxId = sendSharedContext.getInformalNotificationContext().getRecipient().getDestinatario().getTaxId();
        searchMetadataSteps.verifyNotificationsMetadataAttributes(sharedSteps.getNotificationIun(), recipientTaxId,
                INFORMAL_RECIPIENT_TYPE, Map.of(PAID_METADATA, Boolean.TRUE.toString()));
    }

    /**
     * Prova che App IO ha comunicato il pagamento anche quando il suo PAYMENT viene scartato come duplicato.
     */
    @Then("App IO ha comunicato il pagamento dell'avviso")
    public void appIoNotifiedPayment() {
        informalNotificationSteps.verifyTimelineElementAndDetailsArrive(IO_PAYMENT_PROGRESS, Map.of("details_deliveryDetailCode", IO_PAYMENT_PROGRESS_CODE));
    }

    /**
     * Va usato dopo le verifiche che entrambi i canali hanno comunicato il pagamento.
     */
    @Then("nella timeline della comunicazione bonaria è presente un solo elemento {string}")
    public void singleInformalTimelineElement(String category) {
        long count = assertHoldsFor(settings.getSideEffectWindow(), POLL_INTERVAL, () -> informalTimelineCount(category),
                current -> assertThat(current)
                        .as("Più di un elemento %s nella timeline della comunicazione bonaria %s", category, sharedSteps.getNotificationIun())
                        .isLessThanOrEqualTo(1));
        assertThat(count)
                .as("Attesa una sola occorrenza di %s nella timeline della comunicazione bonaria %s", category, sharedSteps.getNotificationIun())
                .isEqualTo(1);
    }

    /**
     * Il paymentSourceChannel non distingue i canali (vedi {@link PaymentChannel}): solo il PAYMENT scritto da pagoPA
     * ha come eventTimestamp la data di pagamento pubblicata dal test.
     */
    @Then("l'elemento PAYMENT della comunicazione bonaria è stato scritto dal pagamento comunicato da {string}")
    public void informalPaymentWrittenBy(String channel) {
        List<InformalTimelineElementV1> payments = elementsOf(sharedSteps.getSentInformalSentNotificationLastVersion().getTimeline(), INFORMAL_PAYMENT);
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
     * Per tutta la durata del primo canale della campagna non deve partire alcun invio su un canale successivo.
     */
    @Then("il workflow della comunicazione bonaria si interrompe al pagamento senza proseguire sugli altri canali")
    public void informalWorkflowStopsOnPaid() {
        Duration escalationWindow = escalationWindow();
        informalNotificationSteps.verifyTimelineElementArrive(WORKFLOW_DONE_REACHED);
        informalNotificationSteps.verifyFinalNotificationStatus(INFORMAL_STATUS_COMPLETED);
        assertHoldsFor(escalationWindow, SLOW_POLL_INTERVAL, this::escalationSends, sends ->
                assertThat(sends).as("La comunicazione bonaria %s è stata inviata su un canale successivo dopo il pagamento", sharedSteps.getNotificationIun()).isEmpty());
    }

    /**
     * Lo scenario è significativo solo se il pagamento è stato registrato prima dell'invio sul canale successivo.
     */
    @Then("il workflow della comunicazione bonaria prosegue sugli altri canali dopo il pagamento")
    public void informalWorkflowContinuesAfterPayment() {
        Duration escalationWindow = escalationWindow();
        Predicate<Optional<InformalTimelineElementV1>> escalated = Optional::isPresent;
        InformalTimelineElementV1 escalation = Poller.pollUntil(() -> escalationSends().stream().findFirst(), escalated,
                        PollingConfig.of(escalationWindow, SLOW_POLL_INTERVAL),
                        last -> String.format("La comunicazione bonaria %s non è stata inviata su un canale successivo entro %s",
                                sharedSteps.getNotificationIun(), escalationWindow))
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

    private Duration escalationWindow() {
        assertThat(context.getFirstChannelTimeout())
                .as("Il primo canale della campagna della comunicazione bonaria non ha una durata")
                .isNotNull();
        return context.getFirstChannelTimeout().plus(settings.getEscalationMargin());
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

    @Then("la prima notifica legale resta annullata senza pagamento pagoPA")
    public void previousLegalNotificationCancelledWithoutPayment() {
        String previousIun = context.getPreviousNotificationIun();
        assertThat(previousIun).as("Lo scenario non ha creato una seconda notifica con lo stesso avviso").isNotNull();
        assertThat(timelineItemsOf(previousIun, "NOTIFICATION_CANCELLED"))
                .as("La notifica %s deve risultare annullata", previousIun).isNotEmpty();
        assertNoTimelineElementFor(settings.getSideEffectWindow(), previousIun, PAYMENT_NOTICE_PAID);
    }

    @Then("SEND rifiuta la seconda notifica perché l'avviso è già utilizzato")
    public void secondNotificationRejectedBecauseNoticeAlreadyUsed() {
        assertThat(context.getSecondNotificationRejectionStatus())
                .as("La seconda notifica con lo stesso avviso %s è stata accettata invece di essere rifiutata con 409", context.getNotice())
                .isEqualTo(409);
    }

    @Then("la notifica legale resta nello stato {string}")
    public void legalNotificationStatusUnchanged(String expectedStatus) {
        assertThat(sharedSteps.getSentNotificationLastVersion().getNotificationStatus().getValue())
                .as("Lo stato della notifica legale %s non deve cambiare dopo il pagamento", sharedSteps.getNotificationIun())
                .isEqualTo(expectedStatus);
    }

    @Then("lo stato della notifica legale non cambia dopo il pagamento")
    public void legalNotificationStatusUnchangedAfterPayment() {
        assertThat(context.getLegalStatusBeforePayment())
                .as("Stato della notifica legale prima del pagamento non registrato nello scenario")
                .isNotNull();
        legalNotificationStatusUnchanged(context.getLegalStatusBeforePayment());
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
     * Prova che l'evento è stato elaborato e scartato: le verifiche negative successive partono da un'elaborazione
     * avvenuta. Log group e testo del log sono configurabili (pn.payment-events.cost-service.*).
     */
    @Then("SEND scarta il pagamento perché l'avviso non è censito")
    public void paymentDiscardedForUnknownNotice() {
        String query = String.format("\"%s\" \"%s\"", settings.getPaymentDiscardedLogMessage(), context.getNotice().paymentInfoPk());
        Instant publishedAt = context.getLastEventPublishedAt();
        Consumer<Long> verify = count -> assertThat(count)
                .as("Nessun log di scarto su %s per la ricerca %s", settings.getCostServiceLogGroup(), query)
                .isPositive();
        try {
            Poller.pollUntil(() -> logCount(settings.getCostServiceLogGroup(), query, publishedAt), verify,
                    PollingConfig.of(settings.getAsyncProcessingTimeout(), SLOW_POLL_INTERVAL));
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    /**
     * Va usato dopo la verifica dello scarto, così copre anche un log indicizzato da CloudWatch dopo quello di scarto.
     */
    @Then("SEND ignora il pagamento senza registrare errori")
    public void paymentIgnoredWithoutErrors() {
        String errorLogQuery = String.format("\"ERROR\" \"%s\"", context.getNotice().noticeCode());
        Instant publishedAt = context.getLastEventPublishedAt();
        assertHoldsFor(settings.getIgnoredPaymentWindow(), SLOW_POLL_INTERVAL,
                () -> logCount(settings.getCostServiceLogGroup(), errorLogQuery, publishedAt),
                count -> assertThat(count)
                        .as("Log di errore su %s per la ricerca %s", settings.getCostServiceLogGroup(), errorLogQuery)
                        .isZero());
    }

    private void awaitTimelineElementOnDynamo(String category) {
        Consumer<List<Map<String, AttributeValue>>> verify = items ->
                assertThat(items).as("Elemento %s non presente nella timeline", category).isNotEmpty();
        try {
            Poller.pollUntil(() -> timelineItems(category), verify, PollingConfig.of(settings.getAsyncProcessingTimeout(), POLL_INTERVAL));
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    private void assertNoTimelineElementFor(Duration window, String iun, String category) {
        assertHoldsFor(window, SLOW_POLL_INTERVAL, () -> timelineItemsOf(iun, category), items ->
                assertThat(items).as("Elemento %s presente nella timeline della notifica %s", category, iun).isEmpty());
    }

    private List<Map<String, AttributeValue>> timelineItems(String category) {
        return timelineItemsOf(sharedSteps.getNotificationIun(), category);
    }

    private List<Map<String, AttributeValue>> timelineItemsOf(String iun, String category) {
        QueryResponse response = dynamoDbService.call(DynamoTableName.TIMELINE, Map.of(
                ":v_iun", AttributeValue.builder().s(iun).build(),
                ":v_category", AttributeValue.builder().s(category).build()));
        return response.items();
    }

    private List<Map<String, AttributeValue>> paymentInfoItems(String pk) {
        return dynamoDbService.call(DynamoTableName.PAYMENT_INFO, Map.of(
                ":v_pk", AttributeValue.builder().s(pk).build())).items();
    }

    private long informalTimelineCount(String category) {
        return elementsOf(sharedSteps.getSentInformalSentNotificationLastVersion().getTimeline(), category).size();
    }

    /**
     * Scorre tutte le pagine: CloudWatch può restituire pagine vuote o parziali prima di quelle con i risultati.
     */
    private long logCount(String logGroup, String query, Instant publishedAt) {
        long minutesSincePublish = Duration.between(publishedAt, Instant.now()).toMinutes() + 1;
        return cloudWatchLogsClient.filterLogEventsPaginator(CloudWatchQueryBuilder.search(logGroup, query, minutesSincePublish))
                .events()
                .stream()
                .count();
    }
}
