package it.pagopa.interop.maintenance;

import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.tenant_process.ApiClient;
import it.pagopa.interop.generated.openapi.clients.tenant_process.api.TenantApi;
import it.pagopa.interop.generated.openapi.clients.tenant_process.model.Tenant;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Component
public class InteropMaintenanceServiceImpl implements InteropMaintenanceService {

    private final TenantApi bffTenantApi;
    private final TenantApi maintenanceTenantApi;
    private final IdentityService identityService;
    private final ApiClient bffApiClient;
    private final ApiClient maintenanceApiClient;

    public InteropMaintenanceServiceImpl(
            @Value("${TENANT_PROCESS_HOST}") String basePath,
            RestTemplate restTemplate,
            @Qualifier("interopIdentityService") IdentityService identityService,
            InteropClientConfigs clientConfigs
    ) {
        bffApiClient = new ApiClient(restTemplate).setBasePath(clientConfigs.getBaseUrl());
        this.bffTenantApi = new TenantApi(bffApiClient);
        maintenanceApiClient = new ApiClient(restTemplate).setBasePath(basePath);
        this.maintenanceTenantApi = new TenantApi(maintenanceApiClient);
        this.identityService = identityService;
    }

    @Override
    public void changeTenantKind(String tenantAlias, String tenantKind) {
        String xCorrelationId = UUID.randomUUID().toString();

        String tokenBff = identityService.getToken(tenantAlias, "admin");
        bffApiClient.setBearerToken(tokenBff);
        ResponseEntity<Tenant> tenant = bffTenantApi.getTenantWithHttpInfo(
                xCorrelationId,
                identityService.getOrganizationId(tenantAlias));

        List<String> metadataVersion = tenant.getHeaders().get("X-Metadata-Version");
        System.out.println("Header metadata version: " + metadataVersion);

        String currentVersion = metadataVersion.get(0);

        // TODO al momento resta scoperto il campo selfcareInstitutionType, che dobbiamo capire da dove ottene, oppure
        //  se non specificarlo affatto. Rif https://pagopaspa.slack.com/archives/C0ADC8WCYCW/p1779797267239509
        // TODO resta scoperto anche currentVersion, l'header che hanno indicato non lo sto ricevendo (ricontrollare)



        // mapping verso dto di creazione, impostando il tenantKind corretto (da parametro, non da output del passo precedente)
        // POST modifica tenant con polling fino a successo
    }



}
