package it.pagopa.interop.producerkeychain;

import it.pagopa.interop.common.client.AbstractClient;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.common.operation.SimpleOperation;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.ProducerKeychainApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.notification.cache.NotificationCache;
import it.pagopa.interop.utils.HttpCallExecutor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ToString
@EqualsAndHashCode(callSuper = true)
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProducerKeychainClientImpl extends AbstractClient implements ProducerKeychainClient {

    private final ProducerKeychainApi producerKeychainApi;
    private final RestTemplate restTemplate;
    private final String basePath;
    private final NotificationCache cache;

    public ProducerKeychainClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs, NotificationCache cache, HttpCallExecutor httpCallExecutor) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getBaseUrl();
        this.producerKeychainApi = new ProducerKeychainApi(createApiClient("dummyBearer"));
        this.cache = cache;
        super.httpCallExecutor = httpCallExecutor;
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.producerKeychainApi.setApiClient(createApiClient(bearerToken));
    }

    @Override
    public ProducerKeychain get(UUID id) {
        return performOperation(() -> this.producerKeychainApi.getProducerKeychainWithHttpInfo(id)).orElseThrow(() -> new RuntimeException("Risorsa non trovata"));
    }

    @Override
    public List<ProducerKeychain> getAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ProducerKeychain> getPage(int page, int size) { throw new UnsupportedOperationException(); }

    @Override
    public UUID getId(ProducerKeychain entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UUID create(ProducerKeychainSeed seed) {
        Optional<CreatedResource> createdResource = performOperation(
            () -> producerKeychainApi.createProducerKeychainWithHttpInfo(seed));
        return createdResource.orElseThrow(() -> new RuntimeException("Il portachiavi erogatore non è stato generato correttamente")).getId();
    }

    @Override
    public CreatedResource createProducerKeychain(ProducerKeychainSeed producerKeychainSeed) {
        return performOperation(SimpleOperation.of(() -> producerKeychainApi.createProducerKeychain(producerKeychainSeed), res -> res)).orElseThrow(() -> new IllegalStateException("Errore nella creazione del producer keychain (response non 2xx o body nullo)"));
    }

    @Override
    public ProducerKeychain getProducerKeychain(UUID producerKeychainId) {
        return performOperation(SimpleOperation.of(() -> producerKeychainApi.getProducerKeychain(producerKeychainId), res -> res)).orElseThrow(() -> new IllegalStateException("Errore nel recupero del producer keychain (response non 2xx o body nullo)"));
    }

    @Override
    public void createProducerKeychainKey(UUID producerKeychainId, KeySeed keySeed) {
        performOperation(() -> producerKeychainApi.createProducerKeyWithHttpInfo(producerKeychainId, keySeed));
    }

    @Override
    public void linkEService(UUID producerKeychainId, EServiceAdditionDetailsSeed seed) {
        performOperation(() -> producerKeychainApi.addProducerKeychainEServiceWithHttpInfo(producerKeychainId, seed));
    }

    @Override
    public void createProducerKey(UUID keychainId, KeySeed keySeed) {
        performOperation(() -> producerKeychainApi.createProducerKeyWithHttpInfo(keychainId, keySeed));
    }

    @Override
    public List<String> getProducerKeysIds(UUID keychainId) {
        Optional<PublicKeys> publicKeys = performOperation(
            () -> producerKeychainApi.getProducerKeysWithHttpInfo(keychainId, 0, 30, null));
        if(publicKeys.isPresent()) {
            return publicKeys.get().getKeys().stream().map(PublicKey::getKeyId).toList();
        } else {
            throw new RuntimeException("Risorsa non trovata");
        }
    }

    @Override
    public void deleteProducerKey(UUID keychainId, String keyId) {
        performOperation(() -> producerKeychainApi.deleteProducerKeyByIdWithHttpInfo(keychainId, keyId));
    }

    @Override
    public void removeUserFromKeychain(UUID keychainId, UUID userId) {
        performOperation(() -> producerKeychainApi.removeProducerKeychainUserWithHttpInfo(keychainId, userId));
    }

    @Override
    public void addUserToProducerKeychain(UUID keychainId, UUID userId) {
        performOperation(() -> producerKeychainApi.addProducerKeychainUsersWithHttpInfo(keychainId, new AddUsersToClientRequest().addUserIdsItem(userId)));
    }

    @Override
    public UUID generateId(EntityIdType entityIdType) {
        return switch (entityIdType){
            case INVALID_ID -> UUID.fromString("00000000-0000-4000-8000-abcdefabcdef"); // La classe UUID non permette di formare un UUID malformato
            case NON_EXISTENT_ID -> UUID.fromString("00000000-0000-4000-8000-abcdefabcdef");
            case VALID_ID -> UUID.randomUUID();
            default -> throw new IllegalStateException("Tipo di id non supportato: " + entityIdType.name());
        };
    }
}
