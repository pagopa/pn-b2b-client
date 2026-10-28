package it.pagopa.pn.cucumber.steps.paperTracker;

import it.pagopa.pn.client.b2b.generated.openapi.clients.papertracker.model.PaperTrackerOutput;
import it.pagopa.pn.client.b2b.generated.openapi.clients.papertracker.model.PaperTrackerOutputsResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.papertracker.model.PaperTrackerOutputsResponseResultsInner;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

final class PaperTrackerOutputProcessingModeValidator {

    private PaperTrackerOutputProcessingModeValidator() {
    }

    static void validate(PaperTrackerOutputsResponse response,
                         Map<String, List<Map<String, AttributeValue>>> persistedOutputs,
                         String expectedMode) {
        if (!"DRY".equals(expectedMode) && !"RUN".equals(expectedMode)) {
            throw new IllegalArgumentException("La modalità attesa deve essere DRY oppure RUN");
        }

        Map<String, Set<String>> expectedCreated = apiOutputIdentities(response);
        assertThat(persistedOutputs).as("Gli output persistiti non devono essere null").isNotNull();
        Map<String, Set<String>> actualCreated = new LinkedHashMap<>();

        persistedOutputs.forEach((trackingId, items) -> {
            assertThat(trackingId).as("trackingId del gruppo DynamoDB").isNotBlank();
            assertThat(items).as("Lista output DynamoDB per trackingId %s", trackingId).isNotNull();
            Set<String> createdValues = new HashSet<>();
            for (Map<String, AttributeValue> item : items) {
                assertThat(item).as("Output DynamoDB nullo per trackingId %s", trackingId).isNotNull();
                String created = requiredString(item, "created", trackingId);
                String actualTrackingId = requiredString(item, "trackingId", trackingId);
                assertThat(actualTrackingId)
                        .as("trackingId persistito per gruppo %s e created %s", trackingId, created)
                        .isEqualTo(trackingId);
                AttributeValue modeAttribute = item.get("processingMode");
                String mode = modeAttribute == null ? null : modeAttribute.s();
                assertThat(mode)
                        .as("processingMode per trackingId %s, created %s", trackingId, created)
                        .isEqualTo(expectedMode);
                assertThat(createdValues.add(created))
                        .as("Output DynamoDB duplicato per trackingId %s, created %s", trackingId, created)
                        .isTrue();
            }
            // Le query includono anche PCRETRY senza output: solo i gruppi non vuoti sono confrontabili.
            if (!createdValues.isEmpty()) {
                actualCreated.put(trackingId, createdValues);
            }
        });

        assertThat(actualCreated.keySet())
                .as("trackingId degli output persistiti rispetto alla risposta /outputs")
                .containsExactlyInAnyOrderElementsOf(expectedCreated.keySet());
        expectedCreated.forEach((trackingId, createdValues) ->
                assertThat(actualCreated.get(trackingId))
                        .as("Output persistiti per trackingId %s: identificativi created", trackingId)
                        .containsExactlyInAnyOrderElementsOf(createdValues));
    }

    private static Map<String, Set<String>> apiOutputIdentities(PaperTrackerOutputsResponse response) {
        assertThat(response).as("La risposta /outputs non deve essere null").isNotNull();
        assertThat(response.getResults()).as("I risultati di /outputs non devono essere vuoti").isNotEmpty();
        Map<String, Set<String>> expectedCreated = new LinkedHashMap<>();
        Set<String> seenTrackingIds = new HashSet<>();
        for (PaperTrackerOutputsResponseResultsInner result : response.getResults()) {
            assertThat(result).as("Gruppo nullo nella risposta /outputs").isNotNull();
            String trackingId = result.getTrackingId();
            assertThat(trackingId).as("trackingId nella risposta /outputs").isNotBlank();
            assertThat(seenTrackingIds.add(trackingId))
                    .as("Gruppo /outputs duplicato per trackingId %s", trackingId).isTrue();
            Set<String> createdValues = new HashSet<>();
            if (result.getOutputs() != null) {
                for (PaperTrackerOutput output : result.getOutputs()) {
                    assertThat(output).as("Output API nullo per trackingId %s", trackingId).isNotNull();
                    String created = output.getCreated();
                    assertThat(created).as("created dell'output API per trackingId %s", trackingId).isNotBlank();
                    assertThat(createdValues.add(created))
                            .as("Output API duplicato per trackingId %s, created %s", trackingId, created)
                            .isTrue();
                }
            }
            if (!createdValues.isEmpty()) {
                expectedCreated.put(trackingId, createdValues);
            }
        }
        assertThat(expectedCreated).as("La risposta /outputs deve contenere almeno un output").isNotEmpty();
        return expectedCreated;
    }

    private static String requiredString(Map<String, AttributeValue> item, String field, String trackingId) {
        AttributeValue attribute = item.get(field);
        String value = attribute == null ? null : attribute.s();
        assertThat(value).as("Campo %s dell'output DynamoDB per trackingId %s", field, trackingId).isNotBlank();
        return value;
    }
}
