package it.pagopa.interop.eservice.service;

import it.pagopa.interop.common.client.IClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.*;

import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

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
    @Builder
    class EServiceCreateRequest {
        private String name;
        private String description;
        private EServiceTechnology technology;
        private EServiceMode mode;
        private DescriptorSeedForEServiceCreation descriptor;
        private Boolean isSignalHubEnabled;
        private Boolean isConsumerDelegable;
        private Boolean isClientAccessDelegable;
        private Boolean personalData;

        public static EServiceCreateRequest fromSeed(EServiceSeed seed) {
            return EServiceCreateRequest.builder()
                .name(seed.getName())
                .description(seed.getDescription())
                .descriptor(seed.getDescriptor())
                .technology(seed.getTechnology())
                .mode(seed.getMode())
                .isSignalHubEnabled(seed.getIsSignalHubEnabled())
                .isConsumerDelegable(seed.getIsConsumerDelegable())
                .isClientAccessDelegable(seed.getIsClientAccessDelegable())
                .personalData(seed.getPersonalData())
                .build();
        }

        public EServiceSeed toSeed() {
            return new EServiceSeed()
                    .name(this.getName())
                    .description(this.getDescription())
                    .descriptor(this.getDescriptor())
                    .technology(this.getTechnology())
                    .mode(this.getMode())
                    .isSignalHubEnabled(this.getIsSignalHubEnabled())
                    .isConsumerDelegable(this.getIsConsumerDelegable())
                    .isClientAccessDelegable(this.getIsClientAccessDelegable())
                    .personalData(this.getPersonalData());
        }
    }

    @Data
    @Builder
    class EServicePatchRequest {
        private EServiceTechnology technology;
        private Boolean isSignalHubEnabled;
        private String name;
        private String description;
        private EServiceMode mode;
        private Boolean isConsumerDelegable;
        private Boolean isClientAccessDelegable;
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
    }

    @Data
    @Builder
    class EServiceArchivingRequest {
        private String archivingReason;
    }

    EServices getAll(EserviceListRequest payload);

    void delete(UUID id);

    ResponseEntity<EService> getWithHttpInfo(UUID id);

    Document uploadInterface(EServiceInterfaceUploadRequest body);

    Document uploadAsyncExchangeCallbackInterface(EServiceInterfaceUploadRequest body);

    void deleteEServiceDescriptorAsyncExchangeCallbackInterface(UUID eServiceId, UUID descriptorId);

    FileDownloadMultipart downloadEServiceDescriptorAsyncExchangeCallbackInterface(UUID eServiceId, UUID descriptorId);

    EService createEService(EServiceCreateRequest body);

    EService patchEService(UUID eServiceId, EServicePatchRequest body);

    EService patchEServiceName(UUID eServiceId, EServiceNamePatchRequest body);

    EService patchEServiceDelegation(UUID eServiceId, EServiceDelegationPatchRequest body);

    EService patchEServiceDescription(UUID eServiceId, EServiceDescriptionPatchRequest body);

    EService scheduleArchiveEService(UUID eServiceId, EServiceArchivingRequest body);

    EService cancelScheduleArchiveEService(UUID eServiceId);

    FileDownloadMultipart getDescriptorInterface(UUID eServiceId, UUID descriptorId);
}
