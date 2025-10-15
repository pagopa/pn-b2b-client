package it.pagopa.pn.cucumber.steps.censimentoStimeMittenti;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.pn.cucumber.steps.censimentoStimeMittenti.model.ModuloCommessa;
import it.pagopa.pn.cucumber.steps.censimentoStimeMittenti.model.StimeMittentiContext;
import it.pagopa.pn.cucumber.steps.delayer.client.DelayerLambdaClient;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerSenderLimit;
import it.pagopa.pn.cucumber.steps.delayer.utils.DelayerSenderLimitUtils;
import it.pagopa.pn.cucumber.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class CensimentoStimeMittentiSteps {
    public static final int MAX_ATTEMPTS = 10;
    public static final int SLEEP_MILLIS = 500;
    private final DelayerLambdaClient lambdaClient;
    private final StimeMittentiContext context;

    @When("si verifica che la tabella pn-DelayerSenderLimit contenga i nuovi limiti mittenti")
    public void fetchSenderLimitUntilCondition() throws Exception {
        for (DelayerSenderLimit senderLimit : context.expected.senderLimits) {
            lambdaClient.pollSenderLimitUntilCondition(senderLimit.getDeliveryDate(), context.province, null, MAX_ATTEMPTS, SLEEP_MILLIS, actual -> {
                boolean ok = actual.contains(senderLimit);
                if (!ok) log.info("SenderLimit mancante: {}", senderLimit);
                return ok;
            });
        }
    }

    @Given("vengono recuperate le stime mittenti da {string} a {string} per la provincia {string}")
    public void getSenderLimits(String meseAnnoDa, String meseAnnoA, String provincia) throws Exception {
        int attempt = 2;
        int sleepMillis = 1000;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M-yyyy");
        YearMonth da = YearMonth.parse(meseAnnoDa, formatter);
        YearMonth a = YearMonth.parse(meseAnnoA, formatter);
        context.da = da;
        context.a = a;
        context.province = provincia;

        List<LocalDate> mondays = DelayerSenderLimitUtils.getMondaysBetween(da, a, false, false);

        for(LocalDate monday : mondays) {
            List<DelayerSenderLimit> limits = lambdaClient.pollSenderLimit(monday.toString(), provincia, null, attempt, sleepMillis);
            context.actual.senderLimits.addAll(limits);
        }
    }


    @When("viene applicato localmente il nuovo modulo commessa {string}")
    public void calculateSenderLimitByCommessa(String path) {
        ModuloCommessa mc = FileUtils.readJsonAsSafe(path, ModuloCommessa.class);
        YearMonth periodoRiferimento = YearMonth.parse(mc.getPeriodoRiferimento());

        if(periodoRiferimento.isBefore(context.da) || periodoRiferimento.isAfter(context.a))
            throw new IllegalArgumentException("Periodo di riferimento nel modulo commessa invalido");

        context.moduloCommessa = mc;

        List<DelayerSenderLimit> newMonthSenderLimits = DelayerSenderLimitUtils.calculateSenderLimitByCommessa(mc);

        // Bisogna concatenare le nuove stime mittenti per questo mese con le actual
        context.expected.senderLimits = DelayerSenderLimitUtils.calculateSenderLimitByCommessa(mc);
    }
}
