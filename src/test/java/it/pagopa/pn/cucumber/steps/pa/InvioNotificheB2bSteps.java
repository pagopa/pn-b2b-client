package it.pagopa.pn.cucumber.steps.pa;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.impl.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.testclient.IPnWebPaClient;
import it.pagopa.pn.client.b2b.pa.testclient.PnExternalServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.testclient.PnGPDClientImpl;
import it.pagopa.pn.client.b2b.pa.testclient.PnPaymentInfoClientImpl;
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
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.text.SimpleDateFormat;
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

    @Value("${pn.external.costo_base_notifica}")
    private Integer costoBaseNotifica;

    private final PnPaB2bUtils b2bUtils;
    private final IPnWebPaClient webPaClient;
    private final IPnPaB2bClient b2bClient;
    private final PnExternalServiceClientImpl safeStorageClient;
    private final SharedSteps sharedSteps;
    private final PnGPDClientImpl pnGPDClientImpl;

    private final PnPaymentInfoClientImpl pnPaymentInfoClient;
    private List<PaymentPositionModel> paymentPositionModel;

    private PaymentResponse paymentResponse;
    private PaymentPositionModelBaseResponse paymentPositionModelBaseResponse;
    private List<PaymentInfoV21> paymentInfoResponse;
    private PaymentInfo paymentInfo;
    private String DeleteGDPresponse;
    private Integer amountGPD;
    private List<Integer> amountNotifica;
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
        this.pnGPDClientImpl = sharedSteps.getPnGPDClientImpl();
        this.pnPaymentInfoClient=sharedSteps.getPnPaymentInfoClientImpl();
        this.paymentPositionModel=new ArrayList<PaymentPositionModel>();
        this.amountNotifica=new ArrayList<Integer>();
    }


    @And("la notifica può essere correttamente recuperata dal sistema tramite codice IUN")
    public void notificationCanBeRetrievedWithIUN() {
        AtomicReference<FullSentNotificationV21> notificationByIun = new AtomicReference<>();
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

            FullSentNotificationV21 notifica120 = null;

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
        AtomicReference<FullSentNotificationV21> notificationByIun = new AtomicReference<>();
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
        TimelineElementV20 timelineElement = sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataFromTest);
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
        TimelineElementV20 timelineElement = sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataFromTest);
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

    @And("viene effettuato un controllo sulla durata della retention del PAGOPA di {string} per l'elemento di timeline {string}")
    public void retentionCheckLoadForTimelineElementPAGOPA(String documentType, String timelineEventCategory, @Transpose DataTest dataFromTest) throws InterruptedException {
        TimelineElementV20 timelineElement = sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataFromTest);
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
        FullSentNotificationV21 notificationByIun = b2bUtils.getNotificationByIun(IUN);
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
        OffsetDateTime timelineEventDateDays = timelineEventDate.truncatedTo(ChronoUnit.HOURS);
        OffsetDateTime retentionUntilDays = retentionUntil.truncatedTo(ChronoUnit.HOURS);
        Integer timelineEventDateMinutes = timelineEventDate.getMinute();
        Integer retentionUntilMinutes = retentionUntil.getMinute();
        long between = ChronoUnit.DAYS.between(timelineEventDateDays, retentionUntilDays);
        logger.info("Difference: " + between);
        return retentionTime == between && Math.abs(timelineEventDateMinutes - retentionUntilMinutes) <= 10;
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
        NewNotificationRequestStatusResponseV21 newNotificationRequestStatusResponse = Assertions.assertDoesNotThrow(() ->
                this.b2bClient.getNotificationRequestStatusAllParam(notificationRequestId, paProtocolNumber, idempotenceToken));
        Assertions.assertNotNull(newNotificationRequestStatusResponse.getNotificationRequestStatus());
        logger.debug(newNotificationRequestStatusResponse.getNotificationRequestStatus());
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

    private String generateRandomIuv(){
        int randomSleepToRandomize = new Random().nextInt(100);
        try {
            Thread.sleep(randomSleepToRandomize);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String threadNumber = (Thread.currentThread().getId()+"");
        String numberOfThread = threadNumber.length() < 2 ? "0"+threadNumber: threadNumber.substring(0, 2);
        String timeNano = System.nanoTime()+"";
        String iuv = String.format("47%13s44", (numberOfThread+timeNano).substring(0,13));
        logger.info("Iuv generato: " + iuv);
        return iuv;
    }

    @And("viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore {string} e amount {string} per {string} con (CF)(Piva) {string}")
    public void vieneCreataUnaPosizioneDebitoria(String organitationCode,String amount,String name,String taxId) {
        String iuv = generateRandomIuv();
        logger.info("IUPD generate: " + organitationCode +"-64c8e41bfec846e04"+ iuv, System.currentTimeMillis());
        sharedSteps.addIuvGPD(iuv);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        PaymentPositionModel paymentPositionModelSend = new PaymentPositionModel()
                .iupd(String.format(organitationCode+"-64c8e41bfec846e04"+  iuv, System.currentTimeMillis()))
                .type(PaymentPositionModel.TypeEnum.F)
                .companyName("Automation")
                .fullName(name)
                .fiscalCode(taxId)
                .addPaymentOptionItem(new PaymentOptionModel()
                        .iuv(iuv)
                        .amount(Long.parseLong(amount))
                        .description("Test Automation")
                        .isPartialPayment(false)
                        .dueDate(new StringBuilder(dateTimeFormatter.format(OffsetDateTime.now().plusDays(2))))
                        .retentionDate(new StringBuilder(dateTimeFormatter.format(OffsetDateTime.now().plusDays(2))))
                        .addTransferItem(new TransferModel()
                                .idTransfer(TransferModel.IdTransferEnum._1)
                                .organizationFiscalCode(organitationCode)
                                .amount(Long.parseLong(amount))
                                .remittanceInformation("Test Automation")
                                .category("9/0301100TS/")
                                .iban("IT30N0103076271000001823603")));

        logger.info("Request: " + paymentPositionModelSend.toString());
        amountNotifica.add(Integer.parseInt(amount));
        try {

            Assertions.assertDoesNotThrow(() -> {
                paymentPositionModel.add(pnGPDClientImpl.createPosition(organitationCode, paymentPositionModelSend, null, true));
            });

            Assertions.assertNotNull(paymentPositionModel);
            Assertions.assertNotNull(amountNotifica);
            logger.info("Request: " + paymentPositionModel);
        } catch (AssertionFailedError assertionFailedError) {

            String message = assertionFailedError.getMessage() +
                    "{la posizione debitoria " + (paymentPositionModel == null ? "NULL" : paymentPositionModel.toString()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());

        }
    }

    @And("lettura amount posizione debitoria per la notifica corrente di {string}")
    public void letturaAmountPosizioneDebitoria(String user) {
        PaymentPositionModel postionUser = new PaymentPositionModel();
        for(PaymentPositionModel position: paymentPositionModel){
            if(position.getFullName().equalsIgnoreCase(user)){
                postionUser=position;
            }
        }

        List<PaymentInfoRequest> paymentInfoRequestList= new ArrayList<PaymentInfoRequest>();

        PaymentInfoRequest paymentInfoRequest = new PaymentInfoRequest()
                .creditorTaxId(postionUser.getPaymentOption().get(0).getTransfer().get(0).getOrganizationFiscalCode())
                .noticeCode("3"+postionUser.getPaymentOption().get(0).getIuv());

        paymentInfoRequestList.add(paymentInfoRequest);

        logger.info("User: " + postionUser);
        logger.info("Messaggio json da allegare: " + paymentInfoRequest);
        for(int i=0; i< NUM_CHECK_PAYMENT_INFO ;i++) {
            try {
                Assertions.assertDoesNotThrow(() -> {
                    paymentInfoResponse = pnPaymentInfoClient.getPaymentInfoV21(paymentInfoRequestList);
                    logger.info("Risposta recupero posizione debitoria: " + paymentInfoResponse.toString());
                });
                Assertions.assertNotNull(paymentInfoResponse);
                if(amountGPD != paymentInfoResponse.get(0).getAmount()){
                    amountGPD = paymentInfoResponse.get(0).getAmount();
                    break;
                }
                try {
                    Thread.sleep(WAITING_PAYMENT_INFO);
                } catch (InterruptedException exc) {
                    throw new RuntimeException(exc);
                }
            } catch (AssertionFailedError assertionFailedError) {
                String message = assertionFailedError.getMessage() +
                        "{la posizione debitoria " + (paymentInfoResponse == null ? "NULL" : paymentInfoResponse.toString()) + " }";
                throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
            }
        }
    }

    @And("lettura amount posizione debitoria per pagamento {int}")
    public void letturaAmountPosizioneDebitoria(Integer pagamento) {

        PaymentPositionModel postionUser = paymentPositionModel.get(pagamento);


        List<PaymentInfoRequest> paymentInfoRequestList= new ArrayList<PaymentInfoRequest>();

        PaymentInfoRequest paymentInfoRequest = new PaymentInfoRequest()
                .creditorTaxId(postionUser.getPaymentOption().get(0).getTransfer().get(0).getOrganizationFiscalCode())
                .noticeCode("3"+postionUser.getPaymentOption().get(0).getIuv());

        paymentInfoRequestList.add(paymentInfoRequest);

        logger.info("User: " + postionUser);
        logger.info("Messaggio json da allegare: " + paymentInfoRequest);


        try {
            Assertions.assertDoesNotThrow(() -> {
                paymentInfoResponse=pnPaymentInfoClient.getPaymentInfoV21(paymentInfoRequestList);
                logger.info("Risposta recupero posizione debitoria: " + paymentInfoResponse.toString());
            });
            Assertions.assertNotNull(paymentInfoResponse);

            amountGPD=paymentInfoResponse.get(0).getAmount();
            Assertions.assertNotNull(amountGPD);

        } catch (AssertionFailedError assertionFailedError) {

            String message = assertionFailedError.getMessage() +
                    "{la posizione debitoria " + (paymentInfoResponse == null ? "NULL" : paymentInfoResponse.toString()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());

        }
    }


    @And("viene cancellata la posizione debitoria di {string}")
    public void vieneCancellataLaPosizioneDebitoria(String user) {


        try {

            for(PaymentPositionModel position: paymentPositionModel){
                if(position.getFullName().equalsIgnoreCase(user)){
                    Assertions.assertDoesNotThrow(() -> {
                        DeleteGDPresponse = pnGPDClientImpl.deletePosition(position.getPaymentOption().get(0).getTransfer().get(0).getOrganizationFiscalCode(), position.getIupd(), null);
                    });
                }

            }

            Assertions.assertNotNull(DeleteGDPresponse);
            logger.info("Risposta evento cancellazione: " + DeleteGDPresponse);

        } catch (AssertionFailedError assertionFailedError) {

            String message = assertionFailedError.getMessage() +
                    "{la posizione debitoria " + (DeleteGDPresponse == null ? "NULL" : DeleteGDPresponse) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());

        }
    }


    @And("viene cancellata la posizione debitoria del pagamento {int}")
    public void vieneCancellataLaPosizioneDebitoriaDelPagamento(Integer pagamento) {


        try {

            Assertions.assertDoesNotThrow(() -> {
                DeleteGDPresponse = pnGPDClientImpl.deletePosition(paymentPositionModel.get(pagamento).getPaymentOption().get(0).getTransfer().get(0).getOrganizationFiscalCode(), paymentPositionModel.get(pagamento).getIupd(), null);
            });


            Assertions.assertNotNull(DeleteGDPresponse);
            logger.info("Risposta evento cancellazione: " + DeleteGDPresponse);

        } catch (AssertionFailedError assertionFailedError) {

            String message = assertionFailedError.getMessage() +
                    "{la posizione debitoria " + (DeleteGDPresponse == null ? "NULL" : DeleteGDPresponse) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());

        }
    }

    @And("vengono cancellate le posizioni debitorie")
    public void vengonoCancellateLaPosizioniDebitorie() {


        try {

            for(PaymentPositionModel paymentPositionModelUser :paymentPositionModel) {
                Assertions.assertDoesNotThrow(() -> {
                    DeleteGDPresponse = pnGPDClientImpl.deletePosition(paymentPositionModelUser.getPaymentOption().get(0).getTransfer().get(0).getOrganizationFiscalCode(), paymentPositionModelUser.getIupd(), null);
                });
            }

            Assertions.assertNotNull(DeleteGDPresponse);
            logger.info("Risposta evento cancellazione: " + DeleteGDPresponse);

        } catch (AssertionFailedError assertionFailedError) {

            String message = assertionFailedError.getMessage() +
                    "{la posizione debitoria " + (DeleteGDPresponse == null ? "NULL" : DeleteGDPresponse) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());

        }
    }



    @And("viene effettuato il controllo del amount di GPD = {string}")
    public void vieneEffettuatoIlControlloDelAmountDiGPD(String amount) {

        try {
            logger.info("Amount GPD: "+amountGPD);
            Assertions.assertEquals(amountGPD,Integer.parseInt(amount));

        } catch (AssertionFailedError assertionFailedError) {

            String message = assertionFailedError.getMessage() +
                    "{la posizione debitoria " + (paymentPositionModelBaseResponse == null ? "NULL" : paymentPositionModelBaseResponse.toString()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());

        }
    }


    @And("viene effettuato il controllo del amount di GPD con amount notifica del (utente)(pagamento) {int}")
    public void vieneEffettuatoIlControlloDelAmountDiGPDConAmountNotifica(Integer user) {

        try {

            Assertions.assertEquals(amountGPD,amountNotifica.get(user));

        } catch (AssertionFailedError assertionFailedError) {

            String message = assertionFailedError.getMessage() +
                    "{la posizione debitoria " + (paymentPositionModelBaseResponse == null ? "NULL" : paymentPositionModelBaseResponse.toString()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());

        }
    }

    @Then("viene effettuato il controllo del cambiamento del amount nella timeline {string} del (utente)(pagamento) {int}")
    public void vieneEffettuatoIlControlloDelCambiamentoDelAmount(String timelineEventCategory,Integer user) {

        TimelineElementV20 timelineElement = sharedSteps.getTimelineElementByEventId(timelineEventCategory,null);

        amountNotifica.set(user,amountNotifica.get(user) + timelineElement.getDetails().getAnalogCost());


        try {

            Assertions.assertEquals(amountGPD,amountNotifica.get(user));

        } catch (AssertionFailedError assertionFailedError) {

            String message = assertionFailedError.getMessage() +
                    "{la posizione debitoria " + (paymentPositionModelBaseResponse == null ? "NULL" : paymentPositionModelBaseResponse.toString()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());

        }
    }

    @Then("viene effettuato il controllo del cambiamento del amount nella timeline {string} del (utente)(pagamento) {int} (al tentativo):")
    public void vieneEffettuatoIlControlloDelCambiamentoDelAmountAlTentativo(String timelineEventCategory,Integer user,@Transpose DataTest dataFromTest ) {

        TimelineElementV20 timelineElement = sharedSteps.getTimelineElementByEventId(timelineEventCategory,dataFromTest);

        amountNotifica.set(user,amountNotifica.get(user) + timelineElement.getDetails().getAnalogCost());


        try {

            Assertions.assertEquals(amountGPD,amountNotifica.get(user));

        } catch (AssertionFailedError assertionFailedError) {

            String message = assertionFailedError.getMessage() +
                    "{la posizione debitoria " + (paymentPositionModelBaseResponse == null ? "NULL" : paymentPositionModelBaseResponse.toString()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());

        }
    }

    @Then("viene effettuato il controllo dell'aggiornamento del costo totale del utente {int}")
    public void vieneEffettuatoIlControlloDelCambiamentoDelCostoTotale(Integer user) {
        try {
            logger.info("Costo base presente su Notifica"+amountNotifica.get(user));
            logger.info("Costo base presente su GPD"+amountGPD);

            Assertions.assertEquals(amountGPD,amountNotifica.get(user));

        } catch (AssertionFailedError assertionFailedError) {

            String message = assertionFailedError.getMessage() +
                    "{la posizione debitoria " + (paymentPositionModelBaseResponse == null ? "NULL" : paymentPositionModelBaseResponse.toString()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());

        }
    }


    //dopo accettato amount_gpd + 100 (costo base) + pafee
    //Ogni elemento di timeline analogico ha un analog cost per ogni elemento va verificato che aumenti di  + analog_cost.
    //se riufiutata amount_gpd
    @Then("viene verificato il costo finale della notifica amount_gpd + costo_base + pafee + analog_cost per ogni elemento di timeline")
    public void vieneVerificatoIlCostoFinaleDellaNotificaAmount_gpdCosto_basePafeeAnalog_costPerOgniElementoDiTimeline() {
        FullSentNotificationV21 sentNotification = sharedSteps.getSentNotification();
        Integer analogCost = 0;
        for(TimelineElementV20 timelineElem: sentNotification.getTimeline()){
            Integer currentCost = timelineElem.getDetails() == null ? Integer.valueOf(0) : timelineElem.getDetails().getAnalogCost();
            if(currentCost!= null && currentCost > 0)analogCost+=currentCost;
        }
        Integer paFee = sentNotification.getPaFee();
        Integer costoBaseSend = this.costoBaseNotifica;
        Integer amountGpd = amountNotifica.get(0);

        Integer costoTotale = amountGpd + costoBaseSend + (paFee == null ? 0 : paFee) + analogCost;

        String creditorTaxId = Assertions.assertDoesNotThrow(()->sentNotification.getRecipients().get(0).getPayments().get(0).getPagoPa().getCreditorTaxId());
        String noticeCode = Assertions.assertDoesNotThrow(()->sentNotification.getRecipients().get(0).getPayments().get(0).getPagoPa().getNoticeCode());
        Assertions.assertNotNull(creditorTaxId);
        Assertions.assertNotNull(noticeCode);

        PaymentInfoRequest paymentInfoRequest = new PaymentInfoRequest()
                .creditorTaxId(creditorTaxId)
                .noticeCode(noticeCode);

        paymentInfoResponse = Assertions.assertDoesNotThrow(() -> pnPaymentInfoClient.getPaymentInfoV21(Collections.singletonList(paymentInfoRequest)));
        Assertions.assertNotNull(paymentInfoResponse);
        System.out.println("Costo totale previsto: {}"+costoTotale);
        System.out.println("Costo attuale su gpd: {}"+paymentInfoResponse.get(0).getAmount());
        Assertions.assertEquals(costoTotale,paymentInfoResponse.get(0).getAmount());
    }

    @And("viene aggiunto il costo della notifica totale del utente {int}")
    public void vieneAggiuntoIlCostoDellaNotificaTotaleAlUtente(Integer user) {

        try {

            for(int i=0;i<amountNotifica.size();i++) {
                Assertions.assertDoesNotThrow(() -> {
                    amountNotifica.set(user, sharedSteps.getSentNotification().getAmount() + sharedSteps.getSentNotification().getPaFee());
                });
            }
            Assertions.assertNotNull(amountNotifica.get(user));

        } catch (AssertionFailedError assertionFailedError) {

            String message = assertionFailedError.getMessage() +
                    "{la posizione debitoria " + (paymentPositionModelBaseResponse == null ? "NULL" : paymentPositionModelBaseResponse.toString()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());

        }
    }

    @And("viene aggiunto il costo della notifica totale")
    public void vieneAggiuntoIlCostoDellaNotificaTotale() {
        try {
            for(int i=0;i<amountNotifica.size();i++) {
                int costototale = costoBaseNotifica+ sharedSteps.getSentNotification().getPaFee();
                logger.info("Amount+costo base:"+costototale);
                amountNotifica.set(i, amountNotifica.get(i) + costototale);
            }
            Assertions.assertNotNull(amountNotifica);

        } catch (AssertionFailedError assertionFailedError) {

            String message = assertionFailedError.getMessage() +
                    "{la posizione debitoria " + (paymentPositionModelBaseResponse == null ? "NULL" : paymentPositionModelBaseResponse.toString()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());

        }
    }


    @And("l'avviso pagopa viene pagato correttamente su checkout")
    public void laNotificaVienePagatasuCheckout() {
        NotificationPriceResponse notificationPrice = this.b2bClient.getNotificationPrice(sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(0).getPagoPa().getCreditorTaxId(),
                sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(0).getPagoPa().getNoticeCode());

        PaymentRequest paymentRequest= new PaymentRequest();
        PaymentNotice paymentNotice= new PaymentNotice();
        paymentNotice.noticeNumber( sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(0).getPagoPa().getNoticeCode());
        paymentNotice.fiscalCode(sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(0).getPagoPa().getCreditorTaxId());
        paymentNotice.companyName("Test Automation");
        paymentNotice.amount(100);
        paymentNotice.description("Test Automation Desk");
        paymentRequest.paymentNotice(paymentNotice);
        paymentRequest.returnUrl("https://api.uat.platform.pagopa.it");

        try {
            Assertions.assertDoesNotThrow(() -> {
                paymentResponse=pnPaymentInfoClient.checkoutCart(paymentRequest);
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
                paymentInfoResponse=pnPaymentInfoClient.getPaymentInfoV21(paymentInfoRequestList);
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
                paymentInfoResponse=pnPaymentInfoClient.getPaymentInfoV21(paymentInfoRequestList);

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
                paymentResponse=pnPaymentInfoClient.checkoutCart(paymentRequest);

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
                paymentResponse=pnPaymentInfoClient.checkoutCart(paymentRequest);
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

        List<PaymentInfoV21> getPaymentInfoV21 = Assertions.assertDoesNotThrow(() -> pnPaymentInfoClient.getPaymentInfoV21(Collections.singletonList(paymentInfoRequest)));

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
                paymentResponse = pnPaymentInfoClient.checkoutCart(paymentRequest);
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

}
