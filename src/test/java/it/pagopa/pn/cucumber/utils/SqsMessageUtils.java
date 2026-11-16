package it.pagopa.pn.cucumber.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Utility per riconoscere messaggi SQS (es. letti da una DLQ condivisa) a partire dal body JSON,
 * così da distinguere il messaggio atteso da eventuali residui di run precedenti.
 */
public final class SqsMessageUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private SqsMessageUtils() {
    }

    /**
     * Restituisce il body del messaggio come JSON; vuoto se il body è assente o non è JSON valido.
     */
    public static Optional<JsonNode> readBody(Message message) {
        if (message == null || message.body() == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(OBJECT_MAPPER.readTree(message.body()));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    /**
     * Verifica una condizione sul body JSON del messaggio; {@code false} se il body non è leggibile.
     */
    public static boolean bodyMatches(Message message, Predicate<JsonNode> condition) {
        return readBody(message).map(condition::test).orElse(false);
    }

    /**
     * Verifica una condizione sul valore testuale di un campo top-level del body;
     * {@code false} se il campo è assente/null o il body non è leggibile.
     */
    public static boolean fieldMatches(Message message, String fieldName, Predicate<String> condition) {
        return bodyMatches(message, body -> body.hasNonNull(fieldName) && condition.test(body.get(fieldName).asText()));
    }

    /**
     * Verifica che il body del messaggio abbia {@code taxId} uguale a quello atteso.
     */
    public static boolean hasTaxId(Message message, String taxId) {
        return fieldMatches(message, "taxId", taxId::equals);
    }
}
