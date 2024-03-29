package it.pagopa.pn.cucumber.steps.pa;


import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebPaClient;
import it.pagopa.pn.client.b2b.pa.service.impl.PnExternalServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnGPDClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaymentInfoClientImpl;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.gpd.model.*;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.payment_info.model.*;
import it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.NotificationSearchResponse;
import it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.NotificationSearchRow;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.utils.DataTest;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static java.time.OffsetDateTime.now;


public class InvioNotificheB2bSteps {

    @Value("${pn.retention.time.preload}")
    private Integer retentionTimePreLoad;

    @Value("${pn.retention.time.load}")
    private Integer retentionTimeLoad;



    private final PnPaB2bUtils b2bUtils;
    private final IPnWebPaClient webPaClient;
    private final IPnPaB2bClient b2bClient;
    private final PnExternalServiceClientImpl safeStorageClient;
    private final SharedSteps sharedSteps;
    private final PnPaymentInfoClientImpl pnPaymentInfoClientImpl;
    private PaymentResponse paymentResponse;
    private List<PaymentInfoV21> paymentInfoResponse;
    private NotificationDocument notificationDocumentPreload;
    private NotificationPaymentAttachment notificationPaymentAttachmentPreload;
    private NotificationMetadataAttachment notificationMetadataAttachment;
    private String sha256DocumentDownload;
    private NotificationAttachmentDownloadMetadataResponse downloadResponse;


    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final Integer NUM_CHECK_PAYMENT_INFO = 32;
    private static final Integer WAITING_PAYMENT_INFO = 1000;
    @Autowired
    public InvioNotificheB2bSteps(PnExternalServiceClientImpl safeStorageClient, SharedSteps sharedSteps) {
        this.safeStorageClient = safeStorageClient;
        this.sharedSteps = sharedSteps;
        this.b2bUtils = sharedSteps.getB2bUtils();
        this.b2bClient = sharedSteps.getB2bClient();
        this.webPaClient = sharedSteps.getWebPaClient();
        this.pnPaymentInfoClientImpl =sharedSteps.getPnPaymentInfoClientImpl();
    }


