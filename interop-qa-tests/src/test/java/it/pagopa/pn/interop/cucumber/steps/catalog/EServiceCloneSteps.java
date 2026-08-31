package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IProducerClient;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.utils.Validations;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService.ERROR_RETRIEVING_PRODUCER_DESCRIPTOR;

public class EServiceCloneSteps {
    private final BFFDataPreparationService dataPreparationService;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final EServicesCommonContext eServicesCommonContext;
    private final IProducerClient producerClient;

    public EServiceCloneSteps(BFFDataPreparationService dataPreparationService,
                              ClientTokenConfigurator clientTokenConfigurator,
                              SharedStepsContext sharedStepsContext) {
        this.dataPreparationService = dataPreparationService;
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.eServicesCommonContext = sharedStepsContext.getEServicesCommonContext();
        this.producerClient = clientTokenConfigurator.getProducerClient();
    }

    @Given("{string} tenta la creazione di una versione in DRAFT per quell'e-service")
    public void tenantTryToCreateVersionWithState(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));

        eServicesCommonContext.setOldDescriptorId(eServicesCommonContext.getDescriptorId());
        UUID descriptorId = dataPreparationService.createNextDraftDescriptor(eServicesCommonContext.getEserviceId());
        eServicesCommonContext.setDescriptorId(descriptorId);
    }

    @Given("{string} ha già creato una versione in {string} per quell'e-service")
    public void tenantHasAlreadyCreatedVersionWithState(String tenantType, String descriptorState) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));

        UUID descriptorId = dataPreparationService.createNextDraftDescriptor(eServicesCommonContext.getEserviceId());
        eServicesCommonContext.setDescriptorId(descriptorId);

        dataPreparationService.bringDescriptorToGivenState(eServicesCommonContext.getEserviceId(), eServicesCommonContext.getDescriptorId(),
                EServiceDescriptorState.fromValue(descriptorState), false);
    }

    @When("l'utente tenta di clonare quell'e-service")
    public void tryCloneEservice() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getEServiceClient().cloneEServiceByDescriptor(eServicesCommonContext.getEserviceId(), eServicesCommonContext.getDescriptorId())
        );
    }

    @When("l'utente tenta di clonare la vecchia versione dell'e-service")
    public void tryCloneOldEserviceVersion() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getEServiceClient().cloneEServiceByDescriptor(eServicesCommonContext.getEserviceId(), eServicesCommonContext.getOldDescriptorId())
        );
    }

    @When("l'utente tenta di clonare il descrittore con id {string} dell'e-service con id {string}")
    public void tryCloneEServiceDescriptor(String descriptorId, String eServiceId) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getEServiceClient().cloneEServiceByDescriptor(
                        UUID.fromString(eServiceId),
                        UUID.fromString(descriptorId)
                )
        );
    }

    @When("l'utente clona quell'e-service")
    public void cloneEservice() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getEServiceClient().cloneEServiceByDescriptor(eServicesCommonContext.getEserviceId(), eServicesCommonContext.getDescriptorId())
        );

        if (!sharedStepsContext.getHttpCallExecutor().getResponseStatus().is2xxSuccessful()) {
            eServicesCommonContext.setName(null);
            eServicesCommonContext.setEserviceId(null);
            eServicesCommonContext.setDescriptorId(null);
            return;
        }

        loadClonedEServiceFromResponse();
    }

    @Then("l'e-service è stato clonato con successo")
    public void verifyEServiceClonedSuccessfully() {
        HttpStatus responseStatus = sharedStepsContext.getHttpCallExecutor().getResponseStatus();
        Assertions.assertThat(responseStatus)
                .as("La clonazione dell'e-service deve restituire uno status HTTP")
                .isNotNull();
        Assertions.assertThat(responseStatus.is2xxSuccessful())
                .as("La clonazione dell'e-service deve avere successo, status ricevuto: %s", responseStatus)
                .isTrue();

        loadClonedEServiceFromResponse();
    }

    private void loadClonedEServiceFromResponse() {
        UUID eserviceId = ((CreatedEServiceDescriptor) sharedStepsContext.getHttpCallExecutor().getResponse()).getId();
        UUID descriptorId = ((CreatedEServiceDescriptor) sharedStepsContext.getHttpCallExecutor().getResponse()).getDescriptorId();

        HttpStatus status = sharedStepsContext.getPollingService().makePolling(
                () -> sharedStepsContext.getHttpCallExecutor().performCall(() -> producerClient.getProducerEServiceDescriptor(eserviceId, descriptorId)),
                res -> res != HttpStatus.NOT_FOUND && sharedStepsContext.getHttpCallExecutor().getResponse() != null,
                ERROR_RETRIEVING_PRODUCER_DESCRIPTOR
        );
        Assertions.assertThat(status.is2xxSuccessful()).isTrue();

        ProducerEServiceDescriptor response =
                (ProducerEServiceDescriptor) sharedStepsContext.getHttpCallExecutor().getResponse();
        Assertions.assertThat(response.getEservice().getName()).isNotNull();

        eServicesCommonContext.setName(response.getEservice().getName());
        eServicesCommonContext.setEserviceId(eserviceId);
        eServicesCommonContext.setDescriptorId(descriptorId);
    }

    @Then("il nome del nuovo e-service contiene {string} seguito dalla data e ora della clonazione")
    public void verifyClonedEServiceNameContainsCloneLabelAndTimestamp(String cloneLabel) {
        String clonedEServiceName = eServicesCommonContext.getName();

        Assertions.assertThat(clonedEServiceName)
                .as("Il nome del nuovo e-service deve contenere l'etichetta '%s'", cloneLabel)
                .contains(cloneLabel);

        int cloneLabelIndex = clonedEServiceName.indexOf(cloneLabel);
        String rawTimestamp = clonedEServiceName.substring(cloneLabelIndex + cloneLabel.length()).trim();

        Assertions.assertThat(rawTimestamp)
                .as("Il nome del nuovo e-service deve contenere una data/ora dopo '%s'", cloneLabel)
                .isNotBlank();

        Assertions.assertThat(Validations.isDdMmYyyyHhMmSsTimestamp(rawTimestamp))
                .as("La parte finale '%s' non è una data/ora valida", rawTimestamp)
                .isTrue();
    }
}
