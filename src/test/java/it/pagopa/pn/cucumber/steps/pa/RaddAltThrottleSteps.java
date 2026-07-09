package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.domain.Destinatario;
import it.pagopa.pn.cucumber.steps.utilitySteps.DestinatarioRegistry;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;

import java.util.stream.IntStream;

import static it.pagopa.pn.cucumber.utils.NotificationValue.generateRandomNumber;

public class RaddAltThrottleSteps {
    private final RaddAltSteps raddAltSteps;
    private final DestinatarioRegistry destinatarioRegistry;

    @Autowired
    public RaddAltThrottleSteps(RaddAltSteps raddAltSteps, DestinatarioRegistry destinatarioRegistry) {
        this.raddAltSteps = raddAltSteps;
        this.destinatarioRegistry = destinatarioRegistry;
    }

    /**
     * actInquiry
     */
    @Then("L'operatore usa lo IUN {string} per recuperare gli atti di {destinatario} un numero di volte superiore al limite definito")
    public void lOperatoreUsoIUNPerRecuperareGliAttiPiuVolteDelLimite(String tipologiaIun, Destinatario destinatario) {
        Assertions.assertThrows(HttpClientErrorException.class, () -> lOperatoreUsoIUNPerRecuperareGliAttiPiuVolte(tipologiaIun, destinatario, 300));
    }

    @Then("L'operatore usa lo IUN {string} per recuperare gli atti di {destinatario} {int} volte")
    public void lOperatoreUsoIUNPerRecuperareGliAttiPiuVolte(String tipologiaIun, Destinatario destinatario, int iteration) {
        raddAltSteps.selectUserRaddAlternative(destinatario);
        IntStream.range(0, iteration).forEach(x -> {
            raddAltSteps.lOperatoreUsoIUNPerRecuperareGliAtti(tipologiaIun, destinatario);
            waitBetweenCalls();
        });
    }

    /**
     * actTransaction
     */
    @Then("Vengono visualizzati sia gli atti e le attestazioni riferiti alla notifica un numero di volte superiore al limite definito")
    public void vengonoVisualizzatiGliAttiPiuVolteDelLimite() {
        Assertions.assertThrows(HttpClientErrorException.class, () -> vengonoVisualizzatiGliAttiPiuVolte(300));
    }

    @Then("Vengono visualizzati sia gli atti e le attestazioni riferiti alla notifica {int} volte")
    public void vengonoVisualizzatiGliAttiPiuVolte(int iteration) {
        IntStream.range(0, iteration).forEach(x -> {
            raddAltSteps.startTransactionActRaddAlternative(generateRandomNumber(), false);
            waitBetweenCalls();
        });
    }

    /**
     * aorInquiry
     */
    @Then("Viene visualizzata la presenza di notifiche un numero di volte superiore al limite definito")
    public void vieneVisualizzataLaPresenzaDiNotifichePiuVolteDelLimite() {
        Assertions.assertThrows(HttpClientErrorException.class, () -> vieneVisualizzataLaPresenzaDiNotifichePiuVolte(destinatarioRegistry.DESTINATARIO_SIGNOR_CASUALE, 300, "UPLOADER"));
    }

    @Then("Viene visualizzata la presenza di notifiche per la persona fisica {destinatario} {int} volte dal operatore radd {string}")
    public void vieneVisualizzataLaPresenzaDiNotifichePiuVolte(Destinatario destinatario, int iteration, String raddOperatorType) {
        IntStream.range(0, iteration).forEach(x -> {
            raddAltSteps.laPersonaFisicaChiedeDiVerificareAdOperatoreRaddLaPresenzaDiNotifiche(destinatario, raddOperatorType);
            waitBetweenCalls();
        });
    }

    /**
     * aorTransaction
     */
    @Then("Si recuperano gli atti su radd alternative per un numero di volte superiore al limite definito")
    public void vengonoRecuperatiGliAttiPiuVolteDelLimite() {
        Assertions.assertThrows(HttpClientErrorException.class, () -> vengonoRecuperatiGliAttiPiuVolte(300, "UPLOADER"));
    }

    @Then("Si recuperano gli atti {int} volte su radd alternative da operatore radd {string}")
    public void vengonoRecuperatiGliAttiPiuVolte(int iteration, String raddOperatorType) {
        IntStream.range(0, iteration).forEach(x -> {
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
