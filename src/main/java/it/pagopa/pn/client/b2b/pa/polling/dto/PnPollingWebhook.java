package it.pagopa.pn.client.b2b.pa.polling.dto;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.ProgressResponseElementV24;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.ProgressResponseElement;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;


@Getter
@Setter
public class PnPollingWebhook {
    private it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.TimelineElementCategoryV20 timelineElementCategoryV20;
    private it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.NotificationStatus notificationStatusV20;
    private it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.TimelineElementCategoryV23 timelineElementCategoryV23;
    private it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.NotificationStatus notificationStatusV24;
    private LinkedList<ProgressResponseElement> progressResponseElementListV20;
    private LinkedList<ProgressResponseElementV24> progressResponseElementListV24;
}