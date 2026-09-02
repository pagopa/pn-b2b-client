package it.pagopa.pn.cucumber.steps.ioMock;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.cucumber.steps.ioMock.dto.IoMockMessagePayloadBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class IoMockMessageSubmissionUnitTest {

    private static final String IO_MESSAGE_ID_REGEX = "^MOCK-([A-Za-z0-9_]+)-(\\d+)-([A-Za-z0-9_]+)$";
    private static final Pattern IO_MESSAGE_ID_PATTERN = Pattern.compile(IO_MESSAGE_ID_REGEX);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("IoMockMessagePayloadBuilder default values conform to OpenAPI spec (feature_level_type = ADVANCED)")
    void testPayloadBuilderDefaults() {
        Map<String, Object> payload = IoMockMessagePayloadBuilder.builder()
                .withSequence("OK_READ_THEN_PAID")
                .buildMap();

        assertThat(payload.get("fiscal_code")).isEqualTo("RSSMRA80A01H5010");
        assertThat(payload.get("feature_level_type")).isEqualTo("ADVANCED");

        @SuppressWarnings("unchecked")
        Map<String, Object> content = (Map<String, Object>) payload.get("content");
        assertThat(content).isNotNull();
        assertThat(content.get("subject")).isEqualTo("Comunicazione istituzionale @io:OK_READ_THEN_PAID");
        assertThat(content.get("markdown")).isNotNull();
    }

    @Test
    @DisplayName("IoMockMessagePayloadBuilder supports omitting fields and adding raw extra fields")
    void testPayloadBuilderCustomizations() {
        Map<String, Object> payloadMissingCf = IoMockMessagePayloadBuilder.builder()
                .withoutField("fiscal_code")
                .buildMap();
        assertThat(payloadMissingCf).doesNotContainKey("fiscal_code");

        Map<String, Object> payloadExtra = IoMockMessagePayloadBuilder.builder()
                .withExtraField("custom_prop", 999)
                .buildMap();
        assertThat(payloadExtra).containsEntry("custom_prop", 999);
    }

    @Test
    @DisplayName("IoMessageId regex validation conforms to MOCK-<sequenceName>-<submitMillis>-<rand>")
    void testIoMessageIdRegexMatching() {
        long currentMillis = System.currentTimeMillis();
        String validId = String.format("MOCK-OK_READ_THEN_PAID-%d-abc123XYZ", currentMillis);

        Matcher matcher = IO_MESSAGE_ID_PATTERN.matcher(validId);
        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group(1)).isEqualTo("OK_READ_THEN_PAID");
        assertThat(Long.parseLong(matcher.group(2))).isEqualTo(currentMillis);
        assertThat(matcher.group(3)).isEqualTo("abc123XYZ");

        // Negative check on invalid patterns
        assertThat(IO_MESSAGE_ID_PATTERN.matcher("REAL-IO-12345").matches()).isFalse();
        assertThat(IO_MESSAGE_ID_PATTERN.matcher("MOCK-INVALID").matches()).isFalse();
        assertThat(IO_MESSAGE_ID_PATTERN.matcher("MOCK-seq-notanumber-rand").matches()).isFalse();
        // UUID with hyphens as rand token should fail the strict [A-Za-z0-9_] regex
        assertThat(IO_MESSAGE_ID_PATTERN.matcher("MOCK-SEQ-1725267600000-550e8400-e29b-41d4-a716-446655440000").matches()).isFalse();
        // Clean alphanumeric / underscore rand token should pass
        assertThat(IO_MESSAGE_ID_PATTERN.matcher("MOCK-std_read_paid-1725267600000-550e8400e29b41d4a716446655440000").matches()).isTrue();
    }

    @Test
    @DisplayName("IoMockMessagePayloadBuilder supports omitting content or sub-fields subject/markdown")
    void testPayloadBuilderContentOmissions() {
        Map<String, Object> payloadNoContent = IoMockMessagePayloadBuilder.builder()
                .withoutField("content")
                .buildMap();
        assertThat(payloadNoContent).doesNotContainKey("content");

        Map<String, Object> payloadNoSubject = IoMockMessagePayloadBuilder.builder()
                .withoutContentField("subject")
                .buildMap();
        @SuppressWarnings("unchecked")
        Map<String, Object> contentMap = (Map<String, Object>) payloadNoSubject.get("content");
        assertThat(contentMap).doesNotContainKey("subject");
        assertThat(contentMap).containsKey("markdown");
    }
}
