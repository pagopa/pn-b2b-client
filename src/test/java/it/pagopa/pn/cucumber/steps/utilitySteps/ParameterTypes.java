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

    @ParameterType("esiste|esista|non esiste|non esista|")
    public static boolean exists(String value) {
        return !value.startsWith("non");
    }

    @ParameterType("abbia|non abbia")
    public static boolean has(String value) {
        return value.equals("abbia");
    }

    @ParameterType("è|non è")
    public static boolean is(String value) {
        return value.equals("è");
    }

    @ParameterType("sono|non sono")
    public static boolean are(String value) {
        return value.equals("sono");
    }

    @ParameterType("uguale|differente")
    public static boolean isTheSame(String value) {
        return value.equals("uguale");
    }

    @ParameterType("contiene|non contiene")
    public static boolean contains(String value) {
        return value.equals("contiene");
    }

    @ParameterType("stato|category")
    public static String streamEventType(String value) {
        return value.equals("stato") ? STREAM_EVENT_TYPE_STATUS : STREAM_EVENT_TYPE_TIMELINE;
    }

    @ParameterType("con|senza")
    public static boolean with(String value) {
        return value.equals("con");
    }

    @ParameterType("analogico|digitale")
    public static boolean isDigital(String value) {
        return value.equals("digitale");
    }

    @ParameterType("uguale|successivo|precedente")
    public static Boolean isSuccessivo(String value) {
        return value.equals("uguale") ? null : value.equals("successivo");
    }

    @ParameterType("pari|superiore|inferiore")
    public static Boolean isSuperiore(String value) {
        return value.equals("pari") ? null : value.equals("superiore");
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

    @ParameterType("corretto|malformato")
    public static boolean isValidQrCode(String value) {
        return value.equals("corretto");
    }

    @ParameterType("delegato|delegante")
    public static boolean isDelegate(String value) {
        return value.equals("delegato");
    }

    @ParameterType("può|non può")
    public static boolean canBe(String value) {
        return value.equals("può");
    }

    @ParameterType("pre|post")
    public static boolean isBefore(String value) {
        return value.equals("pre");
    }

    @ParameterType("baseCost|firstAnalogCost|secondAnalogCost|simpleRegisteredLetterCost")
    public static String deliveryNotificationCost(String value) {
        return value;
    }

    @ParameterType("dev|test|uat|hotfix")
    public static Environment environment(String value) {
        return Environment.valueOf(value.toUpperCase());
    }
}