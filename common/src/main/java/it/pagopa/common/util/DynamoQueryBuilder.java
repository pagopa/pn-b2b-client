package it.pagopa.common.util;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

import java.util.Map;

public class DynamoQueryBuilder {

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
}
