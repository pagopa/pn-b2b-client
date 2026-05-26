package it.pagopa.pn.interop.cucumber.steps.maintenance;

import io.cucumber.java.en.Given;
import it.pagopa.interop.maintenance.InteropMaintenanceService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TenantSteps {
    private final InteropMaintenanceService maintenanceService;

    // FIXME solo per debug, rimuovere
    private final SharedStepsContext sharedStepsContext;

    @Given("il tenant kind dell'ente {string} viene impostato a {string}")
    public void setTenantKind(String tenant, String kind) {
        //String maintenanceToken = sharedStepsContext.getIdentityService().getMaintenanceToken();
        //System.out.println("Maintenance token: " + maintenanceToken);

        //this.maintenanceService.changeTenantKind(tenant, kind);
    }
}
