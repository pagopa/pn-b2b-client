package it.pagopa.pn.cucumber.steps.ioMock;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.cucumber.steps.ioMock.context.IoMockScenarioContext;
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

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IOMockCommonSteps {

    private final IoMockScenarioContext context;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${pn.io-mock.base-url:${pn.delivery.base-url:http://localhost:8080}}")
    private String ioMockBaseUrl;

    @Autowired
    public IOMockCommonSteps(IoMockScenarioContext context, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.context = context;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    //-----------------------------------------------------------------------------------------
    // COMMON WHEN STEPS (HTTP Execution)
    //-----------------------------------------------------------------------------------------

    @When("invoco endpoint {string}")
    public void invokeEndpoint(String endpointMethodAndPath) {
        String[] parts = endpointMethodAndPath.split(" ");
        HttpMethod method = HttpMethod.valueOf(parts[0].trim());
        String path = parts[1].trim();

        String url = ioMockBaseUrl + (path.startsWith("/") ? path : "/" + path);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(context.getRequestPayload(), headers);

        try {
            log.info("Sending {} request to {} with body: {}", method, url, context.getRequestPayload());
            ResponseEntity<String> response = restTemplate.exchange(url, method, entity, String.class);
            context.setResponseEntity(response);
            context.setActualStatusCode(response.getStatusCodeValue());
            context.setResponseBody(response.getBody());

            boolean isTransparent = response.getHeaders().containsKey("x-routed-to-real-io")
                    || (response.getBody() != null && response.getBody().contains("real_io"));
            context.setTransparentRouting(isTransparent);

            if (response.getBody() != null && !response.getBody().isBlank()) {
                try {
                    context.setResponseJson(objectMapper.readTree(response.getBody()));
                } catch (Exception e) {
                    log.warn("Response body is not JSON: {}", response.getBody());
                }
            }
        } catch (HttpStatusCodeException e) {
            log.info("HTTP exception: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            context.setActualStatusCode(e.getRawStatusCode());
            context.setResponseBody(e.getResponseBodyAsString());
            if (e.getResponseBodyAsString() != null && !e.getResponseBodyAsString().isBlank()) {
                try {
                    context.setResponseJson(objectMapper.readTree(e.getResponseBodyAsString()));
                } catch (Exception ex) {
                    log.warn("Error parsing error response JSON: {}", e.getResponseBodyAsString());
                }
            }
        } catch (Exception e) {
            log.error("Unexpected error invoking endpoint: {}", e.getMessage(), e);
            Assertions.fail("Invocazione endpoint fallita con eccezione inattesa: " + e.getMessage());
        }
    }

    //-----------------------------------------------------------------------------------------
    // COMMON THEN STEPS (Status Code and Common Asserts)
    //-----------------------------------------------------------------------------------------

    @Then("verifico che lo status code della risposta sia {int}")
    public void verifyStatusCode(int expectedStatusCode) {
        assertThat(context.getActualStatusCode())
                .as("Status code della risposta non corrispondente")
                .isEqualTo(expectedStatusCode);
    }

    @Then("verifico che lo status code sia {string}")
    public void verifyStatusCodeString(String expectedStatusCode) {
        try {
            int code = Integer.parseInt(expectedStatusCode);
            assertThat(context.getActualStatusCode()).isEqualTo(code);
        } catch (NumberFormatException e) {
            HttpStatus expectedStatus = HttpStatus.valueOf(expectedStatusCode.replace(" ", "_"));
            assertThat(context.getActualStatusCode()).isEqualTo(expectedStatus.value());
        }
    }

    @And("verifico che la richiesta sia stata inoltrata in modo trasparente a IO reale")
    public void verifyTransparentRoutingToRealIO() {
        assertThat(context.getActualStatusCode()).as("Lo status code deve essere 200 OK").isEqualTo(HttpStatus.OK.value());
        assertThat(context.isTransparentRouting()).as("La richiesta non è stata instradata al backend IO reale").isTrue();
    }
}
