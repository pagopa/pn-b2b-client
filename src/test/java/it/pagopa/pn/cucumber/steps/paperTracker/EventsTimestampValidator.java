package it.pagopa.pn.cucumber.steps.paperTracker;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class EventsTimestampValidator implements CustomConditionalValidator {
    List<String> errors;

    private static final String VALIDATION_MESSAGE = "Gli eventi per le triplette A/B/C non cointengono lo stesso timestamp";

    /**
     * Valida il timestamp contenuto all'interno delle triplette degli eventi finali A/B/C.
     * Per passare la validazione, tutti gli eventi finali A/B/C devono contenere lo stesso timestamp.
     *
     * @param trackingNode il nodo trackings da validare
     * @return una lista di messaggi di errore (vuota se valido)
     */
    public List<String> validate(JsonNode trackingNode) {
        errors = new ArrayList<>();
        trackingNode = trackingNode.get("trackings").get(0);
        JsonNode events = trackingNode.get("events");
        if (events == null || !events.isArray()) {
            return errors;
        }
        Set<String> finalEventsTimestamp = new HashSet<>();
        for (JsonNode event : events) {
            JsonNode statusCode = event.get("statusCode");
            if (statusCode != null && statusCode.isTextual()) {
                String statusCodeValue = statusCode.asText();
                if (List.of('A', 'B', 'C').contains(statusCodeValue.charAt(statusCodeValue.length() - 1)) && !statusCodeValue.contains("RECAG011")) {
                    JsonNode timestamp = event.get("statusTimestamp");
                    if (timestamp != null && timestamp.isTextual()) {
                        finalEventsTimestamp.add(timestamp.asText());
                    } else {
                        errors.add("Il campo statusTimestamp è mancante o non è una stringa in un evento finale " + statusCodeValue);
                    }
                }
            }
        }
        if (finalEventsTimestamp.size() > 1) {
            errors.add(VALIDATION_MESSAGE);
        }
        return errors;
    }
}
