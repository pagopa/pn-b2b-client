package it.pagopa.pn.cucumber.steps.pa.utilityVersions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.FilterLogEventsRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

import java.util.Map;

@Slf4j
@Component
public class AwsUtils {

    @Getter
    private final DynamoDbClient dynamoDbClient;
    @Getter
    private final CloudWatchLogsClient cloudWatchLogsClient;

    //NOMI TABELLE INTERROGATE
    public static final String PN_TIMELINES = "pn-Timelines";
    public static final String PN_PAYMENT_INFO = "pn-PaymentInfo";
    public static final String PN_NOTIFICATION_DELIVERY_COST = "pn-NotificationDeliveryCost";

    @Autowired
    public AwsUtils(DynamoDbClient dynamoDbClient, CloudWatchLogsClient cloudWatchLogsClient) {
        this.dynamoDbClient = dynamoDbClient;
        this.cloudWatchLogsClient = cloudWatchLogsClient;
    }

    /**
     * Creates a query for a timelineElement with a specific category, related to a specific notification
     */
    public static QueryRequest buildPnTimelinesCategoryRequest(Map<String, AttributeValue> expressionAttributeValues) {
        return QueryRequest.builder()
                .tableName(PN_TIMELINES)
                .keyConditionExpression("iun = :v_iun")
                .filterExpression("category = :v_category")
                .expressionAttributeValues(expressionAttributeValues)
                .build();
    }

    public static QueryRequest buildPnPaymentInfoRequest(Map<String, AttributeValue> expressionAttributeValues) {
        return QueryRequest.builder()
                .tableName(PN_PAYMENT_INFO)
                .keyConditionExpression("pk = :v_pk")
                .expressionAttributeValues(expressionAttributeValues)
                .build();
    }

    public static QueryRequest buildPnNotificationDeliveryCostRequest(Map<String, AttributeValue> expressionAttributeValues) {
        return QueryRequest.builder()
                .tableName(PN_NOTIFICATION_DELIVERY_COST)
                .keyConditionExpression("pk = :v_pk AND sk = :v_sk")
                .expressionAttributeValues(expressionAttributeValues)
                .build();
    }

    public static FilterLogEventsRequest buildCloudWatchLogRequest(String microservice, String search, long minutes) {
        long startTime = System.currentTimeMillis() - (minutes * 60L * 1000L);
        return FilterLogEventsRequest.builder()
                .logGroupName(microservice)
                .filterPattern(search)
                .startTime(startTime)
                .build();
    }
}
