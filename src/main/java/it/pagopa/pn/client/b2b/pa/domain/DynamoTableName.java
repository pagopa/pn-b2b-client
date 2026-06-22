package it.pagopa.pn.client.b2b.pa.domain;

import lombok.Getter;

@Getter
public enum DynamoTableName {
    TIMELINE("pn-Timelines"),
    PAYMENT_INFO("pn-PaymentInfo"),
    NOTIFICATION_DELIVERY_COST("pn-NotificationDeliveryCost"),
    ONBOARD_INSTITUTIONS("pn-OnboardInstitutions"),
    PN_USER_ATTRIBUTES("pn-UserAttributes");

    private final String value;

    DynamoTableName(String value) {
        this.value = value;
    }

}
