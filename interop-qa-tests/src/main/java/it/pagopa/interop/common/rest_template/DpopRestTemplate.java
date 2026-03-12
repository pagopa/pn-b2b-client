package it.pagopa.interop.common.rest_template;

import it.pagopa.interop.authorization.domain.Auth;
import it.pagopa.interop.authorization.domain.dpop.DpopHeaderPolicy;
import it.pagopa.interop.authorization.service.DPoPTokenService;
import it.pagopa.interop.common.interceptor.dpop.utils.DPoPAccessTokenSupplier;
import it.pagopa.interop.common.interceptor.dpop.DPoPAuthInterceptor;
import it.pagopa.interop.common.interceptor.dpop.DPoPTokenInterceptor;
import it.pagopa.interop.common.interceptor.dpop.IntegrityValidationInterceptor;
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

    private final DPoPAuthInterceptor dpopInterceptor;
    private final DPoPTokenInterceptor dpopTokenInterceptor;
    private final DPoPAccessTokenSupplier dpopAccessTokenSupplier;

    public DpopRestTemplate(RestTemplate restTemplate,
                            DPoPTokenService dpopTokenService,
                            DPoPAccessTokenSupplier dpopAccessTokenSupplier,
                            List<ClientHttpRequestInterceptor> baseInterceptors,
                            KeyPair initialKeyPair) {

        this.restTemplate = restTemplate;
        this.dpopAccessTokenSupplier = dpopAccessTokenSupplier;

        DpopHeaderPolicy initialPolicy = DpopHeaderPolicy.of(DpopHeaderPolicy.Mode.NORMAL);

        this.dpopTokenInterceptor = new DPoPTokenInterceptor(
                dpopAccessTokenSupplier::get,
                initialPolicy
        );

        this.dpopInterceptor = new DPoPAuthInterceptor(
                dpopTokenService,
                dpopAccessTokenSupplier,
                initialPolicy,
                initialKeyPair
        );

        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        if (baseInterceptors != null) {
            interceptors.addAll(baseInterceptors);
        }

        interceptors.add(0, dpopInterceptor);
        interceptors.add(0, dpopTokenInterceptor);
        interceptors.add(new IntegrityValidationInterceptor(true, true, true));

        restTemplate.setInterceptors(interceptors);
    }

    public void setAuth(Auth auth) {
        if (auth == null) {
            return;
        }

        DpopHeaderPolicy incoming = auth.getDpopHeaderPolicy();
        DpopHeaderPolicy effectivePolicy = new DpopHeaderPolicy();

        if (incoming != null) {
            effectivePolicy.setMode(incoming.getMode());
            effectivePolicy.setInvalidAccessToken(incoming.getInvalidAccessToken());
            effectivePolicy.setInvalidDpopProof(incoming.getInvalidDpopProof());
        } else {
            effectivePolicy.setMode(DpopHeaderPolicy.Mode.NORMAL);
        }

        dpopInterceptor.setKeyPair(auth.getKeyPair());
        dpopInterceptor.setPolicy(effectivePolicy);
        dpopTokenInterceptor.setPolicy(effectivePolicy);

        dpopAccessTokenSupplier.setAuth(auth);
    }
}