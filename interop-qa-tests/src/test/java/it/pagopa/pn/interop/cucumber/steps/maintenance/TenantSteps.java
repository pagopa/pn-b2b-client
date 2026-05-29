package it.pagopa.pn.interop.cucumber.steps.maintenance;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import it.pagopa.interop.maintenance.InteropMaintenanceService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class TenantSteps {
    private final InteropMaintenanceService maintenanceService;

    private final SharedStepsContext sharedStepsContext;

    private final ReentrantLock lock = new ReentrantLock();
    private volatile boolean setupPerformed = false;

    public TenantSteps(InteropMaintenanceService maintenanceService, SharedStepsContext sharedStepsContext) {
        this.maintenanceService = maintenanceService;
        this.sharedStepsContext = sharedStepsContext;
    }


    /* Resetta i tenant kind al loro valore "naturale", qualora in un'esecuzione precedente siano stati
     * eseguiti test che ne hanno modificato il valore.
     * DEV NOTE 29/05/2026: si prevede di eseguire i test della feature "adeguamento analisi del rischio" in isolamentp,
     * cioè nella stessa run con gli altri test, per evitare che il cambio di tenant kind possa interferire */
    @Before
    public void resetTenantKind(Scenario scenario) {
        // Double-Checked Locking
        if (!setupPerformed) {
            lock.lock();
            try {
                if (!setupPerformed) {
                    log.info("Aligning tenant kinds");
                    maintenanceService.alignTenantKinds();
                    log.info("Tenant kinds aligned");
                    setupPerformed = true;
                }
            } catch (Exception e) {
                setupPerformed = false;
                throw e;
            } finally {
                lock.unlock();
            }
        }
    }

    @Given("il tenant kind dell'ente {string} viene impostato a {string}")
    public void setTenantKind(String tenant, String kind) {
        this.maintenanceService.changeTenantKind(tenant, kind);
        sharedStepsContext.setTenantType(kind);
    }
}
