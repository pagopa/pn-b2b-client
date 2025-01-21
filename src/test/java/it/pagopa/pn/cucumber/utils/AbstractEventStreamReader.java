package it.pagopa.pn.cucumber.utils;

import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingWebhook;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.ProgressResponseElementV23;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.StreamMetadataResponseV23;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheWebhookB2bSteps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public abstract class AbstractEventStreamReader {
    private final TimingForPolling timingForPolling;
    private final SharedSteps sharedSteps;
    private final IPnPaB2bClient b2bClient;

    public AbstractEventStreamReader(TimingForPolling timingForPolling, SharedSteps sharedSteps) {
        this.timingForPolling = timingForPolling;
        this.sharedSteps = sharedSteps;
        this.b2bClient = sharedSteps.getB2bClient();
    }

//    @Data
//    public static class StatusElementSearchResult<T> {
//        public T notificationStatus;
//        int numCheck;
//        int waiting;
//    }


    public <T, R> void readEventStream(Object pnPollingService, String notificationStatusName, LinkedList<R> progressResponseElementList, List<T> eventStreamList, int position) {
        boolean finded = false;
        Object progressResponseElement = null;
        for (int i = 0; i < getNumCheck(notificationStatusName); i++) {
            sleepTest(Long.valueOf(getWaiting(notificationStatusName)));
            setSentNotification();
            if (verifyNotificationStatusHistoryElement(notificationStatusName)) {
                finded = true;
                break;
            }
        }
        Assertions.assertTrue(finded);
        for (int i = 0; i < 4; i++) {
            progressResponseElement = searchInWebhook(pnPollingService, notificationStatusName, progressResponseElementList, eventStreamList, position);
            if (progressResponseElement != null) {
                break;
            }
            sleepTest(Long.valueOf(sharedSteps.getWait()));
        }
        Assertions.assertNotNull(progressResponseElement);
        log.info("EventProgress: " + progressResponseElement);


    }

//    abstract <T> StatusElementSearchResult<T> getStatusEventForStream(String notificationStatusName);
    abstract int getNumCheck(String notificationStatusName);
    abstract int getWaiting(String notificationStatusName);
    abstract void setSentNotification();
    abstract boolean verifyNotificationStatusHistoryElement(String notificationStatusName);
    abstract boolean verifyProgressResponseElement(Object progressResponseElement);
    abstract void printErrorLog();
    abstract  <T, R> Object searchInWebhook(Object pnPollingService, String notificationStatusName, LinkedList<R> progressResponseElementList, List<T> eventStreamList, int position);

    private void sleepTest(long wait) {
        try {
            Thread.sleep(wait);
        } catch (InterruptedException exc) {
            throw new RuntimeException(exc);
        }
    }

    protected TimingForPolling.TimingResult getTimingForElement(String notificationStatus) {
        return timingForPolling.getTimingForElement(notificationStatus);
    }

    protected SharedSteps getSharedSteps() {
        return sharedSteps;
    }

    protected IPnPaB2bClient getB2bClient() {
        return b2bClient;
    }


}
