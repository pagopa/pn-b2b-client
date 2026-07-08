package it.pagopa.interop.agreement.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
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
    Agreements getAgreements(AgreementsListRequest listRequest);
    Purposes getAgreementPurposes(UUID agreementId);
    Purposes getAgreementPurposes(UUID agreementId, int limit, int offset);
    Document uploadConsumerDocument(UUID agreementId, Resource document, String prettyName);
    FileDownloadMultipart getConsumerDocument(UUID agreementId, UUID documentId);
    Documents getConsumerDocuments(UUID agreementId);
    Documents getConsumerDocuments(UUID agreementId, int offset, int limit);
}
