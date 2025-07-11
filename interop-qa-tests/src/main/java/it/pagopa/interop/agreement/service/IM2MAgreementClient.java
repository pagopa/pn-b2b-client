package it.pagopa.interop.agreement.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Agreement;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementState;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementSubmission;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Agreements;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purposes;
import java.time.LocalDateTime;
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

    // TODO 11/07/2025: in QA non sono ancora state rilasciate le API in oggetto, dunque mancano
    //  gli oggetti concreti da poter usare. Si usano i seguenti come placeholders temporanei.
    @Data
    class Documents { private List<Document> results; }

    @Data
    class Document {
        private UUID id;
        private String name;
        private String prettyName;
        private LocalDateTime createdAt;
    }

    Agreement getAgreementById(UUID id);
    Agreement createAgreement(AgreementSeed agreementPayload);
    Agreement submitAgreement(UUID agreementId, AgreementSubmission agreementSubmission);
    Agreements getAgreements(AgreementsListRequest listRequest);
    Purposes getAgreementPurposes(UUID agreementId);
    Purposes getAgreementPurposes(UUID agreementId, int limit, int offset);
    Documents getConsumerDocuments(UUID agreementId);
    Documents getConsumerDocuments(UUID agreementId, int limit, int offset);
}
