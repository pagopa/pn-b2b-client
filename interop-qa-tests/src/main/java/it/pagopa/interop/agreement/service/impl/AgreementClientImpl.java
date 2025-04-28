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
    public CreatedResource createAgreement(AgreementPayload agreementPayload) {
        return agreementsApi.createAgreement(agreementPayload);
    }

    @Override
    public Agreement getAgreementById(UUID agreementId) {
        return agreementsApi.getAgreementById(agreementId);
    }

    @Override
    public Agreement activateAgreement(UUID agreementId) {
        return agreementsApi.activateAgreement(agreementId);
    }

    @Override
    public Agreement submitAgreement(UUID agreementId, AgreementSubmissionPayload agreementSubmissionPayload) {
        return agreementsApi.submitAgreement(agreementId, agreementSubmissionPayload);
    }

    @Override
    public Agreement suspendAgreement(UUID agreementId) {
        return agreementsApi.suspendAgreement(agreementId);
    }

    @Override
    public void archiveAgreement(UUID agreementId) {
        agreementsApi.archiveAgreement(agreementId);
    }

    @Override
    public Agreement rejectAgreement(UUID agreementId, AgreementRejectionPayload agreementRejectionPayload) {
        return agreementsApi.rejectAgreement(agreementId, agreementRejectionPayload);
    }

    @Override
    public File addAgreementConsumerDocument(UUID agreementId, String name, String prettyName, org.springframework.core.io.Resource doc) {
        return agreementsApi.addAgreementConsumerDocument(agreementId, name, prettyName, doc);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.agreementsApi.setApiClient(createApiClient(bearerToken));
    }

}
