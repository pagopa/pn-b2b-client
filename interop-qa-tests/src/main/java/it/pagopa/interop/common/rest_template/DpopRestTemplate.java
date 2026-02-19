package it.pagopa.interop.common.rest_template;

import it.pagopa.interop.authorization.domain.Auth;
import it.pagopa.interop.authorization.service.DPoPTokenService;
import it.pagopa.interop.authorization.service.utils.DPoPAccessTokenSupplier;
import it.pagopa.interop.common.interceptor.dpop.DPoPInterceptor;
import it.pagopa.interop.common.interceptor.dpop.DPoPTokenInterceptor;
import it.pagopa.interop.common.interceptor.dpop.DigestValidationInterceptor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DpopRestTemplate {

    @Getter
    private final RestTemplate restTemplate;

    private final DPoPInterceptor dpopInterceptor;
    private final DPoPAccessTokenSupplier dpopAccessTokenSupplier;

    public DpopRestTemplate(RestTemplate restTemplate,
                            DPoPTokenService dpopTokenService,
                            DPoPAccessTokenSupplier dpopAccessTokenSupplier,
                            List<ClientHttpRequestInterceptor> baseInterceptors,
                            KeyPair initialKeyPair) {

        this.restTemplate = restTemplate;
        this.dpopAccessTokenSupplier = dpopAccessTokenSupplier;

        this.dpopInterceptor = new DPoPInterceptor(
                dpopTokenService,
                dpopAccessTokenSupplier,
                initialKeyPair
        );

        ClientHttpRequestInterceptor dpopTokenInterceptor =
                new DPoPTokenInterceptor(dpopAccessTokenSupplier);

        ClientHttpRequestInterceptor digestInterceptor =
                new DigestValidationInterceptor(true); // true = fallisce se manca Digest

        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        if (baseInterceptors != null) interceptors.addAll(baseInterceptors);

        // ordine: token -> dpop -> logging
        interceptors.add(0, dpopInterceptor);
        interceptors.add(0, dpopTokenInterceptor);
        interceptors.add(new DigestValidationInterceptor(true));

        restTemplate.setInterceptors(interceptors);
    }

    public void setAuth(Auth auth) {
        dpopInterceptor.setKeyPair(auth.getKeyPair());
        dpopAccessTokenSupplier.setAuth(auth);
        dpopAccessTokenSupplier.prefetch();
        log.info("DpopRestTemplate setAuth: keyPair+auth updated, token prefetched");
    }
}

