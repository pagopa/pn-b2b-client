package it.pagopa.pn.client.b2b.pa.polling.impl.v24;

import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service(PnPollingStrategy.VALIDATION_STATUS_NO_ACCEPTATION)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnPollingServiceValidationStatusNoAccepted extends PnPollingServiceValidationStatus {


    public PnPollingServiceValidationStatusNoAccepted(IPnPaB2bClient b2bClient, TimingForPolling timingForPolling) {
        super(b2bClient, timingForPolling);
    }

    @Override
    protected Integer getAtMost(String value) {
        value = value.replace(value, "NO_ACCEPTED_VALIDATION");
        TimingForPolling.TimingResult timingResult = this.getTimingForTimeline().getTimingForStatusValidation(value);
        return timingResult.numCheck() * timingResult.waiting();
    }
}
