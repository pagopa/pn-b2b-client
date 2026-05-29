package it.pagopa.interop.maintenance;

import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.ConfigFileReader;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.generated.openapi.clients.tenant_process.model.MaintenanceTenantUpdatePayload;
import it.pagopa.interop.generated.openapi.clients.tenant_process.model.Tenant;
import it.pagopa.interop.generated.openapi.clients.tenant_process.model.TenantKind;
import it.pagopa.interop.tenant.service.ITenantsApi;
import it.pagopa.interop.tenant.service.ITenantsProcessApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class InteropMaintenanceServiceImpl implements InteropMaintenanceService {
    private static final String X_METADATA_VERSION = "X-Metadata-Version";

    private final ITenantsApi tenantsBffClient;
    private final ITenantsProcessApi tenantsProcessClient;
    private final TenantMapper mapper;

    private final IdentityService identityService;
    private final PollingService pollingService;
    private final ConfigFileReader configFileReader;

    private final Map<UUID, String> sitCache = new HashMap<>();

    public InteropMaintenanceServiceImpl(
            @Qualifier("interopIdentityService") IdentityService identityService,
            TenantMapper mapper,
            ITenantsApi tenantsBffClient,
            ITenantsProcessApi tenantsProcessClient,
            PollingService pollingService,
            ConfigFileReader configFileReader
    ) {
        this.mapper = mapper;
        this.identityService = identityService;
        this.tenantsBffClient = tenantsBffClient;
        this.tenantsProcessClient = tenantsProcessClient;
        this.pollingService = pollingService;
        this.configFileReader = configFileReader;
    }

    @Override
    public void changeTenantKind(String tenantAlias, String tenantKind) {
        log.debug("Target tenant kind of {}: {}",  tenantAlias, tenantKind);
        String xCorrelationId = UUID.randomUUID().toString();
        String tokenBff = identityService.getToken(tenantAlias, "admin");
        UUID organizationId = identityService.getOrganizationId(tenantAlias);

        tenantsBffClient.setBearerToken(tokenBff);
        tenantsProcessClient.setBearerToken(tokenBff);
        ResponseEntity<Tenant> processTenant = tenantsProcessClient.getTenant(
                xCorrelationId,
                organizationId);
        TenantKind kindIniziale = processTenant.getBody().getKind();
        log.debug("Actual tenant kind of {}: {}",  tenantAlias, kindIniziale);

        String currentVersion = processTenant.getHeaders().get(X_METADATA_VERSION).get(0);
        log.debug("Value of header {} to use as currentVersion: {}", X_METADATA_VERSION, currentVersion);

        /* DEV. NOTE 29/05/2026: necessario recuperare il valore di selfcareInstitutionType dal client BFF perché
        * il client process al momento non espone questa informazione */
        String selfcareInstitutionType = getSelfcareInstitutionType(tokenBff, organizationId);

        TenantKind eTenantKind = TenantKind.valueOf(tenantKind);
        MaintenanceTenantUpdatePayload mapped = this.mapper.mapWith(processTenant.getBody(), Integer.parseInt(currentVersion), eTenantKind, selfcareInstitutionType);
        log.trace("Using this {} to edit tenant: {}", mapped.getClass().getSimpleName(), mapped);

        // Modifica del tenant
        pollingService.makePolling(
                () -> tenantsProcessClient.maintenanceTenantUpdate(xCorrelationId, organizationId, mapped),
                response -> response.getStatusCode().is2xxSuccessful(),
                "Error during maintenance tenant update"
        );

        // Verifica che il tenant kind sia stato modificato
        Tenant modifiedTenant = pollingService.makePolling(
                () -> tenantsProcessClient.getTenant(xCorrelationId, organizationId),
                response -> response.getStatusCode().is2xxSuccessful()
                        && response.getBody().getKind().equals(eTenantKind),
                "Error during maintenance tenant update"
        ).getBody();
        log.debug("Modified tenant kind of {}: {}",  tenantAlias, modifiedTenant.getKind());

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

    /* Confronta i tenant kind di tutti gli enti presenti in configurazione (PA1, PA2...) e verifica che siano coerenti
    * con quando presente in piattaforma. Se così non è per uno o più di questi (es PA1 risulta GSP quando dovrebbe
    * essere PA) allora corregge. */
    @Override
    public void alignTenantKinds() {
        List<it.pagopa.interop.authorization.domain.Tenant> tenantList = configFileReader.getTenantList();
        for (it.pagopa.interop.authorization.domain.Tenant tenant : tenantList) {
            alignTenantKind(tenant);
        }
    }

    /* Verifica che il tenant kind indicato in piattaforma è coerente con quello passato in input, ed in caso contrario
    * lo corregge. */
    @Override
    public void alignTenantKind(it.pagopa.interop.authorization.domain.Tenant tenant) {
        String expectedKind = tenant.getKind();

        String xCorrelationId = String.valueOf(UUID.randomUUID());
        UUID organizationId = identityService.getOrganizationId(tenant.getName());

        String token = identityService.getToken(tenant.getName(), "admin");
        tenantsProcessClient.setBearerToken(token);
        String actualKind = tenantsProcessClient.getTenant(xCorrelationId, organizationId).getBody().getKind().toString();

        if(!actualKind.equals(expectedKind)) {
            this.changeTenantKind(tenant.getName(), expectedKind);
        }
    }

    @Nullable
    /* DEV. NOTE 29/05/2026: necessario recuperare il valore di selfcareInstitutionType dal client BFF perché
     * il client process al momento non espone questa informazione. Il caching è fattibile perché l'informazione
     * non cambia durante i test. */
    private String getSelfcareInstitutionType(String tokenBff, UUID organizationId) {
        if(sitCache.containsKey(organizationId)) {
            return sitCache.get(organizationId);
        }

        tenantsBffClient.setBearerToken(tokenBff);
        it.pagopa.interop.generated.openapi.clients.bff.model.Tenant tenantFromBff = tenantsBffClient.getTenant(organizationId);
        String selfcareInstitutionType = tenantFromBff.getSelfcareInstitutionType();
        sitCache.put(organizationId, selfcareInstitutionType);
        return selfcareInstitutionType;
    }

}
