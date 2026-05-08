package it.pagopa.common.util;

import software.amazon.awssdk.services.cloudwatchlogs.model.FilterLogEventsRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

import java.util.Map;

public class DynamoQueryRequest {

    public static QueryRequest withFilter(String tableName, String expression,
                                          String filter, Map<String, AttributeValue> expressionAttributeValues) {
        return QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression(expression)
                .filterExpression(filter)
                .expressionAttributeValues(expressionAttributeValues)
                .build();
    }

    public static QueryRequest withoutFilter(String tableName, String expression,
                                             Map<String, AttributeValue> expressionAttributeValues) {
        return withFilter(tableName, expression, null, expressionAttributeValues);
    }

//    public static QueryRequest buildPnNotificationDeliveryCostRequest(Map<String, AttributeValue> expressionAttributeValues) {
//        return QueryRequest.builder()
//                .tableName(PN_NOTIFICATION_DELIVERY_COST)
//                .keyConditionExpression("pk = :v_pk AND sk = :v_sk")
//                .expressionAttributeValues(expressionAttributeValues)
//                .build();
//    }

    public static FilterLogEventsRequest buildCloudWatchLogRequest(String microservice, String search, long minutes) {
        long startTime = System.currentTimeMillis() - (minutes * 60L * 1000L);
        return FilterLogEventsRequest.builder()
                .logGroupName(microservice)
                .filterPattern(search)
                .startTime(startTime)
                .build();
    }
}
