package it.pagopa.pn.interop.cucumber.steps.probing.utils;

import it.pagopa.interop.generated.openapi.clients.probing.model.SearchEserviceContent;
import it.pagopa.interop.probing.service.impl.ProbingClient;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.probing.model.ProbingContext;
import it.pagopa.pn.interop.cucumber.utility.enums.ResolvableToken;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static it.pagopa.pn.interop.cucumber.utility.StepParser.intOrRandomOrNull;
import static it.pagopa.pn.interop.cucumber.utility.StepParser.longOrRandomOrNull;
import static it.pagopa.pn.interop.cucumber.utility.StepParser.uuidOrRandomOrNull;

@RequiredArgsConstructor
public class ProbingResolver {

    private final SharedStepsContext sharedStepsContext;
    private final ProbingClient probingClient;
    private final ProbingContext probingContext;

    public String getEserviceName() {
        return sharedStepsContext.getEServicesCommonContext().getName();
    }

    public UUID getEserviceId() {
        return sharedStepsContext.getEServicesCommonContext().getEserviceId();
    }

    public UUID getDescriptorId() {
        return sharedStepsContext.getEServicesCommonContext().getDescriptorId();
    }

    public Long getEserviceRecordId() {
        String name = getEserviceName();
        List<SearchEserviceContent> results = probingClient.findEserviceByName(name);

        if (results.size() != 1) {
            throw new IllegalStateException(
                    "Impossibile recuperare univocamente eserviceRecordId per eservice '" + name + "'. Trovati: " + results.size()
            );
        }
        return results.get(0).getEserviceRecordId();
    }

    private <T> T resolve(String raw, String fieldName, Supplier<T> actualSupplier, Supplier<T> randomSupplier, Supplier<T> blankSupplier, T currentValueForKeep) {
        ResolvableToken token = ResolvableToken.from(raw);
        if (token == null) {
            // non è un token -> lascia che lo interpretino i parser specifici (uuidOrRandomOrNull / intOrRandomOrNull / parse date ecc.)
            return null;
        }

        return switch (token) {
            case ACTUAL -> actualSupplier.get();
            case NULL -> null;
            case RANDOM -> randomSupplier.get();
            case KEEP -> currentValueForKeep;
            case BLANK -> blankSupplier.get();
        };
    }

    public UUID resolveEserviceId(String raw) {
        ResolvableToken token = ResolvableToken.from(raw);
        if (token == null) return uuidOrRandomOrNull(raw);
        return resolve(raw, "eserviceId", this::getEserviceId, () -> uuidOrRandomOrNull("RANDOM"), null, getEserviceId());
    }

    public UUID resolveVersionId(String raw) {
        ResolvableToken token = ResolvableToken.from(raw);
        if (token == null) return uuidOrRandomOrNull(raw);
        return resolve(raw, "versionId", this::getDescriptorId, () -> uuidOrRandomOrNull("RANDOM"), null, getDescriptorId());
    }

    public Long resolveEserviceRecordId(String raw) {
        ResolvableToken token = ResolvableToken.from(raw);
        if (token == null) return longOrRandomOrNull(raw);
        return resolve(raw, "eserviceRecordId", this::getEserviceRecordId, () -> longOrRandomOrNull("RANDOM"), null, getEserviceRecordId());
    }

    public Integer resolveFrequency(String raw) {
        ResolvableToken token = ResolvableToken.from(raw);
        if (token == null) return intOrRandomOrNull(raw);
        return resolve(raw, "frequency", probingContext::getActualFrequency, ProbingResolver::randomPositiveInt, null, probingContext.getActualFrequency());
    }

    private static int randomPositiveInt() {
        return 1 + (int) (Math.random() * Integer.MAX_VALUE);
    }

    public OffsetDateTime resolveDateToken(String raw, OffsetDateTime current) {
        if (raw == null) return null;

        String token = raw.trim();
        String lower = token.toLowerCase();

        if (lower.equals("keep")) return current;
        if (lower.equals("null")) return null;

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
}
