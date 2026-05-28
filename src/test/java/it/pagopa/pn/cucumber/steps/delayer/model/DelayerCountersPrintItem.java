package it.pagopa.pn.cucumber.steps.delayer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DelayerCountersPrintItem extends DelayerCountersExcludeItem {

    private int dailyExecutionCounter;
    private int dailyExecutionNumber;
    private int dailyPrintCapacity;

    private JsonNode lastEvaluatedKeyNextWeek;
    private JsonNode lastEvaluatedKeyPhase2;

    private int sentToNextWeek;
    private int sentToPhaseTwo;

    private int weeklyPrintCapacity;
    private Boolean stopSendToPhaseTwo;
}
