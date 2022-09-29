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
    private PnPaB2bUtils b2bUtils;

    @Autowired
    private IPnPaB2bClient b2bClient;

    @Autowired
    private DataTableTypeUtil dataTableTypeUtil;

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
        String creditorTaxId = notificationRequest.getRecipients().get(0).getPayment().getCreditorTaxId();

        generateNewNotification();

        this.notificationRequest.getRecipients().get(0).getPayment().setCreditorTaxId(creditorTaxId);
    }

    @And("viene generata una nuova notifica con uguale codice fiscale del creditore e uguale codice avviso")
    public void vienePredispostaEInviataUnaNuovaNotificaConUgualeCodiceFiscaleDelCreditoreEUgualeCodiceAvviso() {
        String creditorTaxId = notificationRequest.getRecipients().get(0).getPayment().getCreditorTaxId();
        String noticeCode = notificationRequest.getRecipients().get(0).getPayment().getNoticeCode();

        generateNewNotification();

        this.notificationRequest.getRecipients().get(0).getPayment().setCreditorTaxId(creditorTaxId);
        this.notificationRequest.getRecipients().get(0).getPayment().setNoticeCode(noticeCode);
    }

    @And("viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken {string}")
    public void vienePredispostaEInviataUnaNuovaNotificaConUgualePaProtocolNumberEIdempotenceToken(String idempotenceToken) {
        String paProtocolNumber = notificationRequest.getPaProtocolNumber();

        generateNewNotification();

        this.notificationRequest.setIdempotenceToken(idempotenceToken);
        this.notificationRequest.setPaProtocolNumber(paProtocolNumber);
    }


    @When("la notifica viene inviata tramite api b2b e si attende che venga accettata")
    public void laNotificaVieneInviataOk() {
        Assertions.assertDoesNotThrow(() -> {
            NewNotificationResponse newNotificationRequest = b2bUtils.uploadNotification(notificationRequest);
            notificationResponseComplete = b2bUtils.waitForRequestAcceptation( newNotificationRequest );
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
            b2bUtils.uploadNotification(notificationRequest);
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
                notificationByIun.set(b2bUtils.getNotificationByIun(notificationResponseComplete.getIun()))
        );

        Assertions.assertNotNull(notificationByIun.get());
    }

    @When("si tenta il recupero della notifica dal sistema tramite codice IUN {string}")
    public void laNotificaPuoEssereCorrettamenteRecuperataDalSistemaTramiteCodiceIUN(String IUN) {
        try {
            b2bUtils.getNotificationByIun(IUN);
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
                this.downloadResponse = b2bClient
                        .getSentNotificationDocument(notificationResponseComplete.getIun(), new BigDecimal(documents.get(0).getDocIdx()));

                byte[] bytes = Assertions.assertDoesNotThrow(() ->
                        b2bUtils.downloadFile(this.downloadResponse.getUrl()));
                this.sha256DocumentDownload = b2bUtils.computeSha256(new ByteArrayInputStream(bytes));
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
        this.downloadResponse = b2bClient
                .getSentNotificationAttachment(notificationResponseComplete.getIun(), new BigDecimal(0),downloadType);
        byte[] bytes = Assertions.assertDoesNotThrow(() ->
                b2bUtils.downloadFile(this.downloadResponse.getUrl()));
        this.sha256DocumentDownload = b2bUtils.computeSha256(new ByteArrayInputStream(bytes));
    }

    @When("viene richiesto il download del documento {string} inesistente")
    public void vieneRichiestoIlDownloadDelDocumentoInesistente(String type) {
        String downloadType;
        switch(type) {
            case "NOTIFICA":
                List<NotificationDocument> documents = notificationResponseComplete.getDocuments();
                try {
                    this.downloadResponse = b2bClient
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
            this.downloadResponse = b2bClient
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


    @Then("si verifica la corretta acquisizione della notifica")
    public void laNotificaCorrettamenteInviata() {
        Assertions.assertDoesNotThrow(() -> b2bUtils.verifyNotification( notificationResponseComplete ));
    }


    private void generateNewNotification(){
        assert this.notificationRequest.getRecipients().get(0).getPayment() != null;
        this.notificationRequest = (dataTableTypeUtil.convertNotificationRequest(new HashMap<>())
                .subject(notificationRequest.getSubject())
                .senderDenomination(notificationRequest.getSenderDenomination())
                .addRecipientsItem(dataTableTypeUtil.convertNotificationRecipient(new HashMap<>())
                        .denomination(notificationRequest.getRecipients().get(0).getDenomination())
                        .taxId(notificationRequest.getRecipients().get(0).getTaxId())));
    }

}
