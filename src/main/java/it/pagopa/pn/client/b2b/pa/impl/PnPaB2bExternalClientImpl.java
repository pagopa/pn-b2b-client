package it.pagopa.pn.client.b2b.pa.impl;


import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.ApiClient;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api.*;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.testclient.InteropTokenSingleton;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.api.NotificationProcessCostApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.NotificationProcessCostResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.List;

import static it.pagopa.pn.client.b2b.pa.testclient.InteropTokenSingleton.ENEBLED_INTEROP;

@Component()
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@ConditionalOnProperty( name = IPnPaB2bClient.IMPLEMENTATION_TYPE_PROPERTY, havingValue = "external", matchIfMissing = true)
public class PnPaB2bExternalClientImpl implements IPnPaB2bClient {


    private final ApplicationContext ctx;
    private final RestTemplate restTemplate;
    private final NewNotificationApi newNotificationApi;
    private final SenderReadB2BApi senderReadB2BApi;
    private final LegalFactsApi legalFactsApi;
    private final NotificationPriceApi notificationPriceApi;
    private final NotificationProcessCostApi notificationProcessCostApi;
    private final NotificationCancellationApi  notificationCancellationApi;
    private final PaymentEventsApi paymentEventsApi;
    private final String basePath;
    private final String apiKeyMvp1;
    private final String apiKeyMvp2;
    private final String apiKeyGa;
    private ApiKeyType apiKeySetted = ApiKeyType.MVP_1;

    private String bearerTokenInterop;

    private final String enableInterop;


    private final InteropTokenSingleton interopTokenSingleton;

    public PnPaB2bExternalClientImpl(
            ApplicationContext ctx,
            RestTemplate restTemplate,
            InteropTokenSingleton interopTokenSingleton,
            @Value("${pn.external.base-url}") String basePath,
            @Value("${pn.external.api-key}") String apiKeyMvp1,
            @Value("${pn.external.api-key-2}") String apiKeyMvp2,
            @Value("${pn.external.api-key-GA}") String apiKeyGa,
            @Value("${pn.interop.enable}") String enableInterop
    ) {
        this.ctx = ctx;
        this.restTemplate = restTemplate;
        this.basePath = basePath;
        this.apiKeyMvp1 = apiKeyMvp1;
        this.apiKeyMvp2 = apiKeyMvp2;
        this.apiKeyGa = apiKeyGa;
        this.enableInterop = enableInterop;
        if (ENEBLED_INTEROP.equalsIgnoreCase(enableInterop)) {
            this.bearerTokenInterop = interopTokenSingleton.getTokenInterop();
        }
        this.newNotificationApi = new NewNotificationApi( newApiClient( restTemplate, basePath, apiKeyMvp1, bearerTokenInterop,enableInterop) );
        this.senderReadB2BApi = new SenderReadB2BApi( newApiClient( restTemplate, basePath, apiKeyMvp1, bearerTokenInterop,enableInterop) );
        this.legalFactsApi = new LegalFactsApi(newApiClient( restTemplate, basePath, apiKeyMvp1, bearerTokenInterop,enableInterop));
        this.notificationPriceApi = new NotificationPriceApi(newApiClient( restTemplate, basePath, apiKeyMvp1, bearerTokenInterop,enableInterop));
        this. notificationProcessCostApi = new NotificationProcessCostApi(newApiClientPriv( restTemplate, basePath, apiKeyMvp1, bearerTokenInterop,enableInterop));
        this.paymentEventsApi = new PaymentEventsApi(newApiClient( restTemplate, basePath, apiKeyMvp1, bearerTokenInterop,enableInterop));
        this.notificationCancellationApi = new NotificationCancellationApi(newApiClient( restTemplate, basePath, apiKeyMvp1, bearerTokenInterop,enableInterop));

        this.interopTokenSingleton = interopTokenSingleton;

    }

    private void refreshTokenInteropClient(){
        if (ENEBLED_INTEROP.equalsIgnoreCase(enableInterop)) {
            this.bearerTokenInterop = interopTokenSingleton.getTokenInterop();

            this.newNotificationApi.getApiClient().addDefaultHeader("Authorization", "Bearer " + bearerTokenInterop);
            this.senderReadB2BApi.getApiClient().addDefaultHeader("Authorization", "Bearer " + bearerTokenInterop);
            this.legalFactsApi.getApiClient().addDefaultHeader("Authorization", "Bearer " + bearerTokenInterop);
            this.notificationPriceApi.getApiClient().addDefaultHeader("Authorization", "Bearer " + bearerTokenInterop);
            this.notificationProcessCostApi.getApiClient().addDefaultHeader("Authorization", "Bearer " + bearerTokenInterop);
            this.paymentEventsApi.getApiClient().addDefaultHeader("Authorization", "Bearer " + bearerTokenInterop);
            this.notificationCancellationApi.getApiClient().addDefaultHeader("Authorization", "Bearer " + bearerTokenInterop);
        }
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String apikey, String bearerToken, String enableInterop ) {
        ApiClient newApiClient = new ApiClient( restTemplate );
        newApiClient.setBasePath( basePath );
        newApiClient.addDefaultHeader("x-api-key", apikey );
        if (ENEBLED_INTEROP.equalsIgnoreCase(enableInterop)) {
            newApiClient.addDefaultHeader("Authorization", "Bearer " + bearerToken);
        }
        return newApiClient;
    }

