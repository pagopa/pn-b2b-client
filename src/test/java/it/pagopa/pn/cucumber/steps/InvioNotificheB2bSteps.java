package it.pagopa.pn.cucumber.steps;

import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.FullReceivedNotification;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.impl.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.impl.PnPaB2bExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.springconfig.ApiKeysConfiguration;
import it.pagopa.pn.client.b2b.pa.springconfig.BearerTokenConfiguration;
import it.pagopa.pn.client.b2b.pa.springconfig.RestTemplateConfiguration;
import it.pagopa.pn.client.b2b.pa.testclient.*;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


public class InvioNotificheB2bSteps  {

    @Autowired
    private PnPaB2bUtils b2bUtils;

    @Autowired
    private IPnPaB2bClient b2bClient;

    @Autowired
    private PnSafeStorageInfoExternalClientImpl safeStorageClient;

    @Autowired
    private IPnAppIOB2bClient iPnAppIOB2bClient;

    @Autowired
    private IPnWebUserAttributesClient iPnWebUserAttributesClient;

    @Autowired
    private DataTableTypeUtil dataTableTypeUtil;

    @Value("${pn.retention.time.preload}")
    private Integer retentionTimePreLoad;

    @Value("${pn.retention.time.load}")
    private Integer retentionTimeLoad;

    private NewNotificationRequest notificationRequest;
    private NotificationDocument notificationDocumentPreload;
    private NotificationPaymentAttachment notificationPaymentAttachmentPreload;
    private FullSentNotification notificationResponseComplete;
    private String sha256DocumentDownload;
    private NotificationAttachmentDownloadMetadataResponse downloadResponse;
    private HttpClientErrorException notificationSentError;
    private HttpServerErrorException notficationServerError;


    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    @Given("viene generata una notifica")
    public void vieneGenerataUnaNotifica(@Transpose NewNotificationRequest notificationRequest) {
        this.notificationRequest = notificationRequest;
    }

    @And("destinatario")
    public void destinatario(@Transpose NotificationRecipient recipient) {
        this.notificationRequest.addRecipientsItem(recipient);
    }


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
        Assertions.assertNotNull(notificationResponseComplete);
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

    @Given("viene effettuato il pre-caricamento di un documento")
    public void vieneEffettuatoIlPreCaricamentoDiUnDocumento() {
        NotificationDocument notificationDocument = b2bUtils.newDocument("classpath:/sample.pdf");
        AtomicReference<NotificationDocument> notificationDocumentAtomic = new AtomicReference<>();
        Assertions.assertDoesNotThrow(()-> notificationDocumentAtomic.set(b2bUtils.preloadDocument(notificationDocument)));
        try {
            Thread.sleep( 10 * 1000);
        } catch (InterruptedException e) {
            logger.error("Thread.sleep error retry");
            throw new RuntimeException(e);
        }
        this.notificationDocumentPreload = notificationDocumentAtomic.get();
    }

    @Given("viene effettuato il pre-caricamento di un allegato")
    public void vieneEffettuatoIlPreCaricamentoDiUnAllegato() {
        NotificationPaymentAttachment notificationPaymentAttachment = b2bUtils.newAttachment("classpath:/sample.pdf");
        AtomicReference<NotificationPaymentAttachment> notificationDocumentAtomic = new AtomicReference<>();
        Assertions.assertDoesNotThrow(()-> notificationDocumentAtomic.set(b2bUtils.preloadAttachment(notificationPaymentAttachment)));
        try {
            Thread.sleep( 10 * 1000);
        } catch (InterruptedException e) {
            logger.error("Thread.sleep error retry");
            throw new RuntimeException(e);
        }
        this.notificationPaymentAttachmentPreload = notificationDocumentAtomic.get();
    }

    @Then("viene effettuato un controllo sulla durata della retention di {string} precaricato")
    public void vieneEffettuatoUnControlloSullaDurataDellaRetentionDelDocumentoPrecaricato(String documentType) {
        String key = "";
        switch (documentType){
            case "ATTO OPPONIBILE":
                key = this.notificationDocumentPreload.getRef().getKey();
                break;
            case "PAGOPA":
                key = this.notificationPaymentAttachmentPreload.getRef().getKey();
                break;
            default:
                throw new IllegalArgumentException();
        }
        Assertions.assertTrue(checkRetetion(key,retentionTimePreLoad));
    }

