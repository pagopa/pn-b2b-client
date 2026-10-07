package it.pagopa.interop.purpose.service.impl;

import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.PurposesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.UUID;

import static it.pagopa.interop.utils.BlobFileCreationUtils.createTempFile;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Retryable(
        retryFor = {HttpServerErrorException.class},
        backoff = @Backoff(delay = 2000)
)
public class PurposeApiClientImpl implements IPurposeApiClient {
    private final PurposesApi purposesApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public PurposeApiClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getBaseUrl();
        this.purposesApi = new PurposesApi(createApiClient("dummyBearer"));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public RiskAnalysisFormConfig retrieveLatestRiskAnalysisConfiguration() {
        return purposesApi.retrieveLatestRiskAnalysisConfiguration(null);
    }

    @Override
    public RiskAnalysisFormConfig retrieveRiskAnalysisConfigurationByVersion(String riskAnalysisVersion, UUID eserviceId) {
        return purposesApi.retrieveRiskAnalysisConfigurationByVersion(riskAnalysisVersion, eserviceId);
    }

    @Override
    public CreatedResource createPurpose(PurposeSeed purposeSeed) {
        return purposesApi.createPurpose(purposeSeed);
    }

    @Override
    public PurposeVersionResource createPurposeVersion(UUID purposeId, PurposeVersionSeed purposeVersionSeed) {
        return purposesApi.createPurposeVersion(purposeId, purposeVersionSeed);
    }

    @Override
    public CreatedResource createPurposeForReceiveEservice(PurposeEServiceSeed purposeEServiceSeed) {
        return purposesApi.createPurposeForReceiveEservice(purposeEServiceSeed);
    }

    @Override
    public Purpose getPurpose(UUID purposeId) {
        return purposesApi.getPurpose(purposeId);
    }

    public PurposeVersionResource activatePurposeVersion(UUID purposeId, UUID versionId, DelegationRef delegationRef) {
        return purposesApi.activatePurposeVersion(purposeId, versionId, delegationRef);
    }

    @Override
    public PurposeVersionResource activatePurposeVersion(UUID purposeId, UUID versionId) {
        return purposesApi.activatePurposeVersion(purposeId, versionId, null);
    }

    public PurposeVersionResource suspendPurposeVersion(UUID purposeId, UUID versionId, DelegationRef delegationRef) {
        return purposesApi.suspendPurposeVersion(purposeId, versionId, delegationRef);
    }

    @Override
    public PurposeVersionResource suspendPurposeVersion(UUID purposeId, UUID versionId) {
        return purposesApi.suspendPurposeVersion(purposeId, versionId, null);
    }

    @Override
    public PurposeVersionResource archivePurposeVersion(UUID purposeId, UUID versionId) {
        return purposesApi.archivePurposeVersion(purposeId, versionId);
    }

    @Override
    public void rejectPurposeVersion(UUID purposeId, UUID versionId, RejectPurposeVersionPayload rejectPurposeVersionPayload) {
        purposesApi.rejectPurposeVersion(purposeId, versionId, rejectPurposeVersionPayload);
    }

    @Override
    public PurposeVersionResource clonePurpose(UUID purposeId, PurposeCloneSeed purposeCloneSeed) {
        return purposesApi.clonePurpose(purposeId, purposeCloneSeed);
    }

    @Override
    public void deletePurposeVersion(UUID purposeId, UUID versionId) {
        purposesApi.deletePurposeVersion(purposeId, versionId);
    }

    @Override
    public void deletePurpose(UUID purposeId) {
        purposesApi.deletePurpose(purposeId);
    }

    @Override
    public Purposes getConsumerPurposes(Integer offset, Integer limit, String q, List<UUID> eservicesIds, List<UUID> producersIds, List<PurposeVersionState> states) {
        return purposesApi.getConsumerPurposes(offset, limit, q, eservicesIds, producersIds, states);
    }

    @Override
    public Purposes getProducerPurposes(Integer offset, Integer limit, String q, List<UUID> eservicesIds, List<UUID> consumersIds, List<PurposeVersionState> states) {
        return purposesApi.getProducerPurposes(offset, limit, q, eservicesIds, consumersIds, states);
    }

    @Override
    public File getRiskAnalysisDocument(UUID purposeId, UUID versionId, UUID documentId) {
        try {
            Resource resourceResponse = purposesApi.getRiskAnalysisDocument(purposeId, versionId, documentId);
            return createTempFile("riskAnalysis-document-",resourceResponse.getInputStream());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public PurposeVersionResource updatePurpose(UUID purposeId, PurposeUpdateContent purposeUpdateContent) {
        return purposesApi.updatePurpose(purposeId, purposeUpdateContent);
    }

    @Override
    public PurposeVersionResource updateReversePurpose(UUID purposeId, ReversePurposeUpdateContent reversePurposeUpdateContent) {
        return purposesApi.updateReversePurpose(purposeId, reversePurposeUpdateContent);
    }

    @Override
    public CreatedResource createPurposeFromTemplate(UUID purposeTemplateId, PurposeFromTemplateSeed purposeFromTemplateSeed) throws RestClientException {
        return purposesApi.createPurposeFromTemplate(purposeTemplateId, purposeFromTemplateSeed);
    }

    @Override
    public PurposeVersionResource patchUpdatePurposeFromTemplate(UUID purposeTemplateId, UUID purposeId, PatchPurposeUpdateFromTemplateContent patchPurposeUpdateFromTemplateContent) throws RestClientException {
        return purposesApi.patchUpdatePurposeFromTemplate(purposeTemplateId, purposeId, patchPurposeUpdateFromTemplateContent);
    }

    @Override
    public RemainingDailyCallsResponse getRemainingDailyCalls(UUID purposeId) {
        return  purposesApi.getRemainingDailyCalls(purposeId);
    }

    @Override
    public void assignRiskAnalysis(UUID purposeId, RiskAnalysisAssignmentSeed payload) throws RestClientException {
        purposesApi.assignRiskAnalysisReviewer(purposeId, payload);
    }

    @Override
    public void compileRiskAnalysisForm(UUID purposeId, RiskAnalysisFormSeed payload) throws RestClientException {
        purposesApi.editRiskAnalysisForm(purposeId, payload);
    }

    @Override
    public void submitRiskAnalysis(UUID purposeId, RiskAnalysisSubmissionSeed payload) throws RestClientException {
        purposesApi.submitRiskAnalysis(purposeId, payload);
    }

    @Override
    public void rejectRiskAnalysis(UUID purposeId, RiskAnalysisRejectionSeed payload) throws RestClientException {
        purposesApi.rejectRiskAnalysis(purposeId, payload);
    }

    @Override
    public void signRiskAnalysis(UUID purposeId) throws RestClientException {
        purposesApi.signRiskAnalysis(purposeId);
    }

    @Override
    public Purposes getRiskAnalysisAssignments(Integer offset, Integer limit, List<UUID> eservicesIds, List<RiskAnalysisSigningState> states) {
        return purposesApi.getRiskAnalysisAssignments(offset, limit, eservicesIds, states);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.purposesApi.setApiClient(createApiClient(bearerToken));
    }
}
