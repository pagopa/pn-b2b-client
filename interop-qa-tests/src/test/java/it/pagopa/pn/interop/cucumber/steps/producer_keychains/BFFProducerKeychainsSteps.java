package it.pagopa.pn.interop.cucumber.steps.producer_keychains;

import io.cucumber.java.en.And;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychainSeed;
import it.pagopa.interop.producer_keychains.service.ProducerKeychainsClient;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.producer_keychains.model.ProducerKeychainsContext;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;

import java.util.List;

@Slf4j
public class BFFProducerKeychainsSteps {
    private final ProducerKeychainsClient producerKeychainsClient;
    private final ProducerKeychainsContext producerKeychainsContext;

    public BFFProducerKeychainsSteps(ProducerKeychainsClient producerKeychainsClient,
                                     SharedStepsContext sharedStepsContext,
                                     ProducerKeychainsContext producerKeychainsContext) {

        this.producerKeychainsContext = producerKeychainsContext;
        this.producerKeychainsClient = producerKeychainsClient;
        this.producerKeychainsClient.setHttpCallExecutor(sharedStepsContext.getHttpCallExecutor());
    }

    @And("esiste un producer keychain con nome {string} e con descrizione {string}")
    public void createProducerKeychainWithoutAssociatedUsers(String name, String description) {

        ProducerKeychainSeed seed = new ProducerKeychainSeed().name(name).description(description).members(List.of());
        try {
            CreatedResource response = producerKeychainsClient.createProducerKeychain(seed);
            Assertions.assertThat(response).as("La response contenente l'id del producer keychain creato non deve essere null").isNotNull();
            producerKeychainsContext.setProducerKeychainId(response.getId());
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
        }
    }
}
