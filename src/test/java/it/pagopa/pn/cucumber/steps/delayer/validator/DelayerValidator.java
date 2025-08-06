package it.pagopa.pn.cucumber.steps.delayer.validator;

import it.pagopa.pn.cucumber.steps.delayer.model.DelayerContext;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerPaperDelivery;
import it.pagopa.pn.cucumber.steps.delayer.client.DelayerLambdaClient;

import io.cucumber.datatable.DataTable;
import it.pagopa.pn.cucumber.steps.delayer.model.enums.WorkflowSteps;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DelayerValidator {

    private final DelayerContext context;
    private final DelayerLambdaClient lambdaClient;

    public DelayerValidator(DelayerContext context, DelayerLambdaClient lambdaClient) {
        this.context = context;
        this.lambdaClient = lambdaClient;
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
            throw new IllegalStateException("Tutti i seed hanno fallito allo step: " + step);
        }

        boolean almenoUnoOk = false;

        for (String seed : validSeeds) {
            List<DelayerPaperDelivery> expected = context.expectedPianification.get(seed).get(step.name());
            List<DelayerPaperDelivery> actual = context.actualPianification.get(seed).get(step.name());

            if (expected == null || actual == null || expected.isEmpty() || actual.isEmpty()) {
                registerFailureIfAbsent(seed, "Ranking mancante per step: " + step);
                continue;
            }

            List<Map<String, String>> expectedRank = toComparableMapList(expected, step);
            List<Map<String, String>> actualRank = toComparableMapList(actual, step);

            try {
                Assertions.assertEquals(expectedRank, actualRank);
                almenoUnoOk = true;
            } catch (AssertionError e) {
                registerFailureIfAbsent(seed, "Ordine notifiche errato: " + e.getMessage());
            }
        }

        if (!almenoUnoOk) {
            assertPianifications();
        }
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
                .collect(Collectors.groupingBy(this::extractSeed));

        Set<String> validSeeds = bySeed.keySet().stream()
                .filter(seed -> !context.failPianification.containsKey(seed))
                .collect(Collectors.toSet());

        if (validSeeds.isEmpty()) {
            throw new IllegalStateException("Nessun seed valido per verifica capacità");
        }

        for (String seed : validSeeds) {
            List<DelayerPaperDelivery> deliveries = bySeed.get(seed);

            Set<String> tripleKeys = deliveries.stream()
                    .map(n -> n.getUnifiedDeliveryDriver() + "~" + n.getProvince() + "~" + extractDeliveryDate(n))
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

    private String extractSeed(DelayerPaperDelivery d) {
        return context.groupedBySeed.keySet().stream()
                .filter(d.getRequestId()::contains)
                .findFirst()
                .orElse("UNKNOWN");
    }

    private String extractDeliveryDate(DelayerPaperDelivery n) {
        String[] parts = n.getPk().split("~");
        return (parts.length > 0) ? parts[0] : "";
    }

    private String getNextMonday() {
        return LocalDate.now()
                .with(java.time.temporal.TemporalAdjusters.next(java.time.DayOfWeek.MONDAY))
                .format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
