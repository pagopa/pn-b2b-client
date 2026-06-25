package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService.MutateDescriptorResult;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.EServiceState;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static it.pagopa.pn.interop.cucumber.utility.StepParser.nullableBoolean;

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

    @Then("si ottiene status code {int} e la lista di {int} e-service(s) dal catalogo")
    public void verifyReceivedCatalogResponse(int statusCode, int eServiceNumber) {
        IHttpExecutor httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        Assertions.assertEquals(HttpStatus.valueOf(statusCode), httpCallExecutor.getResponseStatus());
        Assertions.assertEquals(eServiceNumber,
                ((CatalogEServices) httpCallExecutor.getResponse()).getResults().size());
    }

    @Then("si ottiene status code {int} e la lista di {int} e-service(s)")
    public void verifyReceivedResponse(int statusCode, int eServiceNumber) {
        IHttpExecutor httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        Assertions.assertEquals(HttpStatus.valueOf(statusCode), httpCallExecutor.getResponseStatus());
        Assertions.assertEquals(eServiceNumber,
                ((ResponseEntity<CompactEServicesLight>) httpCallExecutor.getResponse()).getBody().getResults().size());
    }

    @Given("{string} ha già creato un e-service {isAsynchronous} con un descrittore in stato {string} con:")
    public void createEserviceWithDescriptorAndState(String tenantType, Boolean isAsync, String descriptorState, UpdateEServiceDescriptorSeed descriptorSeed) {
        createEServiceWithDescriptorInState(tenantType, descriptorState, new EServiceSeed().asyncExchange(isAsync), descriptorSeed);
    }

    @Given("{string} ha già creato un e-service {isAsynchronous} in stato {string} con:")
    public void createEserviceWithDescriptorAndState(String tenantType, Boolean isAsync, String descriptorState, EServiceSeed eServiceSeed) {
        createEServiceWithDescriptorInState(tenantType, descriptorState, eServiceSeed.asyncExchange(isAsync), new UpdateEServiceDescriptorSeed());
    }

    @Given("{string} ha già creato un e-service con un descrittore in stato {string}")
    public void createEserviceWithDescriptor(String tenantType, String descriptorState) {
        createEServiceWithDescriptorInState(tenantType, descriptorState, new EServiceSeed(), new UpdateEServiceDescriptorSeed());
    }

    @Given("{string} ha già creato un e-service con un descrittore in stato {string} e impostando delega amministrativa a {string} e delega tecnica a {string}")
    public void createEserviceWithDescriptorAndSpecifyingConsumerDelegationFlags(String tenantType, String descriptorState, String isConsumerDelegable, String isClientAccessDelegable) {
        createEServiceWithDescriptorInStateSpecifyingConsumerDelegationFlags(tenantType, descriptorState, nullableBoolean(isConsumerDelegable), nullableBoolean(isClientAccessDelegable));
    }

    @Given("{string} ha già creato un e-service in stato {eServiceState}")
    public void createEservice(String tenantType, EServiceState eServiceState) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        EServiceDescriptor eServiceDescriptor = this.dataPreparationService.createEServiceInState(new EServiceSeed(), new UpdateEServiceDescriptorSeed(), eServiceState);
        sharedStepsContext.getEServicesCommonContext().setEserviceId(eServiceDescriptor.getEServiceId());
        sharedStepsContext.getEServicesCommonContext().setDescriptorId(eServiceDescriptor.getDescriptorId());
    }

    private void createEServiceWithDescriptorInState(String tenantType, String descriptorState, EServiceSeed eServiceSeed, UpdateEServiceDescriptorSeed descriptorSeed) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        createEServiceWithDescriptor(descriptorState, dataPreparationService,
                sharedStepsContext.getEServicesCommonContext(),
                eServiceSeed, descriptorSeed);
        sharedStepsContext.getEServicesCommonContext().setProducerName(sharedStepsContext.getIdentityService().getTenantName(tenantType));
    }

    private void createEServiceWithDescriptorInStateSpecifyingConsumerDelegationFlags(String tenantType, String descriptorState, Boolean isConsumerDelegable, Boolean isClientAccessDelegable) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        createEServiceWithDescriptorSpecifyingConsumerDelegationFlags(descriptorState, dataPreparationService,
                sharedStepsContext.getEServicesCommonContext(), isConsumerDelegable, isClientAccessDelegable);
    }

    @Given("{string} porta il descrittore dell'e-service in stato {string}")
    public void bringDescriptorToState(String tenantType, String state) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        UUID eserviceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        dataPreparationService.bringDescriptorToGivenState(
                eserviceId,
                descriptorId,
                EServiceDescriptorState.valueOf(state),
                false);
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
    }

    public static void createEServiceWithDescriptor(
            String descriptorState,
            BFFDataPreparationService dataPreparationService,
            EServicesCommonContext eServiceContext,
            EServiceSeed eServiceSeed,
            UpdateEServiceDescriptorSeed descriptorSeed
    ) {
        EServiceDescriptor eServiceDescriptor = dataPreparationService.createEServiceAndDraftDescriptor(
                eServiceSeed == null ? new EServiceSeed() : eServiceSeed,
                descriptorSeed == null ? new UpdateEServiceDescriptorSeed() : descriptorSeed);
        boolean isAsyncExchange = eServiceSeed != null && eServiceSeed.getAsyncExchange() != null && eServiceSeed.getAsyncExchange();
        dataPreparationService.bringDescriptorToGivenState(eServiceDescriptor.getEServiceId(),
                eServiceDescriptor.getDescriptorId(), EServiceDescriptorState.valueOf(
                        descriptorState), false, isAsyncExchange);
        eServiceContext.setEserviceId(eServiceDescriptor.getEServiceId());
        eServiceContext.setDescriptorId(eServiceDescriptor.getDescriptorId());
    }

    public static void createEServiceWithDescriptorSpecifyingConsumerDelegationFlags(
            String descriptorState,
            BFFDataPreparationService dataPreparationService,
            EServicesCommonContext eServiceContext,
            Boolean isConsumerDelegable,
            Boolean isClientAccessDelegable
    ) {
        EServiceDescriptor eServiceDescriptor = dataPreparationService.createEServiceAndDraftDescriptorSpecifyingConsumerDelegationFlags(new EServiceSeed(), new UpdateEServiceDescriptorSeed(), isConsumerDelegable, isClientAccessDelegable);
        dataPreparationService.bringDescriptorToGivenState(eServiceDescriptor.getEServiceId(),
                eServiceDescriptor.getDescriptorId(), EServiceDescriptorState.valueOf(
                        descriptorState), false);
        eServiceContext.setEserviceId(eServiceDescriptor.getEServiceId());
        eServiceContext.setDescriptorId(eServiceDescriptor.getDescriptorId());
    }

    @Given("{string} ha già creato un e-service con un descrittore in stato {string} e un documento già caricato")
    public void createEServiceWithDescriptorAndDocument(String tenantType, String descriptorState) {
        createEServiceWithDescriptorAndDocuments(tenantType, descriptorState, 1, null, null);
    }

    @Given("{string} ha già creato un e-service con un descrittore in stato {string} e {int} documenti già caricati")
    public void createEServiceWithDescriptorAndDocument(String tenantType, String descriptorState, int documents) {
        String documentNamePrefix = "Document QA test name";
        String documentPrettyNamePrefix = "Document QA test pretty name";
        createEServiceWithDescriptorAndDocuments(tenantType, descriptorState, documents, documentNamePrefix,
                documentPrettyNamePrefix);
    }

    private void createEServiceWithDescriptorAndDocuments(String tenantType, String descriptorState, int documents,
                                                          String documentNamePrefix, String documentPrettyNamePrefix) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));

        EServiceDescriptor eServiceDescriptor = dataPreparationService.createEServiceAndDraftDescriptor(new EServiceSeed(), new UpdateEServiceDescriptorSeed());
        MutateDescriptorResult result = dataPreparationService.bringDescriptorToGivenState(
                eServiceDescriptor.getEServiceId(), eServiceDescriptor.getDescriptorId(),
                EServiceDescriptorState.valueOf(descriptorState), documents, documentNamePrefix,
                documentPrettyNamePrefix, false);
        EServicesCommonContext eServicesCommonContext = sharedStepsContext.getEServicesCommonContext();
        eServicesCommonContext.setEserviceId(eServiceDescriptor.getEServiceId());
        eServicesCommonContext.setDescriptorId(eServiceDescriptor.getDescriptorId());

        eServicesCommonContext.setDocumentsMetadata(result.getDocumentsMetadata());
        // necessari per mantenere compatibilità con test scritti secondo un assetto antecedente
        eServicesCommonContext.setDocumentId(result.getDocumentId(0));
        eServicesCommonContext.setDocumentId2(result.getDocumentId(1));
    }


    @Then("il nome del nuovo e-service non supera i {int} caratteri")
    public void verifyEServiceNameLengthLessThanOrEqualTo(int maxLength) {
        String eServiceName = sharedStepsContext.getEServicesCommonContext().getName();

        org.assertj.core.api.Assertions.assertThat(eServiceName.length())
                .as("Il nome del nuovo e-service supera i %d caratteri", maxLength)
                .isLessThanOrEqualTo(maxLength);
    }
}
