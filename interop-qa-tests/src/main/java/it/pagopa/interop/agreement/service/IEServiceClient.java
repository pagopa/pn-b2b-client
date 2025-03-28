package it.pagopa.interop.agreement.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
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
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface IEServiceClient extends SettableBearerToken {

    CreatedEServiceDescriptor createEService(String xCorrelationId, EServiceSeed eserviceSeed);
    CreatedResource updateDraftDescriptor(String xCorrelationId, UUID eServiceId, UUID descriptorId, UpdateEServiceDescriptorSeed updateEServiceDescriptorSeed);
    CreatedResource createEServiceDocument(String xCorrelationId, UUID eServiceId, UUID descriptorId, String kind, String prettyName, org.springframework.core.io.Resource doc);
    void publishDescriptor(String xCorrelationId, UUID eServiceId, UUID descriptorId);
    void suspendDescriptor(String xCorrelationId, UUID eServiceId, UUID descriptorId);
    CreatedResource createDescriptor(String xCorrelationId, UUID eServiceId);

    ResponseEntity<CreatedResource> createEServiceInstanceFromTemplateWithHttpInfo(
        String xCorrelationId, UUID templateId, InstanceEServiceSeed instanceEServiceSeed);

    ResponseEntity<EServiceTemplateInstances> getEServiceTemplateInstancesWithHttpInfo(
        String xCorrelationId, UUID templateId);

    ResponseEntity<EServiceTemplateInstances> getEServiceTemplateInstancesWithHttpInfo(
        String xCorrelationId, UUID templateId, Integer offset, Integer limit, String producerName,
        List<EServiceDescriptorState> states);

    ResponseEntity<CreatedResource> upgradeEServiceInstanceWithHttpInfo(String xCorrelationId, UUID eServiceId);
    
    ResponseEntity<ProducerEServiceDescriptor> getProducerEServiceDescriptorWithHttpInfo(
        String xCorrelationId, UUID eserviceId, UUID descriptorId);

    ResponseEntity<ProducerEServices> getProducerEServicesWithHttpInfo(
        String xCorrelationId, String eServiceName);

    ResponseEntity<CreatedResource> updateEServiceTemplateInstanceByIdWithHttpInfo(
        UUID eServiceId,
        UpdateEServiceTemplateInstanceSeed updateEServiceTemplateInstanceSeed
    );

    ResponseEntity<CreatedResource> updateDraftDescriptorTemplateInstanceWithHttpInfo(
        String xCorrelationId,
        UUID eServiceId,
        UUID descriptorId,
        UpdateEServiceDescriptorTemplateInstanceSeed updateEServiceDescriptorTemplateInstanceSeed
    );

    ResponseEntity<ProducerEServiceDetails> getProducerEServiceDetailsWithHttpInfo(
        String xCorrelationId, UUID eserviceId);

    ResponseEntity<CreatedResource> addEServiceTemplateInstanceInterfaceRestWithHttpInfo(
        String xCorrelationId, UUID eServiceId, UUID descriptorId, TemplateInstanceInterfaceRESTSeed templateInstanceInterfaceRESTSeed);
}
