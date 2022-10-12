package it.pagopa.pn.client.b2b.pa.cucumber.test.steps;

import io.cucumber.java.After;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.impl.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.testclient.IPnWebRecipientClient;
import it.pagopa.pn.client.b2b.pa.testclient.IPnWebhookB2bClient;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.*;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.NotificationStatus;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.TimelineElementCategory;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.lang.invoke.MethodHandles;
import java.util.List;


public class AvanzamentoNotificheB2bSteps {

    @Autowired
    IPnWebhookB2bClient webhookB2bClient;

    @Autowired
    IPnPaB2bClient b2bClient;

    @Autowired
    private IPnWebRecipientClient webRecipientClient;

    @Autowired
    private PnPaB2bUtils b2bUtils;

    private StreamCreationRequest streamCreationRequest;
    private StreamMetadataResponse eventStream;
    private NewNotificationRequest notificationRequest;
    private NewNotificationResponse newNotificationRequest;
    private FullSentNotification notificationResponseComplete;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Given("nuovo stream {string} con eventType {string}")
    public void nuovoStreamDiEventiConEventType(String title, String eventType) {
        streamCreationRequest = new StreamCreationRequest();
        streamCreationRequest.title(title);
        //STATUS, TIMELINE
        streamCreationRequest.eventType(eventType.equalsIgnoreCase("STATUS") ?
                StreamCreationRequest.EventTypeEnum.STATUS : StreamCreationRequest.EventTypeEnum.TIMELINE);
    }

    @When("viene creato il nuovo stream")
    public void vieneCreatoUnNuovoStreamDiNotifica() {
        this.eventStream = webhookB2bClient.createEventStream(streamCreationRequest);
    }

    @Then("lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id")
    public void laStreamEStatoCreatoEVieneCorrettamenteRecuperatoDalSistema() {
        Assertions.assertDoesNotThrow(() -> {
            StreamMetadataResponse eventStream = webhookB2bClient.getEventStream(this.eventStream.getStreamId());
        });
    }


    @And("vengono letti gli eventi dello stream")
    public void vengonoLettiGliEventiDelloStream() {
        Assertions.assertDoesNotThrow(() -> {
            List<ProgressResponseElement> progressResponseElements = webhookB2bClient.consumeEventStream(this.eventStream.getStreamId(), null);
            logger.info("EventProgress: " + progressResponseElements);
        });
    }

    @Given("viene generata una nuova notifica")
    public void vieneGenerataUnaNuovaNotifica(@Transpose NewNotificationRequest notificationRequest) {
        this.notificationRequest = notificationRequest;
    }

    @And("destinatario della notifica")
    public void destinatarioDellaNotifica(@Transpose NotificationRecipient recipient) {
        this.notificationRequest.addRecipientsItem(recipient);
    }


