package it.pagopa.pn.interop.cucumber.steps.tenant;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.generated.openapi.clients.tenant_process.model.MaintenanceTenantUpdatePayload;
import it.pagopa.interop.generated.openapi.clients.tenant_process.model.Tenant;
import it.pagopa.interop.generated.openapi.clients.tenant_process.model.TenantKind;
import it.pagopa.interop.maintenance.TenantMapper;
import it.pagopa.interop.tenant.service.ITenantsApi;
import it.pagopa.interop.tenant.service.ITenantsProcessApi;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static it.pagopa.interop.maintenance.InteropMaintenanceServiceImpl.X_METADATA_VERSION;

@Slf4j
public class TenantWriteSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final ITenantsApi tenantsBffClient;
    private final ITenantsProcessApi tenantsProcessClient;
    private final TenantMapper mapper;

    private MaintenanceTenantUpdatePayload tenantUpdatePayload;

    public TenantWriteSteps(
            ClientTokenConfigurator clientTokenConfigurator,
            SharedStepsContext sharedStepsContext,
            TenantMapper mapper) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();

        this.tenantsBffClient = clientTokenConfigurator.getTenantsApi();
        this.tenantsProcessClient = clientTokenConfigurator.getTenantsProcessApi();
        this.mapper = mapper;
    }

    @Given("l'ente {string} legge la propria attuale configurazione")
    public void getCurrentVersion(String tenantAlias) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantAlias, null));
        UUID organizationId = identityService.getOrganizationId(tenantAlias);

        ResponseEntity<Tenant> processTenant = tenantsProcessClient.getTenant(
                randomUUID(),
                organizationId);
        TenantKind kindIniziale = processTenant.getBody().getKind();
        log.debug("Actual tenant kind of {}: {}",  tenantAlias, kindIniziale);

        String currentVersion = processTenant.getHeaders().get(X_METADATA_VERSION).get(0);
        int specifiedVersion = Integer.parseInt(currentVersion) - 1;
        log.debug("Value of header {} indicating currentVersion: {}", X_METADATA_VERSION, currentVersion);
        log.debug("Specifying version: {}", specifiedVersion);

        /* DEV. NOTE 29/05/2026: necessario recuperare il valore di selfcareInstitutionType dal client BFF perché
         * il client process al momento non espone questa informazione */
        it.pagopa.interop.generated.openapi.clients.bff.model.Tenant tenantFromBff = tenantsBffClient.getTenant(organizationId);
        String selfcareInstitutionType = tenantFromBff.getSelfcareInstitutionType();

        this.tenantUpdatePayload = this.mapper.mapWith(processTenant.getBody(), specifiedVersion, selfcareInstitutionType);
    }

    @When("si tenta di modificare il tenant {string} con una versione antecedente a quella corrente")
    public void editTenant(String tenantAlias) {
        UUID organizationId = identityService.getOrganizationId(tenantAlias);
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> tenantsProcessClient.maintenanceTenantUpdate(randomUUID(), organizationId, this.tenantUpdatePayload));
    }

    private static String randomUUID() {
        return UUID.randomUUID().toString();
    }

}
