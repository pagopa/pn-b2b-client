package it.pagopa.pn.interop.cucumber.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public final class PreconditionValidator {

    private PreconditionValidator() {
        throw new AssertionError("Utility class");
    }

    public record Precondition(BooleanSupplier precondition, String errorMsg) {}

    public static void checkPrecondition(Precondition precondition) {
        checkPreconditions(null, Collections.singletonList(precondition));
    }

    public static void checkPrecondition(String context, Precondition precondition) {
        checkPreconditions(context, Collections.singletonList(precondition));
    }

    public static void checkPreconditions(List<Precondition> preconditions) {
        checkPreconditions(null, preconditions);
    }

    public static void checkPreconditions(String context, List<Precondition> preconditions) {
        if (preconditions == null || preconditions.isEmpty()) {
            return;
        }

        List<String> violations = new ArrayList<>();
        for (Precondition precondition : preconditions) {
            if (precondition == null) {
                violations.add("precondition definition must not be null");
                continue;
            }

            boolean satisfied;
            try {
                satisfied = precondition.precondition() != null && precondition.precondition().getAsBoolean();
            } catch (RuntimeException e) {
                violations.add(precondition.errorMsg() + " (evaluation error: " + e.getMessage() + ")");
                continue;
            }

            if (!satisfied) {
                violations.add(precondition.errorMsg());
            }
        }

        if (!violations.isEmpty()) {
            throw new IllegalStateException(buildPreconditionFailureMessage(context, violations));
        }
    }

    private static String buildPreconditionFailureMessage(String context, List<String> violations) {
        StringBuilder message = new StringBuilder();
        message.append(isNotBlank(context)
                ? context + " failed with "
                : "Precondition validation failed with ");

        message
                .append(violations.size())
                .append(" precondition(s):");

        for (int i = 0; i < violations.size(); i++) {
            message.append(System.lineSeparator())
                    .append(i + 1)
                    .append(") ")
                    .append(violations.get(i));
        }
        return message.toString();
    }
}

