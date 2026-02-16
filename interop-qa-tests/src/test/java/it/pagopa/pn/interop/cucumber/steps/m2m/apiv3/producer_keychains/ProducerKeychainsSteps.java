package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.producer_keychains;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.KeySeed;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.LinkUser;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.ProducerKey;
import it.pagopa.interop.producer_keychains.service.M2MProducerKeychainsClient;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.producer_keychains.utils.ProducerKeychainsResolver;
import it.pagopa.pn.interop.cucumber.steps.producer_keychains.model.ProducerKeychainsContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class ProducerKeychainsSteps {
    private final M2MProducerKeychainsClient producerKeychainsClient;
    private final IHttpExecutor httpCallExecutor;
    private final ProducerKeychainsResolver resolver;
    private final ProducerKeychainsContext context;

    public ProducerKeychainsSteps(M2MProducerKeychainsClient producerKeychainsClient, SharedStepsContext sharedStepsContext, ProducerKeychainsContext producerKeychainsContext) {
        this.producerKeychainsClient = producerKeychainsClient;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.producerKeychainsClient.setHttpCallExecutor(this.httpCallExecutor);
        this.context = producerKeychainsContext;
        this.resolver = new ProducerKeychainsResolver(producerKeychainsContext, sharedStepsContext);

    }

    @And("viene associato l'utente {string} alla producer keychain {string}")
    public void createProducerKeychainUserAssociation(String userId, String producerKeychainId) {
        UUID userIdValue = resolver.resolveUserId(userId);
        UUID producerKeychainValue = resolver.resolveKeychain(producerKeychainId);

        try {
            LinkUser linkUser = new LinkUser();
            if (userIdValue != null) {
                linkUser.setUserId(userIdValue);
            }

            producerKeychainsClient.createProducerKeychainUserAssociation(producerKeychainValue, linkUser);
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
        }
    }

    @And("l'utente crea una nuova chiave di tipo {string} all'interno del producer-keychains con:")
    public void createKey(String keyType, DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();

        try {
            Map<String, String> seed = rows.get(0);
            UUID keychainId = resolver.resolveKeychain(seed.get("keychainId"));
            KeySeed keySeed = resolver.resolveKeySeed(keyType, seed.get("key"), seed.get("name"), seed.get("alg"), seed.get("use"));

            ProducerKey key = this.producerKeychainsClient.createProducerKeychainKey(keychainId, keySeed);
            context.setProducerKey(key);
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
        }
    }

    @And("viene recuperata la producer-key con kid {string}")
    public void getProducerKey(String rawKid) {
        String kid = resolver.resolveKid(rawKid);

        try {
            ProducerKey pKey = producerKeychainsClient.getProducerKey(kid);
            context.setProducerKey(pKey);
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
        }
    }
}
