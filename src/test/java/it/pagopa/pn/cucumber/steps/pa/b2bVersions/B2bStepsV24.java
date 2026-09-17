package it.pagopa.pn.cucumber.steps.pa.b2bVersions;

import io.cucumber.datatable.DataTable;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.polling.IPnPollingService;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingPredicate;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV26;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v24.NotificationHistoryResponse;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheB2bSteps;
import it.pagopa.pn.cucumber.steps.pa.notificationVersions.NotificationVersion;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.NotificationUtilsV24;
import it.pagopa.pn.cucumber.steps.utilitySteps.PollingType;
import it.pagopa.pn.cucumber.steps.utilitySteps.WaitForEventPredicateFilters;
import it.pagopa.pn.cucumber.steps.utilitySteps.checkTimelineElement.TimelineElementCheck;
import it.pagopa.pn.cucumber.steps.utilitySteps.checkTimelineElement.TimelineElementCheckFilters;
import it.pagopa.pn.cucumber.utils.datatestVersions.AbstractDataTest;
import it.pagopa.pn.cucumber.utils.datatestVersions.DataTestV24;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.IntStream;

import static it.pagopa.pn.client.b2b.pa.domain.Costanti.*;
import static it.pagopa.pn.cucumber.steps.utilitySteps.PollingType.STATUS;
import static it.pagopa.pn.cucumber.steps.utilitySteps.PollingType.TIMELINE;
import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.*;
import static java.util.Objects.nonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.awaitility.Awaitility.await;

@Slf4j
public class B2bStepsV24 implements B2bStepsInterface {
    private List<TimelineElementV26> timelineElementList;
    private TimelineElementV26 timelineElement;
    private NotificationStatusHistoryElementV26 notificationStatusHistoryElement;
    private PnPollingResponseV26 pollingResponse;
    private final AvanzamentoNotificheB2bSteps b2bSteps;
    private final SharedSteps sharedSteps;
    private final IPnPaB2bClient b2bClient;
    private final NotificationVersion version = NotificationVersion.V24;

    public B2bStepsV24(AvanzamentoNotificheB2bSteps b2bSteps) {
        this.b2bSteps = b2bSteps;
        sharedSteps = b2bSteps.getSharedSteps();
        b2bClient = sharedSteps.getB2bClient();
    }

    @Override
    public Object getFullSentNotification() {
        return b2bClient.getSentNotificationV26(sharedSteps.getNotificationIun());
    }

    private FullSentNotificationV26 getFullSentNotificationVersioned() {
        return (FullSentNotificationV26) getFullSentNotification();
    }

    @Override
    public void verifyTestCompatibilityWithVersion(String eventCategoryOrStatus, boolean isEventCategory) {
        if (isEventCategory) {
            List<String> categoriesForVersion = Arrays.stream(TimelineElementCategoryV26.values()).map(TimelineElementCategoryV26::getValue).toList();
            assumeThat(categoriesForVersion)
                    .as("Test skipped: TimelineElementCategory " + eventCategoryOrStatus + " non esiste per la versione " + TimelineElementCategoryV26.class)
                    .contains(eventCategoryOrStatus);
        } else {
            List<String> statusForVersion = Arrays.stream(NotificationStatusV26.values()).map(NotificationStatusV26::getValue).toList();
            assumeThat(statusForVersion)
                    .as("Test skipped: NotificationStatus " + eventCategoryOrStatus + " non esiste per la versione " + NotificationStatusV26.class)
                    .contains(eventCategoryOrStatus);
        }
    }

    @Override
    public void checkFullSentNotificationWithVersion(boolean isPresent, String timelineEventCategory) {
        FullSentNotificationV26 fullSentNotification = getFullSentNotificationVersioned();
        TimelineElementV26 timelineElement = fullSentNotification.getTimeline().stream().filter(
                te -> te.getCategory().getValue().equals(timelineEventCategory)).findAny().orElse(null);
        if (isPresent) {
            assertThat(timelineElement)
                    .as("Il controllo sulla fullSentNotification V26 dovrebbe restituire almeno un elemento")
                    .isNotNull();
        } else {
            assertThat(timelineElement)
                    .as("Il controllo sulla fullSentNotification V26 non dovrebbe restituire elementi")
                    .isNull();
        }
    }

    @Override
    public void checkFullSentNotificationRelatedElementWithVersion(String relatedTimelineElement) {
        FullSentNotificationV26 fullSentNotification = getFullSentNotificationVersioned();

        boolean found = fullSentNotification.getNotificationStatusHistory().stream()
                .filter(history -> history.getRelatedTimelineElements() != null)
                .flatMap(history -> history.getRelatedTimelineElements().stream())
                .anyMatch(element -> element.contains(relatedTimelineElement));

        assertThat(found)
                .as("Il controllo sulla fullSentNotification V26 non dovrebbe avere l'elemento tra i relatedTimelineElements che contenga: %s", relatedTimelineElement + ", IUN: " + sharedSteps.getNotificationIun())
                .isFalse();

    }

