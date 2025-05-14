package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorDocumentSeed;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;

import java.util.UUID;

public class DocumentUpdateSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final EServicesCommonContext eServicesCommonContext;
    private final HttpCallExecutor httpCallExecutor;
    private final IdentityService identityService;
    private final DataPreparationService dataPreparationService;

    public DocumentUpdateSteps(ClientTokenConfigurator clientTokenConfigurator, SharedStepsContext sharedStepsContext,
                               DataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.eServicesCommonContext = sharedStepsContext.getEServicesCommonContext();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.identityService = sharedStepsContext.getIdentityService();
        this.dataPreparationService = dataPreparationService;
    }

    @When("l'utente aggiorna il nome di quel documento")
    public void updateNameDocument() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(() -> clientTokenConfigurator.getEServiceClient().updateEServiceDocumentById(
                eServicesCommonContext.getEserviceId(), eServicesCommonContext.getDescriptorId(), eServicesCommonContext.getDocumentId(),
                new UpdateEServiceDescriptorDocumentSeed().prettyName("updatedPrettyName"))
        );
    }

    @Given("{string} ha già caricato due documenti con nome {string} e {string} in quel descrittore")
    public void uploadTwoDocumentsWithName(String tenantType, String prettyName1, String prettyName2) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        dataPreparationService.addDocumentToDescriptor(eServicesCommonContext.getEserviceId(), eServicesCommonContext.getDescriptorId(), prettyName1);

        UUID documentId2 = dataPreparationService.addDocumentToDescriptor(eServicesCommonContext.getEserviceId(), eServicesCommonContext.getDescriptorId(), prettyName2);
        eServicesCommonContext.setDocumentId2(documentId2);
    }

    @When("l'utente modifica il nome del secondo documento in {string}")
    public void updateSecondDocumentName(String prettyName) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(() -> clientTokenConfigurator.getEServiceClient().updateEServiceDocumentById(
                eServicesCommonContext.getEserviceId(),
                eServicesCommonContext.getDescriptorId(),
                eServicesCommonContext.getDocumentId2(),
                new UpdateEServiceDescriptorDocumentSeed().prettyName(prettyName)
        ));
    }
}
