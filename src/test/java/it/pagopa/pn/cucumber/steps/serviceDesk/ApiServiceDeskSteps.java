package it.pagopa.pn.cucumber.steps.serviceDesk;


import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.testclient.PnExternalServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.testclient.PnServiceDeskClientImplNoApiKey;
import it.pagopa.pn.client.b2b.pa.testclient.PnServiceDeskClientImplWrongApiKey;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.internalb2bradd.model.CompleteTransactionRequest;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.gpd.model.PaymentOptionModel;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.gpd.model.PaymentPositionModel;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.gpd.model.TransferModel;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDeskIntegration.model.*;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDeskIntegration.model.RecipientType;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDeskIntegration.model.TimelineElement;
import org.assertj.core.api.Assert;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.impl.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.testclient.IPServiceDeskClientImpl;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.model.*;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static java.time.OffsetDateTime.now;


public class ApiServiceDeskSteps {


    private final SharedSteps sharedSteps;

    private final PnPaB2bUtils b2bUtils;

    private final IPnPaB2bClient b2bClient;

    private final PnExternalServiceClientImpl safeStorageClient;

    private final IPServiceDeskClientImpl ipServiceDeskClient;

    private NotificationRequest notificationRequest;

    private AnalogAddress analogAddress;

    private NotificationsUnreachableResponse notificationsUnreachableResponse;


    private CreateOperationRequest createOperationRequest;

    private OperationsResponse operationsResponse;

    private VideoUploadRequest videoUploadRequest;

    private VideoUploadResponse videoUploadResponse;

    private NotificationDocument notificationDocument;

    private SearchNotificationRequest searchNotificationRequest;

    private SearchResponse searchResponse;

    private static final String CF_vuoto = null;

    private static final String CF_corretto = "CLMCST42R12D969Z";

    private static final String CF_errato = "CPNTMS85T15H703WCPNTMS85T15H703W|";

    private static final String CF_errato2 = "CPNTM@85T15H703W";

    private static final String ticketid_vuoto = null;

    private static final String ticketid_errato = "XXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxX";

    private static final String ticketoperationid_vuoto = null;

    private static final String ticketoperationid_errato = "abcdfeghilm";

    private final Integer workFlowWaitDefault = 31000;

    private final Integer delay = 420000;

    @Value("${pn.configuration.workflow.wait.millis:31000}")
    private Integer workFlowWait;

    @Value("${pn.retention.videotime.preload}")
    private Integer retentionTimePreLoad;

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private HttpStatusCodeException notificationError;

    private final ApplicationContext ctx;

    private final RestTemplate restTemplate;

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ01234556789";
    private static final String NUMERIC_STRING = "0123456789";

    private List<PaSummary> listPa = null;
    private SearchNotificationsResponse searchNotificationsResponse;
    private SearchNotificationsRequest searchNotificationsRequest;
    private ProfileRequest profileRequest;
    private ProfileResponse profileResponse;
    private NotificationDetailResponse notificationDetailResponse;
    private TimelineResponse timelineResponse;
    private DocumentsRequest documentsRequest;
    private DocumentsResponse documentsResponse;
    private PaNotificationsRequest paNotificationsRequest;
    private ResponseApiKeys responseApiKeys;

    @Autowired
    public ApiServiceDeskSteps(SharedSteps sharedSteps, RestTemplate restTemplate, IPServiceDeskClientImpl ipServiceDeskClient, ApplicationContext ctx, PnExternalServiceClientImpl safeStorageClient) {
        this.sharedSteps = sharedSteps;
        this.b2bUtils = sharedSteps.getB2bUtils();
        this.b2bClient = sharedSteps.getB2bClient();
        this.safeStorageClient = safeStorageClient;
        this.ipServiceDeskClient = sharedSteps.getServiceDeskClient();
        this.restTemplate = restTemplate;
        this.notificationRequest = new NotificationRequest();
        this.notificationsUnreachableResponse = notificationsUnreachableResponse;
        this.analogAddress = new AnalogAddress();
        this.createOperationRequest = new CreateOperationRequest();
        this.videoUploadRequest = new VideoUploadRequest();
        this.searchNotificationRequest = new SearchNotificationRequest();
        this.ctx = ctx;
    }

    @Given("viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il {string}")
    public void createVerifyUnreachableRequest(String cf) {
        switch (cf) {
            case "CF_vuoto":
                notificationRequest.setTaxId(CF_vuoto);
                break;
            case "CF_errato":
                notificationRequest.setTaxId(CF_errato);
                break;
            case "CF_errato2":
                notificationRequest.setTaxId(CF_errato2);
                break;
            default:
                notificationRequest.setTaxId(cf);
                logger.info("Inserito CF:" + cf);
        }
    }

    @Given("viene creata una nuova richiesta per invocare il servizio UNREACHABLE con cf vuoto")
    public void createVerifyUnreachableRequest() {
        notificationRequest.setTaxId(CF_vuoto);
    }

