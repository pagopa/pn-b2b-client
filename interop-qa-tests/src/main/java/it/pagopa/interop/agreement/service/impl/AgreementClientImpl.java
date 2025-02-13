package it.pagopa.interop.agreement.service.impl;

import it.pagopa.interop.agreement.service.IAgreementClient;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.AgreementsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.Agreement;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementRejectionPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementSubmissionPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import java.io.File;
import java.util.UUID;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AgreementClientImpl implements IAgreementClient {
    private final AgreementsApi agreementsApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public AgreementClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getBaseUrl();
        this.agreementsApi = new AgreementsApi(createApiClient("dummyBearer"));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public CreatedResource createAgreement(String xCorrelationId, AgreementPayload agreementPayload) {
        return agreementsApi.createAgreement(agreementPayload);
    }

    @Override
    public Agreement getAgreementById(String xCorrelationId, UUID agreementId) {
        return agreementsApi.getAgreementById(xCorrelationId, agreementId);
    }

    @Override
    public Agreement activateAgreement(String xCorrelationId, UUID agreementId) {
        return agreementsApi.activateAgreement(xCorrelationId, agreementId);
    }

    @Override
    public Agreement submitAgreement(String xCorrelationId, UUID agreementId, AgreementSubmissionPayload agreementSubmissionPayload) {
        return agreementsApi.submitAgreement(xCorrelationId, agreementId, agreementSubmissionPayload);
    }

    @Override
    public Agreement suspendAgreement(String xCorrelationId, UUID agreementId) {
        return agreementsApi.suspendAgreement(xCorrelationId, agreementId);
    }

    @Override
    public void archiveAgreement(String xCorrelationId, UUID agreementId) {
        agreementsApi.archiveAgreement(xCorrelationId, agreementId);
    }

    @Override
    public Agreement rejectAgreement(String xCorrelationId, UUID agreementId, AgreementRejectionPayload agreementRejectionPayload) {
        return agreementsApi.rejectAgreement(xCorrelationId, agreementId, agreementRejectionPayload);
    }

    @Override
    public File addAgreementConsumerDocument(String xCorrelationId, UUID agreementId, String name, String prettyName, org.springframework.core.io.Resource doc) {
        return agreementsApi.addAgreementConsumerDocument(xCorrelationId, agreementId, name, prettyName, doc);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.agreementsApi.setApiClient(createApiClient(bearerToken));
    }

}
