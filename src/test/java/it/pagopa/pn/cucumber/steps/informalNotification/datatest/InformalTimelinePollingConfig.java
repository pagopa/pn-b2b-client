package it.pagopa.pn.cucumber.steps.informalNotification.datatest;


import lombok.Data;

@Data
public class InformalTimelinePollingConfig {

    private Integer numCheck;
    private Integer waitingMultiplier;

    public InformalTimelinePollingConfig(
            Integer numCheck,
            Integer waitingMultiplier) {

        this.numCheck = numCheck;
        this.waitingMultiplier = waitingMultiplier;
    }

    public enum DefaultElementTimeValue {

        REQUEST_ACCEPTED(50, 3),
        REQUEST_REFUSED(50, 3),

        SEND_COURTESY_MESSAGE(200, 5),

        SEND_DIGITAL_MESSAGE(20, 10),
        SEND_DIGITAL_MESSAGE_PROGRESS(200, 5),
        SEND_DIGITAL_MESSAGE_FEEDBACK(200, 5),
        SEND_DIGITAL_MESSAGE_SKIP(200, 5),
        SEND_DIGITAL_FEEDBACK(200, 5),

        SEND_ANALOG_MESSAGE(200, 5),
        SEND_ANALOG_MESSAGE_PROGRESS(200, 5),
        SEND_ANALOG_MESSAGE_FEEDBACK(200, 5),
        COVERPAGE_CREATION_REQUEST(150, 5),
        PREPARE_ANALOG_DELIVERY(150, 5),

        DELIVERED(200, 5),
        INFORMAL_NOTIFICATION_VIEWED(50, 5),
        PAYMENT(180, 5),

        WORKFLOW_ENDED_REACHED(300, 5),
        WORKFLOW_ENDED_UNREACHED(300, 5),
        WORKFLOW_ENDED_UNDELIVERABLE(300, 5),

        WORKFLOW_DONE_REACHED(200, 5),
        WORKFLOW_DONE_UNREACHED(200, 5),

        PUBLIC_REGISTRY_CALL(300, 5),
        GET_ADDRESS(300, 5),

        PUBLIC_REGISTRY_VALIDATION_CALL(300, 5),
        PUBLIC_REGISTRY_VALIDATION_RESPONSE(300, 5),
        VALIDATE_NORMALIZE_ADDRESSES_REQUEST(300, 5);

        private final Integer numCheck;
        private final Integer waitingMultiplier;

        DefaultElementTimeValue(
                Integer numCheck,
                Integer waitingMultiplier) {

            this.numCheck = numCheck;
            this.waitingMultiplier = waitingMultiplier;
        }

        public Integer getNumCheck() {
            return numCheck;
        }

        public Integer getWaitingMultiplier() {
            return waitingMultiplier;
        }
    }
}
