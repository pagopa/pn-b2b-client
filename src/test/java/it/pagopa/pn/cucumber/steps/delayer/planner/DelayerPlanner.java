package it.pagopa.pn.cucumber.steps.delayer.planner;

import it.pagopa.pn.cucumber.steps.delayer.DelayerStepsOld;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerContext;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerPaperDelivery;
import it.pagopa.pn.cucumber.steps.delayer.model.enums.WorkflowSteps;
import it.pagopa.pn.cucumber.steps.delayer.utils.DelayerPaperDeliveryUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static it.pagopa.pn.cucumber.steps.delayer.utils.DelayerPaperDeliveryUtils.*;

public class DelayerPlanner {

    private final DelayerContext context;
    private final DelayerPaperDeliveryUtils utils;

    public DelayerPlanner(DelayerContext context) {
        this.context = context;
        this.utils = new DelayerPaperDeliveryUtils(context);
    }

    public Map<String, List<DelayerPaperDelivery>> simulateAlgorithm(WorkflowSteps endAt, String seed) {
        Map<String, List<DelayerPaperDelivery>> groupedByStep = initWorkflowMap();
        Map<String, List<DelayerPaperDelivery>> frozenByStep = initWorkflowMap();

        List<DelayerPaperDelivery> notifications = new ArrayList<>(context.groupedBySeed.get(seed));
        if (notifications.isEmpty()) {
            throw new RuntimeException("Nessuna notifica trovata per il seed " + seed);
        }

        // Step 1: Sender Limit
        var pairResult = applySenderLimit(notifications, groupedByStep, frozenByStep);
        List<DelayerPaperDelivery> toDriverCapacity = pairResult.getLeft();
        List<DelayerPaperDelivery> toResidualCapacity = pairResult.getRight();
        if (endAt == WorkflowSteps.EVALUATE_SENDER_LIMIT) return finalizeResult(groupedByStep, frozenByStep);

        // Step 2a: Residual Capacity 
        List<DelayerPaperDelivery> residualNotifications = applyResidualCapacity(toResidualCapacity, groupedByStep, frozenByStep);
        if (endAt == WorkflowSteps.EVALUATE_RESIDUAL_CAPACITY) return finalizeResult(groupedByStep, frozenByStep);

        // Step 2b: Driver Capacity
        List<DelayerPaperDelivery> postDriverCapacity = applyDriverCapacity(toDriverCapacity, groupedByStep, frozenByStep);
        List<DelayerPaperDelivery> processedResidues = applyDriverCapacity(residualNotifications, groupedByStep, frozenByStep);
        List<DelayerPaperDelivery> toPrintCapacity = new ArrayList<>(postDriverCapacity);
        toPrintCapacity.addAll(processedResidues);
        if (endAt == WorkflowSteps.EVALUATE_DRIVER_CAPACITY) return finalizeResult(groupedByStep, frozenByStep);

        // Step 3: Print Capacity
        List<DelayerPaperDelivery> toPreparePhase2 = applyPrintCapacity(toPrintCapacity, groupedByStep, frozenByStep);
        if (endAt == WorkflowSteps.EVALUATE_PRINT_CAPACITY) return finalizeResult(groupedByStep, frozenByStep);

        // Step 4: Sent to Prepare Phase 2
        groupedByStep.get(WorkflowSteps.SENT_TO_PREPARE_PHASE_2.name())
                .addAll(deepCopyAndUpdateKeys(toPreparePhase2, WorkflowSteps.SENT_TO_PREPARE_PHASE_2, context.expectedDeliveryDate));

        groupedByStep.put("FROZEN", collectAllFrozen(frozenByStep));
        return groupedByStep;
    }

