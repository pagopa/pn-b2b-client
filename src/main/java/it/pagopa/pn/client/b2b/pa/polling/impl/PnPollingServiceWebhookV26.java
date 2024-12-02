package it.pagopa.pn.client.b2b.pa.polling.impl;

import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingTemplate;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV26;
import it.pagopa.pn.client.b2b.pa.polling.exception.PnPollingException;
import it.pagopa.pn.client.b2b.pa.service.IPnWebhookB2bClient;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.ProgressResponseElementV26;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Predicate;


@Service(PnPollingStrategy.WEBHOOK_V26)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class PnPollingServiceWebhookV26 extends PnPollingTemplate<PnPollingResponseV26> {
    private final IPnWebhookB2bClient webhookB2bClient;
    private final TimingForPolling timingForPolling;
    private List<ProgressResponseElementV26> progressResponseElementListV26;
    private String iun;


    public PnPollingServiceWebhookV26(TimingForPolling timingForPolling, IPnWebhookB2bClient webhookB2bClient) {
        this.timingForPolling = timingForPolling;
        this.webhookB2bClient = webhookB2bClient;
    }

    @Override
    protected Callable<PnPollingResponseV26> getPollingResponse(String iun, PnPollingParameter pnPollingParameter) {
        this.iun = iun;
        return () -> {
            PnPollingResponseV26 pnPollingResponse = new PnPollingResponseV26();
            ResponseEntity<List<ProgressResponseElementV26>> listResponseEntity;
            int deepCount = pnPollingParameter.getDeepCount();

            try {
                ++deepCount;
                pnPollingParameter.setDeepCount(deepCount);
                listResponseEntity = webhookB2bClient.consumeEventStreamHttpV26(pnPollingParameter.getStreamId(), pnPollingParameter.getLastEventId());
                progressResponseElementListV26 = listResponseEntity.getBody();
                pnPollingResponse.setProgressResponseElementListV26(listResponseEntity.getBody());
                log.info("ELEMENTI NEL WEBHOOK: " + Objects.requireNonNull(progressResponseElementListV26));
                if (deepCount >= 250) {
                    throw new PnPollingException("LOP: PROGRESS-ELEMENTS: " + progressResponseElementListV26
                            + " WEBHOOK: " + pnPollingParameter.getStreamId() + " IUN: " + iun + " DEEP: " + deepCount);
                }
            } catch (IllegalStateException illegalStateException) {
                if (deepCount == 249 || deepCount == 248 || deepCount == 247) {
                    throw new PnPollingException((illegalStateException.getMessage() + ("LOP: PROGRESS-ELEMENTS: " + progressResponseElementListV26
                            + " WEBHOOK: " + pnPollingParameter.getStreamId() + " IUN: " + iun + " DEEP: " + deepCount)));
                } else {
                    throw illegalStateException;
                }
            }
            return pnPollingResponse;
        };
    }

    @Override
    protected Predicate<PnPollingResponseV26> checkCondition(String iun, PnPollingParameter pnPollingParameter) {
        return pnPollingResponse -> {
            if (pnPollingResponse.getProgressResponseElementListV26() == null
                    || pnPollingResponse.getProgressResponseElementListV26().isEmpty()) {
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
    protected PnPollingResponseV26 getException(Exception exception) {
        PnPollingResponseV26 pollingResponse = new PnPollingResponseV26();
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


    private boolean isWaitTerminated(PnPollingResponseV26 pnPollingResponse, PnPollingParameter pnPollingParameter) {
        ProgressResponseElementV26 progressResponseElementV26 = pnPollingResponse.getProgressResponseElementListV26()
                .stream()
                .map(progressResponseElement -> {
                    if (!pnPollingParameter.getPnPollingWebhook().getProgressResponseElementListV26().contains(progressResponseElement)) {
                        pnPollingParameter.getPnPollingWebhook().getProgressResponseElementListV26().addLast(progressResponseElement);
                    }
                    return progressResponseElement;
                })
                .filter(toCheckCondition(pnPollingParameter))
                .findAny()
                .orElse(null);
        if (progressResponseElementV26 != null) {
            pnPollingResponse.setProgressResponseElementV26(progressResponseElementV26);
            return true;
        }
        return false;
    }

    private void selectLastEventId(PnPollingResponseV26 pnPollingResponse, PnPollingParameter pnPollingParameter) {
        ProgressResponseElementV26 lastProgress = pnPollingResponse
                .getProgressResponseElementListV26()
                .stream()
                .reduce((prev, curr) -> prev.getEventId().compareTo(curr.getEventId()) < 0 ? curr : prev)
                .orElse(null);
        pnPollingParameter.setLastEventId(Objects.requireNonNull(lastProgress).getEventId());
    }

    private Predicate<ProgressResponseElementV26> toCheckCondition(PnPollingParameter pnPollingParameter) {
        return progressResponseElementV26 ->
                progressResponseElementV26.getIun() != null && progressResponseElementV26.getIun().equals(iun)
                        && progressResponseElementV26.getElement().getCategory() != null && progressResponseElementV26.getElement().getCategory().equals(pnPollingParameter.getPnPollingWebhook().getTimelineElementCategoryV23())
                        ||
                        progressResponseElementV26.getIun() != null && progressResponseElementV26.getIun().equals(iun)
                                && (progressResponseElementV26.getNewStatus() != null && (progressResponseElementV26.getNewStatus().equals(pnPollingParameter.getPnPollingWebhook().getNotificationStatusV23())));
    }
}