    @Override
    public void readEventsUpToTimelineElementFromDeliveryPush(String timelineEventCategory, AbstractDataTest dataTest, boolean existCheck) {
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
                    timelineElementList = getTimelineByDeliveryPush();
                    log.info("NOTIFICATION_TIMELINE: " + timelineElementList);
                    assertThat(timelineElementList).as("La timeline caricata da DeliveryPush non dev'essere null").isNotNull();
                    assertThat(timelineElementList).as("La timeline caricata da DeliveryPush non dev'essere vuota").isNotEmpty();
                    timelineElement = getTimelineElementByIdOrCategory(timelineEventCategory, (DataTestV24) dataTest, timelineElementList);
                    String expectedTimelineElement = B2bUtils.getExpectedTimelineElement(dataTest, timelineEventCategory);
                    if (existCheck) {
                        assertThat(timelineElement)
                                .as("La timeline caricata da DeliveryPush dovrebbe contenere un timelineElement di questo tipo\n" + expectedTimelineElement)
                                .isNotNull();
                    } else {
                        assertThat(timelineElement)
                                .as("La timeline caricata da DeliveryPush NON dovrebbe contenere un timelineElement di questo tipo\n" + expectedTimelineElement)
                                .isNull();
                    }
                });
    }

    @Override
    public void checkNotificationCost(String cost) {
        Long notificationCost = timelineElement.getDetails().getNotificationCost();
        if (cost.equalsIgnoreCase("null")) {
            //ignorare Sonar che dice che il risultato di questo assetNull fallirà sempre in quanto il campo è annotato con @NotNull (non è vero)
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
                PhysicalAddress normalizedAddress = timelineElement.getDetails().getNormalizedAddress();
                String testCase = "NormalizedAddress " + b2bSteps.mapValueFromTable(table, "testCase") + ": ";

                assertThat(b2bSteps.mapValueFromTable(table, "physicalAddress_address"))
                        .as(testCase + " il physical address non coincide col valore atteso")
                        .isEqualTo(normalizedAddress.getAddress());
                assertThat(b2bSteps.mapValueFromTable(table, "at"))
                        .as(testCase + " il campo at(presso) non coincide col valore atteso")
                        .isEqualTo(normalizedAddress.getAt());
                assertThat(b2bSteps.mapValueFromTable(table, "physicalAddress_addressDetails"))
                        .as(testCase + " addressDetails non coincide col valore atteso")
                        .isEqualTo(normalizedAddress.getAddressDetails());
                assertThat(b2bSteps.mapValueFromTable(table, "physicalAddress_zip"))
                        .as(testCase + " lo zipCode non coincide col valore atteso")
                        .isEqualTo(normalizedAddress.getZip());
                assertThat(b2bSteps.mapValueFromTable(table, "physicalAddress_municipality"))
                        .as(testCase + " la municipality non coincide col valore atteso")
                        .isEqualTo(normalizedAddress.getMunicipality());
                assertThat(b2bSteps.mapValueFromTable(table, "physicalAddress_municipalityDetails"))
                        .as(testCase + " i municipalityDetails non coincidono col valore atteso")
                        .isEqualTo(normalizedAddress.getMunicipalityDetails());
                assertThat(b2bSteps.mapValueFromTable(table, "physicalAddress_province"))
                        .as(testCase + " la provincia non coincide col valore atteso")
                        .isEqualTo(normalizedAddress.getProvince());
                assertThat(b2bSteps.mapValueFromTable(table, "physicalAddress_State"))
                        .as(testCase + " il physical address non coincide col valore atteso")
                        .isEqualTo(normalizedAddress.getForeignState());
            });
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
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
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @Override
    public void checkPriceForRecipient(int recipientIndex, String price) {
        String iun = sharedSteps.getNotificationIun();
        FullSentNotificationV26 fullSentNotification = getFullSentNotificationVersioned();
        List<NotificationPaymentItem> listNotificationPaymentItem = fullSentNotification.getRecipients().get(recipientIndex).getPayments();
        if (listNotificationPaymentItem != null) {
            for (NotificationPaymentItem notificationPaymentItem : listNotificationPaymentItem) {
                NotificationPriceResponse notificationPrice = b2bClient.getNotificationPrice(
                        notificationPaymentItem.getPagoPa().getCreditorTaxId(), notificationPaymentItem.getPagoPa().getNoticeCode());
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
                    sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
                }
            }
        }
    }

    @Override
    public void payAvvisoPagoPa(Integer recipientIndex, Integer paymentIndex) {
        FullSentNotificationV26 fullSentNotification = getFullSentNotificationVersioned();
        if (paymentIndex == null) {
            for (int i = 0; i < fullSentNotification.getRecipients().get(recipientIndex).getPayments().size(); i++) {
                payAvvisoPagoPa(recipientIndex, i);
            }
        } else {
            String creditorTaxId = fullSentNotification.getRecipients().get(recipientIndex).getPayments().get(paymentIndex).getPagoPa().getCreditorTaxId();
            String noticeCode = fullSentNotification.getRecipients().get(recipientIndex).getPayments().get(paymentIndex).getPagoPa().getNoticeCode();
            NotificationPriceResponseV23 notificationPrice = b2bClient.getNotificationPriceV23(creditorTaxId, noticeCode);

            PaymentEventsRequestPagoPa eventsRequestPagoPa = new PaymentEventsRequestPagoPa();

            PaymentEventPagoPa paymentEventPagoPa = new PaymentEventPagoPa();
            paymentEventPagoPa.setCreditorTaxId(creditorTaxId);
            paymentEventPagoPa.setNoticeCode(noticeCode);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            paymentEventPagoPa.setPaymentDate(fmt.format(now()));
            paymentEventPagoPa.setAmount(notificationPrice.getTotalPrice());
            List<PaymentEventPagoPa> paymentEventPagoPaList = new LinkedList<>();
            paymentEventPagoPaList.add(paymentEventPagoPa);
            eventsRequestPagoPa.setEvents(paymentEventPagoPaList);

            b2bClient.paymentEventsRequestPagoPa(eventsRequestPagoPa);
        }
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
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @Override
    public void checkIfLastAttemptMatchesIndex(int index) {
        try {
            List<TimelineElementV26> actualTimelineElements = getFullSentNotificationVersioned().getTimeline().stream()
                    .filter(elem -> nonNull(elem.getDetails()))
                    //ignorare Sonar che dice che questo nonNull è inutile in quanto sempre true, non è vero
                    .filter(elem -> nonNull(elem.getDetails().getSentAttemptMade()))
                    .filter(elem -> elem.getDetails().getSentAttemptMade() <= index)
                    .toList();
            List<Integer> actualAttemptsMade = actualTimelineElements.stream()
                    .map(TimelineElementV26::getDetails)
                    .filter(Objects::nonNull)
                    .map(TimelineElementDetailsV26::getSentAttemptMade)
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
            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
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
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @Override
    public void waitForEventOrStatus(String pollingStrategy, PollingType pollingType, String timelineEventCategory, WaitForEventPredicateFilters filters) {
        //FLUSSO NORMALE, CON CARICAMENTO DELLA TIMELINE DA B2B
        if (timelineEventCategory.equals(SEND_ANALOG_FEEDBACK)) {
            pollingStrategy = TIMELINE_SLOW;
        }
        String strategy = NotificationUtilsV24.getPollingStrategy(pollingStrategy);
        IPnPollingService<?> pollingService = sharedSteps.getPollingFactory().getPollingService(strategy);
        PnPollingPredicate pollingPredicate = getPnPollingPredicateForTimeline(timelineEventCategory, filters);
        pollingResponse = (PnPollingResponseV26) pollingService.waitForEvent(
                sharedSteps.getNotificationIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .pnPollingPredicate(pollingPredicate)
                        .build());
        switch (pollingType) {
            case TIMELINE -> {
                timelineElementList = pollingResponse.getNotification().getTimeline();
                log.info("NOTIFICATION_TIMELINE: " + timelineElementList);
            }
            case STATUS ->
                    log.info("NOTIFICATION_STATUS_HISTORY: " + pollingResponse.getNotification().getNotificationStatusHistory());
        }
    }

    @Override
    public void checkIfTimelineElementExists(String category, boolean exists, TimelineElementCheck furtherChecks, TimelineElementCheckFilters filterParams) {
        try {
            boolean result;
            //se siamo giunti a questo metodo dopo aver recuperato la timeline da B2B andiamo a valorizzare timelineElement e timelineElementList col risultato del polling
            if (pollingResponse != null) {
                result = pollingResponse.getResult();
                timelineElement = pollingResponse.getTimelineElement();
                timelineElementList = pollingResponse.getNotification().getTimeline();
            }
            //se invece siamo giunti a questo metodo dopo aver recuperato la timeline da delivery-push, timelineElement e timelineElementList sono già valorizzati
            else {
                result = exists;
            }
            if (exists) {
                assertSoftly(softly -> {
                    assertThat(result)
                            .as(logTimeline(null, category, true))
                            .isTrue();
                    assertThat(timelineElement)
                            .as("L'elemento della timeline non dovrebbe essere null")
                            .isNotNull();
                });
                log.info("TIMELINE_ELEMENT: {}", timelineElement);
                if (furtherChecks != null) {
                    performFurtherChecks(furtherChecks, filterParams);
                }
            } else {
                assertSoftly(softly -> {
                    assertThat(result)
                            .as(logTimeline(null, category, false))
                            .isFalse();
                    assertThat(timelineElement)
                            .as("L'elemento di timeline dovrebbe essere null")
                            .isNull();
                });
                log.info("NOTIFICATION_TIMELINE: {}", timelineElementList);
            }
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @Override
    public void checkIfTimelineElementExistsFromData(boolean exists, String timelineEventCategory, Map<String, String> dataMap) {
        verifyTestCompatibilityWithVersion(timelineEventCategory, true);
        try {
            DataTestV24 dataTest = DataTestV24.convertMap(dataMap);
            boolean mustLoadTimeline = dataTest != null && dataTest.isLoadTimeline();
            if (mustLoadTimeline) {
                loadTimeline(timelineEventCategory, exists, dataTest);
            }
            List<TimelineElementV26> timelineElements = getTimelineElementsByEventId(timelineEventCategory, dataTest);
            if (exists) {
                assertThat(timelineElements)
                        .as(logTimeline(dataTest, timelineEventCategory, true))
                        .isNotEmpty();
                if (dataTest != null && dataTest.getTimelineElement() != null) {
                    boolean atLeastOneSuccessful = false;
                    List<AssertionError> assertionErrorList = new LinkedList<>();
                    for (TimelineElementV26 te : timelineElements) {
                        try {
                            timelineElement = te;
                            log.info("TIMELINE_ELEMENT: " + te);
                            DataTestV24.checkTimelineElementEquality(timelineEventCategory, te, dataTest);
                            atLeastOneSuccessful = true;// se si arriva a questo punto, allora l'ultimo check ha avuto successo e non è necessario continuare
                            break;
                        } catch (AssertionError e) {
                            assertionErrorList.add(e);// se si arriva a questo punto allora l'ultimo check ha fallito e ci si prepara al prossimo
                        }
                    }
                    if (!atLeastOneSuccessful) {// se nessun confronto ha avuto successo allora di certo sarà stata lanciata un'eccezione
                        B2bUtils.logTimelineElementsThatDoNotMatchExpected(assertionErrorList, dataTest, timelineEventCategory);
                    }
                }
            } else {
                log.info("TIMELINE_ELEMENT LIST: " + timelineElements);
                assertThat(timelineElements)
                        .as(logTimeline(dataTest, timelineEventCategory, false))
                        .isEmpty();
            }
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    private String logTimeline(DataTestV24 dataTest, String timelineEventCategory, boolean exists) {
        boolean isWithEventId = dataTest != null && dataTest.getTimelineElement() != null;
        boolean hasCheckOnDeliveryDetailCode = dataTest != null && List.of(SEND_ANALOG_PROGRESS, SEND_SIMPLE_REGISTERED_LETTER_PROGRESS).contains(timelineEventCategory);
        String prefix = exists ? "Non è stato trovato nessun elemento con " : "La ricerca non avrebbe dovuto restituire nessun elemento con ";
        String expectedDdc = "";
        if (hasCheckOnDeliveryDetailCode) {
            expectedDdc = " e DeliveryDetailCode " + dataTest.getTimelineElement().getDetails().getDeliveryDetailCode();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(prefix)
                .append(isWithEventId ? "eventId contenente " : "category ")
                .append(isWithEventId ? dataTest.getTimelineEventId(timelineEventCategory, sharedSteps.getNotificationIun()) : timelineEventCategory)
                .append(hasCheckOnDeliveryDetailCode ? expectedDdc : "")
                .append("\nDi seguito la timeline completa:\n");
        timelineElementList.forEach(te -> {
            String actualDdc = "";
            if (te.getDetails() != null && te.getDetails().getDeliveryDetailCode() != null) {
                actualDdc = " DeliveryDetailCode: " + te.getDetails().getDeliveryDetailCode();
            }
            sb.append(te.getCategory())
                    .append(" EventId: ")
                    .append(te.getElementId())
                    .append(actualDdc)
                    .append("\n");
        });
        return sb.toString();
    }

    private void loadTimeline(String timelineEventCategory, boolean existCheck, DataTestV24 dataTest) {
        if (timelineEventCategory.equals(REQUEST_REFUSED) || dataTest.getLoadTimelineFrom().equals(LOAD_FROM_DELIVERY_PUSH)) {
            //GESTIONE LOAD TIMELINE E RECUPERO NOTIFICA CON CLIENT DI DELIVERY PUSH
            readEventsUpToTimelineElementFromDeliveryPush(timelineEventCategory, dataTest, existCheck);
        } else {
            //FLUSSO NORMALE, CON CARICAMENTO DELLA TIMELINE DA B2B
            waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, WaitForEventPredicateFilters.builder().build());
            timelineElementList = pollingResponse.getNotification().getTimeline();
            assertThat(timelineElementList).as("La timeline caricata da B2B non dev'essere null").isNotNull();
            assertThat(timelineElementList).as("La timeline caricata da B2B non dev'essere vuota").isNotEmpty();
            timelineElement = getTimelineElementByIdOrCategory(timelineEventCategory, dataTest, timelineElementList);
            String expectedTimelineElement = B2bUtils.getExpectedTimelineElement(dataTest, timelineEventCategory);
            if (existCheck) {
                assertThat(timelineElement)
                        .as("La timeline caricata da B2B dovrebbe contenere un timelineElement di questo tipo\n " + expectedTimelineElement)
                        .isNotNull();
            } else {
                assertThat(timelineElement)
                        .as("La timeline caricata da B2B NON dovrebbe contenere un timelineElement di questo tipo\n " + expectedTimelineElement)
                        .isNull();
            }
        }
    }

    private List<TimelineElementV26> getTimelineByDeliveryPush() {
        String iun = sharedSteps.getNotificationIun();
        int recipientsSize = sharedSteps.getRecipientsSize();
        OffsetDateTime creationDate = sharedSteps.getNotificationCreationDate();
        // get timeline from delivery-push
        NotificationHistoryResponse notificationHistory = b2bSteps.getPnPrivateDeliveryPushExternalClient().getNotificationHistoryV24(
                iun,
                recipientsSize,
                creationDate);
        return notificationHistory.getTimeline().stream().map(x -> sharedSteps.deepCopy(x, TimelineElementV26.class)).toList();
    }

    private TimelineElementV26 getTimelineElementByIdOrCategory(String timelineEventCategory, DataTestV24 dataFromTest, List<TimelineElementV26> timelineElementList) {
        // get timeline event id
        if (dataFromTest != null && dataFromTest.getTimelineElement() != null) {
            String timelineEventId = dataFromTest.getTimelineEventId(timelineEventCategory, sharedSteps.getNotificationIun());
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
    private List<TimelineElementV26> getTimelineElementsByEventId(String timelineEventCategory, DataTestV24 dataFromTest) {
        if (timelineElementList == null) {
            timelineElementList = getFullSentNotificationVersioned().getTimeline();
        }
        assertSoftly(softly -> {
            assertThat(timelineElementList).as("La timeline non dev'essere null").isNotNull();
            assertThat(timelineElementList).as("La timeline deve contenere almeno un elemento").isNotEmpty();
        });
        if (dataFromTest != null && dataFromTest.getTimelineElement() != null) {
            // get timeline event id
            String iun = sharedSteps.getNotificationIun();
            String timelineEventId = dataFromTest.getTimelineEventId(timelineEventCategory, iun);
            if (timelineEventCategory.equals(SEND_ANALOG_PROGRESS)
                    || timelineEventCategory.equals(SEND_SIMPLE_REGISTERED_LETTER_PROGRESS)) {
                TimelineElementV26 timelineElementFromTest = dataFromTest.getTimelineElement();
                TimelineElementDetailsV26 timelineElementDetails = timelineElementFromTest.getDetails();
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
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
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
                TimelineElementDetailsV26 details = Objects.requireNonNull(timelineElement.getDetails());
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
                TimelineElementV26 timelineElementRelative = pollingResponse
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
            case CHECK_FAILURE_CAUSE -> assertSoftly(softly -> {
                assertThat(Objects.requireNonNull(timelineElement.getDetails()).getFailureCause())
                        .as("Il campo failureCause non coincide con quanto atteso")
                        .isEqualTo(filterParams.getFailureCause());
            });
            case CHECK_NOT_REFINED_RECIPIENT_INDEX -> assertSoftly(softly -> {
                assertThat(Objects.requireNonNull(timelineElement.getDetails()).getNotRefinedRecipientIndexes())
                        .as("Il campo notRefinedRecipientIndexes non dev'essere null")
                        .isNotNull();
                assertThat(timelineElement.getDetails().getNotRefinedRecipientIndexes())
                        .as("Il campo notRefinedRecipientIndexes non dev'essere vuoto")
                        .isNotEmpty();
            });
            case CHECK_SCHEDULING_DATE_RISPETTO_A_EVENTO -> {
                log.info("TIMELINE ELEMENT: {} , DETAILS {} , SCHEDULING DATE {}",
                        timelineElement, Objects.requireNonNull(timelineElement).getDetails(), Objects.requireNonNull(timelineElement.getDetails()).getSchedulingDate());
                long delayMillis = 0;
                OffsetDateTime digitalDeliveryCreationRequestDate = null;
                FullSentNotificationV26 fullSentNotification = getFullSentNotificationVersioned();
                for (TimelineElementV26 element : fullSentNotification.getTimeline()) {
                    if (Objects.requireNonNull(element.getCategory()).getValue().equals("DIGITAL_DELIVERY_CREATION_REQUEST")
                            && Objects.requireNonNull(element.getDetails()).getRecIndex().equals(filterParams.getRecipientIndex())
                            && filterParams.getOtherEventCategory().equalsIgnoreCase("DIGITAL_DELIVERY_CREATION_REQUEST")) {
                        digitalDeliveryCreationRequestDate = element.getTimestamp();
                        delayMillis = sharedSteps.getSchedulingDaysFailureDigitalRefinement().toMillis();
                        break;
                    } else if (element.getCategory().getValue().equals("SEND_DIGITAL_FEEDBACK")
                            && Objects.requireNonNull(element.getDetails()).getRecIndex().equals(filterParams.getRecipientIndex())
                            && filterParams.getOtherEventCategory().equalsIgnoreCase("SEND_DIGITAL_FEEDBACK")) {
                        digitalDeliveryCreationRequestDate = element.getDetails().getNotificationDate();
                        delayMillis = "OK".equalsIgnoreCase(element.getDetails().getResponseStatus().getValue()) ?
                                sharedSteps.getSchedulingDaysSuccessDigitalRefinement().toMillis() :
                                sharedSteps.getSchedulingDaysFailureDigitalRefinement().toMillis();
                        break;
                    }
                }
                Long schedulingDateMillis = timelineElement.getDetails().getSchedulingDate().toInstant().toEpochMilli();
                Long digitalDeliveryCreationMillis = Objects.requireNonNull(digitalDeliveryCreationRequestDate).toInstant().toEpochMilli();
                long diff = schedulingDateMillis - digitalDeliveryCreationMillis;
                long delta = Long.valueOf(sharedSteps.getSchedulingDelta());
                log.info("PRE-ASSERTION: iun={} schedulingDateMillis={}, digitalDeliveryCreationMillis={}, diff={}, delayMillis={}, delta={}",
                        sharedSteps.getNotificationIun(), schedulingDateMillis, digitalDeliveryCreationMillis, diff, delayMillis, delta);
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
                    sharedSteps.throwAssertionErrorWithIUN(
                            new AssertionFailedError("Error accessing field %s".formatted(filterParams.getFieldPath())));
                }
            }
            case CHECK_ATTESTAZIONI_OPPONIBILI -> {
                List<TimelineElementV26> timelineElementList = pollingResponse.getNotification()
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

    private boolean paymentFromRecipientFound(int recipientIndex) {
        NotificationPaymentItem notificationPaymentItem = null;
        if (pollingResponse.getNotification().getRecipients().get(recipientIndex).getPayments() != null) {
            notificationPaymentItem = pollingResponse
                    .getNotification()
                    .getRecipients()
                    .get(recipientIndex)
                    .getPayments()
                    .stream()
                    .filter(pay -> Objects.requireNonNull(pay.getPagoPa()).getCreditorTaxId().equals(timelineElement.getDetails().getCreditorTaxId())
                            && pay.getPagoPa().getNoticeCode().equals(timelineElement.getDetails().getNoticeCode()))
                    .findAny()
                    .orElse(null);
        }
        return notificationPaymentItem != null;
    }

    private PnPollingPredicate getPnPollingPredicateForTimeline(String timelineEventCategory, WaitForEventPredicateFilters filters) {
        PnPollingPredicate pnPollingPredicate = new PnPollingPredicate();
        if (filters.getStatusHistory() != null) {
            pnPollingPredicate.setNotificationStatusHistoryElementPredicateV26(statusHistory -> statusHistory.getStatus().getValue().equals(filters.getStatusHistory()));
        }
        pnPollingPredicate.setTimelineElementPredicateV26(timelineElement ->
                timelineElement.getCategory() != null
                        && (timelineEventCategory == null || Objects.requireNonNull(timelineElement.getCategory().getValue()).equals(timelineEventCategory))
                        && (filters.getRecipientIndex() == null || Objects.requireNonNull(Objects.requireNonNull(timelineElement.getDetails()).getRecIndex()).equals(filters.getRecipientIndex()))
                        && (filters.getDeliveryDetailCode() == null || Objects.equals(Objects.requireNonNull(timelineElement.getDetails()).getDeliveryDetailCode(), filters.getDeliveryDetailCode()))
                        && (filters.getWithElementIdSuffix() == null || Objects.requireNonNull(timelineElement.getElementId()).contains(filters.getWithElementIdSuffix()))
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
    public void searchCustomTimelineElementInTimeline(String eventId, String timelineEventCategory) {
        DataTestV24 dataTest = new DataTestV24();
        TimelineElementV26 timelineElementExpected = new TimelineElementV26();
        TimelineElementDetailsV26 timelineElementDetails = new TimelineElementDetailsV26();

        timelineElementDetails.setDeliveryDetailCode(eventId);
        timelineElementExpected.setDetails(timelineElementDetails);
        dataTest.setTimelineElement(timelineElementExpected);

        List<TimelineElementV26> timelineElementList = getTimelineElementsByEventId(timelineEventCategory, dataTest);
        timelineElement = timelineElementList.stream().findAny().orElse(null);
    }

    @Override
    public void verificaAssenzaPagamentiF24() {
        Assertions.assertTrue(pollingResponse.getResult());
        Assertions.assertNull(pollingResponse.getTimelineElement());
    }

    @Override
    public void checkNumberOfTimelineElements(String timelineEventCategory, Integer size) {
        int actualNumber = (int) getFullSentNotificationVersioned().getTimeline().stream().filter(x ->
                x.getCategory().getValue().equals(timelineEventCategory)).count();
        assertThat(actualNumber)
                .as("Il numero di elementi di timeline con categoria " + timelineEventCategory + " non coincide con quanto atteso")
                .isEqualTo(size);
    }

    @Override
    public void checkNumberOfTimelineElementsFromData(String timelineEventCategory, Integer size, Map<String, String> dataMap) {

        DataTestV24 dataTest = DataTestV24.convertMap(dataMap);
        String iun = sharedSteps.getNotificationIun();
        FullSentNotificationV26 fullSentNotification = getFullSentNotificationVersioned();
        List<TimelineElementV26> timelineElementList = fullSentNotification.getTimeline();
        String timelineEventId = dataTest.getTimelineEventId(timelineEventCategory, iun);
        int actualNumber;

        if (timelineEventCategory.equals(SEND_ANALOG_PROGRESS)) {
            TimelineElementDetailsV26 timelineElementDetails = dataTest.getTimelineElement().getDetails();
            actualNumber = (int) timelineElementList.stream().filter(x ->
                    x.getElementId().startsWith(timelineEventId)
                            && x.getDetails().getDeliveryDetailCode().equals(timelineElementDetails.getDeliveryDetailCode())).count();
        } else {
            actualNumber = (int) timelineElementList.stream().filter(x -> x.getElementId().startsWith(timelineEventId)).count();
        }
        assertThat(actualNumber)
                .as("Il numero di elementi di timeline che corrispondono al dato passato in input non coincide con quanto atteso: \n " + dataTest.getTimelineElement())
                .isEqualTo(size);
    }

    @Override
    public void waitForScheduleRefinement(Map<String, String> dataMap) throws InterruptedException {
        DataTestV24 dataTest = DataTestV24.convertMap(dataMap);
        timelineElement = getTimelineElementsByEventId(SCHEDULE_REFINEMENT, dataTest).stream().findAny().orElse(null);
        OffsetDateTime schedulingDate = timelineElement.getDetails().getSchedulingDate();
        OffsetDateTime currentDate = now().atZoneSameInstant(ZoneId.of("UTC")).toOffsetDateTime();
        long remainingTime = ChronoUnit.MILLIS.between(currentDate, schedulingDate);
        if (remainingTime > 0) {
            Thread.sleep(remainingTime + 30 * 1000);
        }
        assertThat(getFullSentNotificationVersioned()).as("La fullSentNotification non dev'essere null").isNotNull();
    }

    @Override
    public void waitForSecondAttempt(String timelineEventCategory, Map<String, String> dataMap) throws InterruptedException {
        DataTestV24 dataTest = DataTestV24.convertMap(dataMap);
        timelineElement = getTimelineElementsByEventId(timelineEventCategory, dataTest).stream().findAny().orElse(null);
        OffsetDateTime firstSend = timelineElement.getTimestamp();
        Duration secondNotificationWorkflowWaitingTime = sharedSteps.getSecondNotificationWorkflowWaitingTime();
        OffsetDateTime nextSend = firstSend.plus(secondNotificationWorkflowWaitingTime);
        OffsetDateTime currentDate = now().atZoneSameInstant(ZoneId.of("UTC")).toOffsetDateTime();
        long remainingTime = ChronoUnit.MILLIS.between(currentDate, nextSend);
        if (remainingTime > 0) {
            Thread.sleep(remainingTime + 30 * 1000);
        }
        assertThat(getFullSentNotificationVersioned()).as("La fullSentNotification non dev'essere null").isNotNull();
    }

    @Override
    public void checkOrdineEventiUnivoci(String category1, Boolean isSuccessivo, String category2) {
        FullSentNotificationV26 fullSentNotification = getFullSentNotificationVersioned();
        TimelineElementV26 t1 = fullSentNotification.getTimeline().stream().filter(t -> t.getCategory() != null && t.getCategory().getValue().equals(category1)).findFirst().orElse(null);
        TimelineElementV26 t2 = fullSentNotification.getTimeline().stream().filter(t -> t.getCategory() != null && t.getCategory().getValue().equals(category2)).findFirst().orElse(null);

        OffsetDateTime timestamp1 = t1.getEventTimestamp().truncatedTo(MINUTES);
        OffsetDateTime timestamp2 = t2.getEventTimestamp().truncatedTo(MINUTES);

        OffsetDateTime expectedDate = !category2.equals(SEND_COURTESY_MESSAGE) ? timestamp2 : timestamp2.plus(sharedSteps.getWaitingForReadCourtesyMessage());

        if (isSuccessivo == null) {
            assertThat(timestamp1).as("Il timestamp dell'evento " + category1 + " dev'essere uguale a quello dell'evento " + category2).isEqualTo(expectedDate);
        } else {
            if (isSuccessivo) {
                assertThat(timestamp1).as("Il timestamp dell'evento " + category1 + " dev'essere successivo a quello dell'evento " + category2).isAfter(expectedDate);
            } else {
                assertThat(timestamp1).as("Il timestamp dell'evento " + category1 + " dev'essere precedente a quello dell'evento " + category2).isBefore(expectedDate);
            }
        }
    }

    @Override
    public void vieneSchedulatoIlPerfezionamento(String timelineEventCategory, Map<String, String> dataMap) {
        DataTestV24 dataTest = DataTestV24.convertMap(dataMap);
        timelineElement = getTimelineElementsByEventId(SCHEDULE_REFINEMENT, dataTest).stream().findAny().orElse(null);

        String timelineElementForDateCalculationCategory = switch (timelineEventCategory) {
            case DIGITAL_SUCCESS_WORKFLOW -> SEND_DIGITAL_FEEDBACK;
            case DIGITAL_FAILURE_WORKFLOW -> DIGITAL_DELIVERY_CREATION_REQUEST;
            case ANALOG_SUCCESS_WORKFLOW, ANALOG_FAILURE_WORKFLOW -> SEND_ANALOG_FEEDBACK;
            default ->
                    throw new IllegalArgumentException("Category dalla quale calcolare il perfezionamento non valida");
        };
        TimelineElementV26 timelineElementForDateCalculation = getTimelineElementsByEventId(timelineElementForDateCalculationCategory, dataTest)
                .stream().findAny().orElse(null);
        assertThat(timelineElementForDateCalculation)
                .as("L'elemento di timeline " +
                        timelineElementForDateCalculationCategory +
                        " da cui viene calcolato il perfezionamento in caso di " +
                        timelineEventCategory +
                        " non può essere null")
                .isNotNull();

        OffsetDateTime notificationDate = null;
        Duration schedulingDaysRefinement = null;

        switch (timelineEventCategory) {
            case DIGITAL_SUCCESS_WORKFLOW -> {
                notificationDate = timelineElementForDateCalculation.getDetails().getNotificationDate();
                schedulingDaysRefinement = sharedSteps.getSchedulingDaysSuccessDigitalRefinement();
            }
            case DIGITAL_FAILURE_WORKFLOW -> {
                notificationDate = timelineElementForDateCalculation.getEventTimestamp();
                schedulingDaysRefinement = sharedSteps.getSchedulingDaysFailureDigitalRefinement();
            }
            case ANALOG_SUCCESS_WORKFLOW -> {
                notificationDate = timelineElementForDateCalculation.getEventTimestamp();
                schedulingDaysRefinement = sharedSteps.getSchedulingDaysSuccessAnalogRefinement();
            }
            case ANALOG_FAILURE_WORKFLOW -> {
                notificationDate = timelineElementForDateCalculation.getDetails().getNotificationDate();
                schedulingDaysRefinement = sharedSteps.getSchedulingDaysFailureAnalogRefinement();
            }
        }

        OffsetDateTime schedulingDate = notificationDate.plus(schedulingDaysRefinement);
        int hour = schedulingDate.getHour();
        int minutes = schedulingDate.getMinute();
        if ((hour == 21 && minutes > 0) || hour > 21) {
            Duration timeToAddInNonVisibilityTimeCase = sharedSteps.getTimeToAddInNonVisibilityTimeCase();
            schedulingDate = schedulingDate.plus(timeToAddInNonVisibilityTimeCase);
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        log.info("SCHEDULE_REFINEMENT Original   date: {}", timelineElement.getDetails().getSchedulingDate().format(dtf));
        log.info("SCHEDULE_REFINEMENT Calculated date: {}", schedulingDate.format(dtf));

        OffsetDateTime expectedDate = timelineElement.getDetails().getSchedulingDate();
        assertThat(schedulingDate)
                .as("La schedulingDate dev'essere distante massimo 5 minuti dall'expected")
                .isCloseTo(expectedDate, within(5, MINUTES));
    }

    @Override
    public void checkScartoTemporaleTraDueDeliveryDetailCode(String code1, String code2, Boolean isSuperiore, int timeQuantity, ChronoUnit unitaTemporale) {
        FullSentNotificationV26 fullSentNotification = getFullSentNotificationVersioned();
        TimelineElementV26 t1 = fullSentNotification.getTimeline().stream().filter(t ->
                        t.getDetails() != null
                                && t.getDetails().getDeliveryDetailCode() != null
                                && t.getDetails().getDeliveryDetailCode().equals(code1))
                .findFirst()
                .orElse(null);
        TimelineElementV26 t2 = fullSentNotification.getTimeline().stream().filter(t ->
                        t.getDetails() != null
                                && t.getDetails().getDeliveryDetailCode() != null
                                && t.getDetails().getDeliveryDetailCode().equals(code2))
                .findFirst()
                .orElse(null);

        OffsetDateTime date1 = t1.getEventTimestamp();
        OffsetDateTime date2 = t2.getEventTimestamp();

        OffsetDateTime expectedDate =
                unitaTemporale == DAYS ? date1.plusDays(timeQuantity) :
                        unitaTemporale == HOURS ? date1.plusHours(timeQuantity) :
                                unitaTemporale == MINUTES ? date1.plusMinutes(timeQuantity) :
                                        date1.plusSeconds(timeQuantity);
        if (isSuperiore == null) {
            assertThat(date2)
                    .as("La data di " + code2 + " non è pari a quella di " + code1)
                    .isEqualTo(expectedDate);
        } else {
            if (isSuperiore) {
                assertThat(date2)
                        .as("La data di " + code2 + " non è successiva a quella di " + code1)
                        .isAfterOrEqualTo(expectedDate);
            } else {
                assertThat(date2)
                        .as("La data di " + code2 + " non è antecedente a quella di " + code1)
                        .isBefore(expectedDate);
            }
        }
    }

    @Override
    public void checkReworkTimelineWithVersion() {
        FullSentNotificationV26 fullSentNotification = getFullSentNotificationVersioned();
        List<TimelineElementV26> timeline = fullSentNotification.getTimeline();

        TimelineElementV26 reworkedElement = timeline.stream().filter(te -> te.getElementId().contains("REWORK_")).findFirst().orElse(null);
        assertThat(reworkedElement).as("La fullSentNotification V26 dovrebbe contenere almeno un elemento con REWORK_ nel timelineElementId").isNotNull();
    }

    private String getProperty(String fieldPath, TimelineElementV26 lastTimelineElement) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String sanitizedFieldPath = fieldPath.replace("_", ".");
        return BeanUtils.getProperty(lastTimelineElement, sanitizedFieldPath);
    }
}
