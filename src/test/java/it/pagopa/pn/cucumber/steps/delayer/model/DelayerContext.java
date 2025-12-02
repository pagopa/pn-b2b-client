package it.pagopa.pn.cucumber.steps.delayer.model;

import java.util.*;

public class DelayerContext {

    private static final int STANDARD_PRINT_CAPACITY = 180_000;

    public String expectedDeliveryDate;
    public Integer printCapacity = STANDARD_PRINT_CAPACITY;
    public Integer numeroNotifiche;

    public List<DelayerPaperDelivery> actualCsv = new ArrayList<>();

    public Map<String, Integer> senderLimitMap = new HashMap<>();
    public Map<String, Map<String,Integer>> driverCapacityMap = new HashMap<>();
    public Map<String, Map<String,Integer>> usedDriverCapacityMap = new HashMap<>();

    public Map<String, List<DelayerPaperDelivery>> groupedBySeed = new HashMap<>();
    public Map<String, Map<String, List<DelayerPaperDelivery>>> expectedPianification = new HashMap<>();
    public Map<String, Map<String, List<DelayerPaperDelivery>>> actualPianification = new HashMap<>();
    public Map<String, String> failPianification = new HashMap<>();

    public Map<String, List<String>> priorityConfigMap = Map.of(
            "1", List.of("PRODUCT_RS.ATTEMPT_0"),
            "2", List.of("PRODUCT_AR.ATTEMPT_1", "PRODUCT_890.ATTEMPT_1"),
            "3", List.of("PRODUCT_AR.ATTEMPT_0", "PRODUCT_890.ATTEMPT_0")
    );

    public List<DelayerPaperDelivery> getExpectedByWorkflowStep(WorkflowSteps  step) {
        return expectedPianification.values().stream()
                .flatMap(m -> m.getOrDefault(step.name(), List.of()).stream())
                .toList();
    }
}
