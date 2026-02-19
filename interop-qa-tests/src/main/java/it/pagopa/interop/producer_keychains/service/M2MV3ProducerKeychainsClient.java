package it.pagopa.interop.producer_keychains.service;

import it.pagopa.interop.common.client.AbstractDPoPClient;
import it.pagopa.interop.common.client.NoAuthApiClient;
import it.pagopa.interop.common.rest_template.DpopRestTemplate;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.api.KeysApi;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.api.ProducerKeychainsApi;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.KeySeed;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.LinkUser;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.ProducerKey;
import it.pagopa.interop.producer_keychains.IM2MV3ProducerKeychainsClient;
import it.pagopa.interop.utils.HttpCallExecutor;
import java.util.UUID;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Users;

@ToString
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class M2MV3ProducerKeychainsClient extends AbstractDPoPClient implements
    IM2MV3ProducerKeychainsClient {

    private final ProducerKeychainsApi producerKeychainsApi;
    private final KeysApi keysApi;
    private final String basePath;

    public M2MV3ProducerKeychainsClient(DpopRestTemplate dpopRestTemplate, InteropClientConfigs interopClientConfigs, HttpCallExecutor httpCallExecutor) {
        super(dpopRestTemplate);

        this.basePath = interopClientConfigs.getM2mV3BaseUrl();
        super.httpCallExecutor = httpCallExecutor;

        this.producerKeychainsApi = new ProducerKeychainsApi(createProducerKeychainsApiClient());
        this.keysApi = new KeysApi(createKeysApiClient());
    }

    private ApiClient createProducerKeychainsApiClient() {
        ApiClient apiClient = new NoAuthApiClient(super.getRestTemplate());
        apiClient.setBasePath(basePath);
        return apiClient;
    }

    private ApiClient createKeysApiClient() {
        ApiClient apiClient = new NoAuthApiClient(super.getRestTemplate());
        apiClient.setBasePath(basePath);
        return apiClient;
    }

    public ProducerKey createProducerKeychainKey(UUID keychainId, KeySeed keySeed) {
        return performOperation(() -> producerKeychainsApi.createProducerKeychainKeyWithHttpInfo(keychainId, keySeed))
                .orElseThrow(() -> new IllegalStateException("Errore nella creazione della chiave del producer keychain (response non 2xx o body nullo)"));
    }

    public void deleteProducerKeychainKeyByKid(UUID keychainId, String kid) {
        performOperation(() -> producerKeychainsApi.deleteProducerKeychainKeyByIdWithHttpInfo(keychainId, kid));
    }

    public void createProducerKeychainUserAssociation(UUID producerKeychainId, LinkUser linkUser) {
        performOperation(() -> producerKeychainsApi.addProducerKeychainUserWithHttpInfo(producerKeychainId, linkUser)).orElseThrow(() -> new IllegalStateException("Errore nella creazione della chiave del producer keychain (response non 2xx)"));
    }

    public Users getProducerKeychainUsers(UUID producerKeychainId, Integer limit, Integer offset) {
        return performOperation(() -> producerKeychainsApi.getProducerKeychainUsersWithHttpInfo(producerKeychainId, limit, offset)).orElseThrow(() -> new IllegalStateException("Errore nel recupero delle utenze associate alla producer keychain (response non 2xx o body nullo)"));
    }

    public void deleteProducerKeychainUserAssociationById(UUID keychainId, UUID keyId) {
        performOperation(() -> producerKeychainsApi.removeProducerKeychainUserWithHttpInfo(keychainId, keyId)).orElseThrow(() -> new IllegalStateException("Errore nella cancellazione della chiave del producer keychain (response non 2xx)"));
    }

    public ProducerKey getProducerKey(String kid) {
        return performOperation(
                () -> keysApi.getProducerJWKByKidWithHttpInfo(kid)
        ).orElseThrow(
                () -> new IllegalStateException("Errore nella creazione della chiave del producer keychain (response non 2xx o body nullo)")
        );
    }
}