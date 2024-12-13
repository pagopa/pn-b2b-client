package it.pagopa.pn.client.b2b.pa.polling.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV26;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV26;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingTemplate;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV26;
import it.pagopa.pn.client.b2b.pa.polling.exception.PnPollingException;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Predicate;


@Service(PnPollingStrategy.TIMELINE_RAPID_V26)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class PnPollingServiceTimelineRapidV26 extends PnPollingTemplate<PnPollingResponseV26> {

    protected final TimingForPolling timingForPolling;
    private final IPnPaB2bClient pnPaB2bClient;
    private FullSentNotificationV26 fullSentNotification;


    public PnPollingServiceTimelineRapidV26(TimingForPolling timingForPolling, IPnPaB2bClient pnPaB2bClient) {
        this.timingForPolling = timingForPolling;
        this.pnPaB2bClient = pnPaB2bClient;
    }

    @Override
    public Callable<PnPollingResponseV26> getPollingResponse(String iun, PnPollingParameter pnPollingParameter) {
        return () -> {
            PnPollingResponseV26 pnPollingResponse = new PnPollingResponseV26();
            FullSentNotificationV26 fullSentNotification;
            try {
                fullSentNotification = pnPaB2bClient.getSentNotification(iun);
            } catch (Exception exception) {
                log.error("Error getPollingResponse(), Iun: {}, ApiKey: {}, PnPollingException: {}", iun, pnPaB2bClient.getApiKeySetted().name(), exception.getMessage());
                throw new PnPollingException(exception.getMessage());
            }
            pnPollingResponse.setNotification(fullSentNotification);
            this.fullSentNotification = fullSentNotification;
            return pnPollingResponse;
        };
    }

    @Override
    protected Predicate<PnPollingResponseV26> checkCondition(String iun, PnPollingParameter pnPollingParameter) {
        return pnPollingResponse -> {
            if (pnPollingResponse.getNotification() == null) {
                pnPollingResponse.setResult(false);
                return false;
            }

            if (pnPollingResponse.getNotification().getTimeline().isEmpty() ||
                    !isPresentCategory(pnPollingResponse, pnPollingParameter)) {
                pnPollingResponse.setResult(false);
                return false;
            }

            return true;
        };
    }

    @Override
    protected PnPollingResponseV26 getException(Exception exception) {
        PnPollingResponseV26 pollingResponse = new PnPollingResponseV26();
        pollingResponse.setNotification(this.fullSentNotification);
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

    private boolean isPresentCategory(PnPollingResponseV26 pnPollingResponse, PnPollingParameter pnPollingParameter) {
        TimelineElementV26 timelineElement = pnPollingResponse
                .getNotification()
                .getTimeline()
                .stream()
                .filter(pnPollingParameter.getPnPollingPredicate() == null
                        ?
                        te ->
                                te.getCategory() != null
                                        && Objects.requireNonNull(te.getCategory().getValue()).equals(pnPollingParameter.getValue())
                        :
                        pnPollingParameter.getPnPollingPredicate().getTimelineElementPredicateV26())
                .findAny()
                .orElse(null);

        if (timelineElement != null) {
            pnPollingResponse.setTimelineElement(timelineElement);
            pnPollingResponse.setResult(true);
            return true;
        }
        return false;
    }
}