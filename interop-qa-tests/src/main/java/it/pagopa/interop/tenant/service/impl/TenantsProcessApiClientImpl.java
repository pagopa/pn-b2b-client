package it.pagopa.interop.tenant.service.impl;

import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.generated.openapi.clients.tenant_process.ApiClient;
import it.pagopa.interop.generated.openapi.clients.tenant_process.api.TenantApi;
import it.pagopa.interop.generated.openapi.clients.tenant_process.model.MaintenanceTenantUpdatePayload;
import it.pagopa.interop.generated.openapi.clients.tenant_process.model.Tenant;
import it.pagopa.interop.tenant.service.ITenantsProcessApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Retryable(
        retryFor = { HttpServerErrorException.class },
        backoff = @Backoff(delay = 2000)
)
@Slf4j
/* DEV. NOTE 28/05/2026: si ricorda che, al momento, l'unico modo di utilizzare le API di maintenance contattando
 * TENANT_PROCESS_HOST è attraverso il workflow Github */
public class TenantsProcessApiClientImpl implements ITenantsProcessApi {
    // Nome della variabile d'ambiente contenente il base path per contattare il tenant process
    public static final String TENANT_PROCESS_HOST = "TENANT_PROCESS_HOST";

    // Client da usare per le apis che richiedono autenticazione uguale a BFF
    private final TenantApi processBffTenantApi;

    // Client da usare per le apis di maintenance, che richiedono uno specifico tipo di token di autenticazione
    private final TenantApi processMaintTenantApi;

    private final String basePath;
    private final RestTemplate restTemplate;

    public TenantsProcessApiClientImpl(
            @Value("${" + TENANT_PROCESS_HOST + "}") String basePath,
            RestTemplate restTemplate,
            IdentityService identityService
    ) {
        log.debug("Value of env. var {}: {}", TENANT_PROCESS_HOST, basePath);
        this.basePath = basePath;
        this.restTemplate = restTemplate;
        this.processBffTenantApi = new TenantApi(createApiClient("dummyToken"));

        // Il token per le apis di maintenance è diverso, e indipendente da ruoli ed enti bff
        this.processMaintTenantApi = new TenantApi(createApiClient(identityService.getMaintenanceToken()));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public ResponseEntity<Tenant> getTenant(String xCorrelationId, UUID organizationId) {
        return this.processBffTenantApi.getTenantWithHttpInfo(xCorrelationId, organizationId);
    }

    @Override
    public ResponseEntity<Void> maintenanceTenantUpdate(String xCorrelationId, UUID organizationId, MaintenanceTenantUpdatePayload mapped) {
        return this.processMaintTenantApi.maintenanceTenantUpdateWithHttpInfo(xCorrelationId, organizationId, mapped);
    }

    // Set del token per le apis che seguono autenticazione BFF (quelle di maintenance non seguono le stesse logiche,
    // il token è fisso, ed è indipendente sia da ente che da ruolo)
    @Override
    public void setBearerToken(String bearerToken) {
        this.processBffTenantApi.setApiClient(createApiClient(bearerToken));
    }
}
