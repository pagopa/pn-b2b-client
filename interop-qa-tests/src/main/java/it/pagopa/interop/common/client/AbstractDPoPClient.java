package it.pagopa.interop.common.client;

import it.pagopa.interop.authorization.domain.Auth;
import it.pagopa.interop.authorization.service.DPoPTokenService;
import it.pagopa.interop.authorization.service.utils.DPoPAccessTokenSupplier;
import it.pagopa.interop.common.interceptor.DPoPInterceptor;
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
        RestTemplate restTemplate = new RestTemplate(baseRestTemplate.getRequestFactory());
        restTemplate.setUriTemplateHandler(baseRestTemplate.getUriTemplateHandler());
        restTemplate.setErrorHandler(baseRestTemplate.getErrorHandler());
        restTemplate.setMessageConverters(baseRestTemplate.getMessageConverters());

        // copia interceptors esistenti e aggiungi DPoP
        dpopInterceptor = new DPoPInterceptor(dPoPTokenService, null);
        restTemplate.setInterceptors(new java.util.ArrayList<>(baseRestTemplate.getInterceptors()));
        restTemplate.getInterceptors().add(dpopInterceptor);
        dpopRestTemplate = restTemplate;

        this.dpopAccessTokenSupplier = new DPoPAccessTokenSupplier(dPoPTokenService);
    }

    public void setBearerToken(Auth auth) {
        dpopInterceptor.setKeyPair(auth.getKeyPair());
        dpopAccessTokenSupplier.setAuth(auth);
    }
}