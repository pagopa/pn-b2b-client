package it.pagopa.pn.cucumber.steps.pa.notificationVersions;

import lombok.Getter;

public enum NotificationVersion {
    V1(1), V2(2), V21(21), V23(23), V24(24);

    /**
     * Scopo di questo campo è quello di poter comparare le versioni con < o >
     * In questo modo si possono aggiungere controlli nel codice per verificare
     * se un dato Notification Version è antecedente o successivo a un'altra versione
     */
    @Getter
    private final int value;

    NotificationVersion(int value) {
        this.value = value;
    }
}
