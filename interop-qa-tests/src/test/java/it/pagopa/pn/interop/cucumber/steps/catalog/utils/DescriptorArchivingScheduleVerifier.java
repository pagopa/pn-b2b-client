package it.pagopa.pn.interop.cucumber.steps.catalog.utils;

import it.pagopa.interop.generated.openapi.clients.bff.model.ArchivingSchedule;
import it.pagopa.interop.generated.openapi.clients.bff.model.ArchivingScope;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.UUID;

public class DescriptorArchivingScheduleVerifier {
    private static final int GRACE_PERIOD_ARCHIVING_ESERVICE = 1;
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

    public void registerDescriptorArchivingRequestTimestamp() {
        sharedStepsContext.getEServicesCommonContext()
                .setDescriptorArchivingRequestTimestamp(OffsetDateTime.now(ZoneOffset.UTC));
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
                        + "scope, startedAt o archivableOn assente o non corretto"
        );
    }

    public void pollDescriptorPopulatedArchivingSchedule(UUID eServiceId, UUID descriptorId, ArchivingScope expectedScope) {
        sharedStepsContext.getPollingService().makePolling(
                () -> clientTokenConfigurator.getEServiceClient().getEServiceDescriptor(eServiceId, descriptorId),
                descriptor -> hasPopulatedArchivingSchedule(descriptor, expectedScope),
                "Il descrittore dell'e-service non contiene un archivingSchedule valorizzato: "
                        + "scope, startedAt o archivableOn assente o vuoto"
        );
    }

    private boolean hasExpectedArchivingSchedule(ProducerEServiceDescriptor descriptor, ArchivingScope expectedScope) {
        if (descriptor == null || descriptor.getArchivingSchedule() == null) {
            return false;
        }

        ArchivingSchedule archivingSchedule = descriptor.getArchivingSchedule();
        return expectedScope.equals(archivingSchedule.getScope())
                && isStartedAtWithinTolerance(archivingSchedule.getStartedAt())
                && hasExpectedArchivableOn(archivingSchedule.getArchivableOn());
    }

    private boolean hasPopulatedArchivingSchedule(ProducerEServiceDescriptor descriptor, ArchivingScope expectedScope) {
        if (descriptor == null || descriptor.getArchivingSchedule() == null) {
            return false;
        }

        ArchivingSchedule archivingSchedule = descriptor.getArchivingSchedule();
        return expectedScope.equals(archivingSchedule.getScope())
                && isPopulated(archivingSchedule.getStartedAt())
                && isPopulated(archivingSchedule.getArchivableOn());
    }

    private boolean isPopulated(String value) {
        return value != null && !value.isBlank();
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
        OffsetDateTime descriptorArchivingRequestTimestamp = sharedStepsContext.getEServicesCommonContext()
                .getDescriptorArchivingRequestTimestamp();
        OffsetDateTime referenceTimestamp = descriptorArchivingRequestTimestamp != null
                ? descriptorArchivingRequestTimestamp
                : OffsetDateTime.now(ZoneOffset.UTC);
        return referenceTimestamp.toLocalDate()
                .plusDays(GRACE_PERIOD_ARCHIVING_ESERVICE + 1L)
                .atStartOfDay()
                .atOffset(ZoneOffset.UTC);
    }

    private boolean isStartedAtWithinTolerance(String startedAt) {
        OffsetDateTime descriptorArchivingRequestTimestamp = sharedStepsContext.getEServicesCommonContext()
                .getDescriptorArchivingRequestTimestamp();
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
}