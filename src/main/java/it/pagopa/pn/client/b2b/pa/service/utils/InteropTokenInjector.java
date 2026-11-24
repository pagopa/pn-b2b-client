package it.pagopa.pn.client.b2b.pa.service.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

import static it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton.INTEROP_ENABLED;

/**
 * Single entry point used by the B2B clients to put the Interop token in the
 * {@code Authorization: Bearer <token>} header.
 * <ul>
 *     <li>clients that build a new ApiClient/headers for every call use {@link #applyTo(BiConsumer)};</li>
 *     <li>clients that keep their ApiClients between calls use a {@link InteropTokenBinding}
 *     created with {@link #newBinding(String)}, which re-applies the token only when it changes.</li>
 * </ul>
 */
@Component
public class InteropTokenInjector {
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";

    private final InteropTokenSingleton interopTokenSingleton;
    private final boolean enabled;

    public InteropTokenInjector(InteropTokenSingleton interopTokenSingleton,
                                @Value("${pn.interop.enable}") String enableInterop) {
        this.interopTokenSingleton = interopTokenSingleton;
        this.enabled = INTEROP_ENABLED.equalsIgnoreCase(enableInterop);
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Adds the Authorization header with the current Interop token, only when Interop is enabled.
     *
     * @param headerSetter for example {@code apiClient::addDefaultHeader} or {@code httpHeaders::add}
     */
    public void applyTo(BiConsumer<String, String> headerSetter) {
        if (enabled) {
            headerSetter.accept(AUTHORIZATION, BEARER + interopTokenSingleton.getTokenInterop());
        }
    }

    /**
     * Creates the token state of a client that keeps its ApiClients between calls.
     *
     * @param clientName used in the logs
     */
    public InteropTokenBinding newBinding(String clientName) {
        return new InteropTokenBinding(clientName, interopTokenSingleton, enabled);
    }
}
