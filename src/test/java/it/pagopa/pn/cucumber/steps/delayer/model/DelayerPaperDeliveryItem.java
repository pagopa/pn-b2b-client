package it.pagopa.pn.cucumber.steps.delayer.model;

import it.pagopa.pn.cucumber.steps.delayer.model.enums.WorkflowSteps;
import lombok.Data;

import java.io.Serializable;

@Data
public class DelayerPaperDeliveryItem implements Serializable {

    private String iun;
    private String notificationSentAt;
    private WorkflowSteps workflowStep;
    private int priority;
    private String tenderId;
    private int attempt;
    private String createdAt;
    private String senderPaId;
    private String cap;
    private String province;
    private String requestId;
    private String sk;
    private String pk;
    private String prepareRequestDate;
    private String productType;
    private String unifiedDeliveryDriver;
}
