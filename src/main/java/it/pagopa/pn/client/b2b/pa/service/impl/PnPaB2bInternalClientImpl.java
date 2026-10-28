package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.ApiClient;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.api.NewNotificationApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.api.NotificationPriceV23Api;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.api.SenderReadB2BApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.model.CxTypeAuthFleet;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v26.NotificationProcessCostResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v26.LegalFactDownloadMetadataWithContentTypeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static it.pagopa.pn.client.b2b.pa.utils.JsonDeepCopyMapper.deepCopy;

@Component()
@ConditionalOnProperty(name = IPnPaB2bClient.IMPLEMENTATION_TYPE_PROPERTY, havingValue = "internal")
public class PnPaB2bInternalClientImpl implements IPnPaB2bClient {
    private final NewNotificationApi newNotificationApi;
    private final SenderReadB2BApi senderReadB2BApi;
    private final NotificationPriceV23Api notificationPriceV23Api;
    private final String paId;
    private final String operatorId;

    private final List<String> groups;

    public PnPaB2bInternalClientImpl(
            RestTemplate restTemplate,
            @Value("${pn.internal.delivery-base-url}") String deliveryBasePath,
            @Value("${pn.internal.delivery-push-base-url}") String deliveryPushBasePath,
            @Value("${pn.internal.pa-id}") String paId) {

        this.paId = paId;
        this.operatorId = "TestMv";
        this.groups = Collections.emptyList();
        this.newNotificationApi = new NewNotificationApi(newApiClient(restTemplate, deliveryBasePath));
        this.senderReadB2BApi = new SenderReadB2BApi(newApiClient(restTemplate, deliveryBasePath));
        this.notificationPriceV23Api = new NotificationPriceV23Api(newApiClient(restTemplate, deliveryPushBasePath));
    }

