package it.pagopa.pn.interop.cucumber.steps;

import io.cucumber.java.en.And;
import it.pagopa.pn.interop.cucumber.utility.delay_service.DelayService;
import lombok.AllArgsConstructor;

/* Racchiude steps non associabili a nessuna entità specifica. */
@AllArgsConstructor
public class SharedSteps {
    private final DelayService delayService;

    @And("si attendono {int} secondi")
    public void waitSeconds(int seconds) {
        delayService.delayForSeconds(seconds);
    }
}
