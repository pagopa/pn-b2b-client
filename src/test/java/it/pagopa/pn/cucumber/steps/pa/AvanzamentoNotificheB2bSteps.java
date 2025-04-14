package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingFactory;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV26;
import it.pagopa.pn.client.b2b.pa.polling.impl.v26.PnPollingServiceTimelineRapidV26;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.service.IPnPrivateDeliveryPushExternalClient;
import it.pagopa.pn.client.b2b.pa.service.impl.PnExternalServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.NotificationProcessCostResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.ResponsePaperNotificationFailedDto;
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

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;

import static it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.NotificationFeePolicy.DELIVERY_MODE;
import static it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.NotificationFeePolicy.FLAT_RATE;
import static it.pagopa.pn.cucumber.steps.utilitySteps.Costanti.*;
import static it.pagopa.pn.cucumber.steps.utilitySteps.PollingType.TIMELINE;
import static it.pagopa.pn.cucumber.steps.utilitySteps.checkTimelineElement.TimelineElementCheck.*;
import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

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

    private final Map<NotificationVersion, B2bStepsInterface> mapOfVersionSteps = NotificationVersion.getMapOfB2bSteps(this);

    @Autowired
    public AvanzamentoNotificheB2bSteps(SharedSteps sharedSteps,
                                        TimingForPolling timingForPolling,
                                        IPnPrivateDeliveryPushExternalClient pnPrivateDeliveryPushExternalClient) {
        this.sharedSteps = sharedSteps;
        this.timingForPolling = timingForPolling;
        this.pnPrivateDeliveryPushExternalClient = pnPrivateDeliveryPushExternalClient;

        this.externalClient = sharedSteps.getPnExternalServiceClient();
        this.b2bClient = sharedSteps.getB2bClient();
        this.pnPollingFactory = sharedSteps.getPollingFactory();
    }

    private B2bStepsInterface getB2bStepsInterface() {
        NotificationVersion notificationVersion = sharedSteps.getVersionUsed() == null ?
                sharedSteps.getNotificationVersion(MOST_RECENT) : sharedSteps.getVersionUsed();
        return getB2bStepsInterface(notificationVersion);
    }

    private B2bStepsInterface getB2bStepsInterface(NotificationVersion notificationVersion) {
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

//    @Then("vengono letti gli eventi fino allo stato della notifica {string} V1")
//    public void readingEventUpToTheStatusOfNotificationV1(String status) {
//        readEventsUpToStatus(NotificationVersion.V1, status, true);
//    }

    @Then("vengono letti gli eventi fino allo stato della notifica {string} per il destinatario {int} e presente l'evento {string}")
    public void readingEventUpToTheStatusOfNotification(String status, int recIndex, String evento) {
        readEventsUpToStatus(sharedSteps.getVersionUsed(), status, true);
        getB2bStepsInterface().checkEventPresenceForRecipient(recIndex, evento);
    }

    @Then("recuperando la fullSentNotification con la versione b2b {string} {isPresent} l'elemento di timeline {string}")
    public void checkPresenceOfTimelineElement(String version, boolean isPresent, String timelineEventCategory) {
        NotificationVersion notificationVersion = NotificationVersion.valueOf(version);
        getB2bStepsInterface(notificationVersion).checkFullSentNotificationWithVersion(isPresent, timelineEventCategory);
    }

    // Due soli step richiamavano questo metodo, non serve più, useranno quello di sotto (universale), che
    // era inutilizzato, mentre ora ha un'utilità generica (qualsiasi stato), sia che l'abbia o meno

//    @Then("si verifica che la notifica {has} lo stato VIEWED")
//    public void checksNotificationViewedStatus(boolean has) {
//        checksIfNotificationHasStatus(has, NOTIFICATION_STATUS_VIEWED);
//        String status = NotificationStatus.VIEWED.getValue();
//        PnPollingServiceStatusRapidV26 statusRapidV25 = (PnPollingServiceStatusRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.STATUS_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = statusRapidV25.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(status)
//                        .build());
//        log.info("NOTIFICATION: " + pnPollingResponseV26.getNotification());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getNotificationStatusHistoryElement());
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
//    }

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

//        PnPollingServiceStatusRapidV26 statusRapidV25 = (PnPollingServiceStatusRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.STATUS_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = statusRapidV25.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(status)
//                        .build());
//        log.info("NOTIFICATION: " + pnPollingResponseV26.getNotification());
//        try {
//            Assertions.assertFalse(pnPollingResponseV26.getResult());
//            Assertions.assertNull(pnPollingResponseV26.getNotificationStatusHistoryElement());
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    private void readEventsUpToStatus(NotificationVersion notificationVersion, String status, boolean exists) {
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface(notificationVersion);
        b2bStepsInterface.readEventsUpToStatus(status, exists);
    }

//    private void checkTimelineElementEquality(String timelineEventCategory, TimelineElementV26 elementFromNotification, DataTest dataFromTest) {
//        TimelineElementV26 elementFromTest = dataFromTest.getTimelineElement();
//        TimelineElementDetailsV26 detailsFromNotification = elementFromNotification.getDetails();
//        TimelineElementDetailsV26 detailsFromTest = elementFromTest.getDetails();
//        DelegateInfo delegateInfoFromTest = detailsFromTest != null ? detailsFromTest.getDelegateInfo() : null;
//        DelegateInfo delegateInfoFromNotification = detailsFromNotification != null ? detailsFromNotification.getDelegateInfo() : null;
//
//        switch (timelineEventCategory) {
//            case SEND_COURTESY_MESSAGE -> {
//                if (detailsFromTest != null) {
//                    Assertions.assertEquals(detailsFromNotification.getDigitalAddress(), detailsFromTest.getDigitalAddress());
//                    Assertions.assertEquals(detailsFromNotification.getRecIndex(), detailsFromTest.getRecIndex());
//                }
//            }
//            case REQUEST_REFUSED -> {
//                if (detailsFromTest != null) {
//                    Assertions.assertNotNull(detailsFromNotification.getRefusalReasons());
//                    Assertions.assertEquals(detailsFromNotification.getRefusalReasons().size(), detailsFromTest.getRefusalReasons().size());
//                    for (int i = 0; i < detailsFromNotification.getRefusalReasons().size(); i++) {
//                        Assertions.assertEquals(detailsFromNotification.getRefusalReasons().get(i).getErrorCode(), detailsFromTest.getRefusalReasons().get(i).getErrorCode());
//                    }
//                }
//            }
//            case AAR_GENERATION -> {
//                if (detailsFromTest != null) {
//                    Assertions.assertNotNull(detailsFromNotification.getGeneratedAarUrl());
//                }
//            }
//            case SEND_DIGITAL_FEEDBACK -> {
//                if (detailsFromTest != null) {
//                    Assertions.assertNotNull(detailsFromNotification.getResponseStatus());
//                    Assertions.assertEquals(detailsFromNotification.getResponseStatus().getValue(), detailsFromTest.getResponseStatus().getValue());
//                    Assertions.assertEquals(detailsFromNotification.getDigitalAddress(), detailsFromTest.getDigitalAddress());
//                    Assertions.assertEquals(detailsFromNotification.getSendingReceipts().size(), detailsFromTest.getSendingReceipts().size());
//                    for (int i = 0; i < detailsFromNotification.getSendingReceipts().size(); i++) {
//                        Assertions.assertEquals(detailsFromNotification.getSendingReceipts().get(i), detailsFromTest.getSendingReceipts().get(i));
//                    }
//                }
//            }
//            case REQUEST_ACCEPTED -> {
//                Assertions.assertNotNull(elementFromNotification.getLegalFactsIds());
//                Assertions.assertEquals(elementFromNotification.getLegalFactsIds().size(), elementFromTest.getLegalFactsIds().size());
//                for (int i = 0; i < elementFromNotification.getLegalFactsIds().size(); i++) {
//                    Assertions.assertEquals(elementFromNotification.getLegalFactsIds().get(i).getCategory(), elementFromTest.getLegalFactsIds().get(i).getCategory());
//                    Assertions.assertNotNull(elementFromNotification.getLegalFactsIds().get(i).getKey());
//                }
//            }
//            case SEND_DIGITAL_DOMICILE -> {
//                if (detailsFromTest != null) {
//                    Assertions.assertEquals(detailsFromNotification.getDigitalAddress(), detailsFromTest.getDigitalAddress());
//                }
//            }
//            case DIGITAL_SUCCESS_WORKFLOW, DIGITAL_FAILURE_WORKFLOW -> {
//                Assertions.assertNotNull(elementFromNotification.getLegalFactsIds());
//                Assertions.assertEquals(elementFromNotification.getLegalFactsIds().size(), elementFromTest.getLegalFactsIds().size());
//                for (int i = 0; i < elementFromNotification.getLegalFactsIds().size(); i++) {
//                    Assertions.assertEquals(elementFromNotification.getLegalFactsIds().get(i).getCategory(), elementFromTest.getLegalFactsIds().get(i).getCategory());
//                    Assertions.assertNotNull(elementFromNotification.getLegalFactsIds().get(i).getKey());
//                }
//                if (detailsFromTest != null) {
//                    Assertions.assertEquals(detailsFromNotification.getDigitalAddress(), detailsFromTest.getDigitalAddress());
//                }
//            }
//            case GET_ADDRESS -> {
//                if (detailsFromTest != null) {
//                    Assertions.assertEquals(detailsFromNotification.getDigitalAddressSource(), detailsFromTest.getDigitalAddressSource());
//                    Assertions.assertEquals(detailsFromNotification.getIsAvailable(), detailsFromTest.getIsAvailable());
//                }
//            }
//            case SEND_ANALOG_FEEDBACK -> {
//                if (detailsFromTest != null) {
//                    if (detailsFromTest.getDeliveryDetailCode() != null) {
//                        Assertions.assertEquals(detailsFromTest.getDeliveryDetailCode(), detailsFromNotification.getDeliveryDetailCode());
//                    }
//                    //TODO: ignorare i commenti di Sonar che dice che questa condizione è sempre true (in quanto il campo è annotato con @NotNull)
//                    // A causa di questo suggerimento errato, in precedenza era stato rimosso l'if, causando il fail di alcuni test
//                    if (detailsFromTest.getPhysicalAddress() != null) {
//                        Assertions.assertEquals(detailsFromTest.getPhysicalAddress(), detailsFromNotification.getPhysicalAddress());
//                    }
//                    //TODO: ignorare i commenti di Sonar che dice che questa condizione è sempre true (in quanto il campo è annotato con @NotNull)
//                    // A causa di questo suggerimento errato, in precedenza era stato rimosso l'if, causando il fail di alcuni test
//                    if (detailsFromTest.getResponseStatus() != null && detailsFromTest.getResponseStatus().getValue() != null) {
//                        Assertions.assertEquals(detailsFromTest.getResponseStatus().getValue(), detailsFromNotification.getResponseStatus().getValue());
//                    }
//                    if (detailsFromTest.getDeliveryFailureCause() != null) {
//                        List<String> failureCauses = Arrays.asList(detailsFromTest.getDeliveryFailureCause().split(" "));
//                        Assertions.assertTrue(failureCauses.contains(elementFromNotification.getDetails().getDeliveryFailureCause()), "DeliveryFailureCause not match. IUN: " + sharedSteps.getNotificationIun());
//                    }
//                }
//            }
//            case SEND_ANALOG_PROGRESS, SEND_SIMPLE_REGISTERED_LETTER_PROGRESS -> {
//                if (detailsFromTest != null) {
//                    if (Objects.nonNull(elementFromTest.getLegalFactsIds())) {
//                        Assertions.assertEquals(elementFromNotification.getLegalFactsIds().size(), elementFromTest.getLegalFactsIds().size());
//                        for (int i = 0; i < elementFromNotification.getLegalFactsIds().size(); i++) {
//                            Assertions.assertEquals(elementFromNotification.getLegalFactsIds().get(i).getCategory(), elementFromTest.getLegalFactsIds().get(i).getCategory());
//                            Assertions.assertNotNull(elementFromNotification.getLegalFactsIds().get(i).getKey());
//                        }
//                    }
//                    if (Objects.nonNull(detailsFromTest.getDeliveryDetailCode())) {
//                        Assertions.assertEquals(detailsFromNotification.getDeliveryDetailCode(), detailsFromTest.getDeliveryDetailCode());
//                    }
//                    if (Objects.nonNull(detailsFromTest.getAttachments())) {
//                        Assertions.assertNotNull(detailsFromNotification.getAttachments());
//                        Assertions.assertEquals(detailsFromNotification.getAttachments().size(), detailsFromTest.getAttachments().size());
//
//                        for (int i = 0; i < detailsFromNotification.getAttachments().size(); i++) {
//                            List<String> documentTypes = Arrays.asList(detailsFromTest.getAttachments().get(i).getDocumentType().split(" "));
//                            Assertions.assertTrue(
//                                    documentTypes.contains(detailsFromNotification.getAttachments().get(i).getDocumentType()),
//                                    "DocumentType not match. Actual document types: %s, Expected document types: %s. IUN: %s".formatted(
//                                            detailsFromNotification.getAttachments().stream().map(AttachmentDetails::getDocumentType).toList(),
//                                            detailsFromTest.getAttachments().stream().map(AttachmentDetails::getDocumentType).toList(),
//                                            sharedSteps.getNotificationIun()
//                                    ));
//                        }
//                    }
//
//                    if (Objects.nonNull(detailsFromTest.getDeliveryFailureCause())) {
//                        List<String> failureCauses = Arrays.asList(detailsFromTest.getDeliveryFailureCause().split(" "));
//                        Assertions.assertEquals(Boolean.TRUE, failureCauses.contains(elementFromNotification.getDetails().getDeliveryFailureCause()));
//                    }
//                }
//            }
//            case ANALOG_SUCCESS_WORKFLOW, PREPARE_SIMPLE_REGISTERED_LETTER -> {
//                //TODO: ignorare i commenti di Sonar che dice che questa condizione è sempre true (in quanto il campo è annotato con @NotNull)
//                // A causa di questo suggerimento errato, in precedenza era stato rimosso l'if, causando il fail di alcuni test
//                if (detailsFromTest != null && detailsFromTest.getPhysicalAddress() != null) {
//                    Assertions.assertEquals(detailsFromTest.getPhysicalAddress(), detailsFromNotification.getPhysicalAddress());
//                }
//            }
//            case SEND_SIMPLE_REGISTERED_LETTER -> {
//                if (detailsFromTest != null) {
//                    Assertions.assertEquals(detailsFromNotification.getPhysicalAddress(), detailsFromTest.getPhysicalAddress());
//                    Assertions.assertEquals(detailsFromNotification.getAnalogCost(), detailsFromTest.getAnalogCost());
//                }
//            }
//            case NOTIFICATION_VIEWED -> {
//                Assertions.assertNotNull(elementFromNotification.getLegalFactsIds());
//                Assertions.assertEquals(elementFromNotification.getLegalFactsIds().size(), elementFromTest.getLegalFactsIds().size());
//                for (int i = 0; i < elementFromNotification.getLegalFactsIds().size(); i++) {
//                    Assertions.assertEquals(elementFromNotification.getLegalFactsIds().get(i).getCategory(), elementFromTest.getLegalFactsIds().get(i).getCategory());
//                    Assertions.assertNotNull(elementFromNotification.getLegalFactsIds().get(i).getKey());
//                }
//                if (delegateInfoFromTest != null) {
//                    Assertions.assertEquals(delegateInfoFromNotification.getTaxId(), delegateInfoFromTest.getTaxId());
//                    Assertions.assertEquals(delegateInfoFromNotification.getDelegateType(), delegateInfoFromTest.getDelegateType());
//                    Assertions.assertEquals(delegateInfoFromNotification.getDenomination(), delegateInfoFromTest.getDenomination());
//                }
//            }
//            case COMPLETELY_UNREACHABLE -> {
//                if (Objects.nonNull(elementFromTest.getLegalFactsIds())) {
//                    assert elementFromNotification.getLegalFactsIds() != null;
//                    Assertions.assertEquals(elementFromNotification.getLegalFactsIds().size(), elementFromTest.getLegalFactsIds().size());
//                }
//                for (int i = 0; i < Objects.requireNonNull(elementFromNotification.getLegalFactsIds()).size(); i++) {
//                    Assertions.assertEquals(elementFromNotification.getLegalFactsIds().get(i).getCategory(), elementFromTest.getLegalFactsIds().get(i).getCategory());
//                    Assertions.assertNotNull(elementFromNotification.getLegalFactsIds().get(i).getKey());
//                }
//            }
//            case REFINEMENT -> {
//                if (detailsFromTest != null) {
//                    Assertions.assertEquals(detailsFromNotification.getRecIndex(), detailsFromTest.getRecIndex());
//                }
//            }
//            default ->
//                    throw new IllegalArgumentException("Valore non valido per timelineEventCategory: " + timelineEventCategory);
//        }
//    }

//    private void loadTimelineByDeliveryPush(String timelineEventCategory, DataTest dataFromTest, boolean existCheck) {
//        // calc how much time wait
//        Integer pollingTime = dataFromTest != null ? dataFromTest.getPollingTime() : null;
//        Integer numCheck = dataFromTest != null ? dataFromTest.getNumCheck() : null;
//        String pollingType = dataFromTest != null ? dataFromTest.getPollingType() : null;
//
//        TimingForPolling.TimingResult timingForElement = timingForPolling.getTimingForElement(timelineEventCategory);
//        if ("extraRapid".equals(pollingType)) {
//            timingForElement = timingForPolling.getTimingForElement(timelineEventCategory, false, true);
//        }
//
//        int defaultPollingTime = timingForElement.waiting();
//        int defaultNumCheck = timingForElement.numCheck();
//        int waitingTime = (pollingTime != null ? pollingTime : defaultPollingTime) * (numCheck != null ? numCheck : defaultNumCheck);
//
//        await()
//                .atMost(waitingTime, MILLISECONDS)
//                .with()
//                .pollInterval(pollingTime != null ? pollingTime : defaultPollingTime, MILLISECONDS)
//                .pollDelay(0, MILLISECONDS)
//                .ignoreExceptions()
//                .untilAsserted(() -> {
//                    TimelineElementV26 timelineElement = getTimelineByDeliveryPush(timelineEventCategory, dataFromTest);
//                    List<TimelineElementV26> timelineElementList = sharedSteps.getSentNotificationLastVersion().getTimeline();
//
//                    log.info("NOTIFICATION_TIMELINE: " + timelineElementList);
//                    Assertions.assertNotNull(timelineElementList);
//                    Assertions.assertNotEquals(0, timelineElementList.size());
//                    if (existCheck) {
//                        Assertions.assertNotNull(timelineElement);
//                    } else {
//                        Assertions.assertNull(timelineElement);
//                    }
//                });

//        PnPollingServiceTimelineSlowE2eV23 timelineSlowV23 = (PnPollingServiceTimelineSlowE2eV23) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_SLOW_V23);
//
//        PnPollingResponseV23 pnPollingResponseV23 = timelineSlowV23.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV23.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV23.getResult());
//            Assertions.assertNotNull(pnPollingResponseV23.getTimelineElement());
//            sharedSteps.setSentNotification(pnPollingResponseV23.getNotification());
//            TimelineElementV23 timelineElementV23 = pnPollingResponseV23.getTimelineElement();
//            log.info("TIMELINE_ELEMENT: " + timelineElementV23);
//            sharedSteps.setTimelineElementV23(timelineElementV23);
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
//        }
//        TimelineElementV23 timelineElement = getTimelineByDeliveryPush(timelineEventCategory, dataFromTest);
//        List<TimelineElementV23> timelineElementList = sharedSteps.getSentNotification().getTimeline();
//
//        log.info("NOTIFICATION_TIMELINE: " + timelineElementList);
//        Assertions.assertNotNull(timelineElementList);
//        Assertions.assertNotEquals(0, timelineElementList.size());
//        if (existCheck) {
//            Assertions.assertNotNull(timelineElement);
//        } else {
//            Assertions.assertNull(timelineElement);
//        }
//    }

//    private TimelineElementV26 getTimelineByDeliveryPush(String timelineEventCategory, DataTest dataFromTest) {
//        String requestId = sharedSteps.getNotificationRequestId();
//        byte[] decodedBytes = Base64.getDecoder().decode(requestId);
//        String iun = new String(decodedBytes);
//        // get timeline from delivery-push
//        NotificationHistoryResponse notificationHistory = this.pnPrivateDeliveryPushExternalClient.getNotificationHistory(iun, sharedSteps.getRecipientsSize(), sharedSteps.getNotificationCreationDate());
//        List<TimelineElementV26> timelineElementList = notificationHistory.getTimeline().stream().map(x -> sharedSteps.deepCopy(x, TimelineElementV26.class)).toList();
//        return getTimelineElementByIdOrCategory(timelineEventCategory, dataFromTest, iun, timelineElementList);
//    }

    private TimelineElementV26 getAndStoreTimelineByB2b(String timelineEventCategory, DataTest dataFromTest) {
        // proceed with default flux
        PnPollingServiceTimelineRapidV26 timelineRapidV26 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);

        String iun = sharedSteps.getNotificationIun();
        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV26.waitForEvent(iun, PnPollingParameter.builder().value(timelineEventCategory).build());
        return getTimelineElementByIdOrCategory(timelineEventCategory, dataFromTest, iun, pnPollingResponseV26.getNotification().getTimeline());
    }

    private TimelineElementV26 getTimelineElementByIdOrCategory(String timelineEventCategory, DataTest dataFromTest, String iun, List<TimelineElementV26> timelineElementList) {
        TimelineElementV26 timelineElement;
        // get timeline event id
        if (dataFromTest != null && dataFromTest.getTimelineElement() != null) {
            String timelineEventId = sharedSteps.getTimelineEventId(timelineEventCategory, iun, dataFromTest);
            timelineElement = timelineElementList.stream().filter(elem -> elem.getElementId().startsWith(timelineEventId)).findAny().orElse(null);
        } else {
            timelineElement = timelineElementList.stream().filter(elem -> elem.getCategory().getValue().equals(timelineEventCategory)).findAny().orElse(null);
        }
        return timelineElement;
    }

