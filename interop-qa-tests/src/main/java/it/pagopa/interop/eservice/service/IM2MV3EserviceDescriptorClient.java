package it.pagopa.interop.eservice.service;

import it.pagopa.interop.authorization.service.utils.Authenticable;
import it.pagopa.interop.authorization.service.utils.SettableHeaders;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;

public interface IM2MV3EserviceDescriptorClient extends IM2MEserviceDescriptorClient,
    SettableHeaders, Authenticable {

    @Data
    @Builder
    class EServiceDescriptorAttributePatchRequest {
        private Integer dailyCallsPerConsumer;
    }

    void patchEServiceDescriptorCertifiedAttribute(UUID eServiceId, UUID descriptorId, Integer groupIndex, UUID attributeId, EServiceDescriptorAttributePatchRequest body);
}
