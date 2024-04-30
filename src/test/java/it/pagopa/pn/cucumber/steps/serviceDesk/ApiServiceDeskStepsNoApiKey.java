package it.pagopa.pn.cucumber.steps.serviceDesk;


import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.config.PnB2bClientTimingConfigs;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationAttachmentBodyRef;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationDocument;
import it.pagopa.pn.client.b2b.pa.service.IPServiceDeskClientImplNoApiKey;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.serviceDesk.model.*;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.web.client.HttpStatusCodeException;
import java.io.IOException;


@Slf4j
public class ApiServiceDeskStepsNoApiKey {




    private final PnPaB2bUtils b2bUtils;

    private final IPServiceDeskClientImplNoApiKey ipServiceDeskClientImplNoApiKey;

    private final NotificationRequest notificationRequest;

    private final AnalogAddress analogAddress;

    private NotificationsUnreachableResponse notificationsUnreachableResponse;

    private final CreateOperationRequest createOperationRequest;

    private OperationsResponse operationsResponse;

    private final VideoUploadRequest videoUploadRequest;

    private VideoUploadResponse videoUploadResponse;

    private NotificationDocument notificationDocument;

    private final SearchNotificationRequest searchNotificationRequest;

    private SearchResponse searchResponse;

    private final String CF_vuoto =null;

    private final String CF_errato ="CPNTMS85T15H703WCPNTMS85T15H703W|";

    private final String CF_errato2 ="CPNTM@85T15H703W";

    private final Integer workFlowWaitDefault = 31000;




    private final Integer workFlowWait;

    @Value("${pn.retention.videotime.preload}")
    private Integer retentionTimePreLoad;

    private HttpStatusCodeException notificationError;

    private final ApplicationContext ctx;


    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ01234556789";


    @Autowired
    public ApiServiceDeskStepsNoApiKey(SharedSteps sharedSteps, ApplicationContext ctx, PnB2bClientTimingConfigs timingConfigs) {

        this.b2bUtils = sharedSteps.getB2bUtils();
        this.ipServiceDeskClientImplNoApiKey= sharedSteps.getServiceDeskClientNoApiKey();
        this.notificationRequest=new NotificationRequest();
        this.analogAddress=new AnalogAddress();
        this.createOperationRequest=new CreateOperationRequest();
        this.videoUploadRequest=new VideoUploadRequest();
        this.searchNotificationRequest=new SearchNotificationRequest();
        this.ctx=ctx;

        this.workFlowWait = timingConfigs.getWorkflowWaitMillis();
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
                log.info("Inserito CF:"+cf);
        }
    }


    @When("viene invocato il servizio UNREACHABLE con errore senza API Key")
    public void NotificationsUnreachableResponseWithErrorNoApiKey(){
        try {
            notificationsUnreachableResponse = ipServiceDeskClientImplNoApiKey.notification(notificationRequest);
            try {
                Thread.sleep(getWorkFlowWait());
            } catch (InterruptedException e) {
                log.error("Thread.sleep error retry");
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
        log.info("CF:"+cf);
        String ticketid= null;
        try {
            ticketid = "AUT"+randomAlphaNumeric(12);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("ticketid:"+ticketid);
        createOperationRequest.setTicketId(ticketid);
        String ticketOperationid= null;
        try {
            ticketOperationid = "AUT"+randomAlphaNumeric(7);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("ticketOperationid:"+ticketOperationid);
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
                log.error("Thread.sleep error retry");
                throw new RuntimeException(e);
            }
            Assertions.assertNotNull(notificationsUnreachableResponse);
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
        }

    }


    @Given("viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO senza API Key")
    public void createPreUploadVideoRequestNoApiKey() throws Exception {
        notificationDocument = newDocument("classpath:/video.mp4");
        String resourceName = notificationDocument.getRef().getKey();
        log.info("Resource name:"+resourceName);
        String sha256 = computeSha256( resourceName );
        log.info("sha:"+sha256);
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
                log.error("Thread.sleep error retry");
                throw new RuntimeException(e);
            }
            Assertions.assertNotNull(videoUploadResponse);
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
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
                log.error("Thread.sleep error retry");
                throw new RuntimeException(e);
            }
            Assertions.assertNotNull(searchResponse);
        } catch (HttpStatusCodeException e) {
            this.notificationError = e;
        }

    }

    @Then("il servizio risponde con errore {string} senza API Key")
    public void operationProducedAnErrorNoApiKey(String statusCode) {
        notificationError.getStatusCode();
        Assertions.assertEquals(notificationError.getStatusCode().toString().substring(0, 3), statusCode);
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
