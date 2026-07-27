package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.ArchivingScope;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceArchivingSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.GracePeriodDays;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.catalog.utils.CatalogResolver;
import it.pagopa.pn.interop.cucumber.steps.catalog.utils.DescriptorArchivingScheduleVerifier;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EServiceArchivingSteps {

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;
    private final CatalogResolver catalogResolver;
    private final DescriptorArchivingScheduleVerifier archivingScheduleVerifier;

    public EServiceArchivingSteps(
            ClientTokenConfigurator clientTokenConfigurator,
            SharedStepsContext sharedStepsContext
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.catalogResolver = new CatalogResolver(sharedStepsContext);
        this.archivingScheduleVerifier = new DescriptorArchivingScheduleVerifier(clientTokenConfigurator, sharedStepsContext);
    }

    @When("l'utente avvia il processo di archiviazione dell'e-service con id {string} e specificando la motivazione {string}")
    public void scheduleEServiceArchiving(String eServiceId, String archivingReason) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);
        String resolvedArchivingReason = catalogResolver.resolveArchivingReason(archivingReason);

        scheduleArchiveEService(resolvedEServiceId, resolvedArchivingReason);
    }

    @When("l'utente avvia il processo di archiviazione dell'e-service con id {string} e specificando la motivazione composta da {int} caratteri")
    public void scheduleEServiceArchivingWithReasonLength(String eServiceId, int archivingReasonLength) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);
        String archivingReason = RandomStringUtils.insecure().nextAlphanumeric(archivingReasonLength);

        scheduleArchiveEService(resolvedEServiceId, archivingReason);
    }

    @When("l'utente annulla il processo di archiviazione dell'e-service con id {string}")
    public void cancelEServiceArchiving(String eServiceId) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);

        cancelArchiveEService(resolvedEServiceId);
    }

    @Given("l'utente ha già avviato il processo di archiviazione dell'e-service con id {string} e specificando la motivazione {string}")
    public void eServiceAlreadyInArchiving(String eServiceId, String archivingReason) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);
        String resolvedArchivingReason = catalogResolver.resolveArchivingReason(archivingReason);

        Map<UUID, EServiceDescriptorState> expectedStates = getExpectedArchivingStates(resolvedEServiceId);

        scheduleArchiveEService(resolvedEServiceId, resolvedArchivingReason);
        if (httpCallExecutor.getResponseStatus() == null || !httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            throw new IllegalStateException("L'avvio dell'archiviazione dell'e-service non ha avuto successo");
        }

        expectedStates.forEach((descriptorId, expectedState) ->
                pollDescriptorStateAndArchivingSchedule(resolvedEServiceId, descriptorId, expectedState)
        );
    }

    @Then("il vecchio descrittore è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service")
    @Then("l'annullamento dell'archiviazione manuale dell'intero e-service sul vecchio descrittore, è fallita")
    public void oldDescriptorHasArchivingScheduleWithEServiceScope() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID oldDescriptorId = sharedStepsContext.getEServicesCommonContext().getOldDescriptorId();

        archivingScheduleVerifier.pollDescriptorArchivingSchedule(eServiceId, oldDescriptorId, ArchivingScope.ESERVICE);
    }

    @Then("il descrittore più recente è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service")
    @Then("l'annullamento dell'archiviazione manuale dell'intero e-service sul descrittore più recente, è fallita")
    public void latestDescriptorHasArchivingScheduleWithEServiceScope() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();

        archivingScheduleVerifier.pollDescriptorArchivingSchedule(eServiceId, descriptorId, ArchivingScope.ESERVICE);
    }

    //Step specifico per i test relativi al cron job di archiviazione
    @Then("il descrittore con id {string} dell'e-service avente id {string} è stato correttamente archiviato tramite l'archiviazione manuale dell'intero e-service")
    @Then("il descrittore con id {string} dell'e-service avente id {string} è in fase di archiviazione tramite l'archiviazione manuale dell'intero e-service")
    public void descriptorHasPopulatedArchivingScheduleWithEServiceScope(String descriptorId, String eServiceId) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID eServiceUUID = UUID.fromString(eServiceId);
        UUID descriptorUUID = UUID.fromString(descriptorId);

        // verifichiamo che l'attributo archivingSchedule sia valorizzato in tutti i suoi campi; sul solo campo scope controlliamo anche che il valore coincida con quello atteso
        archivingScheduleVerifier.pollDescriptorPopulatedArchivingSchedule(eServiceUUID, descriptorUUID, ArchivingScope.ESERVICE);
    }

    private void scheduleArchiveEService(UUID eServiceId, String archivingReason) {
        archivingScheduleVerifier.registerDescriptorArchivingRequestTimestamp();
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient()
                        .scheduleArchiveEService(
                                eServiceId,
                            new EServiceArchivingSeed()
                                .archivingReason(archivingReason)
                                .gracePeriodDays(GracePeriodDays.NUMBER_60)
                        ),
                ResponseEntity::getStatusCode
        );
    }

    private Map<UUID, EServiceDescriptorState> getExpectedArchivingStates(UUID eServiceId) {
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();

        ProducerEServiceDescriptor producerEServiceDescriptor = clientTokenConfigurator.getEServiceClient()
                .getEServiceDescriptor(eServiceId, descriptorId);
        List<CompactDescriptor> descriptors = producerEServiceDescriptor.getEservice().getDescriptors();

        Map<UUID, EServiceDescriptorState> expectedStates = new LinkedHashMap<>();
        descriptors.forEach(descriptor -> {
            EServiceDescriptorState expectedState = expectedArchivingState(descriptor.getState());
            expectedStates.put(descriptor.getId(), expectedState);
        });

        EServiceDescriptorState currentDescriptorExpectedState = expectedArchivingState(producerEServiceDescriptor.getState());
        expectedStates.putIfAbsent(
                producerEServiceDescriptor.getId(),
                currentDescriptorExpectedState
        );
        return expectedStates;
    }

    private void pollDescriptorState(UUID eServiceId, UUID descriptorId, EServiceDescriptorState expectedState) {
        sharedStepsContext.getPollingService().makePolling(
                () -> clientTokenConfigurator.getEServiceClient().getEServiceDescriptor(eServiceId, descriptorId),
                descriptor -> descriptor != null && expectedState.equals(descriptor.getState()),
                "Il descriptor " + descriptorId + " dell'e-service non risulta in stato " + expectedState
        );
    }

    private void pollDescriptorStateAndArchivingSchedule(UUID eServiceId, UUID descriptorId, EServiceDescriptorState expectedState) {
        pollDescriptorState(eServiceId, descriptorId, expectedState);
        if (!EServiceDescriptorState.ARCHIVED.equals(expectedState)) {
            archivingScheduleVerifier.pollDescriptorArchivingSchedule(eServiceId, descriptorId, ArchivingScope.ESERVICE);
        }
    }

    private void cancelArchiveEService(UUID eServiceId) {
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient()
                        .cancelEServiceArchiving(eServiceId),
                ResponseEntity::getStatusCode
        );
    }

    private EServiceDescriptorState expectedArchivingState(EServiceDescriptorState descriptorState) {
        return switch (descriptorState) {
            case PUBLISHED, DEPRECATED, ARCHIVING -> EServiceDescriptorState.ARCHIVING;
            case SUSPENDED -> EServiceDescriptorState.ARCHIVING_SUSPENDED;
            case ARCHIVED -> EServiceDescriptorState.ARCHIVED;
            default -> throw new IllegalStateException(
                    "Il descriptor dell'e-service non può essere portato in archiviazione dallo stato " + descriptorState
            );
        };
    }
}
