package it.pagopa.pn.cucumber.steps.nationalRegistry;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.domain.DynamoTableName;
import it.pagopa.pn.client.b2b.pa.service.DynamoDbService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.AssertionsForClassTypes;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@Slf4j
public class INIPECGestionePuntualeEsitiSteps {

    public INIPECGestionePuntualeEsitiSteps(DynamoDbService dynamoDbService) {
        this.dynamoDbService = dynamoDbService;
    }

    private final DynamoDbService dynamoDbService;
    private String notificationSentTimestamp;


    @Then("viene verificato che la richiesta per il cf {string} non risulti in retry")
    public void verifyRetriesAreZero(String cf) {

        List<Map<String, AttributeValue>> filtered = retrieveBatchRequestItemsBySendStatus(cf, "SENT");

        // Assert at least one match
        AssertionsForClassTypes.assertThat(filtered.size())
                .as("No items found for cf=%s and sendStatus=SENT", cf)
                .isNotZero();

        // Assert all retries are 0
        filtered.forEach(item ->
                AssertionsForClassTypes.assertThat(Integer.parseInt(item.get("retry").n()))
                        .as("All retries should be 0")
                        .isZero());
    }

    @Then("viene verificato che la richiesta per il cf {string} risulti in retry")
    public void verifySentToDLQAndRetriesAreGreaterThanZero(String cf) {

        List<Map<String, AttributeValue>> filtered = retrieveBatchRequestItemsBySendStatus(cf, "NOT_SENT");

        // Assert at least one match
        AssertionsForClassTypes.assertThat(filtered.size())
                .as("No items found for cf=%s and sendStatus=NOT_SENT", cf)
                .isNotZero();

        // Assert all retries are > 0
        filtered.forEach(item ->
                AssertionsForClassTypes.assertThat(Integer.parseInt(item.get("retry").n()))
                        .as("Retries should be greater than 0")
                        .isNotZero());
    }

    @Then("viene verificato che le richieste per i cf {string} e {string} risultino entrambe non tra quelle inviate")
    public void verifyCfsNotPresentWithSentOrNotSent(String cf1, String cf2) {

        List<Map<String, AttributeValue>> cf1NotSent = retrieveBatchRequestItemsBySendStatus(cf1, "NOT_SENT");
        List<Map<String, AttributeValue>> cf1Sent = retrieveBatchRequestItemsBySendStatus(cf1, "SENT");

        List<Map<String, AttributeValue>> cf2NotSent = retrieveBatchRequestItemsBySendStatus(cf2, "NOT_SENT");
        List<Map<String, AttributeValue>> cf2Sent = retrieveBatchRequestItemsBySendStatus(cf2, "SENT");

        AssertionsForClassTypes.assertThat(cf1NotSent.size() + cf1Sent.size())
                .as("Items found for cf=%s with status SENT or NOT_SENT", cf1)
                .isZero();

        AssertionsForClassTypes.assertThat(cf2NotSent.size() + cf2Sent.size())
                .as("Items found for cf=%s with status SENT or NOT_SENT", cf2)
                .isZero();
    }


    @And("si attende per {int} minuti")
    public void iWaitForMinutes(int minutes) {
        await().atMost(minutes, TimeUnit.MINUTES)
                .until(() -> false);
    }


    @And("salvo il timestamp corrente")
    public void storeCurrentTimestamp() {
        notificationSentTimestamp = java.time.OffsetDateTime.now().minusHours(2).minusMinutes(1).toString();
    }


    private List<Map<String, AttributeValue>> retrieveBatchRequestItemsBySendStatus(String cf, String sendStatus) {

        // Build attribute values
        Map<String, AttributeValue> attributeValues = Map.of(
                ":v_sendStatus", AttributeValue.builder().s(sendStatus).build(),
                ":v_lastReserved", AttributeValue.builder().s(notificationSentTimestamp).build()
        );

        // Call service
        List<Map<String, AttributeValue>> items = dynamoDbService.callAllPages(
                DynamoTableName.BATCH_REQUESTS_WITH_INDEX_SEND_STATUS,
                attributeValues
        );

        // return filtered items
        return items.stream()
                .filter(item -> item.containsKey("cf")
                        && cf.equals(item.get("cf").s()))
                .toList();
    }

    private List<Map<String, AttributeValue>> retrieveBatchRequestItemsByStatus(String cf, String status) {

        // Build attribute values
        Map<String, AttributeValue> attributeValues = Map.of(
                ":v_status", AttributeValue.builder().s(status).build()
        );

        // Call service
        List<Map<String, AttributeValue>> items = dynamoDbService.callAllPages(
                DynamoTableName.BATCH_REQUESTS_WITH_INDEX_STATUS,
                attributeValues
        );

        // return filtered items
        return items.stream()
                .filter(item -> item.containsKey("cf")
                        && cf.equals(item.get("cf").s())
                        && item.containsKey("lastReserved")
                        && item.get("lastReserved").s().compareTo(notificationSentTimestamp) > 0)
                .toList();

    }


    @Given("imposto il timestamp a 30 minuti fa")
    public void storeTimestampTenMinutesAgo() {
        notificationSentTimestamp = java.time.OffsetDateTime.now()
                .minusMinutes(150)
                .toString();
    }

}
