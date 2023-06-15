package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.impl.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.testclient.*;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.NotificationHistoryResponse;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.utils.*;
import it.pagopa.pn.cucumber.utils.EventId;
import it.pagopa.pn.cucumber.utils.TimelineElementWait;
import it.pagopa.pn.cucumber.utils.TimelineEventId;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import java.lang.invoke.MethodHandles;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.time.OffsetDateTime.now;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.with;

public class AvanzamentoNotificheB2bSteps {


    private final IPnPaB2bClient b2bClient;
    private final SharedSteps sharedSteps;
    private final IPnAppIOB2bClient appIOB2bClient;
    private final IPnWebRecipientClient webRecipientClient;
    private final IPnWebUserAttributesClient webUserAttributesClient;
    private final IPnIoUserAttributerExternaClientImpl ioUserAttributerExternaClient;
    private final IPnPrivateDeliveryPushExternalClientImpl pnPrivateDeliveryPushExternalClient;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    public AvanzamentoNotificheB2bSteps(SharedSteps sharedSteps, IPnAppIOB2bClient appIOB2bClient,
                                        IPnWebUserAttributesClient webUserAttributesClient, IPnIoUserAttributerExternaClientImpl ioUserAttributerExternaClient, IPnPrivateDeliveryPushExternalClientImpl pnPrivateDeliveryPushExternalClient) {
        this.sharedSteps = sharedSteps;
        this.appIOB2bClient = appIOB2bClient;
        this.b2bClient = sharedSteps.getB2bClient();
        this.webRecipientClient = sharedSteps.getWebRecipientClient();
        this.webUserAttributesClient = webUserAttributesClient;
        this.ioUserAttributerExternaClient = ioUserAttributerExternaClient;
        this.pnPrivateDeliveryPushExternalClient = pnPrivateDeliveryPushExternalClient;
    }


    @Then("vengono letti gli eventi fino allo stato della notifica {string} dalla PA {string}")
    public void readingEventsNotificationPA(String status, String pa) {
        sharedSteps.selectPA(pa);
        readingEventUpToTheStatusOfNotification(status);
        sharedSteps.selectPA(SharedSteps.DEFAULT_PA);
    }

    @Then("vengono letti gli eventi fino allo stato della notifica {string}")
    public void readingEventUpToTheStatusOfNotification(String status) {
        Integer numCheck = 10;
        Integer waiting = sharedSteps.getWorkFlowWait();

        NotificationStatus notificationInternalStatus;
        switch (status) {
            case "ACCEPTED":
                numCheck = 2;
                notificationInternalStatus = NotificationStatus.ACCEPTED;
                break;
            case "DELIVERING":
                numCheck = 2;
                waiting = waiting * 4;
                notificationInternalStatus = NotificationStatus.DELIVERING;
                break;
            case "DELIVERED":
                numCheck = 4;
                waiting = waiting * 4;
                notificationInternalStatus = NotificationStatus.DELIVERED;
                break;
            case "CANCELLED":
                notificationInternalStatus = NotificationStatus.CANCELLED;
                break;
            case "EFFECTIVE_DATE":
                notificationInternalStatus = NotificationStatus.EFFECTIVE_DATE;
                break;
            case "COMPLETELY_UNREACHABLE":
                notificationInternalStatus = NotificationStatus.UNREACHABLE;
                break;
            default:
                throw new IllegalArgumentException();
        }

        NotificationStatusHistoryElement notificationStatusHistoryElement = null;

        for (int i = 0; i < numCheck; i++) {
            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));

            logger.info("NOTIFICATION_STATUS_HISTORY: " + sharedSteps.getSentNotification().getNotificationStatusHistory());

            notificationStatusHistoryElement = sharedSteps.getSentNotification().getNotificationStatusHistory().stream().filter(elem -> elem.getStatus().equals(notificationInternalStatus)).findAny().orElse(null);

