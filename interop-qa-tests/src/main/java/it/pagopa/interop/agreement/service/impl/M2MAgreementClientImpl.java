package it.pagopa.interop.agreement.service.impl;

import it.pagopa.interop.agreement.service.IM2MAgreementClient;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.api.AgreementsApi;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Agreement;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementSubmission;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Agreements;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DelegationRef;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Documents;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purposes;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@ToString
@EqualsAndHashCode
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Primary
public class M2MAgreementClientImpl implements IM2MAgreementClient {
    private final AgreementsApi agreementsApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public M2MAgreementClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getM2mBaseUrl();
        this.agreementsApi = new AgreementsApi(createApiClient("dummyBearer"));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);

        apiClient.setBearerToken(bearerToken);

        return apiClient;
    }

    @Override
    public Agreement createAgreement(AgreementSeed agreementSeed) {
        return agreementsApi.createAgreement(agreementSeed);
    }

    @Override
    public Agreement submitAgreement(UUID agreementId, AgreementSubmission agreementSubmission) {
        return agreementsApi.submitAgreement(agreementId, agreementSubmission);
    }

    @Override
    public Agreement approveAgreement(UUID agreementId, DelegationRef delegationRef) {
        return agreementsApi.approveAgreement(agreementId, delegationRef);
    }

    @Override
    public Agreement approveAgreement(UUID agreementId) {
        return agreementsApi.approveAgreement(agreementId, null);
    }

    @Override
    public Agreement unsuspendAgreement(UUID agreementId, DelegationRef delegationRef) {
        return agreementsApi.unsuspendAgreement(agreementId, delegationRef);
    }

    @Override
    public Agreement unsuspendAgreement(UUID agreementId) {
        return agreementsApi.unsuspendAgreement(agreementId, null);
    }

    @Override
    public Agreements getAgreements(AgreementsListRequest listRequest) {
        return agreementsApi.getAgreements(
            listRequest.getOffset(),
            listRequest.getLimit(),
            listRequest.getStates(),
            listRequest.getProducersIds(),
            listRequest.getConsumersIds(),
            listRequest.getDescriptorsIds(),
            listRequest.getEservicesIds()
        );
    }

    @Override
    public Agreement getAgreementById(UUID id) {
        return agreementsApi.getAgreement(id);
    }

    @Override
    public Purposes getAgreementPurposes(UUID agreementId) {
        return this.getAgreementPurposes(agreementId, 30, 0);
    }

    @Override
    public Purposes getAgreementPurposes(UUID agreementId, int limit, int offset) {
        return this.agreementsApi.getAgreementPurposes(agreementId, limit, offset);
    }

    @Override
    public Documents getConsumerDocuments(UUID agreementId) {
        return this.getConsumerDocuments(agreementId, 0, 30);
    }

    @Override
    public Documents getConsumerDocuments(UUID agreementId, int offset, int limit) {
        return this.agreementsApi.getAgreementConsumerDocuments(agreementId, offset, limit);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.agreementsApi.setApiClient(createApiClient(bearerToken));
    }
}
