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

    private Map<String, OverrideConfig> overrides = new HashMap<>();

    @Data
    private static class OverrideConfig {
        private Integer numCheck;
        private Integer waitingMultiplier;
    }


    public enum DefaultElementTimeValue {
        SENDER_ACK_CREATION_REQUEST(22, 1),
        VALIDATE_NORMALIZE_ADDRESSES_REQUEST(22, 1),
        NORMALIZED_ADDRESS(22, 1),
        REQUEST_ACCEPTED(22, 1),
        SEND_COURTESY_MESSAGE(31, 1),
        GET_ADDRESS(12, 2),
        PUBLIC_REGISTRY_CALL(7, 4),
        PUBLIC_REGISTRY_RESPONSE(7, 4),
        SCHEDULE_ANALOG_WORKFLOW(9, 3),
        SCHEDULE_DIGITAL_WORKFLOW(13, 2),
        PREPARE_DIGITAL_DOMICILE(13, 2),
        SEND_DIGITAL_DOMICILE(12, 2),
        SEND_DIGITAL_PROGRESS(13, 3),
        SEND_DIGITAL_FEEDBACK(13, 3),
        REFINEMENT(35, 1),
        SCHEDULE_REFINEMENT(35, 1),
        DIGITAL_DELIVERY_CREATION_REQUEST(35, 1),
        DIGITAL_SUCCESS_WORKFLOW(15, 3),
        DIGITAL_FAILURE_WORKFLOW(29, 1),
        ANALOG_SUCCESS_WORKFLOW(45, 1),
        ANALOG_FAILURE_WORKFLOW(45, 1),
        PREPARE_SIMPLE_REGISTERED_LETTER(34, 1),
        SEND_SIMPLE_REGISTERED_LETTER(40, 1),
        SEND_SIMPLE_REGISTERED_LETTER_PROGRESS(40, 1),
        NOTIFICATION_VIEWED_CREATION_REQUEST(12, 2),
        NOTIFICATION_VIEWED(12, 2),
        PREPARE_ANALOG_DOMICILE(29, 1),
        SEND_ANALOG_DOMICILE(38, 1),
        SEND_ANALOG_PROGRESS(26, 2),
        SEND_ANALOG_FEEDBACK(31, 1),
        PAYMENT(29, 1),
        COMPLETELY_UNREACHABLE(33, 1),
        COMPLETELY_UNREACHABLE_CREATION_REQUEST(31, 1),
        REQUEST_REFUSED(35, 1),
        AAR_CREATION_REQUEST(12, 2),
        AAR_GENERATION(12, 2),
        NOT_HANDLED(29, 1),
        PROBABLE_SCHEDULING_ANALOG_DATE(35, 1),
        NOTIFICATION_CANCELLATION_REQUEST(29, 1),
        NOTIFICATION_CANCELLED(31, 1),
        PREPARE_ANALOG_DOMICILE_FAILURE(35, 1),
        NOTIFICATION_RADD_RETRIEVED(35, 1),

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
        EFFECTIVE_DATE(16, 1),
        PAID(5, 1),
        UNREACHABLE(11, 1),
        CANCELLED(11, 1),

        //V25
        NOTIFICATION_CANCELLED_DOCUMENT_CREATION_REQUEST(31, 1),

        //TIMING FOR WEBHOOK
        WEBHOOK(150000, 500),

        //TIMING FOR GPD / payment-info
        PAYMENT_INFO(150, 1),

        //TIMING FOR TRACING
        INTEROP_TRACING(3, 1),

        //V26
        RETURNED_TO_SENDER(32, 1),
        ANALOG_WORKFLOW_RECIPIENT_DECEASED(40, 1),

        //V27
        PUBLIC_REGISTRY_VALIDATION_CALL(22, 1),
        PUBLIC_REGISTRY_VALIDATION_RESPONSE(22, 1),

        //V29
        NOTIFICATION_TIMELINE_REWORKED(36, 1);

        private final int defaultNumCheck;
        private final int defaultWaitingMultiplier;

        DefaultElementTimeValue(int checkNum, int waitingMultiplier) {
            defaultNumCheck = checkNum;
            defaultWaitingMultiplier = waitingMultiplier;
        }
    }

    public int calculateNumCheckValue(String el) {
        var override = overrides.get(el);
        DefaultElementTimeValue defaultTimingValue = DefaultElementTimeValue.valueOf(el);
        if (override != null && override.getNumCheck() != null) {
            return override.getNumCheck();
        }
        return defaultTimingValue.defaultNumCheck;
    }

    public int calculateWaitingMultiplierValue(String el) {
        var override = overrides.get(el);
        DefaultElementTimeValue defaultTimingValue = DefaultElementTimeValue.valueOf(el);
        if (override != null && override.getWaitingMultiplier() != null) {
            return override.getWaitingMultiplier();
        }
        return defaultTimingValue.defaultWaitingMultiplier;
    }


}
