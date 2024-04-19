package it.pagopa.pn.client.b2b.pa.polling;

import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponse;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;


public interface IPnPollingService<T extends PnPollingResponse> extends SettableApiKey {
    T waitForEvent(String iun, PnPollingParameter pnPollingParameter);
}