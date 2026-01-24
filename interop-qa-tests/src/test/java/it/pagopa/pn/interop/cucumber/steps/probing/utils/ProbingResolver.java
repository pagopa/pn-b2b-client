package it.pagopa.pn.interop.cucumber.steps.probing.utils;

import it.pagopa.interop.generated.openapi.clients.probing.model.SearchEserviceContent;
import it.pagopa.interop.probing.service.impl.ProbingClient;
import it.pagopa.pn.interop.cucumber.steps.probing.model.ProbingContext;
import it.pagopa.pn.interop.cucumber.utility.enums.ResolvableToken;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static it.pagopa.pn.interop.cucumber.utility.StepParser.*;

@RequiredArgsConstructor
public class ProbingResolver {

    private final ProbingClient probingClient;
    private final ProbingContext probingContext;

    public UUID resolveEserviceId(String raw) {
        ResolvableToken token = ResolvableToken.from(raw);
        if (token == null) return uuidOrRandomOrNull(raw);
        return resolve(raw, "eserviceId", () -> probingContext.getActualEserviceRow().getEserviceId(), () -> uuidOrRandomOrNull(ResolvableToken.RANDOM.value()), null, () -> probingContext.getExpectedEserviceRow().getEserviceId());
    }

    public UUID resolveVersionId(String raw) {
        ResolvableToken token = ResolvableToken.from(raw);
        if (token == null) return uuidOrRandomOrNull(raw);
        return resolve(raw, "versionId", () -> probingContext.getActualEserviceRow().getVersionId(), () -> uuidOrRandomOrNull(ResolvableToken.RANDOM.value()), null, () -> probingContext.getExpectedEserviceRow().getVersionId());
    }

    public Long resolveEserviceRecordId(String raw) {
        ResolvableToken token = ResolvableToken.from(raw);
        if (token == null) return longOrRandomOrNull(raw);
        return resolve(raw, "eserviceRecordId", this::getEserviceRecordId, () -> longOrRandomOrNull(ResolvableToken.RANDOM.value()), null, this::getEserviceRecordId);
    }

    public String resolveEserviceName(String raw) {
        ResolvableToken token = ResolvableToken.from(raw);
        if (token == null) return raw;
        return resolve(raw, "eserviceName", () -> probingContext.getActualEserviceRow().getEserviceName(), null, () -> "", () -> probingContext.getExpectedEserviceRow().getEserviceName());
    }

    public String resolveProducer(String raw) {
        ResolvableToken token = ResolvableToken.from(raw);
        if (token == null) return raw;
        return resolve(raw, "producer", () -> probingContext.getActualEserviceRow().getProducerName(), null, () -> "", () -> probingContext.getExpectedEserviceRow().getProducerName());
    }

    public Long getEserviceRecordId() {
        String name = probingContext.getActualEserviceRow().getEserviceName();
        List<SearchEserviceContent> results = probingClient.findEserviceByName(name);

        if (results.size() != 1) {
            throw new IllegalStateException(
                    "Impossibile recuperare univocamente eserviceRecordId per eservice '" + name + "'. Trovati: " + results.size()
            );
        }
        return results.get(0).getEserviceRecordId();
    }

    public Integer resolveFrequency(String raw) {
        if (raw == null) return null;

        // 1) calcola il delta (+N / -N)
        int delta = resolveIntegerDelta(raw);

        // 2) estrai la parte base (prima di + / -)
        String basePart = raw;
        int plusIdx = raw.indexOf('+');
        int minusIdx = raw.indexOf('-', 1);
        int opIdx = plusIdx >= 0 ? plusIdx : minusIdx;
        if (opIdx >= 0) {
            basePart = raw.substring(0, opIdx).trim();
        }

        // 3) risolvi il valore base (token o valore semplice)
        ResolvableToken token = ResolvableToken.from(basePart);

        Integer baseValue;
        if (token == null) {
            baseValue = intOrRandomOrNull(basePart);
        } else {
            baseValue = resolve(
                    basePart,
                    "frequency",
                    () -> probingContext.getActualEserviceRow().getPollingFrequency(),
                    ProbingResolver::randomPositiveInt,
                    null,
                    () -> probingContext.getExpectedEserviceRow().getPollingFrequency()
            );
        }

        // 4) applica il delta
        if (baseValue == null) return null;
        return baseValue + delta;
    }

    private <T> T resolve(String raw, String fieldName, Supplier<T> actualSupplier, Supplier<T> randomSupplier, Supplier<T> blankSupplier, Supplier<T> expectedSupplier) {
        ResolvableToken token = ResolvableToken.from(raw);
        if (token == null) {
            // non è un token -> lascia che lo interpretino i parser specifici (uuidOrRandomOrNull / intOrRandomOrNull / parse date ecc.)
            return null;
        }

        return switch (token) {
            case ACTUAL -> actualSupplier.get();
            case NULL -> null;
            case RANDOM -> randomSupplier.get();
            case EXPECTED -> expectedSupplier.get();
            case BLANK -> blankSupplier.get();
        };
    }

    private static int randomPositiveInt() {
        return 1 + (int) (Math.random() * Integer.MAX_VALUE);
    }

    public OffsetDateTime resolveDateToken(String raw) {
        if (raw == null) return null;

        String token = raw.trim();
        String lower = token.toLowerCase();

        OffsetDateTime now = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        if (lower.equals("now")) return now;

        if (lower.startsWith("now+") && lower.endsWith("h")) {
            long hours = Long.parseLong(lower.substring(4, lower.length() - 1));
            return now.plusHours(hours);
        }
        if (lower.startsWith("now-") && lower.endsWith("h")) {
            long hours = Long.parseLong(lower.substring(4, lower.length() - 1));
            return now.minusHours(hours);
        }

        return OffsetDateTime.parse(token);
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
