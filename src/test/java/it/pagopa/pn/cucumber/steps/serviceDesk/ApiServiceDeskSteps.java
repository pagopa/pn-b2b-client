package it.pagopa.pn.cucumber.steps.serviceDesk;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.config.PnB2bClientTimingConfigs;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationAttachmentBodyRef;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationAttachmentDigests;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationDocument;
import it.pagopa.pn.client.b2b.pa.service.IPServiceDeskClientImpl;
import it.pagopa.pn.client.b2b.pa.service.impl.PnExternalServiceClientImpl;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.model.*;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDeskIntegration.model.*;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static it.pagopa.pn.cucumber.steps.utilitySteps.ThreadUtils.threadWaitMilliseconds;


@Slf4j
public class ApiServiceDeskSteps {
    private final PnPaB2bUtils b2bUtils;
    private final SharedSteps sharedSteps;
    private final IPServiceDeskClientImpl ipServiceDeskClient;
    private final PnExternalServiceClientImpl safeStorageClient;
    private final RestTemplate restTemplate;
    private final NotificationRequest notificationRequest;
    private final AnalogAddress analogAddress;
    private final CreateOperationRequest createOperationRequest;
    private final VideoUploadRequest videoUploadRequest;
    private final SearchNotificationRequest searchNotificationRequest;
    private final ApplicationContext ctx;
    private final Integer workFlowWait;
    @Value("${pn.retention.videotime.preload}")
    private Integer retentionTimePreLoad;
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ01234556789";
    private static final String CF_corretto = "CLMCST42R12D969Z";
    private static final String CF_errato = "CPNTMS85T15H703WCPNTMS85T15H703W|";
    private static final String CF_errato2 = "CPNTM@85T15H703W";
    private final String CF_vuoto = null;
    private static final String ticketid_errato = "XXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxX";
    private final String ticketid_vuoto = null;
    private static final String ticketoperationid_errato = "abcdfeghilm";
    private final String ticketoperationid_vuoto = null;
    private static final Integer delay = 420000;
    private static final Integer workFlowWaitDefault = 31000;
    private List<PaSummary> listPa = null;
    private HttpStatusCodeException notificationError;
    private SearchNotificationsResponse searchNotificationsResponse;
    private SearchNotificationsRequest searchNotificationsRequest;
    private ProfileRequest profileRequest;
    private ProfileResponse profileResponse;
    private NotificationDetailResponse notificationDetailResponse;
    private TimelineResponse timelineResponse;
    private DocumentsRequest documentsRequest;
    private DocumentsResponse documentsResponse;
    private ResponseApiKeys responseApiKeys;
    private NotificationsUnreachableResponse notificationsUnreachableResponse;
    private OperationsResponse operationsResponse;
    private VideoUploadResponse videoUploadResponse;
    private NotificationDocument notificationDocument;
    private SearchResponse searchResponse;


    @Autowired
    public ApiServiceDeskSteps(SharedSteps sharedSteps, RestTemplate restTemplate, ApplicationContext ctx,
                               PnExternalServiceClientImpl safeStorageClient, PnB2bClientTimingConfigs timingConfigs) {
        this.sharedSteps = sharedSteps;
        this.restTemplate = restTemplate;
        this.ctx = ctx;
        this.safeStorageClient = safeStorageClient;
        this.workFlowWait = timingConfigs.getWorkflowWaitMillis();
        this.b2bUtils = sharedSteps.getB2bUtils();
        this.ipServiceDeskClient = sharedSteps.getServiceDeskClient();
        this.notificationRequest = new NotificationRequest();
        this.analogAddress = new AnalogAddress();
        this.createOperationRequest = new CreateOperationRequest();
        this.videoUploadRequest = new VideoUploadRequest();
        this.searchNotificationRequest = new SearchNotificationRequest();
    }

    @Given("viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il {string}")
    public void createVerifyUnreachableRequest(String cf) {
        createRequestByFiscalCode(cf, true);
    }

    @Given("viene creata una nuova richiesta per invocare il servizio UNREACHABLE con cf vuoto")
    public void createVerifyUnreachableRequest() {
        createRequestByFiscalCode(CF_vuoto, true);
    }

