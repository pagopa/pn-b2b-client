package it.pagopa.interop.agreement.service.impl;

import static it.pagopa.interop.utils.ApiClientUtils.V3_UNSUPPORTED_BEARER_MSG;

import it.pagopa.interop.M2MVersionsMapper;
import it.pagopa.interop.agreement.service.IM2MV3AgreementClient;
import it.pagopa.interop.common.client.AbstractDPoPClient;
import it.pagopa.interop.common.rest_template.DpopRestTemplate;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Agreement;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementSubmission;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Agreements;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DelegationRef;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Documents;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purposes;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.api.AgreementsApi;
import it.pagopa.interop.utils.ApiClientUtils;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@ToString
@EqualsAndHashCode
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class M2MV3AgreementClientImpl extends AbstractDPoPClient implements IM2MV3AgreementClient {
    private final AgreementsApi agreementsApi;
    private final String basePath;
    private final M2MVersionsMapper mapper;

    public M2MV3AgreementClientImpl(
        DpopRestTemplate restTemplate,
        InteropClientConfigs interopClientConfigs,
        M2MVersionsMapper mapper
    ) {
        super(restTemplate);
        this.basePath = interopClientConfigs.getM2mV3BaseUrl();
        this.agreementsApi = new AgreementsApi(ApiClientUtils.createApiClient(restTemplate, basePath,
            Collections.emptyMap()));
        this.mapper = mapper;
    }

    @Override
    public Agreement createAgreement(AgreementSeed agreementSeed) {
        it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.AgreementSeed v3Seed = this.mapper.mapToV3(
            agreementSeed);
        return this.mapper.mapToV2(agreementsApi.createAgreement(v3Seed));
    }

    @Override
    public Agreement submitAgreement(UUID agreementId, AgreementSubmission agreementSubmission) {
        it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.AgreementSubmission v3Bean = this.mapper.mapToV3(
            agreementSubmission);
        return this.mapper.mapToV2(agreementsApi.submitAgreement(agreementId, v3Bean));
    }

    @Override
    public Agreement approveAgreement(UUID agreementId, DelegationRef delegationRef) {
        return this.mapper.mapToV2(agreementsApi.approveAgreement(agreementId, mapper.mapToV3(delegationRef)));
    }

    @Override
    public Agreement approveAgreement(UUID agreementId) {
        return this.mapper.mapToV2(agreementsApi.approveAgreement(agreementId, null));
    }

    @Override
    public Agreement unsuspendAgreement(UUID agreementId, DelegationRef delegationRef) {
        return this.mapper.mapToV2(agreementsApi.unsuspendAgreement(agreementId, mapper.mapToV3(delegationRef)));
    }

    @Override
    public Agreement unsuspendAgreement(UUID agreementId) {
        return this.mapper.mapToV2(agreementsApi.unsuspendAgreement(agreementId, null));
    }

    @Override
    public Agreements getAgreements(AgreementsListRequest listRequest) {
        it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Agreements v3Bean = agreementsApi.getAgreements(
            listRequest.getOffset(),
            listRequest.getLimit(),
            this.mapper.mapToV3(listRequest.getStates()),
            listRequest.getProducersIds(),
            listRequest.getConsumersIds(),
            listRequest.getDescriptorsIds(),
            listRequest.getEservicesIds()
        );
        return this.mapper.mapToV2(v3Bean);
    }

    @Override
    public Agreement getAgreementById(UUID id) {
        return this.mapper.mapToV2(agreementsApi.getAgreement(id));
    }

    @Override
    public Purposes getAgreementPurposes(UUID agreementId) {
        return this.getAgreementPurposes(agreementId, 30, 0);
    }

    @Override
    public Purposes getAgreementPurposes(UUID agreementId, int limit, int offset) {
        return this.mapper.mapToV2(this.agreementsApi.getAgreementPurposes(agreementId, limit, offset));
    }

    @Override
    public Documents getConsumerDocuments(UUID agreementId) {
        return this.getConsumerDocuments(agreementId, 0, 30);
    }

    @Override
    public Documents getConsumerDocuments(UUID agreementId, int offset, int limit) {
        return this.mapper.mapToV2(this.agreementsApi.getAgreementConsumerDocuments(agreementId, offset, limit));
    }

    @Override
    public void setBearerToken(String bearerToken) {
        throw new UnsupportedOperationException(V3_UNSUPPORTED_BEARER_MSG);
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        this.agreementsApi.setApiClient(ApiClientUtils.createApiClient(super.getRestTemplate(), basePath, headers));
    }
}
