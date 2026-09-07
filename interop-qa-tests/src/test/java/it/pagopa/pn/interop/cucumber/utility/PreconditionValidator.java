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

    /**
     * Rappresenta una precondizione composta da:
     * <ul>
     *   <li>una condizione booleana valutata lazy ({@link BooleanSupplier})</li>
     *   <li>un messaggio descrittivo da includere in caso di fallimento</li>
     * </ul>
     */
    public record Precondition(BooleanSupplier precondition, String errorMsg) {}

    /**
     * Valida una singola precondizione senza contesto esplicito.
     *
     * @param precondition precondizione da validare
     * @throws IllegalStateException se la precondizione non e soddisfatta
     */
    public static void checkPrecondition(Precondition precondition) {
        checkPreconditions(null, Collections.singletonList(precondition));
    }

    /**
     * Valida una singola precondizione con contesto esplicito, utile per
     * identificare rapidamente il punto del test in cui e avvenuto il fallimento.
     *
     * @param context etichetta del contesto (es. nome metodo/fase)
     * @param precondition precondizione da validare
     * @throws IllegalStateException se la precondizione non e soddisfatta
     */
    public static void checkPrecondition(String context, Precondition precondition) {
        checkPreconditions(context, Collections.singletonList(precondition));
    }

    /**
     * Valida una lista di precondizioni senza contesto esplicito.
     *
     * @param preconditions lista di precondizioni da validare
     * @throws IllegalStateException se almeno una precondizione non e soddisfatta
     */
    public static void checkPreconditions(List<Precondition> preconditions) {
        checkPreconditions(null, preconditions);
    }

    /**
     * Valida una lista di precondizioni e, in caso di fallimento, lancia
     * una {@link IllegalStateException} con il dettaglio completo delle violazioni.
     *
     * <p>Comportamento:</p>
     * <ul>
     *   <li>se la lista e nulla o vuota, non fa nulla</li>
     *   <li>raccoglie tutte le violazioni in un unico messaggio</li>
     *   <li>se la valutazione di una condizione genera eccezione, la segnala come violation</li>
     * </ul>
     *
     * @param context etichetta del contesto (opzionale)
     * @param preconditions lista di precondizioni da validare
     * @throws IllegalStateException se almeno una precondizione non e soddisfatta
     */
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

            String safeErrorMsg = normalizeErrorMessage(precondition.errorMsg());
            BooleanSupplier condition = precondition.precondition();
            if (condition == null) {
                violations.add(safeErrorMsg + " (missing boolean condition)");
                continue;
            }

            boolean satisfied;
            try {
                satisfied = condition.getAsBoolean();
            } catch (RuntimeException e) {
                violations.add(safeErrorMsg + " (evaluation error: " + e.getMessage() + ")");
                continue;
            }

            if (!satisfied) {
                violations.add(safeErrorMsg);
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

    private static String normalizeErrorMessage(String errorMsg) {
        return isNotBlank(errorMsg)
                ? errorMsg
                : "precondition failed (missing error message)";
    }
}

