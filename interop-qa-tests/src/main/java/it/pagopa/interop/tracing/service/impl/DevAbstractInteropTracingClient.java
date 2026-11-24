package it.pagopa.interop.tracing.service.impl;

import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.tracing.config.TracingClientConfigs;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Profile("dev")
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DevAbstractInteropTracingClient extends AbstractInteropTracingClient {
    private BearerTokenType bearerTokenSetted;
    private IdentityService identityService;

    public DevAbstractInteropTracingClient(RestTemplate restTemplate, TracingClientConfigs tracingClientConfigs, @Qualifier("tracingIdentityService") IdentityService identityService) {
        super(restTemplate, tracingClientConfigs);
        this.bearerTokenSetted = BearerTokenType.TENANT_1;
        this.identityService = identityService;
    }

    @Override
    public void setBearerToken(String bearerToken) {
        switch (bearerToken) {
            case "TENANT_1" -> {
                this.tracingsApi.setApiClient(createApiClient(getTracingClientConfigs().getBaseUrl(), getTracingClientConfigs().getBearerToken1()));
                this.bearerTokenSetted = BearerTokenType.TENANT_1;
            }
            case "TENANT_2" -> {
                this.tracingsApi.setApiClient(createApiClient(getTracingClientConfigs().getBaseUrl(), getTracingClientConfigs().getBearerToken2()));
                this.bearerTokenSetted = BearerTokenType.TENANT_2;
            }
            default -> throw new IllegalStateException("Unexpected value: " + bearerToken);
        }
    }

    public BearerTokenType getBearerTokenSetted() {
        return this.bearerTokenSetted;
    }

    public IdentityService getIdentityService() { return this.identityService; }
}
