package it.pagopa.pn.client.b2b.pa.polling.dto;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.ProgressResponseElementV24;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.ProgressResponseElementV25;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.ProgressResponseElement;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.ProgressResponseElementV26;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.ProgressResponseElementV27;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.ProgressResponseElementV23;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class PnPollingWebhook {
    private it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.TimelineElementCategoryV20 timelineElementCategoryV20;
    private it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.TimelineElementCategoryV23 timelineElementCategoryV23;
    private it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23 timelineElementCategoryV24;
    private it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23 timelineElementCategoryV25;
    private it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.TimelineElementCategoryV26 timelineElementCategoryV26;
    private it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.TimelineElementCategoryV26 timelineElementCategoryV27;

    private it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.NotificationStatus notificationStatusV20;
    private it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.NotificationStatus notificationStatusV23;
    private it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus notificationStatusV24;
    private it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatus notificationStatusV25;
    private it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.NotificationStatusV26 notificationStatusV26;
    private it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.NotificationStatusV26 notificationStatusV27;

    private List<ProgressResponseElement> progressResponseElementListV20;
    private List<ProgressResponseElementV23> progressResponseElementListV23;
    private List<ProgressResponseElementV24> progressResponseElementListV24;
    private List<ProgressResponseElementV25> progressResponseElementListV25;
    private List<ProgressResponseElementV26> progressResponseElementListV26;
    private List<ProgressResponseElementV27> progressResponseElementListV27;
}