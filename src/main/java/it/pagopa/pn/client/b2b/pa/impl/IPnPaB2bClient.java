package it.pagopa.pn.client.b2b.pa.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;

import java.util.List;

public interface IPnPaB2bClient {

    String IMPLEMENTATION_TYPE_PROPERTY = "pn.api-type";

    List<PreLoadResponse> presignedUploadRequest(List<PreLoadRequest> preLoadRequest);

    NewNotificationResponse sendNewNotification(NewNotificationRequest newNotificationRequest);

    FullSentNotification getSentNotification(String iun);

    NewNotificationRequestStatusResponse getNotificationRequestStatus(String notificationRequestId);
}
