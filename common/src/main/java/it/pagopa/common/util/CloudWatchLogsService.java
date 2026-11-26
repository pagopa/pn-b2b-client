package it.pagopa.common.util;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.FilterLogEventsRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.FilteredLogEvent;
import software.amazon.awssdk.services.cloudwatchlogs.paginators.FilterLogEventsIterable;

import java.util.List;

/**
 * Integrazione con CloudWatch Logs: incapsula {@link CloudWatchLogsClient}, cosi' che i consumer (step Cucumber o
 * altri client) dipendano da questo costrutto del modulo common invece che dall'SDK AWS direttamente.
 */
@Component
public class CloudWatchLogsService {

    private final CloudWatchLogsClient cloudWatchLogsClient;

    public CloudWatchLogsService(CloudWatchLogsClient cloudWatchLogsClient) {
        this.cloudWatchLogsClient = cloudWatchLogsClient;
    }

    /** Numero di eventi che soddisfano il filtro, negli ultimi {@code minutes} minuti sul log group indicato. */
    public int countMatchingEvents(String logGroup, String filterPattern, long minutes) {
        return fetchMessages(logGroup, filterPattern, minutes).size();
    }

    /**
     * Variante comoda quando i termini di ricerca sono gia' liste di termini da includere/escludere (vedi
     * {@link CloudWatchQueryBuilder#buildFilterPattern}).
     */
    public int countMatchingEvents(String logGroup, List<String> includeTerms, List<String> excludeTerms, long minutes) {
        return countMatchingEvents(logGroup, CloudWatchQueryBuilder.buildFilterPattern(includeTerms, excludeTerms), minutes);
    }

    private List<String> fetchMessages(String logGroup, String filterPattern, long minutes) {
        FilterLogEventsRequest request = CloudWatchQueryBuilder.search(logGroup, filterPattern, minutes);
        FilterLogEventsIterable responses = cloudWatchLogsClient.filterLogEventsPaginator(request);
        return responses.events().stream().map(FilteredLogEvent::message).toList();
    }
}
