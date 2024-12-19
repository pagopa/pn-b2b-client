package it.pagopa.interop.agreement.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;

import java.util.UUID;

public interface IEServiceClient extends SettableBearerToken {

    CreatedEServiceDescriptor createEService(String xCorrelationId, EServiceSeed eserviceSeed);
    CreatedResource updateDraftDescriptor(String xCorrelationId, UUID eServiceId, UUID descriptorId, UpdateEServiceDescriptorSeed updateEServiceDescriptorSeed);
    CreatedResource createEServiceDocument(String xCorrelationId, UUID eServiceId, UUID descriptorId, String kind, String prettyName, org.springframework.core.io.Resource doc);
    void publishDescriptor(String xCorrelationId, UUID eServiceId, UUID descriptorId);
    void suspendDescriptor(String xCorrelationId, UUID eServiceId, UUID descriptorId);
    CreatedResource createDescriptor(String xCorrelationId, UUID eServiceId);
}
