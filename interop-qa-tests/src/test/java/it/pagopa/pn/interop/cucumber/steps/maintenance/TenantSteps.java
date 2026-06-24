package it.pagopa.pn.interop.cucumber.steps.maintenance;

import io.cucumber.java.After;
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
     * DEV NOTE 29/05/2026: si prevede di eseguire i test della feature "adeguamento analisi del rischio" in isolamento,
     * cioè nella stessa run con gli altri test, per evitare che il cambio di tenant kind possa interferire */
    /* DEV NOTE 24/06/2026: disabilitato poiché si sta provando a sganciare Adeguamento Analisi del rischio dalla NRT
    * per risolvere problemi di testing riscontrati con la release 2.20 (rif https://pagopaspa.slack.com/archives/C069AP16WG7/p1782292760197389?thread_ts=1782290422.043309&cid=C069AP16WG7 ) */
    //@Before
    public void resetTenantKind() {
        if(!maintenanceService.isExecutable()) {
            log.info("Impossible to use maintenance service in current environment. Skipping.");
            return;
        }

        /* 29/05/2026 si effettua l'allineamento solo la prima volta, per correggere l'eventuale caso sfortuito
         * tale per cui un'esecuzione precedente della suite di "Adeguamento analisi del rischio" abbia
         * lasciato una condizione incoerente nei tenant kind. */
        // Double-Checked Locking
        if (!setupPerformed) {
            lock.lock();
            try {
                if (!setupPerformed) {
                    log.info("Aligning tenant kinds...");
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

    @After
    public void alignTenantKindsAfterRiskAnalysisTest() {
        if(!maintenanceService.isExecutable()) {
            log.info("Impossible to use maintenance service in current environment. Skipping.");
            return;
        }

        /* 29/05/2026 Se è in esecuzione la suite di test di "Adeguamento analisi del rischio" (che si prevede
        * di eseguire in isolamento, non nella stessa esecuzione di altri test) allora l'allineamento deve essere
        * effettuato A OGNI test, perché ogni test cambia lo stato dei tenant kind */
        if("true".equals(System.getProperty("suite.AdeguamentoAnalisiRischioTest"))) {
            log.info("Aligning tenant kinds...");
            maintenanceService.alignTenantKinds();
            log.info("Tenant kinds aligned");
        } else {
            log.info("Not a AdeguamentoAnalisiRischio test. Skipping.");
        }
    }
    /* ********************************************************************************************************/

    @Given("il tenant kind dell'ente {string} viene impostato a {string}")
    public void setTenantKind(String tenant, String kind) {
        this.maintenanceService.changeTenantKind(tenant, kind);
        sharedStepsContext.setTenantType(kind);
    }
}
