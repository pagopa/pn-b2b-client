package it.pagopa.pn.client.b2b.pa.polling.impl.v20;

import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingTemplate;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV20;
import it.pagopa.pn.client.b2b.pa.polling.exception.PnPollingException;
import it.pagopa.pn.client.b2b.pa.service.IPnWebhookB2bClient;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.ProgressResponseElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Predicate;


@Service(PnPollingStrategy.WEBHOOK_V20)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class PnPollingServiceWebhookV20 extends PnPollingTemplate<PnPollingResponseV20> {
    private final IPnWebhookB2bClient webhookB2bClient;
    private final TimingForPolling timingForPolling;
    private List<ProgressResponseElement> progressResponseElementList;
    private String iun;

    public PnPollingServiceWebhookV20(TimingForPolling timingForPolling, IPnWebhookB2bClient webhookB2bClient) {
        this.timingForPolling = timingForPolling;
        this.webhookB2bClient = webhookB2bClient;
    }

    @Override
    protected Callable<PnPollingResponseV20> getPollingResponse(String iun, PnPollingParameter pnPollingParameter) {
        this.iun = iun;
        return () -> {
            PnPollingResponseV20 pnPollingResponse = new PnPollingResponseV20();
            ResponseEntity<List<ProgressResponseElement>> listResponseEntity;
            int deepCount = pnPollingParameter.getDeepCount();

            try {
                ++deepCount;
                pnPollingParameter.setDeepCount(deepCount);
                listResponseEntity = webhookB2bClient.consumeEventStreamHttp(pnPollingParameter.getStreamId(), pnPollingParameter.getLastEventId());
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
    protected Predicate<PnPollingResponseV20> checkCondition(String iun, PnPollingParameter pnPollingParameter) {
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
    protected PnPollingResponseV20 getException(Exception exception) {
        PnPollingResponseV20 pollingResponse = new PnPollingResponseV20();
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

    private boolean isWaitTerminated(PnPollingResponseV20 pnPollingResponse, PnPollingParameter pnPollingParameter) {
        ProgressResponseElement progressResponseElement = pnPollingResponse.getProgressResponseElementList()
                .stream()
                .peek(pre -> {
                    if (!pnPollingParameter.getPnPollingWebhook().getProgressResponseElementListV20().contains(pre)) {
                        pnPollingParameter.getPnPollingWebhook().getProgressResponseElementListV20().add(pre);
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

    private void selectLastEventId(PnPollingResponseV20 pnPollingResponse, PnPollingParameter pnPollingParameter) {
        ProgressResponseElement lastProgress = pnPollingResponse
                .getProgressResponseElementList()
                .stream()
                .reduce((prev, curr) -> prev.getEventId().compareTo(curr.getEventId()) < 0 ? curr : prev)
                .orElse(null);
        pnPollingParameter.setLastEventId(Objects.requireNonNull(lastProgress).getEventId());
    }

    //TODO TULLIO 1
    /*
    Dopo aver aggiornato il pom di webhook (pom unico anziché tanti duplicati), ho notato che iniziavano a fallire i test del WebhookV10
    Nel vecchio yaml del webhook V10
    https://raw.githubusercontent.com/pagopa/pn-delivery-push/v2.3.2/docs/openapi/api-external-b2b-webhook.yaml
    se cerchi ProgressResponseElement' (con l'apice alla fine) vedrai che utilizza la TimelineElementCategoryV20 (corretta).
    Qua nel pom unico
    https://raw.githubusercontent.com/pagopa/pn-delivery-push/56314229c57ea544dd0f7678ce61ba4e5de180f5/docs/openapi/api-external-b2b-webhook-bundle.yaml
    facendo la stessa ricerca esce invece che utilizza la TimelineElementCategoryV23 (sbagliata).
    Questo causava i fail delle PollingResponse (non erano ritardi).
    Andrebbe segnalato ai dev che va cambiato lo yaml, in modo che webhookV20 restituisca una TimelineElementCategoryV20.
    Una volta sistemato, i seguenti test dovrebbero andare in OK (vanno già in OK con la toppa che ho messo)
    [B2B-STREAM_ES1.2_126]
    [B2B-STREAM_ES1.3_162]
    */
    private Predicate<ProgressResponseElement> toCheckCondition(PnPollingParameter pnPollingParameter) {
        return progressResponseElement ->
                progressResponseElement.getIun() != null
                        && progressResponseElement.getIun().equals(iun)
                        && progressResponseElement.getTimelineEventCategory() != null
                        //TODO: questo falliva
//                        && progressResponseElement.getTimelineEventCategory().equals(
//                        pnPollingParameter.getPnPollingWebhook().getTimelineElementCategoryV23())
                        //TODO: con questo ci mettiamo una toppa, ma non è del tutto corretto
                        && progressResponseElement.getTimelineEventCategory().getValue().equals(
                        pnPollingParameter.getPnPollingWebhook().getTimelineElementCategoryV20().getValue())
                        //TODO: questo è come dovrebbe essere (una volta sistemato il pom, scommentare e rimuovere quelli sopra)
//                        && progressResponseElement.getTimelineEventCategory().equals(
//                        pnPollingParameter.getPnPollingWebhook().getTimelineElementCategoryV20())
                        || progressResponseElement.getIun() != null
                        && progressResponseElement.getIun().equals(iun)
                        && (progressResponseElement.getNewStatus() != null
                        && (progressResponseElement.getNewStatus().equals(pnPollingParameter.getPnPollingWebhook().getNotificationStatusV20())));
    }
}