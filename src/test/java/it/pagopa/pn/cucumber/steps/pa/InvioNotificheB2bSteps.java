package it.pagopa.pn.cucumber.steps.pa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffLegalNotificationSearchRow;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffLegalNotificationsResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.NotificationStatusV26;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.payment.*;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.model.mock.pec.PaperEngageRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.model.mock.pec.PaperEngageRequestAttachmentsInner;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.model.mock.pec.ReceivedMessage;
import it.pagopa.pn.client.b2b.pa.exception.IllegalConfigurationException;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.service.IPnSafeStoragePrivateClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebPaClient;
import it.pagopa.pn.client.b2b.pa.service.impl.PnExternalChannelsServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnExternalServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaymentInfoClientImpl;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.model.FileDownloadResponse;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.NotificationUtilsV24;
import it.pagopa.pn.cucumber.utils.DataTest;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static it.pagopa.pn.cucumber.steps.utilitySteps.Costanti.COMUNE_1;
import static it.pagopa.pn.cucumber.steps.utilitySteps.Costanti.MOST_RECENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.awaitility.Awaitility.await;


@Slf4j
public class InvioNotificheB2bSteps {

    @Value("${pn.retention.time.preload}")
    private Integer retentionTimePreLoad;
    @Value("${pn.retention.time.load}")
    private Integer retentionTimeLoad;
    @Value("${pn.blacklist.tax-ids}")
    private String blackListTaxIdsProperties;
    private final IPnWebPaClient webPaClient;
    @Getter
    private final IPnPaB2bClient b2bClient;
    private final PnExternalServiceClientImpl safeStorageClient;
    private final IPnSafeStoragePrivateClient safeStoragePrivateClient;
    @Getter
    private final SharedSteps sharedSteps;
    @Getter
    private final PnPaymentInfoClientImpl pnPaymentInfoClientImpl;
    private final PnExternalChannelsServiceClientImpl pnExternalChannelsServiceClientImpl;

    private BffPaymentResponse paymentResponse;
    @Getter
    @Setter
    private List<BffPaymentInfoItem> paymentInfoResponse;
    private NotificationDocument notificationDocumentPreload;
    private NotificationPaymentAttachment notificationPaymentAttachmentPreload;
    private NotificationMetadataAttachment notificationMetadataAttachment;
    private String sha256DocumentDownload;
    private NotificationAttachmentDownloadMetadataResponse downloadResponse;
    private List<ReceivedMessage> documentiPec;
    private final JavaMailSender emailSender;
    private List<String> blackListTaxIds;
    private RequestStatus cancellationResponse;

    @Autowired
    public InvioNotificheB2bSteps(PnExternalServiceClientImpl safeStorageClient,
                                  IPnSafeStoragePrivateClient safeStoragePrivateClient,
                                  SharedSteps sharedSteps,
                                  PnExternalChannelsServiceClientImpl pnExternalChannelsServiceClientImpl,
                                  JavaMailSender emailSender) {
        this.safeStorageClient = safeStorageClient;
        this.safeStoragePrivateClient = safeStoragePrivateClient;
        this.sharedSteps = sharedSteps;
//        this.b2bUtils = sharedSteps.getB2bUtils();
        this.b2bClient = sharedSteps.getB2bClient();
        this.webPaClient = sharedSteps.getWebPaClient();
        this.pnPaymentInfoClientImpl = sharedSteps.getPnPaymentInfoClientImpl();
        this.pnExternalChannelsServiceClientImpl = pnExternalChannelsServiceClientImpl;

        this.emailSender = emailSender;
    }

    @And("la notifica può essere correttamente recuperata dal sistema tramite codice IUN")
    public void notificationCanBeRetrievedWithIUN() {
        AtomicReference<FullSentNotificationV29> notificationByIun = new AtomicReference<>();
        notificationCanBeRetrievedWithIUN(notificationByIun, b2bClient::getSentNotificationV29);
    }

    @And("la notifica può essere correttamente recuperata dal sistema tramite codice IUN con OpenApi V1")
    public void notificationCanBeRetrievedWithIUNV1() {
        AtomicReference<FullSentNotification> notificationByIun = new AtomicReference<>();
        notificationCanBeRetrievedWithIUN(notificationByIun, b2bClient::getSentNotificationV1);
    }

    @And("la notifica può essere correttamente recuperata dal sistema tramite codice IUN con OpenApi V20")
    public void notificationCanBeRetrievedWithIUNV2() {
        AtomicReference<FullSentNotificationV20> notificationByIun = new AtomicReference<>();
        notificationCanBeRetrievedWithIUN(notificationByIun, b2bClient::getSentNotificationV2);
    }

