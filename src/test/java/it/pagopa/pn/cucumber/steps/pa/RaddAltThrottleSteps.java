package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.ActInquiryResponseStatus;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.ResponseStatus;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model.StartTransactionResponseStatus;
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
                .takeWhile(i -> i == 0 || raddAltSteps.actInquiryResponse.getStatus().getCode() == ActInquiryResponseStatus.CodeEnum.NUMBER_0)
                .forEach(x -> {
                    raddAltSteps.lOperatoreUsoIUNPerRecuperariGliAtti(tipologiaIun, cf);
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
                .takeWhile(i -> i == 0 || raddAltSteps.startTransactionResponse.getStatus().getCode().equals(StartTransactionResponseStatus.CodeEnum.NUMBER_0)
                    || raddAltSteps.startTransactionResponse.getStatus().getCode().equals(StartTransactionResponseStatus.CodeEnum.NUMBER_2))
                .forEach(x -> raddAltSteps.startTransactionActRaddAlternative(generateRandomNumber(),false));
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
                .takeWhile(i -> i == 0 || raddAltSteps.aorInquiryResponse.getStatus().getCode() == ResponseStatus.CodeEnum.NUMBER_0)
                .forEach(x -> raddAltSteps.laPersonaFisicaChiedeDiVerificareAdOperatoreRaddLaPresenzaDiNotifiche(citizen, raddOperatorType));
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
                .takeWhile(i -> i == 0 || raddAltSteps.aorStartTransactionResponse.getStatus().getCode() == StartTransactionResponseStatus.CodeEnum.NUMBER_0)
                .forEach(x -> {
                    raddAltSteps.operationid = generateRandomNumber();
                    raddAltSteps.vengonoRecuperatiGliAttiDelleNotificheInStatoIrreperibileDaOperatoreRaddType(raddOperatorType);
                });
    }
}
