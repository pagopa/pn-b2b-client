package it.pagopa.interop.agreement.service.impl;

import static java.util.Objects.isNull;

import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateInstances;
import it.pagopa.interop.generated.openapi.clients.bff.model.InstanceEServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import java.util.List;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import java.util.UUID;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EServiceApiClientImpl implements IEServiceClient {
    private final EservicesApi eservicesApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public EServiceApiClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
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
    public CreatedEServiceDescriptor createEService(String xCorrelationId, EServiceSeed eserviceSeed) {
        return eservicesApi.createEService(xCorrelationId, eserviceSeed);
    }

    @Override
    public CreatedResource updateDraftDescriptor(String xCorrelationId, UUID eServiceId, UUID descriptorId, UpdateEServiceDescriptorSeed updateEServiceDescriptorSeed) {
        return eservicesApi.updateDraftDescriptor(xCorrelationId, eServiceId, descriptorId, updateEServiceDescriptorSeed);
    }

    @Override
    public CreatedResource createEServiceDocument(String xCorrelationId, UUID eServiceId, UUID descriptorId, String kind, String prettyName, org.springframework.core.io.Resource doc) {
        return eservicesApi.createEServiceDocument(xCorrelationId, eServiceId, descriptorId, kind, prettyName, doc);
    }

    @Override
    public void publishDescriptor(String xCorrelationId, UUID eServiceId, UUID descriptorId) {
        eservicesApi.publishDescriptor(xCorrelationId, eServiceId, descriptorId);
    }

    @Override
    public void suspendDescriptor(String xCorrelationId, UUID eServiceId, UUID descriptorId) {
        eservicesApi.suspendDescriptor(xCorrelationId, eServiceId, descriptorId);
    }

    @Override
    public CreatedResource createDescriptor(String xCorrelationId, UUID eServiceId) {
        return eservicesApi.createDescriptor(xCorrelationId, eServiceId);
    }

    @Override
    public ResponseEntity<CreatedResource> createEServiceInstanceFromTemplateWithHttpInfo(
        String xCorrelationId, UUID templateId, InstanceEServiceSeed instanceEServiceSeed) {
        /* DEV. NOTE 10/03/2025: al momento InstanceEServiceSeed è required dalla API, tuttavia
        * nessuno dei suoi campi lo è; per comodità si permette a questo metodo di passare NULL
        * mappandolo con un'istanza vuota. */
        return this.eservicesApi.createEServiceInstanceFromTemplateWithHttpInfo(
            xCorrelationId,
            templateId,
            isNull(instanceEServiceSeed) ? new InstanceEServiceSeed() : instanceEServiceSeed);
    }

    @Override
    public ResponseEntity<EServiceTemplateInstances> getEServiceTemplateInstancesWithHttpInfo(
        String xCorrelationId, UUID templateId) {
        return this.eservicesApi.getEServiceTemplateInstancesWithHttpInfo(xCorrelationId, templateId, 0, 100, null, null);
    }

    @Override
    public ResponseEntity<EServiceTemplateInstances> getEServiceTemplateInstancesWithHttpInfo(
        String xCorrelationId, UUID templateId, Integer offset, Integer limit, String producerName,
        List<EServiceDescriptorState> states) {
        return this.eservicesApi.getEServiceTemplateInstancesWithHttpInfo(xCorrelationId, templateId, offset, limit, producerName, states);
    }

    @Override
    public ResponseEntity<CreatedEServiceDescriptor> upgradeEServiceInstanceWithHttpInfo(
        UUID eServiceId) {
        /* TODO 10/03/2025 diversamente da tutte le altre API questa NON si aspetta xCorrelationId. Segnalato, riscontro non ancora ricevuto
            https://pagopaspa.slack.com/archives/C085C3D1U84/p1741624453778969 */
        return this.eservicesApi.upgradeEServiceInstanceWithHttpInfo(eServiceId);
    }

    @Override
    public ResponseEntity<ProducerEServiceDescriptor> getProducerEServiceDescriptorWithHttpInfo(
        String xCorrelationId, UUID eserviceId, UUID descriptorId) {
        return this.eservicesApi.getProducerEServiceDescriptorWithHttpInfo(xCorrelationId, eserviceId, descriptorId);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.eservicesApi.setApiClient(createApiClient(bearerToken));
    }

}
