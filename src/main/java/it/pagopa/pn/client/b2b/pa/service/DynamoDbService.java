package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.common.util.DynamoQueryBuilder;
import it.pagopa.pn.client.b2b.pa.utils.DynamoTableName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DynamoDbService {
    private final DynamoDbClient dynamoDbClient;

    public QueryResponse call(DynamoTableName tableName, Map<String, String> attributeValues) {
        Map<String, AttributeValue> expressionAttributeValues = buildExpressionAttributeValues(attributeValues);
        QueryRequest queryRequest = switch (tableName) {
            case TIMELINE -> buildPnTimelinesCategoryRequest(expressionAttributeValues);
            case PAYMENT_INFO -> buildPnPaymentInfoRequest(expressionAttributeValues);
            case NOTIFICATION_DELIVERY_COST -> buildPnNotificationDeliveryCostRequest(expressionAttributeValues);
            case ONBOARD_INSTITUTIONS -> buildOnboardInstitutionsRequest(expressionAttributeValues);
        };
        return dynamoDbClient.query(queryRequest);
    }

    private QueryRequest buildPnTimelinesCategoryRequest(Map<String, AttributeValue> expressionAttributeValues) {
        return DynamoQueryBuilder.withFilter(DynamoTableName.TIMELINE.getValue(),
                "iun = :v_iun",
                "category = :v_category",
                expressionAttributeValues);
    }

    private QueryRequest buildPnPaymentInfoRequest(Map<String, AttributeValue> expressionAttributeValues) {
        return DynamoQueryBuilder.withoutFilter(DynamoTableName.PAYMENT_INFO.getValue(),
                "pk = :v_pk",
                expressionAttributeValues);
    }

    private QueryRequest buildPnNotificationDeliveryCostRequest(Map<String, AttributeValue> expressionAttributeValues) {
        return DynamoQueryBuilder.withoutFilter(DynamoTableName.NOTIFICATION_DELIVERY_COST.getValue(),
                "pk = :v_pk AND sk = :v_sk",
                expressionAttributeValues);
    }

    public static QueryRequest buildOnboardInstitutionsRequest(Map<String, AttributeValue> expressionAttributeValues) {
        return DynamoQueryBuilder.withoutFilter(DynamoTableName.ONBOARD_INSTITUTIONS.getValue(),
                "id = :v_id",
                expressionAttributeValues);
    }

    private AttributeValue getAttributeValue(String value) {
        return AttributeValue.builder().s(value).build();
    }

    private Map<String, AttributeValue> buildExpressionAttributeValues(Map<String, String> values) {
        return values.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> getAttributeValue(entry.getValue())));
    }
}
