package it.pagopa.interop.maintenance;

public interface InteropMaintenanceService {
    void changeTenantKind(String tenantName, String tenantKind);

    /* Confronta i tenant kind di tutti gli enti presenti in configurazione (PA1, PA2...) e verifica che siano coerenti
    * con quando presente in piattaforma. Se così non è per uno o più di questi (es PA1 risulta GSP quando dovrebbe
    * essere PA) allora corregge. */
    void alignTenantKinds();

    /* Verifica che il tenant kind indicato in piattaforma è coerente con quello passato in input, ed in caso contrario
    * lo corregge. */
    void alignTenantKind(it.pagopa.interop.authorization.domain.Tenant tenant);
}
