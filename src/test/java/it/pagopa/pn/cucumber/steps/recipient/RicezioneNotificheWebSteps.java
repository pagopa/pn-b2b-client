package it.pagopa.pn.cucumber.steps.recipient;

import io.cucumber.java.Before;
import io.cucumber.java.DataTableType;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffFullNotificationV1;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.BffNotificationDetailTimeline;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.config.PnB2bClientTimingConfigs;
import it.pagopa.pn.client.b2b.pa.service.*;
import it.pagopa.pn.client.b2b.pa.service.impl.*;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.model.AddressVerification;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.model.CourtesyChannelType;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.model.LegalAndUnverifiedDigitalAddress;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.model.LegalChannelType;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.consents.model.ConsentAction;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.consents.model.ConsentType;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.*;
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
    private final IPnWebRecipientClient webRecipientClient;
    private IPnWebUserAttributesClient iPnWebUserAttributesClient;
    private final PnPaB2bUtils b2bUtils;
    private final IPnPaB2bClient b2bClient;
    private final PnExternalServiceClientImpl externalClient;
    private final SharedSteps sharedSteps;
    private final IPnWebPaClient webPaClient;
    private final IPnBFFRecipientNotificationClient bffRecipientNotificationClient;
    private final PnB2bClientTimingConfigs timingConfigs;
    private static final Integer waitDefault = 10000;

    private HttpStatusCodeException notificationError;
    private FullReceivedNotificationV23 fullNotification;
    private BffFullNotificationV1 bffFullNotificationV1Recipient;
    private it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffFullNotificationV1 bffFullNotificationV1Sender;

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
    }

    @Autowired
    public RicezioneNotificheWebSteps(ApplicationContext context, SharedSteps sharedSteps, PnWebUserAttributesExternalClientImpl iPnWebUserAttributesClient,
                                      IPnBFFRecipientNotificationClient bffRecipientNotificationClient, PnB2bClientTimingConfigs timingConfigs) {
        this.context = context;
        this.sharedSteps = sharedSteps;
        this.webRecipientClient = sharedSteps.getWebRecipientClient();
        this.b2bUtils = sharedSteps.getB2bUtils();
        this.b2bClient = sharedSteps.getB2bClient();
        this.iPnWebUserAttributesClient = iPnWebUserAttributesClient;
        this.webPaClient = sharedSteps.getWebPaClient();
        this.externalClient = sharedSteps.getPnExternalServiceClient();
        this.bffRecipientNotificationClient = bffRecipientNotificationClient;
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

    private it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.TimelineElementV23 getTimelineElementV23(String category, String deliveryDetailCode) {
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
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV23 timelineElement = null;

        for (it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV23 element : sharedSteps.getSentNotification().getTimeline()) {
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
                this.webRecipientClient.getDocumentsWeb(sharedSteps.getSentNotification().getIun(), it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.DocumentCategory.AAR, finalKeySearch, null);
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

        OffsetDateTime sentAt = sharedSteps.getSentNotification().getSentAt();
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
            case "Lucio Anneo Seneca" ->
                    this.iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_2);
            case "GherkinSrl" ->
                    this.iPnWebUserAttributesClient.setBearerToken(SettableBearerToken.BearerTokenType.PG_1);
            default -> throw new IllegalArgumentException();
        }
    }

    @And("viene inserito un recapito legale {string} con verification code {string}")
    public void nuovoRecapitoLegale(String pec, String verificationCode) {

        Assertions.assertThrows(HttpClientErrorException.class,
               () -> postRecipientLegalAddress("default", pec, verificationCode, false));


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
            if (inserimento) {
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
            if (inserimento) {
                this.iPnWebUserAttributesClient.postRecipientLegalAddress(senderIdPa, LegalChannelType.PEC, (new AddressVerification().value(addressVerification)));
                verificationCode = this.externalClient.getVerificationCode(addressVerification);
            }
            this.iPnWebUserAttributesClient.postRecipientLegalAddress(senderIdPa, LegalChannelType.PEC, (new AddressVerification().value(addressVerification).verificationCode(verificationCode)));
        } catch (HttpStatusCodeException httpStatusCodeException) {
            sharedSteps.setNotificationError(httpStatusCodeException);
        }
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
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV23 timelineElement =
                sharedSteps.getTimelineElementByEventId(timelineEventCategory, dataFromTest);
        // get new timeline
        String iun = sharedSteps.getSentNotification().getIun();
        sharedSteps.setSentNotification(b2bClient.getSentNotification(iun));
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV23 newTimelineElement =
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
            case "cucumberspa" -> {
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

    private void postRecipientLegalAddressSercq(String senderIdPa, String addressVerification, String verificationCode, boolean inserimento) {
        final String[] verifCode = {verificationCode};
        Assertions.assertDoesNotThrow(() -> {
            if (inserimento) {
                this.iPnWebUserAttributesClient.postRecipientLegalAddress(senderIdPa, LegalChannelType.SERCQ, (new AddressVerification().value(addressVerification)));
                verifCode[0] = this.externalClient.getVerificationCode(addressVerification);
            }
            this.iPnWebUserAttributesClient.postRecipientLegalAddress(senderIdPa, LegalChannelType.SERCQ, (new AddressVerification().value(addressVerification).verificationCode(verifCode[0])));
        });
    }

    @And("viene disabilitato il servizio SERCQ SEND")
    public void vieneDisabilitatoSercq() {
        try {
            List<LegalAndUnverifiedDigitalAddress> legalAddressByRecipient = this.iPnWebUserAttributesClient.getLegalAddressByRecipient();
            if (legalAddressByRecipient != null && !legalAddressByRecipient.isEmpty()) {
                this.iPnWebUserAttributesClient.deleteRecipientLegalAddress("default", LegalChannelType.SERCQ);
                log.info("SERCQ DISABLED");
            }
        } catch (HttpStatusCodeException httpStatusCodeException) {
            if (httpStatusCodeException.getStatusCode().is4xxClientError()) {
                log.info("SERCQ NOT FOUND");
            } else {
                throw httpStatusCodeException;
            }
        }
    }

    @Then("vengono accettati i TOS")
    public void vengonoAccettagiTosSercq() {
        Assertions.assertDoesNotThrow(() -> {
            ConsentAction consentAction = new ConsentAction();
            consentAction.setAction(ConsentAction.ActionEnum.ACCEPT);
            this.iPnWebUserAttributesClient.consentAction(ConsentType.TOS, consentAction, "default");
        });
    }

    @And("viene verificata l' assenza di pec inserite per l'utente {string}")
    public void viewedPecDiPiattaformaDi(String user) {
        selectUser(user);
        try {
            List<LegalAndUnverifiedDigitalAddress> legalAddressByRecipient = this.iPnWebUserAttributesClient.getLegalAddressByRecipient();
            Assertions.assertTrue(legalAddressByRecipient.isEmpty());
        } catch (HttpStatusCodeException httpStatusCodeException) {
            if (httpStatusCodeException.getStatusCode().is4xxClientError()) {
                log.info("PEC NOT FOUND");
            } else {
                throw httpStatusCodeException;
            }
        }
    }

    @And("viene verificata l' assenza di pec inserite per l'utente {string} per il comune {string}")
    public void viewedNoPecPerEnte(String user, String senderId) {
        selectUser(user);
        try {
            List<LegalAndUnverifiedDigitalAddress> legalAddressByRecipient = this.iPnWebUserAttributesClient.getLegalAddressByRecipient();
            boolean exists = legalAddressByRecipient.stream()
                    .anyMatch(address -> senderId.equals(address.getSenderId()));

            Assertions.assertFalse(exists);
        } catch (HttpStatusCodeException httpStatusCodeException) {
            if (httpStatusCodeException.getStatusCode().is4xxClientError()) {
                log.info("PEC NOT FOUND");
            } else {
                throw httpStatusCodeException;
            }
        }
    }

    @And("viene verificata la presenza di pec inserite per l'utente {string} per il comune {string}")
    public void viewedPecPerEnte(String user, String senderId) {
        selectUser(user);
        try {
            List<LegalAndUnverifiedDigitalAddress> legalAddressByRecipient = this.iPnWebUserAttributesClient.getLegalAddressByRecipient();
            boolean exists = legalAddressByRecipient.stream()
                    .anyMatch(address -> senderId.equals(address.getSenderId()));

            Assertions.assertTrue(exists);
        } catch (HttpStatusCodeException httpStatusCodeException) {
            if (httpStatusCodeException.getStatusCode().is4xxClientError()) {
                log.info("PEC NOT FOUND");
            } else {
                throw httpStatusCodeException;
            }
        }
    }

    @And("viene verificata la presenza di Sercq attivo per l'utente {string} per il comune {string}")
    public void viewedSercqPerEnte(String user, String senderId) {
        selectUser(user);
        try {
            List<LegalAndUnverifiedDigitalAddress> legalAddressByRecipient = this.iPnWebUserAttributesClient.getLegalAddressByRecipient();
            boolean exists = legalAddressByRecipient.stream()
                    .anyMatch(address -> senderId.equals(address.getSenderId())
                            && "SERCQ".equals(address.getChannelType()));

            Assertions.assertTrue(exists);
        } catch (HttpStatusCodeException httpStatusCodeException) {
            if (httpStatusCodeException.getStatusCode().is4xxClientError()) {
                log.info("PEC NOT FOUND");
            } else {
                throw httpStatusCodeException;
            }
        }
    }

    @And("viene verificata la presenza di Sercq attivo per l'utente {string}")
    public void viewedSercqPerUtente(String user) {
        selectUser(user);
        try {
            List<LegalAndUnverifiedDigitalAddress> legalAddressByRecipient = this.iPnWebUserAttributesClient.getLegalAddressByRecipient();
            boolean exists = legalAddressByRecipient.stream()
                    .anyMatch(address -> "SERCQ".equals(address.getChannelType()));

            Assertions.assertTrue(exists);
        } catch (HttpStatusCodeException httpStatusCodeException) {
            if (httpStatusCodeException.getStatusCode().is4xxClientError()) {
                log.info("PEC NOT FOUND");
            } else {
                throw httpStatusCodeException;
            }
        }
    }

    @And("viene verificata l'assenza di  indirizzi Pec per l'utente {string}")
    public void viewedPecPerUtente(String user) {
        selectUser(user);
        try {
            List<LegalAndUnverifiedDigitalAddress> legalAddressByRecipient = this.iPnWebUserAttributesClient.getLegalAddressByRecipient();
            boolean exists = legalAddressByRecipient.stream()
                    .anyMatch(address -> "PEC".equals(address.getChannelType()));

            Assertions.assertFalse(exists);
        } catch (HttpStatusCodeException httpStatusCodeException) {
            if (httpStatusCodeException.getStatusCode().is4xxClientError()) {
                log.info("PEC NOT FOUND");
            } else {
                throw httpStatusCodeException;
            }
        }
    }

    @And("viene attivato il servizio SERCQ SEND per recapito principale")
    public void attivazioneSercqSend() {
        postRecipientLegalAddressSercq("default", "default", null, true);
    }

    @And("viene attivato il servizio SERCQ SEND per il comune {string}")
    public void attivazioneSercqPerEnteSpecifico(String senderIdPa) {
        postRecipientLegalAddressSercq(senderIdPa, "default", null, true);
    }

    @And("viene inserito un recapito legale {string}")
    public void nuovoRecapitoLegale(String pec) {
        postRecipientLegalAddress("default", pec, null, true);
    }


}


