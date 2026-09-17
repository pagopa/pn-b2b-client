package it.pagopa.pn.cucumber.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.common.util.CloudWatchQueryBuilder;
import it.pagopa.common.util.StringUtils;
import it.pagopa.pn.client.b2b.generated.openapi.clients.io.connector.model.*;
import it.pagopa.pn.client.b2b.pa.domain.DynamoTableName;
import it.pagopa.pn.client.b2b.pa.service.DynamoDbService;
import it.pagopa.pn.client.b2b.pa.service.IPnIOConnectorClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.FilterLogEventsRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.FilterLogEventsResponse;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class IOConnectorSteps {
    private final SharedSteps sharedSteps;
    private final DynamoDbService dynamoDbService;
    private final IPnIOConnectorClient pnIOConnectorClient;

    public static final String SENDER_SERVICE_ID = "01KP5QYVRZDDEMHCN3TV1QY1H6";
    public static final String RECIPIENT_TAX_ID = "PF-ef4f3181-c2a9-4924-9307-d107af8f0c34";
    public static final String CLIENT_ID = "QA_CLIENT_ID";

    //scenario 5 va richiesta data-preparation. si deve richiedere un requestId e un recipientTaxId
    //di un messaggio censito e dotato di allegati. il recipientTaxId viene passato allo step parametrico dal file feature
    @Value("${pn.IOConnector.request-id-for-preexisting-message}")
    public String REQUEST_ID_FOR_PREEXISTING_MESSAGE;


    private MessageRequest messageRequest;
    private MessageResponse messageResponse;
    private GetMessageResponse getMessageResponse;
    private GetProfileResponse getProfileResponse;
    private HttpStatus actualResponseHttpStatus;

    @Autowired(required = false)
    private RestTemplate restTemplate;

    @Autowired(required = false)
    private CloudWatchLogsClient cloudWatchLogsClient;

    //-------SCENARIO 3:

    @Given("viene generata una richiesta valida per la presa in carico del messaggio")
    public void createValidRequest() {
        messageRequest = new MessageRequest();

        String requestId = "TEST-" + System.currentTimeMillis();

        messageRequest.setRequestId(requestId);
        messageRequest.setIun("IUN-TEST");
        messageRequest.setRecipientTaxId(RECIPIENT_TAX_ID);
        messageRequest.setSenderServiceId(SENDER_SERVICE_ID);
        messageRequest.setSubject("Oggetto di test");
        messageRequest.setMarkdown("Contenuto del messaggio");

    }

    @When("modifico il payload della request ma non il request Id")
    public void modifyRequestPayload() {
        messageRequest.setIun("IUN-" + System.currentTimeMillis());
        messageRequest.setRecipientTaxId("BCDEFG12345678901");
    }

    @And("sostituisco un valore non valido nel campo {string}")
    public void sostituiscoValoreNonValido(String field) {

        switch (field) {
            case "requestId":
                messageRequest.setRequestId(null);
                break;

            case "recipientTaxId":
                messageRequest.setRecipientTaxId(null);
                break;

            case "subject":
                messageRequest.setSubject(null);
                break;

            case "markdown":
                messageRequest.setMarkdown(null);
                break;

            case "iun":
                messageRequest.setIun(null);
                break;

            case "TUTTI":
                messageRequest.setRequestId(null);
                messageRequest.setRecipientTaxId(null);
                messageRequest.setSubject(null);
                messageRequest.setMarkdown(null);
                messageRequest.setIun(null);
                break;

            default:
                throw new IllegalArgumentException("Campo non gestito: " + field);
        }
    }


    @When("come orchestratore SEND richiedo l'invio del messaggio verso IO")
    public void invokeMessageAPI() {

        try {
            MessageResponse resp = pnIOConnectorClient.sendIOMessage(CLIENT_ID,
                    messageRequest);
            log.info("message response: {}", resp);
            messageResponse = resp;
            // If no exception is thrown, assume 200 OK
            actualResponseHttpStatus = HttpStatus.OK;
            // If no exception is thrown, and body is null, assume 204 NO_CONTENT
            if (resp == null)
                actualResponseHttpStatus = HttpStatus.NO_CONTENT;

        } catch (HttpStatusCodeException e) {
            log.info("HttpStatusException: {}", e.getMessage());
            actualResponseHttpStatus = e.getStatusCode();
        }
    }

    @Then("verifico che in tabella pn-IOConnectorRequests esista un record per requestId")
    public void verifyRecordExistsForRequestId() {

        Map<String, AttributeValue> attributeValues = Map.of(
                ":v_requestId", AttributeValue.builder().s(messageRequest.getRequestId()).build()
        );

        QueryResponse response = dynamoDbService.call(
                DynamoTableName.IO_CONNECTOR_REQUESTS,
                attributeValues
        );

        // assertions
        assertThat(response)
                .isNotNull();

        assertThat(response.count())
                .as("No records found for requestId: %s", messageRequest.getRequestId())
                .isGreaterThan(0);

        assertThat(response.items())
                .isNotNull()
                .isNotEmpty();
    }


    @Then("verifico che la risposta contenga tutti i campi obbligatori valorizzati")
    public void verificaCampiObbligatoriValorizzati() throws Exception {

        assertThat(messageResponse).isNotNull();

        // campi obbligatori
        assertThat(messageResponse.getRequestId())
                .isNotNull()
                .isNotBlank();

        assertThat(messageResponse.getStatus())
                .isNotNull()
                .isEqualTo(MessageResponse.StatusEnum.ACCEPTED);
    }


    //--------SCENARIO 4:
    @Given("come orchestratore SEND tento la verifica raggiungibilità profilo con senderServiceId valido e CF destinatario: {string}")
    public void invokeProfileVerificationAPIOK(String recipientTaxId) {

        GetProfileRequest getProfileRequest = new GetProfileRequest();
        getProfileRequest.setRecipientTaxId(recipientTaxId);
        getProfileRequest.setSenderServiceId(SENDER_SERVICE_ID);//TODO

        try {
            GetProfileResponse resp = pnIOConnectorClient.getIOProfile(CLIENT_ID,
                    getProfileRequest);
            log.info("profile response: {}", resp);
            getProfileResponse = resp;

            // If no exception is thrown, assume 200 OK (or 2xx)
            actualResponseHttpStatus = HttpStatus.OK;

        } catch (HttpStatusCodeException e) {
            log.info("HttpStatusException: {}", e.getMessage());
            actualResponseHttpStatus = e.getStatusCode();
        }
    }

    @Given("come orchestratore SEND tento la verifica raggiungibilità profilo con senderServiceId: {string} e recipientTaxId: {string}")
    public void invokeProfileVerificationAPI(String senderServiceId, String recipientTaxId) {

        GetProfileRequest getProfileRequest = new GetProfileRequest();
        getProfileRequest.setRecipientTaxId(StringUtils.resolveValue(recipientTaxId));
        getProfileRequest.setSenderServiceId(StringUtils.resolveValue(senderServiceId));

        try {
            GetProfileResponse resp = pnIOConnectorClient.getIOProfile(StringUtils.resolveValue(senderServiceId),
                    getProfileRequest);
            log.info("profile response: {}", resp);
            getProfileResponse = resp;

            // If no exception is thrown, assume 200 OK (or 2xx)
            actualResponseHttpStatus = HttpStatus.OK;

        } catch (HttpStatusCodeException e) {
            log.info("HttpStatusException: {}", e.getMessage());
            actualResponseHttpStatus = e.getStatusCode();
        }
    }

    @Then("verifico che la response contenga l'informazione sulla raggiungibilità del profilo")
    public void verifyProfileStatus() {
        assertThat(getProfileResponse.getStatus())
                .isNotNull();
    }


    //--------SCENARIO 5:

    @Given("come app IO tento il recupero dettagli messaggio con requestID valido e CF destinatario: {string}")
    public void invokeMessageDetialAPI(String recipientTaxId) {

        try {
            GetMessageResponse resp = pnIOConnectorClient.getMessage(REQUEST_ID_FOR_PREEXISTING_MESSAGE, StringUtils.resolveValue(recipientTaxId));
            log.info("message details response: {}", resp);
            getMessageResponse = resp;

            // If no exception is thrown, assume 200 OK (or 2xx)
            actualResponseHttpStatus = HttpStatus.OK;

        } catch (HttpStatusCodeException e) {
            log.info("HttpStatusException: {}", e.getMessage());
            actualResponseHttpStatus = e.getStatusCode();
        }
    }

    @Then("verifico che la lista dettagli allegati sia non vuota")
    public void verifyResponseCodeOKAndAttachmentsDetailPresent() {

        assertThat(getMessageResponse.getAttachments())
                .isNotNull()
                .isNotEmpty();
    }


    @Given("come app IO tento il recupero dettagli messaggio con requestID: {string} e CF destinatario: {string}")
    public void invokeMessageDetialAPI(String requestId, String recipientTaxId) {

        try {
            GetMessageResponse resp = pnIOConnectorClient.getMessage(StringUtils.resolveValue(requestId), StringUtils.resolveValue(recipientTaxId));
            log.info("message details response: {}", resp);
            getMessageResponse = resp;

            // If no exception is thrown, assume 200 OK (or 2xx)
            actualResponseHttpStatus = HttpStatus.OK;

        } catch (HttpStatusCodeException e) {
            log.info("HttpStatusException: {}", e.getMessage());
            actualResponseHttpStatus = e.getStatusCode();
        }
    }

    //--------- COMMON:

    @Then("verifico che si ottenga una response di {string}")
    public void verifyResponseCode(String statusName) {
        HttpStatus expectedStatus = HttpStatus.valueOf(statusName.replace(" ", "_"));
        Assertions.assertEquals(expectedStatus, actualResponseHttpStatus);
    }

    //------- NUOVI STEP AGGIUNTIVI (ERRORI EVENTBRIDGE & MULTI-SERVICE CON ALLEGATI):

    @Given("viene generata una richiesta per la presa in carico con markdown che eccede la lunghezza massima consentita")
    public void createRequestWithMarkdownExceedingMaxLength() {
        createValidRequest();
        messageRequest.setMarkdown("A".repeat(10500));
    }

    @Given("viene generata una richiesta valida con senderServiceId: {string}")
    public void createValidRequestWithSenderServiceId(String senderServiceId) {
        createValidRequest();
        messageRequest.setSenderServiceId(StringUtils.resolveValue(senderServiceId));
    }

    @And("alla richiesta viene associato un allegato PDF valido")
    public void addValidPdfAttachment() {
        if (messageRequest == null) {
            createValidRequest();
        }
        Attachment attachment = new Attachment();
        attachment.setId("att-1");
        attachment.setName("comunicazione.pdf");
        attachment.setFileKey("pn-safestorage/test-comunicazione.pdf");
        messageRequest.setAttachments(List.of(attachment));
    }

    @And("verifico che in tabella pn-IOConnectorRequests esista un record per requestId con il senderServiceId {string}")
    public void verifyRecordExistsWithSenderServiceId(String expectedSenderServiceId) {
        Assertions.assertNotNull(messageRequest, "messageRequest non deve essere null");
        String requestId = messageRequest.getRequestId();
        String resolvedServiceId = StringUtils.resolveValue(expectedSenderServiceId);

        QueryResponse response = dynamoDbService.call(
                DynamoTableName.IO_CONNECTOR_REQUESTS,
                Map.of(":v_requestId", AttributeValue.builder().s(requestId).build())
        );

        assertThat(response).isNotNull();
        assertThat(response.count())
                .as("Nessun record trovato per requestId: %s", requestId)
                .isGreaterThan(0);
        Map<String, AttributeValue> item = response.items().get(0);
        assertThat(item).containsKey("senderServiceId");
        assertThat(item.get("senderServiceId").s()).isEqualTo(resolvedServiceId);
    }

    @And("verifico che su DynamoDB la richiesta evolva nello stato {string}")
    public void verifyRequestEvolvesToStatusInDynamoDB(String expectedStatus) {
        Assertions.assertNotNull(messageRequest, "messageRequest non deve essere null");
        String requestId = messageRequest.getRequestId();
        log.info("Verifica evoluzione stato su DynamoDB per requestId: {} verso {}", requestId, expectedStatus);

        await().atMost(Duration.ofMinutes(2))
                .pollInterval(Duration.ofSeconds(3))
                .ignoreExceptions()
                .untilAsserted(() -> {
                    QueryResponse response = dynamoDbService.call(
                            DynamoTableName.IO_CONNECTOR_REQUESTS,
                            Map.of(":v_requestId", AttributeValue.builder().s(requestId).build())
                    );
                    assertThat(response.count())
                            .as("Nessun record trovato per requestId: %s", requestId)
                            .isGreaterThan(0);
                    Map<String, AttributeValue> item = response.items().get(0);
                    assertThat(item).containsKey("status");
                    assertThat(item.get("status").s()).isEqualTo(expectedStatus);
                });
    }

    @And("verifico la presenza nei log di {string} negli ultimi {int} minuti dell'evento EventBridge con causale {string}")
    public void verifyEventBridgeLog(String microservice, int minutes, String expectedErrorReason) {
        Assertions.assertNotNull(messageRequest, "messageRequest non deve essere null");
        String requestId = messageRequest.getRequestId();
        String searchPattern = String.format("\"%s\" \"%s\"", requestId, expectedErrorReason);

        if (cloudWatchLogsClient != null) {
            await().atMost(Duration.ofMinutes(minutes))
                    .pollInterval(Duration.ofSeconds(5))
                    .ignoreExceptions()
                    .untilAsserted(() -> {
                        FilterLogEventsRequest logRequest = CloudWatchQueryBuilder.search(microservice, searchPattern, minutes);
                        FilterLogEventsResponse logResponse = cloudWatchLogsClient.filterLogEvents(logRequest);
                        assertThat(logResponse.events())
                                .as("Nessun log trovato su %s per requestId %s e causale %s", microservice, requestId, expectedErrorReason)
                                .isNotEmpty();
                    });
        } else {
            log.warn("CloudWatchLogsClient non iniettato, skip verifica log");
        }
    }

    @Given("come app IO tento il recupero dettagli del messaggio inviato con nuovo serviceId e CF destinatario: {string}")
    public void invokeMessageDetailAPINewServiceId(String recipientTaxId) {
        String requestId = (messageRequest != null && messageRequest.getRequestId() != null)
                ? messageRequest.getRequestId()
                : REQUEST_ID_FOR_PREEXISTING_MESSAGE;
        invokeMessageDetialAPI(requestId, recipientTaxId);
    }

    @And("verifico che il link dell'allegato permetta il download del documento PDF")
    public void verifyAttachmentDownload() {
        assertThat(getMessageResponse).isNotNull();
        assertThat(getMessageResponse.getAttachments()).isNotNull().isNotEmpty();

        if (restTemplate != null) {
            for (GetMessageResponseAttachmentsInner attachment : getMessageResponse.getAttachments()) {
                String downloadUrl = attachment.getUrl();
                assertThat(downloadUrl).as("L'URL dell'allegato non deve essere vuoto").isNotBlank();

                if (downloadUrl.startsWith("http://") || downloadUrl.startsWith("https://")) {
                    ResponseEntity<byte[]> response = restTemplate.getForEntity(downloadUrl, byte[].class);
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(response.getHeaders().getContentType()).isNotNull();
                    assertThat(response.getHeaders().getContentType().toString()).contains("application/pdf");
                    assertThat(response.getBody()).isNotNull().isNotEmpty();
                }
            }
        } else {
            log.warn("RestTemplate non iniettato, skip verifica download HTTP allegato");
        }
    }
}
