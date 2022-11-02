package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.impl.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.testclient.*;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.NotificationStatus;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.TimelineElementCategory;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.*;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;


public class AvanzamentoNotificheWebhookB2bSteps {


    private final IPnWebhookB2bClient webhookB2bClient;
    private final IPnPaB2bClient b2bClient;
    private final IPnWebRecipientClient webRecipientClient;
    private final PnPaB2bUtils b2bUtils;
    private final SharedSteps sharedSteps;

    private List<StreamCreationRequest> streamCreationRequestList;
    private List<StreamMetadataResponse> eventStreamList;
    private Integer requestNumber;
    private HttpStatusCodeException notificationError;
    private NewNotificationRequest notificationRequest;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    public AvanzamentoNotificheWebhookB2bSteps(IPnWebhookB2bClient webhookB2bClient, IPnWebRecipientClient webRecipientClient, SharedSteps sharedSteps) {
        this.webhookB2bClient = webhookB2bClient;
        this.webRecipientClient = webRecipientClient;
        this.sharedSteps = sharedSteps;
        this.b2bClient = sharedSteps.getB2bClient();
        this.b2bUtils = sharedSteps.getB2bUtils();
    }

    @Given("si predispo(ngono)(ne) {int} nuov(i)(o) stream denominat(i)(o) {string} con eventType {string}")
    public void vengonoPredispostiNuoviStreamDenominatiConEventType(int number, String title, String eventType) {
        this.streamCreationRequestList = new LinkedList<>();
        this.requestNumber = number;
        for(int i = 0; i<number; i++){
            StreamCreationRequest streamRequest = new StreamCreationRequest();
            streamRequest.title(title);
            //STATUS, TIMELINE
            streamRequest.eventType(eventType.equalsIgnoreCase("STATUS") ?
                    StreamCreationRequest.EventTypeEnum.STATUS : StreamCreationRequest.EventTypeEnum.TIMELINE);
            streamCreationRequestList.add(streamRequest);
        }
    }

    @When("si crea(no) i(l) nuov(o)(i) stream")
    public void vieneCreatoUnNuovoStreamDiNotifica() {
        this.eventStreamList = new LinkedList<>();
        for(StreamCreationRequest request: streamCreationRequestList){
            try{
            StreamMetadataResponse eventStream = webhookB2bClient.createEventStream(request);
            this.eventStreamList.add(eventStream);
            }catch (HttpStatusCodeException e) {
                this.notificationError = e;
            }
        }
    }

    @And("si cancella(no) (lo)(gli) stream creat(o)(i)")
    public void vieneCancellatoLoStreamCreato() {
        for(StreamMetadataResponse eventStream: eventStreamList){
            webhookB2bClient.deleteEventStream(eventStream.getStreamId());
        }

    }

    @And("viene verificata la corretta cancellazione")
    public void vieneVerificataAlCorrettaCancellazione() {
        List<StreamListElement> streamListElements = webhookB2bClient.listEventStreams();
        for(StreamMetadataResponse eventStream: eventStreamList){
            StreamListElement streamListElement = streamListElements.stream().filter(elem -> elem.getStreamId() == eventStream.getStreamId()).findAny().orElse(null);
            Assertions.assertNull(streamListElement);
        }
    }


    @Then("lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id")
    public void laStreamEStatoCreatoEVieneCorrettamenteRecuperatoDalSistema() {
        Assertions.assertDoesNotThrow(() -> {
            StreamMetadataResponse eventStream = webhookB2bClient.getEventStream(this.eventStreamList.get(0).getStreamId());
        });
    }


