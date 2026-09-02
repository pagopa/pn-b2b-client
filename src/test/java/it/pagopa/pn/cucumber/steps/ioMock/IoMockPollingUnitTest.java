package it.pagopa.pn.cucumber.steps.ioMock;

import it.pagopa.pn.cucumber.steps.ioMock.dto.IoMockMessageIdHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.regex.Matcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IoMockPollingUnitTest {

    @Test
    @DisplayName("Generazione ID per T0 produce un timestamp coerente con elapsed < 5s")
    void testBuildMockIdForT0() {
        long before = System.currentTimeMillis();
        String idT0 = IoMockMessageIdHelper.buildMockIdForT0("std_read_paid");
        long after = System.currentTimeMillis();

        assertThat(IoMockMessageIdHelper.isValidMockId(idT0)).isTrue();

        IoMockMessageIdHelper.MockIdComponents components = IoMockMessageIdHelper.parseMockId(idT0);
        assertThat(components.getSequenceName()).isEqualTo("std_read_paid");
        assertThat(components.getSubmitMillis()).isBetween(before - 2_000L, after);
        assertThat(components.getRandToken()).isNotBlank();

        IoMockMessageIdHelper.SnapshotState state = IoMockMessageIdHelper.calculateCumulativeState(components.getSubmitMillis(), after);
        assertThat(state.getStatus()).isEqualTo("PROCESSED");
        assertThat(state.getReadStatus()).isNull();
        assertThat(state.getPaymentStatus()).isNull();
    }

    @Test
    @DisplayName("Generazione ID per T1 produce un timestamp retrodatato tra 5s e 15s con read_status READ")
    void testBuildMockIdForT1() {
        long now = System.currentTimeMillis();
        String idT1 = IoMockMessageIdHelper.buildMockIdForT1("OK_READ_THEN_PAID");

        assertThat(IoMockMessageIdHelper.isValidMockId(idT1)).isTrue();

        IoMockMessageIdHelper.MockIdComponents components = IoMockMessageIdHelper.parseMockId(idT1);
        assertThat(components.getSequenceName()).isEqualTo("OK_READ_THEN_PAID");
        assertThat(now - components.getSubmitMillis()).isBetween(5_000L, 14_999L);

        IoMockMessageIdHelper.SnapshotState state = IoMockMessageIdHelper.calculateCumulativeState(components.getSubmitMillis(), now);
        assertThat(state.getStatus()).isEqualTo("PROCESSED");
        assertThat(state.getReadStatus()).isEqualTo("READ");
        assertThat(state.getPaymentStatus()).isNull();
    }

    @Test
    @DisplayName("Generazione ID per T2 produce un timestamp retrodatato >= 15s con snapshot cumulativo PAID")
    void testBuildMockIdForT2() {
        long now = System.currentTimeMillis();
        String idT2 = IoMockMessageIdHelper.buildMockIdForT2("OK_READ_THEN_PAID");

        assertThat(IoMockMessageIdHelper.isValidMockId(idT2)).isTrue();

        IoMockMessageIdHelper.MockIdComponents components = IoMockMessageIdHelper.parseMockId(idT2);
        assertThat(components.getSequenceName()).isEqualTo("OK_READ_THEN_PAID");
        assertThat(now - components.getSubmitMillis()).isGreaterThanOrEqualTo(15_000L);

        IoMockMessageIdHelper.SnapshotState state = IoMockMessageIdHelper.calculateCumulativeState(components.getSubmitMillis(), now);
        assertThat(state.getStatus()).isEqualTo("PROCESSED");
        assertThat(state.getReadStatus()).isEqualTo("READ");
        assertThat(state.getPaymentStatus()).isEqualTo("PAID");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "MOCK-std_read_paid-1725267600000-abc123XYZ",
            "MOCK-OK_READ_THEN_PAID-1725267600000-550e8400e29b41d4a716446655440000",
            "MOCK-custom_seq_1-0-token_1"
    })
    @DisplayName("Verifica validità sintattica per ID mock ben formati")
    void testValidMockIds(String validId) {
        assertThat(IoMockMessageIdHelper.isValidMockId(validId)).isTrue();
        Matcher matcher = IoMockMessageIdHelper.IO_MESSAGE_ID_PATTERN.matcher(validId);
        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group(1)).isNotBlank();
        assertThat(matcher.group(2)).isNotBlank();
        assertThat(matcher.group(3)).isNotBlank();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "01ARZ3NDEKTSV4RRFFQ69G5FAV",
            "MOCK-std_read_paid-notanumber-rand123",
            "MOCK-INVALID",
            "MOCK--123-abc",
            "MOCK_malformed_underscore",
            "MOCK-seq-123-invalid@char",
            ""
    })
    @DisplayName("Verifica invalidità per identificativi malformati o privi di prefisso MOCK-")
    void testInvalidMockIds(String invalidId) {
        assertThat(IoMockMessageIdHelper.isValidMockId(invalidId)).isFalse();
    }

    @Test
    @DisplayName("parseMockId lancia eccezione per ID non conformi")
    void testParseMockIdException() {
        assertThatThrownBy(() -> IoMockMessageIdHelper.parseMockId("INVALID_ID_123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("non valido");

        assertThatThrownBy(() -> IoMockMessageIdHelper.parseMockId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null");
    }
}
