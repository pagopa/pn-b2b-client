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
     * Verifica che il body del messaggio (InternalCodeSqsDto del national-registry) abbia {@code taxId} uguale a quello
     * atteso e un {@code correlationId} pari al prefisso del correlationId su DB
     * (il servizio pubblica {@code correlationId.split(batchRequestPkSeparator)[0]}).
     */
    public static boolean isNationalRegistryDlqMessageFor(Message message, String taxId, String dbCorrelationId) {
        return bodyMatches(message, body -> {
            String messageCorrelationId = body.path("correlationId").asText();
            return taxId.equals(body.path("taxId").asText())
                    && !messageCorrelationId.isEmpty()
                    && dbCorrelationId.startsWith(messageCorrelationId);
        });
    }
}
