package it.pagopa.pn.client.b2b.pa.service.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

@Component
@Slf4j
public class InteropTokenSingleton implements InteropTokenRefresh{

    public static final String ENEBLED_INTEROP = "true";
    private static String tokenInterop;
    private static OffsetDateTime tokenCreationDate;
    private final ApplicationContext ctx;
    private final RestTemplate restTemplate;
    private final String clientAssertion;
    private final String interopClientId;
    private final String tokenOauth2Path;
    private final String interopBaseUrl;

    private final String enableInterop;

    public InteropTokenSingleton(
            RestTemplate restTemplate,
            ApplicationContext ctx,
            @Value("${pn.interop.base-url}") String interopBaseUrl,
            @Value("${pn.interop.token-oauth2.path}") String tokenOauth2Path,
            @Value("${pn.interop.token-oauth2.client-assertion}") String clientAssertion,
            @Value("${pn.interop.clientId}") String interopClientId,
            @Value("${pn.interop.enable}") String enableInterop){


        this.ctx = ctx;
        this.restTemplate = restTemplate;
        this.interopBaseUrl = interopBaseUrl;
        this.tokenOauth2Path = tokenOauth2Path;
        this.clientAssertion = clientAssertion;
        this.interopClientId = interopClientId;
        this.enableInterop = enableInterop;
    }

    @Synchronized
    public String getTokenInterop(){
        if(tokenInterop == null){
            tokenInterop = getBearerToken();
            tokenCreationDate = OffsetDateTime.now();
        } else if (tokenInterop != null && (Duration.between(tokenCreationDate, OffsetDateTime.now()).getSeconds() >= (60*8))) {
            log.info("refresh interop token1");
            tokenInterop = getBearerToken();
            tokenCreationDate = OffsetDateTime.now();
        }
        return tokenInterop;
        /*
        if(tokenInterop != null && (Duration.between(tokenCreationDate, OffsetDateTime.now()).getSeconds() <= (60*8)) ){
            return tokenInterop;
        }else{
            log.info("refresh interop token");
            tokenInterop = getBearerToken();
            tokenCreationDate = OffsetDateTime.now();
            return tokenInterop;
        }
         */
    }
    public static void main (String[] args){
        System.out.println(Duration.between(OffsetDateTime.now(), OffsetDateTime.now().plusMinutes(9)).getSeconds());
    }

    @Scheduled(cron = "0 0/01 * * * ?")
    public void refreshTokenInteropClient() {
        if (ENEBLED_INTEROP.equalsIgnoreCase(enableInterop)) {
            log.info("refresh interop token");
            tokenInterop = getBearerToken();
            tokenCreationDate = OffsetDateTime.now();
        }
    }

    private String getBearerToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("client_assertion", clientAssertion);
        map.add("client_id", interopClientId);
        map.add("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
        map.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<InteropResponse> response = this.restTemplate.postForEntity( interopBaseUrl + tokenOauth2Path, request , InteropResponse.class );

        return (response.getStatusCode().is2xxSuccessful() ? response.getBody().getAccessToken() : null);

    }


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

        public String getCorrelationId() {
            return correlationId;
        }

        public String getTitle() {
            return title;
        }

        public String getType() {
            return type;
        }

        public List<Error> getErrors() {
            return errors;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public Integer getExpiresIn() {
            return expiresIn;
        }

        public String getTokenType() {
            return tokenType;
        }
    }

    private static class Error {
        private String code;
        private String detail;

        public String getCode() {
            return code;
        }

        public String getDetail() {
            return detail;
        }
    }


}
