package it.pagopa.pn.client.b2b.pa.polling.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.FullSentNotificationV20;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.TimelineElementV20;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingTemplate;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV20;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.utils.TimingForTimeline;
import org.springframework.stereotype.Service;
import java.util.concurrent.Callable;
import java.util.function.Predicate;


@Service(PnPollingStrategy.TIMELINE_RAPID_V20)
public class PnPollingServiceTimelineRapidV20  extends PnPollingTemplate<PnPollingResponseV20> {
    protected final TimingForTimeline timingForTimeline;
    private final IPnPaB2bClient pnPaB2bClient;
    private FullSentNotificationV20 notificationV20;


    public PnPollingServiceTimelineRapidV20(TimingForTimeline timingForTimeline, IPnPaB2bClient pnPaB2bClient) {
        this.timingForTimeline = timingForTimeline;
        this.pnPaB2bClient = pnPaB2bClient;
    }

    @Override
    protected Callable<PnPollingResponseV20> getPollingResponse(String iun, String value) {
        return () -> {
            PnPollingResponseV20 pnPollingResponse = new PnPollingResponseV20();
            FullSentNotificationV20 fullSentNotification = pnPaB2bClient.getSentNotificationV2(iun);
            pnPollingResponse.setNotification(fullSentNotification);
            this.notificationV20 = fullSentNotification;
            return pnPollingResponse;
        };
    }

    @Override
    protected Predicate<PnPollingResponseV20> checkCondition(String iun, String value) {
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
    protected PnPollingResponseV20 getException(Exception exception) {
        PnPollingResponseV20 pollingResponse = new PnPollingResponseV20();
        pollingResponse.setNotification(this.notificationV20);
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

    private boolean isPresentCategory(PnPollingResponseV20 pnPollingResponse, String value) {
        TimelineElementV20 timelineElementV20 = pnPollingResponse
                .getNotification()
                .getTimeline()
                .stream()
                .filter(timelineElement ->
                        timelineElement.getCategory() != null
                        && timelineElement.getCategory().getValue().equals(value))
                .findAny()
                .orElse(null);

        return timelineElementV20 != null;
    }
}
