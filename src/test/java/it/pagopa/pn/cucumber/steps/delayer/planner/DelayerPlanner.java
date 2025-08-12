package it.pagopa.pn.cucumber.steps.delayer.planner;

import it.pagopa.pn.cucumber.steps.delayer.model.DelayerContext;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerPaperDelivery;
import it.pagopa.pn.cucumber.steps.delayer.model.enums.WorkflowSteps;
import it.pagopa.pn.cucumber.steps.delayer.utils.DelayerPaperDeliveryUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
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
//        groupedByStep.get(WorkflowSteps.SENT_TO_PREPARE_PHASE_2.name())
//                .addAll(utils.deepCopyAndUpdateKeys(sortByPriority(toPreparePhase2).stream().limit(this.context.printCapacity).toList(), WorkflowSteps.SENT_TO_PREPARE_PHASE_2, context.expectedDeliveryDate));

        groupedByStep.put("FROZEN", collectAllFrozen(frozenByStep));
        return groupedByStep;
    }

    public void simulateAlgorithm2(Map<String, Map<String, List<DelayerPaperDelivery>>> fromAlgorithm1) {
        // 1) prendi tutti quelli bloccati su EVALUATE_PRINT_CAPACITY
        List<DelayerPaperDelivery> allForStep = fromAlgorithm1.values().stream()
                .flatMap(m -> m.getOrDefault(WorkflowSteps.EVALUATE_PRINT_CAPACITY.name(), List.of()).stream())
                .collect(Collectors.toCollection(ArrayList::new));

        if (allForStep.isEmpty()) {
            return;
        }

        // 2) ordina in-place per priorità
        List<DelayerPaperDelivery> sorted = sortByPriority(allForStep);

        // 3) prendi le prime N (capacity)
        int n = Math.max(0, Math.min(context.printCapacity, sorted.size()));
        List<DelayerPaperDelivery> inPreparePhase2 = new ArrayList<>(sorted.subList(0, n));

        // 4) sposta le prime N nello step SENT_TO_PREPARE_PHASE_2 dentro expectedPianification
        for (DelayerPaperDelivery notification : inPreparePhase2) {
            String seed = extractSeed(notification);

            // mappa interna per il seed, creata se assente
            Map<String, List<DelayerPaperDelivery>> seedMap =
                    context.expectedPianification.computeIfAbsent(seed, k -> new HashMap<>());

            // lista base per lo step, può essere null o immutabile → normalizzo
            List<DelayerPaperDelivery> baseList =
                    seedMap.getOrDefault(WorkflowSteps.SENT_TO_PREPARE_PHASE_2.name(), Collections.emptyList());

            // deep copy + update delle chiavi sulla lista base (può ritornare immutabile) → wrappo in ArrayList
            List<DelayerPaperDelivery> expected = new ArrayList<>(
                    Optional.ofNullable(
                            utils.deepCopyAndUpdateKeys(
                                    baseList,
                                    WorkflowSteps.SENT_TO_PREPARE_PHASE_2,
                                    this.context.expectedDeliveryDate
                            )
                    ).orElseGet(Collections::emptyList)
            );

            // IMPORTANTISSIMO: aggiungo la singola notifica con step/data aggiornati
            // (se non hai un overload per il singolo elemento, usa List.of(...) e poi addAll)
            List<DelayerPaperDelivery> updatedSingle = utils.deepCopyAndUpdateKeys(
                    List.of(notification),
                    WorkflowSteps.SENT_TO_PREPARE_PHASE_2,
                    this.context.expectedDeliveryDate
            );
            expected.addAll(updatedSingle);

            // rimetti la lista (MUTABILE) nella mappa
            seedMap.put(WorkflowSteps.SENT_TO_PREPARE_PHASE_2.name(), expected);
        }
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
        frozenByStep.get(WorkflowSteps.EVALUATE_DRIVER_CAPACITY.name()).addAll(toFreeze);

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

        Map<String, List<DelayerPaperDelivery>> byDriver = utils.groupByUnifiedDeliveryDriver(postDriverCapacity);
        List<DelayerPaperDelivery> toEvaluatePrintCapacity = new ArrayList<>();
        List<DelayerPaperDelivery> toFreeze = frozenByStep.get(WorkflowSteps.EVALUATE_DRIVER_CAPACITY.name());

        for (Map.Entry<String, List<DelayerPaperDelivery>> entry : byDriver.entrySet()) {
            List<DelayerPaperDelivery> notifications = entry.getValue();

            if (notifications == null || notifications.isEmpty()) {
                continue;
            }

            Map<String, Integer> localCapacityMap = new HashMap<>();


            notifications.forEach(notification -> {
                String capDriverkey = getCapDeliveryDriverKey(notification);
                int driverCapacity = Math.max(0, utils.getInitalDriverCapacity(capDriverkey));
                localCapacityMap.putIfAbsent(capDriverkey, driverCapacity);

                if (localCapacityMap.get(capDriverkey) > 0) {
                    toEvaluatePrintCapacity.add(notification);
                    localCapacityMap.put(capDriverkey, Math.max(0, localCapacityMap.get(capDriverkey) - 1));

                    toFreeze.remove(notification);
                } else {
                    if(!toFreeze.contains(notification)){toFreeze.add(notification);}
                }
            });
        }


        groupedByStep.get(WorkflowSteps.EVALUATE_PRINT_CAPACITY.name())
                .addAll(utils.deepCopyAndUpdateKeys(sortByPriority(toEvaluatePrintCapacity), WorkflowSteps.EVALUATE_PRINT_CAPACITY, context.expectedDeliveryDate));

        return toEvaluatePrintCapacity;
    }

    private Map<String, List<DelayerPaperDelivery>> finalizeResult(Map<String, List<DelayerPaperDelivery>> groupedByStep, Map<String, List<DelayerPaperDelivery>> frozenByStep) {
        groupedByStep.put("FROZEN", collectAllFrozen(frozenByStep));
        return groupedByStep;
    }

    private List<DelayerPaperDelivery> collectAllFrozen(Map<String, List<DelayerPaperDelivery>> frozenByStep) {
        String deliveryDate = getNextMonday();

        List<DelayerPaperDelivery> toFreeze = frozenByStep.values().stream()
                .flatMap(List::stream)
                .toList();

        return utils.deepCopyAndUpdateKeys(toFreeze, WorkflowSteps.EVALUATE_SENDER_LIMIT, deliveryDate);
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
