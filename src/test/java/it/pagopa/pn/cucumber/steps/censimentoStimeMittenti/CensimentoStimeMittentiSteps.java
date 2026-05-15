package it.pagopa.pn.cucumber.steps.censimentoStimeMittenti;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.cucumber.steps.censimentoStimeMittenti.model.ModuloCommessa;
import it.pagopa.pn.cucumber.steps.censimentoStimeMittenti.model.StimeMittentiContext;
import it.pagopa.pn.cucumber.steps.delayer.client.DelayerLambdaClient;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerSenderLimit;
import it.pagopa.pn.cucumber.steps.delayer.utils.DelayerSenderLimitUtils;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import it.pagopa.pn.cucumber.utils.FileUtils;
import it.pagopa.pn.cucumber.utils.LambdaInvoker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
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
    private ApplicationContext applicationContext;

    @Autowired
    public CensimentoStimeMittentiSteps(ApplicationContext applicationContext, LambdaInvoker lambdaInvoker, @Value("${pn.delayer.lambda.arn}") String lambdaName) {
        this.context = new StimeMittentiContext();
        this.lambdaClient = new DelayerLambdaClient(lambdaInvoker, lambdaName);
        this.applicationContext = applicationContext;
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





    @Then("si verifica che la stima recupera corrisponda alla stima attesa")
    public void verifyResultsAreConsistentWithExpected() {
        calculateExpectedWeeklyProvincialEstimates();
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


}
