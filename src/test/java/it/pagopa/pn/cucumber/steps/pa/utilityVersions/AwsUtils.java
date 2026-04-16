package it.pagopa.pn.cucumber.steps.pa.utilityVersions;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.FilterLogEventsRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

import java.util.Map;

@Slf4j
public class AwsUtils {

    public final static DynamoDbClient DYNAMO_DB_CLIENT = getDynamoDbClient();

    public final static CloudWatchLogsClient CLOUD_WATCH_LOGS_CLIENT = getCloudWatchLogsClient();

    //NOMI TABELLE INTERROGATE
    public static final String PN_TIMELINES = "pn-Timelines";
    public static final String PN_PAYMENT_INFO = "pn-PaymentInfo";
    public static final String PN_NOTIFICATION_DELIVERY_COST = "pn-NotificationDeliveryCost";

    //NOMI MICRO-SERVIZI (PER CONTROLLARE AUDIT LOG SU CLOUDWATCH)
    public static final String NOTIFICATION_COST_SERVICE = "pn-notification-cost-service";
    public static final String MANDATE = "pn-mandate";


    /**
     * Creates a DynamoDB client and stores it as public final static field.
     */
    private static DynamoDbClient getDynamoDbClient() {
        return DynamoDbClient.builder()
                .region(Region.EU_SOUTH_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    /**
     * Creates a CloudWatch client and stores it as public final static field.
     */
    private static CloudWatchLogsClient getCloudWatchLogsClient() {
        return CloudWatchLogsClient.builder()
                .region(Region.EU_SOUTH_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
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

    public static FilterLogEventsRequest buildCloudWatchLogRequest(String microservice, String search, int minutes) {
        long startTime = System.currentTimeMillis() - (minutes * 60 * 1000);

        return FilterLogEventsRequest.builder()
                .logGroupName(microservice)
                .filterPattern(search)
                .startTime(startTime)
                .build();
    }
}