    private <T> void notificationCanBeRetrievedWithIUN(AtomicReference<T> notificationByIun, Function<String, T> getNotificationByIunFunction) {
        try {
            String iun = sharedSteps.getNotificationIun();
            if (iun != null) {
                assertThatCode(() -> notificationByIun.set(getNotificationByIunFunction.apply(iun)))
                        .as("Il recupero della notifica con IUN '%s' non deve generare eccezioni", iun)
                        .doesNotThrowAnyException();
            } else {
                assertThat(notificationByIun.get())
                        .as("La notifica recuperata con IUN non deve essere nulla quando nessuna notifica inviata è disponibile")
                        .isNotNull();
            }
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @And("la notifica non può essere recuperata dal sistema tramite codice IUN con OpenApi V20 generando un errore")
    public void notificationCanBeRetrievedWithIUNV2Error() {
        try {
            b2bClient.getSentNotificationV2(sharedSteps.getNotificationIun());
        } catch (HttpStatusCodeException e) {
            sharedSteps.setNotificationError(e);
        }
    }

    @And("la notifica non può essere recuperata dal sistema tramite codice IUN con OpenApi V10 generando un errore")
    public void notificationCanBeRetrievedWithIUNV1Error() {
        try {
            b2bClient.getSentNotificationV1(sharedSteps.getNotificationIun());
        } catch (HttpStatusCodeException e) {
            sharedSteps.setNotificationError(e);
        }
    }

    @And("la notifica può essere correttamente recuperata dal sistema tramite codice IUN web PA")
    public void notificationCanBeRetrievedWithIUNWebPA() {
        AtomicReference<BffLegalNotificationsResponse> notificationByIun = new AtomicReference<>();

        assertThat(sharedSteps.getSentNotificationLastVersion())
                .as("La notifica inviata non deve essere nulla prima di recuperare il codice IUN")
                .isNotNull();

        String iun = sharedSteps.getNotificationIun();

        try {
            notificationByIun.set(
                    webPaClient.searchSentNotification(
                            OffsetDateTime.now().minusDays(1), OffsetDateTime.now(),
                            null, null, null, iun, 1, null
                    )
            );

            assertSoftly(softly -> softly.assertThat(notificationByIun.get())
                    .as("La notifica con IUN " + iun + "deve essere trovata nel sistema", iun)
                    .isNotNull());

        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    //TODO MATTEO: ho aggiunto il parametro PA e il successivo setting della pa in sharedSteps, senza andava in errore
    @And("{string} recupera notifica vecchia di 120 giorni da lato web PA e verifica presenza pagamento")
    public void retrieveNotification120DaysOldByIunWebPaSide(String paName) {
        sharedSteps.setPA(paName);
        List<BffLegalNotificationSearchRow> searchedNotifications = searchNotificationWebFromADate(OffsetDateTime.now().minusDays(120));
        FullSentNotificationV29 notifica120 = null;
        for (BffLegalNotificationSearchRow notifica : searchedNotifications) {
            FullSentNotificationV29 result = b2bClient.getSentNotificationV29(notifica.getIun());
            if (result.getRecipients().get(0).getPayments() != null
                    && result.getRecipients().get(0).getPayments().get(0).getPagoPa() != null
                    && result.getRecipients().get(0).getPayments().get(0).getPagoPa().getNoticeCode() != null) {
                notifica120 = result;
                break;
            }
            try {
                await().atMost(sharedSteps.getWorkFlowWait(), TimeUnit.MILLISECONDS);
            } catch (RuntimeException exc) {
                log.error(exc.getMessage());
                throw exc;
            }
        }
        try {
            assertThat(notifica120)
                    .as("La notifica dopo 120 giorni non deve essere nulla")
                    .isNotNull();
            log.info("notifica dopo 120gg: {}", notifica120);
            assertThat(notifica120.getRecipients().get(0).getPayments().get(0).getPagoPa().getAttachment())
                    .as("L'attachment del pagamento deve essere nullo")
                    .isNull();
            sharedSteps.setNotificationIun(notifica120.getIun());
        } catch (AssertionError assertionError) {
            String message = assertionError.getMessage() + "{notifica : " + (notifica120 == null ? "NULL" : notifica120) + " }";
            throw new AssertionError(message, assertionError);
        }
    }


    @And("recupero notifica del {string} lato web dalla PA {string} e verifica presenza pagamento per notifica che è arrivato fino al elemento {string} con feePolicy {string}")
    public void notificationFromADateCanBeRetrievedWithIUNWebPA(String stringDate, String paName, String type, String feePolicy) {
        sharedSteps.setPA(paName);

        LocalDate date = LocalDate.parse(stringDate);
        OffsetDateTime offsetDateTime = date.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime();

        List<BffLegalNotificationSearchRow> searchedNotifications = searchNotificationWebFromADate(offsetDateTime);
        FullSentNotificationV29 notifica = null;

        for (BffLegalNotificationSearchRow notifiche : searchedNotifications) {

            notifica = b2bClient.getSentNotificationV29(notifiche.getIun());

            if (!notifica.getRecipients().get(0).getPayments().isEmpty()
                    && notifica.getRecipients().get(0).getPayments() != null
                    && notifica.getRecipients().get(0).getPayments().get(0).getPagoPa() != null
                    && notifica.getTimeline().toString().contains(type)
                    && notifica.getNotificationFeePolicy().toString().equals(feePolicy)
                    && notifica.getPaFee() == null) {
                break;
            } else {
                notifica = null;
            }
            await().atMost(sharedSteps.getWorkFlowWait(), TimeUnit.MILLISECONDS);
        }

        try {
            assertThat(notifica)
                    .as("La notifica non deve essere nulla")
                    .isNotNull();

            log.info("notifica trovata: {}", notifica);
            notifica.setPaFee(100);
            notifica.setVat(22);
            sharedSteps.setNotificationIun(notifica.getIun());
        } catch (AssertionError assertionError) {
            String message = assertionError.getMessage() +
                    "{notifica : " + (notifica == null ? "NULL" : notifica) + " }";
            throw new AssertionError(message, assertionError);
        }
    }

    private List<BffLegalNotificationSearchRow> searchNotificationWebFromADate(OffsetDateTime data) {
        AtomicReference<BffLegalNotificationsResponse> notificationByIun = new AtomicReference<>();

        notificationByIun.set(Objects.requireNonNull(
                webPaClient.searchSentNotification(data, data.plusDays(20), null, null, null, null, 50, null),
                "Il risultato della ricerca delle notifiche inviate non deve essere nullo"
        ));

        assertSoftly(softly -> {
            softly.assertThat(notificationByIun.get())
                    .as("Verifica che la risposta della ricerca non sia nulla")
                    .isNotNull();

            if (notificationByIun.get() != null) {
                softly.assertThat(notificationByIun.get().getResultsPage())
                        .as("Verifica che la lista dei risultati non sia nulla")
                        .isNotNull();

                softly.assertThat(notificationByIun.get().getResultsPage())
                        .as("Verifica che la lista dei risultati non sia vuota")
                        .isNotEmpty();
            }
        });

        return notificationByIun.get() != null ? notificationByIun.get().getResultsPage() : Collections.emptyList();
    }

    @Then("la notifica può essere correttamente recuperata dal sistema tramite Stato {string} dalla web PA {string}")
    public void notificationCanBeRetrievedWithStatusByWebPA(String status, String paName) {
        sharedSteps.setPA(paName);

        NotificationStatusV26 notificationInternalStatus = switch (status) {
            case "ACCEPTED" -> NotificationStatusV26.ACCEPTED;
            case "DELIVERING" -> NotificationStatusV26.DELIVERING;
            case "DELIVERED" -> NotificationStatusV26.DELIVERED;
            case "CANCELLED" -> NotificationStatusV26.CANCELLED;
            case "EFFECTIVE_DATE" -> NotificationStatusV26.EFFECTIVE_DATE;
            case "REFUSED" -> NotificationStatusV26.REFUSED;
            default -> throw new IllegalArgumentException();
        };

        AtomicReference<BffLegalNotificationsResponse> notificationByIun = new AtomicReference<>();
        try {
            assertThatCode(() ->
                    notificationByIun.set(
                            webPaClient.searchSentNotification(
                                    OffsetDateTime.now().minusDays(1),
                                    OffsetDateTime.now(),
                                    null,
                                    notificationInternalStatus,
                                    null,
                                    null,
                                    1,
                                    null
                            )
                    )
            ).as("Errore durante il recupero della notifica con stato interno: %s", notificationInternalStatus)
                    .doesNotThrowAnyException();

            assertSoftly(softly -> softly.assertThat(notificationByIun.get())
                    .as("La notifica recuperata non deve essere nulla")
                    .isNotNull());
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @Then("la notifica viene recuperata dal sistema tramite codice IUN")
    public void laNotificaVieneRecuperataDalSistemaTramiteCodiceIUN() {
        try {
            sharedSteps.getSentNotificationLastVersion();
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @Given("viene effettuato il pre-caricamento di un documento")
    public void preLoadingOfDocument() {
        NotificationUtilsV24 notificationUtils = new NotificationUtilsV24(sharedSteps.getContext(), sharedSteps.getB2bClient(), sharedSteps.getPollingFactory());
        NotificationDocument notificationDocument = notificationUtils.newDocument("classpath:/sample.pdf");
        AtomicReference<NotificationDocument> notificationDocumentAtomic = new AtomicReference<>();
        assertThatCode(() -> notificationDocumentAtomic.set(notificationUtils.preloadDocument(notificationDocument)))
                .as("Il caricamento e l'assegnazione del documento di notifica non devono generare eccezioni")
                .doesNotThrowAnyException();

        this.notificationDocumentPreload = notificationDocumentAtomic.get();
    }

    @Given("viene effettuato il pre-caricamento di un allegato")
    public void preLoadingOfAttachment() {
        NotificationUtilsV24 notificationUtils = new NotificationUtilsV24(sharedSteps.getContext(), sharedSteps.getB2bClient(), sharedSteps.getPollingFactory());
        NotificationPaymentAttachment notificationPaymentAttachment = notificationUtils.newAttachment("classpath:/sample.pdf");
        AtomicReference<NotificationPaymentAttachment> notificationDocumentAtomic = new AtomicReference<>();
        assertThatCode(() -> notificationDocumentAtomic.set(notificationUtils.preloadAttachment(notificationPaymentAttachment)))
                .as("Il caricamento e l'assegnazione dell'allegato di notifica non devono generare eccezioni")
                .doesNotThrowAnyException();

        this.notificationPaymentAttachmentPreload = notificationDocumentAtomic.get();
    }

    @Given("viene effettuato il pre-caricamento dei metadati f24")
    public void preLoadingOfMetaDatiAttachmentF24() {
        NotificationUtilsV24 notificationUtils = new NotificationUtilsV24(sharedSteps.getContext(), sharedSteps.getB2bClient(), sharedSteps.getPollingFactory());
        NotificationMetadataAttachment notificationPaymentAttachment = notificationUtils.newMetadataAttachment("classpath:/METADATA_CORRETTO.json");
        AtomicReference<NotificationMetadataAttachment> notificationDocumentAtomic = new AtomicReference<>();
        assertThatCode(() -> notificationDocumentAtomic.set(notificationUtils.preloadMetadataAttachment(notificationPaymentAttachment)))
                .as("Il caricamento e l'assegnazione dei metadati dell'allegato di notifica non devono generare eccezioni")
                .doesNotThrowAnyException();
        try {
            Thread.sleep(sharedSteps.getWait());
        } catch (InterruptedException e) {
            log.error("Thread.sleep error retry");
            throw new RuntimeException(e);
        }
        notificationMetadataAttachment = notificationDocumentAtomic.get();
    }


    @Then("viene effettuato un controllo sulla durata della retention di {string} precaricato")
    public void retentionCheckPreload(String documentType) {
        String key = switch (documentType) {
            case "ATTO OPPONIBILE" -> notificationDocumentPreload.getRef().getKey();
            case "PAGOPA" -> notificationPaymentAttachmentPreload.getRef().getKey();
            case "F24" -> notificationMetadataAttachment.getRef().getKey();
            default -> throw new IllegalArgumentException();
        };
        assertThat(checkRetention(key, retentionTimePreLoad))
                .as("La verifica della retention per la chiave " + key + "  con il tempo di retention deve restituire true", key, retentionTimePreLoad)
                .isTrue();
    }

    @And("viene effettuato un controllo sulla durata della retention di {string}")
    public void retentionCheckLoad(String documentType) {
        FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        String key = switch (documentType) {
            case "ATTO OPPONIBILE" -> fullSentNotification.getDocuments().get(0).getRef().getKey();
            case "PAGOPA" ->
                    fullSentNotification.getRecipients().get(0).getPayments().get(0).getPagoPa().getAttachment().getRef().getKey();
            case "F24" ->
                    fullSentNotification.getRecipients().get(0).getPayments().get(0).getF24().getMetadataAttachment().getRef().getKey();
            default -> throw new IllegalArgumentException();
        };
        assertThat(checkRetention(key, retentionTimeLoad))
                .as("La verifica della retention per la chiave " + key + " con il tempo di retention deve restituire true", key, retentionTimeLoad)
                .isTrue();
    }

    @And("viene effettuato un controllo sulla durata della retention di {string} per l'elemento di timeline {string}")
    public void retentionCheckLoadForTimelineElement(String documentType, String timelineEventCategory, @Transpose DataTest dataFromTest) throws RuntimeException {
        TimelineElementV28 timelineElement = sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataFromTest);
        FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        if (documentType.equals("ATTACHMENTS")) {
            for (int i = 0; i < fullSentNotification.getDocuments().size(); i++) {
                String key = fullSentNotification.getDocuments().get(i).getRef().getKey();

                assertThat(checkRetention(key, retentionTimeLoad, timelineElement.getTimestamp()))
                        .as("La verifica della retention ha fallito per la chiave '%s'. retentionTimeLoad: %d, timelineElement.getTimestamp: %s",
                                key, retentionTimeLoad, timelineElement.getTimestamp())
                        .isTrue();
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    @And("viene effettuato un controllo sulla durata della retention del F24 di {string} per l'elemento di timeline {string}")
    public void retentionCheckLoadForTimelineElementF24(String documentType, String timelineEventCategory, @Transpose DataTest dataFromTest) throws RuntimeException {
        TimelineElementV28 timelineElement = sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataFromTest);
        if (documentType.equals("ATTACHMENTS")) {
            FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
            for (int i = 0; i < fullSentNotification.getRecipients().get(0).getPayments().size(); i++) {
                String key = fullSentNotification.getRecipients().get(0).getPayments().get(i).getF24().getMetadataAttachment().getRef().getKey();

                assertThat(timelineElement.getTimestamp()).as("Il timestamp dell'elemento della timeline non deve essere nullo").isNotNull();

                assertThat(checkRetention(key, retentionTimeLoad, timelineElement.getTimestamp()))
                        .as("La verifica della retention per la chiave con tempo di retention e il timestamp deve restituire true",
                                key, retentionTimeLoad, timelineElement.getTimestamp())
                        .isTrue();
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    @And("viene effettuato un controllo sul type zip attachment di {string} per l'elemento di timeline {string} con DOC {string}")
    public void attachmentCheckLoadForTimelineElementF24(String documentType, String timelineEventCategory, String doc, @Transpose DataTest dataFromTest) throws RuntimeException {
        TimelineElementV28 timelineElement = sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataFromTest);
        if (documentType.equals("ATTACHMENTS")) {
            assertSoftly(softly -> {
                softly.assertThat(timelineElement.getDetails().getAttachments())
                        .as("La lista degli allegati non deve essere nulla")
                        .isNotNull();

                softly.assertThat(timelineElement.getDetails().getAttachments().get(0).getDocumentType())
                        .as("Il tipo di documento dell'allegato deve essere uguale a '%s'", doc)
                        .isEqualToIgnoringCase(doc);

                softly.assertThat(timelineElement.getDetails().getAttachments().get(0).getUrl())
                        .as("L'URL dell'allegato deve contenere '.zip'")
                        .contains(".zip");
            });
        } else {
            throw new IllegalArgumentException();
        }
    }

    @And("viene effettuato un controllo sulla durata della retention del PAGOPA di {string} per l'elemento di timeline {string}")
    public void retentionCheckLoadForTimelineElementPAGOPA(String documentType, String timelineEventCategory, @Transpose DataTest dataFromTest) throws RuntimeException {
        TimelineElementV28 timelineElement = sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataFromTest);
        if (documentType.equals("ATTACHMENTS")) {
            FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
            for (int i = 0; i < fullSentNotification.getRecipients().get(0).getPayments().size(); i++) {
                String key = fullSentNotification.getRecipients().get(0).getPayments().get(i).getPagoPa().getAttachment().getRef().getKey();

                assertThat(checkRetention(key, retentionTimeLoad, timelineElement.getTimestamp()))
                        .as("La verifica della retention per la chiave con tempo di retention e il timestamp deve restituire true",
                                key, retentionTimeLoad, timelineElement.getTimestamp())
                        .isTrue();
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Given("viene letta la notifica {string} dal {string}")
    public void vieneLettaLaNotificaDal(String iun, String paName) {
        sharedSteps.setPA(paName);
        assertThat(sharedSteps.getSentNotificationLastVersionByIun(iun))
                .as("La FullSentNotification letta non dev'essere null")
                .isNotNull();
    }

    @When("si tenta il recupero della notifica dal sistema")
    public void retrievalAttemptedIUN() {
        try {
            sharedSteps.getSentNotificationLastVersion();
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @When("si tenta il recupero della notifica dal sistema tramite codice IUN {string}")
    public void retrievalAttemptedIUN(String iun) {
        getNotificationByIunVersioning(iun, MOST_RECENT);
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
                b2bClient.getSentNotificationV1(iun);
            } else if (version.equalsIgnoreCase("V2")) {
                b2bClient.getSentNotificationV2(iun);
            } else {
                b2bClient.getSentNotificationV26(iun);
            }
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @When("viene richiesto il download del documento {string}")
    public void documentDownload(String type) {
        getDownloadFile(type, sharedSteps.getNotificationIun(), 0);
    }

    @When("viene richiesto il download del documento {string} per il destinatario {int}")
    public void documentDownloadPerDestinatario(String type, int destinatario) {
        getDownloadFile(type, sharedSteps.getNotificationIun(), destinatario);
    }

    @When("viene richiesto il download del documento {string} inesistente")
    public void documentAbsentDownload(String type) {
        getDownloadFile(type, sharedSteps.getNotificationIun(), 0);
    }

    @When("viene richiesto il download del documento {string} inesistente per il destinatario {int}")
    public void documentAbsentDownload(String type, int destinatario) {
        getDownloadFile(type, sharedSteps.getNotificationIun(), destinatario);
    }

    private void getDownloadFile(String type, String iun, int destinatario) {
        try {

            if (type.equalsIgnoreCase("NOTIFICA")) {
                List<NotificationDocument> documents = sharedSteps.getSentNotificationLastVersion().getDocuments();
                this.downloadResponse = b2bClient
                        .getSentNotificationDocument(sharedSteps.getNotificationIun(), Integer.parseInt(documents.get(0).getDocIdx()));
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
            byte[] bytes = B2bUtils.downloadFile(this.downloadResponse.getUrl());
            this.sha256DocumentDownload = B2bUtils.computeSha256(new ByteArrayInputStream(bytes));
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @Then("il download si conclude correttamente")
    public void correctlyDownload() {
        assertThat(this.downloadResponse.getSha256())
                .as("L'hash SHA-256 del documento scaricato e diverso dal quello ricevuto in risposta", this.sha256DocumentDownload, this.downloadResponse.getSha256())
                .isEqualTo(this.sha256DocumentDownload);
    }

    @Then("l'operazione ha prodotto un errore con status code {string}")
    public void operationProducedAnError(String statusCode) {
        HttpStatusCodeException httpStatusCodeException = this.sharedSteps.consumeNotificationError();
        assertThat(httpStatusCodeException)
                .as("L'eccezione httpStatusCodeException non dovrebbe essere nulla")
                .isNotNull();

        assertThat(httpStatusCodeException.getStatusCode().toString().substring(0, 3))
                .as("Il codice di stato HTTP non corrisponde a quello atteso", statusCode, httpStatusCodeException.getStatusCode().toString().substring(0, 3))
                .isEqualTo(statusCode);
    }

    @Then("l'operazione ha prodotto un errore con status code {string} con messaggio di errore {string}")
    public void operationProducedAnErrorWithMessage(String statusCode, String errore) {
        HttpStatusCodeException httpStatusCodeException = this.sharedSteps.consumeNotificationError();

        assertSoftly(softly -> {

            softly.assertThat(httpStatusCodeException)
                    .as("Verifica che l' http status code dell'eccezione in SharedSteps non sia nulla")
                    .isNotNull();

            if (httpStatusCodeException != null) {
                softly.assertThat(httpStatusCodeException.getStatusCode().toString().substring(0, 3))
                        .as("Verifica che lo status code sia '%s'", statusCode)
                        .isEqualTo(statusCode);

                byte[] responseBody = httpStatusCodeException.getResponseBodyAsByteArray();
                String responseBodyText = new String(responseBody, StandardCharsets.UTF_8);

                softly.assertThat(responseBodyText)
                        .as("Verifica che il messaggio di errore contenga: '%s'", errore)
                        .contains(errore);
            }
        });
    }

    @Then("l'operazione non ha prodotto errori")
    public void operationProducedNotAnError() {
        HttpStatusCodeException httpStatusCodeException = this.sharedSteps.consumeNotificationError();
        assertThat(httpStatusCodeException)
                .as("Verifica che non siano stati generati errori HTTP")
                .isNull();
    }

    //TODO: inutilizzato, richiede un sacco di metodi di utility per nulla. Gli step che lo richiamavano sono commentati, valutare cancellazione
//    @Then("si verifica la corretta acquisizione della notifica con verifica sha256 dell'allegato di pagamento {string}")
//    public void correctAcquisitionNotificationVerifySha256AllegatiPagamento(String attachment) {
//        assertThatCode(() -> b2bUtils.verifyNotificationAndSha256AllegatiPagamento(sharedSteps.getSentNotificationLastVersion(), attachment))
//                .as("Verifica fallita per la notifica e l'hash SHA-256 dell'allegato di pagamento. Assicurati che non vengano sollevate eccezioni.", attachment)
//                .doesNotThrowAnyException();
//    }


    private boolean checkRetention(String fileKey, Integer retentionTime) {
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
            assertThat(this.sharedSteps.getSentNotificationLastVersion().getAmount())
                    .as("L'importo della notifica dovrebbe essere uguale a " + price, price)
                    .isEqualTo(price);
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @And("la notifica non può essere annullata dal sistema tramite codice IUN")
    public void notificationCaNotBeCanceledWithIUN() {
        try {
            cancellationResponse = sharedSteps.getB2bClient().notificationCancellation(sharedSteps.getNotificationIun());
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @And("si verifica che l'annullamento della notifica abbia prodotto una risposta con i seguenti dati")
    public void verifyCancellationResponse(DataTable dataTable) {
        assertThat(cancellationResponse).as("La risposta alla cancellazione della notifica non dovrebbe essere nulla").isNotNull();
        Map<String, String> inputParams = dataTable.asMap();
        List<StatusDetail> statusDetails = cancellationResponse.getDetails();
        assertThat(statusDetails).isNotNull();
        assertThat(cancellationResponse.getStatus()).isEqualTo(inputParams.get("status"));
        assertThat(statusDetails.get(0).getCode()).isEqualTo(inputParams.get("code"));
        assertThat(statusDetails.get(0).getLevel()).isEqualTo(inputParams.get("level"));
        assertThat(statusDetails.get(0).getDetail()).isEqualTo(inputParams.get("detail"));
    }

    //Annullamento Notifica
    @And("la notifica non può essere annullata dal sistema tramite codice IUN più volte")
    public void notificationNotCanBeCanceledWithIUN() {
        assertSoftly(softly -> softly.assertThatCode(() -> {
            RequestStatus resp = b2bClient.notificationCancellation(sharedSteps.getNotificationIun());
            softly.assertThat(resp)
                    .as("La risposta alla cancellazione della notifica non dovrebbe essere nulla")
                    .isNotNull();
            softly.assertThat(resp.getDetails())
                    .as("I dettagli della risposta non dovrebbero essere nulli")
                    .isNotNull();
            softly.assertThat(resp.getDetails())
                    .as("I dettagli della risposta non dovrebbero essere vuoti")
                    .isNotEmpty();
            softly.assertThat(resp.getDetails().get(0).getCode())
                    .as("Il codice nella risposta dovrebbe essere 'NOTIFICATION_ALREADY_CANCELLED'")
                    .isEqualToIgnoringCase("NOTIFICATION_ALREADY_CANCELLED");
        }).as("La cancellazione della notifica non dovrebbe generare eccezioni").doesNotThrowAnyException());
    }

    @Then("si verifica il corretto annullamento della notifica")
    public void correctCanceledNotification() {
        //Assertions.assertNull(assertionFailedError);
    }

    @And("l'avviso pagopa viene pagato correttamente su checkout")
    public void laNotificaVienePagatasuCheckout() {
        FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        NotificationPriceResponseV23 notificationPrice = this.b2bClient.getNotificationPriceV23(
                Objects.requireNonNull(Objects.requireNonNull(fullSentNotification.getRecipients().get(0).getPayments()).get(0).getPagoPa()).getCreditorTaxId(),
                Objects.requireNonNull(Objects.requireNonNull(fullSentNotification.getRecipients().get(0).getPayments()).get(0).getPagoPa()).getNoticeCode());

        BffPaymentRequest paymentRequest = getPaymentRequest(notificationPrice,
                Objects.requireNonNull(Objects.requireNonNull(fullSentNotification.getRecipients().get(0).getPayments()).get(0).getPagoPa()).getNoticeCode(),
                Objects.requireNonNull(Objects.requireNonNull(fullSentNotification.getRecipients().get(0).getPayments()).get(0).getPagoPa()).getCreditorTaxId(),
                "Test Automation",
                null,
                "Test Automation Desk",
                "https://api.uat.platform.pagopa.it");
        verifyCheckoutCart(paymentRequest, null);
    }

    @Then("verifica stato pagamento di una notifica creditorTaxID {string} noticeCode {string} con errore {string}")
    public void verificaStatoPagamentoNotifica(String creditorTaxID, String noticeCode, String errorCode) {
        verificaStatoPagamentoNotifica(null, errorCode, creditorTaxID, noticeCode);
    }

    @Then("verifica stato pagamento di una notifica con status {string}")
    public void verificaStatoPagamentoNotifica(String status) {
        verificaStatoPagamentoNotifica(status, null, null, null);
    }

    @And("l'avviso pagopa viene pagato correttamente su checkout con errore {string}")
    public void laNotificaVienePagataSuCheckoutError(String codiceErrore) {
        String noticeCode = sharedSteps.getRecipientNoticeCode(0, 0);
        String creditorTaxId = sharedSteps.getRecipientCreditorTaxId(0, 0);

        BffPaymentRequest paymentRequest = getPaymentRequest(null,
                Objects.requireNonNull(noticeCode),
                Objects.requireNonNull(creditorTaxId),
                "Test Automation",
                100,
                "Test Automation Desk",
                "https://api.uat.platform.pagopa.it");

        verifyCheckoutCart(paymentRequest, codiceErrore);
    }

    @And("l'avviso pagopa viene pagato correttamente su checkout creditorTaxID {string} noticeCode {string} con errore {string}")
    public void laNotificaVienePagataSuCheckoutError(String creditorTaxID, String noticeCode, String codiceErrore) {
        BffPaymentRequest paymentRequest = getPaymentRequest(null,
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
        AtomicReference<FullSentNotification> notificationByIun = new AtomicReference<>();
        String iun = sharedSteps.getNotificationIun();

        try {
            assertThatCode(() -> notificationByIun.set(b2bClient.getSentNotificationV1(iun)))
                    .as("La chiamata per ottenere la notifica tramite IUN non dovrebbe generare eccezioni")
                    .doesNotThrowAnyException();

            assertThat(notificationByIun.get())
                    .as("La notifica recuperata non dovrebbe essere nulla")
                    .isNotNull();

            assertThat(notificationByIun.get().getRecipients())
                    .as("I destinatari della notifica non dovrebbero essere nulli o vuoti")
                    .isNotEmpty();

            assertThat(notificationByIun.get().getRecipients().get(0).getPayment())
                    .as("Il pagamento del primo destinatario della notifica non dovrebbe essere nullo")
                    .isNotNull();

        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @And("la notifica a 2 avvisi di pagamento con OpenApi V2")
    public void notificationCanBeRetrievePaymentV2() {
        AtomicReference<FullSentNotificationV20> notificationByIun = new AtomicReference<>();
        String iun = sharedSteps.getNotificationIun();
        try {
            assertThatCode(() -> notificationByIun.set(b2bClient.getSentNotificationV2(iun)))
                    .as("La chiamata per ottenere la notifica tramite IUN non dovrebbe generare eccezioni")
                    .doesNotThrowAnyException();

            assertThat(notificationByIun.get().getRecipients().get(0).getPayment().getNoticeCode())
                    .as("Il codice della notifica del pagamento non dovrebbe essere nullo")
                    .isNotNull();

            assertThat(notificationByIun.get().getRecipients().get(0).getPayment().getNoticeCodeAlternative())
                    .as("Il codice alternativo della notifica del pagamento non dovrebbe essere nullo")
                    .isNotNull();

        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @And("la notifica a 1 avvisi di pagamento con OpenApi V1")
    public void notificationCanBeRetrievePayment1V1() {
        AtomicReference<FullSentNotification> notificationByIun = new AtomicReference<>();
        String iun = sharedSteps.getNotificationIun();
        try {
            assertThatCode(() -> notificationByIun.set(b2bClient.getSentNotificationV1(iun)))
                    .as("La chiamata per ottenere la notifica tramite IUN non dovrebbe generare eccezioni")
                    .doesNotThrowAnyException();

            assertThat(notificationByIun.get())
                    .as("La notifica recuperata non dovrebbe essere nulla")
                    .isNotNull();

            assertThat(notificationByIun.get().getRecipients().get(0).getPayment().getNoticeCode())
                    .as("Il codice della notifica del pagamento non dovrebbe essere nullo")
                    .isNotNull();

        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @And("Si effettua la chiamata su external-reg per ricevere l'url di checkout con noticeCode {string} e creditorTaxId {string}")
    public void siEffettuaLaChiamataSuExternalRegPerRicevereLUrlDiCheckoutConNoticeCodeECreditorTaxId(String noticeCode, String creditorTaxId) {
        PaymentInfoRequest paymentInfoRequest = new PaymentInfoRequest()
                .creditorTaxId(creditorTaxId)
                .noticeCode(noticeCode);

        List<BffPaymentInfoItem> getPaymentInfoV21 = Assertions.assertDoesNotThrow(() -> pnPaymentInfoClientImpl.getPaymentInfoV21(Collections.singletonList(paymentInfoRequest)));
        BffPaymentRequest paymentRequest = getPaymentRequest(null,
                noticeCode,
                creditorTaxId,
                "Test Automation",
                getPaymentInfoV21.get(0).getAmount(),
                "Test Automation Desk",
                "https://api.uat.platform.pagopa.it");

        System.out.println("COSTO NOTIFICA: " + getPaymentInfoV21.get(0).getAmount());

        verifyCheckoutCart(paymentRequest, null);
    }

    private void verifyCheckoutCart(BffPaymentRequest paymentRequest, String codiceErrore) {
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
        } catch (AssertionError assertionError) {
            String message = assertionError.getMessage() +
                    "{la posizione debitoria " + (paymentResponse == null ? "NULL" : paymentResponse.toString()) + " }";
            throw new AssertionError(message, assertionError);
        }
    }

    @Then("si verifica che il physicalAddress sia stato normalizzato correttamente con rimozione caratteri isoLatin1")
    public void controlloCampiAddressNormalizzatore() {
        String regex = "[{}-~¡-ÿ^]";
        String regexCaratteriA = "[æ]";

        FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();

        TimelineElementV28 timelineNormalizer = fullSentNotification.getTimeline().stream().filter(elem -> elem.getCategory().equals(TimelineElementCategoryV23.NORMALIZED_ADDRESS)).findAny().orElse(null);
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

            SoftAssertions softly = new SoftAssertions();

            softly.assertThat(newAddress.getAddress().toUpperCase())
                    .as("Confronto tra gli indirizzi normalizzati")
                    .isEqualTo(normalizedAddress.getAddress());

            softly.assertThat(newAddress.getMunicipality())
                    .as("Confronto tra i comuni normalizzati")
                    .isEqualTo(normalizedAddress.getMunicipality());

            softly.assertThat(newAddress.getMunicipalityDetails())
                    .as("Confronto tra i dettagli del comune normalizzati")
                    .isEqualTo(normalizedAddress.getMunicipalityDetails());

            softly.assertThat(newAddress.getProvince())
                    .as("Confronto tra le province normalizzate")
                    .isEqualTo(normalizedAddress.getProvince());

            softly.assertThat(newAddress.getZip())
                    .as("Confronto tra i CAP normalizzati")
                    .isEqualTo(normalizedAddress.getZip());

            softly.assertAll();


        } catch (AssertionError error) {
            sharedSteps.throwAssertionErrorWithIUN(error);
        }
    }

    private BffPaymentRequest getPaymentRequest(NotificationPriceResponseV23 notificationPrice, String noticeNumber, String fiscalCode, String companyName, Integer amount, String description, String returnUrl) {
        BffPaymentRequest paymentRequest = new BffPaymentRequest();
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


    @And("si verifica il contenuto degli attachment da inviare nella pec del destinatario {int} con {int} allegati")
    public void vieneVerificatoIDocumentiInviatiDellaPecDelDestinatarioConNumeroDiAllegati(Integer destinatario, Integer allegati) {
        try {
            this.documentiPec = pnExternalChannelsServiceClientImpl.getReceivedMessages(sharedSteps.getNotificationIun(), destinatario);
            Assertions.assertNotNull(documentiPec);

            log.info("documenti pec : {}", documentiPec);

            Assertions.assertEquals(allegati, documentiPec.get(0).getDigitalNotificationRequest().getAttachmentUrls().size());
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "Verifica Allegati pec in errore ";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }

    }

    @And("si verifica il contenuto degli attachment da inviare nella pec del destinatario {int} da {string}")
    public void vieneVerificatoIDocumentiInviatiDellaPecDelDestinatario(Integer destinatario, String basePath) {
        try {
            pnExternalChannelsServiceClientImpl.switchBasePath(basePath);
            this.documentiPec = pnExternalChannelsServiceClientImpl.getReceivedMessages(sharedSteps.getNotificationIun(), destinatario);
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
            FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
            //caricamento in Mappa di tutti i documenti della notifica
            for (NotificationDocument documentNotifica : fullSentNotification.getDocuments()) {
                sharedSteps.getMapAllegatiNotificaSha256().put(documentNotifica.getRef().getKey(), documentNotifica.getDigests().getSha256());
            }
            //caricamento in Mappa di tutti i documenti di pagamento della notifica
            for (NotificationPaymentItem documentPagamento : fullSentNotification.getRecipients().get(destinatario).getPayments()) {
                sharedSteps.getMapAllegatiNotificaSha256().put(documentPagamento.getPagoPa().getAttachment().getRef().getKey(), documentPagamento.getPagoPa().getAttachment().getDigests().getSha256());
            }

            Assertions.assertFalse(sharedSteps.getMapAllegatiNotificaSha256().isEmpty());

            boolean checkAllegati = true;
            for (ReceivedMessage documentPec : documentiPec) {
                for (String documentPecKey : documentPec.getDigitalNotificationRequest().getAttachmentUrls()) {
                    if (documentPecKey.contains(tipoAttachment)) {
                        PnExternalServiceClientImpl.SafeStorageResponse safeStorageResponse = safeStorageClient.safeStorageInfo(documentPecKey.substring(14));
                        assertSoftly(softly -> {

                            softly.assertThat(safeStorageResponse)
                                    .as("Il safeStorageResponse non dovrebbe essere nullo")
                                    .isNotNull();

                            softly.assertThat(safeStorageResponse.getChecksum())
                                    .as("Il checksum non dovrebbe essere nullo")
                                    .isNotNull();

                            softly.assertThat(sharedSteps.getMapAllegatiNotificaSha256().get(safeStorageResponse.getKey()))
                                    .as("Il valore per la chiave %s non dovrebbe essere nullo", safeStorageResponse.getKey())
                                    .isNotNull();
                        });
                        if (!safeStorageResponse.getChecksum().equals(sharedSteps.getMapAllegatiNotificaSha256().get(safeStorageResponse.getKey()))) {
                            checkAllegati = false;
                            break;
                        }
                    }
                }
            }
            Assertions.assertTrue(checkAllegati);

        } catch (AssertionError assertionError) {
            String message = assertionError.getMessage() +
                    " Verifica Allegati pec in errore.";
            throw new AssertionError(message, assertionError);
        }
    }

    @And("si verifica il contenuto della pec abbia {int} attachment di tipo {string}")
    public void presenzaAttachment(Integer numeroDocumenti, String tipologia) {
        int contoDocumento = 0;
        for (String attachmentUrl : documentiPec.get(0).getDigitalNotificationRequest().getAttachmentUrls()) {
            contoDocumento += attachmentUrl.contains(tipologia) ? 1 : 0;
        }
        try {
            assertThat(numeroDocumenti).as("Il numero di documenti non coincide col valore atteso").isEqualTo(contoDocumento);
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() + "Verifica Allegati pec in errore ";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @And("si verifica il contenuto degli attachments da inviare in via cartacea al destinatario {int} con {int} allegati")
    public void checkDocumentInviatiPaper(Integer destinatario, Integer allegati) {
        try {
            this.documentiPec = pnExternalChannelsServiceClientImpl.getReceivedMessagesAnalogico(sharedSteps.getNotificationIun(), destinatario);


            Assertions.assertNotNull(documentiPec, "La lista dei documenti PEC ricevuti è nulla o vuota per il destinatario " + destinatario + " l'API con Endpoint: /historical/received-message/" + sharedSteps.getNotificationIun() + "/" + destinatario + " Non ha restituito risultati");

            log.info("documenti analogici : {}", documentiPec);

            Assertions.assertEquals(allegati, documentiPec.get(0).getPaperEngageRequest().getAttachments().size(),
                    "Il numero di allegati ricevuti è diverso da quello atteso. Expected: " + allegati + ", Actual: " + documentiPec.get(0).getPaperEngageRequest().getAttachments().size());

        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() + " Verifica Allegati analogici in errore ";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @And("si verifica che il contenuto degli attachments da inviare in via cartacea abbia {int} attachment di tipo {string}")
    public void presenceAttachmentAnalogicFlow(Integer numeroDocumenti, String tipologia) {
        List<PaperEngageRequestAttachmentsInner> attachments = Optional.ofNullable(documentiPec.get(0))
                .map(ReceivedMessage::getPaperEngageRequest)
                .map(PaperEngageRequest::getAttachments)
                .orElse(List.of());

        long actualCount;
        if ("F24".equalsIgnoreCase(tipologia)) {
            actualCount = attachments.stream().filter(this::isF24).count();
        } else {
            actualCount = attachments.stream()
                    .map(PaperEngageRequestAttachmentsInner::getUri)
                    .filter(uri -> uri != null && uri.contains(tipologia))
                    .count();
        }

        try {
            Assertions.assertEquals(numeroDocumenti.longValue(), actualCount,
                    "Il numero di allegati di tipo '" + tipologia + "' è diverso da quello atteso. Expected: " + numeroDocumenti + ", Actual: " + actualCount);
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() + " - Verifica Allegati Cartacei in errore.";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    /**
     * Riconosce un allegato F24 con cascade a costo crescente:
     * 1) uri / documentType già presenti su PaperEngage
     * 2) metadati SafeStorage (key, documentType, tags, url) via getFile
     * 3) solo in ultima istanza download PDF e ricerca testuale di "F24"
     */
    private boolean isF24(PaperEngageRequestAttachmentsInner attachment) {
        if (attachment == null) {
            return false;
        }
        if (containsF24Hint(attachment.getUri(), attachment.getDocumentType())) {
            return true;
        }
        try {
            FileDownloadResponse fileInfo = getSafeStorageFileInfo(attachment.getUri());
            if (hasF24InSafeStorageMetadata(fileInfo)) {
                return true;
            }
            return isF24FromPdfContent(downloadFromSafeStorage(fileInfo, attachment.getUri()));
        } catch (Exception e) {
            log.warn("isF24: impossibile verificare allegato uri={}: {}", attachment.getUri(), e.getMessage());
            return false;
        }
    }

    private static boolean hasF24InSafeStorageMetadata(FileDownloadResponse fileInfo) {
        if (fileInfo == null) {
            return false;
        }
        if (containsF24Hint(fileInfo.getKey(), fileInfo.getDocumentType())) {
            return true;
        }
        if (fileInfo.getDownload() != null && containsF24Hint(fileInfo.getDownload().getUrl())) {
            return true;
        }
        if (fileInfo.getTags() != null) {
            for (Map.Entry<String, List<String>> entry : fileInfo.getTags().entrySet()) {
                if (containsF24Hint(entry.getKey())) {
                    return true;
                }
                if (entry.getValue() != null) {
                    for (String tagValue : entry.getValue()) {
                        if (containsF24Hint(tagValue)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private static boolean containsF24Hint(String... values) {
        if (values == null) {
            return false;
        }
        for (String value : values) {
            if (value != null && value.toUpperCase(Locale.ROOT).contains("F24")) {
                return true;
            }
        }
        return false;
    }

    // TODO: creare utility condivisa nel modulo common per estrazione/ricerca testo da PDF (PDFBox);
    //       oggi duplicato anche in LegalFactContentVerifySteps.checkTypeAAR e TemplateEngineSteps.isValidPdf
    private boolean isF24FromPdfContent(byte[] pdfContent) {
        if (pdfContent == null || pdfContent.length == 0) {
            return false;
        }
        try (PDDocument document = Loader.loadPDF(pdfContent)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            pdfStripper.setSortByPosition(true);
            String extractedText = pdfStripper.getText(document);
            return extractedText != null && extractedText.toUpperCase(Locale.ROOT).contains("F24");
        } catch (Exception exception) {
            log.warn("isF24FromPdfContent: errore parsing PDF: {}", exception.getMessage());
            return false;
        }
    }

    private FileDownloadResponse getSafeStorageFileInfo(String safeStorageUri) {
        String fileKey = toSafeStorageFileKey(safeStorageUri);
        ResponseEntity<FileDownloadResponse> responseEntity =
                safeStoragePrivateClient.getFileWithHttpInfo(fileKey, "pn-test", false, false);
        return responseEntity != null ? responseEntity.getBody() : null;
    }

    private static byte[] downloadFromSafeStorage(FileDownloadResponse fileInfo, String safeStorageUri) {
        String downloadUrl = fileInfo != null && fileInfo.getDownload() != null ? fileInfo.getDownload().getUrl() : null;
        if (downloadUrl == null || downloadUrl.isBlank()) {
            throw new IllegalStateException("Presigned URL assente per uri=" + safeStorageUri);
        }
        return B2bUtils.downloadFile(downloadUrl);
    }

    private static String toSafeStorageFileKey(String uri) {
        if (uri == null) {
            return null;
        }
        String fileKey = uri.startsWith("safestorage://") ? uri.substring("safestorage://".length()) : uri;
        int q = fileKey.indexOf('?');
        return q >= 0 ? fileKey.substring(0, q) : fileKey;
    }

    @And("si verifica che il {int} documento arrivato sia di tipo {string}")
    public void checkIndexedDocument(Integer documentIndex, String tipologia) {
        ReceivedMessage firstDocumentReceived = documentiPec.get(0);

        assertThat(firstDocumentReceived.getPaperEngageRequest())
                .as("Il PaperEngageRequest non dovrebbe essere nullo")
                .isNotNull();

        assertThat(firstDocumentReceived.getPaperEngageRequest().getAttachments())
                .as("Gli allegati del PaperEngageRequest non dovrebbero essere nulli")
                .isNotNull();

        assertThat(firstDocumentReceived.getPaperEngageRequest().getAttachments())
                .as("L'indice fornito è fuori dai limiti della lista degli allegati", documentIndex)
                .hasSizeGreaterThanOrEqualTo(documentIndex);

        assertThat(firstDocumentReceived.getPaperEngageRequest().getAttachments().get(documentIndex - 1).getDocumentType())
                .as("Il documento all'indice %d non è del tipo atteso: %s", documentIndex, tipologia)
                .isEqualTo(tipologia);

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
        BffPaymentRequest requestCheckout = creationPaymentRequest(dataCheckout);
        try {
            BffPaymentResponse responseCheckout = pnPaymentInfoClientImpl.checkoutCart(requestCheckout);

            assertThat(responseCheckout)
                    .as("Il responseCheckout non dovrebbe essere nullo")
                    .isNotNull();

            assertThat(responseCheckout.getCheckoutUrl())
                    .as("La checkoutUrl non dovrebbe essere nulla")
                    .isNotNull();

            log.info("response checkout: {}", responseCheckout);
        } catch (AssertionError error) {
            //TODO: qua catturiamo l'errore solo per rilanciarlo. Dobbiamo loggare qualcosa?
            throw error;
        }

    }

    @Given("si richiama checkout con restituzione errore")
    public void siRichiamaCheckoutConDatiConErrore(Map<String, String> dataCheckout) {
        BffPaymentRequest requestCheckout = creationPaymentRequest(dataCheckout);
        try {
            pnPaymentInfoClientImpl.checkoutCart(requestCheckout);
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    public BffPaymentRequest creationPaymentRequest(Map<String, String> dataCheckout) {
        FullSentNotificationV29 fullSentNotification = sharedSteps.getSentNotificationLastVersion();
        BffPaymentRequest requestCheckout = new BffPaymentRequest()
                .paymentNotice(new PaymentNotice()
                        .noticeNumber(dataCheckout.get("noticeCode") != null ? dataCheckout.get("noticeCode") :
                                fullSentNotification.getRecipients().get(0).getPayments().get(0).getPagoPa().getNoticeCode())
                        .fiscalCode(dataCheckout.get("fiscalCode") != null ? dataCheckout.get("fiscalCode") :
                                fullSentNotification.getRecipients().get(0).getPayments().get(0).getPagoPa().getCreditorTaxId())
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

        for (String attachmentUrl : getAttachmentListForTypeOfNotification(type)) {
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


    private List<String> getAttachmentListForTypeOfNotification(String type) {
        List<String> attachmentNotification = new ArrayList<>();
        switch (type.toLowerCase()) {
            case "analogico" -> {
                for (PaperEngageRequestAttachmentsInner attachment : documentiPec.get(0).getPaperEngageRequest().getAttachments()) {
                    attachmentNotification.add(attachment.getUri());
                }
            }
            case "digitale" ->
                    attachmentNotification = documentiPec.get(0).getDigitalNotificationRequest().getAttachmentUrls();
            default -> throw new IllegalConfigurationException("Invalid request type: " + type.toLowerCase());
        }
        return attachmentNotification;
    }

    private LegalFactDownloadMetadataResponse getLegalFactIdAAR(String aarType) {
        AtomicReference<LegalFactDownloadMetadataResponse> legalFactDownloadMetadataResponse = new AtomicReference<>();
        try {
            Thread.sleep(sharedSteps.getWait());
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }

        TimelineElementCategoryV28 timelineElementInternalCategory = TimelineElementCategoryV28.AAR_GENERATION;
        TimelineElementV28 timelineElement = null;

        for (TimelineElementV28 element : sharedSteps.getSentNotificationLastVersion().getTimeline()) {

            if (Objects.requireNonNull(element.getCategory().getValue()).equals(timelineElementInternalCategory.getValue())) {
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
                Assertions.assertDoesNotThrow(() -> legalFactDownloadMetadataResponse.set(
                        this.b2bClient.getDownloadLegalFact(sharedSteps.getNotificationIun(), finalKeySearch)));
            } catch (AssertionFailedError assertionFailedError) {
                sharedSteps.throwAssertionErrorWithIUN(assertionFailedError);
            }
        }
        return legalFactDownloadMetadataResponse.get();
    }

    @Then("download attestazione opponibile AAR e controllo del contenuto del file per verificare se il content-type è {string}")
    public void verificaContentTypeAttestazione(String contentType) {
        LegalFactDownloadMetadataResponse legalFactDownloadMetadataResponse = getLegalFactIdAAR("PN_AAR");
        Assertions.assertTrue(B2bUtils.downloadUrlAndCheckContent(legalFactDownloadMetadataResponse.getUrl(), contentType));
    }

    @When("invio una notifica ad ogni taxId della blackList e ricevo un errore {string} con con messaggio di errore {string}")
    public void invioUnaNotificaAdOgniTaxIdDellaBlackListERicevoUnErroreConConMessaggioDiErrore(String errorCode, String errorMessage) {
        blackListTaxIds.forEach(data -> {
            sharedSteps.resetNotificationRequest();
            HashMap<String, String> map = new HashMap<>();
            map.put("taxId", data);
            sharedSteps.addDestinatario(map);
            sharedSteps.laNotificaVieneInviataDallaPA(COMUNE_1);
            operationProducedAnErrorWithMessage(errorCode, errorMessage);
        });
    }

    @And("riprendo tutti i taxId presenti nella blacklist")
    public void riprendoTuttiITaxIdPresentiNellaBlacklist() {
        Assertions.assertNotNull(blackListTaxIdsProperties);
        blackListTaxIds = retrieveTaxIdsFromProperties();
        Assertions.assertNotNull(blackListTaxIds);
        Assertions.assertFalse(blackListTaxIds.isEmpty());
    }

    private List<String> retrieveTaxIdsFromProperties() {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = Assertions.assertDoesNotThrow(() -> objectMapper.readTree(blackListTaxIdsProperties));
        List<String> taxIds = new ArrayList<>();
        for (JsonNode node : rootNode) {
            taxIds.add(node.get("taxId").asText());
        }
        return taxIds;
    }

    private void verificaStatoPagamentoNotifica(String status, String errorCode, String creditorTaxId, String noticeCode) {
        if (creditorTaxId == null)
            creditorTaxId = sharedSteps.getCreditorTaxId(0);
        if (noticeCode == null)
            noticeCode = sharedSteps.getNoticeCode(0);

        List<PaymentInfoRequest> paymentInfoRequestList = new ArrayList<>();
        PaymentInfoRequest paymentInfoRequest = new PaymentInfoRequest()
                .creditorTaxId(creditorTaxId)
                .noticeCode(noticeCode);
        paymentInfoRequestList.add(paymentInfoRequest);

        log.info("Messaggio json da allegare: " + paymentInfoRequest);

        try {
            paymentInfoResponse = pnPaymentInfoClientImpl.getPaymentInfoV21(paymentInfoRequestList);
            log.info("Informazioni sullo stato del Pagamento: " + paymentInfoResponse);

            assertThat(paymentInfoResponse)
                    .as("La risposta del pagamento non dovrebbe essere nulla")
                    .isNotNull();

            if (status != null && errorCode == null) {
                assertThat(paymentInfoResponse.get(0).getStatus().getValue())
                        .as("Lo stato nella risposta dovrebbe essere uguale a " + status)
                        .isEqualToIgnoringCase(status);
            } else if (status == null && errorCode != null) {
                assertThat(paymentInfoResponse.get(0).getErrorCode())
                        .as("Il codice errore nella risposta dovrebbe essere uguale a " + errorCode)
                        .isEqualToIgnoringCase(errorCode);
            }
        } catch (AssertionError assertionError) {
            String message = assertionError.getMessage() +
                    "{Informazioni sullo stato del Pagamento: " + (paymentInfoResponse == null ? "NULL" : paymentInfoResponse) + " }";
            throw new AssertionError(message, assertionError);
        }
    }
}
