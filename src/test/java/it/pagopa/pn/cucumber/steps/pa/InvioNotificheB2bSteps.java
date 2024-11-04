package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.model.mock.pec.PaperEngageRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.model.mock.pec.PaperEngageRequestAttachments;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.model.mock.pec.ReceivedMessage;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebPaClient;
import it.pagopa.pn.client.b2b.pa.service.impl.PnExternalChannelsServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnExternalServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaymentInfoClientImpl;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.payment_info.model.*;
import it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.NotificationSearchResponse;
import it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.NotificationSearchRow;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.utils.DataTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static org.awaitility.Awaitility.await;


@Slf4j
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
    private final PnExternalChannelsServiceClientImpl pnExternalChannelsServiceClientImpl;
    private PaymentResponse paymentResponse;
    private List<PaymentInfoV21> paymentInfoResponse;
    private NotificationDocument notificationDocumentPreload;
    private NotificationPaymentAttachment notificationPaymentAttachmentPreload;
    private NotificationMetadataAttachment notificationMetadataAttachment;
    private String sha256DocumentDownload;
    private NotificationAttachmentDownloadMetadataResponse downloadResponse;
    private List<ReceivedMessage> documentiPec;
    private final JavaMailSender emailSender;


    @Autowired
    public InvioNotificheB2bSteps(
            PnExternalServiceClientImpl safeStorageClient,
            SharedSteps sharedSteps,
            PnExternalChannelsServiceClientImpl pnExternalChannelsServiceClientImpl,
            JavaMailSender emailSender) {
        this.safeStorageClient = safeStorageClient;
        this.sharedSteps = sharedSteps;
        this.b2bUtils = sharedSteps.getB2bUtils();
        this.b2bClient = sharedSteps.getB2bClient();
        this.webPaClient = sharedSteps.getWebPaClient();
        this.pnPaymentInfoClientImpl = sharedSteps.getPnPaymentInfoClientImpl();
        this.pnExternalChannelsServiceClientImpl = pnExternalChannelsServiceClientImpl;
        this.emailSender = emailSender;
    }

    @And("la notifica può essere correttamente recuperata dal sistema tramite codice IUN")
    public void notificationCanBeRetrievedWithIUN() {
        AtomicReference<FullSentNotificationV25> notificationByIun = new AtomicReference<>();
        notificationCanBeRetrievedWithIUN(notificationByIun, b2bUtils::getNotificationByIun);
    }

    @And("la notifica può essere correttamente recuperata dal sistema tramite codice IUN con OpenApi V1")
    public void notificationCanBeRetrievedWithIUNV1() {
        AtomicReference<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.FullSentNotification> notificationByIun = new AtomicReference<>();
        notificationCanBeRetrievedWithIUN(notificationByIun, b2bUtils::getNotificationByIunV1);
    }

    @And("la notifica può essere correttamente recuperata dal sistema tramite codice IUN con OpenApi V20")
    public void notificationCanBeRetrievedWithIUNV2() {
        AtomicReference<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.FullSentNotificationV20> notificationByIun = new AtomicReference<>();
        notificationCanBeRetrievedWithIUN(notificationByIun, b2bUtils::getNotificationByIunV2);
    }

    private <T> void notificationCanBeRetrievedWithIUN(AtomicReference<T> notificationByIun, Function<String, T> getNotificationByIunFunction) {
        try {
            if (sharedSteps.getSentNotification() != null) {
                Assertions.assertDoesNotThrow(() ->
                        notificationByIun.set(getNotificationByIunFunction.apply(sharedSteps.getSentNotification().getIun())));
            } else if (sharedSteps.getSentNotificationV1() != null) {
                Assertions.assertDoesNotThrow(() ->
                        notificationByIun.set(getNotificationByIunFunction.apply(sharedSteps.getSentNotificationV1().getIun())));
            } else if (sharedSteps.getSentNotificationV2() != null) {
                Assertions.assertDoesNotThrow(() ->
                        notificationByIun.set(getNotificationByIunFunction.apply(sharedSteps.getSentNotificationV2().getIun())));
            } else {
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
            sharedSteps.setNotificationError(e);
        }
    }

    @And("la notifica non può essere recuperata dal sistema tramite codice IUN con OpenApi V10 generando un errore")
    public void notificationCanBeRetrievedWithIUNV1Error() {
        AtomicReference<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.FullSentNotification> notificationByIun = new AtomicReference<>();
        try {
            notificationByIun.set(b2bUtils.getNotificationByIunV1(sharedSteps.getSentNotification().getIun()));
        } catch (HttpStatusCodeException e) {
            sharedSteps.setNotificationError(e);
        }
    }

    @And("la notifica può essere correttamente recuperata dal sistema tramite codice IUN web PA")
    public void notificationCanBeRetrievedWithIUNWebPA() {
        AtomicReference<NotificationSearchResponse> notificationByIun = new AtomicReference<>();
        try {
            Assertions.assertDoesNotThrow(() ->
                    notificationByIun.set(webPaClient.searchSentNotification(OffsetDateTime.now().minusDays(1), OffsetDateTime.now(), null, null, null, sharedSteps.getSentNotification().getIun(), 1, null))
            );
            Assertions.assertNotNull(notificationByIun.get());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @And("recupera notifica vecchia di 120 giorni da lato web PA e verifica presenza pagamento")
    public void notification120ggCanBeRetrievedWithIUNWebPA() {
        List<NotificationSearchRow> serarchedNotification = searchNotificationWebFromADate(OffsetDateTime.now().minusDays(120));
        FullSentNotificationV25 notifica120 = null;
        for (NotificationSearchRow notifiche : serarchedNotification) {
            notifica120 = b2bClient.getSentNotification(notifiche.getIun());
            if (notifica120.getRecipients().get(0).getPayments() != null && notifica120.getRecipients().get(0).getPayments().get(0).getPagoPa() != null && notifica120.getRecipients().get(0).getPayments().get(0).getPagoPa().getNoticeCode() != null) {
                break;
            } else {
                notifica120 = null;
            }
            try {
                await().atMost(sharedSteps.getWorkFlowWait(), TimeUnit.MILLISECONDS);
            } catch (RuntimeException exc) {
                log.error(exc.getMessage());
                throw exc;
            }
        }

        try {
            Assertions.assertNotNull(notifica120);
            log.info("notifica dopo 120gg: {}", notifica120);
            Assertions.assertNull(notifica120.getRecipients().get(0).getPayments().get(0).getPagoPa().getAttachment());
            sharedSteps.setSentNotification(notifica120);
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{notifica : " + (notifica120 == null ? "NULL" : notifica120) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @And("recupero notifica del {string} lato web dalla PA {string} e verifica presenza pagamento per notifica che è arrivato fino al elemento {string} con feePolicy {string}")
    public void notificationFromADateCanBeRetrievedWithIUNWebPA(String stringDate, String pa, String type, String feePolicy) {
        sharedSteps.selectPA(pa);
        LocalDate date = LocalDate.parse(stringDate);
        OffsetDateTime offsetDateTime = date.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime();
        List<NotificationSearchRow> serarchedNotification = searchNotificationWebFromADate(offsetDateTime);
        FullSentNotificationV25 notifica = null;
        for (NotificationSearchRow notifiche : serarchedNotification) {
            notifica = b2bClient.getSentNotification(notifiche.getIun());
            if (!notifica.getRecipients().get(0).getPayments().isEmpty() && notifica.getRecipients().get(0).getPayments() != null && notifica.getRecipients().get(0).getPayments().get(0).getPagoPa() != null && notifica.getTimeline().toString().contains(type) && notifica.getNotificationFeePolicy().toString().equals(feePolicy) && notifica.getPaFee() == null) {
                break;
            } else {
                notifica = null;
            }
            await().atMost(sharedSteps.getWorkFlowWait(), TimeUnit.MILLISECONDS);
        }
        try {
            Assertions.assertNotNull(notifica);
            log.info("notifica trovata: {}", notifica);
            notifica.setPaFee(100);
            notifica.setVat(22);
            sharedSteps.setSentNotification(notifica);

        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{notifica : " + (notifica == null ? "NULL" : notifica) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    private List<NotificationSearchRow> searchNotificationWebFromADate(OffsetDateTime data) {
        AtomicReference<NotificationSearchResponse> notificationByIun = new AtomicReference<>();
        Assertions.assertDoesNotThrow(() ->
                notificationByIun.set(webPaClient.searchSentNotification(data, data.plusDays(20), null, null, null, null, 50, null)));
        Assertions.assertNotNull(notificationByIun.get());
        Assertions.assertNotNull(notificationByIun.get().getResultsPage());
        Assertions.assertTrue(notificationByIun.get().getResultsPage().size() > 0);
        return notificationByIun.get().getResultsPage();
    }

    @Then("la notifica può essere correttamente recuperata dal sistema tramite Stato {string} dalla web PA {string}")
    public void notificationCanBeRetrievedWithStatusByWebPA(String status, String paType) {
        sharedSteps.selectPA(paType);

        NotificationStatus notificationInternalStatus = switch (status) {
            case "ACCEPTED" -> NotificationStatus.ACCEPTED;
            case "DELIVERING" -> NotificationStatus.DELIVERING;
            case "DELIVERED" -> NotificationStatus.DELIVERED;
            case "CANCELLED" -> NotificationStatus.CANCELLED;
            case "EFFECTIVE_DATE" -> NotificationStatus.EFFECTIVE_DATE;
            case "REFUSED" -> NotificationStatus.REFUSED;
            default -> throw new IllegalArgumentException();
        };

        AtomicReference<NotificationSearchResponse> notificationByIun = new AtomicReference<>();
        try {
            Assertions.assertDoesNotThrow(() ->
                    notificationByIun.set(webPaClient.searchSentNotification(
                            OffsetDateTime.now().minusDays(1),
                            OffsetDateTime.now(),
                            null,
                            notificationInternalStatus,
                            null,
                            null,
                            1,
                            null))
            );
            Assertions.assertNotNull(notificationByIun.get());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("la notifica viene recuperata dal sistema tramite codice IUN")
    public void laNotificaVieneRecuperataDalSistemaTramiteCodiceIUN() {
        AtomicReference<FullSentNotificationV25> notificationByIun = new AtomicReference<>();
        try {
            FullSentNotificationV25 notificationResponseComplete = b2bUtils.getNotificationByIun(sharedSteps.getSentNotification().getIun());
            notificationByIun.set(notificationResponseComplete);
            sharedSteps.setSentNotification(notificationResponseComplete);
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @Given("viene effettuato il pre-caricamento di un documento")
    public void preLoadingOfDocument() {
        NotificationDocument notificationDocument = b2bUtils.newDocument("classpath:/sample.pdf");
        AtomicReference<NotificationDocument> notificationDocumentAtomic = new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> notificationDocumentAtomic.set(b2bUtils.preloadDocument(notificationDocument)));
        this.notificationDocumentPreload = notificationDocumentAtomic.get();
    }

    @Given("viene effettuato il pre-caricamento di un allegato")
    public void preLoadingOfAttachment() {
        NotificationPaymentAttachment notificationPaymentAttachment = b2bUtils.newAttachment("classpath:/sample.pdf");
        AtomicReference<NotificationPaymentAttachment> notificationDocumentAtomic = new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> notificationDocumentAtomic.set(b2bUtils.preloadAttachment(notificationPaymentAttachment)));
        this.notificationPaymentAttachmentPreload = notificationDocumentAtomic.get();
    }

    @Given("viene effettuato il pre-caricamento dei metadati f24")
    public void preLoadingOfMetaDatiAttachmentF24() {
        NotificationMetadataAttachment notificationPaymentAttachment = b2bUtils.newMetadataAttachment("classpath:/METADATA_CORRETTO.json");
        AtomicReference<NotificationMetadataAttachment> notificationDocumentAtomic = new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> notificationDocumentAtomic.set(b2bUtils.preloadMetadataAttachment(notificationPaymentAttachment)));
        try {
            Thread.sleep(sharedSteps.getWait());
        } catch (InterruptedException e) {
            log.error("Thread.sleep error retry");
            throw new RuntimeException(e);
        }
        this.notificationMetadataAttachment = notificationDocumentAtomic.get();
    }


    @Then("viene effettuato un controllo sulla durata della retention di {string} precaricato")
    public void retentionCheckPreload(String documentType) {
        String key = switch (documentType) {
            case "ATTO OPPONIBILE" -> this.notificationDocumentPreload.getRef().getKey();
            case "PAGOPA" -> this.notificationPaymentAttachmentPreload.getRef().getKey();
            case "F24" -> this.notificationMetadataAttachment.getRef().getKey();
            default -> throw new IllegalArgumentException();
        };
        Assertions.assertTrue(checkRetetion(key, retentionTimePreLoad));
    }


    @And("viene effettuato un controllo sulla durata della retention di {string}")
    public void retentionCheckLoad(String documentType) {
        String key = switch (documentType) {
            case "ATTO OPPONIBILE" -> sharedSteps.getSentNotification().getDocuments().get(0).getRef().getKey();
            case "PAGOPA" ->
                    sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(0).getPagoPa().getAttachment().getRef().getKey();
            case "F24" ->
                    sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(0).getF24().getMetadataAttachment().getRef().getKey();
            default -> throw new IllegalArgumentException();
        };
        Assertions.assertTrue(checkRetetion(key, retentionTimeLoad));
    }

    @And("viene effettuato un controllo sulla durata della retention di {string} per l'elemento di timeline {string}")
    public void retentionCheckLoadForTimelineElement(String documentType, String timelineEventCategory, @Transpose DataTest dataFromTest) throws RuntimeException {
        TimelineElementV25 timelineElement = sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataFromTest);
        if (documentType.equals("ATTACHMENTS")) {
            for (int i = 0; i < sharedSteps.getSentNotification().getDocuments().size(); i++) {
                String key = sharedSteps.getSentNotification().getDocuments().get(i).getRef().getKey();
                Assertions.assertTrue(checkRetention(key, retentionTimeLoad, timelineElement.getTimestamp()));
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    @And("viene effettuato un controllo sulla durata della retention del F24 di {string} per l'elemento di timeline {string}")
    public void retentionCheckLoadForTimelineElementF24(String documentType, String timelineEventCategory, @Transpose DataTest dataFromTest) throws RuntimeException {
        TimelineElementV25 timelineElement = sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataFromTest);
        if (documentType.equals("ATTACHMENTS")) {
            for (int i = 0; i < sharedSteps.getSentNotification().getRecipients().get(0).getPayments().size(); i++) {
                String key = sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(i).getF24().getMetadataAttachment().getRef().getKey();
                Assertions.assertTrue(checkRetention(key, retentionTimeLoad, timelineElement.getTimestamp()));
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    @And("viene effettuato un controllo sul type zip attachment di {string} per l'elemento di timeline {string} con DOC {string}")
    public void attachmentCheckLoadForTimelineElementF24(String documentType, String timelineEventCategory, String doc, @Transpose DataTest dataFromTest) throws RuntimeException {
        TimelineElementV25 timelineElement = sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataFromTest);
        if (documentType.equals("ATTACHMENTS")) {
            Assertions.assertNotNull(timelineElement.getDetails().getAttachments());
            Assertions.assertTrue(doc.equalsIgnoreCase(timelineElement.getDetails().getAttachments().get(0).getDocumentType()));
            Assertions.assertTrue(timelineElement.getDetails().getAttachments().get(0).getUrl().contains(".zip"));
        } else {
            throw new IllegalArgumentException();
        }
    }

    @And("viene effettuato un controllo sulla durata della retention del PAGOPA di {string} per l'elemento di timeline {string}")
    public void retentionCheckLoadForTimelineElementPAGOPA(String documentType, String timelineEventCategory, @Transpose DataTest dataFromTest) throws RuntimeException {
        TimelineElementV25 timelineElement = sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataFromTest);
        if (documentType.equals("ATTACHMENTS")) {
            for (int i = 0; i < sharedSteps.getSentNotification().getRecipients().get(0).getPayments().size(); i++) {
                String key = sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(i).getPagoPa().getAttachment().getRef().getKey();
                Assertions.assertTrue(checkRetention(key, retentionTimeLoad, timelineElement.getTimestamp()));
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Given("viene letta la notifica {string} dal {string}")
    public void vieneLettaLaNotificaDal(String IUN, String pa) {
        sharedSteps.selectPA(pa);
        FullSentNotificationV25 notificationByIun = b2bUtils.getNotificationByIun(IUN);
        sharedSteps.setSentNotification(notificationByIun);
    }

    @When("si tenta il recupero della notifica dal sistema tramite codice IUN {string}")
    public void retrievalAttemptedIUN(String iun) {
        getNotificationByIun(iun);
    }

    @When("si tenta il recupero della notifica dal sistema")
    public void retrievalAttemptedIUN() {
        getNotificationByIun("");
    }

    private void getNotificationByIun(String iun) {
        try {
            if (!iun.isEmpty()) {
                b2bUtils.getNotificationByIun(iun);
            } else {
                b2bUtils.getNotificationByIun(new String(Base64Utils.decodeFromString(this.sharedSteps.getNewNotificationResponse().getNotificationRequestId())));
            }
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @When("si tenta il recupero della notifica dal sistema tramite codice IUN {string} con la V1")
    public void retrievalAttemptedIUNConV1(String iun) {
        getNotificationByIunVersioning(iun, "V1");
    }

    @When("si tenta il recupero della notifica dal sistema tramite codice IUN {string} con la V2")
    public void retrievalAttemptedIUNConV2(String iun) {
        getNotificationByIunVersioning(iun, "V2");
    }

    private void getNotificationByIunVersioning(String iun, String version) {
        try {
            if (version.equalsIgnoreCase("V1")) {
                b2bUtils.getNotificationByIunV1(iun);
            } else if (version.equalsIgnoreCase("V2")) {
                b2bUtils.getNotificationByIunV2(iun);
            }
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @When("viene richiesto il download del documento {string}")
    public void documentDownload(String type) {
        getDownloadFile(type, sharedSteps.getIunVersionamento(), 0);
    }

    @When("viene richiesto il download del documento {string} per il destinatario {int}")
    public void documentDownloadPerDestinatario(String type, int destinatario) {
        getDownloadFile(type, sharedSteps.getSentNotification().getIun(), destinatario);
    }

    @When("viene richiesto il download del documento {string} inesistente")
    public void documentAbsentDownload(String type) {
        getDownloadFile(type, sharedSteps.getSentNotification().getIun(), 0);
    }

    @When("viene richiesto il download del documento {string} inesistente per il destinatario {int}")
    public void documentAbsentDownload(String type, int destinatario) {
        getDownloadFile(type, sharedSteps.getSentNotification().getIun(), destinatario);
    }

    private void getDownloadFile(String type, String iun, int destinatario) {
        try {

            if (type.equalsIgnoreCase("NOTIFICA")) {
                List<NotificationDocument> documents = sharedSteps.getSentNotification().getDocuments();
                this.downloadResponse = b2bClient
                        .getSentNotificationDocument(sharedSteps.getSentNotification().getIun(), Integer.parseInt(documents.get(0).getDocIdx()));
            } else {
                this.downloadResponse = b2bClient
                        .getSentNotificationAttachment(iun, destinatario, type, 0);

                if (downloadResponse != null && downloadResponse.getRetryAfter() != null && downloadResponse.getRetryAfter() > 0) {
                    try {
                        await().atMost(downloadResponse.getRetryAfter() * 3L, TimeUnit.MILLISECONDS);
                        this.downloadResponse = b2bClient
                                .getSentNotificationAttachment(iun, destinatario, type, 0);
                    } catch (RuntimeException exc) {
                        log.error(exc.getMessage());
                        throw exc;
                    }
                }
            }
            byte[] bytes = b2bUtils.downloadFile(this.downloadResponse.getUrl());
            this.sha256DocumentDownload = b2bUtils.computeSha256(new ByteArrayInputStream(bytes));
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

    @Then("l'operazione ha prodotto un errore con status code {string} con messaggio di errore {string}")
    public void operationProducedAnErrorWithMessage(String statusCode, String errore) {
        HttpStatusCodeException httpStatusCodeException = this.sharedSteps.consumeNotificationError();
        Assertions.assertTrue((httpStatusCodeException != null) &&
                (httpStatusCodeException.getStatusCode().toString().substring(0, 3).equals(statusCode)));

        byte[] responseBody = httpStatusCodeException.getResponseBodyAsByteArray();
        String responseBodyText = new String(responseBody, StandardCharsets.UTF_8);

        Assertions.assertTrue(responseBodyText.contains(errore));
    }

    @Then("l'operazione non ha prodotto errori")
    public void operationProducedNotAnError() {
        HttpStatusCodeException httpStatusCodeException = this.sharedSteps.consumeNotificationError();
        Assertions.assertNull(httpStatusCodeException);
    }


    @Then("si verifica la corretta acquisizione della notifica")
    public void correctAcquisitionNotification() {
        Assertions.assertDoesNotThrow(() -> verifyNotificationVersioning("V23"));
    }

    @Then("si verifica la corretta acquisizione della notifica V1")

    public void correctAcquisitionNotificationV1() {
        Assertions.assertDoesNotThrow(() -> verifyNotificationVersioning("V1"));
    }

    @Then("si verifica la corretta acquisizione della notifica V2")
    public void correctAcquisitionNotificationV2() {
        Assertions.assertDoesNotThrow(() -> verifyNotificationVersioning("V2"));
    }

    @Then("si verifica lo scarto dell' acquisizione della notifica V1")
    public void correctAcquisitionNotificationV1Error() {
        verifyNotificationVersioning("V1");

    }

    @Then("si verifica lo scarto dell' acquisizione della notifica V2")
    public void correctAcquisitionNotificationV2Error() {
        verifyNotificationVersioning("V2");
    }

    private void verifyNotificationVersioning(String version) {
        try {
            if (version.equalsIgnoreCase("V1")) {
                b2bUtils.verifyNotificationV1(sharedSteps.getSentNotificationV1());
            } else if (version.equalsIgnoreCase("V2")) {
                b2bUtils.verifyNotificationV2(sharedSteps.getSentNotificationV2());
            } else if (version.equalsIgnoreCase("V23")) {
                b2bUtils.verifyNotification(sharedSteps.getSentNotification());
            }
        } catch (AssertionFailedError assertionFailedError) {
            log.info("Errore di acquisizione notifica");
        }
    }

    @Then("si verifica la corretta acquisizione della notifica con verifica sha256 del allegato di pagamento {string}")
    public void correctAcquisitionNotificationVerifySha256AllegatiPagamento(String attachnament) {
        Assertions.assertDoesNotThrow(() -> b2bUtils.verifyNotificationAndSha256AllegatiPagamento(sharedSteps.getSentNotification(), attachnament));
    }

    @And("viene controllato la presenza del taxonomyCode")
    public void checkTaxonomyCode() {
        Assertions.assertNotNull(this.sharedSteps.getSentNotification().getTaxonomyCode());
        Assertions.assertEquals(this.sharedSteps.getNotificationRequest().getTaxonomyCode(), this.sharedSteps.getSentNotification().getTaxonomyCode());
    }

    @And("vengono prodotte le evidenze: metadati e requestID")
    public void evidenceProduced() {
        Assertions.assertNotNull(this.sharedSteps.getNewNotificationResponse());
        log.info("METADATI: " + '\n' + this.sharedSteps.getNewNotificationResponse());
        log.info("REQUEST-ID: " + '\n' + this.sharedSteps.getNewNotificationResponse().getNotificationRequestId());
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
        log.info("now: " + now);
        log.info("retentionUntil: " + retentionUntil);
        long between = ChronoUnit.DAYS.between(now, retentionUntil);
        log.info("Difference: " + between);
        return retentionTime == between;
    }

    private boolean checkRetention(String fileKey, Integer retentionTime, OffsetDateTime timelineEventTimestamp) throws RuntimeException {
        await().atMost(120000, TimeUnit.MILLISECONDS);
        PnExternalServiceClientImpl.SafeStorageResponse safeStorageResponse = safeStorageClient.safeStorageInfo(fileKey);
        System.out.println(safeStorageResponse);
        OffsetDateTime timelineEventDate = timelineEventTimestamp.atZoneSameInstant(ZoneId.of("Z")).toOffsetDateTime();
        OffsetDateTime retentionUntil = OffsetDateTime.parse(safeStorageResponse.getRetentionUntil());
        log.info("now: " + timelineEventDate);
        log.info("retentionUntil: " + retentionUntil);
        OffsetDateTime timelineEventDateDays = timelineEventDate.truncatedTo(ChronoUnit.DAYS);
        OffsetDateTime retentionUntilDays = retentionUntil.truncatedTo(ChronoUnit.DAYS);

        long between = ChronoUnit.DAYS.between(timelineEventDateDays, retentionUntilDays);

        LocalTime timelineEventDateLocalTime = timelineEventDate.toLocalTime();
        LocalTime retentionUntilLocalTime = retentionUntil.toLocalTime();
        Duration diff = Duration.between(timelineEventDateLocalTime, retentionUntilLocalTime);
        long diffInMinutes = diff.toMinutes();

        log.info("Difference: " + between);
        log.info("diffInMinutes: " + diffInMinutes);
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
        log.debug(newNotificationRequestStatusResponse.getNotificationRequestStatus());
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
            RequestStatus resp = Assertions.assertDoesNotThrow(() ->
                    b2bClient.notificationCancellation(sharedSteps.getSentNotification().getIun()));

            Assertions.assertNotNull(resp);
            Assertions.assertNotNull(resp.getDetails());
            Assertions.assertFalse(resp.getDetails().isEmpty());
            Assertions.assertTrue("NOTIFICATION_ALREADY_CANCELLED".equalsIgnoreCase(resp.getDetails().get(0).getCode()));

        });
    }

    @Then("si verifica il corretto annullamento della notifica")
    public void correctCanceledNotification() {
        //Assertions.assertNull(assertionFailedError);
    }

    @And("l'avviso pagopa viene pagato correttamente su checkout")
    public void laNotificaVienePagatasuCheckout() {
        NotificationPriceResponseV23 notificationPrice = this.b2bClient.getNotificationPriceV23(Objects.requireNonNull(Objects.requireNonNull(sharedSteps.getSentNotification().getRecipients().get(0).getPayments()).get(0).getPagoPa()).getCreditorTaxId(),
                Objects.requireNonNull(Objects.requireNonNull(sharedSteps.getSentNotification().getRecipients().get(0).getPayments()).get(0).getPagoPa()).getNoticeCode());

        PaymentRequest paymentRequest = getPaymentRequest(notificationPrice,
                Objects.requireNonNull(Objects.requireNonNull(sharedSteps.getSentNotification().getRecipients().get(0).getPayments()).get(0).getPagoPa()).getNoticeCode(),
                Objects.requireNonNull(Objects.requireNonNull(sharedSteps.getSentNotification().getRecipients().get(0).getPayments()).get(0).getPagoPa()).getCreditorTaxId(),
                "Test Automation",
                null,
                "Test Automation Desk",
                "https://api.uat.platform.pagopa.it");
        verifyCheckoutCart(paymentRequest, null);
    }

    @Then("verifica stato pagamento di una notifica creditorTaxID {string} noticeCode {string} con errore {string}")
    public void verificaStatoPagamentoNotifica(String creditorTaxID, String noticeCode, String codiceErrore) {
        List<PaymentInfoRequest> paymentInfoRequestList = new ArrayList<>();

        PaymentInfoRequest paymentInfoRequest = new PaymentInfoRequest()
                .creditorTaxId(creditorTaxID)
                .noticeCode(noticeCode);

        paymentInfoRequestList.add(paymentInfoRequest);

        log.info("Messaggio json da allegare: " + paymentInfoRequest);

        try {
            Assertions.assertDoesNotThrow(() -> {
                paymentInfoResponse = pnPaymentInfoClientImpl.getPaymentInfoV21(paymentInfoRequestList);
                log.info("Informazioni sullo stato del Pagamento: " + paymentInfoResponse.toString());
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
        List<PaymentInfoRequest> paymentInfoRequestList = new ArrayList<>();

        PaymentInfoRequest paymentInfoRequest = new PaymentInfoRequest()
                .creditorTaxId(sharedSteps.getNotificationRequest().getRecipients().get(0).getPayments().get(0).getPagoPa().getCreditorTaxId())
                .noticeCode(sharedSteps.getNotificationRequest().getRecipients().get(0).getPayments().get(0).getPagoPa().getNoticeCode());

        paymentInfoRequestList.add(paymentInfoRequest);

        log.info("Messaggio json da allegare: " + paymentInfoRequest);

        try {
            Assertions.assertDoesNotThrow(() -> {
                paymentInfoResponse = pnPaymentInfoClientImpl.getPaymentInfoV21(paymentInfoRequestList);

            });
            Assertions.assertNotNull(paymentInfoResponse);
            log.info("Informazioni sullo stato del Pagamento: " + paymentInfoResponse);
            Assertions.assertTrue(status.equalsIgnoreCase(paymentInfoResponse.get(0).getStatus().getValue()));

        } catch (AssertionFailedError assertionFailedError) {

            String message = assertionFailedError.getMessage() +
                    "{Informazioni sullo stato del Pagamento: " + (paymentInfoResponse == null ? "NULL" : paymentInfoResponse.toString()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @And("l'avviso pagopa viene pagato correttamente su checkout con errore {string}")
    public void laNotificaVienePagatasuCheckoutError(String codiceErrore) {
        PaymentRequest paymentRequest = getPaymentRequest(null,
                Objects.requireNonNull(Objects.requireNonNull(sharedSteps.getNotificationRequest().getRecipients().get(0).getPayments()).get(0).getPagoPa()).getNoticeCode(),
                Objects.requireNonNull(Objects.requireNonNull(sharedSteps.getNotificationRequest().getRecipients().get(0).getPayments()).get(0).getPagoPa()).getCreditorTaxId(),
                "Test Automation",
                100,
                "Test Automation Desk",
                "https://api.uat.platform.pagopa.it");

        verifyCheckoutCart(paymentRequest, codiceErrore);
    }

    @And("l'avviso pagopa viene pagato correttamente su checkout creditorTaxID {string} noticeCode {string} con errore {string}")
    public void laNotificaVienePagatasuCheckoutError(String creditorTaxID, String noticeCode, String codiceErrore) {
        PaymentRequest paymentRequest = getPaymentRequest(null,
                noticeCode,
                creditorTaxID,
                "Test Automation",
                100,
                "Test Automation Desk",
                "https://api.uat.platform.pagopa.it");

        verifyCheckoutCart(paymentRequest, codiceErrore);
    }

    @And("la notifica a 2 avvisi di pagamento con OpenApi V1")
    public void notificationCanBeRetrievePaymentV1() {
        AtomicReference<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.FullSentNotification> notificationByIun = new AtomicReference<>();
        String iun = sharedSteps.getIunVersionamento();

        try {
            Assertions.assertDoesNotThrow(() ->
                    notificationByIun.set(b2bUtils.getNotificationByIunV1(iun)));

            Assertions.assertNotNull(notificationByIun.get());
            Assertions.assertNotNull(Objects.requireNonNull(notificationByIun.get().getRecipients().get(0).getPayment()).getNoticeCode());
            Assertions.assertNotNull(Objects.requireNonNull(notificationByIun.get().getRecipients().get(0).getPayment()).getNoticeCodeAlternative());

        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @And("la notifica a 2 avvisi di pagamento con OpenApi V2")
    public void notificationCanBeRetrievePaymentV2() {
        AtomicReference<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.FullSentNotificationV20> notificationByIun = new AtomicReference<>();
        String iun = sharedSteps.getIunVersionamento();

        try {
            Assertions.assertDoesNotThrow(() ->
                    notificationByIun.set(b2bUtils.getNotificationByIunV2(iun)));

            Assertions.assertNotNull(notificationByIun.get());
            Assertions.assertNotNull(Objects.requireNonNull(notificationByIun.get().getRecipients().get(0).getPayment()).getNoticeCode());
            Assertions.assertNotNull(Objects.requireNonNull(notificationByIun.get().getRecipients().get(0).getPayment()).getNoticeCodeAlternative());

        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @And("la notifica a 1 avvisi di pagamento con OpenApi V1")
    public void notificationCanBeRetrievePayment1V1() {
        AtomicReference<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.FullSentNotification> notificationByIun = new AtomicReference<>();
        String iun = sharedSteps.getIunVersionamento();
        try {
            Assertions.assertDoesNotThrow(() ->
                    notificationByIun.set(b2bUtils.getNotificationByIunV1(iun)));
            Assertions.assertNotNull(notificationByIun.get());
            Assertions.assertNotNull(Objects.requireNonNull(notificationByIun.get().getRecipients().get(0).getPayment()).getNoticeCode());
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
        PaymentRequest paymentRequest = getPaymentRequest(null,
                noticeCode,
                creditorTaxId,
                "Test Automation",
                getPaymentInfoV21.get(0).getAmount(),
                "Test Automation Desk",
                "https://api.uat.platform.pagopa.it");

        System.out.println("COSTO NOTIFICA: " + getPaymentInfoV21.get(0).getAmount());

        verifyCheckoutCart(paymentRequest, null);
    }

    private void verifyCheckoutCart(PaymentRequest paymentRequest, String codiceErrore) {

        try {
            Assertions.assertDoesNotThrow(() -> {
                paymentResponse = pnPaymentInfoClientImpl.checkoutCart(paymentRequest);
                log.info("Risposta recupero posizione debitoria: " + paymentInfoResponse.toString());
            });
            Assertions.assertNotNull(paymentResponse);

            if (codiceErrore != null) {
                Assertions.assertTrue(codiceErrore.equalsIgnoreCase(paymentInfoResponse.get(0).getErrorCode()));
                Assertions.assertTrue(codiceErrore.equalsIgnoreCase(paymentResponse.getCheckoutUrl()));
            }
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{la posizione debitoria " + (paymentResponse == null ? "NULL" : paymentResponse.toString()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @Then("si verifica che il phyicalAddress sia stato normalizzato correttamente con rimozione caratteri isoLatin1")
    public void controlloCampiAddressNormalizzatore() {
        String regex = "[{}-~¡-ÿ^]";
        String regexCaratteriA = "[æ]";

        FullSentNotificationV25 timeline = sharedSteps.getSentNotification();

        TimelineElementV25 timelineNormalizer = timeline.getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV23.NORMALIZED_ADDRESS)).findAny().orElse(null);
        PhysicalAddress oldAddress = timelineNormalizer.getDetails().getOldAddress();
        PhysicalAddress normalizedAddress = timelineNormalizer.getDetails().getNormalizedAddress();

        try {
            Assertions.assertNotNull(normalizedAddress);
            Assertions.assertNotNull(oldAddress);

            log.info("old address: {}", oldAddress);
            log.info("normalized address: {}", normalizedAddress);

            PhysicalAddress newAddress = new PhysicalAddress()
                    .address(oldAddress.getAddress().replaceAll(regexCaratteriA, "A ").replaceAll(regex, " ").toUpperCase())
                    .municipality(oldAddress.getMunicipality().replaceAll(regexCaratteriA, "A ").replaceAll(regex, " ").toUpperCase())
                    .municipalityDetails(oldAddress.getMunicipalityDetails().replaceAll(regexCaratteriA, "A ").replaceAll(regex, " ").toUpperCase())
                    .province(oldAddress.getProvince().replaceAll(regexCaratteriA, "A ").replaceAll(regex, " ").toUpperCase())
                    .zip(oldAddress.getZip().replaceAll(regexCaratteriA, "A ").replaceAll(regex, " ").toUpperCase());

            log.info(" newAddress: {}", newAddress);

            Assertions.assertEquals(newAddress.getAddress().toUpperCase(), normalizedAddress.getAddress());
            Assertions.assertEquals(newAddress.getMunicipality(), normalizedAddress.getMunicipality());
            Assertions.assertEquals(newAddress.getMunicipalityDetails(), normalizedAddress.getMunicipalityDetails());
            Assertions.assertEquals(newAddress.getProvince(), normalizedAddress.getProvince());
            Assertions.assertEquals(newAddress.getZip(), normalizedAddress.getZip());


        } catch (AssertionFailedError error) {
            sharedSteps.throwAssertFailerWithIUN(error);
        }
    }

    private PaymentRequest getPaymentRequest(NotificationPriceResponseV23 notificationPrice, String noticeNumber, String fiscalCode, String companyName, Integer amount, String description, String returnUrl) {
        PaymentRequest paymentRequest = new PaymentRequest();
        PaymentNotice paymentNotice = new PaymentNotice();
        paymentNotice.noticeNumber(noticeNumber);
        paymentNotice.fiscalCode(fiscalCode);
        paymentNotice.companyName(companyName);
        paymentNotice.description(description);
        if (amount != null) {
            paymentNotice.setAmount(amount);
        }
        if (notificationPrice != null) {
            paymentNotice.amount(notificationPrice.getTotalPrice());
        }
        paymentRequest.paymentNotice(paymentNotice);
        paymentRequest.returnUrl(returnUrl);
        return paymentRequest;
    }

    @Given("viene cancellata la notifica con IUN {string}")
    public void vieneCancellataLaNotificaConIUN(String iun) {
        b2bClient.setApiKeys(SettableApiKey.ApiKeyType.GA);
        Assertions.assertDoesNotThrow(() -> {
            RequestStatus resp = Assertions.assertDoesNotThrow(() ->
                    b2bClient.notificationCancellation(iun));

            Assertions.assertNotNull(resp);
            Assertions.assertNotNull(resp.getDetails());
            Assertions.assertTrue(resp.getDetails().size() > 0);
            Assertions.assertTrue("NOTIFICATION_CANCELLATION_ACCEPTED".equalsIgnoreCase(resp.getDetails().get(0).getCode()));

        });
    }


    @And("si verifica il contenuto degli attacchment da inviare nella pec del destinatario {int} con {int} allegati")
    public void vieneVerificatoIDocumentiInviatiDellaPecDelDestinatarioConNumeroDiAllegati(Integer destinatario, Integer allegati) {
        try {
            this.documentiPec = pnExternalChannelsServiceClientImpl.getReceivedMessages(sharedSteps.getIunVersionamento(), destinatario);
            Assertions.assertNotNull(documentiPec);

            log.info("documenti pec : {}", documentiPec);

            Assertions.assertEquals(allegati, documentiPec.get(0).getDigitalNotificationRequest().getAttachmentUrls().size());
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "Verifica Allegati pec in errore ";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }

    }

    @And("si verifica il contenuto degli attacchment da inviare nella pec del destinatario {int} da {string}")
    public void vieneVerificatoIDocumentiInviatiDellaPecDelDestinatario(Integer destinatario, String basePath) {
        try {
            pnExternalChannelsServiceClientImpl.switchBasePath(basePath);
            this.documentiPec = pnExternalChannelsServiceClientImpl.getReceivedMessages(sharedSteps.getIunVersionamento(), destinatario);
            Assertions.assertNotNull(documentiPec);

            log.info("documenti pec : {}", documentiPec);
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "Verifica Allegati pec in errore ";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }

    }

    @And("si verifica lo SHA degli attachment inseriti nella pec del destinatario {int} di tipo {string}")
    public void verificaSHAAllegatiPecDelDestinatario(Integer destinatario, String tipoAttachment) {
        try {

            //caricamento in Mappa di tutti i documenti della notifica
            for (NotificationDocument documentNotifica : sharedSteps.getSentNotification().getDocuments()) {
                sharedSteps.getMapAllegatiNotificaSha256().put(documentNotifica.getRef().getKey(), documentNotifica.getDigests().getSha256());
            }
            //caricamento in Mappa di tutti i documenti di pagamento della notifica
            for (NotificationPaymentItem documentPagamento : sharedSteps.getSentNotification().getRecipients().get(destinatario).getPayments()) {
                sharedSteps.getMapAllegatiNotificaSha256().put(documentPagamento.getPagoPa().getAttachment().getRef().getKey(), documentPagamento.getPagoPa().getAttachment().getDigests().getSha256());
            }

            Assertions.assertFalse(sharedSteps.getMapAllegatiNotificaSha256().isEmpty());

            boolean checkAllegati = true;
            for (ReceivedMessage documentPec : documentiPec) {
                for (String documentPecKey : documentPec.getDigitalNotificationRequest().getAttachmentUrls()) {
                    if (documentPecKey.contains(tipoAttachment)) {
                        PnExternalServiceClientImpl.SafeStorageResponse safeStorageResponse = safeStorageClient.safeStorageInfo(documentPecKey.substring(14));
                        Assertions.assertNotNull(safeStorageResponse);
                        Assertions.assertNotNull(safeStorageResponse.getChecksum());
                        Assertions.assertNotNull(sharedSteps.getMapAllegatiNotificaSha256().get(safeStorageResponse.getKey()));
                        if (!safeStorageResponse.getChecksum().equals(sharedSteps.getMapAllegatiNotificaSha256().get(safeStorageResponse.getKey()))) {
                            checkAllegati = false;
                            break;
                        }
                    }
                }
            }
            Assertions.assertTrue(checkAllegati);

        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "Verifica Allegati pec in errore ";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @And("si verifica il contenuto della pec abbia {int} attachment di tipo {string}")
    public void presenzaAttachment(Integer numeroDocumenti, String tipologia) {
        int contoDocumento = 0;
        for (String attachmentUrl : documentiPec.get(0).getDigitalNotificationRequest().getAttachmentUrls()) {
            contoDocumento += attachmentUrl.contains(tipologia) ? 1 : 0;
        }
        try {
            Assertions.assertSame(numeroDocumenti, contoDocumento);
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() + "Verifica Allegati pec in errore ";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @And("si verifica il contenuto degli attachments da inviare in via cartacea al destinatario {int} con {int} allegati")
    public void checkDocumentInviatiPaper(Integer destinatario, Integer allegati) {
        try {
            this.documentiPec = pnExternalChannelsServiceClientImpl.getReceivedMessagesAnalogico(sharedSteps.getIunVersionamento(), destinatario);
            Assertions.assertNotNull(documentiPec);
            log.info("documenti analogici : {}", documentiPec);
            Assertions.assertEquals(allegati, documentiPec.get(0).getPaperEngageRequest().getAttachments().size());
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() + "Verifica Allegati analogici in errore ";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @And("si verifica che il contenuto degli attachments da inviare in via cartacea abbia {int} attachment di tipo {string}")
    public void presenceAttachmentAnalogicFlow(Integer numeroDocumenti, String tipologia) {
        List<String> attachmentsUri = Optional.ofNullable(documentiPec.get(0))
                .map(ReceivedMessage::getPaperEngageRequest)
                .map(PaperEngageRequest::getAttachments)
                .orElse(List.of())
                .stream()
                .map(PaperEngageRequestAttachments::getUri)
                .filter(uri -> uri.contains(tipologia))
                .toList();
        try {
            Assertions.assertEquals(numeroDocumenti, attachmentsUri.size());
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() + "Verifica Allegati Cartacei in errore ";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @And("si verifica che il {int} documento arrivato sia di tipo {string}")
    public void checkIndexedDocument(Integer documentIndex, String tipologia) {
        ReceivedMessage firstDocumentReceived = documentiPec.get(0);
        Assertions.assertNotNull(firstDocumentReceived.getPaperEngageRequest());
        Assertions.assertNotNull(firstDocumentReceived.getPaperEngageRequest().getAttachments());
        Assertions.assertEquals(firstDocumentReceived.getPaperEngageRequest().getAttachments().get(documentIndex - 1).getDocumentType(), tipologia);
        log.info(firstDocumentReceived.toString());
    }

    @Value("${b2b.sender.mail}")
    private String senderEmail;


    private void sendEmail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@testInvio.com");
        message.setTo(senderEmail);
        message.setSubject("prova invio");
        message.setText("Test invio su pec mittente");
        emailSender.send(message);
    }


    @Given("si invia una email alla pec mittente e si attendono {int} minuti")
    public void siInviaUnaEmailAllaPecMittenteESiAttendonoMinuti(int wait) {
        Assertions.assertDoesNotThrow(this::sendEmail);
        long waiting = ((wait * 60L) * 1000);
        Assertions.assertDoesNotThrow(() -> Thread.sleep(waiting));
    }

    @Given("si richiama checkout con dati:")
    public void siRichiamaCheckoutConDati(Map<String, String> dataCheckout) {
        PaymentRequest requestCheckout = creationPaymentRequest(dataCheckout);
        PaymentResponse responseCheckout = pnPaymentInfoClientImpl.checkoutCart(requestCheckout);
        Assertions.assertNotNull(responseCheckout);
        Assertions.assertNotNull(responseCheckout.getCheckoutUrl());
        log.info("response checkout: {}", responseCheckout);
    }

    @Given("si richiama checkout con restituzione errore")
    public void siRichiamaCheckoutConDatiConErrore(Map<String, String> dataCheckout) {
        PaymentRequest requestCheckout = creationPaymentRequest(dataCheckout);
        try {
            pnPaymentInfoClientImpl.checkoutCart(requestCheckout);
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    public PaymentRequest creationPaymentRequest(Map<String, String> dataCheckout) {

        PaymentRequest requestCheckout = new PaymentRequest()
                .paymentNotice(new PaymentNotice()
                        .noticeNumber(dataCheckout.get("noticeCode") != null ? dataCheckout.get("noticeCode") :
                                sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(0).getPagoPa().getNoticeCode())
                        .fiscalCode(dataCheckout.get("fiscalCode") != null ? dataCheckout.get("fiscalCode") :
                                sharedSteps.getSentNotification().getRecipients().get(0).getPayments().get(0).getPagoPa().getCreditorTaxId())
                        .amount(dataCheckout.get("amount") != null ? Integer.parseInt(dataCheckout.get("amount")) : null)
                        .description(dataCheckout.get("description"))
                        .companyName(dataCheckout.get("companyName")))
                .returnUrl(dataCheckout.get("returnUrl"));
        log.info("request checkout: {}", requestCheckout);
        return requestCheckout;
    }


    @And("si verifica che negli url non contenga il docTag nel {string}")
    public void verificaNonPresenzaDocType(String type) {
        boolean contieneDocTag = false;
        for (String attachmentUrl : getAttachemtListForTypeOfNotification(type)) {
            if (attachmentUrl.contains("docTag")) {
                contieneDocTag = true;
                break;
            }
        }
        try {
            Assertions.assertFalse(contieneDocTag);
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "Verifica Allegati pec in errore ";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }


    public List<String> getAttachemtListForTypeOfNotification(String type) {
        List<String> attchmentNotification = new ArrayList<>();
        switch (type.toLowerCase()) {
            case "analogico" -> {
                for (PaperEngageRequestAttachments attahment : documentiPec.get(0).getPaperEngageRequest().getAttachments()) {
                    attchmentNotification.add(attahment.getUri());
                }
            }
            case "digitale" ->
                    attchmentNotification = documentiPec.get(0).getDigitalNotificationRequest().getAttachmentUrls();
        }
        return attchmentNotification;
    }

    private LegalFactDownloadMetadataResponse getLegalFactIdAAR(String aarType) {
        AtomicReference<LegalFactDownloadMetadataResponse> legalFactDownloadMetadataResponse = new AtomicReference<>();
        try {
            Thread.sleep(sharedSteps.getWait());
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }

        TimelineElementCategoryV23 timelineElementInternalCategory = TimelineElementCategoryV23.AAR_GENERATION;
        TimelineElementV25 timelineElement = null;
        for (TimelineElementV25 element : sharedSteps.getSentNotification().getTimeline()) {
            if (Objects.requireNonNull(element.getCategory()).equals(timelineElementInternalCategory)) {
                timelineElement = element;
                break;
            }
        }

        Assertions.assertNotNull(timelineElement);
        String keySearch = null;
        if (!Objects.requireNonNull(timelineElement.getDetails()).getGeneratedAarUrl().isEmpty()) {

            if (timelineElement.getDetails().getGeneratedAarUrl().contains(aarType)) {
                keySearch = timelineElement.getDetails().getGeneratedAarUrl().substring(timelineElement.getDetails().getGeneratedAarUrl().indexOf(aarType));
            }

            String finalKeySearch = keySearch;
            try {
                Assertions.assertDoesNotThrow(() -> legalFactDownloadMetadataResponse.set(this.b2bClient.getDownloadLegalFact(sharedSteps.getSentNotification().getIun(), finalKeySearch)));
            } catch (AssertionFailedError assertionFailedError) {
                sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
            }
        }
        return legalFactDownloadMetadataResponse.get();
    }

    @Then("download attestazione opponibile AAR e controllo del contenuto del file per verificare se il content-type è {string}")
    public void verificaContentTypeAttestazione(String contentType) {
        LegalFactDownloadMetadataResponse legalFactDownloadMetadataResponse = getLegalFactIdAAR("PN_AAR");
        Assertions.assertTrue(b2bUtils.downloadUrlAndCheckContent(legalFactDownloadMetadataResponse.getUrl(), contentType));
    }

}