    private Pair<List<DelayerPaperDelivery>, List<DelayerPaperDelivery>> applySenderLimit(List<DelayerPaperDelivery> notifications, Map<String, List<DelayerPaperDelivery>> groupedByStep, Map<String, List<DelayerPaperDelivery>> frozenByStep) {

        List<DelayerPaperDelivery> postSenderLimit = new ArrayList<>();
        List<DelayerPaperDelivery> toResidualCapacity = new ArrayList<>();

        // 1. Tutto parte in EVALUTE_SENDER_LIMIT
        groupedByStep.get(WorkflowSteps.EVALUATE_SENDER_LIMIT.name())
                .addAll(deepCopyAndUpdateKeys(notifications, WorkflowSteps.EVALUATE_SENDER_LIMIT, context.expectedDeliveryDate));

        // 2. Separa RS e secondi tentativi
        List<DelayerPaperDelivery> rsOrSecondAttempt = notifications.stream()
                .filter(n -> n.isRS() || n.isSecondAttempt())
                .toList();

        List<DelayerPaperDelivery> toEvaluateNormally = notifications.stream()
                .filter(n -> !(n.isRS() || n.isSecondAttempt()))
                .toList();

        // 3. RS e secondi tentativi vanno direttamente allo step DRIVER_CAPACITY
        postSenderLimit.addAll(deepCopyAndUpdateKeys(rsOrSecondAttempt, WorkflowSteps.EVALUATE_DRIVER_CAPACITY, context.expectedDeliveryDate));

        //4. Gli 890 vengono processati per mittente censito e non
        Map<String, List<DelayerPaperDelivery>> bySenderKey = groupBySender(toEvaluateNormally);

        for (Map.Entry<String, List<DelayerPaperDelivery>> entry : bySenderKey.entrySet()) {
            List<DelayerPaperDelivery> sorted = sortByPriority(entry.getValue());

            if (utils.isMittenteCensito(entry.getKey())) {
                int senderLimit = utils.getSenderLimit(entry);
                postSenderLimit.addAll(deepCopyAndUpdateKeys(sorted.stream().limit(senderLimit).toList(), WorkflowSteps.EVALUATE_DRIVER_CAPACITY, context.expectedDeliveryDate));
                toResidualCapacity.addAll(deepCopyAndUpdateKeys(sorted.stream().skip(senderLimit).toList(), WorkflowSteps.EVALUATE_RESIDUAL_CAPACITY, context.expectedDeliveryDate));
            } else {
                toResidualCapacity.addAll(deepCopyAndUpdateKeys(sorted, WorkflowSteps.EVALUATE_RESIDUAL_CAPACITY, context.expectedDeliveryDate));
            }
        }

        return Pair.of(postSenderLimit, toResidualCapacity);
    }

    private List<DelayerPaperDelivery> applyDriverCapacity(List<DelayerPaperDelivery> toEvaluate, Map<String, List<DelayerPaperDelivery>> groupedByStep, Map<String, List<DelayerPaperDelivery>> frozenByStep) {

        // 1. Raggruppa per driver
        Map<String, List<DelayerPaperDelivery>> byDriverKey = groupByDriver(toEvaluate);
        Map<String, Integer> driverResidualCapacity = new HashMap<>();

        // 2. Calcola capacità residua per ogni driver (inizialmente tutta disponibile)
        for (String driverKey : byDriverKey.keySet()) {
            int capacity = utils.getDriverCapacity(driverKey);
            driverResidualCapacity.put(driverKey, capacity);
        }

        List<DelayerPaperDelivery> assigned = new ArrayList<>();
        List<DelayerPaperDelivery> toFreeze = new ArrayList<>();

        // 3. Processa PRIMA i mittenti censiti
        List<DelayerPaperDelivery> censiti = toEvaluate.stream()
                .filter(n -> utils.isMittenteCensito(getSenderKey(n)))
                .toList();

        for (DelayerPaperDelivery n : sortByPriority(censiti)) {
            assigned.add(n);
            String driverKey = getDriverKey(n);
            int remaining = driverResidualCapacity.getOrDefault(driverKey, 0);
            driverResidualCapacity.put(driverKey, Math.max(0, remaining - 1));
        }

        // 4. Poi processa i mittenti NON censiti
        List<DelayerPaperDelivery> nonCensiti = toEvaluate.stream()
                .filter(n -> !utils.isMittenteCensito(getSenderKey(n)))
                .toList();

        for (DelayerPaperDelivery n : sortByPriority(nonCensiti)) {
            String driverKey = getDriverKey(n);
            int available = driverResidualCapacity.getOrDefault(driverKey, 0);

            if (available > 0) {
                assigned.add(n);
                driverResidualCapacity.put(driverKey, available - 1);
            } else {
                toFreeze.add(n);
            }
        }

        // 5. Aggiorna grouped e frozen con le notifiche elaborate
        groupedByStep.get(WorkflowSteps.EVALUATE_DRIVER_CAPACITY.name())
                .addAll(deepCopyAndUpdateKeys(assigned, WorkflowSteps.EVALUATE_DRIVER_CAPACITY, context.expectedDeliveryDate));

        freezeNotifications(toFreeze, WorkflowSteps.EVALUATE_DRIVER_CAPACITY, frozenByStep);

        return assigned;
    }

