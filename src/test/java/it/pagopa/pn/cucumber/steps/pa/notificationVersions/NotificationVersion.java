package it.pagopa.pn.cucumber.steps.pa.notificationVersions;

import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheB2bSteps;
import it.pagopa.pn.cucumber.steps.pa.b2bVersions.*;
import lombok.Getter;

import java.util.Map;

public enum NotificationVersion {
    V1(1), V2(2), V21(21), V23(23), V24(24), V25(25);

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

    public static Map<NotificationVersion, NotificationStepsInterface> getMapOfNotificationSteps(SharedSteps sharedSteps) {
        return Map.of(
                V1, new NotificationStepsV1(sharedSteps),
                V2, new NotificationStepsV2(sharedSteps),
                V21, new NotificationStepsV21(sharedSteps),
                V23, new NotificationStepsV23(sharedSteps),
                V24, new NotificationStepsV24(sharedSteps)
        );
    }

    public static Map<NotificationVersion, B2bStepsInterface> getMapOfB2bSteps(AvanzamentoNotificheB2bSteps b2bSteps) {
        return Map.of(
                V1, new B2bStepsV1(b2bSteps),
                V2, new B2bStepsV2(b2bSteps),
                V21, new B2bStepsV21(b2bSteps),
                V23, new B2bStepsV23(b2bSteps),
                V24, new B2bStepsV24(b2bSteps)
        );
    }
}
