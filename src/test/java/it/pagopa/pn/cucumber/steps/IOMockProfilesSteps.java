package it.pagopa.pn.cucumber.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IOMockProfilesSteps {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${pn.io-mock.base-url:${pn.delivery.base-url:http://localhost:8080}}")
    private String ioMockBaseUrl;

    private Map<String, Object> requestPayload;
    private ResponseEntity<String> responseEntity;
    private int actualStatusCode;
    private String responseBody;
    private JsonNode responseJson;
    private boolean isTransparentRouting;

    @Autowired
    public IOMockProfilesSteps(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.requestPayload = new HashMap<>();
    }

    //-----------------------------------------------------------------------------------------
    // GIVEN STEPS
    //-----------------------------------------------------------------------------------------

    @Given("un payload di verifica profilo con codice fiscale in deny-list {string}")
    public void preparePayloadWithDenyListFiscalCode(String fiscalCode) {
        requestPayload = new HashMap<>();
        requestPayload.put("fiscal_code", StringUtils.resolveValue(fiscalCode));
    }

    @Given("un payload di verifica profilo con codice fiscale in whitelist {string}")
    public void preparePayloadWithWhitelistFiscalCode(String fiscalCode) {
        requestPayload = new HashMap<>();
        requestPayload.put("fiscal_code", StringUtils.resolveValue(fiscalCode));
    }

    @Given("un payload di verifica profilo con codice fiscale standard {string}")
    public void preparePayloadWithStandardFiscalCode(String fiscalCode) {
        requestPayload = new HashMap<>();
        requestPayload.put("fiscal_code", StringUtils.resolveValue(fiscalCode));
    }

    @Given("un payload di verifica profilo privo del campo obbligatorio {string}")
    public void preparePayloadMissingFiscalCode(String fieldName) {
        requestPayload = new HashMap<>();
    }

    @Given("un payload di verifica profilo contenente campi non definiti nelle specifiche OpenAPI")
    public void preparePayloadWithExtraFields() {
        requestPayload = new HashMap<>();
        requestPayload.put("fiscal_code", "STANDAR_CF_00001");
        requestPayload.put("unknown_extra_field", "unexpected_value");
        requestPayload.put("invalid_parameter", 12345);
    }

    @Given("un payload di verifica profilo con codice fiscale malformato {string}")
    public void preparePayloadWithMalformedFiscalCode(String invalidFiscalCode) {
        requestPayload = new HashMap<>();
        requestPayload.put("fiscal_code", StringUtils.resolveValue(invalidFiscalCode));
    }

    @Given("un payload di verifica profilo con codice fiscale di utente non registrato {string}")
    public void preparePayloadWithNotRegisteredFiscalCode(String notRegisteredFiscalCode) {
        requestPayload = new HashMap<>();
        requestPayload.put("fiscal_code", StringUtils.resolveValue(notRegisteredFiscalCode));
    }

    //-----------------------------------------------------------------------------------------
    // WHEN STEPS
    //-----------------------------------------------------------------------------------------

    @When("invoco endpoint {string}")
    public void invokeEndpoint(String endpointMethodAndPath) {
        String[] parts = endpointMethodAndPath.split(" ");
        HttpMethod method = HttpMethod.valueOf(parts[0].trim());
        String path = parts[1].trim();

        String url = ioMockBaseUrl + (path.startsWith("/") ? path : "/" + path);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestPayload, headers);

        try {
            log.info("Sending {} request to {} with body: {}", method, url, requestPayload);
            responseEntity = restTemplate.exchange(url, method, entity, String.class);
            actualStatusCode = responseEntity.getStatusCodeValue();
            responseBody = responseEntity.getBody();

            // Verifica se è avvenuto un routing trasparente verso IO reale
            isTransparentRouting = responseEntity.getHeaders().containsKey("x-routed-to-real-io")
                    || (responseBody != null && responseBody.contains("real_io"));

            if (responseBody != null && !responseBody.isBlank()) {
                try {
                    responseJson = objectMapper.readTree(responseBody);
                } catch (Exception e) {
                    log.warn("Response body is not JSON: {}", responseBody);
                }
            }
        } catch (HttpStatusCodeException e) {
            log.info("HTTP exception: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            actualStatusCode = e.getRawStatusCode();
            responseBody = e.getResponseBodyAsString();
            if (responseBody != null && !responseBody.isBlank()) {
                try {
                    responseJson = objectMapper.readTree(responseBody);
                } catch (Exception ex) {
                    log.warn("Error parsing error response JSON: {}", responseBody);
                }
            }
        } catch (Exception e) {
            log.error("Unexpected error invoking endpoint: {}", e.getMessage(), e);
            Assertions.fail("Invocazione endpoint fallita con eccezione inattesa: " + e.getMessage());
        }
    }

    //-----------------------------------------------------------------------------------------
    // THEN STEPS
    //-----------------------------------------------------------------------------------------

    @Then("verifico che lo status code della risposta sia {int}")
    public void verifyStatusCode(int expectedStatusCode) {
        assertThat(actualStatusCode)
                .as("Status code della risposta non corrispondente")
                .isEqualTo(expectedStatusCode);
    }

    @Then("verifico che lo status code sia {string}")
    public void verifyStatusCodeString(String expectedStatusCode) {
        try {
            int code = Integer.parseInt(expectedStatusCode);
            assertThat(actualStatusCode).isEqualTo(code);
        } catch (NumberFormatException e) {
            HttpStatus expectedStatus = HttpStatus.valueOf(expectedStatusCode.replace(" ", "_"));
            assertThat(actualStatusCode).isEqualTo(expectedStatus.value());
        }
    }

    @And("^verifico che il body della risposta contenga \"([^\"]*)\" impostato a (true|false)$")
    public void verifySenderAllowedFieldRegex(String fieldName, String boolVal) {
        boolean expectedValue = Boolean.parseBoolean(boolVal);
        checkSenderAllowedField(fieldName, expectedValue);
    }

    @And("verifico che il body della risposta contenga {string} impostato a {string}")
    public void verifySenderAllowedFieldString(String fieldName, String boolVal) {
        boolean expectedValue = Boolean.parseBoolean(boolVal);
        checkSenderAllowedField(fieldName, expectedValue);
    }

    private void checkSenderAllowedField(String fieldName, boolean expectedValue) {
        assertThat(responseJson)
                .as("Il body della risposta non è presente o non è un JSON valido")
                .isNotNull();

        assertThat(responseJson.has(fieldName))
                .as("Il body della risposta non contiene il campo atteso: %s", fieldName)
                .isTrue();

        assertThat(responseJson.get(fieldName).asBoolean())
                .as("Il valore del campo %s non corrisponde a quello atteso (%s)", fieldName, expectedValue)
                .isEqualTo(expectedValue);
    }

    @And("verifico che la richiesta sia stata inoltrata in modo trasparente a IO reale")
    public void verifyTransparentRoutingToRealIO() {
        assertThat(actualStatusCode).as("Lo status code deve essere 200 OK").isEqualTo(HttpStatus.OK.value());
        assertThat(isTransparentRouting).as("La richiesta non è stata instradata al backend IO reale").isTrue();
    }
}
