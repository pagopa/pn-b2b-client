package it.pagopa.pn.client.b2b.pa.polling.dto;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.ProgressResponseElementV23;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.ProgressResponseElementV24;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;


@Getter
@Setter
public class PnPollingWebhook {
    private it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.NotificationStatus notificationStatusV20;
    private it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.TimelineElementCategoryV20 timelineElementCategoryV20;
    private LinkedList<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.ProgressResponseElement> progressResponseElementListV20;
    private NotificationStatus notificationStatusV23;
    private TimelineElementCategoryV23 timelineElementCategoryV23;
    private LinkedList<ProgressResponseElementV23> progressResponseElementListV23;
    private LinkedList<ProgressResponseElementV24> progressResponseElementListV24;
}