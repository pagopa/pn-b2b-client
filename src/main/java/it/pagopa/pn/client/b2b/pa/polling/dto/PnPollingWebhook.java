package it.pagopa.pn.client.b2b.pa.polling.dto;

import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class PnPollingWebhook {

    //TODO TULLIO 2: Una volta che i dev sistemano il pom, rimuovere l'import di TimelineElementCategoryV20 (va benissimo quello di externalb2bwebhook.model.*)

    private it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook_v20.model.TimelineElementCategoryV20 timelineElementCategoryV20;
    private TimelineElementCategoryV23 timelineElementCategoryV23;
    private TimelineElementCategoryV23 timelineElementCategoryV24;
    private TimelineElementCategoryV23 timelineElementCategoryV25;
    private TimelineElementCategoryV26 timelineElementCategoryV26;
    private TimelineElementCategoryV26 timelineElementCategoryV27;
    private TimelineElementCategoryV27 timelineElementCategoryV28;

    private it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook_v20.model.NotificationStatus notificationStatusV20;
    private NotificationStatus notificationStatusV23;
    private NotificationStatus notificationStatusV24;
    private NotificationStatus notificationStatusV25;
    private NotificationStatusV26 notificationStatusV26;
    private NotificationStatusV26 notificationStatusV27;
    private NotificationStatusV26 notificationStatusV28;

    private List<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook_v20.model.ProgressResponseElement> progressResponseElementListV20;
    private List<ProgressResponseElementV23> progressResponseElementListV23;
    private List<ProgressResponseElementV24> progressResponseElementListV24;
    private List<ProgressResponseElementV25> progressResponseElementListV25;
    private List<ProgressResponseElementV26> progressResponseElementListV26;
    private List<ProgressResponseElementV27> progressResponseElementListV27;
    private List<ProgressResponseElementV28> progressResponseElementListV28;
}