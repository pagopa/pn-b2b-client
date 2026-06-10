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

@RequiredArgsConstructor
@Service
public class DynamoDbService {
    private final DynamoDbClient dynamoDbClient;

    public QueryResponse call(DynamoTableName tableName, Map<String, AttributeValue> attributeValues) {
        QueryRequest queryRequest = switch (tableName) {
            case TIMELINE -> buildTimelinesCategoryRequest(attributeValues);
            case PAYMENT_INFO -> buildPaymentInfoRequest(attributeValues);
            case NOTIFICATION_DELIVERY_COST -> buildNotificationDeliveryCostRequest(attributeValues);
            case ONBOARD_INSTITUTIONS -> buildOnboardInstitutionsRequest(attributeValues);
            case BATCH_REQUESTS_WITH_INDEX_SEND_STATUS ->
                    buildBatchRequestsBySendStatusAndLastReservedAfter(attributeValues);
            case BATCH_REQUESTS_WITH_INDEX_STATUS -> buildBatchRequestsByStatus(attributeValues);
        };
        return dynamoDbClient.query(queryRequest);
    }

    private static QueryRequest buildTimelinesCategoryRequest(Map<String, AttributeValue> attributeValues) {
        return DynamoQueryBuilder.withFilter(DynamoTableName.TIMELINE.getValue(),
                "iun = :v_iun",
                "category = :v_category",
                attributeValues);
    }

    private static QueryRequest buildPaymentInfoRequest(Map<String, AttributeValue> attributeValues) {
        return DynamoQueryBuilder.withoutFilter(DynamoTableName.PAYMENT_INFO.getValue(),
                "pk = :v_pk",
                attributeValues);
    }

    private static QueryRequest buildNotificationDeliveryCostRequest(Map<String, AttributeValue> attributeValues) {
        return DynamoQueryBuilder.withoutFilter(DynamoTableName.NOTIFICATION_DELIVERY_COST.getValue(),
                "pk = :v_pk AND sk = :v_sk",
                attributeValues);
    }

    public static QueryRequest buildOnboardInstitutionsRequest(Map<String, AttributeValue> attributeValues) {
        return DynamoQueryBuilder.withoutFilter(DynamoTableName.ONBOARD_INSTITUTIONS.getValue(),
                "id = :v_id",
                attributeValues);
    }

    // added for cases when the result might be paginated
    public List<Map<String, AttributeValue>> callAllPages(
            DynamoTableName tableName,
            Map<String, AttributeValue> attributeValues
    ) {
        QueryRequest baseRequest = switch (tableName) {
            case TIMELINE -> buildTimelinesCategoryRequest(attributeValues);
            case PAYMENT_INFO -> buildPaymentInfoRequest(attributeValues);
            case NOTIFICATION_DELIVERY_COST -> buildNotificationDeliveryCostRequest(attributeValues);
            case ONBOARD_INSTITUTIONS -> buildOnboardInstitutionsRequest(attributeValues);
            case BATCH_REQUESTS_WITH_INDEX_SEND_STATUS ->
                    buildBatchRequestsBySendStatusAndLastReservedAfter(attributeValues);
            case BATCH_REQUESTS_WITH_INDEX_STATUS -> buildBatchRequestsByStatus(attributeValues);
        };

        List<Map<String, AttributeValue>> allItems = new ArrayList<>();

        Map<String, AttributeValue> lastEvaluatedKey = null;

        do {
            QueryRequest.Builder requestBuilder = baseRequest.toBuilder();

            if (lastEvaluatedKey != null && !lastEvaluatedKey.isEmpty()) {
                requestBuilder.exclusiveStartKey(lastEvaluatedKey);
            }

            QueryResponse response = dynamoDbClient.query(requestBuilder.build());

            if (response.hasItems()) {
                allItems.addAll(response.items());
            }

            lastEvaluatedKey = response.lastEvaluatedKey();

        } while (lastEvaluatedKey != null && !lastEvaluatedKey.isEmpty());

        return allItems;
    }

    private static QueryRequest buildBatchRequestsBySendStatusAndLastReservedAfter(
            Map<String, AttributeValue> attributeValues) {

        return DynamoQueryBuilder.withIndex(
                DynamoTableName.BATCH_REQUESTS_WITH_INDEX_SEND_STATUS.getValue(),
                "sendStatus = :v_sendStatus",
                "lastReserved > :v_lastReserved",
                attributeValues,
                "sendStatus-lastReserved-index"
        );
    }

    private static QueryRequest buildBatchRequestsByStatus(
            Map<String, AttributeValue> attributeValues) {

        return DynamoQueryBuilder.withIndex(
                DynamoTableName.BATCH_REQUESTS_WITH_INDEX_STATUS.getValue(),
                "status = :v_status",
                "lastReserved > :v_lastReserved",
                attributeValues,
                "status-index"
        );
    }
}
