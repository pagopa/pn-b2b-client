package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.Then;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;

import java.util.stream.IntStream;

import static it.pagopa.pn.cucumber.utils.NotificationValue.generateRandomNumber;

public class RaddAltThrottleSteps {
    private final RaddAltSteps raddAltSteps;

    @Autowired
    public RaddAltThrottleSteps(RaddAltSteps raddAltSteps) {
        this.raddAltSteps = raddAltSteps;
    }

    /**
     * actInquiry
     */
    @Then("L'operatore usa lo IUN {string} per recuperare gli atti di {string} un numero di volte superiore al limite definito")
    public void lOperatoreUsoIUNPerRecuperariGliAttiPiuVolteDelLimite(String tipologiaIun, String cf) {
        Assertions.assertThrows(HttpClientErrorException.class,
                () -> lOperatoreUsoIUNPerRecuperariGliAttiPiuVolte(tipologiaIun, cf, 300));
    }

    @Then("L'operatore usa lo IUN {string} per recuperare gli atti di {string} {int} volte")
    public void lOperatoreUsoIUNPerRecuperariGliAttiPiuVolte(String tipologiaIun, String cf, int iteration) {
        raddAltSteps.selectUserRaddAlternative(cf);
        IntStream.range(0, iteration)
                .forEach(x -> {
                    raddAltSteps.lOperatoreUsoIUNPerRecuperariGliAtti(tipologiaIun, cf);
                    waitBetweenCalls();
                });
    }

    /**
     * actTransaction
     */
    @Then("Vengono visualizzati sia gli atti e le attestazioni riferiti alla notifica un numero di volte superiore al limite definito")
    public void vengonoVisualizzatiGliAttiPiuVolteDelLimite() {
        Assertions.assertThrows(HttpClientErrorException.class,
                () -> vengonoVisualizzatiGliAttiPiuVolte(300));
    }

    @Then("Vengono visualizzati sia gli atti e le attestazioni riferiti alla notifica {int} volte")
    public void vengonoVisualizzatiGliAttiPiuVolte(int iteration) {
        IntStream.range(0, iteration)
                .forEach(x -> {
                    raddAltSteps.startTransactionActRaddAlternative(generateRandomNumber(),false);
                    waitBetweenCalls();
                });
    }

    /**
     * aorInquiry
     */
    @Then("Viene visualizzata la presenza di notifiche un numero di volte superiore al limite definito")
    public void vieneVisualizzataLaPresenzaDiNotifichePiuVolteDelLimite() {
        Assertions.assertThrows(HttpClientErrorException.class,
                () -> vieneVisualizzataLaPresenzaDiNotifichePiuVolte("Signor casuale", 300, "UPLOADER"));
    }

    @Then("Viene visualizzata la presenza di notifiche per la persona fisica {string} {int} volte dal operatore radd {string}")
    public void vieneVisualizzataLaPresenzaDiNotifichePiuVolte(String citizen, int iteration, String raddOperatorType) {
        IntStream.range(0, iteration)
                .forEach(x -> {
                    raddAltSteps.laPersonaFisicaChiedeDiVerificareAdOperatoreRaddLaPresenzaDiNotifiche(citizen, raddOperatorType);
                    waitBetweenCalls();
                });
    }

    /**
     * aorTransaction
     */
    @Then("Si recuperano gli atti su radd alternative per un numero di volte superiore al limite definito")
    public void vengonoRecuperatiGliAttiPiuVolteDelLimite() {
        Assertions.assertThrows(HttpClientErrorException.class,
                () -> vengonoRecuperatiGliAttiPiuVolte(300, "UPLOADER"));
    }

    @Then("Si recuperano gli atti {int} volte su radd alternative da operatore radd {string}")
    public void vengonoRecuperatiGliAttiPiuVolte(int iteration, String raddOperatorType) {
        IntStream.range(0, iteration)
                .forEach(x -> {
                    raddAltSteps.vengonoRecuperatiGliAttiDelleNotificheInStatoIrreperibileDaOperatoreRaddType(raddOperatorType);
                    waitBetweenCalls();
                });
    }

    private void waitBetweenCalls() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
