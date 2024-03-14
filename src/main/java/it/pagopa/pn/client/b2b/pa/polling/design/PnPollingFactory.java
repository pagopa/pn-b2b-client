package it.pagopa.pn.client.b2b.pa.polling.design;

import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponse;
import it.pagopa.pn.client.b2b.pa.service.IPnPollingService;
import org.springframework.stereotype.Component;
import java.util.Map;


@Component
public class PnPollingFactory {
    private final Map<String, IPnPollingService<?>> pollingServiceMap;

    public PnPollingFactory(Map<String, IPnPollingService<?>> pollingServiceMap) {
        this.pollingServiceMap = pollingServiceMap;
    }

    public IPnPollingService<?> getPollingService(String pollingType) {
        IPnPollingService<?> IPnPollingService = pollingServiceMap.get(pollingType);
        if (IPnPollingService == null) {
            throw new RuntimeException("Unsupported IPnPollingService type");
        }

        return IPnPollingService;
    }

    public void execute(String pollingType, String iun, String checkValue) {
        IPnPollingService<?> iPnPollingService = pollingServiceMap.get(pollingType);
        PnPollingResponse pnPollingResponse = iPnPollingService.waitForEvent(iun, checkValue);
    }
}