package it.pagopa.pn.cucumber.steps.nationalRegistry;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.domain.DynamoTableName;
import it.pagopa.pn.client.b2b.pa.service.DynamoDbService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.AssertionsForClassTypes;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

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

        List<Map<String, AttributeValue>> filtered = retreiveBatchRequestItems(cf, "SENT");

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

    @Then("viene verificato che la richiesta per il cf {string} risulti inviata in DLQ e con retry>0")
    public void verifySentToDLQAndRetriesAreGreaterThanZero(String cf) {

        List<Map<String, AttributeValue>> filtered = retreiveBatchRequestItems(cf, "SENT_TO_DLQ");

        // Assert at least one match
        AssertionsForClassTypes.assertThat(filtered.size())
                .as("No items found for cf=%s and sendStatus=SENT_TO_DLQ", cf)
                .isNotZero();

        // Assert all retries are > 0
        filtered.forEach(item ->
                AssertionsForClassTypes.assertThat(Integer.parseInt(item.get("retry").n()))
                        .as("Retries should be greater than 0")
                        .isNotZero());
    }


    @And("si attende per {int} minuti")
    public void iWaitForMinutes(int minutes) {
        await().atMost(minutes, TimeUnit.MINUTES)
                .until(() -> false);
    }


    @And("salvo il timestamp corrente")
    public void storeCurrentTimestamp() {
        notificationSentTimestamp = java.time.OffsetDateTime.now().toString();
    }


    private List<Map<String, AttributeValue>> retreiveBatchRequestItems(String cf, String sendStatus) {

        // Build attribute values
        Map<String, AttributeValue> attributeValues = Map.of(
                ":v_sendStatus", AttributeValue.builder().s(sendStatus).build(),
                ":v_lastReserved", AttributeValue.builder().s(notificationSentTimestamp).build()
        );

        // Call service
        List<Map<String, AttributeValue>> items = dynamoDbService.callAll(
                DynamoTableName.BATCH_REQUESTS,
                attributeValues
        );

        // return filtered items
        return items.stream()
                .filter(item -> item.containsKey("cf")
                        && cf.equals(item.get("cf").s()))
                .toList();
    }


    @Given("imposto il timestamp a 10 minuti fa")
    public void storeTimestampTenMinutesAgo() {
        notificationSentTimestamp = java.time.OffsetDateTime.now()
                .minusMinutes(300)
                .toString();
    }

}
