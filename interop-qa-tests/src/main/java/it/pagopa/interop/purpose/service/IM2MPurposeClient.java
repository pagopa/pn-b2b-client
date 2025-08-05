package it.pagopa.interop.purpose.service;

import it.pagopa.interop.ListRequest;
import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DelegationRef;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Agreement;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.FileDownloadMultipart;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeVersion;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeVersionSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeVersions;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purposes;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

public interface IM2MPurposeClient extends SettableBearerToken {

    @Data
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    class PurposesListRequest extends ListRequest {
        private List<UUID> eservicesIds;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @SuperBuilder
    class PurposeVersionsListRequest extends ListRequest {
        private UUID purposeId;
    }

    PurposeVersion getVersion(UUID purposeId, UUID purposeVersionId);

    PurposeVersions getVersions(PurposeVersionsListRequest request);

    Agreement getPurposeAgreement(UUID agreementId);

    FileDownloadMultipart downloadPurposeVersionDocument(UUID purposeId, UUID versionId);

    Purpose activatePurpose(UUID purposeId, DelegationRef delegationRef);

    Purpose activatePurpose(UUID purposeId);

    Purpose suspendPurpose(UUID purposeId, DelegationRef delegationRef);

    Purpose suspendPurpose(UUID purposeId);

    Purpose getPurpose(UUID purposeId);

    Purposes getPurposes(PurposesListRequest request);

    PurposeVersion createPurposeVersion(UUID purposeId, PurposeVersionSeed purposeVersionSeed);

    Purpose unsuspendPurpose(UUID purposeId, DelegationRef delegationRef);

    Purpose unsuspendPurpose(UUID purposeId);

    Purpose approvePurpose(UUID purposeId, DelegationRef delegationRef);

    Purpose approvePurpose(UUID purposeId);

    Purpose archivePurpose(UUID purposeId);
}
