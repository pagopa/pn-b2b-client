package it.pagopa.pn.client.b2b.pa.polling.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationRequestStatusResponseV21;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.FullSentNotificationV21;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingTemplate;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV21;
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


@Service(PnPollingStrategy.VALIDATION_STATUS_V21)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnPollingServiceValidationStatusV21 extends PnPollingTemplate<PnPollingResponseV21> {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final IPnPaB2bClient b2bClient;
    private NewNotificationRequestStatusResponseV21 requestStatusResponseV21;
    private FullSentNotificationV21 notificationV21;
    private final TimingForTimeline timingForTimeline;


    public PnPollingServiceValidationStatusV21(IPnPaB2bClient b2bClient, TimingForTimeline timingForTimeline) {
        this.b2bClient = b2bClient;
        this.timingForTimeline = timingForTimeline;
    }

    @Override
    protected Callable<PnPollingResponseV21> getPollingResponse(String id, PnPollingParameter pnPollingParameter) {
        return () -> {
            PnPollingResponseV21 pnPollingResponse = new PnPollingResponseV21();
            NewNotificationRequestStatusResponseV21 statusResponseV21 = b2bClient.getNotificationRequestStatusV21(id);
            pnPollingResponse.setStatusResponse(statusResponseV21);
            this.requestStatusResponseV21 = statusResponseV21;

            if (pnPollingResponse.getStatusResponse().getIun() != null){
                FullSentNotificationV21 fullSentNotificationV21;
                try {
                    fullSentNotificationV21 = b2bClient.getSentNotificationV21(pnPollingResponse.getStatusResponse().getIun());
                } catch (Exception exception) {
                    logger.error("Error getPollingResponse(), Iun: {}, ApiKey: {}, PnPollingException: {}", pnPollingResponse.getStatusResponse().getIun(), b2bClient.getApiKeySetted().name(), exception.getMessage());
                    throw new PnPollingException(exception.getMessage());
                }
                pnPollingResponse.setNotification(fullSentNotificationV21);
                this.notificationV21 = fullSentNotificationV21;

            }
            return pnPollingResponse;
        };
    }

    @Override
    protected Predicate<PnPollingResponseV21> checkCondition(String id, PnPollingParameter pnPollingParameter) {
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
    protected PnPollingResponseV21 getException(Exception exception) {
        PnPollingResponseV21 pollingResponse = new PnPollingResponseV21();
        pollingResponse.setStatusResponse(this.requestStatusResponseV21);
        pollingResponse.setNotification(this.notificationV21);
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

}