    private static it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.ApiClient newApiClientPriv(RestTemplate restTemplate, String basePath, String apikey, String bearerToken, String enableInterop ) {
        it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.ApiClient newApiClient = new it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.ApiClient( restTemplate );
        newApiClient.setBasePath( basePath );
        newApiClient.addDefaultHeader("x-api-key", apikey );
        if (ENEBLED_INTEROP.equalsIgnoreCase(enableInterop)) {
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
        this.notificationCancellationApi.setApiClient(newApiClient( restTemplate, basePath, apiKey,bearerTokenInterop,enableInterop));
    }

    public NotificationAttachmentDownloadMetadataResponse getSentNotificationDocument(String iun, Integer docidx) {
        refreshTokenInteropClient();
        return senderReadB2BApi.retrieveSentNotificationDocument(iun, docidx);
    }

    public NotificationAttachmentDownloadMetadataResponse getSentNotificationAttachment(String iun, Integer recipientIdx, String attachname, Integer attachmentIdx) {
        refreshTokenInteropClient();
        return senderReadB2BApi.retrieveSentNotificationAttachment(iun, recipientIdx, attachname,attachmentIdx);
    }

    public LegalFactDownloadMetadataResponse getLegalFact(String iun, LegalFactCategory legalFactType, String legalFactId) {
        refreshTokenInteropClient();
        return legalFactsApi.retrieveLegalFact(iun, legalFactType, legalFactId);
    }

    public LegalFactDownloadMetadataResponse getDownloadLegalFact(String iun,  String legalFactId) {
        refreshTokenInteropClient();
        return legalFactsApi.downloadLegalFactById(iun,  legalFactId);
    }

    public LegalFactDownloadMetadataResponse getDownloadLegalFact(String iun, LegalFactCategory legalFactType, String legalFactId) {
        refreshTokenInteropClient();
        return legalFactsApi.downloadLegalFactById(iun, legalFactId);
    }

    @Override
    public NotificationPriceResponse getNotificationPrice(String paTaxId, String noticeCode) throws RestClientException {
        refreshTokenInteropClient();
        return this.notificationPriceApi.retrieveNotificationPrice(paTaxId,noticeCode);
    }

    public NotificationProcessCostResponse getNotificationProcessCost(String iun, Integer recipientIndex, it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.NotificationFeePolicy notificationFeePolicy, Boolean applyCost, Integer paFee) throws RestClientException {
        refreshTokenInteropClient();
        return this.notificationProcessCostApi.notificationProcessCost(iun, recipientIndex, notificationFeePolicy, applyCost, paFee);
    }


    public List<PreLoadResponse> presignedUploadRequest(List<PreLoadRequest> preLoadRequest) {
        refreshTokenInteropClient();
        return newNotificationApi.presignedUploadRequest( preLoadRequest );
    }

    public NewNotificationResponse sendNewNotification(NewNotificationRequestV21 newNotificationRequest) {
        refreshTokenInteropClient();
        return newNotificationApi.sendNewNotificationV21( newNotificationRequest );
    }

    @Override
    public FullSentNotificationV21 getSentNotification(String iun) {
        refreshTokenInteropClient();
        return senderReadB2BApi.retrieveSentNotificationV21( iun );
    }

    @Override
    public NewNotificationRequestStatusResponseV21 getNotificationRequestStatus(String notificationRequestId) {
        refreshTokenInteropClient();
        return senderReadB2BApi.retrieveNotificationRequestStatusV21( notificationRequestId, null, null );
    }

    @Override
    public NewNotificationRequestStatusResponseV21 getNotificationRequestStatusAllParam(String notificationRequestId, String paProtocolNumber, String idempotenceToken) {
        refreshTokenInteropClient();
        return senderReadB2BApi.retrieveNotificationRequestStatusV21(notificationRequestId,paProtocolNumber,idempotenceToken);
    }

    @Override
    public void paymentEventsRequestPagoPa(PaymentEventsRequestPagoPa paymentEventsRequestPagoPa) throws RestClientException {
        refreshTokenInteropClient();
        this.paymentEventsApi.paymentEventsRequestPagoPaWithHttpInfo(paymentEventsRequestPagoPa);
    }

    @Override
    public void paymentEventsRequestF24(PaymentEventsRequestF24 paymentEventsRequestF24) throws RestClientException {
        refreshTokenInteropClient();
        this.paymentEventsApi.paymentEventsRequestF24WithHttpInfo(paymentEventsRequestF24);
    }

    @Override
    public RequestStatus notificationCancellation(String iun) throws RestClientException {
        refreshTokenInteropClient();
        RequestStatus status = this.notificationCancellationApi.notificationCancellation(iun);
        return status;
    }



}
