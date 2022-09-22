package it.pagopa.pn.client.b2b.pa.cucumber.test.steps;

import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotification;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NewNotificationRequest;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NewNotificationResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationRecipient;
import it.pagopa.pn.client.b2b.pa.impl.PnPaB2bExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.impl.PnWebhookB2bExternalClientImpl;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.NotificationStatus;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.ProgressResponseElement;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.StreamCreationRequest;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.StreamMetadataResponse;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.lang.invoke.MethodHandles;
import java.util.List;


public class AvanzamentoNotificheB2bSteps {

    @Autowired
    PnWebhookB2bExternalClientImpl pnWebhookB2bExternalClient;


    @Autowired
    PnPaB2bExternalClientImpl pnPaB2bExternalClient;

    @Autowired
    private PnPaB2bUtils utils;

    private StreamCreationRequest streamCreationRequest;
    private StreamMetadataResponse eventStream;
    private NewNotificationRequest notificationRequest;
    private FullSentNotification notificationResponseComplete;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Given("nuovo stream {string} con eventType {string}")
    public void nuovoStreamDiEventiConEventType(String title, String eventType) {
        streamCreationRequest = new StreamCreationRequest();
        streamCreationRequest.title(title);
        //STATUS, TIMELINE
        streamCreationRequest.eventType(eventType.equalsIgnoreCase("STATUS")?
                StreamCreationRequest.EventTypeEnum.STATUS : StreamCreationRequest.EventTypeEnum.TIMELINE );
    }

    @When("viene creato il nuovo stream")
    public void vieneCreatoUnNuovoStreamDiNotifica() {
       this.eventStream  = pnWebhookB2bExternalClient.createEventStream(streamCreationRequest);
    }

    @Then("lo stream è stato creato e viene correttamente recuperato dal sistema tramite stream id")
    public void laStreamEStatoCreatoEVieneCorrettamenteRecuperatoDalSistema() {
        Assertions.assertDoesNotThrow(() -> {
            StreamMetadataResponse eventStream = pnWebhookB2bExternalClient.getEventStream(this.eventStream.getStreamId());
            System.out.println("EventStream: "+eventStream);
        });
    }


    @And("vengono letti gli eventi dello stream")
    public void vengonoLettiGliEventiDelloStream() {
        Assertions.assertDoesNotThrow(() -> {
            List<ProgressResponseElement> progressResponseElements = pnWebhookB2bExternalClient.consumeEventStream(this.eventStream.getStreamId(), null);
            System.out.println("EventProgress: "+progressResponseElements);
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
            NewNotificationResponse newNotificationRequest = utils.uploadNotification(notificationRequest);
            notificationResponseComplete = utils.waitForRequestAcceptation( newNotificationRequest );
        });
        try {
            Thread.sleep( 10 * 1000);
        } catch (InterruptedException e) {
            logger.error("Thread.sleep error retry");
            throw new RuntimeException(e);
        }
    }


    @And("vengono letti gli eventi dello stream fino allo stato {string}")
    public void vengonoLettiGliEventiDelloStreamFinoAlloStato(String status) {
        NotificationStatus notificationStatus;
        switch(status){
            case "ACCEPTED":
                notificationStatus = NotificationStatus.ACCEPTED;
                break;
            case "DELIVERING":
                notificationStatus = NotificationStatus.DELIVERING;
                break;
            default:
                throw new IllegalArgumentException();
        }
        List<ProgressResponseElement> progressResponseElements;
        for( int i = 0; i < 50; i++ ) {
            progressResponseElements = pnWebhookB2bExternalClient.consumeEventStream(this.eventStream.getStreamId(), null);

            ProgressResponseElement progressResponseElement = progressResponseElements.stream().filter(elem -> (elem.getIun().equals(notificationResponseComplete.getIun()) && elem.getNewStatus().equals(notificationStatus))).findAny().orElse(null);
            notificationResponseComplete = pnPaB2bExternalClient.getSentNotification( notificationResponseComplete.getIun() );
            logger.info("IUN: "+notificationResponseComplete.getIun());
            logger.info("*******************************************"+'\n');
            logger.info("EventProgress: "+progressResponseElements);
            logger.info("*******************************************"+'\n');
            logger.info("NOTIFICATION_TIMELINE: "+notificationResponseComplete.getTimeline());
            logger.info("*******************************************"+'\n');
            logger.info("NOTIFICATION_STATUS_HISTORY: "+notificationResponseComplete.getNotificationStatusHistory());
            if (progressResponseElement != null) {
                break;
            }
            try {
                Thread.sleep( 10 * 1000L);
            } catch (InterruptedException exc) {
                throw new RuntimeException( exc );
            }
        }
        progressResponseElements = pnWebhookB2bExternalClient.consumeEventStream(this.eventStream.getStreamId(), null);
        logger.info("EventProgress: "+progressResponseElements);
    }


}
