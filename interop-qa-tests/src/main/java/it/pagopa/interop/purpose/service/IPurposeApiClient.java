package it.pagopa.interop.purpose.service;

import it.pagopa.interop.generated.openapi.clients.bff.model.*;

import java.util.UUID;

public interface IPurposeApiClient {
    RiskAnalysisFormConfig retrieveLatestRiskAnalysisConfiguration(String xCorrelationId);
    CreatedResource createPurpose(String xCorrelationId, PurposeSeed purposeSeed);
    CreatedResource createPurposeForReceiveEservice(String xCorrelationId, PurposeEServiceSeed purposeEServiceSeed);
    Purpose getPurpose(String xCorrelationId, UUID purposeId);
    PurposeVersionResource activatePurposeVersion(String xCorrelationId, UUID purposeId, UUID versionId);
    PurposeVersionResource suspendPurposeVersion(String xCorrelationId, UUID purposeId, UUID versionId);
    PurposeVersionResource archivePurposeVersion(String xCorrelationId, UUID purposeId, UUID versionId);
    void rejectPurposeVersion(String xCorrelationId, UUID purposeId, UUID versionId, RejectPurposeVersionPayload rejectPurposeVersionPayload);

}