    @And("vengono letti gli eventi dello stream")
    public void vengonoLettiGliEventiDelloStream() {
        Assertions.assertDoesNotThrow(() -> {
            List<ProgressResponseElement> progressResponseElements = webhookB2bClient.consumeEventStream(this.eventStreamList.get(0).getStreamId(), null);
            logger.info("EventProgress: " + progressResponseElements);
        });
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
            progressResponseElements = webhookB2bClient.consumeEventStream(this.eventStreamList.get(0).getStreamId(), null);

            progressResponseElement = progressResponseElements.stream().filter(elem -> (elem.getIun().equals(sharedSteps.getSentNotification().getIun()) && elem.getNewStatus().equals(notificationStatus))).findAny().orElse(null);
            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            logger.info("IUN: " + sharedSteps.getSentNotification().getIun());
            logger.info("*******************************************" + '\n');
            logger.info("EventProgress: " + progressResponseElements);
            logger.info("*******************************************" + '\n');
            logger.info("NOTIFICATION_STATUS_HISTORY: " + sharedSteps.getSentNotification().getNotificationStatusHistory());
            NotificationStatusHistoryElement notificationStatusHistoryElement = sharedSteps.getSentNotification().getNotificationStatusHistory().stream().filter(elem -> elem.getStatus().equals(notificationInternalStatus)).findAny().orElse(null);
            if (notificationStatusHistoryElement != null && !finded) {
                wait = i + 4;
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
            ResponseEntity<List<ProgressResponseElement>> listResponseEntity = webhookB2bClient.consumeEventStreamHttp(this.eventStreamList.get(0).getStreamId(), null);
            String retryAfter = listResponseEntity.getHeaders().get("retry-after").get(0);
            progressResponseElements = listResponseEntity.getBody();
            progressResponseElement = progressResponseElements.stream().filter(elem -> (elem.getIun().equals(sharedSteps.getSentNotification().getIun()) && elem.getTimelineEventCategory().equals(timelineElementCategory))).findAny().orElse(null);
            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            logger.info("IUN: " + sharedSteps.getSentNotification().getIun());
            logger.info("*******************************************" + '\n');
            logger.info("EventProgress: " + progressResponseElements);
            logger.info("*******************************************" + '\n');
            logger.info("NOTIFICATION_TIMELINE: " + sharedSteps.getSentNotification().getTimeline());
            TimelineElement timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().orElse(null);
            if (timelineElement != null && !finded) {
                wait = i + 4;
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
            webRecipientClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(), null);
        });
        try {
            Thread.sleep(50 * 1000L);
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }
    }


    @Then("si verifica nello stream che la notifica abbia lo stato VIEWED")
    public void siVerificaNelloStreamCheLaNotificaAbbiaLoStatoVIEWED() {
        List<ProgressResponseElement> progressResponseElements = webhookB2bClient.consumeEventStream(this.eventStreamList.get(0).getStreamId(), null);
        Assertions.assertNotNull(progressResponseElements.stream().filter(elem -> (elem.getIun().equals(sharedSteps.getSentNotification().getIun()) && elem.getNewStatus().equals(NotificationStatus.VIEWED))).findAny().orElse(null));
    }


    @After("@clean")
    public void doSomethingAfter() {
        for(StreamMetadataResponse eventStream: eventStreamList){
            webhookB2bClient.deleteEventStream(eventStream.getStreamId());
            List<StreamListElement> streamListElements = webhookB2bClient.listEventStreams();
            StreamListElement streamListElement = streamListElements.stream().filter(elem -> elem.getStreamId() == eventStream.getStreamId()).findAny().orElse(null);
            logger.info("STREAM SIZE: " + streamListElements.size());
            logger.info("UUID DELETED: " + this.eventStreamList.get(0).getStreamId());
            Assertions.assertNull(streamListElement);
        }
    }

    @Then("l'ultima creazione ha prodotto un errore con status code {string}")
    public void lUltimaCreazioneHaProdottoUnErroreConStatusCode(String statusCode) {
        List<StreamListElement> streamListElements = webhookB2bClient.listEventStreams();
        System.out.println("streamListElements: "+streamListElements.size());
        System.out.println("eventStreamList: "+eventStreamList.size());
        System.out.println("requestNumber: "+requestNumber);
        Assertions.assertTrue((this.notificationError != null) &&
                (this.notificationError.getStatusCode().toString().substring(0,3).equals(statusCode)) && (eventStreamList.size() == (requestNumber-1)));
    }


    @Given("vengono cancellati tutti gli stream presenti")
    public void eliminaTutto() {
        List<StreamListElement> streamListElements = webhookB2bClient.listEventStreams();
        for(StreamListElement elem: streamListElements){
            webhookB2bClient.deleteEventStream(elem.getStreamId());
        }
    }





}


