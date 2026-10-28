package it.pagopa.pn.cucumber.steps.pa.b2bVersions;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElement;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV20;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV23;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV26;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV28;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingPredicate;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheB2bSteps;
import it.pagopa.pn.cucumber.steps.pa.notificationVersions.NotificationVersion;
import it.pagopa.pn.cucumber.steps.utilitySteps.WaitForEventPredicateFilters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static it.pagopa.pn.client.b2b.pa.domain.Costanti.PUBLIC_REGISTRY_RESPONSE;
import static it.pagopa.pn.client.b2b.pa.domain.Costanti.TIMELINE_SLOW;
import static it.pagopa.pn.cucumber.steps.utilitySteps.PollingType.TIMELINE;
import static it.pagopa.pn.cucumber.steps.utilitySteps.checkTimelineElement.TimelineElementCheck.CHECK_DIGITAL_ADDRESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DigitalAddressPollingUnitTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @ParameterizedTest
    @EnumSource(NotificationVersion.class)
    void selectsAddressAfterEmptyRegistryResponses(NotificationVersion version) {
        Predicate<Object> predicate = predicate(version, WaitForEventPredicateFilters.builder()
                .withDigitalAddress(true).recipientIndex(0).build());
        Object emptyResponse = element(version, PUBLIC_REGISTRY_RESPONSE, Map.of("recIndex", 0));
        Object addressResponse = element(version, PUBLIC_REGISTRY_RESPONSE, addressDetails(0));

        assertThat(List.of(emptyResponse).stream().anyMatch(predicate)).isFalse();
        assertThat(List.of(element(version, PUBLIC_REGISTRY_RESPONSE, null), emptyResponse, addressResponse)
                .stream().filter(predicate).findFirst()).contains(addressResponse);
    }

    @ParameterizedTest
    @EnumSource(NotificationVersion.class)
    void rejectsAddressesForOtherRecipientsOrCategories(NotificationVersion version) {
        Predicate<Object> predicate = predicate(version, WaitForEventPredicateFilters.builder()
                .withDigitalAddress(true).recipientIndex(0).build());

        assertThat(predicate.test(element(version, PUBLIC_REGISTRY_RESPONSE, addressDetails(1)))).isFalse();
        assertThat(predicate.test(element(version, "SEND_DIGITAL_DOMICILE", addressDetails(0)))).isFalse();
        assertThat(predicate.test(element(version, PUBLIC_REGISTRY_RESPONSE, Map.of("recIndex", 0)))).isFalse();
    }

    @ParameterizedTest
    @EnumSource(NotificationVersion.class)
    void acceptsAnyRecipientWhenNoRecipientIsSpecified(NotificationVersion version) {
        Predicate<Object> predicate = predicate(version, WaitForEventPredicateFilters.builder()
                .withDigitalAddress(true).build());

        assertThat(predicate.test(element(version, PUBLIC_REGISTRY_RESPONSE, addressDetails(1)))).isTrue();
    }

    @ParameterizedTest
    @EnumSource(NotificationVersion.class)
    void keepsOrdinaryPollingIndependentOfDigitalAddress(NotificationVersion version) {
        Predicate<Object> predicate = predicate(version, WaitForEventPredicateFilters.builder().build());

        assertThat(predicate.test(element(version, PUBLIC_REGISTRY_RESPONSE, null))).isTrue();
        assertThat(predicate.test(element(version, PUBLIC_REGISTRY_RESPONSE, Map.of("recIndex", 0)))).isTrue();
    }

    @Test
    void nationalRegistryStepWaitsForAddressOfRequestedRecipient() {
        assertNationalRegistryStepFilter(1);
    }

    @Test
    void nationalRegistryStepWithoutRecipientWaitsForAnyAddress() {
        assertNationalRegistryStepFilter(null);
    }

    private void assertNationalRegistryStepFilter(Integer recipientIndex) {
        SharedSteps sharedSteps = mock(SharedSteps.class);
        when(sharedSteps.getVersionUsed()).thenReturn(NotificationVersion.V26);
        AvanzamentoNotificheB2bSteps steps = new AvanzamentoNotificheB2bSteps(sharedSteps, null, null, null);
        B2bStepsInterface versionSteps = mock(B2bStepsInterface.class);
        ReflectionTestUtils.setField(steps, "mapOfVersionSteps", Map.of(NotificationVersion.V26, versionSteps));

        if (recipientIndex == null) {
            steps.vieneVerificatoCheElementoTimelineSianoConfiguratoCampoDigitalAddressNationalRegistry(PUBLIC_REGISTRY_RESPONSE);
        } else {
            steps.vieneVerificatoCheNellElementoDiTimelineDellaNotificaSiaPresenteIlCampoDigitalAddressDaNationalRegistryPerLUtente(
                    PUBLIC_REGISTRY_RESPONSE, recipientIndex);
        }

        ArgumentCaptor<WaitForEventPredicateFilters> filters = ArgumentCaptor.forClass(WaitForEventPredicateFilters.class);
        verify(versionSteps).waitForEventOrStatus(eq(TIMELINE_SLOW), eq(TIMELINE), eq(PUBLIC_REGISTRY_RESPONSE), filters.capture());
        assertThat(filters.getValue().isWithDigitalAddress()).isTrue();
        assertThat(filters.getValue().getRecipientIndex()).isEqualTo(recipientIndex);
        verify(versionSteps).checkIfTimelineElementExists(eq(PUBLIC_REGISTRY_RESPONSE), eq(true), eq(CHECK_DIGITAL_ADDRESS), any());
    }

    @SuppressWarnings("unchecked")
    private Predicate<Object> predicate(NotificationVersion version, WaitForEventPredicateFilters filters) {
        AvanzamentoNotificheB2bSteps steps = mock(AvanzamentoNotificheB2bSteps.class);
        when(steps.getSharedSteps()).thenReturn(mock(SharedSteps.class));
        B2bStepsInterface versionSteps = NotificationVersion.createB2bStep(version, steps);
        PnPollingPredicate predicates = ReflectionTestUtils.invokeMethod(versionSteps,
                "getPnPollingPredicateForTimeline", PUBLIC_REGISTRY_RESPONSE, filters);
        Predicate<?> result = switch (version) {
            case V1 -> predicates.getTimelineElementPredicateV1();
            case V2 -> predicates.getTimelineElementPredicateV20();
            case V21 -> predicates.getTimelineElementPredicateV21();
            case V23 -> predicates.getTimelineElementPredicateV23();
            case V24 -> predicates.getTimelineElementPredicateV26();
            case V25, V26 -> predicates.getTimelineElementPredicateV28();
        };
        return (Predicate<Object>) result;
    }

    private Object element(NotificationVersion version, String category, Map<String, Object> details) {
        Class<?> type = switch (version) {
            case V1 -> TimelineElement.class;
            case V2, V21 -> TimelineElementV20.class;
            case V23 -> TimelineElementV23.class;
            case V24 -> TimelineElementV26.class;
            case V25, V26 -> TimelineElementV28.class;
        };
        Map<String, Object> values = new HashMap<>();
        values.put("category", category);
        values.put("details", details);
        return MAPPER.convertValue(values, type);
    }

    private Map<String, Object> addressDetails(int recipientIndex) {
        return Map.of("recIndex", recipientIndex,
                "digitalAddress", Map.of("type", "PEC", "address", "recipient@example.test"));
    }
}
