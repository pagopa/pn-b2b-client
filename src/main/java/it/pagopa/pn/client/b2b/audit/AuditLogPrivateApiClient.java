package it.pagopa.pn.client.b2b.audit;

import it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDelivery.api.InternalOnlyApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDelivery.model.LegalNotificationSearchResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDelivery.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDelivery.model.RequestCheckAarDto;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDelivery.model.ResponseCheckAarDto;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.api_v26.EventComunicationApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.api_v26.LegalFactsPrivateApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v26.LegalFactDownloadMetadataWithContentTypeResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v26.LegalFactListElementV20;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v26.RecipientType;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v26.RequestNotificationViewedDto;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v26.ResponseNotificationViewedDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.List;

import static it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton.INTEROP_ENABLED;

/**
 * Client ad-hoc, usato esclusivamente dagli step di verifica degli audit log di QA-19752, per le 7 API private di
 * pn-delivery e pn-delivery-push che accettano gli header opzionali {@code x-pagopa-pn-uid}/{@code x-pagopa-pn-cx-id}/
 * {@code x-pagopa-pn-cx-type}. Non va confuso con {@code PnPaB2bInternalInformalClientImpl}/{@code PnPaB2bExternalClientImpl},
 * che restano gli unici client usati dal resto della suite: tenerlo separato evita di appesantire quei client,
 * pensati per flussi di business, con API nate solo per testare la propagazione di questi header negli audit log.
 */
@Component
public class AuditLogPrivateApiClient {

    private final InternalOnlyApi internalOnlyApi;
    private final LegalFactsPrivateApi legalFactsPrivateApi;
    private final EventComunicationApi eventComunicationApi;

    public AuditLogPrivateApiClient(RestTemplate restTemplate,
                                    InteropTokenSingleton interopTokenSingleton,
                                    @Value("${pn.delivery.base-url}") String deliveryBaseUrl,
                                    @Value("${pn.internal.delivery-push-base-url}") String deliveryPushBaseUrl,
                                    @Value("${pn.external.api-key}") String apiKey,
                                    @Value("${pn.interop.enable}") String enableInterop) {
        this.internalOnlyApi = new InternalOnlyApi(newDeliveryApiClient(restTemplate, deliveryBaseUrl));
        String bearerTokenInterop = INTEROP_ENABLED.equalsIgnoreCase(enableInterop) ? interopTokenSingleton.getTokenInterop() : null;
        var deliveryPushApiClient = newDeliveryPushApiClient(restTemplate, deliveryPushBaseUrl, apiKey, bearerTokenInterop, enableInterop);
        this.legalFactsPrivateApi = new LegalFactsPrivateApi(deliveryPushApiClient);
        this.eventComunicationApi = new EventComunicationApi(deliveryPushApiClient);
    }

    private static it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDelivery.ApiClient newDeliveryApiClient(RestTemplate restTemplate, String basePath) {
        var apiClient = new it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDelivery.ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        return apiClient;
    }

    private static it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.ApiClient newDeliveryPushApiClient(
            RestTemplate restTemplate, String basePath, String apiKey, String bearerTokenInterop, String enableInterop) {
        var apiClient = new it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.addDefaultHeader("x-api-key", apiKey);
        if (INTEROP_ENABLED.equalsIgnoreCase(enableInterop)) {
            apiClient.addDefaultHeader("Authorization", "Bearer " + bearerTokenInterop);
        }
        return apiClient;
    }

    public LegalNotificationSearchResponse searchNotificationsPrivate(OffsetDateTime startDate, OffsetDateTime endDate, String senderId,
                                                                      Integer size, String uid, String cxId, String cxType) {
        return internalOnlyApi.searchNotificationsPrivate(startDate, endDate, null, null, senderId, null,
                null, null, size, null, uid, cxId, cxTypeAuthFleetDelivery(cxType));
    }

    public ResponseCheckAarDto checkAarQrCode(String recipientType, String recipientInternalId, String aarQrCodeValue,
                                              String uid, String cxId, String cxType) {
        RequestCheckAarDto body = new RequestCheckAarDto()
                .recipientType(recipientType)
                .recipientInternalId(recipientInternalId)
                .aarQrCodeValue(aarQrCodeValue);
        return internalOnlyApi.checkAarQrCode(body, uid, cxId, cxTypeAuthFleetDelivery(cxType));
    }

    public NotificationAttachmentDownloadMetadataResponse getReceivedNotificationDocumentPrivate(String iun, Integer docIdx, String recipientInternalId,
                                                                                                 String uid, String cxId, String cxType) {
        return internalOnlyApi.getReceivedNotificationDocumentPrivate(iun, docIdx, recipientInternalId, null, uid, cxId, cxTypeAuthFleetDelivery(cxType));
    }

    public NotificationAttachmentDownloadMetadataResponse getReceivedNotificationAttachmentPrivate(String iun, String attachmentName, String recipientInternalId,
                                                                                                    String uid, String cxId, String cxType) {
        return internalOnlyApi.getReceivedNotificationAttachmentPrivate(iun, attachmentName, recipientInternalId, null, 0, uid, cxId, cxTypeAuthFleetDelivery(cxType));
    }

    public LegalFactDownloadMetadataWithContentTypeResponse getLegalFactByIdPrivate(String recipientInternalId, String iun, String legalFactId,
                                                                                    String uid, String cxId, String cxType) {
        return legalFactsPrivateApi.getLegalFactByIdPrivate(recipientInternalId, iun, legalFactId, null, cxTypeAuthFleetPush(cxType), null, uid, cxId);
    }

    public List<LegalFactListElementV20> getNotificationLegalFactsPrivate(String recipientInternalId, String iun,
                                                                           String uid, String cxId, String cxType) {
        return legalFactsPrivateApi.getNotificationLegalFactsPrivate(recipientInternalId, iun, null, cxTypeAuthFleetPush(cxType), null, uid, cxId);
    }

    public ResponseNotificationViewedDto notifyNotificationRaddRetrieved(String iun, String recipientType, String recipientInternalId,
                                                                         String raddType, String raddBusinessTransactionId, OffsetDateTime raddBusinessTransactionDate,
                                                                         String uid, String cxId, String cxType) {
        RequestNotificationViewedDto body = new RequestNotificationViewedDto()
                .recipientType(RecipientType.fromValue(recipientType))
                .recipientInternalId(recipientInternalId)
                .raddType(raddType)
                .raddBusinessTransactionId(raddBusinessTransactionId)
                .raddBusinessTransactionDate(raddBusinessTransactionDate);
        return eventComunicationApi.notifyNotificationRaddRetrieved(iun, body, uid, cxId, cxTypeAuthFleetPush(cxType));
    }

    private static it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDelivery.model.CxTypeAuthFleet cxTypeAuthFleetDelivery(String cxType) {
        return cxType == null ? null : it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDelivery.model.CxTypeAuthFleet.fromValue(cxType);
    }

    private static it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v26.CxTypeAuthFleet cxTypeAuthFleetPush(String cxType) {
        return cxType == null ? null : it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v26.CxTypeAuthFleet.fromValue(cxType);
    }
}
