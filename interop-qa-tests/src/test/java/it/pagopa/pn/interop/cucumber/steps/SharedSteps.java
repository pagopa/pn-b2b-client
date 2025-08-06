package it.pagopa.pn.interop.cucumber.steps;

import io.cucumber.java.en.And;

/* Racchiude steps non associabili a nessuna entità specifica. */
public class SharedSteps {
    @And("si attendono {int} secondi")
    public void waitSeconds(int seconds) throws InterruptedException {
        Thread.sleep(seconds * 1000);
    }
}
