package it.pagopa.pn.interop.cucumber.steps.utils;

import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.service.IAuthorizationClient;
import it.pagopa.interop.service.utils.CommonUtils;
import it.pagopa.interop.service.utils.KeyPairGeneratorUtil;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

public class DataPreparationService {
    private static final ClientSeed DEFAULT_CLIENT_SEED = new ClientSeed();
    private IAuthorizationClient authorizationClient;
    private CommonUtils commonUtils;
    private HttpCallExecutor httpCallExecutor;

    static {
        DEFAULT_CLIENT_SEED.setName(String.format("client %d", ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE)));
        DEFAULT_CLIENT_SEED.setDescription("Descrizione client");
        DEFAULT_CLIENT_SEED.setMembers(List.of());
    }

    public DataPreparationService(IAuthorizationClient authorizationClient, HttpCallExecutor httpCallExecutor, CommonUtils commonUtils) {
        this.authorizationClient = authorizationClient;
        this.httpCallExecutor = httpCallExecutor;
        this.commonUtils = commonUtils;
    }

    public UUID createClient(String clientKind, UUID clientId, ClientSeed partialClientSeed) {
        ClientSeed mergedClientSeed = merge(DEFAULT_CLIENT_SEED, partialClientSeed);
        if ((clientKind == "CONSUMER")) {
            httpCallExecutor.performCall(() -> authorizationClient.createConsumerClient("", mergedClientSeed));
        } else {
            httpCallExecutor.performCall(() -> authorizationClient.createApiClient("", mergedClientSeed));
        }
        assertValidResponse();
        commonUtils.makePolling(
                () -> httpCallExecutor.performCall(() -> authorizationClient.getClient("", clientId)),
                res -> res != HttpStatus.NOT_FOUND,
                "Failed to retrieve the client!"
        );
        return clientId;
    }

    public void addMemberToClient(UUID clientId, UUID userId) {
        InlineObject2 inlineObject = new InlineObject2().addUserIdsItem(userId);
        commonUtils.makePolling(
                () -> httpCallExecutor.performCall(() -> authorizationClient.addUsersToClient("", clientId, inlineObject)),
                res -> !res.is5xxServerError(),
                "Failed to add a user to the client!"
        );
        assertValidResponse();
        commonUtils.makePolling(
                () -> httpCallExecutor.performCall(() -> authorizationClient.getClientUsers("", clientId)),
                res -> ((List<CompactUser>) httpCallExecutor.getResponse()).stream().anyMatch(user -> user.getUserId() == userId),
                "Failed to retrieve the client users list!"
        );
    }

    public void addPurposeToClient(UUID clientId, UUID purposeId) {
        PurposeAdditionDetailsSeed purposeAdditionDetailsSeed = new PurposeAdditionDetailsSeed().purposeId(purposeId);
        httpCallExecutor.performCall(() -> authorizationClient.addClientPurpose("", clientId, purposeAdditionDetailsSeed));
        assertValidResponse();

        commonUtils.makePolling(
                () -> authorizationClient.getClient("", clientId),
                res -> ((Client) httpCallExecutor.getResponse()).getPurposes().stream().anyMatch(purp -> purp.getPurposeId() == purposeId),
                "Failed to add a purpose to the client!"
        );
    }
    public String addPublicKeyToClient(UUID clientId, KeySeed keySeed) {
        commonUtils.makePolling(
                () -> httpCallExecutor.performCall(
                        () -> authorizationClient.createKeys("", clientId, KeyPairGeneratorUtil.createKeySeed(KeyUse.SIG, "RS256", KeyPairGeneratorUtil.createBase64PublicKey("RSA", 2048)))),
                res -> res != HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to create a new key!"
        );
        assertValidResponse();
        AtomicReference<Optional<String>> keyFound = new AtomicReference<>(Optional.empty());

        commonUtils.makePolling(
                () -> httpCallExecutor.performCall(
                        () -> authorizationClient.getClientKeys("", clientId, null)),
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

    private void assertValidResponse() {
        Assertions.assertFalse(httpCallExecutor.getClientResponse().isError(),
                "Something went wrong: " + httpCallExecutor.getClientResponse().getReasonPhrase());
    }

    private ClientSeed merge(ClientSeed defaultClientSeed, ClientSeed partialClientSeed) {
        ClientSeed clientSeed = new ClientSeed();
        clientSeed.setMembers(useOrDefault(partialClientSeed.getMembers(), defaultClientSeed.getMembers()));
        clientSeed.setDescription(useOrDefault(partialClientSeed.getDescription(), defaultClientSeed.getDescription()));
        clientSeed.setName(useOrDefault(partialClientSeed.getName(), defaultClientSeed.getName()));
        return clientSeed;
    }

    private <T> T useOrDefault(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }
}