    @And("viene effettuato un controllo sulla durata della retention di {string}")
    public void vieneEffettuatoUnTest(String documentType) {
        String key = "";
        switch (documentType){
            case "ATTO OPPONIBILE":
                key = notificationResponseComplete.getDocuments().get(0).getRef().getKey();
                break;
            case "PAGOPA":
                key = notificationResponseComplete.getRecipients().get(0).getPayment().getPagoPaForm().getRef().getKey();
                break;
            default:
                throw new IllegalArgumentException();
        }
        Assertions.assertTrue(checkRetetion(key,retentionTimeLoad));
    }

    private boolean checkRetetion(String fileKey, Integer retentionTime){
        HashMap<String,String> stringStringHashMap = safeStorageClient.safeStorageInfo(fileKey);
        LocalDateTime localDateTimeNow = LocalDate.now().atStartOfDay();
        OffsetDateTime now = OffsetDateTime.of(localDateTimeNow,ZoneOffset.of("Z"));
        OffsetDateTime retentionUntil = OffsetDateTime.parse(stringStringHashMap.get("retentionUntil"));
        logger.info("now: " + now);
        logger.info("retentionUntil: "+retentionUntil);
        long between = ChronoUnit.DAYS.between(now, retentionUntil);
        logger.info("Difference: "+between);
        return retentionTime == between;
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
                        .getSentNotificationDocument(notificationResponseComplete.getIun(), Integer.parseInt(documents.get(0).getDocIdx()));

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
                .getSentNotificationAttachment(notificationResponseComplete.getIun(), 0,downloadType);
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
                            .getSentNotificationDocument(notificationResponseComplete.getIun(),documents.size());
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
                    .getSentNotificationAttachment(notificationResponseComplete.getIun(), 100,downloadType);
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


    @And("viene controllato la presenza del taxonomyCode")
    public void vieneControllatoLaPresenzaDelTaxonomyCode() {
        Assertions.assertNotNull(this.notificationResponseComplete.getTaxonomyCode());
        if(this.notificationRequest.getTaxonomyCode() != null){
            Assertions.assertEquals(this.notificationRequest.getTaxonomyCode(),this.notificationResponseComplete.getTaxonomyCode());
        }

    }


    @Then("la notifica può essere recuperata tramite AppIO")
    public void laNotificaPuòEssereRecuperataTramiteAppIO() {
        AtomicReference<FullReceivedNotification> notificationByIun = new AtomicReference<>();

        Assertions.assertDoesNotThrow(() ->
                notificationByIun.set(this.iPnAppIOB2bClient.getReceivedNotification(notificationResponseComplete.getIun(), notificationResponseComplete.getRecipients().get(0).getTaxId()))
        );

        Assertions.assertNotNull(notificationByIun.get());
    }

    @Then("il documento notificato può essere recuperata tramite AppIO")
    public void ilDocumentoNotificatoPuòEssereRecuperataTramiteAppIO() {
        List<NotificationDocument> documents = notificationResponseComplete.getDocuments();
        it.pagopa.pn.client.b2b.appIo.generated.openapi.clients.externalAppIO.model.NotificationAttachmentDownloadMetadataResponse sentNotificationDocument =
                iPnAppIOB2bClient.getSentNotificationDocument(notificationResponseComplete.getIun(), Integer.parseInt(documents.get(0).getDocIdx()), notificationResponseComplete.getRecipients().get(0).getTaxId());

        byte[] bytes = Assertions.assertDoesNotThrow(() ->
                b2bUtils.downloadFile(sentNotificationDocument.getUrl()));
        this.sha256DocumentDownload = b2bUtils.computeSha256(new ByteArrayInputStream(bytes));

        Assertions.assertEquals(this.sha256DocumentDownload,sentNotificationDocument.getSha256());
    }

    @And("si tenta il recupero della notifica tramite AppIO")
    public void siTentaIlRecuperoDellaNotificaTramiteAppIO() {
        try {
            this.iPnAppIOB2bClient.getReceivedNotification(notificationResponseComplete.getIun(), "FRMTTR76M06B715E");
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            if (e instanceof HttpServerErrorException) {
                this.notficationServerError = (HttpServerErrorException) e;
            }
        }
    }

    @Then("il tentativo di recupero ha prodotto un errore con status code {string}")
    public void ilTentativoDiRecuperoHaProdottoUnErroreConStatusCode(String statusCode) {
        Assertions.assertTrue((this.notficationServerError != null) &&
                (this.notficationServerError.getStatusCode().toString().substring(0,3).equals(statusCode)));
    }
}
