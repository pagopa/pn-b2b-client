package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.EServiceState;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class EServiceCatalogListingSteps {
    private final BFFDataPreparationService dataPreparationService;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final EServicesCommonContext eServicesCommonContext;
    private final PollingService pollingService;
    private final IHttpExecutor httpExecutor;
    private final IEServiceClient eServiceClient;

    public EServiceCatalogListingSteps(BFFDataPreparationService dataPreparationService,
                                       ClientTokenConfigurator clientTokenConfigurator,
                                       SharedStepsContext sharedStepsContext) {
        this.dataPreparationService = dataPreparationService;
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.eServicesCommonContext = sharedStepsContext.getEServicesCommonContext();
        this.pollingService = sharedStepsContext.getPollingService();
        this.httpExecutor = sharedStepsContext.getHttpCallExecutor();
        this.eServiceClient = clientTokenConfigurator.getEServiceClient();
    }

    @Given("{string} ha già creato {int} e-services in catalogo in stato PUBLISHED o SUSPENDED e {int} in stato DRAFT")
    public void tenantHasAlreadyCreatedEservicesWithSpecificState(String tenantType, int countEServices, int countDraftEServices) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        int suspendedEServices = countEServices / 2;
        int publishedEServices = countEServices - suspendedEServices;

        List<EServiceDescriptor> publishedEServicesDes = createAndStoreEServices(publishedEServices, EServiceState.PUBLISHED);
        List<EServiceDescriptor> suspendedEServicesDes = createAndStoreEServices(suspendedEServices, EServiceState.SUSPENDED);
        List<EServiceDescriptor> draftEServicesDes = createAndStoreEServices(countDraftEServices, EServiceState.DRAFT);

        eServicesCommonContext.setPublishedEservicesIds(publishedEServicesDes);
        eServicesCommonContext.setSuspendedEservicesIds(suspendedEServicesDes);
        eServicesCommonContext.setDraftEServicesIds(draftEServicesDes);
    }

    private List<EServiceDescriptor> createAndStoreEServices(int totalEServices, EServiceState state) {
        List<EServiceDescriptor> eServiceDescriptors = new ArrayList<>();
        for (int i = 0; i < totalEServices; i++) {
            EServiceDescriptor eServiceDescriptor = dataPreparationService.createEServiceInState(
                    new EServiceSeed().name(String.format("eservice-%s-%d-%d", state, i, sharedStepsContext.getTestSeed())),
                    new UpdateEServiceDescriptorSeed(),
                    state);
            eServiceDescriptors.add(eServiceDescriptor);
        }
        return eServiceDescriptors;
    }

    @Given("{string} ha già creato {int} e-services in catalogo in stato PUBLISHED o SUSPENDED e {int} in stato DRAFT impostando il flagPersonalData a {string}")
    public void tenantHasAlreadyCreatedEservicesWithSpecificState(String tenantType, int countEServices, int countDraftEServices, String flagPersonalData) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        int suspendedEServices = countEServices / 2;
        int publishedEServices = countEServices - suspendedEServices;
        int draftEServices = countDraftEServices;
        int totalEServices = countEServices + draftEServices;
        Boolean personalData = flagPersonalData.equals("undefined") ? null : flagPersonalData.equalsIgnoreCase("true");

        List<EServiceDescriptor> eServiceDescriptors = new ArrayList<>();
        // 1. Create the draft e-services with draft descriptors
        for (int i = 0; i < totalEServices; i++) {
            EServiceDescriptor eServiceDescriptor = dataPreparationService.createEServiceAndDraftDescriptor(
                    new EServiceSeed().name(String.format("eservice-%d-%d", i, sharedStepsContext.getTestSeed())).personalData(personalData),
                    new UpdateEServiceDescriptorSeed());
            eServiceDescriptors.add(eServiceDescriptor);
        }

        // 2. Take only the ids of the e-services that needs to be published and suspended
        List<EServiceDescriptor> idsToPublishAndSuspend = eServiceDescriptors.subList(0, suspendedEServices + publishedEServices);

        // 3. For each draft descriptor, in order to publish it, add the document interface
        idsToPublishAndSuspend.forEach(e -> dataPreparationService.addInterfaceToDescriptor(e.getEServiceId(), e.getDescriptorId()));

        // 4. Publish the descriptors
        idsToPublishAndSuspend.forEach(e -> dataPreparationService.publishDescriptor(e.getEServiceId(), e.getDescriptorId()));

        // 5. Suspend the desired number of descriptors
        List<EServiceDescriptor> idsToSuspend = idsToPublishAndSuspend.subList(0, suspendedEServices);
        idsToSuspend.forEach(e -> dataPreparationService.suspendDescriptor(e.getEServiceId(), e.getDescriptorId()));

        eServicesCommonContext.setPublishedEservicesIds(idsToPublishAndSuspend.subList(0, suspendedEServices));
        eServicesCommonContext.setSuspendedEservicesIds(idsToSuspend);
        eServicesCommonContext.setDraftEServicesIds(eServiceDescriptors.subList(0, suspendedEServices + publishedEServices));
    }

    @Given("{string} ha un agreement attivo con un e-service di {string}")
    public void tenantAlreadyHasAnActiveAgreement(String tenantType, String producer) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));

        EServiceDescriptor eServiceDescriptor = eServicesCommonContext.getPublishedEservicesIds().get(0);
        UUID agreementId = dataPreparationService.createAgreement(eServiceDescriptor.getEServiceId(), eServiceDescriptor.getDescriptorId(), null)
                .orElseThrow(() -> new RuntimeException("Failed to create an agreement!"));
        dataPreparationService.submitAgreement(agreementId, AgreementState.ACTIVE);
        sharedStepsContext.setAgreementId(agreementId);
        sharedStepsContext.getAgreementCommonContext().setEserviceSubscribedId(eServiceDescriptor.getEServiceId());
        sharedStepsContext.getAgreementCommonContext().setDescriptorSubscribedId(eServiceDescriptor.getDescriptorId());
    }

    @When("l'utente richiede la lista di e-services per i quali ha almeno un agreement attivo")
    public void requireEServiceListWithActiveAgreement() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpExecutor.performCall(
                () -> eServiceClient.getEServicesCatalog(
                        0, 12, String.valueOf(sharedStepsContext.getTestSeed()), List.of(), List.of(),
                        List.of(EServiceDescriptorState.PUBLISHED, EServiceDescriptorState.SUSPENDED), List.of(AgreementState.ACTIVE),
                        null, null)
        );
    }

    @When("l'utente richiede una operazione di listing sul catalogo")
    public void requireEServiceCatalogList() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpExecutor.performCall(
                () -> eServiceClient.getEServicesCatalog(
                        0, 12, String.valueOf(sharedStepsContext.getTestSeed()), List.of(), List.of(),
                        List.of(EServiceDescriptorState.PUBLISHED, EServiceDescriptorState.SUSPENDED), null,
                        null, null)
        );
    }

    @When("l'utente richiede una operazione di listing sul catalogo limitata ai primi {int} e-services")
    public void requireEServiceCatalogListWithLimit(int limit) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpExecutor.performCall(
                () -> eServiceClient.getEServicesCatalog(
                        0, limit, String.valueOf(sharedStepsContext.getTestSeed()), List.of(), List.of(),
                        List.of(EServiceDescriptorState.PUBLISHED, EServiceDescriptorState.SUSPENDED), null,
                        null, null)
        );
    }

    @When("l'utente richiede una operazione di listing sul catalogo con offset {int}")
    public void requireEServiceCatalogListWithOffset(int offset) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpExecutor.performCall(
                () -> eServiceClient.getEServicesCatalog(
                        offset, 12, String.valueOf(sharedStepsContext.getTestSeed()), List.of(), List.of(),
                        List.of(EServiceDescriptorState.PUBLISHED, EServiceDescriptorState.SUSPENDED), null,
                        null, null)
        );
    }

    @When("l'utente richiede una operazione di listing degli e-services dell'erogatore {string}")
    public void requireEServiceCatalogListForProducer(String producer) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UUID producerId = identityService.getOrganizationId(producer);
        httpExecutor.performCall(
                () -> eServiceClient.getEServicesCatalog(
                        0, 12, String.valueOf(sharedStepsContext.getTestSeed()), List.of(producerId), List.of(),
                        List.of(EServiceDescriptorState.PUBLISHED, EServiceDescriptorState.SUSPENDED), null,
                        null, null)
        );
    }

    @When("l'utente richiede una operazione di listing sul catalogo filtrando per la keyword {string}")
    public void requireEServiceCatalogListByKeyword(String keyword) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpExecutor.performCall(
                () -> eServiceClient.getEServicesCatalog(
                        0, 12, String.format("%s-%s", sharedStepsContext.getTestSeed(), keyword), List.of(), List.of(),
                        List.of(EServiceDescriptorState.PUBLISHED, EServiceDescriptorState.SUSPENDED), null,
                        null, null)
        );
    }

    @Given("{string} ha già creato e pubblicato un e-service contenente la keyword {string}")
    public void tenantHasAlreadyCreatedEServiceWithKeyword(String tenantType, String keyword) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        String eServiceName = String.format("e-service-%s-%s", sharedStepsContext.getTestSeed(), keyword);

        EServiceDescriptor eServiceDescriptor = dataPreparationService.createEServiceAndDraftDescriptor(
                new EServiceSeed().name(eServiceName),
                new UpdateEServiceDescriptorSeed());

        eServicesCommonContext.setEserviceId(eServiceDescriptor.getEServiceId());
        eServicesCommonContext.setDescriptorId(eServiceDescriptor.getDescriptorId());

        dataPreparationService.addInterfaceToDescriptor(eServiceDescriptor.getEServiceId(), eServiceDescriptor.getDescriptorId());
        dataPreparationService.publishDescriptor(eServiceDescriptor.getEServiceId(), eServiceDescriptor.getDescriptorId());
    }

    @Then("la versione più recente dell'e-service è in stato {string}")
    @Then("l'e-service è in stato {string}")
    public void checkEServiceState(String eServiceState) {
        pollingService.makePolling(() -> httpExecutor.performCall(() -> eServiceClient.getProducerEServiceDescriptorWithHttpInfo(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        sharedStepsContext.getEServicesCommonContext().getDescriptorId())),
                HttpStatus::is2xxSuccessful,
                "L'e-service non è stato trovato. Visionare log per maggiori dettagli.");
        ResponseEntity<ProducerEServiceDescriptor> descriptor = (ResponseEntity<ProducerEServiceDescriptor>) httpExecutor.getResponse();
        assertThat(descriptor.getBody().getState()).isEqualTo(EServiceDescriptorState.fromValue(eServiceState));
    }

    @Then("il descrittore con id {string} dell'e-service con id {string} è in stato {string}")
    public void checkDescriptorStateByIds(String descriptorId, String eServiceId, String descriptorState) {
        pollingService.makePolling(() -> httpExecutor.performCall(() -> eServiceClient.getProducerEServiceDescriptorWithHttpInfo(
                        UUID.fromString(eServiceId),
                        UUID.fromString(descriptorId))),
                HttpStatus::is2xxSuccessful,
                "Il descrittore dell'e-service non è stato trovato. Visionare log per maggiori dettagli.");
        ResponseEntity<ProducerEServiceDescriptor> descriptor = (ResponseEntity<ProducerEServiceDescriptor>) httpExecutor.getResponse();
        assertThat(descriptor.getBody().getState()).isEqualTo(EServiceDescriptorState.fromValue(descriptorState));
    }

}
