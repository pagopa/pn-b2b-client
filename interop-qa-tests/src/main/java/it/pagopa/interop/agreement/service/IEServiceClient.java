package it.pagopa.interop.agreement.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementApprovalPolicy;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;

import java.util.UUID;

public interface IEServiceClient extends SettableBearerToken {
    CreatedEServiceDescriptor createEService(EServiceSeed eserviceSeed);
    CreatedResource updateDraftDescriptor(UUID eServiceId, UUID descriptorId, UpdateEServiceDescriptorSeed updateEServiceDescriptorSeed);
    CreatedResource createEServiceDocument(UUID eServiceId, UUID descriptorId, String kind, String prettyName, org.springframework.core.io.Resource doc);
    void publishDescriptor(UUID eServiceId, UUID descriptorId);
    void suspendDescriptor(UUID eServiceId, UUID descriptorId);
    CreatedResource createDescriptor(UUID eServiceId);
    void editAgreementApprovalPolicy(UUID eServiceId, UUID descriptorId, AgreementApprovalPolicy policy);
    ProducerEServiceDescriptor getEServiceDescriptor(UUID eServiceId, UUID descriptorId);

    CreatedResource approveDelegatedEServiceDescriptor(UUID eServiceId, UUID descriptorId);
}
