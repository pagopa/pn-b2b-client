package it.pagopa.pn.cucumber.steps.serviceDesk;


import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationAttachmentBodyRef;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationAttachmentDigests;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationDocument;
import it.pagopa.pn.client.b2b.pa.impl.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.testclient.IPServiceDeskClientImpl;
import it.pagopa.pn.client.b2b.pa.testclient.IPServiceDeskClientImplNoApiKey;
import it.pagopa.pn.client.b2b.pa.testclient.PnExternalServiceClientImpl;
import it.pagopa.pn.client.b2b.pa.testclient.PnServiceDeskClientImplNoApiKey;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.model.*;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


public class ApiServiceDeskStepsNoApiKey {


    private final SharedSteps sharedSteps;

    private final PnPaB2bUtils b2bUtils;

    private final IPnPaB2bClient b2bClient;

    private final PnExternalServiceClientImpl safeStorageClient;

    private final IPServiceDeskClientImplNoApiKey ipServiceDeskClientImplNoApiKey;

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

    private static final String CF_vuoto =null;

    private static final String CF_errato ="CPNTMS85T15H703WCPNTMS85T15H703W|";

    private static final String CF_errato2 ="CPNTM@85T15H703W";

    private static final String ticketid_vuoto =null;

    private static final String ticketid_errato ="XXXXXXXXXXXXXXXXXxxxxxxxxxxxxxxxX";

    private static final String ticketoperationid_vuoto =null;

    private static final String ticketoperationid_errato ="abcdfeghilm";

    private final Integer workFlowWaitDefault = 31000;

    private final Integer delay=420000;

    @Value("${pn.configuration.workflow.wait.millis:31000}")
    private Integer workFlowWait;

    @Value("${pn.retention.videotime.preload}")
    private Integer retentionTimePreLoad;

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private HttpStatusCodeException notificationError;

    private final ApplicationContext ctx;


    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ01234556789";
    private static final String NUMERIC_STRING = "0123456789";


    @Autowired
    public ApiServiceDeskStepsNoApiKey(SharedSteps sharedSteps,ApplicationContext ctx, IPServiceDeskClientImpl ipServiceDeskClient,PnExternalServiceClientImpl safeStorageClient) {
        this.sharedSteps = sharedSteps;
        this.b2bUtils = sharedSteps.getB2bUtils();
        this.b2bClient = sharedSteps.getB2bClient();
        this.safeStorageClient=safeStorageClient;
        this.ipServiceDeskClientImplNoApiKey= sharedSteps.getServiceDeskClientNoApiKey();
        this.notificationRequest=new NotificationRequest();
        this.analogAddress=new AnalogAddress();
        this.createOperationRequest=new CreateOperationRequest();
        this.videoUploadRequest=new VideoUploadRequest();
        this.searchNotificationRequest=new SearchNotificationRequest();
        this.ctx=ctx;
    }

    @Given("viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il {string} senza API Key")
    public void createVerifyUnreachableRequestNoApiKey(String cf) {
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
                logger.info("Inserito CF:"+cf);
        }
    }


    @When("viene invocato il servizio UNREACHABLE con errore senza API Key")
    public void NotificationsUnreachableResponseWithErrorNoApiKey(){
        try {
            notificationsUnreachableResponse = ipServiceDeskClientImplNoApiKey.notification(notificationRequest);
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

    @Given("viene comunicato il nuovo indirizzo con {string} {string} {string} {string} {string} {string} {string} {string} {string} senza API Key")
    public void createNewAddressRequestNoApiKey(String fullname, String namerow2, String address,String addressRow2,String cap, String city, String city2,String pr,String country) {
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


    @Given("viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con {string} senza API Key")
    public void createOperationReqNoApiKey(String cf) {
        createOperationRequest.setTaxId(cf);
        logger.info("CF:"+cf);
        String ticketid= null;
        try {
            ticketid = "AUT"+randomAlphaNumeric(12);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        logger.info("ticketid:"+ticketid);
        createOperationRequest.setTicketId(ticketid);
        String ticketOperationid= null;
        try {
            ticketOperationid = "AUT"+randomAlphaNumeric(7);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        logger.info("ticketOperationid:"+ticketOperationid);
        createOperationRequest.setTicketOperationId(ticketOperationid);
        createOperationRequest.setAddress(analogAddress);

    }

    @When("viene invocato il servizio CREATE_OPERATION senza API Key con errore")
    public void createOperationResponseWithErrorNoApiKey() {
        try {
            operationsResponse = ipServiceDeskClientImplNoApiKey.createOperation(createOperationRequest);
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


    @Given("viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO senza API Key")
    public void createPreUploadVideoRequestNoApiKey() throws Exception {
        notificationDocument = newDocument("classpath:/video.mp4");
        String resourceName = notificationDocument.getRef().getKey();
        logger.info("Resource name:"+resourceName);
        String sha256 = computeSha256( resourceName );
        logger.info("sha:"+sha256);
        videoUploadRequest.setPreloadIdx("AUT"+randomAlphaNumeric(5));
        videoUploadRequest.setSha256(sha256);
        videoUploadRequest.setContentType("application/octet-stream");

    }


    @When("viene invocato il servizio UPLOAD VIDEO senza API Key con {string} con errore")
    public void preUploadVideoResponseNoApiKey(String operationId){
        try {
            videoUploadResponse = ipServiceDeskClientImplNoApiKey.presignedUrlVideoUpload(operationId,videoUploadRequest);
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

    @Given("viene creata una nuova richiesta per invocare il servizio SEARCH per il {string} senza API Key")
    public void createSearchRequestNoApiKey(String cf) {
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

    @When("viene invocato il servizio SEARCH senza API Key con errore")
    public void searchResponseWithErrorNoApiKey(){
        try {
            searchResponse = ipServiceDeskClientImplNoApiKey.searchOperationsFromTaxId(searchNotificationRequest);
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

    @Then("il servizio risponde con errore {string} senza API Key")
    public void operationProducedAnErrorNoApiKey(String statusCode) {
        Assertions.assertTrue((notificationError.getStatusCode() != null) &&
                (notificationError.getStatusCode().toString().substring(0, 3).equals(statusCode)));
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


    public NotificationDocument newDocument(String resourcePath ) {
        return new NotificationDocument()
                .contentType("application/mp4")
                .ref( new NotificationAttachmentBodyRef().key( resourcePath ));
    }

    private String computeSha256( String resName ) throws IOException {
        Resource res = ctx.getResource( resName );
        return computeSha256( res );
    }

    private String computeSha256( Resource res ) throws IOException {
        return b2bUtils.computeSha256(res.getInputStream());
    }



}
