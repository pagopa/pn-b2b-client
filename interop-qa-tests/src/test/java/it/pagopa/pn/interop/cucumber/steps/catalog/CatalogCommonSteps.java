package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.authorization.service.IProducerClient;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService.MutateDescriptorResult;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

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
        createEServiceWithDescriptorInState(tenantType, descriptorState);
    }

    @Given("{string} ha già creato un e-service in stato {string}")
    public void createEservice(String tenantType, String descriptorState) {
        if(descriptorState.equals(EServiceDescriptorState.ARCHIVED.getValue())) {
            // creazione e pubblicazione e-service
            createEServiceWithDescriptorInState(tenantType, "PUBLISHED");
            UUID eserviceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
            UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();

            // creazione e archiviazione di una richiesta di fruizione
            dataPreparationService.createAgreementWithGivenState(
                    AgreementState.fromValue("ARCHIVED"),
                    sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                    sharedStepsContext.getEServicesCommonContext().getDescriptorId(),
                    null,
                    null);

            // sospensione dell'unico descriptor
            IEServiceClient eServiceClient = clientTokenConfigurator.getEServiceClient();
            eServiceClient.suspendDescriptor(eserviceId, descriptorId);

            // polling su stato ARCHIVED del descriptor
            EServiceDescriptorState descriptorStateEn = EServiceDescriptorState.valueOf(descriptorState);
            PollingService pollingService = sharedStepsContext.getPollingService();
            IProducerClient producerClient = clientTokenConfigurator.getProducerClient();
            pollingService.makePolling(
                    () -> producerClient.getProducerEServiceDescriptor(eserviceId, descriptorId),
                    res -> res.getState() == descriptorStateEn,
                    "Non è stato possibile recuperare un descriptor in stato ARCHIVED"
            );
        } else {
            createEServiceWithDescriptorInState(tenantType, descriptorState);
        }
    }

    // TODO seconda versione dello step precedente a seguito di suggerimento di https://pagopaspa.slack.com/archives/C069AP16WG7/p1772813454835609?thread_ts=1772711595.778549&cid=C069AP16WG7 . Fare ordine appena possibile.
    @Given("{string} ha già creato un e-service in stato {string} 2")
    public void createEservice2(String tenantType, String descriptorState) {
        if(descriptorState.equals(EServiceDescriptorState.ARCHIVED.getValue())) {
            // creazione e pubblicazione e-service
            createEServiceWithDescriptorInState(tenantType, "PUBLISHED");
            UUID eserviceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
            UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();

            // creazione agreement
            UUID agreementId = dataPreparationService.createAgreementWithGivenState(
                    AgreementState.fromValue("ACTIVE"),
                    sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                    sharedStepsContext.getEServicesCommonContext().getDescriptorId(),
                    null,
                    null);

            // sospensione dell'unico descriptor
            IEServiceClient eServiceClient = clientTokenConfigurator.getEServiceClient();
            eServiceClient.suspendDescriptor(eserviceId, descriptorId);

            // archiviazione agreement
            dataPreparationService.archiveAgreement(agreementId);

            // polling su stato ARCHIVED del descriptor
            EServiceDescriptorState descriptorStateEn = EServiceDescriptorState.valueOf(descriptorState);
            PollingService pollingService = sharedStepsContext.getPollingService();
            IProducerClient producerClient = clientTokenConfigurator.getProducerClient();
            pollingService.makePolling(
                    () -> producerClient.getProducerEServiceDescriptor(eserviceId, descriptorId),
                    res -> res.getState() == descriptorStateEn,
                    "Non è stato possibile recuperare un descriptor in stato ARCHIVED"
            );
        } else {
            createEServiceWithDescriptorInState(tenantType, descriptorState);
        }
    }

    private void createEServiceWithDescriptorInState(String tenantType, String descriptorState) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        createEServiceWithDescriptor(descriptorState, dataPreparationService,
            sharedStepsContext.getEServicesCommonContext());
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
            documentPrettyNamePrefix);
        EServicesCommonContext eServicesCommonContext = sharedStepsContext.getEServicesCommonContext();
        eServicesCommonContext.setEserviceId(eServiceDescriptor.getEServiceId());
        eServicesCommonContext.setDescriptorId(eServiceDescriptor.getDescriptorId());

        eServicesCommonContext.setDocumentsMetadata(result.getDocumentsMetadata());
        // necessari per mantenere compatibilità con test scritti secondo un assetto antecedente
        eServicesCommonContext.setDocumentId(result.getDocumentId(0));
        eServicesCommonContext.setDocumentId2(result.getDocumentId(1));
    }
}
