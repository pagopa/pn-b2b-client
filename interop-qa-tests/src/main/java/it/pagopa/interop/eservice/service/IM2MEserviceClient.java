package it.pagopa.interop.eservice.service;

import it.pagopa.interop.common.client.IClient;
import it.pagopa.interop.eservice.service.impl.M2MEserviceClientImpl.EServiceInterfaceUploadRequest;
import it.pagopa.interop.eservice.service.impl.M2MEserviceClientImpl.EServicePatchRequest;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Document;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EService;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServices;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.FileDownloadMultipart;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

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

    void delete(UUID id);

    void deleteInterface(UUID eServiceId, UUID descriptorId);

    void unsuspendEService(UUID eServiceId, UUID descriptorId);

    Document uploadInterface(EServiceInterfaceUploadRequest body);

    FileDownloadMultipart downloadEServiceDescriptorInterface(UUID eserviceId, UUID descriptorId);

    void patchEService(UUID eServiceId, EServicePatchRequest body);
}
