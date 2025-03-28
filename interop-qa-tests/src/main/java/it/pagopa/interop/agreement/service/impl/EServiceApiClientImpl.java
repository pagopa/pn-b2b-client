package it.pagopa.interop.agreement.service.impl;

import static java.util.Objects.isNull;

import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateInstances;
import it.pagopa.interop.generated.openapi.clients.bff.model.InstanceEServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServices;
import it.pagopa.interop.generated.openapi.clients.bff.model.TemplateInstanceInterfaceRESTSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorTemplateInstanceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateInstanceSeed;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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
        /* Di default l'api NON restituisce le istanze in stato DRAFT, invece si chiedono in
         * questo modo tutte quante */
        List<EServiceDescriptorState> states = Arrays.stream(EServiceDescriptorState.values()).toList();
        return this.eservicesApi.getEServiceTemplateInstancesWithHttpInfo(xCorrelationId, templateId, 0, 50, null, states);
    }

    @Override
    public ResponseEntity<EServiceTemplateInstances> getEServiceTemplateInstancesWithHttpInfo(
        String xCorrelationId, UUID templateId, Integer offset, Integer limit, String producerName,
        List<EServiceDescriptorState> states) {
        return this.eservicesApi.getEServiceTemplateInstancesWithHttpInfo(xCorrelationId, templateId, offset, limit, producerName, states);
    }

    @Override
    public ResponseEntity<CreatedResource> upgradeEServiceInstanceWithHttpInfo(
        String xCorrelationId,
        UUID eServiceId) {
        return this.eservicesApi.upgradeEServiceInstanceWithHttpInfo(xCorrelationId, eServiceId);
    }

    @Override
    public ResponseEntity<ProducerEServiceDescriptor> getProducerEServiceDescriptorWithHttpInfo(
        String xCorrelationId, UUID eserviceId, UUID descriptorId) {
        return this.eservicesApi.getProducerEServiceDescriptorWithHttpInfo(xCorrelationId, eserviceId, descriptorId);
    }

    @Override
    public ResponseEntity<ProducerEServices> getProducerEServicesWithHttpInfo(
        String xCorrelationId, String eServiceName) {
        return this.eservicesApi.getProducerEServicesWithHttpInfo(xCorrelationId, 0, 50, eServiceName, null, null);
    }

    @Override
    public ResponseEntity<CreatedResource> updateEServiceTemplateInstanceByIdWithHttpInfo(
        UUID eServiceId,
        UpdateEServiceTemplateInstanceSeed updateEServiceTemplateInstanceSeed
    ) {
        return this.eservicesApi.updateEServiceTemplateInstanceByIdWithHttpInfo(eServiceId, updateEServiceTemplateInstanceSeed);
    }

    @Override
    public ResponseEntity<CreatedResource> updateDraftDescriptorTemplateInstanceWithHttpInfo(
        String xCorrelationId,
        UUID eServiceId,
        UUID descriptorId,
        UpdateEServiceDescriptorTemplateInstanceSeed updateEServiceDescriptorTemplateInstanceSeed
    ) {
        return this.eservicesApi.updateDraftDescriptorTemplateInstanceWithHttpInfo(xCorrelationId, eServiceId, descriptorId, updateEServiceDescriptorTemplateInstanceSeed);
    }

    @Override
    public ResponseEntity<ProducerEServiceDetails> getProducerEServiceDetailsWithHttpInfo(
        String xCorrelationId, UUID eserviceId) {
        return this.eservicesApi.getProducerEServiceDetailsWithHttpInfo(xCorrelationId, eserviceId);
    }

    @Override
    public ResponseEntity<CreatedResource> addEServiceTemplateInstanceInterfaceRestWithHttpInfo(
        String xCorrelationId, UUID eServiceId, UUID descriptorId,
        TemplateInstanceInterfaceRESTSeed templateInstanceInterfaceRESTSeed) {
        return this.eservicesApi.addEServiceTemplateInstanceInterfaceRestWithHttpInfo(xCorrelationId, eServiceId, descriptorId, templateInstanceInterfaceRESTSeed);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.eservicesApi.setApiClient(createApiClient(bearerToken));
    }
}
