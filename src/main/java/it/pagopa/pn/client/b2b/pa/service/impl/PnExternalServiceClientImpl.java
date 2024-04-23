package it.pagopa.pn.client.b2b.pa.service.impl;


import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
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

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.OffsetDateTime;
import java.util.*;

import static it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton.ENEBLED_INTEROP;

@Slf4j
@Component
public class PnExternalServiceClientImpl {

    private final RestTemplate restTemplate;

    private final String apiKeyMvp1;
    private final String apiKeyMvp2;
    private final String apiKeyGa;
    private final String apiKeySON;
    private final String apiKeyROOT;

    private final String safeStorageBasePath;
    private final String gruopInfoBasePath;
    private final String extChannelsBasePath;

    private final String deliveryBasePath;
    private final String dataVaultBasePath;


    private final String enableInterop;
    private final String gherkinSrlBearerToken;
    private final String cucumberSpaBearerToken;

    private final String openSearchBaseUrl;
    private final String openSearchUsername;
    private final String openSearchPassword;

    private final String basePathWebApi;

    private final InteropTokenSingleton interopTokenSingleton;

    public PnExternalServiceClientImpl(
            RestTemplate restTemplate,
            InteropTokenSingleton interopTokenSingleton,
            @Value("${pn.safeStorage.base-url}") String safeStorageBasePath,
            @Value("${pn.external.base-url}") String gruopInfoBasePath,
            @Value("${pn.external.api-key}") String apiKeyMvp1,
            @Value("${pn.external.api-key-2}") String apiKeyMvp2,
            @Value("${pn.external.api-key-GA}") String apiKeyGa,
            @Value("${pn.external.api-key-SON}") String apiKeySON,
            @Value("${pn.external.api-key-ROOT}") String apiKeyROOT,
            @Value("${pn.interop.enable}") String enableInterop,
            @Value("${pn.bearer-token.pg1}") String gherkinSrlBearerToken,
            @Value("${pn.bearer-token.pg2}") String cucumberSpaBearerToken,
            @Value("${pn.webapi.external.base-url}") String basePathWebApi,
            @Value("${pn.externalChannels.base-url}") String extChannelsBasePath,
            @Value("${pn.delivery.base-url}") String deliveryBasePath,
            @Value("${pn.dataVault.base-url}") String dataVaultBasePath,
            @Value("${pn.OpenSearch.base-url}") String openSearchBaseUrl,
            @Value("${pn.OpenSearch.username}") String openSearchUsername,
            @Value("${pn.OpenSearch.password}") String openSearchPassword
    ) {
        this.restTemplate = restTemplate;
        this.safeStorageBasePath = safeStorageBasePath;
        this.extChannelsBasePath = extChannelsBasePath;
        this.deliveryBasePath = deliveryBasePath;
        this.dataVaultBasePath = dataVaultBasePath;
        this.gruopInfoBasePath = gruopInfoBasePath;
        this.basePathWebApi = basePathWebApi;
        this.apiKeyMvp1 = apiKeyMvp1;
        this.apiKeyMvp2 = apiKeyMvp2;
        this.apiKeyGa = apiKeyGa;
        this.apiKeySON = apiKeySON;
        this.apiKeyROOT = apiKeyROOT;

        this.enableInterop = enableInterop;

        this.interopTokenSingleton = interopTokenSingleton;

        this.gherkinSrlBearerToken = gherkinSrlBearerToken;
        this.cucumberSpaBearerToken = cucumberSpaBearerToken;

        this.openSearchBaseUrl = openSearchBaseUrl;
        this.openSearchUsername = openSearchUsername;
        this.openSearchPassword = openSearchPassword;

    }


    public SafeStorageResponse safeStorageInfo(String fileKey) throws RestClientException {
        return safeStorageInfoWithHttpInfo(fileKey).getBody();
    }

