package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.eservices.utils;

import it.pagopa.interop.eservice.service.IM2MV3EserviceClient;
import it.pagopa.interop.eservice.service.IM2MV3EserviceDescriptorClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DelegatedDescriptorArchivingRequest;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DelegatedEServiceArchivingRequest;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class M2MV3DelegatedArchivingRequestVerifier {
    private final IM2MV3EserviceClient eServiceClient;
    private final IM2MV3EserviceDescriptorClient descriptorClient;
    private final SharedStepsContext sharedStepsContext;

    public M2MV3DelegatedArchivingRequestVerifier(
            IM2MV3EserviceClient eServiceClient,
            IM2MV3EserviceDescriptorClient descriptorClient,
            SharedStepsContext sharedStepsContext
    ) {
        this.eServiceClient = eServiceClient;
        this.descriptorClient = descriptorClient;
        this.sharedStepsContext = sharedStepsContext;
    }

    public void pollEServiceDelegationArchivingRequestsHistory(UUID eServiceId, Map<RequestState, Integer> expectedCounts) {
        sharedStepsContext.getPollingService().makePolling(
                () -> eServiceClient.get(eServiceId),
                eService -> eService != null
                        && matchesEServiceHistory(eService.getDelegatedArchivingRequest(), expectedCounts),
                "Lo storico M2M v3 delle richieste di archiviazione dell'e-service non coincide con quello atteso"
        );
    }

    public void pollDescriptorDelegationArchivingRequestsHistory(
            UUID eServiceId,
            UUID descriptorId,
            Map<RequestState, Integer> expectedCounts
    ) {
        sharedStepsContext.getPollingService().makePolling(
                () -> descriptorClient.getCompleteResource(eServiceId, descriptorId),
                descriptor -> descriptor != null
                        && matchesDescriptorHistory(descriptor.getDelegatedArchivingRequest(), expectedCounts),
                "Lo storico M2M v3 delle richieste di archiviazione del descrittore non coincide con quello atteso"
        );
    }

    private boolean matchesEServiceHistory(
            List<DelegatedEServiceArchivingRequest> history,
            Map<RequestState, Integer> expectedCounts
    ) {
        List<DelegatedEServiceArchivingRequest> requests = history == null ? List.of() : history;
        if (requests.size() != expectedTotal(expectedCounts)) {
            return false;
        }

        Map<RequestState, Integer> actualCounts = emptyCounts();
        for (DelegatedEServiceArchivingRequest request : requests) {
            if (request == null
                    || request.getRequestedAt() == null
                    || request.getRequesterId() == null
                    || request.getGracePeriodDays() == null
                    || request.getArchivingReason() == null) {
                return false;
            }

            RequestState state = resolveState(
                    request.getAcceptedAt(),
                    request.getRejectedAt(),
                    request.getRejectionReason()
            );
            if (state == null) {
                return false;
            }
            Integer currentCount = actualCounts.get(state);
            actualCounts.put(state, currentCount + 1);
        }

        return actualCounts.equals(normalizeCounts(expectedCounts));
    }

    private boolean matchesDescriptorHistory(
            List<DelegatedDescriptorArchivingRequest> history,
            Map<RequestState, Integer> expectedCounts
    ) {
        List<DelegatedDescriptorArchivingRequest> requests = history == null ? List.of() : history;
        if (requests.size() != expectedTotal(expectedCounts)) {
            return false;
        }

        Map<RequestState, Integer> actualCounts = emptyCounts();
        for (DelegatedDescriptorArchivingRequest request : requests) {
            if (request == null
                    || request.getRequestedAt() == null
                    || request.getRequesterId() == null
                    || request.getGracePeriodDays() == null) {
                return false;
            }

            RequestState state = resolveState(
                    request.getAcceptedAt(),
                    request.getRejectedAt(),
                    request.getRejectionReason()
            );
            if (state == null) {
                return false;
            }
            Integer currentCount = actualCounts.get(state);
            actualCounts.put(state, currentCount + 1);
        }

        return actualCounts.equals(normalizeCounts(expectedCounts));
    }

    private RequestState resolveState(String acceptedAt, String rejectedAt, String rejectionReason) {
        if (acceptedAt == null && rejectedAt == null && rejectionReason == null) {
            return RequestState.PENDING;
        }
        if (acceptedAt != null && rejectedAt == null && rejectionReason == null) {
            return RequestState.ACCEPTED;
        }
        if (acceptedAt == null && rejectedAt != null && rejectionReason != null) {
            return RequestState.REJECTED;
        }
        return null;
    }

    private int expectedTotal(Map<RequestState, Integer> expectedCounts) {
        return expectedCounts.values().stream().mapToInt(Integer::intValue).sum();
    }

    private Map<RequestState, Integer> normalizeCounts(Map<RequestState, Integer> counts) {
        Map<RequestState, Integer> normalized = emptyCounts();
        normalized.putAll(counts);
        return normalized;
    }

    private Map<RequestState, Integer> emptyCounts() {
        Map<RequestState, Integer> counts = new EnumMap<>(RequestState.class);
        for (RequestState state : RequestState.values()) {
            counts.put(state, 0);
        }
        return counts;
    }

    public enum RequestState {
        PENDING,
        ACCEPTED,
        REJECTED
    }
}