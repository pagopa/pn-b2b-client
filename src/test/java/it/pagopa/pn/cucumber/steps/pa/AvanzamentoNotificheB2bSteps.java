package it.pagopa.pn.cucumber.steps.pa;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.domain.DynamoTableName;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingFactory;
import it.pagopa.pn.client.b2b.pa.polling.exception.PnPollingException;
import it.pagopa.pn.client.b2b.pa.service.DynamoDbService;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.service.IPnPrivateDeliveryPushExternalClient;
import it.pagopa.pn.client.b2b.pa.service.impl.PnExternalServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v24.ResponsePaperNotificationFailedDto;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v26.NotificationProcessCostResponse;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.b2bVersions.B2bStepsInterface;
import it.pagopa.pn.cucumber.steps.pa.notificationVersions.NotificationVersion;
import it.pagopa.pn.cucumber.steps.utilitySteps.WaitForEventPredicateFilters;
import it.pagopa.pn.cucumber.steps.utilitySteps.checkTimelineElement.TimelineElementCheckFilters;
import it.pagopa.pn.cucumber.utils.DataTest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpStatusCodeException;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static it.pagopa.pn.cucumber.steps.utilitySteps.Costanti.*;
import static it.pagopa.pn.cucumber.steps.utilitySteps.Costanti.PAYMENT;
import static it.pagopa.pn.cucumber.steps.utilitySteps.PollingType.TIMELINE;
import static it.pagopa.pn.cucumber.steps.utilitySteps.checkTimelineElement.TimelineElementCheck.*;
import static it.pagopa.pn.cucumber.utils.NotificationValue.*;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class AvanzamentoNotificheB2bSteps {

    @Getter
    private final SharedSteps sharedSteps;
    @Getter
    private final IPnPaB2bClient b2bClient;
    private final PnExternalServiceClientImpl externalClient;
    @Getter
    private final IPnPrivateDeliveryPushExternalClient pnPrivateDeliveryPushExternalClient;
    private HttpStatusCodeException notificationError;
    @Getter
    private final PnPollingFactory pnPollingFactory;
    @Getter
    private final TimingForPolling timingForPolling;
    @Getter
    private final DynamoDbService dbService;

    private final Map<NotificationVersion, B2bStepsInterface> mapOfVersionSteps = new HashMap<>();

    @Autowired
    public AvanzamentoNotificheB2bSteps(SharedSteps sharedSteps,
                                        TimingForPolling timingForPolling,
                                        IPnPrivateDeliveryPushExternalClient pnPrivateDeliveryPushExternalClient,
                                        DynamoDbService dbService) {
        this.sharedSteps = sharedSteps;
        this.timingForPolling = timingForPolling;
        this.pnPrivateDeliveryPushExternalClient = pnPrivateDeliveryPushExternalClient;

        this.externalClient = sharedSteps.getPnExternalServiceClient();
        this.b2bClient = sharedSteps.getB2bClient();
        this.pnPollingFactory = sharedSteps.getPollingFactory();
        this.dbService = dbService;
    }

    private B2bStepsInterface getB2bStepsInterface() {
        NotificationVersion notificationVersion = sharedSteps.getVersionUsed() == null ?
                sharedSteps.getNotificationVersion(MOST_RECENT) : sharedSteps.getVersionUsed();
        return getB2bStepsInterface(notificationVersion);
    }

    private B2bStepsInterface getB2bStepsInterface(NotificationVersion notificationVersion) {
        if (mapOfVersionSteps.get(notificationVersion) == null) {
            mapOfVersionSteps.put(notificationVersion, NotificationVersion.createB2bStep(notificationVersion, this));
        }
        return mapOfVersionSteps.get(notificationVersion);
    }

    @Then("vengono letti gli eventi fino allo stato della notifica {string} dalla PA {string}")
    public void readingEventsNotificationPA(String status, String paName) {
        sharedSteps.setPA(paName);
        readingEventUpToTheStatusOfNotification(status);
        sharedSteps.setPA(DEFAULT_PA);
    }

    @Then("vengono letti gli eventi fino allo stato della notifica {string}")
    public void readingEventUpToTheStatusOfNotification(String status) {
        readEventsUpToStatus(sharedSteps.getVersionUsed(), status, true);
    }

    @Then("vengono letti gli eventi fino allo stato della notifica {string} con la versione {string}")
    public void readEventsUpToStatusWithVersion(String status, String version) {
        readEventsUpToStatus(sharedSteps.getNotificationVersion(version), status, true);
    }

    @Then("vengono letti gli eventi fino allo stato della notifica {string} per il destinatario {int} e presente l'evento {string}")
    public void readingEventUpToTheStatusOfNotification(String status, int recIndex, String evento) {
        readEventsUpToStatus(sharedSteps.getVersionUsed(), status, true);
        getB2bStepsInterface().checkEventPresenceForRecipient(recIndex, evento);
    }

    @Then("recuperando la fullSentNotification con la versione b2b {string} {is} presente l'elemento di timeline {string}")
    public void checkPresenceOfTimelineElement(String version, boolean isPresent, String timelineEventCategory) {
        NotificationVersion notificationVersion = sharedSteps.getNotificationVersion(version);
        getB2bStepsInterface(notificationVersion).checkFullSentNotificationWithVersion(isPresent, timelineEventCategory);
    }

    @Then("recuperando la fullSentNotification con la versione b2b {string} controllo che non sia presente l'elementoto {string} nei relatedTimelineElements")
    public void checkPresenceOfTimelineRelatedElement(String version, String timelineEventCategory) {
        NotificationVersion notificationVersion = sharedSteps.getNotificationVersion(version);
        getB2bStepsInterface(notificationVersion).checkFullSentNotificationRelatedElementWithVersion(timelineEventCategory);
    }

    @Then("controllo la correttezza dei timelineElementId degli elementi di timeline della fullSentNotification con versione b2b {string}")
    public void checkReworkTimelineElement(String version) {
        NotificationVersion notificationVersion = sharedSteps.getNotificationVersion(version);
        getB2bStepsInterface(notificationVersion).checkReworkTimelineWithVersion();
    }

    /**
     * Poiché il metodo non era usato da alcuno scenario, sono stati parametrizzati i due
     * metodi che richiamavano questo controllo unicamente per lo stato VIEWED ed è stato aggiunto il
     *
     * @param has (abbia|non abbia), usato nella classe di utility ParameterTypes,
     *            in modo da poterlo usare sia per il caso positivo che il caso negativo.
     */
    @Then("si verifica che la notifica {has} lo stato {string}")
    public void checksIfNotificationHasStatus(boolean has, String status) {
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.readEventsUpToStatus(status, has);
    }

    private void readEventsUpToStatus(NotificationVersion notificationVersion, String status, boolean exists) {
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface(notificationVersion);
        b2bStepsInterface.readEventsUpToStatus(status, exists);
    }

    @Then("viene verificato che il numero di elementi di timeline {string} della notifica sia di {int}")
    public void checkNumberOfTimelineElementsWithCategory(String timelineEventCategory, Integer size) {
        try {
            getB2bStepsInterface().checkNumberOfTimelineElements(timelineEventCategory, size);
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @And("viene verificato che il numero di elementi di timeline {string} sia di {int}")
    public void checkNumberOfTimelineElementsWithCategoryFromMap(String timelineEventCategory, Integer size, Map<String, String> dataMap) {
        try {
            getB2bStepsInterface().checkNumberOfTimelineElementsFromData(timelineEventCategory, size, dataMap);
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica annullata {string}")
    public void readingEventUpToTheTimelineElementOfNotificationDelete(String timelineEventCategory) {
        readEventsUpToTimelineElement(sharedSteps.getVersionUsed(), timelineEventCategory);
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string}")
    public void readEventUpToTimelineElement(String timelineEventCategory) {
        readEventsUpToTimelineElement(sharedSteps.getVersionUsed(), timelineEventCategory);
    }

    @Then("vengono letti gli eventi da delivery push fino all'elemento di timeline della notifica {string}")
    public void readEventUpToTimelineElementFromDeliveryPush(String timelineEventCategory) {
        getB2bStepsInterface().readEventsUpToTimelineElementFromDeliveryPush(timelineEventCategory, null, true);
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con la versione {string}")
    public void readEventUpToTimelineElementWithVersion(String timelineEventCategory, String version) {
        readEventsUpToTimelineElement(sharedSteps.getNotificationVersion(version), timelineEventCategory);
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con notificationCost uguale a {string}")
    public void TimelineElementOfNotification(String timelineEventCategory, String cost) {
        readEventsUpToTimelineElement(sharedSteps.getVersionUsed(), timelineEventCategory);
        getB2bStepsInterface().checkNotificationCost(cost);
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con indirizzo normalizzato:")
    public void vengonoLettiGliEventiFinoAllElementoDiTimelineDellaNotificaConIndirizzoNormalizzato(String timelineEventCategory, DataTable table) {
        readEventsUpToTimelineElement(sharedSteps.getVersionUsed(), timelineEventCategory);
        getB2bStepsInterface().checkNormalizedAddress(table);
    }

    private void readEventsUpToTimelineElement(NotificationVersion notificationVersion, String timelineEventCategory) {
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface(notificationVersion);
        b2bStepsInterface.readEventsUpToTimelineElement(timelineEventCategory);
    }

    @Then("gli eventi di timeline ricevuti sono i seguenti$")
    public void verifyTimelineEventsAreTheOnesExpected(List<String> expectedEvents) {
        List<String> actualTimeline = Optional.ofNullable(sharedSteps.getSentNotificationLastVersion())
                .map(FullSentNotificationV29::getTimeline)
                .orElse(List.of())
                .stream()
                .map(TimelineElementV28::getCategory)
                .filter(Objects::nonNull)
                .map(TimelineElementCategoryV28::toString)
                .toList();
        try {
            Assertions.assertFalse(expectedEvents.stream().anyMatch(Predicate.not(actualTimeline::contains)));
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @Then("viene verificato che per l'elemento di timeline della notifica {string} non ci siano duplicati")
    public void checkTimeLineEventWithoutDuplicates(String timelineEventCategory) {
        getB2bStepsInterface().checkForNoDuplicatedTimelineElements(timelineEventCategory);
    }

    @Then("si verifica che scheduleDate del SCHEDULE_REFINEMENT sia uguale al timestamp di REFINEMENT per l'utente {int}")
    public void verificationDateScheduleRefinementWithRefinement(Integer destinatario) {
        try {
            FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
            OffsetDateTime ricezioneRaccomandata = fullSentNotification.getTimeline().stream().filter(elem ->
                    elem.getCategory().getValue().equals(SCHEDULE_REFINEMENT)
                            && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getDetails().getSchedulingDate();
            OffsetDateTime refinementDate = fullSentNotification.getTimeline().stream().filter(elem ->
                    elem.getCategory().getValue().equals(REFINEMENT)
                            && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();

            log.info("DESTINATARIO : {}", destinatario);
            log.info("ricezioneRaccomandata : {}", ricezioneRaccomandata);
            log.info("refinementDate : {}", refinementDate);

            assertEquals(ricezioneRaccomandata, refinementDate);

        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @Then("si verifica che il timestamp dell'elemento di timeline della notifica SEND_ANALOG_FEEDBACK con deliveryDetailCode RECAG012 sia uguale al timestamp di REFINEMENT")
    public void verificationDateDeliveryDetailCodeRECAG012WithRefinement() {
        try {
            FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
            OffsetDateTime ricezioneRECAG012 = fullSentNotification.getTimeline().stream().filter(elem ->
                    elem.getCategory().getValue().equals(SEND_ANALOG_FEEDBACK)
                            && elem.getDetails().getDeliveryDetailCode().equals("RECAG012")).findAny().get().getDetails().getEventTimestamp();
            OffsetDateTime refinementDate = fullSentNotification.getTimeline().stream().filter(elem ->
                    elem.getCategory().getValue().equals(REFINEMENT)
                            && elem.getDetails().getRecIndex().equals(0)).findAny().get().getTimestamp();

            log.info("ricezioneRaccomandata : {}", ricezioneRECAG012);
            log.info("refinementDate : {}", refinementDate);

            Assertions.assertTrue(checkOffsetDateTime(ricezioneRECAG012, refinementDate));

        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    private boolean checkOffsetDateTime(OffsetDateTime offsetDateTime1, OffsetDateTime offsetDateTime2) {
        return offsetDateTime1.equals(offsetDateTime2);
    }

    @Then("verifica date business in timeline COMPLETELY_UNREACHABLE per l'utente {int}")
    public void verificationDateCompletelyUnreachableWithRefinement(Integer destinatario) {
        try {
            FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
            OffsetDateTime schedulingDate = fullSentNotification.getTimeline().stream().filter(elem -> elem.getCategory().getValue().equals(SCHEDULE_REFINEMENT) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();
            OffsetDateTime completelyUnreachableDate = fullSentNotification.getTimeline().stream().filter(elem -> elem.getCategory().getValue().equals(COMPLETELY_UNREACHABLE) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();
            OffsetDateTime completelyUnreachableRequestDate = fullSentNotification.getTimeline().stream().filter(elem -> elem.getCategory().getValue().equals(COMPLETELY_UNREACHABLE_CREATION_REQUEST) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();
            OffsetDateTime analogFailureDate = fullSentNotification.getTimeline().stream().filter(elem -> elem.getCategory().getValue().equals(ANALOG_FAILURE_WORKFLOW) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();
            OffsetDateTime sendAnalogProgressTimestampDate = fullSentNotification.getTimeline().stream().filter(elem -> elem.getCategory().getValue().equals(SEND_ANALOG_PROGRESS) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();
            OffsetDateTime sendAnalogProgressNotificationDate = fullSentNotification.getTimeline().stream().filter(elem -> elem.getCategory().getValue().equals(SEND_ANALOG_PROGRESS) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getDetails().getNotificationDate();
            OffsetDateTime sendFeedbackTimestampDate = fullSentNotification.getTimeline().stream().filter(elem -> elem.getCategory().getValue().equals(SEND_ANALOG_FEEDBACK) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();
            OffsetDateTime sendFeedbackNotificationDate = fullSentNotification.getTimeline().stream().filter(elem -> elem.getCategory().getValue().equals(SEND_ANALOG_FEEDBACK) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getDetails().getNotificationDate();
            OffsetDateTime prepareAnalogDomicileFailureTimestamp = fullSentNotification.getTimeline().stream().filter(elem -> elem.getCategory().getValue().equals(PREPARE_ANALOG_DOMICILE_FAILURE) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();

            log.info("DESTINATARIO : {}", destinatario);
            log.info("sendAnalogProgressTimestampDate : {}", sendAnalogProgressTimestampDate);
            log.info("sendFeedbackTimestampDate : {} ", sendFeedbackTimestampDate);
            log.info("analogFailureDate Timestamp : {}", analogFailureDate);
            log.info("completelyUnreachableRequestDate Timestamp : {}", completelyUnreachableRequestDate);
            log.info("completelyUnreachableDate Timestamp : {}", completelyUnreachableDate);
            log.info("prepareAnalogDomicileFailureTimestamp : {}", prepareAnalogDomicileFailureTimestamp);

            log.info("schedulingDate Timestamp: {}", schedulingDate);

            log.info("sendAnalogProgressNotificationDate : {}", sendAnalogProgressNotificationDate);
            log.info("sendFeedbackNotificationDate : {}", sendFeedbackNotificationDate);

            assertThat(completelyUnreachableDate)
                    .isCloseTo(schedulingDate, within(1, SECONDS));
            assertThat(completelyUnreachableRequestDate)
                    .isCloseTo(schedulingDate, within(1, SECONDS));
            assertThat(analogFailureDate)
                    .isCloseTo(schedulingDate, within(1, SECONDS));
            assertThat(prepareAnalogDomicileFailureTimestamp)
                    .isCloseTo(schedulingDate, within(1, SECONDS));
            assertThat(sendAnalogProgressNotificationDate)
                    .isCloseTo(sendAnalogProgressTimestampDate, within(1, SECONDS));
            assertThat(sendFeedbackNotificationDate)
                    .isCloseTo(sendFeedbackTimestampDate, within(1, SECONDS));
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @Then("verifica date business in timeline ANALOG_SUCCESS_WORKFLOW per l'utente {int} al tentativo {int}")
    public void verificationDateScheduleRefinementWithSendAnalogFeedback(Integer destinatario, Integer tentativo) {
        try {
            FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
            OffsetDateTime schedulingDate = fullSentNotification.getTimeline().stream().filter(elem -> elem.getCategory().getValue().equals(SCHEDULE_REFINEMENT) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();
            OffsetDateTime sendAnalogProgressNotificationDate = fullSentNotification.getTimeline().stream().filter(elem -> elem.getCategory().getValue().equals(SEND_ANALOG_PROGRESS) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getDetails().getNotificationDate();
            OffsetDateTime sendAnalogProgressTimestampDate = fullSentNotification.getTimeline().stream().filter(elem -> elem.getCategory().getValue().equals(SEND_ANALOG_PROGRESS) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();
            OffsetDateTime sendFeedbackTimestampDate = fullSentNotification.getTimeline().stream().filter(elem -> elem.getCategory().getValue().equals(SEND_ANALOG_FEEDBACK) && elem.getDetails().getRecIndex().equals(destinatario) && elem.getDetails().getSentAttemptMade().equals(tentativo)).findAny().get().getTimestamp();
            OffsetDateTime sendFeedbackNotificationDate = fullSentNotification.getTimeline().stream().filter(elem -> elem.getCategory().getValue().equals(SEND_ANALOG_FEEDBACK) && elem.getDetails().getRecIndex().equals(destinatario) && elem.getDetails().getSentAttemptMade().equals(tentativo)).findAny().get().getDetails().getNotificationDate();
            OffsetDateTime analogSuccessDate = fullSentNotification.getTimeline().stream().filter(elem -> elem.getCategory().getValue().equals(ANALOG_SUCCESS_WORKFLOW) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();

            log.info("DESTINATARIO : {}", destinatario);
            log.info("sendAnalogProgressTimestampDate: {}", sendAnalogProgressTimestampDate);
            log.info("sendFeedbackTimestampDate: {}", sendFeedbackTimestampDate);
            log.info("analogSuccessDate Timestamp: {}", analogSuccessDate);
            log.info("schedulingDate Timestamp: {}", schedulingDate);


            log.info("sendFeedbackNotificationDate : {}", sendFeedbackNotificationDate);
            log.info("sendAnalogProgressNotificationDate: {}", sendAnalogProgressNotificationDate);

            assertThat(analogSuccessDate)
                    .isCloseTo(schedulingDate, within(1, SECONDS));
            assertThat(sendFeedbackTimestampDate)
                    .isCloseTo(schedulingDate, within(1, SECONDS));
            assertThat(sendAnalogProgressNotificationDate)
                    .isCloseTo(sendAnalogProgressTimestampDate, within(1, SECONDS));
            assertThat(sendFeedbackNotificationDate)
                    .isCloseTo(sendFeedbackTimestampDate, within(1, SECONDS));
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }


    @Then("vengono letti gli eventi della timeline e si controlla che l'evento di timeline {string} non esista con la versione {string}")
    public void readingEventsOfTimelineElementOfNotificationV1(String timelineEventCategory, String version) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface(sharedSteps.getNotificationVersion(version));
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_SLOW, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, false, null, null);
    }

    @Then("viene controllato che l'elemento di timeline della notifica {string} non esiste dopo il rifiuto della notifica stessa")
    public void readingNotEventUpToTheTimelineElementOfNotificationRefused(String timelineEventCategory) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, false, null, null);
    }

    @Then("viene controllato che l'elemento di timeline della notifica {string} non esiste")
    public void readingNotEventUpToTheTimelineElementOfNotification(String timelineEventCategory) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, false, null, null);
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} e successivamente annullata")
    public void readingEventUpToTheTimelineElementOfNotificationAndCancel(String timelineEventCategory) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, null, null);
        try {
            Assertions.assertDoesNotThrow(() -> b2bClient.notificationCancellation(sharedSteps.getNotificationIun()));
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con deliveryDetailCode {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCode(String timelineEventCategory, String deliveryDetailCode) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .deliveryDetailCode(deliveryDetailCode)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, null, null);
    }


    @Then("viene verificato che lato utente l'elemento di timeline {string} con deliveryDetailCode {string} non esista")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCodeWithoutSuccess(String timelineEventCategory, String deliveryDetailCode) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .deliveryDetailCode(deliveryDetailCode)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, false, null, null);
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con deliveryDetailCode {string} tentativo {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCode(String timelineEventCategory, String deliveryDetailCode, String attempt) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .deliveryDetailCode(deliveryDetailCode)
                .withElementIdSuffix(attempt)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_SLOW, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, null, null);
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con deliveryDetailCode {string} e verifica data delay più {int}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCode(String timelineEventCategory, String deliveryDetailCode, int delay) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .deliveryDetailCode(deliveryDetailCode)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder()
                .delay(delay)
                .build();
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, CHECK_NOTIFICATION_DATE_DELAY, checkFilters);
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} e verifica data schedulingDate più {int}{string} per il destinatario {int}")
    public void readingEventUpToTheTimelineElementOfNotificationWithVerifySchedulingDate(String timelineEventCategory, int delay, String tipoIncremento, int recipientIndex) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .recipientIndex(recipientIndex)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder()
                .delay(delay)
                .tipoIncremento(tipoIncremento)
                .build();
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, CHECK_SCHEDULING_DATE_DELAY, checkFilters);
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con deliveryDetailCode {string} e verifica tipo DOC {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCodeVerifyTypeDoc(String timelineEventCategory, String deliveryDetailCode, String documentType) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .deliveryDetailCode(deliveryDetailCode)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder()
                .withAttempt(true)
                .documentType(documentType)
                .build();
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, CHECK_ATTACHMENTS, checkFilters);
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con deliveryDetailCode {string} e verifica tipo DOC {string} tentativo {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCodeVerifyTypeDoc(String timelineEventCategory, String deliveryDetailCode, String documentType, String attempt) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .deliveryDetailCode(deliveryDetailCode)
                .withElementIdSuffix(attempt)
                .documentType(documentType)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder()
                .documentType(documentType)
                .build();
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, CHECK_ATTACHMENTS, checkFilters);
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con deliveryDetailCode {string} e deliveryFailureCause {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCodeDeliveryFailureCause(String timelineEventCategory, String deliveryDetailCode, String deliveryFailureCause) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .deliveryDetailCode(deliveryDetailCode)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder()
                .deliveryFailureCause(deliveryFailureCause)
                .build();
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, CHECK_DELIVERY_FAILURE_CAUSE, checkFilters);
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con deliveryDetailCode {string} e deliveryFailureCause {string} tentativo {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCodeDeliveryFailureCause(String timelineEventCategory, String deliveryDetailCode, String deliveryFailureCause, String attempt) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .deliveryDetailCode(deliveryDetailCode)
                .withElementIdSuffix(attempt)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder()
                .deliveryFailureCause(deliveryFailureCause)
                .build();
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, CHECK_DELIVERY_FAILURE_CAUSE, checkFilters);
    }

    @And("viene verificato il campo sendRequestId dell' evento di timeline {string}")
    public void vieneVerificatoCampoSendRequestIdEventoTimeline(String timelineEventCategory) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, CHECK_SEND_REQUEST_ID, null);
    }

    @And("viene verificato il campo serviceLevel dell' evento di timeline {string} sia valorizzato con {string}")
    public void vieneVerificatoCampoServiceLevelEventoTimeline(String timelineEventCategory, String serviceLevelValue) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder()
                .serviceLevel(serviceLevelValue)
                .build();
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, CHECK_SERVICE_LEVEL, checkFilters);
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} per l'utente {int}")
    public void readingEventUpToTheTimelineElementOfNotificationPerUtente(String timelineEventCategory, Integer recipientIndex) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .recipientIndex(recipientIndex)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_SLOW, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, null, null);
    }

    //TODO: FA LA STESSA IDENTICA COSA DEL METODO DI SOPRA, SOLO CON LA FRASE DELLO STEP ESPOSTA IN MANIERA DIVERSA E CON LA POLLING STRATEGY RAPID ANZICHE' SLOW
    @Then("esiste l'elemento di timeline della notifica {string} per l'utente {int}")
    public void verifyEventUpToTheTimelineElementOfNotificationPerUtente(String timelineEventCategory, Integer recipientIndex) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .recipientIndex(recipientIndex)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, null, null);
    }

    //TODO: UGUALE A METODO SOPRA, MA SCENARIO NEGATIVO, CAMBIA L'ASSERT
    @Then("non vengono letti gli eventi fino all'elemento di timeline della notifica {string} per l'utente {int}")
    public void notReadingEventUpToTheTimelineElementOfNotificationPerUtente(String timelineEventCategory, Integer recipientIndex) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .recipientIndex(recipientIndex)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, false, null, null);
    }

    //TODO: IDENTICO AL METODO SOPRA, SOLO CON LA FRASE DELLO STEP ESPOSTA IN MANIERA DIVERSA
    @Then("vengono letti gli eventi e verifico che l'utente {int} non abbia associato un evento {string}")
    public void vengonoLettiGliEventiVerificoCheUtenteNonAbbiaAssociatoEvento(Integer recipientIndex, String timelineEventCategory) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .recipientIndex(recipientIndex)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, false, null, null);
    }

    //TODO: STESSA IDENTICA ASSERTION DEL METODO SOPRA, CAMBIA SOLO IL PREDICATE CHE HA RESPONSE STATUS IN PIU'
    @Then("vengono letti gli eventi e verifico che l'utente {int} non abbia associato un evento {string} con responseStatus {string}")
    public void vengonoLettiGliEventiVerificoCheUtenteNonAbbiaAssociatoEventoWithResponseStatus(Integer recipientIndex, String timelineEventCategory, String responseStatus) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .recipientIndex(recipientIndex)
                .responseStatus(responseStatus)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, false, null, null);
    }

    @Then("viene verificato che nell'elemento di timeline della notifica {string} sia presente il campo Digital Address da National Registry per l utente {int}")
    public void vieneVerificatoCheNellElementoDiTimelineDellaNotificaSiaPresenteIlCampoDigitalAddressDaNationalRegistryPerLUtente(String timelineEventCategory, Integer recipientIndex) {
        readingEventUpToTheTimelineElementOfNotificationPerUtente(timelineEventCategory, recipientIndex);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder().build();
        getB2bStepsInterface().performFurtherChecks(CHECK_DIGITAL_ADDRESS, checkFilters);
    }

    @Then("viene verificato che nell'elemento di timeline della notifica {string} sia presente il campo Digital Address da National Registry")
    public void vieneVerificatoCheElementoTimelineSianoConfiguratoCampoDigitalAddressNationalRegistry(String timelineEventCategory) {
        vieneVerificatoCheNellElementoDiTimelineDellaNotificaSiaPresenteIlCampoDigitalAddressDaNationalRegistryPerLUtente(timelineEventCategory, null);
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} verifica numero pagine AAR {int}")
    public void readingEventUpToTheTimelineElementOfNotificationPerVerificaNumPagine(String timelineEventCategory, Integer numPagine) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder()
                .numberOfPagesAAR(numPagine)
                .build();
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, CHECK_NUMBER_OF_PAGES_AAR, checkFilters);
    }

    @Then("vengono letti gli eventi e verifico che l'utente {int} non abbia associato un evento {string} V1")
    public void vengonoLettiGliEventiVerificoCheUtenteNonAbbiaAssociatoEventoV1(Integer recipientIndex, String timelineEventCategory) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .recipientIndex(recipientIndex)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface(NotificationVersion.V1);
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, false, null, null);
    }

    @Then("verifica generazione Atto opponibile senza la messa a disposizione in DIGITAL_DELIVERY_CREATION_REQUEST")
    public void paVerifyGenerazioneLegalFact() {
        TimelineElementV28 timelineElement = null;
        for (TimelineElementV28 element : sharedSteps.getSentNotificationLastVersion().getTimeline()) {
            if (element.getCategory().getValue().equals(DIGITAL_DELIVERY_CREATION_REQUEST)) {
                timelineElement = element;
                break;
            }
        }
        try {
            log.info("TIMELINE ELEMENT : {}", timelineElement);
            assertNotNull(timelineElement.getLegalFactsIds());
            Assertions.assertTrue(CollectionUtils.isEmpty(timelineElement.getLegalFactsIds()));
            assertNotNull(timelineElement.getDetails().getLegalFactId());

        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @Then("vengono verificati costo = {string} e data di perfezionamento della notifica")
    public void notificationPriceAndDateVerification(String price) {
        try {
            Thread.sleep(sharedSteps.getWait() * 2);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        priceVerification(price, 0);
    }

    @Then("vengono verificati costo = {string} e data di perfezionamento della notifica {string}")
    public void notificationPriceAndDateVerificationV1(String price, String versione) {
        try {
            Thread.sleep(sharedSteps.getWait() * 2);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        priceVerification(price, 0);
    }

    @Then("vengono verificati costo = {string} e data di perfezionamento della notifica V2")
    public void notificationPriceAndDateVerificationV2(String price) {
        try {
            Thread.sleep(sharedSteps.getWait() * 2);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        priceVerification(price, 0);
    }

    @Then("viene verificato il costo = {string} della notifica")
    public void notificationPriceVerification(String price) {
        try {
            Thread.sleep(sharedSteps.getWait() * 2);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        priceVerification(price, 0);
    }

    @And("viene verificato il costo = {string} della notifica con un errore {string}")
    public void attachmentRetrievedError(String price, String errorCode) {
        try {
            Thread.sleep(sharedSteps.getWait() * 2);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        try {
            priceVerification(price, 0);
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
        }
        Assertions.assertTrue(this.notificationError != null && this.notificationError.getStatusCode().toString().substring(0, 3).equals(errorCode));
    }

    @Then("viene verificato il costo = {string} della notifica per l'utente {int}")
    public void notificationPriceVerificationPerDestinatario(String price, Integer recipientIndex) {
        try {
            Thread.sleep(sharedSteps.getWait() * 2);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        priceVerification(price, recipientIndex);
    }

    private void priceVerification(String price, Integer recipientIndex) {
        getB2bStepsInterface().checkPriceForRecipient(recipientIndex, price);
    }

    @Then("viene calcolato il costo = {string} della notifica per l'utente {int}")
    public void notificationPriceProcessPerDestinatario(String price, Integer destinatario) {
        try {
            Thread.sleep(sharedSteps.getWait() * 2);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        priceVerificationProcessCost(price, null, destinatario);
    }

    private void priceVerificationProcessCost(String price, String date, Integer destinatario) {
        FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        NotificationProcessCostResponse notificationProcessCost = this.b2bClient.getNotificationProcessCost(
                sharedSteps.getNotificationIun(),
                destinatario,
                fullSentNotification.getNotificationFeePolicy().getValue(),
                fullSentNotification.getRecipients().get(destinatario).getPayments().get(0).getF24().getApplyCost(),
                fullSentNotification.getPaFee(),
                fullSentNotification.getVat());
        try {
            if (price != null) {
                log.info("Costo notifica: {} destinatario: {}", notificationProcessCost.getAnalogCost(), destinatario);
                assertEquals(notificationProcessCost.getAnalogCost(), Integer.parseInt(price));
            }
            if (date != null) {
                assertNotNull(notificationProcessCost.getRefinementDate());
            }
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);

        }
    }

    @And("{string} tenta di leggere la notifica ricevuta")
    public void userReadReceivedNotificationWithError(String recipient) {
        sharedSteps.selectUser(recipient);
        String iun = sharedSteps.getNotificationIun();
        try {
            sharedSteps.getWebRecipientClient().getFullReceivedNotification(iun, null);
        } catch (HttpStatusCodeException e) {
            sharedSteps.setNotificationError(e);
        }
    }

    @And("{string} legge la notifica ricevuta")
    public void userReadReceivedNotification(String recipient) {
        sharedSteps.selectUser(recipient);
        String iun = sharedSteps.getNotificationIun();
        Assertions.assertDoesNotThrow(() -> {
            sharedSteps.getWebRecipientClient().getFullReceivedNotification(iun, null);
        });
        try {
            Thread.sleep(sharedSteps.getWorkFlowWait());
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }
    }

    @And("{string} legge la notifica ricevuta {string}")
    public void userReadReceivedNotificationVersioning(String recipient, String versione) {
        sharedSteps.selectUser(recipient);
        String iun = sharedSteps.getNotificationIun();
        try {
            sharedSteps.getWebRecipientClient().getFullReceivedNotification(iun, null);
//            if (versione.equalsIgnoreCase("V1")) {
//                sharedSteps.getWebRecipientClient().getReceivedNotificationV1(iun, null);
//            } else {
//                sharedSteps.getWebRecipientClient().getReceivedNotificationV2(iun, null);
//            }
            try {
                Thread.sleep(sharedSteps.getWorkFlowWait());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
        } catch (HttpStatusCodeException e) {
            sharedSteps.setNotificationError(e);
        }
    }

    @And("l'avviso pagopa viene pagato correttamente")
    public void laNotificaVienePagata() {
        laNotificaVienePagataMulti(0);
    }

    @And("l'avviso pagopa viene pagato correttamente dall'utente {int}")
    public void laNotificaVienePagataMulti(Integer recipientIndex) {
        getB2bStepsInterface().payAvvisoPagoPa(recipientIndex, 0);
    }

    //UTILIZZA LO STESSO METODO DI SOPRA, MA DAL TESTO SI ATTENDE UN ESITO DIVERSO. STEP CHE NON VIENE RICHIAMATO IN ALCUN PUNTO
    @And("viene rifiutato il pagamento dell'avviso pagopa dall'utente {int}")
    public void laNotificaVieneRifiutatoIlPagamentoMulti(Integer recipientIndex) {
        getB2bStepsInterface().payAvvisoPagoPa(recipientIndex, 0);
    }

    @And("l'avviso pagopa viene pagato correttamente dall'utente {int} V1")
    public void laNotificaVienePagataMultiV1(Integer recipientIndex) {
        NotificationVersion notificationVersion = NotificationVersion.V1;
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface(notificationVersion);
        b2bStepsInterface.payAvvisoPagoPa(recipientIndex, 0);
    }

    @And("l'avviso pagopa viene pagato correttamente dall'utente {int} V2")
    public void laNotificaVienePagataMultiV2(Integer recipientIndex) {
        NotificationVersion notificationVersion = NotificationVersion.V2;
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface(notificationVersion);
        b2bStepsInterface.payAvvisoPagoPa(recipientIndex, 0);
    }

    @And("l'avviso pagopa {int} viene pagato correttamente dall'utente {int}")
    public void laNotificaVienePagataConAvvisoNumMulti(Integer paymentIndex, Integer recipientIndex) {
        getB2bStepsInterface().payAvvisoPagoPa(recipientIndex, paymentIndex);
        ;
    }

    @And("gli avvisi PagoPa vengono pagati correttamente dal destinatario {int}")
    public void laNotificaVienePagataConAvvisoNumMultiPagoPa(Integer recipientIndex) {
        getB2bStepsInterface().payAvvisoPagoPa(recipientIndex, null);
    }

    @Then("sono presenti {int} attestazioni opponibili RECIPIENT_ACCESS")
    public void sonoPresentiAttestazioniOpponibili(int number) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, NOTIFICATION_VIEWED, filters);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder()
                .numberOfAttestazioniOpponibili(number)
                .build();
        b2bStepsInterface.checkIfTimelineElementExists(NOTIFICATION_VIEWED, true, CHECK_ATTESTAZIONI_OPPONIBILI, checkFilters);
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con responseStatus {string} per l'utente {int}")
    public void vengonoLettiGliEventiFinoAllElementoDiTimelineDellaNotificaConResponseStatusPerUtente(String timelineEventCategory,
                                                                                                      String responseStatus, Integer recipientIndex) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .recipientIndex(recipientIndex)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder()
                .responseStatus(responseStatus)
                .build();
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, CHECK_RESPONSE_STATUS, checkFilters);
    }

    //IDENTICO AL METODO SOPRA, TRANNE CHE PER L'ASSENZA DEL RECIPIENT INDEX NEL PN-POLLING PREDICATE
    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con responseStatus {string}")
    public void vengonoLettiGliEventiFinoAllElementoDiTimelineDellaNotificaConResponseStatus(String timelineEventCategory, String responseStatus) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder()
                .responseStatus(responseStatus)
                .build();
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, CHECK_RESPONSE_STATUS, checkFilters);
    }

    @And("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con responseStatus {string} al tentativo {string}")
    public void vengonoLettiGliEventiFinoAllElementoDiTimelineDellaNotificaConResponseStatusEalTentativo(String timelineEventCategory, String responseStatus, String attempt) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .withElementIdSuffix(attempt)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder()
                .responseStatus(responseStatus)
                .build();
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, CHECK_RESPONSE_STATUS, checkFilters);
    }


    @Then("viene verificato che nell'elemento di timeline della notifica {string} siano configurati i campi municipalityDetails e foreignState")
    public void vieneVerificatoCheElementoTimelineSianoConfiguratiCampiMunicipalityDetailsForeignState(String timelineEventCategory) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, CHECK_MUNICIPALITY_AND_FOREIGN_STATE, null);
    }

    @Then("viene verificato che nell'elemento di timeline della notifica {string} con responseStatus {string} sia presente il campo deliveryDetailCode")
    public void vieneVerificatoCheElementoTimelineSianoConfiguratoCampoDeliveryDetailCode(String timelineEventCategory, String responseStatus) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder()
                .responseStatus(responseStatus)
                .withDeliveryDetailCode(true)
                .build();
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, CHECK_RESPONSE_STATUS, checkFilters);
    }

    @Then("viene verificato che nell'elemento di timeline della notifica {string} con responseStatus {string} sia presente i campi deliveryDetailCode e deliveryFailureCause")
    public void vieneVerificatoCheElementoTimelineSianoConfiguratoCampoDeliveryDetailCodeDeliveryFailureCause(String timelineEventCategory, String responseStatus) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder()
                .responseStatus(responseStatus)
                .withDeliveryDetailCode(true)
                .withDeliveryFailureCause(true)
                .build();
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, CHECK_RESPONSE_STATUS, checkFilters);
    }

    @Then("si attende la corretta sospensione dell'invio cartaceo")
    public void siAttendeLaCorrettaSospensioneDellInvioCartaceo() {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, ANALOG_SUCCESS_WORKFLOW, filters);
        b2bStepsInterface.checkIfTimelineElementExists(ANALOG_SUCCESS_WORKFLOW, false, null, null);
    }

    @Then("si attende il corretto pagamento della notifica")
    public void siAttendeIlCorrettoPagamentoDellaNotifica() {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, PAYMENT, filters);
        b2bStepsInterface.checkIfTimelineElementExists(PAYMENT, true, null, null);
    }

    @Then("si attende il corretto pagamento della notifica V1")
    public void siAttendeIlCorrettoPagamentoDellaNotificaV1() {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface(NotificationVersion.V1);
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, PAYMENT, filters);
        b2bStepsInterface.checkIfTimelineElementExists(PAYMENT, true, null, null);
    }

    @Then("si attende il corretto pagamento della notifica V2")
    public void siAttendeIlCorrettoPagamentoDellaNotificaV2() {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface(NotificationVersion.V2);
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, PAYMENT, filters);
        b2bStepsInterface.checkIfTimelineElementExists(PAYMENT, true, null, null);
    }

    @Then("si attende il corretto pagamento della notifica con l' avviso {int} dal destinatario {int}")
    public void siAttendeIlCorrettoPagamentoDellaNotificaConAvvisoDalDestinatario(Integer avviso, Integer recipientIndex) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, PAYMENT, filters);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder()
                .recipientIndex(recipientIndex)
                .build();
        b2bStepsInterface.checkIfTimelineElementExists(PAYMENT, true, CHECK_PAYMENT_FROM_RECIPIENT_INDEX, checkFilters);
    }

    @Then("si attende il non corretto pagamento della notifica con l' avviso {int} dal destinatario {int}")
    public void siAttendeIlNonCorrettoPagamentoDellaNotificaConAvvisoDalDestinatario(Integer avviso, Integer recipientIndex) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, PAYMENT, filters);
        b2bStepsInterface.checkIfTimelineElementExists(PAYMENT, false, null, null);
    }

    @Then("si attende il corretto pagamento della notifica dell'utente {int}")
    public void siAttendeIlCorrettoPagamentoDellaNotifica(Integer recipientIndex) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .recipientIndex(recipientIndex)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, PAYMENT, filters);
        b2bStepsInterface.checkIfTimelineElementExists(PAYMENT, true, null, null);
    }

    @Then("verifica presenza in Timeline dei solo pagamenti di avvisi PagoPA del destinatario {int}")
    public void verificaPresenzaPagamentiSoloPagopa(Integer recipientIndex) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .recipientIndex(recipientIndex)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, PAYMENT, filters);
        b2bStepsInterface.checkIfTimelineElementExists(PAYMENT, true, CHECK_ONLY_PAYMENTS_PAGOPA, null);
    }

    //AL MOMENTO NON ESISTE UNO SCENARIO CHE INTEGRA QUESTO STEP
    @Then("verifica non presenza in Timeline di pagamenti con avvisi F24 del destinatario {int}")
    public void verificaNonPresenzaPagamentiF24(Integer recipientIndex) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .recipientIndex(recipientIndex)
                .isF24(true)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        try {
            b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, PAYMENT, filters);
            b2bStepsInterface.verificaAssenzaPagamentiF24();
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    //AL MOMENTO NON ESISTE UNO SCENARIO CHE INTEGRA QUESTO STEP
    @Then("si attende il non corretto pagamento della notifica dell'utente {int}")
    public void siAttendeIlNonCorrettoPagamentoDellaNotifica(Integer recipientIndex) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .recipientIndex(recipientIndex)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, PAYMENT, filters);
        b2bStepsInterface.checkIfTimelineElementExists(PAYMENT, false, null, null);
    }

    @Then("viene verificato che nell'elemento di timeline della notifica {string} e' presente il campo Digital Address di piattaforma")
    public void vieneVerificatoCheElementoTimelineSianoConfiguratoCampoDigitalAddressPiattaforma(String timelineEventCategory) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .withElementIdSuffix("SOURCE_PLATFORM")
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder()
                .withPlatformAddress(true)
                .platformAddress("DSRDNI00A01A225I@pec.pagopa.it")//TODO: mettere dentro Costanti ???
                .build();
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, CHECK_DIGITAL_ADDRESS, checkFilters);
    }

    @And("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con deliveryDetailCode {string} per l'utente {int}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCodeAndDestinatario(String timelineEventCategory, String deliveryDetailCode, int recipientIndex) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .recipientIndex(recipientIndex)
                .deliveryDetailCode(deliveryDetailCode)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, null, null);
    }

    //AL MOMENTO LO SCENARIO CHE INTEGRA QUESTO STEP E' @IGNORE
    @Then("viene verificato che nell'elemento di timeline della notifica {string} sia presente il campo Digital Address")
    public void vieneVerificatoCheElementoTimelineSianoConfiguratoCampoDigitalAddress(String timelineEventCategory) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .withElementIdSuffix("SOURCE_PLATFORM")
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder().build();
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, CHECK_DIGITAL_ADDRESS, checkFilters);
    }

    @Then("viene verificato che l'ultimo tentativo effettuato abbia indice {int}")
    public void vieneVerificatoCheUltimoTentativoEffettuatoAbbiaIndice(Integer index) {
        getB2bStepsInterface().checkIfLastAttemptMatchesIndex(index);
    }

    @Then("viene verificato che l'elemento di timeline {string} {exists}")
    public void checkIfTimelineElementExists(String timelineEventCategory, boolean exists, Map<String, String> data) {
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.checkIfTimelineElementExistsFromData(exists, timelineEventCategory, data);
    }

    /**
     * Checks that a certain timeline element has a field with a text value compatible with the specified regular expression.
     *
     * @param timelineEventCategory the category of the timeline element, e.g. "SEND_ANALOG_PROGRESS"
     * @param eventId               the event id of the timeline element, e.g. "CON020"
     * @param fieldPath             the field path of the timeline element object. Each nested field is separated
     *                              by an underscore, e.g. "details_deliveryDetailCode".
     *                              If a field is a sequence of element - like a List - the index of the element must be
     *                              specified with square brackets, e.g. "details_attachments[0]_url"
     * @param regex                 the regular expression that the field value must match
     */
    @And("viene verificato che l'elemento di timeline {string} con evento {string} abbia un valore per il campo {string} compatibile con l'espressione regolare {string}")
    public void vieneVerificatoCheElementoTimelineAbbiaUnValoreDiCampoCompatibileConRegex(String timelineEventCategory, String eventId, String fieldPath, String regex) {
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.searchCustomTimelineElementInTimeline(eventId, timelineEventCategory);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder()
                .fieldPath(fieldPath)
                .fieldRegex(regex)
                .build();
        b2bStepsInterface.performFurtherChecks(CHECK_FIELD_MATCHES_REGEX, checkFilters);
    }

    /**
     * Very similar to {@link #vieneVerificatoCheElementoTimelineAbbiaUnValoreDiCampoCompatibileConRegex(String, String, String, String)},
     * but it uses the last timeline element loaded.
     *
     * @param fieldPath the field path of the timeline element object. Each nested field is separated
     *                  by an underscore, e.g. "details_deliveryDetailCode".
     *                  If a field is a sequence of element - like a List - the index of the element must be
     *                  specified with square brackets, e.g. "details_attachments[0]_url"
     * @param regex     the regular expression that the field value must match
     */
    @And("abbia anche un valore per il campo {string} compatibile con l'espressione regolare {string}")
    public void vieneVerificatoCheElementoTimelineAbbiaUnValoreDiCampoCompatibileConRegex(String fieldPath, String regex) {
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder()
                .fieldPath(fieldPath)
                .fieldRegex(regex)
                .build();
        getB2bStepsInterface().performFurtherChecks(CHECK_FIELD_MATCHES_REGEX, checkFilters);
    }

    @Then("viene verificato che la data della timeline REFINEMENT sia ricezione della raccomandata + 10gg")
    public void verificationDateScheduleRefinementWithRefinementPlus10Days() {
        try {
            FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
            OffsetDateTime scheduleDate = fullSentNotification.getTimeline().stream().filter(elem -> elem.getCategory().getValue().equals(SEND_ANALOG_FEEDBACK)).findAny().get().getTimestamp().plus(sharedSteps.getSchedulingDaysSuccessAnalogRefinement());
            OffsetDateTime refinementDate = fullSentNotification.getTimeline().stream().filter(elem -> elem.getCategory().getValue().equals(REFINEMENT)).findAny().get().getTimestamp();
            log.info("scheduleDate : {}", scheduleDate);
            log.info("refinementDate : {}", refinementDate);
            assertThat(scheduleDate).as("La data di refinement non coincide con quanto atteso").isEqualTo(refinementDate);
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @And("viene schedulato il perfezionamento per decorrenza termini per il caso {string}")
    public void vieneSchedulatoIlPerfezionamento(String timelineEventCategory, Map<String, String> dataMap) {
        try {
            getB2bStepsInterface().vieneSchedulatoIlPerfezionamento(timelineEventCategory, dataMap);
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @And("si attende che sia presente il perfezionamento per decorrenza termini")
    public void siAttendePresenzaPerfezionamentoDecorrenzaTermini(Map<String, String> dataMap) throws InterruptedException {
        try {
            getB2bStepsInterface().waitForScheduleRefinement(dataMap);
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @And("si attende che si ritenti l'invio dopo l'evento {string}")
    public void siAttendeCheSiRitentiInvio(String timelineEventCategory, Map<String, String> dataMap) throws InterruptedException {
        try {
            getB2bStepsInterface().waitForSecondAttempt(timelineEventCategory, dataMap);
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @And("viene verificato che il destinatario {string} di tipo {string} sia nella tabella pn-paper-notification-failed")
    public void vieneVerificatoDestinatarioInPnPaperNotificationFailed(String taxId, String recipientTye) {
        // get internal id from data-vault
        String internalId = externalClient.getInternalIdFromTaxId(recipientTye, taxId);
        // get notifications not delivered from delivery-push
        List<ResponsePaperNotificationFailedDto> notificationFailedList = this.pnPrivateDeliveryPushExternalClient.getPaperNotificationFailed(internalId, true);
        String iun = sharedSteps.getNotificationIun();
        ResponsePaperNotificationFailedDto notificationFailed = notificationFailedList.stream().filter(elem -> elem.getIun().equals(iun)).findFirst().orElse(null);
        assertNotNull(notificationFailed);
    }

    @And("viene verificato che il destinatario {string} di tipo {string} non sia nella tabella pn-paper-notification-failed")
    public void vieneVerificatoDestinatarioNonInPnPaperNotificationFailed(String taxId, String recipientTye) {
        // get internal id from data-vault
        String internalId = externalClient.getInternalIdFromTaxId(recipientTye, taxId);
        // get notifications not delivered from delivery-push
        List<ResponsePaperNotificationFailedDto> notificationFailedList = this.pnPrivateDeliveryPushExternalClient.getPaperNotificationFailed(internalId, true);
        String iun = sharedSteps.getNotificationIun();
        ResponsePaperNotificationFailedDto notificationFailed = notificationFailedList.stream().filter(elem -> elem.getIun().equals(iun)).findFirst().orElse(null);
        Assertions.assertNull(notificationFailed);
    }

    //AL MOMENTO NON ESISTE UNO SCENARIO CHE INTEGRA QUESTO STEP
    @And("vengono letti gli eventi fino all'elemento di timeline {string} della notifica per il destinatario {int}, con deliveryDetailCode {string}, legalFactId con category {string} e documentType {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCodeAndLegalFactIdCategoryAndDocumentType(String timelineEventCategory, Integer recipientIndex, String deliveryDetailCode, String legalFactIdCategory, String documentType) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .recipientIndex(recipientIndex)
                .deliveryDetailCode(deliveryDetailCode)
                .legalFactIdCategory(legalFactIdCategory)
                .documentType(documentType)
                .isLegalFactEmpty(true)
                .isAttachmentEmpty(true)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder()
                .physicalAddressRegex(PHYSICAL_ADDRESS_REGEX)
                .build();
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, CHECK_PHYSICAL_ADDRESS, checkFilters);
    }

    //AL MOMENTO NON ESISTE UNO SCENARIO CHE INTEGRA QUESTO STEP
    @And("vengono letti gli eventi fino all'elemento di timeline {string} della notifica per il destinatario {int}, con deliveryDetailCode {string} e con deliveryFailureCause {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCodeAndDeliveryFailureCause(String timelineEventCategory, Integer recipientIndex, String deliveryDetailCode, String deliveryFailureCause) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .recipientIndex(recipientIndex)
                .deliveryDetailCode(deliveryDetailCode)
                .failureCauses(Arrays.asList(deliveryFailureCause.split(" ")))
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, null, null);
    }

    //AL MOMENTO NON ESISTE UNO SCENARIO CHE INTEGRA QUESTO STEP
    @Then("vengono letti gli eventi fino all'elemento di timeline {string} della notifica per il destinatario {int} con deliveryDetailCode {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithRecIndexAndDeliveryDetailCode(String timelineEventCategory, Integer recipientIndex, String deliveryDetailCode) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .recipientIndex(recipientIndex)
                .deliveryDetailCode(deliveryDetailCode)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, null, null);
    }

    /**
     * Verifica che il timestamp di un evento sia (uguale|successivo|precedente) a quello di un secondo evento.
     * Da usare solo dopo aver appurato la presenza di tali eventi, con TimelineElementCategory che compaiono una sola volta
     * (e in scenari che non prevedono multi-destinatario o invii multipli, che potrebbero portare alla NON univocità della category)
     */
    @And("il timestamp dell'evento {string} è {isSuccessivo} a quello dell'evento {string}")
    public void checkOrdineEventiUnivoci(String category1, Boolean isSuccessivo, String category2) {
        try {
            getB2bStepsInterface().checkOrdineEventiUnivoci(category1, isSuccessivo, category2);
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} e verifica indirizzo secondo tentativo {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithVerifyPhysicalAddress(String timelineEventCategory, String attempt) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .deliveryDetailCode(null)
                .withElementIdSuffix(attempt)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_SLOW, TIMELINE, timelineEventCategory, filters);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder()
                .physicalAddressRegex(PHYSICAL_ADDRESS_REGEX)
                .build();
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, CHECK_PHYSICAL_ADDRESS, checkFilters);
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} al tentativo {string}")
    public void readingEventUpToTheTimelineElementOfNotificationAtAttempt(String timelineEventCategory, String attempt) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .deliveryDetailCode(null)
                .withElementIdSuffix(attempt)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_SLOW, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, null, null);
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} al tentativo {string} per l'utente {int}")
    public void readingEventUpToTheTimelineElementOfNotificationAtAttempt(String timelineEventCategory, String attempt, Integer recIndex) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .deliveryDetailCode(null)
                .recipientIndex(recIndex)
                .withElementIdSuffix(attempt)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_SLOW, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, null, null);
    }

    @Then("viene verificato che non esista l'elemento {string} al tentativo {string}")
    public void checkToTheTimelineForElementOfNotificationAtAttemptNotExist(String timelineEventCategory, String attempt) {
        Assertions.assertThrows(AssertionFailedError.class, () -> readingEventUpToTheTimelineElementOfNotificationAtAttempt(timelineEventCategory, attempt));
    }

    public String mapValueFromTable(DataTable table, String key) {
        String value = table.asMap().get(key);
        if (value.equalsIgnoreCase("null")) {
            return null;
        }
        if (value.equalsIgnoreCase("0_CHAR")) {
            return "";
        }
        return value;
    }


    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con failureCause {string}")
    public void vengonoLettiGliEventiFinoAllElementoDiTimelineDellaNotificaConFailureCause(String timelineEventCategory, String failureCause) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_SLOW, TIMELINE, timelineEventCategory, filters);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder()
                .failureCause(failureCause)
                .build();
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, CHECK_FAILURE_CAUSE, checkFilters);
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con failureCause {string} per l'utente {int}")
    public void vengonoLettiGliEventiFinoAllElementoDiTimelineDellaNotificaConFailureCausePerUtente(String timelineEventCategory, String failureCause, Integer recipientIndex) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .recipientIndex(recipientIndex)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_SLOW, TIMELINE, timelineEventCategory, filters);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder()
                .failureCause(failureCause)
                .build();
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, CHECK_FAILURE_CAUSE, checkFilters);
    }


    /*
    UTILE PER TEST

    @Given("viene vista la pec per l'utente {string}")
    public void vieneRimossaLaPecPerLUtente(String arg0) {
        webUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_1);
        List<LegalDigitalAddress> legalAddressByRecipient = webUserAttributesClient.getLegalAddressByRecipient();
        System.out.println(legalAddressByRecipient);
        webUserAttributesClient.deleteRecipientLegalAddress("default",LegalChannelType.PEC);
        webUserAttributesClient.postRecipientLegalAddress("default", LegalChannelType.PEC,
                (new AddressVerification().verificationCode("17947").value("test@fail.it")));
    }

    @Given("viene {string} l'app IO per {string}")
    public void vieneLAppIOPer(String onOff, String recipient) {
        webUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_2);

        //IoCourtesyDigitalAddressActivation ioCourtesyDigitalAddressActivation = new IoCourtesyDigitalAddressActivation();
        //ioCourtesyDigitalAddressActivation.setActivationStatus(onOff.equalsIgnoreCase("abilitata")?true:false);
        //ioUserAttributerExternaClient.setCourtesyAddressIo(selectTaxIdUser(recipient),ioCourtesyDigitalAddressActivation);
        System.out.println("STATUS IO: "+ioUserAttributerExternaClient.getCourtesyAddressIo(selectTaxIdUser(recipient)));
    }
    */

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} e verifica data schedulingDate per il destinatario {int} rispetto all'evento in timeline {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithVerifySchedulingDate(String timelineEventCategory, int recipientIndex, String evento) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .recipientIndex(recipientIndex)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder()
                .recipientIndex(recipientIndex)
                .otherEventCategory(evento)
                .build();
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, CHECK_SCHEDULING_DATE_RISPETTO_A_EVENTO, checkFilters);
    }

    @Then("viene verificato che nell'elemento di timeline della notifica {string} sia presente il campo notRefinedRecipientIndex")
    public void vieneVerificatoCheElementoTimelineSianoConfiguratoCampoNotRefinedRecipientIndex(String timelineEventCategory) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, CHECK_NOT_REFINED_RECIPIENT_INDEX, null);
    }

    @Then("viene verificato che il campo {string} sia valorizzato a {int}")
    public void notificationPriceVerificationValueResponse(String toValidate, Integer valueToValidate) {
        try {
            FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
            assertNotNull(fullSentNotification);

            switch (toValidate.toLowerCase()) {
                case "vat" -> assertEquals(valueToValidate, fullSentNotification.getVat());
                case "pafee" -> assertEquals(valueToValidate, fullSentNotification.getPaFee());
                default -> throw new IllegalArgumentException("Valore non valido per toValidate: " + toValidate);
            }
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @Then("viene verificato che tutti i campi per il calcolo del iva per il destinatario {int} siano valorizzati")
    public void notificationPriceVerificationResponse(Integer destinatario) {
        List<NotificationPaymentItem> listNotificationPaymentItem = sharedSteps.getSentNotificationLastVersion().getRecipients().get(destinatario).getPayments();

        for (NotificationPaymentItem pagamento : listNotificationPaymentItem) {
            NotificationPriceResponseV23 notificationPrice = this.b2bClient.getNotificationPriceV23(pagamento.getPagoPa().getCreditorTaxId(), pagamento.getPagoPa().getNoticeCode());
            try {
                assertNotNull(notificationPrice.getTotalPrice());
                assertNotNull(notificationPrice.getPartialPrice());
                assertNotNull(notificationPrice.getIun());
                assertNotNull(notificationPrice.getAnalogCost());
                assertNotNull(notificationPrice.getRefinementDate());
                assertNotNull(notificationPrice.getNotificationViewDate());
                assertNotNull(notificationPrice.getSendFee());
                assertNotNull(notificationPrice.getPaFee());
                assertNotNull(notificationPrice.getVat());
                log.info("notification price: {}", notificationPrice);
            } catch (AssertionFailedError assertionFailedError) {
                sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
            }
        }
    }

    @Then("viene verificato che per il calcolo del iva il campo vat sia di {int} e il campo paFee sia di {int} per il destinatario {int}")
    public void notificationPriceVerificationResponse(Integer vat, Integer paFee, Integer destinatario) {
        List<NotificationPaymentItem> listNotificationPaymentItem = sharedSteps.getSentNotificationLastVersion().getRecipients().get(destinatario).getPayments();
        for (NotificationPaymentItem pagamento : listNotificationPaymentItem) {
            NotificationPriceResponseV23 notificationPriceV23 = this.b2bClient.getNotificationPriceV23(pagamento.getPagoPa().getCreditorTaxId(), pagamento.getPagoPa().getNoticeCode());
            try {
                assertNotNull(notificationPriceV23.getTotalPrice());
                assertNotNull(notificationPriceV23.getPartialPrice());
                assertNotNull(notificationPriceV23.getIun());
                assertNotNull(notificationPriceV23.getAnalogCost());
                assertNotNull(notificationPriceV23.getPaFee());
                assertNotNull(notificationPriceV23.getVat());
                assertEquals(vat, notificationPriceV23.getVat());
                assertEquals(paFee, notificationPriceV23.getPaFee());
                log.info("notification price v23: {}", notificationPriceV23);
            } catch (AssertionFailedError assertionFailedError) {
                sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
            }
        }
    }

    @And("viene verificato data corretta del destinatario {int}")
    public void verificationDateNotificationPrice(Integer destinatario) {
        List<NotificationPaymentItem> listNotificationPaymentItem = sharedSteps.getSentNotificationLastVersion().getRecipients().get(destinatario).getPayments();
        if (listNotificationPaymentItem != null) {
            for (NotificationPaymentItem notificationPaymentItem : listNotificationPaymentItem) {
                NotificationPriceResponseV23 notificationPrice = this.b2bClient.getNotificationPriceV23(notificationPaymentItem.getPagoPa().getCreditorTaxId(), notificationPaymentItem.getPagoPa().getNoticeCode());
                try {
                    assertEquals(notificationPrice.getIun(), sharedSteps.getNotificationIun());
                    assertNotNull(notificationPrice.getNotificationViewDate());

                } catch (AssertionFailedError assertionFailedError) {
                    sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
                }
            }
        }
    }

    @And("viene verificato il costo di {int} e il peso di {int} nei details dell'elemento di timeline letto")
    public void verifyPriceAndWeightInvioCartaceo(Integer price, Integer weight) {
        getB2bStepsInterface().verifyPriceAndWeightInvioCartaceo(price, weight);
    }

    @And("viene verificato che il peso della busta cartacea sia di {int}")
    public void verifyPriceAndWeightInvioCartaceo(Integer weight) {
        getB2bStepsInterface().verifyPriceAndWeightInvioCartaceo(null, weight);
    }

    @And("viene verificato che il timestamp dell'evento {string} sia immediatamente successivo a quello dell'evento {string} con una differenza massima di {int} secondi")
    public void confrontoTimestampEventi(String nextTimelineEvent, String previousTimelineEvent, Integer delta) {
        FullSentNotificationV29 fullSentNotificationV26 = sharedSteps.getSentNotificationLastVersion();
        List<TimelineElementV28> timelineElements = fullSentNotificationV26.getTimeline();

        Optional<TimelineElementV28> timelineElementV26OptionalNext = timelineElements.stream()
                .filter(element -> element.getCategory() != null && element.getCategory().toString().equals(nextTimelineEvent))
                .findFirst();
        Optional<TimelineElementV28> timelineElementV26OptionalPrevious = timelineElements.stream()
                .filter(element -> element.getCategory() != null && element.getCategory().toString().equals(previousTimelineEvent))
                .findFirst();

        try {
            Assertions.assertTrue(timelineElementV26OptionalNext.isPresent() && timelineElementV26OptionalPrevious.isPresent());

            Long timestampNext = timelineElementV26OptionalNext.get().getTimestamp().toInstant().toEpochMilli();
            Long timeStampPrevious = timelineElementV26OptionalPrevious.get().getTimestamp().toInstant().toEpochMilli();
            Long diffMillis = timestampNext - timeStampPrevious;
            delta = delta * 1000;

            log.info("PRE-ASSERTION: iun={} nextTimelineEvent={}, previousTimelineEvent={}, diffMillis={}, delta={}",
                    sharedSteps.getNotificationIun(), timestampNext, timeStampPrevious, diffMillis, delta);
            Assertions.assertTrue(diffMillis <= delta);
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @Then("esiste l'elemento di timeline della notifica {string} con notificationCost uguale a {string} per l'utente {int}")
    public void TimelineElementOfNotificationUserCost(String timelineEventCategory, String cost, Integer recipientIndex) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .recipientIndex(recipientIndex)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder()
                .notificationCost(cost)
                .timelineEventCategory(timelineEventCategory)
                .recipientIndex(recipientIndex)
                .build();
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, true, CHECK_NOTIFICATION_COST_FOR_USER, checkFilters);
    }

    @Then("viene controllato che l'elemento di timeline della notifica {string} non esiste con V23")
    public void readingNotEventUpToTheTimelineElementOfNotificationV23(String timelineEventCategory) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface(NotificationVersion.V23);
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(timelineEventCategory, false, null, null);
    }

    @And("controllo che le tempistiche di arrivo tra l elemento {string} con address type {string} digitalAddressSource {string} in {string} e l'elemento {string} siano corrette per la notifica {string}")
    public void controlloCheLeTempisticheDiArrivoTraLElementoConAddressTypeDigitalAddressSourceInELElementoSianoCorrettePerLaNotifica(String firstElement, String addressType, String digitalAddressSource, String responseStatus, String secondElement, String notificationType) {
        FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();

        assertNotNull(fullSentNotification);
        assertNotNull(fullSentNotification.getTimeline());
        String iun = fullSentNotification.getIun();

        TimelineElementV28 firstElementToCheck = getElementToCheck(firstElement, addressType, digitalAddressSource, responseStatus);

        assertNotNull(firstElementToCheck, "first element to check not found iun: " + iun);
        assertNotNull(firstElementToCheck.getEventTimestamp(), "EventTimestamp for first element to check not found iun: " + iun);

        TimelineElementV28 secondElementToCheck = getElementToCheck(secondElement);

        assertNotNull(secondElementToCheck, "second element to check not found iun: " + iun);
        assertNotNull(secondElementToCheck.getDetails(), "Details for second element to check not found iun: " + iun);
        assertNotNull(secondElementToCheck.getDetails().getSchedulingDate(), "SchedulingDate for second element to check not found iun: " + iun);

        assertEquals(firstElementToCheck.getTimestamp(), firstElementToCheck.getEventTimestamp());

        int minsToCheck = getMinutesToCheck(notificationType);

        long differenceInMinutes = Duration.between(getFirstElementTime(firstElementToCheck, firstElement, addressType, iun), secondElementToCheck.getDetails().getSchedulingDate()).toMinutes();
        assertEquals(minsToCheck, differenceInMinutes, "Time between first and second element not correct: " + iun + " expected wait " + minsToCheck + " actual wait " + differenceInMinutes);
    }

    private OffsetDateTime getFirstElementTime(TimelineElementV28 firstElementToCheck, String firstElement, String addressType, String iun) {
        if (firstElement.equalsIgnoreCase("SEND_DIGITAL_FEEDBACK") && addressType.equals("SERCQ")) {
            assertNotNull(firstElementToCheck.getDetails(), "Details for first element to check not found iun: " + iun);
            assertNotNull(firstElementToCheck.getDetails().getNotificationDate(), "NotificationDate for first element to check not found iun: " + iun);
            return firstElementToCheck.getDetails().getNotificationDate();
        } else if (firstElement.equalsIgnoreCase("DIGITAL_DELIVERY_CREATION_REQUEST")) {
            assertNotNull(firstElementToCheck.getDetails(), "Details for first element to check not found iun: " + iun);
            assertNotNull(firstElementToCheck.getDetails().getCompletionWorkflowDate(), "CompletionWorkflowDate for first element to check not found iun: " + iun);
            return firstElementToCheck.getDetails().getCompletionWorkflowDate();
        } else return firstElementToCheck.getEventTimestamp();
    }

    private TimelineElementV28 getElementToCheck(String secondElement) {
        return sharedSteps.getSentNotificationLastVersion().getTimeline()
                .stream().filter(data -> data.getElementId().startsWith(secondElement))
                .findFirst().orElse(null);
    }

    private TimelineElementV28 getElementToCheck(String firstElement, String addressType, String digitalAddressSource, String responseStatus) {
        return sharedSteps.getSentNotificationLastVersion().getTimeline()
                .stream()
                .filter(data -> data.getElementId().startsWith(firstElement))
                .filter(data -> data.getDetails() != null)
                .filter(data -> responseStatus == null || data.getDetails().getResponseStatus().equals(ResponseStatus.fromValue(responseStatus.toUpperCase())))
                .filter(data -> addressType == null || data.getDetails().getDigitalAddress().getType().equals(addressType))
                .filter(data -> digitalAddressSource == null || data.getDetails().getDigitalAddressSource().equals(DigitalAddressSource.fromValue(digitalAddressSource.toUpperCase())))
                .findFirst()
                .orElse(null);
    }

    @And("controllo che le tempistiche di arrivo tra l elemento {string} e l'elemento {string} siano corrette per la notifica {string}")
    public void controlloCheLeTempisticheDiArrivoTraLElementoELElementoSiaDiMinuti(String firstElement, String secondElement, String notificationType) {
        controlloCheLeTempisticheDiArrivoTraLElementoConAddressTypeDigitalAddressSourceInELElementoSianoCorrettePerLaNotifica(firstElement, null, null, null, secondElement, notificationType);
    }

    private int getMinutesToCheck(String notificationType) {
        return switch (notificationType) {
            case "SUCCESSO ANALOGICO" -> sharedSteps.getSchedulingDaysSuccessAnalogRefinement().toMinutesPart();
            case "ERRORE ANALOGICO" -> sharedSteps.getSchedulingDaysFailureAnalogRefinement().toMinutesPart();
            case "SUCCESSO DIGITALE" -> sharedSteps.getSchedulingDaysSuccessDigitalRefinement().toMinutesPart();
            case "ERRORE DIGITALE" -> sharedSteps.getSchedulingDaysFailureDigitalRefinement().toMinutesPart();
            default -> throw new IllegalArgumentException("No notificationType founded");
        };
    }

    @And("viene verificato che l'elemento di timeline {string} con response status {string} con la {string} {string}")
    public void vieneVerificatoCheLElementoDiTimelineConResponseStatusPerLa(String timelineElement, String responseStatus, String type, String address) {

        try {
            FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
            assertSoftly(softly -> {
                assertThat(fullSentNotification).as("La fullSentNotification non dev'essere null").isNotNull();
                assertThat(fullSentNotification.getTimeline()).as("La timeline della fullSentNotification non dev'essere null").isNotNull();
                TimelineElementV28 te = fullSentNotification.getTimeline()
                        .stream()
                        .filter(data -> data.getElementId().startsWith(timelineElement))
                        .filter(data -> data.getDetails() != null)
                        .filter(data -> data.getDetails().getResponseStatus().equals(ResponseStatus.fromValue(responseStatus.toUpperCase())))
                        .filter(data -> data.getDetails().getDigitalAddress().getType().equalsIgnoreCase(type))
                        .filter(data -> data.getDetails().getDigitalAddress().getAddress().equalsIgnoreCase(address))
                        .findFirst().orElse(null);
                assertThat(te).as("Il timelineElement atteso non è stato trovato").isNotNull();
            });
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @And("viene verificato che nell'elemento di timeline della notifica {string} sia presente:")
    public void verificaTimelineDetails(String timelineEventCategory, io.cucumber.datatable.DataTable dataTable) throws Throwable {
        assertNotNull(timelineEventCategory, "Il parametro 'timelineEventCategory' non può essere null.");

        Map<String, String> inputFields = dataTable.asMap();
        Map<String, String> expectedFields = new HashMap<>(inputFields);
        expectedFields.put("category", timelineEventCategory);

        FullSentNotificationV29 fullSentNotification = (FullSentNotificationV29) getB2bStepsInterface().getFullSentNotification();
        List<TimelineElementV28> timeline = fullSentNotification.getTimeline();

        log.info("Ricerca elemento di timeline con i seguenti criteri:");
        expectedFields.forEach((k, v) -> log.info("  - {} = {}", k, v));

        boolean matchFound = timeline.stream()
                .anyMatch(element -> matchesAllFields(element, expectedFields));

        if (!matchFound) {
            throw new AssertionError("Nessun elemento della timeline corrisponde a tutti i criteri: " + expectedFields);
        }
    }

    private boolean matchesAllFields(TimelineElementV28 element, Map<String, String> expectedFields) {
        for (Map.Entry<String, String> entry : expectedFields.entrySet()) {
            String fieldPath = entry.getKey();
            String expectedValue = entry.getValue();

            Object actualFieldValue;
            try {
                actualFieldValue = resolveNestedFieldValue(element, fieldPath);
            } catch (Exception e) {
                log.debug("Errore nell'accesso al campo '{}': {}", fieldPath, e.getMessage());
                return false;
            }

            String actualValue = actualFieldValue != null ? actualFieldValue.toString() : null;

            if (!compareExpectedValue(expectedValue, actualValue)) {
                log.debug("Confronto fallito per '{}': atteso='{}', trovato='{}'", fieldPath, expectedValue, actualValue);
                return false;
            }
        }

        return true;
    }

    private Object resolveNestedFieldValue(Object rootObject, String fieldPath) throws Exception {
        String[] fields = fieldPath.split("\\.");
        Object currentObject = rootObject;

        for (int i = 0; i < fields.length; i++) {
            String fieldName = fields[i];

            Field field = getFieldFromHierarchy(currentObject.getClass(), fieldName);
            field.setAccessible(true);

            currentObject = field.get(currentObject);

            if (currentObject == null && i < fields.length - 1) {
                throw new NullPointerException("Campo intermedio '" + fieldName + "' è null nel path '" + fieldPath + "'");
            }
        }

        return currentObject;
    }

    private Field getFieldFromHierarchy(Class<?> cls, String fieldName) throws NoSuchFieldException {
        while (cls != null) {
            try {
                return cls.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                cls = cls.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Campo '" + fieldName + "' non trovato nella gerarchia di classi.");
    }

    private boolean verifyRegexMatch(String regex, String actualValue) {
        Pattern pattern = Pattern.compile(regex);
        String safeValue = actualValue != null ? actualValue : "";
        Matcher matcher = pattern.matcher(safeValue);

        return matcher.matches();
    }

    private boolean compareExpectedValue(String expectedValue, String actualValue) {
        final String REGEX_TOKEN = "__REGEX__";

        if (expectedValue != null && expectedValue.startsWith(REGEX_TOKEN)) {
            String regex = expectedValue.substring(REGEX_TOKEN.length());
            return verifyRegexMatch(regex, actualValue);
        } else {
            return Objects.equals(expectedValue, actualValue);
        }
    }

    /**
     * Verifica lo scarto temporale tra due eventi con deliveryDetailCode specifico.
     * Da usare solo dopo aver appurato la presenza di tali elementi
     * (e in scenari che non prevedono multi-destinatario o invii multipli, che potrebbero portare alla NON univocità del deliveryDetailCode)
     */
    @And("lo scarto temporale tra {string} e {string} è {isSuperiore} a {int} {unitaTemporale}")
    public void checkScartoTemporaleTraDueDeliveryDetailCode(String code1, String code2, Boolean isSuperiore, int timeQuantity, ChronoUnit unitaTemporale) {
        try {
            getB2bStepsInterface().checkScartoTemporaleTraDueDeliveryDetailCode(code1, code2, isSuperiore, timeQuantity, unitaTemporale);
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    /**
     * Input: 2 o 3 Delivery details code degli elementi SEND_ANALOG_PROGRESS e SEND_ANALOG_FEEDBACK
     * tipo di confronto "uguali" o "diversi"
     * Verifica che i timestamp dei DeliveyDetailCode siano uguali o doversi tra loro
     */
    @And("verifica che i DeliveryDetailCode {string} {string} {string} abbiano timestamp {string}")
    public void checkTimestampTriplettaDetailCode(String detailCode1, String detailCode2, String detailCode3, String compare) {

        OffsetDateTime timestamp1 = null;
        OffsetDateTime timestamp2 = null;
        OffsetDateTime timestamp3 = null;

        try {
            FullSentNotificationV29 fullSentNotification = b2bClient.getSentNotificationV29(sharedSteps.getNotificationIun());
            for (TimelineElementV28 item : fullSentNotification.getTimeline()) {

                if ((item.getCategory().getValue().equals("SEND_ANALOG_FEEDBACK")
                        || item.getCategory().getValue().equals("SEND_ANALOG_PROGRESS"))
                        && item.getDetails().getDeliveryDetailCode() != null) {

                    if (item.getDetails().getDeliveryDetailCode().equals(detailCode1)) {
                        timestamp1 = item.getDetails().getNotificationDate();
                    }
                    if (item.getDetails().getDeliveryDetailCode().equals(detailCode2)) {
                        timestamp2 = item.getDetails().getNotificationDate();
                    }
                    if (detailCode3 != null && !detailCode3.isEmpty()
                            && item.getDetails().getDeliveryDetailCode().equals(detailCode3)) {
                        timestamp3 = item.getDetails().getNotificationDate();
                    }
                }
            }

            assertNotNull(timestamp1, "Timestamp per " + detailCode1 + " non trovato, IUN: " + sharedSteps.getNotificationIun());
            assertNotNull(timestamp2, "Timestamp per " + detailCode2 + " non trovato, IUN: " + sharedSteps.getNotificationIun());

            if ("uguali".equalsIgnoreCase(compare)) {
                assertEquals(timestamp1, timestamp2, timestamp1 + " di deliveryCode " + detailCode1 + " e " + timestamp2 + " di deliveryCode " + detailCode2 + " non coincidono, IUN: " + sharedSteps.getNotificationIun());
                if (detailCode3 != null && !detailCode3.isEmpty()) {
                    assertNotNull(timestamp3, "Timestamp per " + detailCode3 + " non trovato, IUN: " + sharedSteps.getNotificationIun());
                    assertEquals(timestamp1, timestamp3, timestamp1 + " di deliveryCode " + detailCode1 + " e " + timestamp3 + " di deliveryCode " + detailCode3 + " non coincidono, IUN: " + sharedSteps.getNotificationIun());
                }
            } else if ("diversi".equalsIgnoreCase(compare)) {
                assertNotEquals(timestamp1, timestamp2, timestamp1 + " e " + timestamp2 + " devono essere diversi, IUN: " + sharedSteps.getNotificationIun());
                if (detailCode3 != null && !detailCode3.isEmpty()) {
                    assertNotNull(timestamp3, "Timestamp per " + detailCode3 + " non trovato, IUN: " + sharedSteps.getNotificationIun());
                    assertNotEquals(timestamp1, timestamp3, timestamp1 + " di deliveryCode " + detailCode1 + " e " + timestamp3 + " di deliveryCode " + detailCode3 + " devono essere diversi, IUN: " + sharedSteps.getNotificationIun());
                    assertNotEquals(timestamp2, timestamp3, timestamp2 + " di deliveryCode " + detailCode2 + " e " + timestamp3 + " di deliveryCode " + detailCode3 + " devono essere diversi, IUN: " + sharedSteps.getNotificationIun());
                }
            } else {
                throw new IllegalArgumentException("Tipo di confronto non valido: " + compare);
            }
        } catch (Exception exception) {
            log.error("Error getPollingResponse(), Iun: {}, PnPollingException: {}",
                    sharedSteps.getNotificationIun(),
                    exception.getMessage());
            throw new PnPollingException(exception.getMessage());
        }
    }

    /**
     * Input: 2 Triplette di Delivery details code degli elementi SEND_ANALOG_PROGRESS e SEND_ANALOG_FEEDBACK
     * Verifica che i timestamp dei DeliveyDetailCode siano uguali tra loro
     */
    @And("verifica che i DeliveryDetailCode {string} {string} {string} e {string} {string} {string} abbiano timestamp uguali")
    public void checkTimestampTriplettaDetailCodeDouble(
            String detailCode1,
            String detailCode2,
            String detailCode3,
            String detailCode4,
            String detailCode5,
            String detailCode6
    ) {
        String[] detailCodes = {
                detailCode1, detailCode2, detailCode3,
                detailCode4, detailCode5, detailCode6
        };

        try {
            FullSentNotificationV29 fullSentNotification =
                    b2bClient.getSentNotificationV29(sharedSteps.getNotificationIun());

            Map<String, OffsetDateTime> timestampMap = fullSentNotification.getTimeline().stream()
                    .filter(item ->
                            (item.getCategory().getValue().equals("SEND_ANALOG_FEEDBACK") ||
                                    item.getCategory().getValue().equals("SEND_ANALOG_PROGRESS"))
                                    && item.getDetails().getDeliveryDetailCode() != null
                                    && Arrays.asList(detailCodes).contains(item.getDetails().getDeliveryDetailCode())
                    )
                    .collect(Collectors.toMap(
                            item -> item.getDetails().getDeliveryDetailCode(),
                            item -> (OffsetDateTime) item.getDetails().getNotificationDate(),
                            (existing, replacement) -> existing
                    ));

            Arrays.stream(detailCodes).forEach(code ->
                    assertNotNull(timestampMap.get(code),
                            "Timestamp per " + code + " non trovato, IUN: " + sharedSteps.getNotificationIun())
            );

            compareTriple(timestampMap, detailCode1, detailCode2, detailCode3);
            compareTriple(timestampMap, detailCode4, detailCode5, detailCode6);

        } catch (Exception exception) {
            log.error("Error getPollingResponse(), Iun: {}, PnPollingException: {}",
                    sharedSteps.getNotificationIun(),
                    exception.getMessage());
            throw new PnPollingException(exception.getMessage());
        }
    }

    private void compareTriple(Map<String, OffsetDateTime> timestampMap,
                               String code1, String code2, String code3) {

        OffsetDateTime ts1 = timestampMap.get(code1);
        OffsetDateTime ts2 = timestampMap.get(code2);
        OffsetDateTime ts3 = timestampMap.get(code3);

        assertEquals(ts1, ts2, ts1 + " di deliveryCode " + code1 + " e " + ts2 + " di deliveryCode " + code2 + " non coincidono, IUN: " + sharedSteps.getNotificationIun());
        assertEquals(ts1, ts3, ts1 + " di deliveryCode " + code1 + " e " + ts3 + " di deliveryCode " + code3 + " non coincidono, IUN: " + sharedSteps.getNotificationIun());
    }


    @DataTableType
    public synchronized DataTest convertTimelineElement(Map<String, String> data) throws JsonProcessingException {
        String recIndex = getValue(data, DETAILS_REC_INDEX.key);
        String sentAttemptMade = getValue(data, DETAILS_SENT_ATTEMPT_MADE.key);
        String retryNumber = getValue(data, DETAILS_RETRY_NUMBER.key);
        String responseStatus = getValue(data, DETAILS_RESPONSE_STATUS.key);
        String digitalAddressSource = getValue(data, DETAILS_DIGITAL_ADDRESS_SOURCE.key);
        String isAvailable = getValue(data, DETAILS_IS_AVAILABLE.key);
        String isFirstRetry = getValue(data, IS_FIRST_SEND_RETRY.key);
        String progressIndex = getValue(data, PROGRESS_INDEX.key);
        String analogCost = getValue(data, DETAILS_ANALOG_COST.key);
        String pollingTime = getValue(data, POLLING_TIME.key);
        String numCheck = getValue(data, NUM_CHECK.key);
        String pollingType = getValue(data, POLLING_TYPE.key);
        String loadTimeline = getValue(data, LOAD_TIMELINE.key);

        if (data.size() == 1 && data.get("NULL") != null) {
            return null;
        }

        DataTest dataTest = new DataTest();
        TimelineElementV28 timelineElement = new TimelineElementV28()
                .legalFactsIds(getListValue(LegalFactsIdV20.class, data, LEGAL_FACT_IDS.key))
                .details(getValue(data, DETAILS.key) == null ? null : new TimelineElementDetailsV28()
                        .recIndex(recIndex != null ? Integer.parseInt(recIndex) : null)
                        .digitalAddress(getObjValue(DigitalAddress.class, data, DETAILS_DIGITAL_ADDRESS.key))
                        .refusalReasons(getListValue(NotificationRefusedErrorV27.class, data, DETAILS_REFUSAL_REASONS.key))
                        .generatedAarUrl(getValue(data, DETAILS_GENERATED_AAR_URL.key))
                        .responseStatus(responseStatus != null ? ResponseStatus.valueOf(responseStatus) : null)
                        .digitalAddressSource(digitalAddressSource != null ? DigitalAddressSource.valueOf(digitalAddressSource) : null)
                        .sentAttemptMade(sentAttemptMade != null ? Integer.parseInt(sentAttemptMade) : null)
                        .retryNumber(retryNumber != null ? Integer.parseInt(retryNumber) : null)
                        .sendingReceipts(getListValue(SendingReceipt.class, data, DETAILS_SENDING_RECEIPT.key))
                        .isAvailable(isAvailable != null ? Boolean.valueOf(getValue(data, DETAILS_IS_AVAILABLE.key)) : null)
                        .deliveryDetailCode(getValue(data, DETAILS_DELIVERY_DETAIL_CODE.key))
                        .deliveryFailureCause(getValue(data, DETAILS_DELIVERY_FAILURE_CAUSE.key))
                        .attachments(getListValue(AttachmentDetails.class, data, DETAILS_ATTACHMENTS.key))
                        .physicalAddress(getObjValue(PhysicalAddress.class, data, DETAILS_PHYSICALADDRESS.key))
                        .analogCost(analogCost != null ? Integer.parseInt(analogCost) : null)
                        .delegateInfo(getObjValue(DelegateInfo.class, data, DETAILS_DELEGATE_INFO.key))
                );

        // IMPORTANT: no empty data check; enrich with new checks if it is needed
        if (timelineElement.getDetails() != null || timelineElement.getLegalFactsIds() != null) {
            dataTest.setTimelineElement(timelineElement);
        }
        dataTest.setFirstSendRetry(isFirstRetry != null ? Boolean.valueOf(isFirstRetry) : null);
        dataTest.setProgressIndex(progressIndex != null ? Integer.parseInt(progressIndex) : null);
        dataTest.setPollingTime(pollingTime != null ? Integer.parseInt(pollingTime) : null);
        dataTest.setPollingType(pollingType);
        dataTest.setNumCheck(numCheck != null ? Integer.parseInt(numCheck) : null);
        dataTest.setLoadTimeline(loadTimeline != null ? Boolean.valueOf(loadTimeline) : null);

        return dataTest;
    }

    @Then("viene verificato che i dati relativi all'indirizzo nell'elemento di timeline {string} siano valorizzati")
    public void checkAddressDataInTimelineElementsOnDB(String timelineElement) {
        QueryResponse queryResponse = dbService.call(DynamoTableName.TIMELINE, Map.of(
                ":v_iun", AttributeValue.builder().s(sharedSteps.getNotificationIun()).build(),
                ":v_category", AttributeValue.builder().s(timelineElement).build()
        ));

        log.info("Elementi trovati con categoria {}: {}", timelineElement, queryResponse.count());

        try {
            // Assertion: almeno un elemento deve esistere
            assertTrue(queryResponse.count() > 0,
                    "Nessun elemento trovato per la category: " + timelineElement);

            for (Map<String, AttributeValue> item : queryResponse.items()) {

                String category = item.get("category").s();

                // details
                Map<String, AttributeValue> details = item.get("details") != null
                        ? item.get("details").m()
                        : null;

                assertNotNull(details, "details non deve essere null");

                // physicalAddress (sempre presente)
                Map<String, AttributeValue> physicalAddress = details.get("physicalAddress") != null
                        ? details.get("physicalAddress").m()
                        : null;

                assertNotNull(physicalAddress, "details.physicalAddress non deve essere null");
                assertTrue(isNotEmpty(physicalAddress.get("municipality")),
                        "details.physicalAddress.municipality non valorizzato");

                // newAddress solo per NORMALIZED_ADDRESS
                if ("NORMALIZED_ADDRESS".equals(category)) {

                    Map<String, AttributeValue> newAddress = details.get("newAddress") != null
                            ? details.get("newAddress").m()
                            : null;

                    assertNotNull(newAddress, "details.newAddress non deve essere null");

                    assertTrue(isNotEmpty(newAddress.get("foreignState")),
                            "details.newAddress.foreignState non valorizzato");
                    assertTrue(isNotEmpty(newAddress.get("municipality")),
                            "details.newAddress.municipality non valorizzato");
                    assertTrue(isNotEmpty(newAddress.get("zip")),
                            "details.newAddress.zip non valorizzato");
                }
            }

        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    private boolean isNotEmpty(AttributeValue attributeValue) {
        return attributeValue != null
                && attributeValue.s() != null
                && !attributeValue.s().trim().isEmpty();
    }

}
