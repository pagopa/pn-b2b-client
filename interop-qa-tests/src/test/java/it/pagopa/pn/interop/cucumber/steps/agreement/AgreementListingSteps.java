package it.pagopa.pn.interop.cucumber.steps.agreement;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementState;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.apache.commons.lang.NotImplementedException;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AgreementListingSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final DataPreparationService dataPreparationService;

    public AgreementListingSteps(ClientTokenConfigurator clientTokenConfigurator,
                                 SharedStepsContext sharedStepsContext,
                                 DataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.dataPreparationService = dataPreparationService;
    }

    @Given("{string} ha un agreement attivo per ciascun e-service di {string}")
    public void tenantAlreadyHasActiveAgreementForEachEService(String consumer, String producer) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(consumer, null));

        List<UUID> agreementsIds = sharedStepsContext.getEServicesCommonContext().getPublishedEservicesIds().stream()
                .map(eServiceDescriptor -> dataPreparationService.createAgreement(eServiceDescriptor.getEServiceId(), eServiceDescriptor.getDescriptorId(), null))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        agreementsIds.forEach(agreementId -> dataPreparationService.submitAgreement(agreementId, AgreementState.ACTIVE));
    }

    @Given("{string} ha un agreement in stato {string} per l'e-service numero {int} di {string}")
    public void tenantAlreadyHasAnAgreementForTheSpecificEService(String consumer, String agreementState, int eserviceIndex, String producer) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(consumer, null));
        EServiceDescriptor eServiceByIndex = sharedStepsContext.getEServicesCommonContext().getPublishedEservicesIds().get(eserviceIndex);

        dataPreparationService.createAgreementWithGivenState(AgreementState.valueOf(agreementState), eServiceByIndex.getEServiceId(),
                eServiceByIndex.getDescriptorId(), null);
    }

    //TODO da ricontrollare
    @Given("{string} ha già pubblicato una nuova versione per {int} di questi e-service")
    public void tenantHasAlreadyPublishedNewEServiceVersion(String tenantType, int descriptorsCount) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        List<UUID> eserviceIds = sharedStepsContext.getEServicesCommonContext().getPublishedEservicesIds()
                .stream().limit(descriptorsCount).map(EServiceDescriptor::getEServiceId).toList();

        for (UUID eserviceId : eserviceIds) {
            UUID descriptorId = dataPreparationService.createNextDraftDescriptor(eserviceId);
            dataPreparationService.bringDescriptorToGivenState(eserviceId, descriptorId, EServiceDescriptorState.PUBLISHED, false);
        }
    }

    //TODO il metodo getAgreements (/agreements) in GET sembra non essere più disponibile
    @When("l'utente richiede una operazione di listing limitata alle prime {int} richieste di fruizione")
    public void tenantRequireOperationListingWithLimit(int limit) {
        throw new NotImplementedException();
    }

    //TODO il metodo getAgreements (/agreements) in GET sembra non essere più disponibile
    @When("l'utente richiede una operazione di listing con offset {int}")
    public void requireListingOperationWithOffset(int offset) {
        throw new NotImplementedException();
    }

    //TODO il metodo getAgreements (/agreements) in GET sembra non essere più disponibile
    @When("l'utente richiede una operazione di listing delle richieste di fruizione ai propri e-service")
    public void requireAgreementListingOperationToThierEService() {
        throw new NotImplementedException();
    }

    //TODO il metodo getAgreements (/agreements) in GET sembra non essere più disponibile
    @When("l'utente richiede una operazione di listing delle richieste di fruizione che ha creato")
    public void requireListingOperationForEServiceCreated() {
        throw new NotImplementedException();
    }

    //TODO il metodo getAgreements (/agreements) in GET sembra non essere più disponibile
    @When("l'utente richiede una operazione di listing delle richieste di fruizione per {int} specifici e-service")
    public void requireListingOperationForNSpecificEService(int numberEServices) {
        throw new NotImplementedException();
    }

    //TODO il metodo getAgreements (/agreements) in GET sembra non essere più disponibile
    @When("l'utente richiede una operazione di listing delle richieste di fruizione di {string} che sono in stato {string} e {string}")
    public void requireListingOperationForAgreementWithStates(String consumer, String agreementState1, String agreementState2) {
        throw new NotImplementedException();
    }

    //TODO il metodo getAgreements (/agreements) in GET sembra non essere più disponibile
    @When("l'utente richiede una operazione di listing delle richieste di fruizione aggiornabili")
    public void requireListingOperationForUpdatableAgreement() {
        throw new NotImplementedException();
    }

    //TODO il metodo getAgreements (/agreements) in GET sembra non essere più disponibile
    @When("l'utente richiede una operazione di listing delle richieste di fruizione")
    public void userRequireAgreementListingOperation() {
        throw new NotImplementedException();
    }

    @Then("si ottiene status code {int} e la lista di {int} richiest(e)(a) di fruizione")
    public void verifyStatusCodeAndAgreementRequest(int statusCode, int count) {
        HttpCallExecutor httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        Assertions.assertEquals(statusCode, httpCallExecutor.getClientResponse().value());
    }




}
