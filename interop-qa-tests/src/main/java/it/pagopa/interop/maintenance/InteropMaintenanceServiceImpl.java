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

    // TODO appare lecita un'astrazione ad hoc per TenantApi, cioè un client che gestisca quando usare token
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
        bffApiClient = new ApiClient(restTemplate).setBasePath(basePath);
        this.processBffTenantApi = new TenantApi(bffApiClient);
        maintenanceApiClient = new ApiClient(restTemplate).setBasePath(basePath);
        this.processMaintTenantApi = new TenantApi(maintenanceApiClient);
        this.identityService = identityService;
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
        ResponseEntity<Tenant> tenant = processBffTenantApi.getTenantWithHttpInfo(
                xCorrelationId,
                organizationId);

        List<String> metadataVersion = tenant.getHeaders().get("X-Metadata-Version");
        System.out.println("Header metadata version: " + metadataVersion);

        String currentVersion = metadataVersion.get(0);
        System.out.println("Header index 0: " + currentVersion);

        tenantsBffClient.setBearerToken(tokenBff);
        it.pagopa.interop.generated.openapi.clients.bff.model.Tenant tenant1 = tenantsBffClient.getTenant(organizationId);
        String selfcareInstitutionType = tenant1.getSelfcareInstitutionType();

        MaintenanceTenantUpdatePayload mapped = this.mapper.mapWith(tenant.getBody(), Integer.getInteger(currentVersion), TenantKind.valueOf(tenantKind), selfcareInstitutionType);
        System.out.println("Tenant finale, prima dell'applicazione del cambio kind: ");
        System.out.println(mapped);

        // TODO al momento resta scoperto il campo selfcareInstitutionType, che dobbiamo capire da dove ottenerlo, oppure
        //  se non specificarlo affatto. Rif https://pagopaspa.slack.com/archives/C0ADC8WCYCW/p1779797267239509
        // TODO resta scoperto anche currentVersion, l'header che hanno indicato non lo sto ricevendo (ricontrollare)



        // mapping verso dto di creazione, impostando il tenantKind corretto (da parametro, non da output del passo precedente)
        // POST modifica tenant con polling fino a successo
    }



}
