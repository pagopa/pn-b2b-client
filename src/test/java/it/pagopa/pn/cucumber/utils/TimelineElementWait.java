package it.pagopa.pn.cucumber.utils;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategory;

public class TimelineElementWait {

    private TimelineElementCategory timelineElementCategory;
    private Integer numCheck;
    private Integer waiting;

    public TimelineElementWait(TimelineElementCategory timelineElementCategory, Integer numCheck, Integer waiting) {
        this.timelineElementCategory = timelineElementCategory;
        this.numCheck = numCheck;
        this.waiting = waiting;
    }

    public TimelineElementCategory getTimelineElementCategory() {
        return timelineElementCategory;
    }

    public void setTimelineElementCategory(TimelineElementCategory timelineElementCategory) {
        this.timelineElementCategory = timelineElementCategory;
    }

    public Integer getNumCheck() {
        return numCheck;
    }

    public void setNumCheck(Integer numCheck) {
        this.numCheck = numCheck;
    }

    public Integer getWaiting() {
        return waiting;
    }

    public void setWaiting(Integer waiting) {
        this.waiting = waiting;
    }
}
