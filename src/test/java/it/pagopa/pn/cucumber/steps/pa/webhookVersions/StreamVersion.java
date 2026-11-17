package it.pagopa.pn.cucumber.steps.pa.webhookVersions;

import it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheWebhookB2bSteps;
import lombok.Getter;

public enum StreamVersion {

    V10(10), V23(23), V24(24), V25(25), V26(26), V27(27), V28(28), V29(29), V30(30);

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

    public static WebhookStepsInterface createWebhookStep(StreamVersion version, AvanzamentoNotificheWebhookB2bSteps webhookB2bSteps) {
        return switch (version) {
            case V10 -> new WebhookStepsV10(webhookB2bSteps);
            case V23 -> new WebhookStepsV23(webhookB2bSteps);
            case V24 -> new WebhookStepsV24(webhookB2bSteps);
            case V25 -> new WebhookStepsV25(webhookB2bSteps);
            case V26 -> new WebhookStepsV26(webhookB2bSteps);
            case V27 -> new WebhookStepsV27(webhookB2bSteps);
            case V28 -> new WebhookStepsV28(webhookB2bSteps);
            case V29 -> new WebhookStepsV29(webhookB2bSteps);
            case V30 -> new WebhookStepsV30(webhookB2bSteps);
        };
    }
}
