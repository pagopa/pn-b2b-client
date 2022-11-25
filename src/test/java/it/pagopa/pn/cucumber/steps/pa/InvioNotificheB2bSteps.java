package it.pagopa.pn.cucumber.steps.pa;


import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.impl.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.testclient.*;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.ByteArrayInputStream;
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

    @Value("${pn.retention.time.preload}")
    private Integer retentionTimePreLoad;

    @Value("${pn.retention.time.load}")
    private Integer retentionTimeLoad;


    private final PnPaB2bUtils b2bUtils;
    private final IPnPaB2bClient b2bClient;
    private final PnSafeStorageInfoExternalClientImpl safeStorageClient;
    private final IPnWebUserAttributesClient iPnWebUserAttributesClient;
    private final SharedSteps sharedSteps;

    private NotificationDocument notificationDocumentPreload;
    private NotificationPaymentAttachment notificationPaymentAttachmentPreload;
    private String sha256DocumentDownload;
    private NotificationAttachmentDownloadMetadataResponse downloadResponse;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    public InvioNotificheB2bSteps(PnSafeStorageInfoExternalClientImpl safeStorageClient, IPnWebUserAttributesClient iPnWebUserAttributesClient, SharedSteps sharedSteps) {
        this.safeStorageClient = safeStorageClient;
        this.iPnWebUserAttributesClient = iPnWebUserAttributesClient;
        this.sharedSteps = sharedSteps;
        this.b2bUtils = sharedSteps.getB2bUtils();
        this.b2bClient = sharedSteps.getB2bClient();
    }


    @And("la notifica può essere correttamente recuperata dal sistema tramite codice IUN")
    public void notificationCanBeRetrievedWithIUN() {
        AtomicReference<FullSentNotification> notificationByIun = new AtomicReference<>();
        try {
            Assertions.assertDoesNotThrow(() ->
                    notificationByIun.set(b2bUtils.getNotificationByIun(sharedSteps.getSentNotification().getIun()))
            );
            Assertions.assertNotNull(notificationByIun.get());
        }catch (AssertionFailedError assertionFailedError){
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Given("viene effettuato il pre-caricamento di un documento")
    public void preLoadingOfDocument() {
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
    public void preLoadingOfAttachment () {
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
    public void retentionCheckPreload (String documentType) {
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
    public void retentionCheckLoad(String documentType) {
        String key = "";
        switch (documentType){
            case "ATTO OPPONIBILE":
                key = sharedSteps.getSentNotification().getDocuments().get(0).getRef().getKey();
                break;
            case "PAGOPA":
                key = sharedSteps.getSentNotification().getRecipients().get(0).getPayment().getPagoPaForm().getRef().getKey();
                break;
            default:
                throw new IllegalArgumentException();
        }
        Assertions.assertTrue(checkRetetion(key,retentionTimeLoad));
    }


    @When("si tenta il recupero della notifica dal sistema tramite codice IUN {string}")
    public void retrievalAttemptedIUN(String IUN) {
        try {
            b2bUtils.getNotificationByIun(IUN);
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @When("viene richiesto il download del documento {string}")
    public void documentDownload(String type) {
        String downloadType;
        switch(type) {
            case "NOTIFICA":
                List<NotificationDocument> documents = sharedSteps.getSentNotification().getDocuments();
                this.downloadResponse = b2bClient
                        .getSentNotificationDocument(sharedSteps.getSentNotification().getIun(), Integer.parseInt(documents.get(0).getDocIdx()));

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
                .getSentNotificationAttachment(sharedSteps.getSentNotification().getIun(), 0,downloadType);
        byte[] bytes = Assertions.assertDoesNotThrow(() ->
                b2bUtils.downloadFile(this.downloadResponse.getUrl()));
        this.sha256DocumentDownload = b2bUtils.computeSha256(new ByteArrayInputStream(bytes));
    }

    @When("viene richiesto il download del documento {string} inesistente")
    public void documentAbsentDownload(String type) {
        String downloadType;
        switch(type) {
            case "NOTIFICA":
                List<NotificationDocument> documents = sharedSteps.getSentNotification().getDocuments();
                try {
                    this.downloadResponse = b2bClient
                            .getSentNotificationDocument(sharedSteps.getSentNotification().getIun(),documents.size());
                } catch (HttpStatusCodeException e) {
                    this.sharedSteps.setNotificationError(e);
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
                    .getSentNotificationAttachment(sharedSteps.getSentNotification().getIun(), 100,downloadType);
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @Then("il download si conclude correttamente")
    public void correctlyDownload() {
        Assertions.assertEquals(this.sha256DocumentDownload,this.downloadResponse.getSha256());
    }

    @Then("l'operazione ha prodotto un errore con status code {string}")
    public void operationProducedAnError(String statusCode) {
        HttpStatusCodeException httpStatusCodeException = this.sharedSteps.consumeNotificationError();
        Assertions.assertTrue((httpStatusCodeException != null) &&
                (httpStatusCodeException.getStatusCode().toString().substring(0,3).equals(statusCode)));
    }


    @Then("si verifica la corretta acquisizione della notifica")
    public void correctAcquisitionNotification() {
        Assertions.assertDoesNotThrow(() -> b2bUtils.verifyNotification( sharedSteps.getSentNotification() ));
    }



    @And("viene controllato la presenza del taxonomyCode")
    public void checkTaxonomyCode() {
        Assertions.assertNotNull(this.sharedSteps.getSentNotification().getTaxonomyCode());
        if(this.sharedSteps.getNotificationRequest().getTaxonomyCode() != null){
            Assertions.assertEquals(this.sharedSteps.getNotificationRequest().getTaxonomyCode(),
                    this.sharedSteps.getSentNotification().getTaxonomyCode());
        }

    }


    @And("vengono prodotte le evidenze: metadati e requestID")
    public void evidenceProduced() {
        Assertions.assertNotNull(this.sharedSteps.getNewNotificationResponse());
        logger.info("METADATI: "+'\n'+this.sharedSteps.getNewNotificationResponse());
        logger.info("REQUEST-ID: "+'\n'+this.sharedSteps.getNewNotificationResponse().getNotificationRequestId());
    }


    @Then("si verifica la corretta acquisizione della richiesta di invio notifica")
    public void correctAcquisitionRequest() {
        Assertions.assertNotNull(this.sharedSteps.getNewNotificationResponse());
        Assertions.assertNotNull(this.sharedSteps.getNewNotificationResponse().getNotificationRequestId());
        Assertions.assertNotNull(b2bClient.getNotificationRequestStatus(this.sharedSteps.getNewNotificationResponse().getNotificationRequestId()));
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

    @And("l'importo della notifica è {int}")
    public void priceNotificationVerify(Integer price) {
        try {
            Assertions.assertEquals(this.sharedSteps.getSentNotification().getAmount(), price);
        }catch (AssertionFailedError assertionFailedError){
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }
}
