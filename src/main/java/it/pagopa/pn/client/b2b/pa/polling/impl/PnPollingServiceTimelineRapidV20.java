package it.pagopa.pn.client.b2b.pa.polling.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.FullSentNotificationV20;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.TimelineElementV20;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingTemplate;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV20;
import it.pagopa.pn.client.b2b.pa.polling.exception.PnPollingException;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.utils.TimingForTimeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Predicate;


@Service(PnPollingStrategy.TIMELINE_RAPID_V20)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnPollingServiceTimelineRapidV20  extends PnPollingTemplate<PnPollingResponseV20> {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    protected final TimingForTimeline timingForTimeline;
    private final IPnPaB2bClient pnPaB2bClient;
    private FullSentNotificationV20 notificationV20;
    private TimelineElementV20 timelineElementV20;


    public PnPollingServiceTimelineRapidV20(TimingForTimeline timingForTimeline, IPnPaB2bClient pnPaB2bClient) {
        this.timingForTimeline = timingForTimeline;
        this.pnPaB2bClient = pnPaB2bClient;
    }

    @Override
    protected Callable<PnPollingResponseV20> getPollingResponse(String iun, PnPollingParameter pnPollingParameter) {
        return () -> {
            PnPollingResponseV20 pnPollingResponse = new PnPollingResponseV20();
            FullSentNotificationV20 fullSentNotificationV20;
            try {
                fullSentNotificationV20 = pnPaB2bClient.getSentNotificationV2(iun);
            } catch (Exception exception) {
                logger.error("Error getPollingResponse(), Iun: {}, ApiKey: {}, PnPollingException: {}", iun, pnPaB2bClient.getApiKeySetted().name(), exception.getMessage());
                throw new PnPollingException(exception.getMessage());
            }
            pnPollingResponse.setNotification(fullSentNotificationV20);
            this.notificationV20 = fullSentNotificationV20;
            return pnPollingResponse;
        };
    }

    @Override
    protected Predicate<PnPollingResponseV20> checkCondition(String iun, PnPollingParameter pnPollingParameter) {
        return (pnPollingResponse) -> {
            if(pnPollingResponse.getNotification() == null) {
                pnPollingResponse.setResult(false);
                return false;
            }

            if(pnPollingResponse.getNotification().getTimeline().isEmpty() ||
                    !isPresentCategory(pnPollingResponse, pnPollingParameter)) {
                pnPollingResponse.setResult(false);
                return false;
            }

            pnPollingResponse.setResult(true);
            pnPollingResponse.setTimelineElement(timelineElementV20);
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

    private boolean isPresentCategory(PnPollingResponseV20 pnPollingResponse, PnPollingParameter pnPollingParameter) {
        TimelineElementV20 timelineElementV20 = pnPollingResponse
                .getNotification()
                .getTimeline()
                .stream()
                .filter(pnPollingParameter.getPnPollingPredicate() == null
                    ?
                        timelineElement->
                                timelineElement.getCategory() != null
                                        && Objects.requireNonNull(timelineElement.getCategory().getValue()).equals(pnPollingParameter.getValue())
                    :
                        pnPollingParameter.getPnPollingPredicate().getTimelineElementPredicateV20())
                .findAny()
                .orElse(null);

        if(timelineElementV20 != null) {
            this.timelineElementV20 = timelineElementV20;
            return true;
        }
        return false;
    }
}