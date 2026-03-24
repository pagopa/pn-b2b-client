package it.pagopa.pn.interop.cucumber.steps.eservice;

import io.cucumber.java.en.Given;
import it.pagopa.pn.interop.cucumber.steps.agreement.AgreementCommonSteps;
import it.pagopa.pn.interop.cucumber.steps.catalog.EServiceUpdateSteps;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EServiceContainerSteps {
    private final AgreementCommonSteps agreementCommonSteps;
    private final EServiceUpdateSteps eServiceUpdateSteps;

    // TODO 24/03/2026 forse non utile come inizialmente supposto, rimuovere eventualmente
    @Given("{string} ha già creato e successivamente aggiornato la bozza di un e-service")
    public void tenantHasAlreadyCreatedAndUpdatedEService(String tenantType) {
        agreementCommonSteps.tenantHasAlreadyCreatedEServiceWithStatusAndApproval(tenantType, "DRAFT", "AUTOMATIC");
        eServiceUpdateSteps.userUpdateEService(tenantType);
    }
}
