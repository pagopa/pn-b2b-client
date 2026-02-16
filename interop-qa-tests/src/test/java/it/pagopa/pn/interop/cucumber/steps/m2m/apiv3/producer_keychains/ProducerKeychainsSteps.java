package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.producer_keychains;

import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.LinkUser;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.User;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Users;
import it.pagopa.interop.producer_keychains.service.M2MProducerKeychainsClient;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.producer_keychains.model.ProducerKeychainsContext;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import it.pagopa.pn.interop.cucumber.steps.selfcare.model.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;

@Slf4j
public class ProducerKeychainsSteps {
    private final M2MProducerKeychainsClient producerKeychainsClient;
    private final IHttpExecutor httpCallExecutor;
    private final SharedStepsContext sharedStepsContext;
    private final ProducerKeychainsContext producerKeychainsContext;
    private final TenantContext tenantContext;

    public ProducerKeychainsSteps(M2MProducerKeychainsClient producerKeychainsClient, SharedStepsContext sharedStepsContext, ProducerKeychainsContext producerKeychainsContext, TenantContext tenantContext) {

        this.producerKeychainsClient = producerKeychainsClient;
        this.sharedStepsContext = sharedStepsContext;
        this.tenantContext = tenantContext;
        this.producerKeychainsContext = producerKeychainsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.producerKeychainsClient.setHttpCallExecutor(this.httpCallExecutor);

    }

    @And("l'utente associa l'utenza con userId {string} alla producer keychain {string}")
    @And("esiste un utente con id {string} associato alla keychain creata {string}")
    public void createProducerKeychainUserAssociation(String userId, String producerKeychainId) {
        UUID userIdValue = parseNullableUuid(userId);
        UUID producerKeychainValue = resolveProducerKeychainId(producerKeychainId);

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

    @And("viene invocata l'API di recupero utenze associate alla producer keychain {string} con limit {string} offset {string}")
    public void getProducerKeychainUsers(String producerKeychainId, String limit, String offset) {
        UUID producerKeychainValue = resolveProducerKeychainId(producerKeychainId);
        Integer limitValue = parseNullableInteger(limit);
        Integer offsetValue = parseNullableInteger(offset);

        try {

            Users response = producerKeychainsClient.getProducerKeychainUsers(producerKeychainValue, limitValue, offsetValue);
            Assertions.assertThat(response).as("La response contenente l'id del producer keychain creato non deve essere null").isNotNull();

        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
        }
    }

    @When("si verifica che le utenze recuperate siano presenti nella lista di utenti appartenenti al tenant del chiamante")
    public void verifyUsersPresence() {
        List<User> m2mUsers = tenantContext.getM2mUsers();
        List<it.pagopa.interop.generated.openapi.clients.bff.model.User> selfcareUsers = tenantContext.getSelfcareUsers();

        Assertions.assertThat(m2mUsers)
                .as("La lista utenti M2M non deve essere null")
                .isNotNull();
        Assertions.assertThat(selfcareUsers)
                .as("La lista utenti Selfcare non deve essere null")
                .isNotNull();

        Set<UUID> m2mUserIds = m2mUsers.stream()
                .map(User::getUserId)
                .collect(Collectors.toSet());
        Set<UUID> selfcareUserIds = selfcareUsers.stream()
                .map(it.pagopa.interop.generated.openapi.clients.bff.model.User::getUserId)
                .collect(Collectors.toSet());

        Assertions.assertThat(selfcareUserIds)
            .as("Gli userId restituiti da M2M devono essere contenuti in quelli di Selfcare")
            .containsAll(m2mUserIds);
    }

    private UUID parseNullableUuid(String value) {
        if (value == null || "null".equalsIgnoreCase(value)) {
            return null;
        }
        return UUID.fromString(value);
    }

    private UUID resolveProducerKeychainId(String value) {
        if (value == null || "null".equalsIgnoreCase(value)) {
            return null;
        }
        if ("PKCreata".equalsIgnoreCase(value)) {
            return producerKeychainsContext.getProducerKeychainId();
        }
        if ("PKCNonEsistente".equalsIgnoreCase(value)) {
            return UUID.randomUUID();
        }
        return null;
    }

    private Integer parseNullableInteger(String value) {
        if (value == null || "null".equalsIgnoreCase(value)) {
            return null;
        }
        return Integer.valueOf(value);
    }

}
