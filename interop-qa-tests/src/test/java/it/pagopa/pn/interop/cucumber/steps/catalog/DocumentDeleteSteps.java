package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService.MutateDescriptorResult;
import java.util.UUID;

public class DocumentDeleteSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;
    private final EServicesCommonContext eServicesCommonContext;
    private final IdentityService identityService;
    private final BFFDataPreparationService dataPreparationService;

    public DocumentDeleteSteps(ClientTokenConfigurator clientTokenConfigurator,
                               SharedStepsContext sharedStepsContext,
                               BFFDataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.eServicesCommonContext = sharedStepsContext.getEServicesCommonContext();
        this.identityService = sharedStepsContext.getIdentityService();
        this.dataPreparationService = dataPreparationService;
    }

    @When("l'utente cancella quel documento")
    public void userRemoveDocument() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().deleteEServiceDocumentById(
                        eServicesCommonContext.getEserviceId(), eServicesCommonContext.getDescriptorId(), eServicesCommonContext.getDocumentId()
                )
        );
    }

    @When("l'utente cancella quell'interfaccia")
    public void userRemoveInterface() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().deleteEServiceDocumentById(
                        eServicesCommonContext.getEserviceId(), eServicesCommonContext.getDescriptorId(), eServicesCommonContext.getInterfaceId()
                )
        );
    }

    @When("l'utente cancella quell'interfaccia di callback")
    public void userRemoveCallbackInterface() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().deleteEServiceDocumentById(
                        eServicesCommonContext.getEserviceId(), eServicesCommonContext.getDescriptorId(), eServicesCommonContext.getCallbackInterfaceId()
                )
        );
    }

    @Given("{string} ha già creato un e-service con un descrittore in stato {string} con un'interfaccia già caricata")
    public void createEserviceWihtDraftDescriptorAndInterface(String tenantType, String descriptorState) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));

        EServiceDescriptor eServiceDescriptor = dataPreparationService.createEServiceAndDraftDescriptor(new EServiceSeed(), new UpdateEServiceDescriptorSeed());
        MutateDescriptorResult result = dataPreparationService.bringDescriptorToGivenState(eServiceDescriptor.getEServiceId(), eServiceDescriptor.getDescriptorId(), EServiceDescriptorState.fromValue(descriptorState), false);
        UUID interfaceId = result.getInterfaceId();
        if (descriptorState.equalsIgnoreCase("DRAFT")) {
            interfaceId = dataPreparationService.addInterfaceToDescriptor(eServiceDescriptor.getEServiceId(), eServiceDescriptor.getDescriptorId());
        }
        eServicesCommonContext.setEserviceId(eServiceDescriptor.getEServiceId());
        eServicesCommonContext.setDescriptorId(eServiceDescriptor.getDescriptorId());
        eServicesCommonContext.setInterfaceId(interfaceId);
    }
}
