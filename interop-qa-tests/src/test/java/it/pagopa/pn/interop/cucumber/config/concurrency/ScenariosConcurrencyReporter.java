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
    private final long bucketMs;

    /**
     * Rappresenta lo stato di attivazione degli scenari lungo una linea temporale suddivisa in bucket.
     */
    public record TimelineModel(List<String> scenarioNames, List<Long> timeBuckets, boolean[][] matrix) {}

    /**
     * Rappresenta le intersezioni temporali (concorrenza) tra coppie di scenari.
     */
    public record OverlapModel(List<String> scenarioNames, boolean[][] matrix) {}

    /**
     * Allo spegnimento del contesto Spring, genera i modelli e invoca i writer.
     * La gestione degli errori garantisce che il fallimento di un writer non blocchi gli altri.
     */
    @EventListener
    public void onContextClosed(ContextClosedEvent event) {
        List<ScenariosConcurrencyAuditor.ExecutionRecord> history = auditor.getSortedHistory();

        if (history.isEmpty()) {
            log.warn("[REPORT] Nessun dato raccolto. Verificare che gli hooks stiano registrando correttamente.");
            return;
        }

        log.info("[REPORT] Elaborazione modelli di concorrenza per {} record...", history.size());

        TimelineModel timelineModel = this.calculateTimelineModel(history, bucketMs);
        OverlapModel overlapModel = this.calculateOverlapModel(history);

        for (var writer : reportWriters) {
            try {
                writer.write(timelineModel);
                writer.write(overlapModel);
            } catch (Exception e) {
                log.error("[REPORT] Errore critico durante l'esecuzione del writer: {}",
                        writer.getClass().getSimpleName(), e);
            }
        }
    }

    /**
     * Trasforma i record di esecuzione in una matrice temporale discreta.
     */
    public TimelineModel calculateTimelineModel(List<ScenariosConcurrencyAuditor.ExecutionRecord> records, long resolutionMs) {
        if (records.isEmpty()) return new TimelineModel(List.of(), List.of(), new boolean[0][0]);

        long minStart = records.stream().mapToLong(ScenariosConcurrencyAuditor.ExecutionRecord::start).min().getAsLong();
        long maxEnd = records.stream().mapToLong(ScenariosConcurrencyAuditor.ExecutionRecord::end).max().getAsLong();

        List<String> names = records.stream().map(ScenariosConcurrencyAuditor.ExecutionRecord::scenarioName).toList();

        List<Long> buckets = LongStream.iterate(minStart, t -> t < maxEnd, t -> t + resolutionMs)
                .boxed()
                .toList();

        boolean[][] matrix = new boolean[buckets.size()][records.size()];

        for (int tIdx = 0; tIdx < buckets.size(); tIdx++) {
            long tStart = buckets.get(tIdx);
            long tEnd = tStart + resolutionMs;

            for (int sIdx = 0; sIdx < records.size(); sIdx++) {
                ScenariosConcurrencyAuditor.ExecutionRecord rec = records.get(sIdx);
                // Uno scenario è attivo se il suo intervallo [start, end] si sovrappone al bucket [tStart, tEnd]
                matrix[tIdx][sIdx] = (rec.start() < tEnd) && (rec.end() > tStart);
            }
        }

        return new TimelineModel(names, buckets, matrix);
    }

    /**
     * Calcola la matrice di adiacenza delle sovrapposizioni tra scenari.
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

                // Formula matematica di intersezione tra due segmenti
                matrix[i][j] = (recA.start() < recB.end()) && (recB.start() < recA.end());
            }
        }

        return new OverlapModel(names, matrix);
    }
}