    @When("viene invocato il servizio UNREACHABLE")
    public void NotificationsUnreachableResponse() {
        try {
            Assertions.assertDoesNotThrow(() -> {
                notificationsUnreachableResponse = ipServiceDeskClient.notification(notificationRequest);
            });

            Assertions.assertNotNull(notificationsUnreachableResponse);
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{Il cliente presenta un numero di pratiche" + (notificationsUnreachableResponse == null ? "NULL" : notificationsUnreachableResponse.getNotificationsCount()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @When("viene invocato il servizio UNREACHABLE con errore")
    public void notificationsUnreachableResponseWithError() {
        notificationsUnreachableResponseWithErrorSteps();
    }

    @Then("la risposta del servizio UNREACHABLE è {long}")
    public void verifyNotificationsUnreachableResponse(Long count) {
        Long notificationsCount = notificationsUnreachableResponse.getNotificationsCount();
        Assertions.assertEquals(notificationsCount, count);
        log.info("Presenza notifiche per il CF" + this.notificationRequest.getTaxId() + ":" + notificationsCount);
    }

    @Then("il servizio risponde con errore {string}")
    public void operationProducedAnError(String statusCode) {
        operationProducedAnErrorSteps(statusCode);
        log.info("Errore: " + notificationError.getStatusCode() + " " + notificationError.getMessage() + " " + notificationError.getCause());
    }

    @Given("viene comunicato il nuovo indirizzo con {string} {string} {string} {string} {string} {string} {string} {string} {string}")
    public void createNewAddressRequest(String fullname, String namerow2, String address, String addressRow2, String cap, String city, String city2, String pr, String country) {
        setAnalogAddressFields(fullname, namerow2, address, addressRow2, cap, city, city2, pr, country);
    }

    @Given("viene comunicato il nuovo indirizzo con campo indirizzo vuoto")
    public void createNewAddressRequestAddressNull() {
        analogAddress.setFullname("Prova indirizzo vuoto");
        analogAddress.setNameRow2("interno 5");
        analogAddress.setAddress(null);
        analogAddress.setAddressRow2("prova");
        analogAddress.setCap("84100");
        analogAddress.setCity("Napoli");
        analogAddress.setCity2("frazione");
        analogAddress.setPr("NA");
        analogAddress.setCountry("Italia");
    }

    @Given("viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con {string}")
    public void createOperationReq(String cf) {
        createOperationRequestSteps(cf);
    }

    @Given("viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION per con {string} {string} {string}")
    public void createOperationReq(String cf, String ticketid, String ticketOperationid) {
        if (cf.equals("CF_vuoto")) {
            createOperationRequest.setTaxId(CF_vuoto);
        } else {
            createOperationRequest.setTaxId(cf);
        }

        switch (ticketid) {
            case "ticketid_vuoto":
                createOperationRequest.setTicketId(ticketid_vuoto);
                break;

            case "ticketid_errato":
                createOperationRequest.setTicketId(ticketid_errato);
                break;
            default:
                createOperationRequest.setTicketId(ticketid);
        }

        switch (ticketOperationid) {
            case "ticketoperationid_vuoto":
                createOperationRequest.setTicketOperationId(ticketoperationid_vuoto);
                break;
            case "ticketoperationid_errato":
                createOperationRequest.setTicketOperationId(ticketoperationid_errato);
                break;
            default:
                createOperationRequest.setTicketOperationId(ticketOperationid);
        }
        createOperationRequest.setAddress(analogAddress);
    }

    @Given("viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con cf vuoto")
    public void createOperationReqCFVuoto() {
        createOperationRequestSteps(CF_vuoto);
    }

    @When("viene invocato il servizio CREATE_OPERATION con errore")
    public void createOperationResponseWithError() {
        createOperationResponseWithErrorSteps();
    }

    @When("viene invocato il servizio CREATE_OPERATION")
    public void createOperationResponse() {
        try {
            Assertions.assertDoesNotThrow(() -> {
                operationsResponse = ipServiceDeskClient.createOperation(createOperationRequest);
            });

            Assertions.assertNotNull(operationsResponse);
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{Id operation " + (operationsResponse == null ? "NULL" : operationsResponse.getOperationId()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @Then("la risposta del servizio CREATE_OPERATION risponde con esito positivo")
    public void verifyCreateOperationResponse() {
        String idOperation = operationsResponse.getOperationId();
        Assertions.assertNotNull(idOperation);
        log.info("L'operation di creato per il CF:" + createOperationRequest.getTaxId() + " " + idOperation);
    }

    @Given("viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO")
    public void createPreUploadVideoRequest() throws Exception{
        createPreUploadVideoRequestDocumentSteps();
    }

    @Given("viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con formato non corretto")
    public void createPreUploadVideoRequestFormatVideoNotValid() throws Exception {
        notificationDocument = newDocument("classpath:/video.avi");
        createPreUploadVideoRequestDocumentSteps();
    }

    @When("viene invocato il servizio UPLOAD VIDEO")
    public void preUploadVideoResponse() {
        try {
            Assertions.assertDoesNotThrow(() -> {
                videoUploadResponse = ipServiceDeskClient.presignedUrlVideoUpload(operationsResponse.getOperationId(), videoUploadRequest);
            });

            Assertions.assertNotNull(videoUploadResponse);
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{Upload video" + (videoUploadResponse == null ? "NULL" : videoUploadResponse.getUrl()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @When("viene invocato il servizio UPLOAD VIDEO con {string} con errore")
    public void preUploadVideoResponse(String operationId) {
        preUploadVideoResponseSteps(operationId);
    }

    @When("viene invocato il servizio UPLOAD VIDEO con operationid vuoto")
    public void preUploadVideoResponseOperationIdNull() {
        try {
            videoUploadResponse = ipServiceDeskClient.presignedUrlVideoUpload(null, videoUploadRequest);
            threadWaitMilliseconds(getWorkFlowWait());
            Assertions.assertNotNull(videoUploadResponse);
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }

    @When("viene invocato il servizio UPLOAD VIDEO con errore")
    public void preUploadVideoResponseWithError() {
        try {
            log.error("Operation id:" + operationsResponse.getOperationId());
            videoUploadResponse = ipServiceDeskClient.presignedUrlVideoUpload(operationsResponse.getOperationId(), videoUploadRequest);
            threadWaitMilliseconds(getWorkFlowWait());
            Assertions.assertNotNull(videoUploadResponse);
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }

    @Given("viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con sha256 vuoto")
    public void createPreUploadVideoRequestSha256Null() {
        notificationDocument = newDocument("classpath:/video.mp4");
        videoUploadRequest.setPreloadIdx(getPrefixedRandomAlphaNumeric(5));
        videoUploadRequest.setSha256(null);
        videoUploadRequest.setContentType("application/octet-stream");
    }

    @Given("viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con sha256 errato")
    public void createPreUploadVideoRequestSha256Error() throws Exception {
        String sha256 = getSha256ByVideoDocument();
        videoUploadRequest.setPreloadIdx(getPrefixedRandomAlphaNumeric(5));
        videoUploadRequest.setSha256(sha256 + "ERR");
        videoUploadRequest.setContentType("application/octet-stream");

    }

    @Given("viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con preloadIdx vuoto")
    public void createPreUploadVideoRequestPreloadIdxNull() throws Exception {
        createPreUploadVideoRequestSteps(null, "application/octet-stream");
    }

    @Given("viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con preloadIdx errato")
    public void createPreUploadVideoRequestpreloadIdxNotValid() throws Exception {
        String resourceName = "classpath:/test.xml";
        String sha256 = computeSha256(resourceName);
        videoUploadRequest.setPreloadIdx("@@||!!");
        videoUploadRequest.setSha256(sha256);
        videoUploadRequest.setContentType("application/octet-stream");
    }

    @Given("viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con ContentType vuoto")
    public void createPreUploadVideoRequestContentTypeull() throws Exception {
        createPreUploadVideoRequestSteps(getPrefixedRandomAlphaNumeric(5), null);
    }

    @Then("la risposta del servizio UPLOAD VIDEO risponde con esito positivo")
    public void verifyUploadVideoResponse() {
        String url = videoUploadResponse.getUrl();
        Assertions.assertNotNull(url);
        log.info("generata la url:" + url);
        String secretKey = videoUploadResponse.getSecret();
        Assertions.assertNotNull(secretKey);
        String fileKey = videoUploadResponse.getFileKey();
        Assertions.assertNotNull(fileKey);
        log.info("generata la file key:" + fileKey);
    }

    @Given("viene creata una nuova richiesta per invocare il servizio SEARCH per il {string}")
    public void createSearchRequest(String cf) {
        createRequestByFiscalCode(cf, false);
    }

    @When("viene invocato il servizio SEARCH con errore")
    public void searchResponseWithError() {
        searchResponseWithErrorSteps();
    }

    @When("viene invocato il servizio SEARCH")
    public void searchResponse() {
        searchResponseSteps();
    }

    @When("viene invocato il servizio SEARCH con delay")
    public void searchResponseWithDelay() {
        threadWaitMilliseconds(delay);
        searchResponseSteps();
    }

    @Then("Il servizio SEARCH risponde con esito positivo")
    public void verifySearchResponse() {
        List<OperationResponse> lista = searchResponse.getOperations();
        Assertions.assertNotNull(lista);
        log.info("SEARCH " + searchResponse.getOperations().toString());
        //Analalisi output
        for (OperationResponse element : lista) {
            log.info("STAMPA ELEMENTO LISTA " + element.toString());
            Assertions.assertNotNull(element.getOperationId());
            log.info("CF attuale" + element.getTaxId());
            log.info("CF da cercare" + searchNotificationRequest.getTaxId());
            checkOperationResponse(element);
            log.info("STATO NOTIFICA " + lista.get(0).getNotificationStatus().getStatus().getValue());
        }
    }

    @Then("Il servizio SEARCH risponde con esito positivo e lo stato della consegna è {string}")
    public void verifySearchResponseWithStatus(String status) {
        String operationIdToSearch = operationsResponse.getOperationId();
        log.info("OPERATION ID TO SEARCH: " + operationIdToSearch);
        List<OperationResponse> lista = searchResponse.getOperations();
        Assertions.assertNotNull(lista);
        log.info("SEARCH " + searchResponse.getOperations().toString());
        checkOperationResponseList(lista, operationIdToSearch, status, false, null);
    }

    @Then("Il servizio SEARCH risponde con esito positivo con spedizione multipla e lo stato della consegna è {string}")
    public void verifySearchResponseWithStatusSplitNotify(String status) {
        boolean multiOperation = false;
        String operationIdToSearch = operationsResponse.getOperationId();
        log.info("OPERATION ID TO SEARCH: " + operationIdToSearch);
        List<OperationResponse> lista = searchResponse.getOperations();
        Assertions.assertNotNull(lista);
        log.info("SEARCH " + searchResponse.getOperations().toString());
        //Viene controllato che lo stato delle operation è superiore a 1
        List<OperationResponse> listaSplit = new ArrayList<>();
        //Analisi output
        for (OperationResponse element : lista) {
            String actualOperationId = element.getOperationId();
            log.info("ACTUAL OPERATION ID: " + actualOperationId);
            Assertions.assertNotNull(operationIdToSearch);
            if (actualOperationId.compareTo(operationIdToSearch) == 0) {
                listaSplit.add(element);
                log.info("AGGIUNTO ELEMENTO: " + actualOperationId);
            }
            Assertions.assertNotNull(listaSplit);
        }
        int numberOperation = listaSplit.size();

        log.info("Numero di response che contengono l'operation id " + numberOperation);
        if (numberOperation > 1) {
            multiOperation = true;
        }
        Assertions.assertTrue(multiOperation);
        checkOperationResponseList(listaSplit, operationIdToSearch, status, false, null);
    }

    @Then("Il servizio SEARCH risponde con esito positivo per lo {string} e lo stato della consegna è {string}")
    public void verifySearchResponseWithStatusAndIun(String iun, String status) {
        String operationIdToSearch = operationsResponse.getOperationId();
        log.info("OPERATION ID TO SEARCH: " + operationIdToSearch);
        List<OperationResponse> lista = searchResponse.getOperations();
        Assertions.assertNotNull(lista);
        log.info("SEARCH " + searchResponse.getOperations().toString());
        checkOperationResponseList(lista, operationIdToSearch,  status,  true, iun);
    }

    @Then("Il servizio SEARCH risponde con esito positivo con uncompleted iun lo stato della consegna è {string}")
    public void verifySearchResponseWithStatusAndUncompletedIun(String status) {
        String operationIdToSearch = operationsResponse.getOperationId();
        log.info("OPERATION ID TO SEARCH: " + operationIdToSearch);
        List<OperationResponse> lista = searchResponse.getOperations();
        Assertions.assertNotNull(lista);
        log.info("SEARCH " + searchResponse.getOperations().toString());
        checkOperationResponseList(lista, operationIdToSearch, status, false, null);
    }

    @Then("Il servizio SEARCH risponde con lista vuota")
    public void verifySearchResponseEmpty() {
        List<OperationResponse> lista = searchResponse.getOperations();
        log.info("STAMPA LISTA " + Objects.requireNonNull(lista));
        //   Assertions.assertNull(lista);
        Assertions.assertEquals("[]", lista.toString());
    }

    @Then("il video viene caricato su SafeStorage")
    public void loadFileSafeStorage() {
        loadFileSafeStorageSteps();
        notificationDocument.digests(new NotificationAttachmentDigests().sha256(videoUploadRequest.getSha256()));
    }

    @Then("il video viene caricato su SafeStorage con url scaduta")
    public void loadFileSafeStorageUrlExpired() {
        try {
            threadWaitMilliseconds(3720000);//aspetta 62 minuti
            loadFileSafeStorageSteps();
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }

    @Then("il video viene caricato su SafeStorage con errore")
    public void loadFileSafeStorageWithError() {
        try {
            loadFileSafeStorageSteps();
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }

    @Then("viene effettuato un controllo sulla durata della retention")
    public void retentionCheckPreload() {
        String key = notificationDocument.getRef().getKey();
        log.info("Resouce name" + key);
        threadWaitMilliseconds(900000);
        log.info("Fine delay");
        Assertions.assertTrue(checkRetetion(key, retentionTimePreLoad));
    }

    @Given("viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il {string} con API Key errata")
    public void createVerifyUnreachableRequestWrongApiKey(String cf) {
        createRequestByFiscalCode(cf, true);
    }

    @When("viene invocato il servizio UNREACHABLE con errore con API Key errata")
    public void notificationsUnreachableResponseWithErrorWrongApiKey(){
        notificationsUnreachableResponseWithErrorSteps();
    }

    @Given("viene comunicato il nuovo indirizzo con {string} {string} {string} {string} {string} {string} {string} {string} {string} con API Key errata")
    public void createNewAddressRequestWrongApiKey(String fullname, String namerow2, String address,String addressRow2,String cap, String city, String city2,String pr,String country) {
        setAnalogAddressFields(fullname, namerow2, address, addressRow2, cap, city, city2, pr, country);
    }

    @Given("viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con {string} con API Key errata")
    public void createOperationReqWrongApiKey(String cf) {
        createOperationRequestSteps(cf);
    }

    @When("viene invocato il servizio CREATE_OPERATION con API Key errata con errore")
    public void createOperationResponseWithErrorWrongApiKey() {
        createOperationResponseWithErrorSteps();

    }

    @Given("viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con API Key errata")
    public void createPreUploadVideoRequestWrongApiKey() throws Exception{
        createPreUploadVideoRequestDocumentSteps();
    }

    @When("viene invocato il servizio UPLOAD VIDEO con API Key errata con {string} con errore")
    public void preUploadVideoResponseWrongApiKey(String operationId){
        preUploadVideoResponseSteps(operationId);
    }

    @Given("viene creata una nuova richiesta per invocare il servizio SEARCH per il {string} con API Key errata")
    public void createSearchRequestWrongApiKey(String cf) {
        createRequestByFiscalCode(cf, false);
    }

    @When("viene invocato il servizio SEARCH con API Key errata Key con errore")
    public void searchResponseWithErrorWrongApiKey(){
        searchResponseWithErrorSteps();
    }

    @Then("il servizio risponde con errore {string} con API Key errata")
    public void operationProducedAnErrorWrongApiKey(String statusCode) {
        operationProducedAnErrorSteps(statusCode);
    }

    @Given("viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il {string} senza API Key")
    public void createVerifyUnreachableRequestNoApiKey(String cf) {
        createRequestByFiscalCode(cf, true);
    }

    @When("viene invocato il servizio UNREACHABLE con errore senza API Key")
    public void notificationsUnreachableResponseWithErrorNoApiKey(){
        notificationsUnreachableResponseWithErrorSteps();
    }

    @Given("viene comunicato il nuovo indirizzo con {string} {string} {string} {string} {string} {string} {string} {string} {string} senza API Key")
    public void createNewAddressRequestNoApiKey(String fullname, String namerow2, String address,String addressRow2,String cap, String city, String city2, String pr, String country) {
        setAnalogAddressFields(fullname, namerow2, address, addressRow2, cap, city, city2, pr, country);
    }

    @Given("viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con {string} senza API Key")
    public void createOperationReqNoApiKey(String cf) {
        createOperationRequestSteps(cf);
    }

    @When("viene invocato il servizio CREATE_OPERATION senza API Key con errore")
    public void createOperationResponseWithErrorNoApiKey() {
        createOperationResponseWithErrorSteps();
    }

    @Given("viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO senza API Key")
    public void createPreUploadVideoRequestNoApiKey() throws Exception{
        createPreUploadVideoRequestDocumentSteps();
    }

    @When("viene invocato il servizio UPLOAD VIDEO senza API Key con {string} con errore")
    public void preUploadVideoResponseNoApiKey(String operationId){
        preUploadVideoResponseSteps(operationId);
    }

    @Given("viene creata una nuova richiesta per invocare il servizio SEARCH per il {string} senza API Key")
    public void createSearchRequestNoApiKey(String cf) {
        createRequestByFiscalCode(cf, false);
    }

    @When("viene invocato il servizio SEARCH senza API Key con errore")
    public void searchResponseWithErrorNoApiKey(){
        searchResponseWithErrorSteps();
    }

    @Then("il servizio risponde con errore {string} senza API Key")
    public void operationProducedAnErrorNoApiKey(String statusCode) {
        operationProducedAnErrorSteps(statusCode);
    }

    @And("l'operatore richiede l'elenco di tutte le PA che hanno effettuato on boarding")
    public void elencoPaOnboarding() {
        try {
            Assertions.assertDoesNotThrow(() -> {
                listPa = ipServiceDeskClient.getListOfOnboardedPA();
            });
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{Elenco delle PA onbordate " + (listPa == null ? "NULL" : listPa.size()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @Then("Il servizio risponde con esito positivo con la lista delle PA")
    public void verifyServiceResponse() {
        Assertions.assertNotNull(listPa);
        Assertions.assertFalse(listPa.isEmpty());
    }

    @Given("l'operatore richiede l'elenco di tutti i messaggi di cortesia inviati con cf vuoto")
    public void lOperatoreRichiedeLElencoDiDiTuttiIMessaggiDiCortesiaInviatiConCfVuoto() {
        lOperatoreRichiedeLElencoDiDiTuttiIMessaggiDiCortesiaInviatiSteps(CF_vuoto);
    }

    @Given("l'operatore richiede l'elenco di tutti i messaggi di cortesia inviati con cf errato {string}")
    public void lOperatoreRichiedeLElencoDiDiTuttiIMessaggiDiCortesiaInviatiConCfErrato(String cf) {
        lOperatoreRichiedeLElencoDiDiTuttiIMessaggiDiCortesiaInviatiSteps(cf);
    }

    @Given("l'operatore richiede l'elenco di tutti i messaggi di cortesia inviati con recipientType vuoto")
    public void lOperatoreRichiedeLElencoDiDiTuttiIMessaggiDiCortesiaInviatiConRecipientTypeVuoto() {
        try {
            searchNotificationsRequest = new SearchNotificationsRequest();
            searchNotificationsRequest.setTaxId(CF_corretto);
            searchNotificationsRequest.setRecipientType(null);
            searchNotificationsResponse = ipServiceDeskClient.searchNotificationsFromTaxId(10, null, null, null, searchNotificationsRequest);
            threadWaitMilliseconds(getWorkFlowWait());
            Assertions.assertNotNull(searchNotificationsResponse);
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }

    @Given("l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId {string} recipientType  {string} e con searchPageSize {string} searchNextPagesKey {string} startDate {string} endDate {string}")
    public void lOperatoreRichiedeLElencoDiTuttiIMessaggiDiCortesiaInviatiConCfErratoERecipientType(String taxId, String recipientType, String searchPageSize, String searchNextPagesKey, String startDate, String endDate) {
        Integer size = setSearchPageSize(searchPageSize);
        String nextPagesKey = setNextPagesKey(searchNextPagesKey);

        checkElencoDelleNotificheRicevuteSteps(taxId, recipientType, searchPageSize, searchNextPagesKey, startDate, endDate, false);

        if(size==50 && nextPagesKey==null){
            Assertions.assertEquals(50, Objects.requireNonNull(searchNotificationsResponse.getResults()).size());
        }

        List<CourtesyMessage> listCourtesyMessage = new ArrayList<>();
        for (NotificationResponse notificationResponseTmp: Objects.requireNonNull(searchNotificationsResponse.getResults())) {
            if (notificationResponseTmp.getCourtesyMessages()!=null && !notificationResponseTmp.getCourtesyMessages().isEmpty()){
                listCourtesyMessage.add(notificationResponseTmp.getCourtesyMessages().get(0));
            }
        }
        Assertions.assertFalse(listCourtesyMessage.isEmpty());
    }

    @Then("Il servizio risponde correttamente")
    public void ilServizioRispondeCorrettamente() {
        Assertions.assertNull(notificationError);
    }

    @Then("Il servizio risponde correttamente con presenza delle apiKey")
    public void ilServizioRispondeCorrettamenteConPresenzaApikey() {
        Assertions.assertNotNull(responseApiKeys.getTotal());
    }

    @Then("Il servizio risponde correttamente con presenza di allegati {string}")
    public void ilServizioRispondeCorrettamenteAllegatiTrue(String presenzaAllegati) {
        Assertions.assertNull(notificationError);
        Assertions.assertNotNull(documentsResponse);
        if ("true".equalsIgnoreCase(presenzaAllegati)) {
            Assertions.assertTrue(documentsResponse.getDocumentsAvailable());
        } else {
            Assertions.assertFalse(documentsResponse.getDocumentsAvailable());
        }
    }

    @Given("come operatore devo accedere ai dati del profilo di un utente \\(PF e PG) di Piattaforma Notifiche con taxId {string} e recipientType  {string}")
    public void comeOperatoreDevoAccedereAiDatiDelProfiloDiUnUtentePFEPGDiPiattaformaNotifiche(String taxId, String recipientType) {
        try {
            profileRequest = new ProfileRequest();
            if ("NULL".equalsIgnoreCase(taxId)) {
                profileRequest.setTaxId(null);
            } else if ("VUOTO".equalsIgnoreCase(taxId)) {
                profileRequest.setTaxId("");
            } else {
                profileRequest.setTaxId(setTaxID(taxId));
            }

            if (!"NULL".equalsIgnoreCase(recipientType)) {
                setRecipientType(recipientType);
            }

            profileResponse = ipServiceDeskClient.getProfileFromTaxId(profileRequest);
            Assertions.assertNotNull(profileResponse);
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }

    private void checkElencoDelleNotificheRicevuteSteps(String taxId, String recipientType, String searchPageSize, String searchNextPagesKey, String startDate, String endDate, boolean multiTaxId) {
        try {
            Integer size = setSearchPageSize(searchPageSize);
            String nextPagesKey = setNextPagesKey(searchNextPagesKey);
            OffsetDateTime sDate =  setDateSearch(startDate);
            OffsetDateTime eDate =  setDateSearch(endDate);

            searchNotificationsRequest = new SearchNotificationsRequest();
            if ("NULL".equalsIgnoreCase(taxId)) {
                searchNotificationsRequest.setTaxId(null);
            } else if ("VUOTO".equalsIgnoreCase(taxId)) {
                searchNotificationsRequest.setTaxId("");
            } else {
                if(multiTaxId) {
                    searchNotificationsRequest.setTaxId(setTaxID(setTaxID(taxId)));
                } else {
                    searchNotificationsRequest.setTaxId(setTaxID(taxId));
                }
            }

            if (!"NULL".equalsIgnoreCase(recipientType)) {
                setRecipientType(recipientType);
            }

            searchNotificationsResponse = ipServiceDeskClient.searchNotificationsFromTaxId(size, nextPagesKey, sDate, eDate, searchNotificationsRequest);
            Assertions.assertNotNull(searchNotificationsResponse);
            Assertions.assertNotNull(searchNotificationsResponse.getResults());
            Assertions.assertFalse(searchNotificationsResponse.getResults().isEmpty());

            if(size==1 && nextPagesKey==null){
                Assertions.assertEquals(1, searchNotificationsResponse.getResults().size());
            }
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }

    @Given("come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId {string} recipientType  {string} e con searchPageSize {string} searchNextPagesKey {string} startDate {string} endDate {string}")
    public void comeOperatoreDevoAccedereAllElencoDelleNotificheRicevuteDaUnUtenteDiPiattaformaNotificheConCfERecipientType(String taxId, String recipientType, String searchPageSize, String searchNextPagesKey, String startDate, String endDate) {
        checkElencoDelleNotificheRicevuteSteps(taxId, recipientType, searchPageSize, searchNextPagesKey, startDate, endDate, true);
    }

    @Given("come operatore devo accedere ai dettagli di una notifica di cui conosco l’identificativo \\(IUN) {string}")
    public void comeOperatoreDevoAccedereAiDettagliDiUnaNotificaDiCuiConoscoLIdentificativoIUN(String iun) {
        try {
            profileRequest = new ProfileRequest();
            if ("NULL".equalsIgnoreCase(iun)) {
                notificationDetailResponse = ipServiceDeskClient.getNotificationFromIUN(null);
            } else if ("VUOTO".equalsIgnoreCase(iun)) {
                notificationDetailResponse = ipServiceDeskClient.getNotificationFromIUN("");
            } else {
                notificationDetailResponse = ipServiceDeskClient.getNotificationFromIUN(iun);
            }
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }

    @And("invocazione servizio per recupero dettaglio notifica")
    public void recuperoDettaglioNotifica() {
        try {
            Assertions.assertNotNull(searchNotificationsResponse);
            Assertions.assertNotNull(searchNotificationsResponse.getResults());
            Assertions.assertFalse(searchNotificationsResponse.getResults().isEmpty());
            notificationDetailResponse = ipServiceDeskClient.getNotificationFromIUN(searchNotificationsResponse.getResults().get(0).getIun());
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }

    @And("verifica IsMultiRecipients nel dettaglio notifica")
    public void recuperoVerifyIsMultiRecipientsDettaglioNotifica() {
        try {
            notificationDetailResponse = ipServiceDeskClient.getNotificationFromIUN(sharedSteps.getSentNotification().getIun());
            Assertions.assertNotNull(notificationDetailResponse);
            Assertions.assertNotEquals(Boolean.TRUE, notificationDetailResponse.getIsMultiRecipients());
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }

    @And("invocazione servizio per recupero dettaglio timeline notifica con taxId {string} e iun {string}")
    public void invocazioneServizioPerRecuperoDettaglioTimelineNotifica(String taxid, String iun) {
        try {
            //Parametri di Richiesta utilizzati per il recupero della notifica
            String taxidQuery = searchNotificationsRequest.getTaxId();
            RecipientType recipientType = searchNotificationsRequest.getRecipientType();

            //Nuovi Parametri di Richiesta per il recupero della timeline
            searchNotificationsRequest = new SearchNotificationsRequest();
            searchNotificationsRequest.setRecipientType(recipientType);

            String iunSearch = getIunSearch(iun);
            boolean diversoTaxid = false;
            if ("NULL".equalsIgnoreCase(taxid)) {
                searchNotificationsRequest.setTaxId(null);
            } else if ("VUOTO".equalsIgnoreCase(taxid)) {
                searchNotificationsRequest.setTaxId("");
            } else {
                String resultTaxID = setTaxID(taxid);
                searchNotificationsRequest.setTaxId(resultTaxID);
                if (!resultTaxID.equalsIgnoreCase(taxidQuery)) {
                    diversoTaxid = true;
                }
            }
            timelineResponse = ipServiceDeskClient.getTimelineOfIUNAndTaxId(iunSearch, searchNotificationsRequest);
            if (diversoTaxid) {
                Assertions.assertNull(timelineResponse);
            }
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }

    @Then("invocazione servizio per recupero dettaglio timeline notifica multidestinatario con taxId {string} e iun {string} per il  destinatario {int}")
    public void invocazioneServizioPerRecuperoDettaglioTimelineNotificaMultidestinatarioConCfEIun(String taxId, String iun, Integer destinatario) {
        try {
            searchNotificationsRequest = new SearchNotificationsRequest();
            searchNotificationsRequest.setRecipientType(RecipientType.PF);
            if ("NULL".equalsIgnoreCase(taxId)) {
                searchNotificationsRequest.setTaxId(null);
            } else if ("VUOTO".equalsIgnoreCase(taxId)) {
                searchNotificationsRequest.setTaxId("");
            } else {
                searchNotificationsRequest.setTaxId(setTaxID(taxId));
            }
            String iunSearch = setIUNNotifica(iun);
            timelineResponse = ipServiceDeskClient.getTimelineOfIUNAndTaxId(iunSearch, searchNotificationsRequest);
            Assertions.assertNotNull(timelineResponse);
            TimelineElement timelineElement = null;
            for (TimelineElement element : timelineResponse.getTimeline()) {
                if (!destinatario.equals(element.getDetail().getRecIndex())) {
                    timelineElement = element;
                    break;
                }
            }
            Assertions.assertNull(timelineElement);
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }

    @Given("come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN {string} e taxId {string}  recipientType  {string}")
    public void comeOperatoreDevoEffettuareUnCheckSullaDisponibilitaValiditaEDimensioneDegliAllegatiConIUNRecipientType(String iun, String taxId, String recipientType) {
        try {
            documentsRequest = new DocumentsRequest();
            if (sharedSteps.getSentNotification()!= null){
                setRecipientType(sharedSteps.getSentNotification().getRecipients().get(0).getRecipientType().getValue());
            } else {
                setRecipientType(recipientType);
            }

            if ("NULL".equalsIgnoreCase(taxId)) {
                documentsRequest.setTaxId(null);
            } else if ("VUOTO".equalsIgnoreCase(taxId)) {
                documentsRequest.setTaxId("");
            } else if ("NO_SET".equalsIgnoreCase(taxId)) {
                documentsRequest.setTaxId(sharedSteps.getSentNotification().getRecipients().get(0).getTaxId());
            } else {
                documentsRequest.setTaxId(setTaxID(taxId));
            }
            String iunSearch = setIUNNotifica(iun);
            documentsResponse = ipServiceDeskClient.getDocumentsOfIUN(iunSearch, documentsRequest);
            Assertions.assertNotNull(documentsResponse);
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }

    @Then("come operatore devo accedere alla lista delle Notifiche per le quali l’utente risulta destinatario come {string} di una persona fisica o di una persona giuridica con taxId {string} recipientType  {string} e con searchPageSize {string} searchNextPagesKey {string} startDate {string} endDate {string} searchMandateId {string} searchInternalId {string}")
    public void comeOperatoreDevoAccedereAllaListaDelleNotifichePerLeQualiLUtenteRisultaDestinatarioComeDelegatoDiUnaPersonaFisicaODiUnaPersonaGiuridicaConCfRecipientTypeEConSearchPageSizeSearchNextPagesKeyStartDateEndDate(String type, String taxId, String recipientType, String searchPageSize, String searchNextPagesKey, String startDate, String endDate, String searchMandateId, String searchInternalId) {
        try {
            Assertions.assertNotNull(profileResponse);
            if ("delegato".equalsIgnoreCase(type)) {
                Assertions.assertNotNull(profileResponse.getDelegateMandates());
                Assertions.assertFalse(profileResponse.getDelegateMandates().isEmpty());
            }else if ("delegante".equalsIgnoreCase(type)) {
                Assertions.assertNotNull(profileResponse.getDelegatorMandates());
                Assertions.assertFalse(profileResponse.getDelegatorMandates().isEmpty());
            }

            Integer size = setSearchPageSize(searchPageSize);
            String nextPagesKey = setNextPagesKey(searchNextPagesKey);
            OffsetDateTime sDate, eDate;
            OffsetDateTime offsetEndDt = OffsetDateTime.of(OffsetDateTime.now().getYear(), OffsetDateTime.now().getMonth().getValue(), OffsetDateTime.now().getDayOfMonth(), 0, 0, 0, 0,
                    ZoneOffset.UTC);
            // define a formatter for the output
            DateTimeFormatter myFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.S'Z'");

            // create a new OffsetDateTime with time information
            OffsetDateTime realEndOfDay = offsetEndDt
                    .withHour(23)
                    .withMinute(59)
                    .withSecond(59)
                    .withNano(0);

            if (!"NULL".equalsIgnoreCase(startDate)) {
                sDate = setDateSearch(startDate);
            } else {
                sDate = OffsetDateTime.parse(myFormatter.format(offsetEndDt));
            }

            if (!"NULL".equalsIgnoreCase(endDate)) {
                eDate = setDateSearch(endDate);
            } else {
                eDate =  OffsetDateTime.parse(myFormatter.format(realEndOfDay));
            }

            String mandateIdSearch = getTaxIdDelegator(type, taxId, searchMandateId);
            String delegateInternalIdSearch = getTaxIdDelegator(type, taxId, searchInternalId);

            searchNotificationsResponse = ipServiceDeskClient.searchNotificationsAsDelegateFromInternalId(mandateIdSearch, delegateInternalIdSearch,recipientType, size, nextPagesKey, sDate, eDate);
            Assertions.assertNotNull(searchNotificationsResponse);
            Assertions.assertNotNull(searchNotificationsResponse.getResults());
            Assertions.assertFalse(searchNotificationsResponse.getResults().isEmpty());
            //  Assertions.assertTrue(searchNotificationsResponse.getResults().get(0).getIun().equalsIgnoreCase(sharedSteps.getSentNotification().getIun()));
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }

    @Given("come operatore devo accedere alla lista di tutte le notifiche depositate da un ente \\(mittente) su Piattaforma Notifiche in un range temporale con paId {string} e con searchPageSize {string} searchNextPagesKey {string} startDate {string} endDate {string}")
    public void comeOperatoreDevoAccedereAllaListaDiTutteLeNotificheDepositateDaUnEnteMittenteSuPiattaformaNotificheInUnRangeTemporaleConPaIdEConSearchPageSizeSearchNextPagesKeyStartDateEndDate(String paId, String searchPageSize, String searchNextPagesKey, String startDate, String endDate) {
        try {
            Integer size = setSearchPageSize(searchPageSize);
            String nextPagesKey = setNextPagesKey(searchNextPagesKey);
            OffsetDateTime sDate =  setDateSearch(startDate);
            OffsetDateTime eDate =  setDateSearch(endDate);
            PaNotificationsRequest paNotificationsRequest = new PaNotificationsRequest();
            paNotificationsRequest.setId(setPaID( paId));
            paNotificationsRequest.setStartDate(sDate);
            paNotificationsRequest.setEndDate(eDate);
            searchNotificationsResponse = ipServiceDeskClient.searchNotificationsFromSenderId(size, nextPagesKey, paNotificationsRequest);
            Assertions.assertNotNull(searchNotificationsResponse);
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }

    private String getIunSearch(String iun) {
        if ("VUOTO".equalsIgnoreCase(iun)) {
            return "";
        } else if ("".equalsIgnoreCase(iun)) {
            Assertions.assertNotNull(searchNotificationsResponse);
            Assertions.assertNotNull(searchNotificationsResponse.getResults());
            Assertions.assertFalse(searchNotificationsResponse.getResults().isEmpty());
            return searchNotificationsResponse.getResults().get(0).getIun();
        } else {
            return iun;
        }
    }
    private void invocazioneServizioPerRecuperoDettaglioNotificaConIunSteps(String iun, boolean isNotification) {
        try {
            String iunSearch = getIunSearch(iun);
            if(isNotification) {
                notificationDetailResponse = ipServiceDeskClient.getNotificationFromIUN(iunSearch);
                Assertions.assertNotNull(notificationDetailResponse);
            } else {
                timelineResponse = ipServiceDeskClient.getTimelineOfIUN(iunSearch);
                Assertions.assertNotNull(timelineResponse);
            }
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }

    @And("invocazione servizio per recupero dettaglio notifica con iun {string}")
    public void invocazioneServizioPerRecuperoDettaglioNotificaConIun(String iun) {
        invocazioneServizioPerRecuperoDettaglioNotificaConIunSteps(iun, true);
    }

    @And("invocazione servizio per recupero timeline notifica con iun {string}")
    public void invocazioneServizioPerRecuperoTimelineNotificaConIun(String iun) {
        invocazioneServizioPerRecuperoDettaglioNotificaConIunSteps(iun, false);
    }

    @Given("come operatore devo accedere alle informazioni relative alle richieste di API Key avanzate da un Ente mittente di notifiche sulla Piattaforma {string}")
    public void comeOperatoreDevoAccedereAlleInformazioniRelativeAlleRichiesteDiAPIKeyAvanzateDaUnEnteMittenteDiNotificheSullaPiattaforma(String paId) {
        try {
            String paIDSearch =  setPaID(paId);
            responseApiKeys = ipServiceDeskClient.getApiKeys(paIDSearch);
            Assertions.assertNotNull(responseApiKeys);
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }

    private void createRequestByFiscalCode(String cf, boolean isNotificationRequest) {
        if(cf == null) {
            notificationRequest.setTaxId(CF_vuoto);
            return;
        }

        switch (cf) {
            case "CF_vuoto":
                if(isNotificationRequest) {
                    notificationRequest.setTaxId(CF_vuoto);
                } else {
                    searchNotificationRequest.setTaxId(CF_vuoto);
                }
                break;
            case "CF_errato":
                if(isNotificationRequest) {
                    notificationRequest.setTaxId(CF_errato);
                } else {
                    searchNotificationRequest.setTaxId(CF_errato);
                }
                break;
            case "CF_errato2":
                if(isNotificationRequest) {
                    notificationRequest.setTaxId(CF_errato2);
                } else {
                    searchNotificationRequest.setTaxId(CF_errato2);
                }
                break;
            default:
                if(isNotificationRequest) {
                    notificationRequest.setTaxId(cf);
                } else {
                    searchNotificationRequest.setTaxId(cf);
                }
                log.info("Inserito CF:" + cf);
        }
    }


    private String getTaxIdDelegator(String type, String taxId, String searchInternalId) {
        if ("NO_SET".equalsIgnoreCase(searchInternalId)) {
            if ("delegato".equalsIgnoreCase(type)) {
                String taxIdDelegator = setTaxID(taxId);
                Assertions.assertNotNull(profileResponse.getDelegateMandates());
                for (Mandate mandate: profileResponse.getDelegateMandates()) {
                    if (taxIdDelegator.equalsIgnoreCase(mandate.getTaxId())) {
                        return mandate.getDelegateInternalId();
                    }
                }
            } else if ("delegante".equalsIgnoreCase(type)) {
                Assertions.assertNotNull(profileResponse.getDelegatorMandates());
                for (Mandate mandate: profileResponse.getDelegatorMandates()) {
                    String taxIdDelegator = setTaxID(taxId);
                    if (taxIdDelegator.equalsIgnoreCase(mandate.getTaxId())) {
                        return mandate.getDelegateInternalId();
                    }
                }
            }
        }
        return searchInternalId;
    }

    private void createOperationRequestSteps(String cf) {
        log.info("CF:" + cf);
        createOperationRequest.setTaxId(cf);
        String ticketId = getPrefixedRandomAlphaNumeric(12);
        log.info("ticketId:" + ticketId);
        createOperationRequest.setTicketId(ticketId);
        String ticketOperationId = getPrefixedRandomAlphaNumeric(7);
        log.info("ticketOperationId:" + ticketOperationId);
        createOperationRequest.setTicketOperationId(ticketOperationId);
        createOperationRequest.setAddress(analogAddress);
    }

    private void createPreUploadVideoRequestDocumentSteps() throws Exception {
        notificationDocument = newDocument("classpath:/video.mp4");
        String resourceName = notificationDocument.getRef().getKey();
        log.info("Resource name:" + resourceName);
        String sha256 = computeSha256(resourceName);
        log.info("sha:" + sha256);
        videoUploadRequest.setPreloadIdx(getPrefixedRandomAlphaNumeric(5));
        videoUploadRequest.setSha256(sha256);
        videoUploadRequest.setContentType("application/octet-stream");
    }

    private void preUploadVideoResponseSteps(String operationId) {
        try {
            videoUploadResponse = ipServiceDeskClient.presignedUrlVideoUpload(operationId, videoUploadRequest);
            threadWaitMilliseconds(getWorkFlowWait());
            Assertions.assertNotNull(videoUploadResponse);
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }

    private void operationProducedAnErrorSteps(String statusCode) {
        notificationError.getStatusCode();
        Assertions.assertEquals(notificationError.getStatusCode().toString().substring(0, 3), statusCode);
    }

    private void createOperationResponseWithErrorSteps() {
        try {
            operationsResponse = ipServiceDeskClient.createOperation(createOperationRequest);
            threadWaitMilliseconds(getWorkFlowWait());
            Assertions.assertNotNull(notificationsUnreachableResponse);
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }

    private void notificationsUnreachableResponseWithErrorSteps() {
        try {
            notificationsUnreachableResponse = ipServiceDeskClient.notification(notificationRequest);
            threadWaitMilliseconds(getWorkFlowWait());
            Assertions.assertNotNull(notificationsUnreachableResponse);
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }

    private void searchResponseWithErrorSteps() {
        try {
            searchResponse = ipServiceDeskClient.searchOperationsFromTaxId(searchNotificationRequest);
            threadWaitMilliseconds(getWorkFlowWait());
            Assertions.assertNotNull(searchResponse);
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }

    private void createPreUploadVideoRequestSteps(String preloadIdx, String contentType) throws Exception {
        String sha256 = getSha256ByVideoDocument();
        videoUploadRequest.setPreloadIdx(preloadIdx);
        videoUploadRequest.setSha256(sha256);
        videoUploadRequest.setContentType(contentType);
    }

    private void loadFileSafeStorageSteps() {
        // notificationDocument = newDocument("classpath:/video.mp4");
        String resourceName = notificationDocument.getRef().getKey();
        log.info("Resouce name" + resourceName);
        loadToPresigned(videoUploadResponse.getUrl(), videoUploadResponse.getSecret(), videoUploadRequest.getSha256(), resourceName);
        notificationDocument.getRef().setKey(videoUploadResponse.getFileKey());
        notificationDocument.getRef().setVersionToken("v1");
    }

    private void searchResponseSteps() {
        try {
            Assertions.assertDoesNotThrow(() -> {
                searchResponse = ipServiceDeskClient.searchOperationsFromTaxId(searchNotificationRequest);
            });

            Assertions.assertNotNull(searchResponse);
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{Ricerca effettuata " + (searchResponse == null ? "NULL" : Objects.requireNonNull(searchResponse.getOperations()).toString()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    private void lOperatoreRichiedeLElencoDiDiTuttiIMessaggiDiCortesiaInviatiSteps(String cf) {
        try {
            searchNotificationsRequest = new SearchNotificationsRequest();
            searchNotificationsRequest.setTaxId(cf);
            searchNotificationsRequest.setRecipientType(RecipientType.PF);
            searchNotificationsResponse = ipServiceDeskClient.searchNotificationsFromTaxId(10, null, null, null, searchNotificationsRequest);
            threadWaitMilliseconds(getWorkFlowWait());
            Assertions.assertNotNull(searchNotificationsResponse);
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }

    private void checkOperationResponseList(List<OperationResponse> lista, String operationIdToSearch, String status, boolean checkIun, String iun) {
        //Analisi output
        boolean findIun = false;
        boolean foundOperationId = false;
        for (OperationResponse element : lista) {
            log.info("STAMPA ELEMENTO LISTA " + element.toString());
            String actualOperationId = element.getOperationId();
            Assertions.assertNotNull(actualOperationId);
            Assertions.assertNotNull(operationIdToSearch);

            if (actualOperationId.compareTo(operationIdToSearch) == 0 && !foundOperationId) {
                foundOperationId = true;

                checkOperationResponse(element);
                //controllo sullo status
                if (operationIdToSearch.compareTo(actualOperationId) == 0) {
                    log.info("STATO NOTIFICA " + element.getNotificationStatus().getStatus().getValue());
                    Assertions.assertEquals(element.getNotificationStatus().getStatus().getValue(), status);
                }

                if(checkIun) {
                    List<SDNotificationSummary> listaiuns = element.getIuns();
                    for (SDNotificationSummary acutalIun : Objects.requireNonNull(listaiuns)) {
                        //Verifica se lo iun è presente nella lista
                        log.info("IUN ATTUALE " + acutalIun.getIun());
                        if (acutalIun.getIun().compareTo(iun) == 0 && !findIun) {
                            findIun = true;
                        }
                    }
                }
            }
        }

        //Se non viene trovato l'id operation lancio eccezione
        try {
            Assertions.assertTrue(foundOperationId);
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() + "{L'operation id non è presente nella lista" + foundOperationId + "}";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }

        if(checkIun) {
            //Se non viene trovato lo IUN lancio operazione
            try {
                Assertions.assertTrue(findIun);
            } catch (AssertionFailedError assertionFailedError) {
                String message = assertionFailedError.getMessage() + "{Lo iun non è associato al CF" + searchNotificationRequest.getTaxId() + "}";
                throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
            }
        }
    }

    private void checkOperationResponse(OperationResponse operationResponse) {
        //Viene verificato che l'operation id generato fa parte della lista
        Assertions.assertEquals(operationResponse.getTaxId(), searchNotificationRequest.getTaxId());
        Assertions.assertNotNull(operationResponse.getIuns());
        Assertions.assertNotNull(operationResponse.getUncompletedIuns());
        Assertions.assertNotNull(operationResponse.getNotificationStatus());
        Assertions.assertNotNull(operationResponse.getOperationCreateTimestamp());
        Assertions.assertNotNull(operationResponse.getOperationUpdateTimestamp());
    }

    private String getSha256ByVideoDocument() throws Exception {
        notificationDocument = newDocument("classpath:/video.mp4");
        String resourceName = notificationDocument.getRef().getKey();
        return computeSha256(resourceName);
    }

    private void loadToPresigned(String url, String secret, String sha256, String resource) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-type", "application/octet-stream");
        headers.add("x-amz-checksum-sha256", sha256);
        headers.add("x-amz-meta-secret", secret);
        HttpEntity<Resource> req = new HttpEntity<>(ctx.getResource(resource), headers);
        restTemplate.exchange(URI.create(url), HttpMethod.PUT, req, Object.class);
    }

    public Integer getWorkFlowWait() {
        if (workFlowWait == null) return workFlowWaitDefault;
        return workFlowWait;
    }

    private String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public NotificationDocument newDocument(String resourcePath) {
        return new NotificationDocument()
                .contentType("application/mp4")
                .ref(new NotificationAttachmentBodyRef().key(resourcePath));
    }

    private String computeSha256(String resName) throws IOException {
        Resource res = ctx.getResource(resName);
        return computeSha256(res);
    }

    private String computeSha256(Resource res) throws IOException {
        return b2bUtils.computeSha256(res.getInputStream());
    }

    private boolean checkRetetion(String fileKey, Integer retentionTime) {
        PnExternalServiceClientImpl.SafeStorageResponse safeStorageResponse = safeStorageClient.safeStorageInfoPnServiceDesk(fileKey);
        LocalDateTime localDateTimeNow = LocalDate.now().atStartOfDay();
        OffsetDateTime now = OffsetDateTime.of(localDateTimeNow, ZoneOffset.of("Z"));
        OffsetDateTime retentionUntil = OffsetDateTime.parse(safeStorageResponse.getRetentionUntil());
        log.info("now: " + now);
        log.info("retentionUntil: " + retentionUntil);
        long between = ChronoUnit.DAYS.between(now, retentionUntil);
        log.info("Difference: " + between);
        return retentionTime == between;
    }

    private String getPrefixedRandomAlphaNumeric(Integer count) {
        return "AUT" + randomAlphaNumeric(count);
    }

    private void setAnalogAddressFields(String fullname, String namerow2, String address, String addressRow2, String cap, String city, String city2, String pr, String country) {
        analogAddress.setFullname(fullname);
        analogAddress.setNameRow2(namerow2);
        analogAddress.setAddress(address);
        analogAddress.setAddressRow2(addressRow2);
        analogAddress.setCap(cap);
        analogAddress.setCity(city);
        analogAddress.setCity2(city2);
        analogAddress.setPr(pr);
        analogAddress.setCountry(country);
    }

    public OffsetDateTime setDateSearch( String dateInputString ) {
        OffsetDateTime resultDate = null;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        OffsetDateTime sentAt = OffsetDateTime.now();

        if (!"NULL".equalsIgnoreCase(dateInputString)) {
            LocalDateTime localDate = LocalDate.parse(dateInputString, dateTimeFormatter).atStartOfDay();
            resultDate = OffsetDateTime.of(localDate, sentAt.getOffset());
        }
        return resultDate;
    }

    public String setTaxID(String taxId) {
        String result;
        result = switch (taxId) {
            case "Mario Gherkin" -> sharedSteps.getMarioGherkinTaxID();
            case "Mario Cucumber" -> sharedSteps.getMarioCucumberTaxID();
            case "CucumberSpa" -> sharedSteps.getCucumberSpataxId();
            case "GherkinSrl" -> sharedSteps.getGherkinSrltaxId();
            default -> null;
        };
        return result;
    }

    public void setRecipientType(String recipientType) {
        switch (recipientType) {
            case "PF":
                if (searchNotificationsRequest != null) {
                    searchNotificationsRequest.setRecipientType(RecipientType.PF);
                }
                if (profileRequest != null) {
                    profileRequest.setRecipientType(RecipientType.PF);
                }
                if (documentsRequest != null) {
                    documentsRequest.setRecipientType(RecipientType.PF);
                }
                break;
            case "PG":
                if (searchNotificationsRequest != null) {
                    searchNotificationsRequest.setRecipientType(RecipientType.PG);
                }
                if (profileRequest != null) {
                    profileRequest.setRecipientType(RecipientType.PG);
                }
                if (documentsRequest != null) {
                    documentsRequest.setRecipientType(RecipientType.PG);
                }
                break;
            default:
                if (searchNotificationsRequest != null) {
                    searchNotificationsRequest.setRecipientType(null);
                }
                if (profileRequest != null) {
                    profileRequest.setRecipientType(null);
                }
                if (documentsRequest != null) {
                    documentsRequest.setRecipientType(null);
                }
        }
    }

    public String setIUNNotifica(String iun){
        String iunSearch = null;
        if ("VUOTO".equalsIgnoreCase(iun)) {
            iunSearch = "";
        } else if ("NO_SET".equalsIgnoreCase(iun)) {
            if (searchNotificationsResponse!= null && searchNotificationsResponse.getResults()!= null && !searchNotificationsResponse.getResults().isEmpty()) {
                iunSearch = searchNotificationsResponse.getResults().get(0).getIun();
            } else if (sharedSteps.getSentNotification()!= null) {
                iunSearch = sharedSteps.getSentNotification().getIun();
            }
        } else {
            iunSearch = iun;
        }
        return iunSearch;
    }

    public String setPaID(String paId) {
        String paIDSearch = null;
        if ("VUOTO".equalsIgnoreCase(paId)) {
            paIDSearch = "";
        } else if ("NO_SET".equalsIgnoreCase(paId)) {
            for (PaSummary paSummary: listPa) {
                paIDSearch = paSummary.getId();
                if (paSummary.getName().contains("Milano") || paSummary.getName().contains("Verona") || paSummary.getName().contains("Palermo")) {
                    paIDSearch = paSummary.getId();
                    break;
                }
            }
        }else {
            paIDSearch = paId;
        }
        return paIDSearch;
    }

    public Integer setSearchPageSize(String searchPageSize) {
        int size = 10;
        if (!"NULL".equalsIgnoreCase(searchPageSize)) {
            size = Integer.parseInt(searchPageSize);
        }
        return size;
    }

    public String setNextPagesKey(String searchNextPagesKey) {
        String nextPagesKey = null;
        if (!"NULL".equalsIgnoreCase(searchNextPagesKey)) {
            nextPagesKey = searchNextPagesKey;
        }
        return nextPagesKey;
    }
}