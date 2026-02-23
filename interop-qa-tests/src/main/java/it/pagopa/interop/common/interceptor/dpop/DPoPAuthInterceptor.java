package it.pagopa.interop.common.interceptor.dpop;

import it.pagopa.interop.authorization.domain.dpop.DpopHeaderPolicy;
import it.pagopa.interop.authorization.service.DPoPTokenService;
import java.net.URI;
import java.net.URISyntaxException;
import javax.annotation.Nonnull;
import lombok.Setter;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.*;

import java.io.IOException;
import java.security.KeyPair;

public class DPoPAuthInterceptor implements ClientHttpRequestInterceptor {
    private final DPoPTokenService dpopService;
    private final java.util.function.Supplier<String> tokenSupplier;

    @Setter private DpopHeaderPolicy policy;

    @Setter
    private volatile KeyPair keyPair;

    public DPoPAuthInterceptor(DPoPTokenService dpopService,
                               java.util.function.Supplier<String> tokenSupplier,
                               DpopHeaderPolicy policy,
                               KeyPair keyPair) {
        this.dpopService = dpopService;
        this.tokenSupplier = tokenSupplier;
        this.policy = policy;
        this.keyPair = keyPair;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        DpopHeaderPolicy.Mode mode = policy.getMode();

        if (mode == DpopHeaderPolicy.Mode.MISSING_DPOP) {
            request.getHeaders().remove("DPoP");
            return execution.execute(request, body);
        }

        if (mode == DpopHeaderPolicy.Mode.INVALID_DPOP) {
            request.getHeaders().set("DPoP", policy.getInvalidDpopProof());
            return execution.execute(request, body);
        }

        KeyPair kp = this.keyPair;
        if (kp == null) throw new IllegalStateException("DPoPInterceptor: keyPair non impostata");

        String token = tokenSupplier.get(); // access token in chiaro
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
            throw new IllegalArgumentException("Errore nella generazione del claim 'htu': l'URI utilizzato non risulta scomponibile nelle sue parti fondamentali", e);
        }
    }
}