package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.mapper.impl.PnTimelineAndLegalFactV23;
import it.pagopa.pn.client.b2b.pa.mapper.model.PnTimelineLegalFactV23;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingFactory;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.*;
import it.pagopa.pn.client.b2b.pa.polling.impl.*;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.service.IPnPrivateDeliveryPushExternalClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebRecipientClient;
import it.pagopa.pn.client.b2b.pa.service.impl.PnExternalServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.NotificationHistoryResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.NotificationProcessCostResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.ResponsePaperNotificationFailedDto;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.utils.DataTest;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpStatusCodeException;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.OffsetDateTime.now;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.awaitility.Awaitility.await;


@Slf4j
public class AvanzamentoNotificheB2bSteps {
    @Autowired
    private PnPaB2bUtils utils;
    private final IPnPaB2bClient b2bClient;
    private final SharedSteps sharedSteps;
    private final IPnWebRecipientClient webRecipientClient;
    private final PnExternalServiceClientImpl externalClient;
    private final IPnPrivateDeliveryPushExternalClient pnPrivateDeliveryPushExternalClient;
    private HttpStatusCodeException notificationError;
    @Value("${pn.external.costo_base_notifica}")
    private Integer costoBaseNotifica;
    private final PnTimelineAndLegalFactV23 pnTimelineAndLegalFactV23;
    private final PnPollingFactory pnPollingFactory;
    private final TimingForPolling timingForPolling;
    private final LegalFactContentVerifySteps legalFactContentVerifySteps;
    @Value("${pn.external.allowed.future.offset.duration}")
    private String pnEcConsAllowedFutureOffsetDuration;
    @Value("${pn.consolidatore.requestId}")
    private String requestIdConsolidator;

    @Autowired
    public AvanzamentoNotificheB2bSteps(SharedSteps sharedSteps,
                                        TimingForPolling timingForPolling,
                                        IPnPrivateDeliveryPushExternalClient pnPrivateDeliveryPushExternalClient,
                                        LegalFactContentVerifySteps legalFactContentVerifySteps) {
        this.sharedSteps = sharedSteps;
        this.pnPrivateDeliveryPushExternalClient = pnPrivateDeliveryPushExternalClient;
        this.externalClient = sharedSteps.getPnExternalServiceClient();
        this.pnTimelineAndLegalFactV23 = new PnTimelineAndLegalFactV23();
        this.b2bClient = sharedSteps.getB2bClient();
        this.webRecipientClient = sharedSteps.getWebRecipientClient();
        this.pnPollingFactory = sharedSteps.getPollingFactory();
        this.timingForPolling = timingForPolling;
        this.legalFactContentVerifySteps = legalFactContentVerifySteps;
    }

    @Then("vengono letti gli eventi fino allo stato della notifica {string} dalla PA {string}")
    public void readingEventsNotificationPA(String status, String pa) {
        sharedSteps.selectPA(pa);
        readingEventUpToTheStatusOfNotification(status);
        sharedSteps.selectPA(SharedSteps.DEFAULT_PA);
    }

