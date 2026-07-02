package it.pagopa.interop.eservice.service.impl;

import it.pagopa.interop.M2MVersionsMapper;
import it.pagopa.interop.common.client.AbstractDPoPClient;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.common.operation.SimpleOperation;
import it.pagopa.interop.common.rest_template.DpopRestTemplate;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.eservice.service.IM2MV3EserviceClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Document;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EService;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServices;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.FileDownloadMultipart;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.*;
import it.pagopa.interop.utils.ApiClientUtils;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
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
public class M2MV3EserviceClientImpl extends AbstractDPoPClient implements IM2MV3EserviceClient {
    private final EservicesApi eservicesApi;
    private final String basePath;
    private final M2MVersionsMapper vMapper;
    private final EserviceListRequest defaultEserviceListRequest;

    public M2MV3EserviceClientImpl(
        DpopRestTemplate restTemplate,
        InteropClientConfigs interopClientConfigs,
        M2MVersionsMapper vMapper) {
        super(restTemplate);
        this.basePath = interopClientConfigs.getM2mV3BaseUrl();
        this.eservicesApi = new EservicesApi(
            ApiClientUtils.createApiClient(restTemplate, basePath,
                Collections.emptyMap()));

        this.defaultEserviceListRequest = EserviceListRequest.builder()
                .limit(30)
                .offset(0)
                .build();
        this.vMapper = vMapper;
    }

    @Override
    public EServices getAll(EserviceListRequest req) {
        return this.performOperation(SimpleOperation.of(
                () -> eservicesApi.getEServices(req.getOffset(), req.getLimit(), req.getProducerIds(), req.getTemplateIds(),
                        req.getName(), vMapper.mapToV3(req.getEServiceTechnology()), vMapper.mapToV3(req.getEServiceMode()),
                        req.getIsSignalHubEnabled(), req.getIsConsumerDelegable(), req.getIsClientAccessDelegable()),
                res -> res
        )).map(vMapper::mapToV2).orElse(null);
    }

    @Override
    public EService get(UUID id) {
        return this.performOperation(SimpleOperation.of(
                () -> eservicesApi.getEService(id),
                res -> res
        )).map(vMapper::mapToV2).orElse(null);
    }

    public ResponseEntity<EService> getWithHttpInfo(UUID id){
        return vMapper.map(eservicesApi.getEServiceWithHttpInfo(id), vMapper::mapToV2);
    }

    @Override
    public List<EService> getAll() {
        return this.performOperation(SimpleOperation.of(
                () -> vMapper.mapToV2(eservicesApi.getEServices(
                        this.defaultEserviceListRequest.getOffset(),
                        this.defaultEserviceListRequest.getLimit(),
                        this.defaultEserviceListRequest.getProducerIds(),
                        this.defaultEserviceListRequest.getTemplateIds(),
                        this.defaultEserviceListRequest.getName(),
                        vMapper.mapToV3(this.defaultEserviceListRequest.getEServiceTechnology()),
                        vMapper.mapToV3(this.defaultEserviceListRequest.getEServiceMode()),
                        this.defaultEserviceListRequest.getIsSignalHubEnabled(),
                        this.defaultEserviceListRequest.getIsConsumerDelegable(),
                        this.defaultEserviceListRequest.getIsClientAccessDelegable()
                )),
                EServices::getResults
        )).orElse(List.of());
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
        return vMapper.mapToV2(this.eservicesApi.uploadEServiceDescriptorInterface(
            body.getEServiceId(),
            body.getDescriptorId(),
            body.getFile(),
            body.getPrettyName()
        ));
    }

    @Override
    public Document uploadAsyncExchangeCallbackInterface(EServiceInterfaceUploadRequest body) {
        return vMapper.mapToV2(this.eservicesApi.uploadEServiceDescriptorAsyncExchangeCallbackInterface(
            body.getEServiceId(),
            body.getDescriptorId(),
            body.getFile(),
            body.getPrettyName()
        ));
    }

