package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.pa.service.utils.SettableAuthTokenRadd;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.privateb2braddalt.model.*;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.*;
import org.springframework.web.client.RestClientException;

public interface IPnRaddAlternativeClient extends SettableAuthTokenRadd {

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

    }
