package it.pagopa.pn.client.b2b.pa.mapper.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactCategory;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV27;
import it.pagopa.pn.client.b2b.pa.mapper.model.PnTimelineLegalFactV27;
import it.pagopa.pn.client.b2b.pa.service.IPnTimelineLegalFactService;


public class PnTimelineAndLegalFactV27 implements IPnTimelineLegalFactService<PnTimelineLegalFactV27> {

    public PnTimelineLegalFactV27 getCategory(String legalFactCategory) {
        TimelineElementCategoryV27 timelineElementInternalCategory;
        LegalFactCategory category;
        switch (legalFactCategory) {
            case "SENDER_ACK" -> {
                timelineElementInternalCategory = TimelineElementCategoryV27.REQUEST_ACCEPTED;
                category = LegalFactCategory.SENDER_ACK;
            }
            case "RECIPIENT_ACCESS" -> {
                timelineElementInternalCategory = TimelineElementCategoryV27.NOTIFICATION_VIEWED;
                category = LegalFactCategory.RECIPIENT_ACCESS;
            }
            case "PEC_RECEIPT" -> {
                timelineElementInternalCategory = TimelineElementCategoryV27.SEND_DIGITAL_PROGRESS;
                category = LegalFactCategory.PEC_RECEIPT;
            }
            case "DIGITAL_DELIVERY" -> {
                timelineElementInternalCategory = TimelineElementCategoryV27.DIGITAL_SUCCESS_WORKFLOW;
                category = LegalFactCategory.DIGITAL_DELIVERY;
            }
            case "DIGITAL_DELIVERY_FAILURE" -> {
                timelineElementInternalCategory = TimelineElementCategoryV27.DIGITAL_FAILURE_WORKFLOW;
                category = LegalFactCategory.DIGITAL_DELIVERY;
            }
            case "SEND_ANALOG_PROGRESS" -> {
                timelineElementInternalCategory = TimelineElementCategoryV27.SEND_ANALOG_PROGRESS;
                category = LegalFactCategory.ANALOG_DELIVERY;
            }
            // NOTIFICATION_CANCELLED introdotto con V25
            case "COMPLETELY_UNREACHABLE", "NOTIFICATION_CANCELLED" -> {
                timelineElementInternalCategory = TimelineElementCategoryV27.COMPLETELY_UNREACHABLE;
                category = LegalFactCategory.ANALOG_FAILURE_DELIVERY;
            }
            default -> throw new IllegalArgumentException();
        }
        PnTimelineLegalFactV27 pnTimelineLegalFact = new PnTimelineLegalFactV27();
        pnTimelineLegalFact.setTimelineElementInternalCategory(timelineElementInternalCategory);
        pnTimelineLegalFact.setLegalFactCategory(category);

        return pnTimelineLegalFact;
    }
}
