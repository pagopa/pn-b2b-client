package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.pa.service.utils.SettableAuthTokenRadd;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.*;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD_V2.CreateRegistryRequestV2;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD_V2.GetRegistryResponseV2;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD_V2.RegistryV2;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD_V2.UpdateRegistryRequestV2;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.RegistryUploadRequest;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.RegistryUploadResponse;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCsv.VerifyRequestResponse;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.privateb2braddalt.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;


public interface IPnRaddAlternativeV2Client extends SettableAuthTokenRadd {
    ActInquiryResponse actInquiry( String uid, String recipientTaxId, String recipientType, String qrCode, String iun) throws RestClientException;
    AbortTransactionResponse abortActTransaction(String uid, AbortTransactionRequest abortTransactionRequest) throws RestClientException;
    CompleteTransactionResponse completeActTransaction(String uid, CompleteTransactionRequest completeTransactionRequest) throws RestClientException;
    StartTransactionResponse startActTransaction(String uid, ActStartTransactionRequest actStartTransactionRequest) throws RestClientException;
    AORInquiryResponse aorInquiry( String uid, String recipientTaxId, String recipientType) throws RestClientException;
    AbortTransactionResponse abortAorTransaction(String uid, AbortTransactionRequest abortTransactionRequest) throws RestClientException;
    CompleteTransactionResponse completeAorTransaction(String uid,  CompleteTransactionRequest completeTransactionRequest) throws RestClientException;
    StartTransactionResponse startAorTransaction(String uid,  AorStartTransactionRequest aorStartTransactionRequest) throws RestClientException;
    DocumentUploadResponse documentUpload( String uid, DocumentUploadRequest documentUploadRequest) throws RestClientException;
    OperationsActDetailsResponse getActPracticesByInternalId(String internalId, FilterRequest filterRequest) throws RestClientException;
    OperationsResponse getActPracticesByIun(String iun) throws RestClientException;
    OperationActResponse getActTransactionByOperationId(String transactionId) throws RestClientException;
    OperationsAorDetailsResponse getAorPracticesByInternalId(String internalId, FilterRequest filterRequest) throws RestClientException;
    OperationsResponse getAorPracticesByIun(String iun) throws RestClientException;
    OperationAorResponse getAorTransactionByOperationId(String transactionId) throws RestClientException;
    byte[] documentDownload(String operationType, String operationId, String attachmentId) throws RestClientException;
    RegistryUploadResponse uploadRegistryRequests(String uid, RegistryUploadRequest registryUploadRequest) throws RestClientException;
    VerifyRequestResponse verifyRequest(String uid, String requestId) throws RestClientException;

    //CreateRegistryResponse addRegistry(String uid, CreateRegistryRequest createRegistryRequest) throws RestClientException;
    RegistryV2 addRegistry(String partnerId, CreateRegistryRequestV2 createRegistryRequestV2) throws RestClientException;

    //void deleteRegistry(String uid, String registryId, String endDate) throws RestClientException;
    void deleteRegistry(String partnerId, String locationId);

    //RegistriesResponse retrieveRegistries(String uid, Integer limit, String lastKey, String cap, String city, String pr, String externalCode) throws RestClientException;
     GetRegistryResponseV2 retrieveRegistries(String xPagopaPnCxId, Integer limit, String lastKey) throws RestClientException;


    //void updateRegistry(String uid, String registryId, UpdateRegistryRequest updateRegistryRequest) throws RestClientException;
    RegistryV2 updateRegistry(String partnerId, String locationId, UpdateRegistryRequestV2 updateRegistryRequestV2);

    ResponseEntity<Void> deleteRegistryWithHttpInfo(String partnerId, String locationId);
}
