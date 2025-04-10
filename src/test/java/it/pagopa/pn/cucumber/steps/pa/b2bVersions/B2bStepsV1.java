package it.pagopa.pn.cucumber.steps.pa.b2bVersions;

import io.cucumber.datatable.DataTable;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.*;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPriceResponse;
import it.pagopa.pn.client.b2b.pa.polling.IPnPollingService;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingPredicate;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV1;
import it.pagopa.pn.client.b2b.pa.polling.impl.v1.PnPollingServiceTimelineRapidV1;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.NotificationHistoryResponse;
import it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheB2bSteps;
import it.pagopa.pn.cucumber.steps.pa.notificationVersions.NotificationStepsV1;
import it.pagopa.pn.cucumber.steps.pa.notificationVersions.NotificationVersion;
import it.pagopa.pn.cucumber.steps.utilitySteps.PollingType;
import it.pagopa.pn.cucumber.steps.utilitySteps.WaitForEventPredicateFilters;
import it.pagopa.pn.cucumber.steps.utilitySteps.checkTimelineElement.TimelineElementCheck;
import it.pagopa.pn.cucumber.steps.utilitySteps.checkTimelineElement.TimelineElementCheckFilters;
import it.pagopa.pn.cucumber.utils.datatest.DataTestV1;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;

import java.lang.reflect.InvocationTargetException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.IntStream;

import static it.pagopa.pn.cucumber.steps.utilitySteps.Costanti.*;
import static it.pagopa.pn.cucumber.steps.utilitySteps.PollingType.STATUS;
import static it.pagopa.pn.cucumber.steps.utilitySteps.PollingType.TIMELINE;
import static java.time.OffsetDateTime.now;
import static java.util.Objects.nonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.awaitility.Awaitility.await;

@Slf4j
public class B2bStepsV1 implements B2bStepsInterface {

    private TimelineElement timelineElement;
    private NotificationStatusHistoryElement notificationStatusHistoryElement;
    private PnPollingResponseV1 pollingResponse;
    private final NotificationVersion version;
    private final AvanzamentoNotificheB2bSteps b2bSteps;

    public B2bStepsV1(AvanzamentoNotificheB2bSteps b2bSteps) {
        version = NotificationVersion.V1;
        this.b2bSteps = b2bSteps;
    }

    @Override
    public Object getFullSentNotification() {
        return b2bSteps.getB2bClient().getSentNotificationV1(b2bSteps.getSharedSteps().getNotificationIun());
    }

    private FullSentNotification getFullSentNotificationVersioned() {
        return (FullSentNotification) getFullSentNotification();
    }

    @Override
    public void checkFullSentNotificationWithVersion(boolean isPresent, String timelineEventCategory, Integer introducingVersion) {
        FullSentNotification fullSentNotification = getFullSentNotificationVersioned();
        TimelineElement timelineElement = fullSentNotification.getTimeline().stream().filter(
                te -> te.getCategory().getValue().equals(timelineEventCategory)).findAny().orElse(null);
        if (isPresent) {
            assertSoftly(softly -> {
                if (introducingVersion != null) {
                    assertThat(version.getValue())
                            .as("L'elemento di timeline {} è stato introdotto con una versione pari o superiore a questa", timelineEventCategory)
                            .isGreaterThanOrEqualTo(introducingVersion);
                }
                assertThat(timelineElement)
                        .as("Il controllo sulla fullSentNotification V{} dovrebbe restituire almeno un elemento", version.getValue())
                        .isNotNull();
            });
        } else {
            assertSoftly(softly -> {
                if (introducingVersion != null) {
                    assertThat(version.getValue())
                            .as("L'elemento di timeline {} è stato introdotto con una versione più recente di questa", timelineEventCategory)
                            .isLessThan(introducingVersion);
                }
                assertThat(timelineElement)
                        .as("Il controllo sulla fullSentNotification V{} non dovrebbe restituire elementi", version.getValue())
                        .isNull();
            });
        }
    }

