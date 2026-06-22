package it.pagopa.pn.client.b2b.pa.polling.impl.v30;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV29;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatusHistoryElementV26;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingTemplate;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV30;
import it.pagopa.pn.client.b2b.pa.polling.exception.PnPollingException;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;
import java.util.function.Predicate;


@Service(PnPollingStrategy.STATUS_RAPID_V30)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class PnPollingServiceStatusRapidV30 extends PnPollingTemplate<PnPollingResponseV30> {

    protected final TimingForPolling timingForPolling;
    private final IPnPaB2bClient b2bClient;
    private FullSentNotificationV29 fullSentNotification;

    public PnPollingServiceStatusRapidV30(TimingForPolling timingForPolling, IPnPaB2bClient b2bClient) {
        this.timingForPolling = timingForPolling;
        this.b2bClient = b2bClient;
    }

    @Override
    protected Callable<PnPollingResponseV30> getPollingResponse(String iun, PnPollingParameter pnPollingParameter) {
        return () -> {
            PnPollingResponseV30 pnPollingResponse = new PnPollingResponseV30();
            try {
                fullSentNotification = b2bClient.getSentNotificationV29(iun);
            } catch (Exception exception) {
                log.error("Error getPollingResponse(), Iun: {}, ApiKey: {}, PnPollingException: {}", iun, b2bClient.getApiKeySetted().name(), exception.getMessage());
                throw new PnPollingException(exception.getMessage());
            }
            pnPollingResponse.setNotification(fullSentNotification);
            return pnPollingResponse;
        };
    }

    @Override
    protected Predicate<PnPollingResponseV30> checkCondition(String iun, PnPollingParameter pnPollingParameter) {
        return pnPollingResponse -> {
            if (pnPollingResponse.getNotification() == null) {
                pnPollingResponse.setResult(false);
                return false;
            }
            if (!isEqualStatus(pnPollingResponse, pnPollingParameter)) {
                pnPollingResponse.setResult(false);
                return false;
            }
            return true;
        };
    }

    @Override
    protected PnPollingResponseV30 getException(Exception exception) {
        PnPollingResponseV30 pollingResponse = new PnPollingResponseV30();
        pollingResponse.setNotification(fullSentNotification);
        pollingResponse.setResult(false);
        return pollingResponse;
    }

    @Override
    protected Integer getPollInterval(String value) {
        TimingForPolling.TimingResult timingResult = timingForPolling.getTimingForElement(value);
        return timingResult.waiting();
    }

    @Override
    protected Integer getAtMost(String value) {
        TimingForPolling.TimingResult timingResult = timingForPolling.getTimingForElement(value);
        return timingResult.waiting() * timingResult.numCheck();
    }

    @Override
    public boolean setApiKeys(ApiKeyType apiKey) {
        return b2bClient.setApiKeys(apiKey);
    }

    @Override
    public void setApiKey(String apiKeyString) {
        b2bClient.setApiKey(apiKeyString);
    }

    @Override
    public ApiKeyType getApiKeySetted() {
        return b2bClient.getApiKeySetted();
    }

    private boolean isEqualStatus(PnPollingResponseV30 pnPollingResponse, PnPollingParameter pnPollingParameter) {
        NotificationStatusHistoryElementV26 notificationStatusHistoryElement = pnPollingResponse.getNotification()
                .getNotificationStatusHistory()
                .stream()
                .filter(pnPollingParameter.getPnPollingPredicate() == null ?
                        statusHistory -> statusHistory.getStatus().getValue().equals(pnPollingParameter.getValue())
                        : pnPollingParameter.getPnPollingPredicate().getNotificationStatusHistoryElementPredicateV26())
                .findAny()
                .orElse(null);
        if (notificationStatusHistoryElement != null) {
            pnPollingResponse.setNotificationStatusHistoryElement(notificationStatusHistoryElement);
            pnPollingResponse.setResult(true);
            return true;
        }
        return false;
    }
}