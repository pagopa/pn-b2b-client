package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalprivate.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v27.CxTypeAuthFleet;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v27.LegalFactDownloadMetadataWithContentTypeResponse;

import java.util.List;


public interface IPnPaB2bPrivateClient extends SettableApiKey {
    //delivery-push-private todo t frontespizio
    LegalFactDownloadMetadataWithContentTypeResponse getLegalFactByIdPrivate(String recipientInternalId, String iun, String legalFactId, String mandateId, CxTypeAuthFleet xPagopaPnCxType, List<String> xPagopaPnCxGroups);
    //delivery-private todo t frontespizio
    NotificationAttachmentDownloadMetadataResponse getReceivedNotificationDocumentPrivate(String iun, Integer docIdx, String recipientInternalId, String mandateId);
  }