package it.pagopa.interop.agreement.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Agreement;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementState;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementSubmission;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Agreements;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DelegationRef;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Documents;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purposes;
import java.util.List;
import java.util.UUID;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.*;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.UUID;

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
    Agreement approveAgreement(UUID agreementId, DelegationRef delegationRef);
    Agreement approveAgreement(UUID agreementId);
    Agreement unsuspendAgreement(UUID agreementId, DelegationRef delegationRef);
    Agreement unsuspendAgreement(UUID agreementId);
    Agreements getAgreements(AgreementsListRequest listRequest);
    Purposes getAgreementPurposes(UUID agreementId);
    Purposes getAgreementPurposes(UUID agreementId, int limit, int offset);
    Document uploadConsumerDocument(UUID agreementId, Resource document, String prettyName);
    FileDownloadMultipart getConsumerDocument(UUID agreementId, UUID documentId);
    Documents getConsumerDocuments(UUID agreementId);
    Documents getConsumerDocuments(UUID agreementId, int offset, int limit);
}
