package it.pagopa.pn.cucumber.steps.utilitySteps;

import io.cucumber.java.ParameterType;

/**
 * Classe pensata come contenitore per tutti i ParameterType del progetto.
 */
//TODO: al momento è presente un solo parameter, ma altri possibili candidati sono:
// i nomi delle PA, le versioni di webhook e notifica, le operazioni che si possono svolgere su un'apiKey (rotate, block, create...)
public class ParameterTypes {

    @ParameterType("abbia|non abbia")
    public boolean has(String value) {
        return value.equals("abbia");
    }
}
