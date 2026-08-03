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
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.model.FileDownloadInfo;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.model.FileDownloadResponse;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.NotificationUtilsV24;
import it.pagopa.pn.cucumber.utils.DataTest;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
import java.util.stream.Collectors;

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
    @Value("${pn.safeStorage.apikey}")
    private String defaultSafeStorageApiKey;
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
            logPaperEngageAttachmentsDump("checkDocumentInviatiPaper");

            Assertions.assertEquals(allegati, documentiPec.get(0).getPaperEngageRequest().getAttachments().size(),
                    "Il numero di allegati ricevuti è diverso da quello atteso. Expected: " + allegati + ", Actual: " + documentiPec.get(0).getPaperEngageRequest().getAttachments().size());

        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() + " Verifica Allegati analogici in errore ";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @And("si verifica che il contenuto degli attachments da inviare in via cartacea abbia {int} attachment di tipo {string}")
    public void presenceAttachmentAnalogicFlow(Integer numeroDocumenti, String tipologia) {
        logPaperEngageAttachmentsDump("presenceAttachmentAnalogicFlow tipologia=" + tipologia);
        if ("F24".equalsIgnoreCase(tipologia)) {
            logPaperEngageDiagnosticContext();
            logNotificationPaymentsDiagnostic(0);
            logPaperEngageSafeStorageForAllAttachments();
            logF24ShaVsPaperEngage(0);
        }
        List<String> attachmentsUri = Optional.ofNullable(documentiPec.get(0))
                .map(ReceivedMessage::getPaperEngageRequest)
                .map(PaperEngageRequest::getAttachments)
                .orElse(List.of())
                .stream()
                .map(PaperEngageRequestAttachmentsInner::getUri)
                .filter(uri -> uri != null && uri.contains(tipologia))
                .toList();
        long matchDocumentType = Optional.ofNullable(documentiPec.get(0))
                .map(ReceivedMessage::getPaperEngageRequest)
                .map(PaperEngageRequest::getAttachments)
                .orElse(List.of())
                .stream()
                .filter(a -> a.getDocumentType() != null && a.getDocumentType().contains(tipologia))
                .count();
        log.info("PaperEngage filter by uri.contains('{}'): count={}; filter by documentType.contains('{}'): count={}",
                tipologia, attachmentsUri.size(), tipologia, matchDocumentType);
        try {
            Assertions.assertEquals(numeroDocumenti, attachmentsUri.size(),
                    "Il numero di allegati di tipo '" + tipologia + "' è diverso da quello atteso. Expected: " + numeroDocumenti + ", Actual: " + attachmentsUri.size());
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() + " - Verifica Allegati Cartacei in errore.";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    /** Dump diagnostico allegati cartacei (uri + documentType + sha256) — utile per review NRT. */
    private void logPaperEngageAttachmentsDump(String context) {
        if (documentiPec == null || documentiPec.isEmpty() || documentiPec.get(0).getPaperEngageRequest() == null) {
            log.warn("PaperEngage attachments dump [{}]: documentiPec/paperEngage assenti", context);
            return;
        }
        List<PaperEngageRequestAttachmentsInner> attachments = Optional.ofNullable(
                        documentiPec.get(0).getPaperEngageRequest().getAttachments())
                .orElse(List.of());
        Map<String, Long> byDocumentType = attachments.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getDocumentType() == null ? "null" : a.getDocumentType(),
                        Collectors.counting()));
        Map<String, Long> bySha256 = attachments.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getSha256() == null ? "null" : a.getSha256(),
                        Collectors.counting()));
        log.info("PaperEngage attachments dump [{}]: iun={}, total={}, byDocumentType={}, distinctSha256={}",
                context, sharedSteps.getNotificationIun(), attachments.size(), byDocumentType, bySha256.size());
        int i = 0;
        for (PaperEngageRequestAttachmentsInner attachment : attachments) {
            log.info("PaperEngage attachment[{}]: order={}, documentType={}, sha256={}, uri={}",
                    i++,
                    attachment.getOrder(),
                    attachment.getDocumentType(),
                    attachment.getSha256(),
                    attachment.getUri());
        }
    }

    /** Contesto plico + frequenza sha (diagnostica massiva QA-16429). */
    private void logPaperEngageDiagnosticContext() {
        if (documentiPec == null || documentiPec.isEmpty()) {
            log.warn("PaperEngage diagnostic context: documentiPec assenti");
            return;
        }
        ReceivedMessage msg = documentiPec.get(0);
        log.info("PaperEngage diagnostic ReceivedMessage FULL: {}", msg);
        if (msg.getPaperEngageRequest() != null) {
            log.info("PaperEngage diagnostic PaperEngageRequest FULL: {}", msg.getPaperEngageRequest());
        }
        List<PaperEngageRequestAttachmentsInner> attachments = Optional.ofNullable(msg.getPaperEngageRequest())
                .map(PaperEngageRequest::getAttachments)
                .orElse(List.of());
        Map<String, Long> bySha = attachments.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getSha256() == null ? "null" : a.getSha256(),
                        LinkedHashMap::new,
                        Collectors.counting()));
        log.info("PaperEngage diagnostic shaFrequency: {}", bySha);
        bySha.forEach((sha, count) -> {
            List<String> orders = attachments.stream()
                    .filter(a -> Objects.equals(sha, a.getSha256()))
                    .map(a -> String.valueOf(a.getOrder()))
                    .toList();
            log.info("PaperEngage diagnostic shaGroup: count={}, sha={}, orders={}", count, sha, orders);
        });
    }

    /** Dump payments delivery (F24 meta + pagoPa) per associare al plico. */
    private void logNotificationPaymentsDiagnostic(int recipientIdx) {
        String iun = sharedSteps.getNotificationIun();
        FullSentNotificationV29 sent = sharedSteps.getSentNotificationLastVersion();
        if (sent == null || sent.getRecipients() == null || sent.getRecipients().size() <= recipientIdx) {
            log.warn("Payments diagnostic: sent/recipient assenti iun={}", iun);
            return;
        }
        List<NotificationPaymentItem> payments = sent.getRecipients().get(recipientIdx).getPayments();
        log.info("Payments diagnostic START: iun={}, recipientIdx={}, paymentsSize={}",
                iun, recipientIdx, payments == null ? 0 : payments.size());
        if (payments == null) {
            return;
        }
        for (int i = 0; i < payments.size(); i++) {
            NotificationPaymentItem p = payments.get(i);
            String f24Title = null;
            Boolean f24ApplyCost = null;
            String f24MetaKey = null;
            String f24MetaVersion = null;
            String f24MetaSha = null;
            String f24MetaContentType = null;
            String pagoPaNotice = null;
            String pagoPaAttachKey = null;
            String pagoPaAttachSha = null;
            String pagoPaContentType = null;
            if (p.getF24() != null) {
                f24Title = p.getF24().getTitle();
                f24ApplyCost = p.getF24().getApplyCost();
                if (p.getF24().getMetadataAttachment() != null) {
                    f24MetaContentType = p.getF24().getMetadataAttachment().getContentType();
                    if (p.getF24().getMetadataAttachment().getDigests() != null) {
                        f24MetaSha = p.getF24().getMetadataAttachment().getDigests().getSha256();
                    }
                    if (p.getF24().getMetadataAttachment().getRef() != null) {
                        f24MetaKey = p.getF24().getMetadataAttachment().getRef().getKey();
                        f24MetaVersion = p.getF24().getMetadataAttachment().getRef().getVersionToken();
                    }
                }
            }
            if (p.getPagoPa() != null) {
                pagoPaNotice = p.getPagoPa().getNoticeCode();
                if (p.getPagoPa().getAttachment() != null) {
                    pagoPaContentType = p.getPagoPa().getAttachment().getContentType();
                    if (p.getPagoPa().getAttachment().getDigests() != null) {
                        pagoPaAttachSha = p.getPagoPa().getAttachment().getDigests().getSha256();
                    }
                    if (p.getPagoPa().getAttachment().getRef() != null) {
                        pagoPaAttachKey = p.getPagoPa().getAttachment().getRef().getKey();
                    }
                }
            }
            log.info("Payments diagnostic item: idx={}, hasF24={}, hasPagoPa={}, f24Title={}, f24ApplyCost={}, "
                            + "f24MetaKey={}, f24MetaVersion={}, f24MetaSha={}, f24MetaContentType={}, "
                            + "pagoPaNotice={}, pagoPaAttachKey={}, pagoPaAttachSha={}, pagoPaContentType={}, payment={}",
                    i, p.getF24() != null, p.getPagoPa() != null, f24Title, f24ApplyCost,
                    f24MetaKey, f24MetaVersion, f24MetaSha, f24MetaContentType,
                    pagoPaNotice, pagoPaAttachKey, pagoPaAttachSha, pagoPaContentType, p);
            if (f24MetaKey != null) {
                try {
                    PnExternalServiceClientImpl.SafeStorageResponse metaSs =
                            safeStorageClient.safeStorageInfo(toSafeStorageFileKey(f24MetaKey));
                    log.info("Payments diagnostic F24 meta SafeStorage: idx={}, key={}, ss={}",
                            i, f24MetaKey, metaSs);
                } catch (Exception e) {
                    log.warn("Payments diagnostic F24 meta SafeStorage FAILED: idx={}, key={}, error={}",
                            i, f24MetaKey, e.getMessage());
                }
            }
        }
        log.info("Payments diagnostic END: iun={}", iun);
    }

    private static final List<String> SAFE_STORAGE_GETFILE_CX_CANDIDATES = List.of(
            "pn-delivery-push",
            "pn-delivery",
            "pn-test",
            "pn-paper-channel",
            "pn-external-channels",
            "pn-service-desk"
    );

    /**
     * Diagnostica QA-16429 / PEC_4: per ogni allegato del plico usa
     * {@link IPnSafeStoragePrivateClient#getFileWithHttpInfo} (metadataOnly=false) con più cx-id,
     * come nei test SafeStorage che ottengono il presigned download.
     */
    private void logPaperEngageSafeStorageForAllAttachments() {
        List<PaperEngageRequestAttachmentsInner> attachments = Optional.ofNullable(documentiPec)
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0).getPaperEngageRequest())
                .map(PaperEngageRequest::getAttachments)
                .orElse(List.of());
        if (attachments.isEmpty()) {
            log.warn("PaperEngage SafeStorage dump: attachments assenti iun={}", sharedSteps.getNotificationIun());
            return;
        }

        Map<String, Long> okByCx = new LinkedHashMap<>();
        Map<String, Long> failByCx = new LinkedHashMap<>();
        Map<String, Long> bySsDocumentType = new LinkedHashMap<>();
        int attachmentsWithAnyOk = 0;
        int attachmentsWithDownloadUrl = 0;
        int f24HintCount = 0;

        log.info("PaperEngage SafeStorage getFile dump START: iun={}, attachments={}, cxCandidates={}, metadataOnly=false",
                sharedSteps.getNotificationIun(), attachments.size(), SAFE_STORAGE_GETFILE_CX_CANDIDATES);

        for (PaperEngageRequestAttachmentsInner attachment : attachments) {
            String uri = attachment.getUri();
            String fileKey = toSafeStorageFileKey(uri);
            boolean anyOk = false;
            FileDownloadResponse best = null;
            String bestCx = null;

            // baseline: vecchio helper (cx fisso pn-delivery-push + metadataOnly=true)
            try {
                PnExternalServiceClientImpl.SafeStorageResponse legacy = safeStorageClient.safeStorageInfo(fileKey);
                log.info("PaperEngage SafeStorage LEGACY safeStorageInfo: order={}, fileKey={}, ssFull={}",
                        attachment.getOrder(), fileKey, legacy);
            } catch (Exception e) {
                log.info("PaperEngage SafeStorage LEGACY safeStorageInfo FAILED: order={}, fileKey={}, error={}",
                        attachment.getOrder(), fileKey, e.getMessage());
            }

            for (String cxId : SAFE_STORAGE_GETFILE_CX_CANDIDATES) {
                try {
                    maybeSwitchSafeStorageApiKey(cxId);
                    ResponseEntity<FileDownloadResponse> responseEntity =
                            safeStoragePrivateClient.getFileWithHttpInfo(fileKey, cxId, false, false);
                    FileDownloadResponse body = responseEntity != null ? responseEntity.getBody() : null;
                    Integer httpStatus = responseEntity != null ? responseEntity.getStatusCodeValue() : null;
                    String downloadUrl = Optional.ofNullable(body)
                            .map(FileDownloadResponse::getDownload)
                            .map(FileDownloadInfo::getUrl)
                            .orElse(null);
                    Object retryAfter = Optional.ofNullable(body)
                            .map(FileDownloadResponse::getDownload)
                            .map(FileDownloadInfo::getRetryAfter)
                            .orElse(null);
                    String ssDocType = body != null ? body.getDocumentType() : null;
                    String ssChecksum = body != null ? body.getChecksum() : null;
                    boolean checksumMatchPaper = ssChecksum != null && ssChecksum.equals(attachment.getSha256());

                    okByCx.merge(cxId, 1L, Long::sum);
                    anyOk = true;
                    if (ssDocType != null) {
                        bySsDocumentType.merge(ssDocType, 1L, Long::sum);
                    }
                    if (downloadUrl != null && best == null) {
                        best = body;
                        bestCx = cxId;
                    }

                    log.info("PaperEngage SafeStorage getFile OK: order={}, fileKey={}, cxId={}, httpStatus={}, "
                                    + "ssDocType={}, ssDocStatus={}, ssChecksum={}, ssContentType={}, ssContentLength={}, "
                                    + "ssVersionId={}, ssRetentionUntil={}, ssTags={}, checksumMatchPaper={}, "
                                    + "hasDownloadUrl={}, downloadRetryAfter={}, urlPath={}, downloadUrl={}, bodyFull={}",
                            attachment.getOrder(),
                            fileKey,
                            cxId,
                            httpStatus,
                            ssDocType,
                            body != null ? body.getDocumentStatus() : null,
                            ssChecksum,
                            body != null ? body.getContentType() : null,
                            body != null ? body.getContentLength() : null,
                            body != null ? body.getVersionId() : null,
                            body != null ? body.getRetentionUntil() : null,
                            body != null ? body.getTags() : null,
                            checksumMatchPaper,
                            downloadUrl != null,
                            retryAfter,
                            extractUrlPath(downloadUrl),
                            downloadUrl,
                            body);
                } catch (Exception e) {
                    failByCx.merge(cxId, 1L, Long::sum);
                    String status = null;
                    if (e instanceof HttpStatusCodeException httpEx) {
                        status = String.valueOf(httpEx.getStatusCode().value());
                    }
                    log.info("PaperEngage SafeStorage getFile FAIL: order={}, fileKey={}, cxId={}, httpStatus={}, error={}",
                            attachment.getOrder(), fileKey, cxId, status, e.getMessage());
                }
            }

            if (anyOk) {
                attachmentsWithAnyOk++;
            }

            if (best != null && best.getDownload() != null && best.getDownload().getUrl() != null) {
                attachmentsWithDownloadUrl++;
                String downloadUrl = best.getDownload().getUrl();
                try {
                    DownloadBody downloaded = downloadWithHeaders(downloadUrl);
                    byte[] bytes = downloaded.bytes;
                    String computedSha = B2bUtils.computeSha256(new ByteArrayInputStream(bytes));
                    boolean pdfMagic = bytes.length >= 4
                            && bytes[0] == '%' && bytes[1] == 'P' && bytes[2] == 'D' && bytes[3] == 'F';
                    boolean contentHasF24 = bytesContainsAsciiIgnoreCase(bytes, "F24");
                    String contentAsciiHint = extractAsciiHints(bytes, List.of("F24", "f24", "PAGOPA", "pagoPa", "AAR"));
                    boolean f24Hint = containsIgnoreCase(fileKey, "F24")
                            || containsIgnoreCase(best.getDocumentType(), "F24")
                            || containsIgnoreCase(extractUrlPath(downloadUrl), "F24")
                            || containsIgnoreCase(downloaded.probe.contentDisposition, "F24")
                            || containsIgnoreCase(downloadUrl, "F24")
                            || containsIgnoreCase(downloaded.probe.allHeaders, "F24")
                            || contentHasF24;
                    if (f24Hint) {
                        f24HintCount++;
                    }
                    log.info("PaperEngage SafeStorage getFile BODY: order={}, bestCx={}, paperSha={}, "
                                    + "ssDocType={}, ssChecksum={}, bytesLen={}, computedSha={}, computedMatchPaper={}, "
                                    + "pdfMagic={}, contentHasF24={}, contentAsciiHint={}, f24Hint={}, "
                                    + "httpStatus={}, contentDisposition={}, httpHeaders={}",
                            attachment.getOrder(),
                            bestCx,
                            attachment.getSha256(),
                            best.getDocumentType(),
                            best.getChecksum(),
                            bytes.length,
                            computedSha,
                            Objects.equals(computedSha, attachment.getSha256()),
                            pdfMagic,
                            contentHasF24,
                            contentAsciiHint,
                            f24Hint,
                            downloaded.probe.httpStatus,
                            downloaded.probe.contentDisposition,
                            downloaded.probe.allHeaders);
                } catch (Exception downloadEx) {
                    log.warn("PaperEngage SafeStorage getFile BODY FAILED: order={}, bestCx={}, error={}",
                            attachment.getOrder(), bestCx, downloadEx.getMessage());
                }
            } else {
                log.info("PaperEngage SafeStorage getFile BODY SKIP: order={}, fileKey={}, reason=no-download-url-from-any-cx",
                        attachment.getOrder(), fileKey);
            }
        }

        log.info("PaperEngage SafeStorage getFile dump SUMMARY: iun={}, attachments={}, attachmentsWithAnyOk={}, "
                        + "attachmentsWithDownloadUrl={}, f24HintCount={}, okByCx={}, failByCx={}, bySsDocumentType={}",
                sharedSteps.getNotificationIun(),
                attachments.size(),
                attachmentsWithAnyOk,
                attachmentsWithDownloadUrl,
                f24HintCount,
                okByCx,
                failByCx,
                bySsDocumentType);
    }

    private void maybeSwitchSafeStorageApiKey(String cxId) {
        // Allineato a SafeStorageSteps: per pn-delivery si usa la api-key dedicata.
        if ("pn-delivery".equalsIgnoreCase(cxId)) {
            safeStoragePrivateClient.setApiKey("pn-delivery_api_key");
        } else if (defaultSafeStorageApiKey != null) {
            safeStoragePrivateClient.setApiKey(defaultSafeStorageApiKey);
        }
    }

    private static String toSafeStorageFileKey(String uri) {
        if (uri == null) {
            return null;
        }
        String fileKey = uri.startsWith("safestorage://") ? uri.substring("safestorage://".length()) : uri;
        int q = fileKey.indexOf('?');
        return q >= 0 ? fileKey.substring(0, q) : fileKey;
    }

    private static String extractUrlPath(String downloadUrl) {
        if (downloadUrl == null) {
            return null;
        }
        try {
            java.net.URI u = java.net.URI.create(downloadUrl);
            return u.getPath();
        } catch (Exception e) {
            int q = downloadUrl.indexOf('?');
            return q >= 0 ? downloadUrl.substring(0, q) : downloadUrl;
        }
    }

    private static final class DownloadProbe {
        final Integer httpStatus;
        final String contentType;
        final String contentLength;
        final String contentDisposition;
        final String allHeaders;

        DownloadProbe(Integer httpStatus, String contentType, String contentLength,
                      String contentDisposition, String allHeaders) {
            this.httpStatus = httpStatus;
            this.contentType = contentType;
            this.contentLength = contentLength;
            this.contentDisposition = contentDisposition;
            this.allHeaders = allHeaders;
        }
    }

    private static final class DownloadBody {
        final byte[] bytes;
        final DownloadProbe probe;

        DownloadBody(byte[] bytes, DownloadProbe probe) {
            this.bytes = bytes;
            this.probe = probe;
        }
    }

    private static DownloadBody downloadWithHeaders(String downloadUrl) throws java.io.IOException {
        java.net.HttpURLConnection conn = null;
        try {
            conn = openDownloadConnection(downloadUrl, "GET", false);
            int status = conn.getResponseCode();
            Map<String, List<String>> headerFields = conn.getHeaderFields();
            StringBuilder headers = new StringBuilder();
            if (headerFields != null) {
                for (Map.Entry<String, List<String>> e : headerFields.entrySet()) {
                    if (e.getKey() == null) {
                        continue;
                    }
                    headers.append(e.getKey()).append('=').append(e.getValue()).append("; ");
                }
            }
            DownloadProbe probe = new DownloadProbe(
                    status,
                    conn.getContentType(),
                    conn.getHeaderField("Content-Length"),
                    conn.getHeaderField("Content-Disposition"),
                    headers.toString());
            try (java.io.InputStream in = status >= 400 ? conn.getErrorStream() : conn.getInputStream()) {
                if (in == null) {
                    return new DownloadBody(new byte[0], probe);
                }
                return new DownloadBody(in.readAllBytes(), probe);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static java.net.HttpURLConnection openDownloadConnection(String downloadUrl, String method, boolean range)
            throws java.io.IOException {
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(downloadUrl).openConnection();
        conn.setInstanceFollowRedirects(true);
        conn.setConnectTimeout(15_000);
        conn.setReadTimeout(60_000);
        conn.setRequestMethod(method);
        if (range) {
            conn.setRequestProperty("Range", "bytes=0-0");
        }
        conn.connect();
        return conn;
    }

    private static boolean bytesContainsAsciiIgnoreCase(byte[] bytes, String token) {
        if (bytes == null || token == null || token.isEmpty()) {
            return false;
        }
        String lower = new String(bytes, StandardCharsets.ISO_8859_1).toLowerCase(Locale.ROOT);
        return lower.contains(token.toLowerCase(Locale.ROOT));
    }

    private static String extractAsciiHints(byte[] bytes, List<String> tokens) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        String ascii = new String(bytes, StandardCharsets.ISO_8859_1);
        String lower = ascii.toLowerCase(Locale.ROOT);
        List<String> found = new ArrayList<>();
        for (String token : tokens) {
            if (token != null && lower.contains(token.toLowerCase(Locale.ROOT))) {
                found.add(token);
            }
        }
        return String.join(",", found);
    }

    private static boolean containsIgnoreCase(String value, String token) {
        return value != null && token != null && value.toLowerCase(Locale.ROOT).contains(token.toLowerCase(Locale.ROOT));
    }

    /**
     * Diagnostica QA-16429 / PEC_4: confronta SHA PDF F24 (download B2B per attachmentIdx)
     * con gli sha256 del PaperEngage. Il digests dei metadata F24 NON deve matchare il plico.
     */
    private void logF24ShaVsPaperEngage(int recipientIdx) {
        String iun = sharedSteps.getNotificationIun();
        FullSentNotificationV29 sent = sharedSteps.getSentNotificationLastVersion();
        if (sent == null || sent.getRecipients() == null || sent.getRecipients().size() <= recipientIdx) {
            log.warn("F24 sha compare: sent notification/recipient assenti iun={}", iun);
            return;
        }
        List<NotificationPaymentItem> payments = sent.getRecipients().get(recipientIdx).getPayments();
        if (payments == null || payments.isEmpty()) {
            log.warn("F24 sha compare: payments assenti iun={} recipient={}", iun, recipientIdx);
            return;
        }

        Set<String> metaShas = new LinkedHashSet<>();
        Set<String> apiPdfShas = new LinkedHashSet<>();
        Set<String> computedPdfShas = new LinkedHashSet<>();
        int f24PaymentCount = 0;
        int downloadOk = 0;
        int downloadFail = 0;

        for (int attachmentIdx = 0; attachmentIdx < payments.size(); attachmentIdx++) {
            NotificationPaymentItem payment = payments.get(attachmentIdx);
            if (payment.getF24() == null) {
                continue;
            }
            f24PaymentCount++;
            if (payment.getF24().getMetadataAttachment() != null
                    && payment.getF24().getMetadataAttachment().getDigests() != null
                    && payment.getF24().getMetadataAttachment().getDigests().getSha256() != null) {
                metaShas.add(payment.getF24().getMetadataAttachment().getDigests().getSha256());
            }
            try {
                NotificationAttachmentDownloadMetadataResponse resp =
                        downloadF24AttachmentWithRetry(iun, recipientIdx, attachmentIdx);
                if (resp == null) {
                    downloadFail++;
                    log.warn("F24 sha compare: resp null iun={} attachmentIdx={}", iun, attachmentIdx);
                    continue;
                }
                if (resp.getSha256() != null) {
                    apiPdfShas.add(resp.getSha256());
                }
                DownloadProbe probe = new DownloadProbe(null, null, null, null, null);
                log.info("F24 sha compare download metadata: attachmentIdx={}, respFull={}, filename={}, "
                                + "apiSha={}, url={}, contentLength={}, contentType={}, retryAfter={}",
                        attachmentIdx, resp, resp.getFilename(), resp.getSha256(), resp.getUrl(),
                        resp.getContentLength(), resp.getContentType(), resp.getRetryAfter());
                if (resp.getUrl() != null) {
                    DownloadBody body = downloadWithHeaders(resp.getUrl());
                    probe = body.probe;
                    byte[] bytes = body.bytes;
                    String computed = B2bUtils.computeSha256(new ByteArrayInputStream(bytes));
                    computedPdfShas.add(computed);
                    boolean contentHasF24 = bytesContainsAsciiIgnoreCase(bytes, "F24");
                    String contentAsciiHint = extractAsciiHints(bytes, List.of("F24", "f24", "PAGOPA", "pagoPa"));
                    boolean pdfMagic = bytes.length >= 4
                            && bytes[0] == '%' && bytes[1] == 'P' && bytes[2] == 'D' && bytes[3] == 'F';
                    log.info("F24 sha compare download body: attachmentIdx={}, apiSha={}, computedSha={}, "
                                    + "matchApiComputed={}, bytesLen={}, pdfMagic={}, contentHasF24={}, "
                                    + "contentAsciiHint={}, filename={}, httpStatus={}, httpHeaders={}, contentDisposition={}",
                            attachmentIdx, resp.getSha256(), computed,
                            Objects.equals(resp.getSha256(), computed), bytes.length, pdfMagic,
                            contentHasF24, contentAsciiHint, resp.getFilename(),
                            probe.httpStatus, probe.allHeaders, probe.contentDisposition);
                } else {
                    log.info("F24 sha compare download: attachmentIdx={}, apiSha={}, url=null, retryAfter={}",
                            attachmentIdx, resp.getSha256(), resp.getRetryAfter());
                }
                downloadOk++;
            } catch (Exception e) {
                downloadFail++;
                log.warn("F24 sha compare: download failed iun={} attachmentIdx={}: {}",
                        iun, attachmentIdx, e.getMessage());
            }
        }

        List<PaperEngageRequestAttachmentsInner> paperAttachments = Optional.ofNullable(documentiPec)
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0).getPaperEngageRequest())
                .map(PaperEngageRequest::getAttachments)
                .orElse(List.of());

        Set<String> paperShas = paperAttachments.stream()
                .map(PaperEngageRequestAttachmentsInner::getSha256)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        long paperMatchApiPdf = paperAttachments.stream()
                .filter(a -> a.getSha256() != null && apiPdfShas.contains(a.getSha256()))
                .count();
        long paperMatchComputedPdf = paperAttachments.stream()
                .filter(a -> a.getSha256() != null && computedPdfShas.contains(a.getSha256()))
                .count();
        long paperMatchMeta = paperAttachments.stream()
                .filter(a -> a.getSha256() != null && metaShas.contains(a.getSha256()))
                .count();

        Set<String> apiInPaper = apiPdfShas.stream().filter(paperShas::contains).collect(Collectors.toCollection(LinkedHashSet::new));
        Set<String> computedInPaper = computedPdfShas.stream().filter(paperShas::contains).collect(Collectors.toCollection(LinkedHashSet::new));

        log.info("F24 sha compare SUMMARY: iun={}, f24Payments={}, downloadOk={}, downloadFail={}, "
                        + "distinctMetaSha={}, distinctApiPdfSha={}, distinctComputedPdfSha={}, "
                        + "paperAttachments={}, paperMatchApiPdf={}, paperMatchComputedPdf={}, paperMatchMeta={}, "
                        + "apiPdfShaIntersectPaper={}, computedPdfShaIntersectPaper={}",
                iun, f24PaymentCount, downloadOk, downloadFail,
                metaShas.size(), apiPdfShas.size(), computedPdfShas.size(),
                paperAttachments.size(), paperMatchApiPdf, paperMatchComputedPdf, paperMatchMeta,
                apiInPaper, computedInPaper);
        log.info("F24 sha compare SETS: metaShas={}, apiPdfShas={}, computedPdfShas={}",
                metaShas, apiPdfShas, computedPdfShas);
    }

    private NotificationAttachmentDownloadMetadataResponse downloadF24AttachmentWithRetry(
            String iun, int recipientIdx, int attachmentIdx) throws InterruptedException {
        NotificationAttachmentDownloadMetadataResponse resp =
                b2bClient.getSentNotificationAttachment(iun, recipientIdx, "F24", attachmentIdx);
        if (resp != null && resp.getRetryAfter() != null && resp.getRetryAfter() > 0) {
            Thread.sleep(resp.getRetryAfter() * 3L);
            resp = b2bClient.getSentNotificationAttachment(iun, recipientIdx, "F24", attachmentIdx);
        }
        return resp;
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
