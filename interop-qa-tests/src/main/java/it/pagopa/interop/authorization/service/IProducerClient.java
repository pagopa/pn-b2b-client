package it.pagopa.interop.authorization.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;

import java.util.UUID;

public interface IProducerClient extends SettableBearerToken {
    ProducerEServiceDescriptor getProducerEServiceDescriptor(String xCorrelationId, UUID eserviceId, UUID descriptorId);
}
