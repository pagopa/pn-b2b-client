package it.pagopa.common.util;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

import java.util.Map;

public class DynamoQueryBuilder {

    public static QueryRequest withoutFilter(String tableName, String keyCondition, Map<String, AttributeValue> expressionAttributeValues) {
        return withFilter(tableName, keyCondition, null, expressionAttributeValues);
    }

    public static QueryRequest withFilter(String tableName, String keyCondition, String filter, Map<String, AttributeValue> expressionAttributeValues) {
        return QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression(keyCondition)
                .filterExpression(filter)
                .expressionAttributeValues(expressionAttributeValues)
                .build();
    }

    public static QueryRequest withIndex(String tableName, String keyCondition, String sortKeyCondition, Map<String, AttributeValue> expressionAttributeValues, String indexName) {
        return QueryRequest.builder()
                .tableName(tableName)
                .indexName(indexName)
                .keyConditionExpression(keyCondition + " AND " + sortKeyCondition)
                .expressionAttributeValues(expressionAttributeValues)
                .build();
    }
}
