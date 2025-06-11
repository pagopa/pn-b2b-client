package it.pagopa.interop.eservice.service;

import it.pagopa.interop.common.client.IClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptors;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

public interface IM2MEserviceDescriptorClient extends IClient<EServiceDescriptor, UUID> {

    @Data
    @Builder
    class EserviceDescriptorsListRequest {
        @NonNull private UUID eserviceId;
        @NonNull private Integer offset;
        @NonNull private Integer limit;
        private EServiceDescriptorState state;
    }

    EServiceDescriptor get(UUID eserviceId, UUID descriptorId);
    EServiceDescriptors getAll(EserviceDescriptorsListRequest eserviceDescriptorsListRequest);
    EServiceDescriptors getAll(UUID eserviceId);
}
