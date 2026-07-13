package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.FullNotificationSearchResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.LegalNotificationSearchResponse;
import it.pagopa.pn.client.b2b.pa.domain.Destinatario;
import it.pagopa.pn.client.b2b.pa.domain.NotificationSearchParam;

/**
 * Le due ricerche notifiche disponibili sia sull'API B2B di destinatari strutturati sia
 * sull'API internal. L'istanza attiva viene scelta da {@link B2BRecipientExternalClientImpl}
 * in base al BearerTokenType corrente.
 */
interface RecipientSearchDelegate {
    LegalNotificationSearchResponse searchReceivedDelegatedNotification(Destinatario destinatario, NotificationSearchParam param);

    FullNotificationSearchResponse searchReceivedNotification(Destinatario destinatario, NotificationSearchParam param);
}
