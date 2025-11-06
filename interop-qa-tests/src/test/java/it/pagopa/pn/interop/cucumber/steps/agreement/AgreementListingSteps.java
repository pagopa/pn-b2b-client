package it.pagopa.pn.interop.cucumber.steps.agreement;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementState;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AgreementListingSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final BFFDataPreparationService dataPreparationService;

    public AgreementListingSteps(ClientTokenConfigurator clientTokenConfigurator,
                                 SharedStepsContext sharedStepsContext,
                                 BFFDataPreparationService dataPreparationService) {
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

    @When("l'utente richiede una operazione di listing limitata alle prime {int} richieste di fruizione")
    public void tenantRequireOperationListingWithLimit(int limit) {
        requireConsumerListingOperation(0, limit, null);
    }

    @When("l'utente richiede una operazione di listing con offset {int}")
    public void requireListingOperationWithOffset(int offset) {
        requireConsumerListingOperation(offset, 12, null);
    }

    @When("l'utente richiede una operazione di listing delle richieste di fruizione ai propri e-service")
    public void requireAgreementListingOperationToThierEService() {
        requireProducerListingOperation(0, 12, null, null, null);
    }

    @When("l'utente richiede una operazione di listing delle richieste di fruizione che ha creato")
    public void requireListingOperationForEServiceCreated() {
        requireConsumerListingOperation(0, 12, null);
    }

    @When("l'utente richiede una operazione di listing delle richieste di fruizione per {int} specifici e-service")
    public void requireListingOperationForNSpecificEService(int numberEServices) {
        List<UUID> publishedEservicesIds = sharedStepsContext.getEServicesCommonContext().getPublishedEservicesIds().stream()
                .map(EServiceDescriptor::getEServiceId).limit(numberEServices).toList();

        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient().getProducerAgreements(
                        0, 12, publishedEservicesIds, null, null, null
                )
        );
    }

    @When("l'utente richiede una operazione di listing delle richieste di fruizione di {string} che sono in stato {string} e {string}")
    public void requireListingOperationForAgreementWithStates(String consumer, String agreementState1, String agreementState2) {
        requireProducerListingOperation(0, 12, List.of(identityService.getOrganizationId(consumer)),
                List.of(AgreementState.fromValue(agreementState1), AgreementState.fromValue(agreementState2)), null);
    }

    @When("l'utente richiede una operazione di listing delle richieste di fruizione aggiornabili")
    public void requireListingOperationForUpdatableAgreement() {
        requireConsumerListingOperation(0, 12, true);
    }

    @When("l'utente richiede una operazione di listing delle richieste di fruizione")
    public void userRequireAgreementListingOperation() {
        requireConsumerListingOperation(0, 12, null);
    }

    private void requireConsumerListingOperation(int offset, int limit, Boolean showOnlyUpgradeable) {
        List<UUID> publishedEservicesIds = sharedStepsContext.getEServicesCommonContext().getPublishedEservicesIds().stream()
                .map(EServiceDescriptor::getEServiceId).toList();

        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient().getConsumerAgreements(offset, limit,
                        publishedEservicesIds, null, null, showOnlyUpgradeable)
        );
    }

    private void requireProducerListingOperation(int offset, int limit, List<UUID> consumerIds, List<AgreementState> states, Boolean showOnlyUpgradeable) {
        List<UUID> publishedEservicesIds = sharedStepsContext.getEServicesCommonContext().getPublishedEservicesIds().stream()
                .map(EServiceDescriptor::getEServiceId).toList();

        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getAgreementClient().getProducerAgreements(0, 12,
                        publishedEservicesIds, consumerIds, states, showOnlyUpgradeable)
        );
    }

    @Then("si ottiene status code {int} e la lista di {int} richiest(e)(a) di fruizione")
    public void verifyStatusCodeAndAgreementRequest(int statusCode, int count) {
        IHttpExecutor httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        Assertions.assertEquals(statusCode, httpCallExecutor.getResponseStatus().value());
    }




}
