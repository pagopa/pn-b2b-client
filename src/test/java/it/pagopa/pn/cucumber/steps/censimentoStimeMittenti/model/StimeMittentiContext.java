package it.pagopa.pn.cucumber.steps.censimentoStimeMittenti.model;

import it.pagopa.pn.cucumber.steps.delayer.model.DelayerSenderLimit;

import java.time.YearMonth;
import java.util.List;

public class StimeMittentiContext {
    public static class SenderLimitsForPeriod {
        public List<DelayerSenderLimit> senderLimits;
    }

    public YearMonth da;
    public YearMonth a;
    public String province;
    public SenderLimitsForPeriod actual = new SenderLimitsForPeriod();
    public SenderLimitsForPeriod expected = new SenderLimitsForPeriod();
    public ModuloCommessa moduloCommessa;
}
