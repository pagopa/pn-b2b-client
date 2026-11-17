package it.pagopa.pn.client.b2b.pa.mapper.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactCategory;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV28;
import it.pagopa.pn.client.b2b.pa.mapper.model.PnTimelineLegalFactV28;
import it.pagopa.pn.client.b2b.pa.service.IPnTimelineLegalFactService;


public class PnTimelineAndLegalFactV28 implements IPnTimelineLegalFactService<PnTimelineLegalFactV28> {

    public PnTimelineLegalFactV28 getCategory(String legalFactCategory) {
        TimelineElementCategoryV28 timelineElementInternalCategory;
        LegalFactCategory category;
        switch (legalFactCategory) {
            case "SENDER_ACK" -> {
                timelineElementInternalCategory = TimelineElementCategoryV28.REQUEST_ACCEPTED;
                category = LegalFactCategory.SENDER_ACK;
            }
            case "RECIPIENT_ACCESS" -> {
                timelineElementInternalCategory = TimelineElementCategoryV28.NOTIFICATION_VIEWED;
                category = LegalFactCategory.RECIPIENT_ACCESS;
            }
            case "PEC_RECEIPT" -> {
                timelineElementInternalCategory = TimelineElementCategoryV28.SEND_DIGITAL_PROGRESS;
                category = LegalFactCategory.PEC_RECEIPT;
            }
            case "DIGITAL_DELIVERY" -> {
                timelineElementInternalCategory = TimelineElementCategoryV28.DIGITAL_SUCCESS_WORKFLOW;
                category = LegalFactCategory.DIGITAL_DELIVERY;
            }
            case "DIGITAL_DELIVERY_FAILURE" -> {
                timelineElementInternalCategory = TimelineElementCategoryV28.DIGITAL_FAILURE_WORKFLOW;
                category = LegalFactCategory.DIGITAL_DELIVERY;
            }
            case "SEND_ANALOG_PROGRESS" -> {
                timelineElementInternalCategory = TimelineElementCategoryV28.SEND_ANALOG_PROGRESS;
                category = LegalFactCategory.ANALOG_DELIVERY;
            }
            // NOTIFICATION_CANCELLED introdotto con V25
            case "COMPLETELY_UNREACHABLE", "NOTIFICATION_CANCELLED" -> {
                timelineElementInternalCategory = TimelineElementCategoryV28.COMPLETELY_UNREACHABLE;
                category = LegalFactCategory.ANALOG_FAILURE_DELIVERY;
            }
            default -> throw new IllegalArgumentException();
        }
        PnTimelineLegalFactV28 pnTimelineLegalFact = new PnTimelineLegalFactV28();
        pnTimelineLegalFact.setTimelineElementInternalCategory(timelineElementInternalCategory);
        pnTimelineLegalFact.setLegalFactCategory(category);

        return pnTimelineLegalFact;
    }
}
