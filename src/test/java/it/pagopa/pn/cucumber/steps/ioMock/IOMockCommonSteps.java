package it.pagopa.pn.cucumber.steps.ioMock;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.wrapper.ApiCallHelper;
import it.pagopa.pn.client.b2b.pa.wrapper.ApiResult;
import it.pagopa.pn.cucumber.steps.ioMock.context.IoMockScenarioContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
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

        executeHttpRequest(method, path);
    }

    public void executeHttpRequest(HttpMethod method, String path) {
        String url = ioMockBaseUrl + (path.startsWith("/") ? path : "/" + path);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Applica header configurati nel context
        if (context.getRequestHeaders() != null) {
            context.getRequestHeaders().forEach(headers::add);
        }

        HttpEntity<?> entity;
        if (method == HttpMethod.GET) {
            entity = new HttpEntity<>(headers);
        } else if (context.getRawPayloadString() != null) {
            entity = new HttpEntity<>(context.getRawPayloadString(), headers);
        } else if (context.getRequestPayload() != null) {
            entity = new HttpEntity<>(context.getRequestPayload(), headers);
        } else {
            entity = new HttpEntity<>(headers);
        }

        try {
            log.info("Sending {} request to IO Mock", method);
            HttpEntity<?> requestEntity = entity;
            ApiResult<ResponseEntity<String>> result = ApiCallHelper.callBody(() -> restTemplate.exchange(url, method, requestEntity, String.class));
            if (result.isError()) {
                handleErrorResponse(result);
                return;
            }
            ResponseEntity<String> response = result.body();
            context.setResponseEntity(response);
            context.setActualStatusCode(response.getStatusCodeValue());
            context.setResponseBody(response.getBody());

            boolean isTransparent = response.getHeaders().containsKey("x-routed-to-real-io")
                    || (response.getHeaders().containsKey("x-routed-to") && "real-io".equalsIgnoreCase(response.getHeaders().getFirst("x-routed-to")))
                    || (response.getHeaders().containsKey("www-authenticate") && response.getHeaders().getFirst("www-authenticate").contains("api.io.pagopa.it"))
                    || (response.getBody() != null && response.getBody().contains("real_io"));
            context.setTransparentRouting(isTransparent);

            if (response.getBody() != null && !response.getBody().isBlank()) {
                try {
                    JsonNode jsonNode = objectMapper.readTree(response.getBody());
                    context.setResponseJson(jsonNode);
                    if (jsonNode.hasNonNull("id")) {
                        context.setCreatedMessageId(jsonNode.get("id").asText());
                    }
                } catch (Exception e) {
                    log.warn("Response body is not JSON");
                }
            }
        } catch (Exception e) {
            log.error("Unexpected error invoking endpoint: {}", e.getMessage(), e);
            Assertions.fail("Invocazione endpoint fallita con eccezione inattesa: " + e.getMessage());
        }
    }

    private void handleErrorResponse(ApiResult<?> result) {
        log.info("HTTP error: {} {}", result.statusCode(), result.problem());
        String errorBody = result.problem() == null ? "" : result.problem();
        HttpHeaders errorHeaders = result.headers();
        context.setActualStatusCode(result.statusCode());
        context.setResponseBody(errorBody);

        String wwwAuthenticate = errorHeaders.getFirst("www-authenticate");
        boolean isTransparent = errorHeaders.containsKey("x-routed-to-real-io")
                || "real-io".equalsIgnoreCase(errorHeaders.getFirst("x-routed-to"))
                || (wwwAuthenticate != null && wwwAuthenticate.contains("io.pagopa.it"))
                || errorBody.contains("real_io") || errorBody.contains("subscription key");
        if (isTransparent) {
            context.setTransparentRouting(true);
        }

        if (!errorBody.isBlank()) {
            try {
                context.setResponseJson(objectMapper.readTree(errorBody));
            } catch (Exception ex) {
                log.warn("Error parsing error response JSON");
            }
        }
    }

    //-----------------------------------------------------------------------------------------
    // COMMON THEN STEPS
    //-----------------------------------------------------------------------------------------


    @Then("la richiesta viene instradata con successo verso l'ambiente reale di IO")
    public void verifyTransparentRoutingToRealIO() {
        // Verifica la trasparenza del routing verso App IO reale (stato 200 OK o esito backend reale IO)
        assertThat(context.getActualStatusCode())
                .as("Lo status code restituito per il routing trasparente non è valido: %d", context.getActualStatusCode())
                .isIn(HttpStatus.OK.value(), HttpStatus.NOT_FOUND.value(), HttpStatus.FORBIDDEN.value(), HttpStatus.UNAUTHORIZED.value(), HttpStatus.BAD_GATEWAY.value());
        assertThat(context.isTransparentRouting())
                .as("La richiesta non è stata instradata al backend IO reale")
                .isTrue();
    }
}
