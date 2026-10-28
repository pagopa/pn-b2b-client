package it.pagopa.pn.cucumber.steps.paperTracker;

import it.pagopa.pn.client.b2b.generated.openapi.clients.papertracker.model.PaperTrackerOutput;
import it.pagopa.pn.client.b2b.generated.openapi.clients.papertracker.model.PaperTrackerOutputsResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.papertracker.model.PaperTrackerOutputsResponseResultsInner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaperTrackerOutputProcessingModeValidatorTest {

    private static final String TRACKING = "PREPARE_ANALOG_DOMICILE.IUN_test.RECINDEX_0.ATTEMPT_0.PCRETRY_0";
    private static final String RETRY = "PREPARE_ANALOG_DOMICILE.IUN_test.RECINDEX_0.ATTEMPT_0.PCRETRY_1";
    private static final String CREATED = "2026-10-28T10:00:00Z";
    private static final String NEXT_CREATED = "2026-10-28T10:01:00Z";

    @ParameterizedTest
    @ValueSource(strings = {"DRY", "RUN"})
    void acceptsMatchingOutputsInBothModes(String mode) {
        assertDoesNotThrow(() -> validate(response(group(TRACKING, CREATED, NEXT_CREATED)),
                Map.of(TRACKING, List.of(item(TRACKING, CREATED, mode), item(TRACKING, NEXT_CREATED, mode))), mode));
    }

    @Test
    void keepsSameCreatedValueDistinctAcrossPcretryGroups() {
        assertDoesNotThrow(() -> validate(response(group(TRACKING, CREATED), group(RETRY, CREATED)),
                Map.of(TRACKING, List.of(item(TRACKING, CREATED, "RUN")),
                        RETRY, List.of(item(RETRY, CREATED, "RUN"))), "RUN"));
    }

    @Test
    void permitsEmptyQueriedGroupsWithoutHidingActualOutputs() {
        assertDoesNotThrow(() -> validate(response(group(TRACKING, CREATED), group(RETRY)),
                Map.of(TRACKING, List.of(item(TRACKING, CREATED, "RUN")), RETRY, List.of()), "RUN"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"DRY", "OTHER"})
    void rejectsWrongOrMissingProcessingMode(String mode) {
        AssertionError error = assertThrows(AssertionError.class, () -> validate(response(group(TRACKING, CREATED)),
                Map.of(TRACKING, List.of(item(TRACKING, CREATED, mode))), "RUN"));
        assertThat(error.getMessage()).contains("processingMode", TRACKING);
    }

    @Test
    void rejectsMixedModesInsteadOfFilteringOutWrongRecords() {
        AssertionError error = assertThrows(AssertionError.class, () -> validate(response(group(TRACKING, CREATED, NEXT_CREATED)),
                Map.of(TRACKING, List.of(item(TRACKING, CREATED, "RUN"), item(TRACKING, NEXT_CREATED, "DRY"))), "RUN"));
        assertThat(error.getMessage()).contains("processingMode", TRACKING, NEXT_CREATED, "RUN", "DRY");
    }

    @Test
    void rejectsOnlyOnePersistedOutputWhenApiContainsTwo() {
        assertThrows(AssertionError.class, () -> validate(response(group(TRACKING, CREATED, NEXT_CREATED)),
                Map.of(TRACKING, List.of(item(TRACKING, CREATED, "RUN"))), "RUN"));
    }

    @Test
    void rejectsUnexpectedPersistedGroup() {
        assertThrows(AssertionError.class, () -> validate(response(group(TRACKING, CREATED)),
                Map.of(TRACKING, List.of(item(TRACKING, CREATED, "RUN")),
                        RETRY, List.of(item(RETRY, CREATED, "RUN"))), "RUN"));
    }

    @Test
    void rejectsMissingPersistedGroup() {
        assertThrows(AssertionError.class, () -> validate(response(group(TRACKING, CREATED), group(RETRY, CREATED)),
                Map.of(TRACKING, List.of(item(TRACKING, CREATED, "RUN")), RETRY, List.of()), "RUN"));
    }

    @Test
    void rejectsUnexpectedPersistedCreated() {
        assertThrows(AssertionError.class, () -> validate(response(group(TRACKING, CREATED)),
                Map.of(TRACKING, List.of(item(TRACKING, CREATED, "RUN"), item(TRACKING, NEXT_CREATED, "RUN"))), "RUN"));
    }

    @Test
    void rejectsDuplicatedApiCreatedWithinOneTracking() {
        assertThrows(AssertionError.class, () -> validate(response(group(TRACKING, CREATED, CREATED)),
                Map.of(TRACKING, List.of(item(TRACKING, CREATED, "RUN"))), "RUN"));
    }

    @Test
    void rejectsDuplicatedPersistedCreatedWithinOneTracking() {
        assertThrows(AssertionError.class, () -> validate(response(group(TRACKING, CREATED)),
                Map.of(TRACKING, List.of(item(TRACKING, CREATED, "RUN"), item(TRACKING, CREATED, "RUN"))), "RUN"));
    }

    @Test
    void rejectsDuplicatedApiTrackingGroup() {
        assertThrows(AssertionError.class, () -> validate(response(group(TRACKING, CREATED), group(TRACKING, NEXT_CREATED)),
                Map.of(TRACKING, List.of(item(TRACKING, CREATED, "RUN"), item(TRACKING, NEXT_CREATED, "RUN"))), "RUN"));
    }

    @Test
    void rejectsPersistedTrackingIdFromAnotherGroup() {
        assertThrows(AssertionError.class, () -> validate(response(group(TRACKING, CREATED)),
                Map.of(TRACKING, List.of(item(RETRY, CREATED, "RUN"))), "RUN"));
    }

    @Test
    void rejectsResponseWithoutAnyActualOutput() {
        assertThrows(AssertionError.class, () -> validate(response(group(TRACKING)), Map.of(TRACKING, List.of()), "RUN"));
    }

    @Test
    void rejectsEmptyResultsAndNullResponse() {
        assertThrows(AssertionError.class, () -> validate(response(), Map.of(), "RUN"));
        assertThrows(AssertionError.class, () -> validate(null, Map.of(), "RUN"));
    }

    @Test
    void rejectsApiOutputWithoutCreated() {
        assertThrows(AssertionError.class, () -> validate(response(group(TRACKING, (String) null)),
                Map.of(TRACKING, List.of(item(TRACKING, CREATED, "RUN"))), "RUN"));
    }

    @Test
    void rejectsPersistedOutputWithoutCreated() {
        assertThrows(AssertionError.class, () -> validate(response(group(TRACKING, CREATED)),
                Map.of(TRACKING, List.of(item(TRACKING, null, "RUN"))), "RUN"));
    }

    @Test
    void rejectsInvalidExpectedModeBeforeReadingOutputs() {
        assertThrows(IllegalArgumentException.class, () -> validate(null, null, "OTHER"));
    }

    @Test
    void doesNotExposeRecordPayloadInFailureMessage() {
        Map<String, AttributeValue> record = item(TRACKING, CREATED, "DRY");
        record.put("payload", string("do-not-print-payload"));
        AssertionError error = assertThrows(AssertionError.class, () -> validate(response(group(TRACKING, CREATED)),
                Map.of(TRACKING, List.of(record)), "RUN"));
        assertThat(error.getMessage()).contains(TRACKING, CREATED).doesNotContain("do-not-print-payload");
    }

    private static void validate(PaperTrackerOutputsResponse response,
                                 Map<String, List<Map<String, AttributeValue>>> persisted,
                                 String mode) {
        PaperTrackerOutputProcessingModeValidator.validate(response, persisted, mode);
    }

    private static PaperTrackerOutputsResponse response(PaperTrackerOutputsResponseResultsInner... groups) {
        return new PaperTrackerOutputsResponse().results(Arrays.asList(groups));
    }

    private static PaperTrackerOutputsResponseResultsInner group(String trackingId, String... createdValues) {
        return new PaperTrackerOutputsResponseResultsInner().trackingId(trackingId).outputs(Arrays.stream(createdValues)
                .map(created -> new PaperTrackerOutput().created(created)).toList());
    }

    private static Map<String, AttributeValue> item(String trackingId, String created, String mode) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("trackingId", string(trackingId));
        if (created != null) {
            item.put("created", string(created));
        }
        if (mode != null) {
            item.put("processingMode", string(mode));
        }
        return item;
    }

    private static AttributeValue string(String value) {
        return AttributeValue.builder().s(value).build();
    }
}