//    private void loadTimeline(String timelineEventCategory, boolean existCheck, @Transpose DataTest dataFromTest) {
//        TimelineElementV26 timelineElement;
//        if (!timelineEventCategory.equals(TimelineElementCategoryV26.REQUEST_REFUSED.getValue())) {
//            timelineElement = getAndStoreTimelineByB2b(timelineEventCategory, dataFromTest);
//            List<TimelineElementV26> timelineElementList = sharedSteps.getSentNotificationLastVersion().getTimeline();
//
//            log.info("NOTIFICATION_TIMELINE: " + timelineElementList);
//            String iun = sharedSteps.getNotificationIun();
//            Assertions.assertNotNull(timelineElementList, "timelineElementList is null. IUN: " + iun);
//            Assertions.assertNotEquals(0, timelineElementList.size(), "timelineElementList is empty. IUN: " + iun);
//            if (existCheck) {
//                Assertions.assertNotNull(timelineElement, "timelineElement is null. IUN: " + iun);
//            } else {
//                Assertions.assertNull(timelineElement, "timelineElement is not null. IUN: " + iun);
//            }
//        } else {
//            //GESTIONE LOAD TIMELINE E RECUPERO NOTIFICA CON CLIENT DI DELIVERY PUSH
//            loadTimelineByDeliveryPush(timelineEventCategory, dataFromTest, existCheck);
//        }
//    }

    @Then("viene verificato che il numero di elementi di timeline {string} della notifica sia di {int}")
    public void checkNumberOfTimelineElementsWithCategory(String timelineEventCategory, Integer size) {
        try {
            getB2bStepsInterface().checkNumberOfTimelineElements(timelineEventCategory, size);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    @And("viene verificato che il numero di elementi di timeline {string} sia di {int}")
    public void checkNumberOfTimelineElementsWithCategoryFromMap(String timelineEventCategory, Integer size, Map<String, String> dataMap) {
        try {
            getB2bStepsInterface().checkNumberOfTimelineElementsFromData(timelineEventCategory, size, dataMap);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
        }

//        String iun = sharedSteps.getNotificationIun();
//        FullSentNotificationV26 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
//        List<TimelineElementV26> timelineElementList = fullSentNotification.getTimeline();
//        // get timeline event id
//        String timelineEventId = sharedSteps.getTimelineEventId(timelineEventCategory, iun, dataFromTest);
//        if (timelineEventCategory.equals(SEND_ANALOG_PROGRESS)) {
//
//            TimelineElementV26 timelineElementFromTest = dataFromTest.getTimelineElement();
//            TimelineElementDetailsV26 timelineElementDetails = timelineElementFromTest.getDetails();
//
//            Assertions.assertEquals(size, timelineElementList.stream().filter(elem ->
//                    elem.getElementId().startsWith(timelineEventId)
//                            && elem.getDetails().getDeliveryDetailCode().equals(timelineElementDetails.getDeliveryDetailCode())).count());
//        } else {
//            Assertions.assertEquals(size, timelineElementList.stream().filter(elem ->
//                    elem.getElementId().startsWith(timelineEventId)).count());
//        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica annullata {string}")
    public void readingEventUpToTheTimelineElementOfNotificationDelete(String timelineEventCategory) {
        readEventsUpToTimelineElement(sharedSteps.getVersionUsed(), timelineEventCategory);
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string}")
    public void readEventUpToTimelineElement(String timelineEventCategory) {
        readEventsUpToTimelineElement(sharedSteps.getVersionUsed(), timelineEventCategory);
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con la versione {string}")
    public void readEventUpToTimelineElementWithVersion(String timelineEventCategory, String version) {
        readEventsUpToTimelineElement(sharedSteps.getNotificationVersion(version), timelineEventCategory);
    }

    //TODO: creato un unico metodo che legge con una versione che viene passata come parametro
//    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} V1")
//    public void readingEventUpToTheTimelineElementOfNotificationV1(String timelineEventCategory) {
//        readEventsUpToTimelineElement(NotificationVersion.V1, timelineEventCategory);
//    }

//    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} V2")
//    public void readingEventUpToTheTimelineElementOfNotificationV2(String timelineEventCategory) {
//        readEventsUpToTimelineElement(NotificationVersion.V2, timelineEventCategory);
//    }

//    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} V21")
//    public void readingEventUpToTheTimelineElementOfNotificationV21(String timelineEventCategory) {
//        readEventsUpToTimelineElement(NotificationVersion.V21, timelineEventCategory);
//    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} abbia notificationCost uguale a {string}")
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
                .map(FullSentNotificationV26::getTimeline)
                .orElse(List.of())
                .stream()
                .map(TimelineElementV26::getCategory)
                .filter(Objects::nonNull)
                .map(TimelineElementCategoryV26::toString)
                .toList();
        try {
            Assertions.assertFalse(expectedEvents.stream().anyMatch(Predicate.not(actualTimeline::contains)));
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    @Then("viene verificato che per l'elemento di timeline della notifica {string} non ci siano duplicati")
    public void checkTimeLineEventWithoutDuplicates(String timelineEventCategory) {
        getB2bStepsInterface().checkForNoDuplicatedTimelineElements(timelineEventCategory);
    }

    @Then("si verifica che scheduleDate del SCHEDULE_REFINEMENT sia uguale al timestamp di REFINEMENT per l'utente {int}")
    public void verificationDateScheduleRefinementWithRefinement(Integer destinatario) {
        try {
            FullSentNotificationV26 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
            OffsetDateTime ricezioneRaccomandata = fullSentNotification.getTimeline().stream().filter(elem ->
                    elem.getCategory().getValue().equals(SCHEDULE_REFINEMENT)
                            && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getDetails().getSchedulingDate();
            OffsetDateTime refinementDate = fullSentNotification.getTimeline().stream().filter(elem ->
                    elem.getCategory().getValue().equals(REFINEMENT)
                            && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();

            log.info("DESTINATARIO : {}", destinatario);
            log.info("ricezioneRaccomandata : {}", ricezioneRaccomandata);
            log.info("refinementDate : {}", refinementDate);

            Assertions.assertEquals(ricezioneRaccomandata, refinementDate);

        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    @Then("si verifica che il timestamp dell'elemento di timeline della notifica SEND_ANALOG_FEEDBACK con deliveryDetailCode RECAG012 sia uguale al timestamp di REFINEMENT")
    public void verificationDateDeliveryDetailCodeRECAG012WithRefinement() {
        try {
            FullSentNotificationV26 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
            OffsetDateTime ricezioneRECAG012 = fullSentNotification.getTimeline().stream().filter(elem ->
                    elem.getCategory().getValue().equals(SEND_ANALOG_FEEDBACK)
                            && elem.getDetails().getDeliveryDetailCode().equals("RECAG012")).findAny().get().getDetails().getEventTimestamp();
            OffsetDateTime refinementDate = fullSentNotification.getTimeline().stream().filter(elem ->
                    elem.getCategory().getValue().equals(REFINEMENT)
                            && elem.getDetails().getRecIndex().equals(0)).findAny().get().getTimestamp();

            log.info("ricezioneRaccomandata : {}", ricezioneRECAG012);
            log.info("refinementDate : {}", refinementDate);

            Assertions.assertTrue(checkOffsetDateTime(ricezioneRECAG012, refinementDate));

        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    private boolean checkOffsetDateTime(OffsetDateTime offsetDateTime1, OffsetDateTime offsetDateTime2) {
        return offsetDateTime1.equals(offsetDateTime2);
    }

    @Then("verifica date business in timeline COMPLETELY_UNREACHABLE per l'utente {int}")
    public void verificationDateCompletelyUnreachableWithRefinement(Integer destinatario) {
        try {
            FullSentNotificationV26 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
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
            FullSentNotificationV26 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
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


    @Then("vengono letti gli eventi della timeline e si controlla che l'evento di timeline {string} non esista con la V1")
    public void readingEventsOfTimelineElementOfNotificationV1(String timelineEventCategory) {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface(NotificationVersion.V1);
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_SLOW, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(false, null, null);

//        String iun = sharedSteps.getNotificationIun();
//        PnPollingServiceTimelineSlowV1 timelineSlowV1 = (PnPollingServiceTimelineSlowV1) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_SLOW_V1);
//        PnPollingResponseV1 pnPollingResponseV1 = timelineSlowV1.waitForEvent(iun,
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .build());
//        log.info("NOTIFICATION_TIMELINE V1 : " + pnPollingResponseV1.getNotification().getTimeline());
//        try {
//            Assertions.assertFalse(pnPollingResponseV1.getResult());
//            Assertions.assertNull(pnPollingResponseV1.getTimelineElement());
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    @Then("viene controllato che l'elemento di timeline della notifica {string} non esiste dopo il rifiuto della notifica stessa")
    public void readingNotEventUpToTheTimelineElementOfNotificationRefused(String timelineEventCategory) {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(false, null, null);

//        PnPollingServiceTimelineRapidV26 timelineRapidV26 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV26.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertFalse(pnPollingResponseV26.getResult());
//            Assertions.assertNull(pnPollingResponseV26.getTimelineElement());
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    @Then("viene controllato che l'elemento di timeline della notifica {string} non esiste")
    public void readingNotEventUpToTheTimelineElementOfNotification(String timelineEventCategory) {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(false, null, null);

//        PnPollingServiceTimelineRapidV26 timelineRapidV26 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV26.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertFalse(pnPollingResponseV26.getResult());
//            Assertions.assertNull(pnPollingResponseV26.getTimelineElement());
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} e successivamente annullata")
    public void readingEventUpToTheTimelineElementOfNotificationAndCancel(String timelineEventCategory) {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(true, null, null);
        try {
            Assertions.assertDoesNotThrow(() -> b2bClient.notificationCancellation(sharedSteps.getNotificationIun()));
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
        }

//        PnPollingServiceTimelineRapidV26 timelineRapidV26 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV26.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            log.info("TIMELINE_ELEMENT: " + pnPollingResponseV26.getTimelineElement());
//            Assertions.assertDoesNotThrow(() -> b2bClient.notificationCancellation(sharedSteps.getNotificationIun()));
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con deliveryDetailCode {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCode(String timelineEventCategory, String deliveryDetailCode) {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .deliveryDetailCode(deliveryDetailCode)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(true, null, null);

//        PnPollingResponseV26 pnPollingResponseV26 = getPollingResponse(timelineEventCategory, deliveryDetailCode);
//        Objects.requireNonNull(pnPollingResponseV26.getNotification(), "La notifica non può essere null");
//        log.info("NOTIFICATION_TIMELINE: {}", pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            String iun = sharedSteps.getNotificationIun() != null ? sharedSteps.getNotificationIun() : "UNKNOWN";
//            assertSoftly(softly -> {
//                softly.assertThat(pnPollingResponseV26.getResult())
//                        .as("Verifica che il polling abbia avuto successo per IUN: " + iun)
//                        .isTrue();
//                softly.assertThat(pnPollingResponseV26.getTimelineElement())
//                        .as("Verifica che l'elemento di timeline esista per IUN: " + iun)
//                        .isNotNull();
//            });
//            if (pnPollingResponseV26.getTimelineElement() != null) {
//                log.info("TIMELINE_ELEMENT: {}", pnPollingResponseV26.getTimelineElement());
//            }
//        } catch (AssertionError assertionError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionError);
//        }
    }

    @Then("viene verificato che lato utente l'elemento di timeline {string} con deliveryDetailCode {string} non esista")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCodeWithoutSuccess(String timelineEventCategory, String deliveryDetailCode) {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .deliveryDetailCode(deliveryDetailCode)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(false, null, null);

//        PnPollingResponseV26 pnPollingResponseV26 = getPollingResponse(timelineEventCategory, deliveryDetailCode);
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertFalse(pnPollingResponseV26.getResult());
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con deliveryDetailCode {string} tentativo {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCode(String timelineEventCategory, String deliveryDetailCode, String attempt) {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .deliveryDetailCode(deliveryDetailCode)
                .attempt(attempt)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_SLOW, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(true, null, null);

//        PnPollingServiceTimelineSlowV26 timelineRapidV25 = (PnPollingServiceTimelineSlowV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_SLOW_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV25.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .pnPollingPredicate(getPnPollingPredicateForTimelineV26(timelineEventCategory, deliveryDetailCode, attempt))
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            String iun = sharedSteps.getNotificationIun();
//            Assertions.assertTrue(pnPollingResponseV26.getResult(), "Polling failed. IUN: " + iun);
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement(), "Timeline element not found. IUN: " + iun);
//            log.info("TIMELINE_ELEMENT: " + pnPollingResponseV26.getTimelineElement());
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
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
        b2bStepsInterface.checkIfTimelineElementExists(true, CHECK_NOTIFICATION_DATE_DELAY, checkFilters);

//        PnPollingResponseV26 pnPollingResponseV26 = getPollingResponse(timelineEventCategory, deliveryDetailCode);
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            TimelineElementV26 timelineElement = pnPollingResponseV26.getTimelineElement();
//            log.info("TIMELINE_ELEMENT: " + timelineElement);
//            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//            Assertions.assertEquals(Objects.requireNonNull(timelineElement.getDetails()).getNotificationDate().format(fmt), now().plusDays(delay).format(fmt));
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
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
        b2bStepsInterface.checkIfTimelineElementExists(true, CHECK_SCHEDULING_DATE_DELAY, checkFilters);

//        PnPollingServiceTimelineRapidV26 timelineRapidV25 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV25.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .pnPollingPredicate(getPnPollingPredicateForTimelineV26(timelineEventCategory, recipientIndex))
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            TimelineElementV26 timelineElement = pnPollingResponseV26.getTimelineElement();
//            log.info("TIMELINE_ELEMENT: " + timelineElement);
//            OffsetDateTime digitalDeliveryCreationRequestDate = Objects.requireNonNull(timelineElement).getTimestamp();
//            Assertions.assertNotNull(Objects.requireNonNull(timelineElement.getDetails()).getSchedulingDate());
//            Assertions.assertNotNull(tipoIncremento);
//            if ("d".equalsIgnoreCase(tipoIncremento)) {
//                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//                Assertions.assertEquals(timelineElement.getDetails().getSchedulingDate().format(fmt), Objects.requireNonNull(digitalDeliveryCreationRequestDate).plusDays(delay).format(fmt));
//            } else if ("m".equalsIgnoreCase(tipoIncremento)) {
//                DateTimeFormatter fmt1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
//                Assertions.assertEquals(timelineElement.getDetails().getSchedulingDate().format(fmt1), Objects.requireNonNull(digitalDeliveryCreationRequestDate).plusMinutes(delay).format(fmt1));
//            }
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
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
        b2bStepsInterface.checkIfTimelineElementExists(true, CHECK_ATTACHMENTS, checkFilters);

//        PnPollingResponseV26 pnPollingResponseV26 = getPollingResponse(timelineEventCategory, deliveryDetailCode);
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            TimelineElementV26 timelineElement = pnPollingResponseV26.getTimelineElement();
//            log.info("TIMELINE_ELEMENT: " + timelineElement);
//            Assertions.assertNotNull(Objects.requireNonNull(timelineElement.getDetails()).getAttachments());
//            Assertions.assertFalse(timelineElement.getDetails().getAttachments().isEmpty());
//            Assertions.assertNotNull(timelineElement.getDetails().getAttachments().get(0).getDocumentType());
//            Assertions.assertEquals(Objects.requireNonNull(timelineElement.getDetails().getAttachments().get(0).getDocumentType()), documentType);
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con deliveryDetailCode {string} e verifica tipo DOC {string} tentativo {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCodeVerifyTypeDoc(String timelineEventCategory, String deliveryDetailCode, String documentType, String attempt) {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .deliveryDetailCode(deliveryDetailCode)
                .attempt(attempt)
                .documentType(documentType)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder()
                .documentType(documentType)
                .build();
        b2bStepsInterface.checkIfTimelineElementExists(true, CHECK_ATTACHMENTS, checkFilters);

//        PnPollingServiceTimelineRapidV26 timelineRapidV25 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV25.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .pnPollingPredicate(getPnPollingPredicateForTimelineV26(timelineEventCategory, null, deliveryDetailCode, attempt, documentType, null, false, false, null, false, null))
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            TimelineElementV26 timelineElement = pnPollingResponseV26.getTimelineElement();
//            log.info("TIMELINE_ELEMENT: " + timelineElement);
//            Assertions.assertNotNull(Objects.requireNonNull(timelineElement.getDetails()).getAttachments());
//            Assertions.assertFalse(timelineElement.getDetails().getAttachments().isEmpty());
//            Assertions.assertNotNull(timelineElement.getDetails().getAttachments().get(0).getDocumentType());
//            Assertions.assertTrue(Objects.requireNonNull(timelineElement.getDetails().getAttachments().get(0).getDocumentType()).equals(documentType) || Objects.equals(timelineElement.getDetails().getAttachments().get(0).getDocumentType(), "Indagine"));
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
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
        b2bStepsInterface.checkIfTimelineElementExists(true, CHECK_DELIVERY_FAILURE_CAUSE, checkFilters);

//        PnPollingResponseV26 pnPollingResponseV26 = getPollingResponse(timelineEventCategory, deliveryDetailCode);
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            TimelineElementV26 timelineElement = pnPollingResponseV26.getTimelineElement();
//            log.info("TIMELINE_ELEMENT: " + timelineElement);
//            Assertions.assertEquals(Objects.requireNonNull(timelineElement.getDetails()).getDeliveryFailureCause(), deliveryFailureCause);
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con deliveryDetailCode {string} e deliveryFailureCause {string} tentativo {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCodeDeliveryFailureCause(String timelineEventCategory, String deliveryDetailCode, String deliveryFailureCause, String attempt) {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .deliveryDetailCode(deliveryDetailCode)
                .attempt(attempt)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder()
                .deliveryFailureCause(deliveryFailureCause)
                .build();
        b2bStepsInterface.checkIfTimelineElementExists(true, CHECK_DELIVERY_FAILURE_CAUSE, checkFilters);

//        PnPollingServiceTimelineRapidV26 timelineRapidV26 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV26.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .pnPollingPredicate(getPnPollingPredicateForTimelineV26(timelineEventCategory, deliveryDetailCode, attempt))
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            TimelineElementV26 timelineElement = pnPollingResponseV26.getTimelineElement();
//            log.info("TIMELINE_ELEMENT: " + timelineElement);
//            Assertions.assertEquals(Objects.requireNonNull(timelineElement.getDetails()).getDeliveryFailureCause(), deliveryFailureCause);
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    @And("viene verificato il campo sendRequestId dell' evento di timeline {string}")
    public void vieneVerificatoCampoSendRequestIdEventoTimeline(String timelineEventCategory) {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(true, CHECK_SEND_REQUEST_ID, null);

//        PnPollingServiceTimelineRapidV26 timelineRapidV25 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV25.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            TimelineElementV26 timelineElement = pnPollingResponseV26.getTimelineElement();
//            log.info("TIMELINE_ELEMENT: " + timelineElement);
//            Assertions.assertNotNull(timelineElement.getDetails());
//            Assertions.assertNotNull(timelineElement.getDetails().getSendRequestId());
//            String sendRequestId = timelineElement.getDetails().getSendRequestId();
//            TimelineElementV26 timelineElementRelative = pnPollingResponseV26
//                    .getNotification()
//                    .getTimeline()
//                    .stream()
//                    .filter(elem -> Objects.requireNonNull(elem.getElementId()).equals(sendRequestId))
//                    .findAny()
//                    .orElse(null);
//            Assertions.assertNotNull(timelineElementRelative);
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    @And("viene verificato il campo serviceLevel dell' evento di timeline {string} sia valorizzato con {string}")
    public void vieneVerificatoCampoServiceLevelEventoTimeline(String timelineEventCategory, String serviceLevelValue) {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder()
                .serviceLevel(serviceLevelValue)
                .build();
        b2bStepsInterface.checkIfTimelineElementExists(true, CHECK_SERVICE_LEVEL, checkFilters);

//        PnPollingServiceTimelineRapidV26 timelineRapidV26 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV26.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .serviceLevelValue(timelineEventCategory)
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            ServiceLevel level = switch (serviceLevelValue) {
//                case "AR_REGISTERED_LETTER" -> ServiceLevel.AR_REGISTERED_LETTER;
//                case "REGISTERED_LETTER_890" -> ServiceLevel.REGISTERED_LETTER_890;
//                default -> throw new IllegalArgumentException();
//            };
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            TimelineElementV26 timelineElement = pnPollingResponseV26.getTimelineElement();
//            log.info("TIMELINE_ELEMENT: " + timelineElement);
//            Assertions.assertNotNull(timelineElement.getDetails());
//            Assertions.assertEquals(timelineElement.getDetails().getServiceLevel(), level);
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} per l'utente {int}")
    public void readingEventUpToTheTimelineElementOfNotificationPerUtente(String timelineEventCategory, Integer recipientIndex) {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .recipientIndex(recipientIndex)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_SLOW, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(true, null, null);

//        PnPollingServiceTimelineSlowV26 timelineSlowV25 = (PnPollingServiceTimelineSlowV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_SLOW_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineSlowV25.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .pnPollingPredicate(getPnPollingPredicateForTimelineV26(timelineEventCategory, recipientIndex))
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            timelineElement = pnPollingResponseV26.getTimelineElement();
//            log.info("TIMELINE_ELEMENT: " + pnPollingResponseV26.getTimelineElement());
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    //TODO: FA LA STESSA IDENTICA COSA DEL METODO DI SOPRA, SOLO CON LA FRASE DELLO STEP ESPOSTA IN MANIERA DIVERSA
    // E LA POLLING STRATEGY E' RAPID ANZICHE' SLOW
    @Then("esiste l'elemento di timeline della notifica {string} per l'utente {int}")
    public void verifyEventUpToTheTimelineElementOfNotificationPerUtente(String timelineEventCategory, Integer recipientIndex) {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .recipientIndex(recipientIndex)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(true, null, null);

//        PnPollingServiceTimelineRapidV26 timelineRapidV25 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV25.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .pnPollingPredicate(getPnPollingPredicateForTimelineV26(timelineEventCategory, recipientIndex))
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            log.info("TIMELINE_ELEMENT: " + pnPollingResponseV26.getTimelineElement());
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    //TODO: UGUALE A METODO SOPRA, MA SCENARIO NEGATIVO, CAMBIA L'ASSERT
    @Then("non vengono letti gli eventi fino all'elemento di timeline della notifica {string} per l'utente {int}")
    public void notReadingEventUpToTheTimelineElementOfNotificationPerUtente(String timelineEventCategory, Integer recipientIndex) {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .recipientIndex(recipientIndex)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(false, null, null);

//        PnPollingServiceTimelineRapidV26 timelineRapidV25 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV25.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .pnPollingPredicate(getPnPollingPredicateForTimelineV26(timelineEventCategory, recipientIndex))
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertFalse(pnPollingResponseV26.getResult());
//            Assertions.assertNull(pnPollingResponseV26.getTimelineElement());
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    //TODO: IDENTICO AL METODO SOPRA, SOLO CON LA FRASE DELLO STEP ESPOSTA IN MANIERA DIVERSA
    @Then("vengono letti gli eventi e verifico che l'utente {int} non abbia associato un evento {string}")
    public void vengonoLettiGliEventiVerificoCheUtenteNonAbbiaAssociatoEvento(Integer recipientIndex, String timelineEventCategory) {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .recipientIndex(recipientIndex)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(false, null, null);

//        PnPollingServiceTimelineRapidV26 timelineRapidV25 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV25.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .pnPollingPredicate(getPnPollingPredicateForTimelineV26(timelineEventCategory, recipientIndex))
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertFalse(pnPollingResponseV26.getResult());
//            Assertions.assertNull(pnPollingResponseV26.getTimelineElement());
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
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
        b2bStepsInterface.checkIfTimelineElementExists(false, null, null);

//        PnPollingServiceTimelineRapidV26 timelineRapidV25 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV25.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .pnPollingPredicate(getPnPollingPredicateForTimelineV26(timelineEventCategory, recipientIndex, null, null, null, responseStatus, false, false, null, false, null))
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertFalse(pnPollingResponseV26.getResult());
//            Assertions.assertNull(pnPollingResponseV26.getTimelineElement());
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    @Then("viene verificato che nell'elemento di timeline della notifica {string} sia presente il campo Digital Address da National Registry per l utente {int}")
    public void vieneVerificatoCheNellElementoDiTimelineDellaNotificaSiaPresenteIlCampoDigitalAddressDaNationalRegistryPerLUtente(String timelineEventCategory, Integer recipientIndex) {
        readingEventUpToTheTimelineElementOfNotificationPerUtente(timelineEventCategory, recipientIndex);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder().build();
        getB2bStepsInterface().performFurtherChecks(CHECK_DIGITAL_ADDRESS, checkFilters);

//        Assertions.assertNotNull(timelineElement);//questo viene verificato quando chiama readingEventUpToTheTimelineElementOfNotificationPerUtente (che a sua volta richiama checkIfTimelineElementExists)
//        Assertions.assertNotNull(timelineElement.getDetails());
//        Assertions.assertNotNull(timelineElement.getDetails().getDigitalAddress());
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
        b2bStepsInterface.checkIfTimelineElementExists(true, CHECK_NUMBER_OF_PAGES_AAR, checkFilters);

//        PnPollingServiceTimelineRapidV26 timelineRapidV25 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV25.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            TimelineElementV26 timelineElement = pnPollingResponseV26.getTimelineElement();
//            log.info("TIMELINE_ELEMENT: " + timelineElement);
//            Assertions.assertEquals(Objects.requireNonNull(timelineElement.getDetails()).getNumberOfPages(), numPagine);
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    @Then("vengono letti gli eventi e verifico che l'utente {int} non abbia associato un evento {string} V1")
    public void vengonoLettiGliEventiVerificoCheUtenteNonAbbiaAssociatoEventoV1(Integer recipientIndex, String timelineEventCategory) {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .recipientIndex(recipientIndex)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface(NotificationVersion.V1);
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(false, null, null);

//        String iun = sharedSteps.getNotificationIun();
//        PnPollingPredicate pnPollingPredicate = new PnPollingPredicate();
//        pnPollingPredicate.setTimelineElementPredicateV1(timelineElementV1 ->
//                timelineElementV1.getCategory() != null
//                        && Objects.requireNonNull(timelineElementV1.getCategory().getValue()).equals(timelineEventCategory)
//                        && Objects.requireNonNull(Objects.requireNonNull(timelineElementV1.getDetails()).getRecIndex()).equals(recipientIndex));
//        PnPollingServiceTimelineRapidV1 timelineRapidV1 = (PnPollingServiceTimelineRapidV1) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V1);
//        PnPollingResponseV1 pnPollingResponseV1 = timelineRapidV1.waitForEvent(iun,
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .pnPollingPredicate(pnPollingPredicate)
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV1.getNotification().getTimeline());
//        try {
//            Assertions.assertFalse(pnPollingResponseV1.getResult());
//            Assertions.assertNull(pnPollingResponseV1.getTimelineElement());
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    @Then("verifica generazione Atto opponibile senza la messa a disposizione in DIGITAL_DELIVERY_CREATION_REQUEST")
    public void paVerifyGenerazioneLegalFact() {
        TimelineElementV26 timelineElement = null;
        for (TimelineElementV26 element : sharedSteps.getSentNotificationLastVersion().getTimeline()) {
            if (element.getCategory().getValue().equals(DIGITAL_DELIVERY_CREATION_REQUEST)) {
                timelineElement = element;
                break;
            }
        }
        try {
            log.info("TIMELINE ELEMENT : {}", timelineElement);
            Assertions.assertNotNull(timelineElement.getLegalFactsIds());
            Assertions.assertTrue(CollectionUtils.isEmpty(timelineElement.getLegalFactsIds()));
            Assertions.assertNotNull(timelineElement.getDetails().getLegalFactId());

        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    //TODO MATTEO spostato in LegalFactContentVerifySteps, qua non c'entrava nulla
//    @Then("la PA richiede il download dell'attestazione opponibile {string} senza legalFactType")
//    public void paRequiresDownloadOfLegalFactId(String legalFactCategory) {
//        downloadLegalFactId(legalFactCategory, true, false, false, null);
//    }

    //TODO MATTEO spostato in LegalFactContentVerifySteps, qua non c'entrava nulla
//    @Then("la PA richiede il download dell'attestazione opponibile {string}")
//    public void paRequiresDownloadOfLegalFact(String legalFactCategory) {
//        String legalFactUrl = downloadLegalFact(legalFactCategory, true, false, false, null);
//        legalFactContentVerifySteps.setLegalFactUrl(legalFactUrl);
//    }

    //TODO MATTEO spostato in LegalFactContentVerifySteps, qua non c'entrava nulla
//    @Then("la PA richiede il download dell'attestazione opponibile {string} con deliveryDetailCode {string}")
//    public void paRequiresDownloadOfLegalFactWithDeliveryDetailCode(String legalFactCategory, String deliveryDetailCode) {
//        String legalFactUrl = downloadLegalFact(legalFactCategory, true, false, false, deliveryDetailCode);
//        legalFactContentVerifySteps.setLegalFactUrl(legalFactUrl);
//    }

    //TODO MATTEO spostato in LegalFactContentVerifySteps, qua non c'entrava nulla
//    @Then("viene richiesto tramite appIO il download dell'attestazione opponibile {string}")
//    public void appIODownloadLegalFact(String legalFactCategory) {
//        String legalFactUrl = downloadLegalFact(legalFactCategory, false, true, false, null);
//        legalFactContentVerifySteps.setLegalFactUrl(legalFactUrl);
//    }

    //TODO MATTEO spostato in LegalFactContentVerifySteps, qua non c'entrava nulla
//    @Then("{string} richiede il download dell'attestazione opponibile {string}")
//    public void userDownloadLegalFact(String user, String legalFactCategory) {
//        sharedSteps.selectUser(user);
//        String legalFactUrl = downloadLegalFact(legalFactCategory, false, false, true, null);
//        legalFactContentVerifySteps.setLegalFactUrl(legalFactUrl);
//    }

    //TODO MATTEO spostato in LegalFactContentVerifySteps, qua non c'entrava nulla
//    @Then("la PA richiede il download dell'attestazione opponibile PEC_RECEIPT")
//    public void paRequiresDownloadOfLegalFactPecRecipient() {
//        downloadLegalFactPecRecipient("PEC_RECEIPT", true, false, false, null);
//    }

    //TODO MATTEO spostato in LegalFactContentVerifySteps, qua non c'entrava nulla
//    @Then("{string} richiede il download dell'attestazione opponibile PEC_RECEIPT")
//    public void userDownloadLegalFactPecRecipient(String user) {
//        sharedSteps.selectUser(user);
//        downloadLegalFactPecRecipient("PEC_RECEIPT", false, false, true, null);
//    }

    //TODO MATTEO spostato in LegalFactContentVerifySteps, qua non c'entrava nulla
//    @Then("{string} richiede il download dell'attestazione opponibile {string} con errore {string}")
//    public void userDownloadLegalFactError(String user, String legalFactCategory, String statusCode) {
//        try {
//            sharedSteps.selectUser(user);
//            String legalFactUrl = downloadLegalFact(legalFactCategory, false, false, true, null);
//            legalFactContentVerifySteps.setLegalFactUrl(legalFactUrl);
//        } catch (AssertionFailedError assertionFailedError) {
//            Assertions.assertEquals(assertionFailedError.getCause().getMessage().substring(0, 3), statusCode);
//        }
//    }

    //TODO MATTEO spostato in LegalFactContentVerifySteps, qua non c'entrava nulla
//    @And("ricerca ed effettua download del legalFact con la categoria {string}")
//    public void ricercaEdEffettuaDownloadDelLegalFactConLaCategoria(String legalFactCategory) {
//        String legalFactUrl = downloadLegalFact(legalFactCategory, false, false, true, null);
//        legalFactContentVerifySteps.setLegalFactUrl(legalFactUrl);
//    }

    //TODO MATTEO spostato in LegalFactContentVerifySteps, qua non c'entrava nulla
//    @And("ricerca ed effettua download del legalFact con la categoria {string} con DetailCode {string}")
//    public void ricercaEdEffettuaDownloadDelLegalFactConLaCategoria(String legalFactCategory, String deliveryDetailCode) {
//        String legalFactUrl = downloadLegalFact(legalFactCategory, false, false, true, deliveryDetailCode);
//        legalFactContentVerifySteps.setLegalFactUrl(legalFactUrl);
//    }

    //TODO MATTEO spostato in LegalFactContentVerifySteps, qua non c'entrava nulla
//    @Then("viene verificato che la chiave dell'attestazione opponibile {string} è {string}")
//    public void verifiedThatTheKeyOfTheLegalFactIs(String legalFactCategory, String key) {
//        try {
//            Thread.sleep(sharedSteps.getWait());
//        } catch (InterruptedException exc) {
//            throw new RuntimeException(exc);
//        }
//
//        PnTimelineLegalFactV26 categoriesV26 = pnTimelineAndLegalFactV26.getCategory(legalFactCategory);
//        TimelineElementV26 timelineElement = sharedSteps.getSentNotificationLastVersion().getTimeline().stream().filter(elem -> elem.getCategory().equals(categoriesV26.getTimelineElementInternalCategory())).findAny().orElse(null);
//
//        try {
//            Assertions.assertNotNull(timelineElement.getLegalFactsIds());
//            Assertions.assertEquals(categoriesV26.getLegalFactCategory().getValue(), timelineElement.getLegalFactsIds().get(0).getCategory());
//            Assertions.assertTrue(timelineElement.getLegalFactsIds().get(0).getKey().contains(key));
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
//    }

    //TODO MATTEO spostato in LegalFactContentVerifySteps, qua non c'entrava nulla
//    @Then("l'ente {string} richiede l'attestazione opponibile {string}")
//    public void paRequiresLegalFact(String paName, String legalFactCategory) {
//        sharedSteps.setPA(paName);
//        try {
//            takeLegalFact(legalFactCategory, null);
//        } catch (HttpStatusCodeException e) {
//            this.sharedSteps.setNotificationError(e);
//        }
//    }

    //TODO MATTEO spostato in LegalFactContentVerifySteps, qua non c'entrava nulla
//    @Then("l'ente {string} richiede l'attestazione opponibile {string} con deliveryDetailCode {string}")
//    public void paRequiresLegalFactConDeliveryDetailCode(String paName, String legalFactCategory, String deliveryDetailCode) {
//        sharedSteps.setPA(paName);
//        try {
//            takeLegalFact(legalFactCategory, deliveryDetailCode);
//        } catch (HttpStatusCodeException e) {
//            this.sharedSteps.setNotificationError(e);
//        }
//    }

    //TODO MATTEO spostato in LegalFactContentVerifySteps, qua non c'entrava nulla
//    @Then("tra gli elementi di timeline con categoria {string} è presente un legalFact con categoria {string}")
//    public void checkLegalFactAllVersions(String timelineElementCategory, String legalFactCategory) {
//        List<LegalFactsIdV20> legalFactsList = this.sharedSteps.getSentNotificationLastVersion().getTimeline().stream().filter(
//                x -> x.getCategory().getValue().equals(timelineElementCategory)).findFirst().orElse(null).getLegalFactsIds();
//        Assertions.assertFalse(legalFactsList.isEmpty());
//        LegalFactsIdV20 legalFact = legalFactsList.stream().filter(x -> x.getCategory().equals(legalFactCategory)).findFirst().orElse(null);
//        Assertions.assertNotNull(legalFact);
//        this.legalFactContentVerifySteps.setLegalFactType(legalFactCategory);
//        this.legalFactContentVerifySteps.setLegalFactUrl(legalFact.getKey());
//        log.info("LEGAL FACT CATEGORY = " + legalFact.getCategory());
//        log.info("LEGAL FACT URL: " + legalFact.getKey());
//    }

    //TODO MATTEO spostato in LegalFactContentVerifySteps, qua non c'entrava nulla
//    public String getKeyLegalFact(String key) {
//        if (key.contains("PN_LEGAL_FACTS")) {
//            return key.substring(key.indexOf("PN_LEGAL_FACTS"));
//        } else if (key.contains("PN_NOTIFICATION_ATTACHMENTS")) {
//            return key.substring(key.indexOf("PN_NOTIFICATION_ATTACHMENTS"));
//        } else if (key.contains("PN_EXTERNAL_LEGAL_FACTS")) {
//            return key.substring(key.indexOf("PN_EXTERNAL_LEGAL_FACTS"));
//        } else if (key.contains("PN_PRINTED")) {
//            return key.substring(key.indexOf("PN_PRINTED"));
//        } else if (key.contains("PN_F24")) {
//            return key.substring(key.indexOf("PN_F24"));
//        }
//        return null;
//    }

    //TODO MATTEO spostato in LegalFactContentVerifySteps, qua non c'entrava nulla
//    private LegalFactDownloadMetadataResponse takeLegalFact(String legalFactCategory, String deliveryDetailCode) {
//        try {
//            Thread.sleep(sharedSteps.getWait());
//        } catch (InterruptedException exc) {
//            throw new RuntimeException(exc);
//        }
//
//        PnTimelineLegalFactV26 categoriesV26 = pnTimelineAndLegalFactV26.getCategory(legalFactCategory);
//
//        TimelineElementV26 timelineElement = null;
//
//        for (TimelineElementV26 element : sharedSteps.getSentNotificationLastVersion().getTimeline()) {
//            if (!Objects.equals(element.getCategory(), categoriesV26.getTimelineElementInternalCategory())) {
//                continue;
//            }
//
//            if (deliveryDetailCode == null ||
//                    (element.getDetails() != null && Objects.equals(element.getDetails().getDeliveryDetailCode(), deliveryDetailCode))) {
//                timelineElement = element;
//                break;
//            }
//        }
//
//        System.out.println("ELEMENT: " + timelineElement);
//        Assertions.assertNotNull(timelineElement);
//
//        Assertions.assertNotNull(timelineElement.getLegalFactsIds());
//        Assertions.assertFalse(CollectionUtils.isEmpty(timelineElement.getLegalFactsIds()));
//        Assertions.assertEquals(categoriesV26.getLegalFactCategory().getValue(), timelineElement.getLegalFactsIds().get(0).getCategory());
//        LegalFactCategory categorySearch = LegalFactCategory.fromValue(timelineElement.getLegalFactsIds().get(0).getCategory());
//        String key = timelineElement.getLegalFactsIds().get(0).getKey();
//        String keySearch = getKeyLegalFact(key);
//
//
//        LegalFactDownloadMetadataResponse legalFactDownloadMetadataResponse = this.b2bClient.getLegalFact(sharedSteps.getNotificationIun(), categorySearch, keySearch);
//
//        Assertions.assertNotNull(legalFactDownloadMetadataResponse);
//
//        return legalFactDownloadMetadataResponse;
//    }

    //TODO MATTEO spostato in LegalFactContentVerifySteps, qua non c'entrava nulla
//    private String downloadLegalFact(String legalFactCategory, boolean pa, boolean appIO, boolean webRecipient, String deliveryDetailCode) {
//        try {
//            Thread.sleep(sharedSteps.getWait());
//        } catch (InterruptedException exc) {
//            throw new RuntimeException(exc);
//        }
//
//        PnTimelineLegalFactV26 categoriesV26 = pnTimelineAndLegalFactV26.getCategory(legalFactCategory);
//        TimelineElementV26 timelineElement = null;
//
//        for (TimelineElementV26 element : sharedSteps.getSentNotificationLastVersion().getTimeline()) {
//            if (!Objects.equals(element.getCategory(), categoriesV26.getTimelineElementInternalCategory())) {
//                continue;
//            }
//
//            if (deliveryDetailCode == null) {
//                timelineElement = element;
//                break;
//            }
//
//            if (element.getDetails() != null && Objects.equals(element.getDetails().getDeliveryDetailCode(), deliveryDetailCode)) {
//                timelineElement = element;
//                break;
//            }
//        }
//
//        try {
//            System.out.println("ELEMENT: " + timelineElement);
//            Assertions.assertNotNull(timelineElement);
//
//            Assertions.assertNotNull(timelineElement.getLegalFactsIds());
//            Assertions.assertFalse(CollectionUtils.isEmpty(timelineElement.getLegalFactsIds()));
//            Assertions.assertEquals(categoriesV26.getLegalFactCategory().getValue(), timelineElement.getLegalFactsIds().get(0).getCategory());
//            LegalFactCategory categorySearch = LegalFactCategory.fromValue(timelineElement.getLegalFactsIds().get(0).getCategory());
//            String key = timelineElement.getLegalFactsIds().get(0).getKey();
//            String finalKeySearch = getKeyLegalFact(key);
//
//            if (pa) {
//                LegalFactDownloadMetadataResponse legalFactDownloadMetadataResponse = Assertions.assertDoesNotThrow(() -> this.b2bClient.getLegalFact(sharedSteps.getNotificationIun(), categorySearch, finalKeySearch));
//                return legalFactDownloadMetadataResponse.getUrl();
//            }
////            if (appIO) {
////                 Assertions.assertDoesNotThrow(() -> this.appIOB2bClient.getLegalFact(sharedSteps.getSentNotification().getIun(), categorySearch.toString(), finalKeySearch,
////                  sharedSteps.getSentNotification().getRecipients().get(0).getTaxId()));
////            }
//            if (webRecipient) {
//                it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.v25.model.LegalFactDownloadMetadataResponse legalFactDownloadMetadataResponse =
//                        Assertions.assertDoesNotThrow(() ->
//                                sharedSteps.getWebRecipientClient().getLegalFact(sharedSteps.getNotificationIun(),
//                                        sharedSteps.deepCopy(categorySearch,
//                                                it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.v25.model.LegalFactCategory.class),
//                                        finalKeySearch
//                                ));
//                System.out.println("NOME FILE PEC RECIPIENT DEST" + legalFactDownloadMetadataResponse.getFilename());
//                return legalFactDownloadMetadataResponse.getUrl();
//            }
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
//        return null;
//    }

    //TODO MATTEO spostato in LegalFactContentVerifySteps, qua non c'entrava nulla
//    private void downloadLegalFactPecRecipient(String legalFactCategory, boolean pa, boolean appIO, boolean webRecipient, String deliveryDetailCode) {
//        try {
//            Thread.sleep(sharedSteps.getWait());
//        } catch (InterruptedException exc) {
//            throw new RuntimeException(exc);
//        }
//
//        TimelineElementV26 timelineElement = null;
//
//        TimelineElementCategoryV26 timelineElementInternalCategory = TimelineElementCategoryV26.SEND_DIGITAL_PROGRESS;
//        LegalFactCategory category = LegalFactCategory.PEC_RECEIPT;
//
//        for (TimelineElementV26 element : sharedSteps.getSentNotificationLastVersion().getTimeline()) {
//            if (!Objects.equals(element.getCategory(), timelineElementInternalCategory)) {
//                continue;
//            }
//
//            if (deliveryDetailCode == null ||
//                    (element.getDetails() != null && Objects.equals(element.getDetails().getDeliveryDetailCode(), deliveryDetailCode))) {
//                timelineElement = element;
//                break;
//            }
//        }
//
//        try {
//            System.out.println("ELEMENT: " + timelineElement);
//            Assertions.assertNotNull(timelineElement);
//
//            Assertions.assertNotNull(timelineElement.getLegalFactsIds());
//            Assertions.assertFalse(CollectionUtils.isEmpty(timelineElement.getLegalFactsIds()));
//            Assertions.assertEquals(category.getValue(), timelineElement.getLegalFactsIds().get(0).getCategory());
//            LegalFactCategory categorySearch = LegalFactCategory.fromValue(timelineElement.getLegalFactsIds().get(0).getCategory());
//            String key = timelineElement.getLegalFactsIds().get(0).getKey();
//            String keySearch = null;
//            //TODO Verificare....
//            if (key.contains("PN_LEGAL_FACTS")) {
//                keySearch = key.substring(key.indexOf("PN_LEGAL_FACTS"));
//            } else if (key.contains("PN_NOTIFICATION_ATTACHMENTS")) {
//                keySearch = key.substring(key.indexOf("PN_NOTIFICATION_ATTACHMENTS"));
//            } else if (key.contains("PN_EXTERNAL_LEGAL_FACTS")) {
//                keySearch = key.substring(key.indexOf("PN_EXTERNAL_LEGAL_FACTS"));
//            } else if (key.contains("PN_PRINTED")) {
//                keySearch = key.substring(key.indexOf("PN_PRINTED"));
//            } else if (key.contains("PN_F24")) {
//                keySearch = key.substring(key.indexOf("PN_F24"));
//            }
//
//            String finalKeySearch = keySearch;
//            if (pa) {
//                LegalFactDownloadMetadataResponse legalFactDownloadMetadataResponse = this.b2bClient.getLegalFact(sharedSteps.getNotificationIun(), categorySearch, finalKeySearch);
//                Assertions.assertNotNull(legalFactDownloadMetadataResponse);
//                Assertions.assertNotNull(legalFactDownloadMetadataResponse.getFilename());
//                Assertions.assertTrue(legalFactDownloadMetadataResponse.getFilename().contains(".eml"));
//            }
//
//            if (webRecipient) {
//
//                it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.v25.model.LegalFactDownloadMetadataResponse legalFactDownloadMetadataResponse =
//                        sharedSteps.getWebRecipientClient().getLegalFact(sharedSteps.getNotificationIun(),
//                                sharedSteps.deepCopy(categorySearch,
//                                        it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.v25.model.LegalFactCategory.class),
//                                finalKeySearch);
//                Assertions.assertNotNull(legalFactDownloadMetadataResponse);
//                Assertions.assertNotNull(legalFactDownloadMetadataResponse.getFilename());
//                Assertions.assertTrue(legalFactDownloadMetadataResponse.getFilename().contains(".eml"));
//            }
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
//    }

    //TODO MATTEO spostato in LegalFactContentVerifySteps, qua non c'entrava nulla
//    private void downloadLegalFactId(String legalFactCategory, boolean pa, boolean appIO, boolean webRecipient, String deliveryDetailCode) {
//        try {
//            Thread.sleep(sharedSteps.getWait());
//        } catch (InterruptedException exc) {
//            throw new RuntimeException(exc);
//        }
//
//        PnTimelineLegalFactV26 categoriesV26 = pnTimelineAndLegalFactV26.getCategory(legalFactCategory);
//
//
//        TimelineElementV26 timelineElement = null;
//
//        for (TimelineElementV26 element : sharedSteps.getSentNotificationLastVersion().getTimeline()) {
//            if (!Objects.equals(element.getCategory(), categoriesV26.getTimelineElementInternalCategory())) {
//                continue;
//            }
//
//            if (deliveryDetailCode == null ||
//                    (element.getDetails() != null && Objects.equals(element.getDetails().getDeliveryDetailCode(), deliveryDetailCode))) {
//                timelineElement = element;
//                break;
//            }
//        }
//
//        try {
//            System.out.println("ELEMENT: " + timelineElement);
//            Assertions.assertNotNull(timelineElement.getLegalFactsIds());
//            Assertions.assertFalse(CollectionUtils.isEmpty(timelineElement.getLegalFactsIds()));
//            Assertions.assertEquals(categoriesV26.getLegalFactCategory().getValue(), timelineElement.getLegalFactsIds().get(0).getCategory());
//            LegalFactCategory categorySearch = LegalFactCategory.fromValue(timelineElement.getLegalFactsIds().get(0).getCategory());
//            String key = timelineElement.getLegalFactsIds().get(0).getKey();
//            String finalKeySearch = getKeyLegalFact(key);
//
//            if (pa) {
//                Assertions.assertDoesNotThrow(() -> this.b2bClient.getDownloadLegalFact(sharedSteps.getNotificationIun(), finalKeySearch));
//            }
////            if (appIO) {
////                 Assertions.assertDoesNotThrow(() -> this.appIOB2bClient.getLegalFact(sharedSteps.getSentNotification().getIun(), categorySearch.toString(), finalKeySearch,
////                        sharedSteps.getSentNotification().getRecipients().get(0).getTaxId()));
////            }
//            if (webRecipient) {
//                Assertions.assertDoesNotThrow(() -> sharedSteps.getWebRecipientClient().getLegalFact(sharedSteps.getNotificationIun(),
//                        sharedSteps.deepCopy(categorySearch,
//                                it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.v25.model.LegalFactCategory.class),
//                        finalKeySearch
//                ));
//            }
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
//    }

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
        FullSentNotificationV26 fullSentNotification = sharedSteps.getSentNotificationLastVersion();

        it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.NotificationFeePolicy notificationFeePolicy =
                fullSentNotification.getNotificationFeePolicy().equals(NotificationFeePolicy.DELIVERY_MODE) ? DELIVERY_MODE : FLAT_RATE;

        NotificationProcessCostResponse notificationProcessCost = this.b2bClient.getNotificationProcessCost(
                sharedSteps.getNotificationIun(),
                destinatario,
                notificationFeePolicy,
                fullSentNotification.getRecipients().get(destinatario).getPayments().get(0).getF24().getApplyCost(),
                fullSentNotification.getPaFee(),
                fullSentNotification.getVat());
        try {
            if (price != null) {
                log.info("Costo notifica: {} destinatario: {}", notificationProcessCost.getAnalogCost(), destinatario);
                Assertions.assertEquals(notificationProcessCost.getAnalogCost(), Integer.parseInt(price));
            }
            if (date != null) {
                Assertions.assertNotNull(notificationProcessCost.getRefinementDate());
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
            sharedSteps.getWebRecipientClient().getReceivedNotification(iun, null);
        } catch (HttpStatusCodeException e) {
            sharedSteps.setNotificationError(e);
        }
    }

    @And("{string} legge la notifica ricevuta")
    public void userReadReceivedNotification(String recipient) {
        sharedSteps.selectUser(recipient);
        String iun = sharedSteps.getNotificationIun();
        Assertions.assertDoesNotThrow(() -> {
            sharedSteps.getWebRecipientClient().getReceivedNotification(iun, null);
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
            if (versione.equalsIgnoreCase("V1")) {
                sharedSteps.getWebRecipientClient().getReceivedNotificationV1(iun, null);
            } else {
                sharedSteps.getWebRecipientClient().getReceivedNotificationV2(iun, null);
            }
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
//        FullSentNotificationV26 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
//        NotificationPriceResponseV23 notificationPrice = this.b2bClient.getNotificationPriceV23(fullSentNotification.getRecipients().get(0).getPayments().get(0).getPagoPa().getCreditorTaxId(),
//                fullSentNotification.getRecipients().get(recipientIndex).getPayments().get(0).getPagoPa().getNoticeCode());
//
//        PaymentEventsRequestPagoPa eventsRequestPagoPa = new PaymentEventsRequestPagoPa();
//
//        PaymentEventPagoPa paymentEventPagoPa = new PaymentEventPagoPa();
//        paymentEventPagoPa.setNoticeCode(fullSentNotification.getRecipients().get(recipientIndex).getPayments().get(0).getPagoPa().getNoticeCode());
//        paymentEventPagoPa.setCreditorTaxId(fullSentNotification.getRecipients().get(0).getPayments().get(0).getPagoPa().getCreditorTaxId());
//        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//        paymentEventPagoPa.setPaymentDate(fmt.format(now()));
//        paymentEventPagoPa.setAmount(notificationPrice.getTotalPrice());
//
//        List<PaymentEventPagoPa> paymentEventPagoPaList = new LinkedList<>();
//        paymentEventPagoPaList.add(paymentEventPagoPa);
//        eventsRequestPagoPa.setEvents(paymentEventPagoPaList);
//        b2bClient.paymentEventsRequestPagoPa(eventsRequestPagoPa);
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
        b2bStepsInterface.checkIfTimelineElementExists(true, CHECK_ATTESTAZIONI_OPPONIBILI, checkFilters);

//        String timelineEventCategory = TimelineElementCategoryV26.NOTIFICATION_VIEWED.getValue();
//        PnPollingServiceTimelineRapidV26 timelineRapidV25 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV25.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            List<TimelineElementV26> listTimelineElement = pnPollingResponseV26
//                    .getNotification()
//                    .getTimeline()
//                    .stream()
//                    .filter(elem -> Objects.requireNonNull(elem.getCategory()).getValue().equals(timelineEventCategory))
//                    .toList();
//            Assertions.assertNotNull(listTimelineElement);
//            Assertions.assertEquals(number, listTimelineElement.size());
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
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
        b2bStepsInterface.checkIfTimelineElementExists(true, CHECK_RESPONSE_STATUS, checkFilters);

//        PnPollingServiceTimelineRapidV26 timelineRapidV25 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV25.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .pnPollingPredicate(getPnPollingPredicateForTimelineV26(timelineEventCategory, recipientIndex))
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            TimelineElementV26 timelineElement = pnPollingResponseV26.getTimelineElement();
//            log.info("TIMELINE_ELEMENT: " + timelineElement);
//            Assertions.assertNotNull(Objects.requireNonNull(Objects.requireNonNull(timelineElement).getDetails()).getResponseStatus());
//            Assertions.assertEquals(timelineElement.getDetails().getResponseStatus().getValue(), responseStatus);
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
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
        b2bStepsInterface.checkIfTimelineElementExists(true, CHECK_RESPONSE_STATUS, checkFilters);

//        PnPollingServiceTimelineRapidV26 timelineRapidV26 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV26.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            TimelineElementV26 timelineElement = pnPollingResponseV26.getTimelineElement();
//            log.info("TIMELINE_ELEMENT: " + timelineElement);
//            Assertions.assertNotNull(Objects.requireNonNull(timelineElement.getDetails()).getResponseStatus());
//            Assertions.assertEquals(timelineElement.getDetails().getResponseStatus().getValue(), responseStatus);
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con responseStatus {string} e digitalAddressSource {string}")
    public void vengonoLettiGliEventiFinoAllElementoDiTimelineDellaNotificaConResponseStatusAndDigitalAddressSource(String timelineEventCategory, String responseStatus, String digitalAddressSource) {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder()
                .responseStatus(responseStatus)
                .digitalAddressSource(digitalAddressSource)
                .build();
        b2bStepsInterface.checkIfTimelineElementExists(true, CHECK_RESPONSE_STATUS, checkFilters);

//        PnPollingServiceTimelineRapidV26 timelineRapidV26 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV26.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            TimelineElementV26 timelineElement = pnPollingResponseV26.getTimelineElement();
//            log.info("TIMELINE_ELEMENT: " + timelineElement);
//            Assertions.assertNotNull(Objects.requireNonNull(timelineElement.getDetails()).getResponseStatus());
//            Assertions.assertEquals(timelineElement.getDetails().getResponseStatus().getValue(), responseStatus);
//            Assertions.assertEquals(timelineElement.getDetails().getDigitalAddressSource().getValue(), digitalAddressSource);
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    @Then("viene verificato che nell'elemento di timeline della notifica {string} siano configurati i campi municipalityDetails e foreignState")
    public void vieneVerificatoCheElementoTimelineSianoConfiguratiCampiMunicipalityDetailsForeignState(String timelineEventCategory) {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(true, CHECK_MUNICIPALITY_AND_FOREIGN_STATE, null);

//        PnPollingServiceTimelineRapidV26 timelineRapidV26 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV26.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            TimelineElementV26 timelineElement = pnPollingResponseV26.getTimelineElement();
//            log.info("TIMELINE_ELEMENT: " + timelineElement);
//            Assertions.assertNotNull(Objects.requireNonNull(timelineElement.getDetails()).getPhysicalAddress().getMunicipality());
//            Assertions.assertNotNull(timelineElement.getDetails().getPhysicalAddress().getForeignState());
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
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
        b2bStepsInterface.checkIfTimelineElementExists(true, CHECK_RESPONSE_STATUS, checkFilters);

//        PnPollingServiceTimelineRapidV26 timelineRapidV26 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV26.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            TimelineElementV26 timelineElement = pnPollingResponseV26.getTimelineElement();
//            log.info("TIMELINE_ELEMENT: " + timelineElement);
//            Assertions.assertNotNull(Objects.requireNonNull(timelineElement.getDetails()).getResponseStatus());
//            Assertions.assertEquals(timelineElement.getDetails().getResponseStatus().getValue(), responseStatus);
//            Assertions.assertNotNull(timelineElement.getDetails().getDeliveryDetailCode());
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
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
        b2bStepsInterface.checkIfTimelineElementExists(true, CHECK_RESPONSE_STATUS, checkFilters);

    }

    @Then("si attende la corretta sospensione dell'invio cartaceo")
    public void siAttendeLaCorrettaSospensioneDellInvioCartaceo() {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, ANALOG_SUCCESS_WORKFLOW, filters);
        b2bStepsInterface.checkIfTimelineElementExists(false, null, null);

//        String timelineEventCategory = TimelineElementCategoryV26.ANALOG_SUCCESS_WORKFLOW.getValue();
//        PnPollingServiceTimelineRapidV26 timelineRapidV25 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV25.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertFalse(pnPollingResponseV26.getResult());
//            Assertions.assertNull(pnPollingResponseV26.getTimelineElement());
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    @Then("si attende il corretto pagamento della notifica")
    public void siAttendeIlCorrettoPagamentoDellaNotifica() {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, PAYMENT, filters);
        b2bStepsInterface.checkIfTimelineElementExists(true, null, null);

//        String iun = sharedSteps.getNotificationIun();
//        String timelineEventCategory = TimelineElementCategoryV26.PAYMENT.getValue();
//        PnPollingServiceTimelineRapidV26 timelineRapidV25 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV25.waitForEvent(iun,
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            log.info("TIMELINE_ELEMENT: " + pnPollingResponseV26.getTimelineElement());
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    @Then("si attende il corretto pagamento della notifica V1")
    public void siAttendeIlCorrettoPagamentoDellaNotificaV1() {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface(NotificationVersion.V1);
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, PAYMENT, filters);
        b2bStepsInterface.checkIfTimelineElementExists(true, null, null);

//        String timelineEventCategory = TimelineElementCategoryV23.PAYMENT.getValue();
//        PnPollingServiceTimelineRapidV1 timelineRapidV1 = (PnPollingServiceTimelineRapidV1) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V1);
//        PnPollingResponseV1 pnPollingResponseV1 = timelineRapidV1.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV1.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV1.getResult());
//            Assertions.assertNotNull(pnPollingResponseV1.getTimelineElement());
//            log.info("TIMELINE_ELEMENT: " + pnPollingResponseV1.getTimelineElement());
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    @Then("si attende il corretto pagamento della notifica V2")
    public void siAttendeIlCorrettoPagamentoDellaNotificaV2() {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface(NotificationVersion.V2);
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, PAYMENT, filters);
        b2bStepsInterface.checkIfTimelineElementExists(true, null, null);

//        String timelineEventCategory = TimelineElementCategoryV23.PAYMENT.getValue();
//        PnPollingServiceTimelineRapidV20 timelineRapidV2 = (PnPollingServiceTimelineRapidV20) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V20);
//        PnPollingResponseV20 pnPollingResponseV20 = timelineRapidV2.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV20.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV20.getResult());
//            Assertions.assertNotNull(pnPollingResponseV20.getTimelineElement());
//            log.info("TIMELINE_ELEMENT: " + pnPollingResponseV20.getTimelineElement());
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    @Then("si attende il corretto pagamento della notifica con l' avviso {int} dal destinatario {int}")
    public void siAttendeIlCorrettoPagamentoDellaNotificaConAvvisoDalDestinatario(Integer avviso, Integer recipientIndex) {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, PAYMENT, filters);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder()
                .recipientIndex(recipientIndex)
                .build();
        b2bStepsInterface.checkIfTimelineElementExists(true, CHECK_PAYMENT_FROM_RECIPIENT_INDEX, checkFilters);

//        String timelineEventCategory = TimelineElementCategoryV26.PAYMENT.getValue();
//        PnPollingServiceTimelineRapidV26 timelineRapidV26 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV26.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            TimelineElementV26 timelineElement = pnPollingResponseV26.getTimelineElement();
//            log.info("TIMELINE_ELEMENT: " + timelineElement);
//            if (Objects.requireNonNull(timelineElement.getDetails()).getRecIndex().equals(recipientIndex)) {
//                boolean esiste = false;
//                if (pnPollingResponseV26.getNotification().getRecipients().get(recipientIndex).getPayments() != null) {
//                    NotificationPaymentItem notificationPaymentItem = pnPollingResponseV26
//                            .getNotification()
//                            .getRecipients()
//                            .get(recipientIndex)
//                            .getPayments()
//                            .stream()
//                            .filter(pay -> Objects.requireNonNull(pay.getPagoPa()).getCreditorTaxId().equals(timelineElement.getDetails().getCreditorTaxId())
//                                    && pay.getPagoPa().getNoticeCode().equals(timelineElement.getDetails().getNoticeCode())).findAny().orElse(null);
//                    esiste = notificationPaymentItem != null;
//                }
//                Assertions.assertTrue(esiste);
//            }
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    @Then("si attende il non corretto pagamento della notifica con l' avviso {int} dal destinatario {int}")
    public void siAttendeIlNonCorrettoPagamentoDellaNotificaConAvvisoDalDestinatario(Integer avviso, Integer recipientIndex) {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, PAYMENT, filters);
        b2bStepsInterface.checkIfTimelineElementExists(false, null, null);

//        String timelineEventCategory = TimelineElementCategoryV26.PAYMENT.getValue();
//        PnPollingServiceTimelineRapidV26 timelineRapidV25 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV25.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertFalse(pnPollingResponseV26.getResult());
//            Assertions.assertNull(pnPollingResponseV26.getTimelineElement());
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    @Then("si attende il corretto pagamento della notifica dell'utente {int}")
    public void siAttendeIlCorrettoPagamentoDellaNotifica(Integer recipientIndex) {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .recipientIndex(recipientIndex)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, PAYMENT, filters);
        b2bStepsInterface.checkIfTimelineElementExists(true, null, null);

//        String timelineEventCategory = TimelineElementCategoryV26.PAYMENT.getValue();
//        PnPollingServiceTimelineRapidV26 timelineRapidV25 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV25.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .pnPollingPredicate(getPnPollingPredicateForTimelineV26(timelineEventCategory, recipientIndex))
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            log.info("TIMELINE_ELEMENT: " + pnPollingResponseV26.getTimelineElement());
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    @Then("verifica presenza in Timeline dei solo pagamenti di avvisi PagoPA del destinatario {int}")
    public void verificaPresenzaPagamentiSoloPagopa(Integer recipientIndex) {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .recipientIndex(recipientIndex)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, PAYMENT, filters);
        b2bStepsInterface.checkIfTimelineElementExists(true, CHECK_ONLY_PAYMENTS_PAGOPA, null);

//        String timelineEventCategory = TimelineElementCategoryV26.PAYMENT.getValue();
//        PnPollingServiceTimelineRapidV26 timelineRapidV25 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV25.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .pnPollingPredicate(getPnPollingPredicateForTimelineV26(timelineEventCategory, recipientIndex))
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            TimelineElementV26 timelineElement = pnPollingResponseV26.getTimelineElement();
//            log.info("TIMELINE_ELEMENT: " + timelineElement);
//            Assertions.assertNull(Objects.requireNonNull(timelineElement.getDetails()).getIdF24());
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    //AL MOMENTO NON ESISTE UNO SCENARIO CHE INTEGRA QUESTO STEP
    @Then("verifica non presenza in Timeline di pagamenti con avvisi F24 del destinatario {int}")
    public void verificaNonPresenzaPagamentiF24(Integer recipientIndex) {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .recipientIndex(recipientIndex)
                .isF24(true)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, PAYMENT, filters);
        b2bStepsInterface.verificaAssenzaPagamentiF24();

//        String timelineEventCategory = TimelineElementCategoryV26.PAYMENT.getValue();
//        PnPollingServiceTimelineRapidV26 timelineRapidV25 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV25.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .pnPollingPredicate(getPnPollingPredicateForTimelineV26(timelineEventCategory, recipientIndex, null, null, null, null, true, false, null, false, null))
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNull(pnPollingResponseV26.getTimelineElement());
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    //AL MOMENTO NON ESISTE UNO SCENARIO CHE INTEGRA QUESTO STEP
    @Then("si attende il non corretto pagamento della notifica dell'utente {int}")
    public void siAttendeIlNonCorrettoPagamentoDellaNotifica(Integer recipientIndex) {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .recipientIndex(recipientIndex)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, PAYMENT, filters);
        b2bStepsInterface.checkIfTimelineElementExists(false, null, null);

//        String timelineEventCategory = TimelineElementCategoryV26.PAYMENT.getValue();
//        PnPollingServiceTimelineRapidV26 timelineRapidV25 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV25.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .pnPollingPredicate(getPnPollingPredicateForTimelineV26(timelineEventCategory, recipientIndex))
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertFalse(pnPollingResponseV26.getResult());
//            Assertions.assertNull(pnPollingResponseV26.getTimelineElement());
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    @Then("viene verificato che nell'elemento di timeline della notifica {string} e' presente il campo Digital Address di piattaforma")
    public void vieneVerificatoCheElementoTimelineSianoConfiguratoCampoDigitalAddressPiattaforma(String timelineEventCategory) {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .attempt("SOURCE_PLATFORM")
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder()
                .withPlatformAddress(true)
                .platformAddress("DSRDNI00A01A225I@pec.pagopa.it")//TODO: mettere dentro Costanti ???
                .build();
        b2bStepsInterface.checkIfTimelineElementExists(true, CHECK_DIGITAL_ADDRESS, checkFilters);

//        PnPollingServiceTimelineRapidV26 timelineRapidV25 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV25.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .pnPollingPredicate(getPnPollingPredicateForTimelineV26(timelineEventCategory, null, null, "SOURCE_PLATFORM", null, null, false, false, null, false, null))
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            TimelineElementV26 timelineElement = pnPollingResponseV26.getTimelineElement();
//            log.info("TIMELINE_ELEMENT: " + timelineElement);
//            Assertions.assertNotNull(Objects.requireNonNull(timelineElement.getDetails()).getDigitalAddress());
//            Assertions.assertFalse("DSRDNI00A01A225I@pec.pagopa.it".equalsIgnoreCase(timelineElement.getDetails().getDigitalAddress().getAddress()));
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    @And("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con deliveryDetailCode {string} per l'utente {int}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCodeAndDestinatario(String timelineEventCategory, String deliveryDetailCode, int recipientIndex) {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .recipientIndex(recipientIndex)
                .deliveryDetailCode(deliveryDetailCode)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(true, null, null);

//        PnPollingServiceTimelineRapidV26 timelineRapidV26 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV26.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .pnPollingPredicate(getPnPollingPredicateForTimelineV26(timelineEventCategory, recipientIndex, deliveryDetailCode, null, null, null, false, false, null, false, null))
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            TimelineElementV26 timelineElementV26 = pnPollingResponseV26.getTimelineElement();
//            log.info("TIMELINE_ELEMENT: " + timelineElementV26);
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    //AL MOMENTO LO SCENARIO CHE INTEGRA QUESTO STEP E' @IGNORE
    @Then("viene verificato che nell'elemento di timeline della notifica {string} sia presente il campo Digital Address")
    public void vieneVerificatoCheElementoTimelineSianoConfiguratoCampoDigitalAddress(String timelineEventCategory) {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .attempt("SOURCE_PLATFORM")
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder().build();
        b2bStepsInterface.checkIfTimelineElementExists(true, CHECK_DIGITAL_ADDRESS, checkFilters);

//        PnPollingServiceTimelineRapidV26 timelineRapidV25 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV25.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .pnPollingPredicate(getPnPollingPredicateForTimelineV26(timelineEventCategory, null, null, "SOURCE_PLATFORM", null, null, false, false, null, false, null))
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            TimelineElementV26 timelineElement = pnPollingResponseV26.getTimelineElement();
//            log.info("TIMELINE_ELEMENT: " + timelineElement);
//            Assertions.assertNotNull(Objects.requireNonNull(timelineElement.getDetails()).getDigitalAddress());
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    @Then("viene verificato che l'ultimo tentativo effettuato abbia indice {int}")
    public void vieneVerificatoCheUltimoTentativoEffettuatoAbbiaIndice(Integer index) {
        getB2bStepsInterface().checkIfLastAttemptMatchesIndex(index);
    }

    //TODO MATTEO: IMPORTANTISSIMO, sostituisce vieneVerificatoElementoTimeline
    @Then("viene verificato che l'elemento di timeline {string} esista")
    public void checkIfTimelineElementExists(String timelineEventCategory, Map<String, String> data) {
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.checkIfTimelineElementExistsFromData(true, timelineEventCategory, data);
    }

//    public void vieneVerificatoElementoTimeline(String timelineEventCategory, @Transpose DataTest dataFromTest) {
//        boolean mustLoadTimeline = dataFromTest != null && dataFromTest.isLoadTimeline();
//        if (mustLoadTimeline) {
//            loadTimeline(timelineEventCategory, true, dataFromTest);
//        }
//        try {
//            List<TimelineElementV26> timelineElements = sharedSteps.getTimelineElementsByEventId(timelineEventCategory, dataFromTest);
//            assertThat(timelineElements)
//                    .withFailMessage("Not found a time element '%s'. IUN: %s".formatted(timelineEventCategory, sharedSteps.getNotificationIun()))
//                    .isNotEmpty();
//
//            if (dataFromTest != null && dataFromTest.getTimelineElement() != null) {
//                boolean atLeastOneSuccessful = false;
//                AssertionFailedError assertionFailedError = null;
//                for (TimelineElementV26 te : timelineElements) {
//                    try {
////                        this.lastTimelineElement = timelineElement;
//                        log.info("TIMELINE_ELEMENT: " + te);
//                        checkTimelineElementEquality(timelineEventCategory, te, dataFromTest);
//
//                        // se si arriva a questo punto, allora l'ultimo check ha avuto successo e non è necessario continuare
//                        atLeastOneSuccessful = true;
//                        break;
//                    } catch (AssertionFailedError e) {
//                        // se si arriva a questo punto allora l'ultimo check ha fallito e ci si prepara al prossimo
//                        assertionFailedError = e;
//                    }
//                }
//
//                // se nessun confronto ha avuto successo allora di certo sarà stata lanciata un'eccezione
//                if (!atLeastOneSuccessful) {
//                    // si rilancia l'ultima eccezione catturata
//                    throw assertionFailedError;
//                }
//            }
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
//    }

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

//        DataTest dataTest = new DataTest();
//        TimelineElementV26 testTimelineElement = new TimelineElementV26();
//        TimelineElementDetailsV26 timelineElementDetails = new TimelineElementDetailsV26();
//
//        timelineElementDetails.deliveryDetailCode(eventId);
//        testTimelineElement.details(timelineElementDetails);
//        dataTest.setTimelineElement(testTimelineElement);
//
//        TimelineElementV26 timelineElement = sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataTest);
//        try {
//            Assertions.assertNotNull(timelineElement, "Not found the time element (%s,%s)".formatted(timelineEventCategory, eventId));
//
//            String fieldValue = getProperty(fieldPath, timelineElement);
//            Assertions.assertNotNull(fieldValue, "Field %s has NULL value in timeline element".formatted(fieldPath));
//
//            Assertions.assertTrue(fieldValue.matches(regex), "Field %s with value %s does not match regex %s".formatted(fieldPath, fieldValue, regex));
//        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
//            sharedSteps.throwAssertionErrorWithIUN(new AssertionFailedError("Error accessing field %s".formatted(fieldPath)));
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
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

//        try {
//            Assertions.assertNotNull(timelineElement,
//                    "There is no time element to analyze. Remember that this proposition is made "
//                            + "to be called after another that get a timeline event, such as "
//                            + "'it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheB2bSteps.vieneVerificatoElementoTimeline'");
//
//            String fieldValue = getProperty(fieldPath, timelineElement);
//            Assertions.assertNotNull(fieldValue,
//                    "Field %s has NULL value in timeline element".formatted(fieldPath));
//
//            Assertions.assertTrue(fieldValue.matches(regex),
//                    "Field %s with value %s does not match regex %s".formatted(fieldPath, fieldValue,
//                            regex));
//        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
//            sharedSteps.throwAssertionErrorWithIUN(
//                    new AssertionFailedError("Error accessing field %s".formatted(fieldPath)));
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

//    private String getProperty(String fieldPath, TimelineElementV26 lastTimelineElement)
//            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
//        String sanitizedFieldPath = fieldPath.replace("_", ".");
//        return BeanUtils.getProperty(lastTimelineElement, sanitizedFieldPath);
//    }


    @Then("viene verificato che la data della timeline REFINEMENT sia ricezione della raccomandata + 10gg")
    public void verificationDateScheduleRefinementWithRefinementPlus10Days() {

        try {
            FullSentNotificationV26 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
            OffsetDateTime scheduleDate = fullSentNotification.getTimeline().stream().filter(elem -> elem.getCategory().getValue().equals(SEND_ANALOG_FEEDBACK)).findAny().get().getTimestamp().plus(sharedSteps.getSchedulingDaysSuccessAnalogRefinement());
            OffsetDateTime refinementDate = fullSentNotification.getTimeline().stream().filter(elem -> elem.getCategory().getValue().equals(REFINEMENT)).findAny().get().getTimestamp();
            log.info("scheduleDate : {}", scheduleDate);
            log.info("refinementDate : {}", refinementDate);

            Assertions.assertEquals(scheduleDate, refinementDate);

        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    @And("viene verificato che l'elemento di timeline {string} non esista")
    public void vieneVerificatoCheElementoTimelineNonEsista(String timelineEventCategory, Map<String, String> dataMap) {

        getB2bStepsInterface().verifyTimelineElementDoesNotExists(true, timelineEventCategory, dataMap);

        //TODO MATTEO TEST
//        loadTimeline(timelineEventCategory, false, dataTest);
//        TimelineElementV26 timelineElement = sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataMap);
//        try {
//            log.info("TIMELINE_ELEMENT: " + timelineElement);
//            Assertions.assertNull(timelineElement);
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    /* TODO 12/02/2025 Accorpare con vieneVerificatoCheElementoTimelineNonEsista(String timelineEventCategory, @Transpose DataTest dataFromTest)
        parametrizzando il load della timeline. */
    @And("viene verificato che l'elemento di timeline {string} non esista nella timeline caricata")
    public void vieneVerificatoCheElementoTimelineNonEsistaNotLoadTimeline(String timelineEventCategory, Map<String, String> dataMap) {

        getB2bStepsInterface().verifyTimelineElementDoesNotExists(false, timelineEventCategory, dataMap);

        //TODO MATTEO TEST
//        TimelineElementV26 timelineElement = sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataMap);
//        try {
//            log.info("TIMELINE_ELEMENT: " + timelineElement);
//            Assertions.assertNull(timelineElement);
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    @And("viene schedulato il perfezionamento per decorrenza termini per il caso {string}")
    public void vieneSchedulatoIlPerfezionamento(String timelineCategory, @Transpose DataTest dataFromTest) {

        TimelineElementV26 timelineElement = sharedSteps.getTimelineElementByEventId(SCHEDULE_REFINEMENT, dataFromTest);

        TimelineElementV26 timelineElementForDateCalculation = null;
        if (timelineCategory.equals(DIGITAL_SUCCESS_WORKFLOW)) {
            timelineElementForDateCalculation = sharedSteps.getTimelineElementByEventId(SEND_DIGITAL_FEEDBACK, dataFromTest);
        } else if (timelineCategory.equals(DIGITAL_FAILURE_WORKFLOW)) {
            timelineElementForDateCalculation = sharedSteps.getTimelineElementByEventId(DIGITAL_DELIVERY_CREATION_REQUEST, dataFromTest);
        } else if (timelineCategory.equals(ANALOG_SUCCESS_WORKFLOW)) {
            timelineElementForDateCalculation = sharedSteps.getTimelineElementByEventId(SEND_ANALOG_FEEDBACK, dataFromTest);
        } else if (timelineCategory.equals(ANALOG_FAILURE_WORKFLOW)) {
            timelineElementForDateCalculation = sharedSteps.getTimelineElementByEventId(SEND_ANALOG_FEEDBACK, dataFromTest);
        }

        Assertions.assertNotNull(timelineElementForDateCalculation);

        OffsetDateTime notificationDate = null;
        Duration schedulingDaysRefinement = null;

        if (timelineCategory.equals(DIGITAL_SUCCESS_WORKFLOW)) {
            notificationDate = timelineElementForDateCalculation.getDetails().getNotificationDate();
            schedulingDaysRefinement = sharedSteps.getSchedulingDaysSuccessDigitalRefinement();
        } else if (timelineCategory.equals(DIGITAL_FAILURE_WORKFLOW)) {
            notificationDate = timelineElementForDateCalculation.getTimestamp();
            schedulingDaysRefinement = sharedSteps.getSchedulingDaysFailureDigitalRefinement();
        } else if (timelineCategory.equals(ANALOG_SUCCESS_WORKFLOW)) {
            notificationDate = timelineElementForDateCalculation.getTimestamp();
            schedulingDaysRefinement = sharedSteps.getSchedulingDaysSuccessAnalogRefinement();
        } else if (timelineCategory.equals(ANALOG_FAILURE_WORKFLOW)) {
            notificationDate = timelineElementForDateCalculation.getDetails().getNotificationDate();
            schedulingDaysRefinement = sharedSteps.getSchedulingDaysFailureAnalogRefinement();
        }

        OffsetDateTime schedulingDate = notificationDate.plus(schedulingDaysRefinement);
        int hour = schedulingDate.getHour();
        int minutes = schedulingDate.getMinute();
        if ((hour == 21 && minutes > 0) || hour > 21) {
            Duration timeToAddInNonVisibilityTimeCase = sharedSteps.getTimeToAddInNonVisibilityTimeCase();
            schedulingDate = schedulingDate.plus(timeToAddInNonVisibilityTimeCase);
        }
        DateTimeFormatter fmt1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        System.out.println(timelineElement.getDetails().getSchedulingDate().format(fmt1));
        System.out.println(schedulingDate.format(fmt1));

        OffsetDateTime expectedDate = timelineElement.getDetails().getSchedulingDate();
        assertThat(expectedDate).isCloseTo(schedulingDate, within(5, MINUTES));
    }

    @And("si attende che sia presente il perfezionamento per decorrenza termini")
    public void siAttendePresenzaPerfezionamentoDecorrenzaTermini(@Transpose DataTest dataFromTest) throws InterruptedException {
        String iun = sharedSteps.getNotificationIun();
        if (dataFromTest != null && dataFromTest.getTimelineElement() != null) {
            TimelineElementV26 timelineElement = sharedSteps.getTimelineElementByEventId(SCHEDULE_REFINEMENT, dataFromTest);

            OffsetDateTime schedulingDate = timelineElement.getDetails().getSchedulingDate();
            OffsetDateTime currentDate = now().atZoneSameInstant(ZoneId.of("UTC")).toOffsetDateTime();
            long remainingTime = ChronoUnit.MILLIS.between(currentDate, schedulingDate);
            if (remainingTime > 0) {
                Thread.sleep(remainingTime + 30 * 1000);
            }
            // get the updated notification
            FullSentNotificationV26 fullSentNotification = b2bClient.getSentNotificationV26(iun);
            Assertions.assertNotNull(fullSentNotification);
        }
    }

    @And("si attende che si ritenti l'invio dopo l'evento {string}")
    public void siAttendeCheSiRitentiInvio(String timelineEventCategory, @Transpose DataTest dataFromTest) throws InterruptedException {
        String iun = sharedSteps.getNotificationIun();
        if (dataFromTest != null && dataFromTest.getTimelineElement() != null) {

            TimelineElementV26 timelineElement = sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataFromTest);

            OffsetDateTime firstSend = timelineElement.getTimestamp();
            Duration secondNotificationWorkflowWaitingTime = sharedSteps.getSecondNotificationWorkflowWaitingTime();
            OffsetDateTime nextSend = firstSend.plus(secondNotificationWorkflowWaitingTime);
            OffsetDateTime currentDate = now().atZoneSameInstant(ZoneId.of("UTC")).toOffsetDateTime();
            long remainingTime = ChronoUnit.MILLIS.between(currentDate, nextSend);
            if (remainingTime > 0) {
                Thread.sleep(remainingTime + 30 * 1000);
            }
            // get the updated notification
            FullSentNotificationV26 fullSentNotification = b2bClient.getSentNotificationV26(iun);
            Assertions.assertNotNull(fullSentNotification);
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
        Assertions.assertNotNull(notificationFailed);
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
        b2bStepsInterface.checkIfTimelineElementExists(true, CHECK_PHYSICAL_ADDRESS, checkFilters);

//        PnPollingServiceTimelineRapidV26 timelineRapidV25 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV25.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .pnPollingPredicate(getPnPollingPredicateForTimelineV26(timelineEventCategory, recipientIndex, deliveryDetailCode, null, documentType, null, false, true, legalFactIdCategory, true, null))
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            TimelineElementV26 timelineElement = pnPollingResponseV26.getTimelineElement();
//            log.info("TIMELINE_ELEMENT: " + timelineElement);
//            Assertions.assertNotNull(Objects.requireNonNull(timelineElement.getDetails()).getPhysicalAddress());
//            Assertions.assertTrue(timelineElement.getDetails().getPhysicalAddress().getAddress().matches("^[A-Z0-9_.\\-:@' \\[\\]]*$"));
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
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
        b2bStepsInterface.checkIfTimelineElementExists(true, null, null);

//        List<String> failureCauses = Arrays.asList(deliveryFailureCause.split(" "));
//        PnPollingServiceTimelineRapidV26 timelineRapidV25 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV25.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .pnPollingPredicate(getPnPollingPredicateForTimelineV26(timelineEventCategory, recipientIndex, deliveryDetailCode, null, null, null, false, false, null, false, failureCauses))
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            log.info("TIMELINE_ELEMENT: " + pnPollingResponseV26.getTimelineElement());
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
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
        b2bStepsInterface.checkIfTimelineElementExists(true, null, null);

//        PnPollingServiceTimelineRapidV26 timelineRapidV25 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV25.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .pnPollingPredicate(getPnPollingPredicateForTimelineV26(timelineEventCategory, recipientIndex, deliveryDetailCode, null, null, null, false, false, null, false, null))
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            log.info("TIMELINE_ELEMENT: " + pnPollingResponseV26.getTimelineElement());
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    @And("controlla che il timestamp di {string} sia dopo quello di invio e di attesa di lettura del messaggio di cortesia")
    public void verificaTimestamp(String timelineEventCategory, @Transpose DataTest dataFromTest) {

        TimelineElementV26 timelineElementCategory = getAndStoreTimelineByB2b(timelineEventCategory, dataFromTest);
        TimelineElementV26 timelineElementSendCourtesyMessage = getAndStoreTimelineByB2b("SEND_COURTESY_MESSAGE", dataFromTest);


        Duration waitingForReadCourtesyMessage = sharedSteps.getWaitingForReadCourtesyMessage();

        OffsetDateTime timestampEventCategory = timelineElementCategory.getTimestamp();
        OffsetDateTime timestampEventSendCourtesyMessage = timelineElementSendCourtesyMessage.getTimestamp();
        OffsetDateTime timestampEventSendCourtesyMessageWithWaitingTime = timestampEventSendCourtesyMessage.plus(waitingForReadCourtesyMessage);

        boolean test = timestampEventCategory.isEqual(timestampEventSendCourtesyMessageWithWaitingTime) || timestampEventCategory.isAfter(timestampEventSendCourtesyMessageWithWaitingTime);

        log.info("timestamp " + timelineEventCategory + ": " + timestampEventCategory);
        log.info("timestamp SEND_COURTESY_MESSAGE ( +" + waitingForReadCourtesyMessage + " minutes): " + timestampEventSendCourtesyMessageWithWaitingTime);
        log.info("timestamp " + timelineEventCategory + " is after or equal timestamp SEND_COURTESY_MESSAGE?: " + test);

        Assertions.assertTrue(test);
    }


    //TODO MATTEO spostato in LegalFactContentVerifySteps, qua non c'entrava nulla
//    @Then("download attestazione opponibile AAR e controllo del contenuto del file per verificare se il tipo è {string}")
//    public void downloadAttestazioneOpponibileAAREControlloDelContenutoDelFilePerVerificareSeIlTipoE(String aarType) {
//        LegalFactDownloadMetadataResponse legalFactDownloadMetadataResponse = getLegalFactIdAAR("PN_AAR");
//        byte[] source = utils.downloadFile(legalFactDownloadMetadataResponse.getUrl());
//        Assertions.assertNotNull(source);
//        Assertions.assertTrue(checkTypeAAR(source, aarType));
//    }

    //TODO MATTEO spostato in LegalFactContentVerifySteps, qua non c'entrava nulla
//    @And("download attestazione opponibile AAR")
//    public void downloadLegalFactIdAAR() {
//        getLegalFactIdAAR("PN_AAR");
//    }

    //TODO MATTEO spostato in LegalFactContentVerifySteps, qua non c'entrava nulla
//    private LegalFactDownloadMetadataResponse getLegalFactIdAAR(String aarType) {
//        AtomicReference<LegalFactDownloadMetadataResponse> legalFactDownloadMetadataResponse = new AtomicReference<>();
//        try {
//            Thread.sleep(sharedSteps.getWait());
//        } catch (InterruptedException exc) {
//            throw new RuntimeException(exc);
//        }
//
//        TimelineElementV26 timelineElement = null;
//        for (TimelineElementV26 element : sharedSteps.getSentNotificationLastVersion().getTimeline()) {
//            if (Objects.requireNonNull(element.getCategory().getValue()).equals(AAR_GENERATION)) {
//                timelineElement = element;
//                break;
//            }
//        }
//
//        Assertions.assertNotNull(timelineElement);
//        String keySearch = null;
//        if (!Objects.requireNonNull(timelineElement.getDetails()).getGeneratedAarUrl().isEmpty()) {
//
//            if (timelineElement.getDetails().getGeneratedAarUrl().contains(aarType)) {
//                keySearch = timelineElement.getDetails().getGeneratedAarUrl().substring(timelineElement.getDetails().getGeneratedAarUrl().indexOf(aarType));
//            }
//
//            String finalKeySearch = keySearch;
//            try {
//                Assertions.assertDoesNotThrow(() -> legalFactDownloadMetadataResponse.set(
//                        this.b2bClient.getDownloadLegalFact(sharedSteps.getNotificationIun(), finalKeySearch)));
//            } catch (AssertionFailedError assertionFailedError) {
//                sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//            }
//        }
//        return legalFactDownloadMetadataResponse.get();
//    }

    //TODO MATTEO spostato in LegalFactContentVerifySteps, qua non c'entrava nulla
//    private boolean checkTypeAAR(byte[] source, String aarType) {
//        Pattern pattern = Pattern.compile("\\((CAF)\\s");
//        try (final PDDocument document = Loader.loadPDF(source)) {
//            final PDFTextStripper pdfStripper = new PDFTextStripper();
//            pdfStripper.setSortByPosition(true);
//            String extractedText = pdfStripper.getText(document);
//            Matcher matcher = pattern.matcher(extractedText);
//            if (aarType.equals("AAR")) {  //if AAR then check ' CAF ' pattern NOT exist
//                return !matcher.find();
//            } else if (aarType.equals("AAR RADD")) { //if AAR RADD then check ' CAF ' pattern exist
//                return matcher.find();
//            }
//        } catch (Exception exception) {
//            log.error("Error parsing PDF {}", exception);
//        }
//        return false;
//    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} e verifica indirizzo secondo tentativo {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithVerifyPhysicalAddress(String timelineEventCategory, String attempt) {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .deliveryDetailCode(null)
                .attempt(attempt)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_SLOW, TIMELINE, timelineEventCategory, filters);
        TimelineElementCheckFilters checkFilters = TimelineElementCheckFilters.builder()
                .physicalAddressRegex(PHYSICAL_ADDRESS_REGEX)
                .build();
        b2bStepsInterface.checkIfTimelineElementExists(true, CHECK_PHYSICAL_ADDRESS, checkFilters);

//        PnPollingServiceTimelineSlowV26 timelineSlowV25 = (PnPollingServiceTimelineSlowV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_SLOW_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineSlowV25.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .pnPollingPredicate(getPnPollingPredicateForTimelineV26(timelineEventCategory, null, attempt))
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            TimelineElementV26 timelineElement = pnPollingResponseV26.getTimelineElement();
//            log.info("TIMELINE_ELEMENT: " + timelineElement);
//            Assertions.assertNotNull(Objects.requireNonNull(timelineElement.getDetails()).getPhysicalAddress());
//            Assertions.assertTrue(timelineElement.getDetails().getPhysicalAddress().getAddress().matches("^[A-Z0-9_.\\-:;@' \\[\\] ]*$"));
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} al tentativo {string}")
    public void readingEventUpToTheTimelineElementOfNotificationAtAttempt(String timelineEventCategory, String attempt) {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder()
                .deliveryDetailCode(null)
                .attempt(attempt)
                .build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_SLOW, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(true, null, null);

//        PnPollingServiceTimelineSlowV26 timelineSlowV25 = (PnPollingServiceTimelineSlowV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_SLOW_V26);
//
//        PnPollingResponseV26 pnPollingResponseV26 = timelineSlowV25.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .pnPollingPredicate(getPnPollingPredicateForTimelineV26(timelineEventCategory, null, attempt))
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            log.info("TIMELINE_ELEMENT: " + pnPollingResponseV26.getTimelineElement());
//            timelineElement = pnPollingResponseV26.getTimelineElement();
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
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
        b2bStepsInterface.checkIfTimelineElementExists(true, CHECK_FAILURE_CAUSE, checkFilters);

//        PnPollingServiceTimelineRapidV26 timelineRapidV25 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV25.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            TimelineElementV26 timelineElement = pnPollingResponseV26.getTimelineElement();
//            log.info("TIMELINE_ELEMENT: " + timelineElement);
//            Assertions.assertEquals(Objects.requireNonNull(timelineElement.getDetails()).getFailureCause(), failureCause);
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
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
        b2bStepsInterface.checkIfTimelineElementExists(true, CHECK_FAILURE_CAUSE, checkFilters);

//        PnPollingServiceTimelineSlowV26 timelineSlowV25 = (PnPollingServiceTimelineSlowV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_SLOW_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineSlowV25.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .pnPollingPredicate(getPnPollingPredicateForTimelineV26(timelineEventCategory, recipientIndex))
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            TimelineElementV26 timelineElement = pnPollingResponseV26.getTimelineElement();
//            log.info("TIMELINE_ELEMENT: " + timelineElement);
//            Assertions.assertEquals(Objects.requireNonNull(timelineElement.getDetails()).getFailureCause(), failureCause);
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
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
        b2bStepsInterface.checkIfTimelineElementExists(true, CHECK_SCHEDULING_DATE_RISPETTO_A_EVENTO, checkFilters);

//        PnPollingServiceTimelineRapidV26 timelineRapidV25 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV25.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .pnPollingPredicate(getPnPollingPredicateForTimelineV26(timelineEventCategory, recipientIndex))
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            TimelineElementV26 timelineElement = pnPollingResponseV26.getTimelineElement();
//            Assertions.assertNotNull(Objects.requireNonNull(timelineElement.getDetails()).getSchedulingDate());
//            log.info("TIMELINE ELEMENT: {} , DETAILS {} , SCHEDULING DATE {}",
//                    timelineElement, Objects.requireNonNull(timelineElement).getDetails(), Objects.requireNonNull(timelineElement.getDetails()).getSchedulingDate());
//            //RECUPERO Data DeliveryCreationRequest
//            long delayMillis = 0;
//            OffsetDateTime digitalDeliveryCreationRequestDate = null;
//            for (TimelineElementV26 element : sharedSteps.getSentNotificationLastVersion().getTimeline()) {
//                if (Objects.requireNonNull(element.getCategory()).getValue().equals("DIGITAL_DELIVERY_CREATION_REQUEST") && Objects.requireNonNull(element.getDetails()).getRecIndex().equals(recipientIndex) && evento.equalsIgnoreCase("DIGITAL_DELIVERY_CREATION_REQUEST")) {
//                    digitalDeliveryCreationRequestDate = element.getTimestamp();
//                    delayMillis = sharedSteps.getSchedulingDaysFailureDigitalRefinement().toMillis();
//                    break;
//                } else if (element.getCategory().getValue().equals("SEND_DIGITAL_FEEDBACK") && Objects.requireNonNull(element.getDetails()).getRecIndex().equals(recipientIndex) && evento.equalsIgnoreCase("SEND_DIGITAL_FEEDBACK")) {
//                    digitalDeliveryCreationRequestDate = element.getDetails().getNotificationDate();
//                    delayMillis = "OK".equalsIgnoreCase(element.getDetails().getResponseStatus().getValue()) ? sharedSteps.getSchedulingDaysSuccessDigitalRefinement().toMillis() : sharedSteps.getSchedulingDaysFailureDigitalRefinement().toMillis();
//                    break;
//                }
//            }
//            Long schedulingDateMillis = timelineElement.getDetails().getSchedulingDate().toInstant().toEpochMilli();
//            Long digitalDeliveryCreationMillis = Objects.requireNonNull(digitalDeliveryCreationRequestDate).toInstant().toEpochMilli();
//            long diff = schedulingDateMillis - digitalDeliveryCreationMillis;
//            long delta = Long.valueOf(sharedSteps.getSchedulingDelta());
//            log.info("PRE-ASSERTION: iun={} schedulingDateMillis={}, digitalDeliveryCreationMillis={}, diff={}, delayMillis={}, delta={}",
//                    sharedSteps.getNotificationIun(), schedulingDateMillis, digitalDeliveryCreationMillis, diff, delayMillis, delta);
//            Assertions.assertTrue(diff <= delayMillis + delta && diff >= delayMillis - delta, "le tempistiche di arrivo tra gli elementi cercati non sono corrette");
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    @Then("viene verificato che nell'elemento di timeline della notifica {string} sia presente il campo notRefinedRecipientIndex")
    public void vieneVerificatoCheElementoTimelineSianoConfiguratoCampoNotRefinedRecipientIndex(String timelineEventCategory) {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface();
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(true, CHECK_NOT_REFINED_RECIPIENT_INDEX, null);

//        PnPollingServiceTimelineRapidV26 timelineRapidV26 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV26.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            TimelineElementV26 timelineElement = pnPollingResponseV26.getTimelineElement();
//            log.info("TIMELINE_ELEMENT: " + timelineElement);
//            Assertions.assertNotNull(Objects.requireNonNull(timelineElement.getDetails()).getNotRefinedRecipientIndexes());
//            Assertions.assertFalse(timelineElement.getDetails().getNotRefinedRecipientIndexes().isEmpty());
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    @Then("viene verificato che il campo {string} sia valorizzato a {int}")
    public void notificationPriceVerificationValueResponse(String toValidate, Integer valueToValidate) {
        try {
            FullSentNotificationV26 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
            Assertions.assertNotNull(fullSentNotification);

            switch (toValidate.toLowerCase()) {
                case "vat" -> Assertions.assertEquals(valueToValidate, fullSentNotification.getVat());
                case "pafee" -> Assertions.assertEquals(valueToValidate, fullSentNotification.getPaFee());
                default -> throw new IllegalArgumentException("Valore non valido per toValidate: " + toValidate);
            }

        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    @Then("viene verificato che tutti i campi per il calcolo del iva per il destinatario {int} siano valorizzati")
    public void notificationPriceVerificationResponse(Integer destinatario) {
        List<NotificationPaymentItem> listNotificationPaymentItem = sharedSteps.getSentNotificationLastVersion().getRecipients().get(destinatario).getPayments();

        for (NotificationPaymentItem pagamento : listNotificationPaymentItem) {
            NotificationPriceResponseV23 notificationPrice = this.b2bClient.getNotificationPriceV23(pagamento.getPagoPa().getCreditorTaxId(), pagamento.getPagoPa().getNoticeCode());
            try {
                Assertions.assertNotNull(notificationPrice.getTotalPrice());
                Assertions.assertNotNull(notificationPrice.getPartialPrice());
                Assertions.assertNotNull(notificationPrice.getIun());
                Assertions.assertNotNull(notificationPrice.getAnalogCost());
                Assertions.assertNotNull(notificationPrice.getRefinementDate());
                Assertions.assertNotNull(notificationPrice.getNotificationViewDate());
                Assertions.assertNotNull(notificationPrice.getSendFee());
                Assertions.assertNotNull(notificationPrice.getPaFee());
                Assertions.assertNotNull(notificationPrice.getVat());
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
                Assertions.assertNotNull(notificationPriceV23.getTotalPrice());
                Assertions.assertNotNull(notificationPriceV23.getPartialPrice());
                Assertions.assertNotNull(notificationPriceV23.getIun());
                Assertions.assertNotNull(notificationPriceV23.getAnalogCost());
                Assertions.assertNotNull(notificationPriceV23.getPaFee());
                Assertions.assertNotNull(notificationPriceV23.getVat());
                Assertions.assertEquals(vat, notificationPriceV23.getVat());
                Assertions.assertEquals(paFee, notificationPriceV23.getPaFee());
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
                    Assertions.assertEquals(notificationPrice.getIun(), sharedSteps.getNotificationIun());
                    Assertions.assertNotNull(notificationPrice.getNotificationViewDate());

                } catch (AssertionFailedError assertionFailedError) {
                    sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
                }
            }
        }
    }

    //TODO MATTEO: i seguenti metodi sono stati assorbiti da "getPnPollingPredicateForTimeline" all'interno delle classi che implementano B2bStepsInterface.
    // anziché avere 4 metodi con overload (di cui il quarto con 11 parametri), ne è stato fatto uno unico che prende la stringa timelineEventCategory e il
    // nuovo oggetto WaitForEventPredicateFilters, che altro non è che un contenitore per tutti quei parametri)

//    private PnPollingPredicate getPnPollingPredicateForTimelineV26(String timelineEventCategory, Integer destinatario) {
//        return getPnPollingPredicateForTimelineV26(timelineEventCategory, destinatario, null, null, null, null, false, false, null, false, null);
//    }

//    private PnPollingPredicate getPnPollingPredicateForTimelineV26(String timelineEventCategory, String deliveryDetailCode) {
//        return getPnPollingPredicateForTimelineV26(timelineEventCategory, null, deliveryDetailCode, null, null, null, false, false, null, false, null);
//    }

//    private PnPollingPredicate getPnPollingPredicateForTimelineV26(String timelineEventCategory, String deliveryDetailCode, String attempt) {
//        return getPnPollingPredicateForTimelineV26(timelineEventCategory, null, deliveryDetailCode, attempt, null, null, false, false, null, false, null);
//    }

//    private PnPollingPredicate getPnPollingPredicateForTimelineV26(String timelineEventCategory, Integer destinatario, String deliveryDetailCode, String attempt, String tipoDoc, String responseStatus, boolean isF24, boolean isLegalFactEmpty, String legalFactIdCategory, boolean isAttachmentEmpty, List<String> failureCauses) {
//        PnPollingPredicate pnPollingPredicate = new PnPollingPredicate();
//        pnPollingPredicate.setTimelineElementPredicateV26(
//                timelineElementV26 ->
//                        timelineElementV26.getCategory() != null
//                                && (timelineEventCategory == null || Objects.requireNonNull(timelineElementV26.getCategory().getValue()).equals(timelineEventCategory))
//                                && (destinatario == null || Objects.requireNonNull(Objects.requireNonNull(timelineElementV26.getDetails()).getRecIndex()).equals(destinatario))
//                                && (deliveryDetailCode == null || Objects.equals(Objects.requireNonNull(timelineElementV26.getDetails()).getDeliveryDetailCode(), deliveryDetailCode))
//                                && (attempt == null || Objects.requireNonNull(timelineElementV26.getElementId()).contains(attempt))
//                                && (tipoDoc == null || Objects.equals(Objects.requireNonNull(Objects.requireNonNull(timelineElementV26.getDetails()).getAttachments()).get(0).getDocumentType(), tipoDoc))
//                                && (responseStatus == null || Objects.requireNonNull(Objects.requireNonNull(timelineElementV26.getDetails()).getResponseStatus().getValue()).equals(responseStatus))
//                                && (!isF24 || Objects.requireNonNull(timelineElementV26.getDetails()).getIdF24() != null)
//                                && (!isLegalFactEmpty || Objects.nonNull(timelineElementV26.getLegalFactsIds()) && !timelineElementV26.getLegalFactsIds().isEmpty())
//                                && (legalFactIdCategory == null || Objects.requireNonNull(Objects.requireNonNull(timelineElementV26.getLegalFactsIds()).get(0)).getCategory().equals(legalFactIdCategory))
//                                && (!isAttachmentEmpty || Objects.nonNull(Objects.requireNonNull(timelineElementV26.getDetails()).getAttachments()) && !timelineElementV26.getDetails().getAttachments().isEmpty())
//                                && (legalFactIdCategory == null || failureCauses.contains(Objects.requireNonNull(Objects.requireNonNull(timelineElementV26.getDetails()).getDeliveryFailureCause())))
//        );
//        return pnPollingPredicate;
//    }


    @And("viene verificato il costo di {int} e il peso di {int} nei details dell'elemento di timeline letto")
    public void verifyPriceAndWeightInvioCartaceo(Integer price, Integer weight) {
        getB2bStepsInterface().verifyPriceAndWeightInvioCartaceo(price, weight);
    }


    @And("viene verificato che il peso della busta cartacea sia di {int}")
    public void verifyPriceAndWeightInvioCartaceo(Integer weight) {
        getB2bStepsInterface().verifyPriceAndWeightInvioCartaceo(null, weight);
    }

//    private PnPollingResponseV26 getPollingResponse(String timelineEventCategory, String deliveryDetailCode) {
//        PnPollingServiceTimelineRapidV26 timelineRapidV26 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//
//        return timelineRapidV26.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .pnPollingPredicate(getPnPollingPredicateForTimelineV26(timelineEventCategory, deliveryDetailCode))
//                        .build());
//    }

    @And("viene verificato che il timestamp dell'evento {string} sia immediatamente successivo a quello dell'evento {string} con una differenza massima di {int} secondi")
    public void confrontoTimestampEventi(String nextTimelineEvent, String previousTimelineEvent, Integer delta) {
        FullSentNotificationV26 fullSentNotificationV26 = sharedSteps.getSentNotificationLastVersion();
        List<TimelineElementV26> timelineElements = fullSentNotificationV26.getTimeline();

        Optional<TimelineElementV26> timelineElementV26OptionalNext = timelineElements.stream()
                .filter(element -> element.getCategory() != null && element.getCategory().toString().equals(nextTimelineEvent))
                .findFirst();
        Optional<TimelineElementV26> timelineElementV26OptionalPrevious = timelineElements.stream()
                .filter(element -> element.getCategory() != null && element.getCategory().toString().equals(previousTimelineEvent))
                .findFirst();
        Assertions.assertTrue(timelineElementV26OptionalNext.isPresent() && timelineElementV26OptionalPrevious.isPresent());

        Long timestampNext = timelineElementV26OptionalNext.get().getTimestamp().toInstant().toEpochMilli();
        Long timeStampPrevious = timelineElementV26OptionalPrevious.get().getTimestamp().toInstant().toEpochMilli();
        Long diffMillis = timestampNext - timeStampPrevious;
        delta = delta * 1000;

        log.info("PRE-ASSERTION: iun={} nextTimelineEvent={}, previousTimelineEvent={}, diffMillis={}, delta={}",
                sharedSteps.getNotificationIun(), timestampNext, timeStampPrevious, diffMillis, delta);
        Assertions.assertTrue(diffMillis <= delta);
    }

    @Then("esiste l'elemento di timeline della notifica {string} abbia notificationCost uguale a {string} per l'utente {int}")
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
        b2bStepsInterface.checkIfTimelineElementExists(true, CHECK_NOTIFICATION_COST_FOR_USER, checkFilters);

//        TimelineElementV26 event = readingEventUpToTheTimelineElementOfNotificationForCategoryUser(timelineEventCategory, recipientIndex);
//        Long notificationCost = event.getDetails().getNotificationCost();
//
//        if (cost.equalsIgnoreCase("null")) {
//            assertThat(notificationCost)
//                    .as("Il notificationCost dovrebbe essere null per la categoria '%s' e destinatario '%d'", timelineEventCategory, recipientIndex)
//                    .isNull();
//        } else if (cost.equalsIgnoreCase("NotNull")) {
//            assertThat(notificationCost)
//                    .as("Il notificationCost non dovrebbe essere null per la categoria '%s' e destinatario '%d'", timelineEventCategory, recipientIndex)
//                    .isNotNull();
//        } else {
//            assertThat(notificationCost)
//                    .as("Il notificationCost dovrebbe essere uguale a '%s' per la categoria '%s' e destinatario '%d'", cost, timelineEventCategory, recipientIndex)
//                    .isEqualTo(Long.parseLong(cost));
//        }
    }

//    public TimelineElementV26 readingEventUpToTheTimelineElementOfNotificationForCategoryUser(String timelineEventCategory, Integer recipientIndex) {
//        PnPollingServiceTimelineRapidV26 timelineRapidV26 = (PnPollingServiceTimelineRapidV26) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V26);
//        PnPollingResponseV26 pnPollingResponseV26 = timelineRapidV26.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .pnPollingPredicate(getPnPollingPredicateForTimelineV26(timelineEventCategory, recipientIndex))
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV26.getNotification().getTimeline());
//        try {
//            Assertions.assertTrue(pnPollingResponseV26.getResult());
//            Assertions.assertNotNull(pnPollingResponseV26.getTimelineElement());
//            timelineElement = pnPollingResponseV26.getTimelineElement();
//            log.info("TIMELINE_ELEMENT: " + timelineElement);
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
//        return timelineElement;
//    }

    @Then("viene controllato che l'elemento di timeline della notifica {string} non esiste con V23")
    public void readingNotEventUpToTheTimelineElementOfNotificationV23(String timelineEventCategory) {

        WaitForEventPredicateFilters filters = WaitForEventPredicateFilters.builder().build();
        B2bStepsInterface b2bStepsInterface = getB2bStepsInterface(NotificationVersion.V23);
        b2bStepsInterface.waitForEventOrStatus(TIMELINE_RAPID, TIMELINE, timelineEventCategory, filters);
        b2bStepsInterface.checkIfTimelineElementExists(false, null, null);

//        PnPollingServiceTimelineRapidV23 timelineRapidV23 = (PnPollingServiceTimelineRapidV23) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V23);
//        PnPollingResponseV23 pnPollingResponseV23 = timelineRapidV23.waitForEvent(sharedSteps.getNotificationIun(),
//                PnPollingParameter.builder()
//                        .value(timelineEventCategory)
//                        .build());
//        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV23.getNotification().getTimeline());
//        try {
//            Assertions.assertFalse(pnPollingResponseV23.getResult());
//            Assertions.assertNull(pnPollingResponseV23.getTimelineElement());
//        } catch (AssertionFailedError assertionFailedError) {
//            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
//        }
    }

    @And("controllo che le tempistiche di arrivo tra l elemento {string} con address type {string} digitalAddressSource {string} in {string} e l'elemento {string} siano corrette per la notifica {string}")
    public void controlloCheLeTempisticheDiArrivoTraLElementoConAddressTypeDigitalAddressSourceInELElementoSianoCorrettePerLaNotifica(String firstElement, String addressType, String digitalAddressSource, String responseStatus, String secondElement, String notificationType) {
        FullSentNotificationV26 fullSentNotification = sharedSteps.getSentNotificationLastVersion();

        Assertions.assertNotNull(fullSentNotification);
        Assertions.assertNotNull(fullSentNotification.getTimeline());
        String iun = fullSentNotification.getIun();

        TimelineElementV26 firstElementToCheck = getElementToCheck(firstElement, addressType, digitalAddressSource, responseStatus);

        Assertions.assertNotNull(firstElementToCheck, "first element to check not found iun: " + iun);
        Assertions.assertNotNull(firstElementToCheck.getEventTimestamp(), "EventTimestamp for first element to check not found iun: " + iun);

        TimelineElementV26 secondElementToCheck = getElementToCheck(secondElement);

        Assertions.assertNotNull(secondElementToCheck, "second element to check not found iun: " + iun);
        Assertions.assertNotNull(secondElementToCheck.getDetails(), "Details for second element to check not found iun: " + iun);
        Assertions.assertNotNull(secondElementToCheck.getDetails().getSchedulingDate(), "SchedulingDate for second element to check not found iun: " + iun);

        Assertions.assertEquals(firstElementToCheck.getTimestamp(), firstElementToCheck.getEventTimestamp());

        int minsToCheck = getMinutesToCheck(notificationType);

        long differenceInMinutes = Duration.between(getFirstElementTime(firstElementToCheck, firstElement, addressType, iun), secondElementToCheck.getDetails().getSchedulingDate()).toMinutes();
        Assertions.assertEquals(minsToCheck, differenceInMinutes, "Time between first and second element not correct: " + iun + " expected wait " + minsToCheck + " actual wait " + differenceInMinutes);
    }

    private OffsetDateTime getFirstElementTime(TimelineElementV26 firstElementToCheck, String firstElement, String addressType, String iun) {
        if (firstElement.equalsIgnoreCase("SEND_DIGITAL_FEEDBACK") && addressType.equals("SERCQ")) {
            Assertions.assertNotNull(firstElementToCheck.getDetails(), "Details for first element to check not found iun: " + iun);
            Assertions.assertNotNull(firstElementToCheck.getDetails().getNotificationDate(), "NotificationDate for first element to check not found iun: " + iun);
            return firstElementToCheck.getDetails().getNotificationDate();
        } else if (firstElement.equalsIgnoreCase("DIGITAL_DELIVERY_CREATION_REQUEST")) {
            Assertions.assertNotNull(firstElementToCheck.getDetails(), "Details for first element to check not found iun: " + iun);
            Assertions.assertNotNull(firstElementToCheck.getDetails().getCompletionWorkflowDate(), "CompletionWorkflowDate for first element to check not found iun: " + iun);
            return firstElementToCheck.getDetails().getCompletionWorkflowDate();
        } else return firstElementToCheck.getEventTimestamp();
    }

    private TimelineElementV26 getElementToCheck(String secondElement) {
        return sharedSteps.getSentNotificationLastVersion().getTimeline()
                .stream().filter(data -> data.getElementId().startsWith(secondElement))
                .findFirst().orElse(null);
    }

    private TimelineElementV26 getElementToCheck(String firstElement, String addressType, String digitalAddressSource, String responseStatus) {
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
            FullSentNotificationV26 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
            assertSoftly(softly -> {
                assertThat(fullSentNotification).as("La fullSentNotification non dev'essere null").isNotNull();
                assertThat(fullSentNotification.getTimeline()).as("La timeline della fullSentNotification non dev'essere null").isNotNull();
                TimelineElementV26 te = fullSentNotification.getTimeline()
                        .stream()
                        .filter(data -> data.getElementId().startsWith(timelineElement))
                        .filter(data -> data.getDetails() != null)
                        .filter(data -> data.getDetails().getResponseStatus().equals(ResponseStatus.fromValue(responseStatus.toUpperCase())))
                        .filter(data -> data.getDetails().getDigitalAddress().getType().equalsIgnoreCase(type))
                        .filter(data -> data.getDetails().getDigitalAddress().getAddress().equalsIgnoreCase(address))
                        .findFirst().orElse(null);
                assertThat(te).as("Il timelineElement atteso non è stato trovato").isNotNull();
            });
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
        }
    }

    /**
     * Verifica lo scarto temporale tra due eventi con deliveryDetailCode specifico.
     * Da usare solo dopo aver appurato la presenza di tali elementi
     * (e in scenari che non prevedono multi-destinatario o invii multipli, che potrebbero portare alla NON univocità del deliveryDetailCode)
     */
    @And("lo scarto temporale tra {string} e {string} è {isSuperiore} a {int} {unitaTemporale}")
    public void checkScartoTemporaleTraDueDeliveryDetailCode(String code1, String code2, Boolean isSuperiore, int timeQuantity, ChronoUnit unitaTemporale) {
        FullSentNotificationV26 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
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

        OffsetDateTime date1 = t1.getEventTimestamp().truncatedTo(MINUTES);
        OffsetDateTime date2 = t2.getEventTimestamp().truncatedTo(MINUTES);

        OffsetDateTime expectedDate =
                unitaTemporale == DAYS ? date1.plusDays(timeQuantity) :
                        unitaTemporale == HOURS ? date1.plusHours(timeQuantity) :
                                date1.plusMinutes(timeQuantity);
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
}