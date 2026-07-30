package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.*;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingFactory;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.service.IPnPrivateDeliveryPushExternalClient;
import it.pagopa.pn.client.b2b.pa.service.impl.PnExternalServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaB2bInternalInformalClientImpl;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDelivery.model.InformalSentNotificationV1;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.informalNotification.builders.InformalRecipientBuilder;
import it.pagopa.pn.cucumber.steps.informalNotification.mapper.InformalNotificationRequestMapper;
import it.pagopa.pn.cucumber.steps.informalNotification.utils.NotificationInformalUtilsV1;
import it.pagopa.pn.cucumber.utils.GroupPosition;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class PresaInCaricoNoticaBonariaSteps {

    @Value("${pn.external.senderId}")
    private String senderId;
    @Value("${pn.external.senderId-2}")
    private String senderId2;
    @Value("${pn.external.senderId-GA}")
    private String senderIdGA;
    @Value("${pn.external.senderId-SON}")
    private String senderIdSON;
    @Value("${pn.external.senderId-ROOT}")
    private String senderIdROOT;

    @Getter
    private final SharedSteps sharedSteps;
    @Getter
    private final IPnPaB2bClient b2bClient;
    private final PnExternalServiceClientImpl externalClient;
    @Getter
    private final IPnPrivateDeliveryPushExternalClient pnPrivateDeliveryPushExternalClient;
    @Getter
    private final PnPollingFactory pnPollingFactory;
    @Getter
    private final TimingForPolling timingForPolling;
    @Getter
    private final PnPaB2bInternalInformalClientImpl pnPaB2bInternalInformalClientImpl;

    private InformalNotificationRequestV1 informalNotificationRequestV1;
    private MessageResponse messageResponse;
    private UUID messageId;
    private Exception lastException;
    private String savedIun;
    private String currentGroupId;
    private String currentCxId;
    private String savedNotificationRequestId;
    private TerminationRequestStatus terminationStatus;
    private String paName;

    private NewInformalNotificationResponse newInformalNotificationResponse;
    private NotificationAttachmentDownloadMetadataResponse attachmentResponse;
    private NewInformalNotificationRequestStatusResponseV1 statusResponse;
    private InformalSentNotificationV1 informalNotificationResponse;

    private final InformalNotificationRequestMapper informalNotificationRequestMapper;
    private final InformalRecipientBuilder recipientBuilder;
    private final NotificationInformalUtilsV1 notificationInformalUtilsV1;

    private FullSentInformalNotificationV1 fullInformalNotificationResponse;
    private InformalTimelineElementV1 timelineElement;
    private String recipientTaxId;
    private it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internawebrecipientinformal.model.CxTypeAuthFleet recipientCxType;


    @Autowired
    public PresaInCaricoNoticaBonariaSteps(NotificationInformalUtilsV1 notificationInformalUtilsV1, InformalNotificationRequestMapper informalNotificationRequestMapper, InformalRecipientBuilder recipientBuilder, PnPaB2bInternalInformalClientImpl pnPaB2bInternalInformalClientImpl, SharedSteps sharedSteps, TimingForPolling timingForPolling, IPnPrivateDeliveryPushExternalClient pnPrivateDeliveryPushExternalClient) {
        this.sharedSteps = sharedSteps;
        this.timingForPolling = timingForPolling;
        this.pnPrivateDeliveryPushExternalClient = pnPrivateDeliveryPushExternalClient;
        this.pnPaB2bInternalInformalClientImpl = pnPaB2bInternalInformalClientImpl;
        this.externalClient = sharedSteps.getPnExternalServiceClient();
        this.b2bClient = sharedSteps.getB2bClient();
        this.pnPollingFactory = sharedSteps.getPollingFactory();
        this.informalNotificationRequestMapper = informalNotificationRequestMapper;
        //notificationInformalUtilsV1 = new NotificationInformalUtilsV1(sharedSteps.getContext(), b2bClient, sharedSteps.getPollingFactory());//todo t bonarie elimina
        this.notificationInformalUtilsV1 = notificationInformalUtilsV1;
        this.recipientBuilder = recipientBuilder;
    }

    // STEP CREAZIONE E INVIO NOTIFICA

    @And("mittente della notifica bonaria: {string}")
    public void setSenderInformal(String paName) {

        this.paName = paName;

        this.currentCxId = switch (paName) {
            case "Comune_1" -> senderId;
            case "Comune_2" -> senderId2;
            case "Comune_Multi" -> senderIdGA;
            case "Comune_Root" -> senderIdROOT;
            default -> throw new IllegalArgumentException("PA bonaria non valida: " + paName);
        };

        if (!paName.equalsIgnoreCase("Comune_Root")) {
            this.currentGroupId =
                    sharedSteps.getGroupIdByPa(paName, GroupPosition.FIRST);
        }
    }

    @And("imposto un gruppo non attivo per {string}")
    public void getDeletedGroupId(String paName) {
        this.currentGroupId = sharedSteps.getGroupIdByPa(paName, GroupPosition.FIRST, "DELETED");
    }

    @Given("viene creata una nuova notifica bonaria con i seguenti parametri")
    public void createInformal(Map<String, String> dataInput) {

        Map<String, String> data = new HashMap<>(dataInput);

        //gestione custom group
        handleGroup(data);

        informalNotificationRequestV1 = informalNotificationRequestMapper.buildInformalNotificationRequest(data);
    }


    @Given("viene creata una nuova notifica bonaria con valori di default")
    public void createInformal() {

        informalNotificationRequestV1 = informalNotificationRequestMapper.buildInformalNotificationRequest(Map.of());

        log.info("Invio notifica bonaria - request: {}", informalNotificationRequestV1);
    }

    @And("destinatario della notifica bonaria")
    public void addInformalRecipientLight(Map<String, String> data) {

        assertNotNull(informalNotificationRequestV1,
                "Creare prima la notifica bonaria");

        Map<String, String> cleanedData = data.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().trim(),
                        e -> e.getValue() != null ? e.getValue().trim() : null
                ));

        InformalNotificationRecipientV1 recipient = recipientBuilder.build(cleanedData, currentCxId);
        informalNotificationRequestV1.getRecipients().add(recipient);
    }

    @Then("viene inviata una nuova notifica bonaria con content type non valido")
    public void sendInformalInvalidContentType() {
        try {
            informalNotificationRequestV1 = notificationInformalUtilsV1.preloadAndPrepare(informalNotificationRequestV1, paName);
            informalNotificationRequestV1.getDocuments().forEach(doc -> doc.setContentType("application/txt"));
            newInformalNotificationResponse = pnPaB2bInternalInformalClientImpl.sendNewInformalNotificationV1(currentCxId, informalNotificationRequestV1);
            savedNotificationRequestId = newInformalNotificationResponse.getNotificationRequestId();
            lastException = null;

        } catch (Exception e) {
            lastException = e;
            newInformalNotificationResponse = null;
            log.info("Eccezione: ", e);
        }
    }

    @Then("l'invio della notifica bonaria fallisce")
    public void sendInformalError() {
        try {
            informalNotificationRequestV1 = notificationInformalUtilsV1.preloadAndPrepare(informalNotificationRequestV1, paName);
            newInformalNotificationResponse = pnPaB2bInternalInformalClientImpl.sendNewInformalNotificationV1(currentCxId, informalNotificationRequestV1);
            fail("Atteso errore ma la richiesta è andata a buon fine " + newInformalNotificationResponse + "***" + informalNotificationRequestV1 + "***" + paName);

        } catch (Exception e) {
            lastException = e;
            log.info("Eccezione: ", e);
        }
    }

    @Then("viene inviata una nuova notifica bonaria")
    public void sendInformal() throws IOException {
        informalNotificationRequestV1 = notificationInformalUtilsV1.preloadAndPrepare(informalNotificationRequestV1, paName);
        newInformalNotificationResponse = pnPaB2bInternalInformalClientImpl.sendNewInformalNotificationV1(currentCxId, informalNotificationRequestV1);
        savedNotificationRequestId = newInformalNotificationResponse.getNotificationRequestId();

        lastException = null;
    }

    @Then("viene inviata una nuova notifica bonaria con sha non valido")
    public void sendInformalShaNotValid() {
        try {
            informalNotificationRequestV1 = notificationInformalUtilsV1.preloadAndPrepare(informalNotificationRequestV1, paName);
            informalNotificationRequestV1.getDocuments().forEach(doc -> doc.setDigests(new NotificationAttachmentDigests().sha256("INVALID_SHA")));
            newInformalNotificationResponse = pnPaB2bInternalInformalClientImpl.sendNewInformalNotificationV1(currentCxId, informalNotificationRequestV1);
            savedNotificationRequestId = newInformalNotificationResponse.getNotificationRequestId();
            lastException = null;

        } catch (Exception e) {
            lastException = e;
            newInformalNotificationResponse = null;
            log.info("Eccezione: ", e);
        }

    }

    @When("viene inviata una nuova notifica bonaria con fileKey duplicata")
    public void sendInformalDuplicateKey() {
        try {
            informalNotificationRequestV1 = notificationInformalUtilsV1.preloadAndPrepare(informalNotificationRequestV1, paName);
            var docs = informalNotificationRequestV1.getDocuments();
            if (docs.size() >= 2) {
                docs.get(1).getRef().setKey(docs.get(0).getRef().getKey());
            }
            newInformalNotificationResponse = pnPaB2bInternalInformalClientImpl.sendNewInformalNotificationV1(currentCxId, informalNotificationRequestV1);
            savedNotificationRequestId = newInformalNotificationResponse.getNotificationRequestId();
            lastException = null;

        } catch (Exception e) {
            lastException = e;
            newInformalNotificationResponse = null;
            log.info("Eccezione: ", e);
        }
    }


    @Then("viene inviata una nuova notifica bonaria con nome attachment non valido")
    public void sendInformalInvalidAttachmentName() {
        try {
            informalNotificationRequestV1 = notificationInformalUtilsV1.preloadAndPrepare(informalNotificationRequestV1, paName);
            informalNotificationRequestV1.getDocuments().forEach(doc -> {
                if (doc.getRef() != null) {
                    doc.getRef().setKey("PN_NOTIFICATION_ATTACHMENT-c3bc9525a5ac4f45a4fb7e940b2b9815.pdf");
                }
            });

            newInformalNotificationResponse = pnPaB2bInternalInformalClientImpl.sendNewInformalNotificationV1(currentCxId, informalNotificationRequestV1);
            savedNotificationRequestId = newInformalNotificationResponse.getNotificationRequestId();
            lastException = null;

        } catch (Exception e) {
            lastException = e;
            newInformalNotificationResponse = null;
            log.info("Eccezione: ", e);
        }
    }

    @And("la sottomissione della notifica bonaria è andata a buon fine")
    public void verifyNotificationSent() {
        assertNull(lastException, "Errore non atteso");
        assertNotNull(newInformalNotificationResponse, "Response notifica nulla");
        assertNotNull(savedIun, "IUN non valorizzato");
        assertNotNull(savedNotificationRequestId, "notificationRequestId non valorizzato");
    }

    //*** STEP MESSAGGI ***

    @When("si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie")
    public void createNewInformalMessage(NewMessageRequest newMessageRequest) {
        try {
            this.messageResponse = pnPaB2bInternalInformalClientImpl.createMessage(currentCxId, newMessageRequest);
            assertNotNull(this.messageResponse.getMessageId(), "messageId non valorizzato: creazione messaggio fallita");
            this.messageId = this.messageResponse.getMessageId();
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
            messageResponse = pnPaB2bInternalInformalClientImpl.getMessage(messageId, currentCxId);
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
            messageResponse = pnPaB2bInternalInformalClientImpl.getMessage(messageId, currentCxId);
            log.info("Message response: {}", messageResponse);
            lastException = null;
        } catch (Exception e) {
            lastException = e;
            messageResponse = null;
            log.info("Eccezione durante getMessage", e);
        }
    }

    @And("l'operazione è andata a buon fine")
    public void verifyMessageCreated() {
        assertNull(lastException, "Errore non atteso");
        assertNotNull(messageResponse, "Response messaggio nulla");
        assertNotNull(messageResponse.getCreatedAt(), "Campo create AT messaggio nullo");
    }


    //*** RECUPERO DOCUMENTI ***

    @When("si tenta il recupero documento della notifica bonaria")
    public void getDocument() {
        try {
            attachmentResponse = pnPaB2bInternalInformalClientImpl.getSentInformalNotificationDocument(currentCxId, savedIun, 0);
            lastException = null;

        } catch (Exception e) {
            lastException = e;
            attachmentResponse = null;
        }
    }

    @When("si tenta il recupero documento con indice {int}")
    public void getDocumentWithIndex(int docIdx) {
        try {
            attachmentResponse = pnPaB2bInternalInformalClientImpl.getSentInformalNotificationDocument(currentCxId, savedIun, docIdx);
            lastException = null;

        } catch (Exception e) {
            lastException = e;
            attachmentResponse = null;
        }
    }

    @When("si tenta il recupero documento con IUN {string}")
    public void getDocumentWithIun(String iun) {
        try {
            attachmentResponse = pnPaB2bInternalInformalClientImpl.getSentInformalNotificationDocument(currentCxId, iun, 0);
            lastException = null;

        } catch (Exception e) {
            lastException = e;
            attachmentResponse = null;
        }
    }

    @Then("il download risulta correttamente effettuato")
    public void verifyAttachmentAvailable() {
        assertNotNull(attachmentResponse, "attachmentResponse nulla");
        assertNotNull(attachmentResponse.getUrl(), "URL allegato mancante");
    }

    //*** DOCUMENTO NON CONFORME

    @And("documento non valido: {string}")
    public void setInvalidDocument(String tipoErrore) {

        switch (tipoErrore) {

            case "SHA NON INTEGRO" -> {
                informalNotificationRequestV1.getDocuments().forEach(doc -> doc.setDigests(new NotificationAttachmentDigests().sha256("INVALID_SHA")));
            }
            case "FORMATO NON CONFORME" -> {
                informalNotificationRequestV1.getDocuments().forEach(doc -> {
                    doc.setContentType("text/plain");
                    //doc.getRef().setKey("classpath:/file.txt");
                });
            }
            case "ALLEGATO TROPPO GRANDE" -> {
                informalNotificationRequestV1.getDocuments().forEach(doc -> {
                    doc.setContentType("application/pdf");
                    doc.getRef().setKey("classpath:/allegato_30Mb.pdf");
                });
            }
            default -> throw new IllegalArgumentException("Tipo errore non supportato: " + tipoErrore);
        }
    }


    //*** STEP ALLEGATI PAGAMENTO

    @When("si tenta il recupero allegato pagamento della notifica bonaria")
    public void getAttachment() {
        try {
            attachmentResponse = pnPaB2bInternalInformalClientImpl.getSentInformalNotificationAttachment(savedIun, currentCxId, 0, 0);
            lastException = null;

        } catch (Exception e) {
            lastException = e;
            attachmentResponse = null;
        }
    }

    @When("si tenta il recupero allegato pagamento con IUN {string}")
    public void getAttachmentWithIun(String iun) {
        try {
            attachmentResponse = pnPaB2bInternalInformalClientImpl.getSentInformalNotificationAttachment(iun, currentCxId, 0, 0);
            lastException = null;

        } catch (Exception e) {
            lastException = e;
            attachmentResponse = null;
        }
    }

    @When("si tenta il recupero allegato pagamento con recipient {int} e attachment {int}")
    public void getAttachmentCustom(int recipientIdx, int attachmentIdx) {
        try {
            attachmentResponse = pnPaB2bInternalInformalClientImpl.getSentInformalNotificationAttachment(savedIun, currentCxId, recipientIdx, attachmentIdx);

            lastException = null;

        } catch (Exception e) {
            lastException = e;
            attachmentResponse = null;
        }
    }

    //*** STATO DELLA NOTIFICA

    @When("si verifica lo stato della richiesta di notifica bonaria")
    public void getNotificationStatus() {
        try {
            statusResponse = pnPaB2bInternalInformalClientImpl.getNotificationStatusByRequestId(currentCxId, savedNotificationRequestId);
            lastException = null;

        } catch (Exception e) {
            lastException = e;
            statusResponse = null;
        }
    }

    @When("si verifica lo stato della richiesta di notifica bonaria con notification id {string}")
    public void getNotificationStatusWithNotificationId(String notificationId) {
        try {
            statusResponse = pnPaB2bInternalInformalClientImpl.getNotificationStatusByRequestId(currentCxId, notificationId);
            lastException = null;

        } catch (Exception e) {
            lastException = e;
            statusResponse = null;
        }
    }

    @Then("si verifica che la notifica bonaria sia in stato {string}")
    public void verifyNotificationStatus(String expectedStatus) {

        AtomicReference<String> lastStatus = new AtomicReference<>(null);

        try {
            await().atMost(Duration.ofMinutes(12)).pollInterval(Duration.ofSeconds(3)).until(() -> {
                statusResponse = pnPaB2bInternalInformalClientImpl.getNotificationStatusByRequestId(currentCxId, savedNotificationRequestId);
                if (statusResponse == null) {
                    return false;
                }
                String actualStatus = statusResponse.getNotificationRequestStatus();
                lastStatus.set(actualStatus);
                log.info("Polling stato: {}", actualStatus);
                return expectedStatus.equals(actualStatus);
            });

        } catch (Exception e) {
            throw new AssertionError("Stato finale non valido.\n" + "Atteso: " + expectedStatus + "\n" + "Ultimo stato ricevuto: " + lastStatus.get() + "\n" + "Response: " + statusResponse, e);

        } finally {
            log.info("=== RESPONSE FINALE NOTIFICA ===");
            if (statusResponse != null) {
                log.info("Response: {}", statusResponse);
            } else {
                log.info("Nessuna response disponibile");
            }
        }
        savedIun = statusResponse.getIun();
        sharedSteps.setNotificationIun(savedIun);
        lastException = null;
    }

    @Then("la notifica bonaria è stata rifiutata per l'errore: {string}")
    public void verifyNotificationStatusError(String expectedError) {

        assertNotNull(statusResponse, "StatusResponse nullo");
        List<NotificationRequestRefusedProblemError> errors = statusResponse.getErrors();
        assertNotNull(errors, "Lista errori nulla");
        assertFalse(errors.isEmpty(), "Nessun errore presente");

        boolean found = errors.stream().anyMatch(e -> expectedError.equals(e.getCode()));
        assertTrue(found, "Errore atteso non trovato: " + expectedError);
    }

    @When("si tenta il recupero della notifica bonaria tramite IUN")
    public void getInformalNotification() {
        try {
            informalNotificationResponse = pnPaB2bInternalInformalClientImpl.getSentInformalNotification(savedIun);
            lastException = null;

        } catch (Exception e) {
            lastException = e;
            informalNotificationResponse = null;
        }
    }

    @Then("la notifica bonaria è recuperabile tramite IUN")
    public void verifyNotificationRetrieved() {

        assertNull(lastException, "Errore non atteso");
        assertNotNull(informalNotificationResponse, "Response nulla");
        assertEquals(savedIun, informalNotificationResponse.getIun());
    }


    //*** TERMINAZIONE DELLA NOTIFICA

    @When("si tenta la terminazione della notifica bonaria")
    public void terminateInformalNotification() {

        try {
            terminationStatus = pnPaB2bInternalInformalClientImpl.terminateInformalWorkflow(currentCxId, savedIun);
            lastException = null;
        } catch (Exception e) {
            lastException = e;
        }
    }

    @Then("la terminazione della notifica bonaria è andata a buon fine")
    public void verifyTerminationSucceeded() {
        assertNull(lastException, "Errore non atteso durante la terminazione");
    }

    @Then("la terminazione della notifica bonaria è accettata")
    public void verifyTerminationAccepted() {

        assertNull(lastException, "Errore non atteso durante la terminazione");
        assertNotNull(terminationStatus, "terminationStatus nullo");

        boolean accepted = terminationStatus.getDetails().stream().anyMatch(d -> "NOTIFICATION_TERMINATION_ACCEPTED".equals(d.getCode()));
        assertTrue(accepted, "Codice NOTIFICATION_TERMINATION_ACCEPTED non presente");
    }

    @Then("la notifica bonaria risulta già terminata")
    public void verifyAlreadyTerminated() {
        assertNull(lastException, "Errore non atteso durante la terminazione");
        assertNotNull(terminationStatus, "terminationStatus nullo");
        assertTrue(terminationStatus.getDetails().stream().anyMatch(d -> "NOTIFICATION_ALREADY_TERMINATED".equals(d.getCode())), "Codice NOTIFICATION_ALREADY_TERMINATED non presente");
    }

    //*** CONTROLLI PresaInCaricoNoticaBonariaSteps.javaGENERICI

