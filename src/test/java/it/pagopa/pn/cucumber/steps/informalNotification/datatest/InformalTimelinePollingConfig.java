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

        REQUEST_ACCEPTED(10, 1),
        REQUEST_REFUSED(10, 1),

        SEND_DIGITAL_MESSAGE(20, 1),
        SEND_DIGITAL_MESSAGE_PROGRESS(30, 1),
        SEND_DIGITAL_MESSAGE_FEEDBACK(60, 1),
        SEND_DIGITAL_MESSAGE_SKIP(60, 1),

        SEND_ANALOG_MESSAGE(30, 1),
        SEND_ANALOG_MESSAGE_PROGRESS(60, 1),
        SEND_ANALOG_MESSAGE_FEEDBACK(60, 1),
        COVERPAGE_CREATION_REQUEST(60, 1),
        PREPARE_ANALOG_DELIVERY(60, 1),

        DELIVERED(90, 1),
        INFORMAL_NOTIFICATION_VIEWED(180, 1),
        PAYMENT(180, 1),

        WORKFLOW_ENDED_REACHED(300, 1),
        WORKFLOW_ENDED_UNREACHED(300, 1),
        WORKFLOW_ENDED_UNDELIVERABLE(300, 1),

        WORKFLOW_DONE_REACHED(300, 1),
        WORKFLOW_DONE_UNREACHED(300, 1);

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
