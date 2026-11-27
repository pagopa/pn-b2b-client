package it.pagopa.pn.cucumber.steps.pa.service;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

/**
 * Record della tabella pn-PtDelegations: ogni variazione di una delega PA→PT (creazione, revoca)
 * viene scritta come nuova riga, così la tabella conserva lo storico.
 *
 * @param status stato della variazione come salvato su Dynamo (ACTIVE / DELETED)
 */
public record PtDelegationRecord(String delegationId,
                                 String institutionId,
                                 String status,
                                 Instant createdAt,
                                 Instant lastChangeAt,
                                 String sourceEventType) {

    public static PtDelegationRecord from(Map<String, AttributeValue> item) {
        return new PtDelegationRecord(
                string(item, "delegationId"),
                string(item, "institutionId"),
                string(item, "status"),
                instant(item, "createdAt"),
                instant(item, "lastChangeAt"),
                string(item, "sourceEventType"));
    }

    private static String string(Map<String, AttributeValue> item, String attribute) {
        return Optional.ofNullable(item.get(attribute)).map(AttributeValue::s).orElse(null);
    }

    private static Instant instant(Map<String, AttributeValue> item, String attribute) {
        return Optional.ofNullable(string(item, attribute)).map(Instant::parse).orElse(null);
    }
}
