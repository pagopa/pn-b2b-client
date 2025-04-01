package it.pagopa.pn.client.b2b.pa.polling.impl.v28;

import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingTemplate;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV28;
import it.pagopa.pn.client.b2b.pa.polling.exception.PnPollingException;
import it.pagopa.pn.client.b2b.pa.service.IPnWebhookB2bClient;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v28.ProgressResponseElementV28;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Predicate;


@Service(PnPollingStrategy.WEBHOOK_V28)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class PnPollingServiceWebhookV28 extends PnPollingTemplate<PnPollingResponseV28> {
    private final IPnWebhookB2bClient webhookB2bClient;
    private final TimingForPolling timingForPolling;
    private List<ProgressResponseElementV28> progressResponseElementListV28;
    private String iun;


    public PnPollingServiceWebhookV28(TimingForPolling timingForPolling, IPnWebhookB2bClient webhookB2bClient) {
        this.timingForPolling = timingForPolling;
        this.webhookB2bClient = webhookB2bClient;
    }

    @Override
    protected Callable<PnPollingResponseV28> getPollingResponse(String iun, PnPollingParameter pnPollingParameter) {
        this.iun = iun;
        return () -> {
            PnPollingResponseV28 pnPollingResponse = new PnPollingResponseV28();
            ResponseEntity<List<ProgressResponseElementV28>> listResponseEntity;
            int deepCount = pnPollingParameter.getDeepCount();

            try {
                ++deepCount;
                pnPollingParameter.setDeepCount(deepCount);
                listResponseEntity = webhookB2bClient.consumeEventStreamHttpV28(pnPollingParameter.getStreamId(), pnPollingParameter.getLastEventId());
                progressResponseElementListV28 = listResponseEntity.getBody();
                pnPollingResponse.setProgressResponseElementList(listResponseEntity.getBody());
                log.info("ELEMENTI NEL WEBHOOK: " + Objects.requireNonNull(progressResponseElementListV28));
                if (deepCount >= 250) {
                    throw new PnPollingException("LOP: PROGRESS-ELEMENTS: " + progressResponseElementListV28
                            + " WEBHOOK: " + pnPollingParameter.getStreamId() + " IUN: " + iun + " DEEP: " + deepCount);
                }
            } catch (IllegalStateException illegalStateException) {
                if (deepCount == 249 || deepCount == 248 || deepCount == 247) {
                    throw new PnPollingException((illegalStateException.getMessage() + ("LOP: PROGRESS-ELEMENTS: " + progressResponseElementListV28
                            + " WEBHOOK: " + pnPollingParameter.getStreamId() + " IUN: " + iun + " DEEP: " + deepCount)));
                } else {
                    throw illegalStateException;
                }
            }
            return pnPollingResponse;
        };
    }

    @Override
    protected Predicate<PnPollingResponseV28> checkCondition(String iun, PnPollingParameter pnPollingParameter) {
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
    protected PnPollingResponseV28 getException(Exception exception) {
        PnPollingResponseV28 pollingResponse = new PnPollingResponseV28();
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


    private boolean isWaitTerminated(PnPollingResponseV28 pnPollingResponse, PnPollingParameter pnPollingParameter) {
        ProgressResponseElementV28 progressResponseElementV28 = pnPollingResponse.getProgressResponseElementList()
                .stream()
                .peek(progressResponseElement -> {
                    if (!pnPollingParameter.getPnPollingWebhook().getProgressResponseElementListV28().contains(progressResponseElement)) {
                        pnPollingParameter.getPnPollingWebhook().getProgressResponseElementListV28().add(progressResponseElement);
                    }
                })
                .filter(toCheckCondition(pnPollingParameter))
                .findAny()
                .orElse(null);
        if (progressResponseElementV28 != null) {
            pnPollingResponse.setProgressResponseElement(progressResponseElementV28);
            return true;
        }
        return false;
    }

    private void selectLastEventId(PnPollingResponseV28 pnPollingResponse, PnPollingParameter pnPollingParameter) {
        ProgressResponseElementV28 lastProgress = pnPollingResponse
                .getProgressResponseElementList()
                .stream()
                .reduce((prev, curr) -> prev.getEventId().compareTo(curr.getEventId()) < 0 ? curr : prev)
                .orElse(null);
        pnPollingParameter.setLastEventId(Objects.requireNonNull(lastProgress).getEventId());
    }

    private Predicate<ProgressResponseElementV28> toCheckCondition(PnPollingParameter pnPollingParameter) {
        return progressResponseElementV28 ->
                progressResponseElementV28.getIun() != null
                        && progressResponseElementV28.getIun().equals(iun)
                        && progressResponseElementV28.getElement().getCategory() != null
                        && progressResponseElementV28.getElement().getCategory().equals(
                        pnPollingParameter.getPnPollingWebhook().getTimelineElementCategoryV28())
                        || progressResponseElementV28.getIun() != null
                        && progressResponseElementV28.getIun().equals(iun)
                        && (progressResponseElementV28.getNewStatus() != null
                        && (progressResponseElementV28.getNewStatus().equals(pnPollingParameter.getPnPollingWebhook().getNotificationStatusV28())));
    }
}