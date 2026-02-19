package it.pagopa.interop.common.client;

import it.pagopa.interop.authorization.domain.Auth;
import it.pagopa.interop.common.rest_template.DpopRestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

@Slf4j
public abstract class AbstractDPoPClient extends AbstractClient {

    private final DpopRestTemplate dpop;

    protected AbstractDPoPClient(DpopRestTemplate dpop) {
        this.dpop = dpop;
    }

    protected RestTemplate getRestTemplate() {
        return dpop.getRestTemplate();
    }

    public void setAuth(Auth auth) {
        dpop.setAuth(auth);
    }
}

