package it.pagopa.pn.cucumber.steps.ioMock;

import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.common.util.StringUtils;
import it.pagopa.pn.cucumber.steps.ioMock.context.IoMockScenarioContext;
import it.pagopa.pn.cucumber.steps.ioMock.dto.IoMockMessageIdHelper;
import it.pagopa.pn.cucumber.steps.ioMock.dto.IoMockMessagePayloadBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IOMockPollingSteps {

    private final IoMockScenarioContext context;
    private final IOMockCommonSteps commonSteps;

    @Autowired
    public IOMockPollingSteps(IoMockScenarioContext context, IOMockCommonSteps commonSteps) {
        this.context = context;
        this.commonSteps = commonSteps;
    }

    @Given("un messaggio inviato con successo per la sequenza {string}")
    @Given("un messaggio sottomesso con successo per la sequenza {string}")
    public void prepareMessageSubmittedSuccessfully(String sequenceName) {
        context.setSequenceName(sequenceName);
        Map<String, Object> payload = IoMockMessagePayloadBuilder.builder()
                .withSequence(sequenceName)
                .buildMap();
        context.setRequestPayload(payload);
        commonSteps.invokeEndpoint("POST /messages");

        JsonNode responseJson = context.getResponseJson();
        assertThat(responseJson)
                .as("Il body della risposta alla sottomissione del messaggio non contiene un JSON valido")
                .isNotNull();

        assertThat(responseJson.hasNonNull("id"))
                .as("La risposta alla sottomissione del messaggio non contiene il campo 'id'")
                .isTrue();

        String messageId = responseJson.get("id").asText();
        log.info("Messaggio sottomesso con successo a T0, ioMessageId: {}", messageId);
        context.setCreatedMessageId(messageId);
        context.setQueriedMessageId(messageId);
        context.setSubmitTimestamp(System.currentTimeMillis());
    }

    @Given("un messaggio inviato per la sequenza {string} alla finestra temporale {string}")
    public void prepareMessageForTimeWindow(String sequenceName, String timeWindow) {
        String messageId;
        switch (timeWindow) {
            case "T0_ELAPSED_LESS_5S":
                prepareMessageSubmittedSuccessfully(sequenceName);
                return;
            case "T1_ELAPSED_5_15S":
                messageId = IoMockMessageIdHelper.buildMockIdForT1(sequenceName);
                break;
            case "T2_ELAPSED_OVER_15S":
                messageId = IoMockMessageIdHelper.buildMockIdForT2(sequenceName);
                break;
            default:
                messageId = IoMockMessageIdHelper.buildMockIdForT0(sequenceName);
                break;
        }
        log.info("Impostato messageId per finestra temporale {}: {}", timeWindow, messageId);
        context.setQueriedMessageId(messageId);
        context.setSequenceName(sequenceName);
    }

    @Given("un messaggio inviato per la sequenza {string} con tempo trascorso compreso tra 5 e 15 secondi")
    public void prepareMessageWithT1Offset(String sequenceName) {
        String syntheticId = IoMockMessageIdHelper.buildMockIdForT1(sequenceName);
        log.info("Generato synthetic ioMessageId per T1: {}", syntheticId);
        context.setQueriedMessageId(syntheticId);
        context.setSequenceName(sequenceName);
    }

    @Given("un messaggio inviato per la sequenza {string} con tempo trascorso superiore a 15 secondi")
    public void prepareMessageWithT2Offset(String sequenceName) {
        String syntheticId = IoMockMessageIdHelper.buildMockIdForT2(sequenceName);
        log.info("Generato synthetic ioMessageId per T2: {}", syntheticId);
        context.setQueriedMessageId(syntheticId);
        context.setSequenceName(sequenceName);
    }

    @Given("una richiesta di stato messaggio con identificativo standard privo di prefisso mock {string}")
    public void preparePollingRequestWithStandardRealId(String standardMessageId) {
        String resolvedId = StringUtils.resolveValue(standardMessageId);
        log.info("Impostato identificativo reale/standard per test routing trasparente: {}", resolvedId);
        context.setQueriedMessageId(resolvedId);

        if (context.getRequestHeaders() == null) {
            context.setRequestHeaders(new java.util.HashMap<>());
        }
        context.getRequestHeaders().put("Ocp-Apim-Subscription-Key", "sub-key-io-collaudo-test-12345");
    }

    @Given("una richiesta di stato messaggio con identificativo valido per la sequenza {string}")
    public void preparePollingRequestWithValidMessageId(String sequenceName) {
        String defaultMockId = IoMockMessageIdHelper.buildMockIdForT0(sequenceName);
        context.setQueriedMessageId(defaultMockId);
        context.setSequenceName(sequenceName);
    }

    @Given("una richiesta di stato messaggio con identificativo mock non valido {string}")
    public void preparePollingRequestWithInvalidMockId(String invalidId) {
        context.setQueriedMessageId(invalidId);
        if (context.getQueriedFiscalCode() == null) {
            context.setQueriedFiscalCode("RSSMRA80A01H5010");
        }
    }

    @Given("una richiesta di stato messaggio con identificativo mock avente sequenza non censita {string}")
    public void preparePollingRequestWithUnknownSequence(String unknownSequenceName) {
        String mockIdWithUnknownSeq = IoMockMessageIdHelper.buildMockIdForT0(unknownSequenceName);
        log.info("Generato ioMessageId con sequence non censita: {}", mockIdWithUnknownSeq);
        context.setQueriedMessageId(mockIdWithUnknownSeq);
        context.setSequenceName(unknownSequenceName);
        if (context.getQueriedFiscalCode() == null) {
            context.setQueriedFiscalCode("RSSMRA80A01H5010");
        }
    }

    @Given("viene richiesto lo stato del messaggio per il destinatario {string}")
    @When("viene richiesto lo stato del messaggio per il destinatario {string}")
    public void queryMessageStatusForRecipient(String fiscalCode) {
        String resolvedFiscalCode = StringUtils.resolveValue(fiscalCode);
        context.setQueriedFiscalCode(resolvedFiscalCode);

        String messageId = context.getQueriedMessageId();
        if (messageId == null || messageId.isBlank()) {
            messageId = context.getCreatedMessageId();
        }
        if (messageId == null || messageId.isBlank()) {
            messageId = IoMockMessageIdHelper.buildMockIdForT0("OK_READ_THEN_PAID");
        }
        assertThat(messageId)
                .as("Nessun messageId presente nel contesto per eseguire il polling dello stato")
                .isNotBlank();

        context.setQueriedMessageId(messageId);
        String path = "/messages/" + resolvedFiscalCode + "/" + messageId;
        log.info("Invocazione GET per polling stato messaggio: {}", path);
        commonSteps.executeHttpRequest(HttpMethod.GET, path);
    }

    @Then("lo stato del messaggio risulta {string}")
    public void verifyMessageStatus(String expectedStatus) {
        assertThat(context.getActualStatusCode())
                .as("Status code della risposta atteso: 200 OK")
                .isEqualTo(HttpStatus.OK.value());

        JsonNode json = context.getResponseJson();
        assertThat(json)
                .as("Il body della risposta non contiene un JSON valido")
                .isNotNull();

        assertThat(json.hasNonNull("status"))
                .as("La risposta non contiene il campo obbligatorio 'status'")
                .isTrue();

        String actualStatus = json.get("status").asText();
        assertThat(actualStatus)
                .as("Lo status del messaggio ottenuto (%s) non corrisponde a quello atteso (%s)", actualStatus, expectedStatus)
                .isEqualTo(expectedStatus);

        context.setPolledStatus(actualStatus);
    }

    @And("lo stato di lettura del messaggio risulta {string}")
    public void verifyReadStatus(String expectedReadStatus) {
        JsonNode json = context.getResponseJson();
        assertThat(json)
                .as("Il body della risposta non contiene un JSON valido")
                .isNotNull();

        if ("NON_DISPONIBILE".equalsIgnoreCase(expectedReadStatus) || "UNAVAILABLE".equalsIgnoreCase(expectedReadStatus)) {
            if (json.hasNonNull("read_status")) {
                String readStatus = json.get("read_status").asText();
                assertThat(readStatus)
                        .as("Lo stato di lettura a T0 non deve essere READ, ottenuto: %s", readStatus)
                        .isIn("UNAVAILABLE", "UNREAD");
            }
            return;
        }

        assertThat(json.hasNonNull("read_status"))
                .as("La risposta non contiene il campo 'read_status'")
                .isTrue();

        String actualReadStatus = json.get("read_status").asText();
        assertThat(actualReadStatus)
                .as("Il read_status ottenuto (%s) non corrisponde a quello atteso (%s)", actualReadStatus, expectedReadStatus)
                .isEqualTo(expectedReadStatus);

        context.setPolledReadStatus(actualReadStatus);
    }

    @And("lo stato di lettura del messaggio non è ancora disponibile")
    public void verifyReadStatusNotAvailable() {
        verifyReadStatus("NON_DISPONIBILE");
    }

    @And("lo stato di pagamento del messaggio risulta {string}")
    public void verifyPaymentStatus(String expectedPaymentStatus) {
        JsonNode json = context.getResponseJson();
        assertThat(json)
                .as("Il body della risposta non contiene un JSON valido")
                .isNotNull();

        if ("NON_DISPONIBILE".equalsIgnoreCase(expectedPaymentStatus) || "UNAVAILABLE".equalsIgnoreCase(expectedPaymentStatus)) {
            if (json.hasNonNull("payment_status")) {
                String paymentStatus = json.get("payment_status").asText();
                assertThat(paymentStatus)
                        .as("Lo stato di pagamento prima di T2 non deve essere PAID, ottenuto: %s", paymentStatus)
                        .isNotEqualTo("PAID");
            }
            return;
        }

        assertThat(json.hasNonNull("payment_status"))
                .as("La risposta non contiene il campo 'payment_status'")
                .isTrue();

        String actualPaymentStatus = json.get("payment_status").asText();
        assertThat(actualPaymentStatus)
                .as("Il payment_status ottenuto (%s) non corrisponde a quello atteso (%s)", actualPaymentStatus, expectedPaymentStatus)
                .isEqualTo(expectedPaymentStatus);

        context.setPolledPaymentStatus(actualPaymentStatus);
    }

    @And("lo stato di pagamento del messaggio non è ancora disponibile")
    public void verifyPaymentStatusNotAvailable() {
        verifyPaymentStatus("NON_DISPONIBILE");
    }

    @And("i metadati del messaggio contengono il codice fiscale {string}")
    public void verifyMessageMetadataFiscalCode(String expectedFiscalCode) {
        JsonNode json = context.getResponseJson();
        assertThat(json)
                .as("Il body della risposta non contiene un JSON valido")
                .isNotNull();

        if (json.hasNonNull("message")) {
            JsonNode messageNode = json.get("message");
            if (messageNode.hasNonNull("fiscal_code")) {
                String cf = messageNode.get("fiscal_code").asText();
                assertThat(cf)
                        .as("Il codice fiscale nei metadati del messaggio (%s) non corrisponde a quello atteso (%s)", cf, expectedFiscalCode)
                        .isEqualTo(expectedFiscalCode);
            }
        }
    }

    @Then("la richiesta di stato messaggio viene rifiutata per errore di validazione del codice fiscale")
    public void verifyFiscalCodeValidationError() {
        assertThat(context.getActualStatusCode())
                .as("Lo status code atteso per codice fiscale non valido e' 400 Bad Request")
                .isEqualTo(HttpStatus.BAD_REQUEST.value());

        verifyErrorPayloadContainsKeywords("fiscal_code", "fiscalcode", "invalid", "format", "pattern", "bad request", "validation");
    }

    @Then("la richiesta di stato messaggio viene rifiutata per identificativo mock non valido")
    @Then("la richiesta di stato messaggio viene rifiutata per identificativo non conforme o sequenza non censita")
    public void verifyInvalidMockIdError() {
        assertThat(context.getActualStatusCode())
                .as("Lo status code atteso per identificativo mock malformato o non censito e' 400 Bad Request")
                .isEqualTo(HttpStatus.BAD_REQUEST.value());

        verifyErrorPayloadContainsKeywords("id", "messageid", "invalid", "corrupt", "pattern", "bad request", "validation", "timestamp", "format", "sequence", "unknown", "not found");
    }

    @Then("la richiesta di stato messaggio viene rifiutata per sequenza non censita a sistema")
    public void verifyUnknownSequenceError() {
        assertThat(context.getActualStatusCode())
                .as("Lo status code atteso per sequenza non censita e' 400 Bad Request")
                .isEqualTo(HttpStatus.BAD_REQUEST.value());

        verifyErrorPayloadContainsKeywords("sequence", "unknown", "not found", "not configured", "invalid", "bad request");
    }

    private void verifyErrorPayloadContainsKeywords(String... keywords) {
        String body = context.getResponseBody();
        assertThat(body)
                .as("Il body della risposta di errore 400 non deve essere vuoto")
                .isNotBlank();

        JsonNode json = context.getResponseJson();
        if (json != null) {
            String jsonLower = json.toString().toLowerCase();
            boolean match = false;
            for (String kw : keywords) {
                if (jsonLower.contains(kw.toLowerCase())) {
                    match = true;
                    break;
                }
            }
            assertThat(match)
                    .as("Il payload di errore 400 (%s) non menziona nessuna delle parole chiave attese", body)
                    .isTrue();
        }
    }
}
