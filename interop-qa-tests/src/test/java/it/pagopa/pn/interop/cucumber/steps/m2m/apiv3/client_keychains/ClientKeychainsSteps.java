package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.client_keychains;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import it.pagopa.interop.agreement.service.IM2MV3ClientsClient;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.*;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.client_keychains.model.ClientKeychainsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.client_keychains.utils.ClientKeychainsResolver;
import it.pagopa.pn.interop.cucumber.steps.producer_keychains.model.ProducerKeychainsContext;
import it.pagopa.pn.interop.cucumber.steps.selfcare.model.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class ClientKeychainsSteps {
    private final IHttpExecutor httpCallExecutor;
    private final IM2MV3ClientsClient clientsApi;
    private final ClientKeychainsResolver resolver;
    private final ClientKeychainsContext clientKeychainsContext;

    public ClientKeychainsSteps(ClientTokenConfigurator clientTokenConfigurator, SharedStepsContext sharedStepsContext, ProducerKeychainsContext producerKeychainsContext, TenantContext tenantContext) {
        clientsApi = clientTokenConfigurator.getM2mV3ClientsClient();
        httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        clientsApi.setHttpCallExecutor(httpCallExecutor);
        clientKeychainsContext = new ClientKeychainsContext();
        resolver = new ClientKeychainsResolver(sharedStepsContext, clientKeychainsContext);
    }

    @And("l'utente crea una nuova chiave di tipo {string} all'interno del client-keychains con:")
    public void createKey(String keyType, DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();

        Map<String, String> seed = rows.get(0);
        UUID keychainId = resolver.resolveClientKeychain(seed.get("keychainId"));
        KeySeed keySeed = resolver.resolveKeySeed(
                keyType,
                seed.get("key"),
                seed.get("name"),
                seed.get("alg"),
                seed.get("use")
        );

        Key key;

        try {
            key = clientsApi.createClientKey(keychainId, keySeed);
        } catch (IllegalStateException e) {
            // qualsiasi errore -> esco lasciando lo status per il Then.
            // Attenzione: questa post potrebbe soffrire di eventual consistency e beccare 404 nonostante la creazione
            // se si ritenta la creazione dopo il 404 si va in 409
            log.warn(httpCallExecutor.getErrorMessage());
            return;
        }

        clientKeychainsContext.setKey(key);
        clientKeychainsContext.setActualKeySeed(keySeed);

        httpCallExecutor.snapshot();
        try {

            PollingService.makePolling(
                    () -> {
                        try {
                            int page = 0;
                            int limit = 50;
                            JWKs jwks = clientsApi.getClientKeys(keychainId, page, limit);
                            List<JWK> keys = jwks.getResults();
                            int totalKeys = jwks.getPagination().getTotalCount();

                            while(totalKeys != keys.size())
                                keys.addAll(clientsApi.getClientKeys(keychainId, ++page, limit).getResults());

                            return keys;
                        } catch (IllegalStateException e) {
                            HttpStatus status = httpCallExecutor.getResponseStatus();

                            if (HttpStatus.NOT_FOUND.equals(status)) {
                                log.warn("Get key retryable failure: {}", httpCallExecutor.getErrorMessage());
                                return null;
                            }

                            return null;
                        }
                    },
                    fetchedKey ->
                            httpCallExecutor.getResponseStatus() != null
                                    && httpCallExecutor.getResponseStatus().is2xxSuccessful()
                                    && fetchedKey != null
                                    && fetchedKey.stream().anyMatch(jwk -> jwk.equals(key.getJwk())),
                    "La chiave non risulta creata correttamente dopo la creazione",
                    5,
                    1_000L
            );
        } catch (PollingPredicateException e) {
            log.warn(httpCallExecutor.getErrorMessage());
        } finally {
            httpCallExecutor.resetFormSnapshot();
        }
    }

}
