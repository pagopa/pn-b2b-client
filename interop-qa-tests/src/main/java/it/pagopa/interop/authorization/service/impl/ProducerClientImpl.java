package it.pagopa.interop.authorization.service.impl;

import it.pagopa.interop.authorization.service.IProducerClient;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServices;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Retryable(
        retryFor = { HttpServerErrorException.class },
        backoff = @Backoff(delay = 2000)
)
public class ProducerClientImpl implements IProducerClient {
    private final EservicesApi eservicesApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public ProducerClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getBaseUrl();
        this.eservicesApi = new EservicesApi(createApiClient("dummyBearer"));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public ProducerEServiceDescriptor getProducerEServiceDescriptor(UUID eserviceId, UUID descriptorId) {
        return eservicesApi.getProducerEServiceDescriptor(eserviceId, descriptorId);
    }

    @Override
    public ProducerEServiceDetails getProducerEServiceDetails(UUID eserviceId) {
        return eservicesApi.getProducerEServiceDetails(eserviceId);
    }

    @Override
    public ProducerEServices getProducerEServices(Integer offset, Integer limit, String q, List<UUID> consumersIds, Boolean delegated) {
        /* DEV. NOTE 22/10/2025: il campo "personalData" è stato aggiunto a posteriori della
         * stesura di questo metodo. Essendo opzionale, lo si pone a null per mantenere compatibilità con i test esistenti. */
        return eservicesApi.getProducerEServices(offset, limit, q, consumersIds, delegated, null);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.eservicesApi.setApiClient(createApiClient(bearerToken));
    }

}
