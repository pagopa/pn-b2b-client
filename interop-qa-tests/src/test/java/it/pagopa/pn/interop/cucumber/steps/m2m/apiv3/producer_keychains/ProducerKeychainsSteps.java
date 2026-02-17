package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.producer_keychains;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.KeySeed;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.LinkUser;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.ProducerKey;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.User;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Users;
import it.pagopa.interop.producer_keychains.service.M2MProducerKeychainsClient;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.producer_keychains.utils.ProducerKeychainsResolver;
import it.pagopa.pn.interop.cucumber.steps.producer_keychains.model.ProducerKeychainsContext;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Map;
import java.util.stream.Collectors;


import it.pagopa.pn.interop.cucumber.steps.selfcare.model.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;


@Slf4j
public class ProducerKeychainsSteps {
    private final M2MProducerKeychainsClient producerKeychainsClient;
    private final IHttpExecutor httpCallExecutor;
    private final ProducerKeychainsResolver resolver;
    private final ProducerKeychainsContext context;
    private final ProducerKeychainsContext producerKeychainsContext;
    private final TenantContext tenantContext;

    public ProducerKeychainsSteps(M2MProducerKeychainsClient producerKeychainsClient, SharedStepsContext sharedStepsContext, ProducerKeychainsContext producerKeychainsContext, TenantContext tenantContext) {

        this.producerKeychainsClient = producerKeychainsClient;
        this.tenantContext = tenantContext;
        this.producerKeychainsContext = producerKeychainsContext;
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

            PollingService.makePolling(() -> httpCallExecutor.performCall(() -> producerKeychainsClient.createProducerKeychainUserAssociation(producerKeychainValue, linkUser)), status -> status == HttpStatus.NO_CONTENT, "Errore durante la creazione dell'associazione utente-producer keychain", 30, 1_000L);

            if (httpCallExecutor.getResponseStatus().is2xxSuccessful() && userIdValue != null && producerKeychainValue != null) {
                PollingService.makePolling(() -> producerKeychainsClient.getProducerKeychainUsers(producerKeychainValue, 50, 0), users -> httpCallExecutor.getResponseStatus().is2xxSuccessful() && users != null && users.getResults() != null && users.getResults().stream().anyMatch(user -> userIdValue.equals(user.getUserId())), "L'utente non risulta associato alla producer keychain dopo la creazione", 30, 1_000L);
            }
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
        }
    }

    @And("viene invocata l'API di recupero utenze associate alla producer keychain {string} con limit {string} offset {string}")
    public void getProducerKeychainUsers(String producerKeychainId, String limit, String offset) {
        UUID producerKeychainValue = resolver.resolveKeychain(producerKeychainId);
        Integer limitValue = resolver.resolveInteger(limit);
        Integer offsetValue = resolver.resolveInteger(offset);

        try {

            Users response = producerKeychainsClient.getProducerKeychainUsers(producerKeychainValue, limitValue, offsetValue);
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

            ProducerKey key = PollingService.makePolling(() -> this.producerKeychainsClient.createProducerKeychainKey(keychainId, keySeed), createdKey -> createdKey != null, "Errore durante la creazione della chiave del producer keychain", 30, 1_000L);

            context.setProducerKey(key);

            if (httpCallExecutor.getResponseStatus().is2xxSuccessful() && key != null && key.getProducerKeychainId() != null) {
                String kid = String.valueOf(key.getProducerKeychainId());
                PollingService.makePolling(() -> producerKeychainsClient.getProducerKey(kid), createdKey -> httpCallExecutor.getResponseStatus().is2xxSuccessful() && createdKey != null && kid.equals(String.valueOf(createdKey.getProducerKeychainId())), "La chiave non risulta creata correttamente dopo la creazione", 30, 1_000L);
            }
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
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

            PollingService.makePolling(() -> httpCallExecutor.performCall(() -> producerKeychainsClient.deleteProducerKeychainUserAssociationById(producerKeychainValue, userIdValue)), status -> status == HttpStatus.NO_CONTENT, "Errore durante l'eliminazione dell'associazione utente-producer keychain", 30, 1_000L);

            if (httpCallExecutor.getResponseStatus().is2xxSuccessful() && userIdValue != null && producerKeychainValue != null) {
                PollingService.makePolling(() -> producerKeychainsClient.getProducerKeychainUsers(producerKeychainValue, 50, 0), users -> httpCallExecutor.getResponseStatus().is2xxSuccessful() && users != null && users.getResults() != null && users.getResults().stream().noneMatch(user -> userIdValue.equals(user.getUserId())), "L'utente risulta ancora associato alla producer keychain dopo l'eliminazione", 30, 1_000L);
            }
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

    @And("viene eliminata la producer-key con keychainId {string}, kid {string}")
    public void deleteProducerKey(String rawKid, String rawKeychainId) {
        String kid = resolver.resolveKid(rawKid);
        UUID keychainId = resolver.resolveKeychain(rawKeychainId);

        try {
            PollingService.makePolling(() -> httpCallExecutor.performCall(() -> producerKeychainsClient.deleteProducerKeychainKeyByKid(keychainId, kid)), status -> status == HttpStatus.NO_CONTENT, "Errore durante l'eliminazione della chiave del producer keychain", 30, 1_000L);

            if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
                PollingService.makePolling(() -> {
                    try {
                        producerKeychainsClient.getProducerKey(kid);
                    } catch (IllegalStateException e) {
                    }
                    return httpCallExecutor.getResponseStatus();
                }, status -> status == HttpStatus.NOT_FOUND, "La chiave risulta ancora presente dopo l'eliminazione", 30, 1_000L);
            }
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
        }
    }
}