    @When("la notifica viene inviata e si attende che lo stato diventi ACCEPTED")
    public void laNotificaVieneInviataESiAttendeCheLoStatoDiventiACCEPTED() {
        Assertions.assertDoesNotThrow(() -> {
            NewNotificationResponse newNotificationRequest = b2bUtils.uploadNotification(notificationRequest);
            notificationResponseComplete = b2bUtils.waitForRequestAcceptation(newNotificationRequest);
        });
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            logger.error("Thread.sleep error retry");
            throw new RuntimeException(e);
        }
    }


    @And("vengono letti gli eventi dello stream fino allo stato {string}")
    public void vengonoLettiGliEventiDelloStreamFinoAlloStato(String status) {
        NotificationStatus notificationStatus;
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus notificationInternalStatus;
        switch (status) {
            case "ACCEPTED":
                notificationStatus = NotificationStatus.ACCEPTED;
                notificationInternalStatus =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.ACCEPTED;
                break;
            case "DELIVERING":
                notificationStatus = NotificationStatus.DELIVERING;
                notificationInternalStatus =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.DELIVERING;
                break;
            case "DELIVERED":
                notificationStatus = NotificationStatus.DELIVERED;
                notificationInternalStatus =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.DELIVERED;
                break;
            case "CANCELLED":
                notificationStatus = NotificationStatus.CANCELLED;
                notificationInternalStatus =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.CANCELLED;
                break;
            case "EFFECTIVE_DATE":
                notificationStatus = NotificationStatus.EFFECTIVE_DATE;
                notificationInternalStatus =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.EFFECTIVE_DATE;
                break;
            default:
                throw new IllegalArgumentException();
        }
        List<ProgressResponseElement> progressResponseElements;
        ProgressResponseElement progressResponseElement = null;
        int wait = 48;
        boolean finded = false;
        for (int i = 0; i < wait; i++) {
            progressResponseElements = webhookB2bClient.consumeEventStream(this.eventStream.getStreamId(), null);

            progressResponseElement = progressResponseElements.stream().filter(elem -> (elem.getIun().equals(notificationResponseComplete.getIun()) && elem.getNewStatus().equals(notificationStatus))).findAny().orElse(null);
            notificationResponseComplete = b2bClient.getSentNotification(notificationResponseComplete.getIun());
            logger.info("IUN: " + notificationResponseComplete.getIun());
            logger.info("*******************************************" + '\n');
            logger.info("EventProgress: " + progressResponseElements);
            logger.info("*******************************************" + '\n');
            logger.info("NOTIFICATION_STATUS_HISTORY: " + notificationResponseComplete.getNotificationStatusHistory());
            NotificationStatusHistoryElement notificationStatusHistoryElement = notificationResponseComplete.getNotificationStatusHistory().stream().filter(elem -> elem.getStatus().equals(notificationInternalStatus)).findAny().orElse(null);
            if (notificationStatusHistoryElement != null && !finded) {
                wait = i + 2;
                finded = true;
            }
            if (progressResponseElement != null) {
                break;
            }
            try {
                Thread.sleep(10 * 1000L);
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
        }
        Assertions.assertNotNull(progressResponseElement);
        logger.info("EventProgress: " + progressResponseElement);
    }


    @Then("vengono letti gli eventi dello stream fino all'elemento di timeline {string}")
    public void vengonoLettiGliEventiDelloStreamFinoAllElementoDiTimeline(String timelineEventCategory) {
        TimelineElementCategory timelineElementCategory;
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory timelineElementInternalCategory;
        switch (timelineEventCategory) {
            case "REQUEST_ACCEPTED":
                timelineElementCategory = TimelineElementCategory.REQUEST_ACCEPTED;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory.REQUEST_ACCEPTED;
                break;
            case "AAR_GENERATION":
                timelineElementCategory = TimelineElementCategory.AAR_GENERATION;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory.AAR_GENERATION;
                break;
            case "GET_ADDRESS":
                timelineElementCategory = TimelineElementCategory.GET_ADDRESS;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory.GET_ADDRESS;
                break;
            case "SEND_DIGITAL_DOMICILE":
                timelineElementCategory = TimelineElementCategory.SEND_DIGITAL_DOMICILE;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory.SEND_DIGITAL_DOMICILE;
                break;
            case "NOTIFICATION_VIEWED":
                timelineElementCategory = TimelineElementCategory.NOTIFICATION_VIEWED;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory.NOTIFICATION_VIEWED;
                break;
            case "SEND_COURTESY_MESSAGE":
                timelineElementCategory = TimelineElementCategory.SEND_COURTESY_MESSAGE;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory.SEND_COURTESY_MESSAGE;
                break;
            default:
                throw new IllegalArgumentException();
        }
        List<ProgressResponseElement> progressResponseElements;
        ProgressResponseElement progressResponseElement = null;
        int wait = 48;
        boolean finded = false;
        for (int i = 0; i < wait; i++) {
            progressResponseElements = webhookB2bClient.consumeEventStream(this.eventStream.getStreamId(), null);

            progressResponseElement = progressResponseElements.stream().filter(elem -> (elem.getIun().equals(notificationResponseComplete.getIun()) && elem.getTimelineEventCategory().equals(timelineElementCategory))).findAny().orElse(null);
            notificationResponseComplete = b2bClient.getSentNotification(notificationResponseComplete.getIun());
            logger.info("IUN: " + notificationResponseComplete.getIun());
            logger.info("*******************************************" + '\n');
            logger.info("EventProgress: " + progressResponseElements);
            logger.info("*******************************************" + '\n');
            logger.info("NOTIFICATION_TIMELINE: " + notificationResponseComplete.getTimeline());
            TimelineElement timelineElement = notificationResponseComplete.getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().orElse(null);
            if (timelineElement != null && !finded) {
                wait = i + 2;
                finded = true;
            }

            if (progressResponseElement != null) {
                break;
            }
            try {
                Thread.sleep(10 * 1000L);
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
        }
        Assertions.assertNotNull(progressResponseElement);
        logger.info("EventProgress: " + progressResponseElement);
    }

    @And("il destinatario legge la notifica")
    public void ilDestinatarioLeggeLaNotifica() {
        Assertions.assertDoesNotThrow(() -> {
            webRecipientClient.getReceivedNotification(notificationResponseComplete.getIun(), null);
        });
        try {
            Thread.sleep(50 * 1000L);
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Then("vengono letti gli eventi dello stream fino allo stato della notifica {string}")
    public void vengonoLettiGliEventiDelloStreamFinoAlloStatoDellaNotifica(String status) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus notificationInternalStatus;
        switch (status) {
            case "ACCEPTED":
                notificationInternalStatus =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.ACCEPTED;
                break;
            case "DELIVERING":
                notificationInternalStatus =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.DELIVERING;
                break;
            case "DELIVERED":
                notificationInternalStatus =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.DELIVERED;
                break;
            case "CANCELLED":
                notificationInternalStatus =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.CANCELLED;
                break;
            case "EFFECTIVE_DATE":
                notificationInternalStatus =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.EFFECTIVE_DATE;
                break;
            default:
                throw new IllegalArgumentException();
        }

        NotificationStatusHistoryElement notificationStatusHistoryElement = null;

        for (int i = 0; i < 20; i++) {
            notificationResponseComplete = b2bClient.getSentNotification(notificationResponseComplete.getIun());

            logger.info("NOTIFICATION_STATUS_HISTORY: " + notificationResponseComplete.getNotificationStatusHistory());

            notificationStatusHistoryElement = notificationResponseComplete.getNotificationStatusHistory().stream().filter(elem -> elem.getStatus().equals(notificationInternalStatus)).findAny().orElse(null);

            if (notificationStatusHistoryElement != null) {
                break;
            }
            try {
                Thread.sleep(10 * 1000L);
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
        }
        Assertions.assertNotNull(notificationStatusHistoryElement);

    }

    @Then("vengono letti gli eventi dello stream fino all'elemento di timeline della notifica {string}")
    public void vengonoLettiGliEventiDelloStreamFinoAllElementoDiTimelineDellaNotifica(String timelineEventCategory) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory timelineElementInternalCategory;
        switch (timelineEventCategory) {
            case "REQUEST_ACCEPTED":
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory.REQUEST_ACCEPTED;
                break;
            case "AAR_GENERATION":
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory.AAR_GENERATION;
                break;
            case "GET_ADDRESS":
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory.GET_ADDRESS;
                break;
            case "SEND_DIGITAL_DOMICILE":
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory.SEND_DIGITAL_DOMICILE;
                break;
            case "NOTIFICATION_VIEWED":
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory.NOTIFICATION_VIEWED;
                break;
            case "SEND_COURTESY_MESSAGE":
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory.SEND_COURTESY_MESSAGE;
                break;
            default:
                throw new IllegalArgumentException();
        }
        TimelineElement timelineElement = null;

        for (int i = 0; i < 20; i++) {
            notificationResponseComplete = b2bClient.getSentNotification(notificationResponseComplete.getIun());

            logger.info("NOTIFICATION_TIMELINE: " + notificationResponseComplete.getTimeline());

            timelineElement = notificationResponseComplete.getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().orElse(null);
            if (timelineElement != null) {
                break;
            }
            try {
                Thread.sleep(10 * 1000L);
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
        }
        Assertions.assertNotNull(timelineElement);
    }

    @Then("si verifica che la notifica abbia lo stato VIEWED")
    public void siVerificaCheLaNotificaAbbiaLoStatoVIEWED() {
        notificationResponseComplete = b2bClient.getSentNotification(notificationResponseComplete.getIun());
        Assertions.assertNotNull(notificationResponseComplete.getNotificationStatusHistory().stream().filter(elem -> elem.getStatus().equals(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.VIEWED)).findAny().orElse(null));
    }


    @Then("si verifica nello stream che la notifica abbia lo stato VIEWED")
    public void siVerificaNelloStreamCheLaNotificaAbbiaLoStatoVIEWED() {
        List<ProgressResponseElement> progressResponseElements = webhookB2bClient.consumeEventStream(this.eventStream.getStreamId(), null);
        Assertions.assertNotNull(progressResponseElements.stream().filter(elem -> (elem.getIun().equals(notificationResponseComplete.getIun()) && elem.getNewStatus().equals(NotificationStatus.VIEWED))).findAny().orElse(null));
    }


    @And("viene cancellato lo stream creato")
    public void vieneCancellatoLoStreamCreato() {
        webhookB2bClient.deleteEventStream(eventStream.getStreamId());
    }

    @And("viene verificata al corretta cancellazione")
    public void vieneVerificataAlCorrettaCancellazione() {
        List<StreamListElement> streamListElements = webhookB2bClient.listEventStreams();
        StreamListElement streamListElement = streamListElements.stream().filter(elem -> elem.getStreamId() == eventStream.getStreamId()).findAny().orElse(null);
        Assertions.assertNull(streamListElement);
    }


    @After("@clean")
    public void doSomethingAfter() {
        webhookB2bClient.deleteEventStream(eventStream.getStreamId());
        List<StreamListElement> streamListElements = webhookB2bClient.listEventStreams();
        StreamListElement streamListElement = streamListElements.stream().filter(elem -> elem.getStreamId() == eventStream.getStreamId()).findAny().orElse(null);
        logger.info("STREAM SIZE: " + streamListElements.size());
        logger.info("UUID DELETED: " + eventStream.getStreamId());
        Assertions.assertNull(streamListElement);
    }

}


