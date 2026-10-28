package it.pagopa.pn.cucumber.utils;

public enum TimelineEventId {

    SEND_ANALOG_FEEDBACK("SEND_ANALOG_FEEDBACK") {
        @Override
        public String buildEventId(EventId eventId) {
            return new TimelineEventIdBuilder()
                    .withCategory(this.getValue())
                    .withIun(eventId.getIun())
                    .withRecIndex(eventId.getRecIndex())
                    .withSentAttemptMade(eventId.getSentAttemptMade())
                    .build();
        }
    },

    SEND_ANALOG_PROGRESS("SEND_ANALOG_PROGRESS") {
        @Override
        public String buildEventId(EventId eventId) {
            return new TimelineEventIdBuilder()
                    .withCategory(this.getValue())
                    .withIun(eventId.getIun())
                    .withRecIndex(eventId.getRecIndex())
                    .withSentAttemptMade(eventId.getSentAttemptMade())
                    .withProgressIndex(eventId.getProgressIndex())
                    .build();
        }
    },

    PREPARE_SIMPLE_REGISTERED_LETTER("PREPARE_SIMPLE_REGISTERED_LETTER") {
        @Override
        public String buildEventId(EventId eventId) {
            return new TimelineEventIdBuilder()
                    .withCategory(this.getValue())
                    .withIun(eventId.getIun())
                    .withRecIndex(eventId.getRecIndex())
                    .build();
        }
    },

    SEND_SIMPLE_REGISTERED_LETTER("SEND_SIMPLE_REGISTERED_LETTER") {
        @Override
        public String buildEventId(EventId eventId) {
            return new TimelineEventIdBuilder()
                    .withCategory(this.getValue())
                    .withIun(eventId.getIun())
                    .withRecIndex(eventId.getRecIndex())
                    .build();
        }
    },

    SEND_SIMPLE_REGISTERED_LETTER_PROGRESS("SEND_SIMPLE_REGISTERED_LETTER_PROGRESS") {
        @Override
        public String buildEventId(EventId eventId) {
            return new TimelineEventIdBuilder()
                    .withCategory(this.getValue())
                    .withIun(eventId.getIun())
                    .withRecIndex(eventId.getRecIndex())
                    .withProgressIndex(eventId.getProgressIndex())
                    .build();
        }
    },

    ANALOG_FAILURE_WORKFLOW("ANALOG_FAILURE_WORKFLOW") {
        @Override
        public String buildEventId(EventId eventId) {
            return new TimelineEventIdBuilder()
                    .withCategory(this.getValue())
                    .withIun(eventId.getIun())
                    .withRecIndex(eventId.getRecIndex())
                    .build();
        }
    },

    PREPARE_ANALOG_DOMICILE("PREPARE_ANALOG_DOMICILE") {
        @Override
        public String buildEventId(EventId eventId) {
            return new TimelineEventIdBuilder()
                    .withCategory(this.getValue())
                    .withIun(eventId.getIun())
                    .withRecIndex(eventId.getRecIndex())
                    .withSentAttemptMade(eventId.getSentAttemptMade())
                    .build();
        }
    },

    PREPARE_ANALOG_DOMICILE_FAILURE("PREPARE_ANALOG_DOMICILE_FAILURE") {
        @Override
        public String buildEventId(EventId eventId) {
            return new TimelineEventIdBuilder()
                    .withCategory(this.getValue())
                    .withIun(eventId.getIun())
                    .withRecIndex(eventId.getRecIndex())
//                    .withSentAttemptMade(eventId.getSentAttemptMade())//TODO VAS ???
                    .build();
        }
    },

    SEND_ANALOG_DOMICILE("SEND_ANALOG_DOMICILE") {
        @Override
        public String buildEventId(EventId eventId) {
            return new TimelineEventIdBuilder()
                    .withCategory(this.getValue())
                    .withIun(eventId.getIun())
                    .withRecIndex(eventId.getRecIndex())
                    .withSentAttemptMade(eventId.getSentAttemptMade())
                    .build();
        }
    },

    NOTIFICATION_VIEWED("NOTIFICATION_VIEWED") {
        @Override
        public String buildEventId(EventId eventId) {
            return new TimelineEventIdBuilder()
                    .withCategory(this.getValue())
                    .withIun(eventId.getIun())
                    .withRecIndex(eventId.getRecIndex())
                    .build();
        }
    },