    @Then("vengono letti gli eventi fino allo stato della notifica {string}")
    public void readingEventUpToTheStatusOfNotification(String status) {
        PnPollingPredicate pnPollingPredicate = new PnPollingPredicate();
        pnPollingPredicate.setNotificationStatusHistoryElementPredicateV23(
                statusHistory -> statusHistory
                        .getStatus()
                        .getValue().equals(status)
        );

        PnPollingServiceStatusRapidV25 statusRapidV25 = (PnPollingServiceStatusRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.STATUS_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = statusRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(status)
                        .pnPollingPredicate(pnPollingPredicate)
                        .build());
        log.info("NOTIFICATION_STATUS_HISTORY: " + pnPollingResponseV25.getNotification().getNotificationStatusHistory());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getNotificationStatusHistoryElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            log.info("NOTIFICATION_STATUS_HISTORY_ELEMENT: " + pnPollingResponseV25.getNotificationStatusHistoryElement());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi fino allo stato della notifica {string} V1")
    public void readingEventUpToTheStatusOfNotificationV1(String status) {
        String iun;
        if (sharedSteps.getSentNotificationV1() != null) {
            iun = sharedSteps.getSentNotificationV1().getIun();
        } else {
            iun = sharedSteps.getSentNotification().getIun();
        }

        PnPollingServiceStatusRapidV1 statusRapidV1 = (PnPollingServiceStatusRapidV1) pnPollingFactory.getPollingService(PnPollingStrategy.STATUS_RAPID_V1);

        PnPollingResponseV1 pnPollingResponseV1 = statusRapidV1.waitForEvent(iun,
                PnPollingParameter.builder()
                        .value(status)
                        .build());

        log.info("NOTIFICATION_STATUS_HISTORY v1: " + pnPollingResponseV1.getNotification().getNotificationStatusHistory());
        try {
            Assertions.assertTrue(pnPollingResponseV1.getResult());
            Assertions.assertNotNull(pnPollingResponseV1.getNotificationStatusHistoryElement());
            sharedSteps.setSentNotificationV1(pnPollingResponseV1.getNotification());
            log.info("NOTIFICATION_STATUS_HISTORY_ELEMENT v1: " + pnPollingResponseV1.getNotificationStatusHistoryElement());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi fino allo stato della notifica {string} per il destinatario {int} e presente l'evento {string}")
    public void readingEventUpToTheStatusOfNotification(String status, int destinatario, String evento) {
        PnPollingServiceStatusRapidV25 statusRapidV25 = (PnPollingServiceStatusRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.STATUS_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = statusRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(status)
                        .build());
        log.info("NOTIFICATION_STATUS_HISTORY: " + pnPollingResponseV25.getNotification().getNotificationStatusHistory());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getNotificationStatusHistoryElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            NotificationStatusHistoryElement notificationStatusHistoryElement = pnPollingResponseV25.getNotificationStatusHistoryElement();
            log.info("NOTIFICATION_STATUS_HISTORY_ELEMENT: " + notificationStatusHistoryElement);

            List<String> timelineElements = notificationStatusHistoryElement.getRelatedTimelineElements();
            boolean esiste = false;
            for (String tmpTimeline : timelineElements) {
                if (tmpTimeline.contains(evento) && tmpTimeline.contains("RECINDEX_" + destinatario)) {
                    esiste = true;
                    break;
                }
            }
            Assertions.assertTrue(esiste);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    private void checkTimelineElementEquality(String timelineEventCategory, TimelineElementV25 elementFromNotification, DataTest dataFromTest) {
        TimelineElementV23 elementFromTest = dataFromTest.getTimelineElement();
        TimelineElementDetailsV23 detailsFromNotification = elementFromNotification.getDetails();
        TimelineElementDetailsV23 detailsFromTest = elementFromTest.getDetails();
        DelegateInfo delegateInfoFromTest = detailsFromTest != null ? detailsFromTest.getDelegateInfo() : null;
        DelegateInfo delegateInfoFromNotification = detailsFromNotification != null ? detailsFromNotification.getDelegateInfo() : null;

        switch (timelineEventCategory) {
            case "SEND_COURTESY_MESSAGE":
                if (detailsFromTest != null) {
                    Assertions.assertEquals(detailsFromNotification.getDigitalAddress(), detailsFromTest.getDigitalAddress());
                    Assertions.assertEquals(detailsFromNotification.getRecIndex(), detailsFromTest.getRecIndex());
                }
                break;
            case "REQUEST_REFUSED":
                if (detailsFromTest != null) {
                    Assertions.assertNotNull(detailsFromNotification.getRefusalReasons());
                    Assertions.assertEquals(detailsFromNotification.getRefusalReasons().size(), detailsFromTest.getRefusalReasons().size());
                    for (int i = 0; i < detailsFromNotification.getRefusalReasons().size(); i++) {
                        Assertions.assertEquals(detailsFromNotification.getRefusalReasons().get(i).getErrorCode(), detailsFromTest.getRefusalReasons().get(i).getErrorCode());
                    }
                }
                break;
            case "AAR_GENERATION":
                if (detailsFromTest != null) {
                    Assertions.assertNotNull(detailsFromNotification.getGeneratedAarUrl());
                }
                break;
            case "SEND_DIGITAL_FEEDBACK":
                if (detailsFromTest != null) {
                    Assertions.assertNotNull(detailsFromNotification.getResponseStatus());
                    Assertions.assertEquals(detailsFromNotification.getResponseStatus().getValue(), detailsFromTest.getResponseStatus().getValue());
                    Assertions.assertEquals(detailsFromNotification.getDigitalAddress(), detailsFromTest.getDigitalAddress());
                    Assertions.assertEquals(detailsFromNotification.getSendingReceipts().size(), detailsFromTest.getSendingReceipts().size());
                    for (int i = 0; i < detailsFromNotification.getSendingReceipts().size(); i++) {
                        Assertions.assertEquals(detailsFromNotification.getSendingReceipts().get(i), detailsFromTest.getSendingReceipts().get(i));
                    }
                }
                break;
            case "REQUEST_ACCEPTED":
                Assertions.assertNotNull(elementFromNotification.getLegalFactsIds());
                Assertions.assertEquals(elementFromNotification.getLegalFactsIds().size(), elementFromTest.getLegalFactsIds().size());
                for (int i = 0; i < elementFromNotification.getLegalFactsIds().size(); i++) {
                    Assertions.assertEquals(elementFromNotification.getLegalFactsIds().get(i).getCategory(), elementFromTest.getLegalFactsIds().get(i).getCategory());
                    Assertions.assertNotNull(elementFromNotification.getLegalFactsIds().get(i).getKey());
                }
                break;
            case "SEND_DIGITAL_DOMICILE":
                if (detailsFromTest != null) {
                    Assertions.assertEquals(detailsFromNotification.getDigitalAddress(), detailsFromTest.getDigitalAddress());
                }
                break;
            case "DIGITAL_SUCCESS_WORKFLOW":
            case "DIGITAL_FAILURE_WORKFLOW":
                Assertions.assertNotNull(elementFromNotification.getLegalFactsIds());
                Assertions.assertEquals(elementFromNotification.getLegalFactsIds().size(), elementFromTest.getLegalFactsIds().size());
                for (int i = 0; i < elementFromNotification.getLegalFactsIds().size(); i++) {
                    Assertions.assertEquals(elementFromNotification.getLegalFactsIds().get(i).getCategory(), elementFromTest.getLegalFactsIds().get(i).getCategory());
                    Assertions.assertNotNull(elementFromNotification.getLegalFactsIds().get(i).getKey());
                }
                if (detailsFromTest != null) {
                    Assertions.assertEquals(detailsFromNotification.getDigitalAddress(), detailsFromTest.getDigitalAddress());
                }
                break;
            case "GET_ADDRESS":
                if (detailsFromTest != null) {
                    Assertions.assertEquals(detailsFromNotification.getDigitalAddressSource(), detailsFromTest.getDigitalAddressSource());
                    Assertions.assertEquals(detailsFromNotification.getIsAvailable(), detailsFromTest.getIsAvailable());
                }
                break;
            case "SEND_ANALOG_FEEDBACK":
                if (detailsFromTest != null) {
                    if (Objects.nonNull(detailsFromTest.getDeliveryDetailCode()))
                        Assertions.assertEquals(detailsFromNotification.getDeliveryDetailCode(), detailsFromTest.getDeliveryDetailCode());
                    Assertions.assertEquals(detailsFromNotification.getPhysicalAddress(), detailsFromTest.getPhysicalAddress());
                    Assertions.assertEquals(detailsFromNotification.getResponseStatus().getValue(), detailsFromTest.getResponseStatus().getValue());
                    if (Objects.nonNull(detailsFromTest.getDeliveryFailureCause())) {
                        List<String> failureCauses = Arrays.asList(detailsFromTest.getDeliveryFailureCause().split(" "));
                        Assertions.assertTrue(failureCauses.contains(elementFromNotification.getDetails().getDeliveryFailureCause()));
                    }
                }
                break;
            case "SEND_ANALOG_PROGRESS":
            case "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS":
                if (detailsFromTest != null) {
                    if (Objects.nonNull(elementFromTest.getLegalFactsIds())) {
                        Assertions.assertEquals(elementFromNotification.getLegalFactsIds().size(), elementFromTest.getLegalFactsIds().size());
                        for (int i = 0; i < elementFromNotification.getLegalFactsIds().size(); i++) {
                            Assertions.assertEquals(elementFromNotification.getLegalFactsIds().get(i).getCategory(), elementFromTest.getLegalFactsIds().get(i).getCategory());
                            Assertions.assertNotNull(elementFromNotification.getLegalFactsIds().get(i).getKey());
                        }
                    }
                    if (Objects.nonNull(detailsFromTest.getDeliveryDetailCode())) {
                        Assertions.assertEquals(detailsFromNotification.getDeliveryDetailCode(), detailsFromTest.getDeliveryDetailCode());
                    }
                    if (Objects.nonNull(detailsFromTest.getAttachments())) {
                        Assertions.assertNotNull(detailsFromNotification.getAttachments());
                        Assertions.assertEquals(detailsFromNotification.getAttachments().size(), detailsFromTest.getAttachments().size());

                        for (int i = 0; i < detailsFromNotification.getAttachments().size(); i++) {
                            List<String> documentTypes = Arrays.asList(detailsFromTest.getAttachments().get(i).getDocumentType().split(" "));
                            Assertions.assertTrue(documentTypes.contains(detailsFromNotification.getAttachments().get(i).getDocumentType()));
                        }
                    }

                    if (Objects.nonNull(detailsFromTest.getDeliveryFailureCause())) {
                        List<String> failureCauses = Arrays.asList(detailsFromTest.getDeliveryFailureCause().split(" "));
                        Assertions.assertEquals(Boolean.TRUE, failureCauses.contains(elementFromNotification.getDetails().getDeliveryFailureCause()));
                    }
                }
                break;
            case "ANALOG_SUCCESS_WORKFLOW":
            case "PREPARE_SIMPLE_REGISTERED_LETTER":
                if (detailsFromTest != null) {
                    Assertions.assertEquals(detailsFromNotification.getPhysicalAddress(), detailsFromTest.getPhysicalAddress());
                }
                break;
            case "SEND_SIMPLE_REGISTERED_LETTER":
                if (detailsFromTest != null) {
                    Assertions.assertEquals(detailsFromNotification.getPhysicalAddress(), detailsFromTest.getPhysicalAddress());
                    Assertions.assertEquals(detailsFromNotification.getAnalogCost(), detailsFromTest.getAnalogCost());
                }
                break;
            case "NOTIFICATION_VIEWED":
                Assertions.assertNotNull(elementFromNotification.getLegalFactsIds());
                Assertions.assertEquals(elementFromNotification.getLegalFactsIds().size(), elementFromTest.getLegalFactsIds().size());
                for (int i = 0; i < elementFromNotification.getLegalFactsIds().size(); i++) {
                    Assertions.assertEquals(elementFromNotification.getLegalFactsIds().get(i).getCategory(), elementFromTest.getLegalFactsIds().get(i).getCategory().getValue());
                    Assertions.assertNotNull(elementFromNotification.getLegalFactsIds().get(i).getKey());
                }
                if (delegateInfoFromTest != null) {
                    Assertions.assertEquals(delegateInfoFromNotification.getTaxId(), delegateInfoFromTest.getTaxId());
                    Assertions.assertEquals(delegateInfoFromNotification.getDelegateType(), delegateInfoFromTest.getDelegateType());
                    Assertions.assertEquals(delegateInfoFromNotification.getDenomination(), delegateInfoFromTest.getDenomination());
                }
            case "COMPLETELY_UNREACHABLE":
                if (Objects.nonNull(elementFromTest.getLegalFactsIds())) {
                    assert elementFromNotification.getLegalFactsIds() != null;
                    Assertions.assertEquals(elementFromNotification.getLegalFactsIds().size(), elementFromTest.getLegalFactsIds().size());
                }
                for (int i = 0; i < Objects.requireNonNull(elementFromNotification.getLegalFactsIds()).size(); i++) {
                    Assertions.assertEquals(elementFromNotification.getLegalFactsIds().get(i).getCategory(), elementFromTest.getLegalFactsIds().get(i).getCategory());
                    Assertions.assertNotNull(elementFromNotification.getLegalFactsIds().get(i).getKey());
                }
        }
    }

    private void loadTimelineByDeliveryPush(String timelineEventCategory, DataTest dataFromTest, boolean existCheck) {
        // calc how much time wait
        Integer pollingTime = dataFromTest != null ? dataFromTest.getPollingTime() : null;
        Integer numCheck = dataFromTest != null ? dataFromTest.getNumCheck() : null;
        String pollingType = dataFromTest != null ? dataFromTest.getPollingType() : null;

        //TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);
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
                TimelineElementV25 timelineElement = getTimelineByDeliveryPush(timelineEventCategory, dataFromTest);
                List<TimelineElementV25> timelineElementList = sharedSteps.getSentNotification().getTimeline();

                log.info("NOTIFICATION_TIMELINE: " + timelineElementList);
                Assertions.assertNotNull(timelineElementList);
                Assertions.assertNotEquals(0, timelineElementList.size());
                if (existCheck) {
                    Assertions.assertNotNull(timelineElement);
                } else {
                    Assertions.assertNull(timelineElement);
                }
            });

/**
        PnPollingServiceTimelineSlowE2eV23 timelineSlowV23 = (PnPollingServiceTimelineSlowE2eV23) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_SLOW_V23);

        PnPollingResponseV23 pnPollingResponseV23 = timelineSlowV23.waitForEvent(sharedSteps.getIunVersionamento(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV23.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV23.getResult());
            Assertions.assertNotNull(pnPollingResponseV23.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV23.getNotification());
            TimelineElementV23 timelineElementV23 = pnPollingResponseV23.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElementV23);
            sharedSteps.setTimelineElementV23(timelineElementV23);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
        TimelineElementV23 timelineElement = getTimelineByDeliveryPush(timelineEventCategory, dataFromTest);
        List<TimelineElementV23> timelineElementList = sharedSteps.getSentNotification().getTimeline();

        log.info("NOTIFICATION_TIMELINE: " + timelineElementList);
        Assertions.assertNotNull(timelineElementList);
        Assertions.assertNotEquals(0, timelineElementList.size());
        if (existCheck) {
            Assertions.assertNotNull(timelineElement);
        } else {
            Assertions.assertNull(timelineElement);
        }
**/


    }

    private TimelineElementV25 getTimelineByDeliveryPush(String timelineEventCategory, DataTest dataFromTest) {
        String requestId = sharedSteps.getNewNotificationResponse().getNotificationRequestId();
        byte[] decodedBytes = Base64.getDecoder().decode(requestId);
        String iun = new String(decodedBytes);
        NewNotificationRequestV24 newNotificationRequest = sharedSteps.getNotificationRequest();
        // get timeline from delivery-push
        NotificationHistoryResponse notificationHistory = this.pnPrivateDeliveryPushExternalClient.getNotificationHistory(iun, newNotificationRequest.getRecipients().size(), sharedSteps.getNotificationCreationDate());
        List<TimelineElementV25> timelineElementList = notificationHistory.getTimeline();
        FullSentNotificationV25 fullSentNotification = new FullSentNotificationV25();
        fullSentNotification.setTimeline(timelineElementList);
        sharedSteps.setSentNotification(fullSentNotification);
        return getTimelineElementByIdOrCategory(timelineEventCategory, dataFromTest, iun, timelineElementList);
    }

    private TimelineElementV25 getAndStoreTimelineByB2b(String timelineEventCategory, DataTest dataFromTest) {
        // proceed with default flux
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        String iun = sharedSteps.getSentNotification().getIun();
        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(iun, PnPollingParameter.builder().value(timelineEventCategory).build());
        sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
        return getTimelineElementByIdOrCategory(timelineEventCategory, dataFromTest, iun, pnPollingResponseV25.getNotification().getTimeline());
    }

    private TimelineElementV25 getTimelineElementByIdOrCategory(String timelineEventCategory, DataTest dataFromTest, String iun, List<TimelineElementV25> timelineElementList) {
        TimelineElementV25 timelineElement;
        // get timeline event id
        if (dataFromTest != null && dataFromTest.getTimelineElement() != null) {
            String timelineEventId = sharedSteps.getTimelineEventId(timelineEventCategory, iun, dataFromTest);
            timelineElement = timelineElementList.stream().filter(elem -> elem.getElementId().startsWith(timelineEventId)).findAny().orElse(null);
        } else {
            timelineElement = timelineElementList.stream().filter(elem -> elem.getCategory().getValue().equals(timelineEventCategory)).findAny().orElse(null);
        }
        return timelineElement;
    }

    private void loadTimeline(String timelineEventCategory, boolean existCheck, @Transpose DataTest dataFromTest) {
        TimelineElementV25 timelineElement;
        if (!timelineEventCategory.equals(TimelineElementCategoryV23.REQUEST_REFUSED.getValue())) {
            timelineElement = getAndStoreTimelineByB2b(timelineEventCategory, dataFromTest);
            List<TimelineElementV25> timelineElementList = sharedSteps.getSentNotification().getTimeline();

            log.info("NOTIFICATION_TIMELINE: " + timelineElementList);
            Assertions.assertNotNull(timelineElementList);
            Assertions.assertNotEquals(0, timelineElementList.size());
            if (existCheck) {
                Assertions.assertNotNull(timelineElement);
            } else {
                Assertions.assertNull(timelineElement);
            }
        } else {
            //GESTIONE LOAD TIMELINE E RECUPERO NOTIFICA CON CLIENT DI DELIVERY PUSH
            loadTimelineByDeliveryPush(timelineEventCategory, dataFromTest, existCheck);
        }
    }

    @And("viene verificato che il numero di elementi di timeline {string} sia di {long}")
    public void getTimelineElementListSize(String timelineEventCategory, Long size, @Transpose DataTest dataFromTest) {
        List<TimelineElementV25> timelineElementList = sharedSteps.getSentNotification().getTimeline();
        String iun;
        if (timelineEventCategory.equals(TimelineElementCategoryV23.REQUEST_REFUSED.getValue())) {

            String requestId = sharedSteps.getNewNotificationResponse().getNotificationRequestId();
            byte[] decodedBytes = Base64.getDecoder().decode(requestId);
            iun = new String(decodedBytes);
        } else {
            // proceed with default flux
            iun = sharedSteps.getSentNotification().getIun();
        }
        // get timeline event id
        String timelineEventId = sharedSteps.getTimelineEventId(timelineEventCategory, iun, dataFromTest);
        if (timelineEventCategory.equals(TimelineElementCategoryV23.SEND_ANALOG_PROGRESS.getValue())) {
            TimelineElementV23 timelineElementFromTest = dataFromTest.getTimelineElement();
            TimelineElementDetailsV23 timelineElementDetails = timelineElementFromTest.getDetails();

            Assertions.assertEquals(size, timelineElementList.stream().filter(elem -> elem.getElementId().startsWith(timelineEventId) && elem.getDetails().getDeliveryDetailCode().equals(timelineElementDetails.getDeliveryDetailCode())).count());
        } else {
            Assertions.assertEquals(size, timelineElementList.stream().filter(elem -> elem.getElementId().startsWith(timelineEventId)).count());
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica annullata {string}")
    public void readingEventUpToTheTimelineElementOfNotificationDelete(String timelineEventCategory) {
        readingEventUpToTheTimelineElementOfNotificationForCategory(timelineEventCategory);
    }


    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string}")
    public void readingEventUpToTheTimelineElementOfNotification(String timelineEventCategory) {
        readingEventUpToTheTimelineElementOfNotificationForCategory(timelineEventCategory);
    }

    @Then("gli eventi di timeline ricevuti sono i seguenti$")
    public void verifyTimelineEventsAreTheOnesExpected(List<String> expectedEvents) {
        List<String> actualTimeline = Optional.ofNullable(sharedSteps.getSentNotification())
                .map(FullSentNotificationV25::getTimeline)
                .orElse(List.of())
                .stream()
                .map(TimelineElementV25::getCategory)
                .map(TimelineElementCategoryV23::toString)
                .toList();
        try {
            Assertions.assertFalse(expectedEvents.stream().anyMatch(Predicate.not(actualTimeline::contains)));
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("viene verificato che per l'elemento di timeline della notifica {string} non ci siano duplicati")
    public void checkTimeLineEventWithoutDuplicates(String timelineEventCategory) {
        checkTimeLineEventDuplicates(timelineEventCategory);
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} abbia notificationCost ugauale a {string}")
    public void TimelineElementOfNotification(String timelineEventCategory, String cost) {
        TimelineElementV25 event = readingEventUpToTheTimelineElementOfNotificationForCategory(timelineEventCategory);
        Long notificationCost = event.getDetails().getNotificationCost();

        if (cost.equalsIgnoreCase("null")) {
            Assertions.assertNull(notificationCost);
        } else {
            Assertions.assertEquals(Long.parseLong(cost), notificationCost);
        }
    }

    @Then("si verifica che scheduleDate del SCHEDULE_REFINEMENT sia uguale al timestamp di REFINEMENT per l'utente {int}")
    public void verificationDateScheduleRefinementWithRefinement(Integer destinatario) {
        try {
            OffsetDateTime ricezioneRaccomandata = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV23.SCHEDULE_REFINEMENT) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getDetails().getSchedulingDate();
            OffsetDateTime refinementDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV23.REFINEMENT) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();

            log.info("DESTINATARIO : {}", destinatario);
            log.info("ricezioneRaccomandata : {}", ricezioneRaccomandata);
            log.info("refinementDate : {}", refinementDate);

            Assertions.assertEquals(ricezioneRaccomandata, refinementDate);

        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }


    @Then("si verifica che il timestamp dell'elemento di timeline della notifica SEND_ANALOG_FEEDBACK con deliveryDetailCode RECAG012 sia uguale al timestamp di REFINEMENT")
    public void verificationDateDeliveryDetailCodeRECAG012WithRefinement() {
        try {
            OffsetDateTime ricezioneRECAG012 = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV23.SEND_ANALOG_FEEDBACK) && elem.getDetails().getDeliveryDetailCode().equals("RECAG012")).findAny().get().getDetails().getEventTimestamp();
            OffsetDateTime refinementDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV23.REFINEMENT) && elem.getDetails().getRecIndex().equals(0)).findAny().get().getTimestamp();

            log.info("ricezioneRaccomandata : {}", ricezioneRECAG012);
            log.info("refinementDate : {}", refinementDate);

            Assertions.assertTrue(checkOffsetDateTime(ricezioneRECAG012, refinementDate));

        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    private boolean checkOffsetDateTime(OffsetDateTime offsetDateTime1, OffsetDateTime offsetDateTime2) {
        return offsetDateTime1.equals(offsetDateTime2);
    }

    @Then("verifica date business in timeline COMPLETELY_UNREACHABLE per l'utente {int}")
    public void verificationDateComplettelyUnreachableWithRefinement(Integer destinatario) {
        try {
            OffsetDateTime shedulingDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV23.SCHEDULE_REFINEMENT) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();
            OffsetDateTime complettelyUnreachableDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV23.COMPLETELY_UNREACHABLE) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();
            OffsetDateTime complettelyUnreachableRequestDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV23.COMPLETELY_UNREACHABLE_CREATION_REQUEST) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();
            OffsetDateTime analogFailureDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV23.ANALOG_FAILURE_WORKFLOW) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();
            OffsetDateTime sendAnalogProgressTimestampDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV23.SEND_ANALOG_PROGRESS) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();
            OffsetDateTime sendAnalogProgressNotificationDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV23.SEND_ANALOG_PROGRESS) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getDetails().getNotificationDate();
            OffsetDateTime sendFeedbackTimestampDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV23.SEND_ANALOG_FEEDBACK) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();
            OffsetDateTime sendFeedbackNotificationDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV23.SEND_ANALOG_FEEDBACK) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getDetails().getNotificationDate();
            OffsetDateTime prepareAnalogDomicileFailureTimestamp = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV23.PREPARE_ANALOG_DOMICILE_FAILURE) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();

            log.info("DESTINATARIO : {}", destinatario);
            log.info("sendAnalogProgressTimestampDate : {}", sendAnalogProgressTimestampDate);
            log.info("sendFeedbackTimestampDate : {} ", sendFeedbackTimestampDate);
            log.info("analogFailureDate Timestamp : {}", analogFailureDate);
            log.info("complettelyUnreachableRequestDate Timestamp : {}", complettelyUnreachableRequestDate);
            log.info("complettelyUnreachableDate Timestamp : {}", complettelyUnreachableDate);
            log.info("prepareAnalogDomicileFailureTimestamp : {}", prepareAnalogDomicileFailureTimestamp);

            log.info("shedulingDate Timestamp: {}", shedulingDate);

            log.info("sendAnalogProgressNotificationDate : {}", sendAnalogProgressNotificationDate);
            log.info("sendFeedbackNotificationDate : {}", sendFeedbackNotificationDate);

            Assertions.assertEquals(shedulingDate, complettelyUnreachableDate);
            Assertions.assertEquals(shedulingDate, complettelyUnreachableRequestDate);
            Assertions.assertEquals(shedulingDate, analogFailureDate);
            Assertions.assertEquals(shedulingDate, prepareAnalogDomicileFailureTimestamp);
            Assertions.assertEquals(sendAnalogProgressTimestampDate, sendAnalogProgressNotificationDate);
            Assertions.assertEquals(sendFeedbackTimestampDate, sendFeedbackNotificationDate);
            //TODO  Verificare..
            // Assertions.assertEquals(sendFeedbackDate,sendAnalogProgressDate);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("verifica date business in timeline ANALOG_SUCCESS_WORKFLOW per l'utente {int} al tentativo {int}")
    public void verificationDateScheduleRefinementWithSendAnalogFeedback(Integer destinatario, Integer tentativo) {
        try {
            OffsetDateTime shedulingDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV23.SCHEDULE_REFINEMENT) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();
            OffsetDateTime sendAnalogProgressNotificationDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV23.SEND_ANALOG_PROGRESS) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getDetails().getNotificationDate();
            OffsetDateTime sendAnalogProgressTimestampDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV23.SEND_ANALOG_PROGRESS) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();
            OffsetDateTime sendFeedbackTimestampDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV23.SEND_ANALOG_FEEDBACK) && elem.getDetails().getRecIndex().equals(destinatario) && elem.getDetails().getSentAttemptMade().equals(tentativo)).findAny().get().getTimestamp();
            OffsetDateTime sendFeedbackNotificationDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV23.SEND_ANALOG_FEEDBACK) && elem.getDetails().getRecIndex().equals(destinatario) && elem.getDetails().getSentAttemptMade().equals(tentativo)).findAny().get().getDetails().getNotificationDate();
            OffsetDateTime analogSuccessDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV23.ANALOG_SUCCESS_WORKFLOW) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();

            log.info("DESTINATARIO : {}", destinatario);
            log.info("sendAnalogProgressTimestampDate: {}", sendAnalogProgressTimestampDate);
            log.info("sendFeedbackTimestampDate: {}", sendFeedbackTimestampDate);
            log.info("analogSuccessDate Timestamp: {}", analogSuccessDate);
            log.info("shedulingDate Timestamp: {}", shedulingDate);


            log.info("sendFeedbackNotificationDate : {}", sendFeedbackNotificationDate);
            log.info("sendAnalogProgressNotificationDate: {}", sendAnalogProgressNotificationDate);

            Assertions.assertEquals(shedulingDate, analogSuccessDate);
            Assertions.assertEquals(shedulingDate, sendFeedbackTimestampDate);

            Assertions.assertEquals(sendAnalogProgressTimestampDate, sendAnalogProgressNotificationDate);
            Assertions.assertEquals(sendFeedbackTimestampDate, sendFeedbackNotificationDate);

        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    public TimelineElementV25 readingEventUpToTheTimelineElementOfNotificationForCategory(String timelineEventCategory) {
        PnPollingServiceTimelineSlowV25 timelineSlowV25 = (PnPollingServiceTimelineSlowV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_SLOW_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineSlowV25.waitForEvent(sharedSteps.getIunVersionamento(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            TimelineElementV25 timelineElement = pnPollingResponseV25.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            sharedSteps.setTimelineElement(timelineElement);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
        return pnPollingResponseV25.getTimelineElement();
    }

    public TimelineElementV25 readingEventUpToTheTimelineElementOfNotificationForCategoryExtraRapid(String timelineEventCategory) {
        PnPollingServiceTimelineExtraRapidV25 timelineSlowV25 = (PnPollingServiceTimelineExtraRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_EXTRA_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineSlowV25.waitForEvent(sharedSteps.getIunVersionamento(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            TimelineElementV25 timelineElement = pnPollingResponseV25.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            sharedSteps.setTimelineElement(timelineElement);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
        return pnPollingResponseV25.getTimelineElement();
    }

    public void checkTimeLineEventDuplicates(String timelineEventCategory) {
        int countTimeLineEventCategory = 0;

        for (TimelineElementV25 timelineElement : sharedSteps.getSentNotification().getTimeline()) {
            if (timelineEventCategory.equals(timelineElement.getCategory().getValue())) {
                countTimeLineEventCategory++;
            }
        }

        try {
            Assertions.assertTrue(countTimeLineEventCategory <= 1,
                    "L'elemento di timeline della notifica '" + timelineEventCategory + "' è presente solo una volta.");
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} V1")
    public void readingEventUpToTheTimelineElementOfNotificationV1(String timelineEventCategory) {
        PnPollingServiceTimelineSlowV1 timelineSlowV1 = (PnPollingServiceTimelineSlowV1) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_SLOW_V1);

        PnPollingResponseV1 pnPollingResponseV1 = timelineSlowV1.waitForEvent(sharedSteps.getIunVersionamento(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .build());

        log.info("NOTIFICATION_TIMELINE V1 : " + pnPollingResponseV1.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV1.getResult());
            Assertions.assertNotNull(pnPollingResponseV1.getNotification().getTimeline());
            sharedSteps.setSentNotificationV1(pnPollingResponseV1.getNotification());
            log.info("TIMELINE_ELEMENT: " + pnPollingResponseV1.getTimelineElement());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} V2")
    public void readingEventUpToTheTimelineElementOfNotificationV2(String timelineEventCategory) {
        PnPollingServiceTimelineRapidV20 timelineRapidV20 = (PnPollingServiceTimelineRapidV20) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V20);

        PnPollingResponseV20 pnPollingResponseV20 = timelineRapidV20.waitForEvent(sharedSteps.getIunVersionamento(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .build());

        log.info("NOTIFICATION_TIMELINE V2 : " + pnPollingResponseV20.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV20.getResult());
            Assertions.assertNotNull(pnPollingResponseV20.getTimelineElement());
            sharedSteps.setSentNotificationV2(pnPollingResponseV20.getNotification());
            log.info("TIMELINE_ELEMENT: " + pnPollingResponseV20.getTimelineElement());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} V21")
    public void readingEventUpToTheTimelineElementOfNotificationV21(String timelineEventCategory) {
        PnPollingServiceTimelineRapidV21 timelineRapidV21 = (PnPollingServiceTimelineRapidV21) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V21);

        PnPollingResponseV21 pnPollingResponseV21 = timelineRapidV21.waitForEvent(sharedSteps.getIunVersionamento(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .build());

        log.info("NOTIFICATION_TIMELINE V21 : " + pnPollingResponseV21.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV21.getResult());
            Assertions.assertNotNull(pnPollingResponseV21.getTimelineElement());
            sharedSteps.setSentNotificationV21(pnPollingResponseV21.getNotification());
            log.info("TIMELINE_ELEMENT: " + pnPollingResponseV21.getTimelineElement());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi della timeline e si controlla che l'evento di timeline {string} non esista con la V1")
    public void readingEventsOfTimelineElementOfNotificationV1(String timelineEventCategory) {
        String iun = null;

        if (sharedSteps.getSentNotification() != null) {
            iun = sharedSteps.getSentNotification().getIun();
        } else if (sharedSteps.getSentNotificationV1() != null) {
            iun = sharedSteps.getSentNotificationV1().getIun();
        } else if (sharedSteps.getSentNotificationV2() != null) {
            iun = sharedSteps.getSentNotificationV2().getIun();
        }

        PnPollingServiceTimelineSlowV1 timelineSlowV1 = (PnPollingServiceTimelineSlowV1) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_SLOW_V1);

        PnPollingResponseV1 pnPollingResponseV1 = timelineSlowV1.waitForEvent(iun,
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .build());

        log.info("NOTIFICATION_TIMELINE V1 : " + pnPollingResponseV1.getNotification().getTimeline());
        try {
            Assertions.assertFalse(pnPollingResponseV1.getResult());
            Assertions.assertNull(pnPollingResponseV1.getTimelineElement());
            sharedSteps.setSentNotificationV1(pnPollingResponseV1.getNotification());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("viene controllato che l'elemento di timeline della notifica {string} non esiste dopo il rifiuto della notifica stessa")
    public void readingNotEventUpToTheTimelineElementOfNotificationRefused(String timelineEventCategory) {
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertFalse(pnPollingResponseV25.getResult());
            Assertions.assertNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("viene controllato che l'elemento di timeline della notifica {string} non esiste")
    public void readingNotEventUpToTheTimelineElementOfNotification(String timelineEventCategory) {
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertFalse(pnPollingResponseV25.getResult());
            Assertions.assertNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} e successivamente annullata")
    public void readingEventUpToTheTimelineElementOfNotificationAndCancel(String timelineEventCategory) {
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            log.info("TIMELINE_ELEMENT: " + pnPollingResponseV25.getTimelineElement());
            Assertions.assertDoesNotThrow(() ->
                    b2bClient.notificationCancellation(sharedSteps.getSentNotification().getIun())
            );
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con deliveryDetailCode {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCode(String timelineEventCategory, String deliveryDetailCode) {
        PnPollingResponseV25 pnPollingResponseV25 = getPollingResponse(timelineEventCategory, deliveryDetailCode);

        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            log.info("TIMELINE_ELEMENT: " + pnPollingResponseV25.getTimelineElement());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("viene verificato che lato utente l'elemento di timeline {string} con deliveryDetailCode {string} non esista")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCodeWithouthSuccess(String timelineEventCategory, String deliveryDetailCode) {
        PnPollingResponseV25 pnPollingResponseV25 = getPollingResponse(timelineEventCategory, deliveryDetailCode);
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());

        try {
            Assertions.assertFalse(pnPollingResponseV25.getResult());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con deliveryDetailCode {string} tentativo {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCode(String timelineEventCategory, String deliveryDetailCode, String attempt) {
        PnPollingServiceTimelineSlowV25 timelineRapidV25 = (PnPollingServiceTimelineSlowV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_SLOW_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .pnPollingPredicate(getPnPollingPredicateForTimelineV25(timelineEventCategory, deliveryDetailCode, attempt))
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            log.info("TIMELINE_ELEMENT: " + pnPollingResponseV25.getTimelineElement());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con deliveryDetailCode {string} e verifica data delay più {int}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCode(String timelineEventCategory, String deliveryDetailCode, int delay) {
        PnPollingResponseV25 pnPollingResponseV25 = getPollingResponse(timelineEventCategory, deliveryDetailCode);

        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            TimelineElementV25 timelineElement = pnPollingResponseV25.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            //Assertions.assertNotNull(timelineElement.getDetails().getAttachments());
            //Assertions.assertTrue(timelineElement.getDetails().getAttachments().size()>0);
            //Assertions.assertNotNull(timelineElement.getDetails().getAttachments().get(0).getDate());
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            Assertions.assertEquals(Objects.requireNonNull(timelineElement.getDetails()).getNotificationDate().format(fmt), now().plusDays(delay).format(fmt));
            //Assertions.assertTrue(timelineElement.getDetails().getAttachments().get(0).getDate().format(fmt).equals(OffsetDateTime.now().plusDays(delay).format(fmt)));
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} e verifica data schedulingDate più {int}{string} per il destinatario {int}")
    public void readingEventUpToTheTimelineElementOfNotificationWithVerifySchedulingDate(String timelineEventCategory,  int delay, String tipoIncremento, int destinatario) {
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .pnPollingPredicate(getPnPollingPredicateForTimelineV25(timelineEventCategory, destinatario))
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            TimelineElementV25 timelineElement = pnPollingResponseV25.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            OffsetDateTime digitalDeliveryCreationRequestDate = Objects.requireNonNull(timelineElement).getTimestamp();
            Assertions.assertNotNull(Objects.requireNonNull(timelineElement.getDetails()).getSchedulingDate());
            Assertions.assertNotNull(tipoIncremento);
            if ("d".equalsIgnoreCase(tipoIncremento)) {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                Assertions.assertEquals(timelineElement.getDetails().getSchedulingDate().format(fmt), Objects.requireNonNull(digitalDeliveryCreationRequestDate).plusDays(delay).format(fmt));
            } else if ("m".equalsIgnoreCase(tipoIncremento)) {
                DateTimeFormatter fmt1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                Assertions.assertEquals(timelineElement.getDetails().getSchedulingDate().format(fmt1), Objects.requireNonNull(digitalDeliveryCreationRequestDate).plusMinutes(delay).format(fmt1));
            }
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con deliveryDetailCode {string} e verifica tipo DOC {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCodeVerifyTypeDoc(String timelineEventCategory, String deliveryDetailCode, String tipoDoc) {
        PnPollingResponseV25 pnPollingResponseV25 = getPollingResponse(timelineEventCategory, deliveryDetailCode);

        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            TimelineElementV25 timelineElement = pnPollingResponseV25.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNotNull(Objects.requireNonNull(timelineElement.getDetails()).getAttachments());
            Assertions.assertFalse(timelineElement.getDetails().getAttachments().isEmpty());
            Assertions.assertNotNull(timelineElement.getDetails().getAttachments().get(0).getDocumentType());
            // Assertions.assertTrue(timelineElement.getDetails().getNotificationDate().format(fmt).equals(OffsetDateTime.now().plusDays(delay).format(fmt)));
            Assertions.assertEquals(Objects.requireNonNull(timelineElement.getDetails().getAttachments().get(0).getDocumentType()), tipoDoc);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con deliveryDetailCode {string} e verifica tipo DOC {string} tentativo {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCodeVerifyTypeDoc(String timelineEventCategory, String deliveryDetailCode, String tipoDoc, String attempt) {
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .pnPollingPredicate(getPnPollingPredicateForTimelineV25(timelineEventCategory, null, deliveryDetailCode, attempt, tipoDoc, null, false, false, null, false, null))
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            TimelineElementV25 timelineElement = pnPollingResponseV25.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNotNull(Objects.requireNonNull(timelineElement.getDetails()).getAttachments());
            Assertions.assertFalse(timelineElement.getDetails().getAttachments().isEmpty());
            Assertions.assertNotNull(timelineElement.getDetails().getAttachments().get(0).getDocumentType());
            // Assertions.assertTrue(timelineElement.getDetails().getNotificationDate().format(fmt).equals(OffsetDateTime.now().plusDays(delay).format(fmt)));
            Assertions.assertTrue(Objects.requireNonNull(timelineElement.getDetails().getAttachments().get(0).getDocumentType()).equals(tipoDoc) || Objects.equals(timelineElement.getDetails().getAttachments().get(0).getDocumentType(), "Indagine"));
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con deliveryDetailCode {string} e deliveryFailureCause {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCodeDeliveryFailureCause(String timelineEventCategory, String deliveryDetailCode, String deliveryCause) {
        PnPollingResponseV25 pnPollingResponseV25 = getPollingResponse(timelineEventCategory, deliveryDetailCode);

        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            TimelineElementV25 timelineElement = pnPollingResponseV25.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertEquals(Objects.requireNonNull(timelineElement.getDetails()).getDeliveryFailureCause(), deliveryCause);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con deliveryDetailCode {string} e deliveryFailureCause {string} tentativo {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCodeDeliveryFailureCause(String timelineEventCategory, String deliveryDetailCode, String deliveryCause, String attempt) {
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .pnPollingPredicate(getPnPollingPredicateForTimelineV25(timelineEventCategory, deliveryDetailCode, attempt))
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            TimelineElementV25 timelineElement = pnPollingResponseV25.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertEquals(Objects.requireNonNull(timelineElement.getDetails()).getDeliveryFailureCause(), deliveryCause);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @And("viene verificato il campo sendRequestId dell' evento di timeline {string}")
    public void vieneVerificatoCampoSendRequestIdEventoTimeline(String timelineEventCategory) {
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            TimelineElementV25 timelineElement = pnPollingResponseV25.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNotNull(timelineElement.getDetails());
            Assertions.assertNotNull(timelineElement.getDetails().getSendRequestId());
            String sendRequestId = timelineElement.getDetails().getSendRequestId();
            TimelineElementV25 timelineElementRelative = pnPollingResponseV25
                    .getNotification()
                    .getTimeline()
                    .stream()
                    .filter(elem -> Objects.requireNonNull(elem.getElementId()).equals(sendRequestId))
                    .findAny()
                    .orElse(null);
            Assertions.assertNotNull(timelineElementRelative);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @And("viene verificato il campo serviceLevel dell' evento di timeline {string} sia valorizzato con {string}")
    public void vieneVerificatoCampoServiceLevelEventoTimeline(String timelineEventCategory, String value) {
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            ServiceLevel level = switch (value) {
                case "AR_REGISTERED_LETTER" -> ServiceLevel.AR_REGISTERED_LETTER;
                case "REGISTERED_LETTER_890" -> ServiceLevel.REGISTERED_LETTER_890;
                default -> throw new IllegalArgumentException();
            };
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            TimelineElementV25 timelineElement = pnPollingResponseV25.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNotNull(timelineElement.getDetails());
            Assertions.assertEquals(timelineElement.getDetails().getServiceLevel(), level);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} per l'utente {int}")
    public void readingEventUpToTheTimelineElementOfNotificationPerUtente(String timelineEventCategory, Integer destinatario) {
        PnPollingServiceTimelineSlowV25 timelineSlowV25 = (PnPollingServiceTimelineSlowV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_SLOW_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineSlowV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .pnPollingPredicate(getPnPollingPredicateForTimelineV25(timelineEventCategory, destinatario))
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            log.info("TIMELINE_ELEMENT: " + pnPollingResponseV25.getTimelineElement());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("esiste l'elemento di timeline della notifica {string} per l'utente {int}")
    public void verifyEventUpToTheTimelineElementOfNotificationPerUtente(String timelineEventCategory, Integer destinatario) {
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .pnPollingPredicate(getPnPollingPredicateForTimelineV25(timelineEventCategory, destinatario))
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            log.info("TIMELINE_ELEMENT: " + pnPollingResponseV25.getTimelineElement());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("non vengono letti gli eventi fino all'elemento di timeline della notifica {string} per l'utente {int}")
    public void notReadingEventUpToTheTimelineElementOfNotificationPerUtente(String timelineEventCategory, Integer destinatario) {
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .pnPollingPredicate(getPnPollingPredicateForTimelineV25(timelineEventCategory, destinatario))
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertFalse(pnPollingResponseV25.getResult());
            Assertions.assertNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} verifica numero pagine AAR {int}")
    public void readingEventUpToTheTimelineElementOfNotificationPerVerificaNumPagine(String timelineEventCategory, Integer numPagine) {
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            TimelineElementV25 timelineElement = pnPollingResponseV25.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertEquals(Objects.requireNonNull(timelineElement.getDetails()).getNumberOfPages(), numPagine);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi e verifico che l'utente {int} non abbia associato un evento {string}")
    public void vengonoLettiGliEventiVerificoCheUtenteNonAbbiaAssociatoEvento(Integer destinatario, String timelineEventCategory) {
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .pnPollingPredicate(getPnPollingPredicateForTimelineV25(timelineEventCategory, destinatario))
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertFalse(pnPollingResponseV25.getResult());
            Assertions.assertNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi e verifico che l'utente {int} non abbia associato un evento {string} V1")
    public void vengonoLettiGliEventiVerificoCheUtenteNonAbbiaAssociatoEventoV1(Integer destinatario, String timelineEventCategory) {
        String iun;
        if (sharedSteps.getSentNotificationV1() != null) {
            iun = sharedSteps.getSentNotificationV1().getIun();
        } else if (sharedSteps.getSentNotificationV2() != null) {
            iun = sharedSteps.getSentNotificationV2().getIun();
        } else {
            iun = sharedSteps.getSentNotification().getIun();
        }

        PnPollingPredicate pnPollingPredicate = new PnPollingPredicate();
        pnPollingPredicate.setTimelineElementPredicateV1(timelineElementV1 ->
                timelineElementV1.getCategory() != null
                        && Objects.requireNonNull(timelineElementV1.getCategory().getValue()).equals(timelineEventCategory)
                        && Objects.requireNonNull(Objects.requireNonNull(timelineElementV1.getDetails()).getRecIndex()).equals(destinatario));

        PnPollingServiceTimelineRapidV1 timelineRapidV1 = (PnPollingServiceTimelineRapidV1) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V1);

        PnPollingResponseV1 pnPollingResponseV1 = timelineRapidV1.waitForEvent(iun,
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .pnPollingPredicate(pnPollingPredicate)
                        .build());

        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV1.getNotification().getTimeline());
        try {
            Assertions.assertFalse(pnPollingResponseV1.getResult());
            Assertions.assertNull(pnPollingResponseV1.getTimelineElement());
            sharedSteps.setSentNotificationV1(pnPollingResponseV1.getNotification());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi e verifico che l'utente {int} non abbia associato un evento {string} con responseStatus {string}")
    public void vengonoLettiGliEventiVerificoCheUtenteNonAbbiaAssociatoEventoWithResponseStatus(Integer destinatario, String timelineEventCategory, String responseStatus) {
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .pnPollingPredicate(getPnPollingPredicateForTimelineV25(timelineEventCategory, destinatario, null, null, null, responseStatus, false, false, null, false, null))
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertFalse(pnPollingResponseV25.getResult());
            Assertions.assertNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("la PA richiede il download dell'attestazione opponibile {string}")
    public void paRequiresDownloadOfLegalFact(String legalFactCategory) {
        String legalFactUrl = downloadLegalFact(legalFactCategory, true, false, false, null);
        legalFactContentVerifySteps.setLegalFactUrl(legalFactUrl);
    }

    @Then("verifica generazione Atto opponibile senza la messa a disposizione in {string}")
    public void paVerifyGenerazioneLegalFact(String legalFactCategory) {
        TimelineElementCategoryV23 timelineElementInternalCategory = null;

        if (legalFactCategory.equalsIgnoreCase("DIGITAL_DELIVERY_CREATION_REQUEST"))
            timelineElementInternalCategory = TimelineElementCategoryV23.DIGITAL_DELIVERY_CREATION_REQUEST;

        TimelineElementV25 timelineElement = null;
        Assertions.assertNotNull(timelineElementInternalCategory);
        for (TimelineElementV25 element : sharedSteps.getSentNotification().getTimeline()) {
            if (element.getCategory().equals(timelineElementInternalCategory)) {
                timelineElement = element;
                break;
            }
        }
        try {
            System.out.println("ELEMENT: " + timelineElement);
            Assertions.assertNotNull(timelineElement.getLegalFactsIds());
            Assertions.assertTrue(CollectionUtils.isEmpty(timelineElement.getLegalFactsIds()));
            Assertions.assertNotNull(timelineElement.getDetails().getLegalFactId());

        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("la PA richiede il download dell'attestazione opponibile {string} senza legalFactType")
    public void paRequiresDownloadOfLegalFactId(String legalFactCategory) {
        downloadLegalFactId(legalFactCategory, true, false, false, null);
    }

    @Then("la PA richiede il download dell'attestazione opponibile {string} con deliveryDetailCode {string}")
    public void paRequiresDownloadOfLegalFactWithDeliveryDetailCode(String legalFactCategory, String deliveryDetailCode) {
        String legalFactUrl = downloadLegalFact(legalFactCategory, true, false, false, deliveryDetailCode);
        legalFactContentVerifySteps.setLegalFactUrl(legalFactUrl);
    }

    @Then("viene richiesto tramite appIO il download dell'attestazione opponibile {string}")
    public void appIODownloadLegalFact(String legalFactCategory) {
        String legalFactUrl = downloadLegalFact(legalFactCategory, false, true, false, null);
        legalFactContentVerifySteps.setLegalFactUrl(legalFactUrl);
    }

    @Then("{string} richiede il download dell'attestazione opponibile {string}")
    public void userDownloadLegalFact(String user, String legalFactCategory) {
        sharedSteps.selectUser(user);
        String legalFactUrl = downloadLegalFact(legalFactCategory, false, false, true, null);
        legalFactContentVerifySteps.setLegalFactUrl(legalFactUrl);
    }

    @Then("la PA richiede il download dell'attestazione opponibile PEC_RECEIPT")
    public void paRequiresDownloadOfLegalFactPecRecipient() {
        downloadLegalFactPecRecipient("PEC_RECEIPT", true, false, false, null);
    }

    @Then("{string} richiede il download dell'attestazione opponibile PEC_RECEIPT")
    public void userDownloadLegalFactPecRecipient(String user) {
        sharedSteps.selectUser(user);
        downloadLegalFactPecRecipient("PEC_RECEIPT", false, false, true, null);
    }

    @Then("{string} richiede il download dell'attestazione opponibile {string} con errore {string}")
    public void userDownloadLegalFactError(String user, String legalFactCategory, String statusCode) {
        try {
            sharedSteps.selectUser(user);
            String legalFactUrl = downloadLegalFact(legalFactCategory, false, false, true, null);
            legalFactContentVerifySteps.setLegalFactUrl(legalFactUrl);
        } catch (AssertionFailedError assertionFailedError) {
            // System.out.println(assertionFailedError.getCause().toString());
            // System.out.println(assertionFailedError.getCause().getMessage().toString());
            Assertions.assertEquals(assertionFailedError.getCause().getMessage().substring(0, 3), statusCode);
        }
    }

    @And("ricerca ed effettua download del legalFact con la categoria {string}")
    public void ricercaEdEffettuaDownloadDelLegalFactConLaCategoria(String legalFactCategory) {
        String legalFactUrl = downloadLegalFact(legalFactCategory, false, false, true, null);
        legalFactContentVerifySteps.setLegalFactUrl(legalFactUrl);
    }

    private String downloadLegalFact(String legalFactCategory, boolean pa, boolean appIO, boolean webRecipient, String deliveryDetailCode) {
        try {
            Thread.sleep(sharedSteps.getWait());
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }

        PnTimelineLegalFactV23 categoriesV23 = pnTimelineAndLegalFactV23.getCategory(legalFactCategory);
        TimelineElementV25 timelineElement = null;

        for (TimelineElementV25 element : sharedSteps.getSentNotification().getTimeline()) {
            if (Objects.requireNonNull(element.getCategory()).equals(categoriesV23.getTimelineElementInternalCategory())) {
                if (deliveryDetailCode == null) {
                    timelineElement = element;
                    break;
                } else if (Objects.equals(Objects.requireNonNull(element.getDetails()).getDeliveryDetailCode(), deliveryDetailCode)) {
                    timelineElement = element;
                    break;
                }
            }
        }

        try {
            System.out.println("ELEMENT: " + timelineElement);
            Assertions.assertNotNull(timelineElement);

            Assertions.assertNotNull(timelineElement.getLegalFactsIds());
            Assertions.assertFalse(CollectionUtils.isEmpty(timelineElement.getLegalFactsIds()));
            Assertions.assertEquals(categoriesV23.getLegalFactCategory().getValue(), timelineElement.getLegalFactsIds().get(0).getCategory());
            LegalFactCategory categorySearch = LegalFactCategory.fromValue(timelineElement.getLegalFactsIds().get(0).getCategory());
            String key = timelineElement.getLegalFactsIds().get(0).getKey();
            String finalKeySearch = getKeyLegalFact(key);

            if (pa) {
                LegalFactDownloadMetadataResponse legalFactDownloadMetadataResponse = Assertions.assertDoesNotThrow(() -> this.b2bClient.getLegalFact(sharedSteps.getSentNotification().getIun(), categorySearch, finalKeySearch));
                return legalFactDownloadMetadataResponse.getUrl();
            }
            if (appIO) {
                // Assertions.assertDoesNotThrow(() -> this.appIOB2bClient.getLegalFact(sharedSteps.getSentNotification().getIun(), categorySearch.toString(), finalKeySearch,
                //  sharedSteps.getSentNotification().getRecipients().get(0).getTaxId()));
            }
            if (webRecipient) {
                it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.v25.model.LegalFactDownloadMetadataResponse legalFactDownloadMetadataResponse =
                        Assertions.assertDoesNotThrow(() ->
                                this.webRecipientClient.getLegalFact(sharedSteps.getSentNotification().getIun(),
                                        sharedSteps.deepCopy(categorySearch,
                                                it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.v25.model.LegalFactCategory.class),
                                        finalKeySearch
                                ));
                System.out.println("NOME FILE PEC RECIPIENT DEST" + legalFactDownloadMetadataResponse.getFilename());
                return legalFactDownloadMetadataResponse.getUrl();
            }
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
        return null;
    }

    private void downloadLegalFactPecRecipient(String legalFactCategory, boolean pa, boolean appIO, boolean webRecipient, String deliveryDetailCode) {
        try {
            Thread.sleep(sharedSteps.getWait());
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }

        TimelineElementV25 timelineElement = null;

        TimelineElementCategoryV23 timelineElementInternalCategory = TimelineElementCategoryV23.SEND_DIGITAL_PROGRESS;
        LegalFactCategory category = LegalFactCategory.PEC_RECEIPT;

        for (TimelineElementV25 element : sharedSteps.getSentNotification().getTimeline()) {

            if (element.getCategory().equals(timelineElementInternalCategory)) {
                if (deliveryDetailCode == null) {
                    timelineElement = element;
                    break;
                } else if (element.getDetails().getDeliveryDetailCode().equals(deliveryDetailCode)) {
                    timelineElement = element;
                    break;
                }
            }
        }

        try {
            System.out.println("ELEMENT: " + timelineElement);
            Assertions.assertNotNull(timelineElement);

            Assertions.assertNotNull(timelineElement.getLegalFactsIds());
            Assertions.assertFalse(CollectionUtils.isEmpty(timelineElement.getLegalFactsIds()));
            Assertions.assertEquals(category, timelineElement.getLegalFactsIds().get(0).getCategory());
            LegalFactCategory categorySearch = LegalFactCategory.fromValue(timelineElement.getLegalFactsIds().get(0).getCategory());
            String key = timelineElement.getLegalFactsIds().get(0).getKey();
            String keySearch = null;
            //TODO Verificare....
            if (key.contains("PN_LEGAL_FACTS")) {
                keySearch = key.substring(key.indexOf("PN_LEGAL_FACTS"));
            } else if (key.contains("PN_NOTIFICATION_ATTACHMENTS")) {
                keySearch = key.substring(key.indexOf("PN_NOTIFICATION_ATTACHMENTS"));
            } else if (key.contains("PN_EXTERNAL_LEGAL_FACTS")) {
                keySearch = key.substring(key.indexOf("PN_EXTERNAL_LEGAL_FACTS"));
            } else if (key.contains("PN_F24")) {
                keySearch = key.substring(key.indexOf("PN_F24"));
            }


            String finalKeySearch = keySearch;
            if (pa) {
                LegalFactDownloadMetadataResponse legalFactDownloadMetadataResponse = this.b2bClient.getLegalFact(sharedSteps.getSentNotification().getIun(), categorySearch, finalKeySearch);
                //System.out.println("NOME FILE PEC RECIPIENT PA"+legalFactDownloadMetadataResponse.getFilename());
                Assertions.assertNotNull(legalFactDownloadMetadataResponse);
                Assertions.assertNotNull(legalFactDownloadMetadataResponse.getFilename());
                Assertions.assertTrue(legalFactDownloadMetadataResponse.getFilename().contains(".eml"));
            }

            if (webRecipient) {

                it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.v25.model.LegalFactDownloadMetadataResponse legalFactDownloadMetadataResponse = this.webRecipientClient.getLegalFact(sharedSteps.getSentNotification().getIun(),
                        sharedSteps.deepCopy(categorySearch,
                                it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.v25.model.LegalFactCategory.class),
                        finalKeySearch);
                // System.out.println("NOME FILE PEC RECIPIENT DEST"+legalFactDownloadMetadataResponse.getFilename());
                Assertions.assertNotNull(legalFactDownloadMetadataResponse);
                Assertions.assertNotNull(legalFactDownloadMetadataResponse.getFilename());
                Assertions.assertTrue(legalFactDownloadMetadataResponse.getFilename().contains(".eml"));
            }
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    private void downloadLegalFactId(String legalFactCategory, boolean pa, boolean appIO, boolean webRecipient, String deliveryDetailCode) {
        try {
            Thread.sleep(sharedSteps.getWait());
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }

        PnTimelineLegalFactV23 categoriesV23 = pnTimelineAndLegalFactV23.getCategory(legalFactCategory);


        TimelineElementV25 timelineElement = null;

        for (TimelineElementV25 element : sharedSteps.getSentNotification().getTimeline()) {

            if (element.getCategory().equals(categoriesV23.getTimelineElementInternalCategory())) {
                if (deliveryDetailCode == null) {
                    timelineElement = element;
                    break;
                } else if (element.getDetails().getDeliveryDetailCode().equals(deliveryDetailCode)) {
                    timelineElement = element;
                    break;
                }
            }
        }

        try {
            System.out.println("ELEMENT: " + timelineElement);
            Assertions.assertNotNull(timelineElement.getLegalFactsIds());
            Assertions.assertFalse(CollectionUtils.isEmpty(timelineElement.getLegalFactsIds()));
            Assertions.assertEquals(categoriesV23.getLegalFactCategory().getValue(), timelineElement.getLegalFactsIds().get(0).getCategory());
            LegalFactCategory categorySearch = LegalFactCategory.fromValue(timelineElement.getLegalFactsIds().get(0).getCategory());
            String key = timelineElement.getLegalFactsIds().get(0).getKey();
            String finalKeySearch = getKeyLegalFact(key);

            if (pa) {
                Assertions.assertDoesNotThrow(() -> this.b2bClient.getDownloadLegalFact(sharedSteps.getSentNotification().getIun(), finalKeySearch));
            }
            if (appIO) {

                // Assertions.assertDoesNotThrow(() -> this.appIOB2bClient.getLegalFact(sharedSteps.getSentNotification().getIun(), categorySearch.toString(), finalKeySearch,
                //        sharedSteps.getSentNotification().getRecipients().get(0).getTaxId()));
            }
            if (webRecipient) {
                Assertions.assertDoesNotThrow(() -> this.webRecipientClient.getLegalFact(sharedSteps.getSentNotification().getIun(),
                        sharedSteps.deepCopy(categorySearch,
                                it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.v25.model.LegalFactCategory.class),
                        finalKeySearch
                ));
            }
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("si verifica che la notifica abbia lo stato VIEWED")
    public void checksNotificationViewedStatus() {
        String status = NotificationStatus.VIEWED.getValue();
        PnPollingServiceStatusRapidV25 statusRapidV25 = (PnPollingServiceStatusRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.STATUS_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = statusRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(status)
                        .build());
        log.info("NOTIFICATION: " + pnPollingResponseV25.getNotification());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getNotificationStatusHistoryElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("si verifica che la notifica non abbia lo stato {string}")
    public void checksNotificationNotHaveStatus(String status) {
        //AL MOMENTO NON ESISTE UNO SCENARIO CHE INTEGRA QUESTO STEP
        PnPollingServiceStatusRapidV25 statusRapidV25 = (PnPollingServiceStatusRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.STATUS_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = statusRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(status)
                        .build());
        log.info("NOTIFICATION: " + pnPollingResponseV25.getNotification());
        try {
            Assertions.assertFalse(pnPollingResponseV25.getResult());
            Assertions.assertNull(pnPollingResponseV25.getNotificationStatusHistoryElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
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

        if (versione.equalsIgnoreCase("V1")) {
            priceVerificationV1(price, null, 0);
        } else if (versione.equalsIgnoreCase("V2")) {
            priceVerificationV2(price, null, 0);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Then("vengono verificati costo = {string} e data di perfezionamento della notifica V2")
    public void notificationPriceAndDateVerificationV2(String price) {
        try {
            Thread.sleep(sharedSteps.getWait() * 2);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        priceVerificationV2(price, null, 0);
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

        Assertions.assertTrue((this.notificationError != null) &&
                (this.notificationError.getStatusCode().toString().substring(0, 3).equals(errorCode)));
    }

    @Then("viene verificato il costo = {string} della notifica per l'utente {int}")
    public void notificationPriceVerificationPerDestinatario(String price, Integer destinatario) {
        try {
            Thread.sleep(sharedSteps.getWait() * 2);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        priceVerification(price, destinatario);
    }

    private void priceVerification(String price, Integer destinatario) {
        if (sharedSteps.getSentNotification() != null) {
            List<NotificationPaymentItem> listNotificationPaymentItem = sharedSteps.getSentNotification().getRecipients().get(destinatario).getPayments();
            if (listNotificationPaymentItem != null) {
                for (NotificationPaymentItem notificationPaymentItem : listNotificationPaymentItem) {
                    it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPriceResponse notificationPrice = this.b2bClient.getNotificationPrice(notificationPaymentItem.getPagoPa().getCreditorTaxId(), notificationPaymentItem.getPagoPa().getNoticeCode());
                    try {
                        Assertions.assertEquals(notificationPrice.getIun(), sharedSteps.getSentNotification().getIun());
                        if (price != null) {
                            log.info("Costo notifica: {} destinatario: {}", notificationPrice.getAmount(), destinatario);
                            Assertions.assertEquals(notificationPrice.getAmount(), Integer.parseInt(price));
                        }
                        if (notificationPrice.getRefinementDate() != null) {
                            Assertions.assertEquals(OffsetDateTime.now().toLocalDate(), notificationPrice.getRefinementDate().toLocalDate());
                        }
                    } catch (AssertionFailedError assertionFailedError) {
                        sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
                    }
                }
            }
        } else if (sharedSteps.getSentNotificationV1() != null) {
            priceVerificationV1(price, null, destinatario);
        } else if (sharedSteps.getSentNotificationV2() != null) {
            priceVerificationV2(price, null, destinatario);
        }
    }

    private void priceVerificationV1(String price, String date, Integer destinatario) {
        List<String> datiPagamento = sharedSteps.getDatiPagamentoVersionamento(destinatario, 0);
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPriceResponse notificationPrice = this.b2bClient.getNotificationPrice(datiPagamento.get(0),
                datiPagamento.get(1));
        try {
            Assertions.assertEquals(notificationPrice.getIun(), sharedSteps.getIunVersionamento());
            if (price != null) {
                log.info("Costo notifica: {} destinatario: {}", notificationPrice.getAmount(), destinatario);
                Assertions.assertEquals(notificationPrice.getAmount(), Integer.parseInt(price));
            }
            if (date != null) {
                Assertions.assertNotNull(notificationPrice.getRefinementDate());
            }
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    private void priceVerificationV2(String price, String date, Integer destinatario) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPriceResponse notificationPrice = this.b2bClient.getNotificationPrice(sharedSteps.getSentNotificationV2().getRecipients().get(destinatario).getPayment().getCreditorTaxId(),
                sharedSteps.getSentNotificationV2().getRecipients().get(destinatario).getPayment().getNoticeCode());
        try {
            Assertions.assertEquals(notificationPrice.getIun(), sharedSteps.getSentNotificationV2().getIun());
            if (price != null) {
                log.info("Costo notifica: {} destinatario: {}", notificationPrice.getAmount(), destinatario);
                Assertions.assertEquals(notificationPrice.getAmount(), Integer.parseInt(price));
            }
            if (date != null) {
                Assertions.assertNotNull(notificationPrice.getRefinementDate());
            }
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    public List<NotificationPriceResponseV23> priceVerificationV23(Integer price, String date, Integer destinatario, String tipologiaCosto) {

        if (sharedSteps.getSentNotification() != null) {
            List<NotificationPaymentItem> listNotificationPaymentItem = sharedSteps.getSentNotification().getRecipients().get(destinatario).getPayments();
            List<NotificationPriceResponseV23> listNotificationPriceV23 = new ArrayList<>();


            if (listNotificationPaymentItem != null) {
                for (NotificationPaymentItem notificationPaymentItem : listNotificationPaymentItem) {
                    NotificationPriceResponseV23 notificationPriceV23 = this.b2bClient.getNotificationPriceV23(notificationPaymentItem.getPagoPa().getCreditorTaxId(), notificationPaymentItem.getPagoPa().getNoticeCode());

                    try {
                        Assertions.assertEquals(notificationPriceV23.getIun(), sharedSteps.getSentNotification().getIun());
                        if (price != null) {
                            log.info("notificationPriceV23: {} destinatario: {}", notificationPriceV23, destinatario);
                            switch (tipologiaCosto.toLowerCase()) {
                                case "parziale":
                                    Assertions.assertEquals(price, notificationPriceV23.getPartialPrice());
                                    break;
                                case "totale":
                                    Assertions.assertEquals(price, notificationPriceV23.getTotalPrice());
                                    break;
                            }
                        }
                        if (date != null) {
                            Assertions.assertNotNull(notificationPriceV23.getRefinementDate());
                            listNotificationPriceV23.add(notificationPriceV23);
                        }

                    } catch (AssertionFailedError assertionFailedError) {
                        sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
                    }
                }
                return listNotificationPriceV23;
            }
        }
        return null;
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
        NotificationProcessCostResponse notificationProcessCost = null;
        if (sharedSteps.getSentNotification().getNotificationFeePolicy().equals(NotificationFeePolicy.DELIVERY_MODE)) {
            notificationProcessCost = this.b2bClient.getNotificationProcessCost(sharedSteps.getSentNotification().getIun(), destinatario, it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.NotificationFeePolicy.DELIVERY_MODE, sharedSteps.getSentNotification().getRecipients().get(destinatario).getPayments().get(0).getF24().getApplyCost(), sharedSteps.getSentNotification().getPaFee(), sharedSteps.getSentNotification().getVat());
        } else {
            notificationProcessCost = this.b2bClient.getNotificationProcessCost(sharedSteps.getSentNotification().getIun(), destinatario, it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.NotificationFeePolicy.FLAT_RATE, sharedSteps.getSentNotification().getRecipients().get(destinatario).getPayments().get(0).getF24().getApplyCost(), sharedSteps.getSentNotification().getPaFee(), sharedSteps.getSentNotification().getVat());
        }
        try {
            if (price != null) {
                log.info("Costo notifica: {} destinatario: {}", notificationProcessCost.getAnalogCost(), destinatario);
                Assertions.assertEquals(notificationProcessCost.getAnalogCost(), Integer.parseInt(price));
            }
            if (date != null) {
                Assertions.assertNotNull(notificationProcessCost.getRefinementDate());
            }
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);

        }
    }

    @And("{string} tenta di leggere la notifica ricevuta")
    public void userReadReceivedNotificationWithError(String recipient) {
        sharedSteps.selectUser(recipient);

        String iun = sharedSteps.getIunVersionamento();

        try {
            webRecipientClient.getReceivedNotification(iun, null);
        } catch (HttpStatusCodeException e) {
            if (e instanceof HttpStatusCodeException) {
                sharedSteps.setNotificationError(e);
            }
        }

    }

    @And("{string} legge la notifica ricevuta")
    public void userReadReceivedNotification(String recipient) {
        sharedSteps.selectUser(recipient);

        String iun = sharedSteps.getIunVersionamento();

        Assertions.assertDoesNotThrow(() -> {
            webRecipientClient.getReceivedNotification(iun, null);
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

        String iun = sharedSteps.getIunVersionamento();

        try {
            if (versione.equalsIgnoreCase("V1")) {
                webRecipientClient.getReceivedNotificationV1(iun, null);
            } else {
                webRecipientClient.getReceivedNotificationV2(iun, null);
            }

            try {
                Thread.sleep(sharedSteps.getWorkFlowWait());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

        } catch (HttpStatusCodeException e) {
            if (e instanceof HttpStatusCodeException) {
                sharedSteps.setNotificationError((HttpStatusCodeException) e);
            }
        }


    }

    @Then("viene verificato che la chiave dell'attestazione opponibile {string} è {string}")
    public void verifiedThatTheKeyOfTheLegalFactIs(String legalFactCategory, String key) {
        try {
            Thread.sleep(sharedSteps.getWait());
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }

        PnTimelineLegalFactV23 categoriesV23 = pnTimelineAndLegalFactV23.getCategory(legalFactCategory);
        TimelineElementV25 timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(categoriesV23.getTimelineElementInternalCategory())).findAny().orElse(null);

        try {
            Assertions.assertNotNull(timelineElement.getLegalFactsIds());
            Assertions.assertEquals(categoriesV23.getLegalFactCategory().getValue(), timelineElement.getLegalFactsIds().get(0).getCategory());
            Assertions.assertTrue(timelineElement.getLegalFactsIds().get(0).getKey().contains(key));
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @And("l'avviso pagopa viene pagato correttamente")
    public void laNotificaVienePagata() {
        NotificationPriceResponseV23 notificationPrice = this.b2bClient.getNotificationPriceV23(sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(0).getPagoPa().getCreditorTaxId(),
                sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(0).getPagoPa().getNoticeCode());

        PaymentEventsRequestPagoPa eventsRequestPagoPa = new PaymentEventsRequestPagoPa();

        PaymentEventPagoPa paymentEventPagoPa = new PaymentEventPagoPa();
        paymentEventPagoPa.setNoticeCode(sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(0).getPagoPa().getNoticeCode());
        paymentEventPagoPa.setCreditorTaxId(sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(0).getPagoPa().getCreditorTaxId());
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        paymentEventPagoPa.setPaymentDate(fmt.format(now().atZoneSameInstant(ZoneId.of("UTC"))));
        paymentEventPagoPa.setAmount(notificationPrice.getTotalPrice());

        List<PaymentEventPagoPa> paymentEventPagoPaList = new LinkedList<>();
        paymentEventPagoPaList.add(paymentEventPagoPa);

        eventsRequestPagoPa.setEvents(paymentEventPagoPaList);

        b2bClient.paymentEventsRequestPagoPa(eventsRequestPagoPa);
    }

    @And("l'avviso pagopa viene pagato correttamente dall'utente {int}")
    public void laNotificaVienePagataMulti(Integer utente) {

        if (sharedSteps.getSentNotification() != null) {
            NotificationPriceResponseV23 notificationPrice = this.b2bClient.getNotificationPriceV23(sharedSteps.getSentNotification().getRecipients().get(utente).getPayments().get(0).getPagoPa().getCreditorTaxId(),
                    sharedSteps.getSentNotification().getRecipients().get(utente).getPayments().get(0).getPagoPa().getNoticeCode());

            PaymentEventsRequestPagoPa eventsRequestPagoPa = new PaymentEventsRequestPagoPa();

            PaymentEventPagoPa paymentEventPagoPa = new PaymentEventPagoPa();
            paymentEventPagoPa.setNoticeCode(sharedSteps.getSentNotification().getRecipients().get(utente).getPayments().get(0).getPagoPa().getNoticeCode());
            paymentEventPagoPa.setCreditorTaxId(sharedSteps.getSentNotification().getRecipients().get(utente).getPayments().get(0).getPagoPa().getCreditorTaxId());
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            paymentEventPagoPa.setPaymentDate(fmt.format(now()));
            paymentEventPagoPa.setAmount(notificationPrice.getTotalPrice());

            List<PaymentEventPagoPa> paymentEventPagoPaList = new LinkedList<>();
            paymentEventPagoPaList.add(paymentEventPagoPa);

            eventsRequestPagoPa.setEvents(paymentEventPagoPaList);

            b2bClient.paymentEventsRequestPagoPa(eventsRequestPagoPa);
        } else if (sharedSteps.getSentNotificationV1() != null) {
            NotificationPriceResponseV23 notificationPrice = this.b2bClient.getNotificationPriceV23(sharedSteps.getSentNotificationV1().getRecipients().get(utente).getPayment().getCreditorTaxId(),
                    sharedSteps.getSentNotificationV1().getRecipients().get(utente).getPayment().getNoticeCode());

            PaymentEventsRequestPagoPa eventsRequestPagoPa = new PaymentEventsRequestPagoPa();

            PaymentEventPagoPa paymentEventPagoPa = new PaymentEventPagoPa();
            paymentEventPagoPa.setNoticeCode(sharedSteps.getSentNotificationV1().getRecipients().get(utente).getPayment().getNoticeCode());
            paymentEventPagoPa.setCreditorTaxId(sharedSteps.getSentNotificationV1().getRecipients().get(utente).getPayment().getCreditorTaxId());
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            paymentEventPagoPa.setPaymentDate(fmt.format(now()));
            paymentEventPagoPa.setAmount(notificationPrice.getPartialPrice());

            List<PaymentEventPagoPa> paymentEventPagoPaList = new LinkedList<>();
            paymentEventPagoPaList.add(paymentEventPagoPa);

            eventsRequestPagoPa.setEvents(paymentEventPagoPaList);

            b2bClient.paymentEventsRequestPagoPa(eventsRequestPagoPa);
        }


    }

    @And("l'avviso pagopa viene pagato correttamente dall'utente {int} V1")
    public void laNotificaVienePagataMultiV1(Integer utente) {

        String noticeCode = null;
        String creditorTaxId = null;

        if (sharedSteps.getSentNotificationV1() != null) {
            noticeCode = sharedSteps.getSentNotificationV1().getRecipients().get(utente).getPayment().getNoticeCode();
            creditorTaxId = sharedSteps.getSentNotificationV1().getRecipients().get(utente).getPayment().getCreditorTaxId();
        } else if (sharedSteps.getSentNotificationV2() != null) {
            noticeCode = sharedSteps.getSentNotificationV2().getRecipients().get(utente).getPayment().getNoticeCode();
            creditorTaxId = sharedSteps.getSentNotificationV2().getRecipients().get(utente).getPayment().getCreditorTaxId();
        } else if (sharedSteps.getSentNotification() != null) {
            noticeCode = sharedSteps.getSentNotification().getRecipients().get(utente).getPayments().get(0).getPagoPa().getNoticeCode();
            creditorTaxId = sharedSteps.getSentNotification().getRecipients().get(utente).getPayments().get(0).getPagoPa().getCreditorTaxId();
        }

        NotificationPriceResponseV23 notificationPrice = this.b2bClient.getNotificationPriceV23(creditorTaxId, noticeCode);

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.PaymentEventsRequestPagoPa eventsRequestPagoPa = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.PaymentEventsRequestPagoPa();

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.PaymentEventPagoPa paymentEventPagoPa = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.PaymentEventPagoPa();
        paymentEventPagoPa.setNoticeCode(noticeCode);
        paymentEventPagoPa.setCreditorTaxId(creditorTaxId);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        paymentEventPagoPa.setPaymentDate(fmt.format(now()));
        paymentEventPagoPa.setAmount(notificationPrice.getPartialPrice());

        List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.PaymentEventPagoPa> paymentEventPagoPaList = new LinkedList<>();
        paymentEventPagoPaList.add(paymentEventPagoPa);

        eventsRequestPagoPa.setEvents(paymentEventPagoPaList);

        b2bClient.paymentEventsRequestPagoPaV1(eventsRequestPagoPa);


    }

    @And("l'avviso pagopa viene pagato correttamente dall'utente {int} V2")
    public void laNotificaVienePagataMultiV2(Integer utente) {

        String noticeCode = null;
        String creditorTaxId = null;

        if (sharedSteps.getSentNotificationV1() != null) {
            noticeCode = sharedSteps.getSentNotificationV1().getRecipients().get(utente).getPayment().getNoticeCode();
            creditorTaxId = sharedSteps.getSentNotificationV1().getRecipients().get(utente).getPayment().getCreditorTaxId();
        } else if (sharedSteps.getSentNotificationV2() != null) {
            noticeCode = sharedSteps.getSentNotificationV2().getRecipients().get(utente).getPayment().getNoticeCode();
            creditorTaxId = sharedSteps.getSentNotificationV2().getRecipients().get(utente).getPayment().getCreditorTaxId();
        } else if (sharedSteps.getSentNotification() != null) {
            noticeCode = sharedSteps.getSentNotification().getRecipients().get(utente).getPayments().get(0).getPagoPa().getNoticeCode();
            creditorTaxId = sharedSteps.getSentNotification().getRecipients().get(utente).getPayments().get(0).getPagoPa().getCreditorTaxId();
        }


        NotificationPriceResponseV23 notificationPrice = this.b2bClient.getNotificationPriceV23(creditorTaxId, noticeCode);

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.PaymentEventsRequestPagoPa eventsRequestPagoPa = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.PaymentEventsRequestPagoPa();

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.PaymentEventPagoPa paymentEventPagoPa = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.PaymentEventPagoPa();
        paymentEventPagoPa.setNoticeCode(noticeCode);
        paymentEventPagoPa.setCreditorTaxId(creditorTaxId);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        paymentEventPagoPa.setPaymentDate(fmt.format(now()));
        paymentEventPagoPa.setAmount(notificationPrice.getPartialPrice());

        List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.PaymentEventPagoPa> paymentEventPagoPaList = new LinkedList<>();
        paymentEventPagoPaList.add(paymentEventPagoPa);

        eventsRequestPagoPa.setEvents(paymentEventPagoPaList);

        b2bClient.paymentEventsRequestPagoPaV2(eventsRequestPagoPa);


    }

    @And("l'avviso pagopa {int} viene pagato correttamente dall'utente {int}")
    public void laNotificaVienePagataConAvvisoNumMulti(Integer idAvviso, Integer utente) {
        if (sharedSteps.getSentNotification() != null) {
            NotificationPriceResponseV23 notificationPrice = this.b2bClient.getNotificationPriceV23(sharedSteps.getSentNotification().getRecipients().get(utente).getPayments().get(idAvviso).getPagoPa().getCreditorTaxId(),
                    sharedSteps.getSentNotification().getRecipients().get(utente).getPayments().get(idAvviso).getPagoPa().getNoticeCode());

            PaymentEventsRequestPagoPa eventsRequestPagoPa = new PaymentEventsRequestPagoPa();

            PaymentEventPagoPa paymentEventPagoPa = new PaymentEventPagoPa();
            paymentEventPagoPa.setNoticeCode(sharedSteps.getSentNotification().getRecipients().get(utente).getPayments().get(idAvviso).getPagoPa().getNoticeCode());
            paymentEventPagoPa.setCreditorTaxId(sharedSteps.getSentNotification().getRecipients().get(utente).getPayments().get(idAvviso).getPagoPa().getCreditorTaxId());
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            paymentEventPagoPa.setPaymentDate(fmt.format(now()));
            paymentEventPagoPa.setAmount(notificationPrice.getTotalPrice());

            List<PaymentEventPagoPa> paymentEventPagoPaList = new LinkedList<>();
            paymentEventPagoPaList.add(paymentEventPagoPa);

            eventsRequestPagoPa.setEvents(paymentEventPagoPaList);

            b2bClient.paymentEventsRequestPagoPa(eventsRequestPagoPa);
        } else if (sharedSteps.getSentNotificationV1() != null) {
            NotificationPriceResponseV23 notificationPrice = this.b2bClient.getNotificationPriceV23(sharedSteps.getSentNotificationV1().getRecipients().get(0).getPayment().getCreditorTaxId(),
                    sharedSteps.getSentNotificationV1().getRecipients().get(utente).getPayment().getNoticeCode());

            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.PaymentEventsRequestPagoPa eventsRequestPagoPa = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.PaymentEventsRequestPagoPa();

            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.PaymentEventPagoPa paymentEventPagoPa = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.PaymentEventPagoPa();
            paymentEventPagoPa.setNoticeCode(sharedSteps.getSentNotificationV1().getRecipients().get(utente).getPayment().getNoticeCode());
            paymentEventPagoPa.setCreditorTaxId(sharedSteps.getSentNotificationV1().getRecipients().get(0).getPayment().getCreditorTaxId());
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            paymentEventPagoPa.setPaymentDate(fmt.format(now()));
            paymentEventPagoPa.setAmount(notificationPrice.getPartialPrice());

            List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.PaymentEventPagoPa> paymentEventPagoPaList = new LinkedList<>();
            paymentEventPagoPaList.add(paymentEventPagoPa);

            eventsRequestPagoPa.setEvents(paymentEventPagoPaList);

            b2bClient.paymentEventsRequestPagoPaV1(eventsRequestPagoPa);
        }

    }

    @And("gli avvisi PagoPa vengono pagati correttamente dal destinatario {int}")
    public void laNotificaVienePagataConAvvisoNumMultiPagoPa(Integer destinatario) {

        List<PaymentEventPagoPa> paymentEventPagoPaList = new LinkedList<>();
        PaymentEventsRequestPagoPa eventsRequestPagoPa = new PaymentEventsRequestPagoPa();
        for (NotificationPaymentItem notificationPaymentItem : sharedSteps.getSentNotification().getRecipients().get(destinatario).getPayments()) {
            NotificationPriceResponseV23 notificationPrice = this.b2bClient.getNotificationPriceV23(notificationPaymentItem.getPagoPa().getCreditorTaxId(), notificationPaymentItem.getPagoPa().getNoticeCode());
            PaymentEventPagoPa paymentEventPagoPa = new PaymentEventPagoPa();
            paymentEventPagoPa.setNoticeCode(notificationPaymentItem.getPagoPa().getNoticeCode());
            paymentEventPagoPa.setCreditorTaxId(notificationPaymentItem.getPagoPa().getCreditorTaxId());
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            paymentEventPagoPa.setPaymentDate(fmt.format(now()));
            paymentEventPagoPa.setAmount(notificationPrice.getTotalPrice());
            paymentEventPagoPaList.add(paymentEventPagoPa);
        }

        eventsRequestPagoPa.setEvents(paymentEventPagoPaList);

        b2bClient.paymentEventsRequestPagoPa(eventsRequestPagoPa);
    }

    @And("viene rifiutato il pagamento dell'avviso pagopa  dall'utente {int}")
    public void laNotificaVieneRifiutatoIlPagamentoMulti(Integer utente) {
        NotificationPriceResponseV23 notificationPrice = this.b2bClient.getNotificationPriceV23(sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(0).getPagoPa().getCreditorTaxId(),
                sharedSteps.getSentNotification().getRecipients().get(utente).getPayments().get(0).getPagoPa().getNoticeCode());

        PaymentEventsRequestPagoPa eventsRequestPagoPa = new PaymentEventsRequestPagoPa();

        PaymentEventPagoPa paymentEventPagoPa = new PaymentEventPagoPa();
        paymentEventPagoPa.setNoticeCode(sharedSteps.getSentNotification().getRecipients().get(utente).getPayments().get(0).getPagoPa().getNoticeCode());
        paymentEventPagoPa.setCreditorTaxId(sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(0).getPagoPa().getCreditorTaxId());
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        paymentEventPagoPa.setPaymentDate(fmt.format(now()));
        paymentEventPagoPa.setAmount(notificationPrice.getTotalPrice());

        List<PaymentEventPagoPa> paymentEventPagoPaList = new LinkedList<>();
        paymentEventPagoPaList.add(paymentEventPagoPa);

        eventsRequestPagoPa.setEvents(paymentEventPagoPaList);

        b2bClient.paymentEventsRequestPagoPa(eventsRequestPagoPa);
    }


    @Then("sono presenti {int} attestazioni opponibili RECIPIENT_ACCESS")
    public void sonoPresentiAttestazioniOpponibili(int number) {
        String timelineEventCategory = TimelineElementCategoryV23.NOTIFICATION_VIEWED.getValue();
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            List<TimelineElementV25> listTimelineElement = pnPollingResponseV25
                    .getNotification()
                    .getTimeline()
                    .stream()
                    .filter(elem -> Objects.requireNonNull(elem.getCategory()).getValue().equals(timelineEventCategory))
                    .toList();
            Assertions.assertNotNull(listTimelineElement);
            Assertions.assertEquals(number, listTimelineElement.size());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con responseStatus {string} per l'utente {int}")
    public void vengonoLettiGliEventiFinoAllElementoDiTimelineDellaNotificaConResponseStatusPerUtente(String timelineEventCategory, String code, Integer destinatario) {
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .pnPollingPredicate(getPnPollingPredicateForTimelineV25(timelineEventCategory, destinatario))
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            TimelineElementV25 timelineElement = pnPollingResponseV25.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNotNull(Objects.requireNonNull(Objects.requireNonNull(timelineElement).getDetails()).getResponseStatus());
            Assertions.assertEquals(timelineElement.getDetails().getResponseStatus().getValue(), code);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con responseStatus {string}")
    public void vengonoLettiGliEventiFinoAllElementoDiTimelineDellaNotificaConResponseStatus(String timelineEventCategory, String code) {
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            TimelineElementV25 timelineElement = pnPollingResponseV25.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNotNull(Objects.requireNonNull(timelineElement.getDetails()).getResponseStatus());
            Assertions.assertEquals(timelineElement.getDetails().getResponseStatus().getValue(), code);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con responseStatus {string} e digitalAddressSource {string}")
    public void vengonoLettiGliEventiFinoAllElementoDiTimelineDellaNotificaConResponseStatusAndDigitalAddressSource(String timelineEventCategory, String code, String digitalAddressSource) {
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            TimelineElementV25 timelineElement = pnPollingResponseV25.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNotNull(Objects.requireNonNull(timelineElement.getDetails()).getResponseStatus());
            Assertions.assertEquals(timelineElement.getDetails().getResponseStatus().getValue(), code);
            Assertions.assertEquals(timelineElement.getDetails().getDigitalAddressSource().getValue(), digitalAddressSource);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("viene verificato che nell'elemento di timeline della notifica {string} siano configurati i campi municipalityDetails e foreignState")
    public void vieneVerificatoCheElementoTimelineSianoConfiguratiCampiMunicipalityDetailsForeignState(String timelineEventCategory) {
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            TimelineElementV25 timelineElement = pnPollingResponseV25.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNotNull(Objects.requireNonNull(timelineElement.getDetails()).getPhysicalAddress().getMunicipality());
            Assertions.assertNotNull(timelineElement.getDetails().getPhysicalAddress().getForeignState());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("viene verificato che nell'elemento di timeline della notifica {string} con responseStatus {string} sia presente il campo deliveryDetailCode")
    public void vieneVerificatoCheElementoTimelineSianoConfiguratoCampoDeliveryDetailCode(String timelineEventCategory, String code) {
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            TimelineElementV25 timelineElement = pnPollingResponseV25.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNotNull(Objects.requireNonNull(timelineElement.getDetails()).getResponseStatus());
            Assertions.assertEquals(timelineElement.getDetails().getResponseStatus().getValue(), code);
            Assertions.assertNotNull(timelineElement.getDetails().getDeliveryDetailCode());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("viene verificato che nell'elemento di timeline della notifica {string} con responseStatus {string} sia presente i campi deliveryDetailCode e deliveryFailureCause")
    public void vieneVerificatoCheElementoTimelineSianoConfiguratoCampoDeliveryDetailCodeDeliveryFailureCause(String timelineEventCategory, String code) {
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            TimelineElementV25 timelineElement = pnPollingResponseV25.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNotNull(Objects.requireNonNull(timelineElement.getDetails()).getResponseStatus());
            Assertions.assertEquals(timelineElement.getDetails().getResponseStatus().getValue(), code);
            Assertions.assertNotNull(timelineElement.getDetails().getDeliveryDetailCode());
            Assertions.assertNotNull(timelineElement.getDetails().getDeliveryFailureCause());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("si attende la corretta sospensione dell'invio cartaceo")
    public void siAttendeLaCorrettaSopsensioneDellInvioCartaceo() {
        String timelineEventCategory = TimelineElementCategoryV23.ANALOG_SUCCESS_WORKFLOW.getValue();
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertFalse(pnPollingResponseV25.getResult());
            Assertions.assertNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("si attende il corretto pagamento della notifica")
    public void siAttendeIlCorrettoPagamentoDellaNotifica() {
        String iun;
        if (sharedSteps.getSentNotification() != null) {
            iun = sharedSteps.getSentNotification().getIun();
        } else {
            iun = sharedSteps.getSentNotificationV1().getIun();
        }

        String timelineEventCategory = TimelineElementCategoryV23.PAYMENT.getValue();
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(iun,
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            log.info("TIMELINE_ELEMENT: " + pnPollingResponseV25.getTimelineElement());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("si attende il corretto pagamento della notifica V1")
    public void siAttendeIlCorrettoPagamentoDellaNotificaV1() {
        String timelineEventCategory = TimelineElementCategoryV23.PAYMENT.getValue();
        PnPollingServiceTimelineRapidV1 timelineRapidV1 = (PnPollingServiceTimelineRapidV1) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V1);

        PnPollingResponseV1 pnPollingResponseV1 = timelineRapidV1.waitForEvent(sharedSteps.getIunVersionamento(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV1.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV1.getResult());
            Assertions.assertNotNull(pnPollingResponseV1.getTimelineElement());
            sharedSteps.setSentNotificationV1(pnPollingResponseV1.getNotification());
            log.info("TIMELINE_ELEMENT: " + pnPollingResponseV1.getTimelineElement());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("si attende il corretto pagamento della notifica V2")
    public void siAttendeIlCorrettoPagamentoDellaNotificaV2() {
        String timelineEventCategory = TimelineElementCategoryV23.PAYMENT.getValue();
        PnPollingServiceTimelineRapidV20 timelineRapidV2 = (PnPollingServiceTimelineRapidV20) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V20);

        PnPollingResponseV20 pnPollingResponseV20 = timelineRapidV2.waitForEvent(sharedSteps.getIunVersionamento(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV20.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV20.getResult());
            Assertions.assertNotNull(pnPollingResponseV20.getTimelineElement());
            sharedSteps.setSentNotificationV2(pnPollingResponseV20.getNotification());
            log.info("TIMELINE_ELEMENT: " + pnPollingResponseV20.getTimelineElement());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("si attende il corretto pagamento della notifica con l' avviso {int} dal destinatario {int}")
    public void siAttendeIlCorrettoPagamentoDellaNotificaConAvvisoDalDestinatario(Integer avviso, Integer destinatario) {
        String timelineEventCategory = TimelineElementCategoryV23.PAYMENT.getValue();
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            TimelineElementV25 timelineElement = pnPollingResponseV25.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            if (Objects.requireNonNull(timelineElement.getDetails()).getRecIndex().equals(destinatario)) {
                boolean esiste = false;
                if (pnPollingResponseV25.getNotification().getRecipients().get(destinatario).getPayments() != null) {
                    NotificationPaymentItem notificationPaymentItem = pnPollingResponseV25
                            .getNotification()
                            .getRecipients()
                            .get(destinatario)
                            .getPayments()
                            .stream()
                            .filter(pay -> Objects.requireNonNull(pay.getPagoPa()).getCreditorTaxId().equals(timelineElement.getDetails().getCreditorTaxId())
                                    && pay.getPagoPa().getNoticeCode().equals(timelineElement.getDetails().getNoticeCode())).findAny().orElse(null);
                    esiste = notificationPaymentItem != null;
                }
                Assertions.assertTrue(esiste);
                //Assertions.assertTrue(sharedSteps.getSentNotification().getRecipients().get(destinatario).getPayments().get(avviso).getPagoPa().getCreditorTaxId().equals(timelineElement.getDetails().getCreditorTaxId()));
                //Assertions.assertTrue(sharedSteps.getSentNotification().getRecipients().get(destinatario).getPayments().get(avviso).getPagoPa().getNoticeCode().equals(timelineElement.getDetails().getNoticeCode()));
            }
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("si attende il non corretto pagamento della notifica con l' avviso {int} dal destinatario {int}")
    public void siAttendeIlNonCorrettoPagamentoDellaNotificaConAvvisoDalDestinatario(Integer avviso, Integer destinatario) {
        String timelineEventCategory = TimelineElementCategoryV23.PAYMENT.getValue();
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertFalse(pnPollingResponseV25.getResult());
            Assertions.assertNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            //Assertions.assertTrue(sharedSteps.getSentNotification().getRecipients().get(destinatario).getPayments().get(avviso).getPagoPa().getCreditorTaxId().equals(timelineElement.getDetails().getCreditorTaxId()));
            //Assertions.assertTrue(sharedSteps.getSentNotification().getRecipients().get(destinatario).getPayments().get(avviso).getPagoPa().getNoticeCode().equals(timelineElement.getDetails().getNoticeCode()));
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("si attende il corretto pagamento della notifica dell'utente {int}")
    public void siAttendeIlCorrettoPagamentoDellaNotifica(Integer utente) {
        String timelineEventCategory = TimelineElementCategoryV23.PAYMENT.getValue();
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .pnPollingPredicate(getPnPollingPredicateForTimelineV25(timelineEventCategory, utente))
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            log.info("TIMELINE_ELEMENT: " + pnPollingResponseV25.getTimelineElement());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("verifica presenza in Timeline dei solo pagamenti di avvisi PagoPA del destinatario {int}")
    public void verificaPresenzaPagamentiSoloPagopa(Integer utente) {
        String timelineEventCategory = TimelineElementCategoryV23.PAYMENT.getValue();
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .pnPollingPredicate(getPnPollingPredicateForTimelineV25(timelineEventCategory, utente))
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            TimelineElementV25 timelineElement = pnPollingResponseV25.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNull(Objects.requireNonNull(timelineElement.getDetails()).getIdF24());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("verifica non presenza in Timeline di pagamenti con avvisi F24 del destinatario {int}")
    public void verificaNonPresenzaPagamentiF24(Integer utente) {
        //AL MOMENTO NON ESISTE UNO SCENARIO CHE INTEGRA QUESTO STEP
        String timelineEventCategory = TimelineElementCategoryV23.PAYMENT.getValue();
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .pnPollingPredicate(getPnPollingPredicateForTimelineV25(timelineEventCategory, utente, null, null, null, null, true, false, null, false, null))
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("si attende il non corretto pagamento della notifica dell'utente {int}")
    public void siAttendeIlNonCorrettoPagamentoDellaNotifica(Integer utente) {
        //AL MOMENTO NON ESISTE UNO SCENARIO CHE INTEGRA QUESTO STEP
        String timelineEventCategory = TimelineElementCategoryV23.PAYMENT.getValue();
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .pnPollingPredicate(getPnPollingPredicateForTimelineV25(timelineEventCategory, utente))
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertFalse(pnPollingResponseV25.getResult());
            Assertions.assertNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("viene verificato che nell'elemento di timeline della notifica {string} e' presente il campo Digital Address di piattaforma")
    public void vieneVerificatoCheElementoTimelineSianoConfiguratoCampoDigitalAddressPiattaforma(String timelineEventCategory) {
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .pnPollingPredicate(getPnPollingPredicateForTimelineV25(timelineEventCategory, null, null, "SOURCE_PLATFORM", null, null, false, false, null, false, null))
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            TimelineElementV25 timelineElement = pnPollingResponseV25.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNotNull(Objects.requireNonNull(timelineElement.getDetails()).getDigitalAddress());
            Assertions.assertFalse("DSRDNI00A01A225I@pec.pagopa.it".equalsIgnoreCase(timelineElement.getDetails().getDigitalAddress().getAddress()));
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("viene verificato che nell'elemento di timeline della notifica {string} sia presente il campo Digital Address")
    public void vieneVerificatoCheElementoTimelineSianoConfiguratoCampoDigitalAddress(String timelineEventCategory) {
        //AL MOMENTO LO SCENARIO CHE INTEGRA QUESTO STEP E' @IGNORE
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .pnPollingPredicate(getPnPollingPredicateForTimelineV25(timelineEventCategory, null, null, "SOURCE_PLATFORM", null, null, false, false, null, false, null))
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            TimelineElementV25 timelineElement = pnPollingResponseV25.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNotNull(Objects.requireNonNull(timelineElement.getDetails()).getDigitalAddress());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("viene verificato che l'elemento di timeline {string} esista")
    public void vieneVerificatoElementoTimeline(String timelineEventCategory, @Transpose DataTest dataFromTest) {
        boolean mustLoadTimeline = dataFromTest != null ? dataFromTest.getLoadTimeline() : false;
        if (mustLoadTimeline) {
            loadTimeline(timelineEventCategory, true, dataFromTest);
        }
        try {

            TimelineElementV25 timelineElement = sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataFromTest);

            log.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNotNull(timelineElement);
            if (dataFromTest != null && dataFromTest.getTimelineElement() != null) {
                checkTimelineElementEquality(timelineEventCategory, timelineElement, dataFromTest);
            }
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("viene verificato che la data della timeline REFINEMENT sia ricezione della raccomandata + 10gg")
    public void verificationDateScheduleRefinementWithRefinementPlus10Days() {

        try {
            OffsetDateTime scheduleDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV23.SEND_ANALOG_FEEDBACK)).findAny().get().getTimestamp().plus(sharedSteps.getSchedulingDaysSuccessAnalogRefinement());
            OffsetDateTime refinementDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV23.REFINEMENT)).findAny().get().getTimestamp();
            log.info("scheduleDate : {}", scheduleDate);
            log.info("refinementDate : {}", refinementDate);

            Assertions.assertEquals(scheduleDate, refinementDate);

        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @And("viene verificato che l'elemento di timeline {string} non esista")
    public void vieneVerificatoCheElementoTimelineNonEsista(String timelineEventCategory, @Transpose DataTest dataFromTest) {
        loadTimeline(timelineEventCategory, false, dataFromTest);

        TimelineElementV25 timelineElement = sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataFromTest);

        try {
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNull(timelineElement);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @And("viene schedulato il perfezionamento per decorrenza termini per il caso {string}")
    public void vieneSchedulatoIlPerfezionamento(String timelineCategory, @Transpose DataTest dataFromTest) {

        TimelineElementV25 timelineElement = sharedSteps.getTimelineElementByEventId(TimelineElementCategoryV23.SCHEDULE_REFINEMENT.getValue(), dataFromTest);

        TimelineElementV25 timelineElementForDateCalculation = null;
        if (timelineCategory.equals(TimelineElementCategoryV23.DIGITAL_SUCCESS_WORKFLOW.getValue())) {
            timelineElementForDateCalculation = sharedSteps.getTimelineElementByEventId(TimelineElementCategoryV23.SEND_DIGITAL_FEEDBACK.getValue(), dataFromTest);
        } else if (timelineCategory.equals(TimelineElementCategoryV23.DIGITAL_FAILURE_WORKFLOW.getValue())) {
            timelineElementForDateCalculation = sharedSteps.getTimelineElementByEventId(TimelineElementCategoryV23.DIGITAL_DELIVERY_CREATION_REQUEST.getValue(), dataFromTest);
        } else if (timelineCategory.equals(TimelineElementCategoryV23.ANALOG_SUCCESS_WORKFLOW.getValue())) {
            timelineElementForDateCalculation = sharedSteps.getTimelineElementByEventId(TimelineElementCategoryV23.SEND_ANALOG_FEEDBACK.getValue(), dataFromTest);
        } else if (timelineCategory.equals(TimelineElementCategoryV23.ANALOG_FAILURE_WORKFLOW.getValue())) {
            timelineElementForDateCalculation = sharedSteps.getTimelineElementByEventId(TimelineElementCategoryV23.SEND_ANALOG_FEEDBACK.getValue(), dataFromTest);

        }

        Assertions.assertNotNull(timelineElementForDateCalculation);

        OffsetDateTime notificationDate = null;
        Duration schedulingDaysRefinement = null;

        if (timelineCategory.equals(TimelineElementCategoryV23.DIGITAL_SUCCESS_WORKFLOW.getValue())) {
            notificationDate = timelineElementForDateCalculation.getDetails().getNotificationDate();
            schedulingDaysRefinement = sharedSteps.getSchedulingDaysSuccessDigitalRefinement();
        } else if (timelineCategory.equals(TimelineElementCategoryV23.DIGITAL_FAILURE_WORKFLOW.getValue())) {
            notificationDate = timelineElementForDateCalculation.getTimestamp();
            schedulingDaysRefinement = sharedSteps.getSchedulingDaysFailureDigitalRefinement();
        } else if (timelineCategory.equals(TimelineElementCategoryV23.ANALOG_SUCCESS_WORKFLOW.getValue())) {
            notificationDate = timelineElementForDateCalculation.getTimestamp();
            schedulingDaysRefinement = sharedSteps.getSchedulingDaysSuccessAnalogRefinement();
        } else if (timelineCategory.equals(TimelineElementCategoryV23.ANALOG_FAILURE_WORKFLOW.getValue())) {
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
        Assertions.assertEquals(timelineElement.getDetails().getSchedulingDate().format(fmt1), schedulingDate.format(fmt1));
        //Assertions.assertEquals(timelineElement.getDetails().getSchedulingDate(), schedulingDate);
    }

    @And("si attende che sia presente il perfezionamento per decorrenza termini")
    public void siAttendePresenzaPerfezionamentoDecorrenzaTermini(@Transpose DataTest dataFromTest) throws InterruptedException {
        String iun = sharedSteps.getSentNotification().getIun();
        if (dataFromTest != null && dataFromTest.getTimelineElement() != null) {
            TimelineElementV25 timelineElement = sharedSteps.getTimelineElementByEventId(TimelineElementCategoryV23.SCHEDULE_REFINEMENT.getValue(), dataFromTest);

            OffsetDateTime schedulingDate = timelineElement.getDetails().getSchedulingDate();
            OffsetDateTime currentDate = now().atZoneSameInstant(ZoneId.of("UTC")).toOffsetDateTime();
            long remainingTime = ChronoUnit.MILLIS.between(currentDate, schedulingDate);
            if (remainingTime > 0) {
                Thread.sleep(remainingTime + 30 * 1000);
            }
            // get the updated notification
            sharedSteps.setSentNotification(b2bClient.getSentNotification(iun));
        }
    }

    @And("si attende che si ritenti l'invio dopo l'evento {string}")
    public void siAttendeCheSiRitentiInvio(String timelineEventCategory, @Transpose DataTest dataFromTest) throws InterruptedException {
        String iun = sharedSteps.getSentNotification().getIun();
        if (dataFromTest != null && dataFromTest.getTimelineElement() != null) {

            TimelineElementV25 timelineElement = sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataFromTest);

            OffsetDateTime firstSend = timelineElement.getTimestamp();
            Duration secondNotificationWorkflowWaitingTime = sharedSteps.getSecondNotificationWorkflowWaitingTime();
            OffsetDateTime nextSend = firstSend.plus(secondNotificationWorkflowWaitingTime);
            OffsetDateTime currentDate = now().atZoneSameInstant(ZoneId.of("UTC")).toOffsetDateTime();
            long remainingTime = ChronoUnit.MILLIS.between(currentDate, nextSend);
            if (remainingTime > 0) {
                Thread.sleep(remainingTime + 30 * 1000);
            }
            // get the updated notification
            sharedSteps.setSentNotification(b2bClient.getSentNotification(iun));
        }
    }

    @And("viene verificato che il destinatario {string} di tipo {string} sia nella tabella pn-paper-notification-failed")
    public void vieneVerificatoDestinatarioInPnPaperNotificationFailed(String taxId, String recipientTye) {
        // get internal id from data-vault
        String internalId = externalClient.getInternalIdFromTaxId(recipientTye, taxId);
        // get notifications not delivered from delivery-push
        List<ResponsePaperNotificationFailedDto> notificationFailedList = this.pnPrivateDeliveryPushExternalClient.getPaperNotificationFailed(internalId, true);
        String iun = sharedSteps.getSentNotification().getIun();
        ResponsePaperNotificationFailedDto notificationFailed = notificationFailedList.stream().filter(elem -> elem.getIun().equals(iun)).findFirst().orElse(null);
        Assertions.assertNotNull(notificationFailed);
    }

    @And("viene verificato che il destinatario {string} di tipo {string} non sia nella tabella pn-paper-notification-failed")
    public void vieneVerificatoDestinatarioNonInPnPaperNotificationFailed(String taxId, String recipientTye) {
        // get internal id from data-vault
        String internalId = externalClient.getInternalIdFromTaxId(recipientTye, taxId);
        // get notifications not delivered from delivery-push
        List<ResponsePaperNotificationFailedDto> notificationFailedList = this.pnPrivateDeliveryPushExternalClient.getPaperNotificationFailed(internalId, true);
        String iun = sharedSteps.getSentNotification().getIun();
        ResponsePaperNotificationFailedDto notificationFailed = notificationFailedList.stream().filter(elem -> elem.getIun().equals(iun)).findFirst().orElse(null);
        Assertions.assertNull(notificationFailed);
    }

    @Then("viene verificato che il numero di elementi di timeline {string} della notifica sia di {long}")
    public void checkNumElOfTimelineCategory(String timelineEventCategory, Long numEl) {
        Long actualNumElements = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().getValue().equals(timelineEventCategory)).count();

        try {
            Assertions.assertEquals(numEl, actualNumElements);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @And("vengono letti gli eventi fino all'elemento di timeline {string} della notifica per il destinatario {int}, con deliveryDetailCode {string}, legalFactId con category {string} e documentType {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCodeAndLegalFactIdCategoryAndDocumentType(String timelineEventCategory, Integer recIndex, String deliveryDetailCode, String legalFactIdCategory, String documentType) {
        //AL MOMENTO NON ESISTE UNO SCENARIO CHE INTEGRA QUESTO STEP
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .pnPollingPredicate(getPnPollingPredicateForTimelineV25(timelineEventCategory, recIndex, deliveryDetailCode, null, documentType, null, false, true, legalFactIdCategory, true, null))
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            TimelineElementV25 timelineElement = pnPollingResponseV25.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNotNull(Objects.requireNonNull(timelineElement.getDetails()).getPhysicalAddress());
            Assertions.assertTrue(timelineElement.getDetails().getPhysicalAddress().getAddress().matches("^[A-Z0-9_.\\-:@' \\[\\]]*$"));
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @And("vengono letti gli eventi fino all'elemento di timeline {string} della notifica per il destinatario {int}, con deliveryDetailCode {string} e con deliveryFailureCause {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCodeAndDeliveryFailureCause(String timelineEventCategory, Integer recIndex, String deliveryDetailCode, String deliveryFailureCause) {
        //AL MOMENTO NON ESISTE UNO SCENARIO CHE INTEGRA QUESTO STEP
        List<String> failureCauses = Arrays.asList(deliveryFailureCause.split(" "));
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .pnPollingPredicate(getPnPollingPredicateForTimelineV25(timelineEventCategory, recIndex, deliveryDetailCode, null, null, null, false, false, null, false, failureCauses))
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            log.info("TIMELINE_ELEMENT: " + pnPollingResponseV25.getTimelineElement());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline {string} della notifica per il destinatario {int} con deliveryDetailCode {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithRecIndexAndDeliveryDetailCode(String timelineEventCategory, Integer recIndex, String deliveryDetailCode) {
        //AL MOMENTO NON ESISTE UNO SCENARIO CHE INTEGRA QUESTO STEP
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .pnPollingPredicate(getPnPollingPredicateForTimelineV25(timelineEventCategory, recIndex, deliveryDetailCode, null, null, null, false, false, null, false, null))
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            log.info("TIMELINE_ELEMENT: " + pnPollingResponseV25.getTimelineElement());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @And("controlla che il timestamp di {string} sia dopo quello di invio e di attesa di lettura del messaggio di cortesia")
    public void verificaTimestamp(String timelineEventCategory, @Transpose DataTest dataFromTest) {

        TimelineElementV25 timelineElementCategory = getAndStoreTimelineByB2b(timelineEventCategory, dataFromTest);
        TimelineElementV25 timelineElementSendCourtesyMessage = getAndStoreTimelineByB2b("SEND_COURTESY_MESSAGE", dataFromTest);


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

    @And("download attestazione opponibile AAR")
    public void downloadLegalFactIdAAR() {
        getLegalFactIdAAR("PN_AAR");
    }

    public LegalFactDownloadMetadataResponse getLegalFactIdAAR(String aarType) {
        AtomicReference<LegalFactDownloadMetadataResponse> legalFactDownloadMetadataResponse = new AtomicReference<>();
        try {
            Thread.sleep(sharedSteps.getWait());
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }

        TimelineElementCategoryV23 timelineElementInternalCategory = TimelineElementCategoryV23.AAR_GENERATION;
        TimelineElementV25 timelineElement = null;

        for (TimelineElementV25 element : sharedSteps.getSentNotification().getTimeline()) {

            if (Objects.requireNonNull(element.getCategory()).equals(timelineElementInternalCategory)) {
                timelineElement = element;
                break;
            }
        }

        Assertions.assertNotNull(timelineElement);
            String keySearch = null;
                if (!Objects.requireNonNull(timelineElement.getDetails()).getGeneratedAarUrl().isEmpty()) {

                    if (timelineElement.getDetails().getGeneratedAarUrl().contains(aarType)) {
                            keySearch = timelineElement.getDetails().getGeneratedAarUrl().substring(timelineElement.getDetails().getGeneratedAarUrl().indexOf(aarType));
                        }

                        String finalKeySearch = keySearch;
                        try {
                            Assertions.assertDoesNotThrow(() -> legalFactDownloadMetadataResponse.set(
                                    this.b2bClient.getDownloadLegalFact(sharedSteps.getSentNotification().getIun(), finalKeySearch)));
                        } catch (AssertionFailedError assertionFailedError) {
                            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
                        }
                    }
                    return legalFactDownloadMetadataResponse.get();
                }


    @Then("download attestazione opponibile AAR e controllo del contenuto del file per verificare se il tipo è {string}")
    public void downloadAttestazioneOpponibileAAREControlloDelContenutoDelFilePerVerificareSeIlTipoE(String aarType) {
        LegalFactDownloadMetadataResponse legalFactDownloadMetadataResponse = getLegalFactIdAAR("PN_AAR");
        byte[] source = utils.downloadFile(legalFactDownloadMetadataResponse.getUrl());
        Assertions.assertNotNull(source);
        Assertions.assertTrue(checkTypeAAR(source, aarType));
    }

    private boolean checkTypeAAR(byte[] source, String aarType) {
        Pattern pattern = Pattern.compile("\\((CAF)\\s");
        try (final PDDocument document = Loader.loadPDF(source)) {
            final PDFTextStripper pdfStripper = new PDFTextStripper();
            pdfStripper.setSortByPosition(true);
            String extractedText = pdfStripper.getText(document);
            Matcher matcher = pattern.matcher(extractedText);
            if (aarType.equals("AAR")) {  //if AAR then check ' CAF ' pattern NOT exist
                return !matcher.find();
            } else if (aarType.equals("AAR RADD")) { //if AAR RADD then check ' CAF ' pattern exist
                return matcher.find();
            }
        } catch (Exception exception) {
            log.error("Error parsing PDF {}", exception);
        }
        return false;
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} e verifica indirizzo secondo tentativo {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithVerifyPhysicalAddress(String timelineEventCategory, String attempt) {
        PnPollingServiceTimelineSlowV25 timelineSlowV25 = (PnPollingServiceTimelineSlowV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_SLOW_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineSlowV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .pnPollingPredicate(getPnPollingPredicateForTimelineV25(timelineEventCategory, null, attempt))
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            TimelineElementV25 timelineElement = pnPollingResponseV25.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNotNull(Objects.requireNonNull(timelineElement.getDetails()).getPhysicalAddress());
            Assertions.assertTrue(timelineElement.getDetails().getPhysicalAddress().getAddress().matches("^[A-Z0-9_.\\-:@' \\[\\] ]*$"));
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} al tentativo {string}")
    public void readingEventUpToTheTimelineElementOfNotificationAtAttempt(String timelineEventCategory, String attempt) {
        PnPollingServiceTimelineSlowV25 timelineSlowV25 = (PnPollingServiceTimelineSlowV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_SLOW_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineSlowV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .pnPollingPredicate(getPnPollingPredicateForTimelineV25(timelineEventCategory, null, attempt))
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            log.info("TIMELINE_ELEMENT: " + pnPollingResponseV25.getTimelineElement());
            TimelineElementV25 timelineElement = pnPollingResponseV25.getTimelineElement();
            sharedSteps.setTimelineElement(timelineElement);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("viene verificato che nell'elemento di timeline della notifica {string} sia presente il campo Digital Address da National Registry")
    public void vieneVerificatoCheElementoTimelineSianoConfiguratoCampoDigitalAddressNationalRegistry(String timelineEventCategory) {
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            TimelineElementV25 timelineElement = pnPollingResponseV25.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNotNull(Objects.requireNonNull(timelineElement.getDetails()).getDigitalAddress());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    //Notifica Annullata

    //Annullamento Notifica
    @And("la notifica può essere annullata dal sistema tramite codice IUN")
    public void notificationCanBeCanceledWithIUN() {

        if (sharedSteps.getSentNotification() != null) {
            Assertions.assertDoesNotThrow(() -> {
                RequestStatus resp = Assertions.assertDoesNotThrow(() ->
                        this.b2bClient.notificationCancellation(sharedSteps.getSentNotification().getIun()));

                Assertions.assertNotNull(resp);
                Assertions.assertNotNull(resp.getDetails());
                Assertions.assertTrue(resp.getDetails().size() > 0);
                Assertions.assertTrue("NOTIFICATION_CANCELLATION_ACCEPTED".equalsIgnoreCase(resp.getDetails().get(0).getCode()));

            });
        } else if (sharedSteps.getSentNotificationV1() != null) {
            Assertions.assertDoesNotThrow(() -> {
                RequestStatus resp = Assertions.assertDoesNotThrow(() ->
                        this.b2bClient.notificationCancellation(sharedSteps.getSentNotificationV1().getIun()));

                Assertions.assertNotNull(resp);
                Assertions.assertNotNull(resp.getDetails());
                Assertions.assertTrue(resp.getDetails().size() > 0);
                Assertions.assertTrue("NOTIFICATION_CANCELLATION_ACCEPTED".equalsIgnoreCase(resp.getDetails().get(0).getCode()));

            });
        }


    }


    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con indirizzo normalizzato:")
    public void vengonoLettiGliEventiFinoAllElementoDiTimelineDellaNotificaConIndirizzoNormalizzato(String timelineEventCategory, DataTable table) {
        TimelineElementV25 timelineElement = readingEventUpToTheTimelineElementOfNotificationForCategory(timelineEventCategory);

        System.out.println(table);
        log.info("indirizzo: {}", timelineElement.getDetails().getOldAddress());
        //FARE CHECK RISULTATO
        log.info("indirizzo Normalizzato: {}", timelineElement.getDetails().getNormalizedAddress());
        try {
            Assertions.assertEquals(mapValueFromTable(table, "physicalAddress_address"), timelineElement.getDetails().getNormalizedAddress().getAddress());
            Assertions.assertEquals(mapValueFromTable(table, "at"), timelineElement.getDetails().getNormalizedAddress().getAt());
            Assertions.assertEquals(mapValueFromTable(table, "physicalAddress_addressDetails"), timelineElement.getDetails().getNormalizedAddress().getAddressDetails());
            Assertions.assertEquals(mapValueFromTable(table, "physicalAddress_zip"), timelineElement.getDetails().getNormalizedAddress().getZip());
            Assertions.assertEquals(mapValueFromTable(table, "physicalAddress_municipality"), timelineElement.getDetails().getNormalizedAddress().getMunicipality());
            Assertions.assertEquals(mapValueFromTable(table, "physicalAddress_municipalityDetails"), timelineElement.getDetails().getNormalizedAddress().getMunicipalityDetails());
            Assertions.assertEquals(mapValueFromTable(table, "physicalAddress_province"), timelineElement.getDetails().getNormalizedAddress().getProvince());
            Assertions.assertEquals(mapValueFromTable(table, "physicalAddress_State"), timelineElement.getDetails().getNormalizedAddress().getForeignState());

        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }


    private String mapValueFromTable(DataTable table, String key) {
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
    public void vengonoLettiGliEventiFinoAllElementoDiTimelineDellaNotificaConfailureCause(String timelineEventCategory, String failureCause) {
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            TimelineElementV25 timelineElement = pnPollingResponseV25.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertEquals(Objects.requireNonNull(timelineElement.getDetails()).getFailureCause(), failureCause);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con failureCause {string} per l'utente {int}")
    public void vengonoLettiGliEventiFinoAllElementoDiTimelineDellaNotificaConfailureCausePerUtente(String timelineEventCategory, String failureCause, Integer destinatario) {
        PnPollingServiceTimelineSlowV25 timelineSlowV25 = (PnPollingServiceTimelineSlowV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_SLOW_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineSlowV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .pnPollingPredicate(getPnPollingPredicateForTimelineV25(timelineEventCategory, destinatario))
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            TimelineElementV25 timelineElement = pnPollingResponseV25.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertEquals(Objects.requireNonNull(timelineElement.getDetails()).getFailureCause(), failureCause);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
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

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} e verifica data schedulingDate per il destinatario {int} rispetto ell'evento in timeline {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithVerifySchedulingDate(String timelineEventCategory, int destinatario, String evento) {
        long delayMillis = 0;
        OffsetDateTime digitalDeliveryCreationRequestDate = null;

        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .pnPollingPredicate(getPnPollingPredicateForTimelineV25(timelineEventCategory, destinatario))
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification().getTimeline());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            TimelineElementV25 timelineElement = pnPollingResponseV25.getTimelineElement();
            Assertions.assertNotNull(Objects.requireNonNull(timelineElement.getDetails()).getSchedulingDate());
            log.info("TIMELINE ELEMENT: {} , DETAILS {} , SCHEDULING DATE {}",
                    timelineElement, Objects.requireNonNull(timelineElement).getDetails(), Objects.requireNonNull(timelineElement.getDetails()).getSchedulingDate());
            //RECUPERO Data DeliveryCreationRequest
            for (TimelineElementV25 element : sharedSteps.getSentNotification().getTimeline()) {
                if (Objects.requireNonNull(element.getCategory()).getValue().equals("DIGITAL_DELIVERY_CREATION_REQUEST") && Objects.requireNonNull(element.getDetails()).getRecIndex().equals(destinatario) && evento.equalsIgnoreCase("DIGITAL_DELIVERY_CREATION_REQUEST")) {
                    digitalDeliveryCreationRequestDate = element.getTimestamp();
                    delayMillis = sharedSteps.getSchedulingDaysFailureDigitalRefinement().toMillis();
                    break;
                } else if (element.getCategory().getValue().equals("SEND_DIGITAL_FEEDBACK") && Objects.requireNonNull(element.getDetails()).getRecIndex().equals(destinatario) && evento.equalsIgnoreCase("SEND_DIGITAL_FEEDBACK")) {
                    if ("OK".equalsIgnoreCase(element.getDetails().getResponseStatus().getValue())) {
                        digitalDeliveryCreationRequestDate = element.getDetails().getNotificationDate();
                        delayMillis = sharedSteps.getSchedulingDaysSuccessDigitalRefinement().toMillis();
                        break;
                    }
                }
            }
            //Duration ss = sharedSteps.getSchedulingDaysSuccessDigitalRefinement();
            //DateTimeFormatter fmt1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            Long schedulingDateMillis = timelineElement.getDetails().getSchedulingDate().toInstant().toEpochMilli();
            Long digitalDeliveryCreationMillis = Objects.requireNonNull(digitalDeliveryCreationRequestDate).toInstant().toEpochMilli();
            long diff = schedulingDateMillis - digitalDeliveryCreationMillis;
            long delta = Long.parseLong(sharedSteps.getSchedulingDelta());
            log.info("PRE-ASSERTION: iun={} schedulingDateMillis={}, digitalDeliveryCreationMillis={}, diff={}, delayMillis={}, delta={}",
                    sharedSteps.getSentNotification().getIun(), schedulingDateMillis, digitalDeliveryCreationMillis, diff, delayMillis, delta);
            Assertions.assertTrue(diff <= delayMillis + delta && diff >= delayMillis - delta);
            //Assertions.assertTrue(timelineElement.getDetails().getSchedulingDate().format(fmt1).equals(digitalDeliveryCreationRequestDate.plusMinutes(delay).format(fmt1)));
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("viene verificato che nell'elemento di timeline della notifica {string} sia presente il campo notRefinedRecipientIndex")
    public void vieneVerificatoCheElementoTimelineSianoConfiguratoCampoNotRefinedRecipientIndex(String timelineEventCategory) {
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        PnPollingResponseV25 pnPollingResponseV25 = timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .build());
        log.info("NOTIFICATION_TIMELINE: " + pnPollingResponseV25.getNotification());
        try {
            Assertions.assertTrue(pnPollingResponseV25.getResult());
            Assertions.assertNotNull(pnPollingResponseV25.getTimelineElement());
            sharedSteps.setSentNotification(pnPollingResponseV25.getNotification());
            TimelineElementV25 timelineElement = pnPollingResponseV25.getTimelineElement();
            log.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNotNull(Objects.requireNonNull(timelineElement.getDetails()).getNotRefinedRecipientIndexes());
            Assertions.assertFalse(timelineElement.getDetails().getNotRefinedRecipientIndexes().isEmpty());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("viene verificato il costo {string} di una notifica {string} del utente {string}")
    public void notificationPriceVerificationIvaIncluded(String tipoCosto, String tipoNotifica, String user) {

        FullSentNotificationV25 notificaV23 = sharedSteps.getSentNotification();
        Assertions.assertNotNull(notificaV23);

        Integer pricePartial;
        Integer priceTotal;

        if (sharedSteps.getSentNotification().getNotificationFeePolicy().equals(NotificationFeePolicy.DELIVERY_MODE)) {
            pricePartial = calcoloPrezzo(tipoNotifica, tipoCosto, user, notificaV23);
            priceTotal = calcoloPrezzo(tipoNotifica, tipoCosto, user, notificaV23);
        } else if (sharedSteps.getSentNotification().getNotificationFeePolicy().equals(NotificationFeePolicy.FLAT_RATE)) {
            pricePartial = 0;
            priceTotal = 0;
        } else {
            throw new IllegalArgumentException();
        }

        switch (tipoCosto.toLowerCase()) {
            case "parziale" -> {
                priceVerificationV1(String.valueOf(pricePartial), null, Integer.parseInt(user));
                priceVerificationV23(pricePartial, null, Integer.parseInt(user), tipoCosto);
            }
            case "totale" -> priceVerificationV23(priceTotal, null, Integer.parseInt(user), tipoCosto);
            default -> throw new IllegalArgumentException();
        }
    }

    @Then("viene verificato che il campo {string} sia valorizzato a {int}")
    public void notificationPriceVerificationValueResponse(String toValidate, Integer valueToValidate) {
        try {
            FullSentNotificationV25 notifica = sharedSteps.getB2bUtils().getNotificationByIun(sharedSteps.getIunVersionamento());
           Assertions.assertNotNull(notifica);

            switch (toValidate.toLowerCase()) {
                case "vat" -> Assertions.assertEquals(valueToValidate, notifica.getVat());
                case "pafee" -> Assertions.assertEquals(valueToValidate, notifica.getPaFee());
            }

        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    public Integer calcoloPrezzo(String tipoNotifica, String tipoCosto, String user, FullSentNotificationV25 notificaV23) {

        List<TimelineElementV25> listaNotifica = notificaV23.getTimeline().stream().filter(value -> value.getDetails() != null && value.getDetails().getAnalogCost() != null).toList();

        int pricePartial;
        int priceTotal;

        Integer paFee = notificaV23.getPaFee();
        Integer vat = notificaV23.getVat();


        switch (tipoNotifica.toLowerCase()) {
            case "890", "ar", "rir" -> {
                TimelineElementV25 analogFirstAttempt = listaNotifica.stream().filter(value -> value.getElementId().contains("ATTEMPT_0") && value.getElementId().contains("RECINDEX_" + user)).findAny().orElse(null);
                TimelineElementV25 analogSecondAttempt = listaNotifica.stream().filter(value -> value.getElementId().contains("ATTEMPT_1") && value.getElementId().contains("RECINDEX_" + user)).findAny().orElse(null);
                Integer analogCostFirstAttempt = analogFirstAttempt.getDetails().getAnalogCost();
                Integer analogCostSecondAttempt = analogSecondAttempt != null && analogSecondAttempt.getDetails() != null ? analogSecondAttempt.getDetails().getAnalogCost() : 0;
                pricePartial = costoBaseNotifica + analogCostFirstAttempt + analogCostSecondAttempt;
                priceTotal = Math.round(paFee + costoBaseNotifica + (analogCostFirstAttempt + analogCostSecondAttempt) + (float) ((analogCostFirstAttempt + analogCostSecondAttempt) * vat) / 100);
            }
            case "rs", "ris" -> {
                TimelineElementV25 analogNotification = listaNotifica.stream().filter(value -> value.getElementId().contains("RECINDEX_" + user)).findAny().orElse(null);
                Integer analogCost = analogNotification.getDetails().getAnalogCost();
                pricePartial = costoBaseNotifica + analogCost;
                priceTotal = paFee + costoBaseNotifica + analogCost + Math.round(((float) (analogCost) * vat / 100));
            }
            default -> throw new IllegalArgumentException();
        }

        return switch (tipoCosto.toLowerCase()) {
            case "parziale" -> pricePartial;
            case "totale" -> priceTotal;
            default -> null;
        };
    }

    @Then("viene verificato che tutti i campi per il calcolo del iva per il destinatario {int} siano valorizzati")
    public void notificationPriceVerificationResponse(Integer destinatario) {
        List<NotificationPaymentItem> listNotificationPaymentItem = sharedSteps.getSentNotification().getRecipients().get(destinatario).getPayments();

        for (NotificationPaymentItem pagamento : listNotificationPaymentItem) {
            NotificationPriceResponseV23 notificationPriceV23 = this.b2bClient.getNotificationPriceV23(pagamento.getPagoPa().getCreditorTaxId(), pagamento.getPagoPa().getNoticeCode());

            try {
                Assertions.assertNotNull(notificationPriceV23.getTotalPrice());
                Assertions.assertNotNull(notificationPriceV23.getPartialPrice());
                Assertions.assertNotNull(notificationPriceV23.getIun());
                Assertions.assertNotNull(notificationPriceV23.getAnalogCost());
                Assertions.assertNotNull(notificationPriceV23.getRefinementDate());
                Assertions.assertNotNull(notificationPriceV23.getNotificationViewDate());
                Assertions.assertNotNull(notificationPriceV23.getSendFee());
                Assertions.assertNotNull(notificationPriceV23.getPaFee());
                Assertions.assertNotNull(notificationPriceV23.getVat());
                log.info("notification price v23: {}", notificationPriceV23);
            } catch (AssertionFailedError assertionFailedError) {
                sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
            }
        }
    }

    @Then("viene verificato che per il calcolo del iva il campo vat sia di {int} e il campo paFee sia di {int} per il destinatario {int}")
    public void notificationPriceVerificationResponse(Integer vat, Integer paFee, Integer destinatario) {

        List<NotificationPaymentItem> listNotificationPaymentItem = sharedSteps.getSentNotification().getRecipients().get(destinatario).getPayments();

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
                sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
            }
        }
    }


    @And("viene verificato data corretta del destinatario {int}")
    public void verificationDateNotificationPrice(Integer destinatario) {

        List<NotificationPaymentItem> listNotificationPaymentItem = sharedSteps.getSentNotification().getRecipients().get(destinatario).getPayments();

        if (listNotificationPaymentItem != null) {
            for (NotificationPaymentItem notificationPaymentItem : listNotificationPaymentItem) {
                NotificationPriceResponseV23 notificationPrice = this.b2bClient.getNotificationPriceV23(notificationPaymentItem.getPagoPa().getCreditorTaxId(), notificationPaymentItem.getPagoPa().getNoticeCode());
                try {
                    Assertions.assertEquals(notificationPrice.getIun(), sharedSteps.getSentNotification().getIun());
                    Assertions.assertNotNull(notificationPrice.getNotificationViewDate());

                } catch (AssertionFailedError assertionFailedError) {
                    sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
                }
            }

        }

    }

    @Then("l'ente {string} richiede l'attestazione opponibile {string}")
    public void paRequiresLegalFact(String ente, String legalFactCategory) {
        sharedSteps.selectPA(ente);
        try {
            takeLegalFact(legalFactCategory, null);
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @Then("l'ente {string} richiede l'attestazione opponibile {string} con deliveryDetailCode {string}")
    public void paRequiresLegalFactConDeliveryDetailCode(String ente, String legalFactCategory, String deliveryDetailCode) {
        sharedSteps.selectPA(ente);
        try {
            takeLegalFact(legalFactCategory, deliveryDetailCode);
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    public String getKeyLegalFact(String key) {
        if (key.contains("PN_LEGAL_FACTS")) {
            return key.substring(key.indexOf("PN_LEGAL_FACTS"));
        } else if (key.contains("PN_NOTIFICATION_ATTACHMENTS")) {
            return key.substring(key.indexOf("PN_NOTIFICATION_ATTACHMENTS"));
        } else if (key.contains("PN_EXTERNAL_LEGAL_FACTS")) {
            return key.substring(key.indexOf("PN_EXTERNAL_LEGAL_FACTS"));
        } else if (key.contains("PN_F24")) {
            return key.substring(key.indexOf("PN_F24"));
        }
        return null;
    }

    private LegalFactDownloadMetadataResponse takeLegalFact(String legalFactCategory, String deliveryDetailCode) {
        try {
            Thread.sleep(sharedSteps.getWait());
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }

        PnTimelineLegalFactV23 categoriesV23 = pnTimelineAndLegalFactV23.getCategory(legalFactCategory);

        TimelineElementV25 timelineElement = null;

        for (TimelineElementV25 element : sharedSteps.getSentNotification().getTimeline()) {

            if (element.getCategory().equals(categoriesV23.getTimelineElementInternalCategory())) {
                if (deliveryDetailCode == null) {
                    timelineElement = element;
                    break;
                } else if (element.getDetails().getDeliveryDetailCode().equals(deliveryDetailCode)) {
                    timelineElement = element;
                    break;
                }
            }
        }

        System.out.println("ELEMENT: " + timelineElement);
        Assertions.assertNotNull(timelineElement);

        Assertions.assertNotNull(timelineElement.getLegalFactsIds());
        Assertions.assertFalse(CollectionUtils.isEmpty(timelineElement.getLegalFactsIds()));
        Assertions.assertEquals(categoriesV23.getLegalFactCategory().getValue(), timelineElement.getLegalFactsIds().get(0).getCategory());
        LegalFactCategory categorySearch = LegalFactCategory.fromValue(timelineElement.getLegalFactsIds().get(0).getCategory());
        String key = timelineElement.getLegalFactsIds().get(0).getKey();
        String keySearch = getKeyLegalFact(key);


        LegalFactDownloadMetadataResponse legalFactDownloadMetadataResponse = this.b2bClient.getLegalFact(sharedSteps.getSentNotification().getIun(), categorySearch, keySearch);

        Assertions.assertNotNull(legalFactDownloadMetadataResponse);

        return legalFactDownloadMetadataResponse;
    }

    private PnPollingPredicate getPnPollingPredicateForTimelineV25(String timelineEventCategory, Integer destinatario) {
        return getPnPollingPredicateForTimelineV25(timelineEventCategory, destinatario, null, null, null, null, false, false, null, false, null);
    }

    private PnPollingPredicate getPnPollingPredicateForTimelineV25(String timelineEventCategory, String deliveryDetailCode) {
        return getPnPollingPredicateForTimelineV25(timelineEventCategory, null, deliveryDetailCode, null, null, null, false, false, null, false, null);
    }

    private PnPollingPredicate getPnPollingPredicateForTimelineV25(String timelineEventCategory, String deliveryDetailCode, String attempt) {
        return getPnPollingPredicateForTimelineV25(timelineEventCategory, null, deliveryDetailCode, attempt, null, null, false, false, null, false, null);
    }

    private PnPollingPredicate getPnPollingPredicateForTimelineV25(String timelineEventCategory, Integer destinatario, String deliveryDetailCode, String attempt, String tipoDoc, String responseStatus, boolean isF24, boolean isLegalFactEmpty, String legalFactIdCategory, boolean isAttachmentEmpty, List<String> failureCauses) {
        PnPollingPredicate pnPollingPredicate = new PnPollingPredicate();
        pnPollingPredicate.setTimelineElementPredicateV25(
                timelineElementV25 ->
                        timelineElementV25.getCategory() != null
                                && (timelineEventCategory == null || Objects.requireNonNull(timelineElementV25.getCategory().getValue()).equals(timelineEventCategory))
                                && (destinatario == null || Objects.requireNonNull(Objects.requireNonNull(timelineElementV25.getDetails()).getRecIndex()).equals(destinatario))
                                && (deliveryDetailCode == null || Objects.equals(Objects.requireNonNull(timelineElementV25.getDetails()).getDeliveryDetailCode(), deliveryDetailCode))
                                && (attempt == null || Objects.requireNonNull(timelineElementV25.getElementId()).contains(attempt))
                                && (tipoDoc == null || Objects.equals(Objects.requireNonNull(Objects.requireNonNull(timelineElementV25.getDetails()).getAttachments()).get(0).getDocumentType(), tipoDoc))
                                && (responseStatus == null || Objects.requireNonNull(Objects.requireNonNull(timelineElementV25.getDetails()).getResponseStatus().getValue()).equals(responseStatus))
                                && (!isF24 || Objects.requireNonNull(timelineElementV25.getDetails()).getIdF24() != null)
                                && (!isLegalFactEmpty || Objects.nonNull(timelineElementV25.getLegalFactsIds()) && !timelineElementV25.getLegalFactsIds().isEmpty())
                                && (legalFactIdCategory == null || Objects.requireNonNull(Objects.requireNonNull(timelineElementV25.getLegalFactsIds()).get(0)).getCategory().equals(legalFactIdCategory))
                                && (!isAttachmentEmpty || Objects.nonNull(Objects.requireNonNull(timelineElementV25.getDetails()).getAttachments()) && !timelineElementV25.getDetails().getAttachments().isEmpty())
                                && (legalFactIdCategory == null || failureCauses.contains(Objects.requireNonNull(Objects.requireNonNull(timelineElementV25.getDetails()).getDeliveryFailureCause())))
        );
        return pnPollingPredicate;
    }


    @And ("viene verificato il costo di {int} e il peso di {int} nei details del'elemento di timeline letto")
    public void verificaCostoePesoInvioCartaceo(Integer costo, Integer peso){
        TimelineElementV25 timeline = sharedSteps.getTimelineElement();
        try {
            Assertions.assertEquals(costo, timeline.getDetails().getAnalogCost());
            Assertions.assertEquals(peso, timeline.getDetails().getEnvelopeWeight());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }


    @And ("viene verificato che il peso della busta cartacea sia di {int}")
    public void verificaCostoePesoInvioCartaceo(Integer peso){
        TimelineElementV25 timeline = sharedSteps.getTimelineElement();
        try {
            Assertions.assertEquals(peso, timeline.getDetails().getEnvelopeWeight());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    private Map<String, String> populateConsolidatoreMap(Instant date) {
        String iun = sharedSteps.getSentNotification().getIun();
        Map<String, String> mapInfo = new HashMap<>();
        mapInfo.put("requestId", requestIdConsolidator);
        mapInfo.put("attachments", null);
        mapInfo.put("clientRequestTimeStamp", utils.getOffsetDateTimeFromDate(date));
        mapInfo.put("deliveryFailureCause", null);
        mapInfo.put("discoveredAddress", null);
        mapInfo.put("iun", iun);
        mapInfo.put("productType", "890");
        mapInfo.put("registeredLetterCode", null);
        mapInfo.put("statusCode", "CON020");
        mapInfo.put("statusDateTime", utils.getOffsetDateTimeFromDate(date));
        mapInfo.put("statusDescription", "Affido conservato");
        return mapInfo;
    }

    @Then("viene invocato il consolidatore con clientRequestTimeStamp e statusDateTime nel {string}")
    public void vieneInvocatoIlConsolidatore(String statusDate) {
        Instant now;
        if (statusDate.equalsIgnoreCase("Futuro")) {
            now = Instant.now()
                    .plusSeconds(utils.convertToSeconds(pnEcConsAllowedFutureOffsetDuration)).plusSeconds(60);
        } else {
            now = Instant.now();
        }

        Map<String, String> mapInfo = populateConsolidatoreMap(now);
        try {
            externalClient.pushConsolidatoreNotification(mapInfo);
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    public PnPollingResponseV25 getPollingResponse(String timelineEventCategory, String deliveryDetailCode) {
        PnPollingServiceTimelineRapidV25 timelineRapidV25 = (PnPollingServiceTimelineRapidV25) pnPollingFactory.getPollingService(PnPollingStrategy.TIMELINE_RAPID_V25);

        return timelineRapidV25.waitForEvent(sharedSteps.getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value(timelineEventCategory)
                        .pnPollingPredicate(getPnPollingPredicateForTimelineV25(timelineEventCategory, deliveryDetailCode))
                        .build());
    }

    @And("viene verificato che il timestamp dell'evento {string} sia immediatamente successivo a quello dell'evento {string} con una differenza massima di {int} secondi")
    public void confrontoTimestampEventi(String nextTimelineEvent, String previousTimelineEvent, Integer delta) {
        FullSentNotificationV25 fullSentNotificationV23 = sharedSteps.getSentNotification();
        List<TimelineElementV25> timelineElements = fullSentNotificationV23.getTimeline();

        Optional<TimelineElementV25> timelineElementV23OptionalNext = timelineElements.stream()
                .filter(element -> element.getCategory() != null && element.getCategory().toString().equals(nextTimelineEvent))
                .findFirst();
        Optional<TimelineElementV25> timelineElementV23OptionalPrevious = timelineElements.stream()
                .filter(element -> element.getCategory() != null && element.getCategory().toString().equals(previousTimelineEvent))
                .findFirst();
        Assertions.assertTrue(timelineElementV23OptionalNext.isPresent() && timelineElementV23OptionalPrevious.isPresent());

        Long timestampNext = timelineElementV23OptionalNext.get().getTimestamp().toInstant().toEpochMilli();
        Long timeStampPrevious = timelineElementV23OptionalPrevious.get().getTimestamp().toInstant().toEpochMilli();
        Long diffMillis = timestampNext - timeStampPrevious;
        delta = delta * 1000;

        log.info("PRE-ASSERTION: iun={} nextTimelineEvent={}, previousTimelineEvent={}, diffMillis={}, delta={}",
                sharedSteps.getSentNotification().getIun(), timestampNext, timeStampPrevious, diffMillis, delta);
        Assertions.assertTrue(diffMillis <= delta);
    }

}