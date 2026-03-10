package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.producer_keychains;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.*;
import it.pagopa.interop.producer_keychains.service.M2MV3ProducerKeychainsClient;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.producer_keychains.utils.ProducerKeychainsResolver;
import it.pagopa.pn.interop.cucumber.steps.producer_keychains.model.ProducerKeychainsContext;
import it.pagopa.pn.interop.cucumber.steps.selfcare.model.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
public class ProducerKeychainsSteps {
    private final M2MV3ProducerKeychainsClient producerKeychainsClient;
    private final IHttpExecutor httpCallExecutor;
    private final ProducerKeychainsResolver resolver;
    private final ProducerKeychainsContext producerKeychainsContext;
    private final TenantContext tenantContext;

    public ProducerKeychainsSteps(M2MV3ProducerKeychainsClient producerKeychainsClient, SharedStepsContext sharedStepsContext, ProducerKeychainsContext producerKeychainsContext, TenantContext tenantContext) {

        this.producerKeychainsClient = producerKeychainsClient;
        this.tenantContext = tenantContext;
        this.producerKeychainsContext = producerKeychainsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.producerKeychainsClient.setHttpCallExecutor(this.httpCallExecutor);
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
            httpCallExecutor.snapshot();

            PollingService.makePolling(
                    () -> {
                        final int limit = 50;
                        int offset = 0;
                        List<User> allUsers = new ArrayList<>();
                        Users usersPage;

                        do {
                            usersPage = producerKeychainsClient.getProducerKeychainUsers(
                                    producerKeychainValue,
                                    limit,
                                    offset
                            );

                            if (!httpCallExecutor.getResponseStatus().is2xxSuccessful() || usersPage == null) {
                                return null;
                            }

                            List<User> pageResults = usersPage.getResults();
                            if (pageResults.isEmpty()) {
                                break;
                            }

                            allUsers.addAll(pageResults);
                            offset += limit;

                        } while (usersPage.getResults().size() == limit);

                        Users aggregatedUsers = new Users();
                        aggregatedUsers.setResults(allUsers);
                        return aggregatedUsers;
                    },
                    users -> httpCallExecutor.getResponseStatus().is2xxSuccessful() && users != null && users.getResults().stream().anyMatch(user -> userIdValue.equals(user.getUserId())),
                    "L'utente non risulta associato alla producer keychain dopo la creazione",
                    5,
                    1_000L
            );

            httpCallExecutor.resetFormSnapshot();

        } catch (IllegalStateException e) {
            log.warn(httpCallExecutor.getErrorMessage());
        }
    }

    @And("viene invocata l'API di recupero utenze associate alla producer keychain {string} con limit {string} offset {string}")
    public void getProducerKeychainUsers(String producerKeychainId, String limit, String offset) {
        UUID producerKeychainValue = resolver.resolveKeychain(producerKeychainId);
        Integer limitValue = resolver.resolveInteger(limit);
        Integer offsetValue = resolver.resolveInteger(offset);

        try {

            Users response = producerKeychainsClient.getProducerKeychainUsers(producerKeychainValue, limitValue, offsetValue);
            tenantContext.setM2mUsers(response.getResults());
            Assertions.assertThat(response).as("La response contenente l'id del producer keychain creato non deve essere null").isNotNull();

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

            // Polling necessario per evitare l'errore di eventual consistency 404 Producer JWK not found
            ProducerKey key = PollingService.makePolling(
                    () -> {
                        try {
                            return producerKeychainsClient.createProducerKeychainKey(keychainId, keySeed);
                        } catch (IllegalStateException e){
                            log.warn(httpCallExecutor.getErrorMessage());
                            return null;
                        }
                    },
                    createdKey -> httpCallExecutor.getResponseStatus().is2xxSuccessful() && createdKey != null,
                    "",
                    5,
                    1_000);

            producerKeychainsContext.setProducerKey(key);
            producerKeychainsContext.setActualKeySeed(keySeed);

            httpCallExecutor.snapshot();
            String kid = key.getJwk().getKid();
            PollingService.makePolling(
                    () -> {
                        try {
                            return producerKeychainsClient.getProducerKey(kid);
                        } catch (IllegalStateException e) {
                            log.warn(httpCallExecutor.getErrorMessage());
                            return null;
                        }
                    },
                    createdKey -> httpCallExecutor.getResponseStatus().is2xxSuccessful() && createdKey != null && createdKey.getJwk().equals(key.getJwk()),
                    "La chiave non risulta creata correttamente dopo la creazione",
                    5,
                    1_000L);
            httpCallExecutor.resetFormSnapshot();

        }
        catch (PollingPredicateException | IllegalStateException e) {
            log.warn(httpCallExecutor.getErrorMessage());
        }
    }


    @When("si verifica che le utenze recuperate siano presenti nella lista di utenti appartenenti al tenant del chiamante")
    public void verifyUsersPresence() {
        List<User> m2mUsers = tenantContext.getM2mUsers();
        List<it.pagopa.interop.generated.openapi.clients.bff.model.User> selfcareUsers = tenantContext.getSelfcareUsers();

        Assertions.assertThat(m2mUsers).as("La lista utenti M2M non deve essere null").isNotNull();
        Assertions.assertThat(selfcareUsers).as("La lista utenti Selfcare non deve essere null").isNotNull();

        Set<UUID> m2mUserIds = m2mUsers.stream().map(User::getUserId).collect(Collectors.toSet());
        Set<UUID> selfcareUserIds = selfcareUsers.stream().map(it.pagopa.interop.generated.openapi.clients.bff.model.User::getUserId).collect(Collectors.toSet());

        Assertions.assertThat(selfcareUserIds).as("Gli userId restituiti da M2M devono essere contenuti in quelli di Selfcare").containsAll(m2mUserIds);
    }

    @When("l'utente elimina l'associazione tra l'utenza con userId {string} e la producer keychain {string}")
    public void deleteProducerKeychainUserAssociation(String userId, String producerKeychainId) {
        UUID userIdValue = resolver.resolveUserId(userId);
        UUID producerKeychainValue = resolver.resolveKeychain(producerKeychainId);

        try {

            producerKeychainsClient.deleteProducerKeychainUserAssociationById(producerKeychainValue, userIdValue);

            if (httpCallExecutor.getResponseStatus().is2xxSuccessful() && userIdValue != null && producerKeychainValue != null) {
                httpCallExecutor.snapshot();
                PollingService.makePolling(() -> producerKeychainsClient.getProducerKeychainUsers(producerKeychainValue, 50, 0), users -> httpCallExecutor.getResponseStatus().is2xxSuccessful() && users != null && users.getResults() != null && users.getResults().stream().noneMatch(user -> userIdValue.equals(user.getUserId())), "L'utente risulta ancora associato alla producer keychain dopo l'eliminazione", 30, 1_000L);
                httpCallExecutor.resetFormSnapshot();
            }


        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
        }
    }

    @And("viene recuperata la producer-key con kid {string}")
    public void getProducerKey(String rawKid) {
        String kid = resolver.resolveKid(rawKid);
        try {
            ProducerKey pKey = PollingService.makePolling(
                    () -> {
                        try {
                            return producerKeychainsClient.getProducerKey(kid);
                        } catch (IllegalStateException e) {
                            log.warn(httpCallExecutor.getErrorMessage());
                            return null;
                        }
                    },
                    res -> httpCallExecutor.getResponseStatus().is2xxSuccessful() && res != null,
                    "",
                    5,
                    1_000
            );
            producerKeychainsContext.setProducerKey(pKey);
        } catch (PollingPredicateException e) {
            log.warn(e.getMessage());
        }
    }

    @And("viene eliminata la producer-key con keychainId {string}, kid {string}")
    public void deleteProducerKey(String rawKeychainId, String rawKid) {
        String kid = resolver.resolveKid(rawKid);
        UUID keychainId = resolver.resolveKeychain(rawKeychainId);

        try {

            producerKeychainsClient.deleteProducerKeychainKeyByKid(keychainId, kid);

            if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
                httpCallExecutor.snapshot();
                PollingService.makePolling(() -> {
                    try {
                        producerKeychainsClient.getProducerKey(kid);
                    } catch (IllegalStateException e) {
                    }
                    return httpCallExecutor.getResponseStatus();
                }, status -> status == HttpStatus.NOT_FOUND, "La chiave risulta ancora presente dopo l'eliminazione", 30, 1_000L);
                httpCallExecutor.resetFormSnapshot();
            }


        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
        }
    }
}
