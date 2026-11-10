package it.pagopa.interop.producer_keychains;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.common.SettableHttpCallExecutor;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.ProducerKeychainAddEService;

import java.util.UUID;

public interface IM2MProducerKeychainsClient extends SettableBearerToken, SettableHttpCallExecutor {
    void linkEservice(UUID producerKeychainId, ProducerKeychainAddEService seed);
}
