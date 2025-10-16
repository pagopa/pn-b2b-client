package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class EventArchivingSteps {

    private String bucketName = System.getenv("EVENTS_BUCKET");
    private String dynamoTable = System.getenv("DYNAMO_TABLE");
    private AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
    private AmazonDynamoDB dynamoClient = AmazonDynamoDBClientBuilder.defaultClient();


}