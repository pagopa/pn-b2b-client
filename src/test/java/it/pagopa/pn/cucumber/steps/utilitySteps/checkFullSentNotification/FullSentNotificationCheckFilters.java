package it.pagopa.pn.cucumber.steps.utilitySteps.checkFullSentNotification;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
public class FullSentNotificationCheckFilters {

    private Map<String, String> timelineElementMap;
    private int size;

}
