package it.pagopa.pn.client.b2b.pa.mapper.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactCategory;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV23;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV26;
import it.pagopa.pn.client.b2b.pa.mapper.model.PnTimelineLegalFactV26;
import it.pagopa.pn.client.b2b.pa.service.IPnTimelineLegalFactService;


public class PnTimelineAndLegalFactV26 implements IPnTimelineLegalFactService<PnTimelineLegalFactV26> {

    public PnTimelineLegalFactV26 getCategory(String legalFactCategory) {
        TimelineElementCategoryV26 timelineElementInternalCategory;
        LegalFactCategory category;
        switch (legalFactCategory) {
            case "SENDER_ACK" -> {
                timelineElementInternalCategory = TimelineElementCategoryV26.REQUEST_ACCEPTED;
                category = LegalFactCategory.SENDER_ACK;
            }
            case "RECIPIENT_ACCESS" -> {
                timelineElementInternalCategory = TimelineElementCategoryV26.NOTIFICATION_VIEWED;
                category = LegalFactCategory.RECIPIENT_ACCESS;
            }
            case "PEC_RECEIPT" -> {
                timelineElementInternalCategory = TimelineElementCategoryV26.SEND_DIGITAL_PROGRESS;
                category = LegalFactCategory.PEC_RECEIPT;
            }
            case "DIGITAL_DELIVERY" -> {
                timelineElementInternalCategory = TimelineElementCategoryV26.DIGITAL_SUCCESS_WORKFLOW;
                category = LegalFactCategory.DIGITAL_DELIVERY;
            }
            case "DIGITAL_DELIVERY_FAILURE" -> {
                timelineElementInternalCategory = TimelineElementCategoryV26.DIGITAL_FAILURE_WORKFLOW;
                category = LegalFactCategory.DIGITAL_DELIVERY;
            }
            case "SEND_ANALOG_PROGRESS" -> {
                timelineElementInternalCategory = TimelineElementCategoryV26.SEND_ANALOG_PROGRESS;
                category = LegalFactCategory.ANALOG_DELIVERY;
            }
            case "COMPLETELY_UNREACHABLE" -> {
                timelineElementInternalCategory = TimelineElementCategoryV26.COMPLETELY_UNREACHABLE;
                category = LegalFactCategory.ANALOG_FAILURE_DELIVERY;
            }
            default -> throw new IllegalArgumentException();
        }
        PnTimelineLegalFactV26 pnTimelineLegalFactV26 = new PnTimelineLegalFactV26();
        pnTimelineLegalFactV26.setTimelineElementInternalCategory(timelineElementInternalCategory);
        pnTimelineLegalFactV26.setLegalFactCategory(category);

        return pnTimelineLegalFactV26;
    }


}
