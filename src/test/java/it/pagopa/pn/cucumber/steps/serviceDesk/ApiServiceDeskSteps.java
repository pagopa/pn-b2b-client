package it.pagopa.pn.cucumber.steps.serviceDesk;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.config.PnB2bClientTimingConfigs;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV28;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV29;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationAttachmentBodyRef;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationAttachmentDigests;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationDocument;
import it.pagopa.pn.client.b2b.pa.service.IPServiceDeskClient;
import it.pagopa.pn.client.b2b.pa.service.impl.PnExternalServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.wrapper.ApiResult;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.model.*;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDeskIntegration.model.*;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import it.pagopa.pn.cucumber.steps.utilitySteps.Destinatario;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
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

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static it.pagopa.pn.cucumber.steps.utilitySteps.Costanti.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;


@Slf4j
public class ApiServiceDeskSteps {
    @Value("${pn.iun.withf24Payment.colombo}")
    private String iunWithF24Payment;

    @Value("${pn.iun.withPagoPaPayment.colombo}")
    private String iunWithPagoPAPayment;

    @Value("${pn.iun.withoutPayment.colombo}")
    private String iunWithoutPayment;

    public static final String IUN_ERRATO = "JRDT-XAPH-JQYW-202312-J-1";
    private final SharedSteps sharedSteps;
    private final IPServiceDeskClient ipServiceDeskClient;
    private final PnExternalServiceClientImpl safeStorageClient;
    private final RestTemplate restTemplate;
    private final NotificationRequest notificationRequest;
    private final AnalogAddress analogAddress;
    private final CreateOperationRequest createOperationRequest;
    private CreateActOperationRequest createActOperationRequestV1;
    private CreateActOperationRequestV2 createActOperationRequestV2;
    private final VideoUploadRequest videoUploadRequest;
    private final SearchNotificationRequest searchNotificationRequest;
    private final ApplicationContext ctx;
    private final Integer workFlowWait;
    @Value("${pn.retention.videotime.preload}")
    private Integer retentionTimePreLoad;
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ01234556789";
    private static final String CF_CORRETTO = "CLMCST42R12D969Z";
    private static final String CF_ADA = "LVLDAA85T50G702B";
    private static final String CF_ERRATO = "CPNTMS85T15H703WCPNTMS85T15H703W|";
    private static final String PIVA_ERRATA = "1234567899999999999999999999999999999";
    private static final String CF_ERRATO_2 = "CPNTM@85T15H703W";
    private final String cfVuoto = null;
    private static final String TICKET_ID_ERRATO = "XXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxX";
    private final String ticketIdVuoto = null;
    private static final String TICKET_OPERATION_ID_ERRATO = "abcdfeghilm";
    private final String ticketOperationIdVuoto = null;
    private static final Integer DELAY = 420000;
    private static final Integer WORK_FLOW_WAIT_DEFAULT = 31000;
    private List<PaSummary> listPa = null;
    private HttpStatusCodeException notificationError;
    private SearchNotificationsResponse searchNotificationsResponse;
    private NotificationRecipientDetailResponse notificationRecipientDetailResponse;
    private SearchNotificationsRequest searchNotificationsRequest;
    private ProfileRequest profileRequest;
    private ProfileResponse profileResponse;
    private NotificationDetailResponse notificationDetailResponse;
    private TimelineResponse timelineResponse;
    private DocumentsRequest documentsRequest;
    private DocumentsResponse documentsResponse;
    private ResponseApiKeys responseApiKeys;
    private NotificationsUnreachableResponse notificationsUnreachableResponse;
    private OperationsResponse operationsResponseV1;
    private CreateOperationsResponseV2 operationsResponseV2;
    private VideoUploadResponse videoUploadResponse;
    private NotificationDocument notificationDocument;
    private SearchResponse searchResponse;
    private OperationResponse searchOperationResponse;
    private String operationId;
    private GetOperationsResponseV2 getOperationsResponseV2;
    private ApiResult httpResponse;
    private String statusOperationResponse;

    @Autowired
    public ApiServiceDeskSteps(SharedSteps sharedSteps, RestTemplate restTemplate, ApplicationContext ctx,
                               PnExternalServiceClientImpl safeStorageClient, PnB2bClientTimingConfigs timingConfigs) {
        this.sharedSteps = sharedSteps;
        this.restTemplate = restTemplate;
        this.ctx = ctx;
        this.safeStorageClient = safeStorageClient;
        this.workFlowWait = timingConfigs.getWorkflowWaitMillis();
        this.ipServiceDeskClient = sharedSteps.getServiceDeskClient();
        this.notificationRequest = new NotificationRequest();
        this.analogAddress = new AnalogAddress();
        this.createOperationRequest = new CreateOperationRequest();
        this.createActOperationRequestV1 = new CreateActOperationRequest();
        this.videoUploadRequest = new VideoUploadRequest();
        this.searchNotificationRequest = new SearchNotificationRequest();
    }

    @And("viene chiamato service desk e si controlla la presenza dell'elemento {string} nella response")
    public void invocazioneServizioPerVerificaElementoTimelineNEllaResponse(String elemento) {
        if (sharedSteps.getNotificationIun() != null) {
            timelineResponse = ipServiceDeskClient.getTimelineOfIUN(sharedSteps.getNotificationIun());

            Assertions.assertNotNull(timelineResponse);
            Assertions.assertNotNull(timelineResponse.getTimeline());

            boolean hasRefinementCategory = timelineResponse.getTimeline().stream()
                    .anyMatch(entry -> elemento.equalsIgnoreCase(entry.getCategory().toString()));

            Assertions.assertTrue(hasRefinementCategory, "La categoria " + elemento + " non è presente nella timeline.");
        }
    }

    @And("si verifica che lo stato della notifica recuperata sia: {string}")
    public void verifyNotificationStatus(String expectedStatus) {
        Assertions.assertEquals(timelineResponse.getIunStatus().getValue(), expectedStatus);
    }

    @Given("viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il {string}")
    public void createVerifyUnreachableRequest(String cf) {
        createRequestByFiscalCode(cf, true);
    }

    @Given("viene creata una nuova richiesta per invocare il servizio UNREACHABLE con cf vuoto")
    public void createVerifyUnreachableRequest() {
        createRequestByFiscalCode(cfVuoto, true);
    }

