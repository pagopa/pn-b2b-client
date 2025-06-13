package it.pagopa.interop.eservice.service.impl;

import it.pagopa.interop.common.client.AbstractClient;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.common.operation.SimpleOperation;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.eservice.service.IM2MEserviceClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EService;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServices;
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
public class M2MEserviceClientImpl extends AbstractClient implements IM2MEserviceClient {
    private final EservicesApi eservicesApi;
    private final RestTemplate restTemplate;
    private final String basePath;
    private EserviceListRequest defaultEserviceListRequest;

    public M2MEserviceClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getM2mBaseUrl();
        this.eservicesApi = new EservicesApi(createApiClient("dummyBearer"));

        this.defaultEserviceListRequest = EserviceListRequest.builder()
                .limit(30)
                .offset(0)
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
    public EServices getAll(EserviceListRequest req) {
        return this.performOperation(SimpleOperation.of(
                () -> eservicesApi.getEServices(req.getOffset(), req.getLimit(), req.getProducerIds(), req.getTemplateIds()),
                res -> res
        )).orElse(null);
    }


    @Override
    public EServiceDescriptor getDescriptor(UUID eserviceId, UUID descriptorId) {
        return this.performOperation(SimpleOperation.of(
                () -> eservicesApi.getEServiceDescriptor(eserviceId, descriptorId),
                res -> res
        )).orElse(null);
    }

    @Override
    public EService get(UUID id) {
        return this.performOperation(SimpleOperation.of(
                () -> eservicesApi.getEService(id),
                res -> res
        )).orElse(null);
    }

    @Override
    public List<EService> getAll() {
        return this.performOperation(SimpleOperation.of(
                () -> eservicesApi.getEServices(
                        this.defaultEserviceListRequest.getOffset(),
                        this.defaultEserviceListRequest.getLimit(),
                        this.defaultEserviceListRequest.getProducerIds(),
                        this.defaultEserviceListRequest.getTemplateIds()
                ),
                EServices::getResults
        )).orElse(List.of());
    }

    @Override
    public UUID getId(EService entity) {
        return entity == null ? null : entity.getId();
    }

    @Override
    public UUID generateId(EntityIdType type) {
        return switch (type){
            case INVALID_ID -> UUID.fromString("00000000-0000-4000-8000-abcdefabcdef"); // La classe UUID non permette di formare un UUID malformato
            case NON_EXISTENT_ID -> UUID.fromString("00000000-0000-4000-8000-abcdefabcdef");
            default -> throw new IllegalStateException("Tipo di id non supportato: " + type.name());
        };
    }
}
