package it.pagopa.pn.cucumber.steps.recipient;

import io.cucumber.java.Before;
import io.cucumber.java.DataTableType;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffFullNotificationV1;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffNotificationDetailTimeline;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.tos.privacy.BffConsent;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.tos.privacy.BffTosPrivacyActionBody;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.tos.privacy.Consent;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.tos.privacy.ConsentType;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.config.PnB2bClientTimingConfigs;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV23;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV25;
import it.pagopa.pn.client.b2b.pa.service.*;
import it.pagopa.pn.client.b2b.pa.service.impl.*;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.model.*;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.consents.model.ConsentAction;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.*;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.v25.model.DocumentCategory;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.utils.DataTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.awaitility.Awaitility.await;

@Slf4j
public class RicezioneNotificheWebSteps {
    private final ApplicationContext context;
    private IPnWebRecipientClient webRecipientClient;
    private IPnWebUserAttributesClient iPnWebUserAttributesClient;
    private final PnPaB2bUtils b2bUtils;
    private final IPnPaB2bClient b2bClient;
    private final PnExternalServiceClientImpl externalClient;
    private final SharedSteps sharedSteps;
    private final IPnWebPaClient webPaClient;
    private final IPnBFFRecipientNotificationClient bffRecipientNotificationClient;
    private final IPnTosPrivacyClient iPnTosPrivacyClient;
    private final PnB2bClientTimingConfigs timingConfigs;
    private static final Integer waitDefault = 10000;

    private HttpStatusCodeException notificationError;
    private FullReceivedNotificationV24 fullNotification;
    private BffFullNotificationV1 bffFullNotificationV1Recipient;
    private it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffFullNotificationV1 bffFullNotificationV1Sender;

    private static final String TOS_VERSION = "2";
    private static final String ACCEPT_TOS = "ACCETTA";

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

    @Before("@useB2B")
    public void beforeMethod() {
        this.iPnWebUserAttributesClient = context.getBean(B2BUserAttributesExternalClientImpl.class);
        if (!(webRecipientClient instanceof B2BRecipientExternalClientImpl)) {
            this.webRecipientClient = context.getBean(B2BRecipientExternalClientImpl.class);
            sharedSteps.setWebRecipientClient(webRecipientClient);
        }
    }

    @Autowired
    public RicezioneNotificheWebSteps(ApplicationContext context, SharedSteps sharedSteps, PnWebUserAttributesExternalClientImpl iPnWebUserAttributesClient,
                                      IPnBFFRecipientNotificationClient bffRecipientNotificationClient, IPnTosPrivacyClient iPnTosPrivacyClient, PnB2bClientTimingConfigs timingConfigs) {
        this.context = context;
        this.sharedSteps = sharedSteps;
        this.webRecipientClient = sharedSteps.getWebRecipientClient();
        this.b2bUtils = sharedSteps.getB2bUtils();
        this.b2bClient = sharedSteps.getB2bClient();
        this.iPnWebUserAttributesClient = iPnWebUserAttributesClient;
        this.webPaClient = sharedSteps.getWebPaClient();
        this.externalClient = sharedSteps.getPnExternalServiceClient();
        this.bffRecipientNotificationClient = bffRecipientNotificationClient;
        this.iPnTosPrivacyClient = iPnTosPrivacyClient;
        this.timingConfigs = timingConfigs;
    }

