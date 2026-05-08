package it.pagopa.pn.client.b2b.pa.utils;

public enum DynamoTableName {
    TIMELINE("pn-Timelines"),
    PAYMENT_INFO("pn-PaymentInfo"),
    NOTIFICATION_DELIVERY_COST("pn-NotificationDeliveryCost");

    private final String value;

    DynamoTableName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
