package it.pagopa.pn.client.b2b.pa.polling.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.FullSentNotificationV20;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationRequestStatusResponse;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingTemplate;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV20;
import it.pagopa.pn.client.b2b.pa.polling.exception.PnPollingException;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import java.util.concurrent.Callable;
import java.util.function.Predicate;


@Slf4j
@Service(PnPollingStrategy.VALIDATION_STATUS_V20)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnPollingServiceValidationStatusV20 extends PnPollingTemplate<PnPollingResponseV20> {
    private final IPnPaB2bClient b2bClient;
    private NewNotificationRequestStatusResponse requestStatusResponseV20;
    private FullSentNotificationV20 notificationV20;
    private final TimingForPolling timingForPolling;


    public PnPollingServiceValidationStatusV20(IPnPaB2bClient b2bClient, TimingForPolling timingForPolling) {
        this.b2bClient = b2bClient;
        this.timingForPolling = timingForPolling;
    }

    @Override
    protected Callable<PnPollingResponseV20> getPollingResponse(String id, PnPollingParameter pnPollingParameter) {
        return () -> {
            PnPollingResponseV20 pnPollingResponse = new PnPollingResponseV20();
            NewNotificationRequestStatusResponse statusResponseV20 = b2bClient.getNotificationRequestStatusV2(id);
            pnPollingResponse.setStatusResponse(statusResponseV20);
            this.requestStatusResponseV20 = statusResponseV20;

            if (pnPollingResponse.getStatusResponse().getIun() != null){
                FullSentNotificationV20 fullSentNotificationV20;
                try {
                    fullSentNotificationV20 = b2bClient.getSentNotificationV2(pnPollingResponse.getStatusResponse().getIun());
                } catch (Exception exception) {
                    log.error("Error getPollingResponse(), Iun: {}, ApiKey: {}, PnPollingException: {}", pnPollingResponse.getStatusResponse().getIun(), b2bClient.getApiKeySetted().name(), exception.getMessage());
                    throw new PnPollingException(exception.getMessage());
                }
                pnPollingResponse.setNotification(fullSentNotificationV20);
                this.notificationV20 = fullSentNotificationV20;
            }
            return pnPollingResponse;
        };
    }

    @Override
    protected Predicate<PnPollingResponseV20> checkCondition(String id, PnPollingParameter pnPollingParameter) {
        return pnPollingResponse -> {
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
    protected PnPollingResponseV20 getException(Exception exception) {
        PnPollingResponseV20 pollingResponse = new PnPollingResponseV20();
        pollingResponse.setStatusResponse(this.requestStatusResponseV20);
        pollingResponse.setNotification(this.notificationV20);
        pollingResponse.setResult(false);
        return pollingResponse;
    }

    @Override
    protected Integer getPollInterval(String value) {
        value = value.concat("_VALIDATION");
        TimingForPolling.TimingResult timingResult = timingForPolling.getTimingForStatusValidation(value);
        return timingResult.waiting();
    }

    @Override
    protected Integer getAtMost(String value) {
        value = value.concat("_VALIDATION");
        TimingForPolling.TimingResult timingResult = timingForPolling.getTimingForStatusValidation(value);
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
