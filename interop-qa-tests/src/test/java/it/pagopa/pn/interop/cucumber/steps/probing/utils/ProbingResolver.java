package it.pagopa.pn.interop.cucumber.steps.probing.utils;

import it.pagopa.interop.generated.openapi.clients.probing.model.EserviceStateBE;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.utils.AbstractResolver;
import it.pagopa.pn.interop.cucumber.steps.probing.model.ProbingContext;
import it.pagopa.pn.interop.cucumber.utility.StepParser;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.UUID;

@RequiredArgsConstructor
public class ProbingResolver extends AbstractResolver {

    private final ProbingContext probingContext;

    public UUID resolveEserviceId(String raw) {
        return resolveOrParse(
                raw,
                StepParser::uuidOrRandomOrNull,
                () -> probingContext.getActualEserviceRow().getEserviceId(),
                () -> probingContext.getExpectedEserviceRow().getEserviceId(),
                UUID::randomUUID,
                null
        );
    }

    public UUID resolveVersionId(String raw) {
        return resolveOrParse(
                raw,
                StepParser::uuidOrRandomOrNull,
                () -> probingContext.getActualEserviceRow().getVersionId(),
                () -> probingContext.getExpectedEserviceRow().getVersionId(),
                UUID::randomUUID,
                null
        );
    }

    public Long resolveEserviceRecordId(String raw) {
        return resolveOrParse(
                raw,
                StepParser::longOrRandomOrNull,
                this::getEserviceRecordId,
                this::getEserviceRecordId,
                () -> 1L + (long) (Math.random() * Long.MAX_VALUE),
                null
        );
    }

    public String resolveEserviceName(String raw) {
        return resolveOrParse(
                raw,
                v -> v, // non token: ritorna raw
                () -> probingContext.getActualEserviceRow().getEserviceName(),
                () -> probingContext.getExpectedEserviceRow().getEserviceName(),
                null,
                () -> ""
        );
    }

    public String resolveProducer(String raw) {
        return resolveOrParse(
                raw,
                v -> v,
                () -> probingContext.getActualEserviceRow().getProducerName(),
                () -> probingContext.getExpectedEserviceRow().getProducerName(),
                null,
                () -> ""
        );
    }

    public EserviceStateBE resolveEserviceStateBE(String raw) {
        return resolveOrParse(
                raw,
                v -> v == null ? null : EserviceStateBE.fromValue(v),
                () -> EserviceStateBE.fromValue(probingContext.getActualEserviceRow().getState()),
                () -> EserviceStateBE.fromValue(probingContext.getExpectedEserviceRow().getState()),
                null,
                null
        );
    }

    public Long getEserviceRecordId() {
        return probingContext.getActualEserviceRow().getId();
    }

    public Integer resolveFrequency(String raw) {
        if (raw == null) return null;

        // 1) delta (+N / -N)
        int delta = resolveIntegerDelta(raw);

        // 2) parte base (prima di + / -) -> se non c'è operatore resta tutta la stringa
        String basePart = raw;
        int plusIdx = raw.indexOf('+');
        int minusIdx = raw.indexOf('-', 1); // evita il "-" iniziale tipo "-1"
        int opIdx = plusIdx >= 0 ? plusIdx : minusIdx;
        if (opIdx >= 0) {
            basePart = raw.substring(0, opIdx).trim();
        }

        // 3) risolvi base: token -> actual/expected/random/null, altrimenti parse int
        Integer baseValue = resolveOrParse(
                basePart,
                StepParser::intOrRandomOrNull, // NON token
                () -> probingContext.getActualEserviceRow().getPollingFrequency(),
                () -> probingContext.getExpectedEserviceRow().getPollingFrequency(),
                ProbingResolver::randomPositiveInt,
                null
        );

        // 4) applica delta
        if (baseValue == null) return null;
        return baseValue + delta;
    }

    public Duration resolveSchedulerInterval(String row) {
        String normalizedRow = StepParser.normalize(row);
        return Duration.ofMinutes(normalizedRow == null ? ProbingContext.SCHEDULER_INTERVAL : Integer.parseInt(row));
    }

    private static int randomPositiveInt() {
        return 1 + (int) (Math.random() * Integer.MAX_VALUE);
    }

    public OffsetTime resolvePollingStartTime(String raw) {
        return resolveOrParse(
                raw,
                v -> {
                    OffsetDateTime dt = StepParser.dateTimeOrNull(v);
                    return dt == null ? null : ProbingUtils.italyToday(dt.toLocalTime());
                },
                probingContext::getActualStartTime,
                probingContext::getActualEndTime
        );
    }

    public OffsetTime resolvePollingEndTime(String raw) {
        return resolveOrParse(
                raw,
                v -> {
                    OffsetDateTime dt = StepParser.dateTimeOrNull(v);
                    return dt == null ? null : ProbingUtils.italyToday(dt.toLocalTime());
                },
                probingContext::getActualStartTime,
                probingContext::getActualEndTime
        );
    }


    private Integer resolveIntegerDelta(String raw) {
        if (raw == null) return 0;

        String token = raw.trim();

        int plusIdx = token.indexOf('+');
        int minusIdx = token.indexOf('-', 1);

        if (plusIdx > -1) {
            return Integer.parseInt(token.substring(plusIdx + 1).trim());
        }

        if (minusIdx > -1) {
            return -Integer.parseInt(token.substring(minusIdx + 1).trim());
        }

        return 0;
    }
}
