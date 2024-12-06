package it.pagopa.pn.client.b2b.pa.service.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.pn.client.b2b.pa.config.PnBaseUrlConfig;
import it.pagopa.pn.client.b2b.pa.config.PnInteropConfig;
import lombok.Getter;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@Slf4j
public class InteropTokenSingleton implements InteropTokenRefresh {
    public static final String ENEBLED_INTEROP = "true";
    private String tokenInterop;
    private final RestTemplate restTemplate;
    private final String clientAssertion;
    private final String interopClientId;
    private final String tokenOauth2Path;
    private final String interopBaseUrl;
    private final String enableInterop;


    @Autowired
    public InteropTokenSingleton(RestTemplate restTemplate,
                                 PnBaseUrlConfig pnBaseUrlConfig,
                                 PnInteropConfig pnInteropConfig) {
        this.restTemplate = restTemplate;
        this.interopBaseUrl = pnBaseUrlConfig.getInteropBaseUrl();
        this.tokenOauth2Path = pnInteropConfig.getTokenOauth2Path();
        this.clientAssertion = pnInteropConfig.getClientAssertion();
        this.interopClientId = pnInteropConfig.getInteropClientId();
        this.enableInterop = pnInteropConfig.getEnableInterop();
    }

    public String getTokenInterop() {
        if (tokenInterop == null) {
            generateToken();
        }
        return tokenInterop;
    }

    @Synchronized
    private void generateToken() {
        if (tokenInterop == null) {
            tokenInterop = getBearerToken();
        }
    }

    @Scheduled(cron = "0 0/03 * * * ?")
    public void refreshTokenInteropClient() {
        if (ENEBLED_INTEROP.equalsIgnoreCase(enableInterop)) {
            log.info("refresh interop token");
            tokenInterop = getBearerToken();
        }
    }

    private String getBearerToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_assertion", clientAssertion);
        map.add("client_id", interopClientId);
        map.add("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
        map.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<InteropResponse> response = this.restTemplate.postForEntity(interopBaseUrl + tokenOauth2Path, request, InteropResponse.class);

        return (response.getStatusCode().is2xxSuccessful() ? Objects.requireNonNull(response.getBody()).getAccessToken() : null);
    }

    @Getter
    private static class InteropResponse {
        private String correlationId;
        private Integer status;
        private String title;
        private String type;
        private List<Error> errors;

        @JsonProperty("access_token")
        private String accessToken;
        @JsonProperty("expires_in")
        private Integer expiresIn;
        @JsonProperty("token_type")
        private String tokenType;
    }

    @Getter
    private static class Error {
        private String code;
        private String detail;
    }
}