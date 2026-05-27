package it.pagopa.interop.maintenance;

import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.generated.openapi.clients.tenant_process.ApiClient;
import it.pagopa.interop.generated.openapi.clients.tenant_process.api.TenantApi;
import it.pagopa.interop.generated.openapi.clients.tenant_process.model.MaintenanceTenantUpdatePayload;
import it.pagopa.interop.generated.openapi.clients.tenant_process.model.Tenant;
import it.pagopa.interop.generated.openapi.clients.tenant_process.model.TenantKind;
import it.pagopa.interop.tenant.service.ITenantsApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Component
public class InteropMaintenanceServiceImpl implements InteropMaintenanceService {

    // TODO appare lecita un'astrazione ad hoc per TenantApi di process, cioè un client che gestisca quando usare token
    //  bff e quando usare token maintenance
    private final TenantApi processBffTenantApi;
    private final TenantApi processMaintTenantApi;
    private final ITenantsApi tenantsBffClient;
    private final IdentityService identityService;
    private final ApiClient bffApiClient;
    private final ApiClient maintenanceApiClient;
    private final TenantMapper mapper;

    public InteropMaintenanceServiceImpl(
            @Value("${TENANT_PROCESS_HOST}") String basePath,
            RestTemplate restTemplate,
            @Qualifier("interopIdentityService") IdentityService identityService,
            TenantMapper mapper,
            ITenantsApi tenantsBffClient
    ) {
        System.out.println("HOST preso da envar: " + basePath);
        System.out.println("Contenuto della envar TENANT_PROCESS_HOST: " + System.getenv("TENANT_PROCESS_HOST"));
        System.out.println("Contenuto della property TENANT_PROCESS_HOST: " + System.getProperty("TENANT_PROCESS_HOST"));
        System.out.println("Contenuto della envar aCaso: " + System.getenv("aCaso"));
        System.out.println("Contenuto della envar GITHUB_ENV: " + System.getenv("GITHUB_ENV"));
        bffApiClient = new ApiClient(restTemplate).setBasePath(basePath);
        this.processBffTenantApi = new TenantApi(bffApiClient);
        this.identityService = identityService;
        maintenanceApiClient = new ApiClient(restTemplate).setBasePath(basePath);
        maintenanceApiClient.setBearerToken(this.identityService::getMaintenanceToken);
        this.processMaintTenantApi = new TenantApi(maintenanceApiClient);
        this.mapper = mapper;
        this.tenantsBffClient = tenantsBffClient;
    }

    @Override
    public void changeTenantKind(String tenantAlias, String tenantKind) {
        String xCorrelationId = UUID.randomUUID().toString();
        String tokenBff = identityService.getToken(tenantAlias, "admin");
        UUID organizationId = identityService.getOrganizationId(tenantAlias);

        bffApiClient.setBearerToken(tokenBff);
        System.out.println("Base path usato per get tenant: " + bffApiClient.getBasePath());
        ResponseEntity<Tenant> processTenant = processBffTenantApi.getTenantWithHttpInfo(
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
        processMaintTenantApi.maintenanceTenantUpdate(xCorrelationId, organizationId, mapped);

        // FIXME utile solo a fini di debug, si verifica che il tenant appena modificato differisca dal
        //  precedente solo per il tenant kind e per il campo "updatedAt"
        sleep();
        ResponseEntity<Tenant> processTenantPostKindUpdate = processBffTenantApi.getTenantWithHttpInfo(
                xCorrelationId,
                organizationId);
        System.out.println("Dopo la modifica, il tenant kind risulta ora essere: " +  processTenantPostKindUpdate.getBody().getKind());
        processTenantPostKindUpdate.getBody().setUpdatedAt(null);
        processTenantPostKindUpdate.getBody().setKind(null);
        processTenant.getBody().setUpdatedAt(null);
        processTenant.getBody().setKind(null);
        System.out.println("A meno di tenantKind e updatedAt le due versioni del tenant risultano uguali -> " + processTenant.getBody().equals(processTenantPostKindUpdate.getBody()));

        // FIXME utile solo ai fini di debug, il ripristino del corretto tenant kind dovrà essere fatto altrove
        mapped.getTenant().setKind(kindIniziale);
        processMaintTenantApi.maintenanceTenantUpdate(xCorrelationId, organizationId, mapped);
        sleep();
        processTenantPostKindUpdate = processBffTenantApi.getTenantWithHttpInfo(
                xCorrelationId,
                organizationId);
        System.out.println("Dopo il ripristino, il tenant kind risulta ora essere: " +  processTenantPostKindUpdate.getBody().getKind());
    }

    private static void sleep() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
