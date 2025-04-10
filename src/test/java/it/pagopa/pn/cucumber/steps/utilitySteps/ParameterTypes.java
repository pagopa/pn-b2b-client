package it.pagopa.pn.cucumber.steps.utilitySteps;

import io.cucumber.java.ParameterType;

/**
 * Classe pensata come contenitore per tutti i ParameterType del progetto.
 * Si tratta quasi esclusivamente di opposti (abbia-non abbia, contiene-non contiene, etc) in modo da evitare
 * la scrittura di due metodi distinti per dei controlli per il quale basterebbe un singolo metodo con l'aggiunta
 * di un parametro booleano.
 */
//TODO: al momento sono presenti due soli parameter, ma altri possibili candidati sono:
// i nomi delle PA, le versioni di webhook e notifica, le operazioni che si possono svolgere su un'apiKey (rotate, block, create...)
public class ParameterTypes {

    @ParameterType("abbia|non abbia")
    public static boolean has(String value) {
        return value.equals("abbia");
    }

    @ParameterType("uguale|differente")
    public static boolean isUguale(String condition) {
        return condition.equalsIgnoreCase("uguale");
    }

    @ParameterType("è presente|non è presente")
    public static boolean isPresent(String condition) {
        return condition.equalsIgnoreCase("è presente");
    }

    @ParameterType("contiene|non contiene")
    public static boolean contains(String condition) {
        return condition.equalsIgnoreCase("contiene");
    }


}
