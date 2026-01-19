package it.pagopa.pn.cucumber.steps.censimentoStimeMittenti.interfaces;

import it.pagopa.pn.cucumber.steps.delayer.model.DelayerSenderLimit;

import java.util.List;

@FunctionalInterface
public interface SenderLimitCondition {
    boolean test(List<DelayerSenderLimit> items);
}

