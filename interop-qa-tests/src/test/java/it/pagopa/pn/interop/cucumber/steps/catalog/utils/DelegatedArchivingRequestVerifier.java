package it.pagopa.pn.interop.cucumber.steps.catalog.utils;

import it.pagopa.interop.generated.openapi.clients.bff.model.DelegatedArchivingRequest;
import it.pagopa.interop.generated.openapi.clients.bff.model.GracePeriodDays;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext.ExpectedDelegatedArchivingRequest;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.UUID;

public class DelegatedArchivingRequestVerifier {
    private static final Duration REQUESTED_AT_TOLERANCE = Duration.ofSeconds(3);

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;

    public DelegatedArchivingRequestVerifier(
            ClientTokenConfigurator clientTokenConfigurator,
            SharedStepsContext sharedStepsContext
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
    }

    public void registerEServiceArchivingRequest(
            GracePeriodDays gracePeriodDays,
            String archivingReason
    ) {
        ExpectedDelegatedArchivingRequest expectedRequest = new ExpectedDelegatedArchivingRequest();
        expectedRequest.setRequestedAt(OffsetDateTime.now(ZoneOffset.UTC));
        expectedRequest.setGracePeriodDays(gracePeriodDays);
        expectedRequest.setArchivingReason(archivingReason);
        sharedStepsContext.getEServicesCommonContext()
                .setExpectedDelegatedArchivingRequest(expectedRequest);
    }

    public void registerDescriptorArchivingRequest(
            UUID descriptorId,
            GracePeriodDays gracePeriodDays
    ) {
        ExpectedDelegatedArchivingRequest expectedRequest = new ExpectedDelegatedArchivingRequest();
        expectedRequest.setRequestedAt(OffsetDateTime.now(ZoneOffset.UTC));
        expectedRequest.setGracePeriodDays(gracePeriodDays);
        expectedRequest.setDescriptorId(Objects.requireNonNull(
                descriptorId,
                "L'ID del vecchio descrittore è obbligatorio"
        ));
        sharedStepsContext.getEServicesCommonContext()
                .setExpectedDelegatedArchivingRequest(expectedRequest);
    }

    public void pollPendingEServiceArchivingRequest(UUID eServiceId) {
        UUID expectedRequesterId = sharedStepsContext.getDelegationCommonContext().getDelegateId();
        ExpectedDelegatedArchivingRequest expectedRequest = Objects.requireNonNull(
                sharedStepsContext.getEServicesCommonContext().getExpectedDelegatedArchivingRequest(),
                "Nessuna richiesta di archiviazione è stata registrata"
        );

        UUID latestDescriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        sharedStepsContext.getPollingService().makePolling(
                () -> getDelegatedArchivingRequest(eServiceId, latestDescriptorId),
                request -> isExpectedPendingEServiceRequest(request, expectedRequest, expectedRequesterId),
                "L'e-service non contiene la richiesta di archiviazione in stato pending attesa"
        );
    }

    public void pollPendingDescriptorArchivingRequest(UUID eServiceId) {
        UUID expectedRequesterId = sharedStepsContext.getDelegationCommonContext().getDelegateId();
        ExpectedDelegatedArchivingRequest expectedRequest = Objects.requireNonNull(
                sharedStepsContext.getEServicesCommonContext().getExpectedDelegatedArchivingRequest(),
                "Nessuna richiesta di archiviazione è stata registrata"
        );

        sharedStepsContext.getPollingService().makePolling(
                () -> getDelegatedArchivingRequest(eServiceId, expectedRequest.getDescriptorId()),
                request -> isExpectedPendingDescriptorRequest(request, expectedRequest, expectedRequesterId),
                "Il descrittore non contiene la richiesta di archiviazione in stato pending attesa"
        );
    }

    private DelegatedArchivingRequest getDelegatedArchivingRequest(
            UUID eServiceId,
            UUID descriptorId
    ) {
        ProducerEServiceDescriptor descriptor = clientTokenConfigurator.getEServiceClient()
                .getEServiceDescriptor(eServiceId, descriptorId);
        return descriptor == null || descriptor.getEservice() == null
                ? null
                : descriptor.getEservice().getDelegatedArchivingRequest();
    }

    private boolean isExpectedPendingEServiceRequest(
            DelegatedArchivingRequest request,
            ExpectedDelegatedArchivingRequest expectedRequest,
            UUID expectedRequesterId
    ) {
        return hasExpectedPendingFields(request, expectedRequest, expectedRequesterId)
                && Objects.equals(expectedRequest.getArchivingReason(), request.getArchivingReason());
    }

    private boolean isExpectedPendingDescriptorRequest(
            DelegatedArchivingRequest request,
            ExpectedDelegatedArchivingRequest expectedRequest,
            UUID expectedRequesterId
    ) {
        return hasExpectedPendingFields(request, expectedRequest, expectedRequesterId)
                && request.getDescriptorId() != null
                && Objects.equals(expectedRequest.getDescriptorId(), request.getDescriptorId())
                && request.getArchivingReason() == null;
    }

    private boolean hasExpectedPendingFields(
            DelegatedArchivingRequest request,
            ExpectedDelegatedArchivingRequest expectedRequest,
            UUID expectedRequesterId
    ) {
        return request != null
                && isRequestedAtWithinTolerance(request.getRequestedAt(), expectedRequest.getRequestedAt())
                && Objects.equals(expectedRequesterId, request.getRequesterId())
                && Objects.equals(expectedRequest.getGracePeriodDays(), request.getGracePeriodDays())
                && request.getAcceptedAt() == null
                && request.getRejectedAt() == null
                && request.getRejectionReason() == null;
    }

    private boolean isRequestedAtWithinTolerance(String requestedAt, OffsetDateTime expectedRequestedAt) {
        if (requestedAt == null || requestedAt.isBlank() || expectedRequestedAt == null) {
            return false;
        }

        try {
            OffsetDateTime actualRequestedAt = OffsetDateTime.parse(requestedAt);
            Duration delta = Duration.between(
                    expectedRequestedAt.toInstant(),
                    actualRequestedAt.toInstant()
            ).abs();
            return ZoneOffset.UTC.equals(actualRequestedAt.getOffset())
                    && delta.compareTo(REQUESTED_AT_TOLERANCE) <= 0;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}