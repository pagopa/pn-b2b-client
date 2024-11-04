package it.pagopa.pn.client.b2b.pa.polling.impl.v24;

import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


@Service(PnPollingStrategy.STATUS_EXTRA_RAPID)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnPollingServiceStatusExtraRapid extends PnPollingServiceStatusRapid {
    public PnPollingServiceStatusExtraRapid(TimingForPolling timingForPolling, IPnPaB2bClient pnPaB2bClient) {
        super(timingForPolling, pnPaB2bClient);
    }

    @Override
    protected Integer getPollInterval(String value) {
        TimingForPolling.TimingResult timingResult = timingForPolling.getTimingForElement(value, true, true);
        return timingResult.waiting();
    }

    @Override
    protected Integer getAtMost(String value) {
        TimingForPolling.TimingResult timingResult = timingForPolling.getTimingForElement(value, true, true);
        return timingResult.waiting() * timingResult.numCheck();
    }
}