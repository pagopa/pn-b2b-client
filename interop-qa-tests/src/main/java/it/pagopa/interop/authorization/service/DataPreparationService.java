package it.pagopa.interop.authorization.service;

import it.pagopa.interop.authorization.service.IAuthorizationClient.Users;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.common.SettableHttpCallExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.nonNull;

/* 28/05/2025 Introdotto molto tempo dopo il suo omonimo
* it.pagopa.pn.interop.cucumber.steps.DataPreparationService
* per la necessità di avere anche al di fuori dello scope di test funzioni già presenti in
* it.pagopa.pn.interop.cucumber.steps.DataPreparationService. */
@Slf4j
@Service("mainDataPreparationService")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DataPreparationService implements SettableHttpCallExecutor {
    private static final ClientSeed DEFAULT_CLIENT_SEED = new ClientSeed();
    @Setter private IAuthorizationClient authorizationClient;
    private final PollingService pollingService;
    @Setter private IHttpExecutor httpCallExecutor;

    static {
        DEFAULT_CLIENT_SEED.setName(String.format("client %d", ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE)));
        DEFAULT_CLIENT_SEED.setDescription("Descrizione client");
        DEFAULT_CLIENT_SEED.setMembers(List.of());
    }

    public DataPreparationService(
        IAuthorizationClient authorizationClient,
        PollingService pollingService,
        IHttpExecutor httpCallExecutor
    ) {
        this.authorizationClient = authorizationClient;
        this.httpCallExecutor = httpCallExecutor;
        this.pollingService = pollingService;
    }

    public UUID createClient(String clientKind, ClientSeed partialClientSeed) {
        ClientSeed mergedClientSeed = merge(DEFAULT_CLIENT_SEED, partialClientSeed);
        if ("CONSUMER".equals(clientKind)) {
            httpCallExecutor.performCall(() -> authorizationClient.createConsumerClient(mergedClientSeed));
        } else {
            httpCallExecutor.performCall(() -> authorizationClient.createApiClient(mergedClientSeed));
        }
        assertValidResponse();
        UUID clientId = ((CreatedResource) httpCallExecutor.getResponse()).getId();
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(() -> authorizationClient.getClient(clientId)),
                res -> res != HttpStatus.NOT_FOUND,
                "Failed to retrieve the client!"
        );
        return clientId;
    }

    public void addMemberToClient(UUID clientId, UUID userId) {
        tryAddMemberToClient(clientId, userId);
        assertValidResponse();
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(() -> authorizationClient.getClientUsers(clientId)),
                res -> Optional.ofNullable(httpCallExecutor.getResponse())
                        .map(obj -> (List<CompactUser>) obj)
                        .orElse(List.of())
                        .stream()
                        .anyMatch(user -> user.getUserId().equals(userId)),
                "Failed to retrieve the client users list!"
        );
    }

    public void tryAddMemberToClient(UUID clientId, UUID userId) {
        Users users = new Users().addUserId(userId);
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(() -> authorizationClient.addUsersToClient(clientId, users)),
                res -> !res.is5xxServerError(),
                "Failed to add a user to the client!"
        );
    }

    public String addPublicKeyToClient(UUID clientId, KeySeed keySeed) {
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                        () -> authorizationClient.createKeys(clientId, List.of(keySeed))),
                res -> res != HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to create a new key!"
        );
        assertValidResponse();
        AtomicReference<Optional<String>> keyFound = new AtomicReference<>(Optional.empty());

        pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                        () -> authorizationClient.getClientKeys(clientId, 0, 50, null)),
                res -> {
                    keyFound.set(((PublicKeys) httpCallExecutor.getResponse()).getKeys().stream()
                            .filter(ks -> ks.getName().equals(keySeed.getName()))
                            .map(PublicKey::getKeyId).findAny());
                    return keyFound.get().isPresent();
                },
                "There was an error while retrieving the client keys!"
        );
        return keyFound.get().isPresent() ? keyFound.get().get() : null;
    }


    public void editClientAdmin(UUID clientId, ClientAdminConfig adminConfig) {
        httpCallExecutor.performCall(
            () -> authorizationClient.editClientAdmin(
                clientId,
                adminConfig));
        assertValidResponse();
        pollingService.makePolling(
            () -> authorizationClient.getClient(clientId),
            client -> nonNull(client.getAdmin()) && client.getAdmin().getUserId().equals(adminConfig.getAdminId()),
            "L'amministratore del client non è stato modificato correttamente: adminId vuoto oppure difforme da quello indicato");
    }

    public void setAuthToken(String token) {
        authorizationClient.setBearerToken(token);
    }

    public ClientSeed merge(ClientSeed defaultClientSeed, ClientSeed partialClientSeed) {
        ClientSeed clientSeed = new ClientSeed();
        clientSeed.setMembers(useOrDefault(partialClientSeed.getMembers(), defaultClientSeed.getMembers()));
        clientSeed.setDescription(useOrDefault(partialClientSeed.getDescription(), defaultClientSeed.getDescription()));
        clientSeed.setName(useOrDefault(partialClientSeed.getName(), defaultClientSeed.getName()));
        return clientSeed;
    }

    private <T> T useOrDefault(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    private void assertValidResponse() {
        if (!httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            throw new RuntimeException("Invalid response %s: %s".formatted(
                httpCallExecutor.getResponseStatus(),
                httpCallExecutor.getResponse()
            ));
        }
    }
}
