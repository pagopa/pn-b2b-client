package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.time.OffsetDateTime;

public class EServiceUpdateSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;

    public EServiceUpdateSteps(ClientTokenConfigurator clientTokenConfigurator,
                               SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
    }

    @When("l'utente aggiorna quell'e-service")
    public void userUpdateEService() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        userUpdateEServiceImpl();
    }

    @When("{string} aggiorna quell'e-service")
    public void userUpdateEService(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        userUpdateEServiceImpl();
    }

    private void userUpdateEServiceImpl() {
        IHttpExecutor httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().updateEServiceById(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        new UpdateEServiceSeed()
                                .name(String.format("e-service - %d", sharedStepsContext.getTestSeed()))
                                .description("Nuova descrizione")
                                .mode(EServiceMode.DELIVER)
                                .technology(EServiceTechnology.SOAP)
                )
        );
        if(httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            sharedStepsContext.getEServicesCommonContext().setEServiceEditTimestamp(OffsetDateTime.now());
        }
    }
}
