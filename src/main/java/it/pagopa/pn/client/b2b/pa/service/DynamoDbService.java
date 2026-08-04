package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.common.util.DynamoQueryBuilder;
import it.pagopa.pn.client.b2b.pa.domain.DynamoTableName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DynamoDbService {
    private final DynamoDbClient dynamoDbClient;

    public QueryResponse call(DynamoTableName tableName, Map<String, AttributeValue> attributeValues) {
        return dynamoDbClient.query(buildRequest(tableName, attributeValues));
    }

    private static QueryRequest buildRequest(DynamoTableName tableName, Map<String, AttributeValue> attributeValues) {
        return switch (tableName) {
            //query WITH filters
            case TIMELINE -> queryWithFilters(tableName, List.of("iun", "category"), attributeValues);
            case REWORKED_TIMELINES_FOR_INVOICING ->
                    queryWithFilters(tableName, List.of("paId_invoicingDay", "iun"), attributeValues);
            //query WITHOUT filters
            case PAYMENT_INFO, COST_COMPONENTS, COST_UPDATE_RESULT, USER_ATTRIBUTES ->
                    queryWithoutFilters(tableName, "pk", attributeValues);
            case ONBOARD_INSTITUTIONS -> queryWithoutFilters(tableName, "id", attributeValues);
            case NOTIFICATION_REWORKS,NOTIFICATIONS -> queryWithoutFilters(tableName, "iun", attributeValues);
            case IO_CONNECTOR_REQUESTS, PAPER_REQUEST_ERROR ->
                    queryWithoutFilters(tableName, "requestId", attributeValues);
            case NOTIFICATION_DELIVERY_COST -> queryWithoutFilters(tableName, List.of("pk", "sk"), attributeValues);
            case NOTIFICATIONS_METADATA -> queryWithoutFilters(tableName, "iun_recipientId", attributeValues);
            //query WITH sorting
            case BATCH_REQUESTS_WITH_INDEX_SEND_STATUS ->
                    buildBatchRequestsBySendStatusAndLastReservedAfter(attributeValues);
            case BATCH_REQUESTS_WITH_INDEX_STATUS -> buildBatchRequestsByStatus(attributeValues);
        };
    }

    private static QueryRequest queryWithoutFilters(DynamoTableName tableName, String column, Map<String, AttributeValue> attributeValues) {
        return queryWithoutFilters(tableName, List.of(column), attributeValues);
    }

    /**
     * Builds a query for DynamoDb only with the key condition (in case of multiple conditions the AND logic will be applied)
     */
    private static QueryRequest queryWithoutFilters(DynamoTableName tableName, List<String> columns, Map<String, AttributeValue> attributeValues) {
        validateColumns(tableName, attributeValues, columns);
        String keyCondition = columns.stream()
                .map(column -> String.format("%s = :v_%s", column, column))
                .collect(Collectors.joining(" AND "));
        return DynamoQueryBuilder.withoutFilter(tableName.getValue(),
                keyCondition,
                attributeValues);
    }

    /**
     * Builds a query for DynamoDb only with the key condition and one or more filters.
     * The first column is used for the key condition, the remaining ones for the filters (in case of multiple filters, the AND logic will be applied)
     */
    private static QueryRequest queryWithFilters(DynamoTableName tableName, List<String> columns, Map<String, AttributeValue> attributeValues) {
        validateColumns(tableName, attributeValues, columns);
        String keyCondition = String.format("%s = :v_%s", columns.get(0), columns.get(0));
        String filterCondition = columns.stream()
                .skip(1)
                .map(column -> String.format("%s = :v_%s", column, column))
                .collect(Collectors.joining(" AND "));
        return DynamoQueryBuilder.withFilter(
                tableName.getValue(),
                keyCondition,
                filterCondition,
                attributeValues
        );
    }

    private static void validateColumns(DynamoTableName tableName, Map<String, AttributeValue> attributeValues, List<String> columns) {
        if (attributeValues.keySet().size() != columns.size()) {
            throw new IllegalArgumentException(String.format("Mismatch between number of arguments while querying table %s. AttributeValues size: %s. Columns size: %s",
                    tableName.getValue(), attributeValues.keySet().size(), columns.size()));
        }
        List<String> columnsNotMatching = new ArrayList<>();
        attributeValues.keySet().forEach(key -> {
            String strippedKey = key.replaceFirst(":v_", "");
            if (!columns.contains(strippedKey)) {
                columnsNotMatching.add(strippedKey);
            }
        });
        if (!columnsNotMatching.isEmpty()) {
            throw new IllegalArgumentException(String.format("Mismatch between the following AttributeValues and Columns while querying table %s:%s",
                    tableName.getValue(), columnsNotMatching));
        }
    }

    /**
     * Added for cases when the result might be paginated:
     * a DynamoDB query returns at most 1 MB per page, so the SDK paginator is used to transparently fetch and flatten all pages.
     */
    public List<Map<String, AttributeValue>> callAllPages(DynamoTableName tableName, Map<String, AttributeValue> attributeValues) {
        QueryRequest baseRequest = buildRequest(tableName, attributeValues);
        return dynamoDbClient.queryPaginator(baseRequest)
                .items()
                .stream()
                .toList();
    }

    private static QueryRequest buildBatchRequestsBySendStatusAndLastReservedAfter(Map<String, AttributeValue> attributeValues) {
        return DynamoQueryBuilder.withIndex(
                DynamoTableName.BATCH_REQUESTS_WITH_INDEX_SEND_STATUS.getValue(),
                "sendStatus = :v_sendStatus",
                "lastReserved > :v_lastReserved",
                attributeValues,
                "sendStatus-lastReserved-index"
        );
    }

    private static QueryRequest buildBatchRequestsByStatus(Map<String, AttributeValue> attributeValues) {
        return DynamoQueryBuilder.withIndex(
                DynamoTableName.BATCH_REQUESTS_WITH_INDEX_STATUS.getValue(),
                "status = :v_status",
                "lastReserved > :v_lastReserved",
                attributeValues,
                "status-index"
        );
    }
}