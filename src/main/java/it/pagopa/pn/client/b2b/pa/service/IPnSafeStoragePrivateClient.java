package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

public interface IPnSafeStoragePrivateClient {

    FileCreationResponse createFile(FileCreationRequest fileCreationRequest) throws RestClientException;

    ResponseEntity<FileCreationResponse> createFileWithHttpInfo(String cxId, FileCreationRequest fileCreationRequest) throws RestClientException;
    FileDownloadResponse getFile(String fileKey, Boolean metadataOnly, Boolean tags) throws RestClientException;

    ResponseEntity<FileDownloadResponse> getFileWithHttpInfo(String fileKey, String cxId, Boolean metadataOnly, Boolean tags) throws RestClientException;
    OperationResultCodeResponse updateFileMetadata(String fileKey, UpdateFileMetadataRequest updateFileMetadataRequest) throws RestClientException;
    ResponseEntity<OperationResultCodeResponse> updateFileMetadataWithHttpInfo(String fileKey, String cxId, UpdateFileMetadataRequest updateFileMetadataRequest) throws RestClientException;

    AdditionalFileTagsGetResponse additionalFileTagsGet(String fileKey, String xPagopaSafestorageCxId) throws RestClientException;

    AdditionalFileTagsSearchResponse additionalFileTagsSearch(String xPagopaSafestorageCxId, String logic, Boolean tags) throws RestClientException;
    AdditionalFileTagsUpdateResponse additionalFileTagsUpdate(String fileKey, String xPagopaSafestorageCxId, AdditionalFileTagsUpdateRequest additionalFileTagsUpdateRequest) throws RestClientException;

}
