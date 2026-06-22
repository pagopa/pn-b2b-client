package it.pagopa.pn.cucumber.steps.pa.notificationVersions;

import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheB2bSteps;
import it.pagopa.pn.cucumber.steps.pa.b2bVersions.*;
import lombok.Getter;

public enum NotificationVersion {
    V1(1), V2(2), V21(21), V23(23), V24(24), V25(25), V26(26);

    /**
     * Scopo di questo campo è quello di poter comparare le versioni con < o >
     * In questo modo si possono aggiungere controlli nel codice per verificare
     * se una specifica Notification Version è antecedente o successivo a un'altra versione
     */
    @Getter
    private final int value;

    NotificationVersion(int value) {
        this.value = value;
    }

    public static NotificationStepsInterface createNotificationStep(NotificationVersion version, SharedSteps sharedSteps) {
        return switch (version) {
            case V1 -> new NotificationStepsV1(sharedSteps);
            case V2 -> new NotificationStepsV2(sharedSteps);
            case V21 -> new NotificationStepsV21(sharedSteps);
            case V23 -> new NotificationStepsV23(sharedSteps);
            case V24 -> new NotificationStepsV24(sharedSteps);
            case V25 -> new NotificationStepsV25(sharedSteps);
            case V26 -> new NotificationStepsV26(sharedSteps);
        };
    }

    public static B2bStepsInterface createB2bStep(NotificationVersion version, AvanzamentoNotificheB2bSteps b2bSteps) {
        return switch (version) {
            case V1 -> new B2bStepsV1(b2bSteps);
            case V2 -> new B2bStepsV2(b2bSteps);
            case V21 -> new B2bStepsV21(b2bSteps);
            case V23 -> new B2bStepsV23(b2bSteps);
            case V24 -> new B2bStepsV24(b2bSteps);
            case V25 -> new B2bStepsV25(b2bSteps);
            case V26 -> new B2bStepsV26(b2bSteps);
        };
    }
}