    @When("viene invocato il servizio UNREACHABLE")
    public void NotificationsUnreachableResponse() {

        try {
            Assertions.assertDoesNotThrow(() -> {
                notificationsUnreachableResponse = ipServiceDeskClient.notification(notificationRequest);
            });

            try {
                Thread.sleep(getWorkFlowWait());
            } catch (InterruptedException e) {
                logger.error("Thread.sleep error retry");
                throw new RuntimeException(e);
            }
            Assertions.assertNotNull(notificationsUnreachableResponse);

        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{Il cliente presenta un numero di pratiche" + (notificationsUnreachableResponse == null ? "NULL" : notificationsUnreachableResponse.getNotificationsCount()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @When("viene invocato il servizio UNREACHABLE con errore")
    public void NotificationsUnreachableResponseWithError() {
        try {
            notificationsUnreachableResponse = ipServiceDeskClient.notification(notificationRequest);
            try {
                Thread.sleep(getWorkFlowWait());
            } catch (InterruptedException e) {
                logger.error("Thread.sleep error retry");
                throw new RuntimeException(e);
            }
            Assertions.assertNotNull(notificationsUnreachableResponse);
        } catch (HttpStatusCodeException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notificationError = (HttpStatusCodeException) e;
            }
        }
    }


    @Then("la risposta del servizio UNREACHABLE è {long}")
    public void verifyNotificationsUnreachableResponse(Long count) {
        Long notificationsCount = notificationsUnreachableResponse.getNotificationsCount();
        Assertions.assertEquals(notificationsCount, count);
        logger.info("Presenza notifiche per il CF" + this.notificationRequest.getTaxId() + ":" + notificationsCount);
    }

    @Then("il servizio risponde con errore {string}")
    public void operationProducedAnError(String statusCode) {
        Assertions.assertTrue((notificationError.getStatusCode() != null) &&
                (notificationError.getStatusCode().toString().substring(0, 3).equals(statusCode)));
        logger.info("Errore: " + notificationError.getStatusCode() + " " + notificationError.getMessage() + " " + notificationError.getCause());
    }

    @Given("viene comunicato il nuovo indirizzo con {string} {string} {string} {string} {string} {string} {string} {string} {string}")
    public void createNewAddressRequest(String fullname, String namerow2, String address, String addressRow2, String cap, String city, String city2, String pr, String country) {
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
        createOperationRequest.setTaxId(cf);
        logger.info("CF:" + cf);
        String ticketid = null;
        try {
            ticketid = "AUT" + randomAlphaNumeric(12);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        logger.info("ticketid:" + ticketid);
        createOperationRequest.setTicketId(ticketid);
        String ticketOperationid = null;
        try {
            ticketOperationid = "AUT" + randomAlphaNumeric(7);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        logger.info("ticketOperationid:" + ticketOperationid);
        createOperationRequest.setTicketOperationId(ticketOperationid);
        createOperationRequest.setAddress(analogAddress);

    }

    @Given("viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION per con {string} {string} {string}")
    public void createOperationReq(String cf, String ticketid, String ticketOperationid) {
        switch (cf) {
            case "CF_vuoto":
                createOperationRequest.setTaxId(CF_vuoto);
                break;
            default:
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
            case "ticketOperationid_vuoto":
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
        createOperationRequest.setTaxId(CF_vuoto);
        String ticketid = null;
        try {
            ticketid = "AUT" + randomAlphaNumeric(12);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        logger.info("ticketid:" + ticketid);
        createOperationRequest.setTicketId(ticketid);
        String ticketOperationid = null;
        try {
            ticketOperationid = "AUT" + randomAlphaNumeric(7);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        logger.info("ticketOperationid:" + ticketOperationid);
        createOperationRequest.setTicketOperationId(ticketOperationid);
        createOperationRequest.setAddress(analogAddress);

    }


    @When("viene invocato il servizio CREATE_OPERATION con errore")
    public void createOperationResponseWithError() {
        try {
            operationsResponse = ipServiceDeskClient.createOperation(createOperationRequest);
            try {
                Thread.sleep(getWorkFlowWait());
            } catch (InterruptedException e) {
                logger.error("Thread.sleep error retry");
                throw new RuntimeException(e);
            }
            Assertions.assertNotNull(notificationsUnreachableResponse);
        } catch (HttpStatusCodeException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notificationError = (HttpStatusCodeException) e;
            }
        }

    }

    @When("viene invocato il servizio CREATE_OPERATION")
    public void createOperationResponse() {
        try {
            Assertions.assertDoesNotThrow(() -> {
                operationsResponse = ipServiceDeskClient.createOperation(createOperationRequest);
            });

            try {
                Thread.sleep(getWorkFlowWait());
            } catch (InterruptedException e) {
                logger.error("Thread.sleep error retry");
                throw new RuntimeException(e);
            }
            Assertions.assertNotNull(operationsResponse);

        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{Id operation " + (operationsResponse == null ? "NULL" : operationsResponse.getOperationId()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @Then("la risposta del servizio CREATE_OPERATION risponde con esito positivo")
    public void verifyCreateOperationResponse() {
        String idoperation = operationsResponse.getOperationId();
        Assertions.assertNotNull(idoperation);
        logger.info("L'operation di creato per il CF:" + createOperationRequest.getTaxId() + " " + idoperation);
    }

    @Given("viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO")
    public void createPreUploadVideoRequest() throws Exception {
        notificationDocument = newDocument("classpath:/video.mp4");
        String resourceName = notificationDocument.getRef().getKey();
        logger.info("Resource name:" + resourceName);
        String sha256 = computeSha256(resourceName);
        logger.info("sha:" + sha256);
        videoUploadRequest.setPreloadIdx("AUT" + randomAlphaNumeric(5));
        videoUploadRequest.setSha256(sha256);
        videoUploadRequest.setContentType("application/octet-stream");

    }

    @Given("viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con formato non corretto")
    public void createPreUploadVideoRequestFormatVideoNotValid() throws Exception {
        notificationDocument = newDocument("classpath:/video.avi");
        String resourceName = notificationDocument.getRef().getKey();
        logger.info("Resource name:" + resourceName);
        String sha256 = computeSha256(resourceName);
        logger.info("sha:" + sha256);
        videoUploadRequest.setPreloadIdx("AUT" + randomAlphaNumeric(5));
        videoUploadRequest.setSha256(sha256);
        videoUploadRequest.setContentType("application/octet-stream");

    }

    @When("viene invocato il servizio UPLOAD VIDEO")
    public void preUploadVideoResponse() {
        try {
            Assertions.assertDoesNotThrow(() -> {
                videoUploadResponse = ipServiceDeskClient.presignedUrlVideoUpload(operationsResponse.getOperationId(), videoUploadRequest);
            });

            try {
                Thread.sleep(getWorkFlowWait());
            } catch (InterruptedException e) {
                logger.error("Thread.sleep error retry");
                throw new RuntimeException(e);
            }
            Assertions.assertNotNull(videoUploadResponse);

        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{Upload video" + (videoUploadResponse == null ? "NULL" : videoUploadResponse.getUrl()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @When("viene invocato il servizio UPLOAD VIDEO con {string} con errore")
    public void preUploadVideoResponse(String operationId) {
        try {
            videoUploadResponse = ipServiceDeskClient.presignedUrlVideoUpload(operationId, videoUploadRequest);
            try {
                Thread.sleep(getWorkFlowWait());
            } catch (InterruptedException e) {
                logger.error("Thread.sleep error retry");
                throw new RuntimeException(e);
            }
            Assertions.assertNotNull(videoUploadResponse);
        } catch (HttpStatusCodeException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notificationError = (HttpStatusCodeException) e;
            }
        }
    }

    @When("viene invocato il servizio UPLOAD VIDEO con operationid vuoto")
    public void preUploadVideoResponseOperationIdNull() {
        try {
            videoUploadResponse = ipServiceDeskClient.presignedUrlVideoUpload(null, videoUploadRequest);
            try {
                Thread.sleep(getWorkFlowWait());
            } catch (InterruptedException e) {
                logger.error("Thread.sleep error retry");
                throw new RuntimeException(e);
            }
            Assertions.assertNotNull(videoUploadResponse);
        } catch (HttpStatusCodeException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notificationError = (HttpStatusCodeException) e;
            }
        }
    }


    @When("viene invocato il servizio UPLOAD VIDEO con errore")
    public void preUploadVideoResponseWithError() {
        try {
            logger.error("Operation id:" + operationsResponse.getOperationId());
            videoUploadResponse = ipServiceDeskClient.presignedUrlVideoUpload(operationsResponse.getOperationId(), videoUploadRequest);
            try {
                Thread.sleep(getWorkFlowWait());
            } catch (InterruptedException e) {
                logger.error("Thread.sleep error retry");
                throw new RuntimeException(e);
            }
            Assertions.assertNotNull(videoUploadResponse);
        } catch (HttpStatusCodeException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notificationError = (HttpStatusCodeException) e;
            }
        }
    }


    @Given("viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con sha256 vuoto")
    public void createPreUploadVideoRequestSha256Null() throws Exception {
        notificationDocument = newDocument("classpath:/video.mp4");
        String resourceName = notificationDocument.getRef().getKey();
        String sha256 = computeSha256(resourceName);
        videoUploadRequest.setPreloadIdx("AUT" + randomAlphaNumeric(5));
        videoUploadRequest.setSha256(null);
        videoUploadRequest.setContentType("application/octet-stream");

    }

    @Given("viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con sha256 errato")
    public void createPreUploadVideoRequestSha256Error() throws Exception {
        notificationDocument = newDocument("classpath:/video.mp4");
        String resourceName = notificationDocument.getRef().getKey();
        String sha256 = computeSha256(resourceName);
        videoUploadRequest.setPreloadIdx("AUT" + randomAlphaNumeric(5));
        videoUploadRequest.setSha256(sha256 + "ERR");
        videoUploadRequest.setContentType("application/octet-stream");

    }

    @Given("viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con preloadIdx vuoto")
    public void createPreUploadVideoRequestPreloadIdxNull() throws Exception {
        notificationDocument = newDocument("classpath:/video.mp4");
        String resourceName = notificationDocument.getRef().getKey();
        String sha256 = computeSha256(resourceName);
        videoUploadRequest.setPreloadIdx(null);
        videoUploadRequest.setSha256(sha256);
        videoUploadRequest.setContentType("application/octet-stream");
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
        notificationDocument = newDocument("classpath:/video.mp4");
        String resourceName = notificationDocument.getRef().getKey();
        String sha256 = computeSha256(resourceName);
        videoUploadRequest.setPreloadIdx("AUT" + randomAlphaNumeric(5));
        videoUploadRequest.setSha256(sha256);
        videoUploadRequest.setContentType(null);
    }

    @Then("la risposta del servizio UPLOAD VIDEO risponde con esito positivo")
    public void verifyUploadVideoResponse() {
        String url = videoUploadResponse.getUrl();
        Assertions.assertNotNull(url);
        logger.info("generata la url:" + url);
        String secretkey = videoUploadResponse.getSecret();
        Assertions.assertNotNull(secretkey);
        logger.info("generata la secret key:" + secretkey);
        String filekey = videoUploadResponse.getFileKey();
        Assertions.assertNotNull(filekey);
        logger.info("generata la file key:" + filekey);
    }

    @Given("viene creata una nuova richiesta per invocare il servizio SEARCH per il {string}")
    public void createSearchRequest(String cf) {
        switch (cf) {
            case "CF_vuoto":
                searchNotificationRequest.setTaxId(CF_vuoto);
                break;
            case "CF_errato":
                searchNotificationRequest.setTaxId(CF_errato);
                break;
            case "CF_errato2":
                searchNotificationRequest.setTaxId(CF_errato2);
                break;
            default:
                searchNotificationRequest.setTaxId(cf);
        }
    }

    @When("viene invocato il servizio SEARCH con errore")
    public void searchResponseWithError() {
        try {
            searchResponse = ipServiceDeskClient.searchOperationsFromTaxId(searchNotificationRequest);
            try {
                Thread.sleep(getWorkFlowWait());
            } catch (InterruptedException e) {
                logger.error("Thread.sleep error retry");
                throw new RuntimeException(e);
            }
            Assertions.assertNotNull(searchResponse);
        } catch (HttpStatusCodeException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notificationError = (HttpStatusCodeException) e;
            }
        }
    }

    @When("viene invocato il servizio SEARCH")
    public void searchResponse() {
        try {
            Assertions.assertDoesNotThrow(() -> {
                searchResponse = ipServiceDeskClient.searchOperationsFromTaxId(searchNotificationRequest);
                //List<OperationResponse> op = searchResponse.getOperations();

            });

            try {
                Thread.sleep(getWorkFlowWait());
            } catch (InterruptedException e) {
                logger.error("Thread.sleep error retry");
                throw new RuntimeException(e);
            }
            Assertions.assertNotNull(searchResponse);

        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{Ricerca effettuata " + (searchResponse == null ? "NULL" : searchResponse.getOperations().toString()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @When("viene invocato il servizio SEARCH con delay")
    public void searchResponseWithDelay() {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            logger.error("Thread.sleep error retry");
            throw new RuntimeException(e);
        }
        try {
            Assertions.assertDoesNotThrow(() -> {
                searchResponse = ipServiceDeskClient.searchOperationsFromTaxId(searchNotificationRequest);
                //List<OperationResponse> op = searchResponse.getOperations();

            });

            try {
                Thread.sleep(getWorkFlowWait());
            } catch (InterruptedException e) {
                logger.error("Thread.sleep error retry");
                throw new RuntimeException(e);
            }
            Assertions.assertNotNull(searchResponse);

        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() +
                    "{Ricerca effettuata " + (searchResponse == null ? "NULL" : searchResponse.getOperations().toString()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @Then("Il servizio SEARCH risponde con esito positivo")
    public void verifySearchResponse() {
        List<OperationResponse> lista = searchResponse.getOperations();
        Assertions.assertNotNull(lista);
        logger.info("SEARCH " + searchResponse.getOperations().toString());
        //Analalisi output
        for (OperationResponse element : lista) {
            logger.info("STAMPA ELEMENTO LISTA " + element.toString());
            Assertions.assertNotNull(element.getOperationId());
            logger.info("CF attuale" + element.getTaxId());
            logger.info("CF da cercare" + searchNotificationRequest.getTaxId());
            Assertions.assertEquals(element.getTaxId(), searchNotificationRequest.getTaxId());
            Assertions.assertNotNull(element.getIuns());
            Assertions.assertNotNull(element.getUncompletedIuns());
            Assertions.assertNotNull(element.getNotificationStatus());
            logger.info("STATO NOTIFICA " + lista.get(0).getNotificationStatus().getStatus().getValue());
            Assertions.assertNotNull(element.getOperationCreateTimestamp());
            Assertions.assertNotNull(element.getOperationUpdateTimestamp());

        }

    }

    @Then("Il servizio SEARCH risponde con esito positivo e lo stato della consegna è {string}")
    public void verifySearchResponseWithStatus(String status) {
        boolean findOperationId = false;
        String operationIdToSearch = operationsResponse.getOperationId();
        logger.info("OPERATION ID TO SEARCH: " + operationIdToSearch);
        List<OperationResponse> lista = searchResponse.getOperations();
        Assertions.assertNotNull(lista);
        logger.info("SEARCH " + searchResponse.getOperations().toString());
        //Analisi output
        for (OperationResponse element : lista) {
            logger.info("STAMPA ELEMENTO LISTA " + element.toString());
            String actualOperationId = element.getOperationId();
            Assertions.assertNotNull(actualOperationId);
            if (actualOperationId.compareTo(operationIdToSearch) == 0 && findOperationId == false) {
                findOperationId = true;
            }
            // Assertions.assertEquals(element.getTaxId(),notificationRequest.getTaxId());
            //Viene verificato che l'operation id generato fa parte della lista

            Assertions.assertNotNull(element.getIuns());

            Assertions.assertNotNull(element.getUncompletedIuns());
            Assertions.assertNotNull(element.getNotificationStatus());
            //controllo sullo status
            if (operationIdToSearch.compareTo(actualOperationId) == 0) {
                logger.info("STATO NOTIFICA " + element.getNotificationStatus().getStatus().getValue());
                Assertions.assertEquals(element.getNotificationStatus().getStatus().getValue(), status);
            }
            Assertions.assertNotNull(element.getOperationCreateTimestamp());
            Assertions.assertNotNull(element.getOperationUpdateTimestamp());
        }

        //Se non viene trovato l'id operation lancio eccezione
        try {
            Assertions.assertTrue(findOperationId);
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() + "{L'operation id non è presente nella lista" + findOperationId + "}";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }

    }


    @Then("Il servizio SEARCH risponde con esito positivo con spedizione multipla e lo stato della consegna è {string}")
    public void verifySearchResponseWithStatusSplitNotify(String status) {
        boolean findOperationId = false;
        boolean multiOperation = false;
        String operationIdToSearch = operationsResponse.getOperationId();
        logger.info("OPERATION ID TO SEARCH: " + operationIdToSearch);
        List<OperationResponse> lista = searchResponse.getOperations();
        Assertions.assertNotNull(lista);
        logger.info("SEARCH " + searchResponse.getOperations().toString());
        //Viene controllato che lo stato delle operation è superiore a 1


        List<OperationResponse> listaSplit = new ArrayList<>();

        //Analisi output
        for (OperationResponse element : lista) {
            String actualOperationId = element.getOperationId();
            logger.info("ACTUAL OPERATION ID: " + actualOperationId);
            if (actualOperationId.compareTo(operationIdToSearch) == 0) {
                listaSplit.add(element);
                logger.info("AGGIUNTO ELEMENTO: " + actualOperationId);
            }
            Assertions.assertNotNull(listaSplit);
        }
        int numberOperation = listaSplit.size();

        logger.info("Numero di response che contengono l'operation id " + numberOperation);
        if (numberOperation > 1) {
            multiOperation = true;
        }
        Assertions.assertTrue(multiOperation);
        //Analisi output
        for (OperationResponse element : listaSplit) {
            logger.info("STAMPA ELEMENTO LISTA " + element.toString());
            String actualOperationId = element.getOperationId();
            Assertions.assertNotNull(actualOperationId);
            if (actualOperationId.compareTo(operationIdToSearch) == 0 && findOperationId == false) {
                findOperationId = true;
            }
            // Assertions.assertEquals(element.getTaxId(),notificationRequest.getTaxId());
            //Viene verificato che l'operation id generato fa parte della lista

            Assertions.assertNotNull(element.getIuns());

            Assertions.assertNotNull(element.getUncompletedIuns());
            Assertions.assertNotNull(element.getNotificationStatus());
            //controllo sullo status
            if (operationIdToSearch.compareTo(actualOperationId) == 0) {
                logger.info("STATO NOTIFICA " + element.getNotificationStatus().getStatus().getValue());
                Assertions.assertEquals(element.getNotificationStatus().getStatus().getValue(), status);
            }
            Assertions.assertNotNull(element.getOperationCreateTimestamp());
            Assertions.assertNotNull(element.getOperationUpdateTimestamp());
        }

        //Se non viene trovato l'id operation lancio eccezione
        try {
            Assertions.assertTrue(findOperationId);
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() + "{L'operation id non è presente nella lista" + findOperationId + "}";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }

    }


    @Then("Il servizio SEARCH risponde con esito positivo per lo {string} e lo stato della consegna è {string}")
    public void verifySearchResponseWithStatusAndIun(String iun, String status) {
        boolean findOperationId = false;
        boolean findIun = false;
        String operationIdToSearch = operationsResponse.getOperationId();
        logger.info("OPERATION ID TO SEARCH: " + operationIdToSearch);
        List<OperationResponse> lista = searchResponse.getOperations();
        Assertions.assertNotNull(lista);
        logger.info("SEARCH " + searchResponse.getOperations().toString());
        //Analisi output
        for (OperationResponse element : lista) {
            logger.info("STAMPA ELEMENTO LISTA " + element.toString());
            String actualOperationId = element.getOperationId();
            Assertions.assertNotNull(actualOperationId);
            if (actualOperationId.compareTo(operationIdToSearch) == 0 && findOperationId == false) {
                findOperationId = true;
            }
            Assertions.assertEquals(element.getTaxId(), searchNotificationRequest.getTaxId());
            //Viene verificato che l'operation id generato fa parte della lista

            Assertions.assertNotNull(element.getIuns());
            List<SDNotificationSummary> listaiuns = element.getIuns();
            if (findOperationId) {
                for (SDNotificationSummary acutalIun : listaiuns) {
                    //Verifica se lo iun è presente nella lista
                    logger.info("IUN ATTUALE " + acutalIun.getIun());
                    if (acutalIun.getIun().compareTo(iun) == 0 && findIun == false) {
                        findIun = true;
                    }
                }
                Assertions.assertNotNull(element.getUncompletedIuns());
                Assertions.assertNotNull(element.getNotificationStatus());
                //controllo sullo status
                if (operationIdToSearch.compareTo(actualOperationId) == 0) {
                    logger.info("STATO NOTIFICA " + element.getNotificationStatus().getStatus().getValue());
                    Assertions.assertEquals(element.getNotificationStatus().getStatus().getValue(), status);
                }
                Assertions.assertNotNull(element.getOperationCreateTimestamp());
                Assertions.assertNotNull(element.getOperationUpdateTimestamp());
            }
        }
        //Se non viene trovato l'id operation lancio eccezione
        try {
            Assertions.assertTrue(findOperationId);
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() + "{L'operation id non è presente nella lista" + findOperationId + "}";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
        //Se non viene trovato lo IUN lancio operazione
        try {
            Assertions.assertTrue(findIun);
        } catch (AssertionFailedError assertionFailedError) {
            String message = assertionFailedError.getMessage() + "{Lo iun non è associato al CF" + searchNotificationRequest.getTaxId() + "}";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

    @Then("Il servizio SEARCH risponde con esito positivo con uncompleted iun lo stato della consegna è {string}")
    public void verifySearchResponseWithStatusAndUncompletedIun(String status) {
        boolean findOperationId = false;
        boolean findIun = false;
        String operationIdToSearch = operationsResponse.getOperationId();
        logger.info("OPERATION ID TO SEARCH: " + operationIdToSearch);
        List<OperationResponse> lista = searchResponse.getOperations();
        Assertions.assertNotNull(lista);
        logger.info("SEARCH " + searchResponse.getOperations().toString());
        //Analisi output
        for (OperationResponse element : lista) {
            logger.info("STAMPA ELEMENTO LISTA " + element.toString());
            String actualOperationId = element.getOperationId();
            Assertions.assertNotNull(actualOperationId);
            if (actualOperationId.compareTo(operationIdToSearch) == 0 && findOperationId == false) {
                findOperationId = true;
            }
            Assertions.assertEquals(element.getTaxId(), searchNotificationRequest.getTaxId());
            //Viene verificato che l'operation id generato fa parte della lista

            Assertions.assertNotNull(element.getUncompletedIuns());
            List<SDNotificationSummary> listaiuns = element.getUncompletedIuns();
            Assertions.assertNotNull(element.getIuns());
            Assertions.assertNotNull(element.getNotificationStatus());
            //controllo sullo status
            if (operationIdToSearch.compareTo(actualOperationId) == 0) {
                logger.info("STATO NOTIFICA " + element.getNotificationStatus().getStatus().getValue());
                Assertions.assertEquals(element.getNotificationStatus().getStatus().getValue(), status);
            }
            Assertions.assertNotNull(element.getOperationCreateTimestamp());
            Assertions.assertNotNull(element.getOperationUpdateTimestamp());
        }
    }

    @Then("Il servizio SEARCH risponde con lista vuota")
    public void verifySearchResponseEmpty() {
        List<OperationResponse> lista = searchResponse.getOperations();
        logger.info("STAMPA LISTA " + lista.toString());
        //   Assertions.assertNull(lista);
        Assertions.assertEquals(lista.toString(), "[]");

    }

    @Then("il video viene caricato su SafeStorage")
    public void loadFileSafeStorage() {
        logger.info("PROVAA");
        // notificationDocument = newDocument("classpath:/video.mp4");
        String resourceName = notificationDocument.getRef().getKey();
        logger.info("Resouce name" + resourceName);
        AtomicReference<NotificationDocument> notificationDocumentAtomic = new AtomicReference<>();
        loadToPresigned(videoUploadResponse.getUrl(), videoUploadResponse.getSecret(), videoUploadRequest.getSha256(), resourceName);
        notificationDocument.getRef().setKey(videoUploadResponse.getFileKey());
        notificationDocument.getRef().setVersionToken("v1");
        notificationDocument.digests(new NotificationAttachmentDigests().sha256(videoUploadRequest.getSha256()));
    }


    @Then("il video viene caricato su SafeStorage con url scaduta")
    public void loadFileSafeStorageUrlExpired() {
        try {
            try {
                logger.info("PROVAA");
                Thread.sleep(3720000);//aspetta 62 minuti
                // notificationDocument = newDocument("classpath:/video.mp4");
                String resourceName = notificationDocument.getRef().getKey();
                logger.info("Resouce name" + resourceName);
                AtomicReference<NotificationDocument> notificationDocumentAtomic = new AtomicReference<>();
                loadToPresigned(videoUploadResponse.getUrl(), videoUploadResponse.getSecret(), videoUploadRequest.getSha256(), resourceName);
                notificationDocument.getRef().setKey(videoUploadResponse.getFileKey());
                notificationDocument.getRef().setVersionToken("v1");
            } catch (InterruptedException e) {
                logger.error("Thread.sleep error retry");
                throw new RuntimeException(e);
            }
        } catch (HttpStatusCodeException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notificationError = (HttpStatusCodeException) e;
            }
        }

    }

    @Then("il video viene caricato su SafeStorage con errore")
    public void loadFileSafeStorageWithError() {
        try {
            logger.info("PROVAA");
            // notificationDocument = newDocument("classpath:/video.mp4");
            String resourceName = notificationDocument.getRef().getKey();
            logger.info("Resouce name" + resourceName);
            AtomicReference<NotificationDocument> notificationDocumentAtomic = new AtomicReference<>();
            loadToPresigned(videoUploadResponse.getUrl(), videoUploadResponse.getSecret(), videoUploadRequest.getSha256(), resourceName);
            notificationDocument.getRef().setKey(videoUploadResponse.getFileKey());
            notificationDocument.getRef().setVersionToken("v1");
        } catch (HttpStatusCodeException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notificationError = (HttpStatusCodeException) e;
            }
        }

    }

    @Then("viene effettuato un controllo sulla durata della retention")
    public void retentionCheckPreload() {
        String key = notificationDocument.getRef().getKey();
        logger.info("Resouce name" + key);
        try {
            Thread.sleep(900000);
        } catch (InterruptedException e) {
            logger.error("Thread.sleep error retry");
            throw new RuntimeException(e);
        }
        logger.info("Fine delay");
        Assertions.assertTrue(checkRetetion(key, retentionTimePreLoad));
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

    private String randomAlphaNumeric(int count) throws Exception {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    private String randomNumeric(int count) throws Exception {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * NUMERIC_STRING.length());
            builder.append(NUMERIC_STRING.charAt(character));
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
        logger.info("safestorage response: " + safeStorageResponse);
        LocalDateTime localDateTimeNow = LocalDate.now().atStartOfDay();
        OffsetDateTime now = OffsetDateTime.of(localDateTimeNow, ZoneOffset.of("Z"));
        OffsetDateTime retentionUntil = OffsetDateTime.parse(safeStorageResponse.getRetentionUntil());
        logger.info("now: " + now);
        logger.info("retentionUntil: " + retentionUntil);
        long between = ChronoUnit.DAYS.between(now, retentionUntil);
        logger.info("Difference: " + between);
        return retentionTime == between;
    }


    //Cruscotto Assistenza............
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
    }


    @Given("l'operatore richiede l'elenco di tutti i messaggi di cortesia inviati con cf vuoto")
    public void lOperatoreRichiedeLElencoDiDiTuttiIMessaggiDiCortesiaInviatiConCfVuoto() {
        try {
            searchNotificationsRequest = new SearchNotificationsRequest();
            searchNotificationsRequest.setTaxId(CF_vuoto);
            searchNotificationsRequest.setRecipientType(RecipientType.PF);
            searchNotificationsResponse = ipServiceDeskClient.searchNotificationsFromTaxId(10, null, null, null, searchNotificationsRequest);
            try {
                Thread.sleep(getWorkFlowWait());
            } catch (InterruptedException e) {
                logger.error("Thread.sleep error retry");
                throw new RuntimeException(e);
            }
            Assertions.assertNotNull(searchNotificationsResponse);
        } catch (HttpStatusCodeException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notificationError = (HttpStatusCodeException) e;
            }
        }
    }

    @Given("l'operatore richiede l'elenco di tutti i messaggi di cortesia inviati con cf errato {string}")
    public void lOperatoreRichiedeLElencoDiDiTuttiIMessaggiDiCortesiaInviatiConCfErrato(String cf) {

        try {
            searchNotificationsRequest = new SearchNotificationsRequest();
            searchNotificationsRequest.setTaxId(cf);
            searchNotificationsRequest.setRecipientType(RecipientType.PF);
            searchNotificationsResponse = ipServiceDeskClient.searchNotificationsFromTaxId(10, null, null, null, searchNotificationsRequest);
            try {
                Thread.sleep(getWorkFlowWait());
            } catch (InterruptedException e) {
                logger.error("Thread.sleep error retry");
                throw new RuntimeException(e);
            }
            Assertions.assertNotNull(searchNotificationsResponse);
        } catch (HttpStatusCodeException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notificationError = (HttpStatusCodeException) e;
            }
        }
    }


    @Given("l'operatore richiede l'elenco di tutti i messaggi di cortesia inviati con recipientType vuoto")
    public void lOperatoreRichiedeLElencoDiDiTuttiIMessaggiDiCortesiaInviatiConRecipientTypeVuoto() {
        try {
            searchNotificationsRequest = new SearchNotificationsRequest();
            searchNotificationsRequest.setTaxId(CF_corretto);
            searchNotificationsRequest.setRecipientType(null);
            searchNotificationsResponse = ipServiceDeskClient.searchNotificationsFromTaxId(10, null, null, null, searchNotificationsRequest);
            try {
                Thread.sleep(getWorkFlowWait());
            } catch (InterruptedException e) {
                logger.error("Thread.sleep error retry");
                throw new RuntimeException(e);
            }
            Assertions.assertNotNull(searchNotificationsResponse);
        } catch (HttpStatusCodeException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notificationError = (HttpStatusCodeException) e;
            }
        }
    }

    //TODO Codice Duplicato
    @Given("l'operatore richiede elenco di tutti i messaggi di cortesia inviati con taxId {string} recipientType  {string} e con searchPageSize {string} searchNextPagesKey {string} startDate {string} endDate {string}")
    public void lOperatoreRichiedeLElencoDiTuttiIMessaggiDiCortesiaInviatiConCfErratoERecipientType(String taxId, String recipientType, String searchPageSize, String searchNextPagesKey, String startDate, String endDate) {
        try {
            Integer size = null;
            String nextPagesKey = null;
            OffsetDateTime sDate = null;
            OffsetDateTime eDate = null;

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            //DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            OffsetDateTime sentAt = OffsetDateTime.now();

            if (!"NULL".equalsIgnoreCase(searchPageSize)) {
                size = Integer.parseInt(searchPageSize);
            } else {
                //Default Value
                size = 10;
            }

            if (!"NULL".equalsIgnoreCase(searchNextPagesKey)) {
                nextPagesKey = searchNextPagesKey;
            }

            if (!"NULL".equalsIgnoreCase(startDate)) {
                LocalDateTime localDateStart = LocalDate.parse(startDate, dateTimeFormatter).atStartOfDay();
                sDate = OffsetDateTime.of(localDateStart, sentAt.getOffset());

                if (!"NULL".equalsIgnoreCase(endDate)) {
                    LocalDateTime localDateEnd = LocalDate.parse(endDate, dateTimeFormatter).atStartOfDay();
                    eDate = OffsetDateTime.of(localDateEnd, sentAt.getOffset());
                }
                /**
                 else {
                 eDate = sentAt;
                 }
                 **/
            }

            if (!"NULL".equalsIgnoreCase(endDate)) {
                LocalDateTime localDateEnd = LocalDate.parse(endDate, dateTimeFormatter).atStartOfDay();
                eDate = OffsetDateTime.of(localDateEnd, sentAt.getOffset());
            }

            searchNotificationsRequest = new SearchNotificationsRequest();
            if ("NULL".equalsIgnoreCase(taxId)) {
                searchNotificationsRequest.setTaxId(null);
            } else if ("VUOTO".equalsIgnoreCase(taxId)) {
                searchNotificationsRequest.setTaxId("");
            } else {
                searchNotificationsRequest.setTaxId(taxId);
            }

            if (!"NULL".equalsIgnoreCase(recipientType)) {
                setRecipientType(recipientType);
            }
            searchNotificationsResponse = ipServiceDeskClient.searchNotificationsFromTaxId(size, nextPagesKey, sDate, eDate, searchNotificationsRequest);

            Assertions.assertNotNull(searchNotificationsResponse);
            Assertions.assertTrue(searchNotificationsResponse.getResults().size() > 0);

            if(size==1 && nextPagesKey==null){
                Assertions.assertTrue(searchNotificationsResponse.getResults().size()==1);
            }
            if(size==50 && nextPagesKey==null){
                Assertions.assertTrue(searchNotificationsResponse.getResults().size()==50);
            }

            Assertions.assertTrue(searchNotificationsResponse.getResults().get(0).getCourtesyMessages().size() > 0);


        } catch (HttpStatusCodeException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notificationError = (HttpStatusCodeException) e;
            }
        }
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


    @Then("Il servizio risponde correttamente")
    public void ilServizioRispondeCorrettamente() {
        Assertions.assertNull(notificationError);
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
                profileRequest.setTaxId(taxId);
            }

            if (!"NULL".equalsIgnoreCase(recipientType)) {
                setRecipientType(recipientType);
            }

            profileResponse = ipServiceDeskClient.getProfileFromTaxId(profileRequest);
            Assertions.assertNotNull(profileResponse);
        } catch (HttpStatusCodeException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notificationError = (HttpStatusCodeException) e;
            }
        }

    }

    //TODO Codice Duplicato
    @Given("come operatore devo accedere all’elenco delle notifiche ricevute da un utente di Piattaforma Notifiche con taxId {string} recipientType  {string} e con searchPageSize {string} searchNextPagesKey {string} startDate {string} endDate {string}")
    public void comeOperatoreDevoAccedereAllElencoDelleNotificheRicevuteDaUnUtenteDiPiattaformaNotificheConCfERecipientType(String taxId, String recipientType, String searchPageSize, String searchNextPagesKey, String startDate, String endDate) {

        try {
            Integer size = null;
            String nextPagesKey = null;
            OffsetDateTime sDate = null;
            OffsetDateTime eDate = null;

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            OffsetDateTime sentAt = OffsetDateTime.now();

            if (!"NULL".equalsIgnoreCase(searchPageSize)) {
                size = Integer.parseInt(searchPageSize);
            } else {
                //Default Value
                size = 10;
            }

            if (!"NULL".equalsIgnoreCase(searchNextPagesKey)) {
                nextPagesKey = searchNextPagesKey;
            }

            if (!"NULL".equalsIgnoreCase(startDate)) {
                LocalDateTime localDateStart = LocalDate.parse(startDate, dateTimeFormatter).atStartOfDay();
                sDate = OffsetDateTime.of(localDateStart, sentAt.getOffset());

                if (!"NULL".equalsIgnoreCase(endDate)) {
                    LocalDateTime localDateEnd = LocalDate.parse(endDate, dateTimeFormatter).atStartOfDay();
                    eDate = OffsetDateTime.of(localDateEnd, sentAt.getOffset());
                }
                /**
                 else {
                 eDate = sentAt;
                 }
                 **/
            }

            if (!"NULL".equalsIgnoreCase(endDate)) {
                LocalDateTime localDateEnd = LocalDate.parse(endDate, dateTimeFormatter).atStartOfDay();
                eDate = OffsetDateTime.of(localDateEnd, sentAt.getOffset());
            }

            searchNotificationsRequest = new SearchNotificationsRequest();
            if ("NULL".equalsIgnoreCase(taxId)) {
                searchNotificationsRequest.setTaxId(null);
            } else if ("VUOTO".equalsIgnoreCase(taxId)) {
                searchNotificationsRequest.setTaxId("");
            } else {
                searchNotificationsRequest.setTaxId(taxId);
            }

            if (!"NULL".equalsIgnoreCase(recipientType)) {
                setRecipientType(recipientType);
            }
            searchNotificationsResponse = ipServiceDeskClient.searchNotificationsFromTaxId(size, nextPagesKey, sDate, eDate, searchNotificationsRequest);

            Assertions.assertNotNull(searchNotificationsResponse);
            if (size==1 && nextPagesKey==null ){
                Assertions.assertNotNull(searchNotificationsResponse.getResults());
                Assertions.assertTrue(searchNotificationsResponse.getResults().size()>0);
                Assertions.assertTrue(searchNotificationsResponse.getResults().size()==1);
            }
        } catch (HttpStatusCodeException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notificationError = (HttpStatusCodeException) e;
            }
        }
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
        } catch (HttpStatusCodeException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notificationError = (HttpStatusCodeException) e;
            }
        }

    }

    @And("invocazione servizio per recupero dettaglio notifica")
    public void recuperoDettaglioNotifica() {
        try {
            Assertions.assertNotNull(searchNotificationsResponse);
            Assertions.assertNotNull(searchNotificationsResponse.getResults());
            Assertions.assertTrue(searchNotificationsResponse.getResults().size() > 0);
            notificationDetailResponse = ipServiceDeskClient.getNotificationFromIUN(searchNotificationsResponse.getResults().get(0).getIun());

        } catch (HttpStatusCodeException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notificationError = (HttpStatusCodeException) e;
            }
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

            String iunSearch = null;
            if ("VUOTO".equalsIgnoreCase(iun)) {
                iunSearch = "";
            }   else if("".equalsIgnoreCase(iun)){
                Assertions.assertNotNull(searchNotificationsResponse);
                Assertions.assertNotNull(searchNotificationsResponse.getResults());
                Assertions.assertTrue(searchNotificationsResponse.getResults().size() > 0);
                iunSearch = searchNotificationsResponse.getResults().get(0).getIun();

            } else {
                iunSearch = iun;
            }

            boolean diversoTaxid = false;
            if ("NULL".equalsIgnoreCase(taxid)) {
                searchNotificationsRequest.setTaxId(null);
            } else if ("VUOTO".equalsIgnoreCase(taxid)) {
                searchNotificationsRequest.setTaxId("");
            } else {
                searchNotificationsRequest.setTaxId(taxid);
                if (!taxid.equalsIgnoreCase(taxidQuery)) {
                    diversoTaxid = true;
                }
            }

            timelineResponse = ipServiceDeskClient.getTimelineOfIUNAndTaxId(iunSearch, searchNotificationsRequest);

            if (diversoTaxid) {
                Assertions.assertNull(timelineResponse);
            }

        } catch (HttpStatusCodeException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notificationError = (HttpStatusCodeException) e;
            }
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
                searchNotificationsRequest.setTaxId(taxId);
            }
            String iunSearch = null;
            if ("VUOTO".equalsIgnoreCase(iun)) {
                iunSearch = "";
            } else if ("NULL".equalsIgnoreCase(iun)) {
                iunSearch = null;
            } else if ("".equalsIgnoreCase(iun)) {
                iunSearch = sharedSteps.getSentNotification().getIun();
            } else {
                iunSearch = iun;
            }

            timelineResponse = ipServiceDeskClient.getTimelineOfIUNAndTaxId(iunSearch, searchNotificationsRequest);

            Assertions.assertNotNull(timelineResponse);

            TimelineElement timelineElement = null;
            for (TimelineElement element : timelineResponse.getTimeline()) {
                if (element.getDetail() != null && element.getDetail().getRecIndex() != null && !destinatario.equals(element.getDetail().getRecIndex())) {
                    timelineElement = element;
                    break;
                }
            }

            Assertions.assertNull(timelineElement);

        } catch (HttpStatusCodeException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notificationError = (HttpStatusCodeException) e;
            }
        }

    }

    @Given("come operatore devo effettuare un check sulla disponibilità , validità e dimensione degli allegati con IUN {string} e taxId {string}  recipientType  {string}")
    public void comeOperatoreDevoEffettuareUnCheckSullaDisponibilitàValiditàEDimensioneDegliAllegatiConIUNRecipientType(String iun, String taxId, String recipientType) {

        try {
            documentsRequest = new DocumentsRequest();
            setRecipientType(recipientType);

            if ("NULL".equalsIgnoreCase(taxId)) {
                documentsRequest.setTaxId(null);
            } else if ("VUOTO".equalsIgnoreCase(taxId)) {
                documentsRequest.setTaxId("");
            } else {
                documentsRequest.setTaxId(taxId);
            }

            String iunSearch = null;
            if ("VUOTO".equalsIgnoreCase(iun)) {
                iunSearch = "";
            }else if ("NULL".equalsIgnoreCase(iun)) {
                    iunSearch = null;
            } else {
                iunSearch = iun;
            }

            documentsResponse = ipServiceDeskClient.getDocumentsOfIUN(iunSearch, documentsRequest);

            Assertions.assertNotNull(documentsResponse);

        } catch (HttpStatusCodeException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notificationError = (HttpStatusCodeException) e;
            }
        }

    }

