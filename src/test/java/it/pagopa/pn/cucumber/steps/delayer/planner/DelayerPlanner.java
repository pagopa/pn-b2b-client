package it.pagopa.pn.cucumber.steps.delayer.planner;

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
import java.util.stream.Stream;

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
        List<DelayerPaperDelivery> sortedNotifications;
        if (notifications.isEmpty()) {
            throw new RuntimeException("Nessuna notifica trovata per il seed " + seed);
        }
        sortedNotifications = sortByPriority(notifications);


        // Step 0: reserve capacity
        // Per ogni driver devo riservare capacità per RS e secondi tentativi

        // Step 1: Sender Limit
        var pairResult = applySenderLimit(sortedNotifications, groupedByStep, frozenByStep);
        List<DelayerPaperDelivery> toEvaluateDriverCapacity = pairResult.getLeft();
        List<DelayerPaperDelivery> toEvaluateResidualCapacity = pairResult.getRight();
        if (endAt == WorkflowSteps.EVALUATE_SENDER_LIMIT) return finalizeResult(groupedByStep, frozenByStep);

        // Step 2: Driver Capacity
        List<DelayerPaperDelivery> toEvaluatePrintCapacity = applyDriverCapacity(toEvaluateDriverCapacity, toEvaluateResidualCapacity, groupedByStep, frozenByStep);
        if (endAt == WorkflowSteps.EVALUATE_DRIVER_CAPACITY) return finalizeResult(groupedByStep, frozenByStep);

        // Step 3: Print Capacity
        List<DelayerPaperDelivery> toPreparePhase2 = applyPrintCapacity(toEvaluatePrintCapacity, groupedByStep, frozenByStep);
        if (endAt == WorkflowSteps.EVALUATE_PRINT_CAPACITY) return finalizeResult(groupedByStep, frozenByStep);

        // Step 4: Sent to Prepare Phase 2
        groupedByStep.get(WorkflowSteps.SENT_TO_PREPARE_PHASE_2.name())
                .addAll(utils.deepCopyAndUpdateKeys(sortByPriority(toPreparePhase2).stream().limit(this.context.printCapacity).toList(), WorkflowSteps.SENT_TO_PREPARE_PHASE_2, context.expectedDeliveryDate));

        groupedByStep.put("FROZEN", collectAllFrozen(frozenByStep));
        return groupedByStep;
    }

    private Pair<List<DelayerPaperDelivery>, List<DelayerPaperDelivery>> applySenderLimit(List<DelayerPaperDelivery> notifications, Map<String, List<DelayerPaperDelivery>> groupedByStep, Map<String, List<DelayerPaperDelivery>> frozenByStep) {

        List<DelayerPaperDelivery> passedSenderLimit = new ArrayList<>();
        List<DelayerPaperDelivery> notPassedSenderLimit = new ArrayList<>();

        // 1. Inserisco le notifiche che verranno elaborate in questo step
        groupedByStep.get(WorkflowSteps.EVALUATE_SENDER_LIMIT.name())
                .addAll(utils.deepCopyAndUpdateKeys(notifications, WorkflowSteps.EVALUATE_SENDER_LIMIT, context.expectedDeliveryDate));

        // 2. Separa RS e secondi tentativi
        List<DelayerPaperDelivery> rsOrSecondAttempt = notifications.stream()
                .filter(n -> n.isRS() || n.isSecondAttempt())
                .toList();

        List<DelayerPaperDelivery> toEvaluateNormally = notifications.stream()
                .filter(n -> !(n.isRS() || n.isSecondAttempt()))
                .toList();

        // 3. RS e secondi tentativi vanno direttamente alla valutazione successiva
        passedSenderLimit.addAll(utils.deepCopyAndUpdateKeys(rsOrSecondAttempt, WorkflowSteps.EVALUATE_DRIVER_CAPACITY, context.expectedDeliveryDate));

        //4. Gli 890 vengono processati per mittente censito e non
        toEvaluateNormally = sortByPriority(toEvaluateNormally);

        for(DelayerPaperDelivery notification : toEvaluateNormally) {
            String senderKey = getSenderKey(notification);

            if (utils.isMittenteCensito(senderKey)) {
                int senderLimit = utils.getSenderLimit(senderKey);
                passedSenderLimit.addAll(utils.deepCopyAndUpdateKeys(Stream.of(notification).limit(senderLimit).toList(), WorkflowSteps.EVALUATE_DRIVER_CAPACITY, context.expectedDeliveryDate));
                notPassedSenderLimit.addAll(utils.deepCopyAndUpdateKeys(Stream.of(notification).skip(senderLimit).toList(), WorkflowSteps.EVALUATE_RESIDUAL_CAPACITY, context.expectedDeliveryDate));
                utils.setSenderLimit(senderKey, Math.max(0, senderLimit - 1));
            } else {
                notPassedSenderLimit.addAll(utils.deepCopyAndUpdateKeys(Stream.of(notification).toList(), WorkflowSteps.EVALUATE_RESIDUAL_CAPACITY, context.expectedDeliveryDate));
            }
        }

        return Pair.of(sortByPriority(passedSenderLimit), sortByPriority(notPassedSenderLimit));
    }

    private List<DelayerPaperDelivery> applyDriverCapacity(List<DelayerPaperDelivery> passedSenderLimit, List<DelayerPaperDelivery> notPassedSenderLimit, Map<String, List<DelayerPaperDelivery>> groupedByStep, Map<String, List<DelayerPaperDelivery>> frozenByStep) {

        // 1. Processa PRIMA le notifiche in passedSenderLimit
        List<DelayerPaperDelivery> toEvaluateDriverCapacity = new ArrayList<>();
        List<DelayerPaperDelivery> toEvaluateResidualCapacity = new ArrayList<>();
        passedSenderLimit = sortByPriority(passedSenderLimit);

        for(DelayerPaperDelivery notification : passedSenderLimit) {
            String unifiedDeliveryDriverKey = getUnifiedDeliveryDriverKey(notification);
            String capDeliveryDriverKey = getCapDeliveryDriverKey(notification);

            int remainingProvincial = utils.getAvailableDriverCapacity(unifiedDeliveryDriverKey);
            int remainingCap = utils.getAvailableDriverCapacity(capDeliveryDriverKey);

            if((notification.isRS() || notification.isSecondAttempt()) || (remainingProvincial > 0 && remainingCap > 0)) {
                toEvaluateDriverCapacity.add(notification);
                utils.setAvailableDriverCapacity(capDeliveryDriverKey, Math.max(0, remainingCap - 1));
            } else {
                toEvaluateResidualCapacity.add(notification);
            }
        }

        // 2. Processa DOPO le notifiche in notPassedSenderLimit
        List<DelayerPaperDelivery> toFreeze = new ArrayList<>();
        toEvaluateResidualCapacity.addAll(notPassedSenderLimit);
        toEvaluateResidualCapacity = sortByPriority(toEvaluateResidualCapacity);

        for (DelayerPaperDelivery notification : new ArrayList<>(toEvaluateResidualCapacity)) {
            String unifiedDeliveryDriverKey = getUnifiedDeliveryDriverKey(notification);
            String capDeliveryDriverKey     = getCapDeliveryDriverKey(notification);

            int remainingProvincial = utils.getAvailableDriverCapacity(unifiedDeliveryDriverKey);
            int remainingCap        = utils.getAvailableDriverCapacity(capDeliveryDriverKey);

            if (remainingProvincial > 0 && remainingCap > 0) {
                if(utils.isMittenteCensito(getSenderKey(notification))){
                    toEvaluateDriverCapacity.add(notification);
                    toEvaluateResidualCapacity.remove(notification);
                    utils.setAvailableDriverCapacity(unifiedDeliveryDriverKey, Math.max(0, remainingProvincial - 1));
                    utils.setAvailableDriverCapacity(capDeliveryDriverKey,     Math.max(0, remainingCap - 1));
                }
            } else {
                toFreeze.add(notification);
            }
        }

        // 3. Congela le notifiche non elaborate
        freezeNotifications(toFreeze, WorkflowSteps.EVALUATE_DRIVER_CAPACITY, frozenByStep);

        // 4. Inserisce le notifiche negli step
        groupedByStep.get(WorkflowSteps.EVALUATE_DRIVER_CAPACITY.name())
                .addAll(utils.deepCopyAndUpdateKeys(sortByPriority(toEvaluateDriverCapacity), WorkflowSteps.EVALUATE_DRIVER_CAPACITY, context.expectedDeliveryDate));

        groupedByStep.get(WorkflowSteps.EVALUATE_RESIDUAL_CAPACITY.name())
                .addAll(utils.deepCopyAndUpdateKeys(sortByPriority(toEvaluateResidualCapacity), WorkflowSteps.EVALUATE_RESIDUAL_CAPACITY, context.expectedDeliveryDate));

        // 5. Restituisce le notifiche da elaborare nel prossimo step
        List<DelayerPaperDelivery> postDriverCapacity = new ArrayList<>();
        postDriverCapacity.addAll(toEvaluateDriverCapacity);
        postDriverCapacity.addAll(toEvaluateResidualCapacity);

        return sortByPriority(postDriverCapacity);
    }

    private List<DelayerPaperDelivery> applyPrintCapacity(List<DelayerPaperDelivery> postDriverCapacity, Map<String, List<DelayerPaperDelivery>> groupedByStep, Map<String, List<DelayerPaperDelivery>> frozenByStep) {

        Map<String, List<DelayerPaperDelivery>> byDriver = utils.groupByDriver(postDriverCapacity);
        List<DelayerPaperDelivery> toEvaluatePrintCapacity = new ArrayList<>();

        for (Map.Entry<String, List<DelayerPaperDelivery>> entry : byDriver.entrySet()) {
            String driverKey = entry.getKey();
            List<DelayerPaperDelivery> notifications = entry.getValue();

            if (notifications == null || notifications.isEmpty()) {
                continue;
            }

            int driverCapacity = Math.max(0, utils.getInitalDriverCapacity(driverKey));
            if (driverCapacity == 0) {
                continue;
            }

            // Prendi le prime N per priorità
            List<DelayerPaperDelivery> topN = sortByPriority(notifications)
                    .stream()
                    .limit(driverCapacity)
                    .toList();

            toEvaluatePrintCapacity.addAll(topN);
        }

        groupedByStep.get(WorkflowSteps.EVALUATE_PRINT_CAPACITY.name())
                .addAll(utils.deepCopyAndUpdateKeys(sortByPriority(toEvaluatePrintCapacity), WorkflowSteps.EVALUATE_PRINT_CAPACITY, context.expectedDeliveryDate));

        return toEvaluatePrintCapacity;
    }

    private List<DelayerPaperDelivery> applyToSentPhase2(List<DelayerPaperDelivery> passedDriverCapacity, Map<String, List<DelayerPaperDelivery>> groupedByStep, Map<String, List<DelayerPaperDelivery>> frozenByStep) {

        // 1. Inserisco le notifiche che verranno elaborate in questo step
        List<DelayerPaperDelivery> toEvaluate = sortByPriority(passedDriverCapacity);

        // Se utilizzassi come limite 180_000 andrei in errore sulla size dell'array
        int effectiveLimit = Math.min(context.printCapacity, toEvaluate.size());
        List<DelayerPaperDelivery> toEvaluatePrintCapacity = toEvaluate.subList(0, effectiveLimit);
        List<DelayerPaperDelivery> frozen = toEvaluate.subList(effectiveLimit, toEvaluate.size());

        groupedByStep.get(WorkflowSteps.EVALUATE_PRINT_CAPACITY.name())
                .addAll(utils.deepCopyAndUpdateKeys(sortByPriority(toEvaluate), WorkflowSteps.EVALUATE_PRINT_CAPACITY, context.expectedDeliveryDate));

        // 3. Congela le notifiche in eccesso
        //freezeNotifications(frozen, WorkflowSteps.EVALUATE_PRINT_CAPACITY, frozenByStep);

        return toEvaluatePrintCapacity;
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
        frozenByStep.get(step.name()).addAll(utils.deepCopyAndUpdateKeys(list, WorkflowSteps.EVALUATE_SENDER_LIMIT, deliveryDate));
    }

}
