package it.pagopa.pn.client.b2b.pa.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.ApiClient;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.api.*;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.model.CxTypeAuthFleet;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaldeliveryPushb2bpa.api.LegalFactsApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaldeliveryPushb2bpa.api.NotificationCancellationApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component()
@ConditionalOnProperty( name = IPnPaB2bClient.IMPLEMENTATION_TYPE_PROPERTY, havingValue = "internal")
public class PnPaB2bInternalClientImpl implements IPnPaB2bClient {

    private final NewNotificationApi newNotificationApi;
    private final SenderReadB2BApi senderReadB2BApi;
    private final NotificationPriceApi notificationPriceApi;
    private final it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.api_v1.SenderReadB2BApi senderReadB2BApiV1;


    private final String paId;
    private final String operatorId;

    private final ObjectMapper objMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();
    private final List<String> groups;

    public PnPaB2bInternalClientImpl(
            RestTemplate restTemplate,
            @Value("${pn.internal.delivery-base-url}") String deliveryBasePath,
            @Value("${pn.internal.delivery-push-base-url}") String deliveryPushBasePath,
            @Value("${pn.internal.pa-id}") String paId
    ) {
        this.paId = paId;
        this.operatorId = "TestMv";
        this.groups = Collections.emptyList();

        this.newNotificationApi = new NewNotificationApi( newApiClient( restTemplate, deliveryBasePath) );
        this.senderReadB2BApi = new SenderReadB2BApi( newApiClient( restTemplate, deliveryBasePath) );
        this.senderReadB2BApiV1 = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.api_v1.SenderReadB2BApi( newApiClient( restTemplate, deliveryBasePath) );
        this.notificationPriceApi = new NotificationPriceApi(newApiClient(restTemplate, deliveryPushBasePath));
    }

    private static it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaldeliveryPushb2bpa.ApiClient
    newApiClient(RestTemplate restTemplate, String basePath, Boolean isDeliveryPushApi ) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaldeliveryPushb2bpa.ApiClient newApiClient = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaldeliveryPushb2bpa.ApiClient(restTemplate);
        newApiClient.setBasePath( basePath );
        return newApiClient;
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath ) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath( basePath );
        return newApiClient;
    }


    public NotificationAttachmentDownloadMetadataResponse getSentNotificationDocument(String iun, Integer docidx) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.model.NotificationAttachmentDownloadMetadataResponse response =
                senderReadB2BApi.getSentNotificationDocument(
                    operatorId
                    , CxTypeAuthFleet.PA
                    , paId
                    , iun
                    , docidx
                    , groups);

        return deepCopy( response, NotificationAttachmentDownloadMetadataResponse.class );
    }

    @Override
    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationAttachmentDownloadMetadataResponse getSentNotificationDocumentV1(String iun, Integer docidx) {
        return null;
    }

    public NotificationAttachmentDownloadMetadataResponse getSentNotificationAttachment(String iun, Integer recipientIdx, String attachname) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.model.NotificationAttachmentDownloadMetadataResponse response =
                senderReadB2BApi.getSentNotificationAttachment(
                    operatorId
                    , CxTypeAuthFleet.PA
                    , paId
                    , iun
                    , recipientIdx
                    , attachname
                    , groups);

        return deepCopy( response, NotificationAttachmentDownloadMetadataResponse.class );
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
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.model.NotificationPriceResponse
                notificationPrice = this.notificationPriceApi.getNotificationPrice(paTaxId,noticeCode);

        return deepCopy( notificationPrice, NotificationPriceResponse.class );
    }

    @Override
    public void paymentEventsRequestPagoPa(PaymentEventsRequestPagoPa paymentEventsRequestPagoPa) throws RestClientException,UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void paymentEventsRequestPagoPaV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.PaymentEventsRequestPagoPa paymentEventsRequestPagoPa) throws RestClientException {

    }

    @Override
    public void paymentEventsRequestF24(PaymentEventsRequestF24 paymentEventsRequestF24) throws RestClientException,UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public RequestStatus notificationCancellation(String iun) throws RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationAttachmentDownloadMetadataResponse getSentNotificationAttachmentV1(String iun, Integer recipientIdx, String attachmentName) {
        return null;
    }

    @Override
    public boolean setApiKeys(ApiKeyType apiKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setApiKey(String apiKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ApiKeyType getApiKeySetted() {
        throw new UnsupportedOperationException();
    }

    public List<PreLoadResponse> presignedUploadRequest(List<PreLoadRequest> preLoadRequest) {

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.model.PreLoadRequest[] requests;
        requests = deepCopy( preLoadRequest, it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.model.PreLoadRequest[].class);

        List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.model.PreLoadResponse> responses;
        responses =newNotificationApi.presignedUploadRequest(
                 operatorId,
                 CxTypeAuthFleet.PA,
                 paId,
                 Arrays.asList( requests ));

        PreLoadResponse[] result = deepCopy( responses, PreLoadResponse[].class );
        return Arrays.asList( result );
    }

    public NewNotificationResponse sendNewNotification(NewNotificationRequest newNotificationRequest) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.model.NewNotificationRequest request;
        request = deepCopy( newNotificationRequest, it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.model.NewNotificationRequest.class );

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.model.NewNotificationResponse response;

        response = newNotificationApi.sendNewNotification(operatorId, CxTypeAuthFleet.PA, paId, "B2B", request, groups,null);

        return deepCopy( response, NewNotificationResponse.class );
    }

    @Override
    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationResponse sendNewNotificationV1(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationRequest newNotificationRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FullSentNotificationV20 getSentNotification(String iun) {

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.model.FullSentNotificationV20 resp;
        resp = senderReadB2BApi.getSentNotificationV20( operatorId, CxTypeAuthFleet.PA, paId, iun, groups );
        return deepCopy( resp, FullSentNotificationV20.class );
    }

    @Override
    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.FullSentNotification getSentNotificationV1(String iun) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NewNotificationRequestStatusResponse getNotificationRequestStatus(String notificationRequestId) {

        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.model.NewNotificationRequestStatusResponse resp;
        resp = senderReadB2BApi.getNotificationRequestStatus(
                operatorId,
                CxTypeAuthFleet.PA,
                paId,
                groups,
                notificationRequestId,
                null,
                null
            );
        return deepCopy( resp, NewNotificationRequestStatusResponse.class );
    }

    @Override
    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationRequestStatusResponse getNotificationRequestStatusV1(String notificationRequestId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NewNotificationRequestStatusResponse getNotificationRequestStatusAllParam(String notificationRequestId, String paProtocolNumber, String idempotenceToken) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpa.model.NewNotificationRequestStatusResponse resp;
        resp = senderReadB2BApi.getNotificationRequestStatus(
                operatorId,
                CxTypeAuthFleet.PA,
                paId,
                groups,
                notificationRequestId,
                paProtocolNumber,
                idempotenceToken
        );
        return deepCopy( resp, NewNotificationRequestStatusResponse.class );
    }

    private <T> T deepCopy( Object obj, Class<T> toClass) {
        try {
            String json = objMapper.writeValueAsString( obj );
            return objMapper.readValue( json, toClass );
        } catch (JsonProcessingException exc ) {
            throw new RuntimeException( exc );
        }
    }

}
