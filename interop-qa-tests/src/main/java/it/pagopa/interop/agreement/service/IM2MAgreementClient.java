package it.pagopa.interop.agreement.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Agreement;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementState;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementSubmission;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Agreements;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

public interface IM2MAgreementClient extends SettableBearerToken {
    @Data
    @Builder
    class AgreementsListRequest {
        @NonNull private Integer offset;
        @NonNull private Integer limit;
        private List<UUID> eservicesIds;
        private List<UUID> producersIds;
        private List<UUID> consumersIds;
        private List<UUID> descriptorsIds;
        private List<AgreementState> states;
        private Boolean showOnlyUpgradeable;
    }

    Agreement getAgreementById(UUID id);
    Agreement createAgreement(AgreementSeed agreementPayload);
    Agreement submitAgreement(UUID agreementId, AgreementSubmission agreementSubmission);
    Agreements getAgreements(AgreementsListRequest listRequest);
}
