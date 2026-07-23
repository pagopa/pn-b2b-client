package it.pagopa.interop.eservice.service.impl;

import static it.pagopa.interop.utils.ApiClientUtils.V3_UNSUPPORTED_BEARER_MSG;

import it.pagopa.interop.M2MVersionsMapper;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.common.client.AbstractDPoPClient;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.common.operation.SimpleOperation;
import it.pagopa.interop.common.rest_template.DpopRestTemplate;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.eservice.service.IM2MV3EserviceDescriptorClient;
import it.pagopa.interop.eservice.service.mapper.EserviceDescriptorDomainMapper;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Documents;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.FileDownloadMultipart;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceDescriptorAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceDescriptorDraftUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.EServiceDescriptorQuotasUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.GracePeriodDays;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.GracePeriodDaysSeed;
import it.pagopa.interop.utils.ApiClientUtils;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@ToString
@EqualsAndHashCode
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class M2MV3EserviceDescriptorClientImpl extends AbstractDPoPClient implements IM2MV3EserviceDescriptorClient {
    private final EservicesApi eservicesApi;
    private final String basePath;
    private final M2MVersionsMapper vMapper;

    private EserviceDescriptorsListRequest defaultDescriptorListRequest;

    public M2MV3EserviceDescriptorClientImpl(
        DpopRestTemplate restTemplate,
        InteropClientConfigs interopClientConfigs,
        M2MVersionsMapper vMapper
    ) {
        super(restTemplate);
        this.basePath = interopClientConfigs.getM2mV3BaseUrl();
        this.eservicesApi = new EservicesApi(
            ApiClientUtils.createApiClient(restTemplate, basePath,
                Collections.emptyMap()));

        this.defaultDescriptorListRequest = EserviceDescriptorsListRequest.builder()
                .limit(30)
                .offset(0)
                .eserviceId(UUID.randomUUID())
                .build();
        this.vMapper = vMapper;
    }

    @Override
    public EServiceDescriptor get(UUID eserviceId, UUID descriptorId) {
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
        )).map(vMapper::mapToV2).orElse(null);
    }


    @Override
    public List<EServiceDescriptor> getAll(EserviceDescriptorsListRequest eserviceDescriptorsListRequest) {
        return this.performOperation(SimpleOperation.of(
                () -> eservicesApi.getEServiceDescriptors(
                        eserviceDescriptorsListRequest.getEserviceId(),
                        eserviceDescriptorsListRequest.getOffset(),
                        eserviceDescriptorsListRequest.getLimit(),
                        vMapper.mapToV3(eserviceDescriptorsListRequest.getState())
                ),
                res -> EserviceDescriptorDomainMapper.mapTo(eserviceDescriptorsListRequest.getEserviceId(), vMapper.mapToV2(res))
        )).orElse(null);
    }

    @Override
    public List<EServiceDescriptor> getAll(UUID eserviceId) {
        this.defaultDescriptorListRequest.setEserviceId(eserviceId);
        return this.getAll(defaultDescriptorListRequest);
    }

    @Override
    public it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor patchEServiceDescriptor(UUID eserviceId,
       UUID descriptorId, EServiceDescriptorPatchRequest body) {
        return vMapper.mapToV2(eservicesApi.updateDraftEServiceDescriptor(
            eserviceId,
            descriptorId,
            new EServiceDescriptorDraftUpdateSeed()
                .voucherLifespan(body.getVoucherLifespan())
                .dailyCallsPerConsumer(body.getDailyCallsPerConsumer())
                .dailyCallsTotal(body.getDailyCallsTotal())
                .audience(body.getAudience())
                .description(body.getDescription())
                .agreementApprovalPolicy(vMapper.mapToV3(body.getAgreementApprovalPolicy()))));
    }

    @Override
    public EServiceDescriptor get(Pair<UUID, UUID> id) {
        if (id == null || id.getLeft() == null || id.getRight() == null) {
            throw new IllegalArgumentException(
                "Gli ID dell'eService e del descriptor non possono essere null.");
        }

        UUID eserviceId = id.getLeft();
        UUID descriptorId = id.getRight();

        return this.performOperation(SimpleOperation.of(
                () -> eservicesApi.getEServiceDescriptor(eserviceId, descriptorId),
                res -> EserviceDescriptorDomainMapper.mapTo(eserviceId, res.getId())
        )).orElse(null);
    }


    @Override
    public List<EServiceDescriptor> getAll() {
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
        return vMapper.mapToV2(eservicesApi.getEServiceDescriptor(eserviceId, descriptorId));
    }

    @Override
    public void deleteInterface(UUID eServiceId, UUID descriptorId) {
        this.eservicesApi.deleteEServiceDescriptorInterface(eServiceId, descriptorId);
    }

    @Override
    public it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor suspendDescriptor(
        UUID eServiceId, UUID descriptorId) {
        return vMapper.mapToV2(this.eservicesApi.suspendDescriptor(eServiceId, descriptorId));
    }

    @Override
    public void unsuspendEService(UUID eServiceId, UUID descriptorId) {
        this.eservicesApi.unsuspendDescriptor(eServiceId, descriptorId);
    }

    @Override
    public FileDownloadMultipart downloadEServiceDescriptorInterface(UUID eserviceId,
        UUID descriptorId) {
        return vMapper.mapToV2(eservicesApi.downloadEServiceDescriptorInterface(eserviceId, descriptorId));
    }

    @Override
    public Documents getDocuments(UUID eserviceId, UUID descriptorId) {
        return vMapper.mapToV2(this.eservicesApi.getEServiceDescriptorDocuments(eserviceId, descriptorId, 0, 50));
    }

    @Override
    public it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor scheduleArchiveEServiceDescriptor(
        UUID eserviceId, UUID descriptorId, Integer gracePeriodDays) {
        return vMapper.mapToV2(eservicesApi.scheduleArchiveEserviceDescriptor(
            eserviceId,
            descriptorId,
            new GracePeriodDaysSeed().gracePeriodDays(GracePeriodDays.fromValue(gracePeriodDays))
        ));
    }

    @Override
    public it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor cancelEServiceDescriptorArchiving(
        UUID eserviceId, UUID descriptorId) {
        return vMapper.mapToV2(eservicesApi.cancelEServiceDescriptorArchiving(eserviceId, descriptorId));
    }

    @Override
    public it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor patchEServiceDescriptorQuotas(
        UUID eserviceId, UUID descriptorId, EServiceDescriptorQuotasPatchRequest body) {
        return vMapper.mapToV2(eservicesApi.updatePublishedEServiceDescriptorQuotas(
            eserviceId,
            descriptorId,
            new EServiceDescriptorQuotasUpdateSeed()
                .voucherLifespan(body.getVoucherLifespan())
                .dailyCallsTotal(body.getDailyCallsTotal())
                .dailyCallsPerConsumer(body.getDailyCallsPerConsumer())
        ));
    }

    public void patchEServiceDescriptorCertifiedAttribute(UUID eServiceId, UUID descriptorId, Integer groupIndex,
        UUID attributeId, EServiceDescriptorAttributePatchRequest body) {
        eservicesApi.updateEServiceDescriptorCertifiedAttributeInGroup(
            eServiceId,
            descriptorId,
            groupIndex,
            attributeId,
            new EServiceDescriptorAttributeSeed()
                .dailyCallsPerConsumer(body.getDailyCallsPerConsumer())
        );
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
