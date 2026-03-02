package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.When;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptionUpdateSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;

public class EServiceDescriptionUpdateSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final EServicesCommonContext eServicesCommonContext;

    public EServiceDescriptionUpdateSteps(ClientTokenConfigurator clientTokenConfigurator,
                               SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.eServicesCommonContext = sharedStepsContext.getEServicesCommonContext();
    }

    @When("l'utente aggiorna la descrizione di quell'e-service")
    public void userUpdateEServiceDescription() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getEServiceClient().updateEServiceDescription(
                        eServicesCommonContext.getEserviceId(),
                        new EServiceDescriptionUpdateSeed().description(String.format("Nuova descrizione - %d", sharedStepsContext.getTestSeed()))
                )
        );
    }

    @When("l'utente {string} di {string} aggiorna la descrizione di quell'e-service")
    public void userUpdateEServiceDescription(String role, String tenant) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getIdentityService().getToken(tenant, role));
        String newDescription = String.format("Nuova descrizione - %d", sharedStepsContext.getTestSeed());
        sharedStepsContext.getHttpCallExecutor().performCall(
            () -> clientTokenConfigurator.getEServiceClient().updateEServiceDescription(
                eServicesCommonContext.getEserviceId(),
                new EServiceDescriptionUpdateSeed().description(newDescription)
            )
        );
        if(sharedStepsContext.getHttpCallExecutor().getResponseStatus().is2xxSuccessful()) {
            sharedStepsContext.getEServicesCommonContext().setDescription(newDescription);
        }
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
    }

    @When("l'utente {string} di {string} aggiorna la descrizione di quell'e-service con successo")
    public void successfullyUserUpdateEServiceDescription(String role, String tenant) {
        userUpdateEServiceDescription(role, tenant);
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getIdentityService().getToken(tenant, role));
        EServicesCommonContext eServiceContext = sharedStepsContext.getEServicesCommonContext();
        sharedStepsContext.getPollingService().makePolling(
            () -> clientTokenConfigurator.getEServiceClient().getEServiceDescriptor(
                eServiceContext.getEserviceId(),
                eServiceContext.getDescriptorId()
            ),
            res -> res.getEservice().getDescription().equals(eServiceContext.getDescription()),
            "L'aggiornamento della descrizione non ha avuto successo"
        );
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
    }
}
