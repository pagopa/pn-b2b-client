package it.pagopa.pn.cucumber.utils;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV20;

public class TimelineElementWait {

    private TimelineElementCategoryV20 timelineElementCategory;
    private it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory timelineElementCategoryV1;
    private it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.TimelineElementCategoryV20 timelineElementCategoryV2;
    private Integer numCheck;
    private Integer waiting;

    public TimelineElementWait(TimelineElementCategoryV20 timelineElementCategory, Integer numCheck, Integer waiting) {
        this.timelineElementCategory = timelineElementCategory;
        this.numCheck = numCheck;
        this.waiting = waiting;
    }

    public TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory timelineElementCategoryV1, Integer numCheck, Integer waiting) {
        this.timelineElementCategoryV1 = timelineElementCategoryV1;
        this.numCheck = numCheck;
        this.waiting = waiting;
    }

    public TimelineElementWait(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.TimelineElementCategoryV20 timelineElementCategoryV2, Integer numCheck, Integer waiting) {
        this.timelineElementCategoryV2 = timelineElementCategoryV2;
        this.numCheck = numCheck;
        this.waiting = waiting;
    }

    public TimelineElementCategoryV20 getTimelineElementCategory() {
        return timelineElementCategory;
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory getTimelineElementCategoryV1() {
        return timelineElementCategoryV1;
    }

    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.TimelineElementCategoryV20 getTimelineElementCategoryV2() {
        return timelineElementCategoryV2;
    }

    public void setTimelineElementCategory(TimelineElementCategoryV20 timelineElementCategory) {
        this.timelineElementCategory = timelineElementCategory;
    }

    public void setTimelineElementCategory(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElementCategory timelineElementCategoryV1) {
        this.timelineElementCategoryV1 = timelineElementCategoryV1;
    }

    public void setTimelineElementCategory(it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.TimelineElementCategoryV20 timelineElementCategoryV2) {
        this.timelineElementCategoryV2 = timelineElementCategoryV2;
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
