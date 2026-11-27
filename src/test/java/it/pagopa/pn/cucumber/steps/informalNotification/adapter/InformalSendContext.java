package it.pagopa.pn.cucumber.steps.informalNotification.adapter;

import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import lombok.Builder;
import lombok.Value;

// Credenziali con cui l'adapter invia la comunicazione: external usa apiKeyType, cxId resta per log/eventuali client internal
@Value
@Builder
public class InformalSendContext {
    String cxId;
    SettableApiKey.ApiKeyType apiKeyType;
}
