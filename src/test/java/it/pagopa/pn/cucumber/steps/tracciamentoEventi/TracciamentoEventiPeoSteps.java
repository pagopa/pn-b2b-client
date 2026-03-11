package it.pagopa.pn.cucumber.steps.tracciamentoEventi;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class TracciamentoEventiPeoSteps {

    private final String safeStorageBaseUrl;
    private final SharedSteps sharedSteps;
    private String requestId;

    @Autowired
    public TracciamentoEventiPeoSteps(@Value("${pn.safeStorage.base-url}") String safeStorageBaseUrl, SharedSteps sharedSteps) {
        this.safeStorageBaseUrl = safeStorageBaseUrl;
        this.sharedSteps = sharedSteps;
    }

    @Given("viene inviata una mail tramite PEO all'indirizzo {string} con allegato {string}")
    public void sendEmailWithAttachment(String emailAddress, String attachmentType) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String timestamp = Instant.now().toString();
        requestId = "TEST_QA_" + timestamp;
        String jsonBody = """
                {
                  "requestId": "%s",
                  "eventType": "COURTESY_MESSAGE",
                  "clientRequestTimeStamp": "%s",
                  "qos": "INTERACTIVE",
                  "receiverDigitalAddress": "%s",
                  "messageText": "Questo è un messaggio di cortesia da parte di QA",
                  "channel": "EMAIL",
                  "subjectText": "Test QA invio email",
                  "messageContentType": "text/plain",
                  "attachmentUrls": %s
                }
                """.formatted(requestId, timestamp, emailAddress, getAttachmentUrls(attachmentType));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(safeStorageBaseUrl + "/external-channels/v1/digital-deliveries/courtesy-full-message-requests/" + requestId))
                .header("x-pagopa-extch-cx-id", "pn-test")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        log.info("Request per invio mail:\n{}", request);
        log.info("Body per invio mail:\n{}", jsonBody);

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 300) {
            throw new RuntimeException("Errore nella chiamata PUT: " + response.statusCode() + " - " + response.body());
        }
        log.info("Email inviata con successo");
    }

    private String getAttachmentUrls(String attachmentType) {
        switch (attachmentType.toLowerCase()) {
            case "null" -> {
                log.info("Nessun allegato richiesto per questo test.");
                return "[]";
            }
            case "virus" -> {
                log.info("Allegato malevolo (EICAR) richiesto.");
                return "[\"https://www.eicar.org/download/eicar.com.txt\"]";
            }
            default -> throw new IllegalArgumentException("Invalid attachment: " + attachmentType);
        }
    }

    @Then("recuperando la request da gestore-repository, verifico che il record contenga un evento con statusCode {string} e status {string}")
    public void checkPnEcRichiesteMetadatiRecord(String statusCode, String status) throws IOException, InterruptedException {
        sleep();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(safeStorageBaseUrl + "/external-channel/gestoreRepository/requests/" + requestId))
                .header("x-pagopa-extch-cx-id", "pn-test")
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Errore nella chiamata GET: " + response.statusCode() + " - " + response.body());
        }
        logPrettyResponse(response.body());
        checkResponseFields(response.body(), statusCode, status);
    }

    private void checkResponseFields(String json, String statusCode, String status) {
        Object eventsList = JsonPath.read(json, "$.requestMetadata.eventsList");
        assertThat(eventsList).as("Il campo eventsList deve esistere").isNotNull();
        assertThat(eventsList).isInstanceOf(JSONArray.class);
        String filter = String.format(
                "$.requestMetadata.eventsList[?(@.digProgrStatus.statusCode == '%s' && @.digProgrStatus.status == '%s')]",
                statusCode, status
        );
        JSONArray filteredEvents = JsonPath.read(json, filter);
        assertThat(filteredEvents)
                .as("L'eventsList dovrebbe contenere un evento con statusCode '%s' e status '%s'", statusCode, status)
                .asList().isNotEmpty();
    }

    private void logPrettyResponse(String rawJson) {
        try {
            Object jsonObject = sharedSteps.getObjMapper().readValue(rawJson, Object.class);
            String prettyJson = sharedSteps.getObjMapper().writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
            log.info("GET response body formattato:\n{}", prettyJson);
        } catch (Exception e) {
            log.warn("Impossibile formattare il JSON, stampo l'originale: {}", rawJson);
        }
    }

    private void sleep() {
        log.info("Waiting 1 minute for the email to be delivered");
        try {
            Thread.sleep(60000L);
        } catch (InterruptedException e) {
            throw new RuntimeException("Error while pausing the Thread");
        }
    }
}
