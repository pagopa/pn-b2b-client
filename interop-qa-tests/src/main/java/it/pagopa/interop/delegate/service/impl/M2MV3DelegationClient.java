package it.pagopa.interop.delegate.service.impl;

import static it.pagopa.interop.utils.ApiClientUtils.V3_UNSUPPORTED_BEARER_MSG;

import it.pagopa.interop.M2MVersionsMapper;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.delegate.service.IM2MV3DelegationClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.ConsumerDelegation;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.ConsumerDelegations;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DelegationSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.ProducerDelegation;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.api.DelegationsApi;
import it.pagopa.interop.utils.ApiClientUtils;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class M2MV3DelegationClient implements IM2MV3DelegationClient {
    private final DelegationsApi delegationsApi;
    private final RestTemplate restTemplate;
    private final String basePath;
    private final M2MVersionsMapper mapper;

    public M2MV3DelegationClient(
        RestTemplate restTemplate,
        InteropClientConfigs interopClientConfigs,
        M2MVersionsMapper mapper
    ) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getM2mV3BaseUrl();
        this.delegationsApi = new DelegationsApi(ApiClientUtils.createApiClient(restTemplate, basePath,
            Collections.emptyMap()));
        this.mapper = mapper;
    }

    @Override
    public ConsumerDelegation createConsumerDelegation(DelegationSeed seed) {
        return mapper.mapToV2(this.delegationsApi.createConsumerDelegation(mapper.mapToV3(seed)));
    }

    @Override
    public ConsumerDelegations getConsumerDelegations(
        @Nullable List<UUID> delegatorIds,
        @Nullable List<UUID> delegateIds,
        @Nullable List<UUID> eserviceIds) {
        return mapper.mapToV2(this.delegationsApi.getConsumerDelegations(
            0,
            30,
            null,
            delegatorIds,
            delegateIds,
            eserviceIds
        ));
    }

    @Override
    public ProducerDelegation getProducerDelegation(UUID delegationId) {
        return mapper.mapToV2(this.delegationsApi.getProducerDelegation(delegationId));
    }

    @Override
    public ConsumerDelegation getConsumerDelegation(UUID delegationId) {
        return mapper.mapToV2(this.delegationsApi.getConsumerDelegation(delegationId));
    }

    @Override
    public void setBearerToken(String bearerToken) {
        throw new UnsupportedOperationException(V3_UNSUPPORTED_BEARER_MSG);
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        this.delegationsApi.setApiClient(ApiClientUtils.createApiClient(restTemplate, basePath, headers));
    }
}