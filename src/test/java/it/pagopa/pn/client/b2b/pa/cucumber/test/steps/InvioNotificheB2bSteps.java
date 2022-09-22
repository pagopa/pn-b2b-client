package it.pagopa.pn.client.b2b.pa.cucumber.test.steps;

import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.impl.IPnPaB2bClient;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;



public class InvioNotificheB2bSteps  {

    @Autowired
    private PnPaB2bUtils utils;

    @Autowired
    private IPnPaB2bClient client;

    private NewNotificationRequest notificationRequest;
    private FullSentNotification notificationResponseComplete;
    private String sha256DocumentDownload;
    private NotificationAttachmentDownloadMetadataResponse downloadResponse;
    private HttpClientErrorException notificationSentError;


    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    @Given("viene generata una notifica")
    public void vieneGenerataUnaNotifica(@Transpose NewNotificationRequest notificationRequest) {
        this.notificationRequest = notificationRequest;
    }

    @And("destinatario")
    public void destinatario(@Transpose NotificationRecipient recipient) {
        this.notificationRequest.addRecipientsItem(recipient);
    }

    /*
    @And("^viene generata una notifica .*")
    public void vieneGenerataUnaNotificaEtc(@Transpose NewNotificationRequest notificationRequest) {
        System.out.println("sono dentro");
        this.notificationRequest = notificationRequest;
    }
    */

    @And("viene generata una nuova notifica con uguale codice fiscale del creditore e diverso codice avviso")
    public void vienePredispostaEInviataUnaNuovaNotificaConUgualeCodiceFiscaleDelCreditoreEDiversoCodiceAvviso() {
        Assertions.assertDoesNotThrow(()-> notificationRequest.getRecipients().get(0).getPayment());
        this.notificationRequest = utils.newNotificationRequest(notificationRequest.getSubject(),
                notificationRequest.getSenderDenomination(),
                notificationRequest.getRecipients().get(0).getDenomination(),
                notificationRequest.getRecipients().get(0).getTaxId(),
                "",
                "",
                notificationRequest.getRecipients().get(0).getPayment().getCreditorTaxId(),"",
                true,false,false);
    }

    @And("viene generata una nuova notifica con uguale codice fiscale del creditore e uguale codice avviso")
    public void vienePredispostaEInviataUnaNuovaNotificaConUgualeCodiceFiscaleDelCreditoreEUgualeCodiceAvviso() {
        Assertions.assertDoesNotThrow(()-> notificationRequest.getRecipients().get(0).getPayment());
        this.notificationRequest = utils.newNotificationRequest(notificationRequest.getSubject(),
                notificationRequest.getSenderDenomination(),
                notificationRequest.getRecipients().get(0).getDenomination(),
                notificationRequest.getRecipients().get(0).getTaxId(),
                "",
                "",
                notificationRequest.getRecipients().get(0).getPayment().getCreditorTaxId(),
                notificationRequest.getRecipients().get(0).getPayment().getNoticeCode(),
                true,false,false);
    }

    @And("viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken {string}")
    public void vienePredispostaEInviataUnaNuovaNotificaConUgualePaProtocolNumberEIdempotenceToken(String idempotenceToken) {
        Assertions.assertDoesNotThrow(()-> notificationRequest.getRecipients().get(0));
        this.notificationRequest = utils.newNotificationRequest(notificationRequest.getSubject(),
                notificationRequest.getSenderDenomination(),
                notificationRequest.getRecipients().get(0).getDenomination(),
                notificationRequest.getRecipients().get(0).getTaxId(),
                idempotenceToken,
                notificationRequest.getPaProtocolNumber(),
                "","",
                true,false,false);
    }


