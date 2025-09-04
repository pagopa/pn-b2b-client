package it.pagopa.interop.eservice.service;

import it.pagopa.interop.common.client.IClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.*;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface IM2MEserviceClient extends IClient<EService, UUID> {
    @Data
    @Builder
    class EserviceListRequest {
        @NonNull private Integer offset;
        @NonNull private Integer limit;
        private List<UUID> producerIds;
        private List<UUID> templateIds;
        String name;
        EServiceTechnology eServiceTechnology;
        EServiceMode eServiceMode;
        Boolean isSignalHubEnabled;
        Boolean isConsumerDelegable;
        Boolean isClientAccessDelegable;
    }

    EServices getAll(EserviceListRequest payload);
    EServiceDescriptor getDescriptor(UUID eserviceId, UUID descriptorId);
}
