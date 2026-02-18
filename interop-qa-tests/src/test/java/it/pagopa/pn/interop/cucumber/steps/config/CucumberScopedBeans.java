package it.pagopa.pn.interop.cucumber.steps.config;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.pn.interop.cucumber.steps.producer_keychains.model.ProducerKeychainsContext;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
/**
 * Definisce e registra un bean con scope di scenario.
 * <p>
 * Questo metodo è usato per creare istanze delle classi di step che NON contengono
 * direttamente metodi glue (cioè metodi annotati con {@code @Given}, {@code @When},
 * {@code @Then}, {@code @And}, ecc.), ma che servono come supporto ad altri step (@link SharedStep).
 * <p>
 * I bean registrati qui vengono gestiti da Spring con ciclo di vita limitato allo
 * scenario Cucumber in esecuzione, grazie all'annotazione {@code @ScenarioScope}.
 *
 * @param sharedStepsContext oggetto condiviso tra gli step per mantenere lo stato
 * @param clientTokenConfigurator componente per configurare il token di autenticazione
 * @return una nuova istanza del bean gestita da Spring
 */
public class CucumberScopedBeans {

    /* 22/07/2025: non più necessario a partire da API v2 parte 2, rimuovere
    @Bean(name = "eserviceSteps")
    @ScenarioScope
    public EserviceSteps eserviceSteps( SharedStepsContext sharedStepsContext,
                                        ClientTokenConfigurator clientTokenConfigurator,
                                        BlobFileCreator blobFileCreator) {
        return new EserviceSteps(sharedStepsContext, clientTokenConfigurator, blobFileCreator);
    }*/

    @Bean
    @ScenarioScope
    public ProducerKeychainsContext producerKeychainsContext() {
        return new ProducerKeychainsContext();
    }
}
