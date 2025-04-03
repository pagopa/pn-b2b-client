package it.pagopa.pn.cucumber.steps.pa.webhookVersions;

import it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheWebhookB2bSteps;
import lombok.Getter;

import java.util.Map;

public enum StreamVersion {

    V10(10), V23(23), V24(24), V25(25), V26(26), V27(27), V28(28);

    /**
     * Scopo di questo campo è quello di poter comparare le versioni con < o >
     * In questo modo si possono aggiungere controlli nel codice per verificare
     * se un dato Stream Version è antecedente o successivo a un'altra versione
     */
    @Getter
    private final int value;

    StreamVersion(int value) {
        this.value = value;
    }

    public static Map<StreamVersion, WebhookStepsInterface> getMapOfWebhookSteps(AvanzamentoNotificheWebhookB2bSteps webhookB2bSteps) {
        return Map.of(
                V10, new WebhookStepsV10(webhookB2bSteps),
                V23, new WebhookStepsV23(webhookB2bSteps),
                V24, new WebhookStepsV24(webhookB2bSteps),
                V25, new WebhookStepsV25(webhookB2bSteps),
                V26, new WebhookStepsV26(webhookB2bSteps),
                V27, new WebhookStepsV27(webhookB2bSteps)
        );
    }
}
