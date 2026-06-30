package it.pagopa.pn.cucumber.steps.delayer.service;

import it.pagopa.pn.cucumber.steps.delayer.client.DelayerLambdaClientV2;
import it.pagopa.pn.cucumber.steps.delayer.loader.DelayerCsvLoader;
import it.pagopa.pn.cucumber.steps.delayer.model.*;
import it.pagopa.pn.cucumber.steps.delayer.model.enums.WorkflowSteps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.lang.Thread.sleep;

@Service
@RequiredArgsConstructor
@Slf4j
public class DelayerSevice {
    public static final int POLLING_MAX_MINUTES = 90;
    public static final String[] CSV_FILES = new String[]{"tcRankingMerged.csv", "tcSenderUnknow.csv", "tcSplitSender.csv", "tcZeroDriver.csv", "tcProvCapNonCensite.csv",
            "spedizioni_3000.csv", "tcWeeklyPrintCapacity.csv", "tcSenderUnknow_5010.csv", "notificationCancelled.csv", "tcSenderPriority.csv", "tcSenderPriorityFrozenW1.csv", "tcSenderPriorityFrozenW2.csv", "tcSenderPriorityFrozenW12.csv"};

    private final DelayerLambdaClientV2 lambdaClient;
    private final DelayerCsvLoader csvLoader;


