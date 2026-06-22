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
            case NOTIFICATION_REWORKS -> buildNotificationReworksRequest(attributeValues);
            case REWORKED_TIMELINES_FOR_INVOICING -> buildReworkedTimelinesForInvoicingRequest(attributeValues);
            case COST_COMPONENTS -> buildCostComponentsRequest(attributeValues);
            case COST_UPDATE_RESULT -> buildCostUpdateResultRequest(attributeValues);
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

    private static QueryRequest buildOnboardInstitutionsRequest(Map<String, AttributeValue> attributeValues) {
        return DynamoQueryBuilder.withoutFilter(DynamoTableName.ONBOARD_INSTITUTIONS.getValue(),
                "id = :v_id",
                attributeValues);
    }

    private static QueryRequest buildNotificationReworksRequest(Map<String, AttributeValue> attributeValues) {
        return DynamoQueryBuilder.withoutFilter(DynamoTableName.NOTIFICATION_REWORKS.getValue(),
                "iun = :v_iun",
                attributeValues);
    }

    private static QueryRequest buildReworkedTimelinesForInvoicingRequest(Map<String, AttributeValue> attributeValues) {
        return DynamoQueryBuilder.withFilter(DynamoTableName.REWORKED_TIMELINES_FOR_INVOICING.getValue(),
                "paId_invoicingDay = :pk",
                "iun = :v_iun",
                attributeValues);
    }

    private static QueryRequest buildCostComponentsRequest(Map<String, AttributeValue> attributeValues) {
        return DynamoQueryBuilder.withoutFilter(DynamoTableName.COST_COMPONENTS.getValue(),
                "pk = :v_pk",
                attributeValues);
    }

    private static QueryRequest buildCostUpdateResultRequest(Map<String, AttributeValue> attributeValues) {
        return DynamoQueryBuilder.withoutFilter(DynamoTableName.COST_UPDATE_RESULT.getValue(),
                "pk = :v_pk",
                attributeValues);
    }
}
