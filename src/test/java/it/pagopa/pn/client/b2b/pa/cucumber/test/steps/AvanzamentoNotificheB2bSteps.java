package it.pagopa.pn.client.b2b.pa.cucumber.test.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.impl.PnWebhookB2bExternalClientImpl;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.StreamCreationRequest;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.StreamMetadataResponse;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;


public class AvanzamentoNotificheB2bSteps {

    @Autowired
    PnWebhookB2bExternalClientImpl pnWebhookB2bExternalClient;

    private StreamCreationRequest streamCreationRequest;
    private StreamMetadataResponse eventStream;

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

    @Then("lo stream è stato creato e viene correttamente recuperato dal sistema")
    public void laStreamÈStatoCreatoEVieneCorrettamenteRecuperatoDalSistema() {
        Assertions.assertDoesNotThrow(() -> {
            pnWebhookB2bExternalClient.getEventStream(eventStream.getStreamId());
        });
    }


}