    @Override
    public void readEventsUpToTimelineElement(String timelineEventCategory) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        waitForEventOrStatus(TIMELINE_SLOW, TIMELINE, timelineEventCategory, filters);
        checkIfTimelineElementExists(true, null, null);
    }

    @Override
    public void readEventsUpToStatus(String status, boolean exists) {
        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .statusHistory(status)
                .build();
        waitForEventOrStatus(STATUS_RAPID, STATUS, status, filters);
        checkIfStatusExists(exists);
    }

    @Override
    public void checkNotificationCost(String cost) {
        Long notificationCost = timelineElement.getDetails().getNotificationCost();
        if (cost.equalsIgnoreCase("null")) {
            //TODO: ignorare Sonar che dice che il risultato di questo assetNull fallirà sempre
            // in quanto il campo è annotato con @NotNull (non è vero)
            Assertions.assertNull(notificationCost);
        } else {
            Assertions.assertEquals(Long.parseLong(cost), notificationCost);
        }
    }

    @Override
    public void checkNormalizedAddress(DataTable table) {
        log.info("indirizzo: {}", timelineElement.getDetails().getOldAddress());
        log.info("indirizzo Normalizzato: {}", timelineElement.getDetails().getNormalizedAddress());
        try {
            assertSoftly(softly -> {
                assertThat(b2bSteps.mapValueFromTable(table, "physicalAddress_address"))
                        .as("NormalizedAddress: il physical address non coincide col valore atteso")
                        .isEqualTo(timelineElement.getDetails().getNormalizedAddress().getAddress());
                assertThat(b2bSteps.mapValueFromTable(table, "at"))
                        .as("NormalizedAddress: il campo at(presso) non coincide col valore atteso")
                        .isEqualTo(timelineElement.getDetails().getNormalizedAddress().getAddress());
                assertThat(b2bSteps.mapValueFromTable(table, "physicalAddress_address"))
                        .as("NormalizedAddress: addressDetails non coincide col valore atteso")
                        .isEqualTo(timelineElement.getDetails().getNormalizedAddress().getAddress());
                assertThat(b2bSteps.mapValueFromTable(table, "physicalAddress_address"))
                        .as("NormalizedAddress: lo zipCode non coincide col valore atteso")
                        .isEqualTo(timelineElement.getDetails().getNormalizedAddress().getAddress());
                assertThat(b2bSteps.mapValueFromTable(table, "physicalAddress_address"))
                        .as("NormalizedAddress: la municipality non coincide col valore atteso")
                        .isEqualTo(timelineElement.getDetails().getNormalizedAddress().getAddress());
                assertThat(b2bSteps.mapValueFromTable(table, "physicalAddress_address"))
                        .as("NormalizedAddress: i municipalityDetails non coincidono col valore atteso")
                        .isEqualTo(timelineElement.getDetails().getNormalizedAddress().getAddress());
                assertThat(b2bSteps.mapValueFromTable(table, "physicalAddress_address"))
                        .as("NormalizedAddress: la provincia non coincide col valore atteso")
                        .isEqualTo(timelineElement.getDetails().getNormalizedAddress().getAddress());
                assertThat(b2bSteps.mapValueFromTable(table, "physicalAddress_address"))
                        .as("NormalizedAddress: il physical address non coincide col valore atteso")
                        .isEqualTo(timelineElement.getDetails().getNormalizedAddress().getAddress());
                assertThat(b2bSteps.mapValueFromTable(table, "physicalAddress_address"))
                        .as("NormalizedAddress: lo stato non coincide col valore atteso")
                        .isEqualTo(timelineElement.getDetails().getNormalizedAddress().getAddress());
            });
        } catch (AssertionFailedError assertionFailedError) {
            b2bSteps.getSharedSteps().throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    @Override
    public void checkEventPresenceForRecipient(int recipientIndex, String evento) {
        try {
            String event = notificationStatusHistoryElement.getRelatedTimelineElements()
                    .stream()
                    .filter(te -> te.contains(evento) && te.contains("RECINDEX_" + recipientIndex))
                    .findFirst()
                    .orElse(null);
            assertThat(event)
                    .as("I relatedTimelineElements del notificationStatusHistoryElement non contengono l'evento " + evento + " per il recipient " + recipientIndex)
                    .isNotNull();
        } catch (AssertionFailedError assertionFailedError) {
            b2bSteps.getSharedSteps().throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    @Override
    public void checkPriceForRecipient(int recipientIndex, String price) {
        String iun = b2bSteps.getSharedSteps().getNotificationIun();
        FullSentNotification fullSentNotification = getFullSentNotificationVersioned();
        NotificationPaymentInfo paymentInfo = fullSentNotification.getRecipients().get(recipientIndex).getPayment();
        if (paymentInfo != null) {
            NotificationPriceResponse notificationPrice = b2bSteps.getB2bClient().getNotificationPrice(
                    paymentInfo.getCreditorTaxId(), paymentInfo.getNoticeCode());
            try {
                Assertions.assertEquals(notificationPrice.getIun(), iun);
                if (price != null) {
                    log.info("Costo notifica: {} destinatario: {}", notificationPrice.getAmount(), recipientIndex);
                    Assertions.assertEquals(Integer.parseInt(price), notificationPrice.getAmount());
                }
                if (notificationPrice.getRefinementDate() != null) {
                    Assertions.assertEquals(OffsetDateTime.now().toLocalDate(), notificationPrice.getRefinementDate().toLocalDate());
                }
            } catch (AssertionFailedError assertionFailedError) {
                b2bSteps.getSharedSteps().throwAssertionErrorWithIUN(assertionFailedError);
            }
        }
    }

    @Override
    public void payAvvisoPagoPa(Integer paymentIndex, Integer recipientIndex) {
        //poiché fino alla V20 le notifiche sono ancora monopagamento, il metodo procede al pagamento del solo e unico avviso
        FullSentNotification fullSentNotification = getFullSentNotificationVersioned();
        String creditorTaxId = fullSentNotification.getRecipients().get(recipientIndex).getPayment().getCreditorTaxId();
        String noticeCode = fullSentNotification.getRecipients().get(recipientIndex).getPayment().getNoticeCode();
        NotificationPriceResponse notificationPrice = b2bSteps.getB2bClient().getNotificationPrice(creditorTaxId, noticeCode);

        PaymentEventsRequestPagoPa eventsRequestPagoPa = new PaymentEventsRequestPagoPa();

        PaymentEventPagoPa paymentEventPagoPa = new PaymentEventPagoPa();
        paymentEventPagoPa.setCreditorTaxId(creditorTaxId);
        paymentEventPagoPa.setNoticeCode(noticeCode);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        paymentEventPagoPa.setPaymentDate(fmt.format(now()));
        paymentEventPagoPa.setAmount(notificationPrice.getAmount());
        List<PaymentEventPagoPa> paymentEventPagoPaList = new LinkedList<>();
        paymentEventPagoPaList.add(paymentEventPagoPa);
        eventsRequestPagoPa.setEvents(paymentEventPagoPaList);

        b2bSteps.getB2bClient().paymentEventsRequestPagoPaV1(eventsRequestPagoPa);
    }

    @Override
    public void checkForNoDuplicatedTimelineElements(String timelineEventCategory) {
        int counter = (int) getFullSentNotificationVersioned().getTimeline()
                .stream()
                .filter(te -> te.getCategory().getValue().equals(timelineEventCategory))
                .count();
        try {
            assertThat(counter)
                    .as("L'elemento di timeline " + timelineEventCategory + " dovrebbe comparire al massimo una volta")
                    .isLessThanOrEqualTo(1);
        } catch (AssertionFailedError assertionFailedError) {
            b2bSteps.getSharedSteps().throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    @Override
    public void checkIfLastAttemptMatchesIndex(int index) {
        try {
            List<TimelineElement> actualTimelineElements = getFullSentNotificationVersioned().getTimeline().stream()
                    .filter(elem -> nonNull(elem.getDetails()))
                    //TODO: ignorare Sonar che dice che questo nonNull è inutile in quanto sempre true, non è vero
                    .filter(elem -> nonNull(elem.getDetails().getSentAttemptMade()))
                    .filter(elem -> elem.getDetails().getSentAttemptMade() <= index)
                    .toList();
            List<Integer> actualAttemptsMade = actualTimelineElements.stream()
                    .map(TimelineElement::getDetails)
                    .filter(Objects::nonNull)
                    .map(TimelineElementDetails::getSentAttemptMade)
                    .distinct()
                    .toList();
            List<Integer> expectedAttemptsMade = IntStream.range(0, index + 1)
                    .boxed()
                    .toList();
            assertThat(actualAttemptsMade)
                    .as("Non è stato trovato alcun elemento di timeline corrispondente a un tentativo di indice minore o uguale a '%d'.".formatted(index))
                    .isNotEmpty()
                    .as("I tentativi effettuati non corrispondono a quelli attesi.")
                    .hasSameElementsAs(expectedAttemptsMade);
        } catch (AssertionError assertionFailedError) {
            b2bSteps.getSharedSteps().throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    @Override
    public void verifyPriceAndWeightInvioCartaceo(Integer price, Integer weight) {
        try {
            if (price != null) {
                assertThat(timelineElement.getDetails().getAnalogCost())
                        .as("Il costo differisce da quanto previsto")
                        .isEqualTo(price);
            }
            if (weight != null) {
                assertThat(timelineElement.getDetails().getEnvelopeWeight())
                        .as("Il peso differisce da quanto previsto")
                        .isEqualTo(weight);
            }
        } catch (AssertionFailedError assertionFailedError) {
            b2bSteps.getSharedSteps().throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    @Override
    public void checkIfTimelineElementFromDataExists(boolean exists, String timelineEventCategory, Map<String, String> dataMap) {
        try {
            DataTestV1 dataTest = DataTestV1.convertMap(dataMap);
            boolean mustLoadTimeline = dataTest != null && dataTest.isLoadTimeline();
            if (mustLoadTimeline) {
                loadTimeline(timelineEventCategory, exists, dataTest);
            }
            List<TimelineElement> timelineElements = getTimelineElementsByEventId(timelineEventCategory, dataTest);
            assertThat(timelineElements)
                    .withFailMessage("Not found a time element '%s'. IUN: %s".formatted(timelineEventCategory, b2bSteps.getSharedSteps().getNotificationIun()))
                    .isNotEmpty();
            if (dataTest != null && dataTest.getTimelineElement() != null) {
                boolean atLeastOneSuccessful = false;
                AssertionFailedError assertionFailedError = null;
                for (TimelineElement te : timelineElements) {
                    try {
                        timelineElement = te;
                        log.info("TIMELINE_ELEMENT: " + te);
                        DataTestV1.checkTimelineElementEquality(timelineEventCategory, te, dataTest);
                        atLeastOneSuccessful = true;// se si arriva a questo punto, allora l'ultimo check ha avuto successo e non è necessario continuare
                    } catch (AssertionFailedError e) {
                        assertionFailedError = e;// se si arriva a questo punto allora l'ultimo check ha fallito e ci si prepara al prossimo
                    }
                }
                if (!atLeastOneSuccessful) {// se nessun confronto ha avuto successo allora di certo sarà stata lanciata un'eccezione
                    throw assertionFailedError;// si rilancia l'ultima eccezione catturata
                }
            }
        } catch (AssertionFailedError assertionFailedError) {
            b2bSteps.getSharedSteps().throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    private void loadTimeline(String timelineEventCategory, boolean existCheck, DataTestV1 dataTest) {
        if (!timelineEventCategory.equals(REQUEST_REFUSED)) {
            timelineElement = getAndStoreTimelineByB2b(timelineEventCategory, dataTest);
            String iun = b2bSteps.getSharedSteps().getNotificationIun();
            List<TimelineElement> timelineElementList = getFullSentNotificationVersioned().getTimeline();
            log.info("NOTIFICATION_TIMELINE: " + timelineElementList);
            Assertions.assertNotNull(timelineElementList, "timelineElementList is null. IUN: " + iun);
            Assertions.assertNotEquals(0, timelineElementList.size(), "timelineElementList is empty. IUN: " + iun);
            if (existCheck) {
                Assertions.assertNotNull(timelineElement, "timelineElement is null. IUN: " + iun);
            } else {
                Assertions.assertNull(timelineElement, "timelineElement is not null. IUN: " + iun);
            }
        } else {
            //GESTIONE LOAD TIMELINE E RECUPERO NOTIFICA CON CLIENT DI DELIVERY PUSH
            loadTimelineByDeliveryPush(timelineEventCategory, dataTest, existCheck);
        }
    }

    private void loadTimelineByDeliveryPush(String timelineEventCategory, DataTestV1 dataTest, boolean existCheck) {
        TimingForPolling timingForPolling = b2bSteps.getTimingForPolling();
        // calc how much time wait
        Integer pollingTime = dataTest != null ? dataTest.getPollingTime() : null;
        Integer numCheck = dataTest != null ? dataTest.getNumCheck() : null;
        String pollingType = dataTest != null ? dataTest.getPollingType() : null;

        TimingForPolling.TimingResult timingForElement = timingForPolling.getTimingForElement(timelineEventCategory);
        if ("extraRapid".equals(pollingType)) {
            timingForElement = timingForPolling.getTimingForElement(timelineEventCategory, false, true);
        }

        int defaultPollingTime = timingForElement.waiting();
        int defaultNumCheck = timingForElement.numCheck();
        int waitingTime = (pollingTime != null ? pollingTime : defaultPollingTime) * (numCheck != null ? numCheck : defaultNumCheck);

        await()
                .atMost(waitingTime, MILLISECONDS)
                .with()
                .pollInterval(pollingTime != null ? pollingTime : defaultPollingTime, MILLISECONDS)
                .pollDelay(0, MILLISECONDS)
                .ignoreExceptions()
                .untilAsserted(() -> {
                    TimelineElement timelineElement = getTimelineByDeliveryPush(timelineEventCategory, dataTest);
                    List<TimelineElement> timelineElementList = getFullSentNotificationVersioned().getTimeline();
                    log.info("NOTIFICATION_TIMELINE: " + timelineElementList);
                    Assertions.assertNotNull(timelineElementList);
                    Assertions.assertNotEquals(0, timelineElementList.size());
                    if (existCheck) {
                        Assertions.assertNotNull(timelineElement);
                    } else {
                        Assertions.assertNull(timelineElement);
                    }
                });
    }

    private TimelineElement getTimelineByDeliveryPush(String timelineEventCategory, DataTestV1 dataTest) {
        String requestId = b2bSteps.getSharedSteps().getNotificationRequestId();
        byte[] decodedBytes = Base64.getDecoder().decode(requestId);
        String iun = new String(decodedBytes);
        // get timeline from delivery-push
        NotificationHistoryResponse notificationHistory = b2bSteps.getPnPrivateDeliveryPushExternalClient().getNotificationHistory(
                iun,
                b2bSteps.getSharedSteps().getRecipientsSize(),
                b2bSteps.getSharedSteps().getNotificationCreationDate());
        List<TimelineElement> timelineElementList = notificationHistory.getTimeline().stream().map(x ->
                b2bSteps.getSharedSteps().deepCopy(x, TimelineElement.class)).toList();
        return getTimelineElementByIdOrCategory(timelineEventCategory, dataTest, iun, timelineElementList);
    }

    private TimelineElement getAndStoreTimelineByB2b(String timelineEventCategory, DataTestV1 dataFromTest) {
        // proceed with default flux
        PnPollingServiceTimelineRapidV1 timelineRapid = (PnPollingServiceTimelineRapidV1) b2bSteps.getPnPollingFactory().getPollingService(PnPollingStrategy.TIMELINE_RAPID_V1);
        String iun = b2bSteps.getSharedSteps().getNotificationIun();
        PnPollingResponseV1 pnPollingResponse = timelineRapid.waitForEvent(iun, PnPollingParameter.builder().value(timelineEventCategory).build());
        return getTimelineElementByIdOrCategory(timelineEventCategory, dataFromTest, iun, pnPollingResponse.getNotification().getTimeline());
    }

    private TimelineElement getTimelineElementByIdOrCategory(String timelineEventCategory, DataTestV1 dataFromTest, String iun, List<TimelineElement> timelineElementList) {
        TimelineElement timelineElement;
        // get timeline event id
        if (dataFromTest != null && dataFromTest.getTimelineElement() != null) {
            String timelineEventId = dataFromTest.getTimelineEventId(timelineEventCategory, iun, dataFromTest);
            timelineElement = timelineElementList.stream().filter(elem -> elem.getElementId().startsWith(timelineEventId)).findAny().orElse(null);
        } else {
            timelineElement = timelineElementList.stream().filter(elem -> elem.getCategory().getValue().equals(timelineEventCategory)).findAny().orElse(null);
        }
        return timelineElement;
    }

    /**
     * Get all timeline elements that match the given event category and data from test
     *
     * @param timelineEventCategory the category of the timeline event
     * @param dataFromTest          the data filters
     * @return a list of timeline elements that match the given event category and data from test
     */
    private List<TimelineElement> getTimelineElementsByEventId(String timelineEventCategory, DataTestV1 dataFromTest) {
        String iun = b2bSteps.getSharedSteps().getNotificationIun();
        FullSentNotification fullSentNotification = getFullSentNotificationVersioned();
        List<TimelineElement> timelineElementList = fullSentNotification.getTimeline();

        if (dataFromTest != null && dataFromTest.getTimelineElement() != null) {
            // get timeline event id
            String timelineEventId = dataFromTest.getTimelineEventId(timelineEventCategory, iun, dataFromTest);
            if (timelineEventCategory.equals(SEND_ANALOG_PROGRESS)
                    || timelineEventCategory.equals(SEND_SIMPLE_REGISTERED_LETTER_PROGRESS)) {
                TimelineElement timelineElementFromTest = dataFromTest.getTimelineElement();
                TimelineElementDetails timelineElementDetails = timelineElementFromTest.getDetails();
                return timelineElementList.stream().filter(elem ->
                                Objects.requireNonNull(elem.getElementId()).startsWith(timelineEventId)
                                        && Objects.equals(Objects.requireNonNull(elem.getDetails()).getDeliveryDetailCode(), Objects.requireNonNull(timelineElementDetails).getDeliveryDetailCode()))
                        .toList();
            }
            return timelineElementList.stream().filter(elem ->
                    Objects.requireNonNull(elem.getElementId()).contains(timelineEventId)).toList();
        }
        return timelineElementList.stream().filter(elem ->
                Objects.requireNonNull(elem.getCategory()).getValue().equals(timelineEventCategory)).toList();
    }

    @Override
    public void waitForEventOrStatus(String pollingStrategy, PollingType pollingType, String timelineEventCategory, WaitForEventPredicateFilters filters) {
        String strategy = NotificationStepsV1.getPollingStrategy(pollingStrategy);
        IPnPollingService<?> pollingService = b2bSteps.getSharedSteps().getB2bUtils().getPollingFactory().getPollingService(strategy);
        PnPollingPredicate pollingPredicate = getPnPollingPredicateForTimeline(timelineEventCategory, filters);
        pollingResponse = (PnPollingResponseV1) pollingService.waitForEvent(
                b2bSteps.getSharedSteps().getNotificationIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .pnPollingPredicate(pollingPredicate)
                        .build());
        switch (pollingType) {
            case TIMELINE -> log.info("NOTIFICATION_TIMELINE: " + pollingResponse.getNotification().getTimeline());
            case STATUS ->
                    log.info("NOTIFICATION_STATUS_HISTORY: " + pollingResponse.getNotification().getNotificationStatusHistory());
        }
    }

    @Override
    public void verifyTimelineElementDoesNotExists(boolean mustLoadTimeline, String timelineEventCategory, Map<String, String> dataMap) {
        DataTestV1 dataTest = DataTestV1.convertMap(dataMap);
        if (mustLoadTimeline) {
            loadTimeline(timelineEventCategory, false, dataTest);
        }
        getTimelineElementsByEventId(timelineEventCategory, dataTest);
        log.info("TIMELINE_ELEMENT: " + timelineElement);
        try {
            assertThat(timelineElement)
                    .as("Timeline element with category " + timelineEventCategory + " should be null")
                    .isNull();
        } catch (AssertionFailedError assertionFailedError) {
            b2bSteps.getSharedSteps().throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    @Override
    public void checkIfTimelineElementExists(boolean exists, TimelineElementCheck furtherChecks, TimelineElementCheckFilters filterParams) {
        try {
            if (exists) {
                assertSoftly(softly -> {
                    assertThat(pollingResponse.getResult())
                            .as("Il risultato del polling dovrebbe essere valorizzato. Primo controllo: Verificare che l'elemento sia presente in timeline e le tempistiche con cui viene prodotto")
                            .isTrue();
                    assertThat(pollingResponse.getTimelineElement())
                            .as("L'elemento della timeline non dovrebbe essere null")
                            .isNotNull();
                });
                timelineElement = pollingResponse.getTimelineElement();
                log.info("TIMELINE_ELEMENT: {}", pollingResponse.getTimelineElement());
                if (furtherChecks != null) {
                    performFurtherChecks(furtherChecks, filterParams);
                }
            } else {
                assertSoftly(softly -> {
                    assertThat(pollingResponse.getResult())
                            .as("Il risultato del polling dovrebbe essere false. Verificare la correttezza dello scenario di test e i dati passati in input\"")
                            .isFalse();
                    assertThat(pollingResponse.getTimelineElement())
                            .as("L'elemento di timeline dovrebbe essere null")
                            .isNull();
                });
                log.info("NOTIFICATION_TIMELINE: {}", pollingResponse.getNotification().getTimeline());
            }
        } catch (AssertionFailedError assertionFailedError) {
            b2bSteps.getSharedSteps().throwAssertionErrorWithIUN(assertionFailedError);
        }
    }


    @Override
    public void checkIfStatusExists(boolean exists) {
        try {
            assertSoftly(softly -> {
                assertThat(pollingResponse)
                        .as("La pollingResponse è null, verificare il corretto comportamento del waitForEvent")
                        .isNotNull();
                if (exists) {
                    assertThat(pollingResponse.getResult())
                            .as("Il risultato del polling deve essere valorizzato")
                            .isTrue();
                    assertThat(pollingResponse.getNotificationStatusHistoryElement())
                            .as("L'elemento dello storico degli stati non dovrebbe essere null")
                            .isNotNull();
                } else {
                    assertThat(pollingResponse.getResult())
                            .as("Il risultato del polling dovrebbe essere false. Verificare la correttezza dello scenario di test e i dati passati in input\"")
                            .isFalse();
                    assertThat(pollingResponse.getTimelineElement())
                            .as("L'elemento dello storico degli stati dovrebbe essere null")
                            .isNull();
                }
            });
            notificationStatusHistoryElement = pollingResponse.getNotificationStatusHistoryElement();
            log.info("NOTIFICATION_STATUS_HISTORY_ELEMENT: " + notificationStatusHistoryElement);
        } catch (AssertionFailedError assertionFailedError) {
            b2bSteps.getSharedSteps().throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    /**
     * Arrivati a questo punto del codice il timelineElement non è mai null
     * (viene verificato nel metodo che richiama quest'altro).
     */
    @Override
    public void performFurtherChecks(TimelineElementCheck furtherChecks, TimelineElementCheckFilters filterParams) {
        switch (furtherChecks) {
            case CHECK_RESPONSE_STATUS -> assertSoftly(softly -> {
                TimelineElementDetails details = Objects.requireNonNull(timelineElement.getDetails());
                assertThat(details.getResponseStatus())
                        .as("Il response status non dev'essere null")
                        .isNotNull();
                assertThat(details.getResponseStatus().getValue())
                        .as("Il response status non coincide con quanto atteso")
                        .isEqualTo(filterParams.getResponseStatus());

                if (filterParams.getDigitalAddressSource() != null) {
                    assertThat(details.getDigitalAddressSource().getValue())
                            .as("La digitalAddressSource non coincide con quanto atteso")
                            .isEqualTo(filterParams.getDigitalAddressSource());
                }
                if (filterParams.isWithDeliveryDetailCode()) {
                    assertThat(details.getDeliveryDetailCode())
                            .as("Il deliveryDetailCode non dev'essere null")
                            .isNotNull();
                }
                if (filterParams.isWithDeliveryFailureCause()) {
                    assertThat(details.getDeliveryFailureCause())
                            .as("La deliveryFailureCause non dev'essere null")
                            .isNotNull();
                }
            });
            case CHECK_MUNICIPALITY_AND_FOREIGN_STATE -> assertSoftly(softly -> {
                assertThat(Objects.requireNonNull(timelineElement.getDetails()).getPhysicalAddress().getMunicipality())
                        .as("Il campo municipality non dev'essere null")
                        .isNotNull();
                assertThat(timelineElement.getDetails().getPhysicalAddress().getForeignState())
                        .as("Il campo foreignState non dev'essere null")
                        .isNotNull();
            });
            case CHECK_PAYMENT_FROM_RECIPIENT_INDEX -> {
                if (Objects.requireNonNull(timelineElement.getDetails()).getRecIndex().equals(filterParams.getRecipientIndex())) {
                    assertThat(paymentFromRecipientFound(filterParams.getRecipientIndex()))
                            .as("L'evento di pagamento da parte del recipient " + filterParams.getRecipientIndex() + " non dev'essere null")
                            .isTrue();
                }
            }
            case CHECK_NOTIFICATION_DATE_DELAY -> {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                assertThat(Objects.requireNonNull(timelineElement.getDetails()).getNotificationDate().format(fmt))
                        .as("La notification date delay non coincide con quanto atteso")
                        .isEqualTo(now().plusDays(filterParams.getDelay()).format(fmt));
            }
            case CHECK_SCHEDULING_DATE_DELAY -> {
                OffsetDateTime digitalDeliveryCreationRequestDate = Objects.requireNonNull(timelineElement).getTimestamp();
                assertSoftly(softly -> {
                    assertThat(Objects.requireNonNull(timelineElement.getDetails()).getSchedulingDate())
                            .as("Il campo schedulingDate non dev'essere null")
                            .isNotNull();
                    assertThat(filterParams.getTipoIncremento()).as("Param tipoIncremento must not be null").isNotNull();
                    assertThat(filterParams.getDelay()).as("Param delay must not be null").isNotNull();
                    if ("d".equalsIgnoreCase(filterParams.getTipoIncremento())) {
                        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        assertThat(Objects.requireNonNull(digitalDeliveryCreationRequestDate).plusDays(filterParams.getDelay()).format(fmt))
                                .as("La scheduling date non coincide con quanto atteso")
                                .isEqualTo(timelineElement.getDetails().getSchedulingDate().format(fmt));
                    } else if ("m".equalsIgnoreCase(filterParams.getTipoIncremento())) {
                        DateTimeFormatter fmt1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                        assertThat(Objects.requireNonNull(digitalDeliveryCreationRequestDate).plusMinutes(filterParams.getDelay()).format(fmt1))
                                .as("La scheduling date non coincide con quanto atteso")
                                .isEqualTo(timelineElement.getDetails().getSchedulingDate().format(fmt1));
                    }
                });
            }
            case CHECK_ATTACHMENTS -> assertSoftly(softly -> {
                assertThat(Objects.requireNonNull(timelineElement.getDetails()).getAttachments())
                        .as("Il campo attachments non dev'essere null")
                        .isNotNull();
                assertThat(timelineElement.getDetails().getAttachments())
                        .as("Il campo attachments non dev'essere vuoto")
                        .isNotEmpty();
                assertThat(timelineElement.getDetails().getAttachments().get(0).getDocumentType())
                        .as("Il tipo di documento del primo attachment non dev'essere null")
                        .isNotNull();
                if (filterParams.isWithAttempt()) {
                    assertThat(timelineElement.getDetails().getAttachments().get(0).getDocumentType())
                            .as("Il documentType non coincide con quanto atteso")
                            .satisfiesAnyOf(
                                    actual -> assertThat(actual).isEqualTo(filterParams.getDocumentType()),
                                    actual -> assertThat(actual).isEqualTo("Indagine")
                            );
                } else {
                    assertThat(timelineElement.getDetails().getAttachments().get(0).getDocumentType())
                            .as("Il documentType non coincide con quanto atteso")
                            .isEqualTo(filterParams.getDocumentType());
                }
            });
            case CHECK_DELIVERY_FAILURE_CAUSE -> assertSoftly(softly -> {
                assertThat(Objects.requireNonNull(timelineElement.getDetails()).getDeliveryFailureCause())
                        .as("La delivery failure cause non coincide con quanto atteso")
                        .isEqualTo(filterParams.getDeliveryFailureCause());
            });
            case CHECK_SEND_REQUEST_ID -> assertSoftly(softly -> {
                assertThat(timelineElement.getDetails())
                        .as("I details del timelineElement non devono essere null")
                        .isNotNull();
                assertThat(timelineElement.getDetails().getSendRequestId())
                        .as("Il campo sendRequestId dei details non dev'essere null")
                        .isNotNull();
                String sendRequestId = timelineElement.getDetails().getSendRequestId();
                TimelineElement timelineElementRelative = pollingResponse
                        .getNotification()
                        .getTimeline()
                        .stream()
                        .filter(elem -> Objects.requireNonNull(elem.getElementId()).equals(sendRequestId))
                        .findAny()
                        .orElse(null);
                assertThat(timelineElementRelative)
                        .as("Il timelineElementRelative non dev'essere null")
                        .isNotNull();
            });
            case CHECK_SERVICE_LEVEL -> {
                ServiceLevel serviceLevel = switch (filterParams.getServiceLevel()) {
                    case "AR_REGISTERED_LETTER" -> ServiceLevel.AR_REGISTERED_LETTER;
                    case "REGISTERED_LETTER_890" -> ServiceLevel.REGISTERED_LETTER_890;
                    default ->
                            throw new IllegalArgumentException("Valore non riconosciuto per ServiceLevel: " + filterParams.getServiceLevel());
                };
                assertSoftly(softly -> {
                    assertThat(timelineElement.getDetails())
                            .as("I details del timelineElement non devono essere null")
                            .isNotNull();
                    assertThat(timelineElement.getDetails().getServiceLevel())
                            .as("Il service level non coincide con quanto atteso")
                            .isEqualTo(serviceLevel);
                });
            }
            case CHECK_ONLY_PAYMENTS_PAGOPA -> assertSoftly(softly -> {
                assertThat(Objects.requireNonNull(timelineElement.getDetails()).getIdF24())
                        .as("L'id F24 dei details del timelineElement dev'essere null")
                        .isNull();
            });
            case CHECK_DIGITAL_ADDRESS -> assertSoftly(softly -> {
                assertThat(Objects.requireNonNull(timelineElement.getDetails()).getDigitalAddress())
                        .as("Il digital address non dev'essere null")
                        .isNotNull();
                if (filterParams.isWithPlatformAddress()) {
                    assertThat(timelineElement.getDetails().getDigitalAddress().getAddress())
                            .as("L'indirizzo di piattaforma deve differire da quello del digital address")
                            .isNotEqualToIgnoringCase(filterParams.getPlatformAddress());
                }
            });
            case CHECK_PHYSICAL_ADDRESS -> assertSoftly(softly -> {
                assertThat(Objects.requireNonNull(timelineElement.getDetails()).getPhysicalAddress())
                        .as("Il physical address non dev'essere null")
                        .isNotNull();
                assertThat(timelineElement.getDetails().getPhysicalAddress().getAddress())
                        .as("Il campo physical address non rispetta la regex " + filterParams.getPhysicalAddressRegex())
                        .matches(filterParams.getPhysicalAddressRegex());
            });
            case CHECK_FAILURE_CAUSE -> {
                assumeThat(version.getValue())
                        .as("Test ignorato: la failureCause è stata introdotta dalla versione 2")
                        .isGreaterThan(1);
            }
            case CHECK_NOT_REFINED_RECIPIENT_INDEX -> {
                assumeThat(version.getValue())
                        .as("Test ignorato: il notRefinedRecipientIndexes è stata introdotto dalla versione 2")
                        .isGreaterThan(1);
            }
            case CHECK_SCHEDULING_DATE_RISPETTO_A_EVENTO -> {
                log.info("TIMELINE ELEMENT: {} , DETAILS {} , SCHEDULING DATE {}",
                        timelineElement, Objects.requireNonNull(timelineElement).getDetails(), Objects.requireNonNull(timelineElement.getDetails()).getSchedulingDate());
                long delayMillis = 0;
                OffsetDateTime digitalDeliveryCreationRequestDate = null;
                FullSentNotification fullSentNotification = getFullSentNotificationVersioned();
                for (TimelineElement element : fullSentNotification.getTimeline()) {
                    if (Objects.requireNonNull(element.getCategory()).getValue().equals("DIGITAL_DELIVERY_CREATION_REQUEST")
                            && Objects.requireNonNull(element.getDetails()).getRecIndex().equals(filterParams.getRecipientIndex())
                            && filterParams.getOtherEventCategory().equalsIgnoreCase("DIGITAL_DELIVERY_CREATION_REQUEST")) {
                        digitalDeliveryCreationRequestDate = element.getTimestamp();
                        delayMillis = b2bSteps.getSharedSteps().getSchedulingDaysFailureDigitalRefinement().toMillis();
                        break;
                    } else if (element.getCategory().getValue().equals("SEND_DIGITAL_FEEDBACK")
                            && Objects.requireNonNull(element.getDetails()).getRecIndex().equals(filterParams.getRecipientIndex())
                            && filterParams.getOtherEventCategory().equalsIgnoreCase("SEND_DIGITAL_FEEDBACK")) {
                        digitalDeliveryCreationRequestDate = element.getDetails().getNotificationDate();
                        delayMillis = "OK".equalsIgnoreCase(element.getDetails().getResponseStatus().getValue()) ?
                                b2bSteps.getSharedSteps().getSchedulingDaysSuccessDigitalRefinement().toMillis() :
                                b2bSteps.getSharedSteps().getSchedulingDaysFailureDigitalRefinement().toMillis();
                        break;
                    }
                }
                Long schedulingDateMillis = timelineElement.getDetails().getSchedulingDate().toInstant().toEpochMilli();
                Long digitalDeliveryCreationMillis = Objects.requireNonNull(digitalDeliveryCreationRequestDate).toInstant().toEpochMilli();
                long diff = schedulingDateMillis - digitalDeliveryCreationMillis;
                long delta = Long.valueOf(b2bSteps.getSharedSteps().getSchedulingDelta());
                log.info("PRE-ASSERTION: iun={} schedulingDateMillis={}, digitalDeliveryCreationMillis={}, diff={}, delayMillis={}, delta={}",
                        b2bSteps.getSharedSteps().getNotificationIun(), schedulingDateMillis, digitalDeliveryCreationMillis, diff, delayMillis, delta);
                assertThat(diff)
                        .as("le tempistiche di arrivo tra gli elementi cercati non sono corrette")
                        .isLessThanOrEqualTo(delayMillis + delta)
                        .isGreaterThanOrEqualTo(delayMillis - delta);
            }
            case CHECK_NOTIFICATION_COST_FOR_USER -> {
                Long notificationCost = timelineElement.getDetails().getNotificationCost();
                if (filterParams.getNotificationCost().equalsIgnoreCase("null")) {
                    assertThat(notificationCost)
                            .as("Il notificationCost dovrebbe essere null per la categoria '%s' e destinatario '%d'",
                                    filterParams.getTimelineEventCategory(),
                                    filterParams.getRecipientIndex())
                            .isNull();
                } else if (filterParams.getNotificationCost().equalsIgnoreCase("NotNull")) {
                    assertThat(notificationCost)
                            .as("Il notificationCost non dovrebbe essere null per la categoria '%s' e destinatario '%d'",
                                    filterParams.getTimelineEventCategory(),
                                    filterParams.getRecipientIndex())
                            .isNotNull();
                } else {
                    assertThat(notificationCost)
                            .as("Il notificationCost dovrebbe essere uguale a '%s' per la categoria '%s' e destinatario '%d'",
                                    filterParams.getNotificationCost(),
                                    filterParams.getTimelineEventCategory(),
                                    filterParams.getRecipientIndex())
                            .isEqualTo(Long.parseLong(filterParams.getNotificationCost()));
                }
            }
            case CHECK_NUMBER_OF_PAGES_AAR -> assertSoftly(softly -> {
                assertThat(Objects.requireNonNull(timelineElement.getDetails()).getNumberOfPages())
                        .as("Il numero di pagine non coincide con quanto atteso")
                        .isEqualTo(filterParams.getNumberOfPagesAAR());
            });
            case CHECK_FIELD_MATCHES_REGEX -> {
                try {
                    String fieldPath = filterParams.getFieldPath();
                    String fieldValue = getProperty(filterParams.getFieldPath(), timelineElement);
                    assertSoftly(softly -> {
                        assertThat(fieldValue)
                                .as("Field " + fieldPath + " has NULL value in timeline element")
                                .isNotNull();
                        assertThat(fieldValue)
                                .as("Field " + fieldPath + " with value " + fieldValue + " does not match regex " + filterParams.getFieldRegex())
                                .matches(filterParams.getFieldRegex());
                    });
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    b2bSteps.getSharedSteps().throwAssertionErrorWithIUN(
                            new AssertionFailedError("Error accessing field %s".formatted(filterParams.getFieldPath())));
                }
            }
            case CHECK_ATTESTAZIONI_OPPONIBILI -> {
                List<TimelineElement> timelineElementList = pollingResponse.getNotification()
                        .getTimeline()
                        .stream()
                        .filter(elem -> Objects.requireNonNull(elem.getCategory()).getValue().equals(NOTIFICATION_VIEWED))
                        .toList();
                assertSoftly(softly -> {
                    assertThat(timelineElementList)
                            .as("La lista di timeline element aventi categoria NOTIFICATION_VIEWED non dovrebbe essere null")
                            .isNotNull();
                    assertThat(timelineElementList.size())
                            .as("Il numero di attestazioni opponibili non coincide con quanto atteso")
                            .isEqualTo(filterParams.getNumberOfAttestazioniOpponibili());
                });

            }
        }
    }

    @Override
    public void searchCustomTimelineElementInTimeline(String eventId, String timelineEventCategory) {
        DataTestV1 dataTest = new DataTestV1();
        TimelineElement timelineElementExpected = new TimelineElement();
        TimelineElementDetails timelineElementDetails = new TimelineElementDetails();

        timelineElementDetails.setDeliveryDetailCode(eventId);
        timelineElementExpected.setDetails(timelineElementDetails);
        dataTest.setTimelineElement(timelineElementExpected);

        List<TimelineElement> timelineElementList = getTimelineElementsByEventId(timelineEventCategory, dataTest);
        timelineElement = timelineElementList.stream().findAny().orElse(null);
    }

    private boolean paymentFromRecipientFound(int recipientIndex) {
        NotificationPaymentInfo notificationPaymentInfo = pollingResponse.getNotification().getRecipients().get(recipientIndex).getPayment();
        return notificationPaymentInfo.getCreditorTaxId().equals(timelineElement.getDetails().getCreditorTaxId())
                && notificationPaymentInfo.getNoticeCode().equals(timelineElement.getDetails().getNoticeCode());
    }

    private PnPollingPredicate getPnPollingPredicateForTimeline(String timelineEventCategory, WaitForEventPredicateFilters filters) {
        PnPollingPredicate pnPollingPredicate = new PnPollingPredicate();
        if (filters.getStatusHistory() != null) {
            pnPollingPredicate.setNotificationStatusHistoryElementPredicateV1(statusHistory -> statusHistory.getStatus().getValue().equals(filters.getStatusHistory()));
        }
        pnPollingPredicate.setTimelineElementPredicateV1(timelineElement ->
                timelineElement.getCategory() != null
                        && (timelineEventCategory == null || Objects.requireNonNull(timelineElement.getCategory().getValue()).equals(timelineEventCategory))
                        && (filters.getRecipientIndex() == null || Objects.requireNonNull(Objects.requireNonNull(timelineElement.getDetails()).getRecIndex()).equals(filters.getRecipientIndex()))
                        && (filters.getDeliveryDetailCode() == null || Objects.equals(Objects.requireNonNull(timelineElement.getDetails()).getDeliveryDetailCode(), filters.getDeliveryDetailCode()))
                        && (filters.getAttempt() == null || Objects.requireNonNull(timelineElement.getElementId()).contains(filters.getAttempt()))
                        && (filters.getDocumentType() == null || Objects.equals(Objects.requireNonNull(Objects.requireNonNull(timelineElement.getDetails()).getAttachments()).get(0).getDocumentType(), filters.getDocumentType()))
                        && (filters.getResponseStatus() == null || Objects.requireNonNull(Objects.requireNonNull(timelineElement.getDetails()).getResponseStatus().getValue()).equals(filters.getResponseStatus()))
                        && (!filters.isF24() || Objects.requireNonNull(timelineElement.getDetails()).getIdF24() != null)
                        && (!filters.isLegalFactEmpty() || Objects.nonNull(timelineElement.getLegalFactsIds()) && !timelineElement.getLegalFactsIds().isEmpty())
                        && (filters.getLegalFactIdCategory() == null || Objects.requireNonNull(Objects.requireNonNull(timelineElement.getLegalFactsIds()).get(0)).getCategory().equals(filters.getLegalFactIdCategory()))
                        && (!filters.isAttachmentEmpty() || Objects.nonNull(Objects.requireNonNull(timelineElement.getDetails()).getAttachments()) && !timelineElement.getDetails().getAttachments().isEmpty())
                        && (filters.getLegalFactIdCategory() == null || filters.getFailureCauses().contains(Objects.requireNonNull(Objects.requireNonNull(timelineElement.getDetails()).getDeliveryFailureCause())))
        );
        return pnPollingPredicate;
    }

    @Override
    public void verificaAssenzaPagamentiF24() {
        try {
            Assertions.assertTrue(pollingResponse.getResult());
            Assertions.assertNull(pollingResponse.getTimelineElement());
        } catch (AssertionFailedError assertionFailedError) {
            b2bSteps.getSharedSteps().throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    private String getProperty(String fieldPath, TimelineElement lastTimelineElement)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String sanitizedFieldPath = fieldPath.replace("_", ".");
        return BeanUtils.getProperty(lastTimelineElement, sanitizedFieldPath);
    }
}
