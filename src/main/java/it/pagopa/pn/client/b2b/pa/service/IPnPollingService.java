package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponse;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;


public interface IPnPollingService<T extends PnPollingResponse> extends SettableApiKey {
    T waitForEvent(String iun, String checkValue);
}