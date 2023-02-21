package it.pagopa.pn.client.b2b.pa.testclient;


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
    private final String apiKeyMvp3;
    private final String apiKeyGa;

    private final String safeStorageBasePath;
    private final String gruopInfoBasePath;


    public PnExternalServiceClientImpl(
            ApplicationContext ctx,
            RestTemplate restTemplate,
            @Value("${pn.safeStorage.base-url}") String safeStorageBasePath,
            @Value("${pn.external.base-url}") String gruopInfoBasePath,
            @Value("${pn.external.api-key}") String apiKeyMvp1,
            @Value("${pn.external.api-key-2}") String apiKeyMvp2,
            @Value("${pn.external.api-key-3}") String apiKeyMvp3,
            @Value("${pn.external.api-key-GA}") String apiKeyGa
    ) {
        this.ctx = ctx;
        this.restTemplate = restTemplate;
        this.safeStorageBasePath = safeStorageBasePath;
        this.gruopInfoBasePath = gruopInfoBasePath;
        this.apiKeyMvp1 = apiKeyMvp1;
        this.apiKeyMvp2 = apiKeyMvp2;
        this.apiKeyMvp3 = apiKeyMvp3;
        this.apiKeyGa = apiKeyGa;
    }


    public HashMap<String, String> safeStorageInfo(String fileKey) throws RestClientException {
        return safeStorageInfoWithHttpInfo(fileKey).getBody();
    }

    public List<HashMap<String, String>> paGroupInfo(SettableApiKey.ApiKeyType apiKeyType) throws RestClientException {
        switch (apiKeyType) {
            case MVP_1:
                return paGroupInfoWithHttpInfo(apiKeyMvp1).getBody();
            case MVP_2:
                return paGroupInfoWithHttpInfo(apiKeyMvp2).getBody();
            case MVP_3:
                return paGroupInfoWithHttpInfo(apiKeyMvp3).getBody();
            case GA:
                return paGroupInfoWithHttpInfo(apiKeyGa).getBody();
            default:
                throw new IllegalArgumentException();
        }
    }


    private ResponseEntity<List<HashMap<String, String>>> paGroupInfoWithHttpInfo(String apiKey) throws RestClientException {
        Object postBody = null;


        final Map<String, Object> uriVariables = new HashMap<String, Object>();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        queryParams.add("metadataOnly", "true");

        final HttpHeaders headerParams = new HttpHeaders();
        headerParams.add("x-api-key", apiKey);


        final String[] localVarAccepts = {
                "application/json", "application/problem+json"
        };
        final List<MediaType> localVarAccept = MediaType.parseMediaTypes(StringUtils.arrayToCommaDelimitedString(localVarAccepts));
        final MediaType localVarContentType = MediaType.APPLICATION_JSON;


        ParameterizedTypeReference<List<HashMap<String, String>>> returnType = new ParameterizedTypeReference<>() {
        };
        return invokeAPI(gruopInfoBasePath, "/ext-registry-b2b/pa/v1/groups", HttpMethod.GET, uriVariables, queryParams, postBody, headerParams, localVarAccept, localVarContentType, returnType);
    }

    private ResponseEntity<HashMap<String, String>> safeStorageInfoWithHttpInfo(String fileKey) throws RestClientException {
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
                "application/json", "application/problem+json"
        };
        final List<MediaType> localVarAccept = MediaType.parseMediaTypes(StringUtils.arrayToCommaDelimitedString(localVarAccepts));
        final MediaType localVarContentType = MediaType.APPLICATION_JSON;


        ParameterizedTypeReference<HashMap<String, String>> returnType = new ParameterizedTypeReference<>() {
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