    NOTIFICATION_VIEWED_CREATION_REQUEST("NOTIFICATION_VIEWED_CREATION_REQUEST") {
        @Override
        public String buildEventId(EventId eventId) {
            return new TimelineEventIdBuilder()
                    .withCategory(this.getValue())
                    .withIun(eventId.getIun())
                    .withRecIndex(eventId.getRecIndex())
                    .build();
        }
    },

    COMPLETELY_UNREACHABLE("COMPLETELY_UNREACHABLE") {
        @Override
        public String buildEventId(EventId eventId) {
            return new TimelineEventIdBuilder()
                    .withCategory(this.getValue())
                    .withIun(eventId.getIun())
                    .withRecIndex(eventId.getRecIndex())
                    .build();
        }
    },

    DIGITAL_DELIVERY_CREATION_REQUEST("DIGITAL_DELIVERY_CREATION_REQUEST") {
        @Override
        public String buildEventId(EventId eventId) {
            return new TimelineEventIdBuilder()
                    .withCategory(this.getValue())
                    .withIun(eventId.getIun())
                    .withRecIndex(eventId.getRecIndex())
                    .build();
        }
    },

    SCHEDULE_ANALOG_WORKFLOW("SCHEDULE_ANALOG_WORKFLOW") {
        @Override
        public String buildEventId(EventId eventId) {
            return new TimelineEventIdBuilder()
                    .withCategory(this.getValue())
                    .withIun(eventId.getIun())
                    .withRecIndex(eventId.getRecIndex())
                    .withSentAttemptMade(eventId.getSentAttemptMade())
                    .build();
        }
    },

    REQUEST_REFUSED("REQUEST_REFUSED") {
        @Override
        public String buildEventId(EventId eventId) {
            return new TimelineEventIdBuilder()
                    .withCategory(this.getValue())
                    .withIun(eventId.getIun())
                    .build();
        }
    },

    AAR_GENERATION("AAR_GEN") {
        @Override
        public String buildEventId(EventId eventId) {
            return new TimelineEventIdBuilder()
                    .withCategory(this.getValue())
                    .withIun(eventId.getIun())
                    .withRecIndex(eventId.getRecIndex())
                    .build();
        }
    },

    SEND_COURTESY_MESSAGE("SEND_COURTESY_MESSAGE") {
        @Override
        public String buildEventId(EventId eventId) {
            return new TimelineEventIdBuilder()
                    .withCategory(this.getValue())
                    .withIun(eventId.getIun())
                    .withRecIndex(eventId.getRecIndex())
                    .withCourtesyAddressType(eventId.getCourtesyAddressType())
                    .build();
        }

        @Override
        public String buildSearchEventIdByIunAndRecipientIndex(String iun, Integer recipientIndex) {
            return new TimelineEventIdBuilder()
                    .withCategory(this.getValue())
                    .withIun(iun)
                    .withRecIndex(recipientIndex)
                    .build();
        }

    },

    REQUEST_ACCEPTED("REQUEST_ACCEPTED") {
        @Override
        public String buildEventId(EventId eventId) {
            return new TimelineEventIdBuilder()
                    .withCategory(this.getValue())
                    .withIun(eventId.getIun())
                    .build();
        }
    },

    SEND_DIGITAL_DOMICILE("SEND_DIGITAL") {
        @Override
        public String buildEventId(EventId eventId) {
            return new TimelineEventIdBuilder()
                    .withCategory(this.getValue())
                    .withIun(eventId.getIun())
                    .withRecIndex(eventId.getRecIndex())
                    .withSource(eventId.getSource())
                    .withIsFirstSendRetry(eventId.getIsFirstSendRetry())
                    .withSentAttemptMade(eventId.getSentAttemptMade())
                    .build();
        }
    },

    SEND_DIGITAL_FEEDBACK("SEND_DIGITAL_FEEDBACK") {
        @Override
        public String buildEventId(EventId eventId) {
            return new TimelineEventIdBuilder()
                    .withCategory(this.getValue())
                    .withIun(eventId.getIun())
                    .withRecIndex(eventId.getRecIndex())
                    .withSource(eventId.getSource())
                    .withIsFirstSendRetry(eventId.getIsFirstSendRetry())
                    .withSentAttemptMade(eventId.getSentAttemptMade())
                    .build();
        }
    },

    GET_ADDRESS("GET_ADDRESS") {
        @Override
        public String buildEventId(EventId eventId) {
            return new TimelineEventIdBuilder()
                    .withCategory(this.getValue())
                    .withIun(eventId.getIun())
                    .withRecIndex(eventId.getRecIndex())
                    .withSource(eventId.getSource())
                    .withSentAttemptMade(eventId.getSentAttemptMade())
                    .build();
        }
    },

