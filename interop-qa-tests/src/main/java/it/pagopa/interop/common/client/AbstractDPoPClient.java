package it.pagopa.interop.common.client;

import it.pagopa.interop.authorization.domain.Auth;
import it.pagopa.interop.common.rest_template.DpopRestTemplate;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.ApiClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractDPoPClient extends AbstractClient {

    private final DpopRestTemplate dpop;

    protected AbstractDPoPClient(DpopRestTemplate dpop) {
        this.dpop = dpop;
    }

    protected ApiClient getApiClient() {
        return new NoAuthApiClient(dpop.getRestTemplate());
    }

    public void setAuth(Auth auth) {
        dpop.setAuth(auth);
    }
}

