package it.pagopa.pn.cucumber.steps.tracciamentoEventi;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.externalchannels.v1.model.CourtesyMessageProgressEvent;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.externalchannels.v1.model.DigitalCourtesyMailRequest;
import it.pagopa.pn.client.b2b.pa.service.IPnExternalChannelsInternalClient;
import it.pagopa.pn.client.b2b.pa.service.impl.PnSafeStoragePrivateClientImpl;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.model.FileCreationRequest;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.model.FileCreationResponse;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class TracciamentoEventiPeoSteps {

    private final SharedSteps sharedSteps;
    private final PnSafeStoragePrivateClientImpl safeStorageClient;
    private final IPnExternalChannelsInternalClient externalChannelsInternalClient;
    private final String deliveryBaseUrl;
    private String clientInUse;
    private String requestId;
    private static final String EICAR = "X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*";

    @Autowired
    public TracciamentoEventiPeoSteps(SharedSteps sharedSteps,
                                      PnSafeStoragePrivateClientImpl safeStorageClient,
                                      IPnExternalChannelsInternalClient externalChannelsInternalClient,
                                      @Value("${pn.internal.delivery-base-url}") String deliveryBaseUrl) {
        this.sharedSteps = sharedSteps;
        this.safeStorageClient = safeStorageClient;
        this.externalChannelsInternalClient = externalChannelsInternalClient;
        this.deliveryBaseUrl = deliveryBaseUrl;
    }

    @Given("il client in uso è {string}")
    public void setClientInUse(String clientId) {
        this.clientInUse = clientId;
        safeStorageClient.customApiClient(clientId);
    }

    @When("viene inviata una mail tramite PEO all'indirizzo {string} con allegato {string}")
    public void sendEmailWithAttachment(String emailAddress, String attachmentType) {

        String timestamp = Instant.now().toString();
        requestId = "TEST_QA_" + timestamp;

        DigitalCourtesyMailRequest emailRequest = new DigitalCourtesyMailRequest();
        emailRequest.setRequestId(requestId);
        emailRequest.setEventType("COURTESY_MESSAGE");
        emailRequest.setClientRequestTimeStamp(timestamp);
        emailRequest.setQos(DigitalCourtesyMailRequest.QosEnum.INTERACTIVE);
        emailRequest.setReceiverDigitalAddress(emailAddress);
        emailRequest.setMessageText("Questo è un messaggio di cortesia da parte di QA");
        emailRequest.setChannel(DigitalCourtesyMailRequest.ChannelEnum.EMAIL);
        emailRequest.setSubjectText("Test QA invio email");
        emailRequest.setMessageContentType(DigitalCourtesyMailRequest.MessageContentTypeEnum.PLAIN);
        emailRequest.setAttachmentUrls(
                attachmentType.equalsIgnoreCase("virus") ? List.of(uploadEicarVirusFile()) : new ArrayList<>());

        try {
            externalChannelsInternalClient.sendDigitalCourtesyMessage(requestId, clientInUse, emailRequest);
            log.info("Email inviata con successo");
            log.info("Request id: {}", requestId);
        } catch (Exception e) {
            log.info("Errore in fase di invio email. {}", e.getMessage());
        }

//        HttpClient client = HttpClient.newHttpClient();
//
//        String jsonBody = """
//                {
//                  "requestId": "%s",
//                  "eventType": "COURTESY_MESSAGE",
//                  "clientRequestTimeStamp": "%s",
//                  "qos": "INTERACTIVE",
//                  "receiverDigitalAddress": "%s",
//                  "messageText": "Questo è un messaggio di cortesia da parte di QA",
//                  "channel": "EMAIL",
//                  "subjectText": "Test QA invio email",
//                  "messageContentType": "text/plain",
//                  "attachmentUrls": %s
//                }
//                """.formatted(requestId, timestamp, emailAddress, getAttachmentUrls(attachmentType));
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(externalChannelsBaseUrl + "/external-channels/v1/digital-deliveries/courtesy-full-message-requests/" + requestId))
//                .header("x-pagopa-extch-cx-id", clientInUse)
//                .header("Content-Type", "application/json")
//                .header("Accept", "application/json")
//                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
//                .build();

//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//        log.info("Response status invio mail: {}", response.statusCode());
//        if (response.statusCode() >= 300) {
//            throw new RuntimeException("Errore nella chiamata PUT: " + response.statusCode() + " - " + response.body());
//        }
//        log.info("Email inviata con successo");
//        log.info("Request per invio mail: {}", emailRequest);
    }