    @Then("come operatore devo accedere alla lista delle Notifiche per le quali l’utente risulta destinatario come {string} di una persona fisica o di una persona giuridica con taxId {string} recipientType  {string} e con searchPageSize {string} searchNextPagesKey {string} startDate {string} endDate {string} searchMandateId {string} searchInternalId {string}")
    public void comeOperatoreDevoAccedereAllaListaDelleNotifichePerLeQualiLUtenteRisultaDestinatarioComeDelegatoDiUnaPersonaFisicaODiUnaPersonaGiuridicaConCfRecipientTypeEConSearchPageSizeSearchNextPagesKeyStartDateEndDate(String type, String taxId, String recipientType, String searchPageSize, String searchNextPagesKey, String startDate, String endDate, String searchMandateId, String searchInternalId) {

        try {
            Assertions.assertNotNull(profileResponse);
            Assertions.assertNotNull(profileResponse.getDelegateMandates());
            Assertions.assertTrue(profileResponse.getDelegateMandates().size() > 0);

            Integer size = null;
            String nextPagesKey = null;
            OffsetDateTime sDate = null;
            OffsetDateTime eDate = null;

            String typeSearch = null;

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            OffsetDateTime sentAt = OffsetDateTime.parse(dateTimeFormatter.format(OffsetDateTime.now()));

            if (!"NULL".equalsIgnoreCase(searchPageSize)) {
                size = Integer.parseInt(searchPageSize);
            } else {
                //Default Value
                size = 10;
            }

            if (!"NULL".equalsIgnoreCase(searchNextPagesKey)) {
                nextPagesKey = searchNextPagesKey;
            }

            if (!"NULL".equalsIgnoreCase(startDate)) {
                LocalDateTime localDateStart = LocalDate.parse(startDate, dateTimeFormatter).atStartOfDay();
                sDate = OffsetDateTime.of(localDateStart, sentAt.getOffset());

                if (!"NULL".equalsIgnoreCase(endDate)) {
                    LocalDateTime localDateEnd = LocalDate.parse(endDate, dateTimeFormatter).atStartOfDay();
                    eDate = OffsetDateTime.of(localDateEnd, sentAt.getOffset());
                } else {
                    eDate = sentAt;
                }
            } else {

                sDate = sentAt;
            }

            if (!"NULL".equalsIgnoreCase(endDate)) {
                LocalDateTime localDateEnd = LocalDate.parse(endDate, dateTimeFormatter).atStartOfDay();
                eDate = OffsetDateTime.of(localDateEnd, sentAt.getOffset());
            } else {
                eDate = sentAt;
            }

            String mandateIdSearch = null;
            if ("NULL".equalsIgnoreCase(searchMandateId)) {
                mandateIdSearch = null;
            } else if ("NO_SET".equalsIgnoreCase(searchMandateId)) {
                mandateIdSearch = profileResponse.getDelegateMandates().get(0).getMandateId();
            } else {
                mandateIdSearch = searchMandateId;
            }

            String internalIdSearch = null;
            if ("NULL".equalsIgnoreCase(searchInternalId)) {
                internalIdSearch = null;
            } else if ("NO_SET".equalsIgnoreCase(searchInternalId)) {
                if ("delegato".equalsIgnoreCase(type)) {
                    typeSearch = profileResponse.getDelegateMandates().get(0).getDelegateInternalId();
                } else if ("delegante".equalsIgnoreCase(type)) {
                    typeSearch = profileResponse.getDelegatorMandates().get(0).getDelegateInternalId();
                }
            } else {
                internalIdSearch = searchInternalId;
            }

            searchNotificationsResponse = ipServiceDeskClient.searchNotificationsAsDelegateFromInternalId(mandateIdSearch, typeSearch,recipientType, size, nextPagesKey, sDate, eDate);

            Assertions.assertNotNull(searchNotificationsResponse);
            Assertions.assertNotNull(searchNotificationsResponse.getResults());
            Assertions.assertTrue(searchNotificationsResponse.getResults().size()>0);
            Assertions.assertTrue(searchNotificationsResponse.getResults().get(0).getIun().equalsIgnoreCase(sharedSteps.getSentNotification().getIun()));

        } catch (HttpStatusCodeException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notificationError = (HttpStatusCodeException) e;
            }
        }

    }


    @Given("come operatore devo accedere alla lista di tutte le notifiche depositate da un ente \\(mittente) su Piattaforma Notifiche in un range temporale con paId {string} e con searchPageSize {string} searchNextPagesKey {string} startDate {string} endDate {string}")
    public void comeOperatoreDevoAccedereAllaListaDiTutteLeNotificheDepositateDaUnEnteMittenteSuPiattaformaNotificheInUnRangeTemporaleConPaIdEConSearchPageSizeSearchNextPagesKeyStartDateEndDate(String paId, String searchPageSize, String searchNextPagesKey, String startDate, String endDate) {
        try {
            Integer size = null;
            String nextPagesKey = null;
            OffsetDateTime sDate = null;
            OffsetDateTime eDate = null;

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            OffsetDateTime sentAt = OffsetDateTime.now();

            if (!"NULL".equalsIgnoreCase(searchPageSize)) {
                size = Integer.parseInt(searchPageSize);
            } else {
                //Default Value
                size = 10;
            }

            if (!"NULL".equalsIgnoreCase(searchNextPagesKey)) {
                nextPagesKey = searchNextPagesKey;
            }

            if (!"NULL".equalsIgnoreCase(startDate)) {
                LocalDateTime localDateStart = LocalDate.parse(startDate, dateTimeFormatter).atStartOfDay();
                sDate = OffsetDateTime.of(localDateStart, sentAt.getOffset());

                if (!"NULL".equalsIgnoreCase(endDate)) {
                    LocalDateTime localDateEnd = LocalDate.parse(endDate, dateTimeFormatter).atStartOfDay();
                    eDate = OffsetDateTime.of(localDateEnd, sentAt.getOffset());
                }
                /**
                 else {
                 eDate = sentAt;
                 }
                 **/
            }

            if (!"NULL".equalsIgnoreCase(endDate)) {
                LocalDateTime localDateEnd = LocalDate.parse(endDate, dateTimeFormatter).atStartOfDay();
                eDate = OffsetDateTime.of(localDateEnd, sentAt.getOffset());
            }

            paNotificationsRequest = new PaNotificationsRequest();
            if ("NULL".equalsIgnoreCase(paId)) {
                paNotificationsRequest.setId(null);
            } else if ("VUOTO".equalsIgnoreCase(paId)) {
                paNotificationsRequest.setId("");
            } else {
                paNotificationsRequest.setId(paId);
            }


            paNotificationsRequest.setStartDate(sDate);
            paNotificationsRequest.setEndDate(eDate);

            searchNotificationsResponse = ipServiceDeskClient.searchNotificationsFromSenderId(size, nextPagesKey, paNotificationsRequest);

            Assertions.assertNotNull(searchNotificationsResponse);
        } catch (HttpStatusCodeException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notificationError = (HttpStatusCodeException) e;
            }
        }
    }

    @And("invocazione servizio per recupero dettaglio notifica con iun {string}")
    public void invocazioneServizioPerRecuperoDettaglioNotificaConIun(String iun) {

        String iunSearch = null;
        try {
            if ("VUOTO".equalsIgnoreCase(iun)) {
                iunSearch = "";
            }else if ("NULL".equalsIgnoreCase(iun)) {
                    iunSearch = null;
            } else if ("".equalsIgnoreCase(iun)) {
                Assertions.assertNotNull(searchNotificationsResponse);
                Assertions.assertNotNull(searchNotificationsResponse.getResults());
                Assertions.assertTrue(searchNotificationsResponse.getResults().size() > 0);
                iunSearch = searchNotificationsResponse.getResults().get(0).getIun();
            } else {
                iunSearch = iun;
            }

            notificationDetailResponse = ipServiceDeskClient.getNotificationFromIUN(iunSearch);

            Assertions.assertNotNull(notificationDetailResponse);
        } catch (HttpStatusCodeException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notificationError = (HttpStatusCodeException) e;
            }
        }
    }


    @And("invocazione servizio per recupero timeline notifica con iun {string}")
    public void invocazioneServizioPerRecuperoTimelineNotificaConIun(String iun) {

        String iunSearch = null;
        try {
            if ("VUOTO".equalsIgnoreCase(iun)) {
                iunSearch = "";
            } else if ("".equalsIgnoreCase(iun)) {
                Assertions.assertNotNull(searchNotificationsResponse);
                Assertions.assertNotNull(searchNotificationsResponse.getResults());
                Assertions.assertTrue(searchNotificationsResponse.getResults().size() > 0);
                iunSearch = searchNotificationsResponse.getResults().get(0).getIun();

            } else if ("NULL".equalsIgnoreCase(iun)) {
                iunSearch = null;
            } else {
                iunSearch = iun;
            }

            timelineResponse = ipServiceDeskClient.getTimelineOfIUN(iunSearch);

            Assertions.assertNotNull(timelineResponse);
        } catch (HttpStatusCodeException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notificationError = (HttpStatusCodeException) e;
            }
        }
    }

    @Given("come operatore devo accedere alle informazioni relative alle richieste di API Key avanzate da un Ente mittente di notifiche sulla Piattaforma {string}")
    public void comeOperatoreDevoAccedereAlleInformazioniRelativeAlleRichiesteDiAPIKeyAvanzateDaUnEnteMittenteDiNotificheSullaPiattaforma(String paId){
        try {


            String paIDSearch = null;
            if ("NULL".equalsIgnoreCase(paId)) {
                paIDSearch = null;
            } else if ("VUOTO".equalsIgnoreCase(paId)) {
                paIDSearch = "";
            }else {
                paIDSearch = paId;
            }

             responseApiKeys = ipServiceDeskClient.getApiKeys(paIDSearch);

            Assertions.assertNotNull(responseApiKeys);
        } catch (HttpStatusCodeException e) {
            if (e instanceof HttpStatusCodeException) {
                this.notificationError = (HttpStatusCodeException) e;
            }
        }
    }



}