    public SafeStorageResponse safeStorageInfoPnServiceDesk(String fileKey) throws RestClientException {
        return safeStoragePnServiceDeskInfoWithHttpInfo(fileKey).getBody();
    }


    private void restTemplateAvoidSSlCertificate() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

        this.restTemplate.setRequestFactory(requestFactory);
    }

    public OpenSearchResponse openSearchGetAudit(String audRetentionType,String auditLogType, int numberOfResult){
        return openSearchGetAuditWithHttpInfo(audRetentionType, auditLogType, numberOfResult).getBody();
    }
    private ResponseEntity<OpenSearchResponse> openSearchGetAuditWithHttpInfo(String audRetentionType,String auditLogType, int numberOfResult) throws RestClientException {

        try {
            restTemplateAvoidSSlCertificate();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }

        String postBody = "{\"query\":{\"bool\":{\"must\":{\"match\":{\"aud_type\":\""+auditLogType+"\"}}}},\"size\":"+numberOfResult+",\"sort\":[{\"@timestamp\": \"desc\"}]}";

        final Map<String, Object> uriVariables = new HashMap<>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        //queryParams.add("format", "json");

        final HttpHeaders headerParams = new HttpHeaders();

        String usernamePassword = openSearchUsername+":"+openSearchPassword;
        headerParams.add("Authorization","Basic "+Base64.getEncoder().encodeToString(usernamePassword.getBytes()));

        final String[] localVarAccepts = {
                "application/json", "application/problem+json"
        };
        final List<MediaType> localVarAccept = MediaType.parseMediaTypes(StringUtils.arrayToCommaDelimitedString(localVarAccepts));
        final MediaType localVarContentType = MediaType.APPLICATION_JSON;

        ParameterizedTypeReference<OpenSearchResponse> returnType = new ParameterizedTypeReference<>() {};
        return invokeAPI(openSearchBaseUrl, "/pn-logs"+audRetentionType+"/_search", HttpMethod.POST, uriVariables, queryParams, postBody, headerParams, localVarAccept, localVarContentType, returnType);
    }


    public HashMap<String, String> getQuickAccessLink(String iun) {
        return getQuickAccessLinkWithHttpInfo(iun).getBody();
    }

    private ResponseEntity<HashMap<String, String>> getQuickAccessLinkWithHttpInfo(String iun) {
        Object postBody = null;

        final Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("iun", iun);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("metadataOnly", "true");

        final HttpHeaders headerParams = new HttpHeaders();

        final String[] localVarAccepts = {
                "application/json", "application/problem+json"
        };
        final List<MediaType> localVarAccept = MediaType.parseMediaTypes(StringUtils.arrayToCommaDelimitedString(localVarAccepts));
        final MediaType localVarContentType = MediaType.APPLICATION_JSON;

        ParameterizedTypeReference<HashMap<String, String>> returnType = new ParameterizedTypeReference<>() {
        };
        return invokeAPI(deliveryBasePath, "/delivery-private/notifications/{iun}/quick-access-link-tokens", HttpMethod.GET, uriVariables, queryParams, postBody, headerParams, localVarAccept, localVarContentType, returnType);
    }

    public List<HashMap<String, String>> paGroupInfo(SettableApiKey.ApiKeyType apiKeyType) throws RestClientException {
        switch (apiKeyType) {
            case MVP_1:
                return paGroupInfoWithHttpInfo(apiKeyMvp1).getBody();
            case MVP_2:
                return paGroupInfoWithHttpInfo(apiKeyMvp2).getBody();
            case GA:
                return paGroupInfoWithHttpInfo(apiKeyGa).getBody();
            case SON:
                return paGroupInfoWithHttpInfo(apiKeySON).getBody();
            case ROOT:
                return paGroupInfoWithHttpInfo(apiKeyROOT).getBody();
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

    public String getVerificationCode(String digitalAddress) {
        return getVerificationCodeWithHttpInfo(digitalAddress).getBody();
    }

    public String getInternalIdFromTaxId(String recipientType, String taxId) {
        return getInternalIdFromTaxIdWithHttpInfo(recipientType, taxId).getBody();
    }

    private ResponseEntity<List<HashMap<String, String>>> pgGroupInfoWithHttpInfo(String bearerToken) throws RestClientException {
        Object postBody = null;

        final Map<String, Object> uriVariables = new HashMap<>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
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


    private ResponseEntity<List<HashMap<String, String>>> paGroupInfoWithHttpInfo(String apiKey) throws RestClientException {
        Object postBody = null;


        final Map<String, Object> uriVariables = new HashMap<>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("metadataOnly", "true");

        final HttpHeaders headerParams = new HttpHeaders();
        headerParams.add("x-api-key", apiKey);

        if (ENEBLED_INTEROP.equalsIgnoreCase(enableInterop)) {
            headerParams.add("Authorization","Bearer "+ interopTokenSingleton.getTokenInterop());
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

    private ResponseEntity<String> getVerificationCodeWithHttpInfo(String digitalAddress) {
        Object postBody = null;

        final Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("digitalAddress", digitalAddress);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("metadataOnly", "true");

        final HttpHeaders headerParams = new HttpHeaders();

        final String[] localVarAccepts = {
                "application/json", "application/problem+json"
        };
        final List<MediaType> localVarAccept = MediaType.parseMediaTypes(StringUtils.arrayToCommaDelimitedString(localVarAccepts));
        final MediaType localVarContentType = MediaType.APPLICATION_JSON;

        ParameterizedTypeReference<String> returnType = new ParameterizedTypeReference<>() {
        };
        return invokeAPI(extChannelsBasePath, "/external-channels/verification-code/{digitalAddress}", HttpMethod.GET, uriVariables, queryParams, postBody, headerParams, localVarAccept, localVarContentType, returnType);
    }

    private ResponseEntity<String> getInternalIdFromTaxIdWithHttpInfo(String recipientType, String taxId) {
        String postBody = taxId;

        final Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("recipientType", recipientType);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("metadataOnly", "true");

        final HttpHeaders headerParams = new HttpHeaders();

        final String[] localVarAccepts = {
                "application/json", "application/problem+json", "text/plain"
        };
        final List<MediaType> localVarAccept = MediaType.parseMediaTypes(StringUtils.arrayToCommaDelimitedString(localVarAccepts));
        final MediaType localVarContentType = MediaType.TEXT_PLAIN;

        ParameterizedTypeReference<String> returnType = new ParameterizedTypeReference<>() {
        };

        return invokeAPI(dataVaultBasePath, "/datavault-private/v1/recipients/external/{recipientType}", HttpMethod.POST, uriVariables, queryParams, postBody, headerParams, localVarAccept, localVarContentType, returnType);
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

        final Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("fileKey", fileKey);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
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

    private ResponseEntity<SafeStorageResponse> safeStoragePnServiceDeskInfoWithHttpInfo(String fileKey) throws RestClientException {
        Object postBody = null;

        if (fileKey == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'fileKey' when calling consumeEventStream");
        }

        final Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("fileKey", fileKey);

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("metadataOnly", "true");

        final HttpHeaders headerParams = new HttpHeaders();
        headerParams.add("x-pagopa-safestorage-cx-id", "pn-service-desk");


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
                log.error(e.getMessage());
            }
        });
        return queryBuilder.toString();

    }
    //OPEN SEARCH RESPONSE
    @Getter
    @Setter
    @ToString
    public static class OpenSearchResponse {
       Integer took;
       Boolean timedOut;
       Shards shards;
       OuterHits hits;

    }
    @Getter
    @Setter
    @ToString
    public static class Shards{
        public Shards() {}
        private Integer total;
        private Integer successful;
        private Integer skipped;

        private Integer failed;

    }
    @Getter
    @Setter
    @ToString
    public static class OuterHits{
        public OuterHits() {
        }

        private Double maxScore;
        private Total total;
        private LinkedList<InnerHits> hits;


    }
    @Getter
    @Setter
    @ToString
    public static class InnerHits{
        public InnerHits() {
        }

        private String index;
        private String type;
        private String id;
        private Double score;
        private Source source;

        public class Source{
            @Override
            public String toString() {
                return "Source{" +
                        "msg='" + msg + '\'' +
                        ", trace_id='" + traceId + '\'' +
                        ", level=" + level +
                        ", logGroup='" + logGroup + '\'' +
                        ", aud_type='" + audType + '\'' +
                        ", pid=" + pid +
                        ", message='" + message + '\'' +
                        ", aud_orig='" + audOrig + '\'' +
                        ", tags=" + Arrays.toString(tags) +
                        ", kinesisSeqNumber='" + kinesisSeqNumber + '\'' +
                        ", hostname='" + hostname + '\'' +
                        ", timestamp='" + timestamp + '\'' +
                        ", level_value=" + levelValue +
                        ", v=" + v +
                        ", name='" + name + '\'' +
                        ", logStream='" + logStream + '\'' +
                        ", logger_name='" + loggerName + '\'' +
                        ", time='" + time + '\'' +
                        '}';
            }

            public Source() {
            }

            private String msg;
            private String traceId;
            private String level;
            private String logGroup;
            private String audType;
            private Integer pid;
            private String message;
            private String audOrig;
            private String[] tags;
            private String kinesisSeqNumber;

            private String hostname;
            @JsonProperty("@timestamp")
            private OffsetDateTime timestamp;

            private Long levelValue;
            private Long v;

            private String name;
            private String logStream;
            private String loggerName;
            private OffsetDateTime time;

            public String getMsg() {
                return msg;
            }

            public void setMsg(String msg) {
                this.msg = msg;
            }

            public String getTraceId() {
                return traceId;
            }

            public void setTraceId(String traceId) {
                this.traceId = traceId;
            }

            public String getLevel() {
                return level;
            }

            public void setLevel(String level) {
                this.level = level;
            }

            public String getLogGroup() {
                return logGroup;
            }

            public void setLogGroup(String logGroup) {
                this.logGroup = logGroup;
            }

            public String getAudType() {
                return audType;
            }

            public void setAudType(String audType) {
                this.audType = audType;
            }

            public Integer getPid() {
                return pid;
            }

            public void setPid(Integer pid) {
                this.pid = pid;
            }

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }

            public String getAudOrig() {
                return audOrig;
            }

            public void setAudOrig(String audOrig) {
                this.audOrig = audOrig;
            }

            public String[] getTags() {
                return tags;
            }

            public void setTags(String[] tags) {
                this.tags = tags;
            }

            public String getKinesisSeqNumber() {
                return kinesisSeqNumber;
            }

            public void setKinesisSeqNumber(String kinesisSeqNumber) {
                this.kinesisSeqNumber = kinesisSeqNumber;
            }

            public String getHostname() {
                return hostname;
            }

            public void setHostname(String hostname) {
                this.hostname = hostname;
            }

            public OffsetDateTime getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(OffsetDateTime timestamp) {
                this.timestamp = timestamp;
            }

            public Long getLevelValue() {
                return levelValue;
            }

            public void setLevelValue(Long levelValue) {
                this.levelValue = levelValue;
            }

            public Long getV() {
                return v;
            }

            public void setV(Long v) {
                this.v = v;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getLogStream() {
                return logStream;
            }

            public void setLogStream(String logStream) {
                this.logStream = logStream;
            }

            public String getLoggerName() {
                return loggerName;
            }

            public void setLoggerName(String loggerName) {
                this.loggerName = loggerName;
            }

            public OffsetDateTime getTime() {
                return time;
            }

            public void setTime(OffsetDateTime time) {
                this.time = time;
            }
        }

    }

    @Getter
    @Setter
    @ToString
    public static class Total{
        public Total() {
        }
        private Integer value;
        private String relation;

    }

}
