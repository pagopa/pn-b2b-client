package it.pagopa.pn.client.b2b.pa.service;

import org.springframework.web.client.RestClientException;

import java.time.OffsetDateTime;
import java.util.List;


public interface IPnPrivateDeliveryPushExternalClient {

    /**
     * V1
     */
    it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v1.NotificationHistoryResponse getNotificationHistoryV1(String iun, Integer numberOfRecipients, OffsetDateTime createdAt) throws RestClientException;

    /**
     * V2
     */
    it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v2.NotificationHistoryResponse getNotificationHistoryV2(String iun, Integer numberOfRecipients, OffsetDateTime createdAt) throws RestClientException;

    /**
     * V21
     */
    it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v21.NotificationHistoryResponse getNotificationHistoryV21(String iun, Integer numberOfRecipients, OffsetDateTime createdAt) throws RestClientException;

    /**
     * V23
     */
    it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v23.NotificationHistoryResponse getNotificationHistoryV23(String iun, Integer numberOfRecipients, OffsetDateTime createdAt) throws RestClientException;

    /**
     * V24
     */
    it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v24.NotificationHistoryResponse getNotificationHistoryV24(String iun, Integer numberOfRecipients, OffsetDateTime createdAt) throws RestClientException;

    List<it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v24.ResponsePaperNotificationFailedDto> getPaperNotificationFailed(String recipientInternalId, Boolean getAAR) throws RestClientException;
}