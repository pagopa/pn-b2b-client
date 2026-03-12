package it.pagopa.interop.common.interceptor.dpop;

import it.pagopa.interop.authorization.domain.dpop.DpopHeaderPolicy;
import it.pagopa.interop.authorization.service.DPoPTokenService;

import javax.annotation.Nonnull;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyPair;
import java.util.function.Supplier;

public class DPoPAuthInterceptor implements ClientHttpRequestInterceptor {

    private final DPoPTokenService dpopService;
    private final Supplier<String> tokenSupplier;

    private final ThreadLocal<DpopHeaderPolicy> policy =
            ThreadLocal.withInitial(DpopHeaderPolicy::new);

    private final ThreadLocal<KeyPair> keyPair = new ThreadLocal<>();

    public DPoPAuthInterceptor(
            DPoPTokenService dpopService,
            Supplier<String> tokenSupplier,
            DpopHeaderPolicy policy,
            KeyPair keyPair
    ) {
        this.dpopService = dpopService;
        this.tokenSupplier = tokenSupplier;
        this.policy.set(policy);
        this.keyPair.set(keyPair);
    }

    public void setPolicy(DpopHeaderPolicy policy) {
        this.policy.set(policy);
    }

    public void setKeyPair(KeyPair keyPair) {
        this.keyPair.set(keyPair);
    }

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution
    ) throws IOException {

        DpopHeaderPolicy currentPolicy = policy.get();
        DpopHeaderPolicy.Mode mode = currentPolicy.getMode();

        if (mode == DpopHeaderPolicy.Mode.MISSING_DPOP) {
            request.getHeaders().remove("DPoP");
            return execution.execute(request, body);
        }

        if (mode == DpopHeaderPolicy.Mode.INVALID_DPOP) {
            request.getHeaders().set("DPoP", currentPolicy.getInvalidDpopProof());
            return execution.execute(request, body);
        }

        KeyPair kp = keyPair.get();
        if (kp == null) {
            throw new IllegalStateException("DPoPInterceptor: keyPair non impostata");
        }

        String token = tokenSupplier.get();

        URI uri = request.getURI();
        String dpop = dpopService.buildProofWithAth(
                kp,
                "dpop+jwt",
                org.springframework.http.HttpMethod.valueOf(request.getMethod().name()),
                buildHtuUri(uri).toString(),
                token
        );

        request.getHeaders().set("DPoP", dpop);
        return execution.execute(request, body);
    }

    @Nonnull
    private static URI buildHtuUri(URI uri) {
        try {
            return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), null, null);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(
                    "Errore nella generazione del claim 'htu': l'URI utilizzato non risulta scomponibile nelle sue parti fondamentali",
                    e
            );
        }
    }

    public void clear() {
        keyPair.remove();
        policy.remove();
    }
}