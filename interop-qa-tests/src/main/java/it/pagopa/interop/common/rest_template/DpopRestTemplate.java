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

    private final DpopHeaderPolicy dpopHeaderPolicy;
    private final DPoPAuthInterceptor dpopInterceptor;
    private final DPoPAccessTokenSupplier dpopAccessTokenSupplier;

    public DpopRestTemplate(RestTemplate restTemplate,
                            DPoPTokenService dpopTokenService,
                            DPoPAccessTokenSupplier dpopAccessTokenSupplier,
                            List<ClientHttpRequestInterceptor> baseInterceptors,
                            KeyPair initialKeyPair) {

        this.restTemplate = restTemplate;
        this.dpopAccessTokenSupplier = dpopAccessTokenSupplier;
        this.dpopHeaderPolicy = DpopHeaderPolicy.of(DpopHeaderPolicy.Mode.NORMAL);

        DPoPTokenInterceptor dpopTokenInterceptor = new DPoPTokenInterceptor(dpopAccessTokenSupplier::get, dpopHeaderPolicy);

        this.dpopInterceptor = new DPoPAuthInterceptor(
                dpopTokenService,
                dpopAccessTokenSupplier,
                dpopHeaderPolicy,
                initialKeyPair
        );

        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        if (baseInterceptors != null) interceptors.addAll(baseInterceptors);

        // ordine: token -> dpop -> logging
        interceptors.add(0, dpopInterceptor);
        interceptors.add(0, dpopTokenInterceptor);
        interceptors.add(new IntegrityValidationInterceptor(true, true, true));

        restTemplate.setInterceptors(interceptors);
    }

    public void setAuth(Auth auth) {
        if (auth != null) { // = a null quando il test non viene avviato in modalità M2M v3
            dpopInterceptor.setKeyPair(auth.getKeyPair());
            DpopHeaderPolicy incoming = auth.getDpopHeaderPolicy();

            if (incoming != null) {
                dpopHeaderPolicy.setMode(incoming.getMode());
                dpopHeaderPolicy.setInvalidAccessToken(incoming.getInvalidAccessToken());
                dpopHeaderPolicy.setInvalidDpopProof(incoming.getInvalidDpopProof());
            } else {
                dpopHeaderPolicy.setMode(DpopHeaderPolicy.Mode.NORMAL);
            }

            dpopAccessTokenSupplier.setAuth(auth);
        } else {
            log.warn("Autenticazione M2M v3 non settata.");
        }
    }
}

