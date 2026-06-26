package it.pagopa.pn.client.b2b.pa.domain;

import lombok.Getter;

@Getter
public enum DynamoTableName {
    TIMELINE("pn-Timelines"),
    PAYMENT_INFO("pn-PaymentInfo"),
    NOTIFICATION_DELIVERY_COST("pn-NotificationDeliveryCost"),
    ONBOARD_INSTITUTIONS("pn-OnboardInstitutions"),
    NOTIFICATION_REWORKS("pn-NotificationReworks"),
    REWORKED_TIMELINES_FOR_INVOICING("pn-ReworkedTimelinesForInvoicing"),
    COST_COMPONENTS("pn-CostComponents"),
    COST_UPDATE_RESULT("pn-CostUpdateResult"),
    USER_ATTRIBUTES("pn-UserAttributes"),
    BATCH_REQUESTS_WITH_INDEX_SEND_STATUS("pn-batchRequests"),
    BATCH_REQUESTS_WITH_INDEX_STATUS("pn-batchRequests");

    private final String value;

    DynamoTableName(String value) {
        this.value = value;
    }

}
