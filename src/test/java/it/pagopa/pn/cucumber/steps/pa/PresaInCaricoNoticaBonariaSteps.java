package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.domain.Destinatario;
import it.pagopa.pn.client.b2b.pa.domain.DynamoTableName;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.FullSentInformalNotificationV1;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.InformalNotificationRecipientV1;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.InformalNotificationRequestV1;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.InformalNotificationStatusV1;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.InformalTimelineElementV1;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.MessageResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.NewInformalNotificationRequestStatusResponseV1;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.NewInformalNotificationResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.NewMessageRequest;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.NotificationAttachmentDigests;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.NotificationRequestRefusedProblemError;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.TerminationRequestStatus;
import it.pagopa.pn.client.b2b.pa.provider.DestinatarioRegistry;
import it.pagopa.pn.client.b2b.pa.provider.SenderInfoProvider;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaB2bInternalInformalClientImpl;
import it.pagopa.pn.cucumber.steps.SendSharedContext;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.informalNotification.builders.InformalRecipientBuilder;
import it.pagopa.pn.cucumber.steps.informalNotification.datatest.InformalDataTestV1;
import it.pagopa.pn.cucumber.steps.informalNotification.datatest.InformalStatusPollingConfig;
import it.pagopa.pn.cucumber.steps.informalNotification.mapper.InformalNotificationRequestMapper;
import it.pagopa.pn.cucumber.steps.informalNotification.utils.NotificationInformalUtilsV1;
import it.pagopa.pn.cucumber.steps.informalNotification.utils.NotificationInformalUtilsWorkFlowV1;
import it.pagopa.pn.cucumber.utils.GroupPosition;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static it.pagopa.pn.client.b2b.pa.domain.Costanti.COMUNE_ROOT;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
public class PresaInCaricoNoticaBonariaSteps {

    @Getter
    private final SharedSteps sharedSteps;
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
    private final InformalNotificationRequestMapper informalNotificationRequestMapper;
    private final InformalRecipientBuilder recipientBuilder;
    private final NotificationInformalUtilsV1 notificationInformalUtilsV1;
    private final SendSharedContext sendSharedContext;
    private final DestinatarioRegistry destinatarioRegistry;

    private FullSentInformalNotificationV1 fullInformalNotificationResponse;
    private InformalTimelineElementV1 timelineElement;
    private it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internawebrecipientinformal.model.NotificationAttachmentDownloadMetadataResponse receivedAttachmentResponse;

    @Autowired
    public PresaInCaricoNoticaBonariaSteps(NotificationInformalUtilsV1 notificationInformalUtilsV1, InformalNotificationRequestMapper informalNotificationRequestMapper,
                                           InformalRecipientBuilder recipientBuilder, PnPaB2bInternalInformalClientImpl pnPaB2bInternalInformalClientImpl,
                                           SharedSteps sharedSteps, SendSharedContext sendSharedContext, DestinatarioRegistry destinatarioRegistry) {
        this.sharedSteps = sharedSteps;
        this.pnPaB2bInternalInformalClientImpl = pnPaB2bInternalInformalClientImpl;
        this.informalNotificationRequestMapper = informalNotificationRequestMapper;
        this.notificationInformalUtilsV1 = notificationInformalUtilsV1;
        this.recipientBuilder = recipientBuilder;
        this.sendSharedContext = sendSharedContext;
        this.destinatarioRegistry = destinatarioRegistry;
    }

    // STEP CREAZIONE E INVIO NOTIFICA

