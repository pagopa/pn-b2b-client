package it.pagopa.interop.agreement.service.impl;

import it.pagopa.interop.agreement.service.IAgreementClient;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.AgreementsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.Agreement;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementRejectionPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementState;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementSubmissionPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementUpdatePayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.Agreements;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactEServicesLight;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganizations;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationRef;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Retryable(
        retryFor = { HttpServerErrorException.class },
        backoff = @Backoff(delay = 2000)
)
public class AgreementClientImpl implements IAgreementClient {
    private final AgreementsApi agreementsApi;
    private final RestTemplate restTemplate;
    private final String basePath;
    Map<HttpStatus, Runnable> statusActionMap = Map.of(
            HttpStatus.BAD_REQUEST, () -> { throw new HttpClientErrorException(HttpStatus.BAD_REQUEST); },
            HttpStatus.FORBIDDEN, () -> { throw new HttpClientErrorException(HttpStatus.FORBIDDEN); },
            HttpStatus.INTERNAL_SERVER_ERROR, () -> { throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR); },
            HttpStatus.NOT_FOUND, () -> { throw new HttpClientErrorException(HttpStatus.NOT_FOUND); });

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
    public  ResponseEntity<Void> getAgreementContract(UUID agreementId) {
        AtomicReference<HttpStatus> statusRef = new AtomicReference<>();
        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            ClientHttpResponse response = execution.execute(request, body);
            statusRef.set(response.getStatusCode());
            return response;
        };
        restTemplate.getInterceptors().add(interceptor);

        try {
            agreementsApi.getAgreementContractWithHttpInfo(agreementId);
        } catch (Exception ignored) {}

        Runnable action = statusActionMap.get(statusRef.get());
        if (action != null) action.run();
        return new ResponseEntity<>(statusRef.get());
    }

    public Agreement activateAgreement(UUID agreementId, DelegationRef delegationRef) {
        return agreementsApi.approveAgreement(agreementId, delegationRef);
    }

    @Override
    public Agreement activateAgreement(UUID agreementId) {
        return agreementsApi.approveAgreement(agreementId, null);
    }

    @Override
    public Agreement submitAgreement(UUID agreementId, AgreementSubmissionPayload agreementSubmissionPayload) {
        return agreementsApi.submitAgreement(agreementId, agreementSubmissionPayload);
    }

    public Agreement suspendAgreement(UUID agreementId, DelegationRef delegationRef) {
        return agreementsApi.suspendAgreement(agreementId, delegationRef);
    }

    @Override
    public Agreement suspendAgreement(UUID agreementId) {
        return agreementsApi.suspendAgreement(agreementId, null);
    }

    @Override
    public Agreement unsuspendAgreement(UUID agreementId) {
        return agreementsApi.unsuspendAgreement(agreementId, null);
    }

    @Override
    public Agreement unsuspendAgreement(UUID agreementId, UUID delegationId) {
        return agreementsApi.unsuspendAgreement(agreementId, new DelegationRef().delegationId(delegationId));
    }

    @Override
    public Agreement updateAgreement(UUID agreementId, AgreementUpdatePayload agreementUpdatePayload) {
        return agreementsApi.updateAgreement(agreementId, agreementUpdatePayload);
    }

    @Override
    public Agreement upgradeAgreement(UUID agreementId) {
        return agreementsApi.upgradeAgreement(agreementId);
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
    public ResponseEntity<Void> addAgreementConsumerDocument(UUID agreementId, String name, String prettyName, org.springframework.core.io.Resource doc) {
        AtomicReference<HttpStatus> statusRef = new AtomicReference<>();
        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            ClientHttpResponse response = execution.execute(request, body);
            statusRef.set(response.getStatusCode());
            return response;
        };
        restTemplate.getInterceptors().add(interceptor);

        try {
            agreementsApi.addAgreementConsumerDocument(agreementId, name, prettyName, doc);
        } catch (Exception ignored) {}

        Runnable action = statusActionMap.get(statusRef.get());
        if (action != null) action.run();
        return new ResponseEntity<>(statusRef.get());
    }

    @Override
    public CreatedResource cloneAgreement(UUID agreementId) {
        return agreementsApi.cloneAgreement(agreementId);
    }

    @Override
    public ResponseEntity<CompactOrganizations> getAgreementConsumers(Integer offset, Integer limit, String q) {
        return agreementsApi.getAgreementsConsumersWithHttpInfo(offset, limit, q);
    }

    @Override
    public ResponseEntity<CompactOrganizations> getAgreementProducers(Integer offset, Integer limit, String q) {
        return agreementsApi.getAgreementsProducersWithHttpInfo(offset, limit, q);
    }

    @Override
    public ResponseEntity<Void> getAgreementConsumerDocument(UUID agreementId, UUID documentId) {
        AtomicReference<HttpStatus> statusRef = new AtomicReference<>();
        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            ClientHttpResponse response = execution.execute(request, body);
            statusRef.set(response.getStatusCode());
            return response;
        };
        restTemplate.getInterceptors().add(interceptor);

        try {
            agreementsApi.getAgreementConsumerDocument(agreementId, documentId);
        } catch (Exception ignored) {}

        restTemplate.getInterceptors().remove(interceptor);
        Runnable action = statusActionMap.get(statusRef.get());
        if (action != null) action.run();
        return new ResponseEntity<>(statusRef.get());
    }

    @Override
    public void deleteAgreement(UUID agreementId) {
        agreementsApi.deleteAgreement(agreementId);
    }

    @Override
    public void removeAgreementConsumerDocument(UUID agreementId, UUID documentId) {
        agreementsApi.removeAgreementConsumerDocument(agreementId, documentId);
    }

    @Override
    public ResponseEntity<CompactEServicesLight> getAgreementEServiceConsumers(Integer offset, Integer limit, String q) {
        return agreementsApi.getAgreementsConsumerEServicesWithHttpInfo(offset, limit, q);
    }

    @Override
    public ResponseEntity<CompactEServicesLight> getAgreementEServiceProducers(Integer offset, Integer limit, String q) {
        return agreementsApi.getAgreementsProducerEServicesWithHttpInfo(offset, limit, q);
    }

    @Override
    public ResponseEntity<Agreements> getConsumerAgreements(Integer offset, Integer limit, List<UUID> eservicesIds, List<UUID> producersIds, List<AgreementState> states, Boolean showOnlyUpgradeable) {
        return agreementsApi.getConsumerAgreementsWithHttpInfo(offset, limit, eservicesIds, producersIds, states, showOnlyUpgradeable);
    }

    @Override
    public ResponseEntity<Agreements> getProducerAgreements(Integer offset, Integer limit, List<UUID> eservicesIds, List<UUID> consumersIds, List<AgreementState> states, Boolean showOnlyUpgradeable) {
        return agreementsApi.getProducerAgreementsWithHttpInfo(offset, limit, eservicesIds, consumersIds, states, showOnlyUpgradeable);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.agreementsApi.setApiClient(createApiClient(bearerToken));
    }

}
