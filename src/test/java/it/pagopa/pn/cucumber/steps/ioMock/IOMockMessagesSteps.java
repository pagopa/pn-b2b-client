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
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Component
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
    // GIVEN STEPS (Payload & Request Preparation)
    //-----------------------------------------------------------------------------------------

    @Given("una sequenza valida censita a sistema {string}")
    public void prepareValidRegisteredSequence(String sequenceName) {
        context.setSequenceName(sequenceName);
        Map<String, Object> payload = IoMockMessagePayloadBuilder.builder()
                .withSequence(sequenceName)
                .buildMap();
        context.setRequestPayload(payload);
    }

    @Given("preparo una richiesta di invio messaggio per la sequenza {string}")
    public void prepareMessageRequestForSequence(String sequenceName) {
        context.setSequenceName(sequenceName);
        Map<String, Object> payload = IoMockMessagePayloadBuilder.builder()
                .withSequence(sequenceName)
                .buildMap();
        context.setRequestPayload(payload);
    }

    @Given("preparo una richiesta di invio messaggio con subject {string}")
    public void prepareMessageRequestWithSubject(String subject) {
        Map<String, Object> payload = IoMockMessagePayloadBuilder.builder()
                .withSubject(subject)
                .buildMap();
        context.setRequestPayload(payload);
    }

    @Given("preparo una richiesta di invio messaggio con subject ordinario privo di marker")
    public void prepareMessageRequestWithoutMarker() {
        Map<String, Object> payload = IoMockMessagePayloadBuilder.builder()
                .withSubject("Notifica ordinaria senza marker")
                .buildMap();
        context.setRequestPayload(payload);
        // Header di autenticazione IO reale
        if (context.getRequestHeaders() == null) {
            context.setRequestHeaders(new HashMap<>());
        }
        context.getRequestHeaders().put("Ocp-Apim-Subscription-Key", "sub-key-io-collaudo-test-12345");
    }

    @Given("imposto l'header di richiesta {string} a {string}")
    public void setRequestHeader(String headerName, String headerValue) {
        if (context.getRequestHeaders() == null) {
            context.setRequestHeaders(new HashMap<>());
        }
        context.getRequestHeaders().put(headerName, headerValue);
    }

    @Given("preparo una richiesta di invio messaggio senza il campo {string}")
    public void prepareMessageRequestMissingField(String fieldName) {
        Map<String, Object> payload = IoMockMessagePayloadBuilder.builder()
                .withoutField(fieldName)
                .buildMap();
        context.setRequestPayload(payload);
    }

    @Given("preparo una richiesta di invio messaggio senza il sotto-campo {string} in {string}")
    public void prepareMessageRequestMissingSubField(String subFieldName, String parentField) {
        Map<String, Object> payload = IoMockMessagePayloadBuilder.builder()
                .withoutContentField(subFieldName)
                .buildMap();
        context.setRequestPayload(payload);
    }

    @Given("preparo una richiesta di invio messaggio contenente campi non definiti nelle specifiche OpenAPI")
    public void prepareMessageRequestWithExtraFields() {
        // Invio tramite mappa non tipizzata per bypassare filtri DTO
        Map<String, Object> payload = IoMockMessagePayloadBuilder.builder()
                .withExtraField("unauthorized_custom_property", "unexpected_value_123")
                .withExtraField("extra_nested_object", Map.of("foo", "bar"))
                .withExtraContentField("extra_content_field", "not_allowed")
                .buildMap();
        context.setRequestPayload(payload);
    }

    @Given("preparo una richiesta di invio messaggio con codice fiscale formalmente non valido {string}")
    public void prepareMessageRequestWithInvalidFiscalCode(String invalidFiscalCode) {
        Map<String, Object> payload = IoMockMessagePayloadBuilder.builder()
                .withFiscalCode(invalidFiscalCode)
                .buildMap();
        context.setRequestPayload(payload);
    }

    @Given("preparo una richiesta di invio messaggio con marker di sequenza non censita {string}")
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

    @When("invoco endpoint POST per l'invio del messaggio")
    public void invokeSubmitMessageEndpoint() {
        commonSteps.invokeEndpoint("POST /messages");
    }

    //-----------------------------------------------------------------------------------------
    // THEN & AND STEPS (Assertions)
    //-----------------------------------------------------------------------------------------

    @Then("l'ID restituito deve essere un ioMessageId conforme per la sequenza {string}")
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
        long minAllowed = now - 60_000L; // fino a 60 secondi nel passato
        long maxAllowed = now + 5_000L;  // tolleranza 5 secondi nel futuro per clock drift

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

    @Then("verifico che l'ID restituito non contenga il prefisso {string}")
    public void verifyMessageIdDoesNotContainPrefix(String forbiddenPrefix) {
        JsonNode responseJson = context.getResponseJson();
        assertThat(responseJson)
                .as("Il body della risposta non contiene un JSON valido")
                .isNotNull();

        assertThat(responseJson.hasNonNull("id"))
                .as("La risposta non contiene il campo 'id'")
                .isTrue();

        String messageId = responseJson.get("id").asText();
        assertThat(messageId)
                .as("L'ID restituito (%s) non deve contenere il prefisso del mock (%s)", messageId, forbiddenPrefix)
                .doesNotStartWith(forbiddenPrefix);
    }

    @And("verifico che gli header originali tra cui {string} siano stati preservati")
    public void verifyOriginalHeadersPreserved(String headerName) {
        assertThat(context.getRequestHeaders())
                .as("Gli header di richiesta originali non sono stati configurati")
                .containsKey(headerName);

        String originalHeaderVal = context.getRequestHeaders().get(headerName);
        assertThat(originalHeaderVal)
                .as("Il valore dell'header originale %s non deve essere nullo", headerName)
                .isNotBlank();
    }

    @And("verifico che il body della risposta contenga i dettagli di errore di validazione schema")
    public void verifySchemaValidationError() {
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

    @And("verifico che il body della risposta contenga l'errore di formato del destinatario")
    public void verifyRecipientFormatError() {
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

    @And("verifico che il body della risposta contenga l'errore di sequenza non configurata a sistema")
    public void verifyUnknownSequenceError() {
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
