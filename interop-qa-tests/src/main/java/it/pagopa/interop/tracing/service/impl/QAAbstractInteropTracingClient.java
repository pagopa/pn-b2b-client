package it.pagopa.interop.tracing.service.impl;

import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.api.HealthApi;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.api.TracingsApi;
import it.pagopa.interop.tracing.config.TracingClientConfigs;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Profile("qa")
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class QAAbstractInteropTracingClient extends AbstractInteropTracingClient {
    private IdentityService identityService;

    public QAAbstractInteropTracingClient(RestTemplate restTemplate, TracingClientConfigs tracingClientConfigs, @Qualifier("tracingIdentityService") IdentityService identityService) {
        super(restTemplate, tracingClientConfigs, identityService.getToken("PA1", null));
        this.bearerTokenSetted = BearerTokenType.TENANT_1;
        this.identityService = identityService;
    }

    @Override
    public void setBearerToken(String bearerToken) {
        switch (bearerToken) {
            case "TENANT_1" -> {
                this.tracingsApi.setApiClient(createApiClient(getTracingClientConfigs().getBaseUrl(), identityService.getToken("PA1", null)));
                this.bearerTokenSetted = BearerTokenType.TENANT_1;
            }
            case "TENANT_2" -> {
                this.tracingsApi.setApiClient(createApiClient(getTracingClientConfigs().getBaseUrl(), identityService.getToken("PA2", null)));
                this.bearerTokenSetted = BearerTokenType.TENANT_2;
            }
            default -> throw new IllegalStateException("Unexpected value: " + bearerToken);
        }
    }

}