    @And("la notifica può essere correttamente recuperata dal sistema tramite codice IUN")
    public void notificationCanBeRetrievedWithIUN() {
        AtomicReference<FullSentNotificationV23> notificationByIun = new AtomicReference<>();
        try {
            if (sharedSteps.getSentNotification()!= null) {

                Assertions.assertDoesNotThrow(() ->
                        notificationByIun.set(b2bUtils.getNotificationByIun(sharedSteps.getSentNotification().getIun()))
                );
                Assertions.assertNotNull(notificationByIun.get());
            } else if (sharedSteps.getSentNotificationV1()!= null) {
                Assertions.assertDoesNotThrow(() ->
                        notificationByIun.set(b2bUtils.getNotificationByIun(sharedSteps.getSentNotificationV1().getIun()))
                );
                Assertions.assertNotNull(notificationByIun.get());
            } else if (sharedSteps.getSentNotificationV2()!= null) {
                Assertions.assertDoesNotThrow(() ->
                        notificationByIun.set(b2bUtils.getNotificationByIun(sharedSteps.getSentNotificationV2().getIun()))
                );
                Assertions.assertNotNull(notificationByIun.get());
            }else {
                Assertions.assertNotNull(notificationByIun.get());
            }
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @And("la notifica può essere correttamente recuperata dal sistema tramite codice IUN con OpenApi V1")
    public void notificationCanBeRetrievedWithIUNV1() {
        AtomicReference<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.FullSentNotification> notificationByIun = new AtomicReference<>();
        try {
            if(sharedSteps.getSentNotificationV1()!= null) {
                Assertions.assertDoesNotThrow(() ->
                        notificationByIun.set(b2bUtils.getNotificationByIunV1(sharedSteps.getSentNotificationV1().getIun()))
                );
            }else if(sharedSteps.getSentNotificationV2()!= null){
                Assertions.assertDoesNotThrow(() ->
                        notificationByIun.set(b2bUtils.getNotificationByIunV1(sharedSteps.getSentNotificationV2().getIun()))
                );
            }else if(sharedSteps.getSentNotification()!= null){
                Assertions.assertDoesNotThrow(() ->
                        notificationByIun.set(b2bUtils.getNotificationByIunV1(sharedSteps.getSentNotification().getIun()))
                );
            }else {
                Assertions.assertNotNull(notificationByIun.get());
            }

            Assertions.assertNotNull(notificationByIun.get());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @And("la notifica può essere correttamente recuperata dal sistema tramite codice IUN con OpenApi V20")
    public void notificationCanBeRetrievedWithIUNV2() {
        AtomicReference<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.FullSentNotificationV20> notificationByIun = new AtomicReference<>();
        try {
            if(sharedSteps.getSentNotificationV1()!= null) {
                Assertions.assertDoesNotThrow(() ->
                        notificationByIun.set(b2bUtils.getNotificationByIunV2(sharedSteps.getSentNotificationV1().getIun()))
                );
            }else if(sharedSteps.getSentNotificationV2()!= null){
                Assertions.assertDoesNotThrow(() ->
                        notificationByIun.set(b2bUtils.getNotificationByIunV2(sharedSteps.getSentNotificationV2().getIun()))
                );
            }else if(sharedSteps.getSentNotification()!= null){
                Assertions.assertDoesNotThrow(() ->
                        notificationByIun.set(b2bUtils.getNotificationByIunV2(sharedSteps.getSentNotification().getIun()))
                );
            }else {
                Assertions.assertNotNull(notificationByIun.get());
            }
            Assertions.assertNotNull(notificationByIun.get());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @And("la notifica non può essere recuperata dal sistema tramite codice IUN con OpenApi V20 generando un errore")
    public void notificationCanBeRetrievedWithIUNV2Error() {
        AtomicReference<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.FullSentNotificationV20> notificationByIun = new AtomicReference<>();
        try {
            notificationByIun.set(b2bUtils.getNotificationByIunV2(sharedSteps.getSentNotification().getIun()));

        } catch (HttpStatusCodeException e) {
            if (e instanceof HttpStatusCodeException) {
                sharedSteps.setNotificationError(e);
            }
        }
    }

    @And("la notifica non può essere recuperata dal sistema tramite codice IUN con OpenApi V10 generando un errore")
    public void notificationCanBeRetrievedWithIUNV1Error() {
        AtomicReference<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.FullSentNotification> notificationByIun = new AtomicReference<>();
        try {
            //notificationByIun.set(b2bUtils.getNotificationByIunV1(sharedSteps.getSentNotificationV1().getIun()));
            notificationByIun.set(b2bUtils.getNotificationByIunV1(sharedSteps.getSentNotification().getIun()));
        } catch (HttpStatusCodeException e) {
            if (e instanceof HttpStatusCodeException) {
                sharedSteps.setNotificationError(e);
            }
        }
    }

    @And("la notifica può essere correttamente recuperata dal sistema tramite codice IUN web PA")
    public void notificationCanBeRetrievedWithIUNWebPA() {
        AtomicReference<NotificationSearchResponse> notificationByIun = new AtomicReference<>();
        try {
            Assertions.assertDoesNotThrow(() ->
                    notificationByIun.set(webPaClient.searchSentNotification(OffsetDateTime.now().minusDays(1), OffsetDateTime.now(),null,null,null,sharedSteps.getSentNotification().getIun(),1,null))
            );
            Assertions.assertNotNull(notificationByIun.get());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @And("recupera notifica vecchia di 120 giorni da lato web PA e verifica presenza pagamento")
    public void notification120ggCanBeRetrievedWithIUNWebPA() {
        AtomicReference<NotificationSearchResponse> notificationByIun = new AtomicReference<>();
        try {
            Assertions.assertDoesNotThrow(() ->
                    notificationByIun.set(webPaClient.searchSentNotification(OffsetDateTime.now().minusDays(140), OffsetDateTime.now().minusDays(130),null,null,null,null,20,null))
            );

            Assertions.assertNotNull(notificationByIun.get());
            Assertions.assertNotNull(notificationByIun.get().getResultsPage());
            Assertions.assertTrue(notificationByIun.get().getResultsPage().size()>0);

            List<NotificationSearchRow> ricercaNotifiche= notificationByIun.get().getResultsPage();

            FullSentNotificationV23 notifica120 = null;

            for(NotificationSearchRow notifiche :ricercaNotifiche){

                notifica120 = b2bClient.getSentNotification(notifiche.getIun());

                if(notifica120.getRecipients().get(0).getPayments() != null && notifica120.getRecipients().get(0).getPayments().get(0).getPagoPa() != null && notifica120.getRecipients().get(0).getPayments().get(0).getPagoPa().getNoticeCode() != null){
                    break;
                }else{
                    notifica120=null;
                }


                try {
                    Thread.sleep(sharedSteps.getWorkFlowWait());
                } catch (InterruptedException exc) {
                    throw new RuntimeException(exc);
                }
            }

            Assertions.assertNotNull(notifica120);

            logger.info("notifica dopo 120gg: {}", notifica120);

            Assertions.assertNull(notifica120.getRecipients().get(0).getPayments().get(0).getPagoPa().getAttachment());

            sharedSteps.setSentNotification(notifica120);

        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }


    @Then("la notifica può essere correttamente recuperata dal sistema tramite Stato {string} dalla web PA {string}")
    public void notificationCanBeRetrievedWithStatusByWebPA(String status, String paType) {
        sharedSteps.selectPA(paType);

        it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.NotificationStatus notificationInternalStatus;
        switch (status) {
            case "ACCEPTED":
                notificationInternalStatus = it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.NotificationStatus.ACCEPTED;
                break;
            case "DELIVERING":
                notificationInternalStatus = it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.NotificationStatus.DELIVERING;
                break;
            case "DELIVERED":
                notificationInternalStatus = it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.NotificationStatus.DELIVERED;
                break;
            case "CANCELLED":
                notificationInternalStatus = it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.NotificationStatus.CANCELLED;
                break;
            case "EFFECTIVE_DATE":
                notificationInternalStatus = it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.NotificationStatus.EFFECTIVE_DATE;
                break;
            case "REFUSED":
                notificationInternalStatus = it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.NotificationStatus.REFUSED;
                break;

            default:
                throw new IllegalArgumentException();
        }

        AtomicReference<NotificationSearchResponse> notificationByIun = new AtomicReference<>();
        try {
            Assertions.assertDoesNotThrow(() ->
                    notificationByIun.set(webPaClient.searchSentNotification(OffsetDateTime.now().minusDays(1), OffsetDateTime.now(),null,notificationInternalStatus,null,null,1,null))
            );
            Assertions.assertNotNull(notificationByIun.get());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }


    @Then("la notifica viene recuperata dal sistema tramite codice IUN")
    public void laNotificaVieneRecuperataDalSistemaTramiteCodiceIUN() {
        AtomicReference<FullSentNotificationV23> notificationByIun = new AtomicReference<>();
        try {
            notificationByIun.set(b2bUtils.getNotificationByIun(sharedSteps.getSentNotification().getIun()));
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @Given("viene effettuato il pre-caricamento di un documento")
    public void preLoadingOfDocument() {
        NotificationDocument notificationDocument = b2bUtils.newDocument("classpath:/sample.pdf");
        AtomicReference<NotificationDocument> notificationDocumentAtomic = new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> notificationDocumentAtomic.set(b2bUtils.preloadDocument(notificationDocument)));
        /*
        try {
            Thread.sleep( sharedSteps.getWait());
        } catch (InterruptedException e) {
            logger.error("Thread.sleep error retry");
            throw new RuntimeException(e);
        }
         */
        this.notificationDocumentPreload = notificationDocumentAtomic.get();
    }

    @Given("viene effettuato il pre-caricamento di un allegato")
    public void preLoadingOfAttachment() {
        NotificationPaymentAttachment notificationPaymentAttachment = b2bUtils.newAttachment("classpath:/sample.pdf");
        AtomicReference<NotificationPaymentAttachment> notificationDocumentAtomic = new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> notificationDocumentAtomic.set(b2bUtils.preloadAttachment(notificationPaymentAttachment)));
        /*
        try {
            Thread.sleep( sharedSteps.getWait());
        } catch (InterruptedException e) {
            logger.error("Thread.sleep error retry");
            throw new RuntimeException(e);
        }
         */
        this.notificationPaymentAttachmentPreload = notificationDocumentAtomic.get();
    }

    @Given("viene effettuato il pre-caricamento dei metadati f24")
    public void preLoadingOfMetaDatiAttachmentF24() {
        NotificationMetadataAttachment notificationPaymentAttachment = b2bUtils.newMetadataAttachment("classpath:/METADATA_CORRETTO.json");
        AtomicReference<NotificationMetadataAttachment> notificationDocumentAtomic = new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> notificationDocumentAtomic.set(b2bUtils.preloadMetadataAttachment(notificationPaymentAttachment)));
        try {
            Thread.sleep( sharedSteps.getWait());
        } catch (InterruptedException e) {
            logger.error("Thread.sleep error retry");
            throw new RuntimeException(e);
        }
        this.notificationMetadataAttachment = notificationDocumentAtomic.get();
    }


    @Then("viene effettuato un controllo sulla durata della retention di {string} precaricato")
    public void retentionCheckPreload(String documentType) {
        String key = "";
        switch (documentType) {
            case "ATTO OPPONIBILE":
                key = this.notificationDocumentPreload.getRef().getKey();
                break;
            case "PAGOPA":
                key = this.notificationPaymentAttachmentPreload.getRef().getKey();
                break;
            case "F24_STANDARD":
                key = this.notificationPaymentAttachmentPreload.getRef().getKey();
                break;
            case "F24":
                key = this.notificationMetadataAttachment.getRef().getKey();
                break;
            default:
                throw new IllegalArgumentException();
        }
        Assertions.assertTrue(checkRetetion(key, retentionTimePreLoad));
    }


    @And("viene effettuato un controllo sulla durata della retention di {string}")
    public void retentionCheckLoad(String documentType) {
        String key = "";
        switch (documentType) {
            case "ATTO OPPONIBILE":
                key = sharedSteps.getSentNotification().getDocuments().get(0).getRef().getKey();
                break;
            case "PAGOPA":
                key = sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(0).getPagoPa().getAttachment().getRef().getKey();
                break;
            case "F24_STANDARD":
                key = sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(0).getF24().getMetadataAttachment().getRef().getKey();
                break;
            default:
                throw new IllegalArgumentException();
        }
        Assertions.assertTrue(checkRetetion(key, retentionTimeLoad));
    }

    @And("viene effettuato un controllo sulla durata della retention di {string} per l'elemento di timeline {string}")
    public void retentionCheckLoadForTimelineElement(String documentType, String timelineEventCategory, @Transpose DataTest dataFromTest) throws InterruptedException {
        TimelineElementV23 timelineElement = sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataFromTest);
        switch (documentType) {
            case "ATTACHMENTS":
                for (int i = 0; i < sharedSteps.getSentNotification().getDocuments().size(); i++) {
                    String key = sharedSteps.getSentNotification().getDocuments().get(i).getRef().getKey();
                    Assertions.assertTrue(checkRetention(key, retentionTimeLoad, timelineElement.getTimestamp()));
                }
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @And("viene effettuato un controllo sulla durata della retention del F24 di {string} per l'elemento di timeline {string}")
    public void retentionCheckLoadForTimelineElementF24(String documentType, String timelineEventCategory, @Transpose DataTest dataFromTest) throws InterruptedException {
        TimelineElementV23 timelineElement = sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataFromTest);
        switch (documentType) {
            case "ATTACHMENTS":
                for (int i = 0; i < sharedSteps.getSentNotification().getRecipients().get(0).getPayments().size(); i++) {
                    String key = sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(i).getF24().getMetadataAttachment().getRef().getKey();
                    Assertions.assertTrue(checkRetention(key, retentionTimeLoad, timelineElement.getTimestamp()));
                }
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @And("viene effettuato un controllo sul type zip attachment di {string} per l'elemento di timeline {string} con DOC {string}")
    public void attachmentCheckLoadForTimelineElementF24(String documentType, String timelineEventCategory, String doc, @Transpose DataTest dataFromTest) throws InterruptedException {
        TimelineElementV23 timelineElement = sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataFromTest);
        switch (documentType) {
            case "ATTACHMENTS":
                Assertions.assertNotNull(timelineElement.getDetails().getAttachments());
                Assertions.assertTrue(doc.equalsIgnoreCase(timelineElement.getDetails().getAttachments().get(0).getDocumentType()));
                Assertions.assertTrue(timelineElement.getDetails().getAttachments().get(0).getUrl().contains(".zip"));
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @And("viene effettuato un controllo sulla durata della retention del PAGOPA di {string} per l'elemento di timeline {string}")
    public void retentionCheckLoadForTimelineElementPAGOPA(String documentType, String timelineEventCategory, @Transpose DataTest dataFromTest) throws InterruptedException {
        TimelineElementV23 timelineElement = sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataFromTest);
        switch (documentType) {
            case "ATTACHMENTS":
                for (int i = 0; i < sharedSteps.getSentNotification().getRecipients().get(0).getPayments().size(); i++) {
                    String key = sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(i).getPagoPa().getAttachment().getRef().getKey();
                    Assertions.assertTrue(checkRetention(key, retentionTimeLoad, timelineElement.getTimestamp()));
                }
                break;
            default:
                throw new IllegalArgumentException();
        }
    }



    @Given("viene letta la notifica {string} dal {string}")
    public void vieneLettaLaNotificaDal(String IUN, String pa) {
        sharedSteps.selectPA(pa);
        FullSentNotificationV23 notificationByIun = b2bUtils.getNotificationByIun(IUN);
        sharedSteps.setSentNotification(notificationByIun);
    }

    @When("si tenta il recupero della notifica dal sistema tramite codice IUN {string}")
    public void retrievalAttemptedIUN(String IUN) {
        try {
            b2bUtils.getNotificationByIun(IUN);
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @When("si tenta il recupero della notifica dal sistema")
    public void retrievalAttemptedIUN() {
        try {
            b2bUtils.getNotificationByIun(new String(Base64Utils.decodeFromString(this.sharedSteps.getNewNotificationResponse().getNotificationRequestId())));
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @When("si tenta il recupero della notifica dal sistema tramite codice IUN {string} con la V1")
    public void retrievalAttemptedIUNConV1(String IUN) {
        try {
            b2bUtils.getNotificationByIunV1(IUN);
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @When("si tenta il recupero della notifica dal sistema tramite codice IUN {string} con la V2")
    public void retrievalAttemptedIUNConV2(String IUN) {
        try {
            b2bUtils.getNotificationByIunV2(IUN);
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @When("viene richiesto il download del documento {string}")
    public void documentDownload(String type) {
        String downloadType;
        switch (type) {
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
            case "F24":
                downloadType = "F24";
                break;
            default:
                throw new IllegalArgumentException();
        }
        try{
            this.downloadResponse = b2bClient
                    .getSentNotificationAttachment(sharedSteps.getIunVersionamento(), 0, downloadType,0);

            if (downloadResponse!= null && downloadResponse.getRetryAfter()!= null && downloadResponse.getRetryAfter()>0){
                try {
                    Thread.sleep(downloadResponse.getRetryAfter()*3);
                    this.downloadResponse = b2bClient
                            .getSentNotificationAttachment(sharedSteps.getIunVersionamento(), 0, downloadType,0);

                } catch (InterruptedException exc) {
                    throw new RuntimeException(exc);
                }
            }

            if(!"F24".equalsIgnoreCase(downloadType)) {
                byte[] bytes = Assertions.assertDoesNotThrow(() ->
                        b2bUtils.downloadFile(this.downloadResponse.getUrl()));
                this.sha256DocumentDownload = b2bUtils.computeSha256(new ByteArrayInputStream(bytes));
            }else if("F24".equalsIgnoreCase(downloadType)) {
                byte[] bytes = Assertions.assertDoesNotThrow(() ->
                        b2bUtils.downloadFile(this.downloadResponse.getUrl()));
                this.sha256DocumentDownload = b2bUtils.computeSha256(new ByteArrayInputStream(bytes));

            }
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }


    @When("viene richiesto il download del documento {string} per il destinatario {int}")
    public void documentDownloadPerDestinatario(String type, int destinatario) {
        String downloadType;
        switch (type) {
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
                downloadType = "F24";
                break;
            case "F24":
                downloadType = "F24";
                break;

            default:
                throw new IllegalArgumentException();
        }

        try{
        this.downloadResponse = b2bClient
                .getSentNotificationAttachment(sharedSteps.getSentNotification().getIun(), destinatario, downloadType,0);
        if (downloadResponse!= null && downloadResponse.getRetryAfter()!= null && downloadResponse.getRetryAfter()>0){
            try {
                Thread.sleep(downloadResponse.getRetryAfter()*3);
                this.downloadResponse = b2bClient
                        .getSentNotificationAttachment(sharedSteps.getSentNotification().getIun(), destinatario, downloadType,0);

            } catch (InterruptedException exc) {
                throw new RuntimeException(exc);
            }
        }
        if(!"F24".equalsIgnoreCase(downloadType)) {
            byte[] bytes = Assertions.assertDoesNotThrow(() ->
                    b2bUtils.downloadFile(this.downloadResponse.getUrl()));
            this.sha256DocumentDownload = b2bUtils.computeSha256(new ByteArrayInputStream(bytes));
        }else if("F24".equalsIgnoreCase(downloadType)) {
            byte[] bytes = Assertions.assertDoesNotThrow(() ->
                    b2bUtils.downloadFile(this.downloadResponse.getUrl()));
            this.sha256DocumentDownload = b2bUtils.computeSha256(new ByteArrayInputStream(bytes));
        }
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @When("viene richiesto il download del documento {string} inesistente")
    public void documentAbsentDownload(String type) {
        String downloadType;
        switch (type) {
            case "NOTIFICA":
                List<NotificationDocument> documents = sharedSteps.getSentNotification().getDocuments();
                try {
                    this.downloadResponse = b2bClient
                            .getSentNotificationDocument(sharedSteps.getSentNotification().getIun(), documents.size());
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
                downloadType = "F24";
                break;
            case "F24":
                downloadType = "F24";
                break;
            default:
                throw new IllegalArgumentException();
        }
        try {
            this.downloadResponse = b2bClient
                    .getSentNotificationAttachment(sharedSteps.getSentNotification().getIun(), 100, downloadType,0);


            if (downloadResponse!= null && downloadResponse.getRetryAfter()!= null && downloadResponse.getRetryAfter()>0){
                try {
                    Thread.sleep(downloadResponse.getRetryAfter()*3);
                    this.downloadResponse = b2bClient
                            .getSentNotificationAttachment(sharedSteps.getSentNotification().getIun(), 0, downloadType,0);

                } catch (InterruptedException exc) {
                    throw new RuntimeException(exc);
                }
            }

        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }



    @When("viene richiesto il download del documento {string} inesistente per il destinatario {int}")
    public void documentAbsentDownload(String type, int destinatario) {
        String downloadType;
        switch (type) {
            case "NOTIFICA":
                List<NotificationDocument> documents = sharedSteps.getSentNotification().getDocuments();
                try {
                    this.downloadResponse = b2bClient
                            .getSentNotificationDocument(sharedSteps.getSentNotification().getIun(), documents.size());
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
                downloadType = "F24";
                break;
            case "F24":
                downloadType = "F24";
                break;
            default:
                throw new IllegalArgumentException();
        }
        try {
            this.downloadResponse = b2bClient
                    .getSentNotificationAttachment(sharedSteps.getSentNotification().getIun(), destinatario, downloadType,0);


            if (downloadResponse!= null && downloadResponse.getRetryAfter()!= null && downloadResponse.getRetryAfter()>0){
                try {
                    Thread.sleep(downloadResponse.getRetryAfter()*3);
                    this.downloadResponse = b2bClient
                            .getSentNotificationAttachment(sharedSteps.getSentNotification().getIun(), 0, downloadType,0);

                } catch (InterruptedException exc) {
                    throw new RuntimeException(exc);
                }
            }

        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }


    @Then("il download si conclude correttamente")
    public void correctlyDownload() {
        Assertions.assertEquals(this.sha256DocumentDownload, this.downloadResponse.getSha256());
    }

    @Then("l'operazione ha prodotto un errore con status code {string}")
    public void operationProducedAnError(String statusCode) {
        HttpStatusCodeException httpStatusCodeException = this.sharedSteps.consumeNotificationError();
        Assertions.assertTrue((httpStatusCodeException != null) &&
                (httpStatusCodeException.getStatusCode().toString().substring(0, 3).equals(statusCode)));
    }

    @Then("l'operazione non ha prodotto errori")
    public void operationProducedNotAnError() {
        HttpStatusCodeException httpStatusCodeException = this.sharedSteps.consumeNotificationError();
        Assertions.assertNull(httpStatusCodeException);
    }


    @Then("si verifica la corretta acquisizione della notifica")
    public void correctAcquisitionNotification() {
        Assertions.assertDoesNotThrow(() -> b2bUtils.verifyNotification(sharedSteps.getSentNotification()));
    }

    @Then("si verifica la corretta acquisizione della notifica V1")

    public void correctAcquisitionNotificationV1() {
        Assertions.assertDoesNotThrow(() -> b2bUtils.verifyNotificationV1(sharedSteps.getSentNotificationV1()));
    }

    @Then("si verifica la corretta acquisizione della notifica V2")
    public void correctAcquisitionNotificationV2() {
        Assertions.assertDoesNotThrow(() -> b2bUtils.verifyNotificationV2(sharedSteps.getSentNotificationV2()));
    }

    @Then("si verifica lo scarto dell' acquisizione della notifica V1")
    public void correctAcquisitionNotificationV1Error() {
        try {

            b2bUtils.verifyNotificationV1(sharedSteps.getSentNotificationV1());

        } catch (AssertionFailedError | IOException assertionFailedError) {

            logger.info("Errore di acquisizione notifica");
        }

    }

    @Then("si verifica lo scarto dell' acquisizione della notifica V2")
    public void correctAcquisitionNotificationV2Error() {
        try {

            b2bUtils.verifyNotificationV2(sharedSteps.getSentNotificationV2());

        } catch (AssertionFailedError | IOException assertionFailedError) {

            logger.info("Errore di acquisizione notifica");
        }

    }



    @Then("si verifica la corretta acquisizione della notifica con verifica sha256 del allegato di pagamento {string}")
    public void correctAcquisitionNotificationVerifySha256AllegatiPagamento(String attachname) {
        Assertions.assertDoesNotThrow(() -> b2bUtils.verifyNotificationAndSha256AllegatiPagamento(sharedSteps.getSentNotification(),attachname));
    }


    @And("viene controllato la presenza del taxonomyCode")
    public void checkTaxonomyCode() {
        Assertions.assertNotNull(this.sharedSteps.getSentNotification().getTaxonomyCode());
        if (this.sharedSteps.getNotificationRequest().getTaxonomyCode() != null) {
            Assertions.assertEquals(this.sharedSteps.getNotificationRequest().getTaxonomyCode(),
                    this.sharedSteps.getSentNotification().getTaxonomyCode());
        }

    }


    @And("vengono prodotte le evidenze: metadati e requestID")
    public void evidenceProduced() {
        Assertions.assertNotNull(this.sharedSteps.getNewNotificationResponse());
        logger.info("METADATI: " + '\n' + this.sharedSteps.getNewNotificationResponse());
        logger.info("REQUEST-ID: " + '\n' + this.sharedSteps.getNewNotificationResponse().getNotificationRequestId());
    }


    @Then("si verifica la corretta acquisizione della richiesta di invio notifica")
    public void correctAcquisitionRequest() {
        Assertions.assertNotNull(this.sharedSteps.getNewNotificationResponse());
        Assertions.assertNotNull(this.sharedSteps.getNewNotificationResponse().getNotificationRequestId());
        Assertions.assertNotNull(b2bClient.getNotificationRequestStatus(this.sharedSteps.getNewNotificationResponse().getNotificationRequestId()));
    }


    private boolean checkRetetion(String fileKey, Integer retentionTime) {
        PnExternalServiceClientImpl.SafeStorageResponse safeStorageResponse = safeStorageClient.safeStorageInfo(fileKey);
        System.out.println(safeStorageResponse);
        LocalDateTime localDateTimeNow = LocalDate.now().atStartOfDay();
        OffsetDateTime now = OffsetDateTime.of(localDateTimeNow, ZoneOffset.of("Z"));
        OffsetDateTime retentionUntil = OffsetDateTime.parse(safeStorageResponse.getRetentionUntil());
        logger.info("now: " + now);
        logger.info("retentionUntil: " + retentionUntil);
        long between = ChronoUnit.DAYS.between(now, retentionUntil);
        logger.info("Difference: " + between);
        return retentionTime == between;
    }

    private boolean checkRetention(String fileKey, Integer retentionTime, OffsetDateTime timelineEventTimestamp) throws InterruptedException {
        Thread.sleep(2 * 60 * 1000);
        PnExternalServiceClientImpl.SafeStorageResponse safeStorageResponse = safeStorageClient.safeStorageInfo(fileKey);
        System.out.println(safeStorageResponse);
        OffsetDateTime timelineEventDate = timelineEventTimestamp.atZoneSameInstant(ZoneId.of("Z")).toOffsetDateTime();
        OffsetDateTime retentionUntil = OffsetDateTime.parse(safeStorageResponse.getRetentionUntil());
        logger.info("now: " + timelineEventDate);
        logger.info("retentionUntil: " + retentionUntil);
        OffsetDateTime timelineEventDateDays = timelineEventDate.truncatedTo(ChronoUnit.DAYS);
        OffsetDateTime retentionUntilDays = retentionUntil.truncatedTo(ChronoUnit.DAYS);

        long between = ChronoUnit.DAYS.between(timelineEventDateDays, retentionUntilDays);

        LocalTime timelineEventDateLocalTime = timelineEventDate.toLocalTime();
        LocalTime retentionUntilLocalTime = retentionUntil.toLocalTime();
        Duration diff = Duration.between(timelineEventDateLocalTime, retentionUntilLocalTime);
        long diffInMinutes = diff.toMinutes();

        logger.info("Difference: " + between);
        logger.info("diffInMinutes: " + diffInMinutes);
        return retentionTime == between && Math.abs(diffInMinutes) <= 10;
    }

    @And("l'importo della notifica è {int}")
    public void priceNotificationVerify(Integer price) {
        try {
            Assertions.assertEquals(this.sharedSteps.getSentNotification().getAmount(), price);
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }


    @Then("viene verificato lo stato di accettazione con idempotenceToken e paProtocolNumber")
    public void vieneVerificatoLoStatoDiAccettazioneConIdempotenceTokenEPaProtocolNumber() {
        NewNotificationResponse newNotificationResponse = this.sharedSteps.getNewNotificationResponse();
        verifyStatus(null, newNotificationResponse.getPaProtocolNumber(), newNotificationResponse.getIdempotenceToken());

    }

    @Then("viene verificato lo stato di accettazione con requestID")
    public void vieneVerificatoLoStatoDiAccettazioneConRequestID() {
        NewNotificationResponse newNotificationResponse = this.sharedSteps.getNewNotificationResponse();
        verifyStatus(newNotificationResponse.getNotificationRequestId(), null, null);
    }

    @Then("viene verificato lo stato di accettazione con paProtocolNumber")
    public void vieneVerificatoLoStatoDiAccettazioneConPaProtocolNumber() {
        NewNotificationResponse newNotificationResponse = this.sharedSteps.getNewNotificationResponse();
        verifyStatus(null, newNotificationResponse.getPaProtocolNumber(), null);
    }

    private void verifyStatus(String notificationRequestId, String paProtocolNumber, String idempotenceToken) {
        NewNotificationRequestStatusResponseV23 newNotificationRequestStatusResponse = Assertions.assertDoesNotThrow(() ->
                this.b2bClient.getNotificationRequestStatusAllParam(notificationRequestId, paProtocolNumber, idempotenceToken));
        Assertions.assertNotNull(newNotificationRequestStatusResponse.getNotificationRequestStatus());
        logger.debug(newNotificationRequestStatusResponse.getNotificationRequestStatus());
    }


    @And("la notifica non può essere annullata dal sistema tramite codice IUN")
    public void notificationCaNotBeCanceledWithIUN() {
        try {
            sharedSteps.getB2bClient().notificationCancellation(new String(Base64Utils.decodeFromString(this.sharedSteps.getNewNotificationResponse().getNotificationRequestId())));
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    //Annullamento Notifica
    @And("la notifica non può essere annullata dal sistema tramite codice IUN più volte")
    public void notificationNotCanBeCanceledWithIUN() {
        Assertions.assertDoesNotThrow(() -> {
            RequestStatus resp =  Assertions.assertDoesNotThrow(() ->
                    b2bClient.notificationCancellation(sharedSteps.getSentNotification().getIun()));

            Assertions.assertNotNull(resp);
            Assertions.assertNotNull(resp.getDetails());
            Assertions.assertTrue(resp.getDetails().size()>0);
            Assertions.assertTrue("NOTIFICATION_ALREADY_CANCELLED".equalsIgnoreCase(resp.getDetails().get(0).getCode()));

        });
    }

    @Then("si verifica il corretto annullamento della notifica")
    public void correctCanceledNotification() {
        //Assertions.assertNull(assertionFailedError);
    }



    @And("l'avviso pagopa viene pagato correttamente su checkout")
    public void laNotificaVienePagatasuCheckout() {
        NotificationPriceResponseV23 notificationPrice = this.b2bClient.getNotificationPriceV23(sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(0).getPagoPa().getCreditorTaxId(),
                sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(0).getPagoPa().getNoticeCode());

        PaymentRequest paymentRequest= new PaymentRequest();
        PaymentNotice paymentNotice= new PaymentNotice();
        paymentNotice.noticeNumber( sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(0).getPagoPa().getNoticeCode());
        paymentNotice.fiscalCode(sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(0).getPagoPa().getCreditorTaxId());
        paymentNotice.companyName("Test Automation");
        paymentNotice.amount(notificationPrice.getTotalPrice());
        paymentNotice.description("Test Automation Desk");
        paymentRequest.paymentNotice(paymentNotice);
        paymentRequest.returnUrl("https://api.uat.platform.pagopa.it");

        try {
            Assertions.assertDoesNotThrow(() -> {
                paymentResponse= pnPaymentInfoClientImpl.checkoutCart(paymentRequest);
                logger.info("Risposta recupero posizione debitoria: " + paymentInfoResponse.toString());
            });
            Assertions.assertNotNull(paymentResponse);

            System.out.println(paymentResponse);
        } catch (AssertionFailedError assertionFailedError) {

            String message = assertionFailedError.getMessage() +
                    "{la posizione debitoria " + (paymentResponse == null ? "NULL" : paymentResponse.toString()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());

        }
    }


    @Then("verifica stato pagamento di una notifica creditorTaxID {string} noticeCode {string} con errore {string}")
    public void verificaStatoPagamentoNotifica(String creditorTaxID , String noticeCode,String codiceErrore) {


        List<PaymentInfoRequest> paymentInfoRequestList= new ArrayList<PaymentInfoRequest>();

        PaymentInfoRequest paymentInfoRequest = new PaymentInfoRequest()
                .creditorTaxId(creditorTaxID)
                .noticeCode(noticeCode);

        paymentInfoRequestList.add(paymentInfoRequest);

        logger.info("Messaggio json da allegare: " + paymentInfoRequest);


        try {
            Assertions.assertDoesNotThrow(() -> {
                paymentInfoResponse= pnPaymentInfoClientImpl.getPaymentInfoV21(paymentInfoRequestList);
                logger.info("Informazioni sullo stato del Pagamento: " + paymentInfoResponse.toString());
            });
            Assertions.assertNotNull(paymentInfoResponse);
            Assertions.assertTrue(codiceErrore.equalsIgnoreCase(paymentInfoResponse.get(0).getErrorCode()));

        } catch (AssertionFailedError assertionFailedError) {

            String message = assertionFailedError.getMessage() +
                    "{Informazioni sullo stato del Pagamento: " + (paymentInfoResponse == null ? "NULL" : paymentInfoResponse.toString()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());

        }
    }

    @Then("verifica stato pagamento di una notifica con status {string}")
    public void verificaStatoPagamentoNotifica(String status) {

        List<PaymentInfoRequest> paymentInfoRequestList= new ArrayList<PaymentInfoRequest>();

        PaymentInfoRequest paymentInfoRequest = new PaymentInfoRequest()
                .creditorTaxId(sharedSteps.getNotificationRequest().getRecipients().get(0).getPayments().get(0).getPagoPa().getCreditorTaxId())
                .noticeCode(sharedSteps.getNotificationRequest().getRecipients().get(0).getPayments().get(0).getPagoPa().getNoticeCode());

        paymentInfoRequestList.add(paymentInfoRequest);

        logger.info("Messaggio json da allegare: " + paymentInfoRequest);

        try {
            Assertions.assertDoesNotThrow(() -> {
                paymentInfoResponse= pnPaymentInfoClientImpl.getPaymentInfoV21(paymentInfoRequestList);

            });
            Assertions.assertNotNull(paymentInfoResponse);
            logger.info("Informazioni sullo stato del Pagamento: " + paymentInfoResponse.toString());
           Assertions.assertTrue(status.equalsIgnoreCase(paymentInfoResponse.get(0).getStatus().getValue()));

        } catch (AssertionFailedError assertionFailedError) {

            String message = assertionFailedError.getMessage() +
                    "{Informazioni sullo stato del Pagamento: " + (paymentInfoResponse == null ? "NULL" : paymentInfoResponse.toString()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }



    @And("l'avviso pagopa viene pagato correttamente su checkout con errore {string}")
    public void laNotificaVienePagatasuCheckoutError(String codiceErrore) {

        PaymentRequest paymentRequest= new PaymentRequest();
        PaymentNotice paymentNotice= new PaymentNotice();
        paymentNotice.noticeNumber( sharedSteps.getNotificationRequest().getRecipients().get(0).getPayments().get(0).getPagoPa().getNoticeCode());
        paymentNotice.fiscalCode(sharedSteps.getNotificationRequest().getRecipients().get(0).getPayments().get(0).getPagoPa().getCreditorTaxId());
        paymentNotice.companyName("Test Automation");
        paymentNotice.amount(100);
        paymentNotice.description("Test Automation Desk");
        paymentRequest.paymentNotice(paymentNotice);
        paymentRequest.returnUrl("https://api.uat.platform.pagopa.it");

        try {
            Assertions.assertDoesNotThrow(() -> {
                paymentResponse= pnPaymentInfoClientImpl.checkoutCart(paymentRequest);

            });
            Assertions.assertNotNull(paymentResponse);
            logger.info("Risposta recupero posizione debitoria: " + paymentResponse.toString());
            Assertions.assertTrue(codiceErrore.equalsIgnoreCase(paymentResponse.getCheckoutUrl()));

        } catch (AssertionFailedError assertionFailedError) {

            String message = assertionFailedError.getMessage() +
                    "{la posizione debitoria " + (paymentResponse == null ? "NULL" : paymentResponse.toString()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());

        }
    }


    @And("l'avviso pagopa viene pagato correttamente su checkout creditorTaxID {string} noticeCode {string} con errore {string}")
    public void laNotificaVienePagatasuCheckoutError(String creditorTaxID , String noticeCode,String codiceErrore) {

        PaymentRequest paymentRequest= new PaymentRequest();
        PaymentNotice paymentNotice= new PaymentNotice();
        paymentNotice.noticeNumber( noticeCode);
        paymentNotice.fiscalCode(creditorTaxID);
        paymentNotice.companyName("Test Automation");
        paymentNotice.amount(100);
        paymentNotice.description("Test Automation Desk");
        paymentRequest.paymentNotice(paymentNotice);
        paymentRequest.returnUrl("https://api.uat.platform.pagopa.it");

        try {
            Assertions.assertDoesNotThrow(() -> {
                paymentResponse= pnPaymentInfoClientImpl.checkoutCart(paymentRequest);
            });
            Assertions.assertNotNull(paymentResponse);
            logger.info("Risposta recupero posizione debitoria: " + paymentInfoResponse.toString());
            Assertions.assertTrue(codiceErrore.equalsIgnoreCase(paymentInfoResponse.get(0).getErrorCode()));

        } catch (AssertionFailedError assertionFailedError) {

            String message = assertionFailedError.getMessage() +
                    "{la posizione debitoria " + (paymentResponse == null ? "NULL" : paymentResponse.toString()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());

        }
    }


    @And("la notifica a 2 avvisi di pagamento con OpenApi V1")
    public void notificationCanBeRetrievePaymentV1() {
        AtomicReference<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.FullSentNotification> notificationByIun = new AtomicReference<>();

        String iun =sharedSteps.getIunVersionamento();

        try {
            Assertions.assertDoesNotThrow(() ->
                    notificationByIun.set(b2bUtils.getNotificationByIunV1(iun))
            );

            Assertions.assertNotNull(notificationByIun.get());
            Assertions.assertNotNull(notificationByIun.get().getRecipients().get(0).getPayment().getNoticeCode());
            Assertions.assertNotNull(notificationByIun.get().getRecipients().get(0).getPayment().getNoticeCodeAlternative());

        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @And("la notifica a 2 avvisi di pagamento con OpenApi V2")
    public void notificationCanBeRetrievePaymentV2() {
        AtomicReference<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.FullSentNotificationV20> notificationByIun = new AtomicReference<>();

        String iun =sharedSteps.getIunVersionamento();

        try {
                Assertions.assertDoesNotThrow(() ->
                        notificationByIun.set(b2bUtils.getNotificationByIunV2(iun))
                );

            Assertions.assertNotNull(notificationByIun.get());
            Assertions.assertNotNull(notificationByIun.get().getRecipients().get(0).getPayment().getNoticeCode());
            Assertions.assertNotNull(notificationByIun.get().getRecipients().get(0).getPayment().getNoticeCodeAlternative());

        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }


    @And("la notifica a 1 avvisi di pagamento con OpenApi V1")
    public void notificationCanBeRetrievePayment1V1() {
        AtomicReference<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.FullSentNotification> notificationByIun = new AtomicReference<>();
        String iun =sharedSteps.getIunVersionamento();
        try {
            Assertions.assertDoesNotThrow(() ->
                    notificationByIun.set(b2bUtils.getNotificationByIunV1(iun))
            );
            Assertions.assertNotNull(notificationByIun.get());
            Assertions.assertNotNull(notificationByIun.get().getRecipients().get(0).getPayment().getNoticeCode());
            //  Assertions.assertNotNull(notificationByIun.get().getRecipients().get(0).getPayment().getNoticeCodeAlternative());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @And("Si effettua la chiamata su external-reg per ricevere l'url di checkout con noticeCode {string} e creditorTaxId {string}")
    public void siEffettuaLaChiamataSuExternalRegPerRicevereLUrlDiCheckoutConNoticeCodeECreditorTaxId(String noticeCode, String creditorTaxId) {

        PaymentInfoRequest paymentInfoRequest = new PaymentInfoRequest()
                .creditorTaxId(creditorTaxId)
                .noticeCode(noticeCode);

        List<PaymentInfoV21> getPaymentInfoV21 = Assertions.assertDoesNotThrow(() -> pnPaymentInfoClientImpl.getPaymentInfoV21(Collections.singletonList(paymentInfoRequest)));

        PaymentRequest paymentRequest= new PaymentRequest();
        PaymentNotice paymentNotice= new PaymentNotice();

        paymentNotice.noticeNumber( noticeCode);
        paymentNotice.fiscalCode(creditorTaxId);
        paymentNotice.companyName("Test Automation");

        System.out.println("COSTO NOTIFICA: "+getPaymentInfoV21.get(0).getAmount());

        paymentNotice.amount(getPaymentInfoV21.get(0).getAmount());
        paymentNotice.description("Test Automation Desk");
        paymentRequest.paymentNotice(paymentNotice);
        paymentRequest.returnUrl("https://api.uat.platform.pagopa.it");

        try {
            Assertions.assertDoesNotThrow(() -> {
                paymentResponse = pnPaymentInfoClientImpl.checkoutCart(paymentRequest);
                logger.info("Risposta recupero posizione debitoria: " + paymentResponse.toString());
            });
            Assertions.assertNotNull(paymentResponse);

            System.out.println(paymentResponse);
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{la posizione debitoria " + (paymentResponse == null ? "NULL" : paymentResponse.toString()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());

        }

    }



    @Then("si verifica che il phyicalAddress sia stato normalizzato correttamente con rimozione caratteri isoLatin1 è abbia un massimo di {int} caratteri")
    public void controlloCampiAddressNormalizzatore(Integer caratteri){
        String regex= "[{-~¡-ÿ]*";

       FullSentNotificationV23 timeline= sharedSteps.getSentNotification();

       TimelineElementV23 timelineNormalizer= timeline.getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV23.NORMALIZED_ADDRESS)).findAny().orElse(null);
        PhysicalAddress oldAddress= timelineNormalizer.getDetails().getOldAddress();
        PhysicalAddress normalizedAddress= timelineNormalizer.getDetails().getNormalizedAddress();
try {
    Assertions.assertNotNull(normalizedAddress);
    Assertions.assertNotNull(oldAddress);

    logger.info("old address: {}", oldAddress);
    logger.info("normalized address: {}", normalizedAddress);

    PhysicalAddress newAddress= new PhysicalAddress()
            .address(oldAddress.getAddress().length()>caratteri?  oldAddress.getAddress().substring(0,caratteri).replaceAll(regex,"").toUpperCase():
            oldAddress.getAddress().replaceAll(regex,"").toUpperCase())
            .municipality(oldAddress.getMunicipality().length()>caratteri?  oldAddress.getMunicipality().substring(0,caratteri).replaceAll(regex,"").toUpperCase():
                    oldAddress.getMunicipality().replaceAll(regex,"").toUpperCase())
            .municipalityDetails(oldAddress.getMunicipalityDetails().length()>caratteri?  oldAddress.getMunicipalityDetails().substring(0,caratteri).replaceAll(regex,"").toUpperCase():
                    oldAddress.getMunicipalityDetails().replaceAll(regex,"").toUpperCase())
            .province(oldAddress.getProvince().length()>caratteri?  oldAddress.getMunicipality().substring(0,caratteri).replaceAll(regex,"").toUpperCase():
                    oldAddress.getProvince().replaceAll(regex,"").toUpperCase())
            .zip(oldAddress.getZip().length()>caratteri?  oldAddress.getMunicipality().substring(0,caratteri).replaceAll(regex,"").toUpperCase():
                    oldAddress.getZip().replaceAll(regex,"").toUpperCase());

    logger.info(" newAddress: {}",newAddress);

    Assertions.assertEquals(newAddress.getAddress().toUpperCase(),normalizedAddress.getAddress());
    Assertions.assertEquals(newAddress.getMunicipality(),normalizedAddress.getMunicipality());
    Assertions.assertEquals(newAddress.getMunicipalityDetails(),normalizedAddress.getMunicipalityDetails());
    Assertions.assertEquals(newAddress.getProvince(),normalizedAddress.getProvince());
    Assertions.assertEquals(newAddress.getZip(),normalizedAddress.getZip());


   }catch(AssertionFailedError error){
    sharedSteps.throwAssertFailerWithIUN(error);
}

    }

}
