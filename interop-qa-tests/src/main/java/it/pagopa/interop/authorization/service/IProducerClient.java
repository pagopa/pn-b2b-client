package it.pagopa.interop.authorization.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;

import java.util.UUID;

public interface IProducerClient extends SettableBearerToken {
    void getProducerEServiceDescriptor(String xCorrelationId, UUID eserviceId, UUID descriptorId);
}
