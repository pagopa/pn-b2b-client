package it.pagopa.interop.eservice.service.impl;

import it.pagopa.interop.APIUnavailableException;
import it.pagopa.interop.common.client.AbstractClient;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.common.operation.SimpleOperation;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.eservice.service.IM2MEserviceClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.*;
import java.util.List;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@ToString
@EqualsAndHashCode
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Primary
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
                () -> eservicesApi.getEServices(req.getOffset(), req.getLimit(), req.getProducerIds(), req.getTemplateIds(),
                        req.getName(), req.getEServiceTechnology(), req.getEServiceMode(),
                        req.getIsSignalHubEnabled(), req.getIsConsumerDelegable(), req.getIsClientAccessDelegable()),
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

    public ResponseEntity<EService> getWithHttpInfo(UUID id){
        return eservicesApi.getEServiceWithHttpInfo(id);
    }

    @Override
    public List<EService> getAll() {
        return this.performOperation(SimpleOperation.of(
                () -> eservicesApi.getEServices(
                        this.defaultEserviceListRequest.getOffset(),
                        this.defaultEserviceListRequest.getLimit(),
                        this.defaultEserviceListRequest.getProducerIds(),
                        this.defaultEserviceListRequest.getTemplateIds(),
                        this.defaultEserviceListRequest.getName(),
                        this.defaultEserviceListRequest.getEServiceTechnology(),
                        this.defaultEserviceListRequest.getEServiceMode(),
                        this.defaultEserviceListRequest.getIsSignalHubEnabled(),
                        this.defaultEserviceListRequest.getIsConsumerDelegable(),
                        this.defaultEserviceListRequest.getIsClientAccessDelegable()
                ),
                EServices::getResults
        )).orElse(List.of());
    }

    @Override
    public List<EService> getPage(int page, int size) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public UUID getId(EService entity) {
        return entity == null ? null : entity.getId();
    }

    @Override
    public void delete(UUID id) {
        this.eservicesApi.deleteEService(id);
    }

    @Override
    public Document uploadInterface(EServiceInterfaceUploadRequest body) {
        return this.eservicesApi.uploadEServiceDescriptorInterface(
            body.getEServiceId(),
            body.getDescriptorId(),
            body.getFile(),
            body.getPrettyName()
        );
    }

    @Override
    public Document uploadAsyncExchangeCallbackInterface(EServiceInterfaceUploadRequest body) {
        throw new APIUnavailableException("Endpoint available only for M2M v3");
    }

    @Override
    public void deleteEServiceDescriptorAsyncExchangeCallbackInterface(UUID eServiceId, UUID descriptorId) {
        throw new APIUnavailableException("Endpoint available only for M2M v3");
    }

    @Override
    public FileDownloadMultipart downloadEServiceDescriptorAsyncExchangeCallbackInterface(UUID eServiceId, UUID descriptorId) {
        throw new APIUnavailableException("Endpoint available only for M2M v3");
    }

    @Override
    public EService createEService(EServiceCreateRequest body) {
        return this.eservicesApi.createEService(body.toSeed());
    }

    @Override
    public EService patchEService(UUID eServiceId, EServicePatchRequest body) {
        return eservicesApi.updateDraftEService(eServiceId, new EServiceDraftUpdateSeed()
            .technology(body.getTechnology())
            .isSignalHubEnabled(body.getIsSignalHubEnabled())
            .mode(body.getMode())
            .description(body.getDescription())
            .name(body.getName())
            .isConsumerDelegable(body.getIsConsumerDelegable())
            .isClientAccessDelegable(body.getIsClientAccessDelegable()));
    }

    @Override
    public EService patchEServiceName(UUID eServiceId, EServiceNamePatchRequest body) {
        return eservicesApi.updatePublishedEServiceName(eServiceId, new EServiceNameUpdateSeed().name(body.getName()));
    }

    @Override
    public EService patchEServiceDelegation(UUID eServiceId, EServiceDelegationPatchRequest body) {
        return eservicesApi.updatePublishedEServiceDelegation(eServiceId, new EServiceDelegationUpdateSeed()
                .isConsumerDelegable(body.getIsConsumerDelegable()).isClientAccessDelegable(body.getIsClientAccessDelegable()));
    }

    @Override
    public EService patchEServiceDescription(UUID eServiceId, EServiceDescriptionPatchRequest body) {
        return eservicesApi.updatePublishedEServiceDescription(eServiceId, new EServiceDescriptionUpdateSeed().description(body.getDescription()));
    }

    @Override
    public EService scheduleArchiveEService(UUID eServiceId, EServiceArchivingRequest body) {
        throw new APIUnavailableException("Endpoint disponibile solo per M2M v3");
    }

    @Override
    public EService cancelScheduleArchiveEService(UUID eServiceId) {
        throw new APIUnavailableException("Endpoint disponibile solo per M2M v3");
    }

    @Override
    public UUID generateId(EntityIdType type) {
        return switch (type){
            case INVALID_ID -> UUID.fromString("00000000-0000-4000-8000-abcdefabcdef"); // La classe UUID non permette di formare un UUID malformato
            case NON_EXISTENT_ID -> UUID.fromString("00000000-0000-4000-8000-abcdefabcdef");
            default -> throw new IllegalStateException("Tipo di id non supportato: " + type.name());
        };
    }

    @Override
    public FileDownloadMultipart getDescriptorInterface(UUID eServiceId, UUID descriptorId) {
        return this.eservicesApi.downloadEServiceDescriptorInterface(eServiceId, descriptorId);
    }
}
