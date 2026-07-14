package it.pagopa.pn.cucumber.steps.censimentoStimeMittenti;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.pn.cucumber.steps.censimentoStimeMittenti.model.ModuloCommessa;
import it.pagopa.pn.cucumber.steps.censimentoStimeMittenti.model.StimeMittentiContext;
import it.pagopa.pn.cucumber.steps.delayer.client.DelayerLambdaClient;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerCountersSumEstimatesItem;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerSenderLimit;
import it.pagopa.pn.cucumber.steps.delayer.service.DelayerSevice;
import it.pagopa.pn.cucumber.steps.delayer.utils.DelayerSenderLimitUtils;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import it.pagopa.pn.cucumber.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
@Slf4j
public class CensimentoStimeMittentiSteps {
    public static final int MAX_ATTEMPTS = 600;
    public static final int SLEEP_MILLIS = 500;
    @Value("${pn.delayer.portfat.lambda.name}")
    private String portfatLambdaName;

    private final DelayerLambdaClient lambdaClient;
    private final StimeMittentiContext context;
    private Map<LocalDate, Integer> expectedWeeklyEstimates;
    private final ApplicationContext applicationContext;
    private final DelayerSevice delayerSevice;

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
            String sha256 = B2bUtils.computeSha256(applicationContext, String.format("classpath:/%s", fileName));
            Map<String,String> uploadParams = prepareParametersForGetPresignedUrl(fileName, sha256, "UPLOAD");
            String uploadResponse = lambdaClient.invoke("GET_PRESIGNED_URL", uploadParams);
            String preloadUrlUpload = extractUrlFromPresignedUrlResponse(uploadResponse, "uploadUrl");
            // viene caricato il file zip su S3 tramite il presigned url ottenuto
            B2bUtils.loadToPresigned(applicationContext, preloadUrlUpload, null, null, String.format("classpath:/%s", fileName), "application/zip");
            Map<String,String> downloadParams = prepareParametersForGetPresignedUrl(fileName, sha256, "DOWNLOAD");
            // viene ottenuto il presigned url per il download del file elaborato
            String downloadResponse = lambdaClient.invoke("GET_PRESIGNED_URL", downloadParams);
            String preloadUrlDownload = extractUrlFromPresignedUrlResponse(downloadResponse, "downloadUrl");

            // viene invocata la lambda portfat che elabora il file e genera le stime mittenti
            lambdaClient.invokePortfatLambda("pn-portfat-eventFileReady-lambda", portfatLambdaName, preloadUrlDownload);

        } catch (Exception e) {
            log.info("Errore non bloccante durante il caricamento del file zip e l'invocazione della lambda Portfat", e);
        }


    }

    private String extractUrlFromPresignedUrlResponse(String response, String fieldName) throws Exception {
        JsonNode root = new ObjectMapper().readTree(response);
        JsonNode body = new ObjectMapper().readTree(root.get("body").asText());
        return body.get(fieldName).asText();
    }


    private Map<String, String> prepareParametersForGetPresignedUrl(
            String fileName,
            String checksumSha256B64,
            String presignedUrlType
    ) {
        return Map.of(
                "fileName", fileName,
                "checksumSha256B64", checksumSha256B64,
                "presignedUrlType", presignedUrlType
        );
    }




    private void calculateExpectedWeeklyProvincialEstimates() {
        YearMonth yearMonth = YearMonth.of(2025, 7); // luglio 2025
        int monthlyRegionalEstimate = 1000;

        // Province e percentuali (es. Perugia 100%, Terni 100%)
        Map<String, Integer> provincePercentages = Map.of(
                "PE", 100
                // , "TR", 100
        );

        expectedWeeklyEstimates = new HashMap<>();

        for (Map.Entry<String, Integer> entry : provincePercentages.entrySet()) {
            String province = entry.getKey();
            int percentage = entry.getValue();

            calculateProvinceWeeklyEstimates(
                    yearMonth,
                    monthlyRegionalEstimate,
                    province,
                    percentage,
                    expectedWeeklyEstimates
            );
        }
    }


    private void calculateProvinceWeeklyEstimates(
            YearMonth yearMonth,
            int regionalMonthlyEstimate,
            String province,
            int percentage,
            Map<LocalDate, Integer> result
    ) {
        int daysInMonth = yearMonth.lengthOfMonth();

        // a) stima provinciale mensile
        double provincialMonthlyEstimate =
                regionalMonthlyEstimate * (percentage / 100.0);

        // b) stima provinciale giornaliera
        double dailyEstimate = provincialMonthlyEstimate / daysInMonth;

        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

        // c+d) settimane che iniziano di lunedì
        LocalDate firstMonday = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate currentMonday = firstMonday;

        while (!currentMonday.isAfter(lastDayOfMonth)) {
            LocalDate weekEnd = currentMonday.plusDays(6);

            long daysInCurrentMonth = Stream.iterate(currentMonday, d -> d.plusDays(1))
                    .limit(7)
                    .filter(d -> !d.isBefore(firstDayOfMonth) && !d.isAfter(lastDayOfMonth))
                    .count();

            if (daysInCurrentMonth > 0) {
                int weeklyEstimate = (int) Math.round(dailyEstimate * daysInCurrentMonth);
                result.merge(currentMonday, weeklyEstimate, Integer::sum);
            }

            currentMonday = currentMonday.plusWeeks(1);
        }
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
                        DelayerCountersSumEstimatesItem item =
                                delayerSevice.getCountersSumEstimates(deliveryDate, province, product);
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
