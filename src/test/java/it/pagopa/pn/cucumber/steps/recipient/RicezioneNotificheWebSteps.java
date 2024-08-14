package it.pagopa.pn.cucumber.steps.recipient;

import io.cucumber.java.DataTableType;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebPaClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebRecipientClient;
import it.pagopa.pn.client.b2b.pa.service.IPnWebUserAttributesClient;
import it.pagopa.pn.client.b2b.pa.service.impl.PnExternalServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.model.AddressVerification;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.model.CourtesyChannelType;
import it.pagopa.pn.client.web.generated.openapi.clients.externalUserAttributes.addressBook.model.LegalChannelType;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.*;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.utils.DataTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.awaitility.Awaitility.await;


@Slf4j
public class RicezioneNotificheWebSteps {
    private final IPnWebRecipientClient webRecipientClient;
    private final IPnWebUserAttributesClient iPnWebUserAttributesClient;
    private final PnPaB2bUtils b2bUtils;
    private final IPnPaB2bClient b2bClient;
    private final PnExternalServiceClientImpl externalClient;
    private final SharedSteps sharedSteps;
    private final IPnWebPaClient webPaClient;
    private HttpStatusCodeException notificationError;

    private FullReceivedNotificationV23 fullNotification;
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


    @Autowired
    public RicezioneNotificheWebSteps(SharedSteps sharedSteps, IPnWebUserAttributesClient iPnWebUserAttributesClient) {
        this.sharedSteps = sharedSteps;
        this.webRecipientClient = sharedSteps.getWebRecipientClient();
        this.b2bUtils = sharedSteps.getB2bUtils();
        this.b2bClient = sharedSteps.getB2bClient();
        this.iPnWebUserAttributesClient = iPnWebUserAttributesClient;
        this.webPaClient = sharedSteps.getWebPaClient();
        this.externalClient = sharedSteps.getPnExternalServiceClient();
    }

    @Then("la notifica può essere correttamente recuperata da {string}")
    public void notificationCanBeCorrectlyReadby(String recipient) {
        sharedSteps.selectUser(recipient);
        Assertions.assertDoesNotThrow(() -> {
            this.fullNotification = webRecipientClient.getReceivedNotification(sharedSteps.getSentNotification().getIun(), null);
        });
    }

