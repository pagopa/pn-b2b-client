package it.pagopa.pn.client.b2b.pa.polling.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.FullSentNotification;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElement;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingTemplate;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV1;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaB2bExternalClientImpl;
import it.pagopa.pn.client.b2b.pa.utils.TimingForTimeline;
import org.springframework.stereotype.Service;
import java.util.concurrent.Callable;
import java.util.function.Predicate;


@Service(PnPollingStrategy.TIMELINE_SLOW_OLD_VERSION)
public class PnPollingServiceTimelineSlowOldVersion extends PnPollingTemplate<PnPollingResponseV1> {
    private final TimingForTimeline timingForTimeline;
    private final PnPaB2bExternalClientImpl pnPaB2bExternalClient;
    private FullSentNotification notificationV1;


    public PnPollingServiceTimelineSlowOldVersion(TimingForTimeline timingForTimeline, PnPaB2bExternalClientImpl pnPaB2bExternalClient) {
        this.timingForTimeline = timingForTimeline;
        this.pnPaB2bExternalClient = pnPaB2bExternalClient;
    }


    @Override
    protected Callable<PnPollingResponseV1> getPollingResponse(String iun, String value) {
        return () -> {
            //Example use v2.3 for check
            PnPollingResponseV1 pnPollingResponse = new PnPollingResponseV1();
            FullSentNotification fullSentNotification = pnPaB2bExternalClient.getSentNotificationV1(iun);
            pnPollingResponse.setNotification(fullSentNotification);
            this.notificationV1 = fullSentNotification;
            return pnPollingResponse;
        };
    }

    @Override
    protected Predicate<PnPollingResponseV1> checkCondition(String iun, String value) {
        //Example use v1 for check
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
    protected PnPollingResponseV1 getException(Exception exception) {
        PnPollingResponseV1 pollingResponse = new PnPollingResponseV1();
        pollingResponse.setNotification(this.notificationV1);
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

    private boolean isPresentCategory(PnPollingResponseV1 pnPollingResponse, String value) {
        TimelineElement timelineElementV1 = pnPollingResponse
                .getNotification()
                .getTimeline()
                .stream()
                .filter(timelineElement -> timelineElement
                        .getCategory()
                        .getValue().equals(value))
                .findAny()
                .orElse(null);

        if(timelineElementV1 == null) {
            return false;
        }

        return true;
    }
}