    public DelayerCountersPrintItem getPrintCapacityCounter(String deliveryDate) {
        try {

            var counters = lambdaClient.getCountersPrint(deliveryDate);
            return counters.getItems().get(0);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Errore durante GET_PRINT_CAPACITY_COUNTER per deliveryDate %s"
                            .formatted(deliveryDate),
                    e
            );
        }
    }

    public DelayerCountersSumEstimatesItem getCountersSumEstimates(String deliveryDate, String province, String product) {
        try {

            return lambdaClient.getCountersSumEstimates(deliveryDate, province, product)
                    .getItems()
                    .stream()
                    .filter(item -> item.getSk().equals(String.format("SUM_ESTIMATES~%s~%s", product, province)))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Nessun item trovato con deliveryDate %s nello sk".formatted(deliveryDate)));
//            return extractLatestNumberOfShipments(counters.getItems());

        } catch (Exception e) {
            throw new RuntimeException(
                    "Errore durante GET_PRINT_CAPACITY_COUNTER per deliveryDate %s"
                            .formatted(deliveryDate),
                    e
            );
        }
    }

    public Integer getPaperDeliveryItemsSize(String deliveryDate, WorkflowSteps workFlowStep) {
        return lambdaClient.getPaperDelivery(deliveryDate, workFlowStep).getItems().size();
    }

    public Integer getUsedCapacity(String unifiedDeliveryDriver, String geoKey, String deliveryDate) {
        return lambdaClient.getUsedCapacity(unifiedDeliveryDriver, geoKey, deliveryDate).map(DelayerUsedCapacity::getUsedCapacity).orElse(-1);
    }

    public Integer getCountersPrintSize(String deliveryDate) {
        return lambdaClient.getCountersPrint(deliveryDate).getItems().size();
    }

    public Integer getUsedSenderLimitSize(String deliveryDate, String pk) {
        return lambdaClient.getUsedSenderLimitByPk(deliveryDate, pk).getItems().size();
    }

    public Integer getUsedSenderLimit(String deliveryDate, String pk) {
        return lambdaClient.getUsedSenderLimitByPk(deliveryDate, pk).getItems().get(0).getSenderLimit();
    }

    public Stream<DelayerPaperDelivery> getResidualPapers(String deliveryDate) {
        var residualPapersRes = lambdaClient.getResidualPapers(deliveryDate, LocalDate.now().toString());
        return csvLoader.downloadResidualPapers(residualPapersRes.getDownloadUrl());
    }

    public void deleteDataAll() {
        Arrays.stream(CSV_FILES).forEach(csv -> {
            try {
                lambdaClient.deleteData(csv);
            } catch (Exception e) {
                if (e.getMessage().contains("The specified key does not exist")) {
                    log.warn("[DELETE_DATA] Key non trovata: {}", csv);
                    return;
                }
                throw new RuntimeException(e);
            }

        });
    }

    public int getAvailableCapacity(String entityId, String deliveryDate) {
        var driver = entityId.split("~")[0];
        var province = entityId.split("~")[1];
        return getAvailableCapacity(driver, province, deliveryDate);

    }

    public int getAvailableCapacity(String driver, String province, String deliveryDate) {
        //TODO da correggere perché sembra questo il punto del vecchio test che adesso non funziona
        String expectedKey = driver + "~" + province;

        var usedCapacity = lambdaClient.getUsedCapacity(driver, province, deliveryDate);

        int declared = usedCapacity
                .map(DelayerUsedCapacity::getDeclaredCapacity)
                .orElse(-1);

        int used = usedCapacity
                .map(DelayerUsedCapacity::getUsedCapacity)
                .orElse(-1);

        if (declared == -1 && used == -1) {
            return -1;
        }

        return declared - used;
    }

    public void importData(String csvName, String deliveryWeek) {
        lambdaClient.importData(csvName, deliveryWeek);
    }

    public String runBatchWorkflowStateMachine(Integer printCapacity, String currentMonday) {
        return lambdaClient.runAlgorithm(printCapacity, currentMonday).getExecutionArn();
    }

    public void waitUntilStepFunctionEnd(DelayerContext context) throws InterruptedException {
        if (context.currentExecutionArn == null) return;

        final String arn = context.currentExecutionArn;

        final long startTime = System.currentTimeMillis();
        final long maxWaitMillis = TimeUnit.MINUTES.toMillis(POLLING_MAX_MINUTES);
        final long pollingIntervalMillis = TimeUnit.MINUTES.toMillis(1);

        log.info("Inizio polling Step Function: {}", arn);

        while (true) {

            DelayerStatusExecution status;

            try {
                status = lambdaClient.getStatusExecution(arn);
            } catch (Exception e) {
                log.error(e.getCause().toString());
                if (System.currentTimeMillis() - startTime > TimeUnit.MINUTES.toMillis(5))
                    throw new RuntimeException("Timeout durante il recupero dello stato della Step Function: " + arn, e);

                sleep(pollingIntervalMillis);
                continue;
            }

            String state = status.getStatus();
            log.info("Stato StepFunction {} → {}", arn, state);

            // Stato non ricevuto
            if (state == null) {
                log.warn("Stato null dalla Step Function {}, riprovo...", arn);
                sleep(pollingIntervalMillis);
                continue;
            }

            switch (state) {
                case "RUNNING":
                    // continua polling
                    break;

                case "SUCCEEDED":
                    log.info("Step Function {} completata con successo.", arn);
                    return;

                case "FAILED":
                case "TIMED_OUT":
                case "ABORTED":
                    log.error("Step Function {} terminata con errore: {} - cause: {}",
                            arn, status.getError(), status.getCause());
                    throw new RuntimeException(
                            "Step Function TERMINATED WITH ERROR: state=" + state +
                                    ", error=" + status.getError() +
                                    ", cause=" + status.getCause()
                    );

                default:
                    log.warn("Stato Step Function {} sconosciuto: {}", arn, state);
                    break;
            }

            // Controllo timeout
            if (System.currentTimeMillis() - startTime > maxWaitMillis) {
                throw new RuntimeException("Timeout: Step Function non è terminata entro 10 minuti: " + arn);
            }

            // Prossimo polling
            sleep(pollingIntervalMillis);
        }
    }

    public List<DelayerPaperDelivery> findByWorkflowStep(Set<String> requestIds, String workflowStep, String deliveryDate, int maxMinutes) throws Exception {
        Set<DelayerPaperDelivery> found = new LinkedHashSet<>();
        Set<String> pending = new HashSet<>(requestIds);

        final int retryFrequency = 3000; // ms
        final int pollingFrequency = 200;     // ms per richiesta
        final long totalMinPollingTimeMillis = maxMinutes * 60L * 1000L;

        long startTime = System.currentTimeMillis();
        int attempt = 0;

        do {
            attempt++;

            long elapsedMillis = System.currentTimeMillis() - startTime;
            long remainingMillis = Math.max(0, totalMinPollingTimeMillis - elapsedMillis);
            long elapsedSeconds = elapsedMillis / 1000;
            long remainingSeconds = remainingMillis / 1000;

            log.info("Tentativo {} - RequestId rimanenti: {} | Workflow step: {} | Tempo trascorso: {}s | Tempo rimanente: {}s",
                    attempt, pending.size(), workflowStep, elapsedSeconds, remainingSeconds);

            Iterator<String> iterator = pending.iterator();
            while (iterator.hasNext()) {
                String requestId = iterator.next();
                var results = pollByRequestId(requestId, 1, pollingFrequency);
                if (requestId.contains("_11") || requestId.contains("_12") || requestId.contains("_13")) {
                    log.info("DEBUG requestId={}", requestId);
                    results.forEach(r ->
                            log.warn("DEBUG requestId={} pk={} deliveryDate={} driver={} province={} product={} priority={}",
                                    requestId,
                                    r.getPk(),
                                    r.getDeliveryDate(),
                                    r.getUnifiedDeliveryDriver(),
                                    r.getProvince(),
                                    r.getProductType(),
                                    r.getPriority()
                            )
                    );
                }

                Optional<DelayerPaperDelivery> match = results.stream()
                        .filter(r -> r.getPk().contains(workflowStep) && r.getPk().contains(deliveryDate))
                        .findFirst();

                match.ifPresent(delivery -> {
                    found.add(delivery);
                    iterator.remove();
                });
            }

            if (!pending.isEmpty()) {
                sleep(retryFrequency);
            }

        } while (!pending.isEmpty() && (System.currentTimeMillis() - startTime < totalMinPollingTimeMillis));

        long elapsedMillis = System.currentTimeMillis() - startTime;
        long elapsedSeconds = elapsedMillis / 1000;

        String reason;
        if (pending.isEmpty()) {
            reason = "Tutte le notifiche trovate correttamente";
        } else if (elapsedMillis >= totalMinPollingTimeMillis) {
            reason = "Timeout raggiunto dopo almeno %d minuti".formatted(maxMinutes);
        } else {
            reason = "Uscita anticipata imprevista (possibile errore nel loop)";
        }

        if (found.size() != requestIds.size()) {
            log.warn("""
                    Trovate %d notifiche su %d attese per step '%s'
                    Tempo totale di polling: %d secondi
                    Motivo dell'uscita: %s
                    """.formatted(found.size(), requestIds.size(), workflowStep, elapsedSeconds, reason));
        } else {
            log.info("""
                    Tutte le %d notifiche trovate correttamente per step '%s'
                    Tempo di polling: %d secondi
                    """.formatted(found.size(), workflowStep, elapsedSeconds));
        }

        return new ArrayList<>(found);
    }

    public List<DelayerPaperDelivery> pollByRequestId(String requestId, int maxAttempts, int sleepMillis) throws Exception {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            var request = lambdaClient.getByRequestId(requestId);
            if (!request.isEmpty()) {
                return request;
            }
            sleep(sleepMillis);
        }

        log.debug("Polling esaurito per requestId {}", requestId);
        return Collections.emptyList();
    }

    public DelayerPayload runDelayerToPaperChannel() {
        return lambdaClient.delayerToPaperChannel();
    }

    private Instant extractRightmostTimestamp(String sk) {
        if (sk == null || sk.isBlank()) {
            return null;
        }

        String[] parts = sk.split("~");
        for (int i = parts.length - 1; i >= 0; i--) {
            try {
                return Instant.parse(parts[i]);
            } catch (Exception ignored) {
                // non è un timestamp, continuo a sinistra
            }
        }

        return null;
    }


    private int extractLatestNumberOfShipments(List<? extends DelayerCountersSumEstimatesItem> items) {
        try {
            Instant latestInstant = null;
            Integer latestShipments = null;

            for (var item : items) {
                String sk = item.getSk();
                Instant itemTimestamp = extractRightmostTimestamp(sk);

                if (itemTimestamp == null) {
                    continue; // item senza timestamp valido nello sk
                }

                if (latestInstant == null || itemTimestamp.isAfter(latestInstant)) {
                    latestInstant = itemTimestamp;
                    latestShipments = Optional.ofNullable(item.getNumberOfShipments()).orElse(0);
                }
            }

            if (latestShipments == null && !items.isEmpty()) {
                latestShipments = Optional.of(items).map(a -> a.get(0)).map(DelayerCountersSumEstimatesItem::getNumberOfShipments).orElse(0); // fallback: prendo il primo item se nessuno ha timestamp valido
            }

            return (latestShipments == null) ? 0 : latestShipments;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON input", e);
        }
    }

    public int getCountersExclude(String deliveryDate, String province, String product) {
        try {
            var counters = lambdaClient.getCountersExclude(deliveryDate, province, product);
            return extractLatestNumberOfShipments(counters.getItems());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int getDeclaredCapacity(String deliveryDate, String province, String product, Set<String> foundProducts) {
        try {
            var declaredCapacityResponse = lambdaClient.getDeclaredCapacity(province, deliveryDate);

            return extractTotalCapacityForProduct(declaredCapacityResponse, product, foundProducts);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int extractTotalCapacityForProduct(DelayerDeclaredCapacity declaredCapacityRespons, String product, Set<String> foundProducts) {
        try {

            var items = declaredCapacityRespons.getItems();

            int totalCapacity = 0;

            for (var item : items) {
                var productsNode = item.getProducts();

                // Check if products list contains the given product
//                boolean hasProduct = false;
                for (var foundProduct : productsNode) {
                    foundProducts.add(foundProduct);

                    if (foundProduct.equals(product)) {
//                        hasProduct = true;
                        totalCapacity += Optional.ofNullable(item.getCapacity()).orElse(0);
                    }
                }

//                if (hasProduct) {
//                    totalCapacity += Optional.ofNullable(item.getCapacity()).orElse(0);
//                }
            }

            return totalCapacity;

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON input", e);
        }
    }


    public int fetchWeeklyEstimateForPA(String deliveryDate, String pk) {

        try {
            var senderLimitResponse = lambdaClient.getSenderLimitByPk(deliveryDate, pk);

            Assertions.assertThat(senderLimitResponse.getItems().size())
                    .as("L'operation deve restituire una lista di items non vuota altrimenti le stime non sono state elaborate")
                    .isGreaterThan(0);
            return senderLimitResponse.getItems().get(0).getWeeklyEstimate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
