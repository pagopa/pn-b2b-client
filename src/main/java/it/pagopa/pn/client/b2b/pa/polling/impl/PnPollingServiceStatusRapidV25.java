package it.pagopa.pn.client.b2b.pa.polling.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV25;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatusHistoryElement;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingTemplate;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV25;
import it.pagopa.pn.client.b2b.pa.polling.exception.PnPollingException;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;
import java.util.function.Predicate;


@Service(PnPollingStrategy.STATUS_RAPID_V25)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class PnPollingServiceStatusRapidV25 extends PnPollingTemplate<PnPollingResponseV25> {

    protected final TimingForPolling timingForPolling;
    private final IPnPaB2bClient pnPaB2bClient;
    private FullSentNotificationV25 fullSentNotification;


    public PnPollingServiceStatusRapidV25(TimingForPolling timingForPolling, IPnPaB2bClient pnPaB2bClient) {
        this.timingForPolling = timingForPolling;
        this.pnPaB2bClient = pnPaB2bClient;
    }

    @Override
    protected Callable<PnPollingResponseV25> getPollingResponse(String iun, PnPollingParameter pnPollingParameter) {
        return () -> {
            PnPollingResponseV25 pnPollingResponse = new PnPollingResponseV25();
            FullSentNotificationV25 fullSentNotification;
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
    protected Predicate<PnPollingResponseV25> checkCondition(String iun, PnPollingParameter pnPollingParameter) {
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
    protected PnPollingResponseV25 getException(Exception exception) {
        PnPollingResponseV25 pollingResponse = new PnPollingResponseV25();
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

    private boolean isEqualStatus(PnPollingResponseV25 pnPollingResponse, PnPollingParameter pnPollingParameter) {
        NotificationStatusHistoryElement notificationStatusHistoryElement = pnPollingResponse.getNotification()
                .getNotificationStatusHistory()
                .stream()
                .filter(pnPollingParameter.getPnPollingPredicate() == null
                        ?
                        statusHistory -> statusHistory
                                .getStatus()
                                .getValue().equals(pnPollingParameter.getValue())
                        :
                        pnPollingParameter.getPnPollingPredicate().getNotificationStatusHistoryElementPredicateV23())
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