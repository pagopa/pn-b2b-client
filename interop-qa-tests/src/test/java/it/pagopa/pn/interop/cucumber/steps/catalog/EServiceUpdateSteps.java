package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.When;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class EServiceUpdateSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;

    public EServiceUpdateSteps(ClientTokenConfigurator clientTokenConfigurator,
                               SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
    }

    @When("l'utente aggiorna quell'e-service")
    public void userUpdateEService() {
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getEServiceClient().updateEServiceById(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        new UpdateEServiceSeed()
                                .name(String.format("e-service - %d", sharedStepsContext.getTestSeed()))
                                .description("Nuova descrizione")
                                .mode(EServiceMode.DELIVER)
                                .technology(EServiceTechnology.SOAP)
                )
        );
    }
}
