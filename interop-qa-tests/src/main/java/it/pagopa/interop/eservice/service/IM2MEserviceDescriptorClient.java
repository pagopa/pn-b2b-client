package it.pagopa.interop.eservice.service;

import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.common.client.IClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementApprovalPolicy;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Documents;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.FileDownloadMultipart;
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

    @Data
    @Builder
    class EServiceDescriptorPatchRequest {
        private String description;
        private List<String> audience;
        private Integer voucherLifespan;
        private Integer dailyCallsPerConsumer;
        private Integer dailyCallsTotal;
        private AgreementApprovalPolicy agreementApprovalPolicy;
    }

    /* TODO 15/09/2025: modella un DTO al momento assente nella specifica OpenAPI. Potrebbero
    *   rendersi necessari adattamenti di qualche tipo a seguito del rilascio. */
    @Data
    @Builder
    class EServiceDescriptorQuotasPatchRequest {
        private Integer dailyCallsPerConsumer;
        private Integer dailyCallsTotal;
    }

    EServiceDescriptor get(UUID eserviceId, UUID descriptorId);
    it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor getDescriptor(UUID eserviceId, UUID descriptorId);
    it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor getCompleteResource(UUID eserviceId, UUID descriptorId);
    List<EServiceDescriptor> getAll(EserviceDescriptorsListRequest eserviceDescriptorsListRequest);
    List<EServiceDescriptor> getAll(UUID eserviceId);
    void deleteInterface(UUID eServiceId, UUID descriptorId);
    void unsuspendEService(UUID eServiceId, UUID descriptorId);
    FileDownloadMultipart downloadEServiceDescriptorInterface(UUID eserviceId, UUID descriptorId);
    Documents getDocuments(UUID eserviceId, UUID descriptorId);
    it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor patchEServiceDescriptor(UUID eserviceId, UUID descriptorId, EServiceDescriptorPatchRequest body);
    it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor patchEServiceDescriptorQuotas(UUID eserviceId, UUID descriptorId, EServiceDescriptorQuotasPatchRequest body);
}
