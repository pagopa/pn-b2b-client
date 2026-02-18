package it.pagopa.interop.common.interceptor;

import it.pagopa.interop.authorization.service.DPoPTokenService;
import lombok.Setter;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.security.KeyPair;
import java.util.Objects;

public class DPoPInterceptor implements ClientHttpRequestInterceptor {
    private final DPoPTokenService dpopService;

    @Setter
    private volatile KeyPair keyPair;

    public DPoPInterceptor(DPoPTokenService dpopService, KeyPair keyPair) {
        this.dpopService = dpopService;
        this.keyPair = keyPair;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        KeyPair kp = this.keyPair;
        if (kp == null) {
            throw new IllegalStateException("DPoPInterceptor: keyPair non impostata. Chiama setBearerToken(Auth) prima di usare il client.");
        }

        String dpop = dpopService.buildProofWith(
                kp,
                "dpop+jwt",
                Objects.requireNonNull(request.getMethod()),
                request.getURI().toString()
        );

        request.getHeaders().set("DPoP", dpop);
        return execution.execute(request, body);
    }
}

