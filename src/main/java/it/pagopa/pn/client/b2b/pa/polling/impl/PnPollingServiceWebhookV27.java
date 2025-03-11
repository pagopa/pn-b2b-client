package it.pagopa.pn.client.b2b.pa.polling.impl;

import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingTemplate;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV27;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingWebhook;
import it.pagopa.pn.client.b2b.pa.polling.exception.PnPollingException;
import it.pagopa.pn.client.b2b.pa.service.IPnWebhookB2bClient;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.ProgressResponseElementV27;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.TimelineElementCategoryV26;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Predicate;


@Service(PnPollingStrategy.WEBHOOK_V27)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class PnPollingServiceWebhookV27 extends PnPollingTemplate<PnPollingResponseV27> {
    private final IPnWebhookB2bClient webhookB2bClient;
    private final TimingForPolling timingForPolling;
    private List<ProgressResponseElementV27> progressResponseElementListV27;
    private String iun;


    public PnPollingServiceWebhookV27(TimingForPolling timingForPolling, IPnWebhookB2bClient webhookB2bClient) {
        this.timingForPolling = timingForPolling;
        this.webhookB2bClient = webhookB2bClient;
    }

    @Override
    protected Callable<PnPollingResponseV27> getPollingResponse(String iun, PnPollingParameter pnPollingParameter) {
        this.iun = iun;
        return () -> {
            PnPollingResponseV27 pnPollingResponse = new PnPollingResponseV27();
            ResponseEntity<List<ProgressResponseElementV27>> listResponseEntity;
            int deepCount = pnPollingParameter.getDeepCount();

            try {
                ++deepCount;
                pnPollingParameter.setDeepCount(deepCount);
                listResponseEntity = webhookB2bClient.consumeEventStreamHttpV27(pnPollingParameter.getStreamId(), pnPollingParameter.getLastEventId());
                progressResponseElementListV27 = listResponseEntity.getBody();
                pnPollingResponse.setProgressResponseElementListV27(listResponseEntity.getBody());
                log.info("ELEMENTI NEL WEBHOOK: " + Objects.requireNonNull(progressResponseElementListV27));
                if (deepCount >= 250) {
                    throw new PnPollingException("LOP: PROGRESS-ELEMENTS: " + progressResponseElementListV27
                            + " WEBHOOK: " + pnPollingParameter.getStreamId() + " IUN: " + iun + " DEEP: " + deepCount);
                }
            } catch (IllegalStateException illegalStateException) {
                if (deepCount == 249 || deepCount == 248 || deepCount == 247) {
                    throw new PnPollingException((illegalStateException.getMessage() + ("LOP: PROGRESS-ELEMENTS: " + progressResponseElementListV27
                            + " WEBHOOK: " + pnPollingParameter.getStreamId() + " IUN: " + iun + " DEEP: " + deepCount)));
                } else {
                    throw illegalStateException;
                }
            }
            return pnPollingResponse;
        };
    }

    @Override
    protected Predicate<PnPollingResponseV27> checkCondition(String iun, PnPollingParameter pnPollingParameter) {
        return pnPollingResponse -> {
            if (pnPollingResponse.getProgressResponseElementListV27() == null
                    || pnPollingResponse.getProgressResponseElementListV27().isEmpty()) {
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
    protected PnPollingResponseV27 getException(Exception exception) {
        PnPollingResponseV27 pollingResponse = new PnPollingResponseV27();
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


    private boolean isWaitTerminated(PnPollingResponseV27 pnPollingResponse, PnPollingParameter pnPollingParameter) {
        ProgressResponseElementV27 progressResponseElementV27 = pnPollingResponse.getProgressResponseElementListV27()
                .stream()
                .peek(progressResponseElement -> {
                    if (!pnPollingParameter.getPnPollingWebhook().getProgressResponseElementListV27().contains(progressResponseElement)) {
                        pnPollingParameter.getPnPollingWebhook().getProgressResponseElementListV27().add(progressResponseElement);
                    }
                })
                .filter(toCheckCondition(pnPollingParameter))
                .findAny()
                .orElse(null);
        if (progressResponseElementV27 != null) {
            pnPollingResponse.setProgressResponseElementV27(progressResponseElementV27);
            return true;
        }
        return false;
    }

    private void selectLastEventId(PnPollingResponseV27 pnPollingResponse, PnPollingParameter pnPollingParameter) {
        ProgressResponseElementV27 lastProgress = pnPollingResponse
                .getProgressResponseElementListV27()
                .stream()
                .reduce((prev, curr) -> prev.getEventId().compareTo(curr.getEventId()) < 0 ? curr : prev)
                .orElse(null);
        pnPollingParameter.setLastEventId(Objects.requireNonNull(lastProgress).getEventId());
    }

    private Predicate<ProgressResponseElementV27> toCheckCondition(PnPollingParameter pnPollingParameter) {
        return progressResponseElementV27 ->
                progressResponseElementV27.getIun() != null && progressResponseElementV27.getIun().equals(iun)
                        && progressResponseElementV27.getElement().getCategory() != null && progressResponseElementV27.getElement().getCategory().getValue().equals(Optional.ofNullable(pnPollingParameter.getPnPollingWebhook()).map(PnPollingWebhook::getTimelineElementCategoryV27).map(TimelineElementCategoryV26::getValue).orElse(null))
                        ||
                        progressResponseElementV27.getIun() != null && progressResponseElementV27.getIun().equals(iun)
                        && (progressResponseElementV27.getNewStatus() != null && (progressResponseElementV27.getNewStatus().equals(pnPollingParameter.getPnPollingWebhook().getNotificationStatusV27())));
    }
}