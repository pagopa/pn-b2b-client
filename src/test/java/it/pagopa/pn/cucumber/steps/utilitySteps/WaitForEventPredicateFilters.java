package it.pagopa.pn.cucumber.steps.utilitySteps;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class WaitForEventPredicateFilters {

    private Integer recipientIndex;
    private String deliveryDetailCode;
    private String attempt;
    private String documentType;
    private String responseStatus;
    private boolean isF24;
    private boolean isLegalFactEmpty;
    private String legalFactIdCategory;
    private boolean isAttachmentEmpty;
    private List<String> failureCauses;
    private String statusHistory;
}
