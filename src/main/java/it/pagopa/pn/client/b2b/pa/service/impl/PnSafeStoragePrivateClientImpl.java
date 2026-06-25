package it.pagopa.pn.client.b2b.pa.service.impl;


import it.pagopa.pn.client.b2b.pa.service.IPnSafeStoragePrivateClient;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.ApiClient;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.api.AdditionalFileTagsApi;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.api.FileDownloadApi;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.api.FileUploadApi;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.model.*;
import org.springframework.beans.factory.annotation.Value;
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

    private final RestTemplate restTemplate;
    private final String safeStorageBaseUrl;
    private String clientIdSafeStorage;
    private FileUploadApi fileUploadApi;
    private FileDownloadApi fileDownloadApi;
    private AdditionalFileTagsApi additionalFileTagsApi;

    public PnSafeStoragePrivateClientImpl(RestTemplate restTemplate,
                                          @Value("${pn.safeStorage.base-url}") String safeStorageBaseUrl,
                                          @Value("${pn.safeStorage.apikey}") String apiKeySafeStorage,
                                          @Value("${pn.safeStorage.clientId}") String clientIdSafeStorage) {

        this.restTemplate = restTemplate;
        this.safeStorageBaseUrl = safeStorageBaseUrl;
        this.clientIdSafeStorage = clientIdSafeStorage;

        fileUploadApi = new FileUploadApi(newApiClient(restTemplate, safeStorageBaseUrl, apiKeySafeStorage));
        fileDownloadApi = new FileDownloadApi(newApiClient(restTemplate, safeStorageBaseUrl, apiKeySafeStorage));
        additionalFileTagsApi = new AdditionalFileTagsApi(newApiClient(restTemplate, safeStorageBaseUrl, apiKeySafeStorage));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String apiKey) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("x-api-key", apiKey);
        return newApiClient;
    }

    public void customApiClient(String clientName) {
        clientIdSafeStorage = clientName;
        fileUploadApi.setApiClient(newApiClient(restTemplate, safeStorageBaseUrl, clientName + "_api_key"));
        fileDownloadApi.setApiClient(newApiClient(restTemplate, safeStorageBaseUrl, clientName + "_api_key"));
        additionalFileTagsApi.setApiClient(newApiClient(restTemplate, safeStorageBaseUrl, clientName + "_api_key"));
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

    @Override
    public void setApiKey(String apiKey) {
        fileUploadApi.setApiClient(newApiClient(restTemplate, safeStorageBaseUrl, apiKey));
        fileDownloadApi.setApiClient(newApiClient(restTemplate, safeStorageBaseUrl, apiKey));
        additionalFileTagsApi.setApiClient(newApiClient(restTemplate, safeStorageBaseUrl, apiKey));
    }

    @Override
    public boolean setApiKeys(ApiKeyType apiKey) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ApiKeyType getApiKeySetted() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