    @Then("la notifica può essere correttamente recuperata da {string}")
    public void notificationCanBeCorrectlyReadby(String recipient) {
        sharedSteps.selectUser(recipient);
        Assertions.assertDoesNotThrow(() -> {
            this.fullNotification = webRecipientClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(), null);
            log.info("timeline received: " + fullNotification.getTimeline());
        });
    }

    @And("lato destinatario vengono letti i dettagli della notifica lato web dal destinatario {string}")
    public void latoDestinatarioVengonoLettiIDettagliDellaNotificaLatoWeb(String user) {
        selectUser(user);
        bffFullNotificationV1Recipient =
                Assertions.assertDoesNotThrow(() ->
                        bffRecipientNotificationClient.getReceivedNotificationV1WithHttpInfoForRecipient(sharedSteps.getSentNotification().getIun())
                                .getBody());
        Assertions.assertNotNull(bffFullNotificationV1Recipient);
        log.info("FULL TIMELINE RECIPIENT: " + bffFullNotificationV1Recipient.getTimeline());
    }

    @And("lato api l'elemento di timeline della notifica {string} con deliveryDetailCode {string} non è visibile")
    public void timelineEventWithCategoryAndDeliveryDetailCodeNotPresent(String category, String deliveryDetailCode) {

        Assertions.assertNull(getTimelineElementV23(category, deliveryDetailCode));
    }

    @And("lato api l'elemento di timeline della notifica {string} con deliveryDetailCode {string} è visibile")
    public void timelineEventWithCategoryAndDeliveryDetailCodePresent(String category, String deliveryDetailCode) {
        Assertions.assertNotNull(getTimelineElementV23(category, deliveryDetailCode));
    }

    private TimelineElementV25 getTimelineElementV23(String category, String deliveryDetailCode) {
        fullNotification.getTimeline().forEach(x -> log.info(x.toString()));
        return fullNotification.getTimeline().stream()
                .filter(x -> x.getCategory().toString().equals(category) &&
                        x.getDetails() != null &&
                        x.getDetails().getDeliveryDetailCode().equals(deliveryDetailCode))
                .findFirst().orElse(null);
    }

    @And("lato destinatario dal web l'elemento di timeline della notifica {string} con deliveryDetailCode {string} è visibile")
    public void latoDestinatarioDalWebLElementoDiTimelineDellaNotificaConDeliveryDetailCodeÈVisibile(String category, String deliveryDetailCode) {
        Optional<BffNotificationDetailTimeline> dato = getNotificationDetailTimeline(category, deliveryDetailCode);
        Assertions.assertFalse(dato.isEmpty());
        Assertions.assertFalse(dato.get().getHidden());
    }

    @And("lato destinatario dal web l'elemento di timeline della notifica {string} con deliveryDetailCode {string} non è visibile")
    public void latoDestinatarioDalWebLElementoDiTimelineDellaNotificaConDeliveryDetailCodeNonÈVisibile(String category, String deliveryDetailCode) {
        Optional<BffNotificationDetailTimeline> dato = getNotificationDetailTimeline(category, deliveryDetailCode);
        Assertions.assertFalse(dato.isEmpty());
        Assertions.assertTrue(dato.get().getHidden());
    }

    @And("lato destinatario dal web l'elemento di timeline della notifica {string} con deliveryDetailCode {string} non è presente")
    public void latoDestinatarioDalWebLElementoDiTimelineDellaNotificaConDeliveryDetailCodeNonÈPresente(String category, String deliveryDetailCode) {
        Optional<BffNotificationDetailTimeline> dato = getNotificationDetailTimeline(category, deliveryDetailCode);
        Assertions.assertTrue(dato.isEmpty());
    }

    private Optional<BffNotificationDetailTimeline> getNotificationDetailTimeline(String category, String deliveryDetailCode) {
        return bffFullNotificationV1Recipient
                .getTimeline()
                .stream()
                .filter(Objects::nonNull)
                .filter(data ->
                        data.getElementId().contains(category) && data.getDetails() != null &&
                                data.getDetails().getDeliveryDetailCode() != null &&
                                data.getDetails().getDeliveryDetailCode().equals(deliveryDetailCode))
                .findFirst();
    }

    @And("lato mittente vengono letti i dettagli della notifica lato web {string}")
    public void latoMittenteVengonoLettiIDettagliDellaNotificaLatoWebDalDestinatario(String sender) {
        selectPa(sender);
        bffFullNotificationV1Sender = Assertions.assertDoesNotThrow(() ->
                bffRecipientNotificationClient.getSentNotificationV1WithHttpInfoForSender(sharedSteps.getSentNotification().getIun())
                        .getBody());
        Assertions.assertNotNull(bffFullNotificationV1Sender);
        log.info("FULL TIMELINE SENDER: " + bffFullNotificationV1Sender.getTimeline());
    }

    @And("lato mittente dal web l'elemento di timeline della notifica {string} con deliveryDetailCode {string} è visibile")
    public void latoMittenteDalWebLElementoDiTimelineDellaNotificaConDeliveryDetailCodeÈVisibile(String category, String deliveryDetailCode) {
        Optional<it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffNotificationDetailTimeline> dato = getNotificationDetailTimelineSender(category, deliveryDetailCode);
        Assertions.assertFalse(dato.isEmpty());
        Assertions.assertFalse(dato.get().getHidden());
    }

    @And("lato mittente dal web l'elemento di timeline della notifica {string} con deliveryDetailCode {string} non è visibile")
    public void latoMittenteDalWebLElementoDiTimelineDellaNotificaConDeliveryDetailCodeNonÈVisibile(String category, String deliveryDetailCode) {
        Optional<it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffNotificationDetailTimeline> dato = getNotificationDetailTimelineSender(category, deliveryDetailCode);
        Assertions.assertFalse(dato.isEmpty());
        Assertions.assertTrue(dato.get().getHidden());
    }

    @And("lato mittente dal web l'elemento di timeline della notifica {string} con deliveryDetailCode {string} non è presente")
    public void latoMittenteDalWebLElementoDiTimelineDellaNotificaConDeliveryDetailCodeNonÈPresente(String category, String deliveryDetailCode) {
        Optional<it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffNotificationDetailTimeline> dato = getNotificationDetailTimelineSender(category, deliveryDetailCode);
        Assertions.assertTrue(dato.isEmpty());
    }

    private Optional<it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffNotificationDetailTimeline> getNotificationDetailTimelineSender(String category, String deliveryDetailCode) {
        return bffFullNotificationV1Sender
                .getTimeline()
                .stream()
                .filter(Objects::nonNull)
                .filter(data ->
                        data.getElementId().contains(category) && data.getDetails() != null &&
                                data.getDetails().getDeliveryDetailCode() != null &&
                                data.getDetails().getDeliveryDetailCode().equals(deliveryDetailCode))
                .findFirst();
    }

    @Then("la notifica non può essere correttamente recuperata da {string}")
    public void notificationCanNotBeCorrectlyReadby(String recipient) {
        sharedSteps.selectUser(recipient);
        this.fullNotification = webRecipientClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(), null);
        Assertions.assertNull(fullNotification);
    }

    @Then("il documento notificato può essere correttamente recuperato da {string}")
    public void theDocumentCanBeProperlyRetrievedBy(String recipient) {
        sharedSteps.selectUser(recipient);
        NotificationAttachmentDownloadMetadataResponse downloadResponse = getRecivedNotificationDocument();
        AtomicReference<String> Sha256 = new AtomicReference<>("");
        Assertions.assertDoesNotThrow(() -> {
            byte[] bytes = Assertions.assertDoesNotThrow(() ->
                    b2bUtils.downloadFile(downloadResponse.getUrl()));
            Sha256.set(b2bUtils.computeSha256(new ByteArrayInputStream(bytes)));
        });
        Assertions.assertEquals(Sha256.get(), downloadResponse.getSha256());
    }

    @Then("il documento notificato non può essere correttamente recuperato da {string}")
    public void theDocumentCanNotBeProperlyRetrievedBy(String recipient) {
        try {
            sharedSteps.selectUser(recipient);
            getRecivedNotificationDocument();
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
        }
    }

    private NotificationAttachmentDownloadMetadataResponse getRecivedNotificationDocument() {

        return webRecipientClient.getReceivedNotificationDocument(
                sharedSteps.getSentNotification().getIun(),
                Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(sharedSteps.getSentNotification()).getDocuments()).get(0).getDocIdx())),
                null
        );
    }

    @Then("l'utente {string} controlla che la data di refinement sia corretta")
    public void theNotificationDateOfRefinementIsCorrectFromUser(String recipient) {
        sharedSteps.selectUser(recipient);

        String iun = sharedSteps.getIunVersionamento();

        try {
            OffsetDateTime scheduleDate = Objects.requireNonNull(webRecipientClient.getReceivedNotification(iun, null).getTimeline().stream().filter(elem -> Objects.requireNonNull(elem.getCategory()).equals(TimelineElementCategoryV23.SCHEDULE_REFINEMENT)).findAny().get().getDetails()).getSchedulingDate();
            OffsetDateTime refinementDate = webRecipientClient.getReceivedNotification(iun, null).getTimeline().stream().filter(elem -> Objects.requireNonNull(elem.getCategory()).equals(TimelineElementCategoryV23.REFINEMENT)).findAny().get().getTimestamp();
            log.info("scheduleDate : {}", scheduleDate);
            log.info("refinementDate : {}", refinementDate);

            Assertions.assertEquals(scheduleDate, refinementDate);

        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("l'allegato {string} può essere correttamente recuperato da {string}")
    public void attachmentCanBeCorrectlyRetrievedBy(String attachmentName, String recipient) {
        //TODO Modificare
        sharedSteps.selectUser(recipient);
        NotificationAttachmentDownloadMetadataResponse downloadResponse = getReceivedNotificationAttachment(attachmentName);

        if (downloadResponse != null && downloadResponse.getRetryAfter() != null && downloadResponse.getRetryAfter() > 0) {
            try {
                await().atMost(downloadResponse.getRetryAfter() * 3L, TimeUnit.MILLISECONDS);
                downloadResponse = getReceivedNotificationAttachment(attachmentName);
            } catch (RuntimeException exc) {
                log.error("Await error exception: {}", exc.getMessage());
                throw exc;
            }
        }

        if (!"F24".equalsIgnoreCase(attachmentName)) {
            AtomicReference<String> Sha256 = new AtomicReference<>("");
            NotificationAttachmentDownloadMetadataResponse finalDownloadResponse = downloadResponse;
            Assertions.assertDoesNotThrow(() -> {
                byte[] bytes = Assertions.assertDoesNotThrow(() ->
                        b2bUtils.downloadFile(Objects.requireNonNull(finalDownloadResponse).getUrl()));
                Sha256.set(b2bUtils.computeSha256(new ByteArrayInputStream(bytes)));
            });
            Assertions.assertEquals(Sha256.get(), Objects.requireNonNull(downloadResponse).getSha256());
        } else {
            NotificationAttachmentDownloadMetadataResponse finalDownloadResponse = downloadResponse;
            Assertions.assertDoesNotThrow(() ->
                    b2bUtils.downloadFile(Objects.requireNonNull(finalDownloadResponse).getUrl()));
        }
    }

    private NotificationAttachmentDownloadMetadataResponse getReceivedNotificationAttachment(String attachmentName) {

        return webRecipientClient.getReceivedNotificationAttachment(
                sharedSteps.getSentNotification().getIun(),
                attachmentName,
                null, 0);
    }

    @And("{string} tenta il recupero dell'allegato {string}")
    public void attachmentRetrievedError(String recipient, String attachmentName) {
        this.notificationError = null;
        sharedSteps.selectUser(recipient);
        retriveAttachement(attachmentName);
    }

    @And("{string} tenta il recupero dell'attestazione {string}")
    public void attachmentAttestazioneRetrievedError(String recipient, String attachmentName) {
        this.notificationError = null;
        sharedSteps.selectUser(recipient);
        retriveAttachement(attachmentName);
    }

    private void retriveAttachement(String attachmentName) {
        try {
            NotificationAttachmentDownloadMetadataResponse downloadResponse = getReceivedNotificationAttachment(attachmentName);

            if (downloadResponse != null && downloadResponse.getRetryAfter() != null && downloadResponse.getRetryAfter() > 0) {
                try {
                    await().atMost(downloadResponse.getRetryAfter() * 3L, TimeUnit.MILLISECONDS);
                    getReceivedNotificationAttachment(attachmentName);
                } catch (RuntimeException exc) {
                    log.error("Await error exception: {}", exc.getMessage());
                    throw exc;
                }
            }
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
        }
    }

    @Then("(il download)(il recupero) ha prodotto un errore con status code {string}")
    public void operationProducedErrorWithStatusCode(String statusCode) {
        Assertions.assertTrue((this.notificationError != null) &&
                (this.notificationError.getStatusCode().toString().substring(0, 3).equals(statusCode)));
    }

    @Then("(il download)(il recupero) non ha prodotto errori")
    public void operationProducedErrorWithStatusCode() {
        try {
            Assertions.assertNull(this.notificationError);
            Assertions.assertNull(sharedSteps.consumeNotificationError());
        } catch (AssertionFailedError e) {
            sharedSteps.throwAssertFailerWithIUN(e);
        }
    }

    @And("download attestazione opponibile AAR da parte {string}")
    public void downloadLegalFactIdAARByRecipient(String recipient) {
        sharedSteps.selectUser(recipient);
        this.notificationError = null;
        try {
            await().atMost(sharedSteps.getWait(), TimeUnit.MILLISECONDS);
        } catch (RuntimeException exc) {
            log.error("Await error exception: {}", exc.getMessage());
            throw exc;
        }

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23 timelineElementInternalCategory = it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23.AAR_GENERATION;
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV25 timelineElement = null;

        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV25 element : sharedSteps.getSentNotification().getTimeline()) {
            if (Objects.requireNonNull(element.getCategory()).equals(timelineElementInternalCategory)) {
                timelineElement = element;
                break;
            }
        }

        Assertions.assertNotNull(timelineElement);
        String keySearch = null;
        Objects.requireNonNull(timelineElement.getDetails());
        if (!timelineElement.getDetails().getGeneratedAarUrl().isEmpty()) {

            if (timelineElement.getDetails().getGeneratedAarUrl().contains("PN_AAR")) {
                keySearch = timelineElement.getDetails().getGeneratedAarUrl().substring(timelineElement.getDetails().getGeneratedAarUrl().indexOf("PN_AAR"));
            }

            String finalKeySearch = "safestorage://" + keySearch;
            try {
                this.webRecipientClient.getDocumentsWeb(sharedSteps.getSentNotification().getIun(), DocumentCategory.AAR, finalKeySearch, null);
            } catch (HttpStatusCodeException e) {
                this.notificationError = e;
            }
        }
    }


    @And("{string} tenta il recupero della notifica")
    public void notificationCanBeCorrectlyReadBy(String recipient) {
        sharedSteps.selectUser(recipient);
        try {
            webRecipientClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(), null);
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
        }
    }


    @Then("la notifica può essere correttamente recuperata con una ricerca da {string}")
    public void notificationCanBeCorrectlyReadWithResearch(String recipient, @Transpose NotificationSearchParam searchParam) {
        sharedSteps.selectUser(recipient);
        Assertions.assertTrue(searchNotification(searchParam));
    }

    @Then("la notifica può essere correttamente recuperata con una ricerca da web PA {string}")
    public void notificationCanBeCorrectlyReadWithResearchWebPA(String paType, @Transpose NotificationSearchParamWebPA searchParam) {
        sharedSteps.selectPA(paType);
        Assertions.assertTrue(searchNotificationWebPA(searchParam));
    }

    @Then("la notifica non viene recuperata con una ricerca da {string}")
    public void notificationCantBeCorrectlyReadWithResearch(String recipient, @Transpose NotificationSearchParam searchParam) {
        sharedSteps.selectUser(recipient);
        Assertions.assertFalse(searchNotification(searchParam));
    }

    @DataTableType
    public NotificationSearchParam convertNotificationSearchParam(Map<String, String> data) {
        NotificationSearchParam searchParam = new NotificationSearchParam();

        PnPaB2bUtils.Pair<OffsetDateTime, OffsetDateTime> dates = getStartDateAndEndDate(data);

        searchParam.startDate = dates.getValue1();
        searchParam.endDate = dates.getValue2();
        searchParam.subjectRegExp = data.getOrDefault("subjectRegExp", null);
        String iun = data.getOrDefault("iunMatch", null);
        if (data.containsKey("status")) {
            searchParam.status = NotificationStatus.valueOf(data.get("status"));
        }
        searchParam.iunMatch = ((iun != null && iun.equalsIgnoreCase("ACTUAL") ? sharedSteps.getSentNotification().getIun() : iun));
        searchParam.size = Integer.parseInt(data.getOrDefault("size", "10"));
        if (searchParam.size == -1) searchParam.size = null;
        return searchParam;
    }

    @DataTableType
    public NotificationSearchParamWebPA convertNotificationSearchParamWebPA(Map<String, String> data) {
        NotificationSearchParamWebPA searchParam = new NotificationSearchParamWebPA();

        PnPaB2bUtils.Pair<OffsetDateTime, OffsetDateTime> dates = getStartDateAndEndDate(data);

        searchParam.startDate = dates.getValue1();
        searchParam.endDate = dates.getValue2();
        searchParam.subjectRegExp = data.getOrDefault("subjectRegExp", null);
        String iun = data.getOrDefault("iunMatch", null);
        searchParam.iunMatch = ((iun != null && iun.equalsIgnoreCase("ACTUAL") ? sharedSteps.getSentNotification().getIun() : iun));
        searchParam.size = Integer.parseInt(data.getOrDefault("size", "10"));
        return searchParam;
    }

    private PnPaB2bUtils.Pair<OffsetDateTime, OffsetDateTime> getStartDateAndEndDate(Map<String, String> data) {

        Calendar now = Calendar.getInstance();
        int month = now.get(Calendar.MONTH);
        String monthString = (((month + "").length() == 2 || month == 9) ? (month + 1) : ("0" + (month + 1))) + "";
        int day = now.get(Calendar.DAY_OF_MONTH);
        String dayString = (day + "").length() == 2 ? (day + "") : ("0" + day);
        String start = data.getOrDefault("startDate", dayString + "/" + monthString + "/" + now.get(Calendar.YEAR));
        String end = data.getOrDefault("endDate", null);

//        OffsetDateTime sentAt = sharedSteps.getSentNotification().getSentAt();
        OffsetDateTime sentAt = Optional.ofNullable(sharedSteps.getSentNotification()).map(FullSentNotificationV25::getSentAt).orElse(OffsetDateTime.now());
        LocalDateTime localDateStart = LocalDate.parse(start, DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay();
        OffsetDateTime startDate = OffsetDateTime.of(localDateStart, sentAt.getOffset());

        OffsetDateTime endDate;
        if (end != null) {
            LocalDateTime localDateEnd = LocalDate.parse(end, DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay();
            endDate = OffsetDateTime.of(localDateEnd, sentAt.getOffset());
        } else {
            endDate = sentAt;
        }

        return new PnPaB2bUtils.Pair<>(startDate, endDate);
    }

    private boolean searchNotification(NotificationSearchParam searchParam) {
        boolean beenFound;
        NotificationSearchResponse notificationSearchResponse = webRecipientClient
                .searchReceivedNotification(
                        searchParam.startDate, searchParam.endDate, searchParam.mandateId,
                        searchParam.senderId, searchParam.status, searchParam.subjectRegExp,
                        searchParam.iunMatch, searchParam.size, null);
        List<NotificationSearchRow> resultsPage = notificationSearchResponse.getResultsPage();
        beenFound = Objects.requireNonNull(resultsPage).stream().filter(elem -> Objects.requireNonNull(elem.getIun()).equals(sharedSteps.getSentNotification().getIun())).findAny().orElse(null) != null;
        if (!beenFound && Boolean.TRUE.equals(notificationSearchResponse.getMoreResult())) {
            while (Boolean.TRUE.equals(notificationSearchResponse.getMoreResult())) {
                List<String> nextPagesKey = notificationSearchResponse.getNextPagesKey();
                for (String pageKey : Objects.requireNonNull(nextPagesKey)) {
                    notificationSearchResponse = webRecipientClient
                            .searchReceivedNotification(
                                    searchParam.startDate, searchParam.endDate, searchParam.mandateId,
                                    searchParam.senderId, searchParam.status, searchParam.subjectRegExp,
                                    searchParam.iunMatch, searchParam.size, pageKey);
                    beenFound = resultsPage.stream().filter(elem -> Objects.requireNonNull(elem.getIun()).equals(sharedSteps.getSentNotification().getIun())).findAny().orElse(null) != null;
                    if (beenFound) break;
                }//for
                if (beenFound) break;
            }//while
        }//search cycle
        return beenFound;
    }


    private boolean searchNotificationWebPA(NotificationSearchParamWebPA searchParam) {
        boolean beenFound;
        it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.NotificationSearchResponse notificationSearchResponse = webPaClient
                .searchSentNotification(
                        searchParam.startDate, searchParam.endDate, searchParam.mandateId,
                        searchParam.status, searchParam.subjectRegExp,
                        searchParam.iunMatch, searchParam.size, null);
        List<it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.NotificationSearchRow> resultsPage = notificationSearchResponse.getResultsPage();
        beenFound = Objects.requireNonNull(resultsPage).stream().filter(elem -> Objects.requireNonNull(elem.getIun()).equals(sharedSteps.getSentNotification().getIun())).findAny().orElse(null) != null;
        if (!beenFound && Boolean.TRUE.equals(notificationSearchResponse.getMoreResult())) {
            while (Boolean.TRUE.equals(notificationSearchResponse.getMoreResult())) {
                List<String> nextPagesKey = notificationSearchResponse.getNextPagesKey();
                for (String pageKey : Objects.requireNonNull(nextPagesKey)) {
                    notificationSearchResponse = webPaClient
                            .searchSentNotification(
                                    searchParam.startDate, searchParam.endDate, searchParam.mandateId,
                                    searchParam.status, searchParam.subjectRegExp,
                                    searchParam.iunMatch, searchParam.size, pageKey);
                    beenFound = resultsPage.stream().filter(elem -> Objects.requireNonNull(elem.getIun()).equals(sharedSteps.getSentNotification().getIun())).findAny().orElse(null) != null;
                    if (beenFound) break;
                }//for
                if (beenFound) break;
            }//while
        }//search cycle
        return beenFound;
    }

    @When("si predispone addressbook per l'utente {string}")
    public void siPredisponeAddressbook(String user) {
        switch (user) {
            case "Mario Cucumber" ->
                    this.iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_1);
            case "Mario Gherkin" ->
                    this.iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_2);
            case "Galileo Galilei" ->
                    this.iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.USER_4);
            case "Lucio Anneo Seneca", "CucumberSpa" ->
                    this.iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_2);
            case "GherkinSrl" ->
                    this.iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
            case "Alda Merini" ->
                    this.iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_3);
            default -> throw new IllegalArgumentException();
        }
    }

    @And("viene inserito un recapito legale {string} con verification code errato {string}")
    public void nuovoRecapitoLegale(String pec, String verificationCode) {
        postRecipientLegalAddressWrongCode("default", pec, verificationCode);
    }

    @When("viene richiesto l'inserimento della pec {string}")
    public void perLUtenteVieneSettatoLaPec(String pec) {
        postRecipientLegalAddress("default", pec, "00000", false);
    }

    @When("viene richiesto l'inserimento del numero di telefono {string}")
    public void vieneRichiestoLInserimentoDelNumeroDiTelefono(String phone) {
        postRecipientCourtesyAddress("default", phone, CourtesyChannelType.SMS, "00000", false);
    }

    @When("viene richiesto l'inserimento del email di cortesia {string}")
    public void vieneRichiestoLInserimentoDelEmailDiCortesia(String email) {
        postRecipientCourtesyAddress("default", email, CourtesyChannelType.EMAIL, "00000", false);
    }

    @And("viene inserito un recapito legale {string} per il comune {string}")
    public void nuovoRecapitoLegaleDalComune(String pec, String pa) {
        String senderIdPa = getSenderIdPa(pa);
        postRecipientLegalAddress(senderIdPa, pec, null, true);
    }

    @And("viene inserito un recapito legale {string} per il comune {string} con verification code errato {string}")
    public void nuovoRecapitoLegaleDalComuneConVerificationCodeErrato(String pec, String pa, String verificationCode) {
        String senderIdPa = getSenderIdPa(pa);
        postRecipientLegalAddressWrongCode(senderIdPa, pec, verificationCode);
    }

    @When("viene richiesto l'inserimento della pec {string} per il comune {string}")
    public void perLUtenteVieneSettatoLaPecPerIlComune(String pec, String pa) {
        String senderIdPa = getSenderIdPa(pa);
        postRecipientLegalAddress(senderIdPa, pec, "00000", false);
    }

    @And("viene richiesto l'inserimento del email di cortesia {string} per il comune {string}")
    public void vieneRichiestoLInserimentoDelEmailDiCortesiaDalComune(String email, String pa) {
        String senderIdPa = getSenderIdPa(pa);
        postRecipientCourtesyAddress(senderIdPa, email, CourtesyChannelType.EMAIL, "00000", false);
    }

    @And("viene inserita l'email di cortesia {string} per il comune {string}")
    public void vieneInseritaEmailDiCortesiaDalComune(String email, String pa) {
        String senderIdPa = getSenderIdPa(pa);
        postRecipientCourtesyAddress(senderIdPa, email, CourtesyChannelType.EMAIL, null, true);
    }

    @When("viene richiesto l'inserimento del numero di telefono {string} per il comune {string}")
    public void vieneRichiestoLInserimentoDelNumeroDiTelefono(String phone, String pa) {
        String senderIdPa = getSenderIdPa(pa);
        postRecipientCourtesyAddress(senderIdPa, phone, CourtesyChannelType.SMS, "00000", false);
    }

    private void postRecipientCourtesyAddress(String senderId, String addressVerification, CourtesyChannelType type, String verificationCode, boolean inserimento) {
        try {
            if(inserimento){
                this.iPnWebUserAttributesClient.postRecipientCourtesyAddress(senderId, CourtesyChannelType.EMAIL, (new AddressVerification().value(addressVerification)));
                verificationCode = this.externalClient.getVerificationCode(addressVerification);
            }
            this.iPnWebUserAttributesClient.postRecipientCourtesyAddress(senderId, type, (new AddressVerification().value(addressVerification).verificationCode(verificationCode)));
        } catch (HttpStatusCodeException httpStatusCodeException) {
            sharedSteps.setNotificationError(httpStatusCodeException);
        }
    }

    private void postRecipientLegalAddress(String senderIdPa, String addressVerification, String verificationCode, boolean inserimento) {
        try {
            if (inserimento){
                this.iPnWebUserAttributesClient.postRecipientLegalAddress(senderIdPa, LegalChannelType.PEC, (new AddressVerification().value(addressVerification)));
                verificationCode = this.externalClient.getVerificationCode(addressVerification);
            }
            this.iPnWebUserAttributesClient.postRecipientLegalAddress(senderIdPa, LegalChannelType.PEC, (new AddressVerification().value(addressVerification).verificationCode(verificationCode)));
        } catch (HttpStatusCodeException httpStatusCodeException) {
            sharedSteps.setNotificationError(httpStatusCodeException);
        }
    }

    private void postRecipientLegalAddressWrongCode(String senderIdPa, String addressVerification, String verificationCode) {
        String[] code = {verificationCode};
        Assertions.assertThrows(HttpStatusCodeException.class, () -> {
            this.iPnWebUserAttributesClient.postRecipientLegalAddress(senderIdPa, LegalChannelType.PEC, (new AddressVerification().value(addressVerification).verificationCode(code[0])));
        });
    }

    @And("viene cancellata l'email di cortesia per il comune {string}")
    public void vieneCancellataEmailDiCortesiaDalComune(String pa) {
        String senderIdPa = getSenderIdPa(pa);

        try {
            this.iPnWebUserAttributesClient.deleteRecipientCourtesyAddress(senderIdPa, CourtesyChannelType.EMAIL);
        } catch (HttpStatusCodeException httpStatusCodeException) {
            sharedSteps.setNotificationError(httpStatusCodeException);
        }
    }

    private String getSenderIdPa(String pa) {
        return switch (pa) {
            case "Comune_1" -> senderId;
            case "Comune_2" -> senderId2;
            case "Comune_Multi" -> senderIdGA;
            case "Comune_Son" -> senderIdSON;
            case "Comune_Root" -> senderIdROOT;
            default -> "default";
        };
    }

    @Then("l'inserimento ha prodotto un errore con status code {string}")
    public void lInserimentoHaProdottoUnErroreConStatusCode(String statusCode) {
        HttpStatusCodeException httpStatusCodeException = this.sharedSteps.consumeNotificationError();
        Assertions.assertTrue((httpStatusCodeException != null) &&
                (httpStatusCodeException.getStatusCode().toString().substring(0, 3).equals(statusCode)));
    }

    @And("verifico che l'atto opponibile a terzi di {string} sia lo stesso")
    public void verificoAttoOpponibileSiaUguale(String timelineEventCategory, @Transpose DataTest dataFromTest) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV25 timelineElement =
                sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataFromTest);
        // get new timeline
        String iun = sharedSteps.getSentNotification().getIun();
        sharedSteps.setSentNotification(b2bClient.getSentNotification(iun));
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV25 newTimelineElement =
                sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataFromTest);
        // check legal fact key
        Assertions.assertEquals(Objects.requireNonNull(timelineElement.getLegalFactsIds()).size(), Objects.requireNonNull(newTimelineElement.getLegalFactsIds()).size());
        for (int i = 0; i < newTimelineElement.getLegalFactsIds().size(); i++) {
            Assertions.assertEquals(newTimelineElement.getLegalFactsIds().get(i).getKey(), timelineElement.getLegalFactsIds().get(i).getKey());
        }
    }

    @And("attendo che gli elementi di timeline SEND_ANALOG_PROGRESS vengano ricevuti tutti")
    public void attendoCheGliElementiDiTimelineSEND_ANALOG_PROGRESSVenganoRicevutiTutti() {
        Integer waiting = timingConfigs.getWaitMillisForSendAnalogEvents() == null ? waitDefault : timingConfigs.getWaitMillisForSendAnalogEvents();
        waitState(waiting);
    }

    public static class NotificationSearchParam {
        OffsetDateTime startDate;
        OffsetDateTime endDate;
        String mandateId;
        String senderId;
        NotificationStatus status;
        String subjectRegExp;
        String iunMatch;
        Integer size = 10;
    }

    private static class NotificationSearchParamWebPA {
        OffsetDateTime startDate;
        OffsetDateTime endDate;
        String mandateId;
        it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.NotificationStatus status;
        String subjectRegExp;
        String iunMatch;
        Integer size = 10;
    }

    public void selectUser(String user) {
        switch (user.trim().toLowerCase()) {
            case "mario cucumber", "ettore fieramosca" -> {
                bffRecipientNotificationClient.setRecipientBearerToken(SettableBearerToken.BearerTokenType.USER_1);
            }
            case "mario gherkin", "cristoforo colombo" -> {
                bffRecipientNotificationClient.setRecipientBearerToken(SettableBearerToken.BearerTokenType.USER_2);
            }
            case "gherkinsrl" -> {
                bffRecipientNotificationClient.setRecipientBearerToken(SettableBearerToken.BearerTokenType.PG_1);
            }
            case "cucumberspa", "lucio anneo seneca" -> {
                bffRecipientNotificationClient.setRecipientBearerToken(SettableBearerToken.BearerTokenType.PG_2);
            }
            case "leonardo da vinci" -> {
                bffRecipientNotificationClient.setRecipientBearerToken(SettableBearerToken.BearerTokenType.USER_3);
            }
            case "dino sauro" -> {
                bffRecipientNotificationClient.setRecipientBearerToken(SettableBearerToken.BearerTokenType.USER_5);
            }
            case "mario cucumber con credenziali non valide" -> {
                bffRecipientNotificationClient.setRecipientBearerToken(SettableBearerToken.BearerTokenType.USER_SCADUTO);
            }
            default -> throw new IllegalArgumentException();

        }

    }

    public void selectPa(String pa) {
        switch (pa) {
            case "Comune_1" -> {
                this.bffRecipientNotificationClient.setSenderBearerToken(SettableBearerToken.BearerTokenType.MVP_1);
            }
            case "Comune_2" -> {
                this.bffRecipientNotificationClient.setSenderBearerToken(SettableBearerToken.BearerTokenType.MVP_2);
            }
            case "Comune_Multi" -> {
                this.bffRecipientNotificationClient.setSenderBearerToken(SettableBearerToken.BearerTokenType.GA);
            }
            case "Comune_Son" -> {
                this.bffRecipientNotificationClient.setSenderBearerToken(SettableBearerToken.BearerTokenType.SON);
            }
            case "Comune_Root" -> {
                this.bffRecipientNotificationClient.setSenderBearerToken(SettableBearerToken.BearerTokenType.ROOT);
            }
            default -> throw new IllegalArgumentException();
        }
    }

    private static void waitState(Integer waitingStateCsv) {
        try {
            Thread.sleep(waitingStateCsv);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void postRecipientLegalAddressSercq(String senderIdPa, String address) {
        Assertions.assertDoesNotThrow(() -> {
            this.iPnWebUserAttributesClient.postRecipientLegalAddress(senderIdPa, LegalChannelType.SERCQ, (new AddressVerification().value(address)));
        });
    }

    @And("viene disabilitato il servizio SERCQ SEND per il comune di {string}")
    public void vieneDisabilitatoSercqPerEnte(String pa) {
        String senderId = getSenderIdPa(pa);
        Assertions.assertDoesNotThrow(() -> {
            List<LegalAndUnverifiedDigitalAddress> legalAddressByRecipient = this.iPnWebUserAttributesClient.getLegalAddressByRecipient();
            if (legalAddressByRecipient != null && !legalAddressByRecipient.isEmpty()) {
                this.iPnWebUserAttributesClient.deleteRecipientLegalAddress(senderId, LegalChannelType.SERCQ);
                log.info("SERCQ DISABLED");
            }
        });
    }



    @And("viene verificata l' assenza di pec inserite per l'utente")
    public void viewedPecDiPiattaformaDi() {


        List<LegalAndUnverifiedDigitalAddress> legalAddressByRecipient = Assertions.assertDoesNotThrow(() -> this.iPnWebUserAttributesClient.getLegalAddressByRecipient());

        Assertions.assertNotNull(legalAddressByRecipient);

        boolean exists = legalAddressByRecipient.stream()
                .anyMatch(address -> LegalChannelType.PEC.equals(address.getChannelType()));
        Assertions.assertFalse(exists, "PEC IS PRESENT");

    }

    @And("viene verificata la presenza di pec inserite per il comune {string}")
    public void verifyPecIsPresentPerUserPerEnte(String pa) {
        String senderId = getSenderIdPa(pa);

        List<LegalAndUnverifiedDigitalAddress> legalAddressByRecipient = Assertions.assertDoesNotThrow(() -> this.iPnWebUserAttributesClient.getLegalAddressByRecipient());
        boolean exists = false;
        if (legalAddressByRecipient != null && !legalAddressByRecipient.isEmpty()) {
            exists = legalAddressByRecipient.stream()
                    .anyMatch(address -> senderId.equals(address.getSenderId()));
        }
        Assertions.assertTrue(exists, "PEC NOT FOUND");
    }

    @And("viene verificato che Sercq sia {string} per il comune {string}")
    public void viewedSercqPerEnte(String act, String pa) {
        String senderId = getSenderIdPa(pa);

        List<LegalAndUnverifiedDigitalAddress> legalAddressByRecipient = Assertions.assertDoesNotThrow(() -> this.iPnWebUserAttributesClient.getLegalAddressByRecipient());

        boolean exists = Optional.ofNullable(legalAddressByRecipient)
                .filter(data -> !data.isEmpty())
                .map(data -> data.stream()
                        .anyMatch(address -> senderId.equals(address.getSenderId()) && LegalChannelType.SERCQ.equals(address.getChannelType())))
                .orElse(false);

        switch (act) {
            case "disabilitato":
                Assertions.assertFalse(exists, "Sercq risulta abilitato per il comune: " + pa);
                break;
            case "abilitato":
                Assertions.assertTrue(exists, "Sercq risulta disabilitato per il comune: " + pa);
                break;
            default:
                throw new IllegalArgumentException("Valore di 'act' non valido: " + act + ". I valori consentiti sono 'abilitato' o 'disabilitato'.");
        }
    }


    @And("viene verificata la presenza di Sercq attivo per l'utente {string} ")
    public void viewedSercqPerUtente(String user) {
        selectUser(user);

        List<LegalAndUnverifiedDigitalAddress> legalAddressByRecipient = Assertions.assertDoesNotThrow(() -> this.iPnWebUserAttributesClient.getLegalAddressByRecipient());
        boolean exists = false;
        if (legalAddressByRecipient != null && !legalAddressByRecipient.isEmpty()) {
            exists = legalAddressByRecipient.stream()
                    .anyMatch(address -> LegalChannelType.SERCQ.equals(address.getChannelType()));
        }
        Assertions.assertTrue(exists, "SERCQ NOT FOUND");
    }


    @And("viene verificata l'assenza di indirizzi Pec per il comune {string}")
    public void viewedPecPerUtentePerEnte(String pa) {
        String senderId = getSenderIdPa(pa);

        List<LegalAndUnverifiedDigitalAddress> legalAddressByRecipient = Assertions.assertDoesNotThrow(() -> this.iPnWebUserAttributesClient.getLegalAddressByRecipient());
        boolean exists = false;
        if (legalAddressByRecipient != null && !legalAddressByRecipient.isEmpty()) {
            exists = legalAddressByRecipient.stream()
                    .anyMatch(address -> LegalChannelType.PEC.equals(address.getChannelType()) && senderId.equals(address.getSenderId()) && address.getCodeValid());
        }
        Assertions.assertFalse(exists, "PEC FOUND");

    }

    //Come da SRS Abilitazione Domicilio Digitale, address è una stringa fissa "x-pagopa-pn-sercq:send-self:notification-already-delivered"
    @And("viene attivato il servizio SERCQ SEND per recapito principale")
    public void attivazioneSercqSend() {
        postRecipientLegalAddressSercq("default", "x-pagopa-pn-sercq:send-self:notification-already-delivered");
    }

    //Come da SRS Abilitazione Domicilio Digitale, address è una stringa fissa "x-pagopa-pn-sercq:send-self:notification-already-delivered"
    @And("viene attivato il servizio SERCQ SEND per il comune {string}")
    public void attivazioneSercqPerEnteSpecifico(String pa) {
        String senderIdPa = getSenderIdPa(pa);
        postRecipientLegalAddressSercq(senderIdPa, "x-pagopa-pn-sercq:send-self:notification-already-delivered");
    }

    @And("viene inserito un recapito legale {string}")
    public void nuovoRecapitoLegale(String pec) {
        postRecipientLegalAddress("default", pec, null, true);
    }

    @And("viene controllato che siano presenti pec verificate inserite per il comune {string}")
    public void waitedAndViewedPecDiPiattaformaDi(String pa) {
        String senderId = getSenderIdPa(pa);

        try {
            Thread.sleep(80000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Sleep was interrupted", e);
        }
        boolean exists = false;
        List<LegalAndUnverifiedDigitalAddress> legalAddressByRecipient = Assertions.assertDoesNotThrow(() -> this.iPnWebUserAttributesClient.getLegalAddressByRecipient());
        if (legalAddressByRecipient != null && !legalAddressByRecipient.isEmpty()) {
            exists = legalAddressByRecipient.stream()
                    .anyMatch(address -> LegalChannelType.PEC.equals(address.getChannelType()) && senderId.equals(address.getSenderId()));


        }
        Assertions.assertTrue(exists, "PEC NOT FOUND");

    }

    @And("viene rimossa se presente la pec per il comune {string}")
    public void vieneRimossaSePresenteLaPecDiPiattaformaDi(String pa) {
        String senderId = getSenderIdPa(pa);
        Assertions.assertDoesNotThrow(() -> {
            this.iPnWebUserAttributesClient.deleteRecipientLegalAddress(senderId, LegalChannelType.PEC);
            log.info("PEC FOUND AND DELETED");
        }, "PEC NOT FOUND");
    }

    @And("vengono rimossi eventuali recapiti presenti per l'utente")
    public void cleanLegalAddressForUser() {
        try {
            List<LegalAndUnverifiedDigitalAddress> legalAddressByRecipient = this.iPnWebUserAttributesClient.getAddressesByRecipient().getLegal();
            if (legalAddressByRecipient != null && !legalAddressByRecipient.isEmpty()) {
                legalAddressByRecipient.stream()
                        .forEach(address -> {
                            this.iPnWebUserAttributesClient.deleteRecipientLegalAddress(address.getSenderId(), address.getChannelType());
                            log.info("Cancellato indirizzo di tipo " + address.getChannelType() + " per il comune " + address.getSenderId());
                        });
            }
            List<CourtesyDigitalAddress> courtesyDigitalAddresses = this.iPnWebUserAttributesClient.getAddressesByRecipient().getCourtesy();
            if (courtesyDigitalAddresses != null && !courtesyDigitalAddresses.isEmpty()) {
                courtesyDigitalAddresses.stream()
                        .forEach(address -> {
                            this.iPnWebUserAttributesClient.deleteRecipientCourtesyAddress(address.getSenderId(), address.getChannelType());
                            log.info("Cancellato indirizzo di cortesia di tipo " + address.getChannelType() + " per il comune " + address.getSenderId());
                        });
            }
        } catch (Exception e) {
            log.error("RIMOZIONE RECAPITI FALLITA:\n" + e);
        }
    }


    @And("Viene verificato che non sia arrivato un evento di {string}")
    public void verificaAssenzaElementoTimeline(String categoryToFind) {

        boolean isPresent = sharedSteps.getSentNotification().getTimeline()
                .stream()
                .map(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV25::getCategory)
                .filter(Objects::nonNull)
                .map(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23::toString)
                .anyMatch(category -> category.equals(categoryToFind));

        if (isPresent) {
            throw new AssertionFailedError("L'evento cercato è stato ritornato!");
        }
    }

    @Given("l'utente {string} {string} i tos per sercq")
    public void lUtenteAccettaITos(String user, String operation) {
        sharedSteps.selectUser(user);
        BffTosPrivacyActionBody.ActionEnum actionEnum = operation.equals(ACCEPT_TOS) ? BffTosPrivacyActionBody.ActionEnum.ACCEPT : BffTosPrivacyActionBody.ActionEnum.DECLINE;
        BffTosPrivacyActionBody bffTosPrivacyBody = new BffTosPrivacyActionBody().action(actionEnum).version(TOS_VERSION).type(ConsentType.TOS_SERCQ);
        Assertions.assertDoesNotThrow(() -> iPnTosPrivacyClient.acceptTosPrivacyV1(List.of(bffTosPrivacyBody)));
    }

    @Given("l'utente {string} controlla l'accettazione {string} dei tos per sercq")
    public void lUtenteControllaAccettazioneDeiTos(String user, String tosStatus) {
        sharedSteps.selectUser(user);
        it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.tos.privacy.ConsentType consentType = ConsentType.TOS_SERCQ;
        List<BffConsent> privacyConsentv1 = Assertions.assertDoesNotThrow(() -> iPnTosPrivacyClient.getTosPrivacyV1(List.of(consentType)));
        Assertions.assertNotNull(privacyConsentv1);
        Assertions.assertFalse(privacyConsentv1.isEmpty());
        privacyConsentv1.forEach(data -> {
            Assertions.assertNotNull(data.getConsentType());
            Assertions.assertNotNull(data.getConsentType().equals(ConsentType.TOS_SERCQ));
            Assertions.assertEquals(data.getAccepted(), tosStatus.equalsIgnoreCase("positiva"));
        });
    }

}




