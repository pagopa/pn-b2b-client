package it.pagopa.pn.client.b2b.pa.service.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenInjector.AUTHORIZATION;
import static it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenInjector.BEARER;

/**
 * Interop token state of one client instance (the clients are prototype beans, so this is not a bean).
 * It remembers the last token put in the headers and implements once the sequence
 * "check token -> refresh -> set header" on all the registered ApiClients.
 * Create it with {@link InteropTokenInjector#newBinding(String)}.
 */
@Slf4j
public final class InteropTokenBinding {

    public enum RefreshOutcome {
        /** Interop is disabled: nothing to do. */
        DISABLED,
        /** The token did not change: headers left as they are. */
        UNCHANGED,
        /** The token changed: header updated on all the registered ApiClients. */
        REFRESHED,
        /** No valid token available: headers keep the previous token. */
        FAILED
    }

    private final String clientName;
    private final InteropTokenSingleton interopTokenSingleton;
    private final boolean enabled;
    private final List<Supplier<BiConsumer<String, String>>> headerSetters = new ArrayList<>();
    private String currentToken;

    InteropTokenBinding(String clientName, InteropTokenSingleton interopTokenSingleton, boolean enabled) {
        this.clientName = clientName;
        this.interopTokenSingleton = interopTokenSingleton;
        this.enabled = enabled;
        if (enabled) {
            this.currentToken = interopTokenSingleton.getTokenInterop();
        }
    }

    /**
     * Registers an ApiClient to keep up to date. The supplier is called at every refresh, so it keeps
     * working when the ApiClient is replaced (for example by setApiKey):
     * {@code binding.register(() -> this.someApi.getApiClient()::addDefaultHeader)}.
     */
    public InteropTokenBinding register(Supplier<BiConsumer<String, String>> headerSetter) {
        headerSetters.add(headerSetter);
        return this;
    }

    /**
     * Adds the Authorization header with the last applied token, only when Interop is enabled.
     * To be used when a new ApiClient is built.
     */
    public void applyTo(BiConsumer<String, String> headerSetter) {
        if (enabled) {
            headerSetter.accept(AUTHORIZATION, BEARER + currentToken);
        }
    }

    /**
     * Reads the token from {@link InteropTokenSingleton} and, if it changed, puts it in the
     * Authorization header of all the registered ApiClients.
     */
    public RefreshOutcome refreshIfChanged() {
        if (!enabled) {
            return RefreshOutcome.DISABLED;
        }
        String token;
        try {
            token = interopTokenSingleton.getTokenInterop();
        } catch (RuntimeException e) {
            log.warn("{}: unable to get the interop token, keeping the previous one", clientName, e);
            return RefreshOutcome.FAILED;
        }
        if (token == null) {
            log.warn("{}: interop token not available, keeping the previous one", clientName);
            return RefreshOutcome.FAILED;
        }
        if (token.equals(currentToken)) {
            return RefreshOutcome.UNCHANGED;
        }
        log.info("{} call interopTokenSingleton", clientName);
        currentToken = token;
        headerSetters.forEach(setter -> setter.get().accept(AUTHORIZATION, BEARER + token));
        return RefreshOutcome.REFRESHED;
    }
}
