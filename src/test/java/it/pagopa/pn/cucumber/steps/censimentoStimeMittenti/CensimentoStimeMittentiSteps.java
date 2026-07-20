package it.pagopa.pn.cucumber.steps.censimentoStimeMittenti;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.pn.cucumber.steps.censimentoStimeMittenti.model.ModuloCommessa;
import it.pagopa.pn.cucumber.steps.censimentoStimeMittenti.model.StimeMittentiContext;
import it.pagopa.pn.cucumber.steps.delayer.client.DelayerLambdaClient;
import it.pagopa.pn.cucumber.steps.delayer.client.DelayerLambdaClientV2;
import it.pagopa.pn.cucumber.steps.delayer.client.PortfatLambdaClient;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerCountersSumEstimatesItem;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerPresigneUrlDownload;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerPresigneUrlUpload;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerSenderLimit;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerSenderLimits;
import it.pagopa.pn.cucumber.steps.delayer.service.DelayerSevice;
import it.pagopa.pn.cucumber.steps.delayer.utils.DelayerSenderLimitUtils;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import it.pagopa.pn.cucumber.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;

import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
@Slf4j
public class CensimentoStimeMittentiSteps {

    private final DelayerLambdaClient lambdaClient;
    private final DelayerLambdaClientV2 lambdaClientV2;
    private final PortfatLambdaClient portfatLambdaClient;
    private final StimeMittentiContext context;
    private final ApplicationContext applicationContext;
    private final DelayerSevice delayerSevice;

    @ParameterType("pn-PaperDeliverySenderLimit|pn-PaperDeliverySenderLimitMock")
    public String senderLimitTable(String tableName) {
        return tableName;
    }

    @ParameterType("contenga|non contenga")
    public boolean containsOrNot(String value) {
        return value.equals("contenga");
    }

    @When("si verifica che la tabella {senderLimitTable} {containsOrNot} i nuovi limiti mittenti per la provincia {string}")
    public void fetchSenderLimitUntilCondition(String senderLimitTable, boolean shouldContain, String province) {
        Set<DelayerSenderLimit> missing = new HashSet<>();
        Assertions.assertThat(context.province).as("Confronto di actual ed expected su province diverse").isEqualTo(province);

        for (DelayerSenderLimit senderLimit : context.expected.senderLimits) {
            try {
                Awaitility.await()
                        .atMost(Duration.ofMinutes(5))
                        .pollInterval(Duration.ofSeconds(10))
                        .pollDelay(Duration.ZERO)
                        .until(() -> {
                            DelayerSenderLimits actual = lambdaClientV2.getSenderLimitByProvinceWithTable(senderLimitTable, senderLimit.getDeliveryDate(), province);
                            boolean found = actual.getItems()
                                    .stream()
                                    .anyMatch(item -> item.getPk().equals(senderLimit.getPk())
                                            && item.getDeliveryDate().equals(senderLimit.getDeliveryDate())
                                            && item.getWeeklyEstimate() == senderLimit.getWeeklyEstimate());

                            log.info("Trovati i seguenti limiti: {}", actual);

                            boolean ok = shouldContain ? found : !found;

                            if (!ok) {
                                log.info("SenderLimit {}: {}", shouldContain ? "mancante" : "ancora presente", senderLimit);
                                missing.add(senderLimit);
                            }
                            return ok;
                        });
            } catch (NoSuchElementException e) {
                Assertions.assertThat(missing).as(shouldContain ? "Stime mittenti mancanti" : "Stime mittenti ancora presenti").isEmpty();
            } catch (Exception e) {
                throw new RuntimeException("Errore inatteso durante il polling", e);
            }
        }
    }

