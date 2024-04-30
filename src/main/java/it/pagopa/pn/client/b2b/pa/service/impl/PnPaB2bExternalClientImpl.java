package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.ApiClient;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api.*;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.api.NotificationProcessCostApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.NotificationProcessCostResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import static it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton.ENEBLED_INTEROP;


@Slf4j
@Component()
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@ConditionalOnProperty( name = IPnPaB2bClient.IMPLEMENTATION_TYPE_PROPERTY, havingValue = "external", matchIfMissing = true)
public class PnPaB2bExternalClientImpl implements IPnPaB2bClient {
    private final RestTemplate restTemplate;
    private final NewNotificationApi newNotificationApi;
    private final it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api_v1.NewNotificationApi newNotificationApiV1;
    private final it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api_v2.NewNotificationApi newNotificationApiV2;
    private final it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api_v21.NewNotificationApi newNotificationApiV21;
    private final SenderReadB2BApi senderReadB2BApi;
    private final it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api_v1.SenderReadB2BApi senderReadB2BApiV1;
    private final it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api_v2.SenderReadB2BApi senderReadB2BApiV2;
    private final it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api_v21.SenderReadB2BApi senderReadB2BApiV21;
    private final LegalFactsApi legalFactsApi;
    private final it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api_v21.NotificationPriceApi notificationPriceApiV21;
    private final NotificationPriceV23Api notificationPriceV23Api;
    private final NotificationProcessCostApi notificationProcessCostApi;
    private final NotificationCancellationApi  notificationCancellationApi;
    private final PaymentEventsApi paymentEventsApi;
    private final it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api_v1.PaymentEventsApi paymentEventsApiV1;
    private final it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api_v2.PaymentEventsApi paymentEventsApiV2;
    private final it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api_v21.PaymentEventsApi paymentEventsApiV21;
    private final String basePath;
    private final String apiKeyMvp1;
    private final String apiKeyMvp2;
    private final String apiKeyGa;
    private final String apiKeySon;
    private final String apiKeyRoot;
    private ApiKeyType apiKeySetted = ApiKeyType.MVP_1;
    private String bearerTokenInterop;
    private final String enableInterop;
    private final InteropTokenSingleton interopTokenSingleton;
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";


    public PnPaB2bExternalClientImpl(RestTemplate restTemplate,
            InteropTokenSingleton interopTokenSingleton,
            @Value("${pn.external.base-url}") String basePath,
            @Value("${pn.external.api-key}") String apiKeyMvp1,
            @Value("${pn.external.api-key-2}") String apiKeyMvp2,
            @Value("${pn.external.api-key-GA}") String apiKeyGa,
            @Value("${pn.external.api-key-SON}") String apiKeySon,
            @Value("${pn.external.api-key-ROOT}") String apiKeyRoot,
            @Value("${pn.interop.enable}") String enableInterop
    ) {
        this.restTemplate = restTemplate;
        this.basePath = basePath;
        this.apiKeyMvp1 = apiKeyMvp1;
        this.apiKeyMvp2 = apiKeyMvp2;
        this.apiKeyGa = apiKeyGa;
        this.apiKeySon = apiKeySon;
        this.apiKeyRoot = apiKeyRoot;
        this.enableInterop = enableInterop;
        if (ENEBLED_INTEROP.equalsIgnoreCase(enableInterop)) {
            this.bearerTokenInterop = interopTokenSingleton.getTokenInterop();
        }
        this.newNotificationApi = new NewNotificationApi( newApiClient( restTemplate, basePath, apiKeyMvp1, bearerTokenInterop,enableInterop) );
        this.newNotificationApiV1 = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api_v1.NewNotificationApi( newApiClient( restTemplate, basePath, apiKeyMvp1, bearerTokenInterop,enableInterop) );
        this.newNotificationApiV2 = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api_v2.NewNotificationApi( newApiClient( restTemplate, basePath, apiKeyMvp1, bearerTokenInterop,enableInterop) );
        this.newNotificationApiV21 = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api_v21.NewNotificationApi( newApiClient( restTemplate, basePath, apiKeyMvp1, bearerTokenInterop,enableInterop) );
        this.senderReadB2BApi = new SenderReadB2BApi( newApiClient( restTemplate, basePath, apiKeyMvp1, bearerTokenInterop,enableInterop) );
        this.senderReadB2BApiV1 = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api_v1.SenderReadB2BApi( newApiClient( restTemplate, basePath, apiKeyMvp1, bearerTokenInterop,enableInterop) );
        this.senderReadB2BApiV2 = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api_v2.SenderReadB2BApi( newApiClient( restTemplate, basePath, apiKeyMvp1, bearerTokenInterop,enableInterop) );
        this.senderReadB2BApiV21 = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api_v21.SenderReadB2BApi( newApiClient( restTemplate, basePath, apiKeyMvp1, bearerTokenInterop,enableInterop) );
        this.legalFactsApi = new LegalFactsApi(newApiClient( restTemplate, basePath, apiKeyMvp1, bearerTokenInterop,enableInterop));
        this.notificationPriceApiV21 = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api_v21.NotificationPriceApi(newApiClient( restTemplate, basePath, apiKeyMvp1, bearerTokenInterop,enableInterop));
        this.notificationPriceV23Api= new NotificationPriceV23Api(newApiClient( restTemplate, basePath, apiKeyMvp1, bearerTokenInterop,enableInterop));
        this.notificationProcessCostApi = new NotificationProcessCostApi(newApiClientPriv( restTemplate, basePath, apiKeyMvp1, bearerTokenInterop,enableInterop));
        this.paymentEventsApi = new PaymentEventsApi(newApiClient( restTemplate, basePath, apiKeyMvp1, bearerTokenInterop,enableInterop));
        this.paymentEventsApiV1 = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api_v1.PaymentEventsApi(newApiClient( restTemplate, basePath, apiKeyMvp1, bearerTokenInterop,enableInterop));
        this.paymentEventsApiV2 = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api_v2.PaymentEventsApi(newApiClient( restTemplate, basePath, apiKeyMvp1, bearerTokenInterop,enableInterop));
        this.paymentEventsApiV21 = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api_v21.PaymentEventsApi(newApiClient( restTemplate, basePath, apiKeyMvp1, bearerTokenInterop,enableInterop));
        this.notificationCancellationApi = new NotificationCancellationApi(newApiClient( restTemplate, basePath, apiKeyMvp1, bearerTokenInterop,enableInterop));
        this.interopTokenSingleton = interopTokenSingleton;
    }

