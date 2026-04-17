package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.producer_keychains;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.*;
import it.pagopa.interop.producer_keychains.IM2MV3ProducerKeychainsClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.producer_keychains.utils.ProducerKeychainsResolver;
import it.pagopa.pn.interop.cucumber.steps.producer_keychains.model.ProducerKeychainsContext;
import it.pagopa.pn.interop.cucumber.steps.selfcare.model.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.SoftAssertions.assertSoftly;


@Slf4j
public class ProducerKeychainsSteps {
    private final IM2MV3ProducerKeychainsClient producerKeychainsClient;
    private final IHttpExecutor httpCallExecutor;
    private final ProducerKeychainsResolver resolver;
    private final ProducerKeychainsContext producerKeychainsContext;
    private final TenantContext tenantContext;

    public ProducerKeychainsSteps(ClientTokenConfigurator clientTokenConfigurator, SharedStepsContext sharedStepsContext, ProducerKeychainsContext producerKeychainsContext, TenantContext tenantContext) {

        this.producerKeychainsClient = clientTokenConfigurator.getM2mV3ProducerKeychainsClient();
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
                        try {

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

                        } catch (IllegalStateException e) {

                            HttpStatus status = httpCallExecutor.getResponseStatus();

                            if (status == null) {
                                return null;
                            }

                            // retry per inconsistenza temporanea
                            if (status == HttpStatus.NOT_FOUND || status.is5xxServerError()) {
                                log.warn("Retry getProducerKeychainUsers: {}", httpCallExecutor.getErrorMessage());
                                return null;
                            }

                            // sad path: lascia propagare
                            throw e;
                        }
                    },
                    users ->
                            httpCallExecutor.getResponseStatus() != null
                                    && httpCallExecutor.getResponseStatus().is2xxSuccessful()
                                    && users != null
                                    && users.getResults().stream()
                                    .anyMatch(user -> userIdValue.equals(user.getUserId())),
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

        Map<String, String> seed = rows.get(0);
        UUID keychainId = resolver.resolveKeychain(seed.get("keychainId"));
        KeySeed keySeed = resolver.resolveKeySeed(
                keyType,
                seed.get("key"),
                seed.get("name"),
                seed.get("alg"),
                seed.get("use")
        );

        ProducerKey key;

        try {
            key = producerKeychainsClient.createProducerKeychainKey(keychainId, keySeed);
        } catch (IllegalStateException e) {
            // qualsiasi errore -> esco lasciando lo status per il Then.
            // Attenzione: questa post potrebbe soffrire di eventual consistency e beccare 404 nonostante la creazione
            // se si ritenta la creazione dopo il 404 si va in 409
            log.warn(httpCallExecutor.getErrorMessage());
            return;
        }

        producerKeychainsContext.setProducerKey(key);
        producerKeychainsContext.setActualKeySeed(keySeed);

        httpCallExecutor.snapshot();
        try {
            String kid = key.getJwk().getKid();

            ProducerKey finalKey = key;
            PollingService.makePolling(
                    () -> {
                        try {
                            return producerKeychainsClient.getProducerKey(kid);
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
                                    && fetchedKey.getJwk().equals(finalKey.getJwk()),
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

    @When("l'utente tenta di creare un portachiavi erogatore per il tenant {string} con:")
    public void createProducerKeychains(String tenant, DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

        if (rows.isEmpty()) {
            throw new IllegalArgumentException("La DataTable è vuota");
        }

        Map<String, String> producerKeychainSeedMap = rows.get(0);

        final String resolvedName = resolver.resolveProducerKeychainName(producerKeychainSeedMap.get("name"));
        final String resolvedDescription = resolver.resolveDescription(producerKeychainSeedMap.get("description"));
        final List<UUID> resolvedMembers = resolver.resolveMembers(producerKeychainSeedMap.get("members"), tenant);

        ProducerKeychainSeed seed = new ProducerKeychainSeed();
        seed.setName(resolvedName);
        seed.setDescription(resolvedDescription);
        seed.setMembers(resolvedMembers);

        try {
            ProducerKeychain keychain = producerKeychainsClient.createProducerKeychain(seed);

            producerKeychainsContext.setExpectedName(resolvedName);
            producerKeychainsContext.setExpectedDescription(resolvedDescription);
            producerKeychainsContext.setExpectedMembers(resolvedMembers);

            producerKeychainsContext.setActualName(keychain.getName());
            producerKeychainsContext.setActualDescription(keychain.getDescription());
            producerKeychainsContext.setProducerKeychainId(keychain.getId());
        } catch (IllegalStateException e) {
            log.warn(httpCallExecutor.getErrorMessage());
        }
    }

    @And("l'oggetto ProducerKeychain restituito rispetta quanto atteso")
    public void assertCreatedKeychain() {
        // I membri del portachiavi non sono restituiti con l'oggetto ProducerKeychain e dovrà essere effettuata un ulteriore chiamata per la verifica
        assertSoftly(softly -> {
            softly.assertThat(producerKeychainsContext.getProducerKeychainId()).isNotNull();
            softly.assertThat(producerKeychainsContext.getActualName()).isEqualTo(producerKeychainsContext.getExpectedName());
            softly.assertThat(producerKeychainsContext.getActualDescription()).isEqualTo(producerKeychainsContext.getExpectedDescription());
        });
    }

    @Then("l'utente tenta l'eliminazione del portachiavi erogatore con id {string}")
    public void deleteProducerKeychain(String rawKeychainId) {
        final UUID resolvedKeychainId = resolver.resolveKeychain(rawKeychainId);

        try{
            producerKeychainsClient.deleteProducerKeychain(resolvedKeychainId);
            httpCallExecutor.snapshot();

            PollingService.makePolling(
                    () -> {
                        try{
                            producerKeychainsClient.getProducerKeychains(resolvedKeychainId);
                        } catch (IllegalStateException e){
                            log.warn(httpCallExecutor.getErrorMessage());
                        }
                        return httpCallExecutor.getResponseStatus();
                    },
                    res -> res.equals(HttpStatus.NOT_FOUND),
                    "Producer keychain non eliminato!",
                    5,
                    1000
            );

            httpCallExecutor.resetFormSnapshot();
        } catch (IllegalStateException e) {
            log.warn(httpCallExecutor.getErrorMessage());
        }
    }
}
