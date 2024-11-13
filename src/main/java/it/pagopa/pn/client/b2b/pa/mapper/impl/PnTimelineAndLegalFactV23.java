package it.pagopa.pn.client.b2b.pa.mapper.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactCategory;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23;
import it.pagopa.pn.client.b2b.pa.mapper.model.PnTimelineLegalFactV23;
import it.pagopa.pn.client.b2b.pa.service.IPnTimelineLegalFactService;


public class PnTimelineAndLegalFactV23 implements IPnTimelineLegalFactService<PnTimelineLegalFactV23> {

    public PnTimelineLegalFactV23 getCategory(String legalFactCategory) {
        TimelineElementCategoryV23 timelineElementInternalCategory;
        LegalFactCategory category;
        switch (legalFactCategory) {
            case "SENDER_ACK" -> {
                timelineElementInternalCategory = TimelineElementCategoryV23.REQUEST_ACCEPTED;
                category = LegalFactCategory.SENDER_ACK;
            }
            case "RECIPIENT_ACCESS" -> {
                timelineElementInternalCategory = TimelineElementCategoryV23.NOTIFICATION_VIEWED;
                category = LegalFactCategory.RECIPIENT_ACCESS;
            }
            case "PEC_RECEIPT" -> {
                timelineElementInternalCategory = TimelineElementCategoryV23.SEND_DIGITAL_PROGRESS;
                category = LegalFactCategory.PEC_RECEIPT;
            }
            case "DIGITAL_DELIVERY" -> {
                timelineElementInternalCategory = TimelineElementCategoryV23.DIGITAL_SUCCESS_WORKFLOW;
                category = LegalFactCategory.DIGITAL_DELIVERY;
            }
            case "DIGITAL_DELIVERY_FAILURE" -> {
                timelineElementInternalCategory = TimelineElementCategoryV23.DIGITAL_FAILURE_WORKFLOW;
                category = LegalFactCategory.DIGITAL_DELIVERY;
            }
            case "SEND_ANALOG_PROGRESS" -> {
                timelineElementInternalCategory = TimelineElementCategoryV23.SEND_ANALOG_PROGRESS;
                category = LegalFactCategory.ANALOG_DELIVERY;
            }
            case "COMPLETELY_UNREACHABLE" -> {
                timelineElementInternalCategory = TimelineElementCategoryV23.COMPLETELY_UNREACHABLE;
                category = LegalFactCategory.ANALOG_FAILURE_DELIVERY;
            }
            //introdotto con V25 //TODO MATTEO
            case "NOTIFICATION_CANCELLED" -> {
                timelineElementInternalCategory = TimelineElementCategoryV23.COMPLETELY_UNREACHABLE;
                category = LegalFactCategory.ANALOG_FAILURE_DELIVERY;
            }
            default -> throw new IllegalArgumentException();
        }
        PnTimelineLegalFactV23 pnTimelineLegalFactV23 = new PnTimelineLegalFactV23();
        pnTimelineLegalFactV23.setTimelineElementInternalCategory(timelineElementInternalCategory);
        pnTimelineLegalFactV23.setLegalFactCategory(category);

        return pnTimelineLegalFactV23;
    }


}