            if (notificationStatusHistoryElement != null) {
                break;
            }
            try {
                Thread.sleep(waiting);
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
        }
        try {
            Assertions.assertNotNull(notificationStatusHistoryElement);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }


    }

    private TimelineElementWait getTimelineElementCategory(String timelineEventCategory) {
        Integer waiting = sharedSteps.getWorkFlowWait();
        TimelineElementWait timelineElementWait;
        switch (timelineEventCategory) {
            case "REQUEST_ACCEPTED":
                timelineElementWait = new TimelineElementWait(TimelineElementCategory.REQUEST_ACCEPTED, 2, waiting);
                break;
            case "AAR_GENERATION":
                timelineElementWait = new TimelineElementWait(TimelineElementCategory.AAR_GENERATION, 2, waiting * 2);
                break;
            case "GET_ADDRESS":
                timelineElementWait = new TimelineElementWait(TimelineElementCategory.GET_ADDRESS, 2, waiting * 2);
                break;
            case "SEND_DIGITAL_DOMICILE":
                timelineElementWait = new TimelineElementWait(TimelineElementCategory.SEND_DIGITAL_DOMICILE, 2, waiting * 2);
                break;
            case "NOTIFICATION_VIEWED":
                timelineElementWait = new TimelineElementWait(TimelineElementCategory.NOTIFICATION_VIEWED, 2, waiting * 2);
                break;
            case "SEND_COURTESY_MESSAGE":
                timelineElementWait = new TimelineElementWait(TimelineElementCategory.SEND_COURTESY_MESSAGE, 15, sharedSteps.getWorkFlowWait());
                break;
            case "DIGITAL_SUCCESS_WORKFLOW":
                timelineElementWait = new TimelineElementWait(TimelineElementCategory.DIGITAL_SUCCESS_WORKFLOW, 3, waiting * 3);
                break;
            case "DIGITAL_FAILURE_WORKFLOW":
                timelineElementWait = new TimelineElementWait(TimelineElementCategory.DIGITAL_FAILURE_WORKFLOW, 15, waiting * 3);
                break;
            case "NOT_HANDLED":
                timelineElementWait = new TimelineElementWait(TimelineElementCategory.NOT_HANDLED, 15, sharedSteps.getWorkFlowWait());
                break;
            case "SEND_DIGITAL_FEEDBACK":
                timelineElementWait = new TimelineElementWait(TimelineElementCategory.SEND_DIGITAL_FEEDBACK, 2, waiting * 3);
                break;
            case "SEND_DIGITAL_PROGRESS":
                timelineElementWait = new TimelineElementWait(TimelineElementCategory.SEND_DIGITAL_PROGRESS, 2, waiting * 3);
                break;
            case "PUBLIC_REGISTRY_CALL":
                timelineElementWait = new TimelineElementWait(TimelineElementCategory.PUBLIC_REGISTRY_CALL, 2, waiting * 4);
                break;
            case "PUBLIC_REGISTRY_RESPONSE":
                timelineElementWait = new TimelineElementWait(TimelineElementCategory.PUBLIC_REGISTRY_RESPONSE, 2, waiting * 4);
                break;
            case "SCHEDULE_ANALOG_WORKFLOW":
                timelineElementWait = new TimelineElementWait(TimelineElementCategory.SCHEDULE_ANALOG_WORKFLOW, 2, waiting * 3);
                break;
            case "ANALOG_SUCCESS_WORKFLOW":
                timelineElementWait = new TimelineElementWait(TimelineElementCategory.ANALOG_SUCCESS_WORKFLOW, 5, waiting * 4);
                break;
            case "ANALOG_FAILURE_WORKFLOW":
                timelineElementWait = new TimelineElementWait(TimelineElementCategory.ANALOG_FAILURE_WORKFLOW, 15, sharedSteps.getWorkFlowWait());
                break;
            case "SEND_ANALOG_DOMICILE":
                timelineElementWait = new TimelineElementWait(TimelineElementCategory.SEND_ANALOG_DOMICILE, 4, waiting * 3);
                break;
            case "SEND_ANALOG_PROGRESS":
                timelineElementWait = new TimelineElementWait(TimelineElementCategory.SEND_ANALOG_PROGRESS, 4, waiting * 3);
                break;
            case "SEND_ANALOG_FEEDBACK":
                timelineElementWait = new TimelineElementWait(TimelineElementCategory.SEND_ANALOG_FEEDBACK, 4, waiting * 3);
                break;
            case "PREPARE_SIMPLE_REGISTERED_LETTER":
                timelineElementWait = new TimelineElementWait(TimelineElementCategory.PREPARE_SIMPLE_REGISTERED_LETTER, 16, waiting * 3);
                break;
            case "SEND_SIMPLE_REGISTERED_LETTER":
                timelineElementWait = new TimelineElementWait(TimelineElementCategory.SEND_SIMPLE_REGISTERED_LETTER, 16, waiting * 3);
                break;
            case "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS":
                timelineElementWait = new TimelineElementWait(TimelineElementCategory.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS, 16, waiting * 3);
                break;
            case "PAYMENT":
                timelineElementWait = new TimelineElementWait(TimelineElementCategory.PAYMENT, 15, sharedSteps.getWorkFlowWait());
                break;
            case "PREPARE_ANALOG_DOMICILE":
                timelineElementWait = new TimelineElementWait(TimelineElementCategory.PREPARE_ANALOG_DOMICILE, 4, waiting * 5);
                break;
            case "COMPLETELY_UNREACHABLE":
                timelineElementWait = new TimelineElementWait(TimelineElementCategory.COMPLETELY_UNREACHABLE, 15, sharedSteps.getWorkFlowWait());
                break;
            case "PREPARE_DIGITAL_DOMICILE":
                timelineElementWait = new TimelineElementWait(TimelineElementCategory.PREPARE_DIGITAL_DOMICILE, 2, waiting * 3);
                break;
            case "SCHEDULE_DIGITAL_WORKFLOW":
                timelineElementWait = new TimelineElementWait(TimelineElementCategory.SCHEDULE_DIGITAL_WORKFLOW, 2,waiting * 3);
                break;
            case "SCHEDULE_REFINEMENT":
                timelineElementWait = new TimelineElementWait(TimelineElementCategory.SCHEDULE_REFINEMENT, 3, waiting);
                break;
            case "REFINEMENT":
                timelineElementWait = new TimelineElementWait(TimelineElementCategory.REFINEMENT, 2, waiting);
                break;
            case "REQUEST_REFUSED":
                timelineElementWait = new TimelineElementWait(TimelineElementCategory.REQUEST_REFUSED, 2, waiting);
                break;
            default:
                throw new IllegalArgumentException();
        }
        return timelineElementWait;
    }

    private void checkTimelineElementEquality(String timelineEventCategory, TimelineElement elementFromNotification, DataTest dataFromTest) {
        TimelineElement elementFromTest = dataFromTest.getTimelineElement();
        TimelineElementDetails detailsFromNotification = elementFromNotification.getDetails();
        TimelineElementDetails detailsFromTest = elementFromTest.getDetails();
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
                    if(Objects.nonNull(detailsFromTest.getDeliveryFailureCause()))
                        Assertions.assertEquals(detailsFromNotification.getDeliveryFailureCause(), detailsFromTest.getDeliveryFailureCause());
                    if(Objects.nonNull(detailsFromTest.getDeliveryDetailCode()))
                        Assertions.assertEquals(detailsFromNotification.getDeliveryDetailCode(), detailsFromTest.getDeliveryDetailCode());
                }
                break;
            case "SEND_ANALOG_PROGRESS":
                if (detailsFromTest != null) {
                    if(Objects.nonNull(elementFromTest.getLegalFactsIds()))
                        Assertions.assertEquals(elementFromNotification.getLegalFactsIds().size(), elementFromTest.getLegalFactsIds().size());
                    for (int i = 0; i < elementFromNotification.getLegalFactsIds().size(); i++) {
                        Assertions.assertEquals(elementFromNotification.getLegalFactsIds().get(i).getCategory(), elementFromTest.getLegalFactsIds().get(i).getCategory());
                        Assertions.assertNotNull(elementFromNotification.getLegalFactsIds().get(i).getKey());
                    }
                    Assertions.assertEquals(detailsFromNotification.getDeliveryDetailCode(), detailsFromTest.getDeliveryDetailCode());
                    if (Objects.nonNull(detailsFromTest.getAttachments())) {
                        Assertions.assertNotNull(detailsFromNotification.getAttachments());
                        Assertions.assertEquals(detailsFromNotification.getAttachments().size(), detailsFromTest.getAttachments().size());

                        for (int i = 0; i < detailsFromNotification.getAttachments().size(); i++) {
                            List<String> documentTypes = Arrays.asList(detailsFromTest.getAttachments().get(i).getDocumentType().split(" "));
                            Assertions.assertEquals(Boolean.TRUE, documentTypes.contains(detailsFromNotification.getAttachments().get(i).getDocumentType()));
                        }
                    }

                    if(Objects.nonNull(detailsFromTest.getDeliveryFailureCause())) {
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
                    Assertions.assertEquals(elementFromNotification.getLegalFactsIds().get(i).getCategory(), elementFromTest.getLegalFactsIds().get(i).getCategory());
                    Assertions.assertNotNull(elementFromNotification.getLegalFactsIds().get(i).getKey());
                }
                if (delegateInfoFromTest != null) {
                    Assertions.assertEquals(delegateInfoFromNotification.getTaxId(), delegateInfoFromTest.getTaxId());
                    Assertions.assertEquals(delegateInfoFromNotification.getDelegateType(), delegateInfoFromTest.getDelegateType());
                    Assertions.assertEquals(delegateInfoFromNotification.getDenomination(), delegateInfoFromTest.getDenomination());
                }
        }
    }

    private TimelineElement getAndStoreTimeline(String timelineEventCategory, DataTest dataFromTest) {
        List<TimelineElement> timelineElementList;
        String iun;
        TimelineElement timelineElement;

        if (timelineEventCategory.equals(TimelineElementCategory.REQUEST_REFUSED.getValue())) {
            String requestId = sharedSteps.getNewNotificationResponse().getNotificationRequestId();
            byte[] decodedBytes = Base64.getDecoder().decode(requestId);
            iun = new String(decodedBytes);
            NewNotificationRequest newNotificationRequest = sharedSteps.getNotificationRequest();
            // get timeline from delivery-push
            NotificationHistoryResponse notificationHistory = this.pnPrivateDeliveryPushExternalClient.getNotificationHistory(iun, newNotificationRequest.getRecipients().size(), sharedSteps.getNotificationCreationDate());
            timelineElementList = notificationHistory.getTimeline();
            FullSentNotification fullSentNotification = new FullSentNotification();
            fullSentNotification.setTimeline(timelineElementList);
            sharedSteps.setSentNotification(fullSentNotification);
        } else {
            // proceed with default flux
            iun = sharedSteps.getSentNotification().getIun();
            sharedSteps.setSentNotification(b2bClient.getSentNotification(iun));
            timelineElementList = sharedSteps.getSentNotification().getTimeline();
        }

        // get timeline event id
        if (dataFromTest != null) {
            String timelineEventId = sharedSteps.getTimelineEventId(timelineEventCategory, iun, dataFromTest);
            timelineElement = timelineElementList.stream().filter(elem -> elem.getElementId().startsWith(timelineEventId)).findAny().orElse(null);
        } else {
            timelineElement = timelineElementList.stream().filter(elem -> elem.getCategory().getValue().equals(timelineEventCategory)).findAny().orElse(null);
        }
        return timelineElement;
    }

    @And("viene verificato che il numero di elementi di timeline {string} sia di {long}")
    public void getTimelineElementListSize(String timelineEventCategory, Long size, @Transpose DataTest dataFromTest) {
        List<TimelineElement> timelineElementList = sharedSteps.getSentNotification().getTimeline();
        String iun;
        if (timelineEventCategory.equals(TimelineElementCategory.REQUEST_REFUSED.getValue())) {
            String requestId = sharedSteps.getNewNotificationResponse().getNotificationRequestId();
            byte[] decodedBytes = Base64.getDecoder().decode(requestId);
            iun = new String(decodedBytes);
        } else {
            // proceed with default flux
            iun = sharedSteps.getSentNotification().getIun();
        }
        // get timeline event id
        String timelineEventId = sharedSteps.getTimelineEventId(timelineEventCategory, iun, dataFromTest);
        if (timelineEventCategory.equals(TimelineElementCategory.SEND_ANALOG_PROGRESS.getValue())) {
            TimelineElement timelineElementFromTest = dataFromTest.getTimelineElement();
            TimelineElementDetails timelineElementDetails = timelineElementFromTest.getDetails();
            Assertions.assertEquals(size, timelineElementList.stream().filter(elem -> elem.getElementId().startsWith(timelineEventId) && elem.getDetails().getDeliveryDetailCode().equals(timelineElementDetails.getDeliveryDetailCode())).count());
        } else {
            Assertions.assertEquals(size, timelineElementList.stream().filter(elem -> elem.getElementId().startsWith(timelineEventId)).count());
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string}")
    public void readingEventUpToTheTimelineElementOfNotification(String timelineEventCategory) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        TimelineElement timelineElement = null;

        for (int i = 0; i < timelineElementWait.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineElementWait.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));

            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());

            timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementWait.getTimelineElementCategory())).findAny().orElse(null);
            if (timelineElement != null) {
                break;
            }
        }
        try {
            Assertions.assertNotNull(timelineElement);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con deliveryDetailCode {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCode(String timelineEventCategory, String deliveryDetailCode) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);
        TimelineElement timelineElement = null;

        for (int i = 0; i < timelineElementWait.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineElementWait.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());

            for (TimelineElement element : sharedSteps.getSentNotification().getTimeline()) {
                if (element.getCategory().equals(timelineElementWait.getTimelineElementCategory()) && element.getDetails().getDeliveryDetailCode().equals(deliveryDetailCode)) {
                    timelineElement = element;
                    break;
                }
            }

            if (timelineElement != null) {
                break;
            }
        }
        try {
            Assertions.assertNotNull(timelineElement);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @And("viene verificato il campo sendRequestId dell' evento di timeline {string}")
    public void vieneVerificatoCampoSendRequestIdEventoTimeline(String timelineEventCategory) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
        TimelineElement timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementWait.getTimelineElementCategory())).findAny().orElse(null);
        Assertions.assertNotNull(timelineElement);
        Assertions.assertNotNull(timelineElement.getDetails());
        Assertions.assertNotNull(timelineElement.getDetails().getSendRequestId());
        String sendRequestId = timelineElement.getDetails().getSendRequestId();
        TimelineElement timelineElementRelative = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getElementId().equals(sendRequestId)).findAny().orElse(null);
        Assertions.assertNotNull(timelineElementRelative);
    }

    @And("viene verificato il campo serviceLevel dell' evento di timeline {string} sia valorizzato con {string}")
    public void vieneVerificatoCampoServiceLevelEventoTimeline(String timelineEventCategory, String value) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        ServiceLevel level;
        switch (value) {
            case "AR_REGISTERED_LETTER":
                level = ServiceLevel.AR_REGISTERED_LETTER;
                break;
            case "REGISTERED_LETTER_890":
                level = ServiceLevel.REGISTERED_LETTER_890;
                break;
            default:
                throw new IllegalArgumentException();

        }
        sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
        TimelineElement timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementWait.getTimelineElementCategory())).findAny().orElse(null);
        System.out.println("TIMELINE_ELEMENT: " + timelineElement);
        Assertions.assertNotNull(timelineElement);
        Assertions.assertNotNull(timelineElement.getDetails());
        Assertions.assertEquals(timelineElement.getDetails().getServiceLevel(), level);
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} per l'utente {int}")
    public void readingEventUpToTheTimelineElementOfNotificationPerUtente(String timelineEventCategory, Integer destinatario) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        TimelineElement timelineElement = null;

        for (int i = 0; i < timelineElementWait.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineElementWait.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));

            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());

            timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementWait.getTimelineElementCategory())).findAny().orElse(null);
            if (timelineElement != null && timelineElement.getDetails().getRecIndex().equals(destinatario)) {
                break;
            }
        }
        try {
            Assertions.assertNotNull(timelineElement);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} verifica numero pagine AAR {int}")
    public void readingEventUpToTheTimelineElementOfNotificationPerVerificaNumPagine(String timelineEventCategory, Integer numPagine) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        TimelineElement timelineElement = null;

        for (int i = 0; i < timelineElementWait.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineElementWait.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));

            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());

            timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementWait.getTimelineElementCategory())).findAny().orElse(null);
            if (timelineElement != null) {
                break;
            }
        }
        try {
            Assertions.assertNotNull(timelineElement);
            Assertions.assertEquals(timelineElement.getDetails().getNumberOfPages(), numPagine);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi e verificho che l'utente {int} non abbia associato un evento {string}")
    public void vengonoLettiGliEventiVerifichoCheUtenteNonAbbiaAssociatoEvento(Integer destinatario, String timelineEventCategory) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        TimelineElement timelineElement = null;
        sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
        for (TimelineElement element : sharedSteps.getSentNotification().getTimeline()) {
            if (element.getCategory().equals(timelineElementWait.getTimelineElementCategory()) && element.getDetails().getRecIndex().equals(destinatario)) {
                timelineElement = element;
            }
        }

        try {
            Assertions.assertNull(timelineElement);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi e verificho che l'utente {int} non abbia associato un evento {string} con responseStatus {string}")
    public void vengonoLettiGliEventiVerifichoCheUtenteNonAbbiaAssociatoEventoWithResponseStatus(Integer destinatario, String timelineEventCategory, String responseStatus) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        TimelineElement timelineElement = null;
        sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
        for (TimelineElement element : sharedSteps.getSentNotification().getTimeline()) {
            if (element.getCategory().equals(timelineElementWait.getTimelineElementCategory())
                    && element.getDetails().getRecIndex().equals(destinatario)
                    && element.getDetails().getResponseStatus().getValue().equals(responseStatus)) {
                timelineElement = element;
            }
        }

        try {
            Assertions.assertNull(timelineElement);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("la PA richiede il download dell'attestazione opponibile {string}")
    public void paRequiresDownloadOfLegalFact(String legalFactCategory) {
        downloadLegalFact(legalFactCategory, true, false, false, null);
    }

    @Then("la PA richiede il download dell'attestazione opponibile {string} con deliveryDetailCode {string}")
    public void paRequiresDownloadOfLegalFactWithDeliveryDetailCode(String legalFactCategory, String deliveryDetailCode) {
        downloadLegalFact(legalFactCategory, true, false, false, deliveryDetailCode);
    }

    @Then("viene richiesto tramite appIO il download dell'attestazione opponibile {string}")
    public void appIODownloadLegalFact(String legalFactCategory) {
        downloadLegalFact(legalFactCategory, false, true, false, null);
    }

    @Then("{string} richiede il download dell'attestazione opponibile {string}")
    public void userDownloadLegalFact(String user, String legalFactCategory) {
        sharedSteps.selectUser(user);
        downloadLegalFact(legalFactCategory, false, false, true, null);
    }

    private void downloadLegalFact(String legalFactCategory, boolean pa, boolean appIO, boolean webRecipient, String deliveryDetailCode) {
        try {
            Thread.sleep(sharedSteps.getWait());
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }

        TimelineElementCategory timelineElementInternalCategory;
        TimelineElement timelineElement = null;
        LegalFactCategory category;
        switch (legalFactCategory) {
            case "SENDER_ACK":
                timelineElementInternalCategory = TimelineElementCategory.REQUEST_ACCEPTED;
                category = LegalFactCategory.SENDER_ACK;
                break;
            case "RECIPIENT_ACCESS":
                timelineElementInternalCategory = TimelineElementCategory.NOTIFICATION_VIEWED;
                category = LegalFactCategory.RECIPIENT_ACCESS;
                break;
            case "PEC_RECEIPT":
                timelineElementInternalCategory = TimelineElementCategory.SEND_DIGITAL_PROGRESS;
                category = LegalFactCategory.PEC_RECEIPT;
                break;
            case "DIGITAL_DELIVERY":
                timelineElementInternalCategory = TimelineElementCategory.DIGITAL_SUCCESS_WORKFLOW;
                category = LegalFactCategory.DIGITAL_DELIVERY;
                break;
            case "DIGITAL_DELIVERY_FAILURE":
                timelineElementInternalCategory = TimelineElementCategory.DIGITAL_FAILURE_WORKFLOW;
                category = LegalFactCategory.DIGITAL_DELIVERY;
                break;
            case "SEND_ANALOG_PROGRESS":
                timelineElementInternalCategory = TimelineElementCategory.SEND_ANALOG_PROGRESS;
                category = LegalFactCategory.ANALOG_DELIVERY;
                break;
            case "COMPLETELY_UNREACHABLE":
                timelineElementInternalCategory = TimelineElementCategory.COMPLETELY_UNREACHABLE;
                category = LegalFactCategory.ANALOG_FAILURE_DELIVERY;
                break;
            default:
                throw new IllegalArgumentException();
        }

        for (TimelineElement element : sharedSteps.getSentNotification().getTimeline()) {
            if (element.getCategory().equals(timelineElementInternalCategory)) {
                if (deliveryDetailCode == null) {
                    timelineElement = element;
                    break;
                } else if (deliveryDetailCode != null && element.getDetails().getDeliveryDetailCode().equals(deliveryDetailCode)) {
                    timelineElement = element;
                    break;
                }
            }
        }

        try {
            System.out.println("ELEMENT: " + timelineElement);
            Assertions.assertNotNull(timelineElement.getLegalFactsIds());
            Assertions.assertFalse(CollectionUtils.isEmpty(timelineElement.getLegalFactsIds()));
            Assertions.assertEquals(category, timelineElement.getLegalFactsIds().get(0).getCategory());
            LegalFactCategory categorySearch = timelineElement.getLegalFactsIds().get(0).getCategory();
            String key = timelineElement.getLegalFactsIds().get(0).getKey();
            String keySearch = null;
            if (key.contains("PN_LEGAL_FACTS")) {
                keySearch = key.substring(key.indexOf("PN_LEGAL_FACTS"));
            } else if (key.contains("PN_NOTIFICATION_ATTACHMENTS")) {
                keySearch = key.substring(key.indexOf("PN_NOTIFICATION_ATTACHMENTS"));
            } else if (key.contains("PN_EXTERNAL_LEGAL_FACTS")) {
                keySearch = key.substring(key.indexOf("PN_EXTERNAL_LEGAL_FACTS"));
            }
            String finalKeySearch = keySearch;
            if (pa) {
                Assertions.assertDoesNotThrow(() -> this.b2bClient.getLegalFact(sharedSteps.getSentNotification().getIun(), categorySearch, finalKeySearch));
            }
            if (appIO) {
                Assertions.assertDoesNotThrow(() -> this.appIOB2bClient.getLegalFact(sharedSteps.getSentNotification().getIun(), categorySearch.toString(), finalKeySearch,
                        sharedSteps.getSentNotification().getRecipients().get(0).getTaxId()));
            }
            if (webRecipient) {
                Assertions.assertDoesNotThrow(() -> this.webRecipientClient.getLegalFact(sharedSteps.getSentNotification().getIun(),
                        sharedSteps.deepCopy(categorySearch,
                                it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.LegalFactCategory.class),
                        finalKeySearch
                ));
            }
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("si verifica che la notifica abbia lo stato VIEWED")
    public void checksNotificationViewedStatus() {
        try {
            Thread.sleep(sharedSteps.getWait() * 2);
        } catch (InterruptedException interruptedException) {
            logger.error("InterruptedException error");
        }
        sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
        try {
            Assertions.assertNotNull(sharedSteps.getSentNotification().getNotificationStatusHistory().stream().filter(elem -> elem.getStatus().equals(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.VIEWED)).findAny().orElse(null));
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

        priceVerification(price, null, 0);
    }

    @Then("viene verificato il costo = {string} della notifica")
    public void notificationPriceVerification(String price) {
        try {
            Thread.sleep(sharedSteps.getWait() * 2);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        priceVerification(price, null, 0);
    }

    @Then("viene verificato il costo = {string} della notifica per l'utente {int}")
    public void notificationPriceVerificationPerDestinatario(String price, Integer destinatario) {
        try {
            Thread.sleep(sharedSteps.getWait() * 2);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        priceVerification(price, null, destinatario);
    }

    private void priceVerification(String price, String date, Integer destinatario) {
        NotificationPriceResponse notificationPrice = this.b2bClient.getNotificationPrice(sharedSteps.getSentNotification().getRecipients().get(destinatario).getPayment().getCreditorTaxId(),
                sharedSteps.getSentNotification().getRecipients().get(destinatario).getPayment().getNoticeCode());
        try {
            Assertions.assertEquals(notificationPrice.getIun(), sharedSteps.getSentNotification().getIun());
            if (price != null) {
                logger.info("Costo notifica: {} destinatario: {}", notificationPrice.getAmount(), destinatario);
                Assertions.assertEquals(notificationPrice.getAmount(), Integer.parseInt(price));
            }
            if (date != null) {
                Assertions.assertNotNull(notificationPrice.getRefinementDate());
            }
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @And("{string} legge la notifica ricevuta")
    public void userReadReceivedNotification(String recipient) {
        sharedSteps.selectUser(recipient);
        Assertions.assertDoesNotThrow(() -> {
            webRecipientClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(), null);
        });
        try {
            Thread.sleep(sharedSteps.getWorkFlowWait());
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Then("viene verificato che la chiave dell'attestazione opponibile {string} è {string}")
    public void verifiedThatTheKeyOfTheLegalFactIs(String legalFactCategory, String key) {
        try {
            Thread.sleep(sharedSteps.getWait());
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }

        TimelineElementCategory timelineElementInternalCategory;
        TimelineElement timelineElement;
        LegalFactCategory category;
        switch (legalFactCategory) {
            case "SENDER_ACK":
                timelineElementInternalCategory = TimelineElementCategory.REQUEST_ACCEPTED;
                timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().orElse(null);
                category = LegalFactCategory.SENDER_ACK;
                break;
            case "RECIPIENT_ACCESS":
                timelineElementInternalCategory = TimelineElementCategory.NOTIFICATION_VIEWED;
                timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().orElse(null);
                category = LegalFactCategory.RECIPIENT_ACCESS;
                break;
            case "PEC_RECEIPT":
                timelineElementInternalCategory = TimelineElementCategory.SEND_DIGITAL_PROGRESS;
                timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().orElse(null);
                category = LegalFactCategory.PEC_RECEIPT;
                break;
            case "DIGITAL_DELIVERY":
                timelineElementInternalCategory = TimelineElementCategory.DIGITAL_SUCCESS_WORKFLOW;
                timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().orElse(null);
                category = LegalFactCategory.DIGITAL_DELIVERY;
                break;
            default:
                throw new IllegalArgumentException();
        }
        try {
            Assertions.assertNotNull(timelineElement.getLegalFactsIds());
            Assertions.assertEquals(category, timelineElement.getLegalFactsIds().get(0).getCategory());
            Assertions.assertTrue(timelineElement.getLegalFactsIds().get(0).getKey().contains(key));
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @And("l'avviso pagopa viene pagato correttamente")
    public void laNotificaVienePagata() {
        NotificationPriceResponse notificationPrice = this.b2bClient.getNotificationPrice(sharedSteps.getSentNotification().getRecipients().get(0).getPayment().getCreditorTaxId(),
                sharedSteps.getSentNotification().getRecipients().get(0).getPayment().getNoticeCode());

        PaymentEventsRequestPagoPa eventsRequestPagoPa = new PaymentEventsRequestPagoPa();

        PaymentEventPagoPa paymentEventPagoPa = new PaymentEventPagoPa();
        paymentEventPagoPa.setNoticeCode(sharedSteps.getSentNotification().getRecipients().get(0).getPayment().getNoticeCode());
        paymentEventPagoPa.setCreditorTaxId(sharedSteps.getSentNotification().getRecipients().get(0).getPayment().getCreditorTaxId());
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        paymentEventPagoPa.setPaymentDate(fmt.format(now()));
        paymentEventPagoPa.setAmount(notificationPrice.getAmount());

        List<PaymentEventPagoPa> paymentEventPagoPaList = new LinkedList<>();
        paymentEventPagoPaList.add(paymentEventPagoPa);

        eventsRequestPagoPa.setEvents(paymentEventPagoPaList);

        b2bClient.paymentEventsRequestPagoPa(eventsRequestPagoPa);
    }

    @Then("il modello f24 viene pagato correttamente")
    public void ilModelloFVienePagatoCorrettamente() {

        PaymentEventsRequestF24 eventsRequestF24 = new PaymentEventsRequestF24();

        NotificationPriceResponse notificationPrice = this.b2bClient.getNotificationPrice(sharedSteps.getSentNotification().getRecipients().get(0).getPayment().getCreditorTaxId(),
                sharedSteps.getSentNotification().getRecipients().get(0).getPayment().getNoticeCode());

        PaymentEventF24 paymentEventF24 = new PaymentEventF24();
        paymentEventF24.setIun(sharedSteps.getSentNotification().getIun());
        paymentEventF24.setRecipientTaxId(sharedSteps.getSentNotification().getRecipients().get(0).getTaxId());
        paymentEventF24.setRecipientType(sharedSteps.getSentNotification().getRecipients().get(0).getRecipientType().equals(NotificationRecipient.RecipientTypeEnum.PF) ? "PF" : "PG");
        paymentEventF24.setPaymentDate(now());
        paymentEventF24.setAmount(notificationPrice.getAmount());

        List<PaymentEventF24> eventF24List = new LinkedList<>();
        eventF24List.add(paymentEventF24);

        eventsRequestF24.setEvents(eventF24List);

        b2bClient.paymentEventsRequestF24(eventsRequestF24);
    }

    @And("si verifica che il timelineId dell'elemento {string} corrisponda a quello di {string}")
    public void siVerificaCheLaDataDellElementoCorrispondaAQuelloDi(String timelineElementCategory, String timelineElementCategoryToCompare) {
        TimelineElementWait timelineElementWaitFirst = getTimelineElementCategory(timelineElementCategory);
        TimelineElementWait timelineElementWaitSecond = getTimelineElementCategory(timelineElementCategoryToCompare);

        TimelineElement timelineElementFirst = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementWaitFirst.getTimelineElementCategory())).findAny().orElse(null);
        TimelineElement timelineElementSecond = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementWaitSecond.getTimelineElementCategory())).findAny().orElse(null);
        System.out.println("timelineElementFirst");
        System.out.println(timelineElementFirst);
        System.out.println("timelineElementSecond");
        System.out.println(timelineElementSecond);
    }

    @Then("sono presenti {int} attestazioni opponibili RECIPIENT_ACCESS")
    public void sonoPresentiAttestazioniOpponibili(int number) {

        TimelineElementCategory timelineElementInternalCategory = TimelineElementCategory.NOTIFICATION_VIEWED;
        boolean findElement = false;
        for (int i = 0; i < 16; i++) {
            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));

            List<TimelineElement> timeline = sharedSteps.getSentNotification().getTimeline();
            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());

            int count = 0;
            for (TimelineElement element : timeline) {
                if (element.getCategory().equals(timelineElementInternalCategory)) count++;
            }

            if (count == number) {
                findElement = true;
                break;
            }

            try {
                Thread.sleep(sharedSteps.getWorkFlowWait());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
        }
        try {
            Assertions.assertTrue(findElement);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con responseStatus {string} per l'utente {int}")
    public void vengonoLettiGliEventiFinoAllElementoDiTimelineDellaNotificaConResponseStatusPerUtente(String timelineEventCategory, String code, Integer destinatario) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        TimelineElement timelineElement = null;

        for (int i = 0; i < timelineElementWait.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineElementWait.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));

            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());

            timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementWait.getTimelineElementCategory())).findAny().orElse(null);
            if (timelineElement != null && timelineElement.getDetails().getRecIndex().equals(destinatario)) {
                break;
            }
        }
        try {
            Assertions.assertNotNull(timelineElement);
            Assertions.assertNotNull(timelineElement.getDetails().getResponseStatus());
            Assertions.assertEquals(timelineElement.getDetails().getResponseStatus().getValue(), code);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con responseStatus {string}")
    public void vengonoLettiGliEventiFinoAllElementoDiTimelineDellaNotificaConResponseStatus(String timelineEventCategory, String code) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        TimelineElement timelineElement = null;

        for (int i = 0; i < timelineElementWait.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineElementWait.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));

            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());

            timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementWait.getTimelineElementCategory())).findAny().orElse(null);
            if (timelineElement != null) {
                break;
            }
        }
        try {
            Assertions.assertNotNull(timelineElement);
            Assertions.assertNotNull(timelineElement.getDetails().getResponseStatus());
            Assertions.assertEquals(timelineElement.getDetails().getResponseStatus().getValue(), code);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("viene verificato che nell'elemento di timeline della notifica {string} siano configurati i campi municipalityDetails e foreignState")
    public void vieneVerificatoCheElementoTimelineSianoConfiguratiCampiMunicipalityDetailsForeignState(String timelineEventCategory) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        TimelineElement timelineElement = null;

        sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
        timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementWait.getTimelineElementCategory())).findAny().orElse(null);
        try {
            logger.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNotNull(timelineElement);
            Assertions.assertNotNull(timelineElement.getDetails().getPhysicalAddress().getMunicipality());
            Assertions.assertNotNull(timelineElement.getDetails().getPhysicalAddress().getForeignState());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("viene verificato che nell'elemento di timeline della notifica {string} con responseStatus {string} sia presente il campo deliveryDetailCode")
    public void vieneVerificatoCheElementoTimelineSianoConfiguratoCampoDeliveryDetailCode(String timelineEventCategory, String code) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        TimelineElement timelineElement = null;

        sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
        timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementWait.getTimelineElementCategory())).findAny().orElse(null);
        try {
            logger.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNotNull(timelineElement);
            Assertions.assertNotNull(timelineElement.getDetails().getResponseStatus());
            Assertions.assertEquals(timelineElement.getDetails().getResponseStatus().getValue(), code);
            Assertions.assertNotNull(timelineElement.getDetails().getDeliveryDetailCode());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("viene verificato che nell'elemento di timeline della notifica {string} con responseStatus {string} sia presente i campi deliveryDetailCode e deliveryFailureCause")
    public void vieneVerificatoCheElementoTimelineSianoConfiguratoCampoDeliveryDetailCodeDeliveryFailureCause(String timelineEventCategory, String code) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        TimelineElement timelineElement = null;

        sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
        timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementWait.getTimelineElementCategory())).findAny().orElse(null);
        try {
            logger.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNotNull(timelineElement);
            Assertions.assertNotNull(timelineElement.getDetails().getResponseStatus());
            Assertions.assertEquals(timelineElement.getDetails().getResponseStatus().getValue(), code);
            Assertions.assertNotNull(timelineElement.getDetails().getDeliveryDetailCode());
            Assertions.assertNotNull(timelineElement.getDetails().getDeliveryFailureCause());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("si attende la corretta sospensione dell'invio cartaceo")
    public void siAttendeLaCorrettaSopsensioneDellInvioCartaceo() {
        TimelineElementWait timelineElementWait = getTimelineElementCategory("ANALOG_SUCCESS_WORKFLOW");

        TimelineElement timelineElement = null;

        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(60000);
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));

            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());

            timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementWait.getTimelineElementCategory())).findAny().orElse(null);
            if (timelineElement != null) {
                break;
            }
        }
        Assertions.assertNull(timelineElement);

    }

    @Then("si attende il corretto pagamento della notifica")
    public void siAttendeIlCorrettoPagamentoDellaNotifica() {
        TimelineElementWait timelineElementWait = getTimelineElementCategory("PAYMENT");

        TimelineElement timelineElement = null;

        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(60000);
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));

            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());

            timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementWait.getTimelineElementCategory())).findAny().orElse(null);
            if (timelineElement != null) {
                break;
            }
        }
        Assertions.assertNotNull(timelineElement);

    }

    @Then("viene verificato che nell'elemento di timeline della notifica {string} e' presente il campo Digital Address di piattaforma")
    public void vieneVerificatoCheElementoTimelineSianoConfiguratoCampoDigitalAddressPiattaforma(String timelineEventCategory) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        TimelineElement timelineElement = null;

        sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
        timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementWait.getTimelineElementCategory()) && elem.getElementId().contains("SOURCE_PLATFORM")).findAny().orElse(null);
        try {
            logger.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNotNull(timelineElement);
            Assertions.assertNotNull(timelineElement.getDetails().getDigitalAddress());
            Assertions.assertFalse("DSRDNI00A01A225I@pnpagopa.postecert.local".equalsIgnoreCase(timelineElement.getDetails().getDigitalAddress().getAddress()));
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }

    }

    @Then("viene verificato che nell'elemento di timeline della notifica {string} sia presente il campo Digital Address")
    public void vieneVerificatoCheElementoTimelineSianoConfiguratoCampoDigitalAddress(String timelineEventCategory) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        TimelineElement timelineElement = null;

        sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
        timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementWait.getTimelineElementCategory()) && elem.getElementId().contains("SOURCE_PLATFORM")).findAny().orElse(null);
        try {
            logger.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNotNull(timelineElement);
            Assertions.assertNotNull(timelineElement.getDetails().getDigitalAddress());

        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }

    }

    @Then("viene letta la timeline fino all'elemento {string}")
    public void vieneLettaLaTimeline(String timelineEventCategory, @Transpose DataTest dataFromTest) {
        // calc how much time wait
        Integer pollingTime = dataFromTest.getPollingTime();
        Integer numCheck = dataFromTest.getNumCheck();
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);
        Integer defaultPollingTime = timelineElementWait.getWaiting();
        Integer defaultNumCheck = timelineElementWait.getNumCheck();
        Integer waitingTime = (pollingTime != null ? pollingTime : defaultPollingTime) * (numCheck != null ? numCheck : defaultNumCheck);

        logger.info("CURRENT_DATE: " + now().toString());
        await()
            .atMost(waitingTime, MILLISECONDS)
            .with()
            .pollInterval(pollingTime != null ? pollingTime : defaultPollingTime, MILLISECONDS)
            .pollDelay(0, MILLISECONDS)
            .ignoreExceptions()
            .untilAsserted(() -> {
                logger.info("#############################∞#################################");
                logger.info("CURRENT_DATE: " + now().toString());
                TimelineElement timelineElement = getAndStoreTimeline(timelineEventCategory, dataFromTest);
                List<TimelineElement> timelineElementList = sharedSteps.getSentNotification().getTimeline();

                logger.info("NOTIFICATION_TIMELINE: " + timelineElementList);
                Assertions.assertNotNull(timelineElementList);
                Assertions.assertNotEquals(timelineElementList.size(), 0);
                Assertions.assertNotNull(timelineElement);
        });
    }

    @And("viene verificato che l'elemento di timeline {string} esista")
    public void vieneVerificatoCheElementoTimelineEsista(String timelineEventCategory, @Transpose DataTest dataFromTest) {
        TimelineElement timelineElement = sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataFromTest);
        try {
            logger.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNotNull(timelineElement);
            if (dataFromTest != null) {
                checkTimelineElementEquality(timelineEventCategory, timelineElement, dataFromTest);
            }
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @And("viene verificato che l'elemento di timeline {string} non esista")
    public void vieneVerificatoCheElementoTimelineNonEsista(String timelineEventCategory, @Transpose DataTest dataFromTest) {
        // calc how much time wait
        Integer pollingTime = dataFromTest.getPollingTime();
        Integer numCheck = dataFromTest.getNumCheck();
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);
        Integer defaultPollingTime = timelineElementWait.getWaiting();
        Integer defaultNumCheck = timelineElementWait.getNumCheck();
        Integer waitingTime = (pollingTime != null ? pollingTime : defaultPollingTime) * (numCheck != null ? numCheck : defaultNumCheck);

        await()
            .atMost(waitingTime, MILLISECONDS)
            .with()
            .pollInterval(pollingTime != null ? pollingTime : defaultPollingTime, MILLISECONDS)
            .pollDelay(0, MILLISECONDS)
            .ignoreExceptions()
            .untilAsserted(() -> {
                TimelineElement timelineElement = getAndStoreTimeline(timelineEventCategory, dataFromTest);
                List<TimelineElement> timelineElementList = sharedSteps.getSentNotification().getTimeline();

                logger.info("NOTIFICATION_TIMELINE: " + timelineElementList);
                Assertions.assertNotNull(timelineElementList);
                Assertions.assertNotEquals(timelineElementList.size(), 0);
                Assertions.assertNull(timelineElement);
            });
    }

    @And("viene schedulato il perfezionamento per decorrenza termini per il caso {string}")
    public void vieneSchedulatoIlPerfezionamento(String timelineCategory, @Transpose DataTest dataFromTest) {
        TimelineElement timelineElement = sharedSteps.getTimelineElementByEventId(TimelineElementCategory.SCHEDULE_REFINEMENT.getValue(), dataFromTest);

        TimelineElement timelineElementForDateCalculation = null;
        if (timelineCategory.equals(TimelineElementCategory.DIGITAL_SUCCESS_WORKFLOW.getValue())) {
            timelineElementForDateCalculation = sharedSteps.getTimelineElementByEventId(TimelineElementCategory.SEND_DIGITAL_FEEDBACK.getValue(), dataFromTest);
        } else if (timelineCategory.equals(TimelineElementCategory.DIGITAL_FAILURE_WORKFLOW.getValue())) {
            timelineElementForDateCalculation = sharedSteps.getTimelineElementByEventId(TimelineElementCategory.DIGITAL_FAILURE_WORKFLOW.getValue(), dataFromTest);
        }  else if (timelineCategory.equals(TimelineElementCategory.ANALOG_SUCCESS_WORKFLOW.getValue())) {
            timelineElementForDateCalculation = sharedSteps.getTimelineElementByEventId(TimelineElementCategory.SEND_ANALOG_FEEDBACK.getValue(), dataFromTest);
        }

        Assertions.assertNotNull(timelineElementForDateCalculation);

        OffsetDateTime notificationDate = null;
        Integer schedulingDaysRefinement = null;
        if (timelineCategory.equals(TimelineElementCategory.DIGITAL_SUCCESS_WORKFLOW.getValue())) {
            notificationDate = timelineElementForDateCalculation.getDetails().getNotificationDate();
            schedulingDaysRefinement = sharedSteps.getSchedulingDaysSuccessDigitalRefinement();
        } else if (timelineCategory.equals(TimelineElementCategory.DIGITAL_FAILURE_WORKFLOW.getValue())) {
            notificationDate = timelineElementForDateCalculation.getTimestamp();
            schedulingDaysRefinement = sharedSteps.getSchedulingDaysFailureDigitalRefinement();
        } else if (timelineCategory.equals(TimelineElementCategory.ANALOG_SUCCESS_WORKFLOW.getValue())) {
            notificationDate = timelineElementForDateCalculation.getTimestamp();
            schedulingDaysRefinement = sharedSteps.getSchedulingDaysSuccessAnalogRefinement();
        }

        OffsetDateTime schedulingDate = notificationDate.plusMinutes(schedulingDaysRefinement);
        Integer hour = schedulingDate.getHour();
        Integer minutes = schedulingDate.getMinute();
        if ((hour == 21 && minutes > 0) || hour > 21) {
            Integer timeToAddInNonVisibilityTimeCase = sharedSteps.getTimeToAddInNonVisibilityTimeCase();
            schedulingDate = schedulingDate.plusMinutes(timeToAddInNonVisibilityTimeCase);
        }
        Assertions.assertEquals(timelineElement.getDetails().getSchedulingDate(), schedulingDate);
    }

    @And("si attende che sia presente il perfezionamento per decorrenza termini")
    public void siAttendePresenzaPerfezionamentoDecorrenzaTermini(@Transpose DataTest dataFromTest) throws InterruptedException {
        String iun = sharedSteps.getSentNotification().getIun();
        if (dataFromTest != null) {
            TimelineElement timelineElement = sharedSteps.getTimelineElementByEventId(TimelineElementCategory.SCHEDULE_REFINEMENT.getValue(), dataFromTest);
            OffsetDateTime schedulingDate = timelineElement.getDetails().getSchedulingDate();
            OffsetDateTime currentDate = now().atZoneSameInstant(ZoneId.of("UTC")).toOffsetDateTime();
            Long remainingTime = ChronoUnit.MILLIS.between(currentDate, schedulingDate);
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
        if (dataFromTest != null) {
            TimelineElement timelineElement = sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataFromTest);
            OffsetDateTime firstSend = timelineElement.getTimestamp();
            Integer secondNotificationWorkflowWaitingTime = sharedSteps.getSecondNotificationWorkflowWaitingTime();
            OffsetDateTime nextSend = firstSend.plusMinutes(secondNotificationWorkflowWaitingTime);
            OffsetDateTime currentDate = now().atZoneSameInstant(ZoneId.of("UTC")).toOffsetDateTime();
            Long remainingTime = ChronoUnit.MILLIS.between(currentDate, nextSend);
            if (remainingTime > 0) {
                Thread.sleep(remainingTime + 30 * 1000);
            }
            // get the updated notification
            sharedSteps.setSentNotification(b2bClient.getSentNotification(iun));
        }
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
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);
        TimelineElement timelineElement = null;

        for (int i = 0; i < timelineElementWait.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineElementWait.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());

            for (TimelineElement element : sharedSteps.getSentNotification().getTimeline()) {
                if (element.getCategory().equals(timelineElementWait.getTimelineElementCategory())
                        && element.getDetails().getRecIndex().equals(recIndex)
                        && element.getDetails().getDeliveryDetailCode().equals(deliveryDetailCode)
                        && Objects.nonNull(element.getLegalFactsIds()) && element.getLegalFactsIds().size() > 0
                        && element.getLegalFactsIds().get(0).getCategory().getValue().equals(legalFactIdCategory)
                        && Objects.nonNull(element.getDetails().getAttachments()) && element.getDetails().getAttachments().size() > 0
                        && element.getDetails().getAttachments().get(0).getDocumentType().equals(documentType)
                ) {
                    timelineElement = element;
                    break;
                }
            }

            if (timelineElement != null) {
                break;
            }
        }
        try {
            Assertions.assertNotNull(timelineElement);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @And("vengono letti gli eventi fino all'elemento di timeline {string} della notifica per il destinatario {int}, con deliveryDetailCode {string} e con deliveryFailureCause {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCodeAndDeliveryFailureCause(String timelineEventCategory, Integer recIndex, String deliveryDetailCode, String deliveryFailureCause) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);
        TimelineElement timelineElement = null;

        List<String> failureCauses = Arrays.asList(deliveryFailureCause.split(" "));

        for (int i = 0; i < timelineElementWait.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineElementWait.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());

            for (TimelineElement element : sharedSteps.getSentNotification().getTimeline()) {
                if (element.getCategory().equals(timelineElementWait.getTimelineElementCategory())
                        && element.getDetails().getRecIndex().equals(recIndex)
                        && failureCauses.contains(element.getDetails().getDeliveryFailureCause())
                        && element.getDetails().getDeliveryDetailCode().equals(deliveryDetailCode)
                ) {
                    timelineElement = element;
                    break;
                }
            }

            if (timelineElement != null) {
                break;
            }
        }
        try {
            Assertions.assertNotNull(timelineElement);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline {string} della notifica per il destinatario {int} con deliveryDetailCode {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithRecIndexAndDeliveryDetailCode(String timelineEventCategory, Integer recIndex, String deliveryDetailCode) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);
        TimelineElement timelineElement = null;

        for (int i = 0; i < timelineElementWait.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineElementWait.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());

            for (TimelineElement element : sharedSteps.getSentNotification().getTimeline()) {
                if (element.getCategory().equals(timelineElementWait.getTimelineElementCategory()) && element.getDetails().getRecIndex().equals(recIndex) && element.getDetails().getDeliveryDetailCode().equals(deliveryDetailCode)) {
                    timelineElement = element;
                    break;
                }
            }

            if (timelineElement != null) {
                break;
            }
        }
        try {
            Assertions.assertNotNull(timelineElement);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @And("controlla che il timestamp di {string} sia dopo quello di invio e di attesa di lettura del messaggio di cortesia")
    public void verificaTimestamp(String timelineEventCategory, @Transpose DataTest dataFromTest) {
        TimelineElement timelineElementCategory = getAndStoreTimeline(timelineEventCategory, dataFromTest);
        TimelineElement timelineElementSendCourtesyMessage = getAndStoreTimeline("SEND_COURTESY_MESSAGE", dataFromTest);

        Integer waitingForReadCourtesyMessage = sharedSteps.getWaitingForReadCourtesyMessage();

        OffsetDateTime timestampEventCategory = timelineElementCategory.getTimestamp();
        OffsetDateTime timestampEventSendCourtesyMessage = timelineElementSendCourtesyMessage.getTimestamp();
        OffsetDateTime timestampEventSendCourtesyMessageWithWaitingTime = timestampEventSendCourtesyMessage.plusMinutes(waitingForReadCourtesyMessage);

        Boolean test = timestampEventCategory.isEqual(timestampEventSendCourtesyMessageWithWaitingTime) || timestampEventCategory.isAfter(timestampEventSendCourtesyMessageWithWaitingTime);

        logger.info("timestamp " + timelineEventCategory + ": " + timestampEventCategory);
        logger.info("timestamp SEND_COURTESY_MESSAGE ( +" + waitingForReadCourtesyMessage + " minutes): " + timestampEventSendCourtesyMessageWithWaitingTime);
        logger.info("timestamp " + timelineEventCategory + " is after or equal timestamp SEND_COURTESY_MESSAGE?: " + test);

        Assertions.assertTrue(test);
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
}
