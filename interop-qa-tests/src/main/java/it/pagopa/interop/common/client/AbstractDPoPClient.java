package it.pagopa.interop.common.client;

import it.pagopa.interop.authorization.domain.Auth;
import it.pagopa.interop.authorization.service.DPoPTokenService;
import it.pagopa.interop.authorization.service.utils.DPoPAccessTokenSupplier;
import it.pagopa.interop.common.interceptor.DPoPInterceptor;
import it.pagopa.interop.common.interceptor.DPoPTokenInterceptor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

@Getter
@Slf4j
public abstract class AbstractDPoPClient extends AbstractClient {

    protected RestTemplate dpopRestTemplate;
    private final DPoPInterceptor dpopInterceptor;
    private final DPoPAccessTokenSupplier dpopAccessTokenSupplier;

    public AbstractDPoPClient(RestTemplate baseRestTemplate, DPoPTokenService dPoPTokenService) {
        this.dpopAccessTokenSupplier = new DPoPAccessTokenSupplier(dPoPTokenService);

        // Gestione RestTemplate
        RestTemplate restTemplate = new RestTemplate(baseRestTemplate.getRequestFactory());
        restTemplate.setUriTemplateHandler(baseRestTemplate.getUriTemplateHandler());
        restTemplate.setErrorHandler(baseRestTemplate.getErrorHandler());
        restTemplate.setMessageConverters(baseRestTemplate.getMessageConverters());

        dpopInterceptor = new DPoPInterceptor(dPoPTokenService, dpopAccessTokenSupplier, null);
        restTemplate.setInterceptors(new java.util.ArrayList<>(baseRestTemplate.getInterceptors()));
        restTemplate.getInterceptors().add(new DPoPTokenInterceptor(dpopAccessTokenSupplier));
        restTemplate.getInterceptors().add(dpopInterceptor);
        dpopRestTemplate = restTemplate;
    }

    public void setAuth(Auth auth) {
        dpopInterceptor.setKeyPair(auth.getKeyPair());
        dpopAccessTokenSupplier.setAuth(auth);
        dpopAccessTokenSupplier.prefetch();
    }
}