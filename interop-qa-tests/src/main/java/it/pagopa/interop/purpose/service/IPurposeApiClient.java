package it.pagopa.interop.purpose.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;

import java.util.UUID;

public interface IPurposeApiClient extends SettableBearerToken {
    RiskAnalysisFormConfig retrieveLatestRiskAnalysisConfiguration();
    CreatedResource createPurpose(PurposeSeed purposeSeed);
    CreatedResource createPurposeForReceiveEservice(PurposeEServiceSeed purposeEServiceSeed);
    Purpose getPurpose(UUID purposeId);
    PurposeVersionResource activatePurposeVersion(UUID purposeId, UUID versionId);
    PurposeVersionResource suspendPurposeVersion(UUID purposeId, UUID versionId);
    PurposeVersionResource archivePurposeVersion(UUID purposeId, UUID versionId);
    void rejectPurposeVersion(UUID purposeId, UUID versionId, RejectPurposeVersionPayload rejectPurposeVersionPayload);
    PurposeVersionResource clonePurpose(UUID purposeId, PurposeCloneSeed purposeCloneSeed);
}
