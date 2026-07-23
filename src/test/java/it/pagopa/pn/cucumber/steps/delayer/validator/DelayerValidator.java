package it.pagopa.pn.cucumber.steps.delayer.validator;

import io.cucumber.datatable.DataTable;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerContext;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerPaperDelivery;
import it.pagopa.pn.cucumber.steps.delayer.model.enums.WorkflowSteps;
import it.pagopa.pn.cucumber.steps.delayer.service.DelayerSevice;
import it.pagopa.pn.cucumber.steps.delayer.utils.DelayerPaperDeliveryUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static it.pagopa.pn.cucumber.steps.delayer.utils.DelayerPaperDeliveryUtils.getNextMondayFromDate;

@Slf4j
@Service
@ScenarioScope
@RequiredArgsConstructor
public class DelayerValidator {

    public static final int FROZEN_POLLING_MAX_MINUTES = 45;
    private final DelayerContext context;
    private final DelayerSevice service;
    private final DelayerPaperDeliveryUtils utils;

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
            assertPianifications();
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
        var actual = step.equals(WorkflowSteps.SENT_TO_PREPARE_PHASE_2) && context.assertPhase2ByExecutionCounter ? context.getActualInPhase2() : context.actualPianification.getOrDefault(seed, Map.of()).get(step.name());
        var expected = step.equals(WorkflowSteps.SENT_TO_PREPARE_PHASE_2) && context.assertPhase2ByExecutionCounter ? context.getExpectedInPhase2() : context.expectedPianification.getOrDefault(seed, Map.of()).get(step.name());

        // Normalizza i null a liste vuote
        List<DelayerPaperDelivery> exp = expected == null ? List.of() : expected;
        List<DelayerPaperDelivery> act = actual == null ? List.of() : actual;

        // Entrambi vuoti -> ok
        if (exp.isEmpty() && act.isEmpty()) {
            return List.of();
        }

        // Confronto campo-per-campo (actual ⊆ expected), usando toComparableMapList interno
        List<String> problems = utils.compare(exp, act);

        if (!problems.isEmpty()) {
            registerFailureIfAbsent(
                    seed,
                    "Verifica actual⊆expected fallita allo step '%s'. Dettagli: %s"
                            .formatted(step, problems)
            );
        }

        return problems;
    }

    public void checkSilentlyAll(WorkflowSteps step) {
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

    }

    public void checkNotExistSilently(List<DelayerPaperDelivery> notifications, String seed, WorkflowSteps ws) {
        if (notifications != null && !notifications.isEmpty()) {
            registerFailureIfAbsent(
                    seed,
                    "Trovate notifiche non attese allo stato " + ws.name() + " :" + notifications.stream()
                            .map(DelayerPaperDelivery::getRequestId)
                            .collect(Collectors.joining(", "))
            );
        }
    }

    public void checkFrozen(WorkflowSteps step, List<DelayerPaperDelivery> frozenExpected) throws Exception {
        Set<String> validSeeds = context.groupedBySeed.keySet().stream()
                .filter(seed -> !context.failPianification.containsKey(seed))
                .collect(Collectors.toSet());

        if (validSeeds.isEmpty()) {
            assertPianifications();
        }

        List<DelayerPaperDelivery> actualFrozen = service.findByWorkflowStep(
                frozenExpected.stream().map(DelayerPaperDelivery::getRequestId).collect(Collectors.toSet()),
                step.name(),
                getNextMondayFromDate(context.expectedDeliveryDate, 1),
                FROZEN_POLLING_MAX_MINUTES
        );

        if (frozenExpected.size() != actualFrozen.size()) {
            validSeeds.forEach(seed -> registerFailureIfAbsent(seed,
                    "Mismatch congelati - attesi: %d, trovati: %d".formatted(frozenExpected.size(), actualFrozen.size())));
            return;
        }

        frozenExpected.forEach(expectedDelivery ->
                actualFrozen.stream()
                        .filter(actualDelivery -> actualDelivery.getRequestId().equals(expectedDelivery.getRequestId()))
                        .findFirst()
                        .ifPresent(actualDelivery ->
                                expectedDelivery.setVirtualNotificationSentAt(actualDelivery.getVirtualNotificationSentAt()))
        );

        frozenExpected.forEach(expectedDelivery ->
                expectedDelivery.setSk(utils.calculateSk(WorkflowSteps.EVALUATE_SENDER_LIMIT, expectedDelivery))
        );

        Set<Map<String, String>> expectedSet = toComparableMapList(frozenExpected, step).stream().collect(Collectors.toSet());
        Set<Map<String, String>> actualSet = toComparableMapList(actualFrozen, step).stream().collect(Collectors.toSet());

        Set<Map<String, String>> missing = new HashSet<>(expectedSet);
        missing.removeAll(actualSet);

        if (!missing.isEmpty()) {
            validSeeds.forEach(seed ->
                    registerFailureIfAbsent(seed, "Differenze tra congelati attesi e trovati: " + missing));
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

}
