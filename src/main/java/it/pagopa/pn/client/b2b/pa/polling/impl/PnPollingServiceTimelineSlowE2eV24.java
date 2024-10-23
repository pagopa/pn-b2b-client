package it.pagopa.pn.client.b2b.pa.polling.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV24;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV24;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV24;
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


@Service(PnPollingStrategy.TIMELINE_SLOW_E2E_V24)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class PnPollingServiceTimelineSlowE2eV24 extends PnPollingServiceTimelineRapidV24 {

    private final IPnPaB2bClient pnPaB2bClient;
    private FullSentNotificationV24 fullSentNotification;

    public PnPollingServiceTimelineSlowE2eV24(TimingForPolling timingForPolling, IPnPaB2bClient pnPaB2bClient) {
        super(timingForPolling, pnPaB2bClient);
        this.pnPaB2bClient = pnPaB2bClient;

    }

    @Override
    public Callable<PnPollingResponseV24> getPollingResponse(String iun, PnPollingParameter pnPollingParameter) {
        return () -> {
            PnPollingResponseV24 pnPollingResponse = new PnPollingResponseV24();
            FullSentNotificationV24 fullSentNotification;
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
    protected Predicate<PnPollingResponseV24> checkCondition(String iun, PnPollingParameter pnPollingParameter) {
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
    protected PnPollingResponseV24 getException(Exception exception) {
        PnPollingResponseV24 pollingResponse = new PnPollingResponseV24();
        pollingResponse.setNotification(this.fullSentNotification);
        pollingResponse.setResult(false);
        return pollingResponse;
    }


    @Override
    protected Integer getPollInterval(String value) {
        TimingForPolling.TimingResult timingResult = timingForPolling.getTimingForElement(value, true, false);
        return timingResult.waiting();
    }

    @Override
    protected Integer getAtMost(String value) {
        TimingForPolling.TimingResult timingResult = timingForPolling.getTimingForElement(value, true, false);
        return timingResult.waiting() * timingResult.numCheck();
    }

    private boolean isPresentCategory(PnPollingResponseV24 pnPollingResponse, PnPollingParameter pnPollingParameter) {
        TimelineElementV24 timelineElement = pnPollingResponse
                .getNotification()
                .getTimeline()
                .stream()
                .filter(pnPollingParameter.getPnPollingPredicate() == null
                        ?
                        te ->
                                te.getCategory() != null
                                        && Objects.requireNonNull(te.getCategory().getValue()).equals(pnPollingParameter.getValue())
                        :
                        pnPollingParameter.getPnPollingPredicate().getTimelineElementPredicateV24())
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