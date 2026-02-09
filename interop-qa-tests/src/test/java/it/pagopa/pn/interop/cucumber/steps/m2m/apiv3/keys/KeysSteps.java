package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.keys;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.ProducerKey;
import it.pagopa.interop.keys.service.Impl.KeysClient;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;

@Slf4j
public class KeysSteps {
    private final KeysClient keysClient;
    private final IHttpExecutor httpCallExecutor;

    public KeysSteps(KeysClient keysClient, SharedStepsContext sharedStepsContext) {
        this.keysClient = keysClient;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.keysClient.setHttpCallExecutor(this.httpCallExecutor);
    }

    @When("l'utente tenta di ottenere la chiave producer con kid {string}")
    public void getProducerJwkByKid(String kid) {
        String requestedKid = kid;

        try {
            ProducerKey response = keysClient.getProducerJWKByKid(requestedKid);
            Assertions.assertThat(response).as("La response contenente i metadati anagrafici dell'e-service non deve essere null").isNotNull();
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
        }


    }


}
