package it.pagopa.interop.eservice.service.impl;

import it.pagopa.interop.common.client.AbstractClient;
import it.pagopa.interop.common.operation.SimpleOperation;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.eservice.service.IM2MEserviceDescriptorClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@ToString
@EqualsAndHashCode
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class M2MEserviceDescriptorClientImpl extends AbstractClient implements IM2MEserviceDescriptorClient {
    private final EservicesApi eservicesApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    private IM2MEserviceDescriptorClient.EserviceDescriptorsListRequest defaultDescriptorListRequest;

    public M2MEserviceDescriptorClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getM2mBaseUrl();
        this.eservicesApi = new EservicesApi(createApiClient("dummyBearer"));

        this.defaultDescriptorListRequest = IM2MEserviceDescriptorClient.EserviceDescriptorsListRequest.builder()
                .limit(30)
                .offset(0)
                .eserviceId(UUID.randomUUID())
                .build();
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.eservicesApi.setApiClient(createApiClient(bearerToken));
    }


    @Override
    public EServiceDescriptor get(UUID eserviceId, UUID descriptorId) {
        return this.performOperation(SimpleOperation.of(
                () -> eservicesApi.getEServiceDescriptor(eserviceId, descriptorId),
                res -> res
        )).orElse(null);
    }


    @Override
    public EServiceDescriptors getAll(EserviceDescriptorsListRequest eserviceDescriptorsListRequest) {
        return this.performOperation(SimpleOperation.of(
                () -> eservicesApi.getEServiceDescriptors(
                        eserviceDescriptorsListRequest.getEserviceId(),
                        eserviceDescriptorsListRequest.getOffset(),
                        eserviceDescriptorsListRequest.getLimit(),
                        eserviceDescriptorsListRequest.getState()
                ),
                res -> res
        )).orElse(null);
    }

    @Override
    public EServiceDescriptors getAll(UUID eserviceId) {
        this.defaultDescriptorListRequest.setEserviceId(eserviceId);
        return this.getAll(defaultDescriptorListRequest);
    }

    @Override
    public EServiceDescriptor get(UUID id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<EServiceDescriptor> getAll() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public UUID getId(EServiceDescriptor entity) {
        return entity == null ? null : entity.getId();
    }

    @Override
    public UUID generateId() {
        return UUID.randomUUID();
    }
}
