package it.pagopa.interop.producer_keychains.service;

import it.pagopa.interop.common.client.AbstractDPoPClient;
import it.pagopa.interop.common.rest_template.DpopRestTemplate;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.api.KeysApi;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.api.ProducerKeychainsApi;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.*;
import it.pagopa.interop.producer_keychains.IM2MV3ProducerKeychainsClient;
import it.pagopa.interop.utils.HttpCallExecutor;

import java.util.UUID;

import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@ToString
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class M2MV3ProducerKeychainsClient extends AbstractDPoPClient implements IM2MV3ProducerKeychainsClient {

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
        ApiClient apiClient = super.getApiClient();
        apiClient.setBasePath(basePath);
        return apiClient;
    }

    private ApiClient createKeysApiClient() {
        ApiClient apiClient = super.getApiClient();
        apiClient.setBasePath(basePath);
        return apiClient;
    }

    @Override
    public ProducerKeychain createProducerKeychain(ProducerKeychainSeed producerKeychainSeed) {
        return performOperation(
                    () -> producerKeychainsApi
                            .createProducerKeychainWithHttpInfo(producerKeychainSeed)
                 )
                .orElseThrow(
                        () -> new IllegalStateException("Errore nella creazione del portachiavi erogatore (response non 2xx o body nullo)")
                );
    }

    @Override
    public void deleteProducerKeychain(UUID producerKeychainId) {
        performOperation(
                () -> producerKeychainsApi
                        .deleteProducerKeychainWithHttpInfo(producerKeychainId)
        );
        if(httpCallExecutor.getResponseStatus().isError())
            throw new IllegalStateException("Errore nell'eliminazione del portachiavi erogatore (response non 2xx o body nullo)");
    }

    @Override
    public ProducerKeychain getProducerKeychains(UUID producerKeychainId) {
        return performOperation(
                () -> producerKeychainsApi
                        .getProducerKeychainWithHttpInfo(producerKeychainId)
        )
                .orElseThrow(
                        () -> new IllegalStateException("Errore nel recupero del portachiavi erogatore (response non 2xx o body nullo)")
                );
    }

    public ProducerKey createProducerKeychainKey(UUID keychainId, KeySeed keySeed) {
        return performOperation(() -> producerKeychainsApi.createProducerKeychainKeyWithHttpInfo(keychainId, keySeed))
                .orElseThrow(() -> new IllegalStateException("Errore nella creazione della chiave del producer keychain (response non 2xx o body nullo)"));
    }

    public void deleteProducerKeychainKeyByKid(UUID keychainId, String kid) {
        performOperation(() -> producerKeychainsApi.deleteProducerKeychainKeyByIdWithHttpInfo(keychainId, kid)).orElseThrow(() -> new IllegalStateException("Errore nella cancellazione della chiave del producer keychain (response non 2xx)"));
    }

    public void createProducerKeychainUserAssociation(UUID producerKeychainId, LinkUser linkUser) {
        performOperation(
                () -> producerKeychainsApi.addProducerKeychainUserWithHttpInfo(producerKeychainId, linkUser)
        ).orElseThrow(() -> new IllegalStateException("Errore durante l'associazione dell'utente al producer keychains"));
    }

    public Users getProducerKeychainUsers(UUID producerKeychainId, Integer limit, Integer offset) {
        return performOperation(() -> producerKeychainsApi.getProducerKeychainUsersWithHttpInfo(producerKeychainId, limit, offset)).orElseThrow(() -> new IllegalStateException("Errore nel recupero delle utenze associate alla producer keychain (response non 2xx o body nullo)"));
    }

    public void deleteProducerKeychainUserAssociationById(UUID keychainId, UUID keyId) {
        performOperation(() -> producerKeychainsApi.removeProducerKeychainUserWithHttpInfo(keychainId, keyId));
    }

    public ProducerKey getProducerKey(String kid) {
        return performOperation(
                () -> keysApi.getProducerJWKByKidWithHttpInfo(kid)
        ).orElseThrow(
                () -> new IllegalStateException("Errore nel recupero della chiave del producer keychain (response non 2xx o body nullo)")
        );
    }
}