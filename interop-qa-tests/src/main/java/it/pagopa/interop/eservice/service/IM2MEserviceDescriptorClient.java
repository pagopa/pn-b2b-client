package it.pagopa.interop.eservice.service;

import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.common.client.IClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorState;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.UUID;

public interface IM2MEserviceDescriptorClient extends IClient<EServiceDescriptor, Pair<UUID,UUID>> {

    @Data
    @Builder
    class EserviceDescriptorsListRequest {
        @NonNull private UUID eserviceId;
        @NonNull private Integer offset;
        @NonNull private Integer limit;
        private EServiceDescriptorState state;
    }

    EServiceDescriptor get(UUID eserviceId, UUID descriptorId);
    List<EServiceDescriptor> getAll(EserviceDescriptorsListRequest eserviceDescriptorsListRequest);
    List<EServiceDescriptor> getAll(UUID eserviceId);
}
