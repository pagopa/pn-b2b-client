package it.pagopa.interop.common.interceptor.dpop;

import it.pagopa.interop.authorization.service.DPoPTokenService;
import lombok.Setter;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.security.KeyPair;

public class DPoPAuthInterceptor implements ClientHttpRequestInterceptor {
    private final DPoPTokenService dpopService;
    private final java.util.function.Supplier<String> tokenSupplier;

    @Setter
    private volatile KeyPair keyPair;

    public DPoPAuthInterceptor(DPoPTokenService dpopService,
                               java.util.function.Supplier<String> tokenSupplier,
                               KeyPair keyPair) {
        this.dpopService = dpopService;
        this.tokenSupplier = tokenSupplier;
        this.keyPair = keyPair;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        KeyPair kp = this.keyPair;
        if (kp == null) throw new IllegalStateException("DPoPInterceptor: keyPair non impostata");

        String token = tokenSupplier.get(); // access token in chiaro
        String dpop = dpopService.buildProofWithAth(
                kp,
                "dpop+jwt",
                org.springframework.http.HttpMethod.valueOf(request.getMethod().name()),
                request.getURI().toString(),
                token
        );

        request.getHeaders().set("DPoP", dpop);
        return execution.execute(request, body);
    }
}

