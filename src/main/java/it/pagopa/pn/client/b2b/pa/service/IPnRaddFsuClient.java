package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.internalb2bradd.model.*;
import org.springframework.web.client.RestClientException;

public interface IPnRaddFsuClient {

    ActInquiryResponse actInquiry(String uid, String recipientTaxId, String recipientType, String qrCode) throws RestClientException;
    AbortTransactionResponse abortActTransaction(String uid, AbortTransactionRequest abortTransactionRequest) throws RestClientException;
    CompleteTransactionResponse completeActTransaction(String uid, CompleteTransactionRequest completeTransactionRequest) throws RestClientException;
    StartTransactionResponse startActTransaction(String uid, ActStartTransactionRequest actStartTransactionRequest) throws RestClientException;
    AORInquiryResponse aorInquiry(String uid, String recipientTaxId, String recipientType) throws RestClientException;
    AbortTransactionResponse abortAorTransaction(String uid, AbortTransactionRequest abortTransactionRequest) throws RestClientException;
    CompleteTransactionResponse completeAorTransaction(String uid, CompleteTransactionRequest completeTransactionRequest) throws RestClientException;
    StartTransactionResponse startAorTransaction(String uid, AorStartTransactionRequest aorStartTransactionRequest) throws RestClientException;
    DocumentUploadResponse documentUpload(String uid, DocumentUploadRequest documentUploadRequest) throws RestClientException;
    OperationsActDetailsResponse getActPracticesByInternalId(String internalId, FilterRequest filterRequest) throws RestClientException;
    OperationsResponse getActPracticesByIun(String iun) throws RestClientException;
    OperationActResponse getActTransactionByOperationId(String operationId) throws RestClientException;
    OperationsAorDetailsResponse getAorPracticesByInternalId(String internalId, FilterRequest filterRequest) throws RestClientException;
    OperationsResponse getAorPracticesByIun(String iun) throws RestClientException;
    OperationAorResponse getAorTransactionByOperationId(String operationId) throws RestClientException;

}
