package it.pagopa.pn.cucumber.steps.delayer.validator;

import it.pagopa.pn.cucumber.steps.delayer.model.DelayerContext;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerPaperDelivery;
import it.pagopa.pn.cucumber.steps.delayer.client.DelayerLambdaClient;

import io.cucumber.datatable.DataTable;
import it.pagopa.pn.cucumber.steps.delayer.model.enums.WorkflowSteps;
import it.pagopa.pn.cucumber.steps.delayer.utils.DelayerPaperDeliveryUtils;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static it.pagopa.pn.cucumber.steps.delayer.utils.DelayerPaperDeliveryUtils.extractSeed;
import static it.pagopa.pn.cucumber.steps.delayer.utils.DelayerPaperDeliveryUtils.extractWorkflowStep;

public class DelayerValidator {

    private final DelayerContext context;
    private final DelayerLambdaClient lambdaClient;
    private final DelayerPaperDeliveryUtils utils;

    public DelayerValidator(DelayerContext context, DelayerLambdaClient lambdaClient, DelayerPaperDeliveryUtils utils) {
        this.context = context;
        this.lambdaClient = lambdaClient;
        this.utils = utils;
    }

    public void assertPianifications() {
        if (context.failPianification.isEmpty()) return;

        StringBuilder sb = new StringBuilder("Pianificazione fallita per i seguenti seed:\n");
        context.failPianification.forEach((seed, err) -> sb.append("• ").append(seed).append(": ").append(err).append("\n"));
        Assertions.fail(sb.toString());
    }

    public void checkRanking(WorkflowSteps step, DataTable expectedOrder) {
        Set<String> validSeeds = context.groupedBySeed.keySet().stream()
                .filter(seed -> !context.failPianification.containsKey(seed))
                .collect(Collectors.toSet());

        if (validSeeds.isEmpty()) {
            throw new IllegalStateException("Tutti i seed hanno fallito allo step: " + step.name());
        }

        boolean almenoUnoOk = false;

        for (String seed : validSeeds) {
            List<String> problems = checkSilently(step, seed);

            if (problems.isEmpty()) {
                almenoUnoOk = true; // questo seed è ok
            } else {
                registerFailureIfAbsent(
                        seed,
                        "Verifica actual⊆expected fallita allo step '%s'. Dettagli: %s"
                                .formatted(step.name(), problems)
                );
            }
        }

        if (!almenoUnoOk) {
            assertPianifications();
        }
    }

    public List<String> checkSilently(WorkflowSteps step, String seed) {
        var actual   = context.actualPianification.getOrDefault(seed, Map.of()).get(step.name());
        var expected = context.expectedPianification.getOrDefault(seed, Map.of()).get(step.name());

        // Normalizza i null a liste vuote
        List<DelayerPaperDelivery> exp = expected == null ? List.of() : expected;
        List<DelayerPaperDelivery> act = actual   == null ? List.of() : actual;

        // Entrambi vuoti -> ok
        if (exp.isEmpty() && act.isEmpty()) {
            return List.of();
        }

        // Prepara info seed/step (servono per il messaggio di failure)
        SeedStep contextInfo;

        if (!exp.isEmpty() && !act.isEmpty()) {
            SeedStep expInfo = ensureHomogeneous(exp, "expected"); // valida che tutte le entry abbiano stesso seed/step
            SeedStep actInfo = ensureHomogeneous(act, "actual");

            if (!expInfo.equals(actInfo)) {
                registerFailureIfAbsent(
                        seed,
                        "Seed/step diversi tra expected (%s,%s) e actual (%s,%s)"
                                .formatted(expInfo.seed(), expInfo.workflowStep(), actInfo.seed(), actInfo.workflowStep())
                );
                return List.of("Seed/step non coerenti tra expected e actual");
            }
            contextInfo = expInfo; // == actInfo
        } else {
            // Una sola non vuota
            List<DelayerPaperDelivery> ref = exp.isEmpty() ? act : exp;
            contextInfo = ensureHomogeneous(ref, exp.isEmpty() ? "actual" : "expected");
        }

        // Confronto campo-per-campo (actual ⊆ expected), usando toComparableMapList interno
        List<String> problems = utils.compare(exp, act);

        if (!problems.isEmpty()) {
            registerFailureIfAbsent(
                    seed,
                    "Verifica actual⊆expected fallita allo step '%s'. Dettagli: %s"
                            .formatted(contextInfo.workflowStep(), problems)
            );
        }

        return problems;
    }

    public Map<String, List<String>> checkSilentlyAll(WorkflowSteps step) {
        Set<String> validSeeds = context.groupedBySeed.keySet().stream()
                .filter(s -> !context.failPianification.containsKey(s))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Map<String, List<String>> result = new LinkedHashMap<>();
        boolean almenoUnoOk = false;

        for (String seed : validSeeds) {
            List<String> problems = checkSilently(step, seed);
            result.put(seed, problems);
            if (problems.isEmpty()) {
                almenoUnoOk = true;
            }
        }

        if (!almenoUnoOk && !result.isEmpty()) {
            assertPianifications();
        }

        return result;
    }