    //@Scheduled(cron = "* * * * * ?")
    private void refreshAndSetTokenInteropClient(){
        if (ENEBLED_INTEROP.equalsIgnoreCase(enableInterop)) {
            String tokenInterop = interopTokenSingleton.getTokenInterop();
            if(!tokenInterop.equals(this.bearerTokenInterop)){
                log.info("b2bClient call interopTokenSingleton");
                this.bearerTokenInterop = tokenInterop;
                this.newNotificationApi.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + bearerTokenInterop);
                this.newNotificationApiV1.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + bearerTokenInterop);
                this.newNotificationApiV2.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + bearerTokenInterop);
                this.newNotificationApiV21.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + bearerTokenInterop);
                this.senderReadB2BApi.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + bearerTokenInterop);
                this.senderReadB2BApiV1.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + bearerTokenInterop);
                this.senderReadB2BApiV2.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + bearerTokenInterop);
                this.senderReadB2BApiV21.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + bearerTokenInterop);
                this.legalFactsApi.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + bearerTokenInterop);
                this.notificationPriceApiV21.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + bearerTokenInterop);
                this.notificationPriceV23Api.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + bearerTokenInterop);
                this.notificationProcessCostApi.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + bearerTokenInterop);
                this.paymentEventsApi.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + bearerTokenInterop);
                this.paymentEventsApiV1.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + bearerTokenInterop);
                this.paymentEventsApiV2.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + bearerTokenInterop);
                this.paymentEventsApiV21.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + bearerTokenInterop);
                this.notificationCancellationApi.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + bearerTokenInterop);
            }
        }
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String apikey, String bearerToken, String enableInterop ) {
        ApiClient newApiClient = new ApiClient( restTemplate );
        newApiClient.setBasePath( basePath );
        newApiClient.addDefaultHeader("x-api-key", apikey );
        if (ENEBLED_INTEROP.equalsIgnoreCase(enableInterop)) {
            newApiClient.addDefaultHeader(AUTHORIZATION, BEARER + bearerToken);
        }
        return newApiClient;
    }

    private static it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.ApiClient newApiClientPriv(RestTemplate restTemplate, String basePath, String apikey, String bearerToken, String enableInterop ) {
        it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.ApiClient newApiClient = new it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.ApiClient( restTemplate );
        newApiClient.setBasePath( basePath );
        newApiClient.addDefaultHeader("x-api-key", apikey );
        if (ENEBLED_INTEROP.equalsIgnoreCase(enableInterop)) {
            newApiClient.addDefaultHeader(AUTHORIZATION, BEARER + bearerToken);
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
            case SON:
                if(this.apiKeySetted != ApiKeyType.SON) {
                    setApiKey(apiKeySon);
                    this.apiKeySetted = ApiKeyType.SON;
                }
                beenSet = true;
                break;
            case ROOT:
                if(this.apiKeySetted != ApiKeyType.ROOT) {
                    setApiKey(apiKeyRoot);
                    this.apiKeySetted = ApiKeyType.ROOT;
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
        this.newNotificationApiV1.setApiClient(newApiClient(restTemplate, basePath, apiKey, bearerTokenInterop,enableInterop));
        this.newNotificationApiV2.setApiClient(newApiClient(restTemplate, basePath, apiKey, bearerTokenInterop,enableInterop));
        this.newNotificationApiV21.setApiClient(newApiClient(restTemplate, basePath, apiKey, bearerTokenInterop,enableInterop));
        this.senderReadB2BApi.setApiClient(newApiClient(restTemplate, basePath, apiKey, bearerTokenInterop,enableInterop));
        this.senderReadB2BApiV1.setApiClient(newApiClient(restTemplate, basePath, apiKey, bearerTokenInterop,enableInterop));
        this.senderReadB2BApiV2.setApiClient(newApiClient(restTemplate, basePath, apiKey, bearerTokenInterop,enableInterop));
        this.senderReadB2BApiV21.setApiClient(newApiClient(restTemplate, basePath, apiKey, bearerTokenInterop,enableInterop));
        this.legalFactsApi.setApiClient(newApiClient(restTemplate, basePath, apiKey, bearerTokenInterop,enableInterop));
        this.notificationPriceApiV21.setApiClient(newApiClient(restTemplate, basePath, apiKey, bearerTokenInterop,enableInterop));
        this.notificationPriceV23Api.setApiClient(newApiClient(restTemplate, basePath, apiKey, bearerTokenInterop,enableInterop));
        this.notificationProcessCostApi.setApiClient(newApiClientPriv(restTemplate, basePath, apiKey, bearerTokenInterop,enableInterop));
        this.paymentEventsApi.setApiClient(newApiClient(restTemplate, basePath, apiKey, bearerTokenInterop,enableInterop));
        this.paymentEventsApiV1.setApiClient(newApiClient(restTemplate, basePath, apiKey, bearerTokenInterop,enableInterop));
        this.paymentEventsApiV2.setApiClient(newApiClient(restTemplate, basePath, apiKey, bearerTokenInterop,enableInterop));
        this.paymentEventsApiV21.setApiClient(newApiClient(restTemplate, basePath, apiKey, bearerTokenInterop,enableInterop));
        this.notificationCancellationApi.setApiClient(newApiClient(restTemplate, basePath, apiKey, bearerTokenInterop,enableInterop));
    }

    public NotificationAttachmentDownloadMetadataResponse getSentNotificationDocument(String iun, Integer docidx) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApi.retrieveSentNotificationDocument(iun, docidx);
    }

    public  it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationAttachmentDownloadMetadataResponse getSentNotificationDocumentV1(String iun, Integer docidx) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApiV1.retrieveSentNotificationDocument(iun, docidx);
    }

    public  it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationAttachmentDownloadMetadataResponse getSentNotificationDocumentV2(String iun, Integer docidx) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApiV2.retrieveSentNotificationDocument(iun, docidx);
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationAttachmentDownloadMetadataResponse getSentNotificationDocumentV21(String iun, Integer docidx) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApiV21.retrieveSentNotificationDocument(iun, docidx);
    }

    public NotificationAttachmentDownloadMetadataResponse getSentNotificationAttachment(String iun, Integer recipientIdx, String attachname, Integer attachmentIdx) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApi.retrieveSentNotificationAttachment(iun, recipientIdx, attachname,attachmentIdx);
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationAttachmentDownloadMetadataResponse getSentNotificationAttachmentV1(String iun, Integer recipientIdx, String attachmentName) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApiV1.retrieveSentNotificationAttachment(iun, recipientIdx, attachmentName);
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationAttachmentDownloadMetadataResponse getSentNotificationAttachmentV2(String iun, Integer recipientIdx, String attachmentName) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApiV2.retrieveSentNotificationAttachment(iun, recipientIdx, attachmentName);
    }
    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationAttachmentDownloadMetadataResponse getSentNotificationAttachmentV21(String iun, Integer recipientIdx, String attachmentName, Integer attachmentIdx) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApiV21.retrieveSentNotificationAttachment(iun, recipientIdx, attachmentName,attachmentIdx);
    }

    public LegalFactDownloadMetadataResponse getLegalFact(String iun, LegalFactCategory legalFactType, String legalFactId) {
        refreshAndSetTokenInteropClient();
        return legalFactsApi.retrieveLegalFact(iun, legalFactType, legalFactId);
    }

    public LegalFactDownloadMetadataResponse getDownloadLegalFact(String iun,  String legalFactId) {
        refreshAndSetTokenInteropClient();
        return legalFactsApi.downloadLegalFactById(iun,  legalFactId);
    }

    @Override
    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationPriceResponse getNotificationPrice(String paTaxId, String noticeCode) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return this.notificationPriceApiV21.retrieveNotificationPrice(paTaxId,noticeCode);
    }

    public NotificationPriceResponseV23 getNotificationPriceV23(String paTaxId, String noticeCode) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return this.notificationPriceV23Api.retrieveNotificationPriceV23(paTaxId,noticeCode);
    }

    public NotificationProcessCostResponse getNotificationProcessCost(String iun, Integer recipientIndex, it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model.NotificationFeePolicy notificationFeePolicy, Boolean applyCost, Integer paFee, Integer vat) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return this.notificationProcessCostApi.notificationProcessCost(iun, recipientIndex, notificationFeePolicy, applyCost, paFee,vat);
    }

    public List<PreLoadResponse> presignedUploadRequest(List<PreLoadRequest> preLoadRequest) {
        refreshAndSetTokenInteropClient();
        return newNotificationApi.presignedUploadRequest( preLoadRequest );
    }

    public NewNotificationResponse sendNewNotification(NewNotificationRequestV23 newNotificationRequest) {
        refreshAndSetTokenInteropClient();
        return newNotificationApi.sendNewNotificationV23( newNotificationRequest );
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationResponse sendNewNotificationV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationRequest newNotificationRequest) {
        refreshAndSetTokenInteropClient();
        return newNotificationApiV1.sendNewNotification( newNotificationRequest );
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationResponse sendNewNotificationV2(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationRequest newNotificationRequest) {
        refreshAndSetTokenInteropClient();
        return newNotificationApiV2.sendNewNotification( newNotificationRequest );
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationResponse sendNewNotificationV21(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationRequestV21 newNotificationRequest) {
        refreshAndSetTokenInteropClient();
        return newNotificationApiV21.sendNewNotificationV21( newNotificationRequest );
    }
    @Override
    public FullSentNotificationV23 getSentNotification(String iun) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApi.retrieveSentNotificationV23( iun );
    }

    @Override
    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.FullSentNotification getSentNotificationV1(String iun) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApiV1.retrieveSentNotification( iun );
    }

    @Override
    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.FullSentNotificationV20 getSentNotificationV2(String iun) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApiV2.retrieveSentNotificationV20( iun );
    }

    @Override
    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.FullSentNotificationV21 getSentNotificationV21(String iun) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApiV21.retrieveSentNotificationV21( iun );
    }

    @Override
    public NewNotificationRequestStatusResponseV23 getNotificationRequestStatus(String notificationRequestId) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApi.retrieveNotificationRequestStatusV23( notificationRequestId, null, null );
    }

    @Override
    public  it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationRequestStatusResponse getNotificationRequestStatusV1(String notificationRequestId) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApiV1.retrieveNotificationRequestStatus( notificationRequestId, null, null );
    }

    @Override
    public  it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationRequestStatusResponse getNotificationRequestStatusV2(String notificationRequestId) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApiV2.retrieveNotificationRequestStatus( notificationRequestId, null, null );
    }

    @Override
    public  it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationRequestStatusResponseV21 getNotificationRequestStatusV21(String notificationRequestId) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApiV21.retrieveNotificationRequestStatusV21( notificationRequestId, null, null );
    }

    @Override
    public NewNotificationRequestStatusResponseV23 getNotificationRequestStatusAllParam(String notificationRequestId, String paProtocolNumber, String idempotenceToken) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApi.retrieveNotificationRequestStatusV23(notificationRequestId,paProtocolNumber,idempotenceToken);
    }

    @Override
    public void paymentEventsRequestPagoPa(PaymentEventsRequestPagoPa paymentEventsRequestPagoPa) throws RestClientException {
        refreshAndSetTokenInteropClient();
        this.paymentEventsApi.paymentEventsRequestPagoPaWithHttpInfo(paymentEventsRequestPagoPa);
    }

    @Override
    public void paymentEventsRequestPagoPaV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.PaymentEventsRequestPagoPa paymentEventsRequestPagoPa) throws RestClientException {
        refreshAndSetTokenInteropClient();
        this.paymentEventsApiV1.paymentEventsRequestPagoPaWithHttpInfo(paymentEventsRequestPagoPa);
    }

    @Override
    public void paymentEventsRequestPagoPaV2(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.PaymentEventsRequestPagoPa paymentEventsRequestPagoPa) throws RestClientException {
        refreshAndSetTokenInteropClient();
        this.paymentEventsApiV2.paymentEventsRequestPagoPaWithHttpInfo(paymentEventsRequestPagoPa);
    }

    @Override
    public void paymentEventsRequestF24(PaymentEventsRequestF24 paymentEventsRequestF24) throws RestClientException {
        refreshAndSetTokenInteropClient();
        this.paymentEventsApi.paymentEventsRequestF24WithHttpInfo(paymentEventsRequestF24);
    }

    @Override
    public RequestStatus notificationCancellation(String iun) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return this.notificationCancellationApi.notificationCancellation(iun);
    }
}