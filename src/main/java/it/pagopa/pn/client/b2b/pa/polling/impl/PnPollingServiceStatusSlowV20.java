package it.pagopa.pn.client.b2b.pa.polling.impl;

import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.utils.TimingForTimeline;
import org.springframework.stereotype.Service;


@Service(PnPollingStrategy.STATUS_SLOW_V20)
public class PnPollingServiceStatusSlowV20 extends PnPollingServiceStatusRapidV20 {
    public PnPollingServiceStatusSlowV20(TimingForTimeline timingForTimeline, IPnPaB2bClient pnPaB2bClient) {
        super(timingForTimeline, pnPaB2bClient);
    }

    @Override
    protected Integer getPollInterval(String value) {
        TimingForTimeline.TimingResult timingResult = timingForTimeline.getTimingForElement(value, true);
        return timingResult.waiting();
    }

    @Override
    protected Integer getAtMost(String value) {
        TimingForTimeline.TimingResult timingResult = timingForTimeline.getTimingForElement(value, true);
        return timingResult.waiting() * timingResult.numCheck();
    }
}