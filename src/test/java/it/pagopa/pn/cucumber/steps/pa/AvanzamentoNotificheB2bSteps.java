package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.impl.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.testclient.*;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.NotificationHistoryResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.ResponsePaperNotificationFailedDto;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.utils.*;
import it.pagopa.pn.cucumber.utils.TimelineElementWait;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpStatusCodeException;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
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
    private final PnExternalServiceClientImpl externalClient;
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
        this.externalClient = sharedSteps.getPnExternalServiceClient();
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
                numCheck = 16;
                waiting = waiting * 4;
                notificationInternalStatus = NotificationStatus.DELIVERED;
                break;
            case "CANCELLED":
                notificationInternalStatus = NotificationStatus.CANCELLED;
                break;
            case "EFFECTIVE_DATE":
                numCheck = 16;
                waiting = waiting * 4;
                notificationInternalStatus = NotificationStatus.EFFECTIVE_DATE;
                break;
            case "COMPLETELY_UNREACHABLE":
                numCheck = 16;
                waiting = waiting * 4;
                notificationInternalStatus = NotificationStatus.UNREACHABLE;
                break;
            case "VIEWED":
                numCheck = 4;
                waiting = waiting * 4;
                notificationInternalStatus = NotificationStatus.VIEWED;
                break;
            case "PAID":
                notificationInternalStatus = NotificationStatus.PAID;
                break;
            case "IN_VALIDATION":
                notificationInternalStatus = NotificationStatus.IN_VALIDATION;
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


    @Then("vengono letti gli eventi fino allo stato della notifica {string} per il destinatario {int} e presente l'evento {string}")
    public void readingEventUpToTheStatusOfNotification(String status, int destinatario, String evento) {
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
                numCheck = 16;
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
            case "VIEWED":
                numCheck = 4;
                waiting = waiting * 4;
                notificationInternalStatus = NotificationStatus.VIEWED;
                break;
            case "PAID":
                notificationInternalStatus = NotificationStatus.PAID;
                break;
            case "IN_VALIDATION":
                notificationInternalStatus = NotificationStatus.IN_VALIDATION;
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
            List<String> timelineElements = notificationStatusHistoryElement.getRelatedTimelineElements();
            boolean esiste = false;
            for (String tmpTimeline: timelineElements) {
                if (tmpTimeline.contains(evento) && tmpTimeline.contains("RECINDEX_"+destinatario)){
                    esiste = true;
                    break;
                };
            }
            Assertions.assertTrue(esiste);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }


    }



    private TimelineElementWait getTimelineElementCategory(String timelineEventCategory) {
        Integer waiting = sharedSteps.getWorkFlowWait();
        TimelineElementWait timelineElementWait;
        switch (timelineEventCategory) {
            case "REQUEST_ACCEPTED":

                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.REQUEST_ACCEPTED, 2, waiting);
                break;
            case "AAR_GENERATION":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.AAR_GENERATION, 2, waiting * 2);
                break;
            case "GET_ADDRESS":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.GET_ADDRESS, 2, waiting * 2);
                break;
            case "SEND_DIGITAL_DOMICILE":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.SEND_DIGITAL_DOMICILE, 2, waiting * 2);
                break;
            case "NOTIFICATION_VIEWED":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.NOTIFICATION_VIEWED, 2, waiting * 2);
                break;
            case "SEND_COURTESY_MESSAGE":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.SEND_COURTESY_MESSAGE, 15, sharedSteps.getWorkFlowWait());
                break;
            case "DIGITAL_SUCCESS_WORKFLOW":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.DIGITAL_SUCCESS_WORKFLOW, 3, waiting * 3);
                break;
            case "DIGITAL_FAILURE_WORKFLOW":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.DIGITAL_FAILURE_WORKFLOW, 15, waiting * 3);
                break;
            case "NOT_HANDLED":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.NOT_HANDLED, 15, sharedSteps.getWorkFlowWait());
                break;
            case "SEND_DIGITAL_FEEDBACK":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.SEND_DIGITAL_FEEDBACK, 2, waiting * 3);
                break;
            case "SEND_DIGITAL_PROGRESS":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.SEND_DIGITAL_PROGRESS, 5, waiting * 4);
                break;
            case "PUBLIC_REGISTRY_CALL":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.PUBLIC_REGISTRY_CALL, 2, waiting * 4);
                break;
            case "PUBLIC_REGISTRY_RESPONSE":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.PUBLIC_REGISTRY_RESPONSE, 4, waiting * 4);
                break;
            case "SCHEDULE_ANALOG_WORKFLOW":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.SCHEDULE_ANALOG_WORKFLOW, 2, waiting * 3);
                break;
            case "ANALOG_SUCCESS_WORKFLOW":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.ANALOG_SUCCESS_WORKFLOW, 5, waiting * 4);
                break;
            case "ANALOG_FAILURE_WORKFLOW":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.ANALOG_FAILURE_WORKFLOW, 15, sharedSteps.getWorkFlowWait());
                break;
            case "SEND_ANALOG_DOMICILE":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.SEND_ANALOG_DOMICILE, 4, waiting * 3);
                break;
            case "SEND_ANALOG_PROGRESS":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.SEND_ANALOG_PROGRESS, 6, waiting * 3);
                break;
            case "SEND_ANALOG_FEEDBACK":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.SEND_ANALOG_FEEDBACK, 6, waiting * 3);
                break;
            case "PREPARE_SIMPLE_REGISTERED_LETTER":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.PREPARE_SIMPLE_REGISTERED_LETTER, 16, waiting * 3);
                break;
            case "SEND_SIMPLE_REGISTERED_LETTER":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.SEND_SIMPLE_REGISTERED_LETTER, 16, waiting * 3);
                break;
            case "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS, 16, waiting * 3);
                break;
            case "PAYMENT":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.PAYMENT, 15, sharedSteps.getWorkFlowWait());
                break;
            case "PREPARE_ANALOG_DOMICILE":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.PREPARE_ANALOG_DOMICILE, 4, waiting * 5);
                break;
            case "COMPLETELY_UNREACHABLE":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.COMPLETELY_UNREACHABLE, 20, sharedSteps.getWorkFlowWait());
                break;
            case "COMPLETELY_UNREACHABLE_CREATION_REQUEST":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.COMPLETELY_UNREACHABLE_CREATION_REQUEST, 15, sharedSteps.getWorkFlowWait());
                break;
            case "PREPARE_DIGITAL_DOMICILE":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.PREPARE_DIGITAL_DOMICILE, 2, waiting * 3);
                break;
            case "SCHEDULE_DIGITAL_WORKFLOW":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.SCHEDULE_DIGITAL_WORKFLOW, 2,waiting * 3);
                break;
            case "SCHEDULE_REFINEMENT":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.SCHEDULE_REFINEMENT, 3, waiting);
                break;
            case "REFINEMENT":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.REFINEMENT, 4, waiting);
                break;
            case "REQUEST_REFUSED":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.REQUEST_REFUSED, 2, waiting);
                break;
            case "DIGITAL_DELIVERY_CREATION_REQUEST":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.DIGITAL_DELIVERY_CREATION_REQUEST, 5, waiting * 3);
                break;
            case "NOTIFICATION_CANCELLATION_REQUEST":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.NOTIFICATION_CANCELLATION_REQUEST, 2, waiting);
                break;
            case "NOTIFICATION_CANCELLED":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.NOTIFICATION_CANCELLED, 5, waiting * 3);
                break;
            default:
                throw new IllegalArgumentException();
        }
        return timelineElementWait;
    }




    private void checkTimelineElementEquality(String timelineEventCategory, TimelineElementV20 elementFromNotification, DataTest dataFromTest) {
        TimelineElementV20 elementFromTest = dataFromTest.getTimelineElement();
        TimelineElementDetailsV20 detailsFromNotification = elementFromNotification.getDetails();
        TimelineElementDetailsV20 detailsFromTest = elementFromTest.getDetails();
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
                    if(Objects.nonNull(detailsFromTest.getDeliveryDetailCode()))
                        Assertions.assertEquals(detailsFromNotification.getDeliveryDetailCode(), detailsFromTest.getDeliveryDetailCode());
                    if(Objects.nonNull(detailsFromTest.getPhysicalAddress()))
                        Assertions.assertEquals(detailsFromNotification.getPhysicalAddress(), detailsFromTest.getPhysicalAddress());
                    if(Objects.nonNull(detailsFromTest.getResponseStatus()))
                        Assertions.assertEquals(detailsFromNotification.getResponseStatus().getValue(), detailsFromTest.getResponseStatus().getValue());
                    if(Objects.nonNull(detailsFromTest.getDeliveryFailureCause())) {
                        List<String> failureCauses = Arrays.asList(detailsFromTest.getDeliveryFailureCause().split(" "));
                        Assertions.assertTrue(failureCauses.contains(elementFromNotification.getDetails().getDeliveryFailureCause()));
                    }
                }
                break;
            case "SEND_ANALOG_PROGRESS":
            case "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS":
                if (detailsFromTest != null) {
                    if(Objects.nonNull(elementFromTest.getLegalFactsIds())) {
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
            case "COMPLETELY_UNREACHABLE":
                if(Objects.nonNull(elementFromTest.getLegalFactsIds())) {
                    assert elementFromNotification.getLegalFactsIds() != null;
                    Assertions.assertEquals(elementFromNotification.getLegalFactsIds().size(), elementFromTest.getLegalFactsIds().size());
                }
                for (int i = 0; i < Objects.requireNonNull(elementFromNotification.getLegalFactsIds()).size(); i++) {
                    Assertions.assertEquals(elementFromNotification.getLegalFactsIds().get(i).getCategory(), elementFromTest.getLegalFactsIds().get(i).getCategory());
                    Assertions.assertNotNull(elementFromNotification.getLegalFactsIds().get(i).getKey());
                }
        }
    }


    private TimelineElementV20 getAndStoreTimeline(String timelineEventCategory, DataTest dataFromTest) {
        List<TimelineElementV20> timelineElementList;
        String iun;
        TimelineElementV20 timelineElement;

        if (timelineEventCategory.equals(TimelineElementCategoryV20.REQUEST_REFUSED.getValue())) {

            String requestId = sharedSteps.getNewNotificationResponse().getNotificationRequestId();
            byte[] decodedBytes = Base64.getDecoder().decode(requestId);
            iun = new String(decodedBytes);
            NewNotificationRequest newNotificationRequest = sharedSteps.getNotificationRequest();
            // get timeline from delivery-push
            NotificationHistoryResponse notificationHistory = this.pnPrivateDeliveryPushExternalClient.getNotificationHistory(iun, newNotificationRequest.getRecipients().size(), sharedSteps.getNotificationCreationDate());
            timelineElementList = notificationHistory.getTimeline();
            FullSentNotificationV20 fullSentNotification = new FullSentNotificationV20();

            fullSentNotification.setTimeline(timelineElementList);
            sharedSteps.setSentNotification(fullSentNotification);
        } else {
            // proceed with default flux
            iun = sharedSteps.getSentNotification().getIun();
            sharedSteps.setSentNotification(b2bClient.getSentNotification(iun));
            timelineElementList = sharedSteps.getSentNotification().getTimeline();
        }

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
        // calc how much time wait
        Integer pollingTime = dataFromTest != null ? dataFromTest.getPollingTime() : null;
        Integer numCheck = dataFromTest != null ? dataFromTest.getNumCheck() : null;
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
                    TimelineElementV20 timelineElement = getAndStoreTimeline(timelineEventCategory, dataFromTest);
                    List<TimelineElementV20> timelineElementList = sharedSteps.getSentNotification().getTimeline();


                    logger.info("NOTIFICATION_TIMELINE: " + timelineElementList);
                    Assertions.assertNotNull(timelineElementList);
                    Assertions.assertNotEquals(timelineElementList.size(), 0);
                    if (existCheck) {
                        Assertions.assertNotNull(timelineElement);
                    } else {
                        Assertions.assertNull(timelineElement);
                    }
                });
    }

    @And("viene verificato che il numero di elementi di timeline {string} sia di {long}")
    public void getTimelineElementListSize(String timelineEventCategory, Long size, @Transpose DataTest dataFromTest) {
        List<TimelineElementV20> timelineElementList = sharedSteps.getSentNotification().getTimeline();
        String iun;
        if (timelineEventCategory.equals(TimelineElementCategoryV20.REQUEST_REFUSED.getValue())) {

            String requestId = sharedSteps.getNewNotificationResponse().getNotificationRequestId();
            byte[] decodedBytes = Base64.getDecoder().decode(requestId);
            iun = new String(decodedBytes);
        } else {
            // proceed with default flux
            iun = sharedSteps.getSentNotification().getIun();
        }
        // get timeline event id
        String timelineEventId = sharedSteps.getTimelineEventId(timelineEventCategory, iun, dataFromTest);
        if (timelineEventCategory.equals(TimelineElementCategoryV20.SEND_ANALOG_PROGRESS.getValue())) {
            TimelineElementV20 timelineElementFromTest = dataFromTest.getTimelineElement();
            TimelineElementDetailsV20 timelineElementDetails = timelineElementFromTest.getDetails();

            Assertions.assertEquals(size, timelineElementList.stream().filter(elem -> elem.getElementId().startsWith(timelineEventId) && elem.getDetails().getDeliveryDetailCode().equals(timelineElementDetails.getDeliveryDetailCode())).count());
        } else {
            Assertions.assertEquals(size, timelineElementList.stream().filter(elem -> elem.getElementId().startsWith(timelineEventId)).count());
        }
    }


    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string}")
    public void readingEventUpToTheTimelineElementOfNotification(String timelineEventCategory) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        TimelineElementV20 timelineElement = null;

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


    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} e successivamente annullata")
    public void readingEventUpToTheTimelineElementOfNotificationAndCancel(String timelineEventCategory) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        TimelineElementV20 timelineElement = null;

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
            Assertions.assertDoesNotThrow(() ->
                    b2bClient.notificationCancellation(sharedSteps.getSentNotification().getIun())
            );

        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }



    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con deliveryDetailCode {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCode(String timelineEventCategory, String deliveryDetailCode) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);
        TimelineElementV20 timelineElement = null;

        for (int i = 0; i < timelineElementWait.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineElementWait.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());
            for (TimelineElementV20 element : sharedSteps.getSentNotification().getTimeline()) {

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

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con deliveryDetailCode {string} tentativo {string}" )
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCode(String timelineEventCategory, String deliveryDetailCode, String attempt) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);
        TimelineElementV20 timelineElement = null;

        for (int i = 0; i < timelineElementWait.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineElementWait.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());

            for (TimelineElementV20 element : sharedSteps.getSentNotification().getTimeline()) {

                if (element.getCategory().equals(timelineElementWait.getTimelineElementCategory()) && element.getElementId().contains(attempt) && element.getDetails().getDeliveryDetailCode().equals(deliveryDetailCode)) {
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


    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con deliveryDetailCode {string} e verifica data delay più {int}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCode(String timelineEventCategory, String deliveryDetailCode, int delay) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        TimelineElementV20 timelineElement = null;
        for (int i = 0; i < timelineElementWait.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineElementWait.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());

            for (TimelineElementV20 element : sharedSteps.getSentNotification().getTimeline()) {
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
            //Assertions.assertNotNull(timelineElement.getDetails().getAttachments());
            //Assertions.assertTrue(timelineElement.getDetails().getAttachments().size()>0);
            //Assertions.assertNotNull(timelineElement.getDetails().getAttachments().get(0).getDate());

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            Assertions.assertTrue(timelineElement.getDetails().getNotificationDate().format(fmt).equals(OffsetDateTime.now().plusDays(delay).format(fmt)));

            //Assertions.assertTrue(timelineElement.getDetails().getAttachments().get(0).getDate().format(fmt).equals(OffsetDateTime.now().plusDays(delay).format(fmt)));
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }


    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} e verifica data schedulingDate più {int}{string} per il destinatario {int}")
    public void readingEventUpToTheTimelineElementOfNotificationWithVerifySchedulingDate(String timelineEventCategory,  int delay, String tipoIncremento, int destinatario) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);
        TimelineElementV20 timelineElement = null;
        OffsetDateTime digitalDeliveryCreationRequestDate = null;

        for (int i = 0; i < timelineElementWait.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineElementWait.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());



            for (TimelineElementV20 element : sharedSteps.getSentNotification().getTimeline()) {
                if (element.getCategory().equals(timelineElementWait.getTimelineElementCategory()) && element.getDetails().getRecIndex().equals(destinatario)) {
                    timelineElement = element;
                    break;
                }
            }

            if (timelineElement != null) {
                break;
            }
        }
        try {

            //RECUPERO Data DeliveryCreationRequest
            for (TimelineElementV20 element : sharedSteps.getSentNotification().getTimeline()) {
                if (element.getCategory().getValue().equals("DIGITAL_DELIVERY_CREATION_REQUEST") && element.getDetails().getRecIndex().equals(destinatario)) {
                    digitalDeliveryCreationRequestDate = element.getTimestamp();
                    break;
                }
            }

            Assertions.assertNotNull(timelineElement);
            Assertions.assertNotNull(timelineElement.getDetails().getSchedulingDate());

            Assertions.assertNotNull(tipoIncremento);
            if ("d".equalsIgnoreCase(tipoIncremento)){
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                Assertions.assertTrue(timelineElement.getDetails().getSchedulingDate().format(fmt).equals(digitalDeliveryCreationRequestDate.plusDays(delay).format(fmt)));
            } else if ("m".equalsIgnoreCase(tipoIncremento)) {
                DateTimeFormatter fmt1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                Assertions.assertTrue(timelineElement.getDetails().getSchedulingDate().format(fmt1).equals(digitalDeliveryCreationRequestDate.plusMinutes(delay).format(fmt1)));
            }

        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con deliveryDetailCode {string} e verifica tipo DOC {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCodeVerifyTypeDoc(String timelineEventCategory, String deliveryDetailCode, String tipoDoc) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);
        TimelineElementV20 timelineElement = null;

        for (int i = 0; i < timelineElementWait.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineElementWait.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());

            for (TimelineElementV20 element : sharedSteps.getSentNotification().getTimeline()) {
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
            Assertions.assertNotNull(timelineElement.getDetails().getAttachments());
            Assertions.assertTrue(timelineElement.getDetails().getAttachments().size()>0);
            Assertions.assertNotNull(timelineElement.getDetails().getAttachments().get(0).getDocumentType());

           // Assertions.assertTrue(timelineElement.getDetails().getNotificationDate().format(fmt).equals(OffsetDateTime.now().plusDays(delay).format(fmt)));

            Assertions.assertTrue(timelineElement.getDetails().getAttachments().get(0).getDocumentType().equals(tipoDoc));
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con deliveryDetailCode {string} e verifica tipo DOC {string} tentativo {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCodeVerifyTypeDoc(String timelineEventCategory, String deliveryDetailCode, String tipoDoc,String attempt) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);
        TimelineElementV20 timelineElement = null;

        for (int i = 0; i < timelineElementWait.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineElementWait.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());

            for (TimelineElementV20 element : sharedSteps.getSentNotification().getTimeline()) {
                if (element.getCategory().equals(timelineElementWait.getTimelineElementCategory()) && element.getElementId().contains(attempt) && element.getDetails().getDeliveryDetailCode().equals(deliveryDetailCode)) {
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
            Assertions.assertNotNull(timelineElement.getDetails().getAttachments());
            Assertions.assertTrue(timelineElement.getDetails().getAttachments().size()>0);
            Assertions.assertNotNull(timelineElement.getDetails().getAttachments().get(0).getDocumentType());

            // Assertions.assertTrue(timelineElement.getDetails().getNotificationDate().format(fmt).equals(OffsetDateTime.now().plusDays(delay).format(fmt)));

            Assertions.assertTrue(timelineElement.getDetails().getAttachments().get(0).getDocumentType().equals(tipoDoc) || timelineElement.getDetails().getAttachments().get(0).getDocumentType().equals("Indagine"));
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }


    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con deliveryDetailCode {string} e deliveryFailureCause {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCodeDeliveryFailureCause(String timelineEventCategory, String deliveryDetailCode, String deliveryCause) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);
        TimelineElementV20 timelineElement = null;

        for (int i = 0; i < timelineElementWait.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineElementWait.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());

            for (TimelineElementV20 element : sharedSteps.getSentNotification().getTimeline()) {
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
            Assertions.assertTrue(timelineElement.getDetails().getDeliveryFailureCause().equals(deliveryCause));
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }


    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con deliveryDetailCode {string} e deliveryFailureCause {string} tentativo {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCodeDeliveryFailureCause(String timelineEventCategory, String deliveryDetailCode, String deliveryCause,String attempt) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        TimelineElementV20 timelineElement = null;

        for (int i = 0; i < timelineElementWait.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineElementWait.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());

            for (TimelineElementV20 element : sharedSteps.getSentNotification().getTimeline()) {

                if (element.getCategory().equals(timelineElementWait.getTimelineElementCategory())  && element.getElementId().contains(attempt) && element.getDetails().getDeliveryDetailCode().equals(deliveryDetailCode)) {
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
            Assertions.assertTrue(timelineElement.getDetails().getDeliveryFailureCause().equals(deliveryCause));
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @And("viene verificato il campo sendRequestId dell' evento di timeline {string}")
    public void vieneVerificatoCampoSendRequestIdEventoTimeline(String timelineEventCategory) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));

        TimelineElementV20 timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementWait.getTimelineElementCategory())).findAny().orElse(null);

        Assertions.assertNotNull(timelineElement);
        Assertions.assertNotNull(timelineElement.getDetails());
        Assertions.assertNotNull(timelineElement.getDetails().getSendRequestId());
        String sendRequestId = timelineElement.getDetails().getSendRequestId();

        TimelineElementV20 timelineElementRelative = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getElementId().equals(sendRequestId)).findAny().orElse(null);

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

        TimelineElementV20 timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementWait.getTimelineElementCategory())).findAny().orElse(null);

        System.out.println("TIMELINE_ELEMENT: " + timelineElement);
        Assertions.assertNotNull(timelineElement);
        Assertions.assertNotNull(timelineElement.getDetails());
        Assertions.assertEquals(timelineElement.getDetails().getServiceLevel(), level);
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} per l'utente {int}")
    public void readingEventUpToTheTimelineElementOfNotificationPerUtente(String timelineEventCategory, Integer destinatario) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        TimelineElementV20 timelineElement = null;

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


    @Then("esiste l'elemento di timeline della notifica {string} per l'utente {int}")
    public void verifyEventUpToTheTimelineElementOfNotificationPerUtente(String timelineEventCategory, Integer destinatario) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        TimelineElementV20 timelineElement = null;

        for (int i = 0; i < timelineElementWait.getNumCheck(); i++) {

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


    @Then("non vengono letti gli eventi fino all'elemento di timeline della notifica {string} per l'utente {int}")
    public void notReadingEventUpToTheTimelineElementOfNotificationPerUtente(String timelineEventCategory, Integer destinatario) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        TimelineElementV20 timelineElement = null;

        boolean pagamentoTrovato = false;

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
                pagamentoTrovato = true;
                break;
            }

        }
        if (!pagamentoTrovato){
            timelineElement = null;
        }
        try {
            Assertions.assertNull(timelineElement);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} verifica numero pagine AAR {int}")
    public void readingEventUpToTheTimelineElementOfNotificationPerVerificaNumPagine(String timelineEventCategory, Integer numPagine) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        TimelineElementV20 timelineElement = null;

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


        TimelineElementV20 timelineElement = null;
        sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
        for (TimelineElementV20 element : sharedSteps.getSentNotification().getTimeline()) {

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

        TimelineElementV20 timelineElement = null;
        sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
        for (TimelineElementV20 element : sharedSteps.getSentNotification().getTimeline()) {

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


    @Then("verifica generazione Atto opponibile senza la messa a disposizione in {string}")
    public void paVerifyGenerazioneLegalFact(String legalFactCategory){
        TimelineElementCategoryV20 timelineElementInternalCategory =  null;

        if (legalFactCategory.equalsIgnoreCase("DIGITAL_DELIVERY_CREATION_REQUEST"))
            timelineElementInternalCategory = TimelineElementCategoryV20.DIGITAL_DELIVERY_CREATION_REQUEST;

        TimelineElementV20 timelineElement = null;
        Assertions.assertNotNull(timelineElementInternalCategory);
        for (TimelineElementV20 element : sharedSteps.getSentNotification().getTimeline()) {
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

    @Then("{string} richiede il download dell'attestazione opponibile {string} con errore {string}")
    public void userDownloadLegalFactError(String user, String legalFactCategory,String statusCode) {
        try {
            sharedSteps.selectUser(user);
            downloadLegalFact(legalFactCategory, false, false, true, null);
        } catch (AssertionFailedError assertionFailedError) {
           //sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
            System.out.println(assertionFailedError.getCause().toString());
            System.out.println(assertionFailedError.getCause().getMessage().toString());
            System.out.println(assertionFailedError.getCause().getMessage().toString().substring(0, 3).equals(statusCode));
        }
    }



    private void downloadLegalFact(String legalFactCategory, boolean pa, boolean appIO, boolean webRecipient, String deliveryDetailCode) {
        try {
            Thread.sleep(sharedSteps.getWait());
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }


        TimelineElementCategoryV20 timelineElementInternalCategory;
        TimelineElementV20 timelineElement = null;
        LegalFactCategory category;
        switch (legalFactCategory) {
            case "SENDER_ACK":
                timelineElementInternalCategory = TimelineElementCategoryV20.REQUEST_ACCEPTED;
                category = LegalFactCategory.SENDER_ACK;
                break;
            case "RECIPIENT_ACCESS":
                timelineElementInternalCategory = TimelineElementCategoryV20.NOTIFICATION_VIEWED;
                category = LegalFactCategory.RECIPIENT_ACCESS;
                break;
            case "PEC_RECEIPT":
                timelineElementInternalCategory = TimelineElementCategoryV20.SEND_DIGITAL_PROGRESS;
                category = LegalFactCategory.PEC_RECEIPT;
                break;
            case "DIGITAL_DELIVERY":
                timelineElementInternalCategory = TimelineElementCategoryV20.DIGITAL_SUCCESS_WORKFLOW;
                category = LegalFactCategory.DIGITAL_DELIVERY;
                break;
            case "DIGITAL_DELIVERY_FAILURE":
                timelineElementInternalCategory = TimelineElementCategoryV20.DIGITAL_FAILURE_WORKFLOW;
                category = LegalFactCategory.DIGITAL_DELIVERY;
                break;
            case "SEND_ANALOG_PROGRESS":
                timelineElementInternalCategory = TimelineElementCategoryV20.SEND_ANALOG_PROGRESS;
                category = LegalFactCategory.ANALOG_DELIVERY;
                break;
            case "COMPLETELY_UNREACHABLE":
                timelineElementInternalCategory = TimelineElementCategoryV20.COMPLETELY_UNREACHABLE;
                category = LegalFactCategory.ANALOG_FAILURE_DELIVERY;
                break;
            default:
                throw new IllegalArgumentException();
        }

        for (TimelineElementV20 element : sharedSteps.getSentNotification().getTimeline()) {

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
            Assertions.assertNotNull(timelineElement);

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

    private void downloadLegalFactId(String legalFactCategory, boolean pa, boolean appIO, boolean webRecipient, String deliveryDetailCode) {
        try {
            Thread.sleep(sharedSteps.getWait());
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }

        TimelineElementCategoryV20 timelineElementInternalCategory;
        TimelineElementV20 timelineElement = null;
        LegalFactCategory category;
        switch (legalFactCategory) {
            case "SENDER_ACK":
                timelineElementInternalCategory = TimelineElementCategoryV20.REQUEST_ACCEPTED;
                category = LegalFactCategory.SENDER_ACK;
                break;
            case "RECIPIENT_ACCESS":
                timelineElementInternalCategory = TimelineElementCategoryV20.NOTIFICATION_VIEWED;
                category = LegalFactCategory.RECIPIENT_ACCESS;
                break;
            case "PEC_RECEIPT":
                timelineElementInternalCategory = TimelineElementCategoryV20.SEND_DIGITAL_PROGRESS;
                category = LegalFactCategory.PEC_RECEIPT;
                break;
            case "DIGITAL_DELIVERY":
                timelineElementInternalCategory = TimelineElementCategoryV20.DIGITAL_SUCCESS_WORKFLOW;
                category = LegalFactCategory.DIGITAL_DELIVERY;
                break;
            case "DIGITAL_DELIVERY_FAILURE":
                timelineElementInternalCategory = TimelineElementCategoryV20.DIGITAL_FAILURE_WORKFLOW;
                category = LegalFactCategory.DIGITAL_DELIVERY;
                break;
            case "SEND_ANALOG_PROGRESS":
                timelineElementInternalCategory = TimelineElementCategoryV20.SEND_ANALOG_PROGRESS;
                category = LegalFactCategory.ANALOG_DELIVERY;
                break;
            case "COMPLETELY_UNREACHABLE":
                timelineElementInternalCategory = TimelineElementCategoryV20.COMPLETELY_UNREACHABLE;

                category = LegalFactCategory.ANALOG_FAILURE_DELIVERY;
                break;
            default:
                throw new IllegalArgumentException();
        }


        for (TimelineElementV20 element : sharedSteps.getSentNotification().getTimeline()) {
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
                Assertions.assertDoesNotThrow(() -> this.b2bClient.getDownloadLegalFact(sharedSteps.getSentNotification().getIun(),  finalKeySearch));
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

    @Then("si verifica che la notifica non abbia lo stato {string}")
    public void checksNotificationNotHaveStatus(String status) {
        try {
            Thread.sleep(sharedSteps.getWait() * 10);
        } catch (InterruptedException interruptedException) {
            logger.error("InterruptedException error");
        }
        sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
        try {
            Assertions.assertNull(sharedSteps.getSentNotification().getNotificationStatusHistory().stream().filter(elem -> elem.getStatus().equals(NotificationStatus.valueOf(status))).findAny().orElse(null));
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


        TimelineElementCategoryV20 timelineElementInternalCategory;
        TimelineElementV20 timelineElement;
        LegalFactCategory category;
        switch (legalFactCategory) {
            case "SENDER_ACK":
                timelineElementInternalCategory = TimelineElementCategoryV20.REQUEST_ACCEPTED;
                timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().orElse(null);
                category = LegalFactCategory.SENDER_ACK;
                break;
            case "RECIPIENT_ACCESS":
                timelineElementInternalCategory = TimelineElementCategoryV20.NOTIFICATION_VIEWED;

                timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().orElse(null);
                category = LegalFactCategory.RECIPIENT_ACCESS;
                break;
            case "PEC_RECEIPT":
                timelineElementInternalCategory = TimelineElementCategoryV20.SEND_DIGITAL_PROGRESS;
                timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().orElse(null);
                category = LegalFactCategory.PEC_RECEIPT;
                break;
            case "DIGITAL_DELIVERY":
                timelineElementInternalCategory = TimelineElementCategoryV20.DIGITAL_SUCCESS_WORKFLOW;
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
        paymentEventPagoPa.setPaymentDate(fmt.format(now().atZoneSameInstant(ZoneId.of("UTC"))));
        paymentEventPagoPa.setAmount(notificationPrice.getAmount());

        List<PaymentEventPagoPa> paymentEventPagoPaList = new LinkedList<>();
        paymentEventPagoPaList.add(paymentEventPagoPa);

        eventsRequestPagoPa.setEvents(paymentEventPagoPaList);

        b2bClient.paymentEventsRequestPagoPa(eventsRequestPagoPa);
    }

    @And("l'avviso pagopa viene pagato correttamente dall'utente {int}")
    public void laNotificaVienePagataMulti(Integer utente) {
        NotificationPriceResponse notificationPrice = this.b2bClient.getNotificationPrice(sharedSteps.getSentNotification().getRecipients().get(0).getPayment().getCreditorTaxId(),
                sharedSteps.getSentNotification().getRecipients().get(utente).getPayment().getNoticeCode());

        PaymentEventsRequestPagoPa eventsRequestPagoPa = new PaymentEventsRequestPagoPa();

        PaymentEventPagoPa paymentEventPagoPa = new PaymentEventPagoPa();
        paymentEventPagoPa.setNoticeCode(sharedSteps.getSentNotification().getRecipients().get(utente).getPayment().getNoticeCode());
        paymentEventPagoPa.setCreditorTaxId(sharedSteps.getSentNotification().getRecipients().get(0).getPayment().getCreditorTaxId());
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        paymentEventPagoPa.setPaymentDate(fmt.format(OffsetDateTime.now()));
        paymentEventPagoPa.setAmount(notificationPrice.getAmount());

        List<PaymentEventPagoPa> paymentEventPagoPaList = new LinkedList<>();
        paymentEventPagoPaList.add(paymentEventPagoPa);

        eventsRequestPagoPa.setEvents(paymentEventPagoPaList);

        b2bClient.paymentEventsRequestPagoPa(eventsRequestPagoPa);
    }

    @And("viene rifiutato il pagamento dell'avviso pagopa  dall'utente {int}")
    public void laNotificaVieneRifiutatoIlPagamentoMulti(Integer utente) {
        NotificationPriceResponse notificationPrice = this.b2bClient.getNotificationPrice(sharedSteps.getSentNotification().getRecipients().get(0).getPayment().getCreditorTaxId(),
                sharedSteps.getSentNotification().getRecipients().get(utente).getPayment().getNoticeCode());

        PaymentEventsRequestPagoPa eventsRequestPagoPa = new PaymentEventsRequestPagoPa();

        PaymentEventPagoPa paymentEventPagoPa = new PaymentEventPagoPa();
        paymentEventPagoPa.setNoticeCode(sharedSteps.getSentNotification().getRecipients().get(utente).getPayment().getNoticeCode());
        paymentEventPagoPa.setCreditorTaxId(sharedSteps.getSentNotification().getRecipients().get(0).getPayment().getCreditorTaxId());
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        paymentEventPagoPa.setPaymentDate(fmt.format(OffsetDateTime.now()));
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

    @Then("sono presenti {int} attestazioni opponibili RECIPIENT_ACCESS")
    public void sonoPresentiAttestazioniOpponibili(int number) {

        TimelineElementCategoryV20 timelineElementInternalCategory = TimelineElementCategoryV20.NOTIFICATION_VIEWED;
        boolean findElement = false;
        for (int i = 0; i < 16; i++) {
            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            List<TimelineElementV20> timeline = sharedSteps.getSentNotification().getTimeline();
            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());

            int count = 0;
            for (TimelineElementV20 element : timeline) {

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

        TimelineElementV20 timelineElement = null;

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
        TimelineElementV20 timelineElement = null;

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

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con responseStatus {string} e digitalAddressSource {string}")
    public void vengonoLettiGliEventiFinoAllElementoDiTimelineDellaNotificaConResponseStatusAndDigitalAddressSource(String timelineEventCategory, String code, String digitalAddressSource) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        TimelineElementV20 timelineElement = null;

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
            Assertions.assertEquals(timelineElement.getDetails().getDigitalAddressSource().getValue(), digitalAddressSource);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("viene verificato che nell'elemento di timeline della notifica {string} siano configurati i campi municipalityDetails e foreignState")
    public void vieneVerificatoCheElementoTimelineSianoConfiguratiCampiMunicipalityDetailsForeignState(String timelineEventCategory) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        TimelineElementV20 timelineElement = null;

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

        TimelineElementV20 timelineElement = null;

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

        TimelineElementV20 timelineElement = null;

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

        TimelineElementV20 timelineElement = null;


        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(6000);
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

        TimelineElementV20 timelineElement = null;

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

    @Then("si attende il corretto pagamento della notifica dell'utente {int}")
    public void siAttendeIlCorrettoPagamentoDellaNotifica(Integer utente) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory("PAYMENT");

        TimelineElementV20 timelineElement = null;

        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(60000);
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));

            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());

            timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementWait.getTimelineElementCategory())).findAny().orElse(null);
            if (timelineElement != null && timelineElement.getDetails().getRecIndex().equals(utente)) {
                break;
            }
        }
        Assertions.assertNotNull(timelineElement);

    }

    @Then("si attende il non corretto pagamento della notifica dell'utente {int}")
    public void siAttendeIlNonCorrettoPagamentoDellaNotifica(Integer utente) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory("PAYMENT");

        TimelineElementV20 timelineElement = null;

        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(60000);
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));

            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());

            timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementWait.getTimelineElementCategory())).findAny().orElse(null);
            if (timelineElement != null && timelineElement.getDetails().getRecIndex().equals(utente)) {
                break;
            }
        }
        Assertions.assertNull(timelineElement);

    }


    @Then("viene verificato che nell'elemento di timeline della notifica {string} e' presente il campo Digital Address di piattaforma")
    public void vieneVerificatoCheElementoTimelineSianoConfiguratoCampoDigitalAddressPiattaforma(String timelineEventCategory) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        TimelineElementV20 timelineElement = null;


        sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
        timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementWait.getTimelineElementCategory()) && elem.getElementId().contains("SOURCE_PLATFORM")).findAny().orElse(null);
        try {
            logger.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNotNull(timelineElement);
            Assertions.assertNotNull(timelineElement.getDetails().getDigitalAddress());
            Assertions.assertFalse("DSRDNI00A01A225I@pec.pagopa.it".equalsIgnoreCase(timelineElement.getDetails().getDigitalAddress().getAddress()));
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }

    }

    @Then("viene verificato che nell'elemento di timeline della notifica {string} sia presente il campo Digital Address")
    public void vieneVerificatoCheElementoTimelineSianoConfiguratoCampoDigitalAddress(String timelineEventCategory) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        TimelineElementV20 timelineElement = null;

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

    @Then("viene verificato che l'elemento di timeline {string} esista")
    public void vieneVerificatoElementoTimeline(String timelineEventCategory, @Transpose DataTest dataFromTest) {
        boolean mustLoadTimeline = dataFromTest != null ? dataFromTest.getLoadTimeline() : false;
        if (mustLoadTimeline) {
            loadTimeline(timelineEventCategory, true, dataFromTest);
        }
        try {

            TimelineElementV20 timelineElement = sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataFromTest);

            logger.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNotNull(timelineElement);
            if (dataFromTest != null && dataFromTest.getTimelineElement() != null) {
                checkTimelineElementEquality(timelineEventCategory, timelineElement, dataFromTest);
            }
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @And("viene verificato che l'elemento di timeline {string} non esista")
    public void vieneVerificatoCheElementoTimelineNonEsista(String timelineEventCategory, @Transpose DataTest dataFromTest) {
        loadTimeline(timelineEventCategory, false, dataFromTest);

        TimelineElementV20 timelineElement = sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataFromTest);

        try {
            logger.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNull(timelineElement);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @And("viene schedulato il perfezionamento per decorrenza termini per il caso {string}")
    public void vieneSchedulatoIlPerfezionamento(String timelineCategory, @Transpose DataTest dataFromTest) {

        TimelineElementV20 timelineElement = sharedSteps.getTimelineElementByEventId(TimelineElementCategoryV20.SCHEDULE_REFINEMENT.getValue(), dataFromTest);

        TimelineElementV20 timelineElementForDateCalculation = null;
        if (timelineCategory.equals(TimelineElementCategoryV20.DIGITAL_SUCCESS_WORKFLOW.getValue())) {
            timelineElementForDateCalculation = sharedSteps.getTimelineElementByEventId(TimelineElementCategoryV20.SEND_DIGITAL_FEEDBACK.getValue(), dataFromTest);
        } else if (timelineCategory.equals(TimelineElementCategoryV20.DIGITAL_FAILURE_WORKFLOW.getValue())) {
            timelineElementForDateCalculation = sharedSteps.getTimelineElementByEventId(TimelineElementCategoryV20.DIGITAL_FAILURE_WORKFLOW.getValue(), dataFromTest);
        }  else if (timelineCategory.equals(TimelineElementCategoryV20.ANALOG_SUCCESS_WORKFLOW.getValue())) {
            timelineElementForDateCalculation = sharedSteps.getTimelineElementByEventId(TimelineElementCategoryV20.SEND_ANALOG_FEEDBACK.getValue(), dataFromTest);
        } else if (timelineCategory.equals(TimelineElementCategoryV20.ANALOG_FAILURE_WORKFLOW.getValue())) {
            timelineElementForDateCalculation = sharedSteps.getTimelineElementByEventId(TimelineElementCategoryV20.SEND_ANALOG_FEEDBACK.getValue(), dataFromTest);

        }

        Assertions.assertNotNull(timelineElementForDateCalculation);

        OffsetDateTime notificationDate = null;
        Duration schedulingDaysRefinement = null;

        if (timelineCategory.equals(TimelineElementCategoryV20.DIGITAL_SUCCESS_WORKFLOW.getValue())) {
            notificationDate = timelineElementForDateCalculation.getDetails().getNotificationDate();
            schedulingDaysRefinement = sharedSteps.getSchedulingDaysSuccessDigitalRefinement();
        } else if (timelineCategory.equals(TimelineElementCategoryV20.DIGITAL_FAILURE_WORKFLOW.getValue())) {
            notificationDate = timelineElementForDateCalculation.getTimestamp();
            schedulingDaysRefinement = sharedSteps.getSchedulingDaysFailureDigitalRefinement();
        } else if (timelineCategory.equals(TimelineElementCategoryV20.ANALOG_SUCCESS_WORKFLOW.getValue())) {
            notificationDate = timelineElementForDateCalculation.getTimestamp();
            schedulingDaysRefinement = sharedSteps.getSchedulingDaysSuccessAnalogRefinement();
        } else if (timelineCategory.equals(TimelineElementCategoryV20.ANALOG_FAILURE_WORKFLOW.getValue())) {
            notificationDate = timelineElementForDateCalculation.getDetails().getNotificationDate();
            schedulingDaysRefinement = sharedSteps.getSchedulingDaysFailureAnalogRefinement();
        }

        OffsetDateTime schedulingDate = notificationDate.plus(schedulingDaysRefinement);
        Integer hour = schedulingDate.getHour();
        Integer minutes = schedulingDate.getMinute();
        if ((hour == 21 && minutes > 0) || hour > 21) {
            Duration timeToAddInNonVisibilityTimeCase = sharedSteps.getTimeToAddInNonVisibilityTimeCase();
            schedulingDate = schedulingDate.plus(timeToAddInNonVisibilityTimeCase);
        }
        Assertions.assertEquals(timelineElement.getDetails().getSchedulingDate(), schedulingDate);
    }

    @And("si attende che sia presente il perfezionamento per decorrenza termini")
    public void siAttendePresenzaPerfezionamentoDecorrenzaTermini(@Transpose DataTest dataFromTest) throws InterruptedException {
        String iun = sharedSteps.getSentNotification().getIun();
        if (dataFromTest != null && dataFromTest.getTimelineElement() != null) {
            TimelineElementV20 timelineElement = sharedSteps.getTimelineElementByEventId(TimelineElementCategoryV20.SCHEDULE_REFINEMENT.getValue(), dataFromTest);

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
        if (dataFromTest != null && dataFromTest.getTimelineElement() != null) {

            TimelineElementV20 timelineElement = sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataFromTest);

            OffsetDateTime firstSend = timelineElement.getTimestamp();
            Duration secondNotificationWorkflowWaitingTime = sharedSteps.getSecondNotificationWorkflowWaitingTime();
            OffsetDateTime nextSend = firstSend.plus(secondNotificationWorkflowWaitingTime);
            OffsetDateTime currentDate = now().atZoneSameInstant(ZoneId.of("UTC")).toOffsetDateTime();
            Long remainingTime = ChronoUnit.MILLIS.between(currentDate, nextSend);
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
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        TimelineElementV20 timelineElement = null;

        for (int i = 0; i < timelineElementWait.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineElementWait.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());

            for (TimelineElementV20 element : sharedSteps.getSentNotification().getTimeline()) {

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
            Assertions.assertNotNull(timelineElement.getDetails().getPhysicalAddress());
            Assertions.assertTrue(timelineElement.getDetails().getPhysicalAddress().getAddress().matches("^[A-Z0-9_\\.\\-:@' \\[\\] ]*$"));
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @And("vengono letti gli eventi fino all'elemento di timeline {string} della notifica per il destinatario {int}, con deliveryDetailCode {string} e con deliveryFailureCause {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithDeliveryDetailCodeAndDeliveryFailureCause(String timelineEventCategory, Integer recIndex, String deliveryDetailCode, String deliveryFailureCause) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        TimelineElementV20 timelineElement = null;

        List<String> failureCauses = Arrays.asList(deliveryFailureCause.split(" "));

        for (int i = 0; i < timelineElementWait.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineElementWait.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());

            for (TimelineElementV20 element : sharedSteps.getSentNotification().getTimeline()) {

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

        TimelineElementV20 timelineElement = null;

        for (int i = 0; i < timelineElementWait.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineElementWait.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());

            for (TimelineElementV20 element : sharedSteps.getSentNotification().getTimeline()) {

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

        TimelineElementV20 timelineElementCategory = getAndStoreTimeline(timelineEventCategory, dataFromTest);
        TimelineElementV20 timelineElementSendCourtesyMessage = getAndStoreTimeline("SEND_COURTESY_MESSAGE", dataFromTest);


        Duration waitingForReadCourtesyMessage = sharedSteps.getWaitingForReadCourtesyMessage();

        OffsetDateTime timestampEventCategory = timelineElementCategory.getTimestamp();
        OffsetDateTime timestampEventSendCourtesyMessage = timelineElementSendCourtesyMessage.getTimestamp();
        OffsetDateTime timestampEventSendCourtesyMessageWithWaitingTime = timestampEventSendCourtesyMessage.plus(waitingForReadCourtesyMessage);

        Boolean test = timestampEventCategory.isEqual(timestampEventSendCourtesyMessageWithWaitingTime) || timestampEventCategory.isAfter(timestampEventSendCourtesyMessageWithWaitingTime);

        logger.info("timestamp " + timelineEventCategory + ": " + timestampEventCategory);
        logger.info("timestamp SEND_COURTESY_MESSAGE ( +" + waitingForReadCourtesyMessage + " minutes): " + timestampEventSendCourtesyMessageWithWaitingTime);
        logger.info("timestamp " + timelineEventCategory + " is after or equal timestamp SEND_COURTESY_MESSAGE?: " + test);

        Assertions.assertTrue(test);
    }


    @And("download attestazione opponibile AAR")
    public void downloadLegalFactIdAAR() {
        try {
            Thread.sleep(sharedSteps.getWait());
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }

        TimelineElementCategoryV20 timelineElementInternalCategory= TimelineElementCategoryV20.AAR_GENERATION;
        TimelineElementV20 timelineElement = null;

        for (TimelineElementV20 element : sharedSteps.getSentNotification().getTimeline()) {

            if (element.getCategory().equals(timelineElementInternalCategory)) {
                timelineElement = element;
                break;
            }
        }

        Assertions.assertNotNull(timelineElement);
        String keySearch = null;
        if (timelineElement.getDetails().getGeneratedAarUrl() != null && !timelineElement.getDetails().getGeneratedAarUrl().isEmpty()) {

            if (timelineElement.getDetails().getGeneratedAarUrl().contains("PN_AAR")) {
                keySearch = timelineElement.getDetails().getGeneratedAarUrl().substring(timelineElement.getDetails().getGeneratedAarUrl().indexOf("PN_AAR"));
            }

            String finalKeySearch = keySearch;
            try {
                Assertions.assertDoesNotThrow(() -> this.b2bClient.getDownloadLegalFact(sharedSteps.getSentNotification().getIun(), finalKeySearch));
            } catch (AssertionFailedError assertionFailedError) {
                sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
            }
        }
    }


    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} e verifica indirizzo secondo tentativo {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithVerifyPhysicalAddress(String timelineEventCategory, String attempt) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);
        TimelineElementV20 timelineElement = null;

        for (int i = 0; i < timelineElementWait.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineElementWait.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());

            for (TimelineElementV20 element : sharedSteps.getSentNotification().getTimeline()) {

                if (element.getCategory().equals(timelineElementWait.getTimelineElementCategory()) && element.getElementId().contains(attempt)) {
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
            Assertions.assertNotNull(timelineElement.getDetails().getPhysicalAddress());
            Assertions.assertTrue(timelineElement.getDetails().getPhysicalAddress().getAddress().matches("^[A-Z0-9_\\.\\-:@' \\[\\] ]*$"));

        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("viene verificato che nell'elemento di timeline della notifica {string} sia presente il campo Digital Address da National Registry")
    public void vieneVerificatoCheElementoTimelineSianoConfiguratoCampoDigitalAddressNationalRegistry(String timelineEventCategory) {

        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        TimelineElementV20 timelineElement = null;


        for (int i = 0; i < timelineElementWait.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineElementWait.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));

            timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementWait.getTimelineElementCategory())).findAny().orElse(null);
            if (timelineElement != null) {
                break;
            }
        }
        try {
            logger.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNotNull(timelineElement);
            Assertions.assertNotNull(timelineElement.getDetails().getDigitalAddress());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }

    }

    //Notifica Annullata

    //Annullamento Notifica
    @And("la notifica può essere annullata dal sistema tramite codice IUN")
    public void notificationCanBeCanceledWithIUN() {

        Assertions.assertDoesNotThrow(() -> {
            RequestStatus resp =  Assertions.assertDoesNotThrow(() ->
                    this.b2bClient.notificationCancellation(sharedSteps.getSentNotification().getIun()));

            Assertions.assertNotNull(resp);
            Assertions.assertNotNull(resp.getDetails());
            Assertions.assertTrue(resp.getDetails().size()>0);
            Assertions.assertTrue("NOTIFICATION_CANCELLATION_ACCEPTED".equalsIgnoreCase(resp.getDetails().get(0).getCode()));

        });
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

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} e verifica data schedulingDate più {int}{string} per il destinatario {int} rispetto ell'evento in timeline {string}")
    public void readingEventUpToTheTimelineElementOfNotificationWithVerifySchedulingDate(String timelineEventCategory,  int delay, String tipoIncremento, int destinatario, String evento) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);
        TimelineElementV20 timelineElement = null;
        OffsetDateTime digitalDeliveryCreationRequestDate = null;

        for (int i = 0; i < timelineElementWait.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineElementWait.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());



            for (TimelineElementV20 element : sharedSteps.getSentNotification().getTimeline()) {
                if (element.getCategory().equals(timelineElementWait.getTimelineElementCategory()) && element.getDetails().getRecIndex().equals(destinatario)) {
                    timelineElement = element;
                    break;
                }
            }

            if (timelineElement != null) {
                break;
            }
        }
        try {

            //RECUPERO Data DeliveryCreationRequest
            for (TimelineElementV20 element : sharedSteps.getSentNotification().getTimeline()) {
                if (element.getCategory().getValue().equals("DIGITAL_DELIVERY_CREATION_REQUEST") && element.getDetails().getRecIndex().equals(destinatario) && evento.equalsIgnoreCase("DIGITAL_DELIVERY_CREATION_REQUEST")) {
                    digitalDeliveryCreationRequestDate = element.getTimestamp();
                    break;
                } else if (element.getCategory().getValue().equals("SEND_DIGITAL_FEEDBACK") && element.getDetails().getRecIndex().equals(destinatario) && evento.equalsIgnoreCase("SEND_DIGITAL_FEEDBACK")) {
                    if ("OK".equalsIgnoreCase(element.getDetails().getResponseStatus().getValue())) {
                        digitalDeliveryCreationRequestDate = element.getDetails().getNotificationDate();
                        break;
                    }
                }
            }

            Assertions.assertNotNull(timelineElement);
            Assertions.assertNotNull(timelineElement.getDetails().getSchedulingDate());

            Assertions.assertNotNull(tipoIncremento);
            if ("d".equalsIgnoreCase(tipoIncremento)){
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                Assertions.assertTrue(timelineElement.getDetails().getSchedulingDate().format(fmt).equals(digitalDeliveryCreationRequestDate.plusDays(delay).format(fmt)));
            } else if ("m".equalsIgnoreCase(tipoIncremento)) {
                DateTimeFormatter fmt1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                Assertions.assertTrue(timelineElement.getDetails().getSchedulingDate().format(fmt1).equals(digitalDeliveryCreationRequestDate.plusMinutes(delay).format(fmt1)));
            }

        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }




}
