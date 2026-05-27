package it.pagopa.pn.client.b2b.pa.service.impl;


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
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnRaddVpceAdapter implements IPnRaddAlternativeClient {

    private final PnRaddNetVpceClientImpl vpce;

    public PnRaddVpceAdapter(PnRaddNetVpceClientImpl vpce) {
        this.vpce = vpce;
    }

    @Override
    public ActInquiryResponse actInquiry(
            String uid,
            String recipientTaxId,
            String recipientType,
            String qrCode,
            String iun
    ) {
        // qui VPCE ritorna un model diverso
        // per ora workaround:
        throw new UnsupportedOperationException("Mapping required VPCE -> ALT");
    }

    @Override
    public byte[] documentDownload(String operationType, String operationId, String attachmentId) {
        return vpce.documentDownload(operationType, operationId, attachmentId);
    }

    @Override
    public RequestResponse retrieveRequestItems(String uid, String requestId, Integer limit, String lastKey) throws RestClientException {
        return null;
    }

    @Override
    public RegistryUploadResponse uploadRegistryRequests(String uid, RegistryUploadRequest registryUploadRequest) throws RestClientException {
        return null;
    }

    @Override
    public VerifyRequestResponse verifyRequest(String uid, String requestId) throws RestClientException {
        return null;
    }

    @Override
    public CreateRegistryResponse addRegistry(String uid, CreateRegistryRequest createRegistryRequest) throws RestClientException {
        return null;
    }

    @Override
    public void deleteRegistry(String uid, String registryId, String endDate) throws RestClientException {

    }

    @Override
    public RegistriesResponse retrieveRegistries(String uid, Integer limit, String lastKey, String cap, String city, String pr, String externalCode) throws RestClientException {
        return null;
    }

    @Override
    public void updateRegistry(String uid, String registryId, UpdateRegistryRequest updateRegistryRequest) throws RestClientException {

    }

     @Override
    public AbortTransactionResponse abortActTransaction(String uid, AbortTransactionRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompleteTransactionResponse completeActTransaction(String uid, CompleteTransactionRequest completeTransactionRequest) throws RestClientException {
        return null;
    }

    @Override
    public StartTransactionResponse startActTransaction(String uid, ActStartTransactionRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AORInquiryResponse aorInquiry(String uid, String recipientTaxId, String recipientType) throws RestClientException {
        return null;
    }

    @Override
    public AbortTransactionResponse abortAorTransaction(String uid, AbortTransactionRequest abortTransactionRequest) throws RestClientException {
        return null;
    }

    @Override
    public CompleteTransactionResponse completeAorTransaction(String uid, CompleteTransactionRequest completeTransactionRequest) throws RestClientException {
        return null;
    }

    @Override
    public it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.StartTransactionResponse startAorTransaction(String uid, AorStartTransactionRequest aorStartTransactionRequest) throws RestClientException {
        return null;
    }

    @Override
    public DocumentUploadResponse documentUpload(String uid, DocumentUploadRequest documentUploadRequest) throws RestClientException {
        return null;
    }

    @Override
    public OperationsActDetailsResponse getActPracticesByInternalId(String internalId, FilterRequest filterRequest) throws RestClientException {
        return null;
    }

    @Override
    public OperationsResponse getActPracticesByIun(String iun) throws RestClientException {
        return null;
    }

    @Override
    public OperationActResponse getActTransactionByOperationId(String transactionId) throws RestClientException {
        return null;
    }

    @Override
    public OperationsAorDetailsResponse getAorPracticesByInternalId(String internalId, FilterRequest filterRequest) throws RestClientException {
        return null;
    }

    @Override
    public OperationsResponse getAorPracticesByIun(String iun) throws RestClientException {
        return null;
    }

    @Override
    public OperationAorResponse getAorTransactionByOperationId(String transactionId) throws RestClientException {
        return null;
    }

    @Override
    public boolean setAuthTokenRadd(AuthTokenRaddType bearerToken) {
        return false;
    }

    @Override
    public AuthTokenRaddType getAuthTokenRaddSetted() {
        return null;
    }
}

