package it.pagopa.interop.eservice.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.*;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface IM2MEserviceClient extends SettableBearerToken {
    @Data
    @Builder
    class EserviceListRequest {
        @NonNull private Integer offset;
        @NonNull private Integer limit;
        private List<UUID> producerIds;
        private List<UUID> templateIds;
    }

    @Data
    @Builder
    class EserviceDescriptorsListRequest {
        @NonNull private UUID eserviceId;
        @NonNull private Integer offset;
        @NonNull private Integer limit;
        private EServiceDescriptorState state;
    }

    EService getEService(UUID eserviceId);
    EServices getEServices(EserviceListRequest eserviceListRequest);
    EServiceDescriptors getEserviceDescriptors(EserviceDescriptorsListRequest eserviceDescriptorsListRequest);
    EServiceDescriptor getEserviceDescriptor(UUID eserviceId, UUID descriptorId);
}