    @When("viene invocato il servizio UNREACHABLE")
    public void NotificationsUnreachableResponse() {
        try {
            Assertions.assertDoesNotThrow(() -> {
                notificationsUnreachableResponse = ipServiceDeskClient.notification(notificationRequest);
            });
            threadWait(getWorkFlowWait());
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
        try {
            operationProducedAnErrorSteps(statusCode);
            log.info("Errore: " + notificationError.getStatusCode() + " " + notificationError.getMessage() + " " + notificationError.getCause());
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
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
            createOperationRequest.setTaxId(cfVuoto);
        } else {
            createOperationRequest.setTaxId(cf);
        }

        switch (ticketid) {
            case "ticketid_vuoto" -> createOperationRequest.setTicketId(ticketIdVuoto);
            case "ticketid_errato" -> createOperationRequest.setTicketId(TICKET_ID_ERRATO);
            default -> createOperationRequest.setTicketId(ticketid);
        }

        switch (ticketOperationid) {
            case "ticketoperationid_vuoto" -> createOperationRequest.setTicketOperationId(ticketOperationIdVuoto);
            case "ticketoperationid_errato" -> createOperationRequest.setTicketOperationId(TICKET_OPERATION_ID_ERRATO);
            default -> createOperationRequest.setTicketOperationId(ticketOperationid);
        }
        createOperationRequest.setAddress(analogAddress);
    }

    @Given("viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con cf vuoto")
    public void createOperationReqCFVuoto() {
        createOperationRequestSteps(cfVuoto);
    }

    @When("viene invocato il servizio CREATE_OPERATION con errore")
    public void createOperationResponseWithError() {
        createOperationResponseWithErrorSteps();
    }

    @When("viene invocato il servizio CREATE_OPERATION")
    public void createOperationResponse() {
        try {
            Assertions.assertDoesNotThrow(() -> {
                operationsResponseV1 = ipServiceDeskClient.createOperation(createOperationRequest);
            });
            threadWait(getWorkFlowWait());
            Assertions.assertNotNull(operationsResponseV1);
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{Id operation " + (operationsResponseV1 == null ? "NULL" : operationsResponseV1.getOperationId()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @Then("la risposta del servizio CREATE_OPERATION risponde con esito positivo")
    public void verifyCreateOperationResponse() {
        String idOperation = operationsResponseV1.getOperationId();
        Assertions.assertNotNull(idOperation);
        this.operationId = idOperation;
        log.info("L'operation di creato per il CF:" + createOperationRequest.getTaxId() + " " + idOperation);
    }

    @Given("viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO")
    public void createPreUploadVideoRequest() throws Exception {
        createPreUploadVideoRequestDocumentSteps();
    }

    @Given("viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO per il video {string}")
    public void createPreUploadVideoRequest(String video) throws Exception {
        createPreUploadVideoRequestDocumentSteps(video);
    }

    @When("viene invocato il servizio UPLOAD VIDEO")
    public void preUploadVideoResponse() {
        try {
            Assertions.assertDoesNotThrow(() -> {
                videoUploadResponse = ipServiceDeskClient.presignedUrlVideoUpload(operationsResponseV1.getOperationId(), videoUploadRequest);
            });
            threadWait(getWorkFlowWait());
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
            threadWait(getWorkFlowWait());
            Assertions.assertNotNull(videoUploadResponse);
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }

    @When("viene invocato il servizio UPLOAD VIDEO con errore")
    public void preUploadVideoResponseWithError() {
        try {
            log.error("Operation id:" + operationsResponseV1.getOperationId());
            videoUploadResponse = ipServiceDeskClient.presignedUrlVideoUpload(operationsResponseV1.getOperationId(), videoUploadRequest);
            threadWait(getWorkFlowWait());
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
        threadWait(DELAY);
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
        if (operationId != null) {
            searchOperationResponse = searchResponse.getOperations().stream().filter(op -> op.getOperationId().equals(operationId)).findFirst().orElse(null);
            assertThat(searchOperationResponse).as("Nel risultato della search non figura l'operation " + operationId).isNotNull();
            log.info("Operation response trovata nella search: {}", searchOperationResponse);
        }
    }

    @Then("Il servizio SEARCH risponde con esito positivo e lo stato della consegna è {string}")
    public void verifySearchResponseWithStatus(String status) {
        String operationIdToSearch = operationsResponseV1.getOperationId();
        log.info("OPERATION ID TO SEARCH: " + operationIdToSearch);
        List<OperationResponse> lista = searchResponse.getOperations();
        Assertions.assertNotNull(lista);
        log.info("SEARCH " + searchResponse.getOperations().toString());
        checkOperationResponseList(lista, operationIdToSearch, status, false, null);
    }

    @Then("Il servizio SEARCH risponde con esito positivo con spedizione multipla e lo stato della consegna è {string}")
    public void verifySearchResponseWithStatusSplitNotify(String status) {
        boolean multiOperation = false;
        String operationIdToSearch = operationsResponseV1.getOperationId();
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

    @Then("Il servizio SEARCH risponde con esito positivo con uncompleted iun lo stato della consegna è {string}")
    public void verifySearchResponseWithStatusAndIun(String iun, String status) {
        String operationIdToSearch = operationsResponseV1.getOperationId();
        log.info("OPERATION ID TO SEARCH: " + operationIdToSearch);
        List<OperationResponse> lista = searchResponse.getOperations();
        Assertions.assertNotNull(lista);
        log.info("SEARCH " + searchResponse.getOperations().toString());
        checkOperationResponseList(lista, operationIdToSearch, status, true, iun);
    }

    @Then("Il servizio SEARCH risponde con lista vuota")
    public void verifySearchResponseEmpty() {
        List<OperationResponse> lista = searchResponse.getOperations();
        log.info("STAMPA LISTA " + Objects.requireNonNull(lista));
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
            threadWait(3720000);//aspetta 62 minuti
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
        threadWait(900000);
        log.info("Fine delay");
        Assertions.assertTrue(checkRetetion(key, retentionTimePreLoad));
    }

    @Given("viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il {string} con API Key errata")
    public void createVerifyUnreachableRequestWrongApiKey(String cf) {
        createRequestByFiscalCode(cf, true);
    }

    @When("viene invocato il servizio UNREACHABLE con errore con API Key errata")
    public void notificationsUnreachableResponseWithErrorWrongApiKey() {
        notificationsUnreachableResponseWithErrorSteps();
    }

    @Given("viene comunicato il nuovo indirizzo con {string} {string} {string} {string} {string} {string} {string} {string} {string} con API Key errata")
    public void createNewAddressRequestWrongApiKey(String fullname, String namerow2, String address, String addressRow2, String cap, String city, String city2, String pr, String country) {
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
    public void createPreUploadVideoRequestWrongApiKey() throws Exception {
        createPreUploadVideoRequestDocumentSteps();
    }

    @When("viene invocato il servizio UPLOAD VIDEO con API Key errata con {string} con errore")
    public void preUploadVideoResponseWrongApiKey(String operationId) {
        preUploadVideoResponseSteps(operationId);
    }

    @Given("viene creata una nuova richiesta per invocare il servizio SEARCH per il {string} con API Key errata")
    public void createSearchRequestWrongApiKey(String cf) {
        createRequestByFiscalCode(cf, false);
    }

    @When("viene invocato il servizio SEARCH con API Key errata Key con errore")
    public void searchResponseWithErrorWrongApiKey() {
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
    public void notificationsUnreachableResponseWithErrorNoApiKey() {
        notificationsUnreachableResponseWithErrorSteps();
    }

    @Given("viene comunicato il nuovo indirizzo con {string} {string} {string} {string} {string} {string} {string} {string} {string} senza API Key")
    public void createNewAddressRequestNoApiKey(String fullname, String namerow2, String address, String addressRow2, String cap, String city, String city2, String pr, String country) {
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
    public void createPreUploadVideoRequestNoApiKey() throws Exception {
        createPreUploadVideoRequestDocumentSteps();
    }

    @When("viene invocato il servizio UPLOAD VIDEO senza API Key con {string} con errore")
    public void preUploadVideoResponseNoApiKey(String operationId) {
        preUploadVideoResponseSteps(operationId);
    }

    @Given("viene creata una nuova richiesta per invocare il servizio SEARCH per il {string} senza API Key")
    public void createSearchRequestNoApiKey(String cf) {
        createRequestByFiscalCode(cf, false);
    }

    @When("viene invocato il servizio SEARCH senza API Key con errore")
    public void searchResponseWithErrorNoApiKey() {
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
        lOperatoreRichiedeLElencoDiDiTuttiIMessaggiDiCortesiaInviatiSteps(cfVuoto);
    }

    @Given("l'operatore richiede l'elenco di tutti i messaggi di cortesia inviati con cf errato {string}")
    public void lOperatoreRichiedeLElencoDiDiTuttiIMessaggiDiCortesiaInviatiConCfErrato(String cf) {
        lOperatoreRichiedeLElencoDiDiTuttiIMessaggiDiCortesiaInviatiSteps(cf);
    }

    @Given("l'operatore richiede l'elenco di tutti i messaggi di cortesia inviati con recipientType vuoto")
    public void lOperatoreRichiedeLElencoDiDiTuttiIMessaggiDiCortesiaInviatiConRecipientTypeVuoto() {
        try {
            searchNotificationsRequest = new SearchNotificationsRequest();
            searchNotificationsRequest.setTaxId(CF_CORRETTO);
            searchNotificationsRequest.setRecipientType(null);
            searchNotificationsResponse = ipServiceDeskClient.searchNotificationsFromTaxId(10, null, null, null, searchNotificationsRequest);
            threadWait(getWorkFlowWait());
            Assertions.assertNotNull(searchNotificationsResponse);
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }

    @Given("l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId {string} recipientType  {string} e con searchPageSize {string} searchNextPagesKey {string} startDate {string} endDate {string}")
    public void lOperatoreRichiedeLElencoDiTuttiIMessaggiDiCortesiaInviatiConCfErratoERecipientType(String taxId, String recipientType, String searchPageSize, String searchNextPagesKey, String startDate, String endDate) {
        Integer size = setSearchPageSize(searchPageSize);
        String nextPagesKey = setNextPagesKey(searchNextPagesKey);

        checkElencoDelleNotificheRicevuteSteps(taxId, recipientType, searchPageSize, searchNextPagesKey, startDate, endDate);

        if (searchNotificationsResponse != null) {
            if (size == 50 && nextPagesKey == null) {
                Assertions.assertEquals(50, Objects.requireNonNull(searchNotificationsResponse.getResults()).size());
            }

            List<CourtesyMessage> listCourtesyMessage = new ArrayList<>();
            for (NotificationResponse notificationResponseTmp : Objects.requireNonNull(searchNotificationsResponse.getResults())) {
                if (notificationResponseTmp.getCourtesyMessages() != null && !notificationResponseTmp.getCourtesyMessages().isEmpty()) {
                    listCourtesyMessage.add(notificationResponseTmp.getCourtesyMessages().get(0));
                }
            }
            Assertions.assertFalse(listCourtesyMessage.isEmpty());
        }
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
            } else if ("ERRATO".equalsIgnoreCase(taxId)) {
                if ("PF".equalsIgnoreCase(recipientType)) {
                    profileRequest.setTaxId(CF_ERRATO);
                } else {
                    profileRequest.setTaxId(PIVA_ERRATA);
                }

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

    private void checkElencoDelleNotificheRicevuteSteps(String taxId, String recipientType, String searchPageSize, String searchNextPagesKey, String startDate, String endDate) {
        try {
            Integer size = setSearchPageSize(searchPageSize);
            String nextPagesKey = setNextPagesKey(searchNextPagesKey);
            OffsetDateTime sDate = getDate(startDate);
            OffsetDateTime eDate = getDate(endDate);

            searchNotificationsRequest = new SearchNotificationsRequest();
            if ("NULL".equalsIgnoreCase(taxId)) {
                searchNotificationsRequest.setTaxId(null);
            } else if ("VUOTO".equalsIgnoreCase(taxId)) {
                searchNotificationsRequest.setTaxId("");
            } else if ("ERRATO".equalsIgnoreCase(taxId)) {
                if ("PF".equalsIgnoreCase(recipientType)) {
                    searchNotificationsRequest.setTaxId(CF_ERRATO);
                } else {
                    searchNotificationsRequest.setTaxId(PIVA_ERRATA);
                }
            } else {
                searchNotificationsRequest.setTaxId(setTaxID(taxId));
            }

            if (!"NULL".equalsIgnoreCase(recipientType)) {
                setRecipientType(recipientType);
            }

            searchNotificationsResponse = ipServiceDeskClient.searchNotificationsFromTaxId(size, nextPagesKey, sDate, eDate, searchNotificationsRequest);
            Assertions.assertNotNull(searchNotificationsResponse);
            Assertions.assertNotNull(searchNotificationsResponse.getResults());
            Assertions.assertFalse(searchNotificationsResponse.getResults().isEmpty());

            if (size == 1 && nextPagesKey == null) {
                Assertions.assertEquals(1, searchNotificationsResponse.getResults().size());
            }
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }

    @Given("come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId {string} recipientType  {string} e con searchPageSize {string} searchNextPagesKey {string} startDate {string} endDate {string}")
    public void comeOperatoreDevoAccedereAllElencoDelleNotificheRicevuteDaUnUtenteDiPiattaformaNotificheConCfERecipientType(String taxId, String recipientType, String searchPageSize, String searchNextPagesKey, String startDate, String endDate) {
        checkElencoDelleNotificheRicevuteSteps(taxId, recipientType, searchPageSize, searchNextPagesKey, startDate, endDate);
    }

    @Given("come operatore devo accedere ai dettagli di una notifica di cui conosco l’identificativo \\(IUN) {string}")
    public void comeOperatoreDevoAccedereAiDettagliDiUnaNotificaDiCuiConoscoLIdentificativoIUN(String iun) {
        try {
            profileRequest = new ProfileRequest();
            String iunParameter = iun.equals("NULL") ? null : iun.equals("VUOTO") ? "" : iun;
            notificationDetailResponse = ipServiceDeskClient.getNotificationFromIUN(iunParameter);
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }

    @Given("come operatore devo accedere ai dettagli di una notifica di cui conosco l’identificativo \\(IUN)")
    public void comeOperatoreDevoAccedereAiDettagliDiUnaNotificaDiCuiConoscoLIdentificativoIUN() {
        try {
            profileRequest = new ProfileRequest();
            notificationDetailResponse = ipServiceDeskClient.getNotificationFromIUN(sharedSteps.getNotificationIun());

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
            notificationDetailResponse = ipServiceDeskClient.getNotificationFromIUN(sharedSteps.getNotificationIun());
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
            } else if ("ERRATO".equalsIgnoreCase(taxid)) {
                searchNotificationsRequest.setTaxId(CF_ERRATO);
            } else if ("ADA".equalsIgnoreCase(taxid)) {
                searchNotificationsRequest.setTaxId(CF_ERRATO);
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
            } else if ("ERRATO".equalsIgnoreCase(taxId)) {
                searchNotificationsRequest.setTaxId(CF_ERRATO);
            } else if ("ADA".equalsIgnoreCase(taxId)) {
                searchNotificationsRequest.setTaxId(CF_ADA);
            } else {
                searchNotificationsRequest.setTaxId(setTaxID(taxId));
            }
            String iunSearch = setIUNNotifica(iun);
            timelineResponse = ipServiceDeskClient.getTimelineOfIUNAndTaxId(iunSearch, searchNotificationsRequest);
            Assertions.assertNotNull(timelineResponse);
            TimelineElement timelineElement = null;
            for (TimelineElement element : timelineResponse.getTimeline()) {
                if (!"REQUEST_ACCEPTED".equalsIgnoreCase(element.getCategory().toString()) && !destinatario.equals(element.getDetail().getRecIndex())) {
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
            FullSentNotificationV29 fullSentNotification = sharedSteps.getNotificationIun() != null ? sharedSteps.getSentNotificationLastVersion() : null;
            documentsRequest = new DocumentsRequest();
            if (fullSentNotification != null) {
                setRecipientType(fullSentNotification.getRecipients().get(0).getRecipientType().getValue());
            } else {
                setRecipientType(recipientType);
            }

            if ("NULL".equalsIgnoreCase(taxId)) {
                documentsRequest.setTaxId(null);
            } else if ("VUOTO".equalsIgnoreCase(taxId)) {
                documentsRequest.setTaxId("");
            } else if ("NO_SET".equalsIgnoreCase(taxId)) {
                documentsRequest.setTaxId(fullSentNotification.getRecipients().get(0).getTaxId());
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
            } else if ("delegante".equalsIgnoreCase(type)) {
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
                sDate = getDate(startDate);
            } else {
                sDate = OffsetDateTime.parse(myFormatter.format(offsetEndDt));
            }

            if (!"NULL".equalsIgnoreCase(endDate)) {
                eDate = getDate(endDate);
            } else {
                eDate = OffsetDateTime.parse(myFormatter.format(realEndOfDay));
            }

            String mandateIdSearch = getTaxIdMandate(type, taxId, searchMandateId);
            String delegateInternalIdSearch = getTaxIdInternal(type, taxId, searchInternalId);

            searchNotificationsResponse = ipServiceDeskClient.searchNotificationsAsDelegateFromInternalId(mandateIdSearch, delegateInternalIdSearch, recipientType, size, nextPagesKey, sDate, eDate);
            Assertions.assertNotNull(searchNotificationsResponse);
            Assertions.assertNotNull(searchNotificationsResponse.getResults());
            Assertions.assertFalse(searchNotificationsResponse.getResults().isEmpty());
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }

    @Given("come operatore devo accedere alla lista di notifiche depositate che rientrano nei seguenti criteri:")
    public void retrieveNotificationsFromData(DataTable dataTable) {
        Map<String, String> inputParams = dataTable.asMap();
        String paId = Optional.ofNullable(inputParams.get("paId")).map(this::setPaID).orElse(null);
        String searchNextPagesKey = inputParams.get("searchNextPagesKey");
        Integer searchPageSize = Integer.parseInt(Optional.ofNullable(inputParams.get("searchPageSize")).orElse("10"));
        OffsetDateTime startDate = Optional.ofNullable(inputParams.get("startDate")).map(this::getDate).orElse(null);
        OffsetDateTime endDate = Optional.ofNullable(inputParams.get("endDate")).map(this::getDate).orElse(null);

        PaNotificationsRequest paNotificationsRequest = new PaNotificationsRequest();
        paNotificationsRequest.setId(paId);
        paNotificationsRequest.setStartDate(startDate);
        paNotificationsRequest.setEndDate(endDate);

        try {
            searchNotificationsResponse = ipServiceDeskClient.searchNotificationsFromSenderId(searchPageSize, searchNextPagesKey, paNotificationsRequest);
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
            if (isNotification) {
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

    @And("invocazione servizio per recupero timeline notifica con iun")
    public void invocazioneServizioPerRecuperoTimelineNotificaConIun() {
        invocazioneServizioPerRecuperoDettaglioNotificaConIunSteps(sharedSteps.getNotificationIun(), false);
    }

    @Given("come operatore devo accedere alle informazioni relative alle richieste di API Key avanzate da un Ente mittente di notifiche sulla Piattaforma {string}")
    public void comeOperatoreDevoAccedereAlleInformazioniRelativeAlleRichiesteDiAPIKeyAvanzateDaUnEnteMittenteDiNotificheSullaPiattaforma(String paId) {
        try {
            String paIDSearch = setPaID(paId);
            responseApiKeys = ipServiceDeskClient.getApiKeys(paIDSearch);
            Assertions.assertNotNull(responseApiKeys);
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }

    private void createRequestByFiscalCode(String cf, boolean isNotificationRequest) {
        if (cf == null) {
            notificationRequest.setTaxId(cfVuoto);
            return;
        }

        switch (cf) {
            case "CF_vuoto" -> {
                if (isNotificationRequest) {
                    notificationRequest.setTaxId(cfVuoto);
                } else {
                    searchNotificationRequest.setTaxId(cfVuoto);
                }
            }
            case "CF_errato" -> {
                if (isNotificationRequest) {
                    notificationRequest.setTaxId(CF_ERRATO);
                } else {
                    searchNotificationRequest.setTaxId(CF_ERRATO);
                }
            }
            case "CF_errato2" -> {
                if (isNotificationRequest) {
                    notificationRequest.setTaxId(CF_ERRATO_2);
                } else {
                    searchNotificationRequest.setTaxId(CF_ERRATO_2);
                }
            }
            default -> {
                if (isNotificationRequest) {
                    notificationRequest.setTaxId(cf);
                } else {
                    searchNotificationRequest.setTaxId(cf);
                }
                log.info("Inserito CF:" + cf);
            }
        }
    }


    private String getTaxIdInternal(String type, String taxId, String searchInternalId) {
        if ("NO_SET".equalsIgnoreCase(searchInternalId)) {
            if ("delegato".equalsIgnoreCase(type)) {
                String taxIdDelegate = setTaxID(taxId);
                Assertions.assertNotNull(profileResponse.getDelegateMandates());
                for (Mandate mandate : profileResponse.getDelegateMandates()) {
                    if (taxIdDelegate.equalsIgnoreCase(mandate.getTaxId())) {
                        return mandate.getDelegateInternalId();
                    }
                }
            } else if ("delegante".equalsIgnoreCase(type)) {
                Assertions.assertNotNull(profileResponse.getDelegatorMandates());
                for (Mandate mandate : profileResponse.getDelegatorMandates()) {
                    String taxIdDelegate = setTaxID(taxId);
                    if (taxIdDelegate.equalsIgnoreCase(mandate.getTaxId())) {
                        return mandate.getDelegateInternalId();
                    }
                }
            }
        }
        if ("NULL".equalsIgnoreCase(searchInternalId)) {
            return null;
        }
        return searchInternalId;
    }

    private String getTaxIdMandate(String type, String taxId, String searchMandatelId) {
        if ("NO_SET".equalsIgnoreCase(searchMandatelId)) {
            if ("delegato".equalsIgnoreCase(type)) {
                String taxIdDelegate = setTaxID(taxId);
                Assertions.assertNotNull(profileResponse.getDelegateMandates());
                for (Mandate mandate : profileResponse.getDelegateMandates()) {
                    if (taxIdDelegate.equalsIgnoreCase(mandate.getTaxId())) {
                        return mandate.getMandateId();
                    }
                }
            } else if ("delegante".equalsIgnoreCase(type)) {
                Assertions.assertNotNull(profileResponse.getDelegatorMandates());
                for (Mandate mandate : profileResponse.getDelegatorMandates()) {
                    String taxIdDelegate = setTaxID(taxId);
                    if (taxIdDelegate.equalsIgnoreCase(mandate.getTaxId())) {
                        return mandate.getMandateId();
                    }
                }
            }
        }
        if ("NULL".equalsIgnoreCase(searchMandatelId)) {
            return null;
        }
        return searchMandatelId;
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
        String sha256 = B2bUtils.computeSha256(ctx, resourceName);
        log.info("sha:" + sha256);
        videoUploadRequest.setPreloadIdx(getPrefixedRandomAlphaNumeric(5));
        videoUploadRequest.setSha256(sha256);
        videoUploadRequest.setContentType("application/octet-stream");
    }

    private void createPreUploadVideoRequestDocumentSteps(String name) throws Exception {
        notificationDocument = newDocument("classpath:/" + name);
        String resourceName = notificationDocument.getRef().getKey();
        log.info("Resource name:" + resourceName);
        String sha256 = B2bUtils.computeSha256(ctx, resourceName);
        log.info("sha:" + sha256);
        videoUploadRequest.setPreloadIdx(getPrefixedRandomAlphaNumeric(5));
        videoUploadRequest.setSha256(sha256);
        videoUploadRequest.setContentType("application/octet-stream");
    }

    private void preUploadVideoResponseSteps(String operationId) {
        try {
            videoUploadResponse = ipServiceDeskClient.presignedUrlVideoUpload(operationId, videoUploadRequest);
            threadWait(getWorkFlowWait());
            Assertions.assertNotNull(videoUploadResponse);
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }

    private void operationProducedAnErrorSteps(String statusCode) {
        notificationError.getStatusCode();
        assertThat(notificationError.getStatusCode().toString().substring(0, 3))
                .as("Il codice di errore non coincide con quanto atteso: " + notificationError)
                .isEqualTo(statusCode);
    }

    private void createOperationResponseWithErrorSteps() {
        try {
            operationsResponseV1 = ipServiceDeskClient.createOperation(createOperationRequest);
            threadWait(getWorkFlowWait());
            Assertions.assertNotNull(notificationsUnreachableResponse);
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }

    private void notificationsUnreachableResponseWithErrorSteps() {
        try {
            notificationsUnreachableResponse = ipServiceDeskClient.notification(notificationRequest);
            threadWait(getWorkFlowWait());
            Assertions.assertNotNull(notificationsUnreachableResponse);
        } catch (HttpStatusCodeException exception) {
            this.notificationError = exception;
        }
    }

    private void searchResponseWithErrorSteps() {
        try {
            searchResponse = ipServiceDeskClient.searchOperationsFromTaxId(searchNotificationRequest);
            threadWait(getWorkFlowWait());
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
            threadWait(getWorkFlowWait());
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
            threadWait(getWorkFlowWait());
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

                if (checkIun) {
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

        if (checkIun) {
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
        return B2bUtils.computeSha256(ctx, resourceName);
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
        if (workFlowWait == null) return WORK_FLOW_WAIT_DEFAULT;
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

    public OffsetDateTime getDate(String dateInputString) {
        OffsetDateTime sentAt = OffsetDateTime.now();
        if ("NULL".equalsIgnoreCase(dateInputString)) return null;
        return switch (dateInputString.toUpperCase()) {
            case "LAST_TEN_MINUTES" -> sentAt.minusMinutes(10);
            case "TODAY" -> sentAt.truncatedTo(ChronoUnit.DAYS);
            default -> LocalDate.parse(dateInputString).atStartOfDay().atOffset(ZoneOffset.UTC);
        };
    }

    public String setTaxID(String taxId) {
        String result;
        result = switch (taxId) {
            case MARIO_GHERKIN -> sharedSteps.getDestinatarioRegistry().DESTINATARIO_MARIO_GHERKIN.getTaxId();
            case MARIO_CUCUMBER -> sharedSteps.getDestinatarioRegistry().DESTINATARIO_MARIO_CUCUMBER.getTaxId();
            case CUCUMBER_SPA -> sharedSteps.getDestinatarioRegistry().DESTINATARIO_CUCUMBER_SPA.getTaxId();
            case GHERKIN_SRL -> sharedSteps.getDestinatarioRegistry().DESTINATARIO_GHERKIN_SRL.getTaxId();
            case GALILEO_GALILEI -> sharedSteps.getDestinatarioRegistry().DESTINATARIO_GALILEO_GALILEI.getTaxId();
            default -> null;
        };
        return result;
    }

    public void setRecipientType(String recipientType) {
        switch (recipientType) {
            case PF -> {
                if (searchNotificationsRequest != null) {
                    searchNotificationsRequest.setRecipientType(RecipientType.PF);
                }
                if (profileRequest != null) {
                    profileRequest.setRecipientType(RecipientType.PF);
                }
                if (documentsRequest != null) {
                    documentsRequest.setRecipientType(RecipientType.PF);
                }
            }
            case PG -> {
                if (searchNotificationsRequest != null) {
                    searchNotificationsRequest.setRecipientType(RecipientType.PG);
                }
                if (profileRequest != null) {
                    profileRequest.setRecipientType(RecipientType.PG);
                }
                if (documentsRequest != null) {
                    documentsRequest.setRecipientType(RecipientType.PG);
                }
            }
            default -> {
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
    }

    public String setIUNNotifica(String iun) {
        String iunSearch = null;
        if ("VUOTO".equalsIgnoreCase(iun)) {
            iunSearch = "";
        } else if ("NO_SET".equalsIgnoreCase(iun)) {
            if (searchNotificationsResponse != null && searchNotificationsResponse.getResults() != null && !searchNotificationsResponse.getResults().isEmpty()) {
                iunSearch = searchNotificationsResponse.getResults().get(0).getIun();
            } else if (sharedSteps.getNotificationIun() != null) {
                iunSearch = sharedSteps.getNotificationIun();
            }
        } else {
            iunSearch = iun;
        }
        return iunSearch;
    }

    public String setPaID(String paId) {
        String paIDSearch;
        if (paId == null)
            return sharedSteps.getB2bClient().getSentNotificationV27(sharedSteps.getNotificationIun()).getSenderPaId();
        return switch (paId.toUpperCase()) {
            case "VUOTO" -> "";
            case "NO_SET" -> {
                paIDSearch = listPa.get(listPa.size() - 1).getId();
                for (PaSummary paSummary : listPa) {
                    String name = paSummary.getName();
                    if (name.contains("Milano") || name.contains("Verona") || name.contains("Palermo")) {
                        paIDSearch = paSummary.getId();
                        break;
                    }
                }
                yield paIDSearch;
            }
            default -> paId;
        };
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

    private void threadWait(int wait) {
        try {
            await().atMost(wait, TimeUnit.MILLISECONDS);
        } catch (RuntimeException exception) {
            log.error("Await error exception");
            throw exception;
        }
    }

    @When("come operatore devo accedere ai dettagli dei pagamenti di una notifica con uno iun {string} associata all' utente {string} con uid {string}")
    public void comeOperatoreDevoAccedereAiDettagliDeiPagamentiDiUnaNotificaConUnoIun(String iun, String taxId, String xPagopaPnUid) {
        String taxIdRequest = createTaxId(taxId);
        try {
            notificationRecipientDetailResponse = ipServiceDeskClient.getNotificationRecipientDetail(createUid(xPagopaPnUid), createIUN(iun), new NotificationRecipientDetailRequest().taxId(taxIdRequest));
        } catch (HttpStatusCodeException e) {
            notificationError = e;
        }
    }

    private String createUid(String xPagopaPnUid) {
        return xPagopaPnUid.equals("vuoto") ? null : "ZenDesk";
    }

    @Then("controllo che la risposta del servizio contenta una lista {string}")
    public void controlloCheLaRispostaDelServizioContentaUnaLista(String listType) {
        Assertions.assertNotNull(notificationRecipientDetailResponse);
        Assertions.assertNotNull(notificationRecipientDetailResponse.getRecipient());
        Assertions.assertNotNull(notificationRecipientDetailResponse.getRecipient().getPayments());
        if (listType.equals("VUOTA")) {
            Assertions.assertTrue(notificationRecipientDetailResponse.getRecipient().getPayments().isEmpty());
        } else {
            Assertions.assertFalse(notificationRecipientDetailResponse.getRecipient().getPayments().isEmpty());
        }
    }

    private String createIUN(String iun) {
        return switch (iun.toUpperCase()) {
            case "VUOTO" -> "";
            case "INESISTENTE" -> INEXISTENT_IUN;
            case "NON VALIDO" -> INVALID_IUN;
            case "ASSOCIATO A PAGAMENTO PAGOPA" -> iunWithPagoPAPayment;
            case "ASSOCIATO A PAGAMENTO F24" -> iunWithF24Payment;
            case "NOTIFICA SENZA PAGAMENTI" -> iunWithoutPayment;
            default -> iun;
        };
    }

    private String createTaxId(String user) {
        return switch (user.toUpperCase()) {
            case "VUOTO" -> "";
            case "ERRATO" -> CF_ERRATO;
            default -> setTaxID(user);
        };
    }


    @Then("controllo che i timestamp di creazione e modifica del recapito {string} {string} siano {string} (tra di loro)(.)")
    public void controlloCheITimestampDiCreazioneEModificaDelRecapitoDiSianoDiversiTraDiLoro(String addressType, String addressCategory, String verificationType) {
        Assertions.assertNotNull(profileResponse);
        Assertions.assertNotNull(profileResponse.getUserAddresses());
        List<Address> addressRetrieved = profileResponse.getUserAddresses()
                .stream()
                .filter(data -> checkAddressAndChannelType(addressType, addressCategory, data))
                .toList();

        if (verificationType.equals("vuoti")) {
            Assertions.assertTrue(addressRetrieved.isEmpty());
        } else {
            Assertions.assertFalse(addressRetrieved.isEmpty());
            Assertions.assertEquals(1, addressRetrieved.size());
            Address address = addressRetrieved.get(0);
            Assertions.assertNotNull(address.getCreated());
            Assertions.assertNotNull(address.getLastModified());
            Assertions.assertEquals(verificationType.equals("uguali"), address.getCreated().equals(address.getLastModified()), "i timestamp non sono " + verificationType + " come previsto dallo scenario del test");
        }
    }

    private boolean checkAddressAndChannelType(String addressType, String addressCategory, Address data) {
        boolean result = false;
        if (addressType.equals("cortesia")) {
            if (data.getCourtesyAddressType() != null && data.getCourtesyAddressType().equals(CourtesyAddressType.COURTESY)) {
                result = data.getCourtesyChannelType().equals(CourtesyChannelType.fromValue(addressCategory.toUpperCase()));
            }
        } else if (addressType.equals("legale")) {
            if (data.getLegalAddressType() != null && data.getLegalAddressType().equals(LegalAddressType.LEGAL)) {
                result = data.getLegalChannelType().equals(LegalChannelType.fromValue(addressCategory.toUpperCase()));
            }
        } else throw new IllegalArgumentException("addressType not valid");
        return result;
    }


    // Call center evoluto nuovo sviluppo
    @Then("il servizio risponde con {int}")
    public void verifyCreateOperationResponse(Integer expected) {
        Assertions.assertNotNull(httpResponse);
        Integer statusCode = httpResponse.status().value();
        Assertions.assertEquals(expected, statusCode);
    }


    @Given("viene popolata una richiesta di creazione Act operation {string} con i seguenti dati")
    public void costruisciRichiestaDaMappa(String version, Map<String, String> data) {
        switch (version.toUpperCase()) {
            case "V1" -> createActOperationRequestV1(data);
            case "V2" -> createActOperationRequestV2(data);
            default -> throw new IllegalArgumentException("Invalid version: " + version);
        }
    }

    public static String getValue(Map<String, String> data, String key) {
        if (data.containsKey(key)) {
            return "null".equalsIgnoreCase(data.get(key)) ? null : data.get(key);
        }
        return null;
    }

    @When("viene invocata l'api {string}")
    public void invokeApi(String api) {
        switch (api.toUpperCase()) {
            case "CREATE_ACT_OPERATION" -> {
                this.httpResponse = ipServiceDeskClient.createActOperationWithHttpInfo(createActOperationRequestV1);
                operationsResponseV1 = maybeBody(httpResponse.body(), OperationsResponse.class).orElse(null);

                if (operationsResponseV1 != null) {
                    Assertions.assertNotNull(operationsResponseV1.getOperationId(), "OperationId nullo nella response di CREATE_ACT_OPERATION V1");
                    operationId = operationsResponseV1.getOperationId();
                    log.info("Operation id V1:" + operationId);
                }
            }
            case "CREATE_ACT_OPERATION V2" -> {
                this.httpResponse = ipServiceDeskClient.createActOperationV2WithHttpInfo(createActOperationRequestV2);
                operationsResponseV2 = maybeBody(httpResponse.body(), CreateOperationsResponseV2.class).orElse(null);

                if (operationsResponseV2 != null) {
                    Assertions.assertNotNull(operationsResponseV2.getOperationId(), "OperationId nullo nella response di CREATE_ACT_OPERATION V2");
                    operationId = operationsResponseV2.getOperationId();
                    log.info("Operation id V2:" + operationId);
                }
            }
            case "GET_ACT_OPERATION_STATUS" -> {
                this.httpResponse = ipServiceDeskClient.getOperationStatusWithHttpInfo(operationId);
                statusOperationResponse = maybeBody(httpResponse.body(), String.class).orElse("");
            }
            case "GET_ACT_OPERATION_STATUS_INVALID_API_KEY" -> {
                this.httpResponse = ipServiceDeskClient.getOperationStatusWithHttpInfoAndInvalidApiKey(getPrefixedRandomAlphaNumeric(7));
                statusOperationResponse = maybeBody(httpResponse.body(), String.class).orElse("");
            }
            case "UPLOAD_VIDEO" -> {
                this.httpResponse = ipServiceDeskClient.presignedUrlVideoUploadWithHttpInfo(operationId, videoUploadRequest);
                videoUploadResponse = maybeBody(httpResponse.body(), VideoUploadResponse.class).orElse(null);
                //Assertions.assertNotNull(videoUploadResponse.getUrl(), "UploadUrl nullo nella response di UPLOAD_VIDEO");
            }
            default -> Assertions.fail("Invalid operation");
        }
    }

    private <T> Optional<T> maybeBody(Object body, Class<T> expectedType) {
        if (body == null) return Optional.empty();
        Assertions.assertTrue(expectedType.isInstance(body),
                "Tipo body inatteso: " + "atteso " + expectedType.getSimpleName()
                        + ", ottenuto " + body.getClass().getSimpleName());
        return Optional.of(expectedType.cast(body));
    }

    @Given("viene settato l'operationId a {string}")
    public void setOperationId(String operationId) {
        this.operationId = operationId.equals("null") ? null : operationId.trim();
    }

    @Then("l'operazione è in stato {string}")
    public void checkOperationActStatus(String status) {
        Assertions.assertNotNull(status);
        Assertions.assertEquals(status.toUpperCase(), statusOperationResponse.toUpperCase());
    }

    public void sendNotification() {
        String iun = sharedSteps.getNotificationIun();
        if (iun != null) return;

        // viene generata una nuova notifica
        Map<String, String> data = new HashMap<>();
        data.put("subject", "notifica analogica con cucumber");
        data.put("senderDenomination", "Comune di palermo");
        sharedSteps.prepareNotificationRequestWithVersion(MOST_RECENT, data);

        // destinatario Mario Gherkin e:
        Destinatario destinatario = sharedSteps.getDestinatarioRegistry().DESTINATARIO_MARIO_GHERKIN;
        Map<String, String> recipentData = new HashMap<>();
        recipentData.put("digitalDomicile", "NULL");
        recipentData.put("physicalAddress_address", "Via@ok_890");
        sharedSteps.getNotificationStepInterface().addRecipientToNotification(destinatario, recipentData);

        // la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
        sharedSteps.sendNotification("Comune_Multi", "ACCEPTED");
    }

    @And("viene atteso lo stato {string} dell'operazione")
    public void pollOperationActStatus(String status) throws Exception {
        pollByStatus(status, 600, 500);
        checkOperationActStatus(status);
    }

    public void pollByStatus(String status, int maxAttempts, int sleepMillis) throws Exception {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            invokeApi("GET_ACT_OPERATION_STATUS");
            System.out.println("Stato attuale: " + statusOperationResponse.toUpperCase());
            if (status.equalsIgnoreCase(statusOperationResponse.toUpperCase())) return;
            Thread.sleep(sleepMillis);
        }
        log.debug("Polling esaurito per operationId {}", operationId);
    }

    private void createActOperationRequestV1(Map<String, String> data) {
        createActOperationRequestV1 = new CreateActOperationRequest();

        createActOperationRequestV1.setTaxId(getValue(data, "taxId"));

        // Obbligatori
        // Automatizzabili
        String ticketId = getValue(data, "ticketId");
        createActOperationRequestV1.setTicketId(ticketId != null && ticketId.equalsIgnoreCase("auto") ?
                getPrefixedRandomAlphaNumeric(12) : ticketId);

        String vrDate = getValue(data, "vrDate");
        createActOperationRequestV1.setVrDate(vrDate != null && vrDate.equalsIgnoreCase("auto") ?
                LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) : vrDate);

        String ticketDate = getValue(data, "ticketDate");
        createActOperationRequestV1.setTicketDate(ticketDate != null && ticketDate.equalsIgnoreCase("auto") ?
                LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) : ticketDate);

        String iun = getValue(data, "iun");
        createActOperationRequestV1.setIun(iun != null && iun.equalsIgnoreCase("auto") ?
                sharedSteps.getNotificationIun() : iun);

        // Opzionali
        // Automatizzabili
        String ticketOperationId = getValue(data, "ticketOperationId");
        createActOperationRequestV1.setTicketOperationId(ticketOperationId != null && ticketOperationId.equalsIgnoreCase("auto") ?
                getPrefixedRandomAlphaNumeric(7) : ticketOperationId);

        // Non automatizzabili
        String addressType = getValue(data, "addressType");
        String addressValue = getValue(data, "addressValue");

        if (addressType != null && addressValue != null) {
            ActDigitalAddress address = new ActDigitalAddress().address(addressValue).type(ActDigitalAddress.TypeEnum.valueOf(addressType));
            createActOperationRequestV1.setAddress(address);
        } else {
            createActOperationRequestV1.setAddress(null);
        }
        log.info("CreateActOperationRequest V1: {}", createActOperationRequestV1);
    }

    private void createActOperationRequestV2(Map<String, String> data) {
        createActOperationRequestV2 = new CreateActOperationRequestV2();

        createActOperationRequestV2.setTaxId(getValue(data, "taxId"));

        // Obbligatori
        // Automatizzabili
        String ticketId = getValue(data, "ticketId");
        createActOperationRequestV2.setTicketId(ticketId != null && ticketId.equalsIgnoreCase("auto") ?
                getPrefixedRandomAlphaNumeric(12) : ticketId);

        String vrDate = getValue(data, "vrDate");
        createActOperationRequestV2.setVrDate(vrDate != null && vrDate.equalsIgnoreCase("auto") ?
                LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) : vrDate);

        String ticketDate = getValue(data, "ticketDate");
        createActOperationRequestV2.setTicketDate(ticketDate != null && ticketDate.equalsIgnoreCase("auto") ?
                LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) : ticketDate);

        String iunListType = getValue(data, "iunListType");
        log.info("TIPOLOGIA IUN UTILIZZATA: {}", iunListType.toUpperCase());
        switch (iunListType.toUpperCase()) {
            case "DATI VALIDI" -> createActOperationRequestV2.setIun(sharedSteps.getNotificationIunList());
            case "LISTA IUN VUOTA" -> createActOperationRequestV2.setIun(new ArrayList<>());
            case "TUTTI IUN INESISTENTI" -> createActOperationRequestV2.setIun(List.of(IUN_ERRATO));
            case "IUN RIPETUTO" -> createActOperationRequestV2.setIun(Stream.concat(
                            sharedSteps.getNotificationIunList().stream(),
                            Stream.of(sharedSteps.getNotificationIunList().get(0)))
                    .toList());
            case "UNO IUN INESISTENTE" -> createActOperationRequestV2.setIun(Stream.concat(
                            sharedSteps.getNotificationIunList().stream(),
                            Stream.of(IUN_ERRATO))
                    .toList());
            default -> throw new IllegalArgumentException("Invalid iunListType: " + iunListType);

        }
        // Opzionali
        // Automatizzabili
        String ticketOperationId = getValue(data, "ticketOperationId");
        createActOperationRequestV2.setTicketOperationId(ticketOperationId != null && ticketOperationId.equalsIgnoreCase("auto") ?
                getPrefixedRandomAlphaNumeric(7) : ticketOperationId);

        // Non automatizzabili
        String addressType = getValue(data, "addressType");
        String addressValue = getValue(data, "addressValue");

        if (addressType != null && addressValue != null) {
            ActDigitalAddress address = new ActDigitalAddress().address(addressValue).type(ActDigitalAddress.TypeEnum.valueOf(addressType));
            createActOperationRequestV2.setAddress(address);
        } else {
            createActOperationRequestV2.setAddress(null);
        }
        log.info("CreateActOperationRequest V2: {}", createActOperationRequestV2);
    }

    @Given("se la chiamata al servizio ha avuto successo")
    public void assumeResponseIsSuccessful() {
        Assumptions.assumeTrue(httpResponse.is2xx(), "Gli step successivi verranno ignorati, in quanto valevoli solo quando la chiamata precedente risponde status 2XX");
    }

    @When("viene invocata l'API v2 GET operations passando {string}")
    public void callGetOperationsV2(String operationIdType) {
        String opIdParam;
        switch (operationIdType.toUpperCase()) {
            case "VALID OP. ID", "VALID OP. ID V1" -> opIdParam = operationId;
            case "INEXISTENT OP. ID" -> opIdParam = "404_operationId";
            case "INVALID OP. ID" -> opIdParam = "x".repeat(33);
            case "OP. ID WITH IUN" ->
                    opIdParam = "SUB#" + operationId + "#" + sharedSteps.getNotificationIunList().get(0);
            default -> throw new IllegalArgumentException("Invalid value for operationIdType: " + operationIdType);
        }
        log.info("OperationId used for get v2: {}", opIdParam);
        this.httpResponse = ipServiceDeskClient.getOperationV2WithHttpInfo(opIdParam);
        getOperationsResponseV2 = maybeBody(httpResponse.body(), GetOperationsResponseV2.class).orElse(null);
        log.info("Response of GET operations V2: {}", getOperationsResponseV2);

        if (getOperationsResponseV2 != null) {
            if (!operationIdType.equalsIgnoreCase("VALID OP. ID V1") && createActOperationRequestV2 != null) {
                assertThat(getOperationsResponseV2.getSubOperations().size()).as("").isEqualTo(createActOperationRequestV2.getIun().size());
                getOperationsResponseV2.getSubOperations().forEach(sub -> assertThat(createActOperationRequestV2.getIun()).asList().as("La response della get non contiene lo IUN: " + sub.getIun()).contains(sub.getIun()));
            }
            log.info("Operation id della GET V2:" + operationId);
        }
    }

    @Then("il campo operationStatus della response è valorizzato con {string}")
    public void checkStatusFieldOfGetOperationResponse(String status) throws InterruptedException {
        try {
            assertThat(getOperationsResponseV2.getStatus()).as("Lo status della GetOperationResponse non coincide con quanto atteso").isEqualTo(status);
        } catch (AssertionError ae) {
            log.info("Waiting 3 minutes for status to get updated");
            Thread.sleep(180000L);
            callGetOperationsV2("VALID OP. ID");
            assertThat(getOperationsResponseV2.getStatus()).as("Dopo 3 minuti di attesa, lo status della GetOperationResponse non coincide ancora con quanto atteso").isEqualTo(status);
        }
    }

    @And("il campo {string} risulta popolato correttamente, e il campo senderPaDescription è {string}")
    public void checkSearchResultFields(String fieldName, String senderPaDescription) {
        List<SDNotificationSummary> fieldToCheck = fieldName.equals("iuns") ? searchOperationResponse.getIuns() : searchOperationResponse.getUncompletedIuns();
        assertThat(fieldToCheck).asList().as("Il campo " + fieldName + "non dev'essere vuoto");
        fieldToCheck.forEach(summary -> {
            if (senderPaDescription.equals("NOT PagoPA")) {
                assertThat(summary.getSenderPaDescription()).isNotEqualTo("PagoPA S.p.A.");
            } else {
                assertThat(summary.getSenderPaDescription()).isEqualTo(senderPaDescription);
            }
        });
    }
}