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
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.FileDownloadMultipart;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@ToString
@EqualsAndHashCode
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class M2MEserviceClientImpl extends AbstractClient implements IM2MEserviceClient {
    /* ***************************************************************************************
        TODO 18/07/2025: astrazioni di oggetti non ancora rilasciati nella specifica OpenAPI,
     *    adattare una volta ottenuta la specifica completa */
    @Data
    public static class EServiceInterfaceUploadRequest {
        private UUID eServiceId;
        private UUID descriptorId;
        private Resource file;
        private String name;

        public EServiceInterfaceUploadRequest resource(Resource resource) {
            this.file = resource;
            return this;
        }

        public EServiceInterfaceUploadRequest name(String name) {
            this.name = name;
            return this;
        }

        public EServiceInterfaceUploadRequest eServiceId(UUID eServiceId) {
            this.eServiceId = eServiceId;
            return this;
        }

        public EServiceInterfaceUploadRequest descriptorId(UUID descriptorId) {
            this.descriptorId = descriptorId;
            return this;
        }
    }

    @Data
    public static class EServiceInterfaceUploadResponse {
        private UUID id;
    }

    @Data
    public static class EServicePatchRequest {
        private String name;
        private String description;

        public EServicePatchRequest name(String name) {
            this.name = name;
            return this;
        }

        public EServicePatchRequest description(String description) {
            this.description = description;
            return this;
        }
    }
    /* ***************************************************************************************/

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
    public UUID getId(EService entity) {
        return entity == null ? null : entity.getId();
    }

    @Override
    public void delete(UUID id) {
        this.eservicesApi.deleteEService(id);
    }

    @Override
    public void deleteInterface(UUID eServiceId, UUID descriptorId) {
        // TODO 16/07/2025 specifica OpenAPI non ancora disponibile, dunque non c'è ancora un
        //  metodo nel client da chiamare
    }

    @Override
    public void unsuspendEService(UUID eServiceId, UUID descriptorId) {
        this.eservicesApi.unsuspendDescriptor(eServiceId, descriptorId);
    }

    @Override
    public EServiceInterfaceUploadResponse uploadInterface(EServiceInterfaceUploadRequest body) {
        // TODO 17/07/2025: specifica OpenAPI non ancora disponibile, dunque non c'è ancora un
        //        //  metodo nel client da chiamare
        return new EServiceInterfaceUploadResponse(); // <-- sostituire con impl. reale una volta rilasciata l'API
    }

    @Override
    public FileDownloadMultipart downloadEServiceDescriptorInterface(UUID eserviceId,
        UUID descriptorId) {
        return eservicesApi.downloadEServiceDescriptorInterface(eserviceId, descriptorId);
    }

    @Override
    public void patchEService(UUID eServiceId, EServicePatchRequest body) {
        // TODO 05/08/2025: specifica OpenAPI non ancora disponibile, dunque non c'è ancora un
        //  metodo nel client da chiamare
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
