package it.pagopa.pn.cucumber.steps.ioMock;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.cucumber.steps.ioMock.context.IoMockScenarioContext;
import it.pagopa.pn.cucumber.steps.ioMock.dto.IoMockMessagePayloadBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IOMockMessagesSteps {

    private static final String IO_MESSAGE_ID_REGEX = "^MOCK-([A-Za-z0-9_]+)-(\\d+)-([A-Za-z0-9_]+)$";
    private static final Pattern IO_MESSAGE_ID_PATTERN = Pattern.compile(IO_MESSAGE_ID_REGEX);

    private final IoMockScenarioContext context;
    private final IOMockCommonSteps commonSteps;
    private final ObjectMapper objectMapper;

    @Autowired
    public IOMockMessagesSteps(IoMockScenarioContext context, IOMockCommonSteps commonSteps, ObjectMapper objectMapper) {
        this.context = context;
        this.commonSteps = commonSteps;
        this.objectMapper = objectMapper;
    }

    //-----------------------------------------------------------------------------------------
    // GIVEN STEPS
    //-----------------------------------------------------------------------------------------

    @Given("una sequenza valida censita a sistema {string}")
    public void prepareValidRegisteredSequence(String sequenceName) {
        context.setSequenceName(sequenceName);
        Map<String, Object> payload = IoMockMessagePayloadBuilder.builder()
                .withSequence(sequenceName)
                .buildMap();
        context.setRequestPayload(payload);
    }

    @Given("una richiesta di invio messaggio con subject ordinario privo di marker")
    public void prepareMessageRequestWithoutMarker() {
        Map<String, Object> payload = IoMockMessagePayloadBuilder.builder()
                .withSubject("Notifica ordinaria senza marker")
                .buildMap();
        context.setRequestPayload(payload);
        if (context.getRequestHeaders() == null) {
            context.setRequestHeaders(new HashMap<>());
        }
        context.getRequestHeaders().put("Ocp-Apim-Subscription-Key", "sub-key-io-collaudo-test-12345");
    }

    @Given("una richiesta di invio messaggio priva del campo obbligatorio {string}")
    public void prepareMessageRequestMissingFieldByDotNotation(String fieldName) {
        if (fieldName.contains(".")) {
            String[] parts = fieldName.split("\\.");
            if ("content".equalsIgnoreCase(parts[0])) {
                Map<String, Object> payload = IoMockMessagePayloadBuilder.builder()
                        .withoutContentField(parts[1])
                        .buildMap();
                context.setRequestPayload(payload);
                return;
            }
        }
        Map<String, Object> payload = IoMockMessagePayloadBuilder.builder()
                .withoutField(fieldName)
                .buildMap();
        context.setRequestPayload(payload);
    }

    @Given("una richiesta di invio messaggio contenente campi non definiti nelle specifiche OpenAPI")
    public void prepareMessageRequestWithExtraFields() {
        Map<String, Object> payload = IoMockMessagePayloadBuilder.builder()
                .withExtraField("unauthorized_custom_property", "unexpected_value_123")
                .withExtraField("extra_nested_object", Map.of("foo", "bar"))
                .withExtraContentField("extra_content_field", "not_allowed")
                .buildMap();
        context.setRequestPayload(payload);
    }

    @Given("una richiesta di invio messaggio con codice fiscale formalmente non valido {string}")
    public void prepareMessageRequestWithInvalidFiscalCode(String invalidFiscalCode) {
        Map<String, Object> payload = IoMockMessagePayloadBuilder.builder()
                .withFiscalCode(invalidFiscalCode)
                .buildMap();
        context.setRequestPayload(payload);
    }

    @Given("una richiesta di invio messaggio con marker di sequenza non censita {string}")
    public void prepareMessageRequestWithUnknownSequence(String unknownSequenceName) {
        context.setSequenceName(unknownSequenceName);
        Map<String, Object> payload = IoMockMessagePayloadBuilder.builder()
                .withSequence(unknownSequenceName)
                .buildMap();
        context.setRequestPayload(payload);
    }

    //-----------------------------------------------------------------------------------------
    // WHEN STEPS
    //-----------------------------------------------------------------------------------------

    @Given("viene richiesta la sottomissione del messaggio")
    @When("viene richiesta la sottomissione del messaggio")
    public void invokeSubmitMessageEndpoint() {
        commonSteps.invokeEndpoint("POST /messages");
    }

    //-----------------------------------------------------------------------------------------
    // THEN & AND STEPS
    //-----------------------------------------------------------------------------------------

    @Given("il messaggio viene preso in carico e viene generato un identificativo conforme per la sequenza {string}")
    @Then("il messaggio viene preso in carico e viene generato un identificativo conforme per la sequenza {string}")
    public void verifyIoMessageIdFormat(String expectedSequenceName) {
        JsonNode responseJson = context.getResponseJson();
        assertThat(responseJson)
                .as("Il body della risposta non contiene un JSON valido")
                .isNotNull();

        assertThat(responseJson.hasNonNull("id"))
                .as("La risposta non contiene il campo 'id'")
                .isTrue();

        String messageId = responseJson.get("id").asText();
        log.info("Verifica ioMessageId generato: {}", messageId);

        // 1. Verifica prefisso iniziale
        assertThat(messageId)
                .as("L'ID restituito deve iniziare con 'MOCK-'")
                .startsWith("MOCK-");

        // 2. Verifica Regex complessiva
        Matcher matcher = IO_MESSAGE_ID_PATTERN.matcher(messageId);
        assertThat(matcher.matches())
                .as("L'ID '%s' non corrisponde alla regex attesa '%s'", messageId, IO_MESSAGE_ID_REGEX)
                .isTrue();

        String actualSequence = matcher.group(1);
        String timestampStr = matcher.group(2);
        String randToken = matcher.group(3);

        // 3. Corrispondenza del token sequenza intermedio
        assertThat(actualSequence)
                .as("Il segmento sequenceName nell'ID non corrisponde alla sequenza attesa")
                .isEqualTo(expectedSequenceName);

        // 4. Parsabilità del timestamp submitMillis e coerenza temporale
        long submitMillis = Long.parseLong(timestampStr);
        long now = System.currentTimeMillis();
        long minAllowed = now - 60_000L;
        long maxAllowed = now + 5_000L;

        assertThat(submitMillis)
                .as("Il timestamp submitMillis (%d) non è coerente con il tempo corrente (%d). Range consentito: [%d, %d]",
                        submitMillis, now, minAllowed, maxAllowed)
                .isBetween(minAllowed, maxAllowed);

        // 5. Presenza della stringa randomica finale alfanumerica
        assertThat(randToken)
                .as("Il token random finale non deve essere vuoto")
                .isNotBlank();

        // Salvataggio nel contesto per step successivi
        context.setCreatedMessageId(messageId);
        context.setSequenceName(expectedSequenceName);
        context.setSubmitTimestamp(submitMillis);
    }

    @And("l'identificativo restituito non contiene il prefisso di mock")
    public void verifyMessageIdDoesNotContainMockPrefix() {
        JsonNode responseJson = context.getResponseJson();
        assertThat(responseJson)
                .as("Il body della risposta non contiene un JSON valido")
                .isNotNull();

        assertThat(responseJson.hasNonNull("id"))
                .as("La risposta non contiene il campo 'id'")
                .isTrue();

        String messageId = responseJson.get("id").asText();
        assertThat(messageId)
                .as("L'ID restituito (%s) non deve contenere il prefisso del mock (MOCK-)", messageId)
                .doesNotStartWith("MOCK-");
    }

    @Then("la richiesta viene rifiutata per errore di validazione formale")
    public void verifyRequestValidationFailed() {
        assertThat(context.getActualStatusCode())
                .as("Lo status code atteso è 400 Bad Request")
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
        verifySchemaValidationError();
    }

    @Then("la richiesta viene rifiutata per errore nel formato del destinatario")
    public void verifyRecipientFormatValidationFailed() {
        assertThat(context.getActualStatusCode())
                .as("Lo status code atteso è 400 Bad Request")
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
        verifyRecipientFormatError();
    }

    @Then("la richiesta viene rifiutata per sequenza non censita a sistema")
    public void verifyUnknownSequenceValidationFailed() {
        assertThat(context.getActualStatusCode())
                .as("Lo status code atteso è 400 Bad Request")
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
        verifyUnknownSequenceError();
    }

    private void verifySchemaValidationError() {
        String body = context.getResponseBody();
        assertThat(body)
                .as("Il body della risposta di errore 400 non deve essere vuoto")
                .isNotBlank();

        JsonNode json = context.getResponseJson();
        if (json != null) {
            boolean hasDetailOrTitle = json.has("detail") || json.has("title") || json.has("errors")
                    || json.has("message") || json.has("status") || json.has("invalidParams")
                    || json.has("violations") || json.has("type") || json.has("error");
            assertThat(hasDetailOrTitle)
                    .as("La risposta 400 non contiene campi standard di errore (detail, title, errors, message, status, invalidParams, violations, error)")
                    .isTrue();
        }
    }

    private void verifyRecipientFormatError() {
        String body = context.getResponseBody();
        assertThat(body)
                .as("Il body della risposta di errore 400 non deve essere vuoto")
                .isNotBlank();

        JsonNode json = context.getResponseJson();
        if (json != null) {
            String jsonStr = json.toString().toLowerCase();
            boolean mentionsFiscalCodeOrValidation = jsonStr.contains("fiscal_code") || jsonStr.contains("fiscalcode")
                    || jsonStr.contains("invalid") || jsonStr.contains("format") || jsonStr.contains("pattern")
                    || jsonStr.contains("bad request") || jsonStr.contains("validation");
            assertThat(mentionsFiscalCodeOrValidation)
                    .as("La risposta di errore non menziona l'invalidità del codice fiscale o formato")
                    .isTrue();
        }
    }

    private void verifyUnknownSequenceError() {
        String body = context.getResponseBody();
        assertThat(body)
                .as("Il body della risposta di errore 400 non deve essere vuoto")
                .isNotBlank();

        JsonNode json = context.getResponseJson();
        if (json != null) {
            String jsonStr = json.toString().toLowerCase();
            boolean mentionsSequenceError = jsonStr.contains("sequence") || jsonStr.contains("unknown")
                    || jsonStr.contains("not found") || jsonStr.contains("not configured")
                    || jsonStr.contains("invalid") || jsonStr.contains("bad request");
            assertThat(mentionsSequenceError)
                    .as("La risposta di errore non descrive l'assenza della sequence indicata")
                    .isTrue();
        }
    }
}
