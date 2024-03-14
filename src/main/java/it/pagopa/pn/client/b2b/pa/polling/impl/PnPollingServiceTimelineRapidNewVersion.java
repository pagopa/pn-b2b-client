package it.pagopa.pn.client.b2b.pa.polling.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV23;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV23;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingTemplate;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV23;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaB2bExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.utils.TimingForTimeline;
import org.springframework.stereotype.Service;
import java.util.concurrent.Callable;
import java.util.function.Predicate;


@Service(PnPollingStrategy.TIMELINE_RAPID_NEW_VERSION)
public class PnPollingServiceTimelineRapidNewVersion extends PnPollingTemplate<PnPollingResponseV23> {
    private final TimingForTimeline timingForTimeline;
    private final PnPaB2bExternalClientImpl pnPaB2bExternalClient;
    private FullSentNotificationV23 notificationV23;


    public PnPollingServiceTimelineRapidNewVersion(TimingForTimeline timingForTimeline, PnPaB2bExternalClientImpl pnPaB2bExternalClient) {
        this.timingForTimeline = timingForTimeline;
        this.pnPaB2bExternalClient = pnPaB2bExternalClient;
    }

    @Override
    public Callable<PnPollingResponseV23> getPollingResponse(String iun, String value) {
        return () -> {
            PnPollingResponseV23 pnPollingResponse = new PnPollingResponseV23();
            FullSentNotificationV23 fullSentNotificationV23 = pnPaB2bExternalClient.getSentNotification(iun);
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

            if(pnPollingResponse.getNotification().getTimeline().isEmpty() ||
                    !isPresentCategory(pnPollingResponse, value)) {
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
        return this.pnPaB2bExternalClient.setApiKeys(apiKey);
    }

    @Override
    public void setApiKey(String apiKeyString) {
        this.pnPaB2bExternalClient.setApiKey(apiKeyString);
    }

    @Override
    public ApiKeyType getApiKeySetted() {
        return this.pnPaB2bExternalClient.getApiKeySetted();
    }

    private boolean isPresentCategory(PnPollingResponseV23 pnPollingResponse, String value) {
        TimelineElementV23 timelineElementV23 = pnPollingResponse
                .getNotification()
                .getTimeline()
                .stream()
                .filter(timelineElement -> timelineElement
                                .getCategory()
                                .getValue().equals(value))
                .findAny()
                .orElse(null);

        return timelineElementV23 != null;
    }
}