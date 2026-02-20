package it.pagopa.pn.interop.cucumber.steps.producer_keychains;

import io.cucumber.java.en.And;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
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
    private final IHttpExecutor httpCallExecutor;
    private final ProducerKeychainsContext producerKeychainsContext;
    private final SharedStepsContext sharedStepsContext;

    public BFFProducerKeychainsSteps(ProducerKeychainsClient producerKeychainsClient,
                                     SharedStepsContext sharedStepsContext,
                                     ProducerKeychainsContext producerKeychainsContext) {

        this.producerKeychainsContext = producerKeychainsContext;
        this.producerKeychainsClient = producerKeychainsClient;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.producerKeychainsClient.setHttpCallExecutor(this.httpCallExecutor);
    }

    @And("esiste un producer keychain con nome {string} e con descrizione {string}")
    public void createProducerKeychainWithoutAssociatedUsers(String name, String description) {

        int seed = sharedStepsContext.getTestSeed();

        String uniqueName = buildUniqueName(name, seed);
        String uniqueDescription = buildUniqueDescription(description, seed);

        ProducerKeychainSeed seedRequest = new ProducerKeychainSeed()
                .name(uniqueName)
                .description(uniqueDescription)
                .members(List.of());

        try {
            CreatedResource response = producerKeychainsClient.createProducerKeychain(seedRequest);
            Assertions.assertThat(response)
                    .as("La response contenente l'id del producer keychain creato non deve essere null")
                    .isNotNull();

            producerKeychainsContext.setProducerKeychainId(response.getId());

            if (httpCallExecutor.getResponseStatus().is2xxSuccessful() && response.getId() != null) {
                httpCallExecutor.snapshot();
                PollingService.makePolling(
                        () -> producerKeychainsClient.getProducerKeychain(response.getId()),
                        keychain -> httpCallExecutor.getResponseStatus().is2xxSuccessful() && keychain != null && response.getId().equals(keychain.getId()),
                        "Il producer keychain non risulta creato correttamente dopo la creazione",
                        30,
                        1_000L
                );
                httpCallExecutor.resetFormSnapshot();
            }
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
        }
    }

    private String buildUniqueName(String base, int seed) {
        String safeBase = (base == null || base.length() < 5) ? "pkname" : base;
        return safeBase + "-" + seed;
    }

    private String buildUniqueDescription(String base, int seed) {
        String safeBase = (base == null || base.length() < 10) ? "producer-keychain" : base;
        return safeBase + "-" + seed;
    }


}
