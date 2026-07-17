package it.pagopa.pn.cucumber.steps.delayer.planner;

import it.pagopa.pn.cucumber.steps.delayer.model.DelayerContext;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerPaperDelivery;
import it.pagopa.pn.cucumber.steps.delayer.model.enums.WorkflowSteps;
import it.pagopa.pn.cucumber.steps.delayer.utils.DelayerPaperDeliveryUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static it.pagopa.pn.cucumber.steps.delayer.utils.DelayerPaperDeliveryUtils.*;

@Component
@ScenarioScope
@RequiredArgsConstructor
public class DelayerPlanner {

    private final DelayerContext context;
    private final DelayerPaperDeliveryUtils utils;

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
        List<DelayerPaperDelivery> toEvaluateSenderPriority = pairResult.getLeft();
        List<DelayerPaperDelivery> toEvaluateResidualCapacity = pairResult.getRight();

        // Step 2: Sender Priority
        List<DelayerPaperDelivery> toEvaluateDriverCapacity = applySenderPriority(toEvaluateSenderPriority, groupedByStep);

        if (endAt == WorkflowSteps.EVALUATE_SENDER_LIMIT) {
            groupedByStep.put(
                    WorkflowSteps.EVALUATE_SENDER_LIMIT.name(),
                    utils.deepCopyAndUpdateKeys(
                            toEvaluateDriverCapacity,
                            WorkflowSteps.EVALUATE_SENDER_LIMIT,
                            context.expectedDeliveryDate
                    )
            );

            return finalizeResult(groupedByStep, frozenByStep);
        }

        if (endAt == WorkflowSteps.EVALUATE_SENDER_PRIORITY) return finalizeResult(groupedByStep, frozenByStep);

        // Step 3: Driver Capacity
        List<DelayerPaperDelivery> toEvaluatePrintCapacity = applyDriverCapacity(
                toEvaluateDriverCapacity,
                toEvaluateResidualCapacity,
                groupedByStep,
                frozenByStep
        );
        if (endAt == WorkflowSteps.EVALUATE_DRIVER_CAPACITY) return finalizeResult(groupedByStep, frozenByStep);

        // Step 4: Print Capacity
        applyPrintCapacity(toEvaluatePrintCapacity, groupedByStep, frozenByStep);
        if (endAt == WorkflowSteps.EVALUATE_PRINT_CAPACITY) return finalizeResult(groupedByStep, frozenByStep);

