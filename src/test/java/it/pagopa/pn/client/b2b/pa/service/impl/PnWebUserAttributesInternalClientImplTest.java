package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.common.http.HttpCallExecutor;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaluserconsents.model.Consent;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaluserconsents.model.ConsentType;
import it.pagopa.pn.cucumber.steps.pf.UserAttributesSteps;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class PnWebUserAttributesInternalClientImplTest {
    private final RestTemplate restTemplate = new RestTemplate();
    private final MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
    private final PnWebUserAttributesInternalClientImpl client = new PnWebUserAttributesInternalClientImpl(
            restTemplate, "http://localhost", "test-user-1", "test-user-2", "test-user-3", "test-user-4",
            "test-user-5", "test-expired", "test-pg-1", "test-pg-2", "test-pg-3", "test-pg-4", "test-agent");

    @Test
    void consentAdapterPreservesHttpMetadataAndMapsTheExistingBodyType() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("X-Test-Response", "test-value");
        server.expect(requestTo("http://localhost/bff/v2/tos-privacy?type=TOS"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(responseHeaders)
                        .body("[{\"consentType\":\"TOS\",\"accepted\":true}]"));

        ResponseEntity<Consent> response = client.getConsentByTypeWithHttpInfo(ConsentType.TOS, null);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("test-value", response.getHeaders().getFirst("X-Test-Response"));
        assertEquals(Boolean.TRUE, response.getBody().getAccepted());
        server.verify();
    }

    @Test
    void existingBodyOnlyMethodRemainsCompatible() {
        server.expect(requestTo("http://localhost/bff/v2/tos-privacy?type=DATAPRIVACY"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[{\"consentType\":\"DATAPRIVACY\",\"accepted\":false}]",
                        MediaType.APPLICATION_JSON));

        Consent consent = client.getConsentByType(ConsentType.DATAPRIVACY, null);

        assertEquals(Boolean.FALSE, consent.getAccepted());
        server.verify();
    }

    @Test
    void anHttpErrorReachesTheExecutorWithItsOriginalStatusAndBody() {
        String errorBody = "{\"detail\":\"test consent unavailable\"}";
        server.expect(requestTo("http://localhost/bff/v2/tos-privacy?type=TOS"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(errorBody));
        HttpCallExecutor executor = new HttpCallExecutor();
        UserAttributesSteps steps = new UserAttributesSteps(client, executor);

        steps.vieneRichiestoUltimoConsensoTipo("TOS");

        assertEquals(404, executor.getStatusCode());
        assertEquals(errorBody, executor.getErrorBody());
        assertThrows(AssertionError.class, steps::recuperoDelConsensoNonHaProdottoErrori);
        server.verify();
    }
}
