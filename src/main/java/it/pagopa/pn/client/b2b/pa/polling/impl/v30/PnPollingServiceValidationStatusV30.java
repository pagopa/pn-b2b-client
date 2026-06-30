package it.pagopa.pn.client.b2b.pa.polling.impl.v30;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV29;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NewNotificationRequestStatusResponseV26;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingTemplate;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV30;
import it.pagopa.pn.client.b2b.pa.polling.exception.PnPollingException;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;
import java.util.function.Predicate;


@Service(PnPollingStrategy.VALIDATION_STATUS_V30)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class PnPollingServiceValidationStatusV30 extends PnPollingTemplate<PnPollingResponseV30> {
    private final IPnPaB2bClient b2bClient;
    private NewNotificationRequestStatusResponseV26 requestStatusResponse;
    private FullSentNotificationV29 fullSentNotification;
    private final TimingForPolling timingForPolling;

    public PnPollingServiceValidationStatusV30(IPnPaB2bClient b2bClient, TimingForPolling timingForPolling) {
        this.b2bClient = b2bClient;
        this.timingForPolling = timingForPolling;
    }

    @Override
    protected Callable<PnPollingResponseV30> getPollingResponse(String id, PnPollingParameter pnPollingParameter) {
        return () -> {
            PnPollingResponseV30 pnPollingResponse = new PnPollingResponseV30();
            requestStatusResponse = b2bClient.getNotificationRequestStatusV26(id);
            pnPollingResponse.setStatusResponse(requestStatusResponse);

            if (pnPollingResponse.getStatusResponse().getIun() != null) {
                try {
                    fullSentNotification = b2bClient.getSentNotificationV29(pnPollingResponse.getStatusResponse().getIun());
                } catch (Exception exception) {
                    log.error("Error getPollingResponse(), Iun: {}, ApiKey: {}, PnPollingException: {}", pnPollingResponse.getStatusResponse().getIun(), b2bClient.getApiKeySetted().name(), exception.getMessage());
                    throw new PnPollingException(exception.getMessage());
                }
                pnPollingResponse.setNotification(fullSentNotification);
            }
            return pnPollingResponse;
        };
    }

    @Override
    protected Predicate<PnPollingResponseV30> checkCondition(String id, PnPollingParameter pnPollingParameter) {
        return pnPollingResponse -> {
            if (pnPollingResponse.getStatusResponse() == null) {
                pnPollingResponse.setResult(false);
                return false;
            }
            if (!pnPollingResponse.getStatusResponse().getNotificationRequestStatus().equalsIgnoreCase(pnPollingParameter.getValue().trim())) {
                pnPollingResponse.setResult(false);
                return false;
            }
            if (pnPollingResponse.getNotification() == null) {
                pnPollingResponse.setResult(false);
                return false;
            }
            pnPollingResponse.setResult(true);
            return true;
        };
    }

    @Override
    protected PnPollingResponseV30 getException(Exception exception) {
        PnPollingResponseV30 pollingResponse = new PnPollingResponseV30();
        pollingResponse.setStatusResponse(requestStatusResponse);
        pollingResponse.setNotification(fullSentNotification);
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
        return b2bClient.setApiKeys(apiKey);
    }

    @Override
    public void setApiKey(String apiKeyString) {
        b2bClient.setApiKey(apiKeyString);
    }

    @Override
    public ApiKeyType getApiKeySetted() {
        return b2bClient.getApiKeySetted();
    }

    protected TimingForPolling getTimingForTimeline() {
        return timingForPolling;
    }


}
