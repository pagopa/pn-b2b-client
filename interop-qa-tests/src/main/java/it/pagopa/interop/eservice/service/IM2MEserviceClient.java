package it.pagopa.interop.eservice.service;

import it.pagopa.interop.common.client.IClient;
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
import org.springframework.core.io.Resource;

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

    /* TODO 18/07/2025: astrazioni di oggetti non ancora rilasciati nella specifica OpenAPI,
     *  adattare una volta ottenuta la specifica completa **********************************/
    @Data
    class EServiceInterfaceUploadRequest {
        private UUID eServiceId;
        private UUID descriptorId;
        private Resource file;
        private String prettyName;

        public EServiceInterfaceUploadRequest resource(Resource resource) {
            this.file = resource;
            return this;
        }

        public EServiceInterfaceUploadRequest prettyName(String name) {
            this.prettyName = name;
            return this;
        }

        public EServiceInterfaceUploadRequest eServiceId(UUID eServiceId) {
            this.eServiceId = eServiceId;
            return this;
        }

        public EServiceInterfaceUploadRequest descriptorId(UUID descriptorId) {
            this.descriptorId = descriptorId;
            return this;
        }
    }

    @Data
    class EServiceInterfaceUploadResponse {
        private UUID id;
    }

    @Data
    @Builder
    class EServicePatchRequest {
        private EServiceTechnology technology;
        private Boolean isSignalHubEnabled;
    }

    @Data
    @Builder
    class EServiceNamePatchRequest {
        private String name;
    }

    @Data
    @Builder
    class EServiceDelegationPatchRequest {
        private Boolean isConsumerDelegable;
        private Boolean isClientAccessDelegable;
    }

    @Data
    @Builder
    class EServiceDescriptionPatchRequest {
        private String description;

        // Ipotetico secondo campo, usato per simulare il caso in cui - per qualche motivo -
        // l'api in questione non coinvolga solo il nome. La sua rimozione o modifica farà parte
        // del processo di revisione det test a seguito del rilascio delle specifiche OpenAPI.
        private String summary;
    }
    /* *************************************************************************************/

    EServices getAll(EserviceListRequest payload);

    EServiceDescriptor getDescriptor(UUID eserviceId, UUID descriptorId);

    void delete(UUID id);

    void deleteInterface(UUID eServiceId, UUID descriptorId);

    void unsuspendEService(UUID eServiceId, UUID descriptorId);

    Document uploadInterface(EServiceInterfaceUploadRequest body);

    FileDownloadMultipart downloadEServiceDescriptorInterface(UUID eserviceId, UUID descriptorId);

    EService patchEService(UUID eServiceId, EServicePatchRequest body);

    EService patchEServiceName(UUID eServiceId, EServiceNamePatchRequest body);

    EService patchEServiceDelegation(UUID eServiceId, EServiceDelegationPatchRequest body);

    EService patchEServiceDescription(UUID eServiceId, EServiceDescriptionPatchRequest body);
}
