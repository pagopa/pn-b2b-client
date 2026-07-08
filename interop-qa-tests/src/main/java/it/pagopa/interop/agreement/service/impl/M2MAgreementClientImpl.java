package it.pagopa.interop.agreement.service.impl;

import it.pagopa.interop.agreement.service.IM2MAgreementClient;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.api.AgreementsApi;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.*;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

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
    public Document uploadConsumerDocument(UUID agreementId, Resource document, String prettyName) {
        return this.agreementsApi.uploadAgreementConsumerDocument(agreementId, document, prettyName);
    }

    @Override
    public FileDownloadMultipart getConsumerDocument(UUID agreementId, UUID documentId) {
        return this.agreementsApi.downloadAgreementConsumerDocument(agreementId, documentId);
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
