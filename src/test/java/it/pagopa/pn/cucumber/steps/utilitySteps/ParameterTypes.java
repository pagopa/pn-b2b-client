package it.pagopa.pn.cucumber.steps.utilitySteps;

import io.cucumber.java.ParameterType;

import java.time.temporal.ChronoUnit;

/**
 * Classe pensata come contenitore per tutti i ParameterType del progetto.
 * Si tratta quasi esclusivamente di opposti (abbia-non abbia, contiene-non contiene, etc) in modo da evitare
 * la scrittura di due metodi distinti per dei controlli per il quale basterebbe un singolo metodo con l'aggiunta
 * di un parametro booleano.
 */
//TODO: Altri possibili candidati sono:
// i nomi delle PA, le versioni di webhook e notifica, le operazioni che si possono svolgere su un'apiKey (rotate, block, create...)
public class ParameterTypes {

    @ParameterType("abbia|non abbia")
    public static boolean has(String value) {
        return value.equals("abbia");
    }

    @ParameterType("uguale|differente")
    public static boolean isUguale(String condition) {
        return condition.equals("uguale");
    }

    @ParameterType("è presente|non è presente")
    public static boolean isPresent(String condition) {
        return condition.equals("è presente");
    }

    @ParameterType("pari|superiore|inferiore")
    public static Boolean isSuperiore(String condition) {
        return condition.equals("pari") ? null : condition.equals("superiore");
    }

    @ParameterType("minuti|ore|giorni")
    public static ChronoUnit unitaTemporale(String value) {
        return switch (value) {
            case "minuti" -> ChronoUnit.MINUTES;
            case "ore" -> ChronoUnit.HOURS;
            case "giorni" -> ChronoUnit.DAYS;
            default ->
                    throw new IllegalArgumentException("Unità temporale non riconosciuta. Aggiungerla al ParameterType se necessario");
        };
    }

    @ParameterType("contiene|non contiene")
    public static boolean contains(String condition) {
        return condition.equals("contiene");
    }
}
