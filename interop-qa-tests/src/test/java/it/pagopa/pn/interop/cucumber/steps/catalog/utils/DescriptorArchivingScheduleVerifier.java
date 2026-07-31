package it.pagopa.pn.interop.cucumber.steps.catalog.utils;

import it.pagopa.interop.generated.openapi.clients.bff.model.ArchivingSchedule;
import it.pagopa.interop.generated.openapi.clients.bff.model.ArchivingScope;
import it.pagopa.interop.generated.openapi.clients.bff.model.GracePeriodDays;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.UUID;

public class DescriptorArchivingScheduleVerifier {
    private static final Duration STARTED_AT_TOLERANCE = Duration.ofSeconds(5);

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;

    public DescriptorArchivingScheduleVerifier(
            ClientTokenConfigurator clientTokenConfigurator,
            SharedStepsContext sharedStepsContext
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
    }

    public void registerDescriptorArchivingRequestTimestamp(GracePeriodDays gracePeriodDays) {
        sharedStepsContext.getEServicesCommonContext()
                .setDescriptorArchivingRequestTimestamp(OffsetDateTime.now(ZoneOffset.UTC));
        sharedStepsContext.getEServicesCommonContext()
                .setDescriptorArchivingGracePeriodDays(gracePeriodDays);
    }

    public void registerEServiceArchivingRequestTimestamp(GracePeriodDays gracePeriodDays) {
        sharedStepsContext.getEServicesCommonContext()
                .setEServiceArchivingRequestTimestamp(OffsetDateTime.now(ZoneOffset.UTC));
        sharedStepsContext.getEServicesCommonContext()
                .setEServiceArchivingGracePeriodDays(gracePeriodDays);
    }

    public void pollDescriptorWithoutArchivingSchedule(UUID eServiceId, UUID descriptorId) {
        sharedStepsContext.getPollingService().makePolling(
                () -> clientTokenConfigurator.getEServiceClient().getEServiceDescriptor(eServiceId, descriptorId),
                descriptor -> descriptor != null && descriptor.getArchivingSchedule() == null,
                "Il descrittore dell'e-service contiene l'attributo inatteso archivingSchedule"
        );
    }

    public void pollDescriptorArchivingSchedule(UUID eServiceId, UUID descriptorId, ArchivingScope expectedScope) {
        sharedStepsContext.getPollingService().makePolling(
                () -> clientTokenConfigurator.getEServiceClient().getEServiceDescriptor(eServiceId, descriptorId),
                descriptor -> hasExpectedArchivingSchedule(descriptor, expectedScope),
                "Il descrittore dell'e-service non contiene un archivingSchedule valido: "
                        + "scope, startedAt, archivableOn o gracePeriodDays assente o non corretto"
        );
    }

    public void pollDescriptorPopulatedArchivingSchedule(UUID eServiceId, UUID descriptorId, ArchivingScope expectedScope) {
        sharedStepsContext.getPollingService().makePolling(
                () -> clientTokenConfigurator.getEServiceClient().getEServiceDescriptor(eServiceId, descriptorId),
                descriptor -> hasPopulatedArchivingSchedule(descriptor, expectedScope),
                "Il descrittore dell'e-service non contiene un archivingSchedule valorizzato: "
                        + "scope, startedAt, archivableOn o gracePeriodDays assente o vuoto"
        );
    }

    public void pollDescriptorWithSameArchivingSchedule(UUID eServiceId, UUID descriptorId, UUID referenceDescriptorId, ArchivingScope expectedScope) {
        sharedStepsContext.getPollingService().makePolling(
                () -> clientTokenConfigurator.getEServiceClient().getEServiceDescriptor(eServiceId, descriptorId),
                descriptor -> hasSameArchivingSchedule(
                        descriptor,
                        clientTokenConfigurator.getEServiceClient().getEServiceDescriptor(eServiceId, referenceDescriptorId),
                        expectedScope
                ),
                "Il descrittore dell'e-service non contiene gli stessi dati di archiviazione del descrittore di riferimento"
        );
    }

    private boolean hasExpectedArchivingSchedule(ProducerEServiceDescriptor descriptor, ArchivingScope expectedScope) {
        if (descriptor == null || descriptor.getArchivingSchedule() == null) {
            return false;
        }

        ArchivingSchedule archivingSchedule = descriptor.getArchivingSchedule();
        return expectedScope.equals(archivingSchedule.getScope())
                && isStartedAtWithinTolerance(archivingSchedule.getStartedAt(), expectedScope)
                && hasExpectedArchivableOn(archivingSchedule.getArchivableOn(), expectedScope)
                && hasExpectedGracePeriodDays(archivingSchedule.getGracePeriodDays(), expectedScope);
    }

