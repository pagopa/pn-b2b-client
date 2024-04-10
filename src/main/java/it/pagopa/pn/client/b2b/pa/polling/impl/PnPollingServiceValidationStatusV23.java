package it.pagopa.pn.client.b2b.pa.polling.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV23;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NewNotificationRequestStatusResponseV23;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingTemplate;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV23;
import it.pagopa.pn.client.b2b.pa.polling.exception.PnPollingException;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.utils.TimingForTimeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.Callable;
import java.util.function.Predicate;


@Service(PnPollingStrategy.VALIDATION_STATUS_V23)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnPollingServiceValidationStatusV23 extends PnPollingTemplate<PnPollingResponseV23> {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final IPnPaB2bClient b2bClient;
    private NewNotificationRequestStatusResponseV23 requestStatusResponseV23;
    private FullSentNotificationV23 notificationV23;
    private final TimingForTimeline timingForTimeline;

    public PnPollingServiceValidationStatusV23(IPnPaB2bClient b2bClient, TimingForTimeline timingForTimeline) {
        this.b2bClient = b2bClient;
        this.timingForTimeline = timingForTimeline;

    }

    @Override
    protected Callable<PnPollingResponseV23> getPollingResponse(String id, PnPollingParameter pnPollingParameter) {
        return () -> {
            PnPollingResponseV23 pnPollingResponse = new PnPollingResponseV23();
            NewNotificationRequestStatusResponseV23 statusResponseV23 = b2bClient.getNotificationRequestStatus(id);
            pnPollingResponse.setStatusResponse(statusResponseV23);
            this.requestStatusResponseV23 = statusResponseV23;

            if (pnPollingResponse.getStatusResponse().getIun() != null){
                FullSentNotificationV23 fullSentNotificationV23;
                try {
                    fullSentNotificationV23 = b2bClient.getSentNotification(pnPollingResponse.getStatusResponse().getIun());
                } catch (Exception exception) {
                    logger.error("Error getPollingResponse(), Iun: {}, ApiKey: {}, PnPollingException: {}", pnPollingResponse.getStatusResponse().getIun(), b2bClient.getApiKeySetted().name(), exception.getMessage());
                    throw new PnPollingException(exception.getMessage());
                }
                pnPollingResponse.setNotification(fullSentNotificationV23);
                this.notificationV23 = fullSentNotificationV23;
            }
            return pnPollingResponse;
        };
    }

    @Override
    protected Predicate<PnPollingResponseV23> checkCondition(String id, PnPollingParameter pnPollingParameter) {
        return (pnPollingResponse) -> {
            if(pnPollingResponse.getStatusResponse() == null) {
                pnPollingResponse.setResult(false);
                return false;
            }

            if(!pnPollingResponse.getStatusResponse().getNotificationRequestStatus().equalsIgnoreCase(pnPollingParameter.getValue().trim())) {
                pnPollingResponse.setResult(false);
                return false;
            }

            if (pnPollingResponse.getNotification() == null){
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
        pollingResponse.setStatusResponse(this.requestStatusResponseV23);
        pollingResponse.setNotification(this.notificationV23);
        pollingResponse.setResult(false);
        return pollingResponse;
    }

    @Override
    protected Integer getPollInterval(String value) {
        value = value.concat("_VALIDATION");
        TimingForTimeline.TimingResult timingResult = timingForTimeline.getTimingForStatusValidation(value);
        return timingResult.waiting();
    }

    @Override
    protected Integer getAtMost(String value) {
        value = value.concat("_VALIDATION");
        TimingForTimeline.TimingResult timingResult = timingForTimeline.getTimingForStatusValidation(value);
        return timingResult.numCheck() * timingResult.waiting();
    }

    @Override
    public boolean setApiKeys(ApiKeyType apiKey) {
        return this.b2bClient.setApiKeys(apiKey);
    }

    @Override
    public void setApiKey(String apiKeyString) {
        this.b2bClient.setApiKey(apiKeyString);
    }

    @Override
    public ApiKeyType getApiKeySetted() {return this.b2bClient.getApiKeySetted(); }

    protected TimingForTimeline getTimingForTimeline(){
        return this.timingForTimeline;
    }

}