    @And("mittente della notifica bonaria: {string}")
    public void setSenderInformal(String paName) {
        setSenderContext(paName);
        if (!paName.equalsIgnoreCase("Comune_Root")) {
            this.currentGroupId =
                    sharedSteps.getGroupIdByPa(paName, GroupPosition.FIRST);
            sendSharedContext.getInformalNotificationContext().setGroupId(currentGroupId);
        }
        sendSharedContext.getInformalNotificationContext().setSenderId(currentCxId);
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

        assertNotNull(informalNotificationRequestV1, "Creare prima la notifica bonaria");
        Map<String, String> cleanedData = data.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().trim(), e -> e.getValue() != null ? e.getValue().trim() : null));
        InformalNotificationRecipientV1 recipient = recipientBuilder.build(cleanedData, currentCxId);
        informalNotificationRequestV1.getRecipients().add(recipient);
        sendSharedContext.getInformalNotificationContext().getRecipient().setDestinatario(destinatarioRegistry.destinatario(data.get("denomination")));
    }

    /**
     * Crea le {@code notificationNumber} notifiche bonarie in parallelo invece che in sequenza, cosi'
     * l'attesa del raggiungimento dello stato {@code COMPLETED_REACHED} di ciascuna (che può richiedere
     * diversi secondi) avviene contemporaneamente per tutte anziché sommarsi. Ogni notifica viene creata
     * e tracciata da {@link #createAndAwaitSingleInformalNotification} usando solo variabili locali: i
     * campi di istanza della classe (es. {@code savedIun}, {@code statusResponse}) sono pensati per una
     * singola notifica per scenario e verrebbero sovrascritti in modo incoerente da thread concorrenti,
     * quindi non vengono toccati da questo flusso.
     */
    @Given("vengono create {int} notifiche bonarie per la pa {string} con campagna {string}")
    public void creaNotificheBonarie(int notificationNumber, String paName, String campaignId, Map<String, String> recipientData) {
        setSenderInformal(paName);
        Map<String, String> cleanedRecipientData = trimData(recipientData);
        sendSharedContext.getInformalNotificationContext().getRecipient().setDestinatario(destinatarioRegistry.destinatario(cleanedRecipientData.get("denomination")));
        IntStream.range(0, notificationNumber)
                .parallel()
                .forEach(i -> createAndAwaitSingleInformalNotification(campaignId, cleanedRecipientData));
    }

    private void createAndAwaitSingleInformalNotification(String campaignId, Map<String, String> recipientData) {
        Map<String, String> creationData = new HashMap<>();
        creationData.put("campaignId", campaignId);
        handleGroup(creationData);
        InformalNotificationRequestV1 request = informalNotificationRequestMapper.buildInformalNotificationRequest(creationData);

        request.getRecipients().add(recipientBuilder.build(new HashMap<>(recipientData), currentCxId));

        try {
            request = notificationInformalUtilsV1.preloadAndPrepare(request, paName);
        } catch (IOException e) {
            throw new UncheckedIOException("Errore durante la preparazione della notifica bonaria con campagna " + campaignId, e);
        }

        NewInformalNotificationResponse response = pnPaB2bInternalInformalClientImpl.sendNewInformalNotificationV1(currentCxId, request);
        String notificationRequestId = response.getNotificationRequestId();

        NewInformalNotificationRequestStatusResponseV1 acceptedStatus =
                pollNotificationRequestStatus(notificationRequestId, InformalNotificationStatusV1.ACCEPTED.getValue());
        pollFullNotificationStatus(acceptedStatus.getIun(), InformalNotificationStatusV1.COMPLETED_REACHED.getValue());
    }

    /**
     * Attende (fino a un massimo di 12 minuti) che la notifica bonaria identificata da {@code iun}
     * raggiunga {@code expectedStatus}. A differenza di {@link #verifyFinalNotificationStatus}, non
     * legge né scrive campi di istanza: può quindi essere invocato in sicurezza da più thread in
     * parallelo, ognuno con il proprio IUN (vedi {@link #createAndAwaitSingleInformalNotification}).
     */
    private FullSentInformalNotificationV1 pollFullNotificationStatus(String iun, String expectedStatus) {
        return pollUntilStatus("notifica IUN=" + iun,
                () -> pnPaB2bInternalInformalClientImpl.getSentInformalNotificationSender(currentCxId, iun, true),
                notification -> notification.getNotificationStatus().getValue(),
                expectedStatus, Duration.ofMinutes(12), Duration.ofSeconds(30));
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

    @And("si verifica che il {string} sia correttamente compilato in pn-Notifications")
    public void gettaxonomyCodeFromDb(String element) {

        QueryResponse response = sharedSteps.getDynamoDbService().call(DynamoTableName.NOTIFICATIONS, Map.of(":v_iun", AttributeValue.builder().s(savedIun).build()));

        assertThat(response.items().size()).as("Query on pn-Notifications non ha restituito nessun elemento").isGreaterThan(0);
        Map<String, AttributeValue> dynamoNotification = response.items().get(0);
        assertThat(dynamoNotification.containsKey(element)).isTrue();
        assertThat(dynamoNotification.get(element).s()).isNotNull();
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


    @Then("il download del destinatario risulta correttamente effettuato")
    public void verifyReceivedDownload() {

        assertNull(lastException);
        assertNotNull(receivedAttachmentResponse);
        assertNotNull(receivedAttachmentResponse.getUrl(), "URL download mancante");
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


    @When("il destinatario {destinatario} tenta il recupero del documento della notifica bonaria")
    public void getReceivedDocument(Destinatario destinatario) {

        String recipientCxId = sharedSteps.getDestinatarioRegistry().getCxId(destinatario);
        try {
            receivedAttachmentResponse = pnPaB2bInternalInformalClientImpl.getReceivedInformalNotificationDocument(recipientCxId, savedIun, toRecipientCxType(destinatario), 0);
            lastException = null;
        } catch (Exception e) {
            lastException = e;
            receivedAttachmentResponse = null;
        }
    }

    @When("il destinatario {destinatario} tenta il recupero dell'allegato di pagamento della notifica bonaria")
    public void getReceivedAttachment(Destinatario destinatario) {

        String recipientCxId = sharedSteps.getDestinatarioRegistry().getCxId(destinatario);
        try {
            receivedAttachmentResponse = pnPaB2bInternalInformalClientImpl.getReceivedInformalNotificationAttachment(recipientCxId, savedIun, toRecipientCxType(destinatario), 0);
            lastException = null;
        } catch (Exception e) {
            lastException = e;
            receivedAttachmentResponse = null;
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

    @When("si verifica lo stato della richiesta della notifica bonaria con errore")
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

    @Then("si attende che la notifica bonaria passi in stato {string}")
    public void verifyFinalNotificationStatus(String expectedStatus) {

        AtomicReference<String> lastStatus = new AtomicReference<>(null);
        AtomicReference<FullSentInformalNotificationV1> lastNotification = new AtomicReference<>();
        InformalStatusPollingConfig.DefaultStatusValue config = InformalStatusPollingConfig.DefaultStatusValue.valueOf(expectedStatus);

        try {
            await().atMost(Duration.ofMinutes(12)).pollInterval(Duration.ofSeconds(10)).until(() -> {
                FullSentInformalNotificationV1 notification = getFullInformalNotification();

                if (notification == null) {
                    return false;
                }
                lastNotification.set(notification);
                String actualStatus = notification.getNotificationStatus().getValue();
                lastStatus.set(actualStatus);

                log.info("Polling stato notifica: {}", actualStatus);
                log.info("IUN: {}", notification.getIun());

                if (config.getStopStatuses().contains(actualStatus)) {
                    throw new AssertionError("Raggiunto stato incompatibile con quello atteso.\n" + "Atteso: " + expectedStatus + "\n" + "Raggiunto: " + actualStatus + "\n" + "Response: " + notification + "\n" + "Ultima FullSentInformalNotificationV1:\n" + lastNotification.get() + "\n" + "IUN:\n" + savedIun);
                }
                return expectedStatus.equals(actualStatus);
            });

        } catch (Exception e) {
            throw new AssertionError("Stato finale non valido.\n" + "Atteso: " + expectedStatus + "\n" + "Ultimo stato ricevuto: " + lastStatus.get() + "\n" + "Response: " + getFullInformalNotification(), e);
        }
        savedIun = statusResponse.getIun();
        sharedSteps.setNotificationIun(savedIun);
        sendSharedContext.getInformalNotificationContext().setIun(savedIun);
        lastException = null;
    }

    /**
     * Attende (fino a un massimo di 12 minuti) che la richiesta di notifica bonaria identificata da
     * {@code notificationRequestId} raggiunga {@code expectedStatus}. Come {@link #pollFullNotificationStatus},
     * non tocca campi di istanza ed è quindi sicuro da chiamare in parallelo su più notifiche.
     */
    private NewInformalNotificationRequestStatusResponseV1 pollNotificationRequestStatus(String notificationRequestId, String expectedStatus) {
        return pollUntilStatus("richiesta notificationRequestId=" + notificationRequestId,
                () -> pnPaB2bInternalInformalClientImpl.getNotificationStatusByRequestId(currentCxId, notificationRequestId),
                NewInformalNotificationRequestStatusResponseV1::getNotificationRequestStatus,
                expectedStatus, Duration.ofMinutes(12), Duration.ofSeconds(3));
    }

    /**
     * Attende che {@code fetch} restituisca un risultato il cui stato (estratto con {@code statusExtractor})
     * coincida con {@code expectedStatus}, ripetendo la chiamata ogni {@code pollInterval} fino a
     * {@code timeout}. Generalizza il polling usato per lo stato della richiesta e per lo stato della
     * notifica bonaria: non tocca campi di istanza, quindi è sicuro da invocare in parallelo su thread diversi.
     */
    private <T> T pollUntilStatus(String description, Supplier<T> fetch, Function<T, String> statusExtractor,
                                  String expectedStatus, Duration timeout, Duration pollInterval) {
        AtomicReference<String> lastStatus = new AtomicReference<>(null);
        AtomicReference<T> lastResult = new AtomicReference<>();
        try {
            await().atMost(timeout).pollInterval(pollInterval).until(() -> {
                T result;
                try {
                    result = fetch.get();
                } catch (Exception e) {
                    log.error("Errore durante il recupero di {}", description, e);
                    throw new RuntimeException("Errore durante il recupero di " + description, e);
                }
                if (result == null) {
                    return false;
                }
                lastResult.set(result);
                String actualStatus = statusExtractor.apply(result);
                lastStatus.set(actualStatus);
                log.info("Polling stato {}: {}", description, actualStatus);
                return expectedStatus.equals(actualStatus);
            });
        } catch (Exception e) {
            throw new AssertionError("Stato finale non valido.\n" + "Atteso: " + expectedStatus + "\n" + "Ultimo stato ricevuto: " + lastStatus.get() + "\n" + "Response: " + lastResult.get(), e);
        }
        return lastResult.get();
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
        assertTrue(responseBody.contains(expectedErrorCode), "Codice errore atteso non trovato: " + expectedErrorCode + "\nResponse body: " + responseBody);
    }

    @When("il recupero del messaggio per le comunicazioni bonarie fallisce")
    public void getInformalMessageExpectError() {
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
        return UUID.fromString(value);
    }

    private Map<String, String> trimData(Map<String, String> data) {
        return data.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().trim(),
                        e -> e.getValue() != null ? e.getValue().trim() : null
                ));
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

    //*********************
    //***** WORKFLOW *****
    //*********************

    @And("il destinatario {destinatario} legge la notifica bonaria")
    public void recipientReadsInformalNotification(Destinatario destinatario) {

        String recipientCxId = sharedSteps.getDestinatarioRegistry().getCxId(destinatario);
        assertDoesNotThrow(() -> pnPaB2bInternalInformalClientImpl.getReceivedInformalNotification(recipientCxId, savedIun, toRecipientCxType(destinatario)));
        try {
            Thread.sleep(sharedSteps.getWorkFlowWait());
        } catch (InterruptedException exc) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(exc);
        }
    }

    @Given("l'ente mittente {string} compila una notifica bonaria con i seguenti dati:")
    public void createInformalNotification(String paName, Map<String, String> dataInput) {

        // STEP 1 - setSenderInformal
        setSenderContext(paName);

        // STEP 2 - createInformal
        Map<String, String> data = new HashMap<>(dataInput);
        handleGroup(data);
        informalNotificationRequestV1 = informalNotificationRequestMapper.buildInformalNotificationRequest(data);

        // STEP 3 - addInformalRecipientLight
        Map<String, String> cleanedData = data.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().trim(), e -> e.getValue() != null ? e.getValue().trim() : null));
        InformalNotificationRecipientV1 recipient = recipientBuilder.build(cleanedData, currentCxId);
        informalNotificationRequestV1.getRecipients().add(recipient);
        sendSharedContext.getInformalNotificationContext().getRecipient().setDestinatario(destinatarioRegistry.destinatario(data.get("denomination")));
    }

    @Then("si attende che venga prodotto l'elemento {string} della notifica bonaria")
    public void verifyTimelineElementArrive(String category) {

        timelineElement = NotificationInformalUtilsWorkFlowV1.waitForTimelineElement(this::getFullInformalNotification, category, null);
        assertNotNull(timelineElement);
    }

    @Then("si attende che venga prodotto l'elemento {string} della notifica bonaria con dettagli")
    public void verifyTimelineElementAndDetailsArrive(String category, Map<String, String> dataMap) {

        InformalDataTestV1 expected = InformalDataTestV1.convertMap(dataMap);
        timelineElement = NotificationInformalUtilsWorkFlowV1.waitForTimelineElement(this::getFullInformalNotification, category, expected != null ? expected.getTimelineElement() : null);
        assertNotNull(timelineElement);
    }

    private FullSentInformalNotificationV1 getFullInformalNotification() {

        try {
            fullInformalNotificationResponse = pnPaB2bInternalInformalClientImpl.getSentInformalNotificationSender(currentCxId, savedIun, true);
            lastException = null;
            log.info("Full informal notification response: {}", fullInformalNotificationResponse);
            return fullInformalNotificationResponse;

        } catch (Exception e) {

            log.error("Errore durante il recupero della notifica. IUN={}", savedIun, e);
            lastException = e;
            fullInformalNotificationResponse = null;

            throw new RuntimeException("Errore durante il recupero della notifica " + savedIun, e);
        }
    }

    private void setSenderContext(String paName) {

        this.paName = paName;
        SenderInfoProvider.PaInfo paInfo = sharedSteps.getSenderInfoProvider().getPaInfo(paName);
        this.currentCxId = paInfo.getSenderId();
        sendSharedContext.getInformalNotificationContext().setSenderId(currentCxId);

        if (!COMUNE_ROOT.equalsIgnoreCase(paName)) {
            this.currentGroupId = sharedSteps.getGroupIdByPa(paName, GroupPosition.FIRST);
        }
    }


    @Then("viene inviata una nuova notifica bonaria e si attende che vada in stato {string}")
    public void sendInformalAndWaitStatus(String expectedStatus) throws IOException {

        informalNotificationRequestV1 = notificationInformalUtilsV1.preloadAndPrepare(informalNotificationRequestV1, paName);
        newInformalNotificationResponse = pnPaB2bInternalInformalClientImpl.sendNewInformalNotificationV1(currentCxId, informalNotificationRequestV1);
        savedNotificationRequestId = newInformalNotificationResponse.getNotificationRequestId();
        log.info("Notifica inviata. notificationRequestId={}", savedNotificationRequestId);

        AtomicReference<String> lastStatus = new AtomicReference<>(null);
        InformalStatusPollingConfig.DefaultStatusValue config = InformalStatusPollingConfig.DefaultStatusValue.valueOf(expectedStatus);

        try {
            await().atMost(Duration.ofMinutes(12)).pollInterval(Duration.ofSeconds(5)).until(() -> {
                statusResponse = pnPaB2bInternalInformalClientImpl.getNotificationStatusByRequestId(currentCxId, savedNotificationRequestId);

                if (statusResponse == null) {
                    return false;
                }
                String actualStatus = statusResponse.getNotificationRequestStatus();
                lastStatus.set(actualStatus);
                log.info("Polling stato: {}", actualStatus);

                if (config.getStopStatuses().contains(actualStatus)) {
                    throw new AssertionError("Raggiunto stato incompatibile con quello atteso.\n" + "Atteso: " + expectedStatus + "\n" + "Raggiunto: " + actualStatus + "\n" + "Response: " + statusResponse);
                }
                return expectedStatus.equals(actualStatus);
            });
        } catch (Exception e) {
            throw new AssertionError("Stato finale non valido.\n" + "Atteso: " + expectedStatus + "\n" + "Ultimo stato ricevuto: " + lastStatus.get() + "\n" + "Response: " + statusResponse, e);
        }
        log.info("=== RESPONSE FINALE NOTIFICA ===");
        log.info("Response: {}", statusResponse);

        savedIun = statusResponse.getIun();
        sharedSteps.setNotificationIun(savedIun);
        lastException = null;
    }

    private it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internawebrecipientinformal.model.CxTypeAuthFleet toRecipientCxType(Destinatario destinatario) {
        return "PF".equalsIgnoreCase(destinatario.getRecipientType()) ? it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internawebrecipientinformal.model.CxTypeAuthFleet.PF : it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internawebrecipientinformal.model.CxTypeAuthFleet.PG;
    }
}



