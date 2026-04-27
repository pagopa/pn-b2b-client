package it.pagopa.pn.cucumber.steps.delayer.model;

import lombok.Data;

@Data
public class DelayerCountersExcludeItem extends DelayerCountersSumEstimatesItem {
    private Long ttl;
}
