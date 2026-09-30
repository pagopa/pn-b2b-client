package it.pagopa.pn.client.b2b.pa.polling.impl.v30;

import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


@Service(PnPollingStrategy.STATUS_EXTRA_RAPID_V30)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnPollingServiceStatusExtraRapidV30 extends PnPollingServiceStatusRapidV30 {

    public PnPollingServiceStatusExtraRapidV30(TimingForPolling timingForPolling, IPnPaB2bClient b2bClient) {
        super(timingForPolling, b2bClient);
    }

    protected Integer getPollInterval(String value) {
        TimingForPolling.TimingResult timingResult = timingForPolling.getTimingForElement(value, true, true);
        return timingResult.waiting();
    }

    protected Integer getAtMost(String value) {
        TimingForPolling.TimingResult timingResult = timingForPolling.getTimingForElement(value, true, true);
        return timingResult.waiting() * timingResult.numCheck();
    }
}