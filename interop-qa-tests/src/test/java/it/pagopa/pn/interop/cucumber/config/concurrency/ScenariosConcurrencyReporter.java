package it.pagopa.pn.interop.cucumber.config.concurrency;

import it.pagopa.pn.interop.cucumber.config.concurrency.writer.ScenariosConcurrencyReportWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

import java.util.List;
import java.util.stream.LongStream;

@Slf4j
@RequiredArgsConstructor
public class ScenariosConcurrencyReporter {

    private final ScenariosConcurrencyAuditor auditor;
    private final List<ScenariosConcurrencyReportWriter> reportWriters;

    /**
     * Rappresenta la timeline: ogni riga è un istante di tempo,
     * ogni booleano indica se lo scenario i-esimo era attivo.
     */
    public record TimelineModel(List<String> scenarioNames, List<Long> timeBuckets, boolean[][] matrix) {}

    /**
     * Rappresenta la sovrapposizione: matrix[i][j] è true se lo scenario i
     * e lo scenario j sono stati eseguiti contemporaneamente.
     */
    public record OverlapModel(List<String> scenarioNames, boolean[][] matrix) {}

    @EventListener
    public void onContextClosed(ContextClosedEvent event) {
        List<ScenariosConcurrencyAuditor.ExecutionRecord> history = auditor.getSortedHistory();

        if (history.isEmpty()) {
            log.warn("[REPORT] Nessun dato raccolto. Verificare la registrazione degli hooks.");
            return;
        }

        TimelineModel timelineModel = this.calculateTimelineModel(history, 1000);
        OverlapModel overlapModel = this.calculateOverlapModel(history);
        for (var writer : reportWriters) {
            writer.write(timelineModel);
            writer.write(overlapModel);
        }


    }

    /**
     * Calcola la matrice della timeline.
     * @param bucketMs la risoluzione temporale (es. 1000ms)
     */
    public TimelineModel calculateTimelineModel(List<ScenariosConcurrencyAuditor.ExecutionRecord> records, long bucketMs) {
        if (records.isEmpty()) return new TimelineModel(List.of(), List.of(), new boolean[0][0]);

        long minStart = records.stream().mapToLong(ScenariosConcurrencyAuditor.ExecutionRecord::start).min().getAsLong();
        long maxEnd = records.stream().mapToLong(ScenariosConcurrencyAuditor.ExecutionRecord::end).max().getAsLong();

        List<String> names = records.stream().map(ScenariosConcurrencyAuditor.ExecutionRecord::scenarioName).toList();

        // Creazione dei bucket temporali
        List<Long> buckets = LongStream.iterate(minStart, t -> t < maxEnd, t -> t + bucketMs)
                .boxed()
                .toList();

        boolean[][] matrix = new boolean[buckets.size()][records.size()];

        for (int tIdx = 0; tIdx < buckets.size(); tIdx++) {
            long tStart = buckets.get(tIdx);
            long tEnd = tStart + bucketMs;

            for (int sIdx = 0; sIdx < records.size(); sIdx++) {
                ScenariosConcurrencyAuditor.ExecutionRecord rec = records.get(sIdx);
                matrix[tIdx][sIdx] = (rec.start() < tEnd) && (rec.end() > tStart);
            }
        }

        return new TimelineModel(names, buckets, matrix);
    }

    /**
     * Calcola la matrice di sovrapposizione tra scenari.
     */
    public OverlapModel calculateOverlapModel(List<ScenariosConcurrencyAuditor.ExecutionRecord> records) {
        int size = records.size();
        List<String> names = records.stream().map(ScenariosConcurrencyAuditor.ExecutionRecord::scenarioName).toList();
        boolean[][] matrix = new boolean[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == j) continue;

                ScenariosConcurrencyAuditor.ExecutionRecord recA = records.get(i);
                ScenariosConcurrencyAuditor.ExecutionRecord recB = records.get(j);

                matrix[i][j] = (recA.start() < recB.end()) && (recB.start() < recA.end());
            }
        }

        return new OverlapModel(names, matrix);
    }
}