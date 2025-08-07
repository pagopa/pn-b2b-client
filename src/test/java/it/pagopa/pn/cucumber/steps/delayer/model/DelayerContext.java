package it.pagopa.pn.cucumber.steps.delayer.model;

import java.util.*;

public class DelayerContext {

    public String expectedDeliveryDate;
    public Integer printCapacity;
    public Integer numeroNotifiche;

    public List<DelayerPaperDelivery> actualCsv = new ArrayList<>();

    public Map<String, Integer> senderLimitMap = new HashMap<>();
    public Map<String, Map<String,Integer>> driverCapCapacityMap = new HashMap<>();

    public Map<String, List<DelayerPaperDelivery>> groupedBySeed = new HashMap<>();
    public Map<String, Map<String, List<DelayerPaperDelivery>>> expectedPianification = new HashMap<>();
    public Map<String, Map<String, List<DelayerPaperDelivery>>> actualPianification = new HashMap<>();
    public Map<String, String> failPianification = new HashMap<>();

    public Map<String, List<String>> priorityConfigMap = Map.of(
            "1", List.of("PRODUCT_RS.ATTEMPT_0"),
            "2", List.of("PRODUCT_AR.ATTEMPT_1", "PRODUCT_890.ATTEMPT_1"),
            "3", List.of("PRODUCT_AR.ATTEMPT_0", "PRODUCT_890.ATTEMPT_0")
    );
}
