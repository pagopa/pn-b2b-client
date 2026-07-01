package it.pagopa.pn.cucumber.steps.delayer.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DelayerCountersExcludeItem extends DelayerCountersSumEstimatesItem {
    private Long ttl;
}
