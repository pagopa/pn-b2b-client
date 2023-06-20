package it.pagopa.pn.client.b2b.pa.testclient;


import it.pagopa.pn.client.b2b.pa.impl.PnPaB2bExternalClientImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class PnExternalServiceClientImpl {

    private final ApplicationContext ctx;
    private final RestTemplate restTemplate;

    private final String apiKeyMvp1;
    private final String apiKeyMvp2;
    private final String apiKeyGa;

    private final String safeStorageBasePath;
    private final String gruopInfoBasePath;
    private String bearerTokenInterop;

    private final String interopBaseUrl;

    private final String tokenOauth2Path;

    private final String clientAssertion;

    private final String interopClientId;


    private final String enableInterop;
    private final String gherkinSrlBearerToken;
    private final String cucumberSpaBearerToken;

    private final String basePathWebApi;
    public PnExternalServiceClientImpl(
            ApplicationContext ctx,
            RestTemplate restTemplate,
            @Value("${pn.safeStorage.base-url}") String safeStorageBasePath,
            @Value("${pn.external.base-url}") String gruopInfoBasePath,
            @Value("${pn.external.api-key}") String apiKeyMvp1,
            @Value("${pn.external.api-key-2}") String apiKeyMvp2,
            @Value("${pn.external.api-key-GA}") String apiKeyGa,
            @Value("${pn.interop.base-url}") String interopBaseUrl,
            @Value("${pn.interop.token-oauth2.path}") String tokenOauth2Path,
            @Value("${pn.interop.token-oauth2.client-assertion}") String clientAssertion,
            @Value("${interop.clientId}") String interopClientId,
            @Value("${pn.interop.enable}") String enableInterop,
            @Value("${pn.bearer-token.pg1}") String gherkinSrlBearerToken,
            @Value("${pn.bearer-token.pg2}") String cucumberSpaBearerToken,
            @Value("${pn.webapi.external.base-url}") String basePathWebApi
    ) {
        this.ctx = ctx;
        this.restTemplate = restTemplate;
        this.safeStorageBasePath = safeStorageBasePath;
        this.gruopInfoBasePath = gruopInfoBasePath;
        this.basePathWebApi = basePathWebApi;
        this.apiKeyMvp1 = apiKeyMvp1;
        this.apiKeyMvp2 = apiKeyMvp2;
        this.apiKeyGa = apiKeyGa;
        this.enableInterop = enableInterop;
        this.interopBaseUrl = interopBaseUrl;
        this.tokenOauth2Path = tokenOauth2Path;
        this.clientAssertion = clientAssertion;
        this.interopClientId = interopClientId;
        this.gherkinSrlBearerToken = gherkinSrlBearerToken;
        this.cucumberSpaBearerToken = cucumberSpaBearerToken;
        if ("true".equalsIgnoreCase(enableInterop)) {
            this.bearerTokenInterop = getBearerToken();
        }
    }


    public SafeStorageResponse safeStorageInfo(String fileKey) throws RestClientException {
        return safeStorageInfoWithHttpInfo(fileKey).getBody();
    }

    public String getBearerToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("client_assertion", clientAssertion);
        map.add("client_id", interopClientId);
        map.add("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
        map.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<PnPaB2bExternalClientImpl.InteropResponse> response = this.restTemplate.postForEntity( interopBaseUrl + tokenOauth2Path, request , PnPaB2bExternalClientImpl.InteropResponse.class );

        return (response.getStatusCode().is2xxSuccessful() ? response.getBody().getAccessToken() : null);

    }

    public List<HashMap<String, String>> paGroupInfo(SettableApiKey.ApiKeyType apiKeyType, String bearerToken) throws RestClientException {
        switch (apiKeyType) {
            case MVP_1:
                return paGroupInfoWithHttpInfo(apiKeyMvp1, bearerToken).getBody();
            case MVP_2:
                return paGroupInfoWithHttpInfo(apiKeyMvp2, bearerToken).getBody();
            case GA:
                return paGroupInfoWithHttpInfo(apiKeyGa, bearerToken).getBody();
            default:
                throw new IllegalArgumentException();
        }
    }

    public List<HashMap<String, String>> pgGroupInfo(SettableBearerToken.BearerTokenType settableBearerToken) throws RestClientException {
        switch (settableBearerToken) {
            case PG_1:
                return pgGroupInfoWithHttpInfo(gherkinSrlBearerToken).getBody();
            case PG_2:
                return pgGroupInfoWithHttpInfo(cucumberSpaBearerToken).getBody();
            default:
                throw new IllegalArgumentException();
        }
    }



    private ResponseEntity<List<HashMap<String, String>>> pgGroupInfoWithHttpInfo(String bearerToken) throws RestClientException {
        Object postBody = null;

        final Map<String, Object> uriVariables = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        queryParams.add("metadataOnly", "true");

        final HttpHeaders headerParams = new HttpHeaders();

        headerParams.add("Authorization","Bearer "+bearerToken);

        final String[] localVarAccepts = {
                "application/json", "application/problem+json"
        };
        final List<MediaType> localVarAccept = MediaType.parseMediaTypes(StringUtils.arrayToCommaDelimitedString(localVarAccepts));
        final MediaType localVarContentType = MediaType.APPLICATION_JSON;


        ParameterizedTypeReference<List<HashMap<String, String>>> returnType = new ParameterizedTypeReference<>() {
        };
        return invokeAPI(basePathWebApi, "/ext-registry/pg/v1/groups", HttpMethod.GET, uriVariables, queryParams, postBody, headerParams, localVarAccept, localVarContentType, returnType);
    }
    ///ext-registry-private/pg/v1/groups-all


    private ResponseEntity<List<HashMap<String, String>>> paGroupInfoWithHttpInfo(String apiKey, String bearerToken) throws RestClientException {
        Object postBody = null;


        final Map<String, Object> uriVariables = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        queryParams.add("metadataOnly", "true");

        final HttpHeaders headerParams = new HttpHeaders();
        headerParams.add("x-api-key", apiKey);
        if ("true".equalsIgnoreCase(enableInterop)) {
            headerParams.add("Authorization","Bearer "+bearerToken);
        }




        final String[] localVarAccepts = {
                "application/json", "application/problem+json"
        };
        final List<MediaType> localVarAccept = MediaType.parseMediaTypes(StringUtils.arrayToCommaDelimitedString(localVarAccepts));
        final MediaType localVarContentType = MediaType.APPLICATION_JSON;


        ParameterizedTypeReference<List<HashMap<String, String>>> returnType = new ParameterizedTypeReference<>() {
        };
        return invokeAPI(gruopInfoBasePath, "/ext-registry-b2b/pa/v1/groups", HttpMethod.GET, uriVariables, queryParams, postBody, headerParams, localVarAccept, localVarContentType, returnType);
    }

    public static class SafeStorageResponse{

            String key;
            String versionId;
            String documentType;
            String documentStatus;
            String contentType;
            Integer contentLength;
            String checksum;
           String retentionUntil;
           Download download;

           public SafeStorageResponse(){}

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getVersionId() {
            return versionId;
        }

        public void setVersionId(String versionId) {
            this.versionId = versionId;
        }

        public String getDocumentType() {
            return documentType;
        }

        public void setDocumentType(String documentType) {
            this.documentType = documentType;
        }

        public String getDocumentStatus() {
            return documentStatus;
        }

        public void setDocumentStatus(String documentStatus) {
            this.documentStatus = documentStatus;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public Integer getContentLength() {
            return contentLength;
        }

        public void setContentLength(Integer contentLength) {
            this.contentLength = contentLength;
        }

        public String getChecksum() {
            return checksum;
        }

        public void setChecksum(String checksum) {
            this.checksum = checksum;
        }

        public String getRetentionUntil() {
            return retentionUntil;
        }

        public void setRetentionUntil(String retentionUntil) {
            this.retentionUntil = retentionUntil;
        }

        public Download getDownload() {
            return download;
        }

        public void setDownload(Download download) {
            this.download = download;
        }

        @Override
        public String toString() {
            return "SafeStorageResponse{" +
                    "key='" + key + '\'' +
                    ", versionId='" + versionId + '\'' +
                    ", documentType='" + documentType + '\'' +
                    ", documentStatus='" + documentStatus + '\'' +
                    ", contentType='" + contentType + '\'' +
                    ", contentLength=" + contentLength +
                    ", checksum='" + checksum + '\'' +
                    ", retentionUntil='" + retentionUntil + '\'' +
                    ", download=" + download +
                    '}';
        }

        public static class Download{
               String url;
               String retryAfter;

            @Override
            public String toString() {
                return "Download{" +
                        "url='" + url + '\'' +
                        ", retryAfter='" + retryAfter + '\'' +
                        '}';
            }

            public Download(){}

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getRetryAfter() {
                return retryAfter;
            }

            public void setRetryAfter(String retryAfter) {
                this.retryAfter = retryAfter;
            }
        }

    }

    private ResponseEntity<SafeStorageResponse> safeStorageInfoWithHttpInfo(String fileKey) throws RestClientException {
        Object postBody = null;

        if (fileKey == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'fileKey' when calling consumeEventStream");
        }

        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("fileKey", fileKey);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        queryParams.add("metadataOnly", "true");

        final HttpHeaders headerParams = new HttpHeaders();
        headerParams.add("x-pagopa-safestorage-cx-id", "pn-delivery-push");


        final String[] localVarAccepts = {
                "application/json", "application/problem+json","*/*"
        };
        final List<MediaType> localVarAccept = MediaType.parseMediaTypes(StringUtils.arrayToCommaDelimitedString(localVarAccepts));
        final MediaType localVarContentType = MediaType.APPLICATION_JSON;


        ParameterizedTypeReference<SafeStorageResponse> returnType = new ParameterizedTypeReference<>() {
        };
        return invokeAPI(safeStorageBasePath, "/safe-storage/v1/files/{fileKey}", HttpMethod.GET, uriVariables, queryParams, postBody, headerParams, localVarAccept, localVarContentType, returnType);
    }

    private <T> ResponseEntity<T> invokeAPI(String basePath, String path, HttpMethod method, Map<String, Object> pathParams, MultiValueMap<String, String> queryParams, Object body, HttpHeaders headerParams, List<MediaType> accept, MediaType contentType, ParameterizedTypeReference<T> returnType) throws RestClientException {

        Map<String, Object> uriParams = new HashMap<>();
        uriParams.putAll(pathParams);

        String finalUri = path;

        if (queryParams != null && !queryParams.isEmpty()) {
            String queryUri = generateQueryUri(queryParams, uriParams);
            finalUri += "?" + queryUri;
        }
        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory();
        uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        restTemplate.setUriTemplateHandler(uriBuilderFactory);

        String expandedPath = restTemplate.getUriTemplateHandler().expand(finalUri, uriParams).toString();
        final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(basePath).path(expandedPath);

        URI uri;
        try {
            uri = new URI(builder.build().toUriString());
        } catch (URISyntaxException ex) {
            throw new RestClientException("Could not build URL: " + builder.toUriString(), ex);
        }

        final RequestEntity.BodyBuilder requestBuilder = RequestEntity.method(method, uri);
        if (accept != null) {
            requestBuilder.accept(accept.toArray(new MediaType[accept.size()]));
        }
        if (contentType != null) {
            requestBuilder.contentType(contentType);
        }

        for (Map.Entry<String, List<String>> entry : headerParams.entrySet()) {
            List<String> values = entry.getValue();
            for (String value : values) {
                if (value != null) {
                    requestBuilder.header(entry.getKey(), value);
                }
            }
        }
        //formParams, contentType
        RequestEntity<Object> requestEntity = requestBuilder.body(body);

        ResponseEntity<T> responseEntity = restTemplate.exchange(requestEntity, returnType);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity;
        } else {
            throw new RestClientException("API returned " + responseEntity.getStatusCode() + " and it wasn't handled by the RestTemplate error handler");
        }
    }

    private String generateQueryUri(MultiValueMap<String, String> queryParams, Map<String, Object> uriParams) {
        StringBuilder queryBuilder = new StringBuilder();
        queryParams.forEach((name, values) -> {
            try {
                final String encodedName = URLEncoder.encode(name.toString(), "UTF-8");
                if (CollectionUtils.isEmpty(values)) {
                    if (queryBuilder.length() != 0) {
                        queryBuilder.append('&');
                    }
                    queryBuilder.append(encodedName);
                } else {
                    int valueItemCounter = 0;
                    for (Object value : values) {
                        if (queryBuilder.length() != 0) {
                            queryBuilder.append('&');
                        }
                        queryBuilder.append(encodedName);
                        if (value != null) {
                            String templatizedKey = encodedName + valueItemCounter++;
                            final String encodedValue = URLEncoder.encode(value.toString(), "UTF-8");
                            uriParams.put(templatizedKey, encodedValue);
                            queryBuilder.append('=').append("{").append(templatizedKey).append("}");
                        }
                    }
                }
            } catch (UnsupportedEncodingException e) {

            }
        });
        return queryBuilder.toString();

    }

}
