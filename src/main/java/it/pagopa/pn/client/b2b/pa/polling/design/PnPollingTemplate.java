package it.pagopa.pn.client.b2b.pa.polling.design;

import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponse;
import it.pagopa.pn.client.b2b.pa.polling.IPnPollingService;
import org.awaitility.core.ConditionTimeoutException;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.awaitility.Awaitility.await;


public abstract class PnPollingTemplate<T extends PnPollingResponse> implements IPnPollingService<T> {
    public T waitForEvent(String id, PnPollingParameter pnPollingParameter) {
        try {
            return await()
                    .atMost(getAtMost(pnPollingParameter.getValue()), MILLISECONDS)
                    .with()
                    .pollInterval(getPollInterval(pnPollingParameter.getValue()), MILLISECONDS)
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
}