    @And("lato utente l'elemento di timeline della notifica {string} con deliveryDetailCode {string} non è visibile")
    public void matteo(String category, String deliveryDetailCode) {
        Assertions.assertNotNull(this.fullNotification);
        fullNotification.getTimeline().forEach(x -> log.info(x.toString()));
        it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.TimelineElementV23 timelineElement =
                fullNotification.getTimeline().stream()
                        .filter(x -> x.getCategory().toString().equals(category) &&
                                x.getDetails() != null &&
                                x.getDetails().getDeliveryDetailCode().equals(deliveryDetailCode))
                        .findFirst().orElse(null);
        Assertions.assertNull(timelineElement);
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

        String iun =sharedSteps.getIunVersionamento();


        try {
            OffsetDateTime scheduleDate = Objects.requireNonNull(webRecipientClient.getReceivedNotification(iun, null).getTimeline().stream().filter(elem -> Objects.requireNonNull(elem.getCategory()).equals(TimelineElementCategoryV23.SCHEDULE_REFINEMENT)).findAny().get().getDetails()).getSchedulingDate();
            OffsetDateTime refinementDate = webRecipientClient.getReceivedNotification(iun, null).getTimeline().stream().filter(elem -> Objects.requireNonNull(elem.getCategory()).equals(TimelineElementCategoryV23.REFINEMENT)).findAny().get().getTimestamp();
            log.info("scheduleDate : {}", scheduleDate);
            log.info("refinementDate : {}", refinementDate);

            Assertions.assertEquals(scheduleDate,refinementDate);

        }catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("l'allegato {string} può essere correttamente recuperato da {string}")
    public void attachmentCanBeCorrectlyRetrievedBy(String attachmentName, String recipient) {
        //TODO Modificare
        sharedSteps.selectUser(recipient);
        NotificationAttachmentDownloadMetadataResponse downloadResponse = getReceivedNotificationAttachment(attachmentName);

        if (downloadResponse!= null && downloadResponse.getRetryAfter()!= null && downloadResponse.getRetryAfter()>0){
            try {
                await().atMost(downloadResponse.getRetryAfter()* 3L, TimeUnit.MILLISECONDS);
                 downloadResponse = getReceivedNotificationAttachment(attachmentName);
            } catch (RuntimeException exc) {
                log.error("Await error exception: {}", exc.getMessage());
                throw exc;
            }
        }

        if (!"F24".equalsIgnoreCase(attachmentName)){
            AtomicReference<String> Sha256 = new AtomicReference<>("");
            NotificationAttachmentDownloadMetadataResponse finalDownloadResponse = downloadResponse;
            Assertions.assertDoesNotThrow(() -> {
                byte[] bytes = Assertions.assertDoesNotThrow(() ->
                        b2bUtils.downloadFile(Objects.requireNonNull(finalDownloadResponse).getUrl()));
                Sha256.set(b2bUtils.computeSha256(new ByteArrayInputStream(bytes)));
            });
            Assertions.assertEquals(Sha256.get(), Objects.requireNonNull(downloadResponse).getSha256());
        }else {
            NotificationAttachmentDownloadMetadataResponse finalDownloadResponse = downloadResponse;
                Assertions.assertDoesNotThrow(() ->
                        b2bUtils.downloadFile(Objects.requireNonNull(finalDownloadResponse).getUrl()));

        }
    }

    private NotificationAttachmentDownloadMetadataResponse getReceivedNotificationAttachment(String attachmentName) {

        return webRecipientClient.getReceivedNotificationAttachment(
                sharedSteps.getSentNotification().getIun(),
                attachmentName,
                null,0);
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

            if (downloadResponse!= null && downloadResponse.getRetryAfter()!= null && downloadResponse.getRetryAfter()>0){
                try {
                    await().atMost(downloadResponse.getRetryAfter()* 3L, TimeUnit.MILLISECONDS);
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
        Assertions.assertTrue((this.notificationError == null && sharedSteps.consumeNotificationError() == null) );
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

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23 timelineElementInternalCategory= it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23.AAR_GENERATION;
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

            String finalKeySearch = "safestorage://"+keySearch;
            try {
                this.webRecipientClient.getDocumentsWeb(sharedSteps.getSentNotification().getIun(), it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.DocumentCategory.AAR,finalKeySearch,null);
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

        PnPaB2bUtils.Pair<OffsetDateTime, OffsetDateTime> dates =  getStartDateAndEndDate(data);

        searchParam.startDate = dates.getValue1();
        searchParam.endDate = dates.getValue2();
        searchParam.subjectRegExp = data.getOrDefault("subjectRegExp", null);
        String iun = data.getOrDefault("iunMatch", null);
        searchParam.iunMatch = ((iun != null && iun.equalsIgnoreCase("ACTUAL") ? sharedSteps.getSentNotification().getIun() : iun));
        searchParam.size = Integer.parseInt(data.getOrDefault("size", "10"));
        return searchParam;
    }

    @DataTableType
    public NotificationSearchParamWebPA convertNotificationSearchParamWebPA(Map<String, String> data) {
        NotificationSearchParamWebPA searchParam = new NotificationSearchParamWebPA();

        PnPaB2bUtils.Pair<OffsetDateTime, OffsetDateTime> dates =  getStartDateAndEndDate(data);

        searchParam.startDate = dates.getValue1();
        searchParam.endDate = dates.getValue2();
        searchParam.subjectRegExp = data.getOrDefault("subjectRegExp", null);
        String iun = data.getOrDefault("iunMatch", null);
        searchParam.iunMatch = ((iun != null && iun.equalsIgnoreCase("ACTUAL") ? sharedSteps.getSentNotification().getIun() : iun));
        searchParam.size = Integer.parseInt(data.getOrDefault("size", "10"));
        return searchParam;
    }

    private PnPaB2bUtils.Pair<OffsetDateTime, OffsetDateTime> getStartDateAndEndDate (Map<String, String> data){

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
            default -> throw new IllegalArgumentException();
        }
    }

    @And("viene inserito un recapito legale {string}")
    public void nuovoRecapitoLegale(String pec) {
        postRecipientLegalAddress("default", pec, null, true);
    }

    @When("viene richiesto l'inserimento della pec {string}")
    public void perLUtenteVieneSettatoLaPec(String pec) {
        postRecipientLegalAddress("default", pec, "00000", false);
    }

    @When("viene richiesto l'inserimento del numero di telefono {string}")
    public void vieneRichiestoLInserimentoDelNumeroDiTelefono(String phone) {
        postRecipientCourtesyAddress ("default", phone, CourtesyChannelType.SMS, "00000", false);
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
    public void perLUtenteVieneSettatoLaPecPerIlComune(String pec,String pa) {
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

    @And("viene cancellata l'email di cortesia per il comune {string}")
    public void vieneCancellataEmailDiCortesiaDalComune( String pa) {
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

    private static class NotificationSearchParam {
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
}