package it.pagopa.pn.client.b2b.pa.polling.impl.v30;

import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingTemplate;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV30;
import it.pagopa.pn.client.b2b.pa.polling.exception.PnPollingException;
import it.pagopa.pn.client.b2b.pa.service.IPnWebhookB2bClient;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.ProgressResponseElementV29;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Predicate;


@Service(PnPollingStrategy.WEBHOOK_V30)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class PnPollingServiceWebhookV30 extends PnPollingTemplate<PnPollingResponseV30> {
    private final IPnWebhookB2bClient webhookB2bClient;
    private final TimingForPolling timingForPolling;
    private List<ProgressResponseElementV29> progressResponseElementList;
    private String iun;

    public PnPollingServiceWebhookV30(TimingForPolling timingForPolling, IPnWebhookB2bClient webhookB2bClient) {
        this.timingForPolling = timingForPolling;
        this.webhookB2bClient = webhookB2bClient;
    }

    @Override
    protected Callable<PnPollingResponseV30> getPollingResponse(String iun, PnPollingParameter pnPollingParameter) {
        this.iun = iun;
        return () -> {
            PnPollingResponseV30 pnPollingResponse = new PnPollingResponseV30();
            ResponseEntity<List<ProgressResponseElementV29>> listResponseEntity;
            int deepCount = pnPollingParameter.getDeepCount();
            try {
                ++deepCount;
                pnPollingParameter.setDeepCount(deepCount);
                listResponseEntity = webhookB2bClient.consumeEventStreamHttpV29(pnPollingParameter.getStreamId(), pnPollingParameter.getLastEventId());
                progressResponseElementList = listResponseEntity.getBody();
                pnPollingResponse.setProgressResponseElementList(listResponseEntity.getBody());
                log.info("ELEMENTI NEL WEBHOOK: " + Objects.requireNonNull(progressResponseElementList));
                if (deepCount >= 250) {
                    throw new PnPollingException("LOP: PROGRESS-ELEMENTS: " + progressResponseElementList
                            + " WEBHOOK: " + pnPollingParameter.getStreamId() + " IUN: " + iun + " DEEP: " + deepCount);
                }
            } catch (IllegalStateException illegalStateException) {
                if (deepCount == 249 || deepCount == 248 || deepCount == 247) {
                    throw new PnPollingException((illegalStateException.getMessage() + ("LOP: PROGRESS-ELEMENTS: " + progressResponseElementList
                            + " WEBHOOK: " + pnPollingParameter.getStreamId() + " IUN: " + iun + " DEEP: " + deepCount)));
                } else {
                    throw illegalStateException;
                }
            }
            return pnPollingResponse;
        };
    }

    @Override
    protected Predicate<PnPollingResponseV30> checkCondition(String iun, PnPollingParameter pnPollingParameter) {
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
    protected PnPollingResponseV30 getException(Exception exception) {
        PnPollingResponseV30 pollingResponse = new PnPollingResponseV30();
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
        return webhookB2bClient.setApiKeys(apiKey);
    }

    @Override
    public void setApiKey(String apiKeyString) {
        webhookB2bClient.setApiKey(apiKeyString);
    }

    @Override
    public ApiKeyType getApiKeySetted() {
        return webhookB2bClient.getApiKeySetted();
    }


    private boolean isWaitTerminated(PnPollingResponseV30 pnPollingResponse, PnPollingParameter pnPollingParameter) {
        ProgressResponseElementV29 progressResponseElement = pnPollingResponse.getProgressResponseElementList()
                .stream()
                .peek(pre -> {
                    if (!pnPollingParameter.getPnPollingWebhook().getProgressResponseElementListV29().contains(pre)) {
                        pnPollingParameter.getPnPollingWebhook().getProgressResponseElementListV29().add(pre);
                    }
                })
                .filter(toCheckCondition(pnPollingParameter))
                .findAny()
                .orElse(null);
        if (progressResponseElement != null) {
            pnPollingResponse.setProgressResponseElement(progressResponseElement);
            return true;
        }
        return false;
    }

    private void selectLastEventId(PnPollingResponseV30 pnPollingResponse, PnPollingParameter pnPollingParameter) {
        ProgressResponseElementV29 lastProgress = pnPollingResponse
                .getProgressResponseElementList()
                .stream()
                .reduce((prev, curr) -> prev.getEventId().compareTo(curr.getEventId()) < 0 ? curr : prev)
                .orElse(null);
        pnPollingParameter.setLastEventId(Objects.requireNonNull(lastProgress).getEventId());
    }

    private Predicate<ProgressResponseElementV29> toCheckCondition(PnPollingParameter pnPollingParameter) {
        return progressResponseElement ->
                progressResponseElement.getIun() != null
                        && progressResponseElement.getIun().equals(iun)
                        && progressResponseElement.getElement().getCategory() != null
                        && progressResponseElement.getElement().getCategory().equals(
                        pnPollingParameter.getPnPollingWebhook().getTimelineElementCategoryV29())
                        || progressResponseElement.getIun() != null
                        && progressResponseElement.getIun().equals(iun)
                        && (progressResponseElement.getNewStatus() != null
                        && (progressResponseElement.getNewStatus().equals(pnPollingParameter.getPnPollingWebhook().getNotificationStatusV29())));
    }
}