    @When("la notifica viene inviata e si riceve una risposta")
    public void laNotificaVieneInviataOk() {
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

    @When("la notifica viene inviata")
    public void laNotificaVieneInviataKO() {
        try {
            utils.uploadNotification(notificationRequest);
        } catch (HttpClientErrorException | IOException e) {
            if(e instanceof HttpClientErrorException){
                this.notificationSentError = (HttpClientErrorException)e;
            }
        }
    }

    @And("la notifica può essere correttamente recuperata dal sistema tramite codice IUN")
    public void laNotificaCorrettamenteRecuperataDalSistemaTramiteCodiceIUN() {
        AtomicReference<FullSentNotification> notificationByIun = new AtomicReference<>();

        Assertions.assertDoesNotThrow(() ->
                notificationByIun.set(utils.getNotificationByIun(notificationResponseComplete.getIun()))
        );

        Assertions.assertNotNull(notificationByIun.get());
    }

    @When("si tenta il recupero della notifica dal sistema tramite codice IUN {string}")
    public void laNotificaPuoEssereCorrettamenteRecuperataDalSistemaTramiteCodiceIUN(String IUN) {
        try {
            utils.getNotificationByIun(IUN);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            if (e instanceof HttpClientErrorException) {
                this.notificationSentError = (HttpClientErrorException) e;
            }
        }
    }

    @When("viene richiesto il download del documento {string}")
    public void vieneRichiestoIlDownloadDelDocumento(String type) {
        String downloadType;
        switch(type) {
            case "NOTIFICA":
                List<NotificationDocument> documents = notificationResponseComplete.getDocuments();
                this.downloadResponse = client
                        .getSentNotificationDocument(notificationResponseComplete.getIun(), new BigDecimal(documents.get(0).getDocIdx()));

                byte[] bytes = Assertions.assertDoesNotThrow(() ->
                        utils.downloadFile(this.downloadResponse.getUrl()));
                this.sha256DocumentDownload = utils.computeSha256(new ByteArrayInputStream(bytes));
                return;
            case "PAGOPA":
                downloadType = "PAGOPA";
                break;
            case "F24_FLAT":
                downloadType = "F24_FLAT";
                break;
            case "F24_STANDARD":
                downloadType = "F24_STANDARD";
                break;
            default: throw new IllegalArgumentException();
        }
        this.downloadResponse = client
                .getSentNotificationAttachment(notificationResponseComplete.getIun(), new BigDecimal(0),downloadType);
        byte[] bytes = Assertions.assertDoesNotThrow(() ->
                utils.downloadFile(this.downloadResponse.getUrl()));
        this.sha256DocumentDownload = utils.computeSha256(new ByteArrayInputStream(bytes));
    }

    @When("viene richiesto il download del documento {string} inesistente")
    public void vieneRichiestoIlDownloadDelDocumentoInesistente(String type) {
        String downloadType;
        switch(type) {
            case "NOTIFICA":
                List<NotificationDocument> documents = notificationResponseComplete.getDocuments();
                try {
                    this.downloadResponse = client
                            .getSentNotificationDocument(notificationResponseComplete.getIun(), new BigDecimal(documents.size()));
                } catch (HttpClientErrorException | HttpServerErrorException e) {
                    if(e instanceof HttpClientErrorException){
                        this.notificationSentError = (HttpClientErrorException)e;
                    }
                }
                return;
            case "PAGOPA":
                downloadType = "PAGOPA";
                break;
            case "F24_FLAT":
                downloadType = "F24_FLAT";
                break;
            case "F24_STANDARD":
                downloadType = "F24_STANDARD";
                break;
            default: throw new IllegalArgumentException();
        }
        try {
            this.downloadResponse = client
                    .getSentNotificationAttachment(notificationResponseComplete.getIun(), new BigDecimal(100),downloadType);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            if(e instanceof HttpClientErrorException){
                this.notificationSentError = (HttpClientErrorException)e;
            }
        }
    }

    @Then("il download si conclude correttamente")
    public void ilDownloadSiConcludeCorrettamente() {
        Assertions.assertEquals(this.sha256DocumentDownload,this.downloadResponse.getSha256());
    }

    @Then("l'operazione ha prodotto un errore con status code {string}")
    public void operazioneHaProdottoUnErrore(String statusCode) {
        Assertions.assertTrue((this.notificationSentError != null) &&
                (this.notificationSentError.getStatusCode().toString().substring(0,3).equals(statusCode)));
    }


    @Then("la risposta di ricezione non presenta errori")
    public void laNotificaCorrettamenteInviata() {
        Assertions.assertDoesNotThrow(() -> utils.verifyNotification( notificationResponseComplete ));
    }



}