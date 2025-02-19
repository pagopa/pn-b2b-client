package it.pagopa.pn.client.b2b.pa.polling.design;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.awaitility.Awaitility.await;

import it.pagopa.pn.client.b2b.pa.polling.IPnPollingService;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponse;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import org.awaitility.core.ConditionTimeoutException;


public abstract class PnPollingTemplate<T extends PnPollingResponse> implements IPnPollingService<T> {
    private boolean pollingActive = true;

    public T waitForEvent(String id, PnPollingParameter pnPollingParameter) {
        try {
            return await()
                    .atMost(getAtMostImpl(pnPollingParameter.getValue()), MILLISECONDS)
                    .with()
                    .pollInterval(getPollIntervalImpl(pnPollingParameter.getValue()), MILLISECONDS)
                    .until(getPollingResponse(id, pnPollingParameter), checkCondition(id, pnPollingParameter));
        } catch (ConditionTimeoutException conditionTimeoutException) {
            //Eseguo il catch nel caso in cui checkCondition() non ritornerà mai true
            return getException(conditionTimeoutException);
        }
    }

    protected abstract Callable<T> getPollingResponse(String id, PnPollingParameter pnPollingParameter);
    protected abstract Predicate<T> checkCondition(String id, PnPollingParameter pnPollingParameter);
    protected abstract T getException(Exception exception);
    protected abstract Integer getPollInterval(String value);
    protected abstract Integer getAtMost(String value);

    @Override
    public void turnOnPolling() {
        pollingActive = true;
    }

    @Override
    public void turnOffPolling() {
        pollingActive = false;
    }

    private Integer getPollIntervalImpl(String value) {
        return pollingActive ? getPollInterval(value) : 1000;
    }

    private Integer getAtMostImpl(String value) {
        return pollingActive ? getAtMost(value) : 3000;
    }

}