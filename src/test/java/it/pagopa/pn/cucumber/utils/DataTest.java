package it.pagopa.pn.cucumber.utils;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV27;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class DataTest {
    private TimelineElementV27 timelineElement;
    private boolean isFirstSendRetry;
    private Integer progressIndex;
    private Integer pollingTime;
    private String pollingType;
    private Integer numCheck;
    private boolean loadTimeline;

    public boolean getIsFirstSendRetry() {
        return isFirstSendRetry;
    }
    public void setFirstSendRetry(boolean firstSendRetry) {
        isFirstSendRetry = firstSendRetry;
    }

    public boolean getLoadTimeline() {
        return loadTimeline;
    }
}