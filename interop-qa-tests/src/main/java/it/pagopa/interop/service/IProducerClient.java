package it.pagopa.interop.service;

import it.pagopa.interop.service.utils.SettableBearerToken;

import java.util.UUID;

public interface IProducerClient extends SettableBearerToken {
    void getProducerEServiceDescriptor(String xCorrelationId, UUID eserviceId, UUID descriptorId);
}
