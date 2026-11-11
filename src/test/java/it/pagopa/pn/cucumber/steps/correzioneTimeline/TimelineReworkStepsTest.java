package it.pagopa.pn.cucumber.steps.correzioneTimeline;

import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery.rework.model.RestartAttemptResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery.rework.model.ReworkError;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery.rework.model.ReworkItem;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery.rework.model.ReworkItemsResponse;
import it.pagopa.pn.client.b2b.pa.service.impl.ReworkTimelineClientImpl;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TimelineReworkStepsTest {

    private static final String IUN = "test-iun";
    private static final String REWORK_ID = "test-restart";
    private static final String PAYMENT_DESCRIPTION = "PAYMENT category found in timeline";

    private ReworkTimelineClientImpl client;
    private TimelineReworkSteps steps;

    @BeforeEach
    void setUp() {
        SharedSteps sharedSteps = mock(SharedSteps.class);
        client = mock(ReworkTimelineClientImpl.class);
        when(sharedSteps.getNotificationIun()).thenReturn(IUN);
        when(client.restartAttempt(eq(IUN), any())).thenReturn(new RestartAttemptResponse().reworkId(REWORK_ID));
        steps = new TimelineReworkSteps(sharedSteps, client, null, null, null);
        steps.callRestartTimeline();
    }

    @Test
    void acceptsPaymentRejectionForCurrentRestart() {
        when(client.retrieveNotificationReworkById(IUN, REWORK_ID)).thenReturn(response(paymentRejectedRestart()));

        assertDoesNotThrow(() -> steps.verifyRestartRejectedForPayment(0));
    }

    @Test
    void ignoresUnrelatedItemsWhenCurrentRestartHasPaymentRejection() {
        when(client.retrieveNotificationReworkById(IUN, REWORK_ID)).thenReturn(response(
                paymentRejectedRestart().reworkId("other-restart").status(ReworkItem.StatusEnum.DONE),
                paymentRejectedRestart()));

        assertDoesNotThrow(() -> steps.verifyRestartRejectedForPayment(0));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidRejections")
    void rejectsErrorsThatDoNotProvePaymentRejection(String description, ReworkItemsResponse response) {
        when(client.retrieveNotificationReworkById(IUN, REWORK_ID)).thenReturn(response);

        assertThrows(AssertionError.class, () -> steps.verifyRestartRejectedForPayment(0));
    }

    private static Stream<Arguments> invalidRejections() {
        return Stream.of(
                Arguments.of("wrong error cause", response(paymentRejectedRestart().errors(List.of(
                        error(ReworkError.CauseEnum.INVALID_ATTEMPT_ID, PAYMENT_DESCRIPTION))))),
                Arguments.of("missing error cause", response(paymentRejectedRestart().errors(List.of(
                        error(null, PAYMENT_DESCRIPTION))))),
                Arguments.of("wrong error description", response(paymentRejectedRestart().errors(List.of(
                        error(ReworkError.CauseEnum.INVALID_TIMELINE_ELEMENT, "Other timeline validation error"))))),
                Arguments.of("missing error description", response(paymentRejectedRestart().errors(List.of(
                        error(ReworkError.CauseEnum.INVALID_TIMELINE_ELEMENT, null))))),
                Arguments.of("cause and description belong to different errors", response(paymentRejectedRestart().errors(List.of(
                        error(ReworkError.CauseEnum.INVALID_TIMELINE_ELEMENT, "Other timeline validation error"),
                        error(ReworkError.CauseEnum.INVALID_ATTEMPT_ID, PAYMENT_DESCRIPTION))))),
                Arguments.of("missing errors", response(paymentRejectedRestart().errors(null))),
                Arguments.of("empty errors", response(paymentRejectedRestart().errors(List.of()))),
                Arguments.of("payment rejection belongs to another restart", response(
                        paymentRejectedRestart().reworkId("other-restart"),
                        paymentRejectedRestart().errors(List.of(error(ReworkError.CauseEnum.INVALID_ATTEMPT_ID, "Invalid attempt"))))),
                Arguments.of("current restart is missing", response(paymentRejectedRestart().reworkId("other-restart"))),
                Arguments.of("wrong recipient", response(paymentRejectedRestart().recIndex("RECINDEX_1"))),
                Arguments.of("wrong request type", response(paymentRejectedRestart().requestType(ReworkItem.RequestTypeEnum.REWORK))),
                Arguments.of("wrong status", response(paymentRejectedRestart().status(ReworkItem.StatusEnum.DONE))),
                Arguments.of("duplicate current restart", response(paymentRejectedRestart(), paymentRejectedRestart()))
        );
    }

    private static ReworkItem paymentRejectedRestart() {
        return new ReworkItem()
                .reworkId(REWORK_ID)
                .recIndex("RECINDEX_0")
                .requestType(ReworkItem.RequestTypeEnum.RESTART)
                .status(ReworkItem.StatusEnum.ERROR)
                .errors(List.of(error(ReworkError.CauseEnum.INVALID_TIMELINE_ELEMENT, PAYMENT_DESCRIPTION)));
    }

    private static ReworkError error(ReworkError.CauseEnum cause, String description) {
        return new ReworkError().cause(cause).description(description);
    }

    private static ReworkItemsResponse response(ReworkItem... items) {
        return new ReworkItemsResponse().items(Arrays.asList(items));
    }
}
