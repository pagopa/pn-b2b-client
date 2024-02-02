package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebRecipientClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebhookB2bClient;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.NotificationStatus;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.ProgressResponseElement;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.StreamCreationRequest;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.StreamListElement;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.StreamMetadataResponse;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.TimelineElementCategoryV20;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class AvanzamentoNotificheWebhookB2bSteps {


    private final IPnWebhookB2bClient webhookB2bClient;
    private final IPnPaB2bClient b2bClient;
    private final IPnWebRecipientClient webRecipientClient;
    private final SharedSteps sharedSteps;

    private LinkedList<ProgressResponseElement> progressResponseElementList = new LinkedList<>();
    private List<StreamCreationRequest> streamCreationRequestList;
    private List<StreamMetadataResponse> eventStreamList;
    private Integer requestNumber;
    private HttpStatusCodeException notificationError;
    private static final Logger logger = LoggerFactory.getLogger(AvanzamentoNotificheWebhookB2bSteps.class);

    @Autowired
    public AvanzamentoNotificheWebhookB2bSteps(IPnWebhookB2bClient webhookB2bClient, SharedSteps sharedSteps) {
        this.sharedSteps = sharedSteps;
        this.webhookB2bClient = webhookB2bClient;
        this.webRecipientClient = sharedSteps.getWebRecipientClient();
        this.b2bClient = sharedSteps.getB2bClient();
    }

    @Given("si predispo(ngono)(ne) {int} nuov(i)(o) stream denominat(i)(o) {string} con eventType {string}")
    public void setUpStreamsWithEventType(int number, String title, String eventType) {
        this.streamCreationRequestList = new LinkedList<>();
        this.requestNumber = number;
        for(int i = 0; i<number; i++){
            StreamCreationRequest streamRequest = new StreamCreationRequest();
            streamRequest.setTitle(title);
            //STATUS, TIMELINE
            streamRequest.setEventType(eventType.equalsIgnoreCase("STATUS") ?
                    StreamCreationRequest.EventTypeEnum.STATUS : StreamCreationRequest.EventTypeEnum.TIMELINE);
            streamCreationRequestList.add(streamRequest);
            streamRequest.setFilterValues(new LinkedList<>());
        }
    }

    @Given("si predispo(ngono)(ne) {int} nuov(i)(o) stream V2 denominat(i)(o) {string} con eventType {string}")
    public void setUpStreamsWithEventTypeV2(int number, String title, String eventType) {
        this.streamCreationRequestList = new LinkedList<>();
        this.requestNumber = number;
        for(int i = 0; i<number; i++){
            StreamCreationRequest streamRequest = new StreamCreationRequest();
            streamRequest.setTitle(title);
            //STATUS, TIMELINE

            if(eventType.equalsIgnoreCase("STATUS")){
                streamRequest.setEventType(StreamCreationRequest.EventTypeEnum.STATUS);

                List<String> filteredValues = Arrays.stream(NotificationStatus.values())
                        .map(Enum::toString)
                        .collect(Collectors.toList());
                streamRequest.setFilterValues(filteredValues);
            }else{
                streamRequest.setEventType(StreamCreationRequest.EventTypeEnum.TIMELINE);

                List<String> filteredValues = Arrays.stream(TimelineElementCategoryV20.values())
                        .map(Enum::toString)
                        .collect(Collectors.toList());
                streamRequest.setFilterValues(filteredValues);
            }

            streamCreationRequestList.add(streamRequest);
        }
    }

    private void setPaWebhook(String pa){
        switch (pa){
            case "Comune_1":
                webhookB2bClient.setApiKeys(SettableApiKey.ApiKeyType.MVP_1);
                sharedSteps.selectPA(pa);
                break;
            case "Comune_2":
                webhookB2bClient.setApiKeys(SettableApiKey.ApiKeyType.MVP_2);
                sharedSteps.selectPA(pa);
                break;
            case "Comune_Multi":
                webhookB2bClient.setApiKeys(SettableApiKey.ApiKeyType.GA);
                sharedSteps.selectPA(pa);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @When("si crea(no) i(l) nuov(o)(i) stream per il {string}")
    public void createdStream(String pa) {
        this.eventStreamList = new LinkedList<>();
        setPaWebhook(pa);
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
    public void deleteStream() {
        for(StreamMetadataResponse eventStream: eventStreamList){
            webhookB2bClient.deleteEventStream(eventStream.getStreamId());
        }

    }

    @And("viene verificata la corretta cancellazione")
    public void verifiedTheCorrectDeletion() {
        List<StreamListElement> streamListElements = webhookB2bClient.listEventStreams();
        for(StreamMetadataResponse eventStream: eventStreamList){
            StreamListElement streamListElement = streamListElements.stream().filter(elem -> elem.getStreamId() == eventStream.getStreamId()).findAny().orElse(null);
            Assertions.assertNull(streamListElement);
        }
    }


    @Then("lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id")
    public void streamBeenCreatedAndCorrectlyRetrievedByStreamId() {
        Assertions.assertDoesNotThrow(() -> {
            StreamMetadataResponse eventStream = webhookB2bClient.getEventStream(this.eventStreamList.get(0).getStreamId());
        });
    }


    @And("vengono letti gli eventi dello stream")
    public void readStreamEvents() {
        Assertions.assertDoesNotThrow(() -> {
            List<ProgressResponseElement> progressResponseElements = webhookB2bClient.consumeEventStream(this.eventStreamList.get(0).getStreamId(), null);
            logger.info("EventProgress: " + progressResponseElements);
        });
    }



    @And("vengono letti gli eventi dello stream del {string} fino allo stato {string}")
    public void readStreamEventsState(String pa,String status) {
        Integer numCheck = 10;
        Integer waiting = sharedSteps.getWorkFlowWait();

        setPaWebhook(pa);
        NotificationStatus notificationStatus;
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus notificationInternalStatus;
        switch (status) {
            case "ACCEPTED":
                numCheck = 2;
                notificationStatus = NotificationStatus.ACCEPTED;
                notificationInternalStatus =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.ACCEPTED;
                break;
            case "DELIVERING":
                numCheck = 2;
                waiting = waiting * 4;
                notificationStatus = NotificationStatus.DELIVERING;
                notificationInternalStatus =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.DELIVERING;
                break;
            case "DELIVERED":
                numCheck = 3;
                waiting = waiting * 4;
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

            case "REFUSED":
                notificationStatus = NotificationStatus.REFUSED;
                notificationInternalStatus =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.REFUSED;
                break;

            default:
                throw new IllegalArgumentException();
        }
        ProgressResponseElement progressResponseElement = null;

        boolean finded = false;
        for (int i = 0; i < numCheck; i++) {

            try {
                Thread.sleep(waiting);
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            NotificationStatusHistoryElement notificationStatusHistoryElement = sharedSteps.getSentNotification().getNotificationStatusHistory().stream().filter(elem -> elem.getStatus().equals(notificationInternalStatus)).findAny().orElse(null);

            if (notificationStatusHistoryElement != null) {
                finded = true;
                break;
            }
        }

        Assertions.assertTrue(finded);
        for (int i = 0; i < 4; i++) {
            progressResponseElement = searchInWebhook(notificationStatus,null,0);
            logger.debug("PROGRESS-ELEMENT: "+progressResponseElement);

            if (progressResponseElement != null) {
                break;
            }

            try {
                Thread.sleep(sharedSteps.getWait());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
        }

        try{
            Assertions.assertNotNull(progressResponseElement);
            logger.info("EventProgress: " + progressResponseElement);
        }catch(AssertionFailedError assertionFailedError){
            String message = assertionFailedError.getMessage()+
                   " {IUN: "+sharedSteps.getSentNotification().getIun()+" -WEBHOOK: "+this.eventStreamList.get(0).getStreamId()+" }";
            throw new AssertionFailedError(message,assertionFailedError.getExpected(),assertionFailedError.getActual(),assertionFailedError.getCause());
        }

    }


    @And("vengono letti gli eventi dello stream del {string} del validatore fino allo stato {string}")
    public void readStreamEventsStateValidatore(String pa,String status) {
        setPaWebhook(pa);
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
        ProgressResponseElement progressResponseElement = null;
        int wait = 48;
        boolean finded = false;
        for (int i = 0; i < wait; i++) {
            progressResponseElement = searchInWebhook(notificationStatus,null,0);
            logger.debug("PROGRESS-ELEMENT: "+progressResponseElement);

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
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
        try{
            Assertions.assertNotNull(progressResponseElement);
            logger.info("EventProgress: " + progressResponseElement);
        }catch(AssertionFailedError assertionFailedError){
            String message = assertionFailedError.getMessage()+
                    " {IUN: "+sharedSteps.getSentNotification().getIun()+" -WEBHOOK: "+this.eventStreamList.get(0).getStreamId()+" }";
            throw new AssertionFailedError(message,assertionFailedError.getExpected(),assertionFailedError.getActual(),assertionFailedError.getCause());
        }

    }

    @And("vengono letti gli eventi dello stream del {string} con la verifica di Allegato non trovato")
    public void readStreamEventsStateRefused(String pa) {

        setPaWebhook(pa);
        NotificationStatus notificationStatus;
        notificationStatus = NotificationStatus.REFUSED;
        ProgressResponseElement progressResponseElement = null;

        for (int i = 0; i < 4; i++) {
            progressResponseElement = searchInWebhookFileNotFound(notificationStatus,null,0);
            logger.debug("PROGRESS-ELEMENT: "+progressResponseElement);

            if (progressResponseElement != null) {
                break;
            }

            try {
                Thread.sleep(sharedSteps.getWait());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
        }

        try{
            Assertions.assertNotNull(progressResponseElement);
            logger.info("EventProgress: " + progressResponseElement);
        }catch(AssertionFailedError assertionFailedError){
            String message = assertionFailedError.getMessage()+
                    " {IUN: "+sharedSteps.getSentNotification().getIun()+" -WEBHOOK: "+this.eventStreamList.get(0).getStreamId()+" }";
            throw new AssertionFailedError(message,assertionFailedError.getExpected(),assertionFailedError.getActual(),assertionFailedError.getCause());
        }
    }

    @Then("vengono letti gli eventi dello stream del {string} fino all'elemento di timeline {string}")
    public void readStreamTimelineElement(String pa,String timelineEventCategory) {
        setPaWebhook(pa);
        TimelineElementCategoryV20 timelineElementCategory;
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20 timelineElementInternalCategory;

        Integer numCheck = 10;
        Integer waiting = sharedSteps.getWorkFlowWait();

        switch (timelineEventCategory) {
            case "REQUEST_ACCEPTED":
                numCheck = 2;
                timelineElementCategory = TimelineElementCategoryV20.REQUEST_ACCEPTED;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20.REQUEST_ACCEPTED;
                break;
            case "AAR_GENERATION":
                numCheck = 2;
                waiting = waiting * 2;
                timelineElementCategory = TimelineElementCategoryV20.AAR_GENERATION;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20.AAR_GENERATION;
                break;
            case "GET_ADDRESS":
                numCheck = 2;
                waiting = waiting * 2;
                timelineElementCategory = TimelineElementCategoryV20.GET_ADDRESS;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20.GET_ADDRESS;
                break;
            case "SEND_DIGITAL_DOMICILE":
                numCheck = 2;
                waiting = waiting * 2;
                timelineElementCategory = TimelineElementCategoryV20.SEND_DIGITAL_DOMICILE;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20.SEND_DIGITAL_DOMICILE;
                break;
            case "NOTIFICATION_VIEWED":
                numCheck = 2;
                waiting = waiting * 2;
                timelineElementCategory = TimelineElementCategoryV20.NOTIFICATION_VIEWED;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20.NOTIFICATION_VIEWED;
                break;
            case "SEND_COURTESY_MESSAGE":
                numCheck = 10;
                timelineElementCategory = TimelineElementCategoryV20.SEND_COURTESY_MESSAGE;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20.SEND_COURTESY_MESSAGE;
                break;
            case "DIGITAL_FAILURE_WORKFLOW":
                numCheck = 9;
                timelineElementCategory = TimelineElementCategoryV20.DIGITAL_FAILURE_WORKFLOW;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20.DIGITAL_FAILURE_WORKFLOW;
                break;
            case "DIGITAL_SUCCESS_WORKFLOW":
                numCheck = 2;
                waiting = waiting * 3;
                timelineElementCategory = TimelineElementCategoryV20.DIGITAL_SUCCESS_WORKFLOW;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20.DIGITAL_SUCCESS_WORKFLOW;
                break;
            case "SCHEDULE_ANALOG_WORKFLOW":
                numCheck = 2;
                waiting = waiting * 3;
                timelineElementCategory = TimelineElementCategoryV20.SCHEDULE_ANALOG_WORKFLOW;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20.SCHEDULE_ANALOG_WORKFLOW;
                break;
            case "NOT_HANDLED":
                numCheck = 9;
                timelineElementCategory = TimelineElementCategoryV20.NOT_HANDLED;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20.NOT_HANDLED;
                break;
            case "SEND_DIGITAL_FEEDBACK":
                numCheck = 2;
                waiting = waiting * 3;
                timelineElementCategory = TimelineElementCategoryV20.SEND_DIGITAL_FEEDBACK;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20.SEND_DIGITAL_FEEDBACK;
                break;
            case "SEND_ANALOG_PROGRESS":
                numCheck = 8;
                timelineElementCategory = TimelineElementCategoryV20.SEND_ANALOG_PROGRESS;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20.SEND_ANALOG_PROGRESS;
                break;
            case "SEND_ANALOG_FEEDBACK":
                numCheck = 10;
                timelineElementCategory = TimelineElementCategoryV20.SEND_ANALOG_FEEDBACK;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20.SEND_ANALOG_FEEDBACK;
                break;
            case "SEND_ANALOG_DOMICILE":
                numCheck = 9;
                timelineElementCategory = TimelineElementCategoryV20.SEND_ANALOG_DOMICILE;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20.SEND_ANALOG_DOMICILE;
                break;
            case "SEND_DIGITAL_PROGRESS":
                numCheck = 2;
                waiting = waiting * 3;
                timelineElementCategory = TimelineElementCategoryV20.SEND_DIGITAL_PROGRESS;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20.SEND_DIGITAL_PROGRESS;
                break;
            case "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS":
                numCheck = 15;
                timelineElementCategory = TimelineElementCategoryV20.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS;
                break;
            case "PUBLIC_REGISTRY_CALL":
                numCheck = 2;
                waiting = waiting * 4;
                timelineElementCategory = TimelineElementCategoryV20.PUBLIC_REGISTRY_CALL;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20.PUBLIC_REGISTRY_CALL;
                break;
            case "PUBLIC_REGISTRY_RESPONSE":
                numCheck = 2;
                waiting = waiting * 4;
                timelineElementCategory = TimelineElementCategoryV20.PUBLIC_REGISTRY_RESPONSE;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20.PUBLIC_REGISTRY_RESPONSE;
                break;
            case "PAYMENT":
                numCheck = 9;
                timelineElementCategory = TimelineElementCategoryV20.PAYMENT;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20.PAYMENT;
                break;
            case "NOTIFICATION_CANCELLATION_REQUEST":
                numCheck = 9;
                timelineElementCategory = TimelineElementCategoryV20.NOTIFICATION_CANCELLATION_REQUEST;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20.NOTIFICATION_CANCELLATION_REQUEST;
                break;
            case "NOTIFICATION_CANCELLED":
                numCheck = 10;
                timelineElementCategory = TimelineElementCategoryV20.NOTIFICATION_CANCELLED;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20.NOTIFICATION_CANCELLED;
                break;
            case "REFINEMENT":
                numCheck = 15;
                timelineElementCategory = TimelineElementCategoryV20.REFINEMENT;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20.REFINEMENT;
                break;
            default:
                throw new IllegalArgumentException();
        }
        ProgressResponseElement progressResponseElement = null;

        boolean finish = false;
        for (int i = 0; i < numCheck; i++) {

            try {
                Thread.sleep(waiting);
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            TimelineElementV20 timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().orElse(null);
            if (timelineElement != null) {
                finish = true;
                break;
            }
        }

        Assertions.assertTrue(finish);
        for (int i = 0; i < 4; i++) {
            progressResponseElement = searchInWebhook(timelineElementCategory,null,0);
            logger.debug("PROGRESS-ELEMENT: "+progressResponseElement);

            if (progressResponseElement != null) {
                break;
            }

            try {
                Thread.sleep(sharedSteps.getWait());
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
        }
        try{
            Assertions.assertNotNull(progressResponseElement);
            Assertions.assertNotSame(progressResponseElement.getTimestamp(), sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().get().getTimestamp());
            logger.info("EventProgress: " + progressResponseElement);

        }catch(AssertionFailedError assertionFailedError){
            String message = assertionFailedError.getMessage()+
                    "{IUN: "+sharedSteps.getSentNotification().getIun()+" -WEBHOOK: "+this.eventStreamList.get(0).getStreamId()+" }";
            throw new AssertionFailedError(message,assertionFailedError.getExpected(),assertionFailedError.getActual(),assertionFailedError.getCause());
        }
    }

    @Then("Si verifica che l'elemento di timeline REFINEMENT abbia il timestamp uguale a quella presente nel webhook")
    public void readStreamTimelineElementAndVerifyDate() {
        OffsetDateTime EventTimestamp=null;
        OffsetDateTime NotificationTimestamp=null;
        try{
            Assertions.assertNotNull(progressResponseElementList);

            EventTimestamp = progressResponseElementList.stream().filter(elem -> elem.getTimelineEventCategory().equals(TimelineElementCategoryV20.REFINEMENT)).findAny().get().getTimestamp();
            NotificationTimestamp =sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals( it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20.SCHEDULE_REFINEMENT)).findAny().get().getDetails().getSchedulingDate();
            logger.info("event timestamp : {}",EventTimestamp);
            logger.info("notification timestamp : {}",NotificationTimestamp);

            Assertions.assertNotSame(EventTimestamp,NotificationTimestamp);

        }catch(AssertionFailedError assertionFailedError){
            String message = assertionFailedError.getMessage()+
                    "{IUN: "+sharedSteps.getSentNotification().getIun()+" -WEBHOOK: "+this.eventStreamList.get(0).getStreamId()+" }";
            throw new AssertionFailedError(message,assertionFailedError.getExpected(),assertionFailedError.getActual(),assertionFailedError.getCause());
        }
    }


    @Then("Si verifica che l'elemento di timeline {string} dello stream di {string} non abbia il timestamp uguale a quella della notifica")
    public void readStreamTimelineElementAndVerify(String timelineEventCategory,String pa) {
        setPaWebhook(pa);
        TimelineElementCategoryV20 timelineElementCategory;
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20 timelineElementInternalCategory;

        Integer numCheck = 10;
        Integer waiting = sharedSteps.getWorkFlowWait();

        switch (timelineEventCategory) {
            case "NOTIFICATION_VIEWED":
                timelineElementCategory = TimelineElementCategoryV20.NOTIFICATION_VIEWED;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20.NOTIFICATION_VIEWED;
                break;
            case "SEND_DIGITAL_PROGRESS":
                timelineElementCategory = TimelineElementCategoryV20.SEND_DIGITAL_PROGRESS;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20.SEND_DIGITAL_PROGRESS;
                break;
            case "SEND_DIGITAL_FEEDBACK":
                timelineElementCategory = TimelineElementCategoryV20.SEND_DIGITAL_FEEDBACK;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20.SEND_DIGITAL_FEEDBACK;
                break;
            case "SEND_ANALOG_PROGRESS":
                timelineElementCategory = TimelineElementCategoryV20.SEND_ANALOG_PROGRESS;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20.SEND_ANALOG_PROGRESS;
                break;
            case "SEND_ANALOG_FEEDBACK":
                timelineElementCategory = TimelineElementCategoryV20.SEND_ANALOG_FEEDBACK;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20.SEND_ANALOG_FEEDBACK;
                break;
            case "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS":
                timelineElementCategory = TimelineElementCategoryV20.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20.SEND_SIMPLE_REGISTERED_LETTER_PROGRESS;
                break;
            case "PAYMENT":
                timelineElementCategory = TimelineElementCategoryV20.PAYMENT;
                timelineElementInternalCategory =
                        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20.PAYMENT;
                break;
            default:
                throw new IllegalArgumentException();
        }

        boolean finish = false;
        for (int i = 0; i < numCheck; i++) {

            try {
                Thread.sleep(waiting);
            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }

            sharedSteps.setSentNotification(b2bClient.getSentNotification(sharedSteps.getSentNotification().getIun()));
            TimelineElementV20 timelineElement = sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().orElse(null);
            if (timelineElement != null) {
                finish = true;
                break;
            }
        }

        Assertions.assertTrue(finish);
        OffsetDateTime EventTimestamp=null;
        OffsetDateTime NotificationTimestamp=null;
        try{
            Assertions.assertNotNull(progressResponseElementList);

            EventTimestamp = progressResponseElementList.stream().filter(elem -> elem.getTimelineEventCategory().equals(timelineElementCategory)).findAny().get().getTimestamp();
            NotificationTimestamp =sharedSteps.getSentNotification().getTimeline().stream().filter(elem -> elem.getCategory().equals(timelineElementInternalCategory)).findAny().get().getTimestamp();

            logger.info("event timestamp : {}",EventTimestamp);
            logger.info("notification timestamp : {}",NotificationTimestamp);

            Assertions.assertTrue(EventTimestamp!=NotificationTimestamp);

        }catch(AssertionFailedError assertionFailedError){
            String message = assertionFailedError.getMessage()+
                    "{IUN: "+sharedSteps.getSentNotification().getIun()+" -WEBHOOK: "+this.eventStreamList.get(0).getStreamId()+" }";
            throw new AssertionFailedError(message,assertionFailedError.getExpected(),assertionFailedError.getActual(),assertionFailedError.getCause());
        }
    }


    private <T> ProgressResponseElement searchInWebhook(T timeLineOrStatus,String lastEventId, int deepCount){

        TimelineElementCategoryV20 timelineElementCategory = null;
        NotificationStatus notificationStatus = null;
        if(timeLineOrStatus instanceof TimelineElementCategoryV20){
            timelineElementCategory = (TimelineElementCategoryV20)timeLineOrStatus;
        }else if(timeLineOrStatus instanceof NotificationStatus){
            notificationStatus = (NotificationStatus)timeLineOrStatus;
        }else{
            throw new IllegalArgumentException();
        }
        ProgressResponseElement progressResponseElement = null;
        ResponseEntity<List<ProgressResponseElement>> listResponseEntity = webhookB2bClient.consumeEventStreamHttp(this.eventStreamList.get(0).getStreamId(), lastEventId);
        int retryAfter = Integer.parseInt(listResponseEntity.getHeaders().get("retry-after").get(0));
        List<ProgressResponseElement> progressResponseElements = listResponseEntity.getBody();

        sharedSteps.setProgressResponseElements(progressResponseElements);

        System.out.println("ELEMENTI NEL WEBHOOK: "+progressResponseElements.toString());
        if(deepCount >= 250){
            throw new IllegalStateException("LOP: PROGRESS-ELEMENTS: "+progressResponseElements
                    +" WEBHOOK: "+this.eventStreamList.get(0).getStreamId()+" IUN: "+sharedSteps.getSentNotification().getIun()+" DEEP: "+deepCount);
        }
        ProgressResponseElement lastProgress = null;
        for(ProgressResponseElement elem: progressResponseElements){
            if(lastProgress == null){
                lastProgress = elem;
            }else if (lastProgress.getEventId().compareTo(elem.getEventId()) < 0){
                lastProgress = elem;
            }
            if(elem.getIun() != null && elem.getIun().equals(sharedSteps.getSentNotification().getIun())){
                if(!progressResponseElementList.contains(elem)){
                    progressResponseElementList.addLast(elem);
                }
                if(((elem.getTimelineEventCategory() != null && (elem.getTimelineEventCategory().equals(timelineElementCategory)))
                        || (elem.getNewStatus() != null && (elem.getNewStatus().equals(notificationStatus))))){
                    progressResponseElement = elem;
                    break;
                }
            }
        }//for

        if(progressResponseElement != null){
            return progressResponseElement;
        }else if(retryAfter == 0){
            try{
                Thread.sleep(500);
                return searchInWebhook(timeLineOrStatus,lastProgress.getEventId(),(deepCount+1));
            }catch (IllegalStateException illegalStateException){
                if(deepCount == 249 || deepCount == 248 || deepCount == 247){
                    throw new IllegalStateException((illegalStateException.getMessage()+("LOP: PROGRESS-ELEMENTS: "+progressResponseElements
                            +" WEBHOOK: "+this.eventStreamList.get(0).getStreamId()+" IUN: "+sharedSteps.getSentNotification().getIun()+" DEEP: "+deepCount)));
                }else{
                    throw illegalStateException;
                }
            }catch (InterruptedException interruptedException){
                logger.error("Thread.sleep error retry");
                throw new RuntimeException(interruptedException);
            }
        }else{
            return null;
        }
    }//searchInWebhookTimelineElement


    private <T> ProgressResponseElement searchInWebhookFileNotFound(T timeLineOrStatus,String lastEventId, int deepCount){

        TimelineElementCategoryV20 timelineElementCategory = null;
        NotificationStatus notificationStatus = null;
        if(timeLineOrStatus instanceof TimelineElementCategoryV20){
            timelineElementCategory = (TimelineElementCategoryV20)timeLineOrStatus;
        }else if(timeLineOrStatus instanceof NotificationStatus){
            notificationStatus = (NotificationStatus)timeLineOrStatus;
        }else{
            throw new IllegalArgumentException();
        }
        ProgressResponseElement progressResponseElement = null;
        ResponseEntity<List<ProgressResponseElement>> listResponseEntity = webhookB2bClient.consumeEventStreamHttp(this.eventStreamList.get(0).getStreamId(), lastEventId);
        int retryAfter = Integer.parseInt(listResponseEntity.getHeaders().get("retry-after").get(0));
        List<ProgressResponseElement> progressResponseElements = listResponseEntity.getBody();
        if(deepCount >= 200){
            throw new IllegalStateException("LOP: PROGRESS-ELEMENTS: "+progressResponseElements
                    +" WEBHOOK: "+this.eventStreamList.get(0).getStreamId()+" IUN: "+sharedSteps.getSentNotification().getIun()+" DEEP: "+deepCount);
        }
        ProgressResponseElement lastProgress = null;
        for(ProgressResponseElement elem: progressResponseElements){
            if("REFUSED".equalsIgnoreCase(elem.getNewStatus().getValue()) && elem.getValidationErrors() != null && elem.getValidationErrors().size()>0){
               if (elem.getValidationErrors().get(0).getErrorCode()!= null && "FILE_NOTFOUND".equalsIgnoreCase(elem.getValidationErrors().get(0).getErrorCode()) )
                   progressResponseElement = elem;
               break;
            }
        }//for
        return progressResponseElement;
    }//searchInWebhookTimelineElement


    @And("{string} legge la notifica")
    public void userReadNotification(String recipient) {
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

    @And("{string} legge la notifica dopo i 10 giorni")
    public void userReadNotificationAfterTot(String recipient) {
        sharedSteps.selectUser(recipient);

        try {
            Thread.sleep(sharedSteps.getSchedulingDaysSuccessAnalogRefinement().toMillis());
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }

        Assertions.assertDoesNotThrow(() -> {
            webRecipientClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(), null);
        });
        try {
            Thread.sleep(sharedSteps.getWorkFlowWait());
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Then("si verifica nello stream del {string} che la notifica abbia lo stato VIEWED")
    public void checkViewedState(String pa) {
        try{
            Thread.sleep(sharedSteps.getWait()*2);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        setPaWebhook(pa);
        ProgressResponseElement progressResponseElement = searchInWebhook(NotificationStatus.VIEWED, null, 0);
        Assertions.assertNotNull(progressResponseElement);
    }


    @After("@clean")
    public void doSomethingAfter() {
        setPaWebhook("Comune_1");
        cleanWebhook();
    }

    @After("@cleanC2")
    public void cleanC2() {
        setPaWebhook("Comune_2");
        cleanWebhook();
    }

    @After("@cleanC3")
    public void cleanC3() {
        setPaWebhook("Comune_Multi");
        cleanWebhook();
        SharedSteps.lastEventID = 0;
    }

    private void cleanWebhook(){
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
    public void lastCreationProducedAnErrorWithStatusCode(String statusCode) {
        List<StreamListElement> streamListElements = webhookB2bClient.listEventStreams();
        System.out.println("streamListElements: "+streamListElements.size());
        System.out.println("eventStreamList: "+eventStreamList.size());
        System.out.println("requestNumber: "+requestNumber);
        Assertions.assertTrue((this.notificationError != null) &&
                (this.notificationError.getStatusCode().toString().substring(0,3).equals(statusCode)) && (eventStreamList.size() == (requestNumber-1)));
    }


    @Given("vengono cancellati tutti gli stream presenti del {string}")
    public void deleteAll(String pa) {
        setPaWebhook(pa);
        List<StreamListElement> streamListElements = webhookB2bClient.listEventStreams();
        for(StreamListElement elem: streamListElements){
            webhookB2bClient.deleteEventStream(elem.getStreamId());
        }
    }


    @And("vengono prodotte le evidenze: metadati, requestID, IUN e stati")
    public void evidenceProducedIunRequestIdAndState() {
        logger.info("METADATI: "+'\n'+sharedSteps.getNewNotificationResponse());
        logger.info("REQUEST-ID: "+'\n'+sharedSteps.getNewNotificationResponse().getNotificationRequestId());
        logger.info("IUN: "+'\n'+sharedSteps.getSentNotification().getIun());
        for(ProgressResponseElement element: progressResponseElementList){
            logger.info("EVENT: "+'\n'+element.getTimelineEventCategory()+" "+element.getTimestamp());
        }

    }

    @Then("viene verificato che il ProgressResponseElement del webhook abbia un EventId incrementale e senza duplicati")
    public void vieneVerificatoCheIlProgressResponseElementIdDelWebhookSiaIncrementaleESenzaDuplicati() {

        List<ProgressResponseElement> progressResponseElements = sharedSteps.getProgressResponseElements();
        Assertions.assertNotNull(progressResponseElements);
        boolean counterIncrement = true ;
        int lastEventID = SharedSteps.lastEventID;
        //logger.info("ELEMENTI NEL WEBHOOK LAST EVENT ID1: "+lastEventID);
        for(ProgressResponseElement elem: progressResponseElements){
            if (lastEventID==0){
                lastEventID = Integer.parseInt(elem.getEventId());
                continue;
            }
            if (Integer.parseInt(elem.getEventId())<=lastEventID){
                counterIncrement = false;
                break;
            }else {
                lastEventID = Integer.parseInt(elem.getEventId());
            }
        }//for
        try{
            Assertions.assertTrue(counterIncrement);
        }catch (AssertionFailedError assertionFailedError){
            throw new AssertionFailedError(assertionFailedError.getMessage()+" PROGRESS-ELEMENT: \n"+progressResponseElements);
        }

        SharedSteps.lastEventID=lastEventID;
        //logger.info("ELEMENTI NEL WEBHOOK LAST EVENT ID2: "+lastEventID);
    }


    @And("vengono letti gli eventi dello stream che contenga {int} eventi")
    public void readStreamNumberEvents(Integer numEventi) {
        Assertions.assertDoesNotThrow(() -> {
            List<ProgressResponseElement> progressResponseElements = webhookB2bClient.consumeEventStream(this.eventStreamList.get(0).getStreamId(), null);
            logger.info("EventProgress: " + progressResponseElements);

            Assertions.assertEquals(progressResponseElements.size() , numEventi);
            System.out.println("ELEMENTI NEL WEBHOOK: "+progressResponseElements.size());
        });

    }
}


