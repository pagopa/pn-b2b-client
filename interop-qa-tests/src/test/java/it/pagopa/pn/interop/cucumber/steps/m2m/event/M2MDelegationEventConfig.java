package it.pagopa.pn.interop.cucumber.steps.m2m.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class M2MDelegationEventConfig {
    private boolean producerDelegationActivated;
    private boolean consumerDelegationActivated;
}
