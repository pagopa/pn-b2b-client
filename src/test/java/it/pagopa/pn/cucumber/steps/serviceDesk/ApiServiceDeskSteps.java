package it.pagopa.pn.cucumber.steps.serviceDesk;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.testclient.IPServiceDeskClientImpl;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.model.*;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpStatusCodeException;

import java.lang.invoke.MethodHandles;
import java.util.List;

public class ApiServiceDeskSteps {

    private final SharedSteps sharedSteps;
    private NotificationRequest notificationRequest;
    private final IPServiceDeskClientImpl ipServiceDeskClient;

    private NotificationsUnreachableResponse notificationsUnreachableResponse;

    private CreateOperationRequest createOperationRequest;

    private OperationsResponse operationsResponse;

    private VideoUploadRequest videoUploadRequest;

    private VideoUploadResponse videoUploadResponse;

    private SearchNotificationRequest searchNotificationRequest;

    private SearchResponse searchResponse;

    private AnalogAddress analogAddress;

    private final Integer workFlowWaitDefault = 31000;

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String CF_vuoto =null;

    private static final String CF_errato ="XXXXXXXXXXXXXXXXX";

    private static final String CF_errato2 ="CPNTM@85T15H703W";

    @Value("${pn.configuration.workflow.wait.millis:31000}")
    private Integer workFlowWait;

    @Autowired
    public ApiServiceDeskSteps(SharedSteps sharedSteps, NotificationRequest notificationRequest, IPServiceDeskClientImpl ipServiceDeskClient, CreateOperationRequest createOperationRequest, OperationsResponse operationsResponse, VideoUploadRequest videoUploadRequest, VideoUploadResponse videoUploadResponse, SearchNotificationRequest searchNotificationRequest, SearchResponse searchResponse, AnalogAddress analogAddress) {
        this.sharedSteps = sharedSteps;
        this.notificationRequest = notificationRequest;
        this.ipServiceDeskClient = ipServiceDeskClient;
        this.createOperationRequest=createOperationRequest;
        this.operationsResponse = operationsResponse;
        this.videoUploadRequest = videoUploadRequest;
        this.videoUploadResponse = videoUploadResponse;
        this.searchNotificationRequest = searchNotificationRequest;
        this.searchResponse = searchResponse;
        this.analogAddress=analogAddress;
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
        }
    }

    @When("viene invocato il servizio UNREACHABLE")
    public void NotificationsUnreachableResponse(){
        try {
            Assertions.assertDoesNotThrow(() -> {
                notificationsUnreachableResponse=ipServiceDeskClient.notification(notificationRequest);
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
                    "{Numero di pratiche non consegnate per Irr Tot " + (notificationsUnreachableResponse == null ? "NULL" : notificationsUnreachableResponse.getNotificationsCount()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }
    @Then("la risposta del servizio UNREACHABLE è {int}")
        public void verifyNotificationsUnreachableResponse(Integer count){
        Long notificationsCount=notificationsUnreachableResponse.getNotificationsCount();
        Assertions.assertEquals(notificationsCount, count);
        logger.info("Presenza notifiche per il CF"+this.notificationRequest.getTaxId()+":"+notificationsCount);
    }


    @Then("il servizio risponde con errore {string}")
    public void operationProducedAnError(String statusCode) {
        HttpStatusCodeException httpStatusCodeException = this.sharedSteps.consumeNotificationError();
        Assertions.assertTrue((httpStatusCodeException != null) &&
                (httpStatusCodeException.getStatusCode().toString().substring(0, 3).equals(statusCode)));
    }

    public Integer getWorkFlowWait() {
        if (workFlowWait == null) return workFlowWaitDefault;
        return workFlowWait;
    }

    @Given("viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION per con {string} {string} {string}")
    public void createOperationRequest(String cf, String ticketid, String ticketOperationid) {
        createOperationRequest.setTaxId(cf);
        createOperationRequest.setTicketId(ticketid);
        createOperationRequest.setTicketId(ticketOperationid);
        createOperationRequest.setAddress(analogAddress);

    }

    @Given("viene comunicato il nuovo indirizzo con {string}")
    public void createOperationRequest(String indirizzo) {

        switch (indirizzo) {
            case "indirizzo_vuoto":
                analogAddress.setFullname("");
                analogAddress.setNameRow2("");
                analogAddress.setAddress("");
                analogAddress.setAddressRow2("");
                analogAddress.cap("");
                analogAddress.setCity("");
                analogAddress.setCity2("");
                analogAddress.setCountry("");
                analogAddress.setPr("");
                break;
            case "indirizzo_errato":

                break;
            case "CF_errato2":

                break;
            default:

        }

    }

    @Given("viene comunicato il nuovo indirizzo con {string} {string} {string} {string} {string} {string} {string} {string} {string}")
    public void createOperationRequest(String fullname, String namerow2, String address,String addressRow2,String cap, String city, String city2,String pr,String country) {
        analogAddress.setFullname(fullname);
        analogAddress.setNameRow2(namerow2);
        analogAddress.setAddress(address);
        analogAddress.setAddressRow2(addressRow2);
        analogAddress.cap(cap);
        analogAddress.setCity(city);
        analogAddress.setCity2(city2);
        analogAddress.setCountry(pr);
        analogAddress.setPr(country);
    }

    @When("viene invocato il servizio CREATE_OPERATION")
    public void createOperationResponse(){
        try {
            Assertions.assertDoesNotThrow(() -> {
                operationsResponse=ipServiceDeskClient.createOperation(createOperationRequest);
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

    @Given("viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con {string} {string} {string}")
    public void createPreUploadVideoRequest(String preloadIdx, String sha256, String contenType) {
        videoUploadRequest.setPreloadIdx(preloadIdx);
        videoUploadRequest.setSha256(sha256);
        videoUploadRequest.setContentType(contenType);

        }

    @When("viene invocato il servizio UPLOAD VIDEO con {string}")
    public void preUploadVideoResponse(String operationId){
        try {
            Assertions.assertDoesNotThrow(() -> {
                videoUploadResponse=ipServiceDeskClient.presignedUrlVideoUpload(operationId,videoUploadRequest);
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

    @When("viene invocato il servizio UPLOAD VIDEO")
    public void preUploadVideoResponse(){
        try {
            Assertions.assertDoesNotThrow(() -> {
                videoUploadResponse=ipServiceDeskClient.presignedUrlVideoUpload(operationsResponse.getOperationId(),videoUploadRequest);
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

    @When("viene invocato il servizio SEARCH")
    public void searchResponse(){
        try {
            Assertions.assertDoesNotThrow(() -> {
                searchResponse=ipServiceDeskClient.searchOperationsFromTaxId(searchNotificationRequest);
                List<OperationResponse> op=searchResponse.getOperations();

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
                    "{Numero di pratiche non consegnate per Irr Tot " + (searchResponse == null ? "NULL" : searchResponse.getOperations().toString()) + " }";
            throw new AssertionFailedError(message, assertionFailedError.getExpected(), assertionFailedError.getActual(), assertionFailedError.getCause());
        }
    }

}

