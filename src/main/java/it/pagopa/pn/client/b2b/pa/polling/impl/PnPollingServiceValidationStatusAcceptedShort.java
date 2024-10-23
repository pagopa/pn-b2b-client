package it.pagopa.pn.client.b2b.pa.polling.impl;

import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service(PnPollingStrategy.VALIDATION_STATUS_ACCEPTATION_SHORT)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnPollingServiceValidationStatusAcceptedShort extends PnPollingServiceValidationStatus {


    public PnPollingServiceValidationStatusAcceptedShort(IPnPaB2bClient b2bClient, TimingForPolling timingForPolling) {
        super(b2bClient, timingForPolling);
    }

    @Override
    protected Integer getPollInterval(String value) {
        value = value.concat("_SHORT_VALIDATION");
        TimingForPolling.TimingResult timingResult = this.getTimingForTimeline().getTimingForStatusValidation(value);
        return timingResult.waiting();
    }

    @Override
    protected Integer getAtMost(String value) {
        value = value.concat("_SHORT_VALIDATION");
        TimingForPolling.TimingResult timingResult = this.getTimingForTimeline().getTimingForStatusValidation(value);
        return timingResult.numCheck() * timingResult.waiting();
    }

}