    @Override
    public void deleteEServiceDescriptorAsyncExchangeCallbackInterface(UUID eServiceId, UUID descriptorId) {
        this.eservicesApi.deleteEServiceDescriptorAsyncExchangeCallbackInterface(eServiceId, descriptorId);
    }

    @Override
    public FileDownloadMultipart downloadEServiceDescriptorAsyncExchangeCallbackInterface(UUID eServiceId, UUID descriptorId) {
        return vMapper.mapToV2(eservicesApi.downloadEServiceDescriptorAsyncExchangeCallbackInterface(eServiceId, descriptorId));
    }

    @Override
    public EService createEService(EServiceCreateRequest body) {
        return vMapper.mapToV2(eservicesApi.createEService(new EServiceSeed()
                .name(body.getName())
                .description(body.getDescription())
                .descriptor(vMapper.mapToV3(body.getDescriptor()))
                .technology(vMapper.mapToV3(body.getTechnology()))
                .mode(vMapper.mapToV3(body.getMode()))
                .isSignalHubEnabled(body.getIsSignalHubEnabled())
                .isConsumerDelegable(body.getIsConsumerDelegable())
                .isClientAccessDelegable(body.getIsClientAccessDelegable())
                .personalData(body.getPersonalData())));
    }

    @Override
    public EService patchEService(UUID eServiceId, EServicePatchRequest body) {
        return vMapper.mapToV2(eservicesApi.updateDraftEService(eServiceId, new EServiceDraftUpdateSeed()
            .technology(vMapper.mapToV3(body.getTechnology()))
            .isSignalHubEnabled(body.getIsSignalHubEnabled())
            .mode(vMapper.mapToV3(body.getMode()))
            .description(body.getDescription())
            .name(body.getName())
            .isConsumerDelegable(body.getIsConsumerDelegable())
            .isClientAccessDelegable(body.getIsClientAccessDelegable())));
    }

    @Override
    public EService patchEServiceName(UUID eServiceId, EServiceNamePatchRequest body) {
        return vMapper.mapToV2(eservicesApi.updatePublishedEServiceName(eServiceId, new EServiceNameUpdateSeed().name(body.getName())));
    }

    @Override
    public EService patchEServiceDelegation(UUID eServiceId, EServiceDelegationPatchRequest body) {
        return vMapper.mapToV2(eservicesApi.updatePublishedEServiceDelegation(eServiceId, new EServiceDelegationUpdateSeed()
                .isConsumerDelegable(body.getIsConsumerDelegable()).isClientAccessDelegable(body.getIsClientAccessDelegable())));
    }

    @Override
    public EService patchEServiceDescription(UUID eServiceId, EServiceDescriptionPatchRequest body) {
        return vMapper.mapToV2(eservicesApi.updatePublishedEServiceDescription(eServiceId, new EServiceDescriptionUpdateSeed().description(body.getDescription())));
    }

    @Override
    public EService scheduleArchiveEService(UUID eServiceId, EServiceArchivingRequest body) {
        EServiceArchivingReasonSeed seed = body == null
            ? null
            : new EServiceArchivingReasonSeed().archivingReason(body.getArchivingReason());
        return vMapper.mapToV2(eservicesApi.scheduleArchiveEservice(eServiceId, seed));
    }

    @Override
    public EService cancelScheduleArchiveEService(UUID eServiceId) {
        return vMapper.mapToV2(eservicesApi.cancelScheduleArchiveEservice(eServiceId));
    }

    @Override
    public FileDownloadMultipart getDescriptorInterface(UUID eServiceId, UUID descriptorId) {
        return vMapper.mapToV2(eservicesApi.downloadEServiceDescriptorInterface(eServiceId, descriptorId));
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
    public void setBearerToken(String bearerToken) {
        throw new UnsupportedOperationException(V3_UNSUPPORTED_BEARER_MSG);
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        this.eservicesApi.setApiClient(
            ApiClientUtils.createApiClient(super.getRestTemplate(), basePath, headers));
    }
}
