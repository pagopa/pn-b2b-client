package it.pagopa.interop.common.interceptor.dpop;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

@RequiredArgsConstructor
public class DPoPTokenInterceptor implements ClientHttpRequestInterceptor {

    private final java.util.function.Supplier<String> tokenSupplier;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String token = tokenSupplier.get();
        if (token == null || token.isBlank()) {
            throw new IllegalStateException("Token vuoto/null");
        }

        request.getHeaders().set("Authorization", "DPoP " + token);

        return execution.execute(request, body);
    }
}

