package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.api.RecipientReadB2BApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.FullNotificationSearchResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.LegalNotificationSearchResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.NotificationStatusV26;
import it.pagopa.pn.client.b2b.pa.domain.Destinatario;
import it.pagopa.pn.client.b2b.pa.domain.NotificationSearchParam;

import java.util.Optional;

/** Ricerca notifiche sull'API B2B di destinatari strutturati (recipientReadB2BApi). */
class B2BExternalRecipientSearchDelegate implements RecipientSearchDelegate {
    private final RecipientReadB2BApi recipientReadB2BApi;

    B2BExternalRecipientSearchDelegate(RecipientReadB2BApi recipientReadB2BApi) {
        this.recipientReadB2BApi = recipientReadB2BApi;
    }

    @Override
    public LegalNotificationSearchResponse searchReceivedDelegatedNotification(Destinatario destinatario, NotificationSearchParam param) {
        return recipientReadB2BApi.searchReceivedDelegatedNotification(
                param.startDate.toString(), param.endDate.toString(), param.senderId, param.recipientId,
                param.group, param.iunMatch, convertStatus(NotificationStatusV26.fromValue(param.status)), param.size, param.nextPagesKey);
    }

    @Override
    public FullNotificationSearchResponse searchReceivedNotification(Destinatario destinatario, NotificationSearchParam param) {
        return recipientReadB2BApi.searchReceivedNotification(param.startDate.toString(), param.endDate.toString(), param.mandateId,
                param.senderId, param.subjectRegExp, param.iunMatch, param.size, param.nextPagesKey, param.communicationType);
    }

    private static it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.NotificationStatusV26 convertStatus(NotificationStatusV26 status) {
        return Optional.ofNullable(status)
                .map(NotificationStatusV26::getValue)
                .map(it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.NotificationStatusV26::fromValue)
                .orElse(null);
    }
}