    public void checkFrozen(WorkflowSteps step, List<DelayerPaperDelivery> frozenExpected) throws Exception {
        Set<String> validSeeds = context.groupedBySeed.keySet().stream()
                .filter(seed -> !context.failPianification.containsKey(seed))
                .collect(Collectors.toSet());

        if (validSeeds.isEmpty()) {
            assertPianifications();
        }

        List<DelayerPaperDelivery> actualFrozen = lambdaClient.findByWorkflowStep(
                frozenExpected.stream().map(DelayerPaperDelivery::getRequestId).collect(Collectors.toSet()),
                step.name(),
                getNextMonday(),
                15
        );

        if (frozenExpected.size() != actualFrozen.size()) {
            validSeeds.forEach(seed -> registerFailureIfAbsent(seed,
                    "Mismatch congelati - attesi: %d, trovati: %d".formatted(frozenExpected.size(), actualFrozen.size())));
            return;
        }

        Set<Map<String, String>> expectedSet = toComparableMapList(frozenExpected, step).stream().collect(Collectors.toSet());
        Set<Map<String, String>> actualSet = toComparableMapList(actualFrozen, step).stream().collect(Collectors.toSet());

        Set<Map<String, String>> missing = new HashSet<>(expectedSet);
        missing.removeAll(actualSet);

        if (!missing.isEmpty()) {
            validSeeds.forEach(seed ->
                    registerFailureIfAbsent(seed, "Differenze tra congelati attesi e trovati: " + missing));
        }
    }

    public void checkDriverCapacity(String compare, int expectedMin, WorkflowSteps step) {
        List<DelayerPaperDelivery> notifiche = context.groupedBySeed.values().stream()
                .flatMap(List::stream)
                .filter(n -> context.expectedPianification.getOrDefault(n.getRequestId(), Map.of())
                        .getOrDefault(step.name(), List.of()).contains(n))
                .toList();

        Map<String, List<DelayerPaperDelivery>> bySeed = notifiche.stream()
                .collect(Collectors.groupingBy(DelayerPaperDeliveryUtils::extractSeed));

        Set<String> validSeeds = bySeed.keySet().stream()
                .filter(seed -> !context.failPianification.containsKey(seed))
                .collect(Collectors.toSet());

        if (validSeeds.isEmpty()) {
            throw new IllegalStateException("Nessun seed valido per verifica capacità");
        }

        for (String seed : validSeeds) {
            List<DelayerPaperDelivery> deliveries = bySeed.get(seed);

            Set<String> tripleKeys = deliveries.stream()
                    .map(n -> n.getUnifiedDeliveryDriver() + "~" + n.getProvince() + "~" + utils.extractDeliveryDate(n))
                    .collect(Collectors.toSet());

            for (String triple : tripleKeys) {
                String[] parts = triple.split("~");
                int available = lambdaClient.getAvailableCapacity(parts[0], parts[1], parts[2]);

                switch (compare.toLowerCase()) {
                    case "esattamente" -> {
                        if (available != expectedMin) {
                            registerFailureIfAbsent(seed, "Capacità esatta non rispettata: %d attesi, %d disponibili".formatted(expectedMin, available));
                        }
                    }
                    case "almeno" -> {
                        if (available < expectedMin) {
                            registerFailureIfAbsent(seed, "Capacità inferiore al minimo: %d attesi, %d disponibili".formatted(expectedMin, available));
                        }
                    }
                    default -> throw new IllegalArgumentException("Comparatore non valido: " + compare);
                }
            }
        }
    }

    private void registerFailureIfAbsent(String seed, String message) {
        context.failPianification.putIfAbsent(seed, message);
    }

    private List<Map<String, String>> toComparableMapList(List<DelayerPaperDelivery> list, WorkflowSteps step) {
        return list.stream()
                .map(n -> {
                    Map<String, String> map = new LinkedHashMap<>();
                    map.put("pk", n.getPk());
                    map.put("sk", n.getSk());
                    map.put("requestId", n.getRequestId());
                    map.put("notificationSentAt", n.getNotificationSentAt());
                    map.put("prepareRequestDate", n.getPrepareRequestDate());
                    map.put("productType", n.getProductType());
                    map.put("senderPaId", n.getSenderPaId());
                    map.put("province", n.getProvince());
                    map.put("cap", n.getCap());
                    map.put("attempt", n.getAttempt());
                    map.put("iun", n.getIun());
                    if (step.getIndex() > 0) {
                        map.put("unifiedDeliveryDriver", n.getUnifiedDeliveryDriver());
                    }
                    return map;
                }).toList();
    }

    private String getNextMonday() {
        return LocalDate.now()
                .with(java.time.temporal.TemporalAdjusters.next(java.time.DayOfWeek.MONDAY))
                .format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    private record SeedStep(String seed, String workflowStep) {}

    // supponendo: record SeedStep(String seed, WorkflowSteps workflowStep) {}
    private SeedStep ensureHomogeneous(List<DelayerPaperDelivery> list, String label) {
        if (list == null || list.isEmpty()) {
            throw new IllegalStateException("Lista '%s' vuota durante la verifica di omogeneità.".formatted(label));
        }

        final String seed0 = extractSeed(list.get(0));
        final WorkflowSteps step0 = extractWorkflowStep(list.get(0));

        // cerca il primo elemento che non combacia per seed o step
        Optional<DelayerPaperDelivery> mismatch = list.stream()
                .filter(n -> {
                    String s = extractSeed(n);
                    WorkflowSteps st = extractWorkflowStep(n);
                    return !seed0.equals(s) || st != step0;
                })
                .findFirst();

        if (mismatch.isPresent()) {
            DelayerPaperDelivery bad = mismatch.get();
            String badSeed = extractSeed(bad);
            WorkflowSteps badStep = extractWorkflowStep(bad);

            throw new IllegalStateException(
                    "Seed non omogenei all'interno di '%s': atteso seed=%s/step=%s ma trovato seed=%s/step=%s (requestId=%s)"
                            .formatted(label, seed0, step0.name(), badSeed, badStep.name(), bad.getRequestId())
            );
        }

        return new SeedStep(seed0, step0.name());
    }

}
