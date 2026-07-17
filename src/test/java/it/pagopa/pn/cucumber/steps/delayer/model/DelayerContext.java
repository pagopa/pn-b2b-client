package it.pagopa.pn.cucumber.steps.delayer.model;

import it.pagopa.pn.cucumber.steps.delayer.model.enums.WorkflowSteps;
import it.pagopa.pn.cucumber.steps.delayer.utils.DelayerPaperDeliveryUtils;
import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@ScenarioScope
public class DelayerContext {

    public static final int STANDARD_PRINT_CAPACITY = 180_000;
    public static final int STANDARD_DAILY_EXECUTIONS = 17;

    public String expectedDeliveryDate;
    public Integer printCapacity;
    public Integer weeklyPrintCapacity;
    public Integer numeroNotifiche;

    public int dailyExecution;
    public int expectedExecutions;
    public int currentStepFunction2ExecutionIndex;
    public int maxDeliveryToPhase2ForExecution;

    public List<DelayerPaperDelivery> actualCsv;

    public String currentExecutionArn;
    public boolean assertPhase2ByExecutionCounter;

    public Map<String, Integer> senderLimitMap;
    public Map<String, Map<String, Integer>> driverCapacityMap;
    public Map<String, Map<String, Integer>> usedDriverCapacityMap;

    public Map<String, List<DelayerPaperDelivery>> groupedBySeed;
    public Map<String, Map<String, List<DelayerPaperDelivery>>> expectedPianification;
    public Map<String, Map<String, List<DelayerPaperDelivery>>> actualPianification;
    public Map<String, String> failPianification;

    public List<DelayerPaperDelivery> frozenExpected;

    public Map<String, List<String>> priorityConfigMap = Map.of(
            "1", List.of("PRODUCT_RS.ATTEMPT_0.LEGAL"),
            "2", List.of("PRODUCT_AR.ATTEMPT_1.LEGAL", "PRODUCT_890.ATTEMPT_1.LEGAL"),
            "3", List.of("PRODUCT_AR.ATTEMPT_0.LEGAL", "PRODUCT_890.ATTEMPT_0.LEGAL"),
            "4", List.of("PRODUCT_RS.ATTEMPT_0.INFORMAL", "PRODUCT_RS.ATTEMPT_1.INFORMAL")
    );

    public DelayerContext() {
        resetContext();
    }

    public void resetContext() {
        expectedDeliveryDate = null;
        printCapacity = STANDARD_PRINT_CAPACITY;
        weeklyPrintCapacity = printCapacity * 7;
        numeroNotifiche = null;

        dailyExecution = STANDARD_DAILY_EXECUTIONS;
        expectedExecutions = 1;
        currentStepFunction2ExecutionIndex = 0;
        maxDeliveryToPhase2ForExecution = (int) Math.ceil(printCapacity / dailyExecution);

        actualCsv = new ArrayList<>();

        currentExecutionArn = null;
        assertPhase2ByExecutionCounter = false;

        senderLimitMap = new HashMap<>();
        driverCapacityMap = new HashMap<>();
        usedDriverCapacityMap = new HashMap<>();

        groupedBySeed = new HashMap<>();
        expectedPianification = new HashMap<>();
        actualPianification = new HashMap<>();
        failPianification = new HashMap<>();

        frozenExpected = new ArrayList<>();
    }

    public List<DelayerPaperDelivery> getExpectedByWorkflowStep(WorkflowSteps step) {
        if (step.equals(WorkflowSteps.SENT_TO_PREPARE_PHASE_2) && assertPhase2ByExecutionCounter)
            return getExpectedInPhase2();
        else
            return expectedPianification.values().stream()
                    .flatMap(m -> m.getOrDefault(step.name(), List.of()).stream())
                    .toList();
    }

    public void setMaxDeliveryToPhase2ForExecution(int limit) {
        if (limit < 0) throw new IllegalArgumentException("Limit non valido");
        printCapacity = limit * dailyExecution;
        weeklyPrintCapacity = printCapacity * 7;
        this.maxDeliveryToPhase2ForExecution = limit;
    }

    public List<DelayerPaperDelivery> getExpectedInPhase2() {
        WorkflowSteps step = WorkflowSteps.SENT_TO_PREPARE_PHASE_2;
        int batchSize = maxDeliveryToPhase2ForExecution;
        int currentIndex = Math.max(currentStepFunction2ExecutionIndex, 1); // parte da 1
        int toSkip = (currentIndex - 1) * batchSize;

        List<DelayerPaperDelivery> toPhase2Weekly = DelayerPaperDeliveryUtils.sortByPriority(expectedPianification.values().stream()
                .flatMap(m -> m.getOrDefault(step.name(), List.of()).stream()).toList());

        return toPhase2Weekly.stream()
                .skip(toSkip)
                .limit(batchSize)
                .toList();
    }

    public List<DelayerPaperDelivery> getActualInPhase2() {
        WorkflowSteps step = WorkflowSteps.SENT_TO_PREPARE_PHASE_2;
        int batchSize = maxDeliveryToPhase2ForExecution;
        int currentIndex = Math.max(currentStepFunction2ExecutionIndex, 1); // parte da 1
        int toSkip = (currentIndex - 1) * batchSize;

        List<DelayerPaperDelivery> toPhase2Weekly = DelayerPaperDeliveryUtils.sortByPriority(actualPianification.values().stream()
                .flatMap(m -> m.getOrDefault(step.name(), List.of()).stream()).toList());

        return toPhase2Weekly.stream()
                .skip(toSkip)
                //.limit(batchSize)
                .toList();
    }

    public void setPrintCapacity(int printCapacity) {
        this.printCapacity = printCapacity;
        this.weeklyPrintCapacity = printCapacity * 7;
        this.maxDeliveryToPhase2ForExecution = (int) Math.ceil(printCapacity / dailyExecution);
    }

    public void setWeeklyPrintCapacity(int weeklyPrintCapacity) {
        this.weeklyPrintCapacity = weeklyPrintCapacity;
        this.printCapacity = (int) Math.ceil(weeklyPrintCapacity / 7);
        this.maxDeliveryToPhase2ForExecution = (int) Math.ceil(printCapacity / dailyExecution);
    }
}