//    private String getAttachmentUrls(String attachmentType) {
//        switch (attachmentType.toLowerCase()) {
//            case "null" -> {
//                log.info("Nessun allegato richiesto per questo test.");
//                return "[]";
//            }
//            case "virus" -> {
//                log.info("Allegato malevolo (EICAR) richiesto.");
//                return String.format("[\"%s\"]", uploadEicarVirusFile());
//            }
//            default -> throw new IllegalArgumentException("Invalid attachment: " + attachmentType);
//        }
//    }

    private String uploadEicarVirusFile() {
        byte[] byteArray = EICAR.getBytes(StandardCharsets.US_ASCII);
        String sha256 = B2bUtils.computeSha256(new ByteArrayInputStream(byteArray));

        FileCreationRequest request = new FileCreationRequest();
        request.setStatus("SAVED");
        request.setContentType("text/plain");
        request.setDocumentType("PN_NOTIFICATION_ATTACHMENTS");

        // Chiamata a Safe Storage per registrare il file e ottenere la presigned url di upload
        ResponseEntity<FileCreationResponse> responseEntity = safeStorageClient.createFileWithHttpInfo(clientInUse, sha256, "SHA256", request);
        assertThat(responseEntity).as("La responseEntity non dev'essere null").isNotNull();
        FileCreationResponse fileCreationResponse = responseEntity.getBody();
        assertThat(fileCreationResponse).as("La FileCreationResponse non dev'essere null").isNotNull();

        String fileKey = fileCreationResponse.getKey();
        String secret = fileCreationResponse.getSecret();
        String url = fileCreationResponse.getUploadUrl();

        B2bUtils.loadToPresignedFromByteArray(sharedSteps.getContext(), url, secret, sha256, byteArray, "text/plain");
        return "safestorage://" + fileKey;
    }

    @Then("recuperando la request da gestore-repository, verifico che il record abbia un'eventsList coi seguenti eventi {string}")
    public void retrieveRequestFromGestoreRepository(String events) throws IOException, InterruptedException {
        log.info("Waiting 1 minute for the email to be delivered");
        Thread.sleep(60000L);

//        List<CourtesyMessageProgressEvent> eventsList = externalChannelsInternalClient.getDigitalCourtesyMessageStatus(requestId, clientInUse);
//        checkEventsListNew(eventsList, events);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(deliveryBaseUrl + "/external-channel/gestoreRepository/requests/" + requestId))
                .header("x-pagopa-extch-cx-id", clientInUse)
                .header("Accept", "application/json")
                .GET()
                .build();

        log.info("Request per recupero da gestore-repository: {}", request);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Errore nella chiamata GET: " + response.statusCode() + " - " + response.body());
        }
        String prettyJson = B2bUtils.logPrettyResponse(response.body());
        checkEventsList(prettyJson, events);
    }

    private void checkEventsList(String json, String events) {
        Object eventsList = JsonPath.read(json, "$.requestMetadata.eventsList");
        assertThat(eventsList).as("Il campo eventsList deve esistere").isNotNull();
        assertThat(eventsList).isInstanceOf(JSONArray.class);

        String[] eventsExpected = events.split(";");
        Map<String, String> mapCodeStatus = new HashMap<>();
        for (String event : eventsExpected) {
            String[] codeStatus = event.split("-");
            mapCodeStatus.put(codeStatus[0], codeStatus[1]);
        }
        mapCodeStatus.forEach((statusCode, status) -> {
            String filter = String.format(
                    "$.requestMetadata.eventsList[?(@.digProgrStatus.statusCode == '%s' && @.digProgrStatus.status == '%s')]",
                    statusCode, status
            );
            JSONArray filteredEvents = JsonPath.read(json, filter);
            assertThat(filteredEvents)
                    .as("L'eventsList dovrebbe contenere un evento con statusCode '%s' e status '%s'", statusCode, status)
                    .asList().isNotEmpty();
        });
    }

    private void checkEventsListNew(List<CourtesyMessageProgressEvent> eventsList, String expectedOutput) {
        assertThat(eventsList).as("L'eventsList restituita non dev'essere null").isNotNull();

        String[] eventsExpected = expectedOutput.split(";");
        Map<String, String> mapCodeStatus = new HashMap<>();
        for (String event : eventsExpected) {
            String[] codeStatus = event.split("-");
            mapCodeStatus.put(codeStatus[0], codeStatus[1]);
        }
        mapCodeStatus.forEach((statusCode, status) -> {

            CourtesyMessageProgressEvent expectedEvent = eventsList.stream().filter(e ->
                            e.getEventCode().toString().equals(statusCode)
                                    && e.getStatus().toString().equalsIgnoreCase(status))
                    .findFirst().orElse(null);
            assertThat(expectedEvent)
                    .as("L'eventsList dovrebbe contenere un evento con statusCode '%s' e status '%s'", statusCode, status)
                    .isNotNull();
        });
    }
}
