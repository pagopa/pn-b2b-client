package it.pagopa.pn.cucumber.steps.ingestionPagamenti.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Body dei messaggi che pn-kafka-bridge scrive nella DLQ dei pagamenti per gli eventi malformati.
 * L'header dell'evento (eventId MALFORMED_EVENT_&lt;uuid&gt;, eventType ERROR_EVENT, publisher KAFKA_BRIDGE, createdAt)
 * viaggia negli attributi del messaggio SQS. Il body contiene il record Kafka così come ricevuto.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PnErrorEventPayload {
    public static final String MALFORMED_DESCRIPTION = "Error: message malformed.";

    private String originalMessage;
    private String description;
    private String timestamp;
}
