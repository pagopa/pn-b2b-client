package it.pagopa.pn.client.b2b.pa.polling.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.FullSentNotification;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationRequestStatusResponse;
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
import java.util.concurrent.Callable;
import java.util.function.Predicate;


@Service(PnPollingStrategy.VALIDATION_STATUS_V1)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnPollingServiceValidationStatusV1 extends PnPollingTemplate<PnPollingResponseV1> {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final IPnPaB2bClient b2bClient;
    private NewNotificationRequestStatusResponse requestStatusResponseV1;
    private FullSentNotification notification;
    private final TimingForTimeline timingForTimeline;


    public PnPollingServiceValidationStatusV1(IPnPaB2bClient b2bClient, TimingForTimeline timingForTimeline) {
        this.b2bClient = b2bClient;
        this.timingForTimeline = timingForTimeline;
    }

    @Override
    protected Callable<PnPollingResponseV1> getPollingResponse(String id, PnPollingParameter pnPollingParameter) {
        return () -> {
            PnPollingResponseV1 pnPollingResponse = new PnPollingResponseV1();
            NewNotificationRequestStatusResponse statusResponseV1 = b2bClient.getNotificationRequestStatusV1(id);
            pnPollingResponse.setStatusResponse(statusResponseV1);
            this.requestStatusResponseV1 = statusResponseV1;

            if (pnPollingResponse.getStatusResponse().getIun() != null){
                FullSentNotification fullSentNotificationV1;
                try {
                    fullSentNotificationV1 = b2bClient.getSentNotificationV1(pnPollingResponse.getStatusResponse().getIun());
                } catch (Exception exception) {
                    logger.error("Error getPollingResponse(), Iun: {}, ApiKey: {}, PnPollingException: {}", pnPollingResponse.getStatusResponse().getIun(), b2bClient.getApiKeySetted().name(), exception.getMessage());
                    throw new PnPollingException(exception.getMessage());
                }
                pnPollingResponse.setNotification(fullSentNotificationV1);
                this.notification = fullSentNotificationV1;
            }
            return pnPollingResponse;
        };
    }

    @Override
    protected Predicate<PnPollingResponseV1> checkCondition(String id, PnPollingParameter pnPollingParameter) {
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
    protected PnPollingResponseV1 getException(Exception exception) {
        PnPollingResponseV1 pollingResponse = new PnPollingResponseV1();
        pollingResponse.setStatusResponse(this.requestStatusResponseV1);
        pollingResponse.setNotification(this.notification);
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
