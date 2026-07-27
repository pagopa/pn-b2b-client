package it.pagopa.pn.interop.cucumber.steps.e_service_template.instance;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.ArchivingSchedule;
import it.pagopa.interop.generated.openapi.clients.bff.model.ArchivingScope;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceArchivingSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.GracePeriodDays;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EServiceTemplateInstanceArchivingSteps {
    private static final Duration STARTED_AT_TOLERANCE = Duration.ofSeconds(5);

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;
    private final EServiceTemplateInstanceUtility eServiceTemplateInstanceUtility;

    private OffsetDateTime archivingRequestTimestamp;
    private GracePeriodDays archivingGracePeriodDays;

    public EServiceTemplateInstanceArchivingSteps(
            ClientTokenConfigurator clientTokenConfigurator,
            SharedStepsContext sharedStepsContext
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.eServiceTemplateInstanceUtility = new EServiceTemplateInstanceUtility(sharedStepsContext);
    }

    @When("l'utente avvia il processo di archiviazione dell'e-service template instance {string} specificando la motivazione {string} e {gracePeriodDays} giorni di preavviso")
    public void scheduleEServiceTemplateInstanceArchiving(String eServiceId, String archivingReason, GracePeriodDays gracePeriodDays) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID resolvedEServiceId = eServiceTemplateInstanceUtility.resolveEServiceTemplateInstanceId(eServiceId);
        String resolvedArchivingReason = eServiceTemplateInstanceUtility.resolveArchivingReason(archivingReason);

        scheduleArchiveEService(resolvedEServiceId, resolvedArchivingReason, gracePeriodDays);
    }

    @When("l'utente avvia il processo di archiviazione dell'e-service template instance {string} specificando una motivazione di {int} caratteri e {gracePeriodDays} giorni di preavviso")
    public void scheduleEServiceTemplateInstanceArchivingWithReasonLength(String eServiceId, int archivingReasonLength, GracePeriodDays gracePeriodDays) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID resolvedEServiceId = eServiceTemplateInstanceUtility.resolveEServiceTemplateInstanceId(eServiceId);
        String archivingReason = RandomStringUtils.insecure().nextAlphanumeric(archivingReasonLength);

        scheduleArchiveEService(resolvedEServiceId, archivingReason, gracePeriodDays);
    }

    @Given("l'utente ha già avviato il processo di archiviazione dell'e-service template instance {string} specificando la motivazione {string} e {gracePeriodDays} giorni di preavviso")
    public void eServiceTemplateInstanceAlreadyInArchiving(String eServiceId, String archivingReason, GracePeriodDays gracePeriodDays) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID resolvedEServiceId = eServiceTemplateInstanceUtility.resolveEServiceTemplateInstanceId(eServiceId);
        String resolvedArchivingReason = eServiceTemplateInstanceUtility.resolveArchivingReason(archivingReason);

        Map<UUID, EServiceDescriptorState> expectedStates = getExpectedArchivingStates(resolvedEServiceId);

        scheduleArchiveEService(resolvedEServiceId, resolvedArchivingReason, gracePeriodDays);
        if (httpCallExecutor.getResponseStatus() == null || !httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            throw new IllegalStateException("L'avvio dell'archiviazione dell'e-service template instance non ha avuto successo");
        }

        expectedStates.forEach((descriptorId, expectedState) ->
                pollDescriptorStateAndArchivingSchedule(resolvedEServiceId, descriptorId, expectedState)
        );
    }

    @Then("la vecchia versione dell'e-service template instance è in stato {string}")
    public void oldEServiceTemplateInstanceVersionIsInState(String descriptorState) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID eServiceId = eServiceTemplateInstanceUtility.resolveEServiceTemplateInstanceId("%actual");
        UUID oldDescriptorId = eServiceTemplateInstanceUtility.resolveOldEServiceTemplateInstanceDescriptorId("%actual");
        EServiceDescriptorState expectedState = EServiceDescriptorState.fromValue(descriptorState);

        pollDescriptorState(eServiceId, oldDescriptorId, expectedState);
    }

    @Then("la versione più recente dell'e-service template instance è in stato {string}")
    public void latestEServiceTemplateInstanceVersionIsInState(String descriptorState) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID eServiceId = eServiceTemplateInstanceUtility.resolveEServiceTemplateInstanceId("%actual");
        UUID descriptorId = eServiceTemplateInstanceUtility.resolveLatestEServiceTemplateInstanceDescriptorId("%actual");
        EServiceDescriptorState expectedState = EServiceDescriptorState.fromValue(descriptorState);

        pollDescriptorState(eServiceId, descriptorId, expectedState);
    }

    @Then("il vecchio descrittore dell'e-service template instance è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service")
    @Then("il vecchio descrittore dell'e-service template instance è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service template instance")
    public void oldDescriptorHasArchivingScheduleWithEServiceScope() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID eServiceId = eServiceTemplateInstanceUtility.resolveEServiceTemplateInstanceId("%actual");
        UUID oldDescriptorId = eServiceTemplateInstanceUtility.resolveOldEServiceTemplateInstanceDescriptorId("%actual");

        pollDescriptorArchivingSchedule(eServiceId, oldDescriptorId, ArchivingScope.ESERVICE);
    }

    @Then("il descrittore più recente dell'e-service template instance è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service")
    @Then("il descrittore più recente dell'e-service template instance è stato correttamente messo in archiviazione tramite l'archiviazione manuale dell'intero e-service template instance")
    public void latestDescriptorHasArchivingScheduleWithEServiceScope() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID eServiceId = eServiceTemplateInstanceUtility.resolveEServiceTemplateInstanceId("%actual");
        UUID descriptorId = eServiceTemplateInstanceUtility.resolveLatestEServiceTemplateInstanceDescriptorId("%actual");

        pollDescriptorArchivingSchedule(eServiceId, descriptorId, ArchivingScope.ESERVICE);
    }

    private void scheduleArchiveEService(UUID eServiceId, String archivingReason, GracePeriodDays gracePeriodDays) {
        registerDescriptorArchivingRequestTimestamp(gracePeriodDays);
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient()
                        .scheduleArchiveEService(
                                eServiceId,
                                new EServiceArchivingSeed()
                                        .archivingReason(archivingReason)
                                        .gracePeriodDays(gracePeriodDays)
                        ),
                ResponseEntity::getStatusCode
        );
    }

    private void registerDescriptorArchivingRequestTimestamp(GracePeriodDays gracePeriodDays) {
        this.archivingRequestTimestamp = OffsetDateTime.now(ZoneOffset.UTC);
        this.archivingGracePeriodDays = gracePeriodDays;
    }

    private Map<UUID, EServiceDescriptorState> getExpectedArchivingStates(UUID eServiceId) {
        UUID descriptorId = eServiceTemplateInstanceUtility.resolveLatestEServiceTemplateInstanceDescriptorId("%actual");

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
                "Il descriptor " + descriptorId + " dell'e-service template instance non risulta in stato " + expectedState
        );
    }

    private void pollDescriptorStateAndArchivingSchedule(UUID eServiceId, UUID descriptorId, EServiceDescriptorState expectedState) {
        pollDescriptorState(eServiceId, descriptorId, expectedState);
        if (!EServiceDescriptorState.ARCHIVED.equals(expectedState)) {
            pollDescriptorArchivingSchedule(eServiceId, descriptorId, ArchivingScope.ESERVICE);
        }
    }

    private void pollDescriptorArchivingSchedule(UUID eServiceId, UUID descriptorId, ArchivingScope expectedScope) {
        sharedStepsContext.getPollingService().makePolling(
                () -> clientTokenConfigurator.getEServiceClient().getEServiceDescriptor(eServiceId, descriptorId),
                descriptor -> hasExpectedArchivingSchedule(descriptor, expectedScope),
                "Il descrittore dell'e-service template instance non contiene un archivingSchedule valido: "
                        + "scope, startedAt, archivableOn o gracePeriodDays assente o non corretto"
        );
    }

    private boolean hasExpectedArchivingSchedule(ProducerEServiceDescriptor descriptor, ArchivingScope expectedScope) {
        if (descriptor == null || descriptor.getArchivingSchedule() == null) {
            return false;
        }

        ArchivingSchedule archivingSchedule = descriptor.getArchivingSchedule();
        return expectedScope.equals(archivingSchedule.getScope())
                && isStartedAtWithinTolerance(archivingSchedule.getStartedAt())
                && hasExpectedArchivableOn(archivingSchedule.getArchivableOn())
                && hasExpectedGracePeriodDays(archivingSchedule.getGracePeriodDays());
    }

    private boolean hasExpectedGracePeriodDays(GracePeriodDays actualGracePeriodDays) {
        return archivingGracePeriodDays != null && archivingGracePeriodDays.equals(actualGracePeriodDays);
    }

    private boolean hasExpectedArchivableOn(String archivableOn) {
        if (archivableOn == null || archivableOn.isBlank()) {
            return false;
        }

        try {
            OffsetDateTime actualArchivableOn = OffsetDateTime.parse(archivableOn);
            OffsetDateTime expectedArchivableOn = calculateExpectedArchivableOn();
            return ZoneOffset.UTC.equals(actualArchivableOn.getOffset())
                    && expectedArchivableOn != null
                    && expectedArchivableOn.isEqual(actualArchivableOn);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private OffsetDateTime calculateExpectedArchivableOn() {
        if (archivingGracePeriodDays == null) {
            return null;
        }

        OffsetDateTime referenceTimestamp = archivingRequestTimestamp != null
                ? archivingRequestTimestamp
                : OffsetDateTime.now(ZoneOffset.UTC);
        return referenceTimestamp.toLocalDate()
                .plusDays(archivingGracePeriodDays.getValue() + 1L)
                .atStartOfDay()
                .atOffset(ZoneOffset.UTC);
    }

    private boolean isStartedAtWithinTolerance(String startedAt) {
        if (startedAt == null || startedAt.isBlank() || archivingRequestTimestamp == null) {
            return false;
        }

        try {
            OffsetDateTime actualStartedAt = OffsetDateTime.parse(startedAt);
            Duration delta = Duration.between(
                    archivingRequestTimestamp.toInstant(),
                    actualStartedAt.toInstant()
            ).abs();
            return ZoneOffset.UTC.equals(actualStartedAt.getOffset()) && delta.compareTo(STARTED_AT_TOLERANCE) <= 0;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private EServiceDescriptorState expectedArchivingState(EServiceDescriptorState descriptorState) {
        return switch (descriptorState) {
            case PUBLISHED, DEPRECATED, ARCHIVING -> EServiceDescriptorState.ARCHIVING;
            case SUSPENDED -> EServiceDescriptorState.ARCHIVING_SUSPENDED;
            case ARCHIVED -> EServiceDescriptorState.ARCHIVED;
            default -> throw new IllegalStateException(
                    "Il descriptor dell'e-service template instance non può essere portato in archiviazione dallo stato " + descriptorState
            );
        };
    }
}