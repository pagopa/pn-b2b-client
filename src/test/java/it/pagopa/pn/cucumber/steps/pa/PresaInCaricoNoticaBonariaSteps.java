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
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.dataTable.InformalNotificationRequestMapper;
import it.pagopa.pn.cucumber.steps.pa.utilityInformalVersion.NotificationInformalUtilsV1;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static it.pagopa.pn.cucumber.utils.NotificationInformalValue.*;
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
    private HttpStatusCodeException notificationError;
    @Getter
    private final PnPollingFactory pnPollingFactory;
    @Getter
    private final TimingForPolling timingForPolling;

    @Getter
    private final PnPaB2bInternalInformalClientImpl pnPaB2bInternalInformalClientImpl;

    private NewMessageRequest newMessageRequest;
    private InformalNotificationRequestV1 informalNotificationRequestV1;
    private MessageResponse messageResponse;
    private UUID messageId;
    private Exception lastException;
    private String savedIun;

    private NewInformalNotificationResponse newInformalNotificationResponse;
    private InformalNotificationRequestMapper informalNotificationRequestMapper;
    private NotificationAttachmentDownloadMetadataResponse attachmentResponse;

    private NewInformalNotificationRequestStatusResponseV1 statusResponse;
    private String savedNotificationRequestId;
    private NotificationInformalUtilsV1 notificationInformalUtilsV1;


    @Autowired
    public PresaInCaricoNoticaBonariaSteps(InformalNotificationRequestMapper informalNotificationRequestMapper, PnPaB2bInternalInformalClientImpl pnPaB2bInternalInformalClientImpl, SharedSteps sharedSteps, TimingForPolling timingForPolling, IPnPrivateDeliveryPushExternalClient pnPrivateDeliveryPushExternalClient, NotificationInformalUtilsV1 notificationInformalUtilsV1) {
        this.sharedSteps = sharedSteps;
        this.timingForPolling = timingForPolling;
        this.pnPrivateDeliveryPushExternalClient = pnPrivateDeliveryPushExternalClient;
        this.pnPaB2bInternalInformalClientImpl = pnPaB2bInternalInformalClientImpl;
        this.externalClient = sharedSteps.getPnExternalServiceClient();
        this.b2bClient = sharedSteps.getB2bClient();
        this.pnPollingFactory = sharedSteps.getPollingFactory();
        this.informalNotificationRequestMapper = informalNotificationRequestMapper;
        this.notificationInformalUtilsV1 = notificationInformalUtilsV1;
    }


    @And("destinatario della notifica bonaria")
    public void addInformalRecipient(Map<String, String> data) {

        assertNotNull(informalNotificationRequestV1, "Creare prima la notifica bonaria");

        InformalNotificationRecipientV1 recipient = new InformalNotificationRecipientV1();

        // recipientType (PF / PG)
        String recipientType = getValue(data, RECIPIENT_TYPE.key);
        if (recipientType != null) {
            recipient.setRecipientType(InformalNotificationRecipientV1.RecipientTypeEnum.fromValue(recipientType));
        }
        recipient.setTaxId(getValue(data, RECIPIENT_TAX_ID.key));
        recipient.setDenomination(getValue(data, RECIPIENT_DENOMINATION.key));

        // digital domicile
        String digitalDomicile = getValue(data, DIGITAL_DOMICILE.key);
        if (digitalDomicile != null) {
            recipient.setDigitalDomicile(new NotificationDigitalAddress().type(NotificationDigitalAddress.TypeEnum.PEC).address(digitalDomicile));
        } else {
            recipient.setDigitalDomicile(null);
        }

        informalNotificationRequestV1.getRecipients().add(recipient);

        //Pagamenti
        int paymentNumber = Integer.parseInt(getValue(data, PAYMENT_MULTY_NUMBER.key));

        List<InformalNotificationPaymentItem> payments = new ArrayList<>();

        for (int i = 0; i < paymentNumber; i++) {

            NotificationPaymentAttachment attachment = informalNotificationRequestMapper.buildPaymentAttachment(data);

            PagoPaPaymentBase pagoPa = new PagoPaPaymentBase().noticeCode(generateNoticeCode(getValue(data, PAYMENT_NOTICE_CODE.key), i)).creditorTaxId(getValue(data, PAYMENT_CREDITOR_TAX_ID.key)).attachment(attachment);

            InformalNotificationPaymentItem item = new InformalNotificationPaymentItem();
            item.setPagoPa(pagoPa);

            payments.add(item);
        }
        recipient.setPayments(payments);
    }

    @Then("viene inviata una nuova notifica bonaria")
    public void sendInformal() {

        try {

            informalNotificationRequestV1 =
                    notificationInformalUtilsV1.preloadAndPrepare(informalNotificationRequestV1);


            newInformalNotificationResponse = pnPaB2bInternalInformalClientImpl.sendNewInformalNotificationV1(informalNotificationRequestV1);

            //TODO t bonarie -> savedIun =

            savedNotificationRequestId =
                    newInformalNotificationResponse.getNotificationRequestId();

            lastException = null;
        } catch (Exception e) {
            lastException = e;
            messageResponse = null;
            newInformalNotificationResponse = null;
            log.info("Eccezione: ", e);
        }
    }

    @And("mittente della notifica bonaria: {string}")
    public void setSenderInformal(String paName) {

        switch (paName) {
            case "COMUNE_1" -> pnPaB2bInternalInformalClientImpl.setCxId(senderId);

            case "COMUNE_2" -> pnPaB2bInternalInformalClientImpl.setCxId(senderId2);

            case "COMUNE_MULTI" -> pnPaB2bInternalInformalClientImpl.setCxId(senderIdGA);

            default -> throw new IllegalArgumentException("PA bonaria non valida: " + paName);
        }
    }

    @Given("viene creata una nuova notifica bonaria con i seguenti parametri")
    public void createInformal(InformalNotificationRequestV1 request) {
        log.info("Invio notifica bonaria - request: {}", request);
        informalNotificationRequestV1 = request;
    }

    @Given("viene creata una nuova notifica bonaria con valori di default")
    public void createInformal() {

        informalNotificationRequestV1 =
                informalNotificationRequestMapper.buildInformalNotificationRequest(Map.of());

        log.info("Invio notifica bonaria - request: {}", informalNotificationRequestV1);
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

    //*** STEP ALLEGATI PAGAMENTO

    @When("si tenta il recupero allegato pagamento della notifica bonaria")
    public void getAttachment() {
        try {
            attachmentResponse = pnPaB2bInternalInformalClientImpl.getSentInformalNotificationAttachment(savedIun, 0, 0);

            lastException = null;

        } catch (Exception e) {
            lastException = e;
            attachmentResponse = null;
        }
    }

    @When("si tenta il recupero allegato pagamento con IUN {string}")
    public void getAttachmentWithIun(String iun) {
        try {
            attachmentResponse = pnPaB2bInternalInformalClientImpl.getSentInformalNotificationAttachment(iun, 0, 0);

            lastException = null;

        } catch (Exception e) {
            lastException = e;
            attachmentResponse = null;
        }
    }

    @When("si tenta il recupero allegato pagamento con recipient {int} e attachment {int}")
    public void getAttachmentCustom(int recipientIdx, int attachmentIdx) {
        try {
            attachmentResponse = pnPaB2bInternalInformalClientImpl.getSentInformalNotificationAttachment(savedIun, recipientIdx, attachmentIdx);

            lastException = null;

        } catch (Exception e) {
            lastException = e;
            attachmentResponse = null;
        }
    }

    //*** STEP MESSAGGI ***

    @When("si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie")
    public void createNewInformalMessage(NewMessageRequest newMessageRequest) {
        try {
            this.messageResponse = pnPaB2bInternalInformalClientImpl.createMessage(newMessageRequest);
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

    //*** RECUPERO DOCUMENTI ***


    @When("si tenta il recupero documento della notifica bonaria")
    public void getDocument() {
        try {
            attachmentResponse =
                    pnPaB2bInternalInformalClientImpl.getSentInformalNotificationDocument(savedIun, 0);

            lastException = null;

        } catch (Exception e) {
            lastException = e;
            attachmentResponse = null;
        }
    }


    @When("si tenta il recupero documento con indice {int}")
    public void getDocumentWithIndex(int docIdx) {
        try {
            attachmentResponse =
                    pnPaB2bInternalInformalClientImpl
                            .getSentInformalNotificationDocument(savedIun, docIdx);

            lastException = null;

        } catch (Exception e) {
            lastException = e;
            attachmentResponse = null;
        }
    }


    @When("si tenta il recupero documento con IUN {string}")
    public void getDocumentWithIun(String iun) {
        try {
            attachmentResponse =
                    pnPaB2bInternalInformalClientImpl
                            .getSentInformalNotificationDocument(iun, 0);

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
            statusResponse =
                    pnPaB2bInternalInformalClientImpl
                            .getNotificationStatusByRequestId(
                                    savedNotificationRequestId
                            );

            lastException = null;

        } catch (Exception e) {
            lastException = e;
            statusResponse = null;
        }
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

    private String generateNoticeCode(String base, int index) {
        if (base == null) return null;
        return base.substring(0, base.length() - 1) + index;
    }
}