package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;
import it.pagopa.pn.interop.cucumber.utility.BlobFileCreator;
import org.springframework.core.io.Resource;

import java.util.UUID;

public class DocumentUploadSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final EServicesCommonContext eServicesCommonContext;
    private final DataPreparationService dataPreparationService;
    private final IdentityService identityService;
    private final BlobFileCreator blobFileCreator;

    public DocumentUploadSteps(ClientTokenConfigurator clientTokenConfigurator,
                               SharedStepsContext sharedStepsContext,
                               DataPreparationService dataPreparationService,
                               BlobFileCreator blobFileCreator) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.eServicesCommonContext = sharedStepsContext.getEServicesCommonContext();
        this.dataPreparationService = dataPreparationService;
        this.identityService = sharedStepsContext.getIdentityService();
        this.blobFileCreator = blobFileCreator;
    }

    @Given("{string} ha già creato un e-service con un descrittore in stato {string} e tecnologia {string}")
    public void createEServiceWithDescriptorStateAndTechnology(String tenantType, String descriptorState, String technology) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));

        EServiceDescriptor eServiceDescriptor = dataPreparationService.createEServiceAndDraftDescriptor(
                new EServiceSeed().technology(EServiceTechnology.fromValue(technology)),
                new UpdateEServiceDescriptorSeed()
        );

        UUID documentId = dataPreparationService.bringDescriptorToGivenState(eServiceDescriptor.getEServiceId(), eServiceDescriptor.getDescriptorId(),
                EServiceDescriptorState.fromValue(descriptorState), false).get("documentId");
        eServicesCommonContext.setEserviceId(eServiceDescriptor.getEServiceId());
        eServicesCommonContext.setDescriptorId(eServiceDescriptor.getDescriptorId());
        eServicesCommonContext.setDocumentId(documentId);
    }

    @When("l'utente carica un documento di interfaccia di tipo {string}")
    public void uploadInterfaceDocument(String fileType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        String fileName = String.format("interface.%s", fileType);
        String filePath = String.format("src/main/resources/%s", fileName);
        Resource resource = blobFileCreator.createBlobFile(filePath, fileName);
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getEServiceClient().createEServiceDocument(eServicesCommonContext.getEserviceId(),
                        eServicesCommonContext.getDescriptorId(), "INTERFACE", "Interfaccia", resource)
        );
    }

    @When("l'utente carica un documento di interfaccia di tipo {string} che contiene il termine localhost")
    public void uploadInterfaceWithLocalhostTerms(String fileType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        String fileName = String.format("localhost-interface.%s", fileType);
        String filePath = String.format("src/main/resources/%s", fileName);
        Resource resource = blobFileCreator.createBlobFile(filePath, fileName);

        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getEServiceClient().createEServiceDocument(eServicesCommonContext.getEserviceId(),
                        eServicesCommonContext.getDescriptorId(), "INTERFACE", "Interfaccia", resource)
        );
    }

    @Given("{string} ha già caricato un documento con nome {string} in quel descrittore")
    public void addDocumentWithName(String tenantType, String prettyName) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        dataPreparationService.addDocumentToDescriptor(eServicesCommonContext.getEserviceId(), eServicesCommonContext.getDescriptorId(), prettyName);
    }

    @When("l'utente carica un documento con nome {string} in quel descrittore")
    public void uploadDocument(String prettyName) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        Resource resource = blobFileCreator.createBlobFile("src/main/resources/dummy.pdf", "documento-test-qa.pdf");
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getEServiceClient().createEServiceDocument(eServicesCommonContext.getEserviceId(),
                        eServicesCommonContext.getDescriptorId(), "DOCUMENT", prettyName, resource)
        );
    }
}
