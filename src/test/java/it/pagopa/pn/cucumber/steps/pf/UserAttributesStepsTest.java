package it.pagopa.pn.cucumber.steps.pf;

import it.pagopa.common.http.HttpCallExecutor;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaluserconsents.model.Consent;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaluserconsents.model.ConsentType;
import it.pagopa.pn.client.b2b.pa.service.impl.PnWebUserAttributesInternalClientImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class UserAttributesStepsTest {
    private final PnWebUserAttributesInternalClientImpl client = mock(PnWebUserAttributesInternalClientImpl.class);
    private final HttpCallExecutor executor = new HttpCallExecutor();
    private final UserAttributesSteps steps = new UserAttributesSteps(client, executor);

    @ParameterizedTest
    @ValueSource(strings = {"TOS", "DATAPRIVACY"})
    void exposesTheRealStatusAndAcceptedConsentToFollowingSteps(String type) {
        Consent body = new Consent().accepted(true);
        ConsentType consentType = ConsentType.valueOf(type);
        when(client.getConsentByTypeWithHttpInfo(consentType, null))
                .thenReturn(ResponseEntity.status(201).body(body));

        steps.vieneRichiestoUltimoConsensoTipo(type);

        steps.recuperoDelConsensoNonHaProdottoErrori();
        steps.ilConsensoAccettato();
        assertEquals(201, executor.getStatusCode());
        assertSame(body, executor.getResponse());
        verify(client).getConsentByTypeWithHttpInfo(consentType, null);
        verifyNoMoreInteractions(client);
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 404, 500})
    void laterHttpErrorsCannotReuseAnEarlierAcceptedConsent(int status) {
        String errorBody = "{\"detail\":\"test consent error\"}";
        RestClientResponseException failure = new RestClientResponseException("test HTTP error", status,
                "test error", new HttpHeaders(), errorBody.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        when(client.getConsentByTypeWithHttpInfo(ConsentType.TOS, null))
                .thenReturn(ResponseEntity.ok(new Consent().accepted(true)))
                .thenThrow(failure);
        steps.vieneRichiestoUltimoConsensoTipo("TOS");
        steps.ilConsensoAccettato();

        steps.vieneRichiestoUltimoConsensoTipo("TOS");

        assertEquals(status, executor.getStatusCode());
        assertEquals(errorBody, executor.getErrorBody());
        assertThrows(AssertionError.class, steps::recuperoDelConsensoNonHaProdottoErrori);
        assertThrows(AssertionError.class, steps::ilConsensoAccettato);
    }

    @Test
    void technicalFailureStopsTheProducerAndClearsThePreviousResult() {
        ResourceAccessException failure = new ResourceAccessException("test connection failure");
        when(client.getConsentByTypeWithHttpInfo(ConsentType.TOS, null))
                .thenReturn(ResponseEntity.ok(new Consent().accepted(true)))
                .thenThrow(failure);
        steps.vieneRichiestoUltimoConsensoTipo("TOS");

        assertSame(failure, assertThrows(ResourceAccessException.class,
                () -> steps.vieneRichiestoUltimoConsensoTipo("TOS")));

        assertFalse(executor.hasResult());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "TOS_DEST_B2B", "TOS_SERCQ", "DATAPRIVACY_SERCQ"})
    void unsupportedConsentTypeClearsThePreviousResultWithoutCallingTheClient(String type) {
        executor.callForEntity(() -> ResponseEntity.ok(new Consent().accepted(true)));

        assertThrows(IllegalArgumentException.class,
                () -> steps.vieneRichiestoUltimoConsensoTipo(type));

        assertFalse(executor.hasResult());
        verifyNoMoreInteractions(client);
    }

    @Test
    void aSuccessfulResponseStillRequiresAnAcceptedConsent() {
        when(client.getConsentByTypeWithHttpInfo(ConsentType.TOS, null))
                .thenReturn(ResponseEntity.ok(new Consent().accepted(false)));

        steps.vieneRichiestoUltimoConsensoTipo("TOS");

        steps.recuperoDelConsensoNonHaProdottoErrori();
        assertThrows(AssertionError.class, steps::ilConsensoAccettato);
    }
}