    private List<DelayerPaperDelivery> applyPrintCapacity(List<DelayerPaperDelivery> input, Map<String, List<DelayerPaperDelivery>> groupedByStep, Map<String, List<DelayerPaperDelivery>> frozenByStep) {

        List<DelayerPaperDelivery> ordered = sortByPriority(input);

        int effectiveLimit = Math.min(context.printCapacity, ordered.size());
        List<DelayerPaperDelivery> accepted = ordered.subList(0, effectiveLimit);
        List<DelayerPaperDelivery> frozen = ordered.subList(effectiveLimit, ordered.size());

        groupedByStep.get(WorkflowSteps.EVALUATE_PRINT_CAPACITY.name())
                .addAll(deepCopyAndUpdateKeys(accepted, WorkflowSteps.EVALUATE_PRINT_CAPACITY, context.expectedDeliveryDate));

        freezeNotifications(frozen, WorkflowSteps.EVALUATE_PRINT_CAPACITY, frozenByStep);
        return accepted;
    }

    private List<DelayerPaperDelivery> deepCopyAndUpdateKeys(List<DelayerPaperDelivery> source, WorkflowSteps step, String deliveryDate) {
        return source.stream()
                .map(DelayerPaperDelivery::new)
                .peek(n -> {
                    n.setPk(utils.calculateNotificationPk(step, deliveryDate));
                    n.setSk(utils.calculateNotificationSk(step, n));
                    n.setPriority(utils.calculatePriority(n));
                })
                .toList();
    }

    private List<DelayerPaperDelivery> applyResidualCapacity(List<DelayerPaperDelivery> toResidualCapacity, Map<String, List<DelayerPaperDelivery>> groupedByStep, Map<String, List<DelayerPaperDelivery>> frozenByStep) {
        groupedByStep.get(WorkflowSteps.EVALUATE_RESIDUAL_CAPACITY.name())
                .addAll(deepCopyAndUpdateKeys(toResidualCapacity, WorkflowSteps.EVALUATE_RESIDUAL_CAPACITY, context.expectedDeliveryDate));

        return toResidualCapacity;
    }

    private Map<String, List<DelayerPaperDelivery>> finalizeResult(Map<String, List<DelayerPaperDelivery>> groupedByStep, Map<String, List<DelayerPaperDelivery>> frozenByStep) {
        groupedByStep.put("FROZEN", collectAllFrozen(frozenByStep));
        return groupedByStep;
    }

    private List<DelayerPaperDelivery> collectAllFrozen(Map<String, List<DelayerPaperDelivery>> frozenByStep) {
        return frozenByStep.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private Map<String, List<DelayerPaperDelivery>> initWorkflowMap() {
        Map<String, List<DelayerPaperDelivery>> map = new HashMap<>();
        for (WorkflowSteps step : WorkflowSteps.values()) {
            map.put(step.name(), new ArrayList<>());
        }
        return map;
    }

    private void freezeNotifications(List<DelayerPaperDelivery> list, WorkflowSteps step, Map<String, List<DelayerPaperDelivery>> frozenByStep) {
        String deliveryDate = getNextMonday();
        frozenByStep.get(step.name()).addAll(deepCopyAndUpdateKeys(list, WorkflowSteps.EVALUATE_SENDER_LIMIT, deliveryDate));
    }

    public List<DelayerPaperDelivery> getExpectedNotification(String workflowStep) {
        List<DelayerPaperDelivery> expected = new ArrayList<>();

        context.expectedPianification.forEach((seed, pianification) -> {
            if (!context.failPianification.containsKey(seed)) expected.addAll(pianification.get(workflowStep));
        });

        return expected;
    }
}
