package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.producer_keychains;

import io.cucumber.java.en.And;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.LinkUser;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Users;
import it.pagopa.interop.producer_keychains.service.M2MProducerKeychainsClient;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.producer_keychains.model.ProducerKeychainsContext;

import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;

@Slf4j
public class ProducerKeychainsSteps {
    private final M2MProducerKeychainsClient producerKeychainsClient;
    private final IHttpExecutor httpCallExecutor;
    private final SharedStepsContext sharedStepsContext;
    private final ProducerKeychainsContext producerKeychainsContext;

    public ProducerKeychainsSteps(M2MProducerKeychainsClient producerKeychainsClient, SharedStepsContext sharedStepsContext, ProducerKeychainsContext producerKeychainsContext) {

        this.producerKeychainsClient = producerKeychainsClient;
        this.sharedStepsContext = sharedStepsContext;
        this.producerKeychainsContext = producerKeychainsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.producerKeychainsClient.setHttpCallExecutor(this.httpCallExecutor);

    }

    @And("l'utente associa l'utenza con userId {string} alla producer keychain {string}")
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

    @And("l'utente viene invocata l'API di recupero utenze associate alla producer keychain {string} con limit {string} offset {string}")
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
