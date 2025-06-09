package it.pagopa.interop.purpose.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeCloneSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeEServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeUpdateContent;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionState;
import it.pagopa.interop.generated.openapi.clients.bff.model.Purposes;
import it.pagopa.interop.generated.openapi.clients.bff.model.RejectPurposeVersionPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.ReversePurposeUpdateContent;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormConfig;
import java.io.File;
import java.util.List;
import java.util.UUID;

public interface IPurposeApiClient extends SettableBearerToken {
    RiskAnalysisFormConfig retrieveLatestRiskAnalysisConfiguration();
    RiskAnalysisFormConfig retrieveRiskAnalysisConfigurationByVersion(String riskAnalysisVersion, UUID eserviceId);
    CreatedResource createPurpose(PurposeSeed purposeSeed);
    PurposeVersionResource createPurposeVersion(UUID purposeId, PurposeVersionSeed purposeVersionSeed);
    CreatedResource createPurposeForReceiveEservice(PurposeEServiceSeed purposeEServiceSeed);
    Purpose getPurpose(UUID purposeId);
    PurposeVersionResource activatePurposeVersion(UUID purposeId, UUID versionId);
    PurposeVersionResource suspendPurposeVersion(UUID purposeId, UUID versionId);
    PurposeVersionResource archivePurposeVersion(UUID purposeId, UUID versionId);
    void rejectPurposeVersion(UUID purposeId, UUID versionId, RejectPurposeVersionPayload rejectPurposeVersionPayload);
    PurposeVersionResource clonePurpose(UUID purposeId, PurposeCloneSeed purposeCloneSeed);
    void deletePurposeVersion(UUID purposeId, UUID versionId);
    void deletePurpose(UUID purposeId);
    Purposes getConsumerPurposes(Integer offset, Integer limit, String q, List<UUID> eservicesIds, List<UUID> producersIds, List<PurposeVersionState> states);
    Purposes getProducerPurposes(Integer offset, Integer limit, String q, List<UUID> eservicesIds, List<UUID> consumersIds, List<PurposeVersionState> states);
    File getRiskAnalysisDocument(UUID purposeId, UUID versionId, UUID documentId);
    PurposeVersionResource updatePurpose(UUID purposeId, PurposeUpdateContent purposeUpdateContent);
    PurposeVersionResource updateReversePurpose(UUID purposeId, ReversePurposeUpdateContent reversePurposeUpdateContent);
}
