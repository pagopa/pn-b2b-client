package it.pagopa.interop.purpose.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplate;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeTemplateDraftUpdateSeed;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

public interface IM2MPurposeTemplateClient extends SettableBearerToken {
    @Data
    @Builder
    class PurposeTemplatePatchRequest {
        private String name;
        private String intendedTarget;
        private String description;
        private EServiceTechnology technology;
        private EServiceMode mode;
        private Boolean isSignalHubEnabled;
    }

    PurposeTemplate getPurposeTemplate(UUID id);
    PurposeTemplate patchPurposeTemplate(UUID id, PurposeTemplateDraftUpdateSeed purposePatchSeed);
}
