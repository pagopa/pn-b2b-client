package it.pagopa.interop.purpose.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purposes;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

public interface IM2MPurposeClient extends SettableBearerToken {

    Purposes getPurposes(PurposesListRequest request);

    @Data
    @Builder
    class PurposesListRequest {
        @NonNull
        private Integer offset;
        @NonNull
        private Integer limit;

        private List<UUID> eservicesIds;
    }
}
