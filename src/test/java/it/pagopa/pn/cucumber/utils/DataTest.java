package it.pagopa.pn.cucumber.utils;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV20;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV20;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV23;

public class DataTest {
    TimelineElementV23 timelineElement;
    boolean isFirstSendRetry;
    Integer progressIndex;
    Integer pollingTime;
    Integer numCheck;
    boolean loadTimeline;

    public boolean getIsFirstSendRetry() {
        return isFirstSendRetry;
    }

    public void setFirstSendRetry(boolean firstSendRetry) {
        isFirstSendRetry = firstSendRetry;
    }

    public TimelineElementV23 getTimelineElement() {
        return timelineElement;
    }

    public void setTimelineElement(TimelineElementV23 timelineElement) {
        this.timelineElement = timelineElement;
    }

    public Integer getProgressIndex() {
        return progressIndex;
    }

    public void setProgressIndex(Integer progressIndex) {
        this.progressIndex = progressIndex;
    }

    public Integer getPollingTime() {
        return pollingTime;
    }

    public void setPollingTime(Integer pollingTime) {
        this.pollingTime = pollingTime;
    }

    public Integer getNumCheck() {
        return numCheck;
    }

    public void setNumCheck(Integer numCheck) {
        this.numCheck = numCheck;
    }

    public boolean getLoadTimeline() {
        return loadTimeline;
    }

    public void setLoadTimeline(boolean loadTimeline) {
        this.loadTimeline = loadTimeline;
    }
}
