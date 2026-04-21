package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.InformalNotificationRequestV1;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.MessageResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.NewInformalNotificationResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.NewMessageRequest;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingFactory;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.service.IPnPrivateDeliveryPushExternalClient;
import it.pagopa.pn.client.b2b.pa.service.impl.PnExternalServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaB2bInternalInformalClientImpl;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.dataTable.InformalNotificationRequestMapper;
import it.pagopa.pn.cucumber.steps.pa.b2bVersions.B2bStepsInterface;
import it.pagopa.pn.cucumber.steps.pa.notificationVersions.NotificationVersion;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class PresaInCaricoNoticaBonariaSteps {

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

    @Getter
    private final PnPaB2bInternalInformalClientImpl pnPaB2bInternalInformalClientImpl;

    private final Map<NotificationVersion, B2bStepsInterface> mapOfVersionSteps = new HashMap<>();


    private NewMessageRequest newMessageRequest;
    private InformalNotificationRequestV1 informalNotificationRequestV1;
    private MessageResponse messageResponse;
    private UUID messageId;
    private Exception lastException;

    private NewInformalNotificationResponse newInformalNotificationResponse;

    @Autowired
    public PresaInCaricoNoticaBonariaSteps(PnPaB2bInternalInformalClientImpl pnPaB2bInternalInformalClientImpl, SharedSteps sharedSteps,
                                           TimingForPolling timingForPolling,
                                           IPnPrivateDeliveryPushExternalClient pnPrivateDeliveryPushExternalClient) {
        this.sharedSteps = sharedSteps;
        this.timingForPolling = timingForPolling;
        this.pnPrivateDeliveryPushExternalClient = pnPrivateDeliveryPushExternalClient;
        this.pnPaB2bInternalInformalClientImpl = pnPaB2bInternalInformalClientImpl;
        this.externalClient = sharedSteps.getPnExternalServiceClient();
        this.b2bClient = sharedSteps.getB2bClient();
        this.pnPollingFactory = sharedSteps.getPollingFactory();
    }

    @When("si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie")
    public void createNewInformalMessage(NewMessageRequest newMessageRequest) {
        try {
            this.messageResponse = pnPaB2bInternalInformalClientImpl.createMessage(newMessageRequest);
            assertNotNull(this.messageResponse.getMessageId(), "messageId non valorizzato: creazione messaggio fallita"
            );
            this.lastException = null;
        } catch (Exception e) {
            this.lastException = e;
            this.messageResponse = null;
            this.messageId = null;
        }

    }

    @Then("tento il recupero del messaggio precedentemente creato per le comunicazioni bonarie")
    public void getInformalMessage() {
        try {
            messageResponse = pnPaB2bInternalInformalClientImpl.getMessage(messageId);
            lastException = null;
        } catch (Exception e) {
            lastException = e;
            messageResponse = null;
        }

    }

    @Then("tento il recupero del messaggio per le comunicazioni bonarie con message id {string}")
    public void getInformalMessageById(String messageIdString) {
        UUID messageId = toUuid(messageIdString);
        try {
            messageResponse = pnPaB2bInternalInformalClientImpl.getMessage(messageId);
            lastException = null;
        } catch (Exception e) {
            lastException = e;
            messageResponse = null;
        }

    }

    @Then("viene inviata una nuova notifica bonaria")
    public void sendInformal() {
        try {
            newInformalNotificationResponse = pnPaB2bInternalInformalClientImpl.sendNewInformalNotificationV1(informalNotificationRequestV1);
            lastException = null;
        } catch (Exception e) {
            lastException = e;
            messageResponse = null;
            log.info("Eccezione: ", e);
        }
    }

    @Given("viene creata una nuova notifica bonaria con i seguenti parametri")
    public void createInformal(InformalNotificationRequestV1 request) {
        log.info("Invio notifica bonaria - request: {}", request);
        informalNotificationRequestV1 = request;
    }

    @Given("viene creata una nuova notifica bonaria con valori di default")
    public void createInformal() {
        InformalNotificationRequestMapper mapper = new InformalNotificationRequestMapper();
        informalNotificationRequestV1 = mapper.buildInformalNotificationRequest(Map.of());
    }

    @And("si riceve errore {int}")
    public void verifyError(int expectedStatus) {
        assertNotNull(lastException, "Non è stato generato l'errore atteso");
        if (lastException instanceof HttpClientErrorException ex) {
            assertEquals(expectedStatus, ex.getStatusCode().value());
        } else {
            fail("Eccezione inattesa: " + lastException.getClass());
        }
    }

    @And("l'operazione non ha generato errori")
    public void verifyMessageRetrieved() {
        assertNull(lastException, "Errore non atteso");
        assertNotNull(messageResponse, "La response non deve essere null");

    }

    public UUID toUuid(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }


//    private B2bStepsInterface getB2bStepsInterface() {
//        NotificationVersion notificationVersion = sharedSteps.getVersionUsed() == null ?
//                sharedSteps.getNotificationVersion(MOST_RECENT) : sharedSteps.getVersionUsed();
//        return getB2bStepsInterface(notificationVersion);
//    }
//
//    private B2bStepsInterface getB2bStepsInterface(NotificationVersion notificationVersion) {
//        if (mapOfVersionSteps.get(notificationVersion) == null) {
//            mapOfVersionSteps.put(notificationVersion, NotificationVersion.createB2bStep(notificationVersion, this));
//        }
//        return mapOfVersionSteps.get(notificationVersion);
//    }


}