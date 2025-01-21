package it.pagopa.pn.cucumber.utils;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatusHistoryElement;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingStrategy;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingParameter;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponseV23;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingWebhook;
import it.pagopa.pn.client.b2b.pa.polling.impl.PnPollingServiceWebhookV23;
import it.pagopa.pn.client.b2b.pa.utils.TimingForPolling;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.NotificationStatus;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.ProgressResponseElementV23;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.StreamMetadataResponseV23;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheWebhookB2bSteps;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class EventStreamReaderV23 extends AbstractEventStreamReader {

    public EventStreamReaderV23(TimingForPolling timingForPolling, SharedSteps sharedSteps) {
        super(timingForPolling, sharedSteps);
    }

//    @Override
//    <T> StatusElementSearchResult<T> getStatusEventForStream(String notificationStatusName) {
//        TimingForPolling.TimingResult timingResult = getTimingForElement(notificationStatusName);
//        StatusElementSearchResult<NotificationStatus> result = new StatusElementSearchResult<>();
//        result.setNotificationStatus(NotificationStatus.valueOf(notificationStatusName));
//        result.setWaiting(timingResult.waiting());
//        result.setNumCheck(timingResult.numCheck());
//        return (StatusElementSearchResult<T>) result;
//    }

    @Override
    int getNumCheck(String notificationStatusName) {
        return getTimingForElement(notificationStatusName).numCheck();
    }

    @Override
    int getWaiting(String notificationStatusName) {
        return getTimingForElement(notificationStatusName).waiting();
    }

    @Override
    void setSentNotification() {
        getSharedSteps().setSentNotificationV23(getB2bClient().getSentNotificationV23(getSharedSteps().getSentNotification().getIun()));
    }

    @Override
    boolean verifyNotificationStatusHistoryElement(String notificationStatusName) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus notificationInternalStatus =
                it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus.valueOf(notificationStatusName);
        return getSharedSteps().getSentNotificationV23().getNotificationStatusHistory().stream().filter(elem -> elem.getStatus().equals(notificationInternalStatus)).findAny().isPresent();
    }

    @Override
    boolean verifyProgressResponseElement(Object progressResponseElementV23) {
        log.debug("PROGRESS-ELEMENT: " + progressResponseElementV23);
        return (ProgressResponseElementV23) progressResponseElementV23 != null;
    }

    @Override
    public void printErrorLog() {

    }

    @Override
    public <T, R> Object searchInWebhook(Object pnPollingService, String notificationStatusName, LinkedList<R> progressResponseElementList, List<T> eventStreamList, int position) {
        PnPollingWebhook pnPollingWebhook = new PnPollingWebhook();
        LinkedList<ProgressResponseElementV23> progressResponse = ((LinkedList<ProgressResponseElementV23>) progressResponseElementList);
        pnPollingWebhook.setNotificationStatusV23(NotificationStatus.valueOf(notificationStatusName));
        progressResponse.clear();
        pnPollingWebhook.setProgressResponseElementListV23(progressResponse);

//        PnPollingServiceWebhookV23 webhookV23 = (PnPollingServiceWebhookV23) getSharedSteps().getPollingFactory().getPollingService(PnPollingStrategy.WEBHOOK_V23);
        PnPollingServiceWebhookV23 webhookV23 = (PnPollingServiceWebhookV23) pnPollingService;
        PnPollingResponseV23 pnPollingResponseV23 = webhookV23.waitForEvent(getSharedSteps().getSentNotification().getIun(),
                PnPollingParameter.builder()
                        .value("WEBHOOK")
                        .pnPollingWebhook(pnPollingWebhook)
                        .deepCount(0)
                        .streamId(((List<StreamMetadataResponseV23>)eventStreamList).get(position).getStreamId())
                        .build());

        log.info("WEBHOOK_PROGRESS_RESPONSE_ELEMENT_V23: " + pnPollingResponseV23.getProgressResponseElementV23());
        if (pnPollingResponseV23.getProgressResponseElementListV23() != null) {
            getSharedSteps().setProgressResponseElementsV23(pnPollingResponseV23.getProgressResponseElementListV23());
            return pnPollingResponseV23.getProgressResponseElementV23();
        }
        return null;

    }

}
