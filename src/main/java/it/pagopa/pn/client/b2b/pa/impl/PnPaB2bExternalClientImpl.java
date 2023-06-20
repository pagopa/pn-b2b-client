package it.pagopa.pn.client.b2b.pa.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.ApiClient;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api.*;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.testclient.PnInteropTokenOauth2Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

@Component()
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@ConditionalOnProperty( name = IPnPaB2bClient.IMPLEMENTATION_TYPE_PROPERTY, havingValue = "external", matchIfMissing = true)
public class PnPaB2bExternalClientImpl implements IPnPaB2bClient {

    private final ApplicationContext ctx;
    private final RestTemplate restTemplate;
    private final NewNotificationApi newNotificationApi;
    private final SenderReadB2BApi senderReadB2BApi;
    private final PaymentEventsApi paymentEventsApi;
    private final LegalFactsApi legalFactsApi;
    private final NotificationPriceApi notificationPriceApi;
    private final String basePath;
    private final String apiKeyMvp1;
    private final String apiKeyMvp2;
    private final String apiKeyGa;
    private ApiKeyType apiKeySetted = ApiKeyType.MVP_1;

    //private PnInteropTokenOauth2Client pnInteropTokenOauth2Client;

    private String bearerTokenInterop;

    private final String interopBaseUrl;

    private final String tokenOauth2Path;

    private final String clientAssertion;

    private final String enableInterop;

    private final String interopClientId;

    public PnPaB2bExternalClientImpl(
            ApplicationContext ctx,
            RestTemplate restTemplate,
            @Value("${pn.external.base-url}") String basePath,
            @Value("${pn.external.api-key}") String apiKeyMvp1,
            @Value("${pn.external.api-key-2}") String apiKeyMvp2,
            @Value("${pn.external.api-key-GA}") String apiKeyGa,
            @Value("${pn.interop.base-url}") String interopBaseUrl,
            @Value("${pn.interop.token-oauth2.path}") String tokenOauth2Path,
            @Value("${pn.interop.token-oauth2.client-assertion}") String clientAssertion,
            @Value("${interop.clientId}") String interopClientId,
            @Value("${pn.interop.enable}") String enableInterop

            //PnInteropTokenOauth2Client pnInteropTokenOauth2Client
    ) {
        this.ctx = ctx;
        this.restTemplate = restTemplate;
        this.basePath = basePath;
        this.apiKeyMvp1 = apiKeyMvp1;
        this.apiKeyMvp2 = apiKeyMvp2;
        this.apiKeyGa = apiKeyGa;
        //this.pnInteropTokenOauth2Client = pnInteropTokenOauth2Client;
        this.enableInterop = enableInterop;
        this.interopBaseUrl = interopBaseUrl;
        this.tokenOauth2Path = tokenOauth2Path;
        this.clientAssertion = clientAssertion;
        this.interopClientId = interopClientId;
        if ("true".equalsIgnoreCase(enableInterop)) {
            this.bearerTokenInterop = getBearerToken();
        }
        this.newNotificationApi = new NewNotificationApi( newApiClient( restTemplate, basePath, apiKeyMvp1, bearerTokenInterop,enableInterop) );
        this.senderReadB2BApi = new SenderReadB2BApi( newApiClient( restTemplate, basePath, apiKeyMvp1, bearerTokenInterop,enableInterop) );
        this.legalFactsApi = new LegalFactsApi(newApiClient( restTemplate, basePath, apiKeyMvp1, bearerTokenInterop,enableInterop));
        this.notificationPriceApi = new NotificationPriceApi(newApiClient( restTemplate, basePath, apiKeyMvp1, bearerTokenInterop,enableInterop));
        this.paymentEventsApi = new PaymentEventsApi(newApiClient( restTemplate, basePath, apiKeyMvp1, bearerTokenInterop,enableInterop));


    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String apikey, String bearerToken, String enableInterop ) {
        ApiClient newApiClient = new ApiClient( restTemplate );
        newApiClient.setBasePath( basePath );
        newApiClient.addDefaultHeader("x-api-key", apikey );
        if ("true".equalsIgnoreCase(enableInterop)) {
            newApiClient.addDefaultHeader("Authorization", "Bearer " + bearerToken);
        }
        return newApiClient;
    }

