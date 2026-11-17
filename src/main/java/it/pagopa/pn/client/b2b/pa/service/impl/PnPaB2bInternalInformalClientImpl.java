package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.api.SenderReadInformalNotificationB2BApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.CxTypeAuthFleet;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.FullSentInformalNotificationV1;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformalmonitorcampaign.api.CampaignStatisticsApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformalmonitorcampaign.model.CampaignStatisticsResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internawebrecipientinformal.api.RecipientReadInformalNotificationApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internawebrecipientinformal.model.FullReceivedInformalNotificationV1;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDelivery.api.InternalOnlyApi;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDelivery.model.InformalSentNotificationV1;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDelivery.model.LegalNotificationSearchResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDelivery.model.NotificationStatusV26;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class PnPaB2bInternalInformalClientImpl {

    private final String operatorId;
    private final RecipientReadInformalNotificationApi recipientReadInformalNotificationApi;
    private final InternalOnlyApi internalOnlyApi;
    private final CampaignStatisticsApi campaignStatisticsApi;
    private final SenderReadInformalNotificationB2BApi senderReadInformalNotificationB2BApi;
    private final List<String> groups;

    public PnPaB2bInternalInformalClientImpl(RestTemplate restTemplate, @Value("${pn.delivery.base-url}") String deliveryBasePathOrigin) {

        this.operatorId = "TestMv";
        this.recipientReadInformalNotificationApi = new RecipientReadInformalNotificationApi(newRecipientInformalApiClient(restTemplate, deliveryBasePathOrigin));
        this.internalOnlyApi = new InternalOnlyApi(newPrivateDeliveryApiClient(restTemplate, deliveryBasePathOrigin));
        this.campaignStatisticsApi = new CampaignStatisticsApi(newCampaignStatisticsApiClient(restTemplate, deliveryBasePathOrigin));
        this.senderReadInformalNotificationB2BApi = new SenderReadInformalNotificationB2BApi(newInformalApiClient(restTemplate, deliveryBasePathOrigin));
        this.groups = Collections.emptyList();
    }

    private static it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDelivery.ApiClient newPrivateDeliveryApiClient(RestTemplate restTemplate, String basePath) {

        var client = new it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDelivery.ApiClient(restTemplate);
        client.setBasePath(basePath);
        return client;
    }

    private static it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internawebrecipientinformal.ApiClient newRecipientInformalApiClient(RestTemplate restTemplate, String basePath) {

        var client = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internawebrecipientinformal.ApiClient(restTemplate);
        client.setBasePath(basePath);
        return client;
    }

    private static it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformalmonitorcampaign.ApiClient newCampaignStatisticsApiClient(RestTemplate restTemplate, String basePath) {

        var client = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformalmonitorcampaign.ApiClient(restTemplate);
        client.setBasePath(basePath);
        return client;
    }

    private static it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.ApiClient newInformalApiClient(RestTemplate restTemplate, String basePath) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.ApiClient newApiClient = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        return newApiClient;
    }

    public InformalSentNotificationV1 getSentInformalNotification(String iun) {
        return internalOnlyApi.getSentInformalNotificationPrivateV1(iun, null);
    }

    public LegalNotificationSearchResponse searchNotificationsPrivate(OffsetDateTime startDate, OffsetDateTime endDate, String recipientId, Boolean recipientIdOpaque, String senderId, List<NotificationStatusV26> status, String mandateId, String cxType, Integer size, String nextPagesKey) {
        return internalOnlyApi.searchNotificationsPrivate(startDate, endDate, recipientId, recipientIdOpaque, senderId, status, mandateId, cxType, size, nextPagesKey);
    }

    public FullReceivedInformalNotificationV1 getReceivedInformalNotification(String cxId, String iun, it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internawebrecipientinformal.model.CxTypeAuthFleet recipientType) {
        return recipientReadInformalNotificationApi.getReceivedInformalNotificationV1(operatorId, recipientType, cxId, "WEB", iun, null, null, false);
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internawebrecipientinformal.model.NotificationAttachmentDownloadMetadataResponse getReceivedInformalNotificationDocument(String cxId, String iun, it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internawebrecipientinformal.model.CxTypeAuthFleet recipientType, Integer docIdx) {
        return recipientReadInformalNotificationApi.getReceivedInformalNotificationDocumentV1(operatorId, recipientType, cxId, "WEB", iun, docIdx, null, null);
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internawebrecipientinformal.model.NotificationAttachmentDownloadMetadataResponse getReceivedInformalNotificationAttachment(String cxId, String iun, it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internawebrecipientinformal.model.CxTypeAuthFleet recipientType, Integer attachmentIdx) {
        return recipientReadInformalNotificationApi.getReceivedInformalNotificationAttachmentV1(operatorId, recipientType, cxId, "WEB", iun, "PAGOPA", null, null, attachmentIdx);
    }

    public CampaignStatisticsResponse getCampaignStatistics(String cxId, String campaignId) {
        return campaignStatisticsApi.getCampaignStatistics(cxId, campaignId);
    }

    public FullSentInformalNotificationV1 getSentInformalNotificationSender(String cxId, String iun, Boolean retrieveMessage) {
        return senderReadInformalNotificationB2BApi.getSentInformalNotificationV1(operatorId, CxTypeAuthFleet.PA, cxId, iun, groups, retrieveMessage);
    }

    /**
     * Overload di {@link #getSentInformalNotificationSender(String, String, Boolean)} con il tipo di risposta scelto dal
     * chiamante. Con {@code String} il body resta il JSON così come ricevuto: serve a verificare che la risposta non
     * contenga campi che il modello generato scarterebbe in deserializzazione.
     */
    public <T> ResponseEntity<T> getSentInformalNotificationSender(String cxId, String iun, Boolean retrieveMessage,
                                                                   ParameterizedTypeReference<T> responseType) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.ApiClient apiClient = senderReadInformalNotificationB2BApi.getApiClient();
        return apiClient.invokeAPI(
                "/delivery/v1/notifications/sent/{iun}",
                HttpMethod.GET,
                Map.of("iun", iun),
                apiClient.parameterToMultiValueMap(null, "retrieveMessage", retrieveMessage),
                null,
                senderHeaders(apiClient, cxId),
                new LinkedMultiValueMap<>(),
                new LinkedMultiValueMap<>(),
                List.of(MediaType.APPLICATION_JSON),
                null,
                new String[]{},
                responseType);
    }

    /**
     * Stato della richiesta di invio ({@code GET /delivery/v1/requests}) letto lato mittente, con il tipo di risposta
     * scelto dal chiamante ({@code String} per il JSON così come ricevuto).
     */
    public <T> ResponseEntity<T> getInformalNotificationRequestStatus(String cxId, String notificationRequestId,
                                                                      ParameterizedTypeReference<T> responseType) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.ApiClient apiClient = senderReadInformalNotificationB2BApi.getApiClient();
        return apiClient.invokeAPI(
                "/delivery/v1/requests",
                HttpMethod.GET,
                Map.of(),
                apiClient.parameterToMultiValueMap(null, "notificationRequestId", notificationRequestId),
                null,
                senderHeaders(apiClient, cxId),
                new LinkedMultiValueMap<>(),
                new LinkedMultiValueMap<>(),
                List.of(MediaType.APPLICATION_JSON),
                null,
                new String[]{},
                responseType);
    }

    /** Header di autenticazione interna del mittente, gli stessi che passano i metodi tipizzati. */
    private HttpHeaders senderHeaders(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.ApiClient apiClient, String cxId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-pagopa-pn-uid", operatorId);
        headers.add("x-pagopa-pn-cx-type", CxTypeAuthFleet.PA.getValue());
        headers.add("x-pagopa-pn-cx-id", cxId);
        headers.add("x-pagopa-pn-cx-groups", apiClient.parameterToString(groups));
        return headers;
    }
}
