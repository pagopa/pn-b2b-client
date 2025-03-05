package it.pagopa.pn.client.b2b.pa.polling.dto;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.ProgressResponseElementV24;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.ProgressResponseElementV25;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.ProgressResponseElement;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.ProgressResponseElementV26;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.ProgressResponseElementV27;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.ProgressResponseElementV23;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;


@Getter
@Setter
public class PnPollingWebhook {
    private it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.NotificationStatus notificationStatusV20;
    private it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.TimelineElementCategoryV20 timelineElementCategoryV20;
    private it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.TimelineElementCategoryV23 timelineElementCategoryV23;
    private it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23 timelineElementCategoryV24;
    private it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23 timelineElementCategoryV25;
    private it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26 timelineElementCategoryV26;
    private it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26 timelineElementCategoryV27;
    private it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.NotificationStatus notificationStatusV23;
    private it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.NotificationStatus notificationStatus_noVersionV26;
    private it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.NotificationStatusV26 notificationStatusV26;
    private it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.NotificationStatus notificationStatus_noVersionV27;
    private it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.NotificationStatusV26 notificationStatusV27;
    private LinkedList<ProgressResponseElement> progressResponseElementListV20;
    private LinkedList<ProgressResponseElementV23> progressResponseElementListV23;
    private LinkedList<ProgressResponseElementV24> progressResponseElementListV24;
    private LinkedList<ProgressResponseElementV25> progressResponseElementListV25;
    private LinkedList<ProgressResponseElementV26> progressResponseElementListV26;
    private LinkedList<ProgressResponseElementV27> progressResponseElementListV27;
}