    @Override
    public boolean setApiKeys(ApiKeyType apiKey) {
        boolean beenSet = false;
        switch(apiKey){
            case MVP_1:
                if(this.apiKeySetted != ApiKeyType.MVP_1){
                    setApiKey(apiKeyMvp1);
                    this.apiKeySetted = ApiKeyType.MVP_1;
                }
                beenSet = true;
                break;
            case MVP_2:
                if(this.apiKeySetted != ApiKeyType.MVP_2) {
                    setApiKey(apiKeyMvp2);
                    this.apiKeySetted = ApiKeyType.MVP_2;
                }
                beenSet = true;
                break;
            case GA:
                if(this.apiKeySetted != ApiKeyType.GA) {
                    setApiKey(apiKeyGa);
                    this.apiKeySetted = ApiKeyType.GA;
                }
                beenSet = true;
                break;
        }
        return beenSet;
    }

    @Override
    public ApiKeyType getApiKeySetted() {
        return this.apiKeySetted;
    }

    public void setApiKey(String apiKey){
        this.newNotificationApi.setApiClient(newApiClient(restTemplate, basePath, apiKey, bearerTokenInterop,enableInterop));
        this.senderReadB2BApi.setApiClient(newApiClient(restTemplate, basePath, apiKey, bearerTokenInterop,enableInterop));
        this.legalFactsApi.setApiClient(newApiClient(restTemplate, basePath, apiKey,bearerTokenInterop,enableInterop));
        this.notificationPriceApi.setApiClient(newApiClient(restTemplate, basePath, apiKey,bearerTokenInterop,enableInterop));
        this.paymentEventsApi.setApiClient(newApiClient( restTemplate, basePath, apiKey,bearerTokenInterop,enableInterop));
    }

    public NotificationAttachmentDownloadMetadataResponse getSentNotificationDocument(String iun, Integer docidx) {
        return senderReadB2BApi.retrieveSentNotificationDocument(iun, docidx);
    }

    public NotificationAttachmentDownloadMetadataResponse getSentNotificationAttachment(String iun, Integer recipientIdx, String attachname) {
        return senderReadB2BApi.retrieveSentNotificationAttachment(iun, recipientIdx, attachname);
    }

    public LegalFactDownloadMetadataResponse getLegalFact(String iun, LegalFactCategory legalFactType, String legalFactId) {
        return legalFactsApi.retrieveLegalFact(iun, legalFactType, legalFactId);
    }

    @Override
    public NotificationPriceResponse getNotificationPrice(String paTaxId, String noticeCode) throws RestClientException {
        return this.notificationPriceApi.retrieveNotificationPrice(paTaxId,noticeCode);
    }



    public List<PreLoadResponse> presignedUploadRequest(List<PreLoadRequest> preLoadRequest) {
        return newNotificationApi.presignedUploadRequest( preLoadRequest );
    }

    public NewNotificationResponse sendNewNotification(NewNotificationRequest newNotificationRequest) {
        return newNotificationApi.sendNewNotification( newNotificationRequest );
    }

    @Override
    public FullSentNotification getSentNotification(String iun) {
        return senderReadB2BApi.retrieveSentNotification( iun );
    }

    @Override
    public NewNotificationRequestStatusResponse getNotificationRequestStatus(String notificationRequestId) {
        return senderReadB2BApi.retrieveNotificationRequestStatus( notificationRequestId, null, null );
    }

    @Override
    public NewNotificationRequestStatusResponse getNotificationRequestStatusAllParam(String notificationRequestId, String paProtocolNumber, String idempotenceToken) {
        return senderReadB2BApi.retrieveNotificationRequestStatus(notificationRequestId,paProtocolNumber,idempotenceToken);
    }

    @Override
    public void paymentEventsRequestPagoPa(PaymentEventsRequestPagoPa paymentEventsRequestPagoPa) throws RestClientException {
        this.paymentEventsApi.paymentEventsRequestPagoPaWithHttpInfo(paymentEventsRequestPagoPa);
    }

    @Override
    public void paymentEventsRequestF24(PaymentEventsRequestF24 paymentEventsRequestF24) throws RestClientException {
        this.paymentEventsApi.paymentEventsRequestF24WithHttpInfo(paymentEventsRequestF24);
    }


    public  String  getBearerToken() {
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

    public static class InteropResponse {
        private String correlationId;
        private Integer status;
        private String title;
        private String type;
        private List<PnInteropTokenOauth2Client.Error> errors;

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

        public List<PnInteropTokenOauth2Client.Error> getErrors() {
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

    public static class Error {
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
