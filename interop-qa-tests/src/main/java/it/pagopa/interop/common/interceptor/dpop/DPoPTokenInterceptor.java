package it.pagopa.interop.common.interceptor.dpop;

import it.pagopa.interop.authorization.domain.dpop.DpopHeaderPolicy;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.function.Supplier;

@AllArgsConstructor
public class DPoPTokenInterceptor implements ClientHttpRequestInterceptor {

    private final Supplier<String> tokenSupplier;
    @Setter
    private DpopHeaderPolicy policy;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {

        DpopHeaderPolicy.Mode mode = policy.getMode();

        if (mode == DpopHeaderPolicy.Mode.MISSING_AUTH) {
            // niente Authorization
            request.getHeaders().remove(HttpHeaders.AUTHORIZATION);
            return execution.execute(request, body);
        }

        if (mode == DpopHeaderPolicy.Mode.INVALID_AUTH) {
            request.getHeaders().set(HttpHeaders.AUTHORIZATION, "DPoP " + policy.getInvalidAccessToken());
            return execution.execute(request, body);
        }

        // NORMAL / altre modalità: header valido
        String token = tokenSupplier.get();
        request.getHeaders().set(HttpHeaders.AUTHORIZATION, "DPoP " + token);
        return execution.execute(request, body);
    }
}