package it.pagopa.pn.client.b2b.pa.service.impl;


import com.fasterxml.jackson.databind.SerializationFeature;
import it.pagopa.pn.client.b2b.pa.service.IPnSafeStoragePrivateClient;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.ApiClient;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.api.AdditionalFileTagsApi;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.api.FileDownloadApi;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.api.FileMetadataUpdateApi;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.api.FileUploadApi;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnSafeStoragePrivateClientImpl implements IPnSafeStoragePrivateClient {

    private final RestTemplate restTemplate;
    private final String safeStorageBaseUrl;
    private String clientIdSafeStorage;
    private final FileUploadApi fileUploadApi;
    private final FileDownloadApi fileDownloadApi;
    private final FileMetadataUpdateApi fileMetadataUpdateApi;
    private final AdditionalFileTagsApi additionalFileTagsApi;

    public PnSafeStoragePrivateClientImpl(RestTemplate restTemplate,
                                          @Value("${pn.safeStorage.base-url}") String safeStorageBaseUrl,
                                          @Value("${pn.safeStorage.apikey}") String apiKeySafeStorage,
                                          @Value("${pn.safeStorage.clientId}") String clientIdSafeStorage) {

        this.restTemplate = withIsoDateSerialization(restTemplate);
        this.safeStorageBaseUrl = safeStorageBaseUrl;
        this.clientIdSafeStorage = clientIdSafeStorage;

        fileUploadApi = new FileUploadApi(newApiClient(this.restTemplate, safeStorageBaseUrl, apiKeySafeStorage));
        fileDownloadApi = new FileDownloadApi(newApiClient(this.restTemplate, safeStorageBaseUrl, apiKeySafeStorage));
        fileMetadataUpdateApi = new FileMetadataUpdateApi(newApiClient(this.restTemplate, safeStorageBaseUrl, apiKeySafeStorage));
        additionalFileTagsApi = new AdditionalFileTagsApi(newApiClient(this.restTemplate, safeStorageBaseUrl, apiKeySafeStorage));
    }

    // Il RestTemplate condiviso serializza le date java.time come timestamp decimali
    // (es. 1790416800.000000000), che SafeStorage non riesce a leggere nei campi data-ora
    // (retentionUntil, availableUntil) e rifiuta con 400 "Failed to read HTTP message".
    // Per questo client si usa una copia che le serializza in ISO-8601 (RFC 3339, come da
    // contratto), riusando la request factory del RestTemplate condiviso, gia' comprensiva
    // dei suoi interceptor.
    private static RestTemplate withIsoDateSerialization(RestTemplate sharedRestTemplate) {
        List<HttpMessageConverter<?>> messageConverters = sharedRestTemplate.getMessageConverters().stream()
                .<HttpMessageConverter<?>>map(converter -> converter instanceof MappingJackson2HttpMessageConverter jacksonConverter
                        ? new MappingJackson2HttpMessageConverter(jacksonConverter.getObjectMapper().copy()
                                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS))
                        : converter)
                .toList();
        RestTemplate safeStorageRestTemplate = new RestTemplate(sharedRestTemplate.getRequestFactory());
        safeStorageRestTemplate.setMessageConverters(messageConverters);
        safeStorageRestTemplate.setErrorHandler(sharedRestTemplate.getErrorHandler());
        return safeStorageRestTemplate;
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String apiKey) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("x-api-key", apiKey);
        return newApiClient;
    }

    public void customApiClient(String clientName) {
        setClientId(clientName);
    }

    @Override
    public void setClientId(String clientId) {
        clientIdSafeStorage = clientId;
        String apiKey = clientId + "_api_key";
        fileUploadApi.setApiClient(newApiClient(restTemplate, safeStorageBaseUrl, apiKey));
        fileDownloadApi.setApiClient(newApiClient(restTemplate, safeStorageBaseUrl, apiKey));
        fileMetadataUpdateApi.setApiClient(newApiClient(restTemplate, safeStorageBaseUrl, apiKey));
        additionalFileTagsApi.setApiClient(newApiClient(restTemplate, safeStorageBaseUrl, apiKey));
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

    @Override
    public OperationResultCodeResponse updateFileMetadata(
            String fileKey, UpdateFileMetadataRequest updateFileMetadataRequest) throws RestClientException {
        return this.fileMetadataUpdateApi.updateFileMetadata(fileKey, clientIdSafeStorage, updateFileMetadataRequest);
    }

    @Override
    public ResponseEntity<OperationResultCodeResponse> updateFileMetadataWithHttpInfo(
            String fileKey, String cxId, UpdateFileMetadataRequest updateFileMetadataRequest) throws RestClientException {
        return this.fileMetadataUpdateApi.updateFileMetadataWithHttpInfo(fileKey, cxId, updateFileMetadataRequest);
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
        fileMetadataUpdateApi.setApiClient(newApiClient(restTemplate, safeStorageBaseUrl, apiKey));
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
