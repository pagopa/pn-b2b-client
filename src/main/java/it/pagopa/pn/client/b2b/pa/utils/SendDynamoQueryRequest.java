package it.pagopa.pn.client.b2b.pa.utils;

import it.pagopa.common.util.DynamoQueryRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

import java.util.Map;

public class SendDynamoQueryRequest {

    public static QueryRequest buildPnTimelinesCategoryRequest(Map<String, AttributeValue> expressionAttributeValues) {
        return DynamoQueryRequest.withFilter(DynamoTableName.TIMELINE.getValue(),
                "iun = :v_iun",
                "category = :v_category",
                expressionAttributeValues);
    }

    public static QueryRequest buildPnPaymentInfoRequest(Map<String, AttributeValue> expressionAttributeValues) {
        return DynamoQueryRequest.withoutFilter(DynamoTableName.PAYMENT_INFO.getValue(),
                "pk = :v_pk",
                expressionAttributeValues);
    }

    public static QueryRequest buildPnNotificationDeliveryCostRequest(Map<String, AttributeValue> expressionAttributeValues) {
        return DynamoQueryRequest.withoutFilter(DynamoTableName.NOTIFICATION_DELIVERY_COST.getValue(),
                "pk = :v_pk AND sk = :v_sk",
                expressionAttributeValues);
    }
}