    private boolean hasPopulatedArchivingSchedule(ProducerEServiceDescriptor descriptor, ArchivingScope expectedScope) {
        if (descriptor == null || descriptor.getArchivingSchedule() == null) {
            return false;
        }

        ArchivingSchedule archivingSchedule = descriptor.getArchivingSchedule();
        return expectedScope.equals(archivingSchedule.getScope())
                && isPopulated(archivingSchedule.getStartedAt())
                && isPopulated(archivingSchedule.getArchivableOn())
                && archivingSchedule.getGracePeriodDays() != null;
    }

    private boolean hasSameArchivingSchedule(
            ProducerEServiceDescriptor descriptor,
            ProducerEServiceDescriptor referenceDescriptor,
            ArchivingScope expectedScope
    ) {
        if (descriptor == null || referenceDescriptor == null
                || descriptor.getArchivingSchedule() == null
                || referenceDescriptor.getArchivingSchedule() == null) {
            return false;
        }

        ArchivingSchedule archivingSchedule = descriptor.getArchivingSchedule();
        ArchivingSchedule referenceArchivingSchedule = referenceDescriptor.getArchivingSchedule();
        return expectedScope.equals(archivingSchedule.getScope())
                && expectedScope.equals(referenceArchivingSchedule.getScope())
                && Objects.equals(archivingSchedule.getStartedAt(), referenceArchivingSchedule.getStartedAt())
                && Objects.equals(archivingSchedule.getArchivableOn(), referenceArchivingSchedule.getArchivableOn())
                && Objects.equals(archivingSchedule.getGracePeriodDays(), referenceArchivingSchedule.getGracePeriodDays());
    }

    private boolean isPopulated(String value) {
        return value != null && !value.isBlank();
    }

    private boolean hasExpectedGracePeriodDays(GracePeriodDays actualGracePeriodDays, ArchivingScope expectedScope) {
        GracePeriodDays expectedGracePeriodDays = getExpectedGracePeriodDays(expectedScope);
        return expectedGracePeriodDays != null && expectedGracePeriodDays.equals(actualGracePeriodDays);
    }

    private boolean hasExpectedArchivableOn(String archivableOn, ArchivingScope expectedScope) {
        if (archivableOn == null || archivableOn.isBlank()) {
            return false;
        }

        try {
            OffsetDateTime actualArchivableOn = OffsetDateTime.parse(archivableOn);
            OffsetDateTime expectedArchivableOn = calculateExpectedArchivableOn(expectedScope);
            return ZoneOffset.UTC.equals(actualArchivableOn.getOffset())
                && expectedArchivableOn != null
                && expectedArchivableOn.isEqual(actualArchivableOn);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private OffsetDateTime calculateExpectedArchivableOn(ArchivingScope expectedScope) {
        GracePeriodDays gracePeriodDays = getExpectedGracePeriodDays(expectedScope);
        if (gracePeriodDays == null) {
            return null;
        }

        OffsetDateTime archivingRequestTimestamp = getExpectedRequestTimestamp(expectedScope);
        OffsetDateTime referenceTimestamp = archivingRequestTimestamp != null
                ? archivingRequestTimestamp
                : OffsetDateTime.now(ZoneOffset.UTC);
        return referenceTimestamp.toLocalDate()
                .plusDays(gracePeriodDays.getValue() + 1L)
                .atStartOfDay()
                .atOffset(ZoneOffset.UTC);
    }

    private boolean isStartedAtWithinTolerance(String startedAt, ArchivingScope expectedScope) {
        OffsetDateTime archivingRequestTimestamp = getExpectedRequestTimestamp(expectedScope);
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

    private OffsetDateTime getExpectedRequestTimestamp(ArchivingScope expectedScope) {
        return switch (expectedScope) {
            case DESCRIPTOR -> sharedStepsContext.getEServicesCommonContext()
                    .getDescriptorArchivingRequestTimestamp();
            case ESERVICE -> sharedStepsContext.getEServicesCommonContext()
                    .getEServiceArchivingRequestTimestamp();
        };
    }

    private GracePeriodDays getExpectedGracePeriodDays(ArchivingScope expectedScope) {
        return switch (expectedScope) {
            case DESCRIPTOR -> sharedStepsContext.getEServicesCommonContext()
                    .getDescriptorArchivingGracePeriodDays();
            case ESERVICE -> sharedStepsContext.getEServicesCommonContext()
                    .getEServiceArchivingGracePeriodDays();
        };
    }
}
