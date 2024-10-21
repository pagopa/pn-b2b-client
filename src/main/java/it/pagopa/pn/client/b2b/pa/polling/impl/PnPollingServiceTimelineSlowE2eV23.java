package it.pagopa.pn.client.b2b.pa.polling.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV23;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV23;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV23;
import it.pagopa.pn.client.b2b.pa.polling.exception.PnPollingException;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Predicate;


@Service(PnPollingStrategy.TIMELINE_SLOW_E2E_V23)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class PnPollingServiceTimelineSlowE2eV23 extends PnPollingServiceTimelineRapidV23 {

    private final IPnPaB2bClient pnPaB2bClient;
    private FullSentNotificationV23 notificationV23;

    public PnPollingServiceTimelineSlowE2eV23(TimingForPolling timingForPolling, IPnPaB2bClient pnPaB2bClient) {
        super(timingForPolling, pnPaB2bClient);
        this.pnPaB2bClient = pnPaB2bClient;

    }

    @Override
    public Callable<PnPollingResponseV23> getPollingResponse(String iun, PnPollingParameter pnPollingParameter) {
        return () -> {
            PnPollingResponseV23 pnPollingResponse = new PnPollingResponseV23();
            FullSentNotificationV23 fullSentNotificationV23;
            try {
                fullSentNotificationV23 = pnPaB2bClient.getSentNotification(iun);
            } catch (Exception exception) {
                log.error("Error getPollingResponse(), Iun: {}, ApiKey: {}, PnPollingException: {}", iun, pnPaB2bClient.getApiKeySetted().name(), exception.getMessage());
                throw new PnPollingException(exception.getMessage());
            }
            pnPollingResponse.setNotification(fullSentNotificationV23);
            this.notificationV23 = fullSentNotificationV23;
            return pnPollingResponse;
        };
    }

    @Override
    protected Predicate<PnPollingResponseV23> checkCondition(String iun, PnPollingParameter pnPollingParameter) {
        return pnPollingResponse -> {


            if(pnPollingResponse.getNotification() == null) {
                pnPollingResponse.setResult(false);
                return false;
            }

            if(pnPollingResponse.getNotification().getTimeline().isEmpty() ||
                    !isPresentCategory(pnPollingResponse, pnPollingParameter)) {
                pnPollingResponse.setResult(false);
                return false;
            }

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
        TimingForPolling.TimingResult timingResult = timingForPolling.getTimingForElement(value, true, false);
        return timingResult.waiting();
    }

    @Override
    protected Integer getAtMost(String value) {
        TimingForPolling.TimingResult timingResult = timingForPolling.getTimingForElement(value, true, false);
        return timingResult.waiting() * timingResult.numCheck();
    }

    private boolean isPresentCategory(PnPollingResponseV23 pnPollingResponse, PnPollingParameter pnPollingParameter) {
        TimelineElementV23 timelineElementV23 = pnPollingResponse
                .getNotification()
                .getTimeline()
                .stream()
                .filter(pnPollingParameter.getPnPollingPredicate() == null
                        ?
                        timelineElement->
                                timelineElement.getCategory() != null
                                        && Objects.requireNonNull(timelineElement.getCategory().getValue()).equals(pnPollingParameter.getValue())
                        :
                        pnPollingParameter.getPnPollingPredicate().getTimelineElementPredicateV23())
                .findAny()
                .orElse(null);

        if(timelineElementV23 != null) {
            pnPollingResponse.setTimelineElement(timelineElementV23);
            pnPollingResponse.setResult(true);
            return true;
        }
        return false;
    }
}