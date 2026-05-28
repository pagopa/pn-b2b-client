package it.pagopa.interop.maintenance;

import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.generated.openapi.clients.tenant_process.model.MaintenanceTenantUpdatePayload;
import it.pagopa.interop.generated.openapi.clients.tenant_process.model.Tenant;
import it.pagopa.interop.generated.openapi.clients.tenant_process.model.TenantKind;
import it.pagopa.interop.tenant.service.ITenantsApi;
import it.pagopa.interop.tenant.service.ITenantsProcessApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class InteropMaintenanceServiceImpl implements InteropMaintenanceService {
    private final ITenantsApi tenantsBffClient;
    private final ITenantsProcessApi tenantsProcessClient;
    private final TenantMapper mapper;
    private final IdentityService identityService;

    public InteropMaintenanceServiceImpl(
            @Value("${TENANT_PROCESS_HOST}") String basePath,
            @Qualifier("interopIdentityService") IdentityService identityService,
            TenantMapper mapper,
            ITenantsApi tenantsBffClient,
            ITenantsProcessApi tenantsProcessClient
    ) {
        System.out.println("HOST preso da envar: " + basePath);
        System.out.println("Contenuto della envar TENANT_PROCESS_HOST: " + System.getenv("TENANT_PROCESS_HOST"));
        System.out.println("Contenuto della property TENANT_PROCESS_HOST: " + System.getProperty("TENANT_PROCESS_HOST"));
        System.out.println("Contenuto della envar aCaso: " + System.getenv("aCaso"));
        System.out.println("Contenuto della envar GITHUB_ENV: " + System.getenv("GITHUB_ENV"));
        this.mapper = mapper;
        this.identityService = identityService;
        this.tenantsBffClient = tenantsBffClient;
        this.tenantsProcessClient = tenantsProcessClient;
    }

    @Override
    public void changeTenantKind(String tenantAlias, String tenantKind) {
        String xCorrelationId = UUID.randomUUID().toString();
        String tokenBff = identityService.getToken(tenantAlias, "admin");
        UUID organizationId = identityService.getOrganizationId(tenantAlias);

        tenantsBffClient.setBearerToken(tokenBff);
        tenantsProcessClient.setBearerToken(tokenBff);
        ResponseEntity<Tenant> processTenant = tenantsProcessClient.getTenant(
                xCorrelationId,
                organizationId);
        TenantKind kindIniziale = processTenant.getBody().getKind();
        System.out.println("Prima della modifica, il tenant kind risulta essere: " + kindIniziale);

        List<String> metadataVersion = processTenant.getHeaders().get("X-Metadata-Version");
        System.out.println("Header metadata version: " + metadataVersion);

        String currentVersion = metadataVersion.get(0);
        System.out.println("Header index 0: " + currentVersion);

        tenantsBffClient.setBearerToken(tokenBff);
        it.pagopa.interop.generated.openapi.clients.bff.model.Tenant tenantFromBff = tenantsBffClient.getTenant(organizationId);
        String selfcareInstitutionType = tenantFromBff.getSelfcareInstitutionType();

        MaintenanceTenantUpdatePayload mapped = this.mapper.mapWith(processTenant.getBody(), Integer.parseInt(currentVersion), TenantKind.valueOf(tenantKind), selfcareInstitutionType);
        System.out.println("Tenant finale, prima dell'applicazione del cambio kind: ");
        System.out.println(mapped);

        // POST modifica tenant con polling fino a successo
        // TODO coprire con polling sia questa che le altre chiamate
        tenantsProcessClient.maintenanceTenantUpdate(xCorrelationId, organizationId, mapped);

        // FIXME utile solo a fini di debug, si verifica che il tenant appena modificato differisca dal
        //  precedente solo per il tenant kind e per il campo "updatedAt"
        sleep();
        ResponseEntity<Tenant> processTenantPostKindUpdate = tenantsProcessClient.getTenant(
                xCorrelationId,
                organizationId);
        System.out.println("Dopo la modifica, il tenant kind risulta ora essere: " +  processTenantPostKindUpdate.getBody().getKind());
        processTenantPostKindUpdate.getBody().setUpdatedAt(null);
        processTenantPostKindUpdate.getBody().setKind(null);
        processTenant.getBody().setUpdatedAt(null);
        processTenant.getBody().setKind(null);
        System.out.println("A meno di tenantKind e updatedAt le due versioni del tenant risultano uguali -> " + processTenant.getBody().equals(processTenantPostKindUpdate.getBody()));

        // FIXME utile solo ai fini di debug, il ripristino del corretto tenant kind dovrà essere fatto altrove
        /*mapped.getTenant().setKind(kindIniziale);
        mapped.setCurrentVersion(Integer.parseInt(processTenantPostKindUpdate.getHeaders().get("X-Metadata-Version").get(0)));
        processMaintTenantApi.maintenanceTenantUpdate(xCorrelationId, organizationId, mapped);
        sleep();
        processTenantPostKindUpdate = processBffTenantApi.getTenantWithHttpInfo(
                xCorrelationId,
                organizationId);
        System.out.println("Dopo il ripristino, il tenant kind risulta ora essere: " + processTenantPostKindUpdate.getBody().getKind());*/
    }

    private static void sleep() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