    DIGITAL_SUCCESS_WORKFLOW("DIGITAL_SUCCESS_WORKFLOW") {
        @Override
        public String buildEventId(EventId eventId) {
            return new TimelineEventIdBuilder()
                    .withCategory(this.getValue())
                    .withIun(eventId.getIun())
                    .withRecIndex(eventId.getRecIndex())
                    .build();
        }
    },

    SCHEDULE_REFINEMENT_WORKFLOW("SCHEDULE_REFINEMENT_WORKFLOW") {
        @Override
        public String buildEventId(EventId eventId) {
            return new TimelineEventIdBuilder()
                    .withCategory(this.getValue())
                    .withIun(eventId.getIun())
                    .withRecIndex(eventId.getRecIndex())
                    .build();
        }
    },

    REFINEMENT("REFINEMENT") {
        @Override
        public String buildEventId(EventId eventId) {
            return new TimelineEventIdBuilder()
                    .withCategory(this.getValue())
                    .withIun(eventId.getIun())
                    .withRecIndex(eventId.getRecIndex())
                    .build();
        }
    },
    DIGITAL_FAILURE_WORKFLOW("DIGITAL_FAILURE_WORKFLOW") {
        @Override
        public String buildEventId(EventId eventId) {
            return new TimelineEventIdBuilder()
                    .withCategory(this.getValue())
                    .withIun(eventId.getIun())
                    .withRecIndex(eventId.getRecIndex())
                    .build();
        }
    },

    ANALOG_SUCCESS_WORKFLOW("ANALOG_SUCCESS_WORKFLOW") {
        @Override
        public String buildEventId(EventId eventId) {
            return new TimelineEventIdBuilder()
                    .withCategory(this.getValue())
                    .withIun(eventId.getIun())
                    .withRecIndex(eventId.getRecIndex())
                    .build();
        }
    },

    ANALOG_WORKFLOW_RECIPIENT_DECEASED("ANALOG_WORKFLOW_RECIPIENT_DECEASED") {
        @Override
        public String buildEventId(EventId eventId) {
            return new TimelineEventIdBuilder()
                    .withCategory(this.getValue())
                    .withIun(eventId.getIun())
                    .withRecIndex(eventId.getRecIndex())
                    .build();
        }
    },

    PUBLIC_REGISTRY_VALIDATION_CALL("PUBLIC_REGISTRY_VALIDATION_CALL") {
        @Override
        public String buildEventId(EventId eventId) {
            StringBuilder sb = new StringBuilder("NATIONAL_REGISTRY_VALIDATION_CALL")
                    .append(TimelineEventIdBuilder.DELIMITER)
                    .append("IUN_")
                    .append(eventId.getIun());
            return sb.toString();
        }
    },

    PUBLIC_REGISTRY_VALIDATION_RESPONSE("PUBLIC_REGISTRY_VALIDATION_RESPONSE") {
        @Override
        public String buildEventId(EventId eventId) {
            StringBuilder sb = new StringBuilder("NATIONAL_REGISTRY_VALIDATION_RESPONSE")
                    .append(TimelineEventIdBuilder.DELIMITER)
                    .append("RECINDEX_")
                    .append(eventId.getRecIndex())
                    .append(TimelineEventIdBuilder.DELIMITER)
                    .append("CORRELATIONID_NATIONAL_REGISTRY_VALIDATION_CALL")
                    .append(TimelineEventIdBuilder.DELIMITER)
                    .append("IUN_")
                    .append(eventId.getIun());
            return sb.toString();
        }
    },
    NOTIFICATION_TIMELINE_REWORKED("NOTIFICATION_TIMELINE_REWORKED") {
        @Override
        public String buildEventId(EventId eventId) {
            return new TimelineEventIdBuilder()
                    .withCategory(this.getValue())
                    .withIun(eventId.getIun())
                    .withRecIndex(eventId.getRecIndex())
                    .build();
        }
    };

    public String buildEventId(EventId eventId) {
        throw new UnsupportedOperationException("Must be implemented for each action type event ID");
    }

    public String buildEventId(String eventId) {
        throw new UnsupportedOperationException("Must be implemented for each action type");
    }

    public String buildSearchEventIdByIunAndRecipientIndex(String iun, Integer recipientIndex) {
        throw new UnsupportedOperationException("Must be implemented for each action type");
    }

    private final String value;

    TimelineEventId(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
