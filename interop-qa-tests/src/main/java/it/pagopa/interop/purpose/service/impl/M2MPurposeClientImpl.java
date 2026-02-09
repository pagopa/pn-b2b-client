package it.pagopa.interop.purpose.service.impl;

import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.api.PurposesApi;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.*;
import it.pagopa.interop.purpose.service.IM2MPurposeClient;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class M2MPurposeClientImpl implements IM2MPurposeClient {
    private final PurposesApi purposesApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public M2MPurposeClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getM2mBaseUrl();
        this.purposesApi = new PurposesApi(createApiClient("dummyBearer"));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    public Purpose activatePurpose(UUID purposeId, DelegationRef delegationRef) {
        return purposesApi.activateDraftPurpose(purposeId, delegationRef);
    }

    @Override
    public Purpose activatePurpose(UUID purposeId) {
        return purposesApi.activateDraftPurpose(purposeId, null);
    }

    public Purpose suspendPurpose(UUID purposeId, DelegationRef delegationRef) {
        return purposesApi.suspendPurpose(purposeId, delegationRef);
    }

    @Override
    public Purpose suspendPurpose(UUID purposeId) {
        return purposesApi.suspendPurpose(purposeId, null);
    }

    @Override
    public Purpose getPurpose(UUID purposeId) {
        return purposesApi.getPurpose(purposeId);
    }

    @Override
    public Purposes getPurposes(PurposesListRequest request) {
        return purposesApi.getPurposes(
            request.getOffset(),
            request.getLimit(),
            request.getEservicesIds(),
            null,
            null,
            null);
    }

    @Override
    public PurposeVersion createPurposeVersion(
        UUID purposeId, PurposeVersionSeed purposeVersionSeed) {
        return purposesApi.createPurposeVersion(purposeId.toString(), purposeVersionSeed);
    }

    public Purpose unsuspendPurpose(UUID purposeId, DelegationRef delegationRef) {
        return purposesApi.unsuspendPurpose(purposeId, delegationRef);
    }

    @Override
    public Purpose unsuspendPurpose(UUID purposeId) {
        return purposesApi.unsuspendPurpose(purposeId, null);
    }

    public Purpose approvePurpose(UUID purposeId, DelegationRef delegationRef) {
        return purposesApi.approvePurpose(purposeId, delegationRef);
    }

    @Override
    public Purpose approvePurpose(UUID purposeId) {
        return purposesApi.approvePurpose(purposeId, null);
    }

    @Override
    public Purpose archivePurpose(UUID purposeId) {
        return purposesApi.archivePurpose(purposeId);
    }

    @Override
    public PurposeVersion getVersion(UUID purposeId, UUID purposeVersionId) {
        return purposesApi.getPurposeVersion(purposeId, purposeVersionId);
    }

    @Override
    public PurposeVersions getVersions(PurposeVersionsListRequest request) {
        return purposesApi.getPurposeVersions(
            request.getPurposeId().toString(),
            request.getOffset(),
            request.getLimit(),
            null);
    }

    @Override
    public Agreement getPurposeAgreement(UUID purposeId) {
        return purposesApi.getPurposeAgreement(purposeId);
    }

    @Override
    public FileDownloadMultipart downloadPurposeVersionDocument(UUID purposeId, UUID versionId) {
        return purposesApi.downloadPurposeVersionRiskAnalysisDocument(purposeId, versionId);
    }

    @Override
    public Purpose patchPurpose(UUID purposeId, PurposePatchRequest body) {
        return purposesApi.updateDraftPurpose(
            purposeId,
                new UpdateDraftPurposeRequest()
                .title(body.getTitle())
                .description(body.getDescription())
                .riskAnalysisForm(body.getRiskAnalysisForm())
                .dailyCalls(body.getDailyCalls())
                .isFreeOfCharge(body.getIsFreeOfCharge())
                .freeOfChargeReason(body.getFreeOfChargeReason()));
    }

    @Override
    public Purpose patchReversePurpose(UUID reversePurposeId, ReversePurposePatchRequest body) {
        return purposesApi.updateDraftReversePurpose(reversePurposeId, new ReversePurposeDraftUpdateSeed()
            .dailyCalls(body.getDailyCalls())
            .title(body.getTitle())
            .isFreeOfCharge(body.getIsFreeOfCharge())
            .freeOfChargeReason(body.getFreeOfChargeReason())
            .description(body.getDescription())
        );
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.purposesApi.setApiClient(createApiClient(bearerToken));
    }
}
