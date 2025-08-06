package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactEServicesLight;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CatalogCommonSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final BFFDataPreparationService dataPreparationService;

    public CatalogCommonSteps(ClientTokenConfigurator clientTokenConfigurator,
                              SharedStepsContext sharedStepsContext,
                              BFFDataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.dataPreparationService = dataPreparationService;
    }

    @Then("si ottiene status code {int} e la lista di {int} e-service(s)")
    public void verifyReceivedResponse(int statusCode, int eServiceNumber) {
        IHttpExecutor httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        Assertions.assertEquals(HttpStatus.valueOf(statusCode), httpCallExecutor.getResponseStatus());
        Assertions.assertEquals(eServiceNumber,
                ((ResponseEntity<CompactEServicesLight>) httpCallExecutor.getResponse()).getBody().getResults().size());

    }

    @Given("{string} ha già creato un e-service con un descrittore in stato {string}")
    public void createEserviceWithDescriptor(String tenantType, String descriptorState) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        createEServiceWithDescriptor(descriptorState, dataPreparationService,
            sharedStepsContext.getEServicesCommonContext());
    }

    public static void createEServiceWithDescriptor(
        String descriptorState,
        BFFDataPreparationService dataPreparationService,
        EServicesCommonContext eServiceContext
    ) {
        EServiceDescriptor eServiceDescriptor = dataPreparationService.createEServiceAndDraftDescriptor(new EServiceSeed(), new UpdateEServiceDescriptorSeed());
        dataPreparationService.bringDescriptorToGivenState(eServiceDescriptor.getEServiceId(),
                eServiceDescriptor.getDescriptorId(), EServiceDescriptorState.valueOf(
                descriptorState), false);
        eServiceContext.setEserviceId(eServiceDescriptor.getEServiceId());
        eServiceContext.setDescriptorId(eServiceDescriptor.getDescriptorId());
    }

    @Given("{string} ha già creato un e-service con un descrittore in stato {string} e un documento già caricato")
    public void createEServiceWithDescriptorAndDocument(String tenantType, String descriptorState) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));

        EServiceDescriptor eServiceDescriptor = dataPreparationService.createEServiceAndDraftDescriptor(new EServiceSeed(), new UpdateEServiceDescriptorSeed());
        Map<String, UUID> result = dataPreparationService.bringDescriptorToGivenState(eServiceDescriptor.getEServiceId(), eServiceDescriptor.getDescriptorId(),
                EServiceDescriptorState.valueOf(descriptorState), true);
        EServicesCommonContext eServicesCommonContext = sharedStepsContext.getEServicesCommonContext();
        eServicesCommonContext.setEserviceId(eServiceDescriptor.getEServiceId());
        eServicesCommonContext.setDescriptorId(eServiceDescriptor.getDescriptorId());
        eServicesCommonContext.setDocumentId(Optional.ofNullable(result).map(x -> x.get("documentId")).orElse(null));
    }
}
