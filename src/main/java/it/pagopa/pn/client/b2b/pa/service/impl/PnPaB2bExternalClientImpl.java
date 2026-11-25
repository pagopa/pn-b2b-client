package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.ApiClient;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api.*;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.api_v26.LegalFactsPrivateApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v26.CxTypeAuthFleet;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v26.LegalFactDownloadMetadataWithContentTypeResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.api_v26.NotificationProcessCostApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v26.NotificationProcessCostResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v26.NotificationFeePolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton.INTEROP_ENABLED;

@Slf4j
@Component()
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@ConditionalOnProperty(name = IPnPaB2bClient.IMPLEMENTATION_TYPE_PROPERTY, havingValue = "external", matchIfMissing = true)
public class PnPaB2bExternalClientImpl implements IPnPaB2bClient {
    private final RestTemplate restTemplate;
    private final NewNotificationApi newNotificationApi;
    private final SenderReadB2BApi senderReadB2BApi;
    private final LegalFactsApi legalFactsApi;
    private final LegalFactsPrivateApi legalFactsPrivateApi;
    private final NotificationPriceApi notificationPriceApiV21;
    private final NotificationPriceV23Api notificationPriceV23Api;
    private final NotificationProcessCostApi notificationProcessCostApi;
    private final NotificationCancellationApi notificationCancellationApi;
    private final PaymentEventsApi paymentEventsApi;
    private final String basePath;
    private final String deliveryPushBasePath;
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
                                     @Value("${pn.internal.delivery-push-base-url}") String deliveryPushBasePath,
                                     @Value("${pn.external.api-key}") String apiKeyMvp1,
                                     @Value("${pn.external.api-key-2}") String apiKeyMvp2,
                                     @Value("${pn.external.api-key-GA}") String apiKeyGa,
                                     @Value("${pn.external.api-key-SON}") String apiKeySon,
                                     @Value("${pn.external.api-key-ROOT}") String apiKeyRoot,
                                     @Value("${pn.interop.enable}") String enableInterop
    ) {
        this.restTemplate = restTemplate;
        this.basePath = basePath;
        this.deliveryPushBasePath = deliveryPushBasePath;
        this.apiKeyMvp1 = apiKeyMvp1;
        this.apiKeyMvp2 = apiKeyMvp2;
        this.apiKeyGa = apiKeyGa;
        this.apiKeySon = apiKeySon;
        this.apiKeyRoot = apiKeyRoot;
        this.enableInterop = enableInterop;
        if (INTEROP_ENABLED.equalsIgnoreCase(enableInterop)) {
            this.bearerTokenInterop = interopTokenSingleton.getTokenInterop();
        }
        this.newNotificationApi = new NewNotificationApi(newApiClient(restTemplate, basePath, apiKeyMvp1, bearerTokenInterop, enableInterop));
        this.senderReadB2BApi = new SenderReadB2BApi(newApiClient(restTemplate, basePath, apiKeyMvp1, bearerTokenInterop, enableInterop));
        this.legalFactsApi = new LegalFactsApi(newApiClient(restTemplate, basePath, apiKeyMvp1, bearerTokenInterop, enableInterop));
        this.legalFactsPrivateApi = new LegalFactsPrivateApi(newApiClientPriv(restTemplate, deliveryPushBasePath, apiKeyMvp1, bearerTokenInterop, enableInterop));
        this.notificationPriceApiV21 = new NotificationPriceApi(newApiClient(restTemplate, basePath, apiKeyMvp1, bearerTokenInterop, enableInterop));
        this.notificationPriceV23Api = new NotificationPriceV23Api(newApiClient(restTemplate, basePath, apiKeyMvp1, bearerTokenInterop, enableInterop));
        this.notificationProcessCostApi = new NotificationProcessCostApi(newApiClientPriv(restTemplate, deliveryPushBasePath, apiKeyMvp1, bearerTokenInterop, enableInterop));
        this.paymentEventsApi = new PaymentEventsApi(newApiClient(restTemplate, basePath, apiKeyMvp1, bearerTokenInterop, enableInterop));
        this.notificationCancellationApi = new NotificationCancellationApi(newApiClient(restTemplate, basePath, apiKeyMvp1, bearerTokenInterop, enableInterop));
        this.interopTokenSingleton = interopTokenSingleton;
    }

    //@Scheduled(cron = "* * * * * ?")
    private void refreshAndSetTokenInteropClient() {
        if (INTEROP_ENABLED.equalsIgnoreCase(enableInterop)) {
            String tokenInterop = interopTokenSingleton.getTokenInterop();
            if (!tokenInterop.equals(this.bearerTokenInterop)) {
                log.info("b2bClient call interopTokenSingleton");
                this.bearerTokenInterop = tokenInterop;
                this.newNotificationApi.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + bearerTokenInterop);
                this.senderReadB2BApi.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + bearerTokenInterop);
                this.legalFactsApi.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + bearerTokenInterop);
                this.legalFactsPrivateApi.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + bearerTokenInterop);
                this.notificationPriceApiV21.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + bearerTokenInterop);
                this.notificationPriceV23Api.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + bearerTokenInterop);
                this.notificationProcessCostApi.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + bearerTokenInterop);
                this.paymentEventsApi.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + bearerTokenInterop);
                this.notificationCancellationApi.getApiClient().addDefaultHeader(AUTHORIZATION, BEARER + bearerTokenInterop);
            }
        }
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String apikey, String bearerToken, String enableInterop) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("x-api-key", apikey);
        if (INTEROP_ENABLED.equalsIgnoreCase(enableInterop)) {
            newApiClient.addDefaultHeader(AUTHORIZATION, BEARER + bearerToken);
        }
        return newApiClient;
    }

    private static it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.ApiClient newApiClientPriv(RestTemplate restTemplate, String basePath, String apikey, String bearerToken, String enableInterop) {
        it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.ApiClient newApiClient = new it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("x-api-key", apikey);
        if (INTEROP_ENABLED.equalsIgnoreCase(enableInterop)) {
            newApiClient.addDefaultHeader(AUTHORIZATION, BEARER + bearerToken);
        }
        return newApiClient;
    }

    @Override
    public boolean setApiKeys(ApiKeyType apiKey) {
        boolean beenSet = false;
        switch (apiKey) {
            case MVP_1 -> {
                if (this.apiKeySetted != ApiKeyType.MVP_1) {
                    setApiKey(apiKeyMvp1);
                    this.apiKeySetted = ApiKeyType.MVP_1;
                }
                beenSet = true;
            }
            case MVP_2 -> {
                if (this.apiKeySetted != ApiKeyType.MVP_2) {
                    setApiKey(apiKeyMvp2);
                    this.apiKeySetted = ApiKeyType.MVP_2;
                }
                beenSet = true;
            }
            case GA -> {
                if (this.apiKeySetted != ApiKeyType.GA) {
                    setApiKey(apiKeyGa);
                    this.apiKeySetted = ApiKeyType.GA;
                }
                beenSet = true;
            }
            case SON -> {
                if (this.apiKeySetted != ApiKeyType.SON) {
                    setApiKey(apiKeySon);
                    this.apiKeySetted = ApiKeyType.SON;
                }
                beenSet = true;
            }
            case ROOT -> {
                if (this.apiKeySetted != ApiKeyType.ROOT) {
                    setApiKey(apiKeyRoot);
                    this.apiKeySetted = ApiKeyType.ROOT;
                }
                beenSet = true;
            }
        }
        return beenSet;
    }

    @Override
    public ApiKeyType getApiKeySetted() {
        return this.apiKeySetted;
    }

    public void setApiKey(String apiKey) {
        this.newNotificationApi.setApiClient(newApiClient(restTemplate, basePath, apiKey, bearerTokenInterop, enableInterop));
        this.senderReadB2BApi.setApiClient(newApiClient(restTemplate, basePath, apiKey, bearerTokenInterop, enableInterop));
        this.legalFactsApi.setApiClient(newApiClient(restTemplate, basePath, apiKey, bearerTokenInterop, enableInterop));
        this.legalFactsPrivateApi.setApiClient(newApiClientPriv(restTemplate, deliveryPushBasePath, apiKey, bearerTokenInterop, enableInterop));
        this.notificationPriceApiV21.setApiClient(newApiClient(restTemplate, basePath, apiKey, bearerTokenInterop, enableInterop));
        this.notificationPriceV23Api.setApiClient(newApiClient(restTemplate, basePath, apiKey, bearerTokenInterop, enableInterop));
        this.notificationProcessCostApi.setApiClient(newApiClientPriv(restTemplate, deliveryPushBasePath, apiKey, bearerTokenInterop, enableInterop));
        this.paymentEventsApi.setApiClient(newApiClient(restTemplate, basePath, apiKey, bearerTokenInterop, enableInterop));
        this.notificationCancellationApi.setApiClient(newApiClient(restTemplate, basePath, apiKey, bearerTokenInterop, enableInterop));
    }

    public NotificationAttachmentDownloadMetadataResponse getSentNotificationDocument(String iun, Integer docIndex) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApi.retrieveSentNotificationDocument(iun, docIndex);
    }

    public NotificationAttachmentDownloadMetadataResponse getSentNotificationAttachment(String iun, Integer recipientIdx, String attachmentName, Integer attachmentIdx) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApi.retrieveSentNotificationAttachment(iun, recipientIdx, attachmentName, attachmentIdx);
    }

    public LegalFactDownloadMetadataResponse getLegalFact(String iun, LegalFactCategory legalFactType, String legalFactId) {
        refreshAndSetTokenInteropClient();
        return legalFactsApi.retrieveLegalFact(iun, legalFactType, legalFactId);
    }

    public LegalFactDownloadMetadataResponse getDownloadLegalFact(String iun, String legalFactId) {
        refreshAndSetTokenInteropClient();
        return legalFactsApi.downloadLegalFactById(iun, legalFactId);
    }

    @Override
    public NotificationPriceResponse getNotificationPrice(String paTaxId, String noticeCode) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return this.notificationPriceApiV21.retrieveNotificationPrice(paTaxId, noticeCode);
    }

    public NotificationPriceResponseV23 getNotificationPriceV23(String paTaxId, String noticeCode) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return this.notificationPriceV23Api.retrieveNotificationPriceV23(paTaxId, noticeCode);
    }

    @Override
    public NotificationProcessCostResponse getNotificationProcessCost(String iun, Integer recipientIndex, String notificationFeePolicy, Boolean applyCost, Integer paFee, Integer vat) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return this.notificationProcessCostApi.notificationProcessCost(iun, recipientIndex, NotificationFeePolicy.valueOf(notificationFeePolicy), applyCost, paFee, vat);
    }

    @Override
    public LegalFactDownloadMetadataWithContentTypeResponse getLegalFactByIdPrivate(String recipientInternalId, String iun, String legalFactId, String mandateId, CxTypeAuthFleet xPagopaPnCxType, List<String> xPagopaPnCxGroups) throws RestClientException {
        return legalFactsPrivateApi.getLegalFactByIdPrivate(recipientInternalId, iun, legalFactId, mandateId, xPagopaPnCxType, xPagopaPnCxGroups);
    }

    public List<PreLoadResponse> presignedUploadRequest(List<PreLoadRequest> preLoadRequest) {
        refreshAndSetTokenInteropClient();
        return newNotificationApi.presignedUploadRequest(preLoadRequest);
    }

    //V10
    @Override
    public NewNotificationResponse sendNewNotificationV1(NewNotificationRequest newNotificationRequest) {
        refreshAndSetTokenInteropClient();
        return newNotificationApi.sendNewNotification(newNotificationRequest);
    }

    @Override
    public NewNotificationRequestStatusResponse getNotificationRequestStatusV1(String notificationRequestId) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApi.retrieveNotificationRequestStatus(notificationRequestId, null, null);
    }

    @Override
    public NewNotificationRequestStatusResponse getNotificationRequestStatusAllParamV1(String notificationRequestId, String paProtocolNumber, String idempotenceToken) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApi.retrieveNotificationRequestStatus(notificationRequestId, paProtocolNumber, idempotenceToken);
    }

    //V20
    @Override
    public NewNotificationResponse sendNewNotificationV2(NewNotificationRequest newNotificationRequest) {
        refreshAndSetTokenInteropClient();
        return newNotificationApi.sendNewNotification(newNotificationRequest);
    }

    @Override
    public NewNotificationRequestStatusResponse getNotificationRequestStatusV2(String notificationRequestId) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApi.retrieveNotificationRequestStatus(notificationRequestId, null, null);
    }

    @Override
    public NewNotificationRequestStatusResponse getNotificationRequestStatusAllParamV2(String notificationRequestId, String paProtocolNumber, String idempotenceToken) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApi.retrieveNotificationRequestStatus(notificationRequestId, paProtocolNumber, idempotenceToken);
    }

    //V21
    @Override
    public NewNotificationResponse sendNewNotificationV21(NewNotificationRequestV21 newNotificationRequest) {
        refreshAndSetTokenInteropClient();
        return newNotificationApi.sendNewNotificationV21(newNotificationRequest);
    }

    @Override
    public NewNotificationRequestStatusResponseV21 getNotificationRequestStatusV21(String notificationRequestId) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApi.retrieveNotificationRequestStatusV21(notificationRequestId, null, null);
    }

    @Override
    public NewNotificationRequestStatusResponseV21 getNotificationRequestStatusAllParamV21(String notificationRequestId, String paProtocolNumber, String idempotenceToken) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApi.retrieveNotificationRequestStatusV21(notificationRequestId, paProtocolNumber, idempotenceToken);
    }

    //V23
    @Override
    public NewNotificationResponse sendNewNotificationV23(NewNotificationRequestV23 newNotificationRequest) {
        refreshAndSetTokenInteropClient();
        return newNotificationApi.sendNewNotificationV23(newNotificationRequest);
    }

    @Override
    public NewNotificationRequestStatusResponseV23 getNotificationRequestStatusV23(String notificationRequestId) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApi.retrieveNotificationRequestStatusV23(notificationRequestId, null, null);
    }

    @Override
    public NewNotificationRequestStatusResponseV23 getNotificationRequestStatusAllParamV23(String notificationRequestId, String paProtocolNumber, String idempotenceToken) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApi.retrieveNotificationRequestStatusV23(notificationRequestId, paProtocolNumber, idempotenceToken);
    }

    //V24
    public NewNotificationResponse sendNewNotificationV24(NewNotificationRequestV24 newNotificationRequest) {
        refreshAndSetTokenInteropClient();
        return newNotificationApi.sendNewNotificationV24(newNotificationRequest);
    }

    @Override
    public NewNotificationRequestStatusResponseV24 getNotificationRequestStatusV24(String notificationRequestId) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApi.retrieveNotificationRequestStatusV24(notificationRequestId, null, null);
    }

    @Override
    public NewNotificationRequestStatusResponseV24 getNotificationRequestStatusAllParamV24(String notificationRequestId, String paProtocolNumber, String idempotenceToken) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApi.retrieveNotificationRequestStatusV24(notificationRequestId, paProtocolNumber, idempotenceToken);
    }

    //V25
    @Override
    public NewNotificationResponse sendNewNotificationV25(NewNotificationRequestV25 newNotificationRequest) {
        refreshAndSetTokenInteropClient();
        return newNotificationApi.sendNewNotificationV25(newNotificationRequest);
    }

    @Override
    public NewNotificationRequestStatusResponseV25 getNotificationRequestStatusV25(String notificationRequestId) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApi.retrieveNotificationRequestStatusV25(notificationRequestId, null, null);
    }

    @Override
    public NewNotificationRequestStatusResponseV25 getNotificationRequestStatusAllParamV25(String notificationRequestId, String protocolNumber, String idempotenceToken) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApi.retrieveNotificationRequestStatusV25(notificationRequestId, protocolNumber, idempotenceToken);
    }

    //V26
    @Override
    public NewNotificationResponse sendNewNotificationV26(NewNotificationRequestV26 newNotificationRequest) {
        refreshAndSetTokenInteropClient();
        return newNotificationApi.sendNewNotificationV26(newNotificationRequest);
    }

    @Override
    public NewNotificationRequestStatusResponseV26 getNotificationRequestStatusV26(String notificationRequestId) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApi.retrieveNotificationRequestStatusV26(notificationRequestId, null, null);
    }

    @Override
    public NewNotificationRequestStatusResponseV26 getNotificationRequestStatusAllParamV26(String notificationRequestId, String protocolNumber, String idempotenceToken) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApi.retrieveNotificationRequestStatusV26(notificationRequestId, protocolNumber, idempotenceToken);
    }


    //FullSentNotifications
    @Override
    public FullSentNotification getSentNotificationV1(String iun) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApi.retrieveSentNotification(iun);
    }

    @Override
    public FullSentNotificationV20 getSentNotificationV2(String iun) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApi.retrieveSentNotificationV20(iun);
    }

    @Override
    public FullSentNotificationV21 getSentNotificationV21(String iun) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApi.retrieveSentNotificationV21(iun);
    }

    @Override
    public FullSentNotificationV23 getSentNotificationV23(String iun) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApi.retrieveSentNotificationV23(iun);
    }

    @Override
    public FullSentNotificationV24 getSentNotificationV24(String iun) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApi.retrieveSentNotificationV24(iun);
    }

    @Override
    public FullSentNotificationV25 getSentNotificationV25(String iun) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApi.retrieveSentNotificationV25(iun);
    }

    @Override
    public FullSentNotificationV26 getSentNotificationV26(String iun) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApi.retrieveSentNotificationV26(iun);
    }

    @Override
    public FullSentNotificationV27 getSentNotificationV27(String iun) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApi.retrieveSentNotificationV27(iun);
    }

    @Override
    public FullSentNotificationV28 getSentNotificationV28(String iun) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApi.retrieveSentNotificationV28(iun);
    }

    @Override
    public FullSentNotificationV29 getSentNotificationV29(String iun) {
        refreshAndSetTokenInteropClient();
        return senderReadB2BApi.retrieveSentNotificationV29(iun);
    }

    @Override
    public void paymentEventsRequestPagoPa(PaymentEventsRequestPagoPa paymentEventsRequestPagoPa) throws RestClientException {
        refreshAndSetTokenInteropClient();
        this.paymentEventsApi.paymentEventsRequestPagoPaWithHttpInfo(paymentEventsRequestPagoPa);
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