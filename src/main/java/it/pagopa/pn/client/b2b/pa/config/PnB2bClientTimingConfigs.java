package it.pagopa.pn.client.b2b.pa.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


@ConfigurationProperties(prefix = "pn.configuration", ignoreUnknownFields = false)
@Data
public class PnB2bClientTimingConfigs {

    private Integer workflowWaitAcceptedMillis;
    private Integer workflowWaitAcceptedExtraRapidMillis;
    private Integer workflowWaitMillis;
    private Integer workflowWaitExtraRapidMillis;
    private Integer waitMillis;
    private Duration schedulingDaysSuccessDigitalRefinement;
    private Duration schedulingDaysFailureDigitalRefinement;
    private Duration schedulingDaysSuccessAnalogRefinement;
    private Duration schedulingDaysFailureAnalogRefinement;
    private Duration nonVisibilityTime;
    private Duration secondNotificationWorkflowWaitingTime;
    private Duration waitingForReadCourtesyMessage;
    private Integer schedulingDeltaMillis;
    private Integer waitingTimingSlowMultiplier;
    private Integer waitMillisShort;
    private Integer waitMillisExtraRapid;
    private Integer waitMillisForSendAnalogEvents;


    private Integer workflowWaitAcceptedMillisShort;
    private Integer workflowWaitMillisShort;

    private Map<String,OverrideConfig> overrides = new HashMap<>();

    @Data
    private static class OverrideConfig {
        private Integer numCheck;
        private Integer waitingMultiplier;
    }


    public enum DefaultsElementTimeValue {
        SENDER_ACK_CREATION_REQUEST(2, 1),
        VALIDATE_NORMALIZE_ADDRESSES_REQUEST(2, 1),
        NORMALIZED_ADDRESS(2, 1),
        REQUEST_ACCEPTED(2, 1),
        SEND_COURTESY_MESSAGE(11, 1),
        GET_ADDRESS(2, 2),
        PUBLIC_REGISTRY_CALL(2, 4),
        PUBLIC_REGISTRY_RESPONSE(2, 4),
        SCHEDULE_ANALOG_WORKFLOW(2, 3),
        SCHEDULE_DIGITAL_WORKFLOW(3, 2),
        PREPARE_DIGITAL_DOMICILE(3, 2),
        SEND_DIGITAL_DOMICILE(2, 2),
        SEND_DIGITAL_PROGRESS(6, 3),
        SEND_DIGITAL_FEEDBACK(6, 3),
        REFINEMENT(15, 1),
        SCHEDULE_REFINEMENT(15, 1),
        DIGITAL_DELIVERY_CREATION_REQUEST(15, 1),
        DIGITAL_SUCCESS_WORKFLOW(8, 3),
        DIGITAL_FAILURE_WORKFLOW(9, 1),
        ANALOG_SUCCESS_WORKFLOW(15, 1),
        ANALOG_FAILURE_WORKFLOW(14, 1),
        PREPARE_SIMPLE_REGISTERED_LETTER(14, 1),
        SEND_SIMPLE_REGISTERED_LETTER(14, 1),
        SEND_SIMPLE_REGISTERED_LETTER_PROGRESS(15, 1),
        NOTIFICATION_VIEWED_CREATION_REQUEST(2, 2),
        NOTIFICATION_VIEWED(2, 2),
        PREPARE_ANALOG_DOMICILE(9, 1),
        SEND_ANALOG_DOMICILE(18, 1),
        SEND_ANALOG_PROGRESS(16, 2),
        SEND_ANALOG_FEEDBACK(11, 1),
        PAYMENT(9, 1),
        COMPLETELY_UNREACHABLE(13, 1),
        COMPLETELY_UNREACHABLE_CREATION_REQUEST(11, 1),
        REQUEST_REFUSED(15, 1),
        AAR_CREATION_REQUEST(2, 2),
        AAR_GENERATION(2, 2),
        NOT_HANDLED(9, 1),
        PROBABLE_SCHEDULING_ANALOG_DATE(15, 1),
        NOTIFICATION_CANCELLATION_REQUEST(9, 1),
        NOTIFICATION_CANCELLED(11, 1),
        PREPARE_ANALOG_DOMICILE_FAILURE(15, 1),
        NOTIFICATION_RADD_RETRIEVED(15, 1),

        //NOTIFICATION STATUS UPDATE TO V2.3
        IN_VALIDATION(2, 1),
        ACCEPTED(2, 1),
        REFUSED(11, 1),
        ACCEPTED_VALIDATION(15, 1),
        NO_ACCEPTED_VALIDATION(8, 1),
        ACCEPTED_SHORT_VALIDATION(231, 1),
        ACCEPTED_EXTRA_RAPID_VALIDATION(450, 1),
        REFUSED_VALIDATION(11, 1),
        DELIVERING(2, 4),
        DELIVERED(8, 4),
        VIEWED(5, 1),
        EFFECTIVE_DATE(11, 1),
        PAID(5, 1),
        UNREACHABLE(11, 1),
        CANCELLED(11, 1),

        //V25
        NOTIFICATION_CANCELLED_DOCUMENT_CREATION_REQUEST(11, 1),

        //TIMING FOR WEBHOOK
        WEBHOOK(150000, 500),

        //TIMING FOR TRACING
        INTEROP_TRACING(3, 1),

        //V26
        RETURNED_TO_SENDER(12, 1),
        ANALOG_WORKFLOW_RECIPIENT_DECEASED(12, 1),

        //V27
        PUBLIC_REGISTRY_VALIDATION_CALL(2, 1),
        PUBLIC_REGISTRY_VALIDATION_RESPONSE(2, 1),

        //v29 todo t v29
        NOTIFICATION_TIMELINE_REWORKED(2, 1);

        private final int defaultNumCheck;
        private final int defaultWaitingMultiplier;

        DefaultsElementTimeValue(int checkNum, int waitingMultiplier) {
            defaultNumCheck = checkNum;
            defaultWaitingMultiplier = waitingMultiplier;

        }
    }

    public int calculateNumCheckValue(String el) {
        var override = overrides.get(el);
        DefaultsElementTimeValue timingValue = DefaultsElementTimeValue.valueOf(el);
        return override == null ? timingValue.defaultNumCheck : override.getNumCheck();
    }

    public int calculateWaitingMultiplierValue(String el) {
        var override = overrides.get(el);
        DefaultsElementTimeValue timingValue = DefaultsElementTimeValue.valueOf(el);
        return override==null ? timingValue.defaultWaitingMultiplier : override.getWaitingMultiplier();
    }


}
