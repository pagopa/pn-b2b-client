package it.pagopa.pn.client.b2b.pa.service.impl;


import it.pagopa.pn.client.b2b.pa.config.PnBaseUrlConfig;
import it.pagopa.pn.client.b2b.pa.config.PnSafeStorageConfig;
import it.pagopa.pn.client.b2b.pa.service.IPnSafeStoragePrivateClient;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.ApiClient;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.api.AdditionalFileTagsApi;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.api.FileDownloadApi;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.api.FileUploadApi;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnSafeStoragePrivateClientImpl implements IPnSafeStoragePrivateClient {

    private final String apiKeySafeStorage;
    private final String clientIdSafeStorage;
    private final FileUploadApi fileUploadApi;
    private final FileDownloadApi fileDownloadApi;
    private final AdditionalFileTagsApi additionalFileTagsApi;
    private final String baseUrl;

    @Autowired
    public PnSafeStoragePrivateClientImpl(RestTemplate restTemplate,
                                          PnBaseUrlConfig pnBaseUrlConfig,
                                          PnSafeStorageConfig pnSafeStorageConfig) {
        this.apiKeySafeStorage = pnSafeStorageConfig.getApiKeySafeStorage();
        this.clientIdSafeStorage = pnSafeStorageConfig.getClientIdSafeStorage();
        this.baseUrl = pnBaseUrlConfig.getSafeStorageBaseUrl();
        fileUploadApi = new FileUploadApi(newApiClient(restTemplate, baseUrl, apiKeySafeStorage));
        fileDownloadApi = new FileDownloadApi(newApiClient(restTemplate, baseUrl, apiKeySafeStorage));
        additionalFileTagsApi = new AdditionalFileTagsApi(newApiClient(restTemplate, baseUrl, apiKeySafeStorage));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String apiKey) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("x-api-key", apiKey);
        return newApiClient;
    }

    public FileCreationResponse createFile(
            String xChecksumValue, String xChecksum, FileCreationRequest fileCreationRequest) throws RestClientException {
        return this.fileUploadApi.createFile(clientIdSafeStorage, xChecksumValue, xChecksum, fileCreationRequest);
    }

    public ResponseEntity<FileCreationResponse> createFileWithHttpInfo(
            String cxId, String xChecksumValue, String xChecksum, FileCreationRequest fileCreationRequest) throws RestClientException {
        return this.fileUploadApi.createFileWithHttpInfo(cxId, xChecksumValue, xChecksum, fileCreationRequest);
    }

    public FileDownloadResponse getFile(String fileKey, Boolean metadataOnly, Boolean tags) throws RestClientException {
        return this.fileDownloadApi.getFile(fileKey, clientIdSafeStorage, metadataOnly, tags);
    }

    public ResponseEntity<FileDownloadResponse> getFileWithHttpInfo(
            String fileKey, String cxId, Boolean metadataOnly, Boolean tags) throws RestClientException {
        return this.fileDownloadApi.getFileWithHttpInfo(fileKey, cxId, metadataOnly, tags);
    }

    public AdditionalFileTagsGetResponse additionalFileTagsGet(String fileKey) throws RestClientException {
        return this.additionalFileTagsApi.additionalFileTagsGet(fileKey, clientIdSafeStorage);
    }

    public ResponseEntity<AdditionalFileTagsGetResponse> additionalFileTagsGetWithHttpInfo(
            String fileKey, String cxId) throws RestClientException {
        return this.additionalFileTagsApi.additionalFileTagsGetWithHttpInfo(fileKey, cxId);
    }

    public AdditionalFileTagsSearchResponse additionalFileTagsSearch(
            String logic, Boolean tags, Map<String, String> tagParams) throws RestClientException {
        return this.additionalFileTagsApi.additionalFileTagsSearch(clientIdSafeStorage, logic, tags, tagParams);
    }

    public ResponseEntity<AdditionalFileTagsSearchResponse> additionalFileTagsSearchWithHttpInfo(
            String cxId, String logic, Boolean tags, Map<String, String> tagParams) throws RestClientException {
        return this.additionalFileTagsApi.additionalFileTagsSearchWithHttpInfo(cxId, logic, tags, tagParams);
    }

    public AdditionalFileTagsUpdateResponse additionalFileTagsUpdate(
            String fileKey, AdditionalFileTagsUpdateRequest additionalFileTagsUpdateRequest) throws RestClientException {
        return this.additionalFileTagsApi.additionalFileTagsUpdate(fileKey, clientIdSafeStorage, additionalFileTagsUpdateRequest);
    }

    public ResponseEntity<AdditionalFileTagsUpdateResponse> additionalFileTagsUpdateWithHttpInfo(
            String fileKey, String cxId, AdditionalFileTagsUpdateRequest additionalFileTagsUpdateRequest) throws RestClientException {
        return this.additionalFileTagsApi.additionalFileTagsUpdateWithHttpInfo(fileKey, cxId, additionalFileTagsUpdateRequest);
    }

    @Override
    public AdditionalFileTagsMassiveUpdateResponse additionalFileTagsMassiveUpdate(
            AdditionalFileTagsMassiveUpdateRequest additionalFileTagsMassiveUpdateRequest) throws RestClientException {
        return this.additionalFileTagsApi.additionalFileTagsMassiveUpdate(clientIdSafeStorage, additionalFileTagsMassiveUpdateRequest);
    }

    @Override
    public ResponseEntity<AdditionalFileTagsMassiveUpdateResponse> additionalFileTagsMassiveUpdateWithHttpInfo(
            String cxId, AdditionalFileTagsMassiveUpdateRequest additionalFileTagsMassiveUpdateRequest) throws RestClientException {
        return this.additionalFileTagsApi.additionalFileTagsMassiveUpdateWithHttpInfo(cxId, additionalFileTagsMassiveUpdateRequest);
    }
}
