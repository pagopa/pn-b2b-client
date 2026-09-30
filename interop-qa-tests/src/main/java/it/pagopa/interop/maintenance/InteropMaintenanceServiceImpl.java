package it.pagopa.interop.maintenance;

import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.ConfigFileReader;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.generated.openapi.clients.tenant_process.model.MaintenanceTenantUpdatePayload;
import it.pagopa.interop.generated.openapi.clients.tenant_process.model.Tenant;
import it.pagopa.interop.generated.openapi.clients.tenant_process.model.TenantKind;
import it.pagopa.interop.tenant.service.ITenantsApi;
import it.pagopa.interop.tenant.service.ITenantsProcessApi;
import it.pagopa.interop.tenant.service.impl.TenantsProcessApiClientImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    public static final String X_METADATA_VERSION = "X-Metadata-Version";

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
        if (!this.isExecutable()) {
            throw new IllegalStateException("Maintenance APIs are not usable in the current environment");
        }

        log.debug("Target tenant kind of {}: {}",  tenantAlias, tenantKind);
        String tokenBff = identityService.getToken(tenantAlias, "admin");
        UUID organizationId = identityService.getOrganizationId(tenantAlias);

        tenantsBffClient.setBearerToken(tokenBff);
        tenantsProcessClient.setBearerToken(tokenBff);
        ResponseEntity<Tenant> processTenant = tenantsProcessClient.getTenant(
                randomUUID(),
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
                () -> tenantsProcessClient.maintenanceTenantUpdate(randomUUID(), organizationId, mapped),
                response -> response.getStatusCode().is2xxSuccessful(),
                "Error during maintenance tenant update"
        );

        // Verifica che il tenant kind sia stato modificato
        Tenant modifiedTenant = pollingService.makePolling(
                () -> tenantsProcessClient.getTenant(randomUUID(), organizationId),
                response -> response.getStatusCode().is2xxSuccessful()
                        && response.getBody().getKind().equals(eTenantKind),
                "Error during maintenance tenant update"
        ).getBody();
        log.debug("Modified tenant kind of {}: {}",  tenantAlias, modifiedTenant.getKind());
    }

    private static String randomUUID() {
        return UUID.randomUUID().toString();
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

    /* Verifica che il tenant kind indicato in piattaforma è coerente con quello passato in input, e in caso contrario
    * lo corregge. */
    @Override
    public void alignTenantKind(it.pagopa.interop.authorization.domain.Tenant tenant) {
        String expectedKind = tenant.getKind();

        UUID organizationId = identityService.getOrganizationId(tenant.getName());

        String token = identityService.getToken(tenant.getName(), "admin");
        tenantsProcessClient.setBearerToken(token);
        String actualKind = tenantsProcessClient.getTenant(randomUUID(), organizationId).getBody().getKind().toString();

        if(!actualKind.equals(expectedKind)) {
            this.changeTenantKind(tenant.getName(), expectedKind);
        }
    }

    /* 29/05/2026 L'uso delle API di maintenance al momento è fattibile solo in ambienti controllati. Attualmente,
    * per esempio, può essere fatto solo attraverso workflow Github. Si astrae in questo metodo la verifica che
    * suddette api siano utilizzabili. */
    @Override
    public boolean isExecutable() {
        /* 29/05/2026 La variabile d'ambiente qui usata è presente solo su workflow Github, ed ha senso solo lì; è
        * condizione necessaria affinché le API interne di Interop siano raggiungibili, e quindi affinché i test
        * di Adeguamento analisi del rischio siano fattibili. Se è assente o vuota - perché l'esecuzione non sta
        * avvenendo su Github o perché l'ambiente Github non è correttamente configurato - allora il servizio
        * non è servibile. */
        return StringUtils.isNotBlank(System.getenv(TenantsProcessApiClientImpl.TENANT_PROCESS_HOST));
    }

    /* DEV. NOTE 29/05/2026: necessario recuperare il valore di selfcareInstitutionType dal client BFF perché
     * il client process al momento non espone questa informazione. Il caching è fattibile perché l'informazione
     * non cambia durante i test. */
    @Nullable
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
