package it.pagopa.interop.eservice.service.impl;

import it.pagopa.interop.APIUnavailableException;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.common.client.AbstractClient;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.common.operation.SimpleOperation;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.eservice.service.IM2MEserviceDescriptorClient;
import it.pagopa.interop.eservice.service.mapper.EserviceDescriptorDomainMapper;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Documents;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorDraftUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorQuotasUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.FileDownloadMultipart;
import java.util.List;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@ToString
@EqualsAndHashCode
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Primary
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
    public it.pagopa.interop.agreement.domain.EServiceDescriptor get(UUID eserviceId, UUID descriptorId) {
        return this.performOperation(SimpleOperation.of(
                () -> eservicesApi.getEServiceDescriptor(eserviceId, descriptorId),
                res -> EserviceDescriptorDomainMapper.mapTo(eserviceId, res.getId())
        )).orElse(null);
    }

    @Override
    public it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor getCompleteResource(
        UUID eserviceId, UUID descriptorId) {
        return this.performOperation(SimpleOperation.of(
            () -> eservicesApi.getEServiceDescriptor(eserviceId, descriptorId),
            res -> res
        )).orElse(null);
    }


    @Override
    public List<it.pagopa.interop.agreement.domain.EServiceDescriptor> getAll(EserviceDescriptorsListRequest eserviceDescriptorsListRequest) {
        return this.performOperation(SimpleOperation.of(
                () -> eservicesApi.getEServiceDescriptors(
                        eserviceDescriptorsListRequest.getEserviceId(),
                        eserviceDescriptorsListRequest.getOffset(),
                        eserviceDescriptorsListRequest.getLimit(),
                        eserviceDescriptorsListRequest.getState()
                ),
                res -> EserviceDescriptorDomainMapper.mapTo(eserviceDescriptorsListRequest.getEserviceId(), res)
        )).orElse(null);
    }

    @Override
    public List<it.pagopa.interop.agreement.domain.EServiceDescriptor> getAll(UUID eserviceId) {
        this.defaultDescriptorListRequest.setEserviceId(eserviceId);
        return this.getAll(defaultDescriptorListRequest);
    }

    @Override
    public it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor patchEServiceDescriptor(UUID eserviceId,
       UUID descriptorId, EServiceDescriptorPatchRequest body) {
        return eservicesApi.updateDraftEServiceDescriptor(
            eserviceId,
            descriptorId,
            new EServiceDescriptorDraftUpdateSeed()
                .voucherLifespan(body.getVoucherLifespan())
                .dailyCallsPerConsumer(body.getDailyCallsPerConsumer())
                .dailyCallsTotal(body.getDailyCallsTotal())
                .audience(body.getAudience())
                .description(body.getDescription())
                .agreementApprovalPolicy(body.getAgreementApprovalPolicy()));
    }

    @Override
    public it.pagopa.interop.agreement.domain.EServiceDescriptor get(Pair<UUID, UUID> id) {

        if (id == null || id.getLeft() == null || id.getRight() == null)
            throw new IllegalArgumentException("Gli ID dell'eService e del descriptor non possono essere null.");

        UUID eserviceId = id.getLeft();
        UUID descriptorId = id.getRight();

        return this.performOperation(SimpleOperation.of(
                () -> eservicesApi.getEServiceDescriptor(eserviceId, descriptorId),
                res -> EserviceDescriptorDomainMapper.mapTo(eserviceId, res.getId())
        )).orElse(null);
    }


    @Override
    public List<it.pagopa.interop.agreement.domain.EServiceDescriptor> getAll() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Pair<UUID, UUID> getId(EServiceDescriptor entity) {
        return Pair.of(entity.getEServiceId(), entity.getDescriptorId());
    }

    @Override
    public Pair<UUID, UUID> generateId(EntityIdType type) {
        UUID eserviceId = UUID.fromString("00000000-0000-4000-8000-abcdefabcdef");
        UUID descriptorId = UUID.fromString("deadbeef-dead-4bad-cafe-000000000000");

        return switch (type) {
            case INVALID_ID -> Pair.of(eserviceId, descriptorId);  // La classe UUID non permette di formare un UUID malformato
            case NON_EXISTENT_ID -> Pair.of(eserviceId, descriptorId);
            default -> throw new IllegalStateException("Tipo di id non supportato: " + type.name());
        };
    }

    @Override
    public it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor getDescriptor(UUID eserviceId, UUID descriptorId) {
        return eservicesApi.getEServiceDescriptor(eserviceId, descriptorId);
    }

    @Override
    public void deleteInterface(UUID eServiceId, UUID descriptorId) {
        this.eservicesApi.deleteEServiceDescriptorInterface(eServiceId, descriptorId);
    }

    @Override
    public it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor suspendDescriptor(
        UUID eServiceId, UUID descriptorId) {
        return this.eservicesApi.suspendDescriptor(eServiceId, descriptorId);
    }

    @Override
    public void unsuspendEService(UUID eServiceId, UUID descriptorId) {
        this.eservicesApi.unsuspendDescriptor(eServiceId, descriptorId);
    }

    @Override
    public FileDownloadMultipart downloadEServiceDescriptorInterface(UUID eserviceId,
        UUID descriptorId) {
        return eservicesApi.downloadEServiceDescriptorInterface(eserviceId, descriptorId);
    }

    @Override
    public Documents getDocuments(UUID eserviceId, UUID descriptorId) {
        return this.eservicesApi.getEServiceDescriptorDocuments(eserviceId, descriptorId, 0, 50);
    }

    @Override
    public it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor scheduleArchiveEServiceDescriptor(
        UUID eserviceId, UUID descriptorId) {
        throw new APIUnavailableException("Endpoint disponibile solo per M2M v3");
    }

    @Override
    public it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor cancelEServiceDescriptorArchiving(
        UUID eserviceId, UUID descriptorId) {
        throw new APIUnavailableException("Endpoint disponibile solo per M2M v3");
    }

    @Override
    public it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor patchEServiceDescriptorQuotas(
        UUID eserviceId, UUID descriptorId, EServiceDescriptorQuotasPatchRequest body) {
        return eservicesApi.updatePublishedEServiceDescriptorQuotas(
            eserviceId,
            descriptorId,
            new EServiceDescriptorQuotasUpdateSeed()
                .voucherLifespan(body.getVoucherLifespan())
                .dailyCallsTotal(body.getDailyCallsTotal())
                .dailyCallsPerConsumer(body.getDailyCallsPerConsumer())
        );
    }
}
