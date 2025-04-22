package it.pagopa.pn.interop.cucumber.steps.agreement;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementState;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementSubmissionPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.List;
import java.util.UUID;

public class AgreementSubmitSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final DataPreparationService dataPreparationService;

    public AgreementSubmitSteps(ClientTokenConfigurator clientTokenConfigurator,
                                SharedStepsContext sharedStepsContext,
                                DataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.dataPreparationService = dataPreparationService;
    }

    @When("l'utente inoltra quella richiesta di fruizione")
    public void tenantSubmitAgreement() {
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient().submitAgreement(this.sharedStepsContext.getAgreementId(),
                        new AgreementSubmissionPayload())
        );
    }

    @Given("{string} ha già sospeso quell'e-service")
    public void tenantSuspendDescriptor(String tenant) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenant, null));
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> dataPreparationService.suspendDescriptor(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        sharedStepsContext.getEServicesCommonContext().getDescriptorId()
                )
        );
    }

    @Then("la richiesta di fruizione assume lo stato {string}")
    public void agreementReachSpecificStatus(String agremeentState) {
        sharedStepsContext.getPollingService().makePolling(
                () -> clientTokenConfigurator.getAgreementClient().getAgreementById(sharedStepsContext.getAgreementId()),
                res -> res.getState().equals(AgreementState.valueOf(agremeentState)),
                String.format("Agreement with id: %s and state: %s was not found!", sharedStepsContext.getAgreementId(), agremeentState.toUpperCase())
        );
    }

    @Given("{string} non possiede uno specifico attributo dichiarato")
    public void tenantDoesntHaveSpecificDeclaredAttribute(String tenant) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenant, null));
        UUID attributeId = dataPreparationService.createAttribute(AttributeKind.DECLARED, null);
        sharedStepsContext.getAttributeCommonContext().setRequiredDeclaredAttributes(List.of(List.of(attributeId)));
    }
}
