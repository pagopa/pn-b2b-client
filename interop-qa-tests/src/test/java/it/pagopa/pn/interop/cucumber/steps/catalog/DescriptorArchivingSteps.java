package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.ArchivingScope;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.catalog.utils.CatalogResolver;
import it.pagopa.pn.interop.cucumber.steps.catalog.utils.DescriptorArchivingScheduleVerifier;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public class DescriptorArchivingSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;
    private final CatalogResolver catalogResolver;
    private final DescriptorArchivingScheduleVerifier archivingScheduleVerifier;

    public DescriptorArchivingSteps(
            ClientTokenConfigurator clientTokenConfigurator,
            SharedStepsContext sharedStepsContext
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.catalogResolver = new CatalogResolver(sharedStepsContext);
        this.archivingScheduleVerifier = new DescriptorArchivingScheduleVerifier(clientTokenConfigurator, sharedStepsContext);
    }

    @When("l'utente archivia la vecchia versione con id {string} dell'e-service con id {string}")
    public void archiveOldDescriptor(String descriptorId, String eServiceId) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID resolvedDescriptorId = catalogResolver.resolveOldDescriptorId(descriptorId);
        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);

        scheduleArchiveDescriptor(resolvedEServiceId, resolvedDescriptorId);
    }

    @When("l'utente archivia la versione più recente dell'e-service")
    public void archiveLatestDescriptor() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();

        scheduleArchiveDescriptor(eServiceId, descriptorId);
    }

    @When("l'utente annulla il processo di archiviazione della vecchia versione con id {string} dell'e-service con id {string}")
    public void cancelOldDescriptorArchiving(String descriptorId, String eServiceId) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID resolvedDescriptorId = catalogResolver.resolveOldDescriptorId(descriptorId);
        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);

        cancelDescriptorArchiving(resolvedEServiceId, resolvedDescriptorId);
    }

    @Given("l'utente ha già messo in archiviazione la vecchia versione con id {string} dell'e-service con id {string}")
    public void oldDescriptorAlreadyInArchiving(String descriptorId, String eServiceId) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID resolvedDescriptorId = catalogResolver.resolveOldDescriptorId(descriptorId);
        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);
        ProducerEServiceDescriptor oldDescriptor = clientTokenConfigurator.getEServiceClient()
                .getEServiceDescriptor(resolvedEServiceId, resolvedDescriptorId);
        EServiceDescriptorState expectedState = expectedArchivingState(oldDescriptor.getState());

        scheduleArchiveDescriptor(resolvedEServiceId, resolvedDescriptorId);
        if (httpCallExecutor.getResponseStatus() == null || !httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            throw new IllegalStateException("L'avvio dell'archiviazione della vecchia versione dell'e-service non ha avuto successo");
        }

        pollDescriptorState(resolvedEServiceId, resolvedDescriptorId, expectedState);
        archivingScheduleVerifier.pollDescriptorArchivingSchedule(resolvedEServiceId, resolvedDescriptorId, ArchivingScope.DESCRIPTOR);
    }

    private void scheduleArchiveDescriptor(UUID eServiceId, UUID descriptorId) {
        archivingScheduleVerifier.registerDescriptorArchivingRequestTimestamp();
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient()
                        .scheduleArchiveDescriptor(eServiceId, descriptorId),
                ResponseEntity::getStatusCode
        );
    }

    private void cancelDescriptorArchiving(UUID eServiceId, UUID descriptorId) {
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient()
                        .cancelDescriptorArchiving(eServiceId, descriptorId),
                ResponseEntity::getStatusCode
        );
    }

    @Then("la vecchia versione dell'e-service è in stato {string}")
    public void oldEServiceVersionIsInState(String descriptorState) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID oldDescriptorId = sharedStepsContext.getEServicesCommonContext().getOldDescriptorId();
        EServiceDescriptorState expectedState = EServiceDescriptorState.fromValue(descriptorState);

        pollDescriptorState(eServiceId, oldDescriptorId, expectedState);
    }

    @Then("il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale del singolo descrittore")
    @Then("l'annullamento dell'archiviazione manuale del vecchio descrittore è fallita")
    public void oldDescriptorHasArchivingScheduleWithDescriptorScope() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID oldDescriptorId = sharedStepsContext.getEServicesCommonContext().getOldDescriptorId();

        archivingScheduleVerifier.pollDescriptorArchivingSchedule(eServiceId, oldDescriptorId, ArchivingScope.DESCRIPTOR);
    }

    //Step specifico per i test relativi al cron job di archiviazione
    @Then("il descrittore con id {string} dell'e-service avente id {string} è stato correttamente archiviato tramite l'archiviazione manuale del singolo descrittore")
    public void descriptorHasPopulatedArchivingScheduleWithDescriptorScope(String descriptorId, String eServiceId) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID eServiceUUID = UUID.fromString(eServiceId);
        UUID descriptorUUID = UUID.fromString(descriptorId);

        // verifichiamo che l'attributo archivingSchedule sia valorizzato in tutti i suoi campi; sul solo campo scope controlliamo anche che il valore coincida con quello atteso
        archivingScheduleVerifier.pollDescriptorPopulatedArchivingSchedule(eServiceUUID, descriptorUUID, ArchivingScope.DESCRIPTOR);
    }

    //Step specifico per i test relativi al cron job di archiviazione
    @Then("il descrittore con id {string} dell'e-service avente id {string} NON è stato archiviato tramite archiviazione manuale")
    public void descriptorHasNoArchivingSchedule(String descriptorId, String eServiceId) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID eServiceUUID = UUID.fromString(eServiceId);
        UUID descriptorUUID = UUID.fromString(descriptorId);

        archivingScheduleVerifier.pollDescriptorWithoutArchivingSchedule(eServiceUUID, descriptorUUID);
    }

    @Then("l'archiviazione manuale del singolo descrittore è stata annullata con successo")
    @Then("il vecchio descrittore non è stato messo in archiviazione tramite l'archiviazione manuale del singolo descrittore")
    @Then("il vecchio descrittore non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service")
    public void oldDescriptorHasNoArchivingSchedule() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID oldDescriptorId = sharedStepsContext.getEServicesCommonContext().getOldDescriptorId();

        archivingScheduleVerifier.pollDescriptorWithoutArchivingSchedule(eServiceId, oldDescriptorId);
    }

    @And("il descrittore più recente non è stato messo in archiviazione tramite l'archiviazione manuale del singolo descrittore")
    @And("il descrittore più recente non è stato messo in archiviazione tramite l'archiviazione manuale dell'intero e-service")
    public void latestDescriptorHasNoArchivingSchedule() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();

        archivingScheduleVerifier.pollDescriptorWithoutArchivingSchedule(eServiceId, descriptorId);
    }

    private void pollDescriptorState(UUID eServiceId, UUID descriptorId, EServiceDescriptorState expectedState) {
        sharedStepsContext.getPollingService().makePolling(
                () -> clientTokenConfigurator.getEServiceClient().getEServiceDescriptor(eServiceId, descriptorId),
                descriptor -> descriptor != null && expectedState.equals(descriptor.getState()),
                "La vecchia versione dell'e-service non risulta in stato " + expectedState
        );
    }

    private EServiceDescriptorState expectedArchivingState(EServiceDescriptorState descriptorState) {
        return switch (descriptorState) {
            case DEPRECATED -> EServiceDescriptorState.ARCHIVING;
            case SUSPENDED -> EServiceDescriptorState.ARCHIVING_SUSPENDED;
            default -> throw new IllegalStateException(
                    "La vecchia versione dell'e-service non può essere portata in archiviazione dallo stato " + descriptorState
            );
        };
    }
}
