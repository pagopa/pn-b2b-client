package it.pagopa.pn.client.b2b.pa.polling.impl.v24;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.ProgressResponseElementV24;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingTemplate;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV24;
import it.pagopa.pn.client.b2b.pa.polling.exception.PnPollingException;
import it.pagopa.pn.client.b2b.pa.service.IPnWebhookB2bClient;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Predicate;


@Service(PnPollingStrategy.WEBHOOK)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class PnPollingServiceWebhook extends PnPollingTemplate<PnPollingResponseV24> {
    private final IPnWebhookB2bClient webhookB2bClient;
    private final TimingForPolling timingForPolling;
    private List<ProgressResponseElementV24> progressResponseElementListV24;
    private String iun;


    public PnPollingServiceWebhook(TimingForPolling timingForPolling, IPnWebhookB2bClient webhookB2bClient) {
        this.timingForPolling = timingForPolling;
        this.webhookB2bClient = webhookB2bClient;
    }

    @Override
    protected Callable<PnPollingResponseV24> getPollingResponse(String iun, PnPollingParameter pnPollingParameter) {
        this.iun = iun;
        return () -> {
            PnPollingResponseV24 pnPollingResponse = new PnPollingResponseV24();
            ResponseEntity<List<ProgressResponseElementV24>> listResponseEntity;
            int deepCount = pnPollingParameter.getDeepCount();

            try {
                ++deepCount;
                pnPollingParameter.setDeepCount(deepCount);
                listResponseEntity = webhookB2bClient.consumeEventStreamHttpV24(pnPollingParameter.getStreamId(), pnPollingParameter.getLastEventId());
                progressResponseElementListV24 = listResponseEntity.getBody();
                pnPollingResponse.setProgressResponseElementList(listResponseEntity.getBody());
                log.info("ELEMENTI NEL WEBHOOK: " + Objects.requireNonNull(progressResponseElementListV24));
                if (deepCount >= 250) {
                    throw new PnPollingException("LOP: PROGRESS-ELEMENTS: " + progressResponseElementListV24
                            + " WEBHOOK: " + pnPollingParameter.getStreamId() + " IUN: " + iun + " DEEP: " + deepCount);
                }
            } catch (IllegalStateException illegalStateException) {
                if (deepCount == 249 || deepCount == 248 || deepCount == 247) {
                    throw new PnPollingException((illegalStateException.getMessage() + ("LOP: PROGRESS-ELEMENTS: " + progressResponseElementListV24
                            + " WEBHOOK: " + pnPollingParameter.getStreamId() + " IUN: " + iun + " DEEP: " + deepCount)));
                } else {
                    throw illegalStateException;
                }
            }
            return pnPollingResponse;
        };
    }

    @Override
    protected Predicate<PnPollingResponseV24> checkCondition(String iun, PnPollingParameter pnPollingParameter) {
        return pnPollingResponse -> {
            if (pnPollingResponse.getProgressResponseElementList() == null
                    || pnPollingResponse.getProgressResponseElementList().isEmpty()) {
                pnPollingResponse.setResult(false);
                return false;
            }

            selectLastEventId(pnPollingResponse, pnPollingParameter);
            if (!isWaitTerminated(pnPollingResponse, pnPollingParameter)) {
                pnPollingResponse.setResult(false);
                return false;
            }

            pnPollingResponse.setResult(true);
            return true;
        };
    }

    @Override
    protected PnPollingResponseV24 getException(Exception exception) {
        PnPollingResponseV24 pollingResponse = new PnPollingResponseV24();
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
        return timingResult.numCheck();
    }

    @Override
    public boolean setApiKeys(ApiKeyType apiKey) {
        return this.webhookB2bClient.setApiKeys(apiKey);
    }

    @Override
    public void setApiKey(String apiKeyString) {
        this.webhookB2bClient.setApiKey(apiKeyString);
    }

    @Override
    public ApiKeyType getApiKeySetted() {
        return this.webhookB2bClient.getApiKeySetted();
    }


    private boolean isWaitTerminated(PnPollingResponseV24 pnPollingResponse, PnPollingParameter pnPollingParameter) {
        ProgressResponseElementV24 progressResponseElementV24 = pnPollingResponse.getProgressResponseElementList()
                .stream()
                .map(progressResponseElement -> {
                    if (!pnPollingParameter.getPnPollingWebhook().getProgressResponseElementListV24().contains(progressResponseElement)) {
                        pnPollingParameter.getPnPollingWebhook().getProgressResponseElementListV24().addLast(progressResponseElement);
                    }
                    return progressResponseElement;
                })
                .filter(toCheckCondition(pnPollingParameter))
                .findAny()
                .orElse(null);
        if (progressResponseElementV24 != null) {
            pnPollingResponse.setProgressResponseElement(progressResponseElementV24);
            return true;
        }
        return false;
    }

    private void selectLastEventId(PnPollingResponseV24 pnPollingResponse, PnPollingParameter pnPollingParameter) {
        ProgressResponseElementV24 lastProgress = pnPollingResponse
                .getProgressResponseElementList()
                .stream()
                .reduce((prev, curr) -> prev.getEventId().compareTo(curr.getEventId()) < 0 ? curr : prev)
                .orElse(null);
        pnPollingParameter.setLastEventId(Objects.requireNonNull(lastProgress).getEventId());
    }

    private Predicate<ProgressResponseElementV24> toCheckCondition(PnPollingParameter pnPollingParameter) {
        return progressResponseElementV24 ->
                progressResponseElementV24.getIun() != null && progressResponseElementV24.getIun().equals(iun)
                        && progressResponseElementV24.getElement().getCategory() != null && progressResponseElementV24.getElement().getCategory().equals(pnPollingParameter.getPnPollingWebhook().getTimelineElementCategoryV23())
                        ||
                        progressResponseElementV24.getIun() != null && progressResponseElementV24.getIun().equals(iun)
                                && (progressResponseElementV24.getNewStatus() != null && (progressResponseElementV24.getNewStatus().equals(pnPollingParameter.getPnPollingWebhook().getNotificationStatusV23())));
    }
}