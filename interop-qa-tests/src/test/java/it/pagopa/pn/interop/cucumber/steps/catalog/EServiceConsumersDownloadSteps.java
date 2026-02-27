package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementState;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;

import java.util.UUID;

public class EServiceConsumersDownloadSteps {
    private final BFFDataPreparationService dataPreparationService;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final EServicesCommonContext eServicesCommonContext;

    public EServiceConsumersDownloadSteps(BFFDataPreparationService dataPreparationService,
                              ClientTokenConfigurator clientTokenConfigurator,
                              SharedStepsContext sharedStepsContext) {
        this.dataPreparationService = dataPreparationService;
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.eServicesCommonContext = sharedStepsContext.getEServicesCommonContext();
    }

    @Given("{string} ha un agreement attivo con quell'e-service")
    public void tenantAlreadyHasAnActiveAgreement(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        UUID agreementId = dataPreparationService.createAgreement(
                eServicesCommonContext.getEserviceId(), eServicesCommonContext.getDescriptorId(), null
        ).orElseThrow();
        dataPreparationService.submitAgreement(agreementId, AgreementState.ACTIVE);
    }

    @When("l'utente richiede una operazione di download dei fruitori di quell'e-service")
    public void userRequiresConsumerDownload() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getEServiceClient().getEServiceConsumers(eServicesCommonContext.getEserviceId())
        );
    }
}
