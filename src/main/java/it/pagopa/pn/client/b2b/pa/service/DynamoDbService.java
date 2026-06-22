package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.common.util.DynamoQueryBuilder;
import it.pagopa.pn.client.b2b.pa.domain.DynamoTableName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

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
            case IO_CONNECTOR_REQUESTS -> buildIOConnectorRequestsRequest(attributeValues);
            case PN_USER_ATTRIBUTES -> buildUserAttributesInfoRequest(attributeValues);
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

    private static QueryRequest buildUserAttributesInfoRequest(Map<String, AttributeValue> attributeValues) {
        return DynamoQueryBuilder.withoutFilter(DynamoTableName.PN_USER_ATTRIBUTES.getValue(),
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

    private static QueryRequest buildIOConnectorRequestsRequest(Map<String, AttributeValue> attributeValues) {
        return DynamoQueryBuilder.withoutFilter(DynamoTableName.IO_CONNECTOR_REQUESTS.getValue(),
                "requestId = :v_requestId",
                attributeValues);
    }
}
