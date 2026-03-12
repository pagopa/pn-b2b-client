package it.pagopa.interop.common.interceptor.dpop;

import it.pagopa.interop.authorization.domain.dpop.DpopHeaderPolicy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.function.Supplier;

public class DPoPTokenInterceptor implements ClientHttpRequestInterceptor {

    private final Supplier<String> tokenSupplier;

    private final ThreadLocal<DpopHeaderPolicy> policy =
            ThreadLocal.withInitial(DpopHeaderPolicy::new);

    public void setPolicy(DpopHeaderPolicy policy) {
        this.policy.set(policy);
    }

    public DPoPTokenInterceptor(Supplier<String> tokenSupplier, DpopHeaderPolicy policy) {
        this.tokenSupplier = tokenSupplier;
        this.policy.set(policy);
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {

        DpopHeaderPolicy currentPolicy = policy.get();
        DpopHeaderPolicy.Mode mode = currentPolicy.getMode();

        if (mode == DpopHeaderPolicy.Mode.MISSING_AUTH) {
            request.getHeaders().remove(HttpHeaders.AUTHORIZATION);
            return execution.execute(request, body);
        }

        if (mode == DpopHeaderPolicy.Mode.INVALID_AUTH) {
            request.getHeaders().set(HttpHeaders.AUTHORIZATION, "DPoP " + currentPolicy.getInvalidAccessToken());
            return execution.execute(request, body);
        }

        String token = tokenSupplier.get();
        request.getHeaders().set(HttpHeaders.AUTHORIZATION, "DPoP " + token);
        return execution.execute(request, body);
    }

    public void clear() {
        policy.remove();
    }
}