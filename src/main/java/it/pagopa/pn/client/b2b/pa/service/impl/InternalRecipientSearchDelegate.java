package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.FullNotificationSearchResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.LegalNotificationSearchResponse;
import it.pagopa.pn.client.b2b.pa.domain.Destinatario;
import it.pagopa.pn.client.b2b.pa.domain.NotificationSearchParam;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.api.RecipientReadApi;
import it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.CxTypeAuthFleet;

import java.util.Optional;

import static it.pagopa.pn.client.b2b.pa.utils.JsonDeepCopyMapper.deepCopy;

/** Ricerca notifiche sull'API internal (recipientReadApi), risolvendo gli header cx-* dal Destinatario. */
class InternalRecipientSearchDelegate implements RecipientSearchDelegate {
    private final RecipientReadApi recipientReadApi;

    InternalRecipientSearchDelegate(RecipientReadApi recipientReadApi) {
        this.recipientReadApi = recipientReadApi;
    }

    @Override
    public LegalNotificationSearchResponse searchReceivedDelegatedNotification(Destinatario destinatario, NotificationSearchParam param) {
        String cxType = resolveActual(param.xPagopaPnCxType, destinatario.getRecipientType());
        String cxId = resolveActual(param.xPagopaPnCxId, String.format("%s-%s", destinatario.getRecipientType(), destinatario.getUid()));
        it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.NotificationStatusV26 statusV26 = Optional.ofNullable(param.status)
                .map(it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.NotificationStatusV26::fromValue)
                .orElse(null);
        it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.LegalNotificationSearchResponse response = recipientReadApi.searchReceivedDelegatedNotification(
                param.xPagopaPnUid, CxTypeAuthFleet.fromValue(cxType), cxId,
                param.startDate, param.endDate, param.xPagopaPnCxGroups, param.senderId, param.recipientId,
                param.group, param.iunMatch, statusV26, param.size, param.nextPagesKey);
        return deepCopy(response, LegalNotificationSearchResponse.class);
    }

    @Override
    public FullNotificationSearchResponse searchReceivedNotification(Destinatario destinatario, NotificationSearchParam param) {
        String cxType = resolveActual(param.xPagopaPnCxType, destinatario.getRecipientType());
        String cxId = resolveActual(param.xPagopaPnCxId, String.format("%s-%s", destinatario.getRecipientType(), destinatario.getUid()));
        it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.FullNotificationSearchResponse response = recipientReadApi.searchReceivedNotification(
                param.xPagopaPnUid, CxTypeAuthFleet.fromValue(cxType), cxId,
                param.startDate, param.endDate, param.xPagopaPnCxGroups, param.mandateId,
                param.senderId, param.subjectRegExp, param.iunMatch, param.size, param.nextPagesKey, param.communicationType);
        return deepCopy(response, FullNotificationSearchResponse.class);
    }

    // NotificationSearchParam.ACTUAL (default quando il campo non è specificato in tabella) -> valore derivato dal destinatario;
    // qualunque altro valore, incluso null esplicito (per simulare un campo obbligatorio mancante), passa invariato
    private static String resolveActual(String value, String actualValue) {
        return NotificationSearchParam.ACTUAL.equals(value) ? actualValue : value;
    }
}
