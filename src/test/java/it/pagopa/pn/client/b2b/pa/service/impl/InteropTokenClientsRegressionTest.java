package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenInjector;
import it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.headerDoesNotExist;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Regression on the 4 clients that use the Interop token: checks the headers really sent on the wire.
 */
class InteropTokenClientsRegressionTest {

    private static final String BASE_URL = "http://localhost";
    private static final String AUTHORIZATION = "Authorization";

    private InteropTokenSingleton singleton;
    private RestTemplate restTemplate;
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        singleton = mock(InteropTokenSingleton.class);
        restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();
    }

    private InteropTokenInjector injector(String enabled) {
        return new InteropTokenInjector(singleton, enabled);
    }

    private PnPaB2bExternalClientImpl b2bClient(String enabled) {
        return new PnPaB2bExternalClientImpl(restTemplate, injector(enabled), BASE_URL, BASE_URL,
                "key-1", "key-2", "key-ga", "key-son", "key-root");
    }

    private PnWebhookB2bExternalClientImpl webhookClient(String enabled) {
        return new PnWebhookB2bExternalClientImpl(restTemplate, injector(enabled), BASE_URL,
                "key-1", "key-2", "key-ga", "key-son");
    }

    private void expectGet(String authorization, String body) {
        server.expect(requestTo(startsWith(BASE_URL)))
                .andExpect(header(AUTHORIZATION, authorization))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));
    }

    @Test
    void b2bClientSendsTheRefreshedTokenAlsoAfterSetApiKey() {
        when(singleton.getTokenInterop()).thenReturn("token-1", "token-1", "token-2");
        PnPaB2bExternalClientImpl client = b2bClient("true");

        // MockRestServiceServer requires all the expectations before the first request
        expectGet("Bearer token-1", "{}");
        expectGet("Bearer token-2", "{}");
        server.expect(requestTo(startsWith(BASE_URL)))
                .andExpect(header(AUTHORIZATION, "Bearer token-2"))
                .andExpect(header("x-api-key", "key-2"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        client.getSentNotificationDocument("IUN", 0);
        client.getSentNotificationDocument("IUN", 0);
        client.setApiKey("key-2");
        client.getSentNotificationDocument("IUN", 0);

        server.verify();
    }

    @Test
    void b2bClientWithoutInteropSendsNoAuthorization() {
        PnPaB2bExternalClientImpl client = b2bClient("false");
        server.expect(requestTo(startsWith(BASE_URL)))
                .andExpect(headerDoesNotExist(AUTHORIZATION))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        client.getSentNotificationDocument("IUN", 0);

        server.verify();
    }

    @Test
    void webhookClientSendsTheRefreshedTokenAlsoAfterSetApiKey() {
        when(singleton.getTokenInterop()).thenReturn("token-1", "token-1", "token-2");
        PnWebhookB2bExternalClientImpl client = webhookClient("true");

        server.expect(requestTo(startsWith(BASE_URL)))
                .andExpect(header(AUTHORIZATION, "Bearer token-1"))
                .andRespond(withNoContent());
        server.expect(requestTo(startsWith(BASE_URL)))
                .andExpect(header(AUTHORIZATION, "Bearer token-2"))
                .andRespond(withNoContent());
        server.expect(requestTo(startsWith(BASE_URL)))
                .andExpect(header(AUTHORIZATION, "Bearer token-2"))
                .andExpect(header("x-api-key", "key-2"))
                .andRespond(withNoContent());

        client.deleteEventStream(UUID.randomUUID());
        client.deleteEventStream(UUID.randomUUID());
        client.setApiKey("key-2");
        client.deleteEventStream(UUID.randomUUID());

        server.verify();
    }

    @Test
    void webhookClientWithoutInteropSendsNoAuthorization() {
        PnWebhookB2bExternalClientImpl client = webhookClient("false");
        server.expect(requestTo(startsWith(BASE_URL)))
                .andExpect(headerDoesNotExist(AUTHORIZATION))
                .andRespond(withNoContent());

        client.deleteEventStream(UUID.randomUUID());

        server.verify();
    }

    @Test
    void informalClientSendsTheCurrentTokenAtEveryCall() {
        when(singleton.getTokenInterop()).thenReturn("token-1", "token-2");
        PnPaB2bExternalInformalClientImpl client = new PnPaB2bExternalInformalClientImpl(restTemplate, BASE_URL,
                injector("true"), "key-1", "key-2", "key-ga", "key-son", "key-root");

        expectGet("Bearer token-1", "{}");
        expectGet("Bearer token-2", "{}");

        client.getMessage(UUID.randomUUID(), SettableApiKey.ApiKeyType.MVP_1);
        client.getMessage(UUID.randomUUID(), SettableApiKey.ApiKeyType.MVP_1);

        server.verify();
    }

    @Test
    void externalServiceClientSendsTheCurrentTokenAtEveryCall() {
        when(singleton.getTokenInterop()).thenReturn("token-1", "token-2");
        PnExternalServiceClientImpl client = new PnExternalServiceClientImpl(restTemplate, injector("true"),
                BASE_URL, BASE_URL, "key-1", "key-2", "key-ga", "key-son", "key-root",
                "pg-1", "pg-2", BASE_URL, BASE_URL, BASE_URL, BASE_URL, BASE_URL, "user", "pwd", "consolidatore");

        expectGet("Bearer token-1", "[]");
        expectGet("Bearer token-2", "[]");

        client.paGroupInfo(SettableApiKey.ApiKeyType.MVP_1);
        client.paGroupInfo(SettableApiKey.ApiKeyType.MVP_1);

        server.verify();
    }

    @Test
    void externalServiceClientWithoutInteropSendsNoAuthorization() {
        PnExternalServiceClientImpl client = new PnExternalServiceClientImpl(restTemplate, injector("false"),
                BASE_URL, BASE_URL, "key-1", "key-2", "key-ga", "key-son", "key-root",
                "pg-1", "pg-2", BASE_URL, BASE_URL, BASE_URL, BASE_URL, BASE_URL, "user", "pwd", "consolidatore");
        server.expect(requestTo(startsWith(BASE_URL)))
                .andExpect(headerDoesNotExist(AUTHORIZATION))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        client.paGroupInfo(SettableApiKey.ApiKeyType.MVP_1);

        server.verify();
    }
}
