package it.pagopa.pn.cucumber.steps.censimentoStimeMittenti;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.pn.cucumber.steps.censimentoStimeMittenti.model.ModuloCommessa;
import it.pagopa.pn.cucumber.steps.censimentoStimeMittenti.model.StimeMittentiContext;
import it.pagopa.pn.cucumber.steps.delayer.client.DelayerLambdaClient;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerSenderLimit;
import it.pagopa.pn.cucumber.steps.delayer.utils.DelayerSenderLimitUtils;
import it.pagopa.pn.cucumber.utils.FileUtils;
import it.pagopa.pn.cucumber.utils.LambdaInvoker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
@Slf4j
public class CensimentoStimeMittentiSteps {
    public static final int MAX_ATTEMPTS = 600;
    public static final int SLEEP_MILLIS = 500;

    private final DelayerLambdaClient lambdaClient;
    private final StimeMittentiContext context;

    @Autowired
    public CensimentoStimeMittentiSteps(LambdaInvoker lambdaInvoker, @Value("${pn.delayer.lambda.arn}") String lambdaName) {
        this.context = new StimeMittentiContext();
        this.lambdaClient = new DelayerLambdaClient(lambdaInvoker, lambdaName);
    }

    @When("si verifica che la tabella pn-DelayerSenderLimit contenga i nuovi limiti mittenti per la provincia {string}")
    public void fetchSenderLimitUntilCondition(String province) {
        Set<DelayerSenderLimit> missing = new HashSet<>();
        Assertions.assertThat(context.province).as("Confronto di actual ed expected su province diverse").isEqualTo(province);

        for (DelayerSenderLimit senderLimit : context.expected.senderLimits) {
            try {
                lambdaClient.pollSenderLimitUntilCondition(senderLimit.getDeliveryDate(), province, null, MAX_ATTEMPTS, SLEEP_MILLIS, actual -> {
                    log.info("Trovati i seguenti limti: {}", actual);
                    boolean ok = actual.contains(senderLimit);
                    if (!ok) {
                        log.info("SenderLimit mancante: {}", senderLimit);
                        missing.add(senderLimit);
                    }
                    return ok;
                });
            } catch (NoSuchElementException e) {
                Assertions.assertThat(missing).as("Stime mittenti mancanti").isEmpty();
            } catch (Exception e) {
                throw new RuntimeException("Errore inatteso durante il polling", e);
            }
        }
    }

    @Given("vengono recuperate le stime mittenti da {string} a {string} per la provincia {string}")
    public void getSenderLimits(String meseAnnoDa, String meseAnnoA, String provincia) throws Exception {
        int attempt = 18;
        int sleepMillis = 500;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M-yyyy");
        YearMonth da = YearMonth.parse(meseAnnoDa, formatter);
        YearMonth a = YearMonth.parse(meseAnnoA, formatter);
        context.actual.senderLimits.clear();
        context.da = da;
        context.a = a;
        context.province = provincia;

        List<LocalDate> mondays = DelayerSenderLimitUtils.getMondaysBetween(da, a, false, false);

        for (LocalDate monday : mondays) {
            List<DelayerSenderLimit> limits = lambdaClient.pollSenderLimit(monday.toString(), provincia, null, attempt, sleepMillis);
            context.actual.senderLimits.addAll(limits);
        }
    }

    @When("vengono applicati localmente i seguenti moduli commessa per la provincia {string}:")
    public void calculateSenderLimitByCommessa(String provincia, DataTable paths) {
        List<ModuloCommessa> commesse = paths.asList().stream()
                .map(path -> FileUtils.readJsonAsSafe(path, ModuloCommessa.class))
                .toList();

        context.applyCommesseInExpected(provincia, commesse.toArray(new ModuloCommessa[0]));
    }

}
