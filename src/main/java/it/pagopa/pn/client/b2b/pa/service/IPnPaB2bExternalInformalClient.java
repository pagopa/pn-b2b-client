package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.model.*;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;

import java.util.List;
import java.util.UUID;

public interface IPnPaB2bExternalInformalClient {

    MessageResponse createMessage(NewMessageRequest request, SettableApiKey.ApiKeyType apiKeyType);

    MessageResponse getMessage(UUID messageId, SettableApiKey.ApiKeyType apiKeyType);

    NewNotificationResponse sendNewInformalNotificationV1(InformalNotificationRequestV1 request, SettableApiKey.ApiKeyType apiKeyType);

    NewInformalNotificationRequestStatusResponseV1 getNotificationStatusByRequestId(String notificationRequestId, SettableApiKey.ApiKeyType apiKeyType);

    NotificationAttachmentDownloadMetadataResponse getSentInformalNotificationDocument(String iun, int docIdx, SettableApiKey.ApiKeyType apiKeyType);

    NotificationAttachmentDownloadMetadataResponse getSentInformalNotificationAttachment(String iun, int recipientIdx, int attachmentIdx, SettableApiKey.ApiKeyType apiKeyType);

    TerminationRequestStatus terminateInformalWorkflow(String iun, SettableApiKey.ApiKeyType apiKeyType);

    FullSentInformalNotificationV1 getSentInformalNotificationSender(String iun, Boolean retrieveMessage, SettableApiKey.ApiKeyType apiKeyType);

    List<InformalPreLoadResponse> informalPresignedUploadRequest(List<InformalPreLoadRequest> requests, SettableApiKey.ApiKeyType apiKeyType);
}