@And("si riceve errore {int}")
public void verifyError(int expectedStatus) {
    assertNotNull(lastException, "Non è stato generato l'errore atteso");

    if (lastException instanceof HttpClientErrorException ex) {
        assertEquals(expectedStatus, ex.getStatusCode().value());
    } else {
        fail("Eccezione inattesa: " + lastException.getClass());
    }
}

@And("si riceve errore {int} {string}")
public void verifyErrorAndMessage(int expectedStatus, String expectedErrorCode) {
    verifyError(expectedStatus);

    HttpClientErrorException ex = (HttpClientErrorException) lastException;
    String responseBody = ex.getResponseBodyAsString();

    assertNotNull(responseBody, "Response body nullo");
    assertTrue(responseBody.contains(expectedErrorCode),
        "Codice errore atteso non trovato: " + expectedErrorCode + "\nResponse body: " + responseBody
    );
}

    @When("il recupero del messaggio per le comunicazioni bonarie fallisce con errore {string}")
    public void getInformalMessageExpectError(String messageIdString) {
        UUID messageId = toUuid(messageIdString);
        try {
            messageResponse = pnPaB2bInternalInformalClientImpl.getMessage(messageId, currentCxId);
            fail("Atteso errore ma la richiesta è andata a buon fine");

        } catch (Exception e) {
            lastException = e;
            messageResponse = null;
            log.info("Errore atteso durante getMessage", e);
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

    private void handleGroup(Map<String, String> data) {

        if (data.containsKey("group")) {

            String value = data.get("group");

            //NULL esplicito → null
            if ("NULL".equalsIgnoreCase(value)) {
                data.put("group", null);
                return;
            }
            // vuoto → fallback API
            if (value == null || value.trim().isEmpty()) {
                data.put("group", currentGroupId);
                return;
            }
            //valore reale → lascia così
            return;
        }
        //CHIAVE ASSENTE → fallback API
        if (currentGroupId != null) {
            data.put("group", currentGroupId);
        }
    }
}
