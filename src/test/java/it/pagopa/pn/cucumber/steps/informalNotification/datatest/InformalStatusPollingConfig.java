package it.pagopa.pn.cucumber.steps.informalNotification.datatest;

import lombok.Data;

import java.util.Set;

@Data
public class InformalStatusPollingConfig {

    private final String expectedStatus;
    private final Set<String> stopStatuses;

    public enum DefaultStatusValue {

        ACCEPTED("ACCEPTED", Set.of("REFUSED")),
        PROCESSING("PROCESSING", Set.of("REFUSED")),
        COMPLETED_REACHED("COMPLETED_REACHED", Set.of("COMPLETED_UNREACHED", "UNDELIVERABLE")),
        COMPLETED_UNREACHED("COMPLETED_UNREACHED", Set.of("COMPLETED_REACHED", "UNDELIVERABLE")),
        UNDELIVERABLE("UNDELIVERABLE", Set.of("COMPLETED_REACHED", "COMPLETED_UNREACHED")),
        REFUSED("REFUSED", Set.of("ACCEPTED"));

        private final String expectedStatus;
        private final Set<String> stopStatuses;

        DefaultStatusValue(String expectedStatus, Set<String> stopStatuses) {

            this.expectedStatus = expectedStatus;
            this.stopStatuses = stopStatuses;
        }

        public String getExpectedStatus() {
            return expectedStatus;
        }

        public Set<String> getStopStatuses() {
            return stopStatuses;
        }
    }
}

