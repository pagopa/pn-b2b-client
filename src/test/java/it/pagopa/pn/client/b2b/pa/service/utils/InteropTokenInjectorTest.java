package it.pagopa.pn.client.b2b.pa.service.utils;

import it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenBinding.RefreshOutcome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClientException;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class InteropTokenInjectorTest {

    private InteropTokenSingleton singleton;

    @BeforeEach
    void setUp() {
        singleton = mock(InteropTokenSingleton.class);
    }

    @Test
    void applyToAddsBearerHeaderWhenEnabled() {
        when(singleton.getTokenInterop()).thenReturn("token-1");
        HttpHeaders headers = new HttpHeaders();

        new InteropTokenInjector(singleton, "TRUE").applyTo(headers::add);

        assertEquals("Bearer token-1", headers.getFirst("Authorization"));
    }

    @Test
    void applyToDoesNothingWhenDisabled() {
        InteropTokenInjector injector = new InteropTokenInjector(singleton, "false");
        HttpHeaders headers = new HttpHeaders();

        injector.applyTo(headers::add);

        assertTrue(headers.isEmpty());
        assertFalse(injector.isEnabled());
        verifyNoInteractions(singleton);
    }

    @Test
    void disabledBindingNeverReadsTheToken() {
        InteropTokenBinding binding = new InteropTokenInjector(singleton, "false").newBinding("test");
        HttpHeaders headers = new HttpHeaders();
        binding.register(() -> headers::set);

        binding.applyTo(headers::set);

        assertEquals(RefreshOutcome.DISABLED, binding.refreshIfChanged());
        assertTrue(headers.isEmpty());
        verifyNoInteractions(singleton);
    }

    @Test
    void validTokenIsNotReapplied() {
        when(singleton.getTokenInterop()).thenReturn("token-1");
        InteropTokenBinding binding = new InteropTokenInjector(singleton, "true").newBinding("test");
        HttpHeaders headers = new HttpHeaders();
        binding.applyTo(headers::set);
        binding.register(() -> (name, value) -> {
            throw new AssertionError("header must not be rewritten");
        });

        assertEquals(RefreshOutcome.UNCHANGED, binding.refreshIfChanged());
        assertEquals("Bearer token-1", headers.getFirst("Authorization"));
    }

    @Test
    void expiredTokenIsReappliedToAllRegisteredClients() {
        when(singleton.getTokenInterop()).thenReturn("token-1", "token-2");
        InteropTokenBinding binding = new InteropTokenInjector(singleton, "true").newBinding("test");
        HttpHeaders first = new HttpHeaders();
        HttpHeaders second = new HttpHeaders();
        binding.applyTo(first::set);
        binding.applyTo(second::set);
        binding.register(() -> first::set).register(() -> second::set);

        assertEquals(RefreshOutcome.REFRESHED, binding.refreshIfChanged());

        assertEquals("Bearer token-2", first.getFirst("Authorization"));
        assertEquals("Bearer token-2", second.getFirst("Authorization"));
        HttpHeaders rebuilt = new HttpHeaders();
        binding.applyTo(rebuilt::set);
        assertEquals("Bearer token-2", rebuilt.getFirst("Authorization"));
    }

    @Test
    void refreshReachesTheApiClientThatReplacedTheRegisteredOne() {
        when(singleton.getTokenInterop()).thenReturn("token-1", "token-2");
        InteropTokenBinding binding = new InteropTokenInjector(singleton, "true").newBinding("test");
        AtomicReference<HttpHeaders> current = new AtomicReference<>(new HttpHeaders());
        binding.register(() -> current.get()::set);
        HttpHeaders replaced = new HttpHeaders();
        current.set(replaced);

        binding.refreshIfChanged();

        assertEquals("Bearer token-2", replaced.getFirst("Authorization"));
    }

    @Test
    void failedRefreshWithNullTokenKeepsThePreviousHeader() {
        when(singleton.getTokenInterop()).thenReturn("token-1", (String) null);
        InteropTokenBinding binding = new InteropTokenInjector(singleton, "true").newBinding("test");
        HttpHeaders headers = new HttpHeaders();
        binding.applyTo(headers::set);
        binding.register(() -> headers::set);

        assertEquals(RefreshOutcome.FAILED, binding.refreshIfChanged());
        assertEquals("Bearer token-1", headers.getFirst("Authorization"));
    }

    @Test
    void failedRefreshWithExceptionKeepsThePreviousHeader() {
        when(singleton.getTokenInterop()).thenReturn("token-1").thenThrow(new RestClientException("interop down"));
        InteropTokenBinding binding = new InteropTokenInjector(singleton, "true").newBinding("test");
        HttpHeaders headers = new HttpHeaders();
        binding.applyTo(headers::set);
        binding.register(() -> headers::set);

        assertEquals(RefreshOutcome.FAILED, binding.refreshIfChanged());
        assertEquals("Bearer token-1", headers.getFirst("Authorization"));
    }

    @Test
    void bindingStartsWithoutTokenWhenTheFirstOneIsMissing() {
        when(singleton.getTokenInterop()).thenReturn(null, "token-1");
        InteropTokenBinding binding = new InteropTokenInjector(singleton, "true").newBinding("test");
        HttpHeaders headers = new HttpHeaders();
        binding.register(() -> headers::set);

        assertEquals(RefreshOutcome.REFRESHED, binding.refreshIfChanged());
        assertEquals("Bearer token-1", headers.getFirst("Authorization"));
    }
}
