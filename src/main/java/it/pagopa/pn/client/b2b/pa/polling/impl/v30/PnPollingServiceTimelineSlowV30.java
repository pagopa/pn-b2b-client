package it.pagopa.pn.client.b2b.pa.polling.impl.v30;

import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


@Service(PnPollingStrategy.TIMELINE_SLOW_V30)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnPollingServiceTimelineSlowV30 extends PnPollingServiceTimelineRapidV30 {

    public PnPollingServiceTimelineSlowV30(TimingForPolling timingForPolling, IPnPaB2bClient b2bClient) {
        super(timingForPolling, b2bClient);
    }

    @Override
    protected Integer getPollInterval(String value) {
        TimingForPolling.TimingResult timingResult = timingForPolling.getTimingForElement(value, true, true, false);
        return timingResult.waiting();
    }

    @Override
    protected Integer getAtMost(String value) {
        TimingForPolling.TimingResult timingResult = timingForPolling.getTimingForElement(value, true, true, false);
        return timingResult.waiting() * timingResult.numCheck();
    }
}
