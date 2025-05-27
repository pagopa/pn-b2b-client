package it.pagopa.pn.cucumber.steps.utilitySteps;

import io.cucumber.java.ParameterType;

import java.time.temporal.ChronoUnit;

import static it.pagopa.pn.cucumber.steps.utilitySteps.Costanti.*;

/**
 * Classe pensata come contenitore per tutti i ParameterType del progetto.
 * Si tratta quasi esclusivamente di opposti (abbia-non abbia, contiene-non contiene, etc) in modo da evitare
 * la scrittura di due metodi distinti per dei controlli per il quale basterebbe un singolo metodo con l'aggiunta
 * di un parametro booleano.
 */
//TODO: altri possibili candidati sono: i nomi delle PA, le versioni di webhook e notifica, le operazioni che si possono svolgere su un'apiKey (rotate, block, create...)
public class ParameterTypes {

    @ParameterType("esista|non esista")
    public static boolean exists(String condition) {
        return condition.equals("esista");
    }

    @ParameterType("abbia|non abbia")
    public static boolean has(String value) {
        return value.equals("abbia");
    }

    @ParameterType("è|non è")
    public static boolean is(String value) {
        return value.equals("è");
    }

    @ParameterType("uguale|differente")
    public static boolean isUguale(String condition) {
        return condition.equals("uguale");
    }

    @ParameterType("contiene|non contiene")
    public static boolean contains(String condition) {
        return condition.equals("contiene");
    }

    @ParameterType("uguale|successivo|precedente")
    public static Boolean isSuccessivo(String condition) {
        return condition.equals("uguale") ? null : condition.equals("successivo");
    }

    @ParameterType("pari|superiore|inferiore")
    public static Boolean isSuperiore(String condition) {
        return condition.equals("pari") ? null : condition.equals("superiore");
    }

    @ParameterType("giorni|ore|minuti|secondi")
    public static ChronoUnit unitaTemporale(String value) {
        return switch (value) {
            case "giorni" -> ChronoUnit.DAYS;
            case "ore" -> ChronoUnit.HOURS;
            case "minuti" -> ChronoUnit.MINUTES;
            case "secondi" -> ChronoUnit.SECONDS;
            default ->
                    throw new IllegalArgumentException("unità temporale non riconosciuta. Valutare se inserirla nei ParameterTypes");
        };
    }

    @ParameterType(COMUNE_1 + "|" + COMUNE_2 + "|" + COMUNE_MULTI + "|" + COMUNE_SON + "|" + COMUNE_ROOT)
    public static String paName(String paName) {
        return paName;
    }


}