        return finalizeResult(groupedByStep, frozenByStep);
    }

    public void simulateAlgorithm2(Map<String, Map<String, List<DelayerPaperDelivery>>> fromAlgorithm1) {
        // 1) prendi tutti quelli bloccati su EVALUATE_PRINT_CAPACITY
        List<DelayerPaperDelivery> allForStep = fromAlgorithm1.values().stream()
                .flatMap(m -> m.getOrDefault(WorkflowSteps.EVALUATE_PRINT_CAPACITY.name(), List.of()).stream())
                .collect(Collectors.toCollection(ArrayList::new));

        if (allForStep.isEmpty()) {
            calculateEvaluateNextWeekInDelayerToPaperChannelStateMachine(fromAlgorithm1);
            return;
        }

        // 2) ordina in-place per priorità
        List<DelayerPaperDelivery> sorted = sortByPriority(allForStep);

        // 3) prendi le prime N (capacity)
        int n = Math.max(0, Math.min(context.weeklyPrintCapacity, sorted.size()));
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

        // 5) aggiorno le notifiche da valutare la settimana successiva
        calculateEvaluateNextWeekInDelayerToPaperChannelStateMachine(fromAlgorithm1);
    }

    private void calculateEvaluateNextWeekInDelayerToPaperChannelStateMachine(Map<String, Map<String, List<DelayerPaperDelivery>>> fromAlgorithm1) {

        fromAlgorithm1.forEach((seed, stepNotificationMap) -> {
            List<DelayerPaperDelivery> inPreparePhase2 = stepNotificationMap.getOrDefault(WorkflowSteps.SENT_TO_PREPARE_PHASE_2.name(), Collections.emptyList());
            List<DelayerPaperDelivery> inEvaluatePrintCapacity = stepNotificationMap.getOrDefault(WorkflowSteps.EVALUATE_PRINT_CAPACITY.name(), Collections.emptyList());

            List<DelayerPaperDelivery> currentFrozen = new ArrayList<>(
                    stepNotificationMap.getOrDefault("FROZEN", Collections.emptyList())
            );

            // Congela ciò che è in EVALUATE_PRINT_CAPACITY ma non in PREPARE_PHASE_2
            for (DelayerPaperDelivery n : inEvaluatePrintCapacity) {
                if (inPreparePhase2.stream().noneMatch(pf2 -> pf2.getRequestId().equals(n.getRequestId()))) {
                    currentFrozen.add(freezeNotification(n));
                }
            }

            stepNotificationMap.put("FROZEN", currentFrozen);
        });
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
                .filter(n -> !n.isInformalCommunication())
                .toList();

        List<DelayerPaperDelivery> toEvaluateNormally = notifications.stream()
                .filter(n -> !((n.isRS() && !n.isInformalCommunication()) || n.isSecondAttempt()))
                .toList();

        // 3. RS e secondi tentativi vanno direttamente alla valutazione successiva
        passedSenderLimit.addAll(utils.deepCopyAndUpdateKeys(rsOrSecondAttempt, WorkflowSteps.EVALUATE_SENDER_PRIORITY, context.expectedDeliveryDate));

        //4. Gli 890 e gli RS INFORMAL (comunicazioni bonarie) vengono processati per mittente censito e non
        toEvaluateNormally = sortByPriority(toEvaluateNormally);

        for (DelayerPaperDelivery notification : toEvaluateNormally) {
            String senderKey = getSenderKey(notification);

            if (utils.isMittenteCensito(senderKey)) {
                int senderLimit = utils.getSenderLimit(senderKey);
                passedSenderLimit.addAll(utils.deepCopyAndUpdateKeys(Stream.of(notification).limit(senderLimit).toList(), WorkflowSteps.EVALUATE_SENDER_PRIORITY, context.expectedDeliveryDate));
                notPassedSenderLimit.addAll(utils.deepCopyAndUpdateKeys(Stream.of(notification).skip(senderLimit).toList(), WorkflowSteps.EVALUATE_RESIDUAL_CAPACITY, context.expectedDeliveryDate));
                utils.setSenderLimit(senderKey, Math.max(0, senderLimit - 1));
            } else {
                notPassedSenderLimit.addAll(utils.deepCopyAndUpdateKeys(Stream.of(notification).toList(), WorkflowSteps.EVALUATE_RESIDUAL_CAPACITY, context.expectedDeliveryDate));
            }
        }

        return Pair.of(sortByPriority(passedSenderLimit), sortByPriority(notPassedSenderLimit));
    }

    private List<DelayerPaperDelivery> applyDriverCapacity(List<DelayerPaperDelivery> passedSenderLimit, List<DelayerPaperDelivery> notPassedSenderLimit, Map<String, List<DelayerPaperDelivery>> groupedByStep, Map<String, List<DelayerPaperDelivery>> frozenByStep) {

        List<DelayerPaperDelivery> toFreeze = new ArrayList<>();

        // 1. Processa PRIMA le notifiche in passedSenderLimit
        List<DelayerPaperDelivery> toEvaluateDriverCapacity = new ArrayList<>();
        List<DelayerPaperDelivery> toEvaluateResidualCapacity = new ArrayList<>();
        passedSenderLimit = sortByPriority(passedSenderLimit);

        for (DelayerPaperDelivery notification : passedSenderLimit) {
            String unifiedDeliveryDriverKey = getUnifiedDeliveryDriverKey(notification);
            String capDeliveryDriverKey = getCapDeliveryDriverKey(notification);

            if (!utils.isDriverCensito(unifiedDeliveryDriverKey)) {
                // Provincia non censita => driver non censito
                // In caso di provincia non censita le notifiche non devono essere congelate e ricaricate
                //toFreeze.add(notification);
                continue;
            }

            int remainingProvincial = utils.getAvailableDriverCapacity(unifiedDeliveryDriverKey);
            int remainingCap = utils.getAvailableDriverCapacity(capDeliveryDriverKey);

            if ((notification.isRS() || notification.isSecondAttempt()) || (remainingProvincial > 0 && remainingCap > 0)) {
                toEvaluateDriverCapacity.add(notification);
                utils.setAvailableDriverCapacity(capDeliveryDriverKey, Math.max(0, remainingCap - 1));
            } else {
                toEvaluateResidualCapacity.add(notification);
            }
        }

        // 2. Processa DOPO le notifiche in notPassedSenderLimit
        toEvaluateResidualCapacity.addAll(notPassedSenderLimit);
        toEvaluateResidualCapacity = sortByPriority(toEvaluateResidualCapacity);

        for (DelayerPaperDelivery notification : new ArrayList<>(toEvaluateResidualCapacity)) {
            String unifiedDeliveryDriverKey = getUnifiedDeliveryDriverKey(notification);
            String capDeliveryDriverKey = getCapDeliveryDriverKey(notification);

            if (!utils.isDriverCensito(unifiedDeliveryDriverKey)) {
                toEvaluateResidualCapacity.remove(notification);
                // Provincia non censita => driver non censito
                // In caso di provincia non censita le notifiche non devono essere congelate e ricaricate
                //toFreeze.add(notification);
                continue;
            }

            int remainingProvincial = utils.getAvailableDriverCapacity(unifiedDeliveryDriverKey);
            int remainingCap = utils.getAvailableDriverCapacity(capDeliveryDriverKey);

            if (remainingProvincial > 0 && remainingCap > 0) {
                if (utils.isMittenteCensito(getSenderKey(notification))) {
                    toEvaluateDriverCapacity.add(notification);
                    toEvaluateResidualCapacity.remove(notification);
                    utils.setAvailableDriverCapacity(unifiedDeliveryDriverKey, Math.max(0, remainingProvincial - 1));
                    utils.setAvailableDriverCapacity(capDeliveryDriverKey, Math.max(0, remainingCap - 1));
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
                    if (!toFreeze.contains(notification)) {
                        toFreeze.add(notification);
                    }
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

    private DelayerPaperDelivery freezeNotification(DelayerPaperDelivery notification) {
        String deliveryDate = getNextMondayFromDate(context.expectedDeliveryDate, 1);
        return utils.deepCopyAndUpdateKeys(List.of(notification), WorkflowSteps.EVALUATE_SENDER_LIMIT, deliveryDate).get(0);
    }

    private List<DelayerPaperDelivery> collectAllFrozen(Map<String, List<DelayerPaperDelivery>> frozenByStep) {
        String deliveryDate = getNextMondayFromDate(context.expectedDeliveryDate, 1);

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

    private List<DelayerPaperDelivery> applySenderPriority(
            List<DelayerPaperDelivery> notifications,
            Map<String, List<DelayerPaperDelivery>> groupedByStep
    ) {
        List<DelayerPaperDelivery> reassigned = new ArrayList<>();

        // RS e secondi tentativi mantengono la priorità tecnica esistente:
        // non partecipano al riordino per senderPriority.
        List<DelayerPaperDelivery> technicalPriorityNotifications = notifications.stream()
                .filter(n -> n.isRS() || n.isSecondAttempt())
                .map(DelayerPaperDelivery::new)
                .toList();

        reassigned.addAll(technicalPriorityNotifications);

        // La senderPriority vale solo per le spedizioni normali / primi tentativi.
        List<DelayerPaperDelivery> normalNotifications = notifications.stream()
                .filter(n -> !(n.isRS() || n.isSecondAttempt()))
                .toList();

        Map<String, List<DelayerPaperDelivery>> bySender = normalNotifications.stream()
                .collect(Collectors.groupingBy(
                        n -> Optional.ofNullable(n.getSenderPaId()).orElse(""),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        for (List<DelayerPaperDelivery> senderNotifications : bySender.values()) {

            // Slot temporali originari del singolo sender.
            // Usiamo getEffectiveNotificationSentAt() per non perdere un eventuale slot virtuale già presente.
            List<String> originalTimeSlots = senderNotifications.stream()
                    .map(DelayerPaperDelivery::getEffectiveNotificationSentAt)
                    .sorted()
                    .toList();

            // Riordino interno allo stesso sender:
            // senderPriority decrescente, poi notificationSentAt crescente.
            List<DelayerPaperDelivery> senderPrioritySorted = senderNotifications.stream()
                    .sorted(
                            Comparator.comparingInt(DelayerPaperDelivery::getSenderPriorityValue)
                                    .reversed()
                                    .thenComparing(DelayerPaperDelivery::getNotificationSentAt)
                                    .thenComparing(DelayerPaperDelivery::getRequestId)
                    )
                    .toList();

            for (int i = 0; i < senderPrioritySorted.size(); i++) {
                DelayerPaperDelivery copy = new DelayerPaperDelivery(senderPrioritySorted.get(i));
                copy.setVirtualNotificationSentAt(originalTimeSlots.get(i));
                reassigned.add(copy);
            }
        }

        // Dopo aver valorizzato virtualNotificationSentAt, il sort globale torna a fare fairness
        // usando gli slot temporali virtuali/originari, senza ordinare globalmente per senderPriority.
        List<DelayerPaperDelivery> sorted = sortByPriority(reassigned);

        groupedByStep.get(WorkflowSteps.EVALUATE_SENDER_PRIORITY.name())
                .addAll(utils.deepCopyAndUpdateKeys(
                        sorted,
                        WorkflowSteps.EVALUATE_SENDER_PRIORITY,
                        context.expectedDeliveryDate
                ));

        return sorted;
    }

}
