package it.pagopa.pn.client.b2b.pa.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.pn.client.b2b.pa.service.IPnRaddAlternativeClient;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.*;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD.CreateRegistryRequest;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD.CreateRegistryResponse;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD.RegistriesResponse;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD.UpdateRegistryRequest;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.RegistryUploadRequest;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.RegistryUploadResponse;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.RequestResponse;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.VerifyRequestResponse;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.privateb2braddalt.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class PnRaddVpceAdapter implements IPnRaddAlternativeClient {

    private final PnRaddNetVpceClientImpl vpceClient;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    public PnRaddVpceAdapter(PnRaddNetVpceClientImpl vpceClient) {
        this.vpceClient = vpceClient;
    }

    @Override
    public ActInquiryResponse actInquiry(String uid, String recipientTaxId, String recipientType, String qrCode, String iun) {
        log.info("USING VPCE ADAPTER - actInquiry");

        var vpce = vpceClient.actInquiry(uid, recipientTaxId, recipientType, qrCode, iun);

        try {
            return objectMapper.convertValue(vpce, it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.ActInquiryResponse.class);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error mapping VPCE -> ALT ActInquiryResponse", e);
        }
    }

    @Override
    public byte[] documentDownload(String operationType, String operationId, String attachmentId) {
        return vpceClient.documentDownload(operationType, operationId, attachmentId);
    }

    @Override
    public RequestResponse retrieveRequestItems(String uid, String requestId, Integer limit, String lastKey) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public RegistryUploadResponse uploadRegistryRequests(String uid, RegistryUploadRequest registryUploadRequest) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public VerifyRequestResponse verifyRequest(String uid, String requestId) throws RestClientException {
        return null;
    }

    @Override
    public CreateRegistryResponse addRegistry(String uid, CreateRegistryRequest createRegistryRequest) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteRegistry(String uid, String registryId, String endDate) throws RestClientException {

    }

    @Override
    public RegistriesResponse retrieveRegistries(String uid, Integer limit, String lastKey, String cap, String city, String pr, String externalCode) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateRegistry(String uid, String registryId, UpdateRegistryRequest updateRegistryRequest) throws RestClientException {

    }

    @Override
    public AbortTransactionResponse abortActTransaction(String uid, AbortTransactionRequest request) {
        log.info("USING VPCE ADAPTER - abortActTransaction");

        var vpceRequest = objectMapper.convertValue(request, it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.model_RaddNetVpce.AbortTransactionRequest.class);
        var vpceResponse = vpceClient.abortActTransaction(uid, vpceRequest);
        return objectMapper.convertValue(vpceResponse, it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.AbortTransactionResponse.class);
    }

    @Override
    public CompleteTransactionResponse completeActTransaction(String uid, CompleteTransactionRequest request) {
        log.info("USING VPCE ADAPTER - completeActTransaction");

        var vpceRequest = objectMapper.convertValue(request, it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.model_RaddNetVpce.CompleteTransactionRequest.class);
        var vpceResponse = vpceClient.completeActTransaction(uid, vpceRequest);
        return objectMapper.convertValue(vpceResponse, it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.CompleteTransactionResponse.class);
    }

    @Override
    public StartTransactionResponse startActTransaction(String uid, ActStartTransactionRequest request) {
        log.info("USING VPCE ADAPTER - startActTransaction");

        var vpceRequest = objectMapper.convertValue(request, it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.model_RaddNetVpce.ActStartTransactionRequest.class);
        var vpceResponse = vpceClient.startActTransaction(uid, vpceRequest);
        return objectMapper.convertValue(vpceResponse, it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.StartTransactionResponse.class);
    }

    @Override
    public AORInquiryResponse aorInquiry(String uid, String recipientTaxId, String recipientType) {
        log.info("USING VPCE ADAPTER - aorInquiry");

        var vpceResponse = vpceClient.aorInquiry(uid, recipientTaxId, recipientType);
        return objectMapper.convertValue(vpceResponse, it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.AORInquiryResponse.class);
    }

    @Override
    public it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.StartTransactionResponse startAorTransaction(String uid, AorStartTransactionRequest request) throws RestClientException {
        log.info("USING VPCE ADAPTER - startAorTransaction");

        var vpceRequest = objectMapper.convertValue(request, it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.model_RaddNetVpce.AorStartTransactionRequest.class);
        var vpceResponse = vpceClient.startAorTransaction(uid, vpceRequest);
        return objectMapper.convertValue(vpceResponse, it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.StartTransactionResponse.class);
    }

    @Override
    public CompleteTransactionResponse completeAorTransaction(String uid, CompleteTransactionRequest request) throws RestClientException {
        log.info("USING VPCE ADAPTER - completeAorTransaction");

        var vpceRequest = objectMapper.convertValue(request, it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.model_RaddNetVpce.CompleteTransactionRequest.class);
        var vpceResponse = vpceClient.completeAorTransaction(uid, vpceRequest);
        return objectMapper.convertValue(vpceResponse, it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.CompleteTransactionResponse.class);
    }

    @Override
    public AbortTransactionResponse abortAorTransaction(String uid, AbortTransactionRequest request) throws RestClientException {
        log.info("USING VPCE ADAPTER - abortAorTransaction");

        var vpceRequest = objectMapper.convertValue(request, it.pagopa.pn.client.b2b.radd.generated.openapi.clients.vpce.model_RaddNetVpce.AbortTransactionRequest.class);
        var vpceResponse = vpceClient.abortAorTransaction(uid, vpceRequest);
        return objectMapper.convertValue(vpceResponse, it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.AbortTransactionResponse.class);
    }

    @Override
    public DocumentUploadResponse documentUpload(String uid, DocumentUploadRequest documentUploadRequest) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public OperationsActDetailsResponse getActPracticesByInternalId(String internalId, FilterRequest filterRequest) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public OperationsResponse getActPracticesByIun(String iun) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public OperationActResponse getActTransactionByOperationId(String transactionId) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public OperationsAorDetailsResponse getAorPracticesByInternalId(String internalId, FilterRequest filterRequest) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public OperationsResponse getAorPracticesByIun(String iun) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public OperationAorResponse getAorTransactionByOperationId(String transactionId) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setAuthTokenRadd(AuthTokenRaddType bearerToken) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AuthTokenRaddType getAuthTokenRaddSetted() {
        throw new UnsupportedOperationException();
    }
}

