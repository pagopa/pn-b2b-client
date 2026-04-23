package it.pagopa.interop.agreement.service.impl;

import it.pagopa.interop.M2MVersionsMapper;
import it.pagopa.interop.agreement.service.IM2MV3ClientsClient;
import it.pagopa.interop.common.client.AbstractDPoPClient;
import it.pagopa.interop.common.rest_template.DpopRestTemplate;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Client;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeVersionState;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purposes;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.api.ClientsApi;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.ClientSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.JWKs;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Key;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.KeySeed;
import it.pagopa.interop.utils.ApiClientUtils;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static it.pagopa.interop.utils.ApiClientUtils.V3_UNSUPPORTED_BEARER_MSG;

@ToString
@EqualsAndHashCode
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class M2MV3ClientsClientImpl extends AbstractDPoPClient implements IM2MV3ClientsClient {
    private final ClientsApi clientsApi;
    private final String basePath;
    private final M2MVersionsMapper mapper;

    public M2MV3ClientsClientImpl(
        DpopRestTemplate restTemplate,
        InteropClientConfigs interopClientConfigs,
        M2MVersionsMapper mapper
    ) {
        super(restTemplate);
        this.basePath = interopClientConfigs.getM2mV3BaseUrl();
        this.clientsApi = new ClientsApi(ApiClientUtils.createApiClient(restTemplate, basePath,
            Collections.emptyMap()));
        this.mapper = mapper;
    }

    @Override
    public Client getClient(UUID clientId) {
        return performOperation(
                () -> clientsApi.getClientWithHttpInfo(clientId)
        ).map(mapper::mapToV2).orElseThrow(() -> new IllegalStateException(
                "Errore nel recupero del client (response non 2xx o body nullo)"
        ));
    }

    @Override
    public Purposes getClientPurposes(UUID clientId) {
        return this.getClientPurposes(clientId, 0, 30);
    }

    @Override
    public Purposes getClientPurposes(UUID clientId, int offset, int limit) {
        it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Purposes v3Bean = clientsApi.getClientPurposes(
            clientId, offset, limit, null, null);
        return this.mapper.mapToV2(v3Bean);
    }

    @Override
    public Purposes getClientPurposes(UUID clientId, Integer offset, Integer limit, List<UUID> eserviceIds, List<PurposeVersionState> states) {
        return performOperation(() -> clientsApi.getClientPurposesWithHttpInfo(
                clientId,
                offset,
                limit,
                eserviceIds,
                mapper.mapToPStateV3(states))
        ).map(mapper::mapToV2).orElseThrow(() -> new IllegalStateException(
                "Errore nel recupero dei client (response non 2xx o body nullo)"
        ));
    }

    @Override
    public void setBearerToken(String bearerToken) {
        throw new UnsupportedOperationException(V3_UNSUPPORTED_BEARER_MSG);
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        this.clientsApi.setApiClient(ApiClientUtils.createApiClient(super.getRestTemplate(), basePath, headers));
    }

    @Override
    public Key createClientKey(UUID clientId, KeySeed keySeed) {
        return performOperation(
                () -> clientsApi.createClientKeyWithHttpInfo(clientId, keySeed)
        ).orElseThrow(
                () -> new IllegalStateException("Errore durante la creazione della key per il client-keychain")
        );
    }

    @Override
    public JWKs getClientKeys(UUID clientId, Integer offset, Integer limit) {
        return performOperation(
                () -> clientsApi.getClientKeysWithHttpInfo(clientId, offset, limit)
        ).orElseThrow(
                () -> new IllegalStateException("Errore durante il recupero delle chiavi")
        );
    }

    @Override
    public it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Client createClient(ClientSeed clientSeed) {
        return performOperation(
                () -> clientsApi.createClientWithHttpInfo(clientSeed)
        ).orElseThrow(
                () -> new IllegalStateException("Errore durante la creazione del client")
        );
    }

    @Override
    public void deleteClient(UUID clientId) {
        performOperation(
                () -> clientsApi.deleteClientWithHttpInfo(clientId)
        );

        if(this.httpCallExecutor.getResponseStatus().isError())
            throw new IllegalStateException("Errore durante l'eliminazione del client con id: " + clientId);
    }
}