package it.pagopa.pn.interop.cucumber.steps.m2m;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.service.IM2MAgreementClient;
import it.pagopa.interop.agreement.service.IM2MAgreementClient.AgreementsListRequest;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementState;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Agreements;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.M2MDataPreparationService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;

public class AgreementSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final M2MDataPreparationService dataPreparationService;
    private final IM2MAgreementClient agreementClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;

    public AgreementSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        M2MDataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.dataPreparationService = dataPreparationService;
        this.agreementClient = clientTokenConfigurator.getM2mAgreementClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
    }

    @Given("{string} ha un agreement m2m attivo per ciascun e-service di {string}")
    public void tenantAlreadyHasActiveAgreementForEachEService(String consumer, String producer) {
        List<UUID> agreementsIds = sharedStepsContext.getEServicesCommonContext().getPublishedEservicesIds().stream()
            .map(eServiceDescriptor -> dataPreparationService.createAgreement(eServiceDescriptor.getEServiceId(), eServiceDescriptor.getDescriptorId(), null))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();

        agreementsIds.forEach(agreementId -> dataPreparationService.submitAgreement(agreementId, AgreementState.ACTIVE));
    }

    @When("l'utente tenta di recuperare la lista completa degli agreements")
    public void agreementsListAttempt() {
        httpCallExecutor.performCall(() -> agreementClient.getAgreements(
            AgreementsListRequest.builder()
                .offset(0)
                .limit(30)
                .build()
        ));
    }

    @Then("sono stati visualizzati correttamente {int} agreements")
    public void agreementsSuccessfullyGot(int expectedSize) {
        HttpStatus clientResponse = httpCallExecutor.getClientResponse();
        if(clientResponse.isError()) {
            Assertions.fail("Agreements list request failed: ", clientResponse);
        }

        Agreements agreements = (Agreements) httpCallExecutor.getResponse();
        Assertions.assertThat(agreements.getResults()).hasSize(expectedSize);
    }

}
