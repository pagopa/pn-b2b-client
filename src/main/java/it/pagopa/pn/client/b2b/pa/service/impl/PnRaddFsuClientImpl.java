package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.config.PnBaseUrlConfig;
import it.pagopa.pn.client.b2b.pa.service.IPnRaddFsuClient;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.internalb2bradd.ApiClient;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.internalb2bradd.api.*;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.internalb2bradd.model.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


@Component
public class PnRaddFsuClientImpl implements IPnRaddFsuClient {

    private final String baseUrl;
    private final ActDocumentInquiryApi actDocumentInquiryApi;
    private final ActTransactionManagementApi actTransactionManagementApi;
    private final AorDocumentInquiryApi aorDocumentInquiryApi;
    private final AorTransactionManagementApi aorTransactionManagementApi;
    private final DocumentUploadApi documentUploadApi;
    private final NotificationInquiryApi notificationInquiryApi;


    public PnRaddFsuClientImpl(RestTemplate restTemplate, PnBaseUrlConfig pnBaseUrlConfig) {
        this.baseUrl = pnBaseUrlConfig.getRaddBaseUrl();
        this.actDocumentInquiryApi = new ActDocumentInquiryApi(newApiClient(restTemplate, baseUrl));
        this.actTransactionManagementApi = new ActTransactionManagementApi(newApiClient(restTemplate, baseUrl));
        this.aorDocumentInquiryApi = new AorDocumentInquiryApi(newApiClient(restTemplate, baseUrl));
        this.aorTransactionManagementApi = new AorTransactionManagementApi(newApiClient(restTemplate, baseUrl));
        this.documentUploadApi = new DocumentUploadApi(newApiClient(restTemplate, baseUrl));
        this.notificationInquiryApi = new NotificationInquiryApi(newApiClient(restTemplate, baseUrl));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        return newApiClient;
    }

    public ActInquiryResponse actInquiry(String uid, String recipientTaxId, String recipientType, String qrCode) throws RestClientException {
        return this.actDocumentInquiryApi.actInquiryWithHttpInfo(uid, recipientTaxId, recipientType, qrCode).getBody();
    }

    public AbortTransactionResponse abortActTransaction(String uid, AbortTransactionRequest abortTransactionRequest) throws RestClientException {
        return this.actTransactionManagementApi.abortActTransactionWithHttpInfo(uid, abortTransactionRequest).getBody();
    }

    public CompleteTransactionResponse completeActTransaction(String uid, CompleteTransactionRequest completeTransactionRequest) throws RestClientException {
        return this.actTransactionManagementApi.completeActTransactionWithHttpInfo(uid, completeTransactionRequest).getBody();
    }

    public StartTransactionResponse startActTransaction(String uid, ActStartTransactionRequest actStartTransactionRequest) throws RestClientException {
        return this.actTransactionManagementApi.startActTransactionWithHttpInfo(uid, actStartTransactionRequest).getBody();
    }

    public AORInquiryResponse aorInquiry(String uid, String recipientTaxId, String recipientType) throws RestClientException {
        return this.aorDocumentInquiryApi.aorInquiryWithHttpInfo(uid, recipientTaxId, recipientType).getBody();
    }

    public AbortTransactionResponse abortAorTransaction(String uid, AbortTransactionRequest abortTransactionRequest) throws RestClientException {
        return this.aorTransactionManagementApi.abortAorTransactionWithHttpInfo(uid, abortTransactionRequest).getBody();
    }

    public CompleteTransactionResponse completeAorTransaction(String uid, CompleteTransactionRequest completeTransactionRequest) throws RestClientException {
        return this.aorTransactionManagementApi.completeAorTransactionWithHttpInfo(uid, completeTransactionRequest).getBody();
    }

    public StartTransactionResponse startAorTransaction(String uid, AorStartTransactionRequest aorStartTransactionRequest) throws RestClientException {
        return this.aorTransactionManagementApi.startAorTransactionWithHttpInfo(uid, aorStartTransactionRequest).getBody();
    }

    public DocumentUploadResponse documentUpload(String uid, DocumentUploadRequest documentUploadRequest) throws RestClientException {
        return this.documentUploadApi.documentUploadWithHttpInfo(uid, documentUploadRequest).getBody();
    }

    public OperationsActDetailsResponse getActPracticesByInternalId(String internalId, FilterRequest filterRequest) throws RestClientException {
        return this.notificationInquiryApi.getActPracticesByInternalIdWithHttpInfo(internalId, filterRequest).getBody();
    }

    public OperationsResponse getActPracticesByIun(String iun) throws RestClientException {
        return this.notificationInquiryApi.getActPracticesByIunWithHttpInfo(iun).getBody();
    }

    public OperationActResponse getActTransactionByOperationId(String operationId) throws RestClientException {
        return this.notificationInquiryApi.getActTransactionByOperationIdWithHttpInfo(operationId).getBody();
    }

    public OperationsAorDetailsResponse getAorPracticesByInternalId(String internalId, FilterRequest filterRequest) throws RestClientException {
        return this.notificationInquiryApi.getAorPracticesByInternalIdWithHttpInfo(internalId, filterRequest).getBody();
    }

    public OperationsResponse getAorPracticesByIun(String iun) throws RestClientException {
        return this.notificationInquiryApi.getAorPracticesByIunWithHttpInfo(iun).getBody();
    }

    public OperationAorResponse getAorTransactionByOperationId(String operationId) throws RestClientException {
        return this.notificationInquiryApi.getAorTransactionByOperationIdWithHttpInfo(operationId).getBody();
    }
}