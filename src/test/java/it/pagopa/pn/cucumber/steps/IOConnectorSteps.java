package it.pagopa.pn.cucumber.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.common.util.StringUtils;
import it.pagopa.pn.client.b2b.generated.openapi.clients.io.connector.model.*;
import it.pagopa.pn.client.b2b.pa.domain.DynamoTableName;
import it.pagopa.pn.client.b2b.pa.service.DynamoDbService;
import it.pagopa.pn.client.b2b.pa.service.IPnIOConnectorClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.lang.reflect.Field;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

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
}
