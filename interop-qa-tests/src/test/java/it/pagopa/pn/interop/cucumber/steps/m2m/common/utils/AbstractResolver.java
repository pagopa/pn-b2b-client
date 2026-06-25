package it.pagopa.pn.interop.cucumber.steps.m2m.common.utils;

import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.utility.enums.ResolvableToken;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractResolver {

    public final SharedStepsContext sharedStepsContext;

    public AbstractResolver(SharedStepsContext sharedStepsContext) {
        this.sharedStepsContext = sharedStepsContext;
    }

    public <T> T resolveOrParse(
            String raw,
            Function<String, T> nonTokenParser,
            Supplier<T> actualSupplier,
            Supplier<T> expectedSupplier,
            Supplier<T> randomSupplier,
            Supplier<T> blankSupplier
    ) {
        if (raw == null) return nonTokenParser.apply(null);

        ResolvableToken token = ResolvableToken.from(raw);
        if (token == null) {
            return nonTokenParser.apply(raw);
        }

        return switch (token) {
            case ACTUAL -> actualSupplier != null ? actualSupplier.get() : null;
            case EXPECTED -> expectedSupplier != null ? expectedSupplier.get() : null;
            case RANDOM -> randomSupplier != null ? randomSupplier.get() : null;
            case BLANK -> blankSupplier != null ? blankSupplier.get() : null;
            case NULL -> null;
            default -> throw new IllegalArgumentException("Unknown token: " + token);
        };
    }

    // overload comodo quando BLANK/RANDOM non servono
    public <T> T resolveOrParse(
            String raw,
            Function<String, T> nonTokenParser,
            Supplier<T> actualSupplier,
            Supplier<T> expectedSupplier
    ) {
        return resolveOrParse(raw, nonTokenParser, actualSupplier, expectedSupplier, null, null);
    }


    public List<UUID> resolveEserviceIds(String raw) {
        List<UUID> singletonEserviceId = sharedStepsContext.getEServicesCommonContext().getEserviceId() == null
                ? Collections.emptyList()
                : List.of(sharedStepsContext.getEServicesCommonContext().getEserviceId());

        return resolveOrParse(
                raw,
                (uuid) -> Collections.singletonList(UUID.fromString(uuid)),
                () -> singletonEserviceId,
                () -> singletonEserviceId,
                () -> Collections.singletonList(UUID.randomUUID()),
                Collections::emptyList
        );
    }
}
