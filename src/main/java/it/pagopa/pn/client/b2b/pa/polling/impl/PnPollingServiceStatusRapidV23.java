package it.pagopa.pn.client.b2b.pa.polling.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV23;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatusHistoryElement;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingTemplate;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV23;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.utils.TimingForTimeline;
import org.springframework.stereotype.Service;
import java.util.concurrent.Callable;
import java.util.function.Predicate;


@Service(PnPollingStrategy.STATUS_RAPID_V23)
public class PnPollingServiceStatusRapidV23 extends PnPollingTemplate<PnPollingResponseV23> {
    protected final TimingForTimeline timingForTimeline;
    private final IPnPaB2bClient pnPaB2bClient;
    private FullSentNotificationV23 notificationV23;


    public PnPollingServiceStatusRapidV23(TimingForTimeline timingForTimeline, IPnPaB2bClient pnPaB2bClient) {
        this.timingForTimeline = timingForTimeline;
        this.pnPaB2bClient = pnPaB2bClient;
    }

    @Override
    protected Callable<PnPollingResponseV23> getPollingResponse(String iun, String value) {
        return () -> {
            PnPollingResponseV23 pnPollingResponse = new PnPollingResponseV23();
            FullSentNotificationV23 fullSentNotificationV23 = pnPaB2bClient.getSentNotification(iun);
            pnPollingResponse.setNotification(fullSentNotificationV23);
            this.notificationV23 = fullSentNotificationV23;
            return pnPollingResponse;
        };
    }

    @Override
    protected Predicate<PnPollingResponseV23> checkCondition(String iun, String value) {
        return (pnPollingResponse) -> {
            if(pnPollingResponse.getNotification() == null) {
                pnPollingResponse.setResult(false);
                return false;
            }

            if(!isEqualState(pnPollingResponse, value)) {
                pnPollingResponse.setResult(false);
                return false;
            }
            pnPollingResponse.setResult(true);
            return true;
        };
    }

    @Override
    protected PnPollingResponseV23 getException(Exception exception) {
        PnPollingResponseV23 pollingResponse = new PnPollingResponseV23();
        pollingResponse.setNotification(this.notificationV23);
        pollingResponse.setResult(false);
        return pollingResponse;
    }

    @Override
    protected Integer getPollInterval(String value) {
        TimingForTimeline.TimingResult timingResult = timingForTimeline.getTimingForElement(value);
        return timingResult.waiting();
    }

    @Override
    protected Integer getAtMost(String value) {
        TimingForTimeline.TimingResult timingResult = timingForTimeline.getTimingForElement(value);
        return timingResult.waiting() * timingResult.numCheck();
    }

    @Override
    public boolean setApiKeys(ApiKeyType apiKey) {
        return this.pnPaB2bClient.setApiKeys(apiKey);
    }

    @Override
    public void setApiKey(String apiKeyString) {
        this.pnPaB2bClient.setApiKey(apiKeyString);
    }

    @Override
    public ApiKeyType getApiKeySetted() {
        return this.pnPaB2bClient.getApiKeySetted();
    }

    private boolean isEqualState(PnPollingResponseV23 pnPollingResponse, String value) {
         NotificationStatusHistoryElement notificationStatusHistoryElement = pnPollingResponse.getNotification()
                .getNotificationStatusHistory()
                .stream()
                .filter(notification -> notification
                        .getStatus()
                        .getValue().equals(value))
                .findAny()
                .orElse(null);
        return notificationStatusHistoryElement != null;
    }
}