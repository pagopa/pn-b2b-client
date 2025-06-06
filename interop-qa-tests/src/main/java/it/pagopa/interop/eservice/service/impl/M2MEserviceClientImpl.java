package it.pagopa.interop.eservice.service.impl;

import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.eservice.service.IM2MEserviceClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EService;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptors;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServices;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@ToString
@EqualsAndHashCode
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class M2MEserviceClientImpl implements IM2MEserviceClient {
    private final EservicesApi eservicesApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public M2MEserviceClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getM2mBaseUrl();
        this.eservicesApi = new EservicesApi(createApiClient("dummyBearer"));
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
    public EService getEService(UUID eserviceId) {
        return eservicesApi.getEService(eserviceId);
    }

    @Override
    public EServices getEServices(EserviceListRequest eserviceListRequest) {
        return eservicesApi.getEServices(
                eserviceListRequest.getOffset(),
                eserviceListRequest.getLimit(),
                eserviceListRequest.getProducerIds(),
                eserviceListRequest.getTemplateIds()
        );
    }

    @Override
    public EServiceDescriptors getEserviceDescriptors(EserviceDescriptorsListRequest eserviceDescriptorsListRequest) {
        return eservicesApi.getEServiceDescriptors(
                eserviceDescriptorsListRequest.getEserviceId(),
                eserviceDescriptorsListRequest.getOffset(),
                eserviceDescriptorsListRequest.getLimit(),
                eserviceDescriptorsListRequest.getState()
        );
    }

    @Override
    public EServiceDescriptor getEserviceDescriptor(UUID eserviceId, UUID descriptorId) {
        return eservicesApi.getEServiceDescriptor(eserviceId, descriptorId);
    }

}
