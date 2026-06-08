package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.ArchivingSchedule;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.catalog.utils.CatalogResolver;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.UUID;

public class DescriptorArchivingSteps {
    private static final int GRACE_PERIOD_ARCHIVING_ESERVICE = 1;
    private static final Duration STARTED_AT_TOLERANCE = Duration.ofSeconds(5);

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;
    private final CatalogResolver catalogResolver;
    private OffsetDateTime descriptorArchivingRequestTimestamp;

    public DescriptorArchivingSteps(
            ClientTokenConfigurator clientTokenConfigurator,
            SharedStepsContext sharedStepsContext
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.catalogResolver = new CatalogResolver(sharedStepsContext);
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
    }

    private void scheduleArchiveDescriptor(UUID eServiceId, UUID descriptorId) {
        descriptorArchivingRequestTimestamp = OffsetDateTime.now(ZoneOffset.UTC);
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
    public void oldDescriptorIsCorrectlyArchivedByManualDescriptorArchiving() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID oldDescriptorId = sharedStepsContext.getEServicesCommonContext().getOldDescriptorId();

        pollDescriptorArchivingSchedule(eServiceId, oldDescriptorId, "DESCRIPTOR");
    }

    @And("il vecchio descrittore non è stato messo in archiviazione tramite l'archiviazione manuale del singolo descrittore")
    public void oldDescriptorHasNotBeenManuallyArchived() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID oldDescriptorId = sharedStepsContext.getEServicesCommonContext().getOldDescriptorId();

        pollDescriptorWithoutArchivingSchedule(eServiceId, oldDescriptorId);
    }

    @And("il descrittore più recente non è stato messo in archiviazione tramite l'archiviazione manuale del singolo descrittore")
    public void latestDescriptorHasNotBeenManuallyArchived() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();

        pollDescriptorWithoutArchivingSchedule(eServiceId, descriptorId);
    }

    private void pollDescriptorState(UUID eServiceId, UUID descriptorId, EServiceDescriptorState expectedState) {
        sharedStepsContext.getPollingService().makePolling(
                () -> clientTokenConfigurator.getEServiceClient().getEServiceDescriptor(eServiceId, descriptorId),
                descriptor -> descriptor != null && expectedState.equals(descriptor.getState()),
                "La vecchia versione dell'e-service non risulta in stato " + expectedState
        );
    }

    private void pollDescriptorWithoutArchivingSchedule(UUID eServiceId, UUID descriptorId) {
        sharedStepsContext.getPollingService().makePolling(
                () -> clientTokenConfigurator.getEServiceClient().getEServiceDescriptor(eServiceId, descriptorId),
                descriptor -> descriptor != null && descriptor.getArchivingSchedule() == null,
            "Il descrittore dell'e-service contiene l'attributo inatteso archivingSchedule"
        );
    }

    private void pollDescriptorArchivingSchedule(UUID eServiceId, UUID descriptorId, String expectedScope) {
        sharedStepsContext.getPollingService().makePolling(
                () -> clientTokenConfigurator.getEServiceClient().getEServiceDescriptor(eServiceId, descriptorId),
                descriptor -> hasExpectedArchivingSchedule(descriptor, expectedScope),
                "Il vecchio descrittore dell'e-service non contiene un archivingSchedule valido: "
                    + "scope, startedAt o archivableOn assente o non corretto"
        );
    }

    private boolean hasExpectedArchivingSchedule(ProducerEServiceDescriptor descriptor, String expectedScope) {
        if (descriptor == null || descriptor.getArchivingSchedule() == null) {
            return false;
        }

        ArchivingSchedule archivingSchedule = descriptor.getArchivingSchedule();
        return archivingSchedule.getScope() != null
                && expectedScope.equals(archivingSchedule.getScope().getValue())
                && isStartedAtWithinTolerance(archivingSchedule.getStartedAt())
                && hasExpectedArchivableOn(archivingSchedule.getArchivableOn());
    }

    private boolean hasExpectedArchivableOn(String archivableOn) {
        if (archivableOn == null || archivableOn.isBlank()) {
            return false;
        }

        try {
            OffsetDateTime actualArchivableOn = OffsetDateTime.parse(archivableOn);
            return ZoneOffset.UTC.equals(actualArchivableOn.getOffset())
                    && calculateExpectedArchivableOn().isEqual(actualArchivableOn);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private OffsetDateTime calculateExpectedArchivableOn() {
        OffsetDateTime referenceTimestamp = descriptorArchivingRequestTimestamp != null
                ? descriptorArchivingRequestTimestamp
                : OffsetDateTime.now(ZoneOffset.UTC);
        return referenceTimestamp.toLocalDate()
                .plusDays(GRACE_PERIOD_ARCHIVING_ESERVICE + 1L)
                .atStartOfDay()
                .atOffset(ZoneOffset.UTC);
    }

    private boolean isStartedAtWithinTolerance(String startedAt) {
        if (startedAt == null || startedAt.isBlank() || descriptorArchivingRequestTimestamp == null) {
            return false;
        }

        try {
            OffsetDateTime actualStartedAt = OffsetDateTime.parse(startedAt);
            Duration delta = Duration.between(
                    descriptorArchivingRequestTimestamp.toInstant(),
                    actualStartedAt.toInstant()
            ).abs();
            return ZoneOffset.UTC.equals(actualStartedAt.getOffset()) && delta.compareTo(STARTED_AT_TOLERANCE) <= 0;
        } catch (DateTimeParseException e) {
            return false;
        }
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
