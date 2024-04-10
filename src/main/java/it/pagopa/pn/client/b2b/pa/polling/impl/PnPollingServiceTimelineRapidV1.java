package it.pagopa.pn.client.b2b.pa.polling.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.FullSentNotification;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElement;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingTemplate;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV1;
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


@Service(PnPollingStrategy.TIMELINE_RAPID_V1)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnPollingServiceTimelineRapidV1 extends PnPollingTemplate<PnPollingResponseV1> {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    protected final TimingForTimeline timingForTimeline;
    private final IPnPaB2bClient pnPaB2bClient;
    private FullSentNotification notificationV1;
    private TimelineElement timelineElementV1;


    public PnPollingServiceTimelineRapidV1(TimingForTimeline timingForTimeline, IPnPaB2bClient pnPaB2bClient) {
        this.timingForTimeline = timingForTimeline;
        this.pnPaB2bClient = pnPaB2bClient;
    }

    @Override
    protected Callable<PnPollingResponseV1> getPollingResponse(String iun, PnPollingParameter pnPollingParameter) {
        return () -> {
            PnPollingResponseV1 pnPollingResponse = new PnPollingResponseV1();
            FullSentNotification fullSentNotificationV1;
            try {
                fullSentNotificationV1 = pnPaB2bClient.getSentNotificationV1(iun);
            } catch (Exception exception) {
                logger.error("Error getPollingResponse(), Iun: {}, ApiKey: {}, PnPollingException: {}", iun, pnPaB2bClient.getApiKeySetted().name(), exception.getMessage());
                throw new PnPollingException(exception.getMessage());
            }
            pnPollingResponse.setNotification(fullSentNotificationV1);
            this.notificationV1 = fullSentNotificationV1;
            return pnPollingResponse;
        };
    }

    @Override
    protected Predicate<PnPollingResponseV1> checkCondition(String iun, PnPollingParameter pnPollingParameter) {
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
            pnPollingResponse.setTimelineElement(timelineElementV1);
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


    private boolean isPresentCategory(PnPollingResponseV1 pnPollingResponse, PnPollingParameter pnPollingParameter) {
        TimelineElement timelineElementV1 = pnPollingResponse
                    .getNotification()
                    .getTimeline()
                    .stream()
                    .filter(pnPollingParameter.getPnPollingPredicate() == null
                        ?
                            timelineElement->
                                timelineElement.getCategory() != null
                                        && Objects.requireNonNull(timelineElement.getCategory().getValue()).equals(pnPollingParameter.getValue())
                        :
                            pnPollingParameter.getPnPollingPredicate().getTimelineElementPredicateV1())
                    .findAny()
                    .orElse(null);

        if(timelineElementV1 != null) {
            this.timelineElementV1 = timelineElementV1;
            return true;
        }
        return false;
    }
}