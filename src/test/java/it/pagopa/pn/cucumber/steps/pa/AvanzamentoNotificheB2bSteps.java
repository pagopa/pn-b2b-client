package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.service.*;
import it.pagopa.pn.client.b2b.pa.service.impl.PnExternalServiceClientImpl;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.NotificationHistoryResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.NotificationProcessCostResponse;
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

public class AvanzamentoNotificheB2bSteps {


    private final IPnPaB2bClient b2bClient;
    private final SharedSteps sharedSteps;
    private final IPnAppIOB2bClient appIOB2bClient;
    private final IPnWebRecipientClient webRecipientClient;
    private final PnExternalServiceClientImpl externalClient;
    private final IPnWebUserAttributesClient webUserAttributesClient;
    private final IPnIoUserAttributerExternaClient ioUserAttributerExternaClient;
    private final IPnPrivateDeliveryPushExternalClient pnPrivateDeliveryPushExternalClient;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private HttpStatusCodeException notificationError;

    @Autowired
    public AvanzamentoNotificheB2bSteps(SharedSteps sharedSteps, IPnAppIOB2bClient appIOB2bClient,
                                        IPnWebUserAttributesClient webUserAttributesClient, IPnIoUserAttributerExternaClient ioUserAttributerExternaClient, IPnPrivateDeliveryPushExternalClient pnPrivateDeliveryPushExternalClient) {
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
                numCheck = 10;
                waiting = waiting * 3;
                notificationInternalStatus = NotificationStatus.DELIVERED;
                break;
            case "CANCELLED":
                notificationInternalStatus = NotificationStatus.CANCELLED;
                break;
            case "EFFECTIVE_DATE":
                numCheck = 8;
                waiting = waiting * 4;
                notificationInternalStatus = NotificationStatus.EFFECTIVE_DATE;
                break;
            case "COMPLETELY_UNREACHABLE":
                numCheck = 8;
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


    @Then("vengono letti gli eventi fino allo stato della notifica {string} V1")
    public void readingEventUpToTheStatusOfNotificationV1(String status) {
        Integer numCheck = 10;
        Integer waiting = sharedSteps.getWorkFlowWait();

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationStatus notificationInternalStatus;
        switch (status) {
            case "ACCEPTED":
                numCheck = 2;
                notificationInternalStatus = it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationStatus.ACCEPTED;
                break;
            case "DELIVERING":
                numCheck = 2;
                waiting = waiting * 4;
                notificationInternalStatus = it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationStatus.DELIVERING;
                break;
            case "DELIVERED":
                numCheck = 10;
                waiting = waiting * 3;
                notificationInternalStatus = it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationStatus.DELIVERED;
                break;
            case "CANCELLED":
                notificationInternalStatus = it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationStatus.CANCELLED;
                break;
            case "EFFECTIVE_DATE":
                numCheck = 8;
                waiting = waiting * 4;
                notificationInternalStatus = it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationStatus.EFFECTIVE_DATE;
                break;
            case "COMPLETELY_UNREACHABLE":
                numCheck = 8;
                waiting = waiting * 4;
                notificationInternalStatus = it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationStatus.UNREACHABLE;
                break;
            case "VIEWED":
                numCheck = 4;
                waiting = waiting * 4;
                notificationInternalStatus = it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationStatus.VIEWED;
                break;
            case "PAID":
                notificationInternalStatus = it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationStatus.PAID;
                break;
            case "IN_VALIDATION":
                notificationInternalStatus = it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationStatus.IN_VALIDATION;
                break;
            default:
                throw new IllegalArgumentException();
        }

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationStatusHistoryElement notificationStatusHistoryElementV1 = null;
        NotificationStatusHistoryElement notificationStatusHistoryElement = null;

        for (int i = 0; i < numCheck; i++) {

            if (sharedSteps.getSentNotificationV1()!= null) {
                sharedSteps.setSentNotificationV1(b2bClient.getSentNotificationV1(sharedSteps.getSentNotificationV1().getIun()));

                logger.info("NOTIFICATION_STATUS_HISTORY v1: " + sharedSteps.getSentNotificationV1().getNotificationStatusHistory());

                notificationStatusHistoryElementV1 = sharedSteps.getSentNotificationV1().getNotificationStatusHistory().stream().filter(elem -> elem.getStatus().equals(notificationInternalStatus)).findAny().orElse(null);

            }
            else if (sharedSteps.getSentNotification()!= null){
                sharedSteps.setSentNotificationV1(b2bClient.getSentNotificationV1(sharedSteps.getSentNotification().getIun()));

                logger.info("NOTIFICATION_STATUS_HISTORY v1: " + sharedSteps.getSentNotificationV1().getNotificationStatusHistory());

                notificationStatusHistoryElementV1 = sharedSteps.getSentNotificationV1().getNotificationStatusHistory().stream().filter(elem -> elem.getStatus().equals(notificationInternalStatus)).findAny().orElse(null);

            }


            if (notificationStatusHistoryElement != null || notificationStatusHistoryElementV1!=null) {
                break;
            }
            try {
                Thread.sleep(waiting);
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
        }
        try {
            if (notificationStatusHistoryElement != null){
                Assertions.assertNotNull(notificationStatusHistoryElement);
            }
            if (notificationStatusHistoryElementV1 != null){
                Assertions.assertNotNull(notificationStatusHistoryElementV1);
            }

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
                numCheck = 10;
                waiting = waiting * 3;
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

    private TimelineElementWait getTimelineElementCategoryShortAnnullamento(String timelineEventCategory) {
        Integer waiting = sharedSteps.getWorkFlowWait();
        TimelineElementWait timelineElementWait;
        switch (timelineEventCategory) {
            case "REQUEST_ACCEPTED":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.REQUEST_ACCEPTED, 2, waiting);
                break;
            case "NOTIFICATION_CANCELLATION_REQUEST":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.NOTIFICATION_CANCELLATION_REQUEST, 2, 11000);
                break;
            case "NOTIFICATION_CANCELLED":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.NOTIFICATION_CANCELLED, 5, 11000 * 3);
                break;
            default:
                throw new IllegalArgumentException();
        }
        return timelineElementWait;
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
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.SEND_COURTESY_MESSAGE, 10, sharedSteps.getWorkFlowWait());
                break;
            case "DIGITAL_SUCCESS_WORKFLOW":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.DIGITAL_SUCCESS_WORKFLOW, 3, waiting * 3);
                break;
            case "DIGITAL_FAILURE_WORKFLOW":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.DIGITAL_FAILURE_WORKFLOW, 8, waiting * 3);
                break;
            case "NOT_HANDLED":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.NOT_HANDLED, 8, sharedSteps.getWorkFlowWait());
                break;
            case "SEND_DIGITAL_FEEDBACK":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.SEND_DIGITAL_FEEDBACK, 4, waiting * 3);
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
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.ANALOG_SUCCESS_WORKFLOW, 8, waiting * 4);
                break;
            case "ANALOG_FAILURE_WORKFLOW":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.ANALOG_FAILURE_WORKFLOW, 8, sharedSteps.getWorkFlowWait());
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
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.PREPARE_SIMPLE_REGISTERED_LETTER, 9, waiting * 3);
                break;
            case "SEND_SIMPLE_REGISTERED_LETTER":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.SEND_SIMPLE_REGISTERED_LETTER, 9, waiting * 3);
                break;
            case "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS, 10, waiting * 3);
                break;
            case "PAYMENT":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.PAYMENT, 8, sharedSteps.getWorkFlowWait());
                break;
            case "PREPARE_ANALOG_DOMICILE":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.PREPARE_ANALOG_DOMICILE, 4, waiting * 5);
                break;
            case "PREPARE_ANALOG_DOMICILE_FAILURE":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.PREPARE_ANALOG_DOMICILE_FAILURE,10, sharedSteps.getWorkFlowWait());
                break;
            case "COMPLETELY_UNREACHABLE":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.COMPLETELY_UNREACHABLE, 10, sharedSteps.getWorkFlowWait());
                break;
            case "COMPLETELY_UNREACHABLE_CREATION_REQUEST":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.COMPLETELY_UNREACHABLE_CREATION_REQUEST, 10, sharedSteps.getWorkFlowWait());
                break;
            case "PREPARE_DIGITAL_DOMICILE":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.PREPARE_DIGITAL_DOMICILE, 2, waiting * 3);
                break;
            case "SCHEDULE_DIGITAL_WORKFLOW":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.SCHEDULE_DIGITAL_WORKFLOW, 2,waiting * 3);
                break;
            case "SCHEDULE_REFINEMENT":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.SCHEDULE_REFINEMENT, 8, waiting);
                break;
            case "REFINEMENT":
                timelineElementWait = new TimelineElementWait(TimelineElementCategoryV20.REFINEMENT, 15, waiting);
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


    private TimelineElementWait getTimelineElementCategoryV1(String timelineEventCategory) {
        Integer waiting = sharedSteps.getWorkFlowWait();
        TimelineElementWait timelineElementWait;
        switch (timelineEventCategory) {
            case "REQUEST_ACCEPTED":

                timelineElementWait = new TimelineElementWait( it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.REQUEST_ACCEPTED, 2, waiting);
                break;
            case "AAR_GENERATION":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.AAR_GENERATION, 2, waiting * 2);
                break;
            case "GET_ADDRESS":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.GET_ADDRESS, 2, waiting * 2);
                break;
            case "SEND_DIGITAL_DOMICILE":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.SEND_DIGITAL_DOMICILE, 2, waiting * 2);
                break;
            case "NOTIFICATION_VIEWED":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.NOTIFICATION_VIEWED, 2, waiting * 2);
                break;
            case "SEND_COURTESY_MESSAGE":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.SEND_COURTESY_MESSAGE, 10, sharedSteps.getWorkFlowWait());
                break;
            case "DIGITAL_SUCCESS_WORKFLOW":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.DIGITAL_SUCCESS_WORKFLOW, 3, waiting * 3);
                break;
            case "DIGITAL_FAILURE_WORKFLOW":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.DIGITAL_FAILURE_WORKFLOW, 8, waiting * 3);
                break;
            case "NOT_HANDLED":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.NOT_HANDLED, 8, sharedSteps.getWorkFlowWait());
                break;
            case "SEND_DIGITAL_FEEDBACK":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.SEND_DIGITAL_FEEDBACK, 4, waiting * 3);
                break;
            case "SEND_DIGITAL_PROGRESS":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.SEND_DIGITAL_PROGRESS, 5, waiting * 4);
                break;
            case "PUBLIC_REGISTRY_CALL":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.PUBLIC_REGISTRY_CALL, 2, waiting * 4);
                break;
            case "PUBLIC_REGISTRY_RESPONSE":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.PUBLIC_REGISTRY_RESPONSE, 4, waiting * 4);
                break;
            case "SCHEDULE_ANALOG_WORKFLOW":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.SCHEDULE_ANALOG_WORKFLOW, 2, waiting * 3);
                break;
            case "ANALOG_SUCCESS_WORKFLOW":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.ANALOG_SUCCESS_WORKFLOW, 8, waiting * 4);
                break;
            case "ANALOG_FAILURE_WORKFLOW":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.ANALOG_FAILURE_WORKFLOW, 8, sharedSteps.getWorkFlowWait());
                break;
            case "SEND_ANALOG_DOMICILE":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.SEND_ANALOG_DOMICILE, 4, waiting * 3);
                break;
            case "SEND_ANALOG_PROGRESS":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.SEND_ANALOG_PROGRESS, 6, waiting * 3);
                break;
            case "SEND_ANALOG_FEEDBACK":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.SEND_ANALOG_FEEDBACK, 6, waiting * 3);
                break;
            case "PREPARE_SIMPLE_REGISTERED_LETTER":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.PREPARE_SIMPLE_REGISTERED_LETTER, 10, waiting * 3);
                break;
            case "SEND_SIMPLE_REGISTERED_LETTER":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.SEND_SIMPLE_REGISTERED_LETTER, 10, waiting * 3);
                break;
            case "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS, 9, waiting * 3);
                break;
            case "PAYMENT":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.PAYMENT, 8, sharedSteps.getWorkFlowWait());
                break;
            case "PREPARE_ANALOG_DOMICILE":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.PREPARE_ANALOG_DOMICILE, 4, waiting * 5);
                break;
            case "COMPLETELY_UNREACHABLE":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.COMPLETELY_UNREACHABLE, 10, sharedSteps.getWorkFlowWait());
                break;
            case "COMPLETELY_UNREACHABLE_CREATION_REQUEST":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.COMPLETELY_UNREACHABLE_CREATION_REQUEST, 8, sharedSteps.getWorkFlowWait());
                break;
            case "PREPARE_DIGITAL_DOMICILE":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.PREPARE_DIGITAL_DOMICILE, 2, waiting * 3);
                break;
            case "SCHEDULE_DIGITAL_WORKFLOW":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.SCHEDULE_DIGITAL_WORKFLOW, 2,waiting * 3);
                break;
            case "SCHEDULE_REFINEMENT":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.SCHEDULE_REFINEMENT, 8, waiting);
                break;
            case "REFINEMENT":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.REFINEMENT, 15, waiting);
                break;
            case "REQUEST_REFUSED":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.REQUEST_REFUSED, 2, waiting);
                break;
            case "DIGITAL_DELIVERY_CREATION_REQUEST":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory.DIGITAL_DELIVERY_CREATION_REQUEST, 5, waiting * 3);
                break;
            default:
                throw new IllegalArgumentException();
        }
        return timelineElementWait;
    }


    private TimelineElementWait getTimelineElementCategoryV2(String timelineEventCategory) {
        Integer waiting = sharedSteps.getWorkFlowWait();
        TimelineElementWait timelineElementWait;
        switch (timelineEventCategory) {
            case "DIGITAL_SUCCESS_WORKFLOW":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.TimelineElementCategoryV20.DIGITAL_SUCCESS_WORKFLOW, 3, waiting * 3);
                break;
            case "NOTIFICATION_CANCELLED":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.TimelineElementCategoryV20.NOTIFICATION_CANCELLED, 5, waiting * 3);
                break;
            case "PAYMENT":
                timelineElementWait = new TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.TimelineElementCategoryV20.PAYMENT, 8, sharedSteps.getWorkFlowWait());
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
            NewNotificationRequestV21 newNotificationRequest = sharedSteps.getNotificationRequest();
            // get timeline from delivery-push
            NotificationHistoryResponse notificationHistory = this.pnPrivateDeliveryPushExternalClient.getNotificationHistory(iun, newNotificationRequest.getRecipients().size(), sharedSteps.getNotificationCreationDate());
            timelineElementList = notificationHistory.getTimeline();
            FullSentNotificationV21 fullSentNotification = new FullSentNotificationV21();

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

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica annullata {string}")
    public void readingEventUpToTheTimelineElementOfNotificationDelete(String timelineEventCategory) {
        TimelineElementWait timelineElementWait = getTimelineElementCategoryShortAnnullamento(timelineEventCategory);
        readingEventUpToTheTimelineElementOfNotificationForCategory(timelineElementWait);
    }


    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string}")
    public void readingEventUpToTheTimelineElementOfNotification(String timelineEventCategory) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);
        readingEventUpToTheTimelineElementOfNotificationForCategory(timelineElementWait);
    }

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} abbia notificationCost ugauale a {string}")
    public void TimelineElementOfNotification(String timelineEventCategory, String cost) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);
        TimelineElementV20 event = readingEventUpToTheTimelineElementOfNotificationForCategory(timelineElementWait);

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
        OffsetDateTime ricezioneRaccomandata = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV20.SCHEDULE_REFINEMENT) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getDetails().getSchedulingDate();
        OffsetDateTime refinementDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV20.REFINEMENT) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();

        logger.info("DESTINATARIO : {}", destinatario);
        logger.info("ricezioneRaccomandata : {}", ricezioneRaccomandata);
        logger.info("refinementDate : {}", refinementDate);

        Assertions.assertEquals(ricezioneRaccomandata,refinementDate);

    }catch (AssertionFailedError assertionFailedError) {
        sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
    }
    }

    @Then("verifica date business in timeline COMPLETELY_UNREACHABLE per l'utente {int}")
    public void verificationDateComplettelyUnreachableWithRefinement(Integer destinatario) {

        try {
            OffsetDateTime shedulingDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV20.SCHEDULE_REFINEMENT) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();
            OffsetDateTime complettelyUnreachableDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV20.COMPLETELY_UNREACHABLE) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();
            OffsetDateTime complettelyUnreachableRequestDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV20.COMPLETELY_UNREACHABLE_CREATION_REQUEST) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();
            OffsetDateTime analogFailureDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV20.ANALOG_FAILURE_WORKFLOW) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();
            OffsetDateTime sendAnalogProgressTimestampDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV20.SEND_ANALOG_PROGRESS) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();
            OffsetDateTime sendAnalogProgressNotificationDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV20.SEND_ANALOG_PROGRESS) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getDetails().getNotificationDate();
            OffsetDateTime sendFeedbackTimestampDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV20.SEND_ANALOG_FEEDBACK) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();
            OffsetDateTime sendFeedbackNotificationDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV20.SEND_ANALOG_FEEDBACK) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getDetails().getNotificationDate();
            OffsetDateTime prepareAnalogDomicileFailureTimestamp = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV20.PREPARE_ANALOG_DOMICILE_FAILURE) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();

            logger.info("DESTINATARIO : {}", destinatario);
            logger.info("sendAnalogProgressTimestampDate : {}", sendAnalogProgressTimestampDate);
            logger.info("sendFeedbackTimestampDate : {} ", sendFeedbackTimestampDate);
            logger.info("analogFailureDate Timestamp : {}", analogFailureDate);
            logger.info("complettelyUnreachableRequestDate Timestamp : {}", complettelyUnreachableRequestDate);
            logger.info("complettelyUnreachableDate Timestamp : {}", complettelyUnreachableDate);
            logger.info("prepareAnalogDomicileFailureTimestamp : {}", prepareAnalogDomicileFailureTimestamp);

            logger.info("shedulingDate Timestamp: {}", shedulingDate);

            logger.info("sendAnalogProgressNotificationDate : {}", sendAnalogProgressNotificationDate);
            logger.info("sendFeedbackNotificationDate : {}", sendFeedbackNotificationDate);

            Assertions.assertEquals(shedulingDate,complettelyUnreachableDate);
            Assertions.assertEquals(shedulingDate,complettelyUnreachableRequestDate);
            Assertions.assertEquals(shedulingDate,analogFailureDate);
            Assertions.assertEquals(shedulingDate,prepareAnalogDomicileFailureTimestamp);
            Assertions.assertEquals(sendAnalogProgressTimestampDate,sendAnalogProgressNotificationDate);
            Assertions.assertEquals(sendFeedbackTimestampDate,sendFeedbackNotificationDate);
            //TODO  Verificare..
           // Assertions.assertEquals(sendFeedbackDate,sendAnalogProgressDate);


        }catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }




    @Then("verifica date business in timeline ANALOG_SUCCESS_WORKFLOW per l'utente {int} al tentativo {int}")
    public void verificationDateScheduleRefinementWithSendAnalogFeedback(Integer destinatario,Integer tentativo) {

        try {
            OffsetDateTime shedulingDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV20.SCHEDULE_REFINEMENT) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();
            OffsetDateTime sendAnalogProgressNotificationDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV20.SEND_ANALOG_PROGRESS) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getDetails().getNotificationDate();
            OffsetDateTime sendAnalogProgressTimestampDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV20.SEND_ANALOG_PROGRESS) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();
            OffsetDateTime sendFeedbackTimestampDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV20.SEND_ANALOG_FEEDBACK) && elem.getDetails().getRecIndex().equals(destinatario) && elem.getDetails().getSentAttemptMade().equals(tentativo)).findAny().get().getTimestamp();
            OffsetDateTime sendFeedbackNotificationDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV20.SEND_ANALOG_FEEDBACK) && elem.getDetails().getRecIndex().equals(destinatario) && elem.getDetails().getSentAttemptMade().equals(tentativo)).findAny().get().getDetails().getNotificationDate();
            OffsetDateTime analogSuccessDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV20.ANALOG_SUCCESS_WORKFLOW) && elem.getDetails().getRecIndex().equals(destinatario)).findAny().get().getTimestamp();

            logger.info("DESTINATARIO : {}", destinatario);
            logger.info("sendAnalogProgressTimestampDate: {}", sendAnalogProgressTimestampDate);
            logger.info("sendFeedbackTimestampDate: {}", sendFeedbackTimestampDate);
            logger.info("analogSuccessDate Timestamp: {}", analogSuccessDate);
            logger.info("shedulingDate Timestamp: {}", shedulingDate);


            logger.info("sendFeedbackNotificationDate : {}", sendFeedbackNotificationDate);
            logger.info("sendAnalogProgressNotificationDate: {}", sendAnalogProgressNotificationDate);

            Assertions.assertEquals(shedulingDate,analogSuccessDate);
            Assertions.assertEquals(shedulingDate,sendFeedbackTimestampDate);

            Assertions.assertEquals(sendAnalogProgressTimestampDate,sendAnalogProgressNotificationDate);
            Assertions.assertEquals(sendFeedbackTimestampDate,sendFeedbackNotificationDate);

        }catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }


    public TimelineElementV20 readingEventUpToTheTimelineElementOfNotificationForCategory(TimelineElementWait timelineElementWait) {

        TimelineElementV20 timelineElement = null;
        String iun = null;
        for (int i = 0; i < timelineElementWait.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineElementWait.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            if (sharedSteps.getSentNotification()!= null) {
                iun = sharedSteps.getSentNotification().getIun();

            } else if (sharedSteps.getSentNotificationV1()!= null) {
                iun = sharedSteps.getSentNotificationV1().getIun();

            } else if (sharedSteps.getSentNotificationV2()!= null) {
                iun = sharedSteps.getSentNotificationV2().getIun();
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(iun));
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

        return timelineElement;
    }


    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} V1")
    public void readingEventUpToTheTimelineElementOfNotificationV1(String timelineEventCategory) {
        TimelineElementWait timelineElementWait = getTimelineElementCategoryV1(timelineEventCategory);

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElement timelineElement = null;
        String iun = null;
        for (int i = 0; i < timelineElementWait.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineElementWait.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            if (sharedSteps.getSentNotification()!= null) {
                iun = sharedSteps.getSentNotification().getIun();

            } else if (sharedSteps.getSentNotificationV1()!= null) {
                iun = sharedSteps.getSentNotificationV1().getIun();

            } else if (sharedSteps.getSentNotificationV2()!= null) {
                iun = sharedSteps.getSentNotificationV2().getIun();
            }

            sharedSteps.setSentNotificationV1(b2bClient.getSentNotificationV1(iun));
            logger.info("NOTIFICATION_TIMELINE V1 : " + sharedSteps.getSentNotificationV1().getTimeline());

            timelineElement = sharedSteps.getSentNotificationV1().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementWait.getTimelineElementCategoryV1())).findAny().orElse(null);
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

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} V2")
    public void readingEventUpToTheTimelineElementOfNotificationV2(String timelineEventCategory) {
        TimelineElementWait timelineElementWait = getTimelineElementCategoryV2(timelineEventCategory);

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.TimelineElementV20 timelineElement = null;
        String iun = null;
        for (int i = 0; i < timelineElementWait.getNumCheck(); i++) {
            try {
                Thread.sleep(timelineElementWait.getWaiting());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            if (sharedSteps.getSentNotification()!= null) {
                iun = sharedSteps.getSentNotification().getIun();

            } else if (sharedSteps.getSentNotificationV1()!= null) {
                iun = sharedSteps.getSentNotificationV1().getIun();

            } else if (sharedSteps.getSentNotificationV2()!= null) {
                iun = sharedSteps.getSentNotificationV2().getIun();
            }

            sharedSteps.setSentNotificationV2(b2bClient.getSentNotificationV2(iun));
            logger.info("NOTIFICATION_TIMELINE V2: " + sharedSteps.getSentNotificationV2().getTimeline());

            timelineElement = sharedSteps.getSentNotificationV2().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementWait.getTimelineElementCategoryV2())).findAny().orElse(null);

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

    @Then("vengono letti gli eventi della timeline e si controlla che l'evento di timeline {string} non esista con la V1")
    public void readingEventsOfTimelineElementOfNotificationV1(String timelineEventCategory) {

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElement timelineElement = null;
        String iun = null;


        if (sharedSteps.getSentNotification()!= null) {
            iun = sharedSteps.getSentNotification().getIun();

        } else if (sharedSteps.getSentNotificationV1()!= null) {
            iun = sharedSteps.getSentNotificationV1().getIun();

        } else if (sharedSteps.getSentNotificationV2()!= null) {
        iun = sharedSteps.getSentNotificationV2().getIun();
    }

        sharedSteps.setSentNotificationV1(b2bClient.getSentNotificationV1(iun));
        logger.info("NOTIFICATION_TIMELINE V1 : " + sharedSteps.getSentNotificationV1().getTimeline());

        timelineElement = sharedSteps.getSentNotificationV1().getTimeline().stream().filter(elem -> elem.getCategory().getValue().equals(timelineEventCategory)).findAny().orElse(null);

        try {
            Assertions.assertNull(timelineElement);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("viene controllato che l'elemento di timeline della notifica {string} non esiste")
    public void readingNotEventUpToTheTimelineElementOfNotification(String timelineEventCategory) {
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
            Assertions.assertNull(timelineElement);
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
                if (element.getCategory().equals(timelineElementWait.getTimelineElementCategory()) && element.getElementId().contains(attempt) && element.getDetails().getDeliveryDetailCode().equals(deliveryDetailCode) && element.getDetails().getAttachments().get(0).getDocumentType().equals(tipoDoc)) {
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

    @Then("vengono letti gli eventi e verificho che l'utente {int} non abbia associato un evento {string} V1")
    public void vengonoLettiGliEventiVerifichoCheUtenteNonAbbiaAssociatoEventoV1(Integer destinatario, String timelineEventCategory) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        if (sharedSteps.getSentNotificationV1()!= null){
            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElement timelineElement = null;
            sharedSteps.setSentNotificationV1(b2bClient.getSentNotificationV1(sharedSteps.getSentNotificationV1().getIun()));
            for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElement element : sharedSteps.getSentNotificationV1().getTimeline()) {

                if (element.getCategory().equals(timelineElementWait.getTimelineElementCategory()) && element.getDetails().getRecIndex().equals(destinatario)) {
                    timelineElement = element;
                }
            }

            try {
                Assertions.assertNull(timelineElement);
            } catch (AssertionFailedError assertionFailedError) {
                sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
            }
        }else if(sharedSteps.getSentNotificationV2()!= null) {
            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElement timelineElement = null;
            sharedSteps.setSentNotificationV1(b2bClient.getSentNotificationV1(sharedSteps.getSentNotificationV2().getIun()));
            for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElement element : sharedSteps.getSentNotificationV1().getTimeline()) {

                if (element.getCategory().equals(timelineElementWait.getTimelineElementCategory()) && element.getDetails().getRecIndex().equals(destinatario)) {
                    timelineElement = element;
                }
            }

            try {
                Assertions.assertNull(timelineElement);
            } catch (AssertionFailedError assertionFailedError) {
                sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
            }

        }else{
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElement timelineElement = null;
                sharedSteps.setSentNotificationV1(b2bClient.getSentNotificationV1(sharedSteps.getSentNotification().getIun()));
                for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElement element : sharedSteps.getSentNotificationV1().getTimeline()) {

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
    public void userDownloadLegalFactError(String user, String legalFactCategory,String statusCode) {
        try {
            sharedSteps.selectUser(user);
            downloadLegalFact(legalFactCategory, false, false, true, null);
        } catch (AssertionFailedError assertionFailedError) {
            // System.out.println(assertionFailedError.getCause().toString());
            // System.out.println(assertionFailedError.getCause().getMessage().toString());
            Assertions.assertTrue(assertionFailedError.getCause().getMessage().toString().substring(0, 3).equals(statusCode));
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
            //TODO Verificare....
            if (key.contains("PN_LEGAL_FACTS")) {
                keySearch = key.substring(key.indexOf("PN_LEGAL_FACTS"));
            } else if (key.contains("PN_NOTIFICATION_ATTACHMENTS")) {
                keySearch = key.substring(key.indexOf("PN_NOTIFICATION_ATTACHMENTS"));
            } else if (key.contains("PN_EXTERNAL_LEGAL_FACTS")) {
                keySearch = key.substring(key.indexOf("PN_EXTERNAL_LEGAL_FACTS"));
            } else if (key.contains("PN_EXTERNAL_LEGAL_FACTS")) {
                keySearch = key.substring(key.indexOf("PN_EXTERNAL_LEGAL_FACTS"));
            } else if (key.contains("PN_F24")) {
                keySearch = key.substring(key.indexOf("PN_F24"));
            }


            String finalKeySearch = keySearch;
            if (pa) {

                Assertions.assertDoesNotThrow(() ->  this.b2bClient.getLegalFact(sharedSteps.getSentNotification().getIun(), categorySearch, finalKeySearch));
                LegalFactDownloadMetadataResponse legalFactDownloadMetadataResponse = this.b2bClient.getLegalFact(sharedSteps.getSentNotification().getIun(), categorySearch, finalKeySearch);
            }
            if (appIO) {

                // Assertions.assertDoesNotThrow(() -> this.appIOB2bClient.getLegalFact(sharedSteps.getSentNotification().getIun(), categorySearch.toString(), finalKeySearch,
                //  sharedSteps.getSentNotification().getRecipients().get(0).getTaxId()));

            }
            if (webRecipient) {
                Assertions.assertDoesNotThrow(() -> this.webRecipientClient.getLegalFact(sharedSteps.getSentNotification().getIun(),
                        sharedSteps.deepCopy(categorySearch,
                                it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.LegalFactCategory.class),
                        finalKeySearch

                ));

                it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.LegalFactDownloadMetadataResponse legalFactDownloadMetadataResponse =this.webRecipientClient.getLegalFact(sharedSteps.getSentNotification().getIun(),
                        sharedSteps.deepCopy(categorySearch,
                                it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.LegalFactCategory.class),
                        finalKeySearch);
                System.out.println("NOME FILE PEC RECIPIENT DEST"+legalFactDownloadMetadataResponse.getFilename());
            }
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }


    private void downloadLegalFactPecRecipient(String legalFactCategory, boolean pa, boolean appIO, boolean webRecipient, String deliveryDetailCode) {
        try {
            Thread.sleep(sharedSteps.getWait());
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }

        TimelineElementV20 timelineElement = null;

        TimelineElementCategoryV20 timelineElementInternalCategory = TimelineElementCategoryV20.SEND_DIGITAL_PROGRESS;
        LegalFactCategory category = LegalFactCategory.PEC_RECEIPT;

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
            //TODO Verificare....
            if (key.contains("PN_LEGAL_FACTS")) {
                keySearch = key.substring(key.indexOf("PN_LEGAL_FACTS"));
            } else if (key.contains("PN_NOTIFICATION_ATTACHMENTS")) {
                keySearch = key.substring(key.indexOf("PN_NOTIFICATION_ATTACHMENTS"));
            } else if (key.contains("PN_EXTERNAL_LEGAL_FACTS")) {
                keySearch = key.substring(key.indexOf("PN_EXTERNAL_LEGAL_FACTS"));
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

                it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.LegalFactDownloadMetadataResponse legalFactDownloadMetadataResponse =this.webRecipientClient.getLegalFact(sharedSteps.getSentNotification().getIun(),
                        sharedSteps.deepCopy(categorySearch,
                                it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.LegalFactCategory.class),
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

                // Assertions.assertDoesNotThrow(() -> this.appIOB2bClient.getLegalFact(sharedSteps.getSentNotification().getIun(), categorySearch.toString(), finalKeySearch,
                //        sharedSteps.getSentNotification().getRecipients().get(0).getTaxId()));
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
    @Then("vengono verificati costo = {string} e data di perfezionamento della notifica {string}")
    public void notificationPriceAndDateVerificationV1(String price,String versione) {
        try {
            Thread.sleep(sharedSteps.getWait() * 2);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        if(versione.equalsIgnoreCase("V1")) {
            priceVerificationV1(price, null, 0);
        }else if(versione.equalsIgnoreCase("V2")){
            priceVerificationV2(price, null, 0);
        }else{
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

        priceVerification(price, null, 0);
    }

    @Then("viene verificato il costo totale della notifica con iva inclusa sia = {string}")
    public void notificationPriceVerificationIvaIncluded(String price) {
        try {
            Thread.sleep(sharedSteps.getWait() * 2);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        priceVerificationV23(price, null, 0);
    }




    @And("viene verificato il costo = {string} della notifica con un errore {string}")
    public void attachmentRetrievedError(String price, String errorCode) {
        try {
            Thread.sleep(sharedSteps.getWait() * 2);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        try {
            priceVerification(price, null, 0);
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

        priceVerification(price, null, destinatario);
    }

    private void priceVerification(String price, String date, Integer destinatario) {

        if(sharedSteps.getSentNotification()!=null) {
            List<NotificationPaymentItem> listNotificationPaymentItem = sharedSteps.getSentNotification().getRecipients().get(destinatario).getPayments();

            if (listNotificationPaymentItem != null){
                for (NotificationPaymentItem notificationPaymentItem: listNotificationPaymentItem) {
                    NotificationPriceResponse notificationPrice = this.b2bClient.getNotificationPrice(notificationPaymentItem.getPagoPa().getCreditorTaxId(), notificationPaymentItem.getPagoPa().getNoticeCode());
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
            }
        }else if(sharedSteps.getSentNotificationV1()!=null) {
            priceVerificationV1(price,date,destinatario);
        }else if(sharedSteps.getSentNotificationV2()!=null){
            priceVerificationV2(price,date,destinatario);
        }
    }

    private void priceVerificationV1(String price, String date, Integer destinatario) {
        NotificationPriceResponse notificationPrice = this.b2bClient.getNotificationPrice(sharedSteps.getSentNotificationV1().getRecipients().get(destinatario).getPayment().getCreditorTaxId(),
                sharedSteps.getSentNotificationV1().getRecipients().get(destinatario).getPayment().getNoticeCode());
        try {
            Assertions.assertEquals(notificationPrice.getIun(), sharedSteps.getSentNotificationV1().getIun());
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


    private void priceVerificationV2(String price, String date, Integer destinatario) {
        NotificationPriceResponse notificationPrice = this.b2bClient.getNotificationPrice(sharedSteps.getSentNotificationV2().getRecipients().get(destinatario).getPayment().getCreditorTaxId(),
                sharedSteps.getSentNotificationV2().getRecipients().get(destinatario).getPayment().getNoticeCode());
        try {
            Assertions.assertEquals(notificationPrice.getIun(), sharedSteps.getSentNotificationV2().getIun());
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

    public List<NotificationPriceResponseV23> priceVerificationV23(String price, String date, Integer destinatario) {

        if(sharedSteps.getSentNotification()!=null) {
            List<NotificationPaymentItem> listNotificationPaymentItem = sharedSteps.getSentNotification().getRecipients().get(destinatario).getPayments();
            List<NotificationPriceResponseV23> listNotificationPriceV23 = new ArrayList<>();


            if (listNotificationPaymentItem != null){
                for (NotificationPaymentItem notificationPaymentItem: listNotificationPaymentItem) {
                    NotificationPriceResponseV23 notificationPriceV23 = this.b2bClient.getNotificationPriceV23(notificationPaymentItem.getPagoPa().getCreditorTaxId(), notificationPaymentItem.getPagoPa().getNoticeCode());

                    try {
                        Assertions.assertEquals(notificationPriceV23.getIun(), sharedSteps.getSentNotification().getIun());
                        if (price != null) {
                            logger.info("Costo notifica: {} destinatario: {}", notificationPriceV23.getTotalPrice(), destinatario);
                            Assertions.assertEquals(Integer.parseInt(price), notificationPriceV23.getTotalPrice());
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
            notificationProcessCost = this.b2bClient.getNotificationProcessCost(sharedSteps.getSentNotification().getIun(), destinatario, it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.NotificationFeePolicy.DELIVERY_MODE, sharedSteps.getSentNotification().getRecipients().get(destinatario).getPayments().get(0).getF24().getApplyCost(), sharedSteps.getSentNotification().getPaFee());
        }else {
            notificationProcessCost = this.b2bClient.getNotificationProcessCost(sharedSteps.getSentNotification().getIun(), destinatario, it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.NotificationFeePolicy.FLAT_RATE, sharedSteps.getSentNotification().getRecipients().get(destinatario).getPayments().get(0).getF24().getApplyCost(), sharedSteps.getSentNotification().getPaFee());
        }
        try {
            if (price != null) {
                logger.info("Costo notifica: {} destinatario: {}", notificationProcessCost.getAmount(), destinatario);
                Assertions.assertEquals(notificationProcessCost.getAmount(), Integer.parseInt(price));
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

        String iun =sharedSteps.getIunVersionamento();

        try {
                webRecipientClient.getReceivedNotification(iun, null);
        } catch (HttpStatusCodeException e) {
            if (e instanceof HttpStatusCodeException) {
                sharedSteps.setNotificationError((HttpStatusCodeException) e);
            }
        }

    }

    @And("{string} legge la notifica ricevuta")
    public void userReadReceivedNotification(String recipient) {
        sharedSteps.selectUser(recipient);

        String iun =sharedSteps.getIunVersionamento();

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
    public void userReadReceivedNotificationVersioning(String recipient,String versione) {
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

       }catch (HttpStatusCodeException e) {
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
        NotificationPriceResponse notificationPrice = this.b2bClient.getNotificationPrice(sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(0).getPagoPa().getCreditorTaxId(),
                sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(0).getPagoPa().getNoticeCode());

        PaymentEventsRequestPagoPa eventsRequestPagoPa = new PaymentEventsRequestPagoPa();

        PaymentEventPagoPa paymentEventPagoPa = new PaymentEventPagoPa();
        paymentEventPagoPa.setNoticeCode(sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(0).getPagoPa().getNoticeCode());
        paymentEventPagoPa.setCreditorTaxId(sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(0).getPagoPa().getCreditorTaxId());
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

        if(sharedSteps.getSentNotification()!= null){
            NotificationPriceResponse notificationPrice = this.b2bClient.getNotificationPrice(sharedSteps.getSentNotification().getRecipients().get(utente).getPayments().get(0).getPagoPa().getCreditorTaxId(),
                    sharedSteps.getSentNotification().getRecipients().get(utente).getPayments().get(0).getPagoPa().getNoticeCode());

            PaymentEventsRequestPagoPa eventsRequestPagoPa = new PaymentEventsRequestPagoPa();

            PaymentEventPagoPa paymentEventPagoPa = new PaymentEventPagoPa();
            paymentEventPagoPa.setNoticeCode(sharedSteps.getSentNotification().getRecipients().get(utente).getPayments().get(0).getPagoPa().getNoticeCode());
            paymentEventPagoPa.setCreditorTaxId(sharedSteps.getSentNotification().getRecipients().get(utente).getPayments().get(0).getPagoPa().getCreditorTaxId());
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            paymentEventPagoPa.setPaymentDate(fmt.format(OffsetDateTime.now()));
            paymentEventPagoPa.setAmount(notificationPrice.getAmount());

            List<PaymentEventPagoPa> paymentEventPagoPaList = new LinkedList<>();
            paymentEventPagoPaList.add(paymentEventPagoPa);

            eventsRequestPagoPa.setEvents(paymentEventPagoPaList);

            b2bClient.paymentEventsRequestPagoPa(eventsRequestPagoPa);
        } else if (sharedSteps.getSentNotificationV1()!= null) {
            NotificationPriceResponse notificationPrice = this.b2bClient.getNotificationPrice(sharedSteps.getSentNotificationV1().getRecipients().get(utente).getPayment().getCreditorTaxId(),
                    sharedSteps.getSentNotificationV1().getRecipients().get(utente).getPayment().getNoticeCode());

            PaymentEventsRequestPagoPa eventsRequestPagoPa = new PaymentEventsRequestPagoPa();

            PaymentEventPagoPa paymentEventPagoPa = new PaymentEventPagoPa();
            paymentEventPagoPa.setNoticeCode(sharedSteps.getSentNotificationV1().getRecipients().get(utente).getPayment().getNoticeCode());
            paymentEventPagoPa.setCreditorTaxId(sharedSteps.getSentNotificationV1().getRecipients().get(utente).getPayment().getCreditorTaxId());
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            paymentEventPagoPa.setPaymentDate(fmt.format(OffsetDateTime.now()));
            paymentEventPagoPa.setAmount(notificationPrice.getAmount());

            List<PaymentEventPagoPa> paymentEventPagoPaList = new LinkedList<>();
            paymentEventPagoPaList.add(paymentEventPagoPa);

            eventsRequestPagoPa.setEvents(paymentEventPagoPaList);

            b2bClient.paymentEventsRequestPagoPa(eventsRequestPagoPa);
        }


    }

    @And("l'avviso pagopa viene pagato correttamente dall'utente {int} V1")
    public void laNotificaVienePagataMultiV1(Integer utente) {

        String noticeCode= null;
        String creditorTaxId= null;

        if (sharedSteps.getSentNotificationV1()!= null) {
            noticeCode= sharedSteps.getSentNotificationV1().getRecipients().get(utente).getPayment().getNoticeCode();
            creditorTaxId= sharedSteps.getSentNotificationV1().getRecipients().get(utente).getPayment().getCreditorTaxId();
        }else if (sharedSteps.getSentNotificationV2()!= null){
            noticeCode= sharedSteps.getSentNotificationV2().getRecipients().get(utente).getPayment().getNoticeCode();
            creditorTaxId= sharedSteps.getSentNotificationV2().getRecipients().get(utente).getPayment().getCreditorTaxId();
        }else if (sharedSteps.getSentNotification()!= null) {
            noticeCode= sharedSteps.getSentNotification().getRecipients().get(utente).getPayments().get(0).getPagoPa().getNoticeCode();
            creditorTaxId= sharedSteps.getSentNotification().getRecipients().get(utente).getPayments().get(0).getPagoPa().getCreditorTaxId();
        }

            NotificationPriceResponse notificationPrice = this.b2bClient.getNotificationPrice(creditorTaxId,noticeCode);

            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.PaymentEventsRequestPagoPa eventsRequestPagoPa = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.PaymentEventsRequestPagoPa();

            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.PaymentEventPagoPa paymentEventPagoPa = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.PaymentEventPagoPa();
            paymentEventPagoPa.setNoticeCode(noticeCode);
            paymentEventPagoPa.setCreditorTaxId(creditorTaxId);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            paymentEventPagoPa.setPaymentDate(fmt.format(OffsetDateTime.now()));
            paymentEventPagoPa.setAmount(notificationPrice.getAmount());

            List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.PaymentEventPagoPa> paymentEventPagoPaList = new LinkedList<>();
            paymentEventPagoPaList.add(paymentEventPagoPa);

            eventsRequestPagoPa.setEvents(paymentEventPagoPaList);

            b2bClient.paymentEventsRequestPagoPaV1(eventsRequestPagoPa);


    }


    @And("l'avviso pagopa viene pagato correttamente dall'utente {int} V2")
    public void laNotificaVienePagataMultiV2(Integer utente) {

        String noticeCode= null;
        String creditorTaxId= null;

        if (sharedSteps.getSentNotificationV1()!= null) {
            noticeCode= sharedSteps.getSentNotificationV1().getRecipients().get(utente).getPayment().getNoticeCode();
            creditorTaxId= sharedSteps.getSentNotificationV1().getRecipients().get(utente).getPayment().getCreditorTaxId();
        }else if (sharedSteps.getSentNotificationV2()!= null){
            noticeCode= sharedSteps.getSentNotificationV2().getRecipients().get(utente).getPayment().getNoticeCode();
            creditorTaxId= sharedSteps.getSentNotificationV2().getRecipients().get(utente).getPayment().getCreditorTaxId();
        }else if (sharedSteps.getSentNotification()!= null) {
            noticeCode= sharedSteps.getSentNotification().getRecipients().get(utente).getPayments().get(0).getPagoPa().getNoticeCode();
            creditorTaxId= sharedSteps.getSentNotification().getRecipients().get(utente).getPayments().get(0).getPagoPa().getCreditorTaxId();
        }


            NotificationPriceResponse notificationPrice = this.b2bClient.getNotificationPrice(creditorTaxId,noticeCode);

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.PaymentEventsRequestPagoPa eventsRequestPagoPa = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.PaymentEventsRequestPagoPa();

            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.PaymentEventPagoPa paymentEventPagoPa = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.PaymentEventPagoPa();
            paymentEventPagoPa.setNoticeCode(noticeCode);
            paymentEventPagoPa.setCreditorTaxId(creditorTaxId);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            paymentEventPagoPa.setPaymentDate(fmt.format(OffsetDateTime.now()));
            paymentEventPagoPa.setAmount(notificationPrice.getAmount());

            List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.PaymentEventPagoPa> paymentEventPagoPaList = new LinkedList<>();
            paymentEventPagoPaList.add(paymentEventPagoPa);

            eventsRequestPagoPa.setEvents(paymentEventPagoPaList);

            b2bClient.paymentEventsRequestPagoPaV2(eventsRequestPagoPa);


    }

    @And("l'avviso pagopa {int} viene pagato correttamente dall'utente {int}")
    public void laNotificaVienePagataConAvvisoNumMulti( Integer idAvviso, Integer utente) {
        if (sharedSteps.getSentNotification()!= null){
            NotificationPriceResponse notificationPrice = this.b2bClient.getNotificationPrice(sharedSteps.getSentNotification().getRecipients().get(utente).getPayments().get(idAvviso).getPagoPa().getCreditorTaxId(),
                    sharedSteps.getSentNotification().getRecipients().get(utente).getPayments().get(idAvviso).getPagoPa().getNoticeCode());

            PaymentEventsRequestPagoPa eventsRequestPagoPa = new PaymentEventsRequestPagoPa();

            PaymentEventPagoPa paymentEventPagoPa = new PaymentEventPagoPa();
            paymentEventPagoPa.setNoticeCode(sharedSteps.getSentNotification().getRecipients().get(utente).getPayments().get(idAvviso).getPagoPa().getNoticeCode());
            paymentEventPagoPa.setCreditorTaxId(sharedSteps.getSentNotification().getRecipients().get(utente).getPayments().get(idAvviso).getPagoPa().getCreditorTaxId());
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            paymentEventPagoPa.setPaymentDate(fmt.format(OffsetDateTime.now()));
            paymentEventPagoPa.setAmount(notificationPrice.getAmount());

            List<PaymentEventPagoPa> paymentEventPagoPaList = new LinkedList<>();
            paymentEventPagoPaList.add(paymentEventPagoPa);

            eventsRequestPagoPa.setEvents(paymentEventPagoPaList);

            b2bClient.paymentEventsRequestPagoPa(eventsRequestPagoPa);
        }else if (sharedSteps.getSentNotificationV1()!= null){
            NotificationPriceResponse notificationPrice = this.b2bClient.getNotificationPrice(sharedSteps.getSentNotificationV1().getRecipients().get(0).getPayment().getCreditorTaxId(),
                    sharedSteps.getSentNotificationV1().getRecipients().get(utente).getPayment().getNoticeCode());

            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.PaymentEventsRequestPagoPa eventsRequestPagoPa = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.PaymentEventsRequestPagoPa();

            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.PaymentEventPagoPa paymentEventPagoPa = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.PaymentEventPagoPa();
            paymentEventPagoPa.setNoticeCode(sharedSteps.getSentNotificationV1().getRecipients().get(utente).getPayment().getNoticeCode());
            paymentEventPagoPa.setCreditorTaxId(sharedSteps.getSentNotificationV1().getRecipients().get(0).getPayment().getCreditorTaxId());
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            paymentEventPagoPa.setPaymentDate(fmt.format(OffsetDateTime.now()));
            paymentEventPagoPa.setAmount(notificationPrice.getAmount());

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
        for (NotificationPaymentItem notificationPaymentItem: sharedSteps.getSentNotification().getRecipients().get(destinatario).getPayments()) {
            NotificationPriceResponse notificationPrice = this.b2bClient.getNotificationPrice(notificationPaymentItem.getPagoPa().getCreditorTaxId(), notificationPaymentItem.getPagoPa().getNoticeCode());
            PaymentEventPagoPa paymentEventPagoPa = new PaymentEventPagoPa();
            paymentEventPagoPa.setNoticeCode(notificationPaymentItem.getPagoPa().getNoticeCode());
            paymentEventPagoPa.setCreditorTaxId(notificationPaymentItem.getPagoPa().getCreditorTaxId());
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            paymentEventPagoPa.setPaymentDate(fmt.format(OffsetDateTime.now()));
            paymentEventPagoPa.setAmount(notificationPrice.getAmount());
            paymentEventPagoPaList.add(paymentEventPagoPa);
        }

        eventsRequestPagoPa.setEvents(paymentEventPagoPaList);

        b2bClient.paymentEventsRequestPagoPa(eventsRequestPagoPa);
    }

    @And("viene rifiutato il pagamento dell'avviso pagopa  dall'utente {int}")
    public void laNotificaVieneRifiutatoIlPagamentoMulti(Integer utente) {
        NotificationPriceResponse notificationPrice = this.b2bClient.getNotificationPrice(sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(0).getPagoPa().getCreditorTaxId(),
                sharedSteps.getSentNotification().getRecipients().get(utente).getPayments().get(0).getPagoPa().getNoticeCode());

        PaymentEventsRequestPagoPa eventsRequestPagoPa = new PaymentEventsRequestPagoPa();

        PaymentEventPagoPa paymentEventPagoPa = new PaymentEventPagoPa();
        paymentEventPagoPa.setNoticeCode(sharedSteps.getSentNotification().getRecipients().get(utente).getPayments().get(0).getPagoPa().getNoticeCode());
        paymentEventPagoPa.setCreditorTaxId(sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(0).getPagoPa().getCreditorTaxId());
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        paymentEventPagoPa.setPaymentDate(fmt.format(OffsetDateTime.now()));
        paymentEventPagoPa.setAmount(notificationPrice.getAmount());

        List<PaymentEventPagoPa> paymentEventPagoPaList = new LinkedList<>();
        paymentEventPagoPaList.add(paymentEventPagoPa);

        eventsRequestPagoPa.setEvents(paymentEventPagoPaList);

        b2bClient.paymentEventsRequestPagoPa(eventsRequestPagoPa);
    }

    @Then("il modello f24 viene pagato correttamente")
    public void ilModelloF24VienePagatoCorrettamente() {
        //TODO Modificare.............. valutare se chiamare getNotificationProcessCost
        PaymentEventsRequestF24 eventsRequestF24 = new PaymentEventsRequestF24();

        NotificationPriceResponse notificationPrice = this.b2bClient.getNotificationPrice(sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(0).getPagoPa().getCreditorTaxId(),
                sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(0).getPagoPa().getNoticeCode());

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

    @Then("il modello f24 viene pagato correttamente dall'utente {int}")
    public void ilModelloF24VienePagatoCorrettamenteDalUtente(Integer utente) {

        //TODO Modificare.............. valutare se chiamare getNotificationProcessCost
        PaymentEventsRequestF24 eventsRequestF24 = new PaymentEventsRequestF24();

        NotificationPriceResponse notificationPrice = this.b2bClient.getNotificationPrice(sharedSteps.getSentNotification().getRecipients().get(utente).getPayments().get(0).getPagoPa().getCreditorTaxId(),
                sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(utente).getPagoPa().getNoticeCode());

        PaymentEventF24 paymentEventF24 = new PaymentEventF24();
        paymentEventF24.setIun(sharedSteps.getSentNotification().getIun());
        paymentEventF24.setRecipientTaxId(sharedSteps.getSentNotification().getRecipients().get(utente).getTaxId());
        paymentEventF24.setRecipientType(sharedSteps.getSentNotification().getRecipients().get(utente).getRecipientType().equals(NotificationRecipient.RecipientTypeEnum.PF) ? "PF" : "PG");
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

        if (sharedSteps.getSentNotification()!= null){
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
        } else {
            TimelineElementWait timelineElementWait = getTimelineElementCategory("PAYMENT");

            TimelineElementV20 timelineElement = null;

            for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException exc) {
                    throw new RuntimeException(exc);
                }

                sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotificationV1().getIun()));

                logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotificationV1().getTimeline());

                timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementWait.getTimelineElementCategory())).findAny().orElse(null);
                if (timelineElement != null) {
                    break;
                }
            }
            Assertions.assertNotNull(timelineElement);
        }


    }


    @Then("si attende il corretto pagamento della notifica V1")
    public void siAttendeIlCorrettoPagamentoDellaNotificaV1() {

        String iun =sharedSteps.getIunVersionamento();

            TimelineElementWait timelineElementWait = getTimelineElementCategoryV1("PAYMENT");
            it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElement timelineElement = null;

            for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException exc) {
                    throw new RuntimeException(exc);
                }

                sharedSteps.setSentNotificationV1(b2bClient.getSentNotificationV1(iun));

                logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotificationV1().getTimeline());

                timelineElement = sharedSteps.getSentNotificationV1().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementWait.getTimelineElementCategoryV1())).findAny().orElse(null);
                if (timelineElement != null) {
                    break;
                }
            }
            Assertions.assertNotNull(timelineElement);

    }

    @Then("si attende il corretto pagamento della notifica V2")
    public void siAttendeIlCorrettoPagamentoDellaNotificaV2() {

        String iun =sharedSteps.getIunVersionamento();

        TimelineElementWait timelineElementWait = getTimelineElementCategoryV2("PAYMENT");
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.TimelineElementV20 timelineElement = null;

        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(60000);
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotificationV2(b2bClient.getSentNotificationV2(iun));

            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotificationV2().getTimeline());

            timelineElement = sharedSteps.getSentNotificationV2().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementWait.getTimelineElementCategoryV2())).findAny().orElse(null);
            if (timelineElement != null) {
                break;
            }
        }
        Assertions.assertNotNull(timelineElement);

    }

    @Then("si attende il corretto pagamento della notifica con l' avviso {int} dal destinatario {int}")
    public void siAttendeIlCorrettoPagamentoDellaNotificaConAvvisoDalDestinatario(Integer avviso, Integer destinatario) {
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


        if (timelineElement.getDetails().getRecIndex()==destinatario) {
            boolean esiste = false;
            if (sharedSteps.getSentNotification().getRecipients().get(destinatario).getPayments()!= null){
                for (NotificationPaymentItem notificationPaymentItem: sharedSteps.getSentNotification().getRecipients().get(destinatario).getPayments()) {
                    if (notificationPaymentItem.getPagoPa().getCreditorTaxId().equals(timelineElement.getDetails().getCreditorTaxId()) && notificationPaymentItem.getPagoPa().getNoticeCode().equals(timelineElement.getDetails().getNoticeCode())){
                        esiste = true;
                        break;
                    }

                }
            }
            Assertions.assertTrue(esiste);
            //Assertions.assertTrue(sharedSteps.getSentNotification().getRecipients().get(destinatario).getPayments().get(avviso).getPagoPa().getCreditorTaxId().equals(timelineElement.getDetails().getCreditorTaxId()));
            //Assertions.assertTrue(sharedSteps.getSentNotification().getRecipients().get(destinatario).getPayments().get(avviso).getPagoPa().getNoticeCode().equals(timelineElement.getDetails().getNoticeCode()));
        }

    }

    @Then("si attende il non corretto pagamento della notifica con l' avviso {int} dal destinatario {int}")
    public void siAttendeIlNonCorrettoPagamentoDellaNotificaConAvvisoDalDestinatario(Integer avviso, Integer destinatario) {
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
        Assertions.assertNull(timelineElement);

        //Assertions.assertTrue(sharedSteps.getSentNotification().getRecipients().get(destinatario).getPayments().get(avviso).getPagoPa().getCreditorTaxId().equals(timelineElement.getDetails().getCreditorTaxId()));
        //Assertions.assertTrue(sharedSteps.getSentNotification().getRecipients().get(destinatario).getPayments().get(avviso).getPagoPa().getNoticeCode().equals(timelineElement.getDetails().getNoticeCode()));


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

    @Then("verifica presenza in Timeline dei solo pagamenti di avvisi PagoPA del destinatario {int}")
    public void verificaPresenzaPagamentiSoloPagopa(Integer utente) {
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
                Assertions.assertNull(timelineElement.getDetails().getIdF24());
            }else {
                timelineElement = null;
            }
        }
        Assertions.assertNotNull(timelineElement);

    }

    @Then("verifica non presenza in Timeline di pagamenti con avvisi F24 del destinatario {int}")
    public void verificaNonPresenzaPagamentiF24(Integer utente) {
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

                if (timelineElement.getDetails().getIdF24()!= null){
                    Assertions.assertNull(timelineElement);
                }
            }
        }
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

    @Then("viene verificato che la data della timeline REFINEMENT sia ricezione della raccomandata + 10gg")
    public void verificationDateScheduleRefinementWithRefinementPlus10Days() {

        try {
            OffsetDateTime scheduleDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV20.SEND_ANALOG_FEEDBACK)).findAny().get().getTimestamp().plus(sharedSteps.getSchedulingDaysSuccessAnalogRefinement());
            OffsetDateTime refinementDate = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV20.REFINEMENT)).findAny().get().getTimestamp();
            logger.info("scheduleDate : {}", scheduleDate);
            logger.info("refinementDate : {}", refinementDate);

            Assertions.assertEquals(scheduleDate,refinementDate);

        }catch (AssertionFailedError assertionFailedError) {
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
            timelineElementForDateCalculation = sharedSteps.getTimelineElementByEventId(TimelineElementCategoryV20.DIGITAL_DELIVERY_CREATION_REQUEST.getValue(), dataFromTest);
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
        DateTimeFormatter fmt1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        System.out.println(timelineElement.getDetails().getSchedulingDate().format(fmt1));
        System.out.println(schedulingDate.format(fmt1));
        Assertions.assertTrue(timelineElement.getDetails().getSchedulingDate().format(fmt1).equals(schedulingDate.format(fmt1)));
        //Assertions.assertEquals(timelineElement.getDetails().getSchedulingDate(), schedulingDate);
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

    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} al tentativo {string}")
    public void readingEventUpToTheTimelineElementOfNotificationAtAttempt(String timelineEventCategory, String attempt) {
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

        if (sharedSteps.getSentNotification()!= null){
            Assertions.assertDoesNotThrow(() -> {
                RequestStatus resp =  Assertions.assertDoesNotThrow(() ->
                        this.b2bClient.notificationCancellation(sharedSteps.getSentNotification().getIun()));

                Assertions.assertNotNull(resp);
                Assertions.assertNotNull(resp.getDetails());
                Assertions.assertTrue(resp.getDetails().size()>0);
                Assertions.assertTrue("NOTIFICATION_CANCELLATION_ACCEPTED".equalsIgnoreCase(resp.getDetails().get(0).getCode()));

            });
        } else if (sharedSteps.getSentNotificationV1()!= null) {
            Assertions.assertDoesNotThrow(() -> {
                RequestStatus resp =  Assertions.assertDoesNotThrow(() ->
                        this.b2bClient.notificationCancellation(sharedSteps.getSentNotificationV1().getIun()));

                Assertions.assertNotNull(resp);
                Assertions.assertNotNull(resp.getDetails());
                Assertions.assertTrue(resp.getDetails().size()>0);
                Assertions.assertTrue("NOTIFICATION_CANCELLATION_ACCEPTED".equalsIgnoreCase(resp.getDetails().get(0).getCode()));

            });
        }


    }


    @And("la notifica non può essere annullata dal sistema tramite codice IUN")
    public void notificationCaNotBeCanceledWithIUN() {
        try {
            sharedSteps.getB2bClient().notificationCancellation(sharedSteps.getSentNotification().getIun());
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }

    }



    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con failureCause {string}")
    public void vengonoLettiGliEventiFinoAllElementoDiTimelineDellaNotificaConfailureCause(String timelineEventCategory, String failureCause) {
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
            Assertions.assertEquals(timelineElement.getDetails().getFailureCause(), failureCause);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }



    @Then("vengono letti gli eventi fino all'elemento di timeline della notifica {string} con failureCause {string} per l'utente {int}")
    public void vengonoLettiGliEventiFinoAllElementoDiTimelineDellaNotificaConfailureCausePerUtente(String timelineEventCategory, String failureCause, Integer destinatario) {
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
            Assertions.assertEquals(timelineElement.getDetails().getFailureCause(), failureCause);
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
        int delay = 0;
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
                    delay = Integer.parseInt(sharedSteps.getSchedulingDaysFailureDigitalRefinementString().replace("m",""));
                    break;
                } else if (element.getCategory().getValue().equals("SEND_DIGITAL_FEEDBACK") && element.getDetails().getRecIndex().equals(destinatario) && evento.equalsIgnoreCase("SEND_DIGITAL_FEEDBACK")) {
                    if ("OK".equalsIgnoreCase(element.getDetails().getResponseStatus().getValue())) {
                        digitalDeliveryCreationRequestDate = element.getDetails().getNotificationDate();
                        delay = Integer.parseInt(sharedSteps.getSchedulingDaysSuccessDigitalRefinementString().replace("m",""));
                        break;
                    }
                }
            }
            logger.info("TIMELINE ELEMENT: {} , DETAILS {} , SCHEDULING DATE {}",
                    timelineElement,timelineElement.getDetails(), timelineElement.getDetails().getSchedulingDate());
            Assertions.assertNotNull(timelineElement);
            Assertions.assertNotNull(timelineElement.getDetails().getSchedulingDate());

            //Duration ss = sharedSteps.getSchedulingDaysSuccessDigitalRefinement();
            //DateTimeFormatter fmt1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

            Long schedulingDateMillis = timelineElement.getDetails().getSchedulingDate().toInstant().toEpochMilli();
            Long digitalDeliveryCreationMillis = digitalDeliveryCreationRequestDate.toInstant().toEpochMilli();
            Long diff = schedulingDateMillis - digitalDeliveryCreationMillis;
            Long delayMillis = ((Long.valueOf(delay)*60)*1000); //TODO: refactor
            Long delta = Long.valueOf(sharedSteps.getSchedulingDelta());
            logger.info("PRE-ASSERTION: schedulingDateMillis {}, digitalDeliveryCreationMillis {}, diff {}, delayMillis {}, delta {}",
                    schedulingDateMillis,digitalDeliveryCreationMillis,diff,delayMillis,delta);
            Assertions.assertTrue(diff <= delayMillis+delta && diff >= delayMillis-delta);

            //Assertions.assertTrue(timelineElement.getDetails().getSchedulingDate().format(fmt1).equals(digitalDeliveryCreationRequestDate.plusMinutes(delay).format(fmt1)));

        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }


    @Then("viene verificato che nell'elemento di timeline della notifica {string} sia presente il campo notRefinedRecipientIndex")
    public void vieneVerificatoCheElementoTimelineSianoConfiguratoCampoNotRefinedRecipientIndex(String timelineEventCategory) {
        TimelineElementWait timelineElementWait = getTimelineElementCategory(timelineEventCategory);

        TimelineElementV20 timelineElement = null;

        sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
        timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementWait.getTimelineElementCategory())).findAny().orElse(null);
        try {
            logger.info("TIMELINE_ELEMENT: " + timelineElement);
            Assertions.assertNotNull(timelineElement);
            Assertions.assertNotNull(timelineElement.getDetails().getNotRefinedRecipientIndexes());
            Assertions.assertTrue(timelineElement.getDetails().getNotRefinedRecipientIndexes().size()>0);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }


}
