package it.pagopa.interop.agreement.service.impl;

import static it.pagopa.interop.utils.ApiClientUtils.V3_UNSUPPORTED_BEARER_MSG;

import it.pagopa.interop.M2MVersionsMapper;
import it.pagopa.interop.agreement.service.IM2MV3ClientsClient;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.api.ClientsApi;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purposes;
import it.pagopa.interop.utils.ApiClientUtils;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@ToString
@EqualsAndHashCode
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class M2MV3ClientsClientImpl implements IM2MV3ClientsClient {
    private final ClientsApi clientsApi;
    private final RestTemplate restTemplate;
    private final String basePath;
    private final M2MVersionsMapper mapper;

    public M2MV3ClientsClientImpl(
        RestTemplate restTemplate,
        InteropClientConfigs interopClientConfigs,
        M2MVersionsMapper mapper
    ) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getM2mV3BaseUrl();
        this.clientsApi = new ClientsApi(ApiClientUtils.createApiClient(restTemplate, basePath,
            Collections.emptyMap()));
        this.mapper = mapper;
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
    public void setBearerToken(String bearerToken) {
        throw new UnsupportedOperationException(V3_UNSUPPORTED_BEARER_MSG);
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        this.clientsApi.setApiClient(ApiClientUtils.createApiClient(restTemplate, basePath, headers));
    }
}