    private static it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaldeliveryPushb2bpa.ApiClient
    newApiClient(RestTemplate restTemplate, String basePath, Boolean isDeliveryPushApi) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaldeliveryPushb2bpa.ApiClient newApiClient = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaldeliveryPushb2bpa.ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        return newApiClient;
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        return newApiClient;
    }


    public NotificationAttachmentDownloadMetadataResponse getSentNotificationDocument(String iun, Integer docIndex) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.model.NotificationAttachmentDownloadMetadataResponse response =
                senderReadB2BApi.getSentNotificationDocument(
                        operatorId
                        , CxTypeAuthFleet.PA
                        , paId
                        , iun
                        , docIndex
                        , groups);

        return deepCopy(response, NotificationAttachmentDownloadMetadataResponse.class);
    }

    /**
     * V1
     */
    @Override
    public NewNotificationResponse sendNewNotificationV1(NewNotificationRequest newNotificationRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NewNotificationRequestStatusResponse getNotificationRequestStatusV1(String notificationRequestId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NewNotificationRequestStatusResponse getNotificationRequestStatusAllParamV1(String notificationRequestId, String paProtocolNumber, String idempotenceToken) {
        throw new UnsupportedOperationException();
    }

    /**
     * V2
     */
    @Override
    public NewNotificationResponse sendNewNotificationV2(NewNotificationRequest newNotificationRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NewNotificationRequestStatusResponse getNotificationRequestStatusV2(String notificationRequestId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NewNotificationRequestStatusResponse getNotificationRequestStatusAllParamV2(String notificationRequestId, String paProtocolNumber, String idempotenceToken) {
        throw new UnsupportedOperationException();
    }

    /**
     * V21
     */
    @Override
    public NewNotificationResponse sendNewNotificationV21(NewNotificationRequestV21 newNotificationRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NewNotificationRequestStatusResponseV21 getNotificationRequestStatusV21(String notificationRequestId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NewNotificationRequestStatusResponseV21 getNotificationRequestStatusAllParamV21(String notificationRequestId, String paProtocolNumber, String idempotenceToken) {
        throw new UnsupportedOperationException();
    }

    /**
     * V23
     */
    @Override
    public NewNotificationResponse sendNewNotificationV23(NewNotificationRequestV23 newNotificationRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NewNotificationRequestStatusResponseV23 getNotificationRequestStatusV23(String notificationRequestId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NewNotificationRequestStatusResponseV23 getNotificationRequestStatusAllParamV23(String notificationRequestId, String paProtocolNumber, String idempotenceToken) {
        throw new UnsupportedOperationException();
    }

    /**
     * V24
     */
    @Override
    public NewNotificationResponse sendNewNotificationV24(NewNotificationRequestV24 newNotificationRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NewNotificationRequestStatusResponseV24 getNotificationRequestStatusV24(String notificationRequestId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NewNotificationRequestStatusResponseV24 getNotificationRequestStatusAllParamV24(String notificationRequestId, String paProtocolNumber, String idempotenceToken) {
        throw new UnsupportedOperationException();
    }

    /**
     * V25
     */
    public NewNotificationResponse sendNewNotificationV25(NewNotificationRequestV25 newNotificationRequest) {
        throw new UnsupportedOperationException();

    }

    @Override
    public NewNotificationRequestStatusResponseV25 getNotificationRequestStatusV25(String notificationRequestId) {
        throw new UnsupportedOperationException();

    }

    @Override
    public NewNotificationRequestStatusResponseV25 getNotificationRequestStatusAllParamV25(String notificationRequestId, String protocolNumber, String idempotenceToken) {
        throw new UnsupportedOperationException();
    }

    /**
    * V26
    */
    @Override
    public NewNotificationResponse sendNewNotificationV26(NewNotificationRequestV26 newNotificationRequest) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.model.NewNotificationRequestV26 request;
        request = deepCopy(newNotificationRequest, it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.model.NewNotificationRequestV26.class);
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.model.NewNotificationResponse response;
        response = newNotificationApi.sendNewNotificationV26(operatorId, CxTypeAuthFleet.PA, paId, "B2B", request, groups, null, null);
        return deepCopy(response, NewNotificationResponse.class);    }

    @Override
    public NewNotificationRequestStatusResponseV26 getNotificationRequestStatusV26(String notificationRequestId) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.model.NewNotificationRequestStatusResponseV26 resp;
        resp = senderReadB2BApi.getNotificationRequestStatusV26(
                operatorId,
                CxTypeAuthFleet.PA,
                paId,
                groups,
                notificationRequestId,
                null,
                null
        );
        return deepCopy(resp, NewNotificationRequestStatusResponseV26.class);
    }

    @Override
    public NewNotificationRequestStatusResponseV26 getNotificationRequestStatusAllParamV26(String notificationRequestId, String protocolNumber, String idempotenceToken) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.model.NewNotificationRequestStatusResponseV26 resp;
        resp = senderReadB2BApi.getNotificationRequestStatusV26(
                operatorId,
                CxTypeAuthFleet.PA,
                paId,
                groups,
                notificationRequestId,
                protocolNumber,
                idempotenceToken
        );
        return deepCopy(resp, NewNotificationRequestStatusResponseV26.class);
    }

    /**
     * FullSentNotifications
     */
    @Override
    public FullSentNotification getSentNotificationV1(String iun) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FullSentNotificationV20 getSentNotificationV2(String iun) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FullSentNotificationV21 getSentNotificationV21(String iun) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FullSentNotificationV23 getSentNotificationV23(String iun) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FullSentNotificationV24 getSentNotificationV24(String iun) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FullSentNotificationV25 getSentNotificationV25(String iun) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FullSentNotificationV26 getSentNotificationV26(String iun) {
        throw new UnsupportedOperationException();
    }

    public FullSentNotificationV27 getSentNotificationV27(String iun) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FullSentNotificationV28 getSentNotificationV28(String iun) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FullSentNotificationV29 getSentNotificationV29(String iun) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.model.FullSentNotificationV29 resp;
        resp = senderReadB2BApi.getSentNotificationV29(operatorId, CxTypeAuthFleet.PA, paId, iun, groups);
        return deepCopy(resp, FullSentNotificationV29.class);
    }


    @Override
    public NotificationAttachmentDownloadMetadataResponse getSentNotificationAttachment(String iun, Integer recipientIdx, String attachmentName, Integer attachmentIdx) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.model.NotificationAttachmentDownloadMetadataResponse response =
                senderReadB2BApi.getSentNotificationAttachment(
                        operatorId
                        , CxTypeAuthFleet.PA
                        , paId
                        , iun
                        , recipientIdx
                        , attachmentName
                        , groups,
                        attachmentIdx);
        return deepCopy(response, NotificationAttachmentDownloadMetadataResponse.class);
    }


    @Override
    public LegalFactDownloadMetadataResponse getLegalFact(String iun, LegalFactCategory legalFactType, String legalFactId) {
        return null;
    }

    @Override
    public LegalFactDownloadMetadataResponse getDownloadLegalFact(String iun, String legalFactId) {
        return null;
    }


    @Override
    public NotificationPriceResponse getNotificationPrice(String paTaxId, String noticeCode) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public NotificationPriceResponseV23 getNotificationPriceV23(String paTaxId, String noticeCode) throws RestClientException {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.model.NotificationPriceResponseV23
                notificationPrice = this.notificationPriceV23Api.getNotificationPriceV23(paTaxId, noticeCode);
        return deepCopy(notificationPrice, NotificationPriceResponseV23.class);
    }

    @Override
    public NotificationProcessCostResponse getNotificationProcessCost(String iun, Integer recipientIndex, String notificationFeePolicy, Boolean applyCost, Integer paFee, Integer vat) throws RestClientException {
        throw new UnsupportedOperationException();
    }


    @Override
    public void paymentEventsRequestPagoPa(PaymentEventsRequestPagoPa paymentEventsRequestPagoPa) throws RestClientException, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }


    @Override
    public void paymentEventsRequestF24(PaymentEventsRequestF24 paymentEventsRequestF24) throws RestClientException, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public RequestStatus notificationCancellation(String iun) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setApiKeys(ApiKeyType apiKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setApiKey(String apiKey) {
        throw new UnsupportedOperationException();
    }

    public ApiKeyType getApiKeySetted() {
        throw new UnsupportedOperationException();
    }

    public List<PreLoadResponse> presignedUploadRequest(List<PreLoadRequest> preLoadRequest) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.model.PreLoadRequest[] requests;
        requests = deepCopy(preLoadRequest, it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.model.PreLoadRequest[].class);

        List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.model.PreLoadResponse> responses;
        responses = newNotificationApi.presignedUploadRequest(
                operatorId,
                CxTypeAuthFleet.PA,
                paId,
                Arrays.asList(requests));

        PreLoadResponse[] result = deepCopy(responses, PreLoadResponse[].class);
        return Arrays.asList(result);
    }

    public NotificationProcessCostResponse getNotificationProcessCost(String iun, Integer recipientIndex, it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v24.NotificationFeePolicy notificationFeePolicy, Boolean applyCost, Integer paFee, Integer vat) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public LegalFactDownloadMetadataWithContentTypeResponse getLegalFactByIdPrivate(String recipientInternalId, String iun, String legalFactId, String mandateId, it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v26.CxTypeAuthFleet xPagopaPnCxType, List<String> xPagopaPnCxGroups) throws RestClientException {
        throw new UnsupportedOperationException();
    }

}
