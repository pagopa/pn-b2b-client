package it.pagopa.pn.cucumber.steps.delayer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DelayerPrintCapacityCounter {
    private String pk;
    private String sk;

    private int dailyExecutionCounter;
    private int dailyExecutionNumber;
    private int dailyPrintCapacity;

    private JsonNode lastEvaluatedKeyNextWeek;
    private JsonNode lastEvaluatedKeyPhase2;

    private int numberOfShipments;
    private int sentToNextWeek;
    private int sentToPhaseTwo;

    private long ttl;
    private int weeklyPrintCapacity;
}
