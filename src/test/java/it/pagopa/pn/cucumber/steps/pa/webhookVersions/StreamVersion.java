package it.pagopa.pn.cucumber.steps.pa.webhookVersions;

import lombok.Getter;

public enum StreamVersion {

    V10(10), V23(23), V24(24), V25(25), V26(26), V27(27);

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
}
