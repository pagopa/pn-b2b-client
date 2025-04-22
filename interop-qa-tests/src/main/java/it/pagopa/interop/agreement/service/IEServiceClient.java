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
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateInstanceDescriptorQuotas;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateInstanceSeed;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface IEServiceClient extends SettableBearerToken {

    CreatedEServiceDescriptor createEService(EServiceSeed eserviceSeed);
    CreatedResource updateDraftDescriptor(UUID eServiceId, UUID descriptorId, UpdateEServiceDescriptorSeed updateEServiceDescriptorSeed);
    CreatedResource createEServiceDocument(UUID eServiceId, UUID descriptorId, String kind, String prettyName, org.springframework.core.io.Resource doc);
    void publishDescriptor(UUID eServiceId, UUID descriptorId);
    void suspendDescriptor(UUID eServiceId, UUID descriptorId);
    CreatedResource createDescriptor(UUID eServiceId);

    ResponseEntity<CreatedResource> createEServiceInstanceFromTemplateWithHttpInfo(
        UUID templateId, InstanceEServiceSeed instanceEServiceSeed);

    ResponseEntity<EServiceTemplateInstances> getEServiceTemplateInstancesWithHttpInfo(
        UUID templateId);

    ResponseEntity<EServiceTemplateInstances> getEServiceTemplateInstancesWithHttpInfo(
        UUID templateId, Integer offset, Integer limit, String producerName,
        List<EServiceDescriptorState> states);

    ResponseEntity<CreatedResource> upgradeEServiceInstanceWithHttpInfo(UUID eServiceId);
    
    ResponseEntity<ProducerEServiceDescriptor> getProducerEServiceDescriptorWithHttpInfo(
        UUID eserviceId, UUID descriptorId);

    ResponseEntity<ProducerEServices> getProducerEServicesWithHttpInfo(
        String eServiceName);

    ResponseEntity<CreatedResource> updateEServiceTemplateInstanceByIdWithHttpInfo(
        UUID eServiceId,
        UpdateEServiceTemplateInstanceSeed updateEServiceTemplateInstanceSeed
    );

    ResponseEntity<CreatedResource> updateDraftDescriptorTemplateInstanceWithHttpInfo(
        UUID eServiceId,
        UUID descriptorId,
        UpdateEServiceDescriptorTemplateInstanceSeed updateEServiceDescriptorTemplateInstanceSeed
    );

    ResponseEntity<CreatedResource> updateTemplateInstanceDescriptorWithHttpInfo(
        UUID eServiceId,
        UUID descriptorId,
        UpdateEServiceTemplateInstanceDescriptorQuotas descriptorQuotas
    );

    ResponseEntity<ProducerEServiceDetails> getProducerEServiceDetailsWithHttpInfo(
        UUID eserviceId);

    ResponseEntity<CreatedResource> addEServiceTemplateInstanceInterfaceRestWithHttpInfo(
        UUID eServiceId, UUID descriptorId, TemplateInstanceInterfaceRESTSeed templateInstanceInterfaceRESTSeed);
}
