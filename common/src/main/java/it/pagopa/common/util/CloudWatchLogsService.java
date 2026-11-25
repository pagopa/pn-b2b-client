package it.pagopa.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.FilterLogEventsRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.FilterLogEventsResponse;
import software.amazon.awssdk.services.cloudwatchlogs.model.FilteredLogEvent;

import java.util.List;
import java.util.Map;

/**
 * Integrazione con CloudWatch Logs: incapsula {@link CloudWatchLogsClient}, cosi' che i consumer (step Cucumber o
 * altri client) dipendano da questo costrutto del modulo common invece che dall'SDK AWS direttamente.
 */
@Component
public class CloudWatchLogsService {

    private final CloudWatchLogsClient cloudWatchLogsClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CloudWatchLogsService(CloudWatchLogsClient cloudWatchLogsClient) {
        this.cloudWatchLogsClient = cloudWatchLogsClient;
    }

    /** Numero di eventi che soddisfano il filtro, negli ultimi {@code minutes} minuti sul log group indicato. */
    public int countMatchingEvents(String logGroup, String filterPattern, long minutes) {
        return fetchMessages(logGroup, filterPattern, minutes).size();
    }

    /** Variante comoda quando i termini di ricerca sono gia' una lista (vedi {@link CloudWatchQueryBuilder#buildFilterPattern}). */
    public int countMatchingEvents(String logGroup, List<String> terms, long minutes) {
        return countMatchingEvents(logGroup, CloudWatchQueryBuilder.buildFilterPattern(terms), minutes);
    }

    /**
     * Verifica che, tra i log JSON (un oggetto per riga) che soddisfano {@code filterPattern}, ne esista almeno uno
     * i cui campi di primo livello corrispondano esattamente a {@code expectedFields}: un valore atteso {@code null}
     * richiede che il campo sia assente, un valore non nullo richiede che il campo sia presente con quel valore.
     * <p>
     * Non e' possibile ottenere lo stesso risultato componendo un filter pattern CloudWatch piu' articolato: la
     * sintassi dei filter pattern non ammette caratteri {@code "} letterali nei termini (necessari per ancorare un
     * campo JSON, es. {@code "cx_id":"valore"}), quindi il confronto sui campi va fatto lato client sul contenuto
     * gia' recuperato.
     */
    public boolean hasMatchingLog(String logGroup, String filterPattern, long minutes, Map<String, String> expectedFields) {
        return fetchMessages(logGroup, filterPattern, minutes).stream().anyMatch(message -> matchesFields(message, expectedFields));
    }

    /**
     * Verifica che, tra i log JSON che soddisfano {@code filterPattern}, ne esista almeno uno in cui tutti i campi
     * indicati siano valorizzati (presenti e non nulli), indipendentemente dal loro valore. Utile quando il valore
     * atteso non e' noto a priori (es. deriva da un token di autenticazione) e conta solo che il campo sia stato
     * propagato.
     */
    public boolean hasFieldsPresent(String logGroup, String filterPattern, long minutes, List<String> fields) {
        return fetchMessages(logGroup, filterPattern, minutes).stream().anyMatch(message -> fieldsPresent(message, fields));
    }

    private List<String> fetchMessages(String logGroup, String filterPattern, long minutes) {
        FilterLogEventsRequest request = CloudWatchQueryBuilder.search(logGroup, filterPattern, minutes);
        FilterLogEventsResponse response = cloudWatchLogsClient.filterLogEvents(request);
        return response.events().stream().map(FilteredLogEvent::message).toList();
    }

    private boolean matchesFields(String message, Map<String, String> expectedFields) {
        try {
            Map<?, ?> json = objectMapper.readValue(message, Map.class);
            return expectedFields.entrySet().stream().allMatch(expected -> {
                Object actual = json.get(expected.getKey());
                return expected.getValue() == null ? actual == null : expected.getValue().equals(actual);
            });
        } catch (Exception e) {
            return false;
        }
    }

    private boolean fieldsPresent(String message, List<String> fields) {
        try {
            Map<?, ?> json = objectMapper.readValue(message, Map.class);
            return fields.stream().allMatch(field -> json.get(field) != null);
        } catch (Exception e) {
            return false;
        }
    }
}