    @Given("vengono recuperate le stime mittenti da {string} a {string} per la provincia {string}")
    public void getSenderLimits(String meseAnnoDa, String meseAnnoA, String provincia) throws Exception {
        int attempt = 18;
        int sleepMillis = 500;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
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

    /*
    Given ricavo il presigned url e carico lo zip
    Then verifico l'elaborazione delle commesse e ottengo le stime settimanali provinciali calcolate dal sistema
    And effettuo il calcolo delle stime settimanali provinciali attese
    Then si verifica che i risultati siano coerenti con quelli attesi
     */
    @Given("vengono caricati i moduli commessa come file zip su portfat: {string}")
    public void uploadZipFile(String fileName) {
        try {
            String preloadUrlDownload = preloadZipFile(fileName, true);
            // viene invocata la lambda portfat che elabora il file e genera le stime mittenti
            portfatLambdaClient.invokePortfatLambda(preloadUrlDownload);

        } catch (Exception e) {
            log.info("Errore non bloccante durante il caricamento del file zip e l'invocazione della lambda Portfat", e);
        }
    }

    @Given("vengono caricati i moduli commessa mock tramite il seguente zip: {string}")
    public void uploadMockZipFile(String fileName) {
        try {
            preloadZipFile(fileName, false);
            // viene invocata la lambda per effettuare il caricamento dei limiti mittenti mock
            delayerSevice.insertMockSenderLimit(fileName);

        } catch (Exception e) {
            log.info("Errore non bloccante durante il caricamento del file zip e l'invocazione della lambda per i limiti mittenti mock", e);
        }
    }


    private String preloadZipFile(String fileName, boolean withDownload) {
        String preloadUrl = null;
        try {
            String sha256 = B2bUtils.computeSha256(applicationContext, String.format("classpath:/%s", fileName));
            DelayerPresigneUrlUpload uploadResponse = lambdaClientV2.getPresignedUrlUpload(fileName, sha256);
            preloadUrl = uploadResponse.getUploadUrl();
            // viene caricato il file zip su S3 tramite il presigned url ottenuto
            B2bUtils.loadToPresigned(applicationContext, preloadUrl, null, null, String.format("classpath:/%s", fileName), "application/zip");
            if (withDownload) {
                // viene ottenuto il presigned url per il download del file elaborato
                DelayerPresigneUrlDownload downloadResponse = lambdaClientV2.getPresignedUrlDownload(fileName);
                preloadUrl = downloadResponse.getDownloadUrl();
            }
        } catch (Exception e) {
            log.info("Errore non bloccante durante il caricamento del file zip e l'invocazione della lambda", e);
        }
        return preloadUrl;
    }

    /**
     * Verifica che la somma delle stime di tutte le PA per la coppia prodotto e provincia sia coerente con quella attesa passata in input.
     * La stima attesa è calcolata internamente in base alla distribuzione dei giorni della settimana considerando anche i giorni a cavallo tra due mesi e al valore di commessa di ciascun mese.
     * Formula:
     * - numberOfShipments = (valore commessa primo mese / giorni totali primo mese) * giorni della settimana a cavallo del primo mese + (valore commessa secondo mese / giorni totali secondo mese) * giorni della settimana a cavallo del secondo mese
     * @param deliveryDate la settimana di riferimento (formato yyyy-MM-dd, deve essere un lunedì)
     * @param product il prodotto di riferimento
     * @param province la provincia di riferimento
     * @param dataTable la tabella con le stime attese, con le seguenti colonne:
     *                  - numberOfShipments: la stima settimanale provinciale attesa
     *                  - firstWeekNumberOfShipments: la stima della prima parte della settimana a cavallo attesa
     *                  - secondWeekNumberOfShipments: la stima della seconda parte della settimana a cavallo attesa
     */
    @And("per la settimana {string}, per il prodotto {string} per la provincia {string} si verifica che la somma delle commesse sia:")
    public void verifyWeeklyProvincialEstimates(String deliveryDate, String product, String province, Map<String, String> dataTable) {
        verifyWeeklyProvincialEstimates(dataTable,
                () -> delayerSevice.getCountersSumEstimates(deliveryDate, province, product));
    }

    @And("per la settimana {string}, per il prodotto {string} per la provincia {string} si verifica che la somma delle commesse nella tabella {senderLimitTable} sia:")
    public void verifyWeeklyProvincialEstimatesInSenderLimitTable(String deliveryDate, String product, String province, Map<String, String> dataTable) {
        verifyWeeklyProvincialEstimates(dataTable,
                () -> delayerSevice.getCountersSumEstimates(deliveryDate, province, product, true));
    }

    private void verifyWeeklyProvincialEstimates(Map<String, String> dataTable,
                                                 Supplier<DelayerCountersSumEstimatesItem> estimatesSupplier) {
        int expectedTotal = Integer.parseInt(dataTable.get("numberOfShipments"));
        int expectedFirstWeek = Integer.parseInt(dataTable.get("firstWeekNumberOfShipments"));
        int expectedSecondWeek = Integer.parseInt(dataTable.get("secondWeekNumberOfShipments"));

        AtomicReference<DelayerCountersSumEstimatesItem> lastResult = new AtomicReference<>();

        try {
            Awaitility.await()
                    .atMost(Duration.ofMinutes(3))
                    .pollInterval(Duration.ofSeconds(20))
                    .pollDelay(Duration.ZERO) // prova subito, poi ripete ogni 20s
                    .until(() -> {
                        DelayerCountersSumEstimatesItem item = estimatesSupplier.get();
                        lastResult.set(item);
                        return item != null
                                && item.getNumberOfShipments() == expectedTotal
                                && item.getFirstWeekNumberOfShipments() == expectedFirstWeek
                                && item.getSecondWeekNumberOfShipments() == expectedSecondWeek;
                    });
        } catch (ConditionTimeoutException e) {
            // Timeout scaduto: eseguo comunque gli assert "classici" per avere
            // un messaggio di errore dettagliato su quale campo non coincide
            DelayerCountersSumEstimatesItem estimatesItem = lastResult.get();
            Assertions.assertThat(estimatesItem)
                    .as("Nessuna stima disponibile dopo 3 minuti di attesa")
                    .isNotNull();

            Assertions.assertThat(estimatesItem.getNumberOfShipments())
                    .as("La somma delle stime settimanali provinciali calcolate internamente non è coerente con quella attesa passata in input")
                    .isEqualTo(expectedTotal);
            Assertions.assertThat(estimatesItem.getFirstWeekNumberOfShipments())
                    .as("La stima della prima parte della settimana a cavallo non è coerente con quella attesa")
                    .isEqualTo(expectedFirstWeek);
            Assertions.assertThat(estimatesItem.getSecondWeekNumberOfShipments())
                    .as("La stima della seconda parte della settimana a cavallo non è coerente con quella attesa")
                    .isEqualTo(expectedSecondWeek);
        }
    }

}
