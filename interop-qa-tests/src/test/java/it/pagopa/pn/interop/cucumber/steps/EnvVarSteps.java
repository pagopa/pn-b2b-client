package it.pagopa.pn.interop.cucumber.steps;

import io.cucumber.java.en.Then;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/* FIXME 14/05/2025: non si tratta di una vera e propria classe di steps, quanto un semplice
 *   strumento di diagnostica per cercare di risolvere il problema che sta impedendo il completamento
 *   delle pipeline su GitHub. E' temporaneo, rimuovere una volta risolto. */

@Slf4j
public class EnvVarSteps {
    @Then("stampa tutte le variabili di ambiente")
    public void envar() {
        System.out.println();
        System.out.println("Stampa tutte le variabili di ambiente: ");
        Map<String, String> env = System.getenv();
        for (String envName : env.keySet()) {
            printKV(envName, env.get(envName));
        }
    }

    @Then("stampa tutte le system properties")
    public void sysprop() {
        System.out.println();
        System.out.println("Stampa tutte le system properties: ");
        System.getProperties().forEach(EnvVarSteps::printKV);
    }

    private static void printKV(Object key, Object value) {
        System.out.format("%s=%s%n", key, value);
    }

}
