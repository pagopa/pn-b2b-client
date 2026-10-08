package it.pagopa.pn.client.b2b.pa.mapper.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV28;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.privateDeliveryPush.model_v26.LegalFactCategoryV20;
import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class PnTimelineLegalFact {

    /*TODO al rilascio di una nuova versione di timelineElement e/o LegalFactCategory, aggiornare i valori in questa classe all'ultima versione*/
    private LegalFactCategoryV20 legalFactCategory;
    private TimelineElementCategoryV28 timelineElementCategory;

    public static PnTimelineLegalFact getCategory(String inputLegalFactCategory) {

        TimelineElementCategoryV28 timelineElementCategory;
        LegalFactCategoryV20 legalFactCategory;

        switch (inputLegalFactCategory) {
            case "SENDER_ACK" -> {
                timelineElementCategory = TimelineElementCategoryV28.REQUEST_ACCEPTED;
                legalFactCategory = LegalFactCategoryV20.SENDER_ACK;
            }
            case "RECIPIENT_ACCESS" -> {
                timelineElementCategory = TimelineElementCategoryV28.NOTIFICATION_VIEWED;
                legalFactCategory = LegalFactCategoryV20.RECIPIENT_ACCESS;
            }
            case "PEC_RECEIPT" -> {
                timelineElementCategory = TimelineElementCategoryV28.SEND_DIGITAL_PROGRESS;
                legalFactCategory = LegalFactCategoryV20.PEC_RECEIPT;
            }
            case "DIGITAL_DELIVERY" -> {
                timelineElementCategory = TimelineElementCategoryV28.DIGITAL_SUCCESS_WORKFLOW;
                legalFactCategory = LegalFactCategoryV20.DIGITAL_DELIVERY;
            }
            case "DIGITAL_DELIVERY_FAILURE" -> {
                timelineElementCategory = TimelineElementCategoryV28.DIGITAL_FAILURE_WORKFLOW;
                legalFactCategory = LegalFactCategoryV20.DIGITAL_DELIVERY;
            }
//            case "ANALOG_DELIVERY_FAILURE" -> {
//                timelineElementCategory = TimelineElementCategoryV28.ANALOG_FAILURE_WORKFLOW;
//                legalFactCategory = LegalFactCategoryV20.ANALOG_DELIVERY;
//            }
            case "SEND_ANALOG_PROGRESS" -> {
                timelineElementCategory = TimelineElementCategoryV28.SEND_ANALOG_PROGRESS;
                legalFactCategory = LegalFactCategoryV20.ANALOG_DELIVERY;
            }
            case "COMPLETELY_UNREACHABLE" -> {
                timelineElementCategory = TimelineElementCategoryV28.COMPLETELY_UNREACHABLE;
                legalFactCategory = LegalFactCategoryV20.ANALOG_FAILURE_DELIVERY;
            }
            case "NOTIFICATION_CANCELLED" -> {
                timelineElementCategory = TimelineElementCategoryV28.NOTIFICATION_CANCELLED;
                legalFactCategory = LegalFactCategoryV20.NOTIFICATION_CANCELLED;
            }
            default -> throw new IllegalArgumentException();
        }

        return PnTimelineLegalFact.builder()
                .legalFactCategory(legalFactCategory)
                .timelineElementCategory(timelineElementCategory)
                .build();

    }
}
