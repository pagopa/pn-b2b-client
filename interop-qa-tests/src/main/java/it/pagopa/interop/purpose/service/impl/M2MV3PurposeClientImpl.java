package it.pagopa.interop.purpose.service.impl;

import static it.pagopa.interop.utils.ApiClientUtils.V3_UNSUPPORTED_BEARER_MSG;

import it.pagopa.interop.M2MVersionsMapper;
import it.pagopa.interop.common.client.AbstractDPoPClient;
import it.pagopa.interop.common.rest_template.DpopRestTemplate;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Agreement;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DelegationRef;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.FileDownloadMultipart;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeVersion;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeVersionSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeVersions;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purposes;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.api.PurposesApi;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.RemainingDailyCallsResponse;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.ReversePurposeDraftUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.UpdateDraftPurposeRequest;
import it.pagopa.interop.purpose.service.IM2MV3PurposeClient;
import it.pagopa.interop.utils.ApiClientUtils;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class M2MV3PurposeClientImpl extends AbstractDPoPClient implements IM2MV3PurposeClient {
    private final PurposesApi purposesApi;
    private final String basePath;
    private final M2MVersionsMapper vMapper;

    public M2MV3PurposeClientImpl(
        DpopRestTemplate restTemplate,
        InteropClientConfigs interopClientConfigs,
        M2MVersionsMapper vMapper
    ) {
        super(restTemplate);
        this.basePath = interopClientConfigs.getM2mV3BaseUrl();
        this.purposesApi = new PurposesApi(
            ApiClientUtils.createApiClient(restTemplate, basePath,
                Collections.emptyMap()));
        this.vMapper = vMapper;
    }

    public Purpose activatePurpose(UUID purposeId, DelegationRef delegationRef) {
        return vMapper.mapToV2(purposesApi.activateDraftPurpose(purposeId, vMapper.mapToV3(delegationRef)));
    }

    @Override
    public Purpose activatePurpose(UUID purposeId) {
        return vMapper.mapToV2(purposesApi.activateDraftPurpose(purposeId, null));
    }

    public Purpose suspendPurpose(UUID purposeId, DelegationRef delegationRef) {
        return vMapper.mapToV2(purposesApi.suspendPurpose(purposeId, vMapper.mapToV3(delegationRef)));
    }

    @Override
    public Purpose suspendPurpose(UUID purposeId) {
        return vMapper.mapToV2(purposesApi.suspendPurpose(purposeId, null));
    }

    @Override
    public Purpose getPurpose(UUID purposeId) {
        return vMapper.mapToV2(purposesApi.getPurpose(purposeId));
    }

    @Override
    public Purposes getPurposes(PurposesListRequest request) {
        return vMapper.mapToV2(purposesApi.getPurposes(
            request.getOffset(),
            request.getLimit(),
            request.getEservicesIds(),
            null,
            null,
            null));
    }

    @Override
    public PurposeVersion createPurposeVersion(
        UUID purposeId, PurposeVersionSeed purposeVersionSeed) {
        return vMapper.mapToV2(
            purposesApi.createPurposeVersion(
                purposeId,
                vMapper.mapToV3(purposeVersionSeed)));
    }

    public Purpose unsuspendPurpose(UUID purposeId, DelegationRef delegationRef) {
        return vMapper.mapToV2(purposesApi.unsuspendPurpose(purposeId, vMapper.mapToV3(delegationRef)));
    }

    @Override
    public Purpose unsuspendPurpose(UUID purposeId) {
        return vMapper.mapToV2(purposesApi.unsuspendPurpose(purposeId, null));
    }

    public Purpose approvePurpose(UUID purposeId, DelegationRef delegationRef) {
        return vMapper.mapToV2(purposesApi.approvePurpose(purposeId, vMapper.mapToV3(delegationRef)));
    }

    @Override
    public Purpose approvePurpose(UUID purposeId) {
        return vMapper.mapToV2(purposesApi.approvePurpose(purposeId, null));
    }

    @Override
    public Purpose archivePurpose(UUID purposeId) {
        return vMapper.mapToV2(purposesApi.archivePurpose(purposeId));
    }

    @Override
    public PurposeVersion getVersion(UUID purposeId, UUID purposeVersionId) {
        return vMapper.mapToV2(purposesApi.getPurposeVersion(purposeId, purposeVersionId));
    }

    @Override
    public PurposeVersions getVersions(PurposeVersionsListRequest request) {
        return vMapper.mapToV2(purposesApi.getPurposeVersions(
            request.getPurposeId(),
            request.getOffset(),
            request.getLimit(),
            null));
    }

    @Override
    public Agreement getPurposeAgreement(UUID purposeId) {
        return vMapper.mapToV2(purposesApi.getPurposeAgreement(purposeId));
    }

    @Override
    public FileDownloadMultipart downloadPurposeVersionDocument(UUID purposeId, UUID versionId) {
        return vMapper.mapToV2(purposesApi.downloadPurposeVersionRiskAnalysisDocument(purposeId, versionId));
    }

    @Override
    public Purpose patchPurpose(UUID purposeId, PurposePatchRequest body) {
        return vMapper.mapToV2(purposesApi.updateDraftPurpose(
            purposeId,
            new UpdateDraftPurposeRequest()
                .title(body.getTitle())
                .description(body.getDescription())
                .riskAnalysisForm(vMapper.mapToV3(body.getRiskAnalysisForm()))
                .dailyCalls(body.getDailyCalls())
                .isFreeOfCharge(body.getIsFreeOfCharge())
                .freeOfChargeReason(body.getFreeOfChargeReason())));
    }

    @Override
    public Purpose patchReversePurpose(UUID reversePurposeId, ReversePurposePatchRequest body) {
        return vMapper.mapToV2(purposesApi.updateDraftReversePurpose(reversePurposeId, new ReversePurposeDraftUpdateSeed()
            .dailyCalls(body.getDailyCalls())
            .title(body.getTitle())
            .isFreeOfCharge(body.getIsFreeOfCharge())
            .freeOfChargeReason(body.getFreeOfChargeReason())
            .description(body.getDescription())
        ));
    }

    @Override
    public RemainingDailyCallsResponse getRemainingDailyCalls(UUID purposeId) throws RestClientException {
        return purposesApi.getRemainingDailyCalls(purposeId);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        throw new UnsupportedOperationException(V3_UNSUPPORTED_BEARER_MSG);
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        this.purposesApi.setApiClient(
            ApiClientUtils.createApiClient(super.getRestTemplate(), basePath